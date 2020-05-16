// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.bubbles;

import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.util.PathParser;
import android.graphics.RectF;
import android.os.UserHandle;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import com.android.systemui.R$layout;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import com.android.systemui.R$dimen;
import android.graphics.Path;
import android.view.LayoutInflater;
import android.graphics.Bitmap;
import android.content.Context;

public class BubbleOverflow implements BubbleViewProvider
{
    private int mBitmapSize;
    private Context mContext;
    private int mDotColor;
    private BubbleExpandedView mExpandedView;
    private Bitmap mIcon;
    private int mIconBitmapSize;
    private LayoutInflater mInflater;
    private BadgedImageView mOverflowBtn;
    private Path mPath;
    
    public BubbleOverflow(final Context mContext) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        this.mBitmapSize = this.mContext.getResources().getDimensionPixelSize(R$dimen.bubble_bitmap_size);
        this.mIconBitmapSize = this.mContext.getResources().getDimensionPixelSize(R$dimen.bubble_overflow_icon_bitmap_size);
    }
    
    @Override
    public Bitmap getBadgedImage() {
        return this.mIcon;
    }
    
    ImageView getBtn() {
        return this.mOverflowBtn;
    }
    
    @Override
    public int getDisplayId() {
        final BubbleExpandedView mExpandedView = this.mExpandedView;
        int virtualDisplayId;
        if (mExpandedView != null) {
            virtualDisplayId = mExpandedView.getVirtualDisplayId();
        }
        else {
            virtualDisplayId = -1;
        }
        return virtualDisplayId;
    }
    
    @Override
    public int getDotColor() {
        return this.mDotColor;
    }
    
    @Override
    public Path getDotPath() {
        return this.mPath;
    }
    
    @Override
    public BubbleExpandedView getExpandedView() {
        return this.mExpandedView;
    }
    
    @Override
    public View getIconView() {
        return (View)this.mOverflowBtn;
    }
    
    @Override
    public String getKey() {
        return "Overflow";
    }
    
    @Override
    public void logUIEvent(final int n, final int n2, final float n3, final float n4, final int n5) {
    }
    
    void setBtnVisible(final int visibility) {
        this.mOverflowBtn.setVisibility(visibility);
    }
    
    @Override
    public void setContentVisibility(final boolean contentVisibility) {
        this.mExpandedView.setContentVisibility(contentVisibility);
    }
    
    void setUpOverflow(final ViewGroup viewGroup, final BubbleStackView stackView) {
        (this.mExpandedView = (BubbleExpandedView)this.mInflater.inflate(R$layout.bubble_expanded_view, viewGroup, false)).setOverflow(true);
        this.mExpandedView.setStackView(stackView);
        this.updateIcon(this.mContext, viewGroup);
    }
    
    @Override
    public boolean showDot() {
        return false;
    }
    
    void updateIcon(final Context context, final ViewGroup viewGroup) {
        final LayoutInflater from = LayoutInflater.from(context);
        this.mInflater = from;
        this.mOverflowBtn = (BadgedImageView)from.inflate(R$layout.bubble_overflow_button, viewGroup, false);
        final TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(new int[] { 16844002 });
        final int color = obtainStyledAttributes.getColor(0, -1);
        obtainStyledAttributes.recycle();
        final TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(16843829, typedValue, true);
        final int color2 = this.mContext.getColor(typedValue.resourceId);
        this.mOverflowBtn.getDrawable().setTint(color2);
        this.mDotColor = color2;
        final AdaptiveIconDrawable adaptiveIconDrawable = new AdaptiveIconDrawable((Drawable)new ColorDrawable(color), (Drawable)new InsetDrawable(this.mOverflowBtn.getDrawable(), this.mBitmapSize - this.mIconBitmapSize));
        final BubbleIconFactory bubbleIconFactory = new BubbleIconFactory(context);
        this.mIcon = bubbleIconFactory.createBadgedIconBitmap((Drawable)adaptiveIconDrawable, null, true).icon;
        final float scale = bubbleIconFactory.getNormalizer().getScale(this.mOverflowBtn.getDrawable(), null, null, null);
        this.mPath = PathParser.createPathFromPathData(context.getResources().getString(17039911));
        final Matrix matrix = new Matrix();
        matrix.setScale(scale, scale, 50.0f, 50.0f);
        this.mPath.transform(matrix);
        this.mOverflowBtn.setVisibility(8);
        this.mOverflowBtn.setRenderedBubble(this);
    }
}
