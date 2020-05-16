// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.bubbles;

import android.text.TextUtils;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Matrix;
import android.graphics.Path$Direction;
import android.graphics.Path;
import android.content.res.TypedArray;
import android.content.res.Resources;
import android.graphics.drawable.shapes.Shape;
import com.android.systemui.recents.TriangleShape;
import android.view.View;
import android.view.ViewOutlineProvider;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.content.Context;
import android.graphics.Outline;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.drawable.ShapeDrawable;
import android.view.ViewGroup;
import android.graphics.RectF;
import android.graphics.Paint;
import android.animation.ArgbEvaluator;
import android.widget.FrameLayout;

public class BubbleFlyoutView extends FrameLayout
{
    private final ArgbEvaluator mArgbEvaluator;
    private boolean mArrowPointingLeft;
    private final Paint mBgPaint;
    private final RectF mBgRect;
    private float mBgTranslationX;
    private float mBgTranslationY;
    private final int mBubbleBitmapSize;
    private final int mBubbleElevation;
    private final float mBubbleIconTopPadding;
    private final int mBubbleSize;
    private final float mCornerRadius;
    private float[] mDotCenter;
    private int mDotColor;
    private final int mFloatingBackgroundColor;
    private final int mFlyoutElevation;
    private final int mFlyoutPadding;
    private final int mFlyoutSpaceFromBubble;
    private final ViewGroup mFlyoutTextContainer;
    private float mFlyoutToDotHeightDelta;
    private float mFlyoutToDotWidthDelta;
    private final ShapeDrawable mLeftTriangleShape;
    private final TextView mMessageText;
    private final float mNewDotRadius;
    private final float mNewDotSize;
    private Runnable mOnHide;
    private final float mOriginalDotSize;
    private float mPercentStillFlyout;
    private float mPercentTransitionedToDot;
    private final int mPointerSize;
    private float mRestingTranslationX;
    private final ShapeDrawable mRightTriangleShape;
    private final ImageView mSenderAvatar;
    private final TextView mSenderText;
    private float mTranslationXWhenDot;
    private float mTranslationYWhenDot;
    private final Outline mTriangleOutline;
    
    public BubbleFlyoutView(final Context context) {
        super(context);
        this.mBgPaint = new Paint(3);
        this.mArgbEvaluator = new ArgbEvaluator();
        this.mArrowPointingLeft = true;
        this.mTriangleOutline = new Outline();
        this.mBgRect = new RectF();
        this.mPercentTransitionedToDot = 1.0f;
        this.mPercentStillFlyout = 0.0f;
        this.mFlyoutToDotWidthDelta = 0.0f;
        this.mFlyoutToDotHeightDelta = 0.0f;
        this.mTranslationXWhenDot = 0.0f;
        this.mTranslationYWhenDot = 0.0f;
        this.mRestingTranslationX = 0.0f;
        LayoutInflater.from(context).inflate(R$layout.bubble_flyout, (ViewGroup)this, true);
        this.mFlyoutTextContainer = (ViewGroup)this.findViewById(R$id.bubble_flyout_text_container);
        this.mSenderText = (TextView)this.findViewById(R$id.bubble_flyout_name);
        this.mSenderAvatar = (ImageView)this.findViewById(R$id.bubble_flyout_avatar);
        this.mMessageText = (TextView)this.mFlyoutTextContainer.findViewById(R$id.bubble_flyout_text);
        final Resources resources = this.getResources();
        this.mFlyoutPadding = resources.getDimensionPixelSize(R$dimen.bubble_flyout_padding_x);
        this.mFlyoutSpaceFromBubble = resources.getDimensionPixelSize(R$dimen.bubble_flyout_space_from_bubble);
        this.mPointerSize = resources.getDimensionPixelSize(R$dimen.bubble_flyout_pointer_size);
        this.mBubbleSize = resources.getDimensionPixelSize(R$dimen.individual_bubble_size);
        final int dimensionPixelSize = resources.getDimensionPixelSize(R$dimen.bubble_bitmap_size);
        this.mBubbleBitmapSize = dimensionPixelSize;
        this.mBubbleIconTopPadding = (this.mBubbleSize - dimensionPixelSize) / 2.0f;
        this.mBubbleElevation = resources.getDimensionPixelSize(R$dimen.bubble_elevation);
        this.mFlyoutElevation = resources.getDimensionPixelSize(R$dimen.bubble_flyout_elevation);
        final float mOriginalDotSize = this.mBubbleBitmapSize * 0.228f;
        this.mOriginalDotSize = mOriginalDotSize;
        final float mNewDotRadius = mOriginalDotSize * 1.0f / 2.0f;
        this.mNewDotRadius = mNewDotRadius;
        this.mNewDotSize = mNewDotRadius * 2.0f;
        final TypedArray obtainStyledAttributes = super.mContext.obtainStyledAttributes(new int[] { 16844002, 16844145 });
        this.mFloatingBackgroundColor = obtainStyledAttributes.getColor(0, -1);
        this.mCornerRadius = (float)obtainStyledAttributes.getDimensionPixelSize(1, 0);
        obtainStyledAttributes.recycle();
        final int mPointerSize = this.mPointerSize;
        this.setPadding(mPointerSize, 0, mPointerSize, 0);
        this.setWillNotDraw(false);
        this.setClipChildren(false);
        this.setTranslationZ((float)this.mFlyoutElevation);
        this.setOutlineProvider((ViewOutlineProvider)new ViewOutlineProvider() {
            public void getOutline(final View view, final Outline outline) {
                BubbleFlyoutView.this.getOutline(outline);
            }
        });
        this.mBgPaint.setColor(this.mFloatingBackgroundColor);
        final int mPointerSize2 = this.mPointerSize;
        final ShapeDrawable mLeftTriangleShape = new ShapeDrawable((Shape)TriangleShape.createHorizontal((float)mPointerSize2, (float)mPointerSize2, true));
        this.mLeftTriangleShape = mLeftTriangleShape;
        final int mPointerSize3 = this.mPointerSize;
        mLeftTriangleShape.setBounds(0, 0, mPointerSize3, mPointerSize3);
        this.mLeftTriangleShape.getPaint().setColor(this.mFloatingBackgroundColor);
        final int mPointerSize4 = this.mPointerSize;
        final ShapeDrawable mRightTriangleShape = new ShapeDrawable((Shape)TriangleShape.createHorizontal((float)mPointerSize4, (float)mPointerSize4, false));
        this.mRightTriangleShape = mRightTriangleShape;
        final int mPointerSize5 = this.mPointerSize;
        mRightTriangleShape.setBounds(0, 0, mPointerSize5, mPointerSize5);
        this.mRightTriangleShape.getPaint().setColor(this.mFloatingBackgroundColor);
    }
    
