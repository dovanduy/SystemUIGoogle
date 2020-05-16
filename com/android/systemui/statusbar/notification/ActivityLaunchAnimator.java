// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.util.MathUtils;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.animation.ValueAnimator;
import android.os.RemoteException;
import android.view.SyncRtSurfaceTransactionApplier$SurfaceParams$Builder;
import android.view.SyncRtSurfaceTransactionApplier$SurfaceParams;
import android.graphics.Matrix;
import android.view.IRemoteAnimationFinishedCallback;
import android.view.RemoteAnimationTarget;
import android.graphics.Rect;
import android.view.SyncRtSurfaceTransactionApplier;
import android.view.IRemoteAnimationRunner$Stub;
import android.view.IRemoteAnimationRunner;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import android.view.RemoteAnimationAdapter;
import android.view.View;
import com.android.internal.policy.ScreenDecorationsUtils;
import com.android.systemui.statusbar.phone.NotificationShadeWindowViewController;
import com.android.systemui.statusbar.phone.NotificationPanelViewController;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.NotificationShadeDepthController;

public class ActivityLaunchAnimator
{
    private boolean mAnimationPending;
    private boolean mAnimationRunning;
    private Callback mCallback;
    private final NotificationShadeDepthController mDepthController;
    private boolean mIsLaunchForActivity;
    private final NotificationListContainer mNotificationContainer;
    private final NotificationPanelViewController mNotificationPanel;
    private final NotificationShadeWindowViewController mNotificationShadeWindowViewController;
    private final Runnable mTimeoutRunnable;
    private final float mWindowCornerRadius;
    
    public ActivityLaunchAnimator(final NotificationShadeWindowViewController mNotificationShadeWindowViewController, final Callback mCallback, final NotificationPanelViewController mNotificationPanel, final NotificationShadeDepthController mDepthController, final NotificationListContainer mNotificationContainer) {
        this.mTimeoutRunnable = new _$$Lambda$ActivityLaunchAnimator$l5Gj6YM2XO6z1WFQpGTriWePKVk(this);
        this.mNotificationPanel = mNotificationPanel;
        this.mNotificationContainer = mNotificationContainer;
        this.mDepthController = mDepthController;
        this.mNotificationShadeWindowViewController = mNotificationShadeWindowViewController;
        this.mCallback = mCallback;
        this.mWindowCornerRadius = ScreenDecorationsUtils.getWindowCornerRadius(mNotificationShadeWindowViewController.getView().getResources());
    }
    
    private void setAnimationPending(final boolean b) {
        this.mAnimationPending = b;
        this.mNotificationShadeWindowViewController.setExpandAnimationPending(b);
        if (b) {
            this.mNotificationShadeWindowViewController.getView().postDelayed(this.mTimeoutRunnable, 500L);
        }
        else {
            this.mNotificationShadeWindowViewController.getView().removeCallbacks(this.mTimeoutRunnable);
        }
    }
    
    public RemoteAnimationAdapter getLaunchAnimation(final View view, final boolean b) {
        if (view instanceof ExpandableNotificationRow && this.mCallback.areLaunchAnimationsEnabled() && !b) {
            return new RemoteAnimationAdapter((IRemoteAnimationRunner)new AnimationRunner((ExpandableNotificationRow)view), 400L, 250L);
        }
        return null;
    }
    
    public boolean isAnimationPending() {
        return this.mAnimationPending;
    }
    
    public boolean isAnimationRunning() {
        return this.mAnimationRunning;
    }
    
    public boolean isLaunchForActivity() {
        return this.mIsLaunchForActivity;
    }
    
    public void setLaunchResult(final int n, final boolean mIsLaunchForActivity) {
        this.mIsLaunchForActivity = mIsLaunchForActivity;
        this.setAnimationPending((n == 2 || n == 0) && this.mCallback.areLaunchAnimationsEnabled());
    }
    
    class AnimationRunner extends IRemoteAnimationRunner$Stub
    {
        private float mCornerRadius;
        private boolean mIsFullScreenLaunch;
        private final float mNotificationCornerRadius;
        private final ExpandAnimationParameters mParams;
        private final ExpandableNotificationRow mSourceNotification;
        private final SyncRtSurfaceTransactionApplier mSyncRtTransactionApplier;
        private final Rect mWindowCrop;
        
        public AnimationRunner(final ExpandableNotificationRow mSourceNotification) {
            this.mWindowCrop = new Rect();
            this.mIsFullScreenLaunch = true;
            this.mSourceNotification = mSourceNotification;
            this.mParams = new ExpandAnimationParameters();
            this.mSyncRtTransactionApplier = new SyncRtSurfaceTransactionApplier((View)this.mSourceNotification);
            this.mNotificationCornerRadius = Math.max(this.mSourceNotification.getCurrentTopRoundness(), this.mSourceNotification.getCurrentBottomRoundness());
        }
        
        private void applyParamsToNotification(final ExpandAnimationParameters expandAnimationParameters) {
            this.mSourceNotification.applyExpandAnimationParams(expandAnimationParameters);
        }
        
        private void applyParamsToNotificationShade(final ExpandAnimationParameters notificationLaunchAnimationParams) {
            ActivityLaunchAnimator.this.mNotificationContainer.applyExpandAnimationParams(notificationLaunchAnimationParams);
            ActivityLaunchAnimator.this.mNotificationPanel.applyExpandAnimationParams(notificationLaunchAnimationParams);
            ActivityLaunchAnimator.this.mDepthController.setNotificationLaunchAnimationParams(notificationLaunchAnimationParams);
        }
        
