// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tileimpl;

import java.util.function.Supplier;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Animatable2$AnimationCallback;
import android.graphics.drawable.Animatable2;
import java.util.Objects;
import com.android.systemui.R$id;
import com.android.systemui.R$color;
import android.view.View$MeasureSpec;
import android.widget.ImageView$ScaleType;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.graphics.Color;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import com.android.systemui.qs.AlphaControlledSignalTileView;
import android.widget.ImageView;
import com.android.systemui.R$dimen;
import android.content.Context;
import com.android.systemui.plugins.qs.QSTile;
import android.view.View;
import com.android.systemui.plugins.qs.QSIconView;

public class QSIconViewImpl extends QSIconView
{
    private boolean mAnimationEnabled;
    protected final View mIcon;
    protected final int mIconSizePx;
    private QSTile.Icon mLastIcon;
    private int mState;
    private int mTint;
    
    public QSIconViewImpl(final Context context) {
        super(context);
        this.mAnimationEnabled = true;
        this.mState = -1;
        this.mIconSizePx = context.getResources().getDimensionPixelSize(R$dimen.qs_tile_icon_size);
        this.addView(this.mIcon = this.createIcon());
    }
    
    private void animateGrayScale(final int n, final int n2, final ImageView imageView, final Runnable runnable) {
        if (imageView instanceof AlphaControlledSignalTileView.AlphaControlledSlashImageView) {
            ((AlphaControlledSignalTileView.AlphaControlledSlashImageView)imageView).setFinalImageTintList(ColorStateList.valueOf(n2));
        }
        if (this.mAnimationEnabled && ValueAnimator.areAnimatorsEnabled()) {
            final float n3 = (float)Color.alpha(n);
            final float n4 = (float)Color.alpha(n2);
            final float n5 = (float)Color.red(n);
            final float n6 = (float)Color.red(n2);
            final ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[] { 0.0f, 1.0f });
            ofFloat.setDuration(350L);
            ofFloat.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$QSIconViewImpl$CeqSBPdIhNYTow_6QM6a9ZwQyb8(n3, n4, n5, n6, imageView));
            ofFloat.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter(this) {
                public void onAnimationEnd(final Animator animator) {
                    runnable.run();
                }
            });
            ofFloat.start();
        }
        else {
            setTint(imageView, n2);
            runnable.run();
        }
    }
    
    public static void setTint(final ImageView imageView, final int n) {
        imageView.setImageTintList(ColorStateList.valueOf(n));
    }
    
    private boolean shouldAnimate(final ImageView imageView) {
        return this.mAnimationEnabled && imageView.isShown() && imageView.getDrawable() != null;
    }
    
    protected View createIcon() {
        final SlashImageView slashImageView = new SlashImageView(super.mContext);
        slashImageView.setId(16908294);
        slashImageView.setScaleType(ImageView$ScaleType.FIT_CENTER);
        return (View)slashImageView;
    }
    
    @Override
    public void disableAnimation() {
        this.mAnimationEnabled = false;
    }
    
    protected final int exactly(final int n) {
        return View$MeasureSpec.makeMeasureSpec(n, 1073741824);
    }
    
    protected int getColor(final int n) {
        return QSTileImpl.getColorForState(this.getContext(), n);
    }
    
    protected int getIconMeasureMode() {
        return 1073741824;
    }
    
    @Override
    public View getIconView() {
        return this.mIcon;
    }
    
    protected final void layout(final View view, final int n, final int n2) {
        view.layout(n, n2, view.getMeasuredWidth() + n, view.getMeasuredHeight() + n2);
    }
    
    protected void onLayout(final boolean b, int n, final int n2, final int n3, final int n4) {
        n = (this.getMeasuredWidth() - this.mIcon.getMeasuredWidth()) / 2;
        this.layout(this.mIcon, n, 0);
    }
    
    protected void onMeasure(int exactly, int size) {
        size = View$MeasureSpec.getSize(exactly);
        exactly = this.exactly(this.mIconSizePx);
        this.mIcon.measure(View$MeasureSpec.makeMeasureSpec(size, this.getIconMeasureMode()), exactly);
        this.setMeasuredDimension(size, this.mIcon.getMeasuredHeight());
    }
    
    protected void setIcon(final ImageView imageView, final QSTile.State state, final boolean b) {
        if (state.disabledByPolicy) {
            imageView.setColorFilter(this.getContext().getColor(R$color.qs_tile_disabled_color));
        }
        else {
            imageView.clearColorFilter();
        }
        final int state2 = state.state;
        if (state2 != this.mState) {
            final int color = this.getColor(state2);
            this.mState = state.state;
            if (this.mTint != 0 && b && this.shouldAnimate(imageView)) {
                this.animateGrayScale(this.mTint, color, imageView, new _$$Lambda$QSIconViewImpl$xTIBDrD33UKSYZv6_hT3f3X3znk(this, imageView, state, b));
                this.mTint = color;
            }
            else {
                if (imageView instanceof AlphaControlledSignalTileView.AlphaControlledSlashImageView) {
                    ((AlphaControlledSignalTileView.AlphaControlledSlashImageView)imageView).setFinalImageTintList(ColorStateList.valueOf(color));
                }
                else {
                    setTint(imageView, color);
                }
                this.mTint = color;
                this.updateIcon(imageView, state, b);
            }
        }
        else {
            this.updateIcon(imageView, state, b);
        }
    }
    
    @Override
    public void setIcon(final QSTile.State state, final boolean b) {
        this.setIcon((ImageView)this.mIcon, state, b);
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getClass().getSimpleName());
        sb.append('[');
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("state=");
        sb2.append(this.mState);
        sb.append(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append(", tint=");
        sb3.append(this.mTint);
        sb.append(sb3.toString());
        if (this.mLastIcon != null) {
            final StringBuilder sb4 = new StringBuilder();
            sb4.append(", lastIcon=");
            sb4.append(this.mLastIcon.toString());
            sb.append(sb4.toString());
        }
        sb.append("]");
        return sb.toString();
    }
    
    protected void updateIcon(final ImageView imageView, final QSTile.State state, final boolean b) {
        final Supplier<QSTile.Icon> iconSupplier = state.iconSupplier;
        QSTile.Icon icon;
        if (iconSupplier != null) {
            icon = iconSupplier.get();
        }
        else {
            icon = state.icon;
        }
        if (!Objects.equals(icon, imageView.getTag(R$id.qs_icon_tag)) || !Objects.equals(state.slash, imageView.getTag(R$id.qs_slash_tag))) {
            final boolean animationEnabled = b && this.shouldAnimate(imageView);
            Drawable imageDrawable;
            if ((this.mLastIcon = icon) != null) {
                if (animationEnabled) {
                    imageDrawable = icon.getDrawable(super.mContext);
                }
                else {
                    imageDrawable = icon.getInvisibleDrawable(super.mContext);
                }
            }
            else {
                imageDrawable = null;
            }
            int padding;
            if (icon != null) {
                padding = icon.getPadding();
            }
            else {
                padding = 0;
            }
            if (imageDrawable != null) {
                imageDrawable.setAutoMirrored(false);
                imageDrawable.setLayoutDirection(this.getLayoutDirection());
            }
            if (imageView instanceof SlashImageView) {
                final SlashImageView slashImageView = (SlashImageView)imageView;
                slashImageView.setAnimationEnabled(animationEnabled);
                slashImageView.setState(null, imageDrawable);
            }
            else {
                imageView.setImageDrawable(imageDrawable);
            }
            imageView.setTag(R$id.qs_icon_tag, (Object)icon);
            imageView.setTag(R$id.qs_slash_tag, (Object)state.slash);
            imageView.setPadding(0, padding, 0, padding);
            if (imageDrawable instanceof Animatable2) {
                final Animatable2 animatable2 = (Animatable2)imageDrawable;
                animatable2.start();
                if (state.isTransient) {
                    animatable2.registerAnimationCallback((Animatable2$AnimationCallback)new Animatable2$AnimationCallback(this) {
                        public void onAnimationEnd(final Drawable drawable) {
                            animatable2.start();
                        }
                    });
                }
            }
        }
    }
}
