// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.feedback;

import com.google.android.systemui.elmyra.sensors.GestureSensor;
import com.google.android.systemui.assist.AssistManagerGoogle;

public class AssistInvocationEffect implements FeedbackEffect
{
    private final AssistManagerGoogle mAssistManager;
    private final FeedbackEffect mOpaHomeButton;
    private final FeedbackEffect mOpaLockscreen;
    
    public AssistInvocationEffect(final AssistManagerGoogle mAssistManager, final OpaHomeButton mOpaHomeButton, final OpaLockscreen mOpaLockscreen) {
        this.mAssistManager = mAssistManager;
        this.mOpaHomeButton = mOpaHomeButton;
        this.mOpaLockscreen = mOpaLockscreen;
    }
    
    @Override
    public void onProgress(final float n, final int n2) {
        if (this.mAssistManager.shouldUseHomeButtonAnimations()) {
            this.mOpaHomeButton.onProgress(n, n2);
            this.mOpaLockscreen.onProgress(n, n2);
        }
        else {
            this.mAssistManager.onInvocationProgress(2, n);
        }
    }
    
    @Override
    public void onRelease() {
        if (this.mAssistManager.shouldUseHomeButtonAnimations()) {
            this.mOpaHomeButton.onRelease();
            this.mOpaLockscreen.onRelease();
        }
        else {
            this.mAssistManager.onInvocationProgress(2, 0.0f);
        }
    }
    
    @Override
    public void onResolve(final GestureSensor.DetectionProperties detectionProperties) {
        if (this.mAssistManager.shouldUseHomeButtonAnimations()) {
            this.mOpaHomeButton.onResolve(detectionProperties);
            this.mOpaLockscreen.onResolve(detectionProperties);
        }
        else {
            this.mAssistManager.onInvocationProgress(2, 1.0f);
        }
    }
}
