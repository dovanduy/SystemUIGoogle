// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip;

import android.content.ComponentName;
import java.util.Objects;
import java.util.function.Consumer;
import com.android.internal.os.SomeArgs;
import android.os.Message;
import android.content.pm.ActivityInfo$WindowLayout;
import android.util.Size;
import android.content.pm.ActivityInfo;
import android.app.PictureInPictureParams;
import android.window.WindowOrganizer;
import android.window.WindowContainerTransaction;
import android.util.Log;
import com.android.systemui.R$integer;
import com.android.systemui.pip.phone.PipUpdateThread;
import android.os.Looper;
import android.view.SurfaceControl$Transaction;
import java.util.HashMap;
import java.util.ArrayList;
import android.content.Context;
import android.os.Handler$Callback;
import android.window.WindowContainerToken;
import android.app.ActivityManager$RunningTaskInfo;
import java.util.List;
import android.os.Handler;
import android.view.SurfaceControl;
import android.graphics.Rect;
import android.os.IBinder;
import java.util.Map;
import android.window.TaskOrganizer;

public class PipTaskOrganizer extends TaskOrganizer
{
    private static final String TAG;
    private final Map<IBinder, Rect> mBoundsToRestore;
    private final int mEnterExitAnimationDuration;
    private boolean mInPip;
    private final Rect mLastReportedBounds;
    private SurfaceControl mLeash;
    private final Handler mMainHandler;
    private int mOneShotAnimationType;
    private final PipAnimationController.PipAnimationCallback mPipAnimationCallback;
    private final PipAnimationController mPipAnimationController;
    private final PipBoundsHandler mPipBoundsHandler;
    private final List<PipTransitionCallback> mPipTransitionCallbacks;
    private PipSurfaceTransactionHelper.SurfaceControlTransactionFactory mSurfaceControlTransactionFactory;
    private final PipSurfaceTransactionHelper mSurfaceTransactionHelper;
    private ActivityManager$RunningTaskInfo mTaskInfo;
    private WindowContainerToken mToken;
    private Handler$Callback mUpdateCallbacks;
    private final Handler mUpdateHandler;
    
    static {
        TAG = PipTaskOrganizer.class.getSimpleName();
    }
    
    public PipTaskOrganizer(final Context context, final PipBoundsHandler mPipBoundsHandler, final PipSurfaceTransactionHelper mSurfaceTransactionHelper) {
        this.mPipTransitionCallbacks = new ArrayList<PipTransitionCallback>();
        this.mLastReportedBounds = new Rect();
        this.mBoundsToRestore = new HashMap<IBinder, Rect>();
        this.mPipAnimationCallback = new PipAnimationController.PipAnimationCallback() {
            @Override
            public void onPipAnimationCancel(final PipTransitionAnimator pipTransitionAnimator) {
                PipTaskOrganizer.this.mMainHandler.post((Runnable)new _$$Lambda$PipTaskOrganizer$1$ECmQdXQo0097_5OFv_Jd_ng68kI(this, pipTransitionAnimator));
            }
            
            @Override
            public void onPipAnimationEnd(final SurfaceControl$Transaction surfaceControl$Transaction, final PipTransitionAnimator pipTransitionAnimator) {
                PipTaskOrganizer.this.finishResize(surfaceControl$Transaction, pipTransitionAnimator.getDestinationBounds(), pipTransitionAnimator.getTransitionDirection());
                PipTaskOrganizer.this.mMainHandler.post((Runnable)new _$$Lambda$PipTaskOrganizer$1$sTXxTgcNucnl3Xer4qcQfLkTurA(this, pipTransitionAnimator));
            }
            
            @Override
            public void onPipAnimationStart(final PipTransitionAnimator pipTransitionAnimator) {
                PipTaskOrganizer.this.mMainHandler.post((Runnable)new _$$Lambda$PipTaskOrganizer$1$niLuhdQ_pJ1hzhK_ghTYdHbkWpY(this, pipTransitionAnimator));
            }
        };
        this.mUpdateCallbacks = (Handler$Callback)new _$$Lambda$PipTaskOrganizer$5wOr4WCerG3hW1uyUvJi4O0nPDE(this);
        this.mOneShotAnimationType = 0;
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mUpdateHandler = new Handler(PipUpdateThread.get().getLooper(), this.mUpdateCallbacks);
        this.mPipBoundsHandler = mPipBoundsHandler;
        this.mEnterExitAnimationDuration = context.getResources().getInteger(R$integer.config_pipResizeAnimationDuration);
        this.mSurfaceTransactionHelper = mSurfaceTransactionHelper;
        this.mPipAnimationController = new PipAnimationController(context, mSurfaceTransactionHelper);
        this.mSurfaceControlTransactionFactory = (PipSurfaceTransactionHelper.SurfaceControlTransactionFactory)_$$Lambda$0FLZQAxNoOm85ohJ3bgjkYQDWsU.INSTANCE;
    }
    
