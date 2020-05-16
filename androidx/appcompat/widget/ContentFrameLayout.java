// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.widget;

import android.view.View;
import androidx.core.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.View$MeasureSpec;
import android.util.AttributeSet;
import android.content.Context;
import android.util.TypedValue;
import android.graphics.Rect;
import android.widget.FrameLayout;

public class ContentFrameLayout extends FrameLayout
{
    private OnAttachListener mAttachListener;
    private final Rect mDecorPadding;
    private TypedValue mFixedHeightMajor;
    private TypedValue mFixedHeightMinor;
    private TypedValue mFixedWidthMajor;
    private TypedValue mFixedWidthMinor;
    private TypedValue mMinWidthMajor;
    private TypedValue mMinWidthMinor;
    
    public ContentFrameLayout(final Context context) {
        this(context, null);
    }
    
    public ContentFrameLayout(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public ContentFrameLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mDecorPadding = new Rect();
    }
    
    public void dispatchFitSystemWindows(final Rect rect) {
        this.fitSystemWindows(rect);
    }
    
    public TypedValue getFixedHeightMajor() {
        if (this.mFixedHeightMajor == null) {
            this.mFixedHeightMajor = new TypedValue();
        }
        return this.mFixedHeightMajor;
    }
    
    public TypedValue getFixedHeightMinor() {
        if (this.mFixedHeightMinor == null) {
            this.mFixedHeightMinor = new TypedValue();
        }
        return this.mFixedHeightMinor;
    }
    
    public TypedValue getFixedWidthMajor() {
        if (this.mFixedWidthMajor == null) {
            this.mFixedWidthMajor = new TypedValue();
        }
        return this.mFixedWidthMajor;
    }
    
    public TypedValue getFixedWidthMinor() {
        if (this.mFixedWidthMinor == null) {
            this.mFixedWidthMinor = new TypedValue();
        }
        return this.mFixedWidthMinor;
    }
    
    public TypedValue getMinWidthMajor() {
        if (this.mMinWidthMajor == null) {
            this.mMinWidthMajor = new TypedValue();
        }
        return this.mMinWidthMajor;
    }
    
    public TypedValue getMinWidthMinor() {
        if (this.mMinWidthMinor == null) {
            this.mMinWidthMinor = new TypedValue();
        }
        return this.mMinWidthMinor;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final OnAttachListener mAttachListener = this.mAttachListener;
        if (mAttachListener != null) {
            mAttachListener.onAttachedFromWindow();
        }
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        final OnAttachListener mAttachListener = this.mAttachListener;
        if (mAttachListener != null) {
            mAttachListener.onDetachedFromWindow();
        }
    }
    
