// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip.tv;

import android.view.View$OnClickListener;
import android.graphics.drawable.Drawable;
import android.animation.AnimatorInflater;
import com.android.systemui.R$anim;
import android.content.res.TypedArray;
import com.android.systemui.R$id;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View$OnFocusChangeListener;
import android.widget.TextView;
import android.widget.ImageView;
import android.animation.Animator;
import android.widget.RelativeLayout;

public class PipControlButtonView extends RelativeLayout
{
    private Animator mButtonFocusGainAnimator;
    private Animator mButtonFocusLossAnimator;
    ImageView mButtonImageView;
    private TextView mDescriptionTextView;
    private View$OnFocusChangeListener mFocusChangeListener;
    private ImageView mIconImageView;
    private final View$OnFocusChangeListener mInternalFocusChangeListener;
    private Animator mTextFocusGainAnimator;
    private Animator mTextFocusLossAnimator;
    
    public PipControlButtonView(final Context context) {
        this(context, null, 0, 0);
    }
    
    public PipControlButtonView(final Context context, final AttributeSet set) {
        this(context, set, 0, 0);
    }
    
    public PipControlButtonView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public PipControlButtonView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mInternalFocusChangeListener = (View$OnFocusChangeListener)new View$OnFocusChangeListener() {
            public void onFocusChange(final View view, final boolean b) {
                if (b) {
                    PipControlButtonView.this.startFocusGainAnimation();
                }
                else {
                    PipControlButtonView.this.startFocusLossAnimation();
                }
                if (PipControlButtonView.this.mFocusChangeListener != null) {
                    PipControlButtonView.this.mFocusChangeListener.onFocusChange((View)PipControlButtonView.this, b);
                }
            }
        };
        ((LayoutInflater)this.getContext().getSystemService("layout_inflater")).inflate(R$layout.tv_pip_control_button, (ViewGroup)this);
        this.mIconImageView = (ImageView)this.findViewById(R$id.icon);
        this.mButtonImageView = (ImageView)this.findViewById(R$id.button);
        this.mDescriptionTextView = (TextView)this.findViewById(R$id.desc);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, new int[] { 16843033, 16843087 }, n, n2);
        this.setImageResource(obtainStyledAttributes.getResourceId(0, 0));
        this.setText(obtainStyledAttributes.getResourceId(1, 0));
        obtainStyledAttributes.recycle();
    }
    
    private static void cancelAnimator(final Animator animator) {
        if (animator.isStarted()) {
            animator.cancel();
        }
    }
    
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mButtonImageView.setOnFocusChangeListener(this.mInternalFocusChangeListener);
        (this.mTextFocusGainAnimator = AnimatorInflater.loadAnimator(this.getContext(), R$anim.tv_pip_controls_focus_gain_animation)).setTarget((Object)this.mDescriptionTextView);
        (this.mButtonFocusGainAnimator = AnimatorInflater.loadAnimator(this.getContext(), R$anim.tv_pip_controls_focus_gain_animation)).setTarget((Object)this.mButtonImageView);
        (this.mTextFocusLossAnimator = AnimatorInflater.loadAnimator(this.getContext(), R$anim.tv_pip_controls_focus_loss_animation)).setTarget((Object)this.mDescriptionTextView);
        (this.mButtonFocusLossAnimator = AnimatorInflater.loadAnimator(this.getContext(), R$anim.tv_pip_controls_focus_loss_animation)).setTarget((Object)this.mButtonImageView);
    }
    
    public void setImageDrawable(final Drawable imageDrawable) {
        this.mIconImageView.setImageDrawable(imageDrawable);
    }
    
    public void setImageResource(final int imageResource) {
        if (imageResource != 0) {
            this.mIconImageView.setImageResource(imageResource);
        }
    }
    
    public void setOnClickListener(final View$OnClickListener onClickListener) {
        this.mButtonImageView.setOnClickListener(onClickListener);
    }
    
    public void setOnFocusChangeListener(final View$OnFocusChangeListener mFocusChangeListener) {
        this.mFocusChangeListener = mFocusChangeListener;
    }
    
    public void setText(final int text) {
        if (text != 0) {
            this.mButtonImageView.setContentDescription((CharSequence)this.getContext().getString(text));
            this.mDescriptionTextView.setText(text);
        }
    }
    
    public void setText(final CharSequence charSequence) {
        this.mButtonImageView.setContentDescription(charSequence);
        this.mDescriptionTextView.setText(charSequence);
    }
    
    public void startFocusGainAnimation() {
        cancelAnimator(this.mButtonFocusLossAnimator);
        cancelAnimator(this.mTextFocusLossAnimator);
        this.mTextFocusGainAnimator.start();
        if (this.mButtonImageView.getAlpha() < 1.0f) {
            this.mButtonFocusGainAnimator.start();
        }
    }
    
    public void startFocusLossAnimation() {
        cancelAnimator(this.mButtonFocusGainAnimator);
        cancelAnimator(this.mTextFocusGainAnimator);
        this.mTextFocusLossAnimator.start();
        if (this.mButtonImageView.hasFocus()) {
            this.mButtonFocusLossAnimator.start();
        }
    }
}
