// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.graphics.Color;
import android.os.Trace;
import com.android.systemui.statusbar.ScrimView;
import com.android.systemui.dock.DockManager;

public enum ScrimState
{
    AOD(6) {
        @Override
        public float getBehindAlpha() {
            float n;
            if (super.mWallpaperSupportsAmbientMode && !super.mHasBackdrop) {
                n = 0.0f;
            }
            else {
                n = 1.0f;
            }
            return n;
        }
        
        @Override
        public boolean isLowPowerState() {
            return true;
        }
        
        @Override
        public void prepare(final ScrimState scrimState) {
            final boolean alwaysOn = super.mDozeParameters.getAlwaysOn();
            final boolean docked = super.mDockManager.isDocked();
            super.mBlankScreen = super.mDisplayRequiresBlanking;
            super.mFrontTint = -16777216;
            float mAodFrontScrimAlpha;
            if (!alwaysOn && !docked) {
                mAodFrontScrimAlpha = 1.0f;
            }
            else {
                mAodFrontScrimAlpha = super.mAodFrontScrimAlpha;
            }
            super.mFrontAlpha = mAodFrontScrimAlpha;
            super.mBehindTint = -16777216;
            super.mBehindAlpha = 0.0f;
            super.mBubbleTint = 0;
            super.mBubbleAlpha = 0.0f;
            super.mAnimationDuration = 1000L;
            super.mAnimateChange = super.mDozeParameters.shouldControlScreenOff();
        }
    }, 
    BOUNCER(3) {
        @Override
        public void prepare(final ScrimState scrimState) {
            super.mBehindAlpha = super.mDefaultScrimAlpha;
            super.mFrontAlpha = 0.0f;
            super.mBubbleAlpha = 0.0f;
        }
    }, 
    BOUNCER_SCRIMMED(4) {
        @Override
        public void prepare(final ScrimState scrimState) {
            super.mBehindAlpha = 0.0f;
            super.mBubbleAlpha = 0.0f;
            super.mFrontAlpha = super.mDefaultScrimAlpha;
        }
    }, 
    BRIGHTNESS_MIRROR(5) {
        @Override
        public void prepare(final ScrimState scrimState) {
            super.mBehindAlpha = 0.0f;
            super.mFrontAlpha = 0.0f;
            super.mBubbleAlpha = 0.0f;
        }
    }, 
    BUBBLE_EXPANDED(9) {
        @Override
        public void prepare(final ScrimState scrimState) {
            super.mFrontTint = 0;
            super.mBehindTint = 0;
            super.mBubbleTint = 0;
            super.mFrontAlpha = 0.0f;
            final float mDefaultScrimAlpha = super.mDefaultScrimAlpha;
            super.mBehindAlpha = mDefaultScrimAlpha;
            super.mBubbleAlpha = mDefaultScrimAlpha;
            super.mAnimationDuration = 220L;
            super.mBlankScreen = false;
        }
    }, 
    KEYGUARD(2) {
        @Override
        public void prepare(final ScrimState scrimState) {
            super.mBlankScreen = false;
            if (scrimState == ScrimState.AOD) {
                super.mAnimationDuration = 500L;
                if (super.mDisplayRequiresBlanking) {
                    super.mBlankScreen = true;
                }
            }
            else if (scrimState == ScrimState.KEYGUARD) {
                super.mAnimationDuration = 500L;
            }
            else {
                super.mAnimationDuration = 220L;
            }
            super.mFrontTint = -16777216;
            super.mBehindTint = -16777216;
            super.mBubbleTint = 0;
            super.mFrontAlpha = 0.0f;
            super.mBehindAlpha = super.mScrimBehindAlphaKeyguard;
            super.mBubbleAlpha = 0.0f;
        }
    }, 
    OFF(1) {
        @Override
        public boolean isLowPowerState() {
            return true;
        }
        
        @Override
        public void prepare(final ScrimState scrimState) {
            super.mFrontTint = -16777216;
            super.mBehindTint = -16777216;
            super.mBubbleTint = scrimState.mBubbleTint;
            super.mFrontAlpha = 1.0f;
            super.mBehindAlpha = 1.0f;
            super.mBubbleAlpha = scrimState.mBubbleAlpha;
            super.mAnimationDuration = 1000L;
        }
    }, 
    PULSING(7) {
        @Override
        public float getBehindAlpha() {
            float behindAlpha;
            if (super.mWakeLockScreenSensorActive) {
                behindAlpha = 0.6f;
            }
            else {
                behindAlpha = ScrimState.AOD.getBehindAlpha();
            }
            return behindAlpha;
        }
        
        @Override
        public void prepare(final ScrimState scrimState) {
            super.mFrontAlpha = super.mAodFrontScrimAlpha;
            super.mBubbleAlpha = 0.0f;
            super.mBehindTint = -16777216;
            super.mFrontTint = -16777216;
            super.mBlankScreen = super.mDisplayRequiresBlanking;
            long mAnimationDuration;
            if (super.mWakeLockScreenSensorActive) {
                mAnimationDuration = 1000L;
            }
            else {
                mAnimationDuration = 220L;
            }
            super.mAnimationDuration = mAnimationDuration;
            if (super.mWakeLockScreenSensorActive && scrimState == ScrimState.AOD) {
                this.updateScrimColor(super.mScrimBehind, 1.0f, -16777216);
            }
        }
    }, 
    UNINITIALIZED, 
    UNLOCKED(8) {
        @Override
        public void prepare(final ScrimState scrimState) {
            super.mBehindAlpha = 0.0f;
            super.mFrontAlpha = 0.0f;
            super.mBubbleAlpha = 0.0f;
            long mKeyguardFadingAwayDuration;
            if (super.mKeyguardFadingAway) {
                mKeyguardFadingAwayDuration = super.mKeyguardFadingAwayDuration;
            }
            else {
                mKeyguardFadingAwayDuration = 300L;
            }
            super.mAnimationDuration = mKeyguardFadingAwayDuration;
            super.mAnimateChange = (super.mLaunchingAffordanceWithPreview ^ true);
            super.mFrontTint = 0;
            super.mBehindTint = 0;
            super.mBubbleTint = 0;
            super.mBlankScreen = false;
            if (scrimState == ScrimState.AOD) {
                this.updateScrimColor(super.mScrimInFront, 1.0f, -16777216);
                this.updateScrimColor(super.mScrimBehind, 1.0f, -16777216);
                this.updateScrimColor(super.mScrimForBubble, 1.0f, -16777216);
                super.mFrontTint = -16777216;
                super.mBehindTint = -16777216;
                super.mBubbleTint = -16777216;
                super.mBlankScreen = true;
            }
        }
    };
    
