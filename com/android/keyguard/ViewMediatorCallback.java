// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

public interface ViewMediatorCallback
{
    CharSequence consumeCustomMessage();
    
    int getBouncerPromptReason();
    
    boolean isScreenOn();
    
    void keyguardDone(final boolean p0, final int p1);
    
    void keyguardDoneDrawing();
    
    void keyguardDonePending(final boolean p0, final int p1);
    
    void keyguardGone();
    
    void onBouncerVisiblityChanged(final boolean p0);
    
    void onCancelClicked();
    
    void playTrustedSound();
    
    void readyForKeyguardDone();
    
    void resetKeyguard();
    
    void setNeedsInput(final boolean p0);
    
    void userActivity();
}