    protected void onMeasure(int n, int measureSpec) {
        final DisplayMetrics displayMetrics = this.getContext().getResources().getDisplayMetrics();
        final int widthPixels = displayMetrics.widthPixels;
        final int heightPixels = displayMetrics.heightPixels;
        final int n2 = 1;
        final boolean b = widthPixels < heightPixels;
        final int mode = View$MeasureSpec.getMode(n);
        final int mode2 = View$MeasureSpec.getMode(measureSpec);
        int measureSpec2 = 0;
        Label_0205: {
            if (mode == Integer.MIN_VALUE) {
                TypedValue typedValue;
                if (b) {
                    typedValue = this.mFixedWidthMinor;
                }
                else {
                    typedValue = this.mFixedWidthMajor;
                }
                if (typedValue != null) {
                    final int type = typedValue.type;
                    if (type != 0) {
                        int n4 = 0;
                        Label_0152: {
                            float n3;
                            if (type == 5) {
                                n3 = typedValue.getDimension(displayMetrics);
                            }
                            else {
                                if (type != 6) {
                                    n4 = 0;
                                    break Label_0152;
                                }
                                final int widthPixels2 = displayMetrics.widthPixels;
                                n3 = typedValue.getFraction((float)widthPixels2, (float)widthPixels2);
                            }
                            n4 = (int)n3;
                        }
                        if (n4 > 0) {
                            final Rect mDecorPadding = this.mDecorPadding;
                            measureSpec2 = View$MeasureSpec.makeMeasureSpec(Math.min(n4 - (mDecorPadding.left + mDecorPadding.right), View$MeasureSpec.getSize(n)), 1073741824);
                            n = 1;
                            break Label_0205;
                        }
                    }
                }
            }
            final int n5 = 0;
            measureSpec2 = n;
            n = n5;
        }
        int measureSpec3 = measureSpec;
        if (mode2 == Integer.MIN_VALUE) {
            TypedValue typedValue2;
            if (b) {
                typedValue2 = this.mFixedHeightMajor;
            }
            else {
                typedValue2 = this.mFixedHeightMinor;
            }
            measureSpec3 = measureSpec;
            if (typedValue2 != null) {
                final int type2 = typedValue2.type;
                measureSpec3 = measureSpec;
                if (type2 != 0) {
                    int n7 = 0;
                    Label_0312: {
                        float n6;
                        if (type2 == 5) {
                            n6 = typedValue2.getDimension(displayMetrics);
                        }
                        else {
                            if (type2 != 6) {
                                n7 = 0;
                                break Label_0312;
                            }
                            final int heightPixels2 = displayMetrics.heightPixels;
                            n6 = typedValue2.getFraction((float)heightPixels2, (float)heightPixels2);
                        }
                        n7 = (int)n6;
                    }
                    measureSpec3 = measureSpec;
                    if (n7 > 0) {
                        final Rect mDecorPadding2 = this.mDecorPadding;
                        measureSpec3 = View$MeasureSpec.makeMeasureSpec(Math.min(n7 - (mDecorPadding2.top + mDecorPadding2.bottom), View$MeasureSpec.getSize(measureSpec)), 1073741824);
                    }
                }
            }
        }
        super.onMeasure(measureSpec2, measureSpec3);
        final int measuredWidth = this.getMeasuredWidth();
        final int measureSpec4 = View$MeasureSpec.makeMeasureSpec(measuredWidth, 1073741824);
        Label_0520: {
            if (n == 0 && mode == Integer.MIN_VALUE) {
                TypedValue typedValue3;
                if (b) {
                    typedValue3 = this.mMinWidthMinor;
                }
                else {
                    typedValue3 = this.mMinWidthMajor;
                }
                if (typedValue3 != null) {
                    n = typedValue3.type;
                    if (n != 0) {
                        Label_0470: {
                            float n8;
                            if (n == 5) {
                                n8 = typedValue3.getDimension(displayMetrics);
                            }
                            else {
                                if (n != 6) {
                                    n = 0;
                                    break Label_0470;
                                }
                                n = displayMetrics.widthPixels;
                                n8 = typedValue3.getFraction((float)n, (float)n);
                            }
                            n = (int)n8;
                        }
                        measureSpec = n;
                        if (n > 0) {
                            final Rect mDecorPadding3 = this.mDecorPadding;
                            measureSpec = n - (mDecorPadding3.left + mDecorPadding3.right);
                        }
                        if (measuredWidth < measureSpec) {
                            measureSpec = View$MeasureSpec.makeMeasureSpec(measureSpec, 1073741824);
                            n = n2;
                            break Label_0520;
                        }
                    }
                }
            }
            n = 0;
            measureSpec = measureSpec4;
        }
        if (n != 0) {
            super.onMeasure(measureSpec, measureSpec3);
        }
    }
    
    public void setAttachListener(final OnAttachListener mAttachListener) {
        this.mAttachListener = mAttachListener;
    }
    
    public void setDecorPadding(final int n, final int n2, final int n3, final int n4) {
        this.mDecorPadding.set(n, n2, n3, n4);
        if (ViewCompat.isLaidOut((View)this)) {
            this.requestLayout();
        }
    }
    
    public interface OnAttachListener
    {
        void onAttachedFromWindow();
        
        void onDetachedFromWindow();
    }
}