    private float clampPercentage(final float b) {
        return Math.min(1.0f, Math.max(0.0f, b));
    }
    
    private float getInterpolatedRadius() {
        final float mNewDotRadius = this.mNewDotRadius;
        final float mPercentTransitionedToDot = this.mPercentTransitionedToDot;
        return mNewDotRadius * mPercentTransitionedToDot + this.mCornerRadius * (1.0f - mPercentTransitionedToDot);
    }
    
    private void getOutline(final Outline outline) {
        if (!this.mTriangleOutline.isEmpty()) {
            final Path path = new Path();
            final float interpolatedRadius = this.getInterpolatedRadius();
            path.addRoundRect(this.mBgRect, interpolatedRadius, interpolatedRadius, Path$Direction.CW);
            outline.setPath(path);
            if (this.mPercentStillFlyout > 0.5f) {
                outline.mPath.addPath(this.mTriangleOutline.mPath);
            }
            final Matrix matrix = new Matrix();
            matrix.postTranslate(this.getLeft() + this.mBgTranslationX, this.getTop() + this.mBgTranslationY);
            final float mPercentTransitionedToDot = this.mPercentTransitionedToDot;
            if (mPercentTransitionedToDot > 0.98f) {
                final float n = (mPercentTransitionedToDot - 0.98f) / 0.02f;
                final float n2 = 1.0f - n;
                final float mNewDotRadius = this.mNewDotRadius;
                matrix.postTranslate(mNewDotRadius * n, mNewDotRadius * n);
                matrix.preScale(n2, n2);
            }
            outline.mPath.transform(matrix);
        }
    }
    
    private void renderBackground(final Canvas canvas) {
        final float n = this.getWidth() - this.mFlyoutToDotWidthDelta * this.mPercentTransitionedToDot;
        final float n2 = this.getHeight() - this.mFlyoutToDotHeightDelta * this.mPercentTransitionedToDot;
        final float interpolatedRadius = this.getInterpolatedRadius();
        final float mTranslationXWhenDot = this.mTranslationXWhenDot;
        final float mPercentTransitionedToDot = this.mPercentTransitionedToDot;
        this.mBgTranslationX = mTranslationXWhenDot * mPercentTransitionedToDot;
        this.mBgTranslationY = this.mTranslationYWhenDot * mPercentTransitionedToDot;
        final RectF mBgRect = this.mBgRect;
        final int mPointerSize = this.mPointerSize;
        final float n3 = (float)mPointerSize;
        final float mPercentStillFlyout = this.mPercentStillFlyout;
        mBgRect.set(n3 * mPercentStillFlyout, 0.0f, n - mPointerSize * mPercentStillFlyout, n2);
        this.mBgPaint.setColor((int)this.mArgbEvaluator.evaluate(this.mPercentTransitionedToDot, (Object)this.mFloatingBackgroundColor, (Object)this.mDotColor));
        canvas.save();
        canvas.translate(this.mBgTranslationX, this.mBgTranslationY);
        this.renderPointerTriangle(canvas, n, n2);
        canvas.drawRoundRect(this.mBgRect, interpolatedRadius, interpolatedRadius, this.mBgPaint);
        canvas.restore();
    }
    