    private void animateResizePip(final Rect rect, final Rect rect2, final int n, final int n2) {
        if (Looper.myLooper() != this.mUpdateHandler.getLooper()) {
            throw new RuntimeException("Callers should call scheduleAnimateResizePip() instead of this directly");
        }
        if (this.mToken != null && this.mLeash != null) {
            this.mUpdateHandler.post((Runnable)new _$$Lambda$PipTaskOrganizer$UThb1C4CG3JKRXSQ8cKQ1U8MqUE(this, rect, rect2, n, n2));
            return;
        }
        Log.w(PipTaskOrganizer.TAG, "Abort animation, invalid leash");
    }
    
    private void finishResize(final SurfaceControl$Transaction surfaceControl$Transaction, Rect rect, final int n) {
        if (Looper.myLooper() == this.mUpdateHandler.getLooper()) {
            this.mLastReportedBounds.set(rect);
            final WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
            if (n == 3) {
                rect = null;
                windowContainerTransaction.setWindowingMode(this.mToken, 0).setActivityWindowingMode(this.mToken, 0);
            }
            if (n == 2) {
                windowContainerTransaction.scheduleFinishEnterPip(this.mToken, rect);
            }
            else {
                windowContainerTransaction.setBounds(this.mToken, rect);
            }
            windowContainerTransaction.setBoundsChangeTransaction(this.mToken, surfaceControl$Transaction);
            WindowOrganizer.applyTransaction(windowContainerTransaction);
            return;
        }
        throw new RuntimeException("Callers should call scheduleResizePip() instead of this directly");
    }
    
    private float getAspectRatioOrDefault(final PictureInPictureParams pictureInPictureParams) {
        float n;
        if (pictureInPictureParams == null) {
            n = this.mPipBoundsHandler.getDefaultAspectRatio();
        }
        else {
            n = pictureInPictureParams.getAspectRatio();
        }
        return n;
    }
    
    private Size getMinimalSize(final ActivityInfo activityInfo) {
        Size size2;
        final Size size = size2 = null;
        if (activityInfo != null) {
            final ActivityInfo$WindowLayout windowLayout = activityInfo.windowLayout;
            if (windowLayout == null) {
                size2 = size;
            }
            else {
                size2 = size;
                if (windowLayout.minWidth > 0) {
                    size2 = size;
                    if (windowLayout.minHeight > 0) {
                        size2 = new Size(windowLayout.minWidth, windowLayout.minHeight);
                    }
                }
            }
        }
        return size2;
    }
    
    private void offsetPip(final Rect rect, final int n, final int n2, final int n3) {
        if (Looper.myLooper() != this.mUpdateHandler.getLooper()) {
            throw new RuntimeException("Callers should call scheduleOffsetPip() instead of this directly");
        }
        if (this.mTaskInfo == null) {
            Log.w(PipTaskOrganizer.TAG, "mTaskInfo is not set");
            return;
        }
        final Rect rect2 = new Rect(rect);
        rect2.offset(n, n2);
        this.animateResizePip(rect, rect2, 1, n3);
    }
    
