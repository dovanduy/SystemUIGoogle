// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.widget;

import android.content.res.TypedArray;
import android.view.ViewGroup$MarginLayoutParams;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityEvent;
import androidx.core.view.GravityCompat;
import android.graphics.Canvas;
import android.view.ViewGroup$LayoutParams;
import android.view.View$MeasureSpec;
import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.appcompat.R$styleable;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

public class LinearLayoutCompat extends ViewGroup
{
    private boolean mBaselineAligned;
    private int mBaselineAlignedChildIndex;
    private int mBaselineChildTop;
    private Drawable mDivider;
    private int mDividerHeight;
    private int mDividerPadding;
    private int mDividerWidth;
    private int mGravity;
    private int[] mMaxAscent;
    private int[] mMaxDescent;
    private int mOrientation;
    private int mShowDividers;
    private int mTotalLength;
    private boolean mUseLargestChild;
    private float mWeightSum;
    
    public LinearLayoutCompat(final Context context) {
        this(context, null);
    }
    
    public LinearLayoutCompat(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public LinearLayoutCompat(final Context context, final AttributeSet set, int n) {
        super(context, set, n);
        this.mBaselineAligned = true;
        this.mBaselineAlignedChildIndex = -1;
        this.mBaselineChildTop = 0;
        this.mGravity = 8388659;
        final TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, set, R$styleable.LinearLayoutCompat, n, 0);
        ViewCompat.saveAttributeDataForStyleable((View)this, context, R$styleable.LinearLayoutCompat, set, obtainStyledAttributes.getWrappedTypeArray(), n, 0);
        n = obtainStyledAttributes.getInt(R$styleable.LinearLayoutCompat_android_orientation, -1);
        if (n >= 0) {
            this.setOrientation(n);
        }
        n = obtainStyledAttributes.getInt(R$styleable.LinearLayoutCompat_android_gravity, -1);
        if (n >= 0) {
            this.setGravity(n);
        }
        final boolean boolean1 = obtainStyledAttributes.getBoolean(R$styleable.LinearLayoutCompat_android_baselineAligned, true);
        if (!boolean1) {
            this.setBaselineAligned(boolean1);
        }
        this.mWeightSum = obtainStyledAttributes.getFloat(R$styleable.LinearLayoutCompat_android_weightSum, -1.0f);
        this.mBaselineAlignedChildIndex = obtainStyledAttributes.getInt(R$styleable.LinearLayoutCompat_android_baselineAlignedChildIndex, -1);
        this.mUseLargestChild = obtainStyledAttributes.getBoolean(R$styleable.LinearLayoutCompat_measureWithLargestChild, false);
        this.setDividerDrawable(obtainStyledAttributes.getDrawable(R$styleable.LinearLayoutCompat_divider));
        this.mShowDividers = obtainStyledAttributes.getInt(R$styleable.LinearLayoutCompat_showDividers, 0);
        this.mDividerPadding = obtainStyledAttributes.getDimensionPixelSize(R$styleable.LinearLayoutCompat_dividerPadding, 0);
        obtainStyledAttributes.recycle();
    }
    
    private void forceUniformHeight(final int n, final int n2) {
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(this.getMeasuredHeight(), 1073741824);
        for (int i = 0; i < n; ++i) {
            final View virtualChild = this.getVirtualChildAt(i);
            if (virtualChild.getVisibility() != 8) {
                final LayoutParams layoutParams = (LayoutParams)virtualChild.getLayoutParams();
                if (layoutParams.height == -1) {
                    final int width = layoutParams.width;
                    layoutParams.width = virtualChild.getMeasuredWidth();
                    this.measureChildWithMargins(virtualChild, n2, 0, measureSpec, 0);
                    layoutParams.width = width;
                }
            }
        }
    }
    
    private void forceUniformWidth(final int n, final int n2) {
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(this.getMeasuredWidth(), 1073741824);
        for (int i = 0; i < n; ++i) {
            final View virtualChild = this.getVirtualChildAt(i);
            if (virtualChild.getVisibility() != 8) {
                final LayoutParams layoutParams = (LayoutParams)virtualChild.getLayoutParams();
                if (layoutParams.width == -1) {
                    final int height = layoutParams.height;
                    layoutParams.height = virtualChild.getMeasuredHeight();
                    this.measureChildWithMargins(virtualChild, measureSpec, 0, n2, 0);
                    layoutParams.height = height;
                }
            }
        }
    }
    
    private void setChildFrame(final View view, final int n, final int n2, final int n3, final int n4) {
        view.layout(n, n2, n3 + n, n4 + n2);
    }
    
