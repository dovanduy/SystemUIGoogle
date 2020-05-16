// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.content.res.Configuration;
import android.content.res.ColorStateList;
import com.android.internal.graphics.ColorUtils;
import android.text.TextUtils;
import android.provider.Settings$Secure;
import android.os.Trace;
import android.graphics.drawable.Animatable2$AnimationCallback;
import android.graphics.drawable.AnimatedVectorDrawable;
import com.android.systemui.R$string;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.R$anim;
import android.view.ViewTreeObserver$OnPreDrawListener;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import com.android.systemui.statusbar.KeyguardAffordanceView;

public class LockIcon extends KeyguardAffordanceView
{
    private static final int[][] LOCK_ANIM_RES_IDS;
    private float mDozeAmount;
    private boolean mDozing;
    private final SparseArray<Drawable> mDrawableCache;
    private int mIconColor;
    private boolean mKeyguardJustShown;
    private int mOldState;
    private final ViewTreeObserver$OnPreDrawListener mOnPreDrawListener;
    private boolean mPredrawRegistered;
    private boolean mPulsing;
    private int mState;
    
    static {
        LOCK_ANIM_RES_IDS = new int[][] { { R$anim.lock_to_error, R$anim.lock_unlock, R$anim.lock_lock, R$anim.lock_scanning }, { R$anim.lock_to_error_circular, R$anim.lock_unlock_circular, R$anim.lock_lock_circular, R$anim.lock_scanning_circular }, { R$anim.lock_to_error_filled, R$anim.lock_unlock_filled, R$anim.lock_lock_filled, R$anim.lock_scanning_filled }, { R$anim.lock_to_error_rounded, R$anim.lock_unlock_rounded, R$anim.lock_lock_rounded, R$anim.lock_scanning_rounded } };
    }
    
    public LockIcon(final Context context, final AttributeSet set) {
        super(context, set);
        this.mDrawableCache = (SparseArray<Drawable>)new SparseArray();
        this.mOnPreDrawListener = (ViewTreeObserver$OnPreDrawListener)new ViewTreeObserver$OnPreDrawListener() {
            public boolean onPreDraw() {
                LockIcon.this.getViewTreeObserver().removeOnPreDrawListener((ViewTreeObserver$OnPreDrawListener)this);
                LockIcon.this.mPredrawRegistered = false;
                final int access$100 = LockIcon.this.mState;
                final LockIcon this$0 = LockIcon.this;
                this$0.mOldState = this$0.mState;
                final Drawable access$101 = LockIcon.this.getIcon(access$100);
                LockIcon.this.setImageDrawable(access$101, false);
                if (access$100 == 2) {
                    final LockIcon this$2 = LockIcon.this;
                    this$2.announceForAccessibility((CharSequence)this$2.getResources().getString(R$string.accessibility_scanning_face));
                }
                if (access$101 instanceof AnimatedVectorDrawable) {
                    final AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable)access$101;
                    animatedVectorDrawable.forceAnimationOnUI();
                    animatedVectorDrawable.clearAnimationCallbacks();
                    animatedVectorDrawable.registerAnimationCallback((Animatable2$AnimationCallback)new Animatable2$AnimationCallback() {
                        public void onAnimationEnd(final Drawable drawable) {
                            if (LockIcon.this.getDrawable() == animatedVectorDrawable && access$100 == LockIcon.this.mState && access$100 == 2) {
                                animatedVectorDrawable.start();
                            }
                            else {
                                Trace.endAsyncSection("LockIcon#Animation", access$100);
                            }
                        }
                    });
                    Trace.beginAsyncSection("LockIcon#Animation", access$100);
                    animatedVectorDrawable.start();
                }
                return true;
            }
        };
    }
    
    private static int getAnimationIndexForTransition(final int n, final int n2, final boolean b, final boolean b2, final boolean b3) {
        if (b2 && !b) {
            return -1;
        }
        if (n2 == 3) {
            return 0;
        }
        if (n != 1 && n2 == 1) {
            return 1;
        }
        if (n == 1 && n2 == 0 && !b3) {
            return 2;
        }
        if (n2 == 2) {
            return 3;
        }
        return -1;
    }
    
    private Drawable getIcon(int n) {
        final int animationIndexForTransition = getAnimationIndexForTransition(this.mOldState, n, this.mPulsing, this.mDozing, this.mKeyguardJustShown);
        if (animationIndexForTransition != -1) {
            n = this.getThemedAnimationResId(animationIndexForTransition);
        }
        else {
            n = getIconForState(n);
        }
        if (!this.mDrawableCache.contains(n)) {
            this.mDrawableCache.put(n, (Object)this.getResources().getDrawable(n));
        }
        return (Drawable)this.mDrawableCache.get(n);
    }
    
    private static int getIconForState(int n) {
        if (n != 0) {
            if (n == 1) {
                n = 17302469;
                return n;
            }
            if (n != 2) {
                if (n != 3) {
                    throw new IllegalArgumentException();
                }
            }
        }
        n = 17302460;
        return n;
    }
    
    private int getThemedAnimationResId(final int n) {
        final int[][] lock_ANIM_RES_IDS = LockIcon.LOCK_ANIM_RES_IDS;
        final String emptyIfNull = TextUtils.emptyIfNull(Settings$Secure.getString(this.getContext().getContentResolver(), "theme_customization_overlay_packages"));
        if (emptyIfNull.contains("com.android.theme.icon_pack.circular.android")) {
            return lock_ANIM_RES_IDS[1][n];
        }
        if (emptyIfNull.contains("com.android.theme.icon_pack.filled.android")) {
            return lock_ANIM_RES_IDS[2][n];
        }
        if (emptyIfNull.contains("com.android.theme.icon_pack.rounded.android")) {
            return lock_ANIM_RES_IDS[3][n];
        }
        return lock_ANIM_RES_IDS[0][n];
    }
    
    private void updateDarkTint() {
        this.setImageTintList(ColorStateList.valueOf(ColorUtils.blendARGB(this.mIconColor, -1, this.mDozeAmount)));
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mDrawableCache.clear();
    }
    
    void onThemeChange(final int mIconColor) {
        this.mDrawableCache.clear();
        this.mIconColor = mIconColor;
        this.updateDarkTint();
    }
    
    void setDozeAmount(final float mDozeAmount) {
        this.mDozeAmount = mDozeAmount;
        this.updateDarkTint();
    }
    
    void update(final int mState, final boolean mPulsing, final boolean mDozing, final boolean mKeyguardJustShown) {
        this.mState = mState;
        this.mPulsing = mPulsing;
        this.mDozing = mDozing;
        this.mKeyguardJustShown = mKeyguardJustShown;
        if (!this.mPredrawRegistered) {
            this.mPredrawRegistered = true;
            this.getViewTreeObserver().addOnPreDrawListener(this.mOnPreDrawListener);
        }
    }
    
    boolean updateIconVisibility(final boolean b) {
        final int visibility = this.getVisibility();
        int visibility2 = 0;
        if (b != (visibility == 0)) {
            if (!b) {
                visibility2 = 4;
            }
            this.setVisibility(visibility2);
            this.animate().cancel();
            if (b) {
                this.setScaleX(0.0f);
                this.setScaleY(0.0f);
                this.animate().setInterpolator((TimeInterpolator)Interpolators.LINEAR_OUT_SLOW_IN).scaleX(1.0f).scaleY(1.0f).withLayer().setDuration(233L).start();
            }
            return true;
        }
        return false;
    }
}
