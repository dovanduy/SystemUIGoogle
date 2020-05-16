// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.Rect;
import androidx.leanback.R$layout;
import android.animation.TypeEvaluator;
import android.content.res.TypedArray;
import android.content.res.Resources;
import androidx.leanback.R$color;
import androidx.leanback.R$drawable;
import androidx.core.view.ViewCompat;
import androidx.leanback.R$styleable;
import androidx.leanback.R$dimen;
import androidx.leanback.R$integer;
import androidx.leanback.R$fraction;
import androidx.leanback.R$id;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import androidx.leanback.R$attr;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.view.View$OnClickListener;
import android.widget.FrameLayout;

public class SearchOrbView extends FrameLayout implements View$OnClickListener
{
    private boolean mAttachedToWindow;
    private boolean mColorAnimationEnabled;
    private ValueAnimator mColorAnimator;
    private final ArgbEvaluator mColorEvaluator;
    private Colors mColors;
    private final ValueAnimator$AnimatorUpdateListener mFocusUpdateListener;
    private final float mFocusedZ;
    private final float mFocusedZoom;
    private ImageView mIcon;
    private Drawable mIconDrawable;
    private View$OnClickListener mListener;
    private final int mPulseDurationMs;
    private View mRootView;
    private final int mScaleDurationMs;
    private View mSearchOrbView;
    private ValueAnimator mShadowFocusAnimator;
    private final float mUnfocusedZ;
    private final ValueAnimator$AnimatorUpdateListener mUpdateListener;
    
    public SearchOrbView(final Context context) {
        this(context, null);
    }
    
    public SearchOrbView(final Context context, final AttributeSet set) {
        this(context, set, R$attr.searchOrbViewStyle);
    }
    
