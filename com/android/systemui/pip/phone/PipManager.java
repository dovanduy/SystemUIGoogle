// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip.phone;

import android.view.IPinnedStackController;
import android.content.pm.ParceledListSlice;
import android.content.res.Configuration;
import java.io.PrintWriter;
import android.window.WindowContainerTransaction;
import android.app.IActivityTaskManager;
import android.app.ActivityTaskManager;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import android.os.RemoteException;
import android.util.Log;
import com.android.systemui.shared.system.PinnedStackListenerForwarder;
import android.app.ActivityManager;
import android.app.ActivityManager$RunningTaskInfo;
import com.android.systemui.Dependency;
import com.android.systemui.UiOffloadThread;
import android.content.ComponentName;
import com.android.systemui.shared.system.WindowManagerWrapper;
import com.android.systemui.pip.PipSurfaceTransactionHelper;
import com.android.systemui.pip.PipSnapAlgorithm;
import com.android.systemui.util.DeviceConfigProxy;
import com.android.systemui.util.FloatingContentCoordinator;
import com.android.systemui.wm.DisplayController;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.view.DisplayInfo;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.wm.DisplayChangeController;
import android.graphics.Rect;
import com.android.systemui.pip.PipBoundsHandler;
import com.android.systemui.shared.recents.IPinnedStackAnimationListener;
import com.android.systemui.shared.system.InputConsumerController;
import android.os.Handler;
import android.content.Context;
import android.app.IActivityManager;
import com.android.systemui.pip.PipTaskOrganizer;
import com.android.systemui.pip.BasePipManager;

public class PipManager implements BasePipManager, PipTransitionCallback
{
    private IActivityManager mActivityManager;
    private PipAppOpsListener mAppOpsListener;
    private Context mContext;
    private Handler mHandler;
    private InputConsumerController mInputConsumerController;
    private PipMediaController mMediaController;
    private PipMenuActivityController mMenuController;
    private IPinnedStackAnimationListener mPinnedStackAnimationRecentsListener;
    private PipBoundsHandler mPipBoundsHandler;
    protected PipTaskOrganizer mPipTaskOrganizer;
    private final Rect mReentryBounds;
    private final DisplayChangeController.OnDisplayChangingListener mRotationController;
    private final TaskStackChangeListener mTaskStackListener;
    private final DisplayInfo mTmpDisplayInfo;
    private final Rect mTmpInsetBounds;
    private final Rect mTmpNormalBounds;
    private PipTouchHandler mTouchHandler;
    