    private void renderPointerTriangle(final Canvas canvas, float n, float n2) {
        canvas.save();
        int n3;
        if (this.mArrowPointingLeft) {
            n3 = 1;
        }
        else {
            n3 = -1;
        }
        final float n4 = (float)n3;
        final float mPercentTransitionedToDot = this.mPercentTransitionedToDot;
        final int mPointerSize = this.mPointerSize;
        final float n5 = n4 * (mPercentTransitionedToDot * mPointerSize * 2.0f);
        if (this.mArrowPointingLeft) {
            n = n5;
        }
        else {
            n = n5 + (n - mPointerSize);
        }
        n2 = n2 / 2.0f - this.mPointerSize / 2.0f;
        ShapeDrawable shapeDrawable;
        if (this.mArrowPointingLeft) {
            shapeDrawable = this.mLeftTriangleShape;
        }
        else {
            shapeDrawable = this.mRightTriangleShape;
        }
        canvas.translate(n, n2);
        shapeDrawable.setAlpha((int)(this.mPercentStillFlyout * 255.0f));
        shapeDrawable.draw(canvas);
        shapeDrawable.getOutline(this.mTriangleOutline);
        this.mTriangleOutline.offset((int)n, (int)n2);
        canvas.restore();
    }
    
    float getRestingTranslationX() {
        return this.mRestingTranslationX;
    }
    
    void hideFlyout() {
        final Runnable mOnHide = this.mOnHide;
        if (mOnHide != null) {
            mOnHide.run();
            this.mOnHide = null;
        }
        this.setVisibility(8);
    }
    
    protected void onDraw(final Canvas canvas) {
        this.renderBackground(canvas);
        this.invalidateOutline();
        super.onDraw(canvas);
    }
    
    void setCollapsePercent(float n) {
        if (Float.isNaN(n)) {
            return;
        }
        n = Math.max(0.0f, Math.min(n, 1.0f));
        this.mPercentTransitionedToDot = n;
        this.mPercentStillFlyout = 1.0f - n;
        int width;
        if (this.mArrowPointingLeft) {
            width = -this.getWidth();
        }
        else {
            width = this.getWidth();
        }
        final float translationX = n * width;
        n = this.clampPercentage((this.mPercentStillFlyout - 0.75f) / 0.25f);
        this.mMessageText.setTranslationX(translationX);
        this.mMessageText.setAlpha(n);
        this.mSenderText.setTranslationX(translationX);
        this.mSenderText.setAlpha(n);
        this.mSenderAvatar.setTranslationX(translationX);
        this.mSenderAvatar.setAlpha(n);
        final int mFlyoutElevation = this.mFlyoutElevation;
        this.setTranslationZ(mFlyoutElevation - (mFlyoutElevation - this.mBubbleElevation) * this.mPercentTransitionedToDot);
        this.invalidate();
    }
    
    void setupFlyoutStartingAsDot(final Bubble.FlyoutMessage flyoutMessage, final PointF pointF, final float n, final boolean mArrowPointingLeft, final int mDotColor, final Runnable runnable, final Runnable mOnHide, final float[] mDotCenter, final boolean b) {
        if (flyoutMessage.senderAvatar != null && flyoutMessage.isGroupChat) {
            this.mSenderAvatar.setVisibility(0);
            this.mSenderAvatar.setImageDrawable(flyoutMessage.senderAvatar);
        }
        else {
            this.mSenderAvatar.setVisibility(8);
            this.mSenderAvatar.setTranslationX(0.0f);
            this.mMessageText.setTranslationX(0.0f);
            this.mSenderText.setTranslationX(0.0f);
        }
        if (!TextUtils.isEmpty(flyoutMessage.senderName)) {
            this.mSenderText.setText(flyoutMessage.senderName);
            this.mSenderText.setVisibility(0);
        }
        else {
            this.mSenderText.setVisibility(8);
        }
        this.mArrowPointingLeft = mArrowPointingLeft;
        this.mDotColor = mDotColor;
        this.mOnHide = mOnHide;
        this.mDotCenter = mDotCenter;
        this.setCollapsePercent(1.0f);
        this.mMessageText.setMaxWidth((int)(n * 0.6f) - this.mFlyoutPadding * 2);
        this.mMessageText.setText(flyoutMessage.message);
        this.post((Runnable)new _$$Lambda$BubbleFlyoutView$MmTh2kLTzOgAqdKgn0YGS6zixjU(this, pointF, b, runnable));
    }
}