    public SearchOrbView(final Context context, final AttributeSet set, int n) {
        super(context, set, n);
        this.mColorEvaluator = new ArgbEvaluator();
        this.mUpdateListener = (ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                SearchOrbView.this.setOrbViewColor((int)valueAnimator.getAnimatedValue());
            }
        };
        this.mFocusUpdateListener = (ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                SearchOrbView.this.setSearchOrbZ(valueAnimator.getAnimatedFraction());
            }
        };
        final Resources resources = context.getResources();
        final View inflate = ((LayoutInflater)context.getSystemService("layout_inflater")).inflate(this.getLayoutResourceId(), (ViewGroup)this, true);
        this.mRootView = inflate;
        this.mSearchOrbView = inflate.findViewById(R$id.search_orb);
        this.mIcon = (ImageView)this.mRootView.findViewById(R$id.icon);
        this.mFocusedZoom = context.getResources().getFraction(R$fraction.lb_search_orb_focused_zoom, 1, 1);
        this.mPulseDurationMs = context.getResources().getInteger(R$integer.lb_search_orb_pulse_duration_ms);
        this.mScaleDurationMs = context.getResources().getInteger(R$integer.lb_search_orb_scale_duration_ms);
        this.mFocusedZ = (float)context.getResources().getDimensionPixelSize(R$dimen.lb_search_orb_focused_z);
        this.mUnfocusedZ = (float)context.getResources().getDimensionPixelSize(R$dimen.lb_search_orb_unfocused_z);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.lbSearchOrbView, n, 0);
        ViewCompat.saveAttributeDataForStyleable((View)this, context, R$styleable.lbSearchOrbView, set, obtainStyledAttributes, n, 0);
        Drawable orbIcon;
        if ((orbIcon = obtainStyledAttributes.getDrawable(R$styleable.lbSearchOrbView_searchOrbIcon)) == null) {
            orbIcon = resources.getDrawable(R$drawable.lb_ic_in_app_search);
        }
        this.setOrbIcon(orbIcon);
        n = resources.getColor(R$color.lb_default_search_color);
        n = obtainStyledAttributes.getColor(R$styleable.lbSearchOrbView_searchOrbColor, n);
        this.setOrbColors(new Colors(n, obtainStyledAttributes.getColor(R$styleable.lbSearchOrbView_searchOrbBrightColor, n), obtainStyledAttributes.getColor(R$styleable.lbSearchOrbView_searchOrbIconColor, 0)));
        obtainStyledAttributes.recycle();
        this.setFocusable(true);
        this.setClipChildren(false);
        this.setOnClickListener((View$OnClickListener)this);
        this.setSoundEffectsEnabled(false);
        this.setSearchOrbZ(0.0f);
        ViewCompat.setZ((View)this.mIcon, this.mFocusedZ);
    }
    
    private void startShadowFocusAnimation(final boolean b, final int n) {
        if (this.mShadowFocusAnimator == null) {
            (this.mShadowFocusAnimator = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f })).addUpdateListener(this.mFocusUpdateListener);
        }
        if (b) {
            this.mShadowFocusAnimator.start();
        }
        else {
            this.mShadowFocusAnimator.reverse();
        }
        this.mShadowFocusAnimator.setDuration((long)n);
    }
    
    private void updateColorAnimator() {
        final ValueAnimator mColorAnimator = this.mColorAnimator;
        if (mColorAnimator != null) {
            mColorAnimator.end();
            this.mColorAnimator = null;
        }
        if (this.mColorAnimationEnabled && this.mAttachedToWindow) {
            (this.mColorAnimator = ValueAnimator.ofObject((TypeEvaluator)this.mColorEvaluator, new Object[] { this.mColors.color, this.mColors.brightColor, this.mColors.color })).setRepeatCount(-1);
            this.mColorAnimator.setDuration((long)(this.mPulseDurationMs * 2));
            this.mColorAnimator.addUpdateListener(this.mUpdateListener);
            this.mColorAnimator.start();
        }
    }
    
    void animateOnFocus(final boolean b) {
        float mFocusedZoom;
        if (b) {
            mFocusedZoom = this.mFocusedZoom;
        }
        else {
            mFocusedZoom = 1.0f;
        }
        this.mRootView.animate().scaleX(mFocusedZoom).scaleY(mFocusedZoom).setDuration((long)this.mScaleDurationMs).start();
        this.startShadowFocusAnimation(b, this.mScaleDurationMs);
        this.enableOrbColorAnimation(b);
    }
    
    public void enableOrbColorAnimation(final boolean mColorAnimationEnabled) {
        this.mColorAnimationEnabled = mColorAnimationEnabled;
        this.updateColorAnimator();
    }
    
    float getFocusedZoom() {
        return this.mFocusedZoom;
    }
    
    int getLayoutResourceId() {
        return R$layout.lb_search_orb;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAttachedToWindow = true;
        this.updateColorAnimator();
    }
    
    public void onClick(final View view) {
        final View$OnClickListener mListener = this.mListener;
        if (mListener != null) {
            mListener.onClick(view);
        }
    }
    
    protected void onDetachedFromWindow() {
        this.mAttachedToWindow = false;
        this.updateColorAnimator();
        super.onDetachedFromWindow();
    }
    
    protected void onFocusChanged(final boolean b, final int n, final Rect rect) {
        super.onFocusChanged(b, n, rect);
        this.animateOnFocus(b);
    }
    
    void scaleOrbViewOnly(final float n) {
        this.mSearchOrbView.setScaleX(n);
        this.mSearchOrbView.setScaleY(n);
    }
    
    public void setOnOrbClickedListener(final View$OnClickListener mListener) {
        this.mListener = mListener;
    }
    
    public void setOrbColors(final Colors mColors) {
        this.mColors = mColors;
        this.mIcon.setColorFilter(mColors.iconColor);
        if (this.mColorAnimator == null) {
            this.setOrbViewColor(this.mColors.color);
        }
        else {
            this.enableOrbColorAnimation(true);
        }
    }
    
    public void setOrbIcon(final Drawable drawable) {
        this.mIconDrawable = drawable;
        this.mIcon.setImageDrawable(drawable);
    }
    
    void setOrbViewColor(final int color) {
        if (this.mSearchOrbView.getBackground() instanceof GradientDrawable) {
            ((GradientDrawable)this.mSearchOrbView.getBackground()).setColor(color);
        }
    }
    
    void setSearchOrbZ(final float n) {
        final View mSearchOrbView = this.mSearchOrbView;
        final float mUnfocusedZ = this.mUnfocusedZ;
        ViewCompat.setZ(mSearchOrbView, mUnfocusedZ + n * (this.mFocusedZ - mUnfocusedZ));
    }
    
    public static class Colors
    {
        public int brightColor;
        public int color;
        public int iconColor;
        
        public Colors(final int color, final int n, final int iconColor) {
            this.color = color;
            int brightColor = n;
            if (n == color) {
                brightColor = getBrightColor(color);
            }
            this.brightColor = brightColor;
            this.iconColor = iconColor;
        }
        
        public static int getBrightColor(final int n) {
            return Color.argb((int)(Color.alpha(n) * 0.85f + 38.25f), (int)(Color.red(n) * 0.85f + 38.25f), (int)(Color.green(n) * 0.85f + 38.25f), (int)(Color.blue(n) * 0.85f + 38.25f));
        }
    }
}
