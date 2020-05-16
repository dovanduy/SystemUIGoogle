// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.os.IBinder;
import java.lang.reflect.Field;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.os.Binder;
import android.content.res.Resources;
import com.android.systemui.R$bool;
import android.os.SystemProperties;
import android.os.RemoteException;
import android.util.Log;
import android.os.Trace;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.DejankUtils;
import android.view.ViewGroup$LayoutParams;
import android.view.View;
import java.util.function.Predicate;
import java.util.Arrays;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.R$integer;
import com.google.android.collect.Lists;
import com.android.systemui.dump.DumpManager;
import android.view.WindowManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import java.util.function.Consumer;
import android.view.ViewGroup;
import android.view.WindowManager$LayoutParams;
import android.view.Display$Mode;
import android.content.Context;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import android.app.IActivityManager;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.Dumpable;
import com.android.systemui.statusbar.RemoteInputController;

public class NotificationShadeWindowController implements Callback, Dumpable, ConfigurationListener
{
    private final IActivityManager mActivityManager;
    private final ArrayList<WeakReference<StatusBarWindowCallback>> mCallbacks;
    private final SysuiColorExtractor mColorExtractor;
    private final Context mContext;
    private final State mCurrentState;
    private final DozeParameters mDozeParameters;
    private ForcePluginOpenListener mForcePluginOpenListener;
    private boolean mHasTopUi;
    private boolean mHasTopUiChanged;
    private final KeyguardBypassController mKeyguardBypassController;
    private final Display$Mode mKeyguardDisplayMode;
    private final boolean mKeyguardScreenRotation;
    private OtherwisedCollapsedListener mListener;
    private final long mLockScreenDisplayTimeout;
    private WindowManager$LayoutParams mLp;
    private final WindowManager$LayoutParams mLpChanged;
    private ViewGroup mNotificationShadeView;
    private float mScreenBrightnessDoze;
    private Consumer<Integer> mScrimsVisibilityListener;
    private final StatusBarStateController.StateListener mStateListener;
    private final WindowManager mWindowManager;
    
    public NotificationShadeWindowController(final Context mContext, final WindowManager mWindowManager, final IActivityManager mActivityManager, final DozeParameters mDozeParameters, final StatusBarStateController statusBarStateController, final ConfigurationController configurationController, final KeyguardBypassController mKeyguardBypassController, final SysuiColorExtractor mColorExtractor, final DumpManager dumpManager) {
        this.mCurrentState = new State();
        this.mCallbacks = (ArrayList<WeakReference<StatusBarWindowCallback>>)Lists.newArrayList();
        this.mStateListener = new StatusBarStateController.StateListener() {
            @Override
            public void onDozingChanged(final boolean dozing) {
                NotificationShadeWindowController.this.setDozing(dozing);
            }
            
            @Override
            public void onStateChanged(final int n) {
                NotificationShadeWindowController.this.setStatusBarState(n);
            }
        };
        this.mContext = mContext;
        this.mWindowManager = mWindowManager;
        this.mActivityManager = mActivityManager;
        this.mKeyguardScreenRotation = this.shouldEnableKeyguardScreenRotation();
        this.mDozeParameters = mDozeParameters;
        this.mScreenBrightnessDoze = mDozeParameters.getScreenBrightnessDoze();
        this.mLpChanged = new WindowManager$LayoutParams();
        this.mKeyguardBypassController = mKeyguardBypassController;
        this.mColorExtractor = mColorExtractor;
        dumpManager.registerDumpable(NotificationShadeWindowController.class.getName(), this);
        this.mLockScreenDisplayTimeout = mContext.getResources().getInteger(R$integer.config_lockScreenDisplayTimeout);
        ((SysuiStatusBarStateController)statusBarStateController).addCallback(this.mStateListener, 1);
        configurationController.addCallback((ConfigurationController.ConfigurationListener)this);
        this.mKeyguardDisplayMode = Arrays.stream(mContext.getDisplay().getSupportedModes()).filter(new _$$Lambda$NotificationShadeWindowController$eZhKF4qxAkYFnq9gGQ6_QkkGic4(mContext.getResources().getInteger(R$integer.config_keyguardRefreshRate), mContext.getDisplay().getMode())).findFirst().orElse(null);
    }
    
