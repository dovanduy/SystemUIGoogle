// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.content.res.ColorStateList;
import com.android.internal.widget.LockPatternUtils;
import android.view.MotionEvent;

public interface KeyguardSecurityView
{
    default boolean disallowInterceptTouch(final MotionEvent motionEvent) {
        return false;
    }
    
    CharSequence getTitle();
    
    boolean needsInput();
    
    void onPause();
    
    void onResume(final int p0);
    
    void reset();
    
    void setKeyguardCallback(final KeyguardSecurityCallback p0);
    
    void setLockPatternUtils(final LockPatternUtils p0);
    
    void showMessage(final CharSequence p0, final ColorStateList p1);
    
    void showPromptReason(final int p0);
    
    void startAppearAnimation();
    
    boolean startDisappearAnimation(final Runnable p0);
}
