// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.view.InputChannel;
import com.android.systemui.tracing.nano.EdgeBackGestureHandlerProto;
import java.io.PrintWriter;
import android.content.ContentResolver;
import android.provider.Settings$Global;
import com.android.systemui.plugins.Plugin;
import android.os.RemoteException;
import android.util.Log;
import android.view.WindowManagerGlobal;
import android.hardware.display.DisplayManager;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import android.hardware.input.InputManager;
import com.android.systemui.bubbles.BubbleController;
import android.view.KeyEvent;
import android.os.SystemClock;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.SysUiStatsLog;
import com.android.systemui.R$string;
import com.android.systemui.R$dimen;
import android.view.WindowManager$LayoutParams;
import android.view.MotionEvent;
import android.view.InputEvent;
import android.content.res.Resources;
import android.view.ViewConfiguration;
import com.android.systemui.Dependency;
import com.android.systemui.tracing.ProtoTracer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.ISystemGestureExclusionListener$Stub;
import com.android.systemui.model.SysUiState;
import android.os.SystemProperties;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.recents.OverviewProxyService;
import java.util.concurrent.Executor;
import android.view.InputMonitor;
import android.view.InputEventReceiver;
import com.android.internal.policy.GestureNavigationSettingsObserver;
import android.view.ISystemGestureExclusionListener;
import android.database.ContentObserver;
import android.graphics.Region;
import android.graphics.PointF;
import android.graphics.Point;
import android.content.Context;
import com.android.systemui.tracing.nano.SystemUiTraceProto;
import com.android.systemui.shared.tracing.ProtoTraceable;
import com.android.systemui.plugins.NavigationEdgeBackPlugin;
import com.android.systemui.plugins.PluginListener;
import android.hardware.display.DisplayManager$DisplayListener;

public class EdgeBackGestureHandler implements DisplayManager$DisplayListener, PluginListener<NavigationEdgeBackPlugin>, ProtoTraceable<SystemUiTraceProto>
{
    private static final int MAX_LONG_PRESS_TIMEOUT;
    private boolean mAllowGesture;
    private final NavigationEdgeBackPlugin.BackCallback mBackCallback;
    private int mBottomGestureHeight;
    private final Context mContext;
    private boolean mDisabledForQuickstep;
    private final int mDisplayId;
    private final Point mDisplaySize;
    private final PointF mDownPoint;
    private NavigationEdgeBackPlugin mEdgeBackPlugin;
    private int mEdgeWidthLeft;
    private int mEdgeWidthRight;
    private final PointF mEndPoint;
    private final Region mExcludeRegion;
    private boolean mFixedRotationFlagEnabled;
    private final ContentObserver mFixedRotationObserver;
    private ISystemGestureExclusionListener mGestureExclusionListener;
    private final GestureNavigationSettingsObserver mGestureNavigationSettingsObserver;
    private boolean mInRejectedExclusion;
    private InputEventReceiver mInputEventReceiver;
    private InputMonitor mInputMonitor;
    private boolean mIsAttached;
    private boolean mIsEnabled;
    private boolean mIsGesturalModeEnabled;
    private boolean mIsNavBarShownTransiently;
    private boolean mIsOnLeftEdge;
    private int mLeftInset;
    private boolean mLogGesture;
    private final int mLongPressTimeout;
    private final Executor mMainExecutor;
    private final OverviewProxyService mOverviewProxyService;
    private PluginManager mPluginManager;
    private OverviewProxyService.OverviewProxyListener mQuickSwitchListener;
    private int mRightInset;
    private int mStartingQuickstepRotation;
    private int mSysUiFlags;
    private TaskStackChangeListener mTaskStackChangeListener;
    private boolean mThresholdCrossed;
    private final float mTouchSlop;
    private final Region mUnrestrictedExcludeRegion;
    
    static {
        MAX_LONG_PRESS_TIMEOUT = SystemProperties.getInt("gestures.back_timeout", 250);
    }
    
