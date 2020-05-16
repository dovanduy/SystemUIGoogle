// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.bubbles;

import android.graphics.Canvas;
import android.graphics.Path;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.util.PathParser;
import com.android.systemui.R$dimen;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Rect;
import java.util.EnumSet;
import com.android.launcher3.icons.DotRenderer;
import android.widget.ImageView;

public class BadgedImageView extends ImageView
{
    private float mAnimatingToDotScale;
    private BubbleViewProvider mBubble;
    private int mBubbleBitmapSize;
    private int mDotColor;
    private boolean mDotIsAnimating;
    private DotRenderer mDotRenderer;
    private float mDotScale;
    private final EnumSet<SuppressionFlag> mDotSuppressionFlags;
    private DotRenderer.DrawParams mDrawParams;
    private boolean mOnLeft;
    private Rect mTempBounds;
    
    public BadgedImageView(final Context context) {
        this(context, null);
    }
    
    public BadgedImageView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public BadgedImageView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public BadgedImageView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mDotSuppressionFlags = EnumSet.of(SuppressionFlag.FLYOUT_VISIBLE);
        this.mDotScale = 0.0f;
        this.mAnimatingToDotScale = 0.0f;
        this.mDotIsAnimating = false;
        this.mTempBounds = new Rect();
        this.mBubbleBitmapSize = this.getResources().getDimensionPixelSize(R$dimen.bubble_bitmap_size);
        this.mDrawParams = new DotRenderer.DrawParams();
        this.mDotRenderer = new DotRenderer(this.mBubbleBitmapSize, PathParser.createPathFromPathData(this.getResources().getString(17039911)), 100);
        this.setFocusable(true);
    }
    
    private void animateDotScale(final float mAnimatingToDotScale, final Runnable runnable) {
        boolean b = true;
        this.mDotIsAnimating = true;
        if (this.mAnimatingToDotScale != mAnimatingToDotScale && this.shouldDrawDot()) {
            this.mAnimatingToDotScale = mAnimatingToDotScale;
            if (mAnimatingToDotScale <= 0.0f) {
                b = false;
            }
            this.clearAnimation();
            this.animate().setDuration(200L).setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN).setUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$BadgedImageView$5JtatqU5fJ_DVxOW3Qg2hefSWas(this, b)).withEndAction((Runnable)new _$$Lambda$BadgedImageView$v47cozs89EavNMNnxmtPzE3ZmYs(this, b, runnable)).start();
            return;
        }
        this.mDotIsAnimating = false;
    }
    
    private boolean shouldDrawDot() {
        return this.mDotIsAnimating || (this.mBubble.showDot() && this.mDotSuppressionFlags.isEmpty());
    }
    
    void addDotSuppressionFlag(final SuppressionFlag e) {
        if (this.mDotSuppressionFlags.add(e)) {
            this.updateDotVisibility(e == SuppressionFlag.BEHIND_STACK);
        }
    }
    
    void drawDot(final Path path) {
        this.mDotRenderer = new DotRenderer(this.mBubbleBitmapSize, path, 100);
        this.invalidate();
    }
    
    float[] getDotCenter() {
        float[] array;
        if (this.mOnLeft) {
            array = this.mDotRenderer.getLeftDotPosition();
        }
        else {
            array = this.mDotRenderer.getRightDotPosition();
        }
        this.getDrawingRect(this.mTempBounds);
        return new float[] { this.mTempBounds.width() * array[0], this.mTempBounds.height() * array[1] };
    }
    
    int getDotColor() {
        return this.mDotColor;
    }
    
    boolean getDotOnLeft() {
        return this.mOnLeft;
    }
    
    boolean getDotPositionOnLeft() {
        return this.getDotOnLeft();
    }
    
    public String getKey() {
        final BubbleViewProvider mBubble = this.mBubble;
        String key;
        if (mBubble != null) {
            key = mBubble.getKey();
        }
        else {
            key = null;
        }
        return key;
    }
    
    public void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (!this.shouldDrawDot()) {
            return;
        }
        this.getDrawingRect(this.mTempBounds);
        final DotRenderer.DrawParams mDrawParams = this.mDrawParams;
        mDrawParams.color = this.mDotColor;
        mDrawParams.iconBounds = this.mTempBounds;
        mDrawParams.leftAlign = this.mOnLeft;
        mDrawParams.scale = this.mDotScale;
        this.mDotRenderer.draw(canvas, mDrawParams);
    }
    
    void removeDotSuppressionFlag(final SuppressionFlag o) {
        if (this.mDotSuppressionFlags.remove(o)) {
            this.updateDotVisibility(o == SuppressionFlag.BEHIND_STACK);
        }
    }
    
    void setDotOnLeft(final boolean mOnLeft) {
        this.mOnLeft = mOnLeft;
        this.invalidate();
    }
    
    void setDotPositionOnLeft(final boolean dotOnLeft, final boolean b) {
        if (b && dotOnLeft != this.getDotOnLeft() && this.shouldDrawDot()) {
            this.animateDotScale(0.0f, new _$$Lambda$BadgedImageView$Z7e3tGxE0eQYPk5Be9lp1Zt58bs(this, dotOnLeft));
        }
        else {
            this.setDotOnLeft(dotOnLeft);
        }
    }
    
    void setDotScale(final float mDotScale) {
        this.mDotScale = mDotScale;
        this.invalidate();
    }
    
    public void setRenderedBubble(final BubbleViewProvider mBubble) {
        this.mBubble = mBubble;
        this.setImageBitmap(mBubble.getBadgedImage());
        this.mDotColor = mBubble.getDotColor();
        this.drawDot(mBubble.getDotPath());
    }
    
    void updateDotVisibility(final boolean b) {
        float n;
        if (this.shouldDrawDot()) {
            n = 1.0f;
        }
        else {
            n = 0.0f;
        }
        if (b) {
            this.animateDotScale(n, null);
        }
        else {
            this.mDotScale = n;
            this.mAnimatingToDotScale = n;
            this.invalidate();
        }
    }
    
    enum SuppressionFlag
    {
        BEHIND_STACK, 
        FLYOUT_VISIBLE;
    }
}