    public PipManager(final Context mContext, final BroadcastDispatcher broadcastDispatcher, final DisplayController displayController, final FloatingContentCoordinator floatingContentCoordinator, final DeviceConfigProxy deviceConfigProxy, final PipBoundsHandler mPipBoundsHandler, final PipSnapAlgorithm pipSnapAlgorithm, final PipSurfaceTransactionHelper pipSurfaceTransactionHelper) {
        this.mHandler = new Handler();
        this.mTmpDisplayInfo = new DisplayInfo();
        this.mTmpInsetBounds = new Rect();
        this.mTmpNormalBounds = new Rect();
        this.mReentryBounds = new Rect();
        this.mRotationController = new _$$Lambda$PipManager$AYejaSf14FPjo5Gs0gXz_uHGoWo(this);
        this.mTaskStackListener = new TaskStackChangeListener() {
            @Override
            public void onActivityPinned(final String s, final int n, final int n2, final int n3) {
                PipManager.this.mTouchHandler.onActivityPinned();
                PipManager.this.mMediaController.onActivityPinned();
                PipManager.this.mMenuController.onActivityPinned();
                PipManager.this.mAppOpsListener.onActivityPinned(s);
                Dependency.get(UiOffloadThread.class).execute((Runnable)_$$Lambda$PipManager$1$GurLWXFKpAPDop_aRGndKBjZCWU.INSTANCE);
            }
            
            @Override
            public void onActivityRestartAttempt(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo, final boolean b, final boolean b2) {
                if (activityManager$RunningTaskInfo.configuration.windowConfiguration.getWindowingMode() != 2) {
                    return;
                }
                PipManager.this.mTouchHandler.getMotionHelper().expandPip(b2);
            }
            
            @Override
            public void onActivityUnpinned() {
                final ComponentName componentName = (ComponentName)PipUtils.getTopPipActivity(PipManager.this.mContext, PipManager.this.mActivityManager).first;
                PipManager.this.mMenuController.onActivityUnpinned();
                PipManager.this.mTouchHandler.onActivityUnpinned(componentName);
                PipManager.this.mAppOpsListener.onActivityUnpinned();
                Dependency.get(UiOffloadThread.class).execute(new _$$Lambda$PipManager$1$ngvLEQ68U0fQkcsOpQTOX3GlNKk(componentName));
            }
        };
        this.mContext = mContext;
        this.mActivityManager = ActivityManager.getService();
        try {
            WindowManagerWrapper.getInstance().addPinnedStackListener(new PipManagerPinnedStackListener());
        }
        catch (RemoteException ex) {
            Log.e("PipManager", "Failed to register pinned stack listener", (Throwable)ex);
        }
        ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mTaskStackListener);
        final IActivityTaskManager service = ActivityTaskManager.getService();
        this.mPipBoundsHandler = mPipBoundsHandler;
        (this.mPipTaskOrganizer = new PipTaskOrganizer(mContext, mPipBoundsHandler, pipSurfaceTransactionHelper)).registerPipTransitionCallback((PipTaskOrganizer.PipTransitionCallback)this);
        this.mInputConsumerController = InputConsumerController.getPipInputConsumer();
        final PipMediaController mMediaController = new PipMediaController(mContext, this.mActivityManager, broadcastDispatcher);
        this.mMediaController = mMediaController;
        final PipMenuActivityController mMenuController = new PipMenuActivityController(mContext, mMediaController, this.mInputConsumerController);
        this.mMenuController = mMenuController;
        this.mTouchHandler = new PipTouchHandler(mContext, this.mActivityManager, service, mMenuController, this.mInputConsumerController, this.mPipBoundsHandler, this.mPipTaskOrganizer, floatingContentCoordinator, deviceConfigProxy, pipSnapAlgorithm);
        this.mAppOpsListener = new PipAppOpsListener(mContext, this.mActivityManager, (PipAppOpsListener.Callback)this.mTouchHandler.getMotionHelper());
        displayController.addDisplayChangingController(this.mRotationController);
        final DisplayInfo displayInfo = new DisplayInfo();
        mContext.getDisplay().getDisplayInfo(displayInfo);
        this.mPipBoundsHandler.onDisplayInfoChanged(displayInfo);
        try {
            this.mPipTaskOrganizer.registerOrganizer(2);
            if (service.getStackInfo(2, 0) != null) {
                this.mInputConsumerController.registerInputConsumer();
            }
        }
        catch (RemoteException | UnsupportedOperationException ex2) {
            final Throwable t;
            t.printStackTrace();
        }
    }
    
    private void onPipTransitionFinishedOrCanceled() {
        this.mTouchHandler.setTouchEnabled(true);
        this.mTouchHandler.onPinnedStackAnimationEnded();
        this.mMenuController.onPinnedStackAnimationEnded();
    }
    
    private void updateMovementBounds(final Rect rect, final boolean b, final boolean b2) {
        this.mPipBoundsHandler.onMovementBoundsChanged(this.mTmpInsetBounds, this.mTmpNormalBounds, rect, this.mTmpDisplayInfo);
        this.mTouchHandler.onMovementBoundsChanged(this.mTmpInsetBounds, this.mTmpNormalBounds, rect, b, b2, this.mTmpDisplayInfo.rotation);
        this.mPipTaskOrganizer.onMovementBoundsChanged(b, b2);
    }
    
    @Override
    public void dump(final PrintWriter printWriter) {
        printWriter.println("PipManager");
        this.mInputConsumerController.dump(printWriter, "  ");
        this.mMenuController.dump(printWriter, "  ");
        this.mTouchHandler.dump(printWriter, "  ");
        this.mPipBoundsHandler.dump(printWriter, "  ");
    }
    
    @Override
    public void onConfigurationChanged(final Configuration configuration) {
        this.mTouchHandler.onConfigurationChanged();
    }
    
    @Override
    public void onPipTransitionCanceled(final ComponentName componentName, final int n) {
        this.onPipTransitionFinishedOrCanceled();
    }
    
    @Override
    public void onPipTransitionFinished(final ComponentName componentName, final int n) {
        this.onPipTransitionFinishedOrCanceled();
    }
    
    @Override
    public void onPipTransitionStarted(final ComponentName componentName, final int n) {
        if (n == 3) {
            this.mReentryBounds.set(this.mTouchHandler.getNormalBounds());
            this.mPipBoundsHandler.applySnapFraction(this.mReentryBounds, this.mPipBoundsHandler.getSnapFraction(this.mPipTaskOrganizer.getLastReportedBounds()));
            this.mPipBoundsHandler.onSaveReentryBounds(componentName, this.mReentryBounds);
        }
        this.mTouchHandler.setTouchEnabled(false);
        final IPinnedStackAnimationListener mPinnedStackAnimationRecentsListener = this.mPinnedStackAnimationRecentsListener;
        if (mPinnedStackAnimationRecentsListener != null) {
            try {
                mPinnedStackAnimationRecentsListener.onPinnedStackAnimationStarted();
            }
            catch (RemoteException ex) {
                Log.e("PipManager", "Failed to callback recents", (Throwable)ex);
            }
        }
    }
    
    @Override
    public void setPinnedStackAnimationListener(final IPinnedStackAnimationListener pinnedStackAnimationListener) {
        this.mHandler.post((Runnable)new _$$Lambda$PipManager$t2XWznriuk4XHpM7EiG9uJamHUY(this, pinnedStackAnimationListener));
    }
    
    @Override
    public void setPinnedStackAnimationType(final int n) {
        this.mHandler.post((Runnable)new _$$Lambda$PipManager$_erwmkZE5c2eLc8r_OSTlUw7erk(this, n));
    }
    
    @Override
    public void setShelfHeight(final boolean b, final int n) {
        this.mHandler.post((Runnable)new _$$Lambda$PipManager$f_jRwYFIWoME7ctX_wrfhuNp1q0(this, b, n));
    }
    
    @Override
    public void showPictureInPictureMenu() {
        this.mTouchHandler.showPictureInPictureMenu();
    }
    
    private class PipManagerPinnedStackListener extends PinnedStackListener
    {
        @Override
        public void onActionsChanged(final ParceledListSlice parceledListSlice) {
            PipManager.this.mHandler.post((Runnable)new _$$Lambda$PipManager$PipManagerPinnedStackListener$w3TtXQNx6JYy0rkssM6SOCMIiCQ(this, parceledListSlice));
        }
        
        @Override
        public void onActivityHidden(final ComponentName componentName) {
            PipManager.this.mHandler.post((Runnable)new _$$Lambda$PipManager$PipManagerPinnedStackListener$jzbSRhWFoxplnSPY2RgqZCPd1ts(this, componentName));
        }
        
        @Override
        public void onAspectRatioChanged(final float n) {
            PipManager.this.mHandler.post((Runnable)new _$$Lambda$PipManager$PipManagerPinnedStackListener$as1Gj0OwAKB_hvEWCKYdRYRFM9g(this, n));
        }
        
        @Override
        public void onConfigurationChanged() {
            PipManager.this.mHandler.post((Runnable)new _$$Lambda$PipManager$PipManagerPinnedStackListener$_tnyP4cjZoY1aQdH46PDBhGhzVU(this));
        }
        
        @Override
        public void onDisplayInfoChanged(final DisplayInfo displayInfo) {
            PipManager.this.mHandler.post((Runnable)new _$$Lambda$PipManager$PipManagerPinnedStackListener$P0_Ji3WptNFaEdrasIn3ZLSvnUM(this, displayInfo));
        }
        
        @Override
        public void onImeVisibilityChanged(final boolean b, final int n) {
            PipManager.this.mHandler.post((Runnable)new _$$Lambda$PipManager$PipManagerPinnedStackListener$u1KCCoxakH7gZKPv7iZK4aLn7MU(this, b, n));
        }
        
        @Override
        public void onListenerRegistered(final IPinnedStackController pinnedStackController) {
            PipManager.this.mHandler.post((Runnable)new _$$Lambda$PipManager$PipManagerPinnedStackListener$MK_nIqOfkRku5_KYToXZ0_DmcZA(this, pinnedStackController));
        }
        
        @Override
        public void onMovementBoundsChanged(final Rect rect, final boolean b) {
            PipManager.this.mHandler.post((Runnable)new _$$Lambda$PipManager$PipManagerPinnedStackListener$dlXdffa3Uh7wstzKiCVgHg0O3Jg(this, rect, b));
        }
    }
}
