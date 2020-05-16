// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.view.View$OnClickListener;
import com.android.systemui.R$string;
import com.android.systemui.R$id;
import com.android.systemui.Dependency;
import com.android.systemui.R$dimen;
import android.view.animation.AnimationUtils;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import com.android.settingslib.animation.DisappearAnimationUtils;
import android.view.ViewGroup;
import com.android.settingslib.animation.AppearAnimationUtils;

public class KeyguardPINView extends KeyguardPinBasedInputView
{
    private final AppearAnimationUtils mAppearAnimationUtils;
    private ViewGroup mContainer;
    private final DisappearAnimationUtils mDisappearAnimationUtils;
    private final DisappearAnimationUtils mDisappearAnimationUtilsLocked;
    private int mDisappearYTranslation;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private ViewGroup mRow0;
    private ViewGroup mRow1;
    private ViewGroup mRow2;
    private ViewGroup mRow3;
    private View[][] mViews;
    
    public KeyguardPINView(final Context context) {
        this(context, null);
    }
    
    public KeyguardPINView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mAppearAnimationUtils = new AppearAnimationUtils(context);
        this.mDisappearAnimationUtils = new DisappearAnimationUtils(context, 125L, 0.6f, 0.45f, AnimationUtils.loadInterpolator(super.mContext, 17563663));
        this.mDisappearAnimationUtilsLocked = new DisappearAnimationUtils(context, 187L, 0.6f, 0.45f, AnimationUtils.loadInterpolator(super.mContext, 17563663));
        this.mDisappearYTranslation = this.getResources().getDimensionPixelSize(R$dimen.disappear_y_translation);
        this.mKeyguardUpdateMonitor = Dependency.get(KeyguardUpdateMonitor.class);
    }
    
    private void enableClipping(final boolean b) {
        this.mContainer.setClipToPadding(b);
        this.mContainer.setClipChildren(b);
        this.mRow1.setClipToPadding(b);
        this.mRow2.setClipToPadding(b);
        this.mRow3.setClipToPadding(b);
        this.setClipChildren(b);
    }
    
    @Override
    protected int getPasswordTextViewId() {
        return R$id.pinEntry;
    }
    
    public int getWrongPasswordStringId() {
        return R$string.kg_wrong_pin;
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mContainer = (ViewGroup)this.findViewById(R$id.container);
        this.mRow0 = (ViewGroup)this.findViewById(R$id.row0);
        this.mRow1 = (ViewGroup)this.findViewById(R$id.row1);
        this.mRow2 = (ViewGroup)this.findViewById(R$id.row2);
        this.mRow3 = (ViewGroup)this.findViewById(R$id.row3);
        this.findViewById(R$id.divider);
        this.mViews = new View[][] { { (View)this.mRow0, null, null }, { this.findViewById(R$id.key1), this.findViewById(R$id.key2), this.findViewById(R$id.key3) }, { this.findViewById(R$id.key4), this.findViewById(R$id.key5), this.findViewById(R$id.key6) }, { this.findViewById(R$id.key7), this.findViewById(R$id.key8), this.findViewById(R$id.key9) }, { this.findViewById(R$id.delete_button), this.findViewById(R$id.key0), this.findViewById(R$id.key_enter) }, { null, super.mEcaView, null } };
        final View viewById = this.findViewById(R$id.cancel_button);
        if (viewById != null) {
            viewById.setOnClickListener((View$OnClickListener)new _$$Lambda$KeyguardPINView$32q9EwjCzWlJ6lNiw9pw0PSsPxs(this));
        }
    }
    
    @Override
    protected void resetState() {
        super.resetState();
        final SecurityMessageDisplay mSecurityMessageDisplay = super.mSecurityMessageDisplay;
        if (mSecurityMessageDisplay != null) {
            mSecurityMessageDisplay.setMessage("");
        }
    }
    
    public void startAppearAnimation() {
        this.enableClipping(false);
        this.setAlpha(1.0f);
        this.setTranslationY(this.mAppearAnimationUtils.getStartTranslation());
        AppearAnimationUtils.startTranslationYAnimation((View)this, 0L, 500L, 0.0f, this.mAppearAnimationUtils.getInterpolator());
        this.mAppearAnimationUtils.startAnimation2d(this.mViews, new Runnable() {
            @Override
            public void run() {
                KeyguardPINView.this.enableClipping(true);
            }
        });
    }
    
    @Override
    public boolean startDisappearAnimation(final Runnable runnable) {
        this.enableClipping(false);
        this.setTranslationY(0.0f);
        AppearAnimationUtils.startTranslationYAnimation((View)this, 0L, 280L, (float)this.mDisappearYTranslation, this.mDisappearAnimationUtils.getInterpolator());
        DisappearAnimationUtils disappearAnimationUtils;
        if (this.mKeyguardUpdateMonitor.needsSlowUnlockTransition()) {
            disappearAnimationUtils = this.mDisappearAnimationUtilsLocked;
        }
        else {
            disappearAnimationUtils = this.mDisappearAnimationUtils;
        }
        disappearAnimationUtils.startAnimation2d(this.mViews, new Runnable() {
            @Override
            public void run() {
                KeyguardPINView.this.enableClipping(true);
                final Runnable val$finishRunnable = runnable;
                if (val$finishRunnable != null) {
                    val$finishRunnable.run();
                }
            }
        });
        return true;
    }
}
