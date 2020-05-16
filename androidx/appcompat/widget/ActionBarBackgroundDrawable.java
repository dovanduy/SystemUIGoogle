// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.widget;

import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

class ActionBarBackgroundDrawable extends Drawable
{
    final ActionBarContainer mContainer;
    
    public ActionBarBackgroundDrawable(final ActionBarContainer mContainer) {
        this.mContainer = mContainer;
    }
    
    public void draw(final Canvas canvas) {
        final ActionBarContainer mContainer = this.mContainer;
        if (mContainer.mIsSplit) {
            final Drawable mSplitBackground = mContainer.mSplitBackground;
            if (mSplitBackground != null) {
                mSplitBackground.draw(canvas);
            }
        }
        else {
            final Drawable mBackground = mContainer.mBackground;
            if (mBackground != null) {
                mBackground.draw(canvas);
            }
            final ActionBarContainer mContainer2 = this.mContainer;
            final Drawable mStackedBackground = mContainer2.mStackedBackground;
            if (mStackedBackground != null && mContainer2.mIsStacked) {
                mStackedBackground.draw(canvas);
            }
        }
    }
    
    public int getOpacity() {
        return 0;
    }
    
    public void getOutline(final Outline outline) {
        final ActionBarContainer mContainer = this.mContainer;
        if (mContainer.mIsSplit) {
            final Drawable mSplitBackground = mContainer.mSplitBackground;
            if (mSplitBackground != null) {
                mSplitBackground.getOutline(outline);
            }
        }
        else {
            final Drawable mBackground = mContainer.mBackground;
            if (mBackground != null) {
                mBackground.getOutline(outline);
            }
        }
    }
    
    public void setAlpha(final int n) {
    }
    
    public void setColorFilter(final ColorFilter colorFilter) {
    }
}