    boolean mAnimateChange;
    long mAnimationDuration;
    float mAodFrontScrimAlpha;
    float mBehindAlpha;
    int mBehindTint;
    boolean mBlankScreen;
    float mBubbleAlpha;
    int mBubbleTint;
    float mDefaultScrimAlpha;
    boolean mDisplayRequiresBlanking;
    DockManager mDockManager;
    DozeParameters mDozeParameters;
    float mFrontAlpha;
    int mFrontTint;
    boolean mHasBackdrop;
    boolean mKeyguardFadingAway;
    long mKeyguardFadingAwayDuration;
    boolean mLaunchingAffordanceWithPreview;
    ScrimView mScrimBehind;
    float mScrimBehindAlphaKeyguard;
    ScrimView mScrimForBubble;
    ScrimView mScrimInFront;
    boolean mWakeLockScreenSensorActive;
    boolean mWallpaperSupportsAmbientMode;
    
    private ScrimState() {
        this.mBlankScreen = false;
        this.mAnimationDuration = 220L;
        this.mFrontTint = 0;
        this.mBehindTint = 0;
        this.mBubbleTint = 0;
        this.mAnimateChange = true;
    }
    
    public boolean getAnimateChange() {
        return this.mAnimateChange;
    }
    
    public long getAnimationDuration() {
        return this.mAnimationDuration;
    }
    
