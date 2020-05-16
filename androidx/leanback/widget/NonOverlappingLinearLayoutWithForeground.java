// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.graphics.drawable.Drawable$Callback;
import android.view.View;
import android.graphics.Canvas;
import android.content.res.TypedArray;
import android.os.Build$VERSION;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;

class NonOverlappingLinearLayoutWithForeground extends LinearLayout
{
    private Drawable mForeground;
    private boolean mForegroundBoundsChanged;
    private final Rect mSelfBounds;
    
    public NonOverlappingLinearLayoutWithForeground(final Context context) {
        this(context, null);
    }
    
    public NonOverlappingLinearLayoutWithForeground(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public NonOverlappingLinearLayoutWithForeground(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mSelfBounds = new Rect();
        if (context.getApplicationInfo().targetSdkVersion < 23 || Build$VERSION.SDK_INT < 23) {
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, new int[] { 16843017 });
            final Drawable drawable = obtainStyledAttributes.getDrawable(0);
            if (drawable != null) {
                this.setForegroundCompat(drawable);
            }
            obtainStyledAttributes.recycle();
        }
    }
    
    public void draw(final Canvas canvas) {
        super.draw(canvas);
        final Drawable mForeground = this.mForeground;
        if (mForeground != null) {
            if (this.mForegroundBoundsChanged) {
                this.mForegroundBoundsChanged = false;
                final Rect mSelfBounds = this.mSelfBounds;
                mSelfBounds.set(0, 0, this.getRight() - this.getLeft(), this.getBottom() - this.getTop());
                mForeground.setBounds(mSelfBounds);
            }
            mForeground.draw(canvas);
        }
    }
    
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        final Drawable mForeground = this.mForeground;
        if (mForeground != null && mForeground.isStateful()) {
            this.mForeground.setState(this.getDrawableState());
        }
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        final Drawable mForeground = this.mForeground;
        if (mForeground != null) {
            mForeground.jumpToCurrentState();
        }
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        this.mForegroundBoundsChanged |= b;
    }
    
    public void setForegroundCompat(final Drawable mForeground) {
        if (Build$VERSION.SDK_INT >= 23) {
            ForegroundHelper.setForeground((View)this, mForeground);
        }
        else if (this.mForeground != mForeground) {
            this.mForeground = mForeground;
            this.mForegroundBoundsChanged = true;
            this.setWillNotDraw(false);
            this.mForeground.setCallback((Drawable$Callback)this);
            if (this.mForeground.isStateful()) {
                this.mForeground.setState(this.getDrawableState());
            }
        }
    }
    
    protected boolean verifyDrawable(final Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.mForeground;
    }
}
