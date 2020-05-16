// 
// Decompiled by Procyon v0.5.36
// 

package androidx.viewpager.widget;

import java.util.Locale;
import android.text.method.SingleLineTransformationMethod;
import android.database.DataSetObserver;
import android.view.View$MeasureSpec;
import android.view.ViewParent;
import android.graphics.drawable.Drawable;
import android.text.method.TransformationMethod;
import android.content.res.TypedArray;
import android.text.TextUtils$TruncateAt;
import androidx.core.widget.TextViewCompat;
import androidx.core.view.ViewCompat;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import java.lang.ref.WeakReference;
import android.widget.TextView;
import android.view.ViewGroup;

@ViewPager.DecorView
public class PagerTitleStrip extends ViewGroup
{
    private static final int[] ATTRS;
    private static final int[] TEXT_ATTRS;
    TextView mCurrText;
    private int mGravity;
    private int mLastKnownCurrentPage;
    float mLastKnownPositionOffset;
    TextView mNextText;
    private int mNonPrimaryAlpha;
    private final PageListener mPageListener;
    ViewPager mPager;
    TextView mPrevText;
    private int mScaledTextSpacing;
    int mTextColor;
    private boolean mUpdatingPositions;
    private boolean mUpdatingText;
    private WeakReference<PagerAdapter> mWatchingAdapter;
    
    static {
        ATTRS = new int[] { 16842804, 16842901, 16842904, 16842927 };
        TEXT_ATTRS = new int[] { 16843660 };
    }
    