    public float getBehindAlpha() {
        return this.mBehindAlpha;
    }
    
    public int getBehindTint() {
        return this.mBehindTint;
    }
    
    public boolean getBlanksScreen() {
        return this.mBlankScreen;
    }
    
    public float getBubbleAlpha() {
        return this.mBubbleAlpha;
    }
    
    public int getBubbleTint() {
        return this.mBubbleTint;
    }
    
    public float getFrontAlpha() {
        return this.mFrontAlpha;
    }
    
    public int getFrontTint() {
        return this.mFrontTint;
    }
    
    public void init(final ScrimView mScrimInFront, final ScrimView mScrimBehind, final ScrimView mScrimForBubble, final DozeParameters mDozeParameters, final DockManager mDockManager) {
        this.mScrimInFront = mScrimInFront;
        this.mScrimBehind = mScrimBehind;
        this.mScrimForBubble = mScrimForBubble;
        this.mDozeParameters = mDozeParameters;
        this.mDockManager = mDockManager;
        this.mDisplayRequiresBlanking = mDozeParameters.getDisplayNeedsBlanking();
    }
    
    public boolean isLowPowerState() {
        return false;
    }
    
    public void prepare(final ScrimState scrimState) {
    }
    
    public void setAodFrontScrimAlpha(final float mAodFrontScrimAlpha) {
        this.mAodFrontScrimAlpha = mAodFrontScrimAlpha;
    }
    
    public void setDefaultScrimAlpha(final float mDefaultScrimAlpha) {
        this.mDefaultScrimAlpha = mDefaultScrimAlpha;
    }
    
    public void setHasBackdrop(final boolean mHasBackdrop) {
        this.mHasBackdrop = mHasBackdrop;
    }
    
    public void setKeyguardFadingAway(final boolean mKeyguardFadingAway, final long mKeyguardFadingAwayDuration) {
        this.mKeyguardFadingAway = mKeyguardFadingAway;
        this.mKeyguardFadingAwayDuration = mKeyguardFadingAwayDuration;
    }
    
    public void setLaunchingAffordanceWithPreview(final boolean mLaunchingAffordanceWithPreview) {
        this.mLaunchingAffordanceWithPreview = mLaunchingAffordanceWithPreview;
    }
    
    public void setScrimBehindAlphaKeyguard(final float mScrimBehindAlphaKeyguard) {
        this.mScrimBehindAlphaKeyguard = mScrimBehindAlphaKeyguard;
    }
    
    public void setWakeLockScreenSensorActive(final boolean mWakeLockScreenSensorActive) {
        this.mWakeLockScreenSensorActive = mWakeLockScreenSensorActive;
    }
    
    public void setWallpaperSupportsAmbientMode(final boolean mWallpaperSupportsAmbientMode) {
        this.mWallpaperSupportsAmbientMode = mWallpaperSupportsAmbientMode;
    }
    
    public void updateScrimColor(final ScrimView scrimView, final float viewAlpha, final int tint) {
        String s;
        if (scrimView == this.mScrimInFront) {
            s = "front_scrim_alpha";
        }
        else {
            s = "back_scrim_alpha";
        }
        Trace.traceCounter(4096L, s, (int)(255.0f * viewAlpha));
        String s2;
        if (scrimView == this.mScrimInFront) {
            s2 = "front_scrim_tint";
        }
        else {
            s2 = "back_scrim_tint";
        }
        Trace.traceCounter(4096L, s2, Color.alpha(tint));
        scrimView.setTint(tint);
        scrimView.setViewAlpha(viewAlpha);
    }
}
