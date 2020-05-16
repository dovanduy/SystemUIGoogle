// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import java.util.Optional;

class FlingVelocityWrapper
{
    private boolean mGuarded;
    private float mVelocity;
    
    FlingVelocityWrapper() {
        this.mGuarded = true;
    }
    
    Optional<Float> consumeVelocity() {
        if (this.mGuarded) {
            return Optional.empty();
        }
        this.mGuarded = true;
        return Optional.of(this.mVelocity);
    }
    
    float getVelocity() {
        return this.mVelocity;
    }
    
    void setVelocity(final float mVelocity) {
        this.mVelocity = mVelocity;
        this.mGuarded = false;
    }
}
