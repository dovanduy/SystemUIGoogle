// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.os.Bundle;
import android.view.ViewRootImpl;

public interface KeyguardViewController
{
    void dismissAndCollapse();
    
    ViewRootImpl getViewRootImpl();
    
    void hide(final long p0, final long p1);
    
    boolean isGoingToNotificationShade();
    
    boolean isShowing();
    
    boolean isUnlockWithWallpaper();
    
    void keyguardGoingAway();
    
    void onCancelClicked();
    
    default void onFinishedGoingToSleep() {
    }
    
    default void onScreenTurnedOn() {
    }
    
    default void onScreenTurningOn() {
    }
    
    default void onStartedGoingToSleep() {
    }
    
    default void onStartedWakingUp() {
    }
    
    void reset(final boolean p0);
    
    void setNeedsInput(final boolean p0);
    
    void setOccluded(final boolean p0, final boolean p1);
    
    boolean shouldDisableWindowAnimationsForUnlock();
    
    boolean shouldSubtleWindowAnimationsForUnlock();
    
    void show(final Bundle p0);
    
    void startPreHideAnimation(final Runnable p0);
}
