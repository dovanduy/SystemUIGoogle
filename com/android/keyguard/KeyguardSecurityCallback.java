// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

public interface KeyguardSecurityCallback
{
    void dismiss(final boolean p0, final int p1);
    
    default void onCancelClicked() {
    }
    
    void onUserInput();
    
    void reportUnlockAttempt(final int p0, final boolean p1, final int p2);
    
    void reset();
    
    void userActivity();
}