    protected boolean checkLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return viewGroup$LayoutParams instanceof LayoutParams;
    }
    
    void drawDividersHorizontal(final Canvas canvas) {
        final int virtualChildCount = this.getVirtualChildCount();
        final boolean layoutRtl = ViewUtils.isLayoutRtl((View)this);
        for (int i = 0; i < virtualChildCount; ++i) {
            final View virtualChild = this.getVirtualChildAt(i);
            if (virtualChild != null && virtualChild.getVisibility() != 8 && this.hasDividerBeforeChildAt(i)) {
                final LayoutParams layoutParams = (LayoutParams)virtualChild.getLayoutParams();
                int n;
                if (layoutRtl) {
                    n = virtualChild.getRight() + layoutParams.rightMargin;
                }
                else {
                    n = virtualChild.getLeft() - layoutParams.leftMargin - this.mDividerWidth;
                }
                this.drawVerticalDivider(canvas, n);
            }
        }
        if (this.hasDividerBeforeChildAt(virtualChildCount)) {
            final View virtualChild2 = this.getVirtualChildAt(virtualChildCount - 1);
            int paddingLeft = 0;
            Label_0223: {
                int n2;
                int n3;
                if (virtualChild2 == null) {
                    if (layoutRtl) {
                        paddingLeft = this.getPaddingLeft();
                        break Label_0223;
                    }
                    n2 = this.getWidth() - this.getPaddingRight();
                    n3 = this.mDividerWidth;
                }
                else {
                    final LayoutParams layoutParams2 = (LayoutParams)virtualChild2.getLayoutParams();
                    if (!layoutRtl) {
                        paddingLeft = virtualChild2.getRight() + layoutParams2.rightMargin;
                        break Label_0223;
                    }
                    n2 = virtualChild2.getLeft() - layoutParams2.leftMargin;
                    n3 = this.mDividerWidth;
                }
                paddingLeft = n2 - n3;
            }
            this.drawVerticalDivider(canvas, paddingLeft);
        }
    }
    
    void drawDividersVertical(final Canvas canvas) {
        final int virtualChildCount = this.getVirtualChildCount();
        for (int i = 0; i < virtualChildCount; ++i) {
            final View virtualChild = this.getVirtualChildAt(i);
            if (virtualChild != null && virtualChild.getVisibility() != 8 && this.hasDividerBeforeChildAt(i)) {
                this.drawHorizontalDivider(canvas, virtualChild.getTop() - ((LayoutParams)virtualChild.getLayoutParams()).topMargin - this.mDividerHeight);
            }
        }
        if (this.hasDividerBeforeChildAt(virtualChildCount)) {
            final View virtualChild2 = this.getVirtualChildAt(virtualChildCount - 1);
            int n;
            if (virtualChild2 == null) {
                n = this.getHeight() - this.getPaddingBottom() - this.mDividerHeight;
            }
            else {
                n = virtualChild2.getBottom() + ((LayoutParams)virtualChild2.getLayoutParams()).bottomMargin;
            }
            this.drawHorizontalDivider(canvas, n);
        }
    }
    
    void drawHorizontalDivider(final Canvas canvas, final int n) {
        this.mDivider.setBounds(this.getPaddingLeft() + this.mDividerPadding, n, this.getWidth() - this.getPaddingRight() - this.mDividerPadding, this.mDividerHeight + n);
        this.mDivider.draw(canvas);
    }
    
    void drawVerticalDivider(final Canvas canvas, final int n) {
        this.mDivider.setBounds(n, this.getPaddingTop() + this.mDividerPadding, this.mDividerWidth + n, this.getHeight() - this.getPaddingBottom() - this.mDividerPadding);
        this.mDivider.draw(canvas);
    }
    
    protected LayoutParams generateDefaultLayoutParams() {
        final int mOrientation = this.mOrientation;
        if (mOrientation == 0) {
            return new LayoutParams(-2, -2);
        }
        if (mOrientation == 1) {
            return new LayoutParams(-1, -2);
        }
        return null;
    }
    
    public LayoutParams generateLayoutParams(final AttributeSet set) {
        return new LayoutParams(this.getContext(), set);
    }
    
    protected LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return new LayoutParams(viewGroup$LayoutParams);
    }
    
    public int getBaseline() {
        if (this.mBaselineAlignedChildIndex < 0) {
            return super.getBaseline();
        }
        final int childCount = this.getChildCount();
        final int mBaselineAlignedChildIndex = this.mBaselineAlignedChildIndex;
        if (childCount <= mBaselineAlignedChildIndex) {
            throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout set to an index that is out of bounds.");
        }
        final View child = this.getChildAt(mBaselineAlignedChildIndex);
        final int baseline = child.getBaseline();
        if (baseline != -1) {
            int mBaselineChildTop;
            final int n = mBaselineChildTop = this.mBaselineChildTop;
            if (this.mOrientation == 1) {
                final int n2 = this.mGravity & 0x70;
                mBaselineChildTop = n;
                if (n2 != 48) {
                    if (n2 != 16) {
                        if (n2 != 80) {
                            mBaselineChildTop = n;
                        }
                        else {
                            mBaselineChildTop = this.getBottom() - this.getTop() - this.getPaddingBottom() - this.mTotalLength;
                        }
                    }
                    else {
                        mBaselineChildTop = n + (this.getBottom() - this.getTop() - this.getPaddingTop() - this.getPaddingBottom() - this.mTotalLength) / 2;
                    }
                }
            }
            return mBaselineChildTop + ((LayoutParams)child.getLayoutParams()).topMargin + baseline;
        }
        if (this.mBaselineAlignedChildIndex == 0) {
            return -1;
        }
        throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout points to a View that doesn't know how to get its baseline.");
    }
    
    int getChildrenSkipCount(final View view, final int n) {
        return 0;
    }
    
    public Drawable getDividerDrawable() {
        return this.mDivider;
    }
    
    public int getDividerWidth() {
        return this.mDividerWidth;
    }
    
    public int getGravity() {
        return this.mGravity;
    }
    
    int getLocationOffset(final View view) {
        return 0;
    }
    
    int getNextLocationOffset(final View view) {
        return 0;
    }
    
    View getVirtualChildAt(final int n) {
        return this.getChildAt(n);
    }
    
    int getVirtualChildCount() {
        return this.getChildCount();
    }
    
    protected boolean hasDividerBeforeChildAt(int n) {
        final boolean b = false;
        final boolean b2 = false;
        boolean b3 = false;
        if (n == 0) {
            if ((this.mShowDividers & 0x1) != 0x0) {
                b3 = true;
            }
            return b3;
        }
        if (n == this.getChildCount()) {
            boolean b4 = b;
            if ((this.mShowDividers & 0x4) != 0x0) {
                b4 = true;
            }
            return b4;
        }
        boolean b5 = b2;
        if ((this.mShowDividers & 0x2) != 0x0) {
            --n;
            while (true) {
                b5 = b2;
                if (n < 0) {
                    break;
                }
                if (this.getChildAt(n).getVisibility() != 8) {
                    b5 = true;
                    break;
                }
                --n;
            }
        }
        return b5;
    }
    
    void layoutHorizontal(int n, int n2, int n3, int n4) {
        final boolean layoutRtl = ViewUtils.isLayoutRtl((View)this);
        final int paddingTop = this.getPaddingTop();
        final int n5 = n4 - n2;
        final int paddingBottom = this.getPaddingBottom();
        final int paddingBottom2 = this.getPaddingBottom();
        final int virtualChildCount = this.getVirtualChildCount();
        n2 = this.mGravity;
        n4 = (n2 & 0x70);
        final boolean mBaselineAligned = this.mBaselineAligned;
        final int[] mMaxAscent = this.mMaxAscent;
        final int[] mMaxDescent = this.mMaxDescent;
        n2 = GravityCompat.getAbsoluteGravity(0x800007 & n2, ViewCompat.getLayoutDirection((View)this));
        if (n2 != 1) {
            if (n2 != 5) {
                n2 = this.getPaddingLeft();
            }
            else {
                n2 = this.getPaddingLeft() + n3 - n - this.mTotalLength;
            }
        }
        else {
            n2 = this.getPaddingLeft() + (n3 - n - this.mTotalLength) / 2;
        }
        int n6;
        int n7;
        if (layoutRtl) {
            n6 = virtualChildCount - 1;
            n7 = -1;
        }
        else {
            n6 = 0;
            n7 = 1;
        }
        int i = 0;
        n3 = n4;
        n4 = paddingTop;
        while (i < virtualChildCount) {
            final int n8 = n6 + n7 * i;
            final View virtualChild = this.getVirtualChildAt(n8);
            if (virtualChild == null) {
                n2 += this.measureNullChild(n8);
            }
            else if (virtualChild.getVisibility() != 8) {
                final int measuredWidth = virtualChild.getMeasuredWidth();
                final int measuredHeight = virtualChild.getMeasuredHeight();
                final LayoutParams layoutParams = (LayoutParams)virtualChild.getLayoutParams();
                int baseline;
                if (mBaselineAligned && layoutParams.height != -1) {
                    baseline = virtualChild.getBaseline();
                }
                else {
                    baseline = -1;
                }
                if ((n = layoutParams.gravity) < 0) {
                    n = n3;
                }
                n &= 0x70;
                if (n != 16) {
                    if (n != 48) {
                        if (n != 80) {
                            n = n4;
                        }
                        else {
                            final int n9 = n = n5 - paddingBottom - measuredHeight - layoutParams.bottomMargin;
                            if (baseline != -1) {
                                n = virtualChild.getMeasuredHeight();
                                n = n9 - (mMaxDescent[2] - (n - baseline));
                            }
                        }
                    }
                    else {
                        final int n10 = n = layoutParams.topMargin + n4;
                        if (baseline != -1) {
                            n = n10 + (mMaxAscent[1] - baseline);
                        }
                    }
                }
                else {
                    n = (n5 - paddingTop - paddingBottom2 - measuredHeight) / 2 + n4 + layoutParams.topMargin - layoutParams.bottomMargin;
                }
                int n11 = n2;
                if (this.hasDividerBeforeChildAt(n8)) {
                    n11 = n2 + this.mDividerWidth;
                }
                n2 = layoutParams.leftMargin + n11;
                this.setChildFrame(virtualChild, n2 + this.getLocationOffset(virtualChild), n, measuredWidth, measuredHeight);
                final int rightMargin = layoutParams.rightMargin;
                n = this.getNextLocationOffset(virtualChild);
                i += this.getChildrenSkipCount(virtualChild, n8);
                n2 += measuredWidth + rightMargin + n;
            }
            ++i;
        }
    }
    
    void layoutVertical(int paddingTop, int i, int n, int n2) {
        final int paddingLeft = this.getPaddingLeft();
        final int n3 = n - paddingTop;
        final int paddingRight = this.getPaddingRight();
        final int paddingRight2 = this.getPaddingRight();
        final int virtualChildCount = this.getVirtualChildCount();
        final int mGravity = this.mGravity;
        paddingTop = (mGravity & 0x70);
        if (paddingTop != 16) {
            if (paddingTop != 80) {
                paddingTop = this.getPaddingTop();
            }
            else {
                paddingTop = this.getPaddingTop() + n2 - i - this.mTotalLength;
            }
        }
        else {
            paddingTop = this.getPaddingTop() + (n2 - i - this.mTotalLength) / 2;
        }
        View virtualChild;
        int measuredWidth;
        int measuredHeight;
        LayoutParams layoutParams;
        for (i = 0; i < virtualChildCount; ++i) {
            virtualChild = this.getVirtualChildAt(i);
            if (virtualChild == null) {
                n = paddingTop + this.measureNullChild(i);
            }
            else {
                n = paddingTop;
                if (virtualChild.getVisibility() != 8) {
                    measuredWidth = virtualChild.getMeasuredWidth();
                    measuredHeight = virtualChild.getMeasuredHeight();
                    layoutParams = (LayoutParams)virtualChild.getLayoutParams();
                    n2 = layoutParams.gravity;
                    if ((n = n2) < 0) {
                        n = (mGravity & 0x800007);
                    }
                    n = (GravityCompat.getAbsoluteGravity(n, ViewCompat.getLayoutDirection((View)this)) & 0x7);
                    Label_0273: {
                        if (n != 1) {
                            if (n != 5) {
                                n = layoutParams.leftMargin + paddingLeft;
                                break Label_0273;
                            }
                            n2 = n3 - paddingRight - measuredWidth;
                            n = layoutParams.rightMargin;
                        }
                        else {
                            n2 = (n3 - paddingLeft - paddingRight2 - measuredWidth) / 2 + paddingLeft + layoutParams.leftMargin;
                            n = layoutParams.rightMargin;
                        }
                        n = n2 - n;
                    }
                    n2 = paddingTop;
                    if (this.hasDividerBeforeChildAt(i)) {
                        n2 = paddingTop + this.mDividerHeight;
                    }
                    paddingTop = n2 + layoutParams.topMargin;
                    this.setChildFrame(virtualChild, n, paddingTop + this.getLocationOffset(virtualChild), measuredWidth, measuredHeight);
                    n = layoutParams.bottomMargin;
                    n2 = this.getNextLocationOffset(virtualChild);
                    i += this.getChildrenSkipCount(virtualChild, i);
                    paddingTop += measuredHeight + n + n2;
                    continue;
                }
            }
            paddingTop = n;
        }
    }
    
    void measureChildBeforeLayout(final View view, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.measureChildWithMargins(view, n2, n3, n4, n5);
    }
    
    void measureHorizontal(final int n, final int n2) {
        this.mTotalLength = 0;
        final int virtualChildCount = this.getVirtualChildCount();
        final int mode = View$MeasureSpec.getMode(n);
        final int mode2 = View$MeasureSpec.getMode(n2);
        if (this.mMaxAscent == null || this.mMaxDescent == null) {
            this.mMaxAscent = new int[4];
            this.mMaxDescent = new int[4];
        }
        final int[] mMaxAscent = this.mMaxAscent;
        final int[] mMaxDescent = this.mMaxDescent;
        mMaxAscent[2] = (mMaxAscent[3] = -1);
        mMaxAscent[0] = (mMaxAscent[1] = -1);
        mMaxDescent[2] = (mMaxDescent[3] = -1);
        mMaxDescent[0] = (mMaxDescent[1] = -1);
        final boolean mBaselineAligned = this.mBaselineAligned;
        final boolean mUseLargestChild = this.mUseLargestChild;
        int n3 = 1073741824;
        final boolean b = mode == 1073741824;
        int b2;
        int i = b2 = 0;
        int max2;
        int max = max2 = b2;
        int n5;
        int n4 = n5 = max2;
        int n7;
        int n6 = n7 = n5;
        int n8 = 1;
        float n9 = 0.0f;
        while (i < virtualChildCount) {
            final View virtualChild = this.getVirtualChildAt(i);
            int n16 = 0;
            int n17 = 0;
            Label_0869: {
                if (virtualChild == null) {
                    this.mTotalLength += this.measureNullChild(i);
                }
                else {
                    if (virtualChild.getVisibility() != 8) {
                        if (this.hasDividerBeforeChildAt(i)) {
                            this.mTotalLength += this.mDividerWidth;
                        }
                        final LayoutParams layoutParams = (LayoutParams)virtualChild.getLayoutParams();
                        final float weight = layoutParams.weight;
                        n9 += weight;
                        Label_0594: {
                            int max3;
                            if (mode == n3 && layoutParams.width == 0 && weight > 0.0f) {
                                if (b) {
                                    this.mTotalLength += layoutParams.leftMargin + layoutParams.rightMargin;
                                }
                                else {
                                    final int mTotalLength = this.mTotalLength;
                                    this.mTotalLength = Math.max(mTotalLength, layoutParams.leftMargin + mTotalLength + layoutParams.rightMargin);
                                }
                                if (!mBaselineAligned) {
                                    n5 = 1;
                                    break Label_0594;
                                }
                                final int measureSpec = View$MeasureSpec.makeMeasureSpec(0, 0);
                                virtualChild.measure(measureSpec, measureSpec);
                                max3 = b2;
                            }
                            else {
                                int width;
                                if (layoutParams.width == 0 && layoutParams.weight > 0.0f) {
                                    layoutParams.width = -2;
                                    width = 0;
                                }
                                else {
                                    width = Integer.MIN_VALUE;
                                }
                                int mTotalLength2;
                                if (n9 == 0.0f) {
                                    mTotalLength2 = this.mTotalLength;
                                }
                                else {
                                    mTotalLength2 = 0;
                                }
                                this.measureChildBeforeLayout(virtualChild, i, n, mTotalLength2, n2, 0);
                                if (width != Integer.MIN_VALUE) {
                                    layoutParams.width = width;
                                }
                                final int measuredWidth = virtualChild.getMeasuredWidth();
                                if (b) {
                                    this.mTotalLength += layoutParams.leftMargin + measuredWidth + layoutParams.rightMargin + this.getNextLocationOffset(virtualChild);
                                }
                                else {
                                    final int mTotalLength3 = this.mTotalLength;
                                    this.mTotalLength = Math.max(mTotalLength3, mTotalLength3 + measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin + this.getNextLocationOffset(virtualChild));
                                }
                                max3 = b2;
                                if (mUseLargestChild) {
                                    max3 = Math.max(measuredWidth, b2);
                                }
                            }
                            b2 = max3;
                        }
                        final int n10 = i;
                        final int n11 = 1073741824;
                        int n12;
                        if (mode2 != 1073741824 && layoutParams.height == -1) {
                            n12 = (n7 = 1);
                        }
                        else {
                            n12 = 0;
                        }
                        int n13 = layoutParams.topMargin + layoutParams.bottomMargin;
                        final int b3 = virtualChild.getMeasuredHeight() + n13;
                        final int combineMeasuredStates = View.combineMeasuredStates(n6, virtualChild.getMeasuredState());
                        if (mBaselineAligned) {
                            final int baseline = virtualChild.getBaseline();
                            if (baseline != -1) {
                                int n14;
                                if ((n14 = layoutParams.gravity) < 0) {
                                    n14 = this.mGravity;
                                }
                                final int n15 = ((n14 & 0x70) >> 4 & 0xFFFFFFFE) >> 1;
                                mMaxAscent[n15] = Math.max(mMaxAscent[n15], baseline);
                                mMaxDescent[n15] = Math.max(mMaxDescent[n15], b3 - baseline);
                            }
                        }
                        max = Math.max(max, b3);
                        if (n8 != 0 && layoutParams.height == -1) {
                            n8 = 1;
                        }
                        else {
                            n8 = 0;
                        }
                        int max4;
                        if (layoutParams.weight > 0.0f) {
                            if (n12 == 0) {
                                n13 = b3;
                            }
                            max4 = Math.max(n4, n13);
                        }
                        else {
                            if (n12 == 0) {
                                n13 = b3;
                            }
                            max2 = Math.max(max2, n13);
                            max4 = n4;
                        }
                        n16 = this.getChildrenSkipCount(virtualChild, n10) + n10;
                        n6 = combineMeasuredStates;
                        n4 = max4;
                        n17 = n11;
                        break Label_0869;
                    }
                    i += this.getChildrenSkipCount(virtualChild, i);
                }
                final int n18 = i;
                n17 = n3;
                n16 = n18;
            }
            final int n19 = n17;
            i = n16 + 1;
            n3 = n19;
        }
        if (this.mTotalLength > 0 && this.hasDividerBeforeChildAt(virtualChildCount)) {
            this.mTotalLength += this.mDividerWidth;
        }
        int max5;
        if (mMaxAscent[1] == -1 && mMaxAscent[0] == -1 && mMaxAscent[2] == -1 && mMaxAscent[3] == -1) {
            max5 = max;
        }
        else {
            max5 = Math.max(max, Math.max(mMaxAscent[3], Math.max(mMaxAscent[0], Math.max(mMaxAscent[1], mMaxAscent[2]))) + Math.max(mMaxDescent[3], Math.max(mMaxDescent[0], Math.max(mMaxDescent[1], mMaxDescent[2]))));
        }
        int n20 = n6;
        int n21 = max5;
        Label_1212: {
            if (mUseLargestChild) {
                if (mode != Integer.MIN_VALUE) {
                    n21 = max5;
                    if (mode != 0) {
                        break Label_1212;
                    }
                }
                this.mTotalLength = 0;
                int n22 = 0;
                while (true) {
                    n21 = max5;
                    if (n22 >= virtualChildCount) {
                        break;
                    }
                    final View virtualChild2 = this.getVirtualChildAt(n22);
                    if (virtualChild2 == null) {
                        this.mTotalLength += this.measureNullChild(n22);
                    }
                    else if (virtualChild2.getVisibility() == 8) {
                        n22 += this.getChildrenSkipCount(virtualChild2, n22);
                    }
                    else {
                        final LayoutParams layoutParams2 = (LayoutParams)virtualChild2.getLayoutParams();
                        if (b) {
                            this.mTotalLength += layoutParams2.leftMargin + b2 + layoutParams2.rightMargin + this.getNextLocationOffset(virtualChild2);
                        }
                        else {
                            final int mTotalLength4 = this.mTotalLength;
                            this.mTotalLength = Math.max(mTotalLength4, mTotalLength4 + b2 + layoutParams2.leftMargin + layoutParams2.rightMargin + this.getNextLocationOffset(virtualChild2));
                        }
                    }
                    ++n22;
                }
            }
        }
        final int n23 = this.mTotalLength + (this.getPaddingLeft() + this.getPaddingRight());
        this.mTotalLength = n23;
        final int resolveSizeAndState = View.resolveSizeAndState(Math.max(n23, this.getSuggestedMinimumWidth()), n, 0);
        final int n24 = (0xFFFFFF & resolveSizeAndState) - this.mTotalLength;
        int n25;
        int max7;
        int n26;
        if (n5 == 0 && (n24 == 0 || n9 <= 0.0f)) {
            final int max6 = Math.max(max2, n4);
            if (mUseLargestChild && mode != 1073741824) {
                for (int j = 0; j < virtualChildCount; ++j) {
                    final View virtualChild3 = this.getVirtualChildAt(j);
                    if (virtualChild3 != null) {
                        if (virtualChild3.getVisibility() != 8) {
                            if (((LayoutParams)virtualChild3.getLayoutParams()).weight > 0.0f) {
                                virtualChild3.measure(View$MeasureSpec.makeMeasureSpec(b2, 1073741824), View$MeasureSpec.makeMeasureSpec(virtualChild3.getMeasuredHeight(), 1073741824));
                            }
                        }
                    }
                }
            }
            n25 = virtualChildCount;
            max7 = n21;
            n26 = max6;
        }
        else {
            final float mWeightSum = this.mWeightSum;
            if (mWeightSum > 0.0f) {
                n9 = mWeightSum;
            }
            mMaxAscent[2] = (mMaxAscent[3] = -1);
            mMaxAscent[0] = (mMaxAscent[1] = -1);
            mMaxDescent[2] = (mMaxDescent[3] = -1);
            mMaxDescent[0] = (mMaxDescent[1] = -1);
            this.mTotalLength = 0;
            int n27 = -1;
            final int n28 = n20;
            int k = 0;
            int n29 = n8;
            final int n30 = virtualChildCount;
            int combineMeasuredStates2 = n28;
            int a = max2;
            int n31 = n24;
            while (k < n30) {
                final View virtualChild4 = this.getVirtualChildAt(k);
                if (virtualChild4 != null) {
                    if (virtualChild4.getVisibility() != 8) {
                        final LayoutParams layoutParams3 = (LayoutParams)virtualChild4.getLayoutParams();
                        final float weight2 = layoutParams3.weight;
                        if (weight2 > 0.0f) {
                            final int n32 = (int)(n31 * weight2 / n9);
                            final int childMeasureSpec = ViewGroup.getChildMeasureSpec(n2, this.getPaddingTop() + this.getPaddingBottom() + layoutParams3.topMargin + layoutParams3.bottomMargin, layoutParams3.height);
                            if (layoutParams3.width == 0 && mode == 1073741824) {
                                int n33;
                                if (n32 > 0) {
                                    n33 = n32;
                                }
                                else {
                                    n33 = 0;
                                }
                                virtualChild4.measure(View$MeasureSpec.makeMeasureSpec(n33, 1073741824), childMeasureSpec);
                            }
                            else {
                                int n34;
                                if ((n34 = virtualChild4.getMeasuredWidth() + n32) < 0) {
                                    n34 = 0;
                                }
                                virtualChild4.measure(View$MeasureSpec.makeMeasureSpec(n34, 1073741824), childMeasureSpec);
                            }
                            combineMeasuredStates2 = View.combineMeasuredStates(combineMeasuredStates2, virtualChild4.getMeasuredState() & 0xFF000000);
                            n9 -= weight2;
                            n31 -= n32;
                        }
                        if (b) {
                            this.mTotalLength += virtualChild4.getMeasuredWidth() + layoutParams3.leftMargin + layoutParams3.rightMargin + this.getNextLocationOffset(virtualChild4);
                        }
                        else {
                            final int mTotalLength5 = this.mTotalLength;
                            this.mTotalLength = Math.max(mTotalLength5, virtualChild4.getMeasuredWidth() + mTotalLength5 + layoutParams3.leftMargin + layoutParams3.rightMargin + this.getNextLocationOffset(virtualChild4));
                        }
                        final boolean b4 = mode2 != 1073741824 && layoutParams3.height == -1;
                        final int n35 = layoutParams3.topMargin + layoutParams3.bottomMargin;
                        final int b5 = virtualChild4.getMeasuredHeight() + n35;
                        final int max8 = Math.max(n27, b5);
                        int b6;
                        if (b4) {
                            b6 = n35;
                        }
                        else {
                            b6 = b5;
                        }
                        final int max9 = Math.max(a, b6);
                        if (n29 != 0 && layoutParams3.height == -1) {
                            n29 = 1;
                        }
                        else {
                            n29 = 0;
                        }
                        if (mBaselineAligned) {
                            final int baseline2 = virtualChild4.getBaseline();
                            if (baseline2 != -1) {
                                int n36;
                                if ((n36 = layoutParams3.gravity) < 0) {
                                    n36 = this.mGravity;
                                }
                                final int n37 = ((n36 & 0x70) >> 4 & 0xFFFFFFFE) >> 1;
                                mMaxAscent[n37] = Math.max(mMaxAscent[n37], baseline2);
                                mMaxDescent[n37] = Math.max(mMaxDescent[n37], b5 - baseline2);
                            }
                        }
                        a = max9;
                        n27 = max8;
                    }
                }
                ++k;
            }
            this.mTotalLength += this.getPaddingLeft() + this.getPaddingRight();
            if (mMaxAscent[1] == -1 && mMaxAscent[0] == -1 && mMaxAscent[2] == -1 && mMaxAscent[3] == -1) {
                max7 = n27;
            }
            else {
                max7 = Math.max(n27, Math.max(mMaxAscent[3], Math.max(mMaxAscent[0], Math.max(mMaxAscent[1], mMaxAscent[2]))) + Math.max(mMaxDescent[3], Math.max(mMaxDescent[0], Math.max(mMaxDescent[1], mMaxDescent[2]))));
            }
            n20 = combineMeasuredStates2;
            n8 = n29;
            n25 = n30;
            n26 = a;
        }
        if (n8 != 0 || mode2 == 1073741824) {
            n26 = max7;
        }
        this.setMeasuredDimension(resolveSizeAndState | (n20 & 0xFF000000), View.resolveSizeAndState(Math.max(n26 + (this.getPaddingTop() + this.getPaddingBottom()), this.getSuggestedMinimumHeight()), n2, n20 << 16));
        if (n7 != 0) {
            this.forceUniformHeight(n25, n);
        }
    }
    
    int measureNullChild(final int n) {
        return 0;
    }
    
    void measureVertical(final int n, final int n2) {
        this.mTotalLength = 0;
        final int virtualChildCount = this.getVirtualChildCount();
        final int mode = View$MeasureSpec.getMode(n);
        final int mode2 = View$MeasureSpec.getMode(n2);
        final int mBaselineAlignedChildIndex = this.mBaselineAlignedChildIndex;
        final boolean mUseLargestChild = this.mUseLargestChild;
        int max;
        int combineMeasuredStates = max = 0;
        int max3;
        int max2 = max3 = max;
        int i;
        int max4 = i = max3;
        int n4;
        int n3 = n4 = i;
        float n5 = 0.0f;
        int n6 = 1;
        while (i < virtualChildCount) {
            final View virtualChild = this.getVirtualChildAt(i);
            if (virtualChild == null) {
                this.mTotalLength += this.measureNullChild(i);
            }
            else if (virtualChild.getVisibility() == 8) {
                i += this.getChildrenSkipCount(virtualChild, i);
            }
            else {
                if (this.hasDividerBeforeChildAt(i)) {
                    this.mTotalLength += this.mDividerHeight;
                }
                final LayoutParams layoutParams = (LayoutParams)virtualChild.getLayoutParams();
                final float weight = layoutParams.weight;
                n5 += weight;
                if (mode2 == 1073741824 && layoutParams.height == 0 && weight > 0.0f) {
                    final int mTotalLength = this.mTotalLength;
                    this.mTotalLength = Math.max(mTotalLength, layoutParams.topMargin + mTotalLength + layoutParams.bottomMargin);
                    n3 = 1;
                }
                else {
                    int height;
                    if (layoutParams.height == 0 && layoutParams.weight > 0.0f) {
                        layoutParams.height = -2;
                        height = 0;
                    }
                    else {
                        height = Integer.MIN_VALUE;
                    }
                    int mTotalLength2;
                    if (n5 == 0.0f) {
                        mTotalLength2 = this.mTotalLength;
                    }
                    else {
                        mTotalLength2 = 0;
                    }
                    this.measureChildBeforeLayout(virtualChild, i, n, 0, n2, mTotalLength2);
                    if (height != Integer.MIN_VALUE) {
                        layoutParams.height = height;
                    }
                    final int measuredHeight = virtualChild.getMeasuredHeight();
                    final int mTotalLength3 = this.mTotalLength;
                    this.mTotalLength = Math.max(mTotalLength3, mTotalLength3 + measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin + this.getNextLocationOffset(virtualChild));
                    if (mUseLargestChild) {
                        max2 = Math.max(measuredHeight, max2);
                    }
                }
                final int n7 = i;
                if (mBaselineAlignedChildIndex >= 0 && mBaselineAlignedChildIndex == n7 + 1) {
                    this.mBaselineChildTop = this.mTotalLength;
                }
                if (n7 < mBaselineAlignedChildIndex && layoutParams.weight > 0.0f) {
                    throw new RuntimeException("A child of LinearLayout with index less than mBaselineAlignedChildIndex has weight > 0, which won't work.  Either remove the weight, or don't set mBaselineAlignedChildIndex.");
                }
                int n8;
                if (mode != 1073741824 && layoutParams.width == -1) {
                    n8 = (n4 = 1);
                }
                else {
                    n8 = 0;
                }
                int n9 = layoutParams.leftMargin + layoutParams.rightMargin;
                final int b = virtualChild.getMeasuredWidth() + n9;
                max = Math.max(max, b);
                final int combineMeasuredStates2 = View.combineMeasuredStates(combineMeasuredStates, virtualChild.getMeasuredState());
                int n10;
                if (n6 != 0 && layoutParams.width == -1) {
                    n10 = 1;
                }
                else {
                    n10 = 0;
                }
                int max5;
                if (layoutParams.weight > 0.0f) {
                    if (n8 == 0) {
                        n9 = b;
                    }
                    max3 = Math.max(max3, n9);
                    max5 = max4;
                }
                else {
                    if (n8 == 0) {
                        n9 = b;
                    }
                    max5 = Math.max(max4, n9);
                }
                final int childrenSkipCount = this.getChildrenSkipCount(virtualChild, n7);
                max4 = max5;
                final int n11 = combineMeasuredStates2;
                i = childrenSkipCount + n7;
                n6 = n10;
                combineMeasuredStates = n11;
            }
            ++i;
        }
        if (this.mTotalLength > 0 && this.hasDividerBeforeChildAt(virtualChildCount)) {
            this.mTotalLength += this.mDividerHeight;
        }
        if (mUseLargestChild && (mode2 == Integer.MIN_VALUE || mode2 == 0)) {
            this.mTotalLength = 0;
            for (int j = 0; j < virtualChildCount; ++j) {
                final View virtualChild2 = this.getVirtualChildAt(j);
                if (virtualChild2 == null) {
                    this.mTotalLength += this.measureNullChild(j);
                }
                else if (virtualChild2.getVisibility() == 8) {
                    j += this.getChildrenSkipCount(virtualChild2, j);
                }
                else {
                    final LayoutParams layoutParams2 = (LayoutParams)virtualChild2.getLayoutParams();
                    final int mTotalLength4 = this.mTotalLength;
                    this.mTotalLength = Math.max(mTotalLength4, mTotalLength4 + max2 + layoutParams2.topMargin + layoutParams2.bottomMargin + this.getNextLocationOffset(virtualChild2));
                }
            }
        }
        final int n12 = this.mTotalLength + (this.getPaddingTop() + this.getPaddingBottom());
        this.mTotalLength = n12;
        final int resolveSizeAndState = View.resolveSizeAndState(Math.max(n12, this.getSuggestedMinimumHeight()), n2, 0);
        final int n13 = (0xFFFFFF & resolveSizeAndState) - this.mTotalLength;
        int n14;
        int n15;
        int a;
        if (n3 == 0 && (n13 == 0 || n5 <= 0.0f)) {
            final int max6 = Math.max(max4, max3);
            if (mUseLargestChild && mode2 != 1073741824) {
                for (int k = 0; k < virtualChildCount; ++k) {
                    final View virtualChild3 = this.getVirtualChildAt(k);
                    if (virtualChild3 != null) {
                        if (virtualChild3.getVisibility() != 8) {
                            if (((LayoutParams)virtualChild3.getLayoutParams()).weight > 0.0f) {
                                virtualChild3.measure(View$MeasureSpec.makeMeasureSpec(virtualChild3.getMeasuredWidth(), 1073741824), View$MeasureSpec.makeMeasureSpec(max2, 1073741824));
                            }
                        }
                    }
                }
            }
            n14 = combineMeasuredStates;
            n15 = max6;
            a = max;
        }
        else {
            final float mWeightSum = this.mWeightSum;
            if (mWeightSum > 0.0f) {
                n5 = mWeightSum;
            }
            this.mTotalLength = 0;
            int n16 = n13;
            int l = 0;
            a = max;
            while (l < virtualChildCount) {
                final View virtualChild4 = this.getVirtualChildAt(l);
                if (virtualChild4.getVisibility() != 8) {
                    final LayoutParams layoutParams3 = (LayoutParams)virtualChild4.getLayoutParams();
                    final float weight2 = layoutParams3.weight;
                    if (weight2 > 0.0f) {
                        final int n17 = (int)(n16 * weight2 / n5);
                        final int paddingLeft = this.getPaddingLeft();
                        final int paddingRight = this.getPaddingRight();
                        final int leftMargin = layoutParams3.leftMargin;
                        final int rightMargin = layoutParams3.rightMargin;
                        final int width = layoutParams3.width;
                        final int n18 = n16 - n17;
                        final int childMeasureSpec = ViewGroup.getChildMeasureSpec(n, paddingLeft + paddingRight + leftMargin + rightMargin, width);
                        if (layoutParams3.height == 0 && mode2 == 1073741824) {
                            int n19;
                            if (n17 > 0) {
                                n19 = n17;
                            }
                            else {
                                n19 = 0;
                            }
                            virtualChild4.measure(childMeasureSpec, View$MeasureSpec.makeMeasureSpec(n19, 1073741824));
                        }
                        else {
                            int n20;
                            if ((n20 = virtualChild4.getMeasuredHeight() + n17) < 0) {
                                n20 = 0;
                            }
                            virtualChild4.measure(childMeasureSpec, View$MeasureSpec.makeMeasureSpec(n20, 1073741824));
                        }
                        combineMeasuredStates = View.combineMeasuredStates(combineMeasuredStates, virtualChild4.getMeasuredState() & 0xFFFFFF00);
                        n5 -= weight2;
                        n16 = n18;
                    }
                    final int n21 = layoutParams3.leftMargin + layoutParams3.rightMargin;
                    final int b2 = virtualChild4.getMeasuredWidth() + n21;
                    final int max7 = Math.max(a, b2);
                    int b3;
                    if (mode != 1073741824 && layoutParams3.width == -1) {
                        b3 = n21;
                    }
                    else {
                        b3 = b2;
                    }
                    max4 = Math.max(max4, b3);
                    if (n6 != 0 && layoutParams3.width == -1) {
                        n6 = 1;
                    }
                    else {
                        n6 = 0;
                    }
                    final int mTotalLength5 = this.mTotalLength;
                    this.mTotalLength = Math.max(mTotalLength5, virtualChild4.getMeasuredHeight() + mTotalLength5 + layoutParams3.topMargin + layoutParams3.bottomMargin + this.getNextLocationOffset(virtualChild4));
                    a = max7;
                }
                ++l;
            }
            this.mTotalLength += this.getPaddingTop() + this.getPaddingBottom();
            n14 = combineMeasuredStates;
            n15 = max4;
        }
        if (n6 != 0 || mode == 1073741824) {
            n15 = a;
        }
        this.setMeasuredDimension(View.resolveSizeAndState(Math.max(n15 + (this.getPaddingLeft() + this.getPaddingRight()), this.getSuggestedMinimumWidth()), n, n14), resolveSizeAndState);
        if (n4 != 0) {
            this.forceUniformWidth(virtualChildCount, n2);
        }
    }
    
    protected void onDraw(final Canvas canvas) {
        if (this.mDivider == null) {
            return;
        }
        if (this.mOrientation == 1) {
            this.drawDividersVertical(canvas);
        }
        else {
            this.drawDividersHorizontal(canvas);
        }
    }
    
    public void onInitializeAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName((CharSequence)"androidx.appcompat.widget.LinearLayoutCompat");
    }
    
    public void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName((CharSequence)"androidx.appcompat.widget.LinearLayoutCompat");
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        if (this.mOrientation == 1) {
            this.layoutVertical(n, n2, n3, n4);
        }
        else {
            this.layoutHorizontal(n, n2, n3, n4);
        }
    }
    
    protected void onMeasure(final int n, final int n2) {
        if (this.mOrientation == 1) {
            this.measureVertical(n, n2);
        }
        else {
            this.measureHorizontal(n, n2);
        }
    }
    
    public void setBaselineAligned(final boolean mBaselineAligned) {
        this.mBaselineAligned = mBaselineAligned;
    }
    
    public void setDividerDrawable(final Drawable mDivider) {
        if (mDivider == this.mDivider) {
            return;
        }
        this.mDivider = mDivider;
        boolean willNotDraw = false;
        if (mDivider != null) {
            this.mDividerWidth = mDivider.getIntrinsicWidth();
            this.mDividerHeight = mDivider.getIntrinsicHeight();
        }
        else {
            this.mDividerWidth = 0;
            this.mDividerHeight = 0;
        }
        if (mDivider == null) {
            willNotDraw = true;
        }
        this.setWillNotDraw(willNotDraw);
        this.requestLayout();
    }
    
    public void setGravity(int mGravity) {
        if (this.mGravity != mGravity) {
            int n = mGravity;
            if ((0x800007 & mGravity) == 0x0) {
                n = (mGravity | 0x800003);
            }
            mGravity = n;
            if ((n & 0x70) == 0x0) {
                mGravity = (n | 0x30);
            }
            this.mGravity = mGravity;
            this.requestLayout();
        }
    }
    
    public void setOrientation(final int mOrientation) {
        if (this.mOrientation != mOrientation) {
            this.mOrientation = mOrientation;
            this.requestLayout();
        }
    }
    
    public boolean shouldDelayChildPressedState() {
        return false;
    }
    
    public static class LayoutParams extends ViewGroup$MarginLayoutParams
    {
        public int gravity;
        public float weight;
        
        public LayoutParams(final int n, final int n2) {
            super(n, n2);
            this.gravity = -1;
            this.weight = 0.0f;
        }
        
        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
            this.gravity = -1;
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.LinearLayoutCompat_Layout);
            this.weight = obtainStyledAttributes.getFloat(R$styleable.LinearLayoutCompat_Layout_android_layout_weight, 0.0f);
            this.gravity = obtainStyledAttributes.getInt(R$styleable.LinearLayoutCompat_Layout_android_layout_gravity, -1);
            obtainStyledAttributes.recycle();
        }
        
        public LayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
            this.gravity = -1;
        }
    }
}