    public PagerTitleStrip(final Context context, final AttributeSet set) {
        super(context, set);
        this.mLastKnownCurrentPage = -1;
        this.mLastKnownPositionOffset = -1.0f;
        this.mPageListener = new PageListener();
        this.addView((View)(this.mPrevText = new TextView(context)));
        this.addView((View)(this.mCurrText = new TextView(context)));
        this.addView((View)(this.mNextText = new TextView(context)));
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, PagerTitleStrip.ATTRS);
        ViewCompat.saveAttributeDataForStyleable((View)this, context, PagerTitleStrip.ATTRS, set, obtainStyledAttributes, 0, 0);
        boolean boolean1 = false;
        final int resourceId = obtainStyledAttributes.getResourceId(0, 0);
        if (resourceId != 0) {
            TextViewCompat.setTextAppearance(this.mPrevText, resourceId);
            TextViewCompat.setTextAppearance(this.mCurrText, resourceId);
            TextViewCompat.setTextAppearance(this.mNextText, resourceId);
        }
        final int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(1, 0);
        if (dimensionPixelSize != 0) {
            this.setTextSize(0, (float)dimensionPixelSize);
        }
        if (obtainStyledAttributes.hasValue(2)) {
            final int color = obtainStyledAttributes.getColor(2, 0);
            this.mPrevText.setTextColor(color);
            this.mCurrText.setTextColor(color);
            this.mNextText.setTextColor(color);
        }
        this.mGravity = obtainStyledAttributes.getInteger(3, 80);
        obtainStyledAttributes.recycle();
        this.mTextColor = this.mCurrText.getTextColors().getDefaultColor();
        this.setNonPrimaryAlpha(0.6f);
        this.mPrevText.setEllipsize(TextUtils$TruncateAt.END);
        this.mCurrText.setEllipsize(TextUtils$TruncateAt.END);
        this.mNextText.setEllipsize(TextUtils$TruncateAt.END);
        if (resourceId != 0) {
            final TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(resourceId, PagerTitleStrip.TEXT_ATTRS);
            boolean1 = obtainStyledAttributes2.getBoolean(0, false);
            obtainStyledAttributes2.recycle();
        }
        if (boolean1) {
            setSingleLineAllCaps(this.mPrevText);
            setSingleLineAllCaps(this.mCurrText);
            setSingleLineAllCaps(this.mNextText);
        }
        else {
            this.mPrevText.setSingleLine();
            this.mCurrText.setSingleLine();
            this.mNextText.setSingleLine();
        }
        this.mScaledTextSpacing = (int)(context.getResources().getDisplayMetrics().density * 16.0f);
    }
    
    private static void setSingleLineAllCaps(final TextView textView) {
        textView.setTransformationMethod((TransformationMethod)new SingleLineAllCapsTransform(textView.getContext()));
    }
    
    int getMinHeight() {
        final Drawable background = this.getBackground();
        int intrinsicHeight;
        if (background != null) {
            intrinsicHeight = background.getIntrinsicHeight();
        }
        else {
            intrinsicHeight = 0;
        }
        return intrinsicHeight;
    }
    
    public int getTextSpacing() {
        return this.mScaledTextSpacing;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final ViewParent parent = this.getParent();
        if (parent instanceof ViewPager) {
            final ViewPager mPager = (ViewPager)parent;
            final PagerAdapter adapter = mPager.getAdapter();
            mPager.setInternalPageChangeListener((ViewPager.OnPageChangeListener)this.mPageListener);
            mPager.addOnAdapterChangeListener((ViewPager.OnAdapterChangeListener)this.mPageListener);
            this.mPager = mPager;
            final WeakReference<PagerAdapter> mWatchingAdapter = this.mWatchingAdapter;
            PagerAdapter pagerAdapter;
            if (mWatchingAdapter != null) {
                pagerAdapter = mWatchingAdapter.get();
            }
            else {
                pagerAdapter = null;
            }
            this.updateAdapter(pagerAdapter, adapter);
            return;
        }
        throw new IllegalStateException("PagerTitleStrip must be a direct child of a ViewPager.");
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        final ViewPager mPager = this.mPager;
        if (mPager != null) {
            this.updateAdapter(mPager.getAdapter(), null);
            this.mPager.setInternalPageChangeListener(null);
            this.mPager.removeOnAdapterChangeListener((ViewPager.OnAdapterChangeListener)this.mPageListener);
            this.mPager = null;
        }
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        if (this.mPager != null) {
            float mLastKnownPositionOffset = this.mLastKnownPositionOffset;
            if (mLastKnownPositionOffset < 0.0f) {
                mLastKnownPositionOffset = 0.0f;
            }
            this.updateTextPositions(this.mLastKnownCurrentPage, mLastKnownPositionOffset, true);
        }
    }
    
    protected void onMeasure(int n, final int n2) {
        if (View$MeasureSpec.getMode(n) == 1073741824) {
            final int n3 = this.getPaddingTop() + this.getPaddingBottom();
            final int childMeasureSpec = ViewGroup.getChildMeasureSpec(n2, n3, -2);
            final int size = View$MeasureSpec.getSize(n);
            n = ViewGroup.getChildMeasureSpec(n, (int)(size * 0.2f), -2);
            this.mPrevText.measure(n, childMeasureSpec);
            this.mCurrText.measure(n, childMeasureSpec);
            this.mNextText.measure(n, childMeasureSpec);
            if (View$MeasureSpec.getMode(n2) == 1073741824) {
                n = View$MeasureSpec.getSize(n2);
            }
            else {
                n = this.mCurrText.getMeasuredHeight();
                n = Math.max(this.getMinHeight(), n + n3);
            }
            this.setMeasuredDimension(size, View.resolveSizeAndState(n, n2, this.mCurrText.getMeasuredState() << 16));
            return;
        }
        throw new IllegalStateException("Must measure with an exact width");
    }
    
    public void requestLayout() {
        if (!this.mUpdatingText) {
            super.requestLayout();
        }
    }
    
    public void setNonPrimaryAlpha(final float n) {
        final int mNonPrimaryAlpha = (int)(n * 255.0f) & 0xFF;
        this.mNonPrimaryAlpha = mNonPrimaryAlpha;
        final int n2 = mNonPrimaryAlpha << 24 | (this.mTextColor & 0xFFFFFF);
        this.mPrevText.setTextColor(n2);
        this.mNextText.setTextColor(n2);
    }
    
    public void setTextSize(final int n, final float n2) {
        this.mPrevText.setTextSize(n, n2);
        this.mCurrText.setTextSize(n, n2);
        this.mNextText.setTextSize(n, n2);
    }
    
    public void setTextSpacing(final int mScaledTextSpacing) {
        this.mScaledTextSpacing = mScaledTextSpacing;
        this.requestLayout();
    }
    
    void updateAdapter(final PagerAdapter pagerAdapter, final PagerAdapter referent) {
        if (pagerAdapter != null) {
            pagerAdapter.unregisterDataSetObserver(this.mPageListener);
            this.mWatchingAdapter = null;
        }
        if (referent != null) {
            referent.registerDataSetObserver(this.mPageListener);
            this.mWatchingAdapter = new WeakReference<PagerAdapter>(referent);
        }
        final ViewPager mPager = this.mPager;
        if (mPager != null) {
            this.mLastKnownCurrentPage = -1;
            this.mLastKnownPositionOffset = -1.0f;
            this.updateText(mPager.getCurrentItem(), referent);
            this.requestLayout();
        }
    }
    
    void updateText(final int mLastKnownCurrentPage, final PagerAdapter pagerAdapter) {
        int count;
        if (pagerAdapter != null) {
            count = pagerAdapter.getCount();
        }
        else {
            count = 0;
        }
        this.mUpdatingText = true;
        final CharSequence charSequence = null;
        CharSequence pageTitle;
        if (mLastKnownCurrentPage >= 1 && pagerAdapter != null) {
            pageTitle = pagerAdapter.getPageTitle(mLastKnownCurrentPage - 1);
        }
        else {
            pageTitle = null;
        }
        this.mPrevText.setText(pageTitle);
        final TextView mCurrText = this.mCurrText;
        CharSequence pageTitle2;
        if (pagerAdapter != null && mLastKnownCurrentPage < count) {
            pageTitle2 = pagerAdapter.getPageTitle(mLastKnownCurrentPage);
        }
        else {
            pageTitle2 = null;
        }
        mCurrText.setText(pageTitle2);
        final int n = mLastKnownCurrentPage + 1;
        CharSequence pageTitle3 = charSequence;
        if (n < count) {
            pageTitle3 = charSequence;
            if (pagerAdapter != null) {
                pageTitle3 = pagerAdapter.getPageTitle(n);
            }
        }
        this.mNextText.setText(pageTitle3);
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(Math.max(0, (int)((this.getWidth() - this.getPaddingLeft() - this.getPaddingRight()) * 0.8f)), Integer.MIN_VALUE);
        final int measureSpec2 = View$MeasureSpec.makeMeasureSpec(Math.max(0, this.getHeight() - this.getPaddingTop() - this.getPaddingBottom()), Integer.MIN_VALUE);
        this.mPrevText.measure(measureSpec, measureSpec2);
        this.mCurrText.measure(measureSpec, measureSpec2);
        this.mNextText.measure(measureSpec, measureSpec2);
        this.mLastKnownCurrentPage = mLastKnownCurrentPage;
        if (!this.mUpdatingPositions) {
            this.updateTextPositions(mLastKnownCurrentPage, this.mLastKnownPositionOffset, false);
        }
        this.mUpdatingText = false;
    }
    
    void updateTextPositions(int paddingTop, final float mLastKnownPositionOffset, final boolean b) {
        if (paddingTop != this.mLastKnownCurrentPage) {
            this.updateText(paddingTop, this.mPager.getAdapter());
        }
        else if (!b && mLastKnownPositionOffset == this.mLastKnownPositionOffset) {
            return;
        }
        this.mUpdatingPositions = true;
        final int measuredWidth = this.mPrevText.getMeasuredWidth();
        final int measuredWidth2 = this.mCurrText.getMeasuredWidth();
        final int measuredWidth3 = this.mNextText.getMeasuredWidth();
        final int n = measuredWidth2 / 2;
        final int width = this.getWidth();
        final int height = this.getHeight();
        final int paddingLeft = this.getPaddingLeft();
        final int paddingRight = this.getPaddingRight();
        paddingTop = this.getPaddingTop();
        final int paddingBottom = this.getPaddingBottom();
        final int n2 = paddingRight + n;
        float n4;
        final float n3 = n4 = 0.5f + mLastKnownPositionOffset;
        if (n3 > 1.0f) {
            n4 = n3 - 1.0f;
        }
        final int n5 = width - n2 - (int)((width - (paddingLeft + n) - n2) * n4) - n;
        final int n6 = measuredWidth2 + n5;
        final int baseline = this.mPrevText.getBaseline();
        final int baseline2 = this.mCurrText.getBaseline();
        final int baseline3 = this.mNextText.getBaseline();
        final int max = Math.max(Math.max(baseline, baseline2), baseline3);
        final int n7 = max - baseline;
        final int n8 = max - baseline2;
        final int n9 = max - baseline3;
        final int max2 = Math.max(Math.max(this.mPrevText.getMeasuredHeight() + n7, this.mCurrText.getMeasuredHeight() + n8), this.mNextText.getMeasuredHeight() + n9);
        final int n10 = this.mGravity & 0x70;
        int n11 = 0;
        int n12 = 0;
        Label_0372: {
            if (n10 != 16) {
                if (n10 != 80) {
                    n11 = n7 + paddingTop;
                    n12 = n8 + paddingTop;
                    paddingTop += n9;
                    break Label_0372;
                }
                paddingTop = height - paddingBottom - max2;
            }
            else {
                paddingTop = (height - paddingTop - paddingBottom - max2) / 2;
            }
            n11 = n7 + paddingTop;
            n12 = n8 + paddingTop;
            paddingTop += n9;
        }
        final TextView mCurrText = this.mCurrText;
        mCurrText.layout(n5, n12, n6, mCurrText.getMeasuredHeight() + n12);
        final int min = Math.min(paddingLeft, n5 - this.mScaledTextSpacing - measuredWidth);
        final TextView mPrevText = this.mPrevText;
        mPrevText.layout(min, n11, measuredWidth + min, mPrevText.getMeasuredHeight() + n11);
        final int max3 = Math.max(width - paddingRight - measuredWidth3, n6 + this.mScaledTextSpacing);
        final TextView mNextText = this.mNextText;
        mNextText.layout(max3, paddingTop, max3 + measuredWidth3, mNextText.getMeasuredHeight() + paddingTop);
        this.mLastKnownPositionOffset = mLastKnownPositionOffset;
        this.mUpdatingPositions = false;
    }
    
    private class PageListener extends DataSetObserver implements OnPageChangeListener, OnAdapterChangeListener
    {
        private int mScrollState;
        
        PageListener() {
        }
        
        public void onAdapterChanged(final ViewPager viewPager, final PagerAdapter pagerAdapter, final PagerAdapter pagerAdapter2) {
            PagerTitleStrip.this.updateAdapter(pagerAdapter, pagerAdapter2);
        }
        
        public void onChanged() {
            final PagerTitleStrip this$0 = PagerTitleStrip.this;
            this$0.updateText(this$0.mPager.getCurrentItem(), PagerTitleStrip.this.mPager.getAdapter());
            float mLastKnownPositionOffset = PagerTitleStrip.this.mLastKnownPositionOffset;
            if (mLastKnownPositionOffset < 0.0f) {
                mLastKnownPositionOffset = 0.0f;
            }
            final PagerTitleStrip this$2 = PagerTitleStrip.this;
            this$2.updateTextPositions(this$2.mPager.getCurrentItem(), mLastKnownPositionOffset, true);
        }
        
        public void onPageScrollStateChanged(final int mScrollState) {
            this.mScrollState = mScrollState;
        }
        
        public void onPageScrolled(final int n, final float n2, int n3) {
            n3 = n;
            if (n2 > 0.5f) {
                n3 = n + 1;
            }
            PagerTitleStrip.this.updateTextPositions(n3, n2, false);
        }
        
        public void onPageSelected(final int n) {
            if (this.mScrollState == 0) {
                final PagerTitleStrip this$0 = PagerTitleStrip.this;
                this$0.updateText(this$0.mPager.getCurrentItem(), PagerTitleStrip.this.mPager.getAdapter());
                float mLastKnownPositionOffset = PagerTitleStrip.this.mLastKnownPositionOffset;
                if (mLastKnownPositionOffset < 0.0f) {
                    mLastKnownPositionOffset = 0.0f;
                }
                final PagerTitleStrip this$2 = PagerTitleStrip.this;
                this$2.updateTextPositions(this$2.mPager.getCurrentItem(), mLastKnownPositionOffset, true);
            }
        }
    }
    
    private static class SingleLineAllCapsTransform extends SingleLineTransformationMethod
    {
        private Locale mLocale;
        
        SingleLineAllCapsTransform(final Context context) {
            this.mLocale = context.getResources().getConfiguration().locale;
        }
        
        public CharSequence getTransformation(CharSequence transformation, final View view) {
            transformation = super.getTransformation(transformation, view);
            String upperCase;
            if (transformation != null) {
                upperCase = transformation.toString().toUpperCase(this.mLocale);
            }
            else {
                upperCase = null;
            }
            return upperCase;
        }
    }
}