    private void adjustScreenOrientation(final State state) {
        if (!state.isKeyguardShowingAndNotOccluded() && !state.mDozing) {
            this.mLpChanged.screenOrientation = -1;
        }
        else if (this.mKeyguardScreenRotation) {
            this.mLpChanged.screenOrientation = 2;
        }
        else {
            this.mLpChanged.screenOrientation = 5;
        }
    }
    
    private void apply(final State state) {
        this.applyKeyguardFlags(state);
        this.applyFocusableFlag(state);
        this.applyForceShowNavigationFlag(state);
        this.adjustScreenOrientation(state);
        this.applyVisibility(state);
        this.applyUserActivityTimeout(state);
        this.applyInputFeatures(state);
        this.applyFitsSystemWindows(state);
        this.applyModalFlag(state);
        this.applyBrightness(state);
        this.applyHasTopUi(state);
        this.applyNotTouchable(state);
        this.applyStatusBarColorSpaceAgnosticFlag(state);
        final WindowManager$LayoutParams mLp = this.mLp;
        if (mLp != null && mLp.copyFrom(this.mLpChanged) != 0) {
            this.mWindowManager.updateViewLayout((View)this.mNotificationShadeView, (ViewGroup$LayoutParams)this.mLp);
        }
        if (this.mHasTopUi != this.mHasTopUiChanged) {
            DejankUtils.whitelistIpcs(new _$$Lambda$NotificationShadeWindowController$cWnla7q4SPNKNSlx9hB8mcjvaHk(this));
        }
        this.notifyStateChangedCallbacks();
    }
    
    private void applyBrightness(final State state) {
        if (state.mForceDozeBrightness) {
            this.mLpChanged.screenBrightness = this.mScreenBrightnessDoze;
        }
        else {
            this.mLpChanged.screenBrightness = -1.0f;
        }
    }
    
    private void applyFitsSystemWindows(final State state) {
        final boolean fitsSystemWindows = state.isKeyguardShowingAndNotOccluded() ^ true;
        final ViewGroup mNotificationShadeView = this.mNotificationShadeView;
        if (mNotificationShadeView != null && mNotificationShadeView.getFitsSystemWindows() != fitsSystemWindows) {
            this.mNotificationShadeView.setFitsSystemWindows(fitsSystemWindows);
            this.mNotificationShadeView.requestApplyInsets();
        }
    }
    
    private void applyFocusableFlag(final State state) {
        final boolean b = state.mNotificationShadeFocusable && state.mPanelExpanded;
        if ((state.mBouncerShowing && (state.mKeyguardOccluded || state.mKeyguardNeedsInput)) || (NotificationRemoteInputManager.ENABLE_REMOTE_INPUT && state.mRemoteInputActive) || state.mBubbleExpanded) {
            final WindowManager$LayoutParams mLpChanged = this.mLpChanged;
            final int flags = mLpChanged.flags & 0xFFFFFFF7;
            mLpChanged.flags = flags;
            mLpChanged.flags = (flags & 0xFFFDFFFF);
        }
        else if (!state.isKeyguardShowingAndNotOccluded() && !b) {
            final WindowManager$LayoutParams mLpChanged2 = this.mLpChanged;
            final int flags2 = mLpChanged2.flags | 0x8;
            mLpChanged2.flags = flags2;
            mLpChanged2.flags = (flags2 & 0xFFFDFFFF);
        }
        else {
            final WindowManager$LayoutParams mLpChanged3 = this.mLpChanged;
            final int flags3 = mLpChanged3.flags & 0xFFFFFFF7;
            mLpChanged3.flags = flags3;
            if (state.mKeyguardNeedsInput) {
                mLpChanged3.flags = (flags3 & 0xFFFDFFFF);
            }
            else {
                mLpChanged3.flags = (0x20000 | flags3);
            }
        }
        this.mLpChanged.softInputMode = 16;
    }
    
    private void applyForceShowNavigationFlag(final State state) {
        if (!state.mPanelExpanded && !state.mBouncerShowing && (!NotificationRemoteInputManager.ENABLE_REMOTE_INPUT || !state.mRemoteInputActive)) {
            final WindowManager$LayoutParams mLpChanged = this.mLpChanged;
            mLpChanged.privateFlags &= 0xFF7FFFFF;
        }
        else {
            final WindowManager$LayoutParams mLpChanged2 = this.mLpChanged;
            mLpChanged2.privateFlags |= 0x800000;
        }
    }
    