    private void resizePip(final Rect rect) {
        if (Looper.myLooper() != this.mUpdateHandler.getLooper()) {
            throw new RuntimeException("Callers should call scheduleResizePip() instead of this directly");
        }
        if (this.mToken != null && this.mLeash != null) {
            final SurfaceControl$Transaction transaction = this.mSurfaceControlTransactionFactory.getTransaction();
            final PipSurfaceTransactionHelper mSurfaceTransactionHelper = this.mSurfaceTransactionHelper;
            mSurfaceTransactionHelper.crop(transaction, this.mLeash, rect);
            mSurfaceTransactionHelper.round(transaction, this.mLeash, this.mInPip);
            transaction.apply();
            return;
        }
        Log.w(PipTaskOrganizer.TAG, "Abort animation, invalid leash");
    }
    
    private void scheduleAnimateResizePip(final Rect arg2, final Rect arg3, final int argi1, final int argi2, final Consumer<Rect> arg4) {
        if (!this.mInPip) {
            return;
        }
        final SomeArgs obtain = SomeArgs.obtain();
        obtain.arg1 = arg4;
        obtain.arg2 = arg2;
        obtain.arg3 = arg3;
        obtain.argi1 = argi1;
        obtain.argi2 = argi2;
        final Handler mUpdateHandler = this.mUpdateHandler;
        mUpdateHandler.sendMessage(mUpdateHandler.obtainMessage(2, (Object)obtain));
    }
    
    private void scheduleFinishResizePip(final SurfaceControl$Transaction arg2, final Rect arg3, final int argi1, final Consumer<Rect> arg4) {
        final SomeArgs obtain = SomeArgs.obtain();
        obtain.arg1 = arg4;
        obtain.arg2 = arg2;
        obtain.arg3 = arg3;
        obtain.argi1 = argi1;
        final Handler mUpdateHandler = this.mUpdateHandler;
        mUpdateHandler.sendMessage(mUpdateHandler.obtainMessage(4, (Object)obtain));
    }
    
    private boolean shouldUpdateDestinationBounds(final PictureInPictureParams pictureInPictureParams) {
        boolean b = true;
        if (pictureInPictureParams != null) {
            final PictureInPictureParams pictureInPictureParams2 = this.mTaskInfo.pictureInPictureParams;
            if (pictureInPictureParams2 != null) {
                return Objects.equals(pictureInPictureParams2.getAspectRatioRational(), pictureInPictureParams.getAspectRatioRational()) ^ true;
            }
        }
        if (pictureInPictureParams == this.mTaskInfo.pictureInPictureParams) {
            b = false;
        }
        return b;
    }
    
    private void userResizePip(final Rect rect, final Rect rect2) {
        if (Looper.myLooper() != this.mUpdateHandler.getLooper()) {
            throw new RuntimeException("Callers should call scheduleUserResizePip() instead of this directly");
        }
        if (this.mToken != null && this.mLeash != null) {
            final SurfaceControl$Transaction transaction = this.mSurfaceControlTransactionFactory.getTransaction();
            this.mSurfaceTransactionHelper.scale(transaction, this.mLeash, rect, rect2);
            transaction.apply();
            return;
        }
        Log.w(PipTaskOrganizer.TAG, "Abort animation, invalid leash");
    }
    
    public void dismissPip(final int n) {
        final WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
        windowContainerTransaction.setActivityWindowingMode(this.mToken, 1);
        WindowOrganizer.applyTransaction(windowContainerTransaction);
        this.scheduleAnimateResizePip(this.mLastReportedBounds, this.mBoundsToRestore.remove(this.mToken.asBinder()), 3, n, null);
        this.mInPip = false;
    }
    
