// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.feedback;

import com.google.android.systemui.elmyra.sensors.GestureSensor;
import com.android.systemui.R$id;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.phone.KeyguardBottomAreaView;

public class OpaLockscreen implements FeedbackEffect
{
    private KeyguardBottomAreaView mKeyguardBottomAreaView;
    private final KeyguardStateController mKeyguardStateController;
    private FeedbackEffect mLockscreenOpaLayout;
    private final StatusBar mStatusBar;
    
    static {
        new DecelerateInterpolator();
        new AccelerateInterpolator();
    }
    
    public OpaLockscreen(final StatusBar mStatusBar, final KeyguardStateController mKeyguardStateController) {
        this.mStatusBar = mStatusBar;
        this.mKeyguardStateController = mKeyguardStateController;
        this.refreshLockscreenOpaLayout();
    }
    
    private void refreshLockscreenOpaLayout() {
        if (this.mStatusBar.getKeyguardBottomAreaView() != null && this.mKeyguardStateController.isShowing()) {
            final KeyguardBottomAreaView keyguardBottomAreaView = this.mStatusBar.getKeyguardBottomAreaView();
            if (this.mLockscreenOpaLayout == null || !keyguardBottomAreaView.equals(this.mKeyguardBottomAreaView)) {
                this.mKeyguardBottomAreaView = keyguardBottomAreaView;
                final FeedbackEffect mLockscreenOpaLayout = this.mLockscreenOpaLayout;
                if (mLockscreenOpaLayout != null) {
                    mLockscreenOpaLayout.onRelease();
                }
                this.mLockscreenOpaLayout = (FeedbackEffect)keyguardBottomAreaView.findViewById(R$id.lockscreen_opa);
            }
            return;
        }
        this.mKeyguardBottomAreaView = null;
        this.mLockscreenOpaLayout = null;
    }
    
    @Override
    public void onProgress(final float n, final int n2) {
        this.refreshLockscreenOpaLayout();
        final FeedbackEffect mLockscreenOpaLayout = this.mLockscreenOpaLayout;
        if (mLockscreenOpaLayout != null) {
            mLockscreenOpaLayout.onProgress(n, n2);
        }
    }
    
    @Override
    public void onRelease() {
        this.refreshLockscreenOpaLayout();
        final FeedbackEffect mLockscreenOpaLayout = this.mLockscreenOpaLayout;
        if (mLockscreenOpaLayout != null) {
            mLockscreenOpaLayout.onRelease();
        }
    }
    
    @Override
    public void onResolve(final GestureSensor.DetectionProperties detectionProperties) {
        this.refreshLockscreenOpaLayout();
        final FeedbackEffect mLockscreenOpaLayout = this.mLockscreenOpaLayout;
        if (mLockscreenOpaLayout != null) {
            mLockscreenOpaLayout.onResolve(detectionProperties);
        }
    }
}