    private void applyHasTopUi(final State state) {
        this.mHasTopUiChanged = (state.mForceHasTopUi || this.isExpanded(state));
    }
    
    private void applyInputFeatures(final State state) {
        if (state.isKeyguardShowingAndNotOccluded() && state.mStatusBarState == 1 && !state.mQsExpanded && !state.mForceUserActivity) {
            final WindowManager$LayoutParams mLpChanged = this.mLpChanged;
            mLpChanged.inputFeatures |= 0x4;
        }
        else {
            final WindowManager$LayoutParams mLpChanged2 = this.mLpChanged;
            mLpChanged2.inputFeatures &= 0xFFFFFFFB;
        }
    }
    
    private void applyKeyguardFlags(final State state) {
        final int mScrimsVisibility = state.mScrimsVisibility;
        final int n = 1;
        final boolean b = mScrimsVisibility == 2;
        if ((state.mKeyguardShowing || (state.mDozing && this.mDozeParameters.getAlwaysOn())) && !state.mBackdropShowing && !b) {
            final WindowManager$LayoutParams mLpChanged = this.mLpChanged;
            mLpChanged.flags |= 0x100000;
        }
        else {
            final WindowManager$LayoutParams mLpChanged2 = this.mLpChanged;
            mLpChanged2.flags &= 0xFFEFFFFF;
        }
        if (state.mDozing) {
            final WindowManager$LayoutParams mLpChanged3 = this.mLpChanged;
            mLpChanged3.privateFlags |= 0x80000;
        }
        else {
            final WindowManager$LayoutParams mLpChanged4 = this.mLpChanged;
            mLpChanged4.privateFlags &= 0xFFF7FFFF;
        }
        if (this.mKeyguardDisplayMode != null) {
            int n2;
            if (this.mKeyguardBypassController.getBypassEnabled() && state.mStatusBarState == 1 && !state.mKeyguardFadingAway && !state.mKeyguardGoingAway) {
                n2 = n;
            }
            else {
                n2 = 0;
            }
            if (!state.mDozing && n2 == 0) {
                this.mLpChanged.preferredDisplayModeId = 0;
            }
            else {
                this.mLpChanged.preferredDisplayModeId = this.mKeyguardDisplayMode.getModeId();
            }
            Trace.setCounter("display_mode_id", (long)this.mLpChanged.preferredDisplayModeId);
        }
    }
    
    private void applyModalFlag(final State state) {
        if (state.mHeadsUpShowing) {
            final WindowManager$LayoutParams mLpChanged = this.mLpChanged;
            mLpChanged.flags |= 0x20;
        }
        else {
            final WindowManager$LayoutParams mLpChanged2 = this.mLpChanged;
            mLpChanged2.flags &= 0xFFFFFFDF;
        }
    }
    
    private void applyNotTouchable(final State state) {
        if (state.mNotTouchable) {
            final WindowManager$LayoutParams mLpChanged = this.mLpChanged;
            mLpChanged.flags |= 0x10;
        }
        else {
            final WindowManager$LayoutParams mLpChanged2 = this.mLpChanged;
            mLpChanged2.flags &= 0xFFFFFFEF;
        }
    }
    
    private void applyStatusBarColorSpaceAgnosticFlag(final State state) {
        if (!this.isExpanded(state)) {
            final WindowManager$LayoutParams mLpChanged = this.mLpChanged;
            mLpChanged.privateFlags |= 0x1000000;
        }
        else {
            final WindowManager$LayoutParams mLpChanged2 = this.mLpChanged;
            mLpChanged2.privateFlags &= 0xFEFFFFFF;
        }
    }
    
    private void applyUserActivityTimeout(final State state) {
        if (state.isKeyguardShowingAndNotOccluded() && state.mStatusBarState == 1 && !state.mQsExpanded) {
            final WindowManager$LayoutParams mLpChanged = this.mLpChanged;
            long mLockScreenDisplayTimeout;
            if (state.mBouncerShowing) {
                mLockScreenDisplayTimeout = 10000L;
            }
            else {
                mLockScreenDisplayTimeout = this.mLockScreenDisplayTimeout;
            }
            mLpChanged.userActivityTimeout = mLockScreenDisplayTimeout;
        }
        else {
            this.mLpChanged.userActivityTimeout = -1L;
        }
    }
    