    public Rect getLastReportedBounds() {
        return new Rect(this.mLastReportedBounds);
    }
    
    public Handler getUpdateHandler() {
        return this.mUpdateHandler;
    }
    
    public void onBackPressedOnTaskRoot(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo) {
    }
    
    public void onMovementBoundsChanged(final boolean b, final boolean b2) {
        final PipAnimationController.PipTransitionAnimator currentAnimator = this.mPipAnimationController.getCurrentAnimator();
        if (currentAnimator != null && currentAnimator.isRunning()) {
            if (currentAnimator.getTransitionDirection() == 2) {
                final Rect destinationBounds = currentAnimator.getDestinationBounds();
                if (!b && !b2 && this.mPipBoundsHandler.getDisplayBounds().contains(destinationBounds)) {
                    return;
                }
                final PipBoundsHandler mPipBoundsHandler = this.mPipBoundsHandler;
                final ActivityManager$RunningTaskInfo mTaskInfo = this.mTaskInfo;
                final Rect destinationBounds2 = mPipBoundsHandler.getDestinationBounds(mTaskInfo.topActivity, this.getAspectRatioOrDefault(mTaskInfo.pictureInPictureParams), null, this.getMinimalSize(this.mTaskInfo.topActivityInfo));
                if (destinationBounds2.equals((Object)destinationBounds)) {
                    return;
                }
                if (currentAnimator.getAnimationType() == 0) {
                    currentAnimator.updateEndValue(destinationBounds2);
                }
                currentAnimator.setDestinationBounds(destinationBounds2);
            }
        }
    }
    
