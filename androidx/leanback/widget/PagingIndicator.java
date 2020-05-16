// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuff$Mode;
import android.view.View$MeasureSpec;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.BitmapFactory;
import androidx.leanback.R$drawable;
import android.animation.ObjectAnimator;
import android.content.res.TypedArray;
import android.content.res.Resources;
import android.animation.Animator;
import androidx.leanback.R$color;
import androidx.leanback.R$dimen;
import androidx.core.view.ViewCompat;
import androidx.leanback.R$styleable;
import android.util.AttributeSet;
import android.content.Context;
import android.view.animation.DecelerateInterpolator;
import android.graphics.Rect;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.animation.AnimatorSet;
import android.util.Property;
import android.animation.TimeInterpolator;
import android.view.View;

public class PagingIndicator extends View
{
    private static final TimeInterpolator DECELERATE_INTERPOLATOR;
    private static final Property<Dot, Float> DOT_ALPHA;
    private static final Property<Dot, Float> DOT_DIAMETER;
    private static final Property<Dot, Float> DOT_TRANSLATION_X;
    private final AnimatorSet mAnimator;
    Bitmap mArrow;
    final int mArrowDiameter;
    private final int mArrowGap;
    Paint mArrowPaint;
    final int mArrowRadius;
    final Rect mArrowRect;
    final float mArrowToBgRatio;
    final Paint mBgPaint;
    private int mCurrentPage;
    int mDotCenterY;
    final int mDotDiameter;
    int mDotFgSelectColor;
    private final int mDotGap;
    final int mDotRadius;
    private int[] mDotSelectedNextX;
    private int[] mDotSelectedPrevX;
    private int[] mDotSelectedX;
    private Dot[] mDots;
    final Paint mFgPaint;
    private final AnimatorSet mHideAnimator;
    boolean mIsLtr;
    private int mPageCount;
    private int mPreviousPage;
    private final int mShadowRadius;
    private final AnimatorSet mShowAnimator;
    
    static {
        DECELERATE_INTERPOLATOR = (TimeInterpolator)new DecelerateInterpolator();
        DOT_ALPHA = new Property<Dot, Float>("alpha") {
            public Float get(final Dot dot) {
                return dot.getAlpha();
            }
            
            public void set(final Dot dot, final Float n) {
                dot.setAlpha(n);
            }
        };
        DOT_DIAMETER = new Property<Dot, Float>("diameter") {
            public Float get(final Dot dot) {
                return dot.getDiameter();
            }
            
            public void set(final Dot dot, final Float n) {
                dot.setDiameter(n);
            }
        };
        DOT_TRANSLATION_X = new Property<Dot, Float>("translation_x") {
            public Float get(final Dot dot) {
                return dot.getTranslationX();
            }
            
            public void set(final Dot dot, final Float n) {
                dot.setTranslationX(n);
            }
        };
    }
    
    public PagingIndicator(final Context context) {
        this(context, null, 0);
    }
    