    private void applyVisibility(final State state) {
        boolean expanded;
        final boolean wouldOtherwiseCollapse = expanded = this.isExpanded(state);
        if (state.mForcePluginOpen) {
            final OtherwisedCollapsedListener mListener = this.mListener;
            if (mListener != null) {
                mListener.setWouldOtherwiseCollapse(wouldOtherwiseCollapse);
            }
            expanded = true;
        }
        if (expanded) {
            this.mNotificationShadeView.setVisibility(0);
        }
        else {
            this.mNotificationShadeView.setVisibility(4);
        }
    }
    
    private boolean isExpanded(final State state) {
        return (!state.mForceCollapsed && (state.isKeyguardShowingAndNotOccluded() || state.mPanelVisible || state.mKeyguardFadingAway || state.mBouncerShowing || state.mHeadsUpShowing || state.mBubblesShowing || state.mScrimsVisibility != 0)) || state.mBackgroundBlurRadius > 0;
    }
    
    private void setKeyguardDark(final boolean b) {
        final int systemUiVisibility = this.mNotificationShadeView.getSystemUiVisibility();
        int systemUiVisibility2;
        if (b) {
            systemUiVisibility2 = (systemUiVisibility | 0x10 | 0x2000);
        }
        else {
            systemUiVisibility2 = (systemUiVisibility & 0xFFFFFFEF & 0xFFFFDFFF);
        }
        this.mNotificationShadeView.setSystemUiVisibility(systemUiVisibility2);
    }
    
