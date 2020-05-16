// 
// Decompiled by Procyon v0.5.36
// 

package androidx.viewpager.widget;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.graphics.Canvas;
import android.view.View;
import android.view.View$OnClickListener;
import android.view.ViewConfiguration;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Paint;

public class PagerTabStrip extends PagerTitleStrip
{
    private boolean mDrawFullUnderline;
    private boolean mDrawFullUnderlineSet;
    private int mFullUnderlineHeight;
    private boolean mIgnoreTap;
    private int mIndicatorColor;
    private int mIndicatorHeight;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private int mMinPaddingBottom;
    private int mMinStripHeight;
    private int mMinTextSpacing;
    private int mTabAlpha;
    private int mTabPadding;
    private final Paint mTabPaint;
    private int mTouchSlop;
    
    public PagerTabStrip(final Context context, final AttributeSet set) {
        super(context, set);
        final Paint mTabPaint = new Paint();
        this.mTabPaint = mTabPaint;
        this.mTabAlpha = 255;
        this.mDrawFullUnderline = false;
        this.mDrawFullUnderlineSet = false;
        mTabPaint.setColor(this.mIndicatorColor = super.mTextColor);
        final float density = context.getResources().getDisplayMetrics().density;
        this.mIndicatorHeight = (int)(3.0f * density + 0.5f);
        this.mMinPaddingBottom = (int)(6.0f * density + 0.5f);
        this.mMinTextSpacing = (int)(64.0f * density);
        this.mTabPadding = (int)(16.0f * density + 0.5f);
        this.mFullUnderlineHeight = (int)(1.0f * density + 0.5f);
        this.mMinStripHeight = (int)(density * 32.0f + 0.5f);
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.setPadding(this.getPaddingLeft(), this.getPaddingTop(), this.getPaddingRight(), this.getPaddingBottom());
        this.setTextSpacing(this.getTextSpacing());
        this.setWillNotDraw(false);
        super.mPrevText.setFocusable(true);
        super.mPrevText.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                final ViewPager mPager = PagerTabStrip.this.mPager;
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });
        super.mNextText.setFocusable(true);
        super.mNextText.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                final ViewPager mPager = PagerTabStrip.this.mPager;
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
            }
        });
        if (this.getBackground() == null) {
            this.mDrawFullUnderline = true;
        }
    }
    
    @Override
    int getMinHeight() {
        return Math.max(super.getMinHeight(), this.mMinStripHeight);
    }
    
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        final int height = this.getHeight();
        final int left = super.mCurrText.getLeft();
        final int mTabPadding = this.mTabPadding;
        final int right = super.mCurrText.getRight();
        final int mTabPadding2 = this.mTabPadding;
        final int mIndicatorHeight = this.mIndicatorHeight;
        this.mTabPaint.setColor(this.mTabAlpha << 24 | (this.mIndicatorColor & 0xFFFFFF));
        final float n = (float)(left - mTabPadding);
        final float n2 = (float)(height - mIndicatorHeight);
        final float n3 = (float)(right + mTabPadding2);
        final float n4 = (float)height;
        canvas.drawRect(n, n2, n3, n4, this.mTabPaint);
        if (this.mDrawFullUnderline) {
            this.mTabPaint.setColor(0xFF000000 | (this.mIndicatorColor & 0xFFFFFF));
            canvas.drawRect((float)this.getPaddingLeft(), (float)(height - this.mFullUnderlineHeight), (float)(this.getWidth() - this.getPaddingRight()), n4, this.mTabPaint);
        }
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        final int action = motionEvent.getAction();
        if (action != 0 && this.mIgnoreTap) {
            return false;
        }
        final float x = motionEvent.getX();
        final float y = motionEvent.getY();
        if (action != 0) {
            if (action != 1) {
                if (action == 2) {
                    if (Math.abs(x - this.mInitialMotionX) > this.mTouchSlop || Math.abs(y - this.mInitialMotionY) > this.mTouchSlop) {
                        this.mIgnoreTap = true;
                    }
                }
            }
            else if (x < super.mCurrText.getLeft() - this.mTabPadding) {
                final ViewPager mPager = super.mPager;
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
            else if (x > super.mCurrText.getRight() + this.mTabPadding) {
                final ViewPager mPager2 = super.mPager;
                mPager2.setCurrentItem(mPager2.getCurrentItem() + 1);
            }
        }
        else {
            this.mInitialMotionX = x;
            this.mInitialMotionY = y;
            this.mIgnoreTap = false;
        }
        return true;
    }
    
    public void setBackgroundColor(final int backgroundColor) {
        super.setBackgroundColor(backgroundColor);
        if (!this.mDrawFullUnderlineSet) {
            this.mDrawFullUnderline = ((backgroundColor & 0xFF000000) == 0x0);
        }
    }
    
    public void setBackgroundDrawable(final Drawable backgroundDrawable) {
        super.setBackgroundDrawable(backgroundDrawable);
        if (!this.mDrawFullUnderlineSet) {
            this.mDrawFullUnderline = (backgroundDrawable == null);
        }
    }
    
    public void setBackgroundResource(final int backgroundResource) {
        super.setBackgroundResource(backgroundResource);
        if (!this.mDrawFullUnderlineSet) {
            this.mDrawFullUnderline = (backgroundResource == 0);
        }
    }
    
    public void setPadding(final int n, final int n2, final int n3, final int n4) {
        final int mMinPaddingBottom = this.mMinPaddingBottom;
        int n5 = n4;
        if (n4 < mMinPaddingBottom) {
            n5 = mMinPaddingBottom;
        }
        super.setPadding(n, n2, n3, n5);
    }
    
    @Override
    public void setTextSpacing(final int n) {
        final int mMinTextSpacing = this.mMinTextSpacing;
        int textSpacing = n;
        if (n < mMinTextSpacing) {
            textSpacing = mMinTextSpacing;
        }
        super.setTextSpacing(textSpacing);
    }
    
    @Override
    void updateTextPositions(final int n, final float n2, final boolean b) {
        super.updateTextPositions(n, n2, b);
        this.mTabAlpha = (int)(Math.abs(n2 - 0.5f) * 2.0f * 255.0f);
        this.invalidate();
    }
}