    public PagingIndicator(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public PagingIndicator(final Context context, final AttributeSet set, int color) {
        super(context, set, color);
        this.mAnimator = new AnimatorSet();
        final Resources resources = this.getResources();
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.PagingIndicator, color, 0);
        ViewCompat.saveAttributeDataForStyleable(this, context, R$styleable.PagingIndicator, set, obtainStyledAttributes, color, 0);
        color = this.getDimensionFromTypedArray(obtainStyledAttributes, R$styleable.PagingIndicator_lbDotRadius, R$dimen.lb_page_indicator_dot_radius);
        this.mDotRadius = color;
        this.mDotDiameter = color * 2;
        color = this.getDimensionFromTypedArray(obtainStyledAttributes, R$styleable.PagingIndicator_arrowRadius, R$dimen.lb_page_indicator_arrow_radius);
        this.mArrowRadius = color;
        this.mArrowDiameter = color * 2;
        this.mDotGap = this.getDimensionFromTypedArray(obtainStyledAttributes, R$styleable.PagingIndicator_dotToDotGap, R$dimen.lb_page_indicator_dot_gap);
        this.mArrowGap = this.getDimensionFromTypedArray(obtainStyledAttributes, R$styleable.PagingIndicator_dotToArrowGap, R$dimen.lb_page_indicator_arrow_gap);
        color = this.getColorFromTypedArray(obtainStyledAttributes, R$styleable.PagingIndicator_dotBgColor, R$color.lb_page_indicator_dot);
        (this.mBgPaint = new Paint(1)).setColor(color);
        this.mDotFgSelectColor = this.getColorFromTypedArray(obtainStyledAttributes, R$styleable.PagingIndicator_arrowBgColor, R$color.lb_page_indicator_arrow_background);
        if (this.mArrowPaint == null && obtainStyledAttributes.hasValue(R$styleable.PagingIndicator_arrowColor)) {
            this.setArrowColor(obtainStyledAttributes.getColor(R$styleable.PagingIndicator_arrowColor, 0));
        }
        obtainStyledAttributes.recycle();
        this.mIsLtr = (resources.getConfiguration().getLayoutDirection() == 0);
        color = resources.getColor(R$color.lb_page_indicator_arrow_shadow);
        this.mShadowRadius = resources.getDimensionPixelSize(R$dimen.lb_page_indicator_arrow_shadow_radius);
        this.mFgPaint = new Paint(1);
        final int dimensionPixelSize = resources.getDimensionPixelSize(R$dimen.lb_page_indicator_arrow_shadow_offset);
        final Paint mFgPaint = this.mFgPaint;
        final float n = (float)this.mShadowRadius;
        final float n2 = (float)dimensionPixelSize;
        mFgPaint.setShadowLayer(n, n2, n2, color);
        this.mArrow = this.loadArrow();
        this.mArrowRect = new Rect(0, 0, this.mArrow.getWidth(), this.mArrow.getHeight());
        this.mArrowToBgRatio = this.mArrow.getWidth() / (float)this.mArrowDiameter;
        (this.mShowAnimator = new AnimatorSet()).playTogether(new Animator[] { this.createDotAlphaAnimator(0.0f, 1.0f), this.createDotDiameterAnimator((float)(this.mDotRadius * 2), (float)(this.mArrowRadius * 2)), this.createDotTranslationXAnimator() });
        (this.mHideAnimator = new AnimatorSet()).playTogether(new Animator[] { this.createDotAlphaAnimator(1.0f, 0.0f), this.createDotDiameterAnimator((float)(this.mArrowRadius * 2), (float)(this.mDotRadius * 2)), this.createDotTranslationXAnimator() });
        this.mAnimator.playTogether(new Animator[] { (Animator)this.mShowAnimator, (Animator)this.mHideAnimator });
        this.setLayerType(1, (Paint)null);
    }
    
    private void adjustDotPosition() {
        int n = 0;
        int mCurrentPage;
        float n2;
        while (true) {
            mCurrentPage = this.mCurrentPage;
            n2 = -1.0f;
            if (n >= mCurrentPage) {
                break;
            }
            this.mDots[n].deselect();
            final Dot dot = this.mDots[n];
            if (n != this.mPreviousPage) {
                n2 = 1.0f;
            }
            dot.mDirection = n2;
            this.mDots[n].mCenterX = (float)this.mDotSelectedPrevX[n];
            ++n;
        }
        this.mDots[mCurrentPage].select();
        final Dot[] mDots = this.mDots;
        final int mCurrentPage2 = this.mCurrentPage;
        final Dot dot2 = mDots[mCurrentPage2];
        if (this.mPreviousPage >= mCurrentPage2) {
            n2 = 1.0f;
        }
        dot2.mDirection = n2;
        final Dot[] mDots2 = this.mDots;
        int mCurrentPage3 = this.mCurrentPage;
        mDots2[mCurrentPage3].mCenterX = (float)this.mDotSelectedX[mCurrentPage3];
        while (++mCurrentPage3 < this.mPageCount) {
            this.mDots[mCurrentPage3].deselect();
            final Dot[] mDots3 = this.mDots;
            mDots3[mCurrentPage3].mDirection = 1.0f;
            mDots3[mCurrentPage3].mCenterX = (float)this.mDotSelectedNextX[mCurrentPage3];
        }
    }
    