    private void setStatusBarState(final int mStatusBarState) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mStatusBarState = mStatusBarState;
        this.apply(mCurrentState);
    }
    
    private boolean shouldEnableKeyguardScreenRotation() {
        final Resources resources = this.mContext.getResources();
        boolean b = false;
        if (SystemProperties.getBoolean("lockscreen.rot_override", false) || resources.getBoolean(R$bool.config_enableLockScreenRotation)) {
            b = true;
        }
        return b;
    }
    
    public void attach() {
        final WindowManager$LayoutParams mLp = new WindowManager$LayoutParams(-1, -1, 2040, -2138832824, -3);
        this.mLp = mLp;
        mLp.token = (IBinder)new Binder();
        final WindowManager$LayoutParams mLp2 = this.mLp;
        mLp2.gravity = 48;
        mLp2.setFitInsetsTypes(0);
        final WindowManager$LayoutParams mLp3 = this.mLp;
        mLp3.softInputMode = 16;
        mLp3.setTitle((CharSequence)"NotificationShade");
        this.mLp.packageName = this.mContext.getPackageName();
        final WindowManager$LayoutParams mLp4 = this.mLp;
        mLp4.layoutInDisplayCutoutMode = 3;
        mLp4.privateFlags |= 0x8000000;
        mLp4.insetsFlags.behavior = 2;
        this.mWindowManager.addView((View)this.mNotificationShadeView, (ViewGroup$LayoutParams)mLp4);
        this.mLpChanged.copyFrom(this.mLp);
        this.onThemeChanged();
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("NotificationShadeWindowController:");
        final StringBuilder sb = new StringBuilder();
        sb.append("  mKeyguardDisplayMode=");
        sb.append(this.mKeyguardDisplayMode);
        printWriter.println(sb.toString());
        printWriter.println(this.mCurrentState);
    }
    
    public boolean getBubblesShowing() {
        return this.mCurrentState.mBubblesShowing;
    }
    
    public boolean getForceHasTopUi() {
        return this.mCurrentState.mForceHasTopUi;
    }
    
    public boolean getForcePluginOpen() {
        return this.mCurrentState.mForcePluginOpen;
    }
    
    public ViewGroup getNotificationShadeView() {
        return this.mNotificationShadeView;
    }
    
    public boolean getPanelExpanded() {
        return this.mCurrentState.mPanelExpanded;
    }
    
    public boolean isShowingWallpaper() {
        return this.mCurrentState.mBackdropShowing ^ true;
    }
    
    public void notifyStateChangedCallbacks() {
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            final StatusBarWindowCallback statusBarWindowCallback = this.mCallbacks.get(i).get();
            if (statusBarWindowCallback != null) {
                final State mCurrentState = this.mCurrentState;
                statusBarWindowCallback.onStateChanged(mCurrentState.mKeyguardShowing, mCurrentState.mKeyguardOccluded, mCurrentState.mBouncerShowing);
            }
        }
    }
    
    @Override
    public void onRemoteInputActive(final boolean mRemoteInputActive) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mRemoteInputActive = mRemoteInputActive;
        this.apply(mCurrentState);
    }
    
    @Override
    public void onThemeChanged() {
        if (this.mNotificationShadeView == null) {
            return;
        }
        this.setKeyguardDark(this.mColorExtractor.getNeutralColors().supportsDarkText());
    }
    
    public void registerCallback(final StatusBarWindowCallback referent) {
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            if (this.mCallbacks.get(i).get() == referent) {
                return;
            }
        }
        this.mCallbacks.add(new WeakReference<StatusBarWindowCallback>(referent));
    }
    
    public void setBackdropShowing(final boolean mBackdropShowing) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mBackdropShowing = mBackdropShowing;
        this.apply(mCurrentState);
    }
    
    public void setBackgroundBlurRadius(final int mBackgroundBlurRadius) {
        final State mCurrentState = this.mCurrentState;
        if (mCurrentState.mBackgroundBlurRadius == mBackgroundBlurRadius) {
            return;
        }
        mCurrentState.mBackgroundBlurRadius = mBackgroundBlurRadius;
        this.apply(mCurrentState);
    }
    
    public void setBouncerShowing(final boolean mBouncerShowing) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mBouncerShowing = mBouncerShowing;
        this.apply(mCurrentState);
    }
    
    public void setBubbleExpanded(final boolean mBubbleExpanded) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mBubbleExpanded = mBubbleExpanded;
        this.apply(mCurrentState);
    }
    
    public void setBubblesShowing(final boolean mBubblesShowing) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mBubblesShowing = mBubblesShowing;
        this.apply(mCurrentState);
    }
    
    public void setDozeScreenBrightness(final int n) {
        this.mScreenBrightnessDoze = n / 255.0f;
    }
    
    public void setDozing(final boolean mDozing) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mDozing = mDozing;
        this.apply(mCurrentState);
    }
    
    public void setForceDozeBrightness(final boolean mForceDozeBrightness) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mForceDozeBrightness = mForceDozeBrightness;
        this.apply(mCurrentState);
    }
    
    public void setForceHasTopUi(final boolean mForceHasTopUi) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mForceHasTopUi = mForceHasTopUi;
        this.apply(mCurrentState);
    }
    
    public void setForcePluginOpen(final boolean mForcePluginOpen) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mForcePluginOpen = mForcePluginOpen;
        this.apply(mCurrentState);
        final ForcePluginOpenListener mForcePluginOpenListener = this.mForcePluginOpenListener;
        if (mForcePluginOpenListener != null) {
            mForcePluginOpenListener.onChange(mForcePluginOpen);
        }
    }
    
    public void setForcePluginOpenListener(final ForcePluginOpenListener mForcePluginOpenListener) {
        this.mForcePluginOpenListener = mForcePluginOpenListener;
    }
    
    public void setForceWindowCollapsed(final boolean mForceCollapsed) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mForceCollapsed = mForceCollapsed;
        this.apply(mCurrentState);
    }
    
    public void setHeadsUpShowing(final boolean mHeadsUpShowing) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mHeadsUpShowing = mHeadsUpShowing;
        this.apply(mCurrentState);
    }
    
    public void setKeyguardFadingAway(final boolean mKeyguardFadingAway) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mKeyguardFadingAway = mKeyguardFadingAway;
        this.apply(mCurrentState);
    }
    
    public void setKeyguardGoingAway(final boolean mKeyguardGoingAway) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mKeyguardGoingAway = mKeyguardGoingAway;
        this.apply(mCurrentState);
    }
    
    public void setKeyguardNeedsInput(final boolean mKeyguardNeedsInput) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mKeyguardNeedsInput = mKeyguardNeedsInput;
        this.apply(mCurrentState);
    }
    
    public void setKeyguardOccluded(final boolean mKeyguardOccluded) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mKeyguardOccluded = mKeyguardOccluded;
        this.apply(mCurrentState);
    }
    
    public void setKeyguardShowing(final boolean mKeyguardShowing) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mKeyguardShowing = mKeyguardShowing;
        this.apply(mCurrentState);
    }
    
    public void setNotTouchable(final boolean mNotTouchable) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mNotTouchable = mNotTouchable;
        this.apply(mCurrentState);
    }
    
    public void setNotificationShadeFocusable(final boolean mNotificationShadeFocusable) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mNotificationShadeFocusable = mNotificationShadeFocusable;
        this.apply(mCurrentState);
    }
    
    public void setNotificationShadeView(final ViewGroup mNotificationShadeView) {
        this.mNotificationShadeView = mNotificationShadeView;
    }
    
    public void setPanelExpanded(final boolean mPanelExpanded) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mPanelExpanded = mPanelExpanded;
        this.apply(mCurrentState);
    }
    
    public void setPanelVisible(final boolean b) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mPanelVisible = b;
        mCurrentState.mNotificationShadeFocusable = b;
        this.apply(mCurrentState);
    }
    
    public void setQsExpanded(final boolean mQsExpanded) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mQsExpanded = mQsExpanded;
        this.apply(mCurrentState);
    }
    
    public void setScrimsVisibility(final int n) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mScrimsVisibility = n;
        this.apply(mCurrentState);
        this.mScrimsVisibilityListener.accept(n);
    }
    
    public void setScrimsVisibilityListener(final Consumer<Integer> mScrimsVisibilityListener) {
        if (mScrimsVisibilityListener != null && this.mScrimsVisibilityListener != mScrimsVisibilityListener) {
            this.mScrimsVisibilityListener = mScrimsVisibilityListener;
        }
    }
    
    public void setStateListener(final OtherwisedCollapsedListener mListener) {
        this.mListener = mListener;
    }
    
    public void setWallpaperSupportsAmbientMode(final boolean mWallpaperSupportsAmbientMode) {
        final State mCurrentState = this.mCurrentState;
        mCurrentState.mWallpaperSupportsAmbientMode = mWallpaperSupportsAmbientMode;
        this.apply(mCurrentState);
    }
    
    public interface ForcePluginOpenListener
    {
        void onChange(final boolean p0);
    }
    
    public interface OtherwisedCollapsedListener
    {
        void setWouldOtherwiseCollapse(final boolean p0);
    }
    
    private static class State
    {
        boolean mBackdropShowing;
        int mBackgroundBlurRadius;
        boolean mBouncerShowing;
        boolean mBubbleExpanded;
        boolean mBubblesShowing;
        boolean mDozing;
        boolean mForceCollapsed;
        boolean mForceDozeBrightness;
        boolean mForceHasTopUi;
        boolean mForcePluginOpen;
        boolean mForceUserActivity;
        boolean mHeadsUpShowing;
        boolean mKeyguardFadingAway;
        boolean mKeyguardGoingAway;
        boolean mKeyguardNeedsInput;
        boolean mKeyguardOccluded;
        boolean mKeyguardShowing;
        boolean mNotTouchable;
        boolean mNotificationShadeFocusable;
        boolean mPanelExpanded;
        boolean mPanelVisible;
        boolean mQsExpanded;
        boolean mRemoteInputActive;
        int mScrimsVisibility;
        int mStatusBarState;
        boolean mWallpaperSupportsAmbientMode;
        
        private boolean isKeyguardShowingAndNotOccluded() {
            return this.mKeyguardShowing && !this.mKeyguardOccluded;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("Window State {");
            sb.append("\n");
            final Field[] declaredFields = State.class.getDeclaredFields();
            final int length = declaredFields.length;
            int n = 0;
        Label_0081_Outer:
            while (true) {
                Label_0094: {
                    if (n >= length) {
                        break Label_0094;
                    }
                    final Field field = declaredFields[n];
                    sb.append("  ");
                    while (true) {
                        try {
                            sb.append(field.getName());
                            sb.append(": ");
                            sb.append(field.get(this));
                            sb.append("\n");
                            ++n;
                            continue Label_0081_Outer;
                            sb.append("}");
                            return sb.toString();
                        }
                        catch (IllegalAccessException ex) {
                            continue;
                        }
                        break;
                    }
                }
            }
        }
    }
}