    public EdgeBackGestureHandler(final Context mContext, final OverviewProxyService mOverviewProxyService, final SysUiState sysUiState, final PluginManager mPluginManager) {
        this.mGestureExclusionListener = (ISystemGestureExclusionListener)new ISystemGestureExclusionListener$Stub() {
            public void onSystemGestureExclusionChanged(final int n, final Region region, final Region region2) {
                if (n == EdgeBackGestureHandler.this.mDisplayId) {
                    EdgeBackGestureHandler.this.mMainExecutor.execute(new _$$Lambda$EdgeBackGestureHandler$1$gxj4RNtkm_JZXkSr9gvVxA9V4Ew(this, region, region2));
                }
            }
        };
        this.mQuickSwitchListener = new OverviewProxyService.OverviewProxyListener() {
            @Override
            public void onQuickSwitchToNewTask(final int n) {
                EdgeBackGestureHandler.this.mStartingQuickstepRotation = n;
                EdgeBackGestureHandler.this.updateDisabledForQuickstep();
            }
        };
        this.mTaskStackChangeListener = new TaskStackChangeListener() {
            @Override
            public void onRecentTaskListFrozenChanged(final boolean b) {
                if (!b) {
                    EdgeBackGestureHandler.this.mStartingQuickstepRotation = -1;
                    EdgeBackGestureHandler.this.mDisabledForQuickstep = false;
                }
            }
        };
        this.mFixedRotationObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            public void onChange(final boolean b, final Uri uri) {
                EdgeBackGestureHandler.this.updatedFixedRotation();
            }
        };
        this.mDisplaySize = new Point();
        this.mExcludeRegion = new Region();
        this.mUnrestrictedExcludeRegion = new Region();
        this.mStartingQuickstepRotation = -1;
        this.mDownPoint = new PointF();
        this.mEndPoint = new PointF();
        this.mThresholdCrossed = false;
        this.mAllowGesture = false;
        this.mLogGesture = false;
        this.mInRejectedExclusion = false;
        this.mBackCallback = new NavigationEdgeBackPlugin.BackCallback() {
            @Override
            public void cancelBack() {
                EdgeBackGestureHandler.this.logGesture(4);
                EdgeBackGestureHandler.this.mOverviewProxyService.notifyBackAction(false, (int)EdgeBackGestureHandler.this.mDownPoint.x, (int)EdgeBackGestureHandler.this.mDownPoint.y, false, EdgeBackGestureHandler.this.mIsOnLeftEdge ^ true);
            }
            
            @Override
            public void triggerBack() {
                EdgeBackGestureHandler.this.sendEvent(0, 4);
                final EdgeBackGestureHandler this$0 = EdgeBackGestureHandler.this;
                int n = 1;
                this$0.sendEvent(1, 4);
                EdgeBackGestureHandler.this.mOverviewProxyService.notifyBackAction(true, (int)EdgeBackGestureHandler.this.mDownPoint.x, (int)EdgeBackGestureHandler.this.mDownPoint.y, false, EdgeBackGestureHandler.this.mIsOnLeftEdge ^ true);
                final EdgeBackGestureHandler this$2 = EdgeBackGestureHandler.this;
                if (this$2.mInRejectedExclusion) {
                    n = 2;
                }
                this$2.logGesture(n);
            }
        };
        final Resources resources = mContext.getResources();
        this.mContext = mContext;
        this.mDisplayId = mContext.getDisplayId();
        this.mMainExecutor = mContext.getMainExecutor();
        this.mOverviewProxyService = mOverviewProxyService;
        this.mPluginManager = mPluginManager;
        Dependency.get(ProtoTracer.class).add(this);
        this.mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop() * 0.75f;
        this.mLongPressTimeout = Math.min(EdgeBackGestureHandler.MAX_LONG_PRESS_TIMEOUT, ViewConfiguration.getLongPressTimeout());
        this.mGestureNavigationSettingsObserver = new GestureNavigationSettingsObserver(this.mContext.getMainThreadHandler(), this.mContext, (Runnable)new _$$Lambda$EdgeBackGestureHandler$vXP18u13L11hr3DHIqYK_3vd0Es(this, resources));
        this.updateCurrentUserResources(resources);
        sysUiState.addCallback((SysUiState.SysUiStateCallback)new _$$Lambda$EdgeBackGestureHandler$_UxuwT8EFp_psZET0wyMM0rypXk(this));
    }
    
    private void cancelGesture(MotionEvent obtain) {
        this.mAllowGesture = false;
        this.mLogGesture = false;
        this.mInRejectedExclusion = false;
        obtain = MotionEvent.obtain(obtain);
        obtain.setAction(3);
        this.mEdgeBackPlugin.onMotionEvent(obtain);
        obtain.recycle();
    }
    
    private WindowManager$LayoutParams createLayoutParams() {
        final Resources resources = this.mContext.getResources();
        final WindowManager$LayoutParams windowManager$LayoutParams = new WindowManager$LayoutParams(resources.getDimensionPixelSize(R$dimen.navigation_edge_panel_width), resources.getDimensionPixelSize(R$dimen.navigation_edge_panel_height), 2024, 8388904, -3);
        windowManager$LayoutParams.privateFlags |= 0x10;
        final StringBuilder sb = new StringBuilder();
        sb.append("EdgeBackGestureHandler");
        sb.append(this.mContext.getDisplayId());
        windowManager$LayoutParams.setTitle((CharSequence)sb.toString());
        windowManager$LayoutParams.accessibilityTitle = this.mContext.getString(R$string.nav_bar_edge_panel);
        windowManager$LayoutParams.setFitInsetsTypes(windowManager$LayoutParams.windowAnimations = 0);
        return windowManager$LayoutParams;
    }
    
    private void disposeInputChannel() {
        final InputEventReceiver mInputEventReceiver = this.mInputEventReceiver;
        if (mInputEventReceiver != null) {
            mInputEventReceiver.dispose();
            this.mInputEventReceiver = null;
        }
        final InputMonitor mInputMonitor = this.mInputMonitor;
        if (mInputMonitor != null) {
            mInputMonitor.dispose();
            this.mInputMonitor = null;
        }
    }
    
    private boolean isWithinTouchRegion(final int n, final int n2) {
        final Point mDisplaySize = this.mDisplaySize;
        if (n2 >= mDisplaySize.y - this.mBottomGestureHeight) {
            return false;
        }
        if (n > (this.mEdgeWidthLeft + this.mLeftInset) * 2 && n < mDisplaySize.x - (this.mEdgeWidthRight + this.mRightInset) * 2) {
            return false;
        }
        final boolean b = n <= this.mEdgeWidthLeft + this.mLeftInset || n >= this.mDisplaySize.x - this.mEdgeWidthRight - this.mRightInset;
        if (this.mIsNavBarShownTransiently) {
            this.mLogGesture = true;
            return b;
        }
        if (this.mExcludeRegion.contains(n, n2)) {
            if (b) {
                this.mOverviewProxyService.notifyBackAction(false, -1, -1, false, this.mIsOnLeftEdge ^ true);
                final PointF mEndPoint = this.mEndPoint;
                mEndPoint.x = -1.0f;
                mEndPoint.y = -1.0f;
                this.mLogGesture = true;
                this.logGesture(3);
            }
            return false;
        }
        this.mInRejectedExclusion = this.mUnrestrictedExcludeRegion.contains(n, n2);
        this.mLogGesture = true;
        return b;
    }
    
    private void logGesture(final int n) {
        if (!this.mLogGesture) {
            return;
        }
        this.mLogGesture = false;
        final int n2 = (int)this.mDownPoint.y;
        int n3;
        if (this.mIsOnLeftEdge) {
            n3 = 1;
        }
        else {
            n3 = 2;
        }
        final PointF mDownPoint = this.mDownPoint;
        final int n4 = (int)mDownPoint.x;
        final int n5 = (int)mDownPoint.y;
        final PointF mEndPoint = this.mEndPoint;
        SysUiStatsLog.write(224, n, n2, n3, n4, n5, (int)mEndPoint.x, (int)mEndPoint.y, this.mEdgeWidthLeft + this.mLeftInset, this.mDisplaySize.x - (this.mEdgeWidthRight + this.mRightInset));
    }
    
    private void onInputEvent(final InputEvent inputEvent) {
        if (inputEvent instanceof MotionEvent) {
            this.onMotionEvent((MotionEvent)inputEvent);
        }
    }
    
    private void onMotionEvent(final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        final boolean b = true;
        if (actionMasked == 0) {
            this.mIsOnLeftEdge = (motionEvent.getX() <= this.mEdgeWidthLeft + this.mLeftInset);
            this.mLogGesture = false;
            this.mInRejectedExclusion = false;
            final boolean mAllowGesture = !QuickStepContract.isBackGestureDisabled(this.mSysUiFlags) && this.isWithinTouchRegion((int)motionEvent.getX(), (int)motionEvent.getY()) && !this.mDisabledForQuickstep && b;
            this.mAllowGesture = mAllowGesture;
            if (mAllowGesture) {
                this.mEdgeBackPlugin.setIsLeftPanel(this.mIsOnLeftEdge);
                this.mEdgeBackPlugin.onMotionEvent(motionEvent);
            }
            if (this.mLogGesture) {
                this.mDownPoint.set(motionEvent.getX(), motionEvent.getY());
                this.mEndPoint.set(-1.0f, -1.0f);
                this.mThresholdCrossed = false;
            }
        }
        else if (this.mAllowGesture || this.mLogGesture) {
            if (!this.mThresholdCrossed) {
                this.mEndPoint.x = (float)(int)motionEvent.getX();
                this.mEndPoint.y = (float)(int)motionEvent.getY();
                if (actionMasked == 5) {
                    if (this.mAllowGesture) {
                        this.logGesture(6);
                        this.cancelGesture(motionEvent);
                    }
                    this.mLogGesture = false;
                    return;
                }
                if (actionMasked == 2) {
                    if (motionEvent.getEventTime() - motionEvent.getDownTime() > this.mLongPressTimeout) {
                        if (this.mAllowGesture) {
                            this.logGesture(7);
                            this.cancelGesture(motionEvent);
                        }
                        this.mLogGesture = false;
                        return;
                    }
                    final float abs = Math.abs(motionEvent.getX() - this.mDownPoint.x);
                    final float abs2 = Math.abs(motionEvent.getY() - this.mDownPoint.y);
                    if (abs2 > abs && abs2 > this.mTouchSlop) {
                        if (this.mAllowGesture) {
                            this.logGesture(8);
                            this.cancelGesture(motionEvent);
                        }
                        this.mLogGesture = false;
                        return;
                    }
                    if (abs > abs2 && abs > this.mTouchSlop) {
                        if (this.mAllowGesture) {
                            this.mThresholdCrossed = true;
                            this.mInputMonitor.pilferPointers();
                        }
                        else {
                            this.logGesture(5);
                        }
                    }
                }
            }
            if (this.mAllowGesture) {
                this.mEdgeBackPlugin.onMotionEvent(motionEvent);
            }
        }
        Dependency.get(ProtoTracer.class).update();
    }
    
    private void sendEvent(int expandedDisplayId, final int n) {
        final long uptimeMillis = SystemClock.uptimeMillis();
        final KeyEvent keyEvent = new KeyEvent(uptimeMillis, uptimeMillis, expandedDisplayId, n, 0, 0, -1, 0, 72, 257);
        expandedDisplayId = Dependency.get(BubbleController.class).getExpandedDisplayId(this.mContext);
        if (n == 4 && expandedDisplayId != -1) {
            keyEvent.setDisplayId(expandedDisplayId);
        }
        InputManager.getInstance().injectInputEvent((InputEvent)keyEvent, 0);
    }
    
    private void setEdgeBackPlugin(final NavigationEdgeBackPlugin mEdgeBackPlugin) {
        final NavigationEdgeBackPlugin mEdgeBackPlugin2 = this.mEdgeBackPlugin;
        if (mEdgeBackPlugin2 != null) {
            mEdgeBackPlugin2.onDestroy();
        }
        (this.mEdgeBackPlugin = mEdgeBackPlugin).setBackCallback(this.mBackCallback);
        this.mEdgeBackPlugin.setLayoutParams(this.createLayoutParams());
        this.updateDisplaySize();
    }
    
    private void setRotationCallbacks(final boolean b) {
        if (b) {
            ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mTaskStackChangeListener);
            this.mOverviewProxyService.addCallback(this.mQuickSwitchListener);
        }
        else {
            ActivityManagerWrapper.getInstance().unregisterTaskStackListener(this.mTaskStackChangeListener);
            this.mOverviewProxyService.removeCallback(this.mQuickSwitchListener);
        }
    }
    
    private void updateDisabledForQuickstep() {
        this.mDisabledForQuickstep = (this.mStartingQuickstepRotation != this.mContext.getResources().getConfiguration().windowConfiguration.getRotation());
    }
    
    private void updateDisplaySize() {
        this.mContext.getDisplay().getRealSize(this.mDisplaySize);
        final NavigationEdgeBackPlugin mEdgeBackPlugin = this.mEdgeBackPlugin;
        if (mEdgeBackPlugin != null) {
            mEdgeBackPlugin.setDisplaySize(this.mDisplaySize);
        }
    }
    
    private void updateIsEnabled() {
        final boolean mIsEnabled = this.mIsAttached && this.mIsGesturalModeEnabled;
        if (mIsEnabled == this.mIsEnabled) {
            return;
        }
        this.mIsEnabled = mIsEnabled;
        this.disposeInputChannel();
        final NavigationEdgeBackPlugin mEdgeBackPlugin = this.mEdgeBackPlugin;
        if (mEdgeBackPlugin != null) {
            mEdgeBackPlugin.onDestroy();
            this.mEdgeBackPlugin = null;
        }
        if (!this.mIsEnabled) {
            this.mGestureNavigationSettingsObserver.unregister();
            ((DisplayManager)this.mContext.getSystemService((Class)DisplayManager.class)).unregisterDisplayListener((DisplayManager$DisplayListener)this);
            this.mPluginManager.removePluginListener(this);
            try {
                WindowManagerGlobal.getWindowManagerService().unregisterSystemGestureExclusionListener(this.mGestureExclusionListener, this.mDisplayId);
            }
            catch (RemoteException ex) {
                Log.e("EdgeBackGestureHandler", "Failed to unregister window manager callbacks", (Throwable)ex);
            }
        }
        else {
            this.mGestureNavigationSettingsObserver.register();
            this.updateDisplaySize();
            ((DisplayManager)this.mContext.getSystemService((Class)DisplayManager.class)).registerDisplayListener((DisplayManager$DisplayListener)this, this.mContext.getMainThreadHandler());
            try {
                WindowManagerGlobal.getWindowManagerService().registerSystemGestureExclusionListener(this.mGestureExclusionListener, this.mDisplayId);
            }
            catch (RemoteException ex2) {
                Log.e("EdgeBackGestureHandler", "Failed to register window manager callbacks", (Throwable)ex2);
            }
            this.mInputMonitor = InputManager.getInstance().monitorGestureInput("edge-swipe", this.mDisplayId);
            this.mInputEventReceiver = new SysUiInputEventReceiver(this.mInputMonitor.getInputChannel(), Looper.getMainLooper());
            this.setEdgeBackPlugin(new NavigationBarEdgePanel(this.mContext));
            this.mPluginManager.addPluginListener((PluginListener<Plugin>)this, NavigationEdgeBackPlugin.class, false);
        }
    }
    
    private void updatedFixedRotation() {
        final boolean mFixedRotationFlagEnabled = this.mFixedRotationFlagEnabled;
        final ContentResolver contentResolver = this.mContext.getContentResolver();
        boolean b = false;
        if (Settings$Global.getInt(contentResolver, "fixed_rotation_transform", 0) != 0) {
            b = true;
        }
        if (mFixedRotationFlagEnabled == (this.mFixedRotationFlagEnabled = b)) {
            return;
        }
        this.setRotationCallbacks(b);
    }
    
    public void dump(final PrintWriter printWriter) {
        printWriter.println("EdgeBackGestureHandler:");
        final StringBuilder sb = new StringBuilder();
        sb.append("  mIsEnabled=");
        sb.append(this.mIsEnabled);
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("  mAllowGesture=");
        sb2.append(this.mAllowGesture);
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("  mDisabledForQuickstep=");
        sb3.append(this.mDisabledForQuickstep);
        printWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("  mInRejectedExclusion");
        sb4.append(this.mInRejectedExclusion);
        printWriter.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append("  mExcludeRegion=");
        sb5.append(this.mExcludeRegion);
        printWriter.println(sb5.toString());
        final StringBuilder sb6 = new StringBuilder();
        sb6.append("  mUnrestrictedExcludeRegion=");
        sb6.append(this.mUnrestrictedExcludeRegion);
        printWriter.println(sb6.toString());
        final StringBuilder sb7 = new StringBuilder();
        sb7.append("  mIsAttached=");
        sb7.append(this.mIsAttached);
        printWriter.println(sb7.toString());
        final StringBuilder sb8 = new StringBuilder();
        sb8.append("  mEdgeWidthLeft=");
        sb8.append(this.mEdgeWidthLeft);
        printWriter.println(sb8.toString());
        final StringBuilder sb9 = new StringBuilder();
        sb9.append("  mEdgeWidthRight=");
        sb9.append(this.mEdgeWidthRight);
        printWriter.println(sb9.toString());
    }
    
    public void onDisplayAdded(final int n) {
    }
    
    public void onDisplayChanged(final int n) {
        if (this.mStartingQuickstepRotation > -1) {
            this.updateDisabledForQuickstep();
        }
        if (n == this.mDisplayId) {
            this.updateDisplaySize();
        }
    }
    
    public void onDisplayRemoved(final int n) {
    }
    
    public void onNavBarAttached() {
        this.mIsAttached = true;
        this.updatedFixedRotation();
        if (this.mFixedRotationFlagEnabled) {
            this.setRotationCallbacks(true);
        }
        this.mContext.getContentResolver().registerContentObserver(Settings$Global.getUriFor("fixed_rotation_transform"), false, this.mFixedRotationObserver, -1);
        this.updateIsEnabled();
    }
    
    public void onNavBarDetached() {
        this.mIsAttached = false;
        if (this.mFixedRotationFlagEnabled) {
            this.setRotationCallbacks(false);
        }
        this.mContext.getContentResolver().unregisterContentObserver(this.mFixedRotationObserver);
        this.updateIsEnabled();
    }
    
    public void onNavBarTransientStateChanged(final boolean mIsNavBarShownTransiently) {
        this.mIsNavBarShownTransiently = mIsNavBarShownTransiently;
    }
    
    public void onNavigationModeChanged(final int n, final Context context) {
        this.mIsGesturalModeEnabled = QuickStepContract.isGesturalMode(n);
        this.updateIsEnabled();
        this.updateCurrentUserResources(context.getResources());
    }
    
    public void onPluginConnected(final NavigationEdgeBackPlugin edgeBackPlugin, final Context context) {
        this.setEdgeBackPlugin(edgeBackPlugin);
    }
    
    public void onPluginDisconnected(final NavigationEdgeBackPlugin navigationEdgeBackPlugin) {
        this.setEdgeBackPlugin(new NavigationBarEdgePanel(this.mContext));
    }
    
    public void setInsets(final int mLeftInset, final int mRightInset) {
        this.mLeftInset = mLeftInset;
        this.mRightInset = mRightInset;
        final NavigationEdgeBackPlugin mEdgeBackPlugin = this.mEdgeBackPlugin;
        if (mEdgeBackPlugin != null) {
            mEdgeBackPlugin.setInsets(mLeftInset, mRightInset);
        }
    }
    
    public void updateCurrentUserResources(final Resources resources) {
        this.mEdgeWidthLeft = this.mGestureNavigationSettingsObserver.getLeftSensitivity(resources);
        this.mEdgeWidthRight = this.mGestureNavigationSettingsObserver.getRightSensitivity(resources);
        this.mBottomGestureHeight = resources.getDimensionPixelSize(17105323);
    }
    
    public void writeToProto(final SystemUiTraceProto systemUiTraceProto) {
        if (systemUiTraceProto.edgeBackGestureHandler == null) {
            systemUiTraceProto.edgeBackGestureHandler = new EdgeBackGestureHandlerProto();
        }
        systemUiTraceProto.edgeBackGestureHandler.allowGesture = this.mAllowGesture;
    }
    
    class SysUiInputEventReceiver extends InputEventReceiver
    {
        SysUiInputEventReceiver(final InputChannel inputChannel, final Looper looper) {
            super(inputChannel, looper);
        }
        
        public void onInputEvent(final InputEvent inputEvent) {
            EdgeBackGestureHandler.this.onInputEvent(inputEvent);
            this.finishInputEvent(inputEvent, true);
        }
    }
}