    private void calculateDotPositions() {
        final int paddingLeft = this.getPaddingLeft();
        final int paddingTop = this.getPaddingTop();
        final int width = this.getWidth();
        final int paddingRight = this.getPaddingRight();
        final int requiredWidth = this.getRequiredWidth();
        final int n = (paddingLeft + (width - paddingRight)) / 2;
        final int mPageCount = this.mPageCount;
        final int[] mDotSelectedX = new int[mPageCount];
        this.mDotSelectedX = mDotSelectedX;
        final int[] mDotSelectedPrevX = new int[mPageCount];
        this.mDotSelectedPrevX = mDotSelectedPrevX;
        final int[] mDotSelectedNextX = new int[mPageCount];
        this.mDotSelectedNextX = mDotSelectedNextX;
        final boolean mIsLtr = this.mIsLtr;
        final int n2 = 1;
        int i = 1;
        if (mIsLtr) {
            final int n3 = n - requiredWidth / 2;
            final int mDotRadius = this.mDotRadius;
            final int mDotGap = this.mDotGap;
            final int mArrowGap = this.mArrowGap;
            mDotSelectedX[0] = n3 + mDotRadius - mDotGap + mArrowGap;
            mDotSelectedNextX[0] = (mDotSelectedPrevX[0] = n3 + mDotRadius) - mDotGap * 2 + mArrowGap * 2;
            while (i < this.mPageCount) {
                final int[] mDotSelectedX2 = this.mDotSelectedX;
                final int[] mDotSelectedPrevX2 = this.mDotSelectedPrevX;
                final int n4 = i - 1;
                final int n5 = mDotSelectedPrevX2[n4];
                final int mArrowGap2 = this.mArrowGap;
                mDotSelectedX2[i] = n5 + mArrowGap2;
                mDotSelectedPrevX2[i] = mDotSelectedPrevX2[n4] + this.mDotGap;
                this.mDotSelectedNextX[i] = mDotSelectedX2[n4] + mArrowGap2;
                ++i;
            }
        }
        else {
            final int n6 = n + requiredWidth / 2;
            final int mDotRadius2 = this.mDotRadius;
            final int mDotGap2 = this.mDotGap;
            final int mArrowGap3 = this.mArrowGap;
            mDotSelectedX[0] = n6 - mDotRadius2 + mDotGap2 - mArrowGap3;
            mDotSelectedNextX[0] = (mDotSelectedPrevX[0] = n6 - mDotRadius2) + mDotGap2 * 2 - mArrowGap3 * 2;
            for (int j = n2; j < this.mPageCount; ++j) {
                final int[] mDotSelectedX3 = this.mDotSelectedX;
                final int[] mDotSelectedPrevX3 = this.mDotSelectedPrevX;
                final int n7 = j - 1;
                final int n8 = mDotSelectedPrevX3[n7];
                final int mArrowGap4 = this.mArrowGap;
                mDotSelectedX3[j] = n8 - mArrowGap4;
                mDotSelectedPrevX3[j] = mDotSelectedPrevX3[n7] - this.mDotGap;
                this.mDotSelectedNextX[j] = mDotSelectedX3[n7] - mArrowGap4;
            }
        }
        this.mDotCenterY = paddingTop + this.mArrowRadius;
        this.adjustDotPosition();
    }
    