        private void applyParamsToWindow(final RemoteAnimationTarget remoteAnimationTarget) {
            final Matrix matrix = new Matrix();
            matrix.postTranslate(0.0f, (float)(this.mParams.top - remoteAnimationTarget.position.y));
            final Rect mWindowCrop = this.mWindowCrop;
            final ExpandAnimationParameters mParams = this.mParams;
            mWindowCrop.set(mParams.left, 0, mParams.right, mParams.getHeight());
            this.mSyncRtTransactionApplier.scheduleApply(true, new SyncRtSurfaceTransactionApplier$SurfaceParams[] { new SyncRtSurfaceTransactionApplier$SurfaceParams$Builder(remoteAnimationTarget.leash).withAlpha(1.0f).withMatrix(matrix).withWindowCrop(this.mWindowCrop).withLayer(remoteAnimationTarget.prefixOrderIndex).withCornerRadius(this.mCornerRadius).withVisibility(true).build() });
        }
        
        private RemoteAnimationTarget getPrimaryRemoteAnimationTarget(final RemoteAnimationTarget[] array) {
            for (final RemoteAnimationTarget remoteAnimationTarget : array) {
                if (remoteAnimationTarget.mode == 0) {
                    return remoteAnimationTarget;
                }
            }
            return null;
        }
        
        private void invokeCallback(final IRemoteAnimationFinishedCallback remoteAnimationFinishedCallback) {
            try {
                remoteAnimationFinishedCallback.onAnimationFinished();
            }
            catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
        
        private void setExpandAnimationRunning(final boolean expandAnimationRunning) {
            ActivityLaunchAnimator.this.mNotificationPanel.setLaunchingNotification(expandAnimationRunning);
            this.mSourceNotification.setExpandAnimationRunning(expandAnimationRunning);
            ActivityLaunchAnimator.this.mNotificationShadeWindowViewController.setExpandAnimationRunning(expandAnimationRunning);
            final NotificationListContainer access$200 = ActivityLaunchAnimator.this.mNotificationContainer;
            ExpandableNotificationRow mSourceNotification;
            if (expandAnimationRunning) {
                mSourceNotification = this.mSourceNotification;
            }
            else {
                mSourceNotification = null;
            }
            access$200.setExpandingNotification(mSourceNotification);
            ActivityLaunchAnimator.this.mAnimationRunning = expandAnimationRunning;
            if (!expandAnimationRunning) {
                ActivityLaunchAnimator.this.mCallback.onExpandAnimationFinished(this.mIsFullScreenLaunch);
                this.applyParamsToNotification(null);
                this.applyParamsToNotificationShade(null);
            }
        }
        
        public void onAnimationCancelled() throws RemoteException {
            this.mSourceNotification.post((Runnable)new _$$Lambda$ActivityLaunchAnimator$AnimationRunner$M_3NAwVAMqbtd1nWxQdGu3JgCNY(this));
        }
        
        public void onAnimationStart(final RemoteAnimationTarget[] array, final RemoteAnimationTarget[] array2, final IRemoteAnimationFinishedCallback remoteAnimationFinishedCallback) throws RemoteException {
            this.mSourceNotification.post((Runnable)new _$$Lambda$ActivityLaunchAnimator$AnimationRunner$sNLXzFzCbt6n0LlixbKU_lp1tVA(this, array, remoteAnimationFinishedCallback));
        }
    }
    
    public interface Callback
    {
        boolean areLaunchAnimationsEnabled();
        
        void onExpandAnimationFinished(final boolean p0);
        
        void onExpandAnimationTimedOut();
        
        void onLaunchAnimationCancelled();
    }
    
    public static class ExpandAnimationParameters
    {
        int bottom;
        int left;
        public float linearProgress;
        int parentStartClipTopAmount;
        int right;
        int startClipTopAmount;
        int[] startPosition;
        float startTranslationZ;
        int top;
        
        public int getBottom() {
            return this.bottom;
        }
        
        public int getHeight() {
            return this.bottom - this.top;
        }
        
        public int getParentStartClipTopAmount() {
            return this.parentStartClipTopAmount;
        }
        
        public float getProgress() {
            return this.linearProgress;
        }
        
        public float getProgress(final long n, final long n2) {
            return MathUtils.constrain((this.linearProgress * 400.0f - n) / n2, 0.0f, 1.0f);
        }
        
        public int getStartClipTopAmount() {
            return this.startClipTopAmount;
        }
        
        public float getStartTranslationZ() {
            return this.startTranslationZ;
        }
        
        public int getTop() {
            return this.top;
        }
        
        public int getTopChange() {
            final int startClipTopAmount = this.startClipTopAmount;
            int n;
            if (startClipTopAmount != 0.0f) {
                n = (int)MathUtils.lerp(0.0f, (float)startClipTopAmount, Interpolators.FAST_OUT_SLOW_IN.getInterpolation(this.linearProgress));
            }
            else {
                n = 0;
            }
            return Math.min(this.top - this.startPosition[1] - n, 0);
        }
        
        public int getWidth() {
            return this.right - this.left;
        }
    }
}
