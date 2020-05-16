// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.content.res.Configuration;
import android.view.ViewGroup$LayoutParams;
import com.android.systemui.Dependency;
import com.android.systemui.R$string;
import com.android.systemui.R$dimen;
import com.android.systemui.R$color;
import android.util.AttributeSet;
import android.content.Context;
import android.view.animation.DecelerateInterpolator;
import com.android.systemui.statusbar.policy.ConfigurationController;
import android.widget.TextView;

public class PromptView extends TextView implements ConfigurationListener
{
    private final DecelerateInterpolator mDecelerateInterpolator;
    private boolean mEnabled;
    private String mHandleString;
    private boolean mHasDarkBackground;
    private int mLastInvocationType;
    private final float mRiseDistance;
    private String mSqueezeString;
    private final int mTextColorDark;
    private final int mTextColorLight;
    
    public PromptView(final Context context) {
        this(context, null);
    }
    
    public PromptView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public PromptView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public PromptView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mDecelerateInterpolator = new DecelerateInterpolator(2.0f);
        this.mHasDarkBackground = false;
        this.mEnabled = false;
        this.mLastInvocationType = 0;
        this.mTextColorDark = this.getContext().getColor(R$color.transcription_text_dark);
        this.mTextColorLight = this.getContext().getColor(R$color.transcription_text_light);
        this.mRiseDistance = this.getResources().getDimension(R$dimen.assist_prompt_rise_distance);
        this.mHandleString = this.getResources().getString(R$string.handle_invocation_prompt);
        this.mSqueezeString = this.getResources().getString(R$string.squeeze_invocation_prompt);
        Dependency.get(ConfigurationController.class).addCallback((ConfigurationController.ConfigurationListener)this);
        this.setHasDarkBackground(this.mHasDarkBackground ^ true);
    }
    
    private void setAlphaProgress(final int n, final float n2) {
        if (n != 2 && n2 > 0.8f) {
            this.setAlpha(0.0f);
        }
        else if (n2 > 0.32000002f) {
            this.setAlpha(1.0f);
        }
        else {
            this.setAlpha(this.mDecelerateInterpolator.getInterpolation(n2 / 0.32000002f));
        }
    }
    
    private void setTranslationYProgress(final float n) {
        this.setTranslationY(-this.mRiseDistance * n);
    }
    
    private void updateViewHeight() {
        final ViewGroup$LayoutParams layoutParams = this.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.height = (int)(this.getResources().getDimension(R$dimen.assist_prompt_start_height) + this.mRiseDistance + super.mContext.getResources().getDimension(R$dimen.transcription_text_size));
        }
        this.requestLayout();
    }
    
    public void disable() {
        this.mEnabled = false;
        this.setVisibility(8);
    }
    
    public void enable() {
        this.mEnabled = true;
    }
    
    public void onConfigChanged(final Configuration configuration) {
        this.mHandleString = this.getResources().getString(R$string.handle_invocation_prompt);
        this.mSqueezeString = this.getResources().getString(R$string.squeeze_invocation_prompt);
    }
    
    public void onDensityOrFontScaleChanged() {
        this.setTextSize(0, super.mContext.getResources().getDimension(R$dimen.transcription_text_size));
        this.updateViewHeight();
    }
    
    protected void onFinishInflate() {
        this.updateViewHeight();
    }
    
    public void onInvocationProgress(final int n, final float translationYProgress) {
        if (translationYProgress > 1.0f) {
            return;
        }
        if (translationYProgress == 0.0f) {
            this.setVisibility(8);
            this.setAlpha(0.0f);
            this.setTranslationY(0.0f);
            this.mLastInvocationType = 0;
        }
        else if (this.mEnabled) {
            if (n != 1) {
                if (n != 2) {
                    this.mLastInvocationType = 0;
                    this.setText((CharSequence)"");
                }
                else if (this.mLastInvocationType != n) {
                    this.mLastInvocationType = n;
                    this.setText((CharSequence)this.mSqueezeString);
                    this.announceForAccessibility((CharSequence)this.mSqueezeString);
                }
            }
            else if (this.mLastInvocationType != n) {
                this.mLastInvocationType = n;
                this.setText((CharSequence)this.mHandleString);
                this.announceForAccessibility((CharSequence)this.mHandleString);
            }
            this.setVisibility(0);
            this.setTranslationYProgress(translationYProgress);
            this.setAlphaProgress(n, translationYProgress);
        }
    }
    
    public void setHasDarkBackground(final boolean mHasDarkBackground) {
        if (mHasDarkBackground != this.mHasDarkBackground) {
            int textColor;
            if (mHasDarkBackground) {
                textColor = this.mTextColorDark;
            }
            else {
                textColor = this.mTextColorLight;
            }
            this.setTextColor(textColor);
            this.mHasDarkBackground = mHasDarkBackground;
        }
    }
}