    private Animator createDotAlphaAnimator(final float n, final float n2) {
        final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)null, (Property)PagingIndicator.DOT_ALPHA, new float[] { n, n2 });
        ofFloat.setDuration(167L);
        ofFloat.setInterpolator(PagingIndicator.DECELERATE_INTERPOLATOR);
        return (Animator)ofFloat;
    }
    
    private Animator createDotDiameterAnimator(final float n, final float n2) {
        final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)null, (Property)PagingIndicator.DOT_DIAMETER, new float[] { n, n2 });
        ofFloat.setDuration(417L);
        ofFloat.setInterpolator(PagingIndicator.DECELERATE_INTERPOLATOR);
        return (Animator)ofFloat;
    }
    
    private Animator createDotTranslationXAnimator() {
        final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)null, (Property)PagingIndicator.DOT_TRANSLATION_X, new float[] { (float)(-this.mArrowGap + this.mDotGap), 0.0f });
        ofFloat.setDuration(417L);
        ofFloat.setInterpolator(PagingIndicator.DECELERATE_INTERPOLATOR);
        return (Animator)ofFloat;
    }
    
    private int getColorFromTypedArray(final TypedArray typedArray, final int n, final int n2) {
        return typedArray.getColor(n, this.getResources().getColor(n2));
    }
    
    private int getDesiredHeight() {
        return this.getPaddingTop() + this.mArrowDiameter + this.getPaddingBottom() + this.mShadowRadius;
    }
    
    private int getDesiredWidth() {
        return this.getPaddingLeft() + this.getRequiredWidth() + this.getPaddingRight();
    }
    
    private int getDimensionFromTypedArray(final TypedArray typedArray, final int n, final int n2) {
        return typedArray.getDimensionPixelOffset(n, this.getResources().getDimensionPixelOffset(n2));
    }
    
    private int getRequiredWidth() {
        return this.mDotRadius * 2 + this.mArrowGap * 2 + (this.mPageCount - 3) * this.mDotGap;
    }
    
    private Bitmap loadArrow() {
        final Bitmap decodeResource = BitmapFactory.decodeResource(this.getResources(), R$drawable.lb_ic_nav_arrow);
        if (this.mIsLtr) {
            return decodeResource;
        }
        final Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        return Bitmap.createBitmap(decodeResource, 0, 0, decodeResource.getWidth(), decodeResource.getHeight(), matrix, false);
    }
    
    int[] getDotSelectedLeftX() {
        return this.mDotSelectedPrevX;
    }
    
    int[] getDotSelectedRightX() {
        return this.mDotSelectedNextX;
    }
    
    int[] getDotSelectedX() {
        return this.mDotSelectedX;
    }
    
    int getPageCount() {
        return this.mPageCount;
    }
    
    protected void onDraw(final Canvas canvas) {
        for (int i = 0; i < this.mPageCount; ++i) {
            this.mDots[i].draw(canvas);
        }
    }
    
    protected void onMeasure(int n, int n2) {
        final int desiredHeight = this.getDesiredHeight();
        final int mode = View$MeasureSpec.getMode(n2);
        if (mode != Integer.MIN_VALUE) {
            if (mode != 1073741824) {
                n2 = desiredHeight;
            }
            else {
                n2 = View$MeasureSpec.getSize(n2);
            }
        }
        else {
            n2 = Math.min(desiredHeight, View$MeasureSpec.getSize(n2));
        }
        final int desiredWidth = this.getDesiredWidth();
        final int mode2 = View$MeasureSpec.getMode(n);
        if (mode2 != Integer.MIN_VALUE) {
            if (mode2 != 1073741824) {
                n = desiredWidth;
            }
            else {
                n = View$MeasureSpec.getSize(n);
            }
        }
        else {
            n = Math.min(desiredWidth, View$MeasureSpec.getSize(n));
        }
        this.setMeasuredDimension(n, n2);
    }
    
    public void onRtlPropertiesChanged(int i) {
        super.onRtlPropertiesChanged(i);
        final int n = 0;
        final boolean mIsLtr = i == 0;
        if (this.mIsLtr != mIsLtr) {
            this.mIsLtr = mIsLtr;
            this.mArrow = this.loadArrow();
            final Dot[] mDots = this.mDots;
            if (mDots != null) {
                int length;
                for (length = mDots.length, i = n; i < length; ++i) {
                    mDots[i].onRtlPropertiesChanged();
                }
            }
            this.calculateDotPositions();
            this.invalidate();
        }
    }
    
    protected void onSizeChanged(final int n, final int n2, final int n3, final int n4) {
        this.setMeasuredDimension(n, n2);
        this.calculateDotPositions();
    }
    
    public void setArrowColor(final int n) {
        if (this.mArrowPaint == null) {
            this.mArrowPaint = new Paint();
        }
        this.mArrowPaint.setColorFilter((ColorFilter)new PorterDuffColorFilter(n, PorterDuff$Mode.SRC_IN));
    }
    
    public class Dot
    {
        float mAlpha;
        float mArrowImageRadius;
        float mCenterX;
        float mDiameter;
        float mDirection;
        int mFgColor;
        float mLayoutDirection;
        float mRadius;
        float mTranslationX;
        final /* synthetic */ PagingIndicator this$0;
        
        public void adjustAlpha() {
            this.mFgColor = Color.argb(Math.round(this.mAlpha * 255.0f), Color.red(this.this$0.mDotFgSelectColor), Color.green(this.this$0.mDotFgSelectColor), Color.blue(this.this$0.mDotFgSelectColor));
        }
        
        void deselect() {
            this.mTranslationX = 0.0f;
            this.mCenterX = 0.0f;
            final PagingIndicator this$0 = this.this$0;
            this.mDiameter = (float)this$0.mDotDiameter;
            final float mRadius = (float)this$0.mDotRadius;
            this.mRadius = mRadius;
            this.mArrowImageRadius = mRadius * this$0.mArrowToBgRatio;
            this.mAlpha = 0.0f;
            this.adjustAlpha();
        }
        
        void draw(final Canvas canvas) {
            final float n = this.mCenterX + this.mTranslationX;
            final PagingIndicator this$0 = this.this$0;
            canvas.drawCircle(n, (float)this$0.mDotCenterY, this.mRadius, this$0.mBgPaint);
            if (this.mAlpha > 0.0f) {
                this.this$0.mFgPaint.setColor(this.mFgColor);
                final PagingIndicator this$2 = this.this$0;
                canvas.drawCircle(n, (float)this$2.mDotCenterY, this.mRadius, this$2.mFgPaint);
                final PagingIndicator this$3 = this.this$0;
                final Bitmap mArrow = this$3.mArrow;
                final Rect mArrowRect = this$3.mArrowRect;
                final float mArrowImageRadius = this.mArrowImageRadius;
                final int n2 = (int)(n - mArrowImageRadius);
                final int mDotCenterY = this.this$0.mDotCenterY;
                canvas.drawBitmap(mArrow, mArrowRect, new Rect(n2, (int)(mDotCenterY - mArrowImageRadius), (int)(n + mArrowImageRadius), (int)(mDotCenterY + mArrowImageRadius)), this.this$0.mArrowPaint);
            }
        }
        
        public float getAlpha() {
            return this.mAlpha;
        }
        
        public float getDiameter() {
            return this.mDiameter;
        }
        
        public float getTranslationX() {
            return this.mTranslationX;
        }
        
        void onRtlPropertiesChanged() {
            float mLayoutDirection;
            if (this.this$0.mIsLtr) {
                mLayoutDirection = 1.0f;
            }
            else {
                mLayoutDirection = -1.0f;
            }
            this.mLayoutDirection = mLayoutDirection;
        }
        
        void select() {
            this.mTranslationX = 0.0f;
            this.mCenterX = 0.0f;
            final PagingIndicator this$0 = this.this$0;
            this.mDiameter = (float)this$0.mArrowDiameter;
            final float mRadius = (float)this$0.mArrowRadius;
            this.mRadius = mRadius;
            this.mArrowImageRadius = mRadius * this$0.mArrowToBgRatio;
            this.mAlpha = 1.0f;
            this.adjustAlpha();
        }
        
        public void setAlpha(final float mAlpha) {
            this.mAlpha = mAlpha;
            this.adjustAlpha();
            this.this$0.invalidate();
        }
        
        public void setDiameter(float n) {
            this.mDiameter = n;
            n /= 2.0f;
            this.mRadius = n;
            final PagingIndicator this$0 = this.this$0;
            this.mArrowImageRadius = n * this$0.mArrowToBgRatio;
            this$0.invalidate();
        }
        
        public void setTranslationX(final float n) {
            this.mTranslationX = n * this.mDirection * this.mLayoutDirection;
            this.this$0.invalidate();
        }
    }
}