    public void onTaskAppeared(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo) {
        Objects.requireNonNull(activityManager$RunningTaskInfo, "Requires RunningTaskInfo");
        final Rect destinationBounds = this.mPipBoundsHandler.getDestinationBounds(activityManager$RunningTaskInfo.topActivity, this.getAspectRatioOrDefault(activityManager$RunningTaskInfo.pictureInPictureParams), null, this.getMinimalSize(activityManager$RunningTaskInfo.topActivityInfo));
        Objects.requireNonNull(destinationBounds, "Missing destination bounds");
        this.mTaskInfo = activityManager$RunningTaskInfo;
        final WindowContainerToken token = activityManager$RunningTaskInfo.token;
        this.mToken = token;
        this.mInPip = true;
        this.mLeash = token.getLeash();
        final Rect bounds = this.mTaskInfo.configuration.windowConfiguration.getBounds();
        this.mBoundsToRestore.put(this.mToken.asBinder(), bounds);
        final int mOneShotAnimationType = this.mOneShotAnimationType;
        if (mOneShotAnimationType == 0) {
            this.scheduleAnimateResizePip(bounds, destinationBounds, 2, this.mEnterExitAnimationDuration, null);
        }
        else {
            if (mOneShotAnimationType != 1) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Unrecognized animation type: ");
                sb.append(this.mOneShotAnimationType);
                throw new RuntimeException(sb.toString());
            }
            this.mUpdateHandler.post((Runnable)new _$$Lambda$PipTaskOrganizer$0QJa2p0Z7tzo0SyffDR3Hs_xA1U(this, destinationBounds));
            this.mOneShotAnimationType = 0;
        }
    }
    
    public void onTaskInfoChanged(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo) {
        final PictureInPictureParams pictureInPictureParams = activityManager$RunningTaskInfo.pictureInPictureParams;
        if (!this.shouldUpdateDestinationBounds(pictureInPictureParams)) {
            final String tag = PipTaskOrganizer.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("Ignored onTaskInfoChanged with PiP param: ");
            sb.append(pictureInPictureParams);
            Log.d(tag, sb.toString());
            return;
        }
        final Rect destinationBounds = this.mPipBoundsHandler.getDestinationBounds(activityManager$RunningTaskInfo.topActivity, this.getAspectRatioOrDefault(pictureInPictureParams), null, this.getMinimalSize(activityManager$RunningTaskInfo.topActivityInfo));
        Objects.requireNonNull(destinationBounds, "Missing destination bounds");
        this.scheduleAnimateResizePip(destinationBounds, this.mEnterExitAnimationDuration, null);
    }
    
    public void onTaskVanished(final ActivityManager$RunningTaskInfo activityManager$RunningTaskInfo) {
        final WindowContainerToken token = activityManager$RunningTaskInfo.token;
        Objects.requireNonNull(token, "Requires valid WindowContainerToken");
        if (token.asBinder() != this.mToken.asBinder()) {
            final String tag = PipTaskOrganizer.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("Unrecognized token: ");
            sb.append(token);
            Log.wtf(tag, sb.toString());
            return;
        }
        this.scheduleAnimateResizePip(this.mLastReportedBounds, this.mBoundsToRestore.remove(token.asBinder()), 3, this.mEnterExitAnimationDuration, null);
        this.mInPip = false;
    }
    
    public void registerPipTransitionCallback(final PipTransitionCallback pipTransitionCallback) {
        this.mPipTransitionCallbacks.add(pipTransitionCallback);
    }
    
    public void scheduleAnimateResizePip(final Rect rect, final int n, final Consumer<Rect> consumer) {
        this.scheduleAnimateResizePip(this.mLastReportedBounds, rect, 0, n, consumer);
    }
    
    public void scheduleFinishResizePip(final Rect rect) {
        final SurfaceControl$Transaction transaction = this.mSurfaceControlTransactionFactory.getTransaction();
        final PipSurfaceTransactionHelper mSurfaceTransactionHelper = this.mSurfaceTransactionHelper;
        mSurfaceTransactionHelper.crop(transaction, this.mLeash, rect);
        mSurfaceTransactionHelper.resetScale(transaction, this.mLeash, rect);
        mSurfaceTransactionHelper.round(transaction, this.mLeash, this.mInPip);
        this.scheduleFinishResizePip(transaction, rect, 0, null);
    }
    
    public void scheduleOffsetPip(final Rect arg2, final int argi1, final int argi2, final Consumer<Rect> arg3) {
        if (!this.mInPip) {
            return;
        }
        final SomeArgs obtain = SomeArgs.obtain();
        obtain.arg1 = arg3;
        obtain.arg2 = arg2;
        obtain.argi1 = argi1;
        obtain.argi2 = argi2;
        final Handler mUpdateHandler = this.mUpdateHandler;
        mUpdateHandler.sendMessage(mUpdateHandler.obtainMessage(3, (Object)obtain));
    }
    
    public void scheduleResizePip(final Rect arg2, final Consumer<Rect> arg3) {
        final SomeArgs obtain = SomeArgs.obtain();
        obtain.arg1 = arg3;
        obtain.arg2 = arg2;
        final Handler mUpdateHandler = this.mUpdateHandler;
        mUpdateHandler.sendMessage(mUpdateHandler.obtainMessage(1, (Object)obtain));
    }
    
    public void scheduleUserResizePip(final Rect arg2, final Rect arg3, final Consumer<Rect> arg4) {
        final SomeArgs obtain = SomeArgs.obtain();
        obtain.arg1 = arg4;
        obtain.arg2 = arg2;
        obtain.arg3 = arg3;
        final Handler mUpdateHandler = this.mUpdateHandler;
        mUpdateHandler.sendMessage(mUpdateHandler.obtainMessage(5, (Object)obtain));
    }
    
    public void setOneShotAnimationType(final int mOneShotAnimationType) {
        this.mOneShotAnimationType = mOneShotAnimationType;
    }
    
    public interface PipTransitionCallback
    {
        void onPipTransitionCanceled(final ComponentName p0, final int p1);
        
        void onPipTransitionFinished(final ComponentName p0, final int p1);
        
        void onPipTransitionStarted(final ComponentName p0, final int p1);
    }
}
