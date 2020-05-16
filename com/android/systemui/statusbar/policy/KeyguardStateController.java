// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

public interface KeyguardStateController extends CallbackController<Callback>
{
    long calculateGoingToFullShadeDelay();
    
    boolean canDismissLockScreen();
    
    long getKeyguardFadingAwayDelay();
    
    long getKeyguardFadingAwayDuration();
    
    default long getShortenedFadingAwayDuration() {
        if (this.isBypassFadingAnimation()) {
            return this.getKeyguardFadingAwayDuration();
        }
        return this.getKeyguardFadingAwayDuration() / 2L;
    }
    
    default boolean isBypassFadingAnimation() {
        return false;
    }
    
    default boolean isFaceAuthEnabled() {
        return false;
    }
    
    boolean isKeyguardFadingAway();
    
    boolean isKeyguardGoingAway();
    
    boolean isLaunchTransitionFadingAway();
    
    boolean isMethodSecure();
    
    boolean isOccluded();
    
    boolean isShowing();
    
    default boolean isUnlocked() {
        return !this.isShowing() || this.canDismissLockScreen();
    }
    
    default void notifyKeyguardDoneFading() {
    }
    
    default void notifyKeyguardFadingAway(final long n, final long n2, final boolean b) {
    }
    
    default void notifyKeyguardGoingAway(final boolean b) {
    }
    
    default void notifyKeyguardState(final boolean b, final boolean b2) {
    }
    
    default void setLaunchTransitionFadingAway(final boolean b) {
    }
    
    public interface Callback
    {
        default void onKeyguardFadingAwayChanged() {
        }
        
        default void onKeyguardShowingChanged() {
        }
        
        default void onUnlockedChanged() {
        }
    }
}
