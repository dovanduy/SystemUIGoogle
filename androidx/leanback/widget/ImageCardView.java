// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.graphics.drawable.Drawable;
import android.content.res.TypedArray;
import android.view.ViewGroup$LayoutParams;
import android.widget.RelativeLayout$LayoutParams;
import androidx.leanback.R$id;
import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.leanback.R$styleable;
import androidx.leanback.R$layout;
import android.view.LayoutInflater;
import androidx.leanback.R$style;
import androidx.leanback.R$attr;
import android.util.AttributeSet;
import android.content.Context;
import android.view.ViewGroup;
import android.animation.ObjectAnimator;
import android.widget.TextView;
import android.widget.ImageView;

public class ImageCardView extends BaseCardView
{
    private boolean mAttachedToWindow;
    private ImageView mBadgeImage;
    private TextView mContentView;
    ObjectAnimator mFadeInAnimator;
    private ImageView mImageView;
    private ViewGroup mInfoArea;
    private TextView mTitleView;
    
    public ImageCardView(final Context context, final AttributeSet set) {
        this(context, set, R$attr.imageCardViewStyle);
    }
    
    public ImageCardView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.buildImageCardView(set, n, R$style.Widget_Leanback_ImageCardView);
    }
    
    private void buildImageCardView(final AttributeSet set, int n, int n2) {
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        final LayoutInflater from = LayoutInflater.from(this.getContext());
        from.inflate(R$layout.lb_image_card_view, (ViewGroup)this);
        final TypedArray obtainStyledAttributes = this.getContext().obtainStyledAttributes(set, R$styleable.lbImageCardView, n, n2);
        ViewCompat.saveAttributeDataForStyleable((View)this, this.getContext(), R$styleable.lbImageCardView, set, obtainStyledAttributes, n, n2);
        final int int1 = obtainStyledAttributes.getInt(R$styleable.lbImageCardView_lbImageCardViewType, 0);
        if (int1 == 0) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if ((int1 & 0x1) == 0x1) {
            n = 1;
        }
        else {
            n = 0;
        }
        final boolean b = (int1 & 0x2) == 0x2;
        final boolean b2 = (int1 & 0x4) == 0x4;
        final boolean b3 = !b2 && (int1 & 0x8) == 0x8;
        final ImageView mImageView = (ImageView)this.findViewById(R$id.main_image);
        this.mImageView = mImageView;
        if (mImageView.getDrawable() == null) {
            this.mImageView.setVisibility(4);
        }
        (this.mFadeInAnimator = ObjectAnimator.ofFloat((Object)this.mImageView, "alpha", new float[] { 1.0f })).setDuration((long)this.mImageView.getResources().getInteger(17694720));
        final ViewGroup mInfoArea = (ViewGroup)this.findViewById(R$id.info_field);
        this.mInfoArea = mInfoArea;
        if (n2 != 0) {
            this.removeView((View)mInfoArea);
            obtainStyledAttributes.recycle();
            return;
        }
        if (n != 0) {
            final TextView mTitleView = (TextView)from.inflate(R$layout.lb_image_card_view_themed_title, mInfoArea, false);
            this.mTitleView = mTitleView;
            this.mInfoArea.addView((View)mTitleView);
        }
        if (b) {
            final TextView mContentView = (TextView)from.inflate(R$layout.lb_image_card_view_themed_content, this.mInfoArea, false);
            this.mContentView = mContentView;
            this.mInfoArea.addView((View)mContentView);
        }
        if (b2 || b3) {
            n2 = R$layout.lb_image_card_view_themed_badge_right;
            if (b3) {
                n2 = R$layout.lb_image_card_view_themed_badge_left;
            }
            final ImageView mBadgeImage = (ImageView)from.inflate(n2, this.mInfoArea, false);
            this.mBadgeImage = mBadgeImage;
            this.mInfoArea.addView((View)mBadgeImage);
        }
        if (n != 0 && !b && this.mBadgeImage != null) {
            final RelativeLayout$LayoutParams layoutParams = (RelativeLayout$LayoutParams)this.mTitleView.getLayoutParams();
            if (b3) {
                layoutParams.addRule(17, this.mBadgeImage.getId());
            }
            else {
                layoutParams.addRule(16, this.mBadgeImage.getId());
            }
            this.mTitleView.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
        }
        if (b) {
            final RelativeLayout$LayoutParams layoutParams2 = (RelativeLayout$LayoutParams)this.mContentView.getLayoutParams();
            if (n == 0) {
                layoutParams2.addRule(10);
            }
            if (b3) {
                layoutParams2.removeRule(16);
                layoutParams2.removeRule(20);
                layoutParams2.addRule(17, this.mBadgeImage.getId());
            }
            this.mContentView.setLayoutParams((ViewGroup$LayoutParams)layoutParams2);
        }
        final ImageView mBadgeImage2 = this.mBadgeImage;
        if (mBadgeImage2 != null) {
            final RelativeLayout$LayoutParams layoutParams3 = (RelativeLayout$LayoutParams)mBadgeImage2.getLayoutParams();
            if (b) {
                layoutParams3.addRule(8, this.mContentView.getId());
            }
            else if (n != 0) {
                layoutParams3.addRule(8, this.mTitleView.getId());
            }
            this.mBadgeImage.setLayoutParams((ViewGroup$LayoutParams)layoutParams3);
        }
        final Drawable drawable = obtainStyledAttributes.getDrawable(R$styleable.lbImageCardView_infoAreaBackground);
        if (drawable != null) {
            this.setInfoAreaBackground(drawable);
        }
        final ImageView mBadgeImage3 = this.mBadgeImage;
        if (mBadgeImage3 != null && mBadgeImage3.getDrawable() == null) {
            this.mBadgeImage.setVisibility(8);
        }
        obtainStyledAttributes.recycle();
    }
    
    private void fadeIn() {
        this.mImageView.setAlpha(0.0f);
        if (this.mAttachedToWindow) {
            this.mFadeInAnimator.start();
        }
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAttachedToWindow = true;
        if (this.mImageView.getAlpha() == 0.0f) {
            this.fadeIn();
        }
    }
    
    @Override
    protected void onDetachedFromWindow() {
        this.mAttachedToWindow = false;
        this.mFadeInAnimator.cancel();
        this.mImageView.setAlpha(1.0f);
        super.onDetachedFromWindow();
    }
    
    public void setInfoAreaBackground(final Drawable background) {
        final ViewGroup mInfoArea = this.mInfoArea;
        if (mInfoArea != null) {
            mInfoArea.setBackground(background);
        }
    }
}
