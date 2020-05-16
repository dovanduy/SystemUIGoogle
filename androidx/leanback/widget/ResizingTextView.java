// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.content.res.TypedArray;
import androidx.core.widget.TextViewCompat;
import android.view.ActionMode$Callback;
import android.text.Layout;
import androidx.leanback.R$styleable;
import android.util.AttributeSet;
import android.content.Context;
import android.annotation.SuppressLint;
import android.widget.TextView;

@SuppressLint({ "AppCompatCustomView" })
class ResizingTextView extends TextView
{
    private float mDefaultLineSpacingExtra;
    private int mDefaultPaddingBottom;
    private int mDefaultPaddingTop;
    private int mDefaultTextSize;
    private boolean mDefaultsInitialized;
    private boolean mMaintainLineSpacing;
    private int mResizedPaddingAdjustmentBottom;
    private int mResizedPaddingAdjustmentTop;
    private int mResizedTextSize;
    private int mTriggerConditions;
    
    public ResizingTextView(final Context context) {
        this(context, null);
    }
    
    public ResizingTextView(final Context context, final AttributeSet set) {
        this(context, set, 16842884);
    }
    
    public ResizingTextView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public ResizingTextView(final Context context, AttributeSet obtainStyledAttributes, final int n, final int n2) {
        super(context, obtainStyledAttributes, n);
        this.mDefaultsInitialized = false;
        obtainStyledAttributes = (AttributeSet)context.obtainStyledAttributes(obtainStyledAttributes, R$styleable.lbResizingTextView, n, n2);
        try {
            this.mTriggerConditions = ((TypedArray)obtainStyledAttributes).getInt(R$styleable.lbResizingTextView_resizeTrigger, 1);
            this.mResizedTextSize = ((TypedArray)obtainStyledAttributes).getDimensionPixelSize(R$styleable.lbResizingTextView_resizedTextSize, -1);
            this.mMaintainLineSpacing = ((TypedArray)obtainStyledAttributes).getBoolean(R$styleable.lbResizingTextView_maintainLineSpacing, false);
            this.mResizedPaddingAdjustmentTop = ((TypedArray)obtainStyledAttributes).getDimensionPixelOffset(R$styleable.lbResizingTextView_resizedPaddingAdjustmentTop, 0);
            this.mResizedPaddingAdjustmentBottom = ((TypedArray)obtainStyledAttributes).getDimensionPixelOffset(R$styleable.lbResizingTextView_resizedPaddingAdjustmentBottom, 0);
        }
        finally {
            ((TypedArray)obtainStyledAttributes).recycle();
        }
    }
    
    private void setPaddingTopAndBottom(final int n, final int n2) {
        if (this.isPaddingRelative()) {
            this.setPaddingRelative(this.getPaddingStart(), n, this.getPaddingEnd(), n2);
        }
        else {
            this.setPadding(this.getPaddingLeft(), n, this.getPaddingRight(), n2);
        }
    }
    
    protected void onMeasure(final int n, final int n2) {
        final boolean mDefaultsInitialized = this.mDefaultsInitialized;
        final boolean b = true;
        if (!mDefaultsInitialized) {
            this.mDefaultTextSize = (int)this.getTextSize();
            this.mDefaultLineSpacingExtra = this.getLineSpacingExtra();
            this.mDefaultPaddingTop = this.getPaddingTop();
            this.mDefaultPaddingBottom = this.getPaddingBottom();
            this.mDefaultsInitialized = true;
        }
        final float n3 = (float)this.mDefaultTextSize;
        final int n4 = 0;
        final int n5 = 0;
        this.setTextSize(0, n3);
        this.setLineSpacing(this.mDefaultLineSpacingExtra, this.getLineSpacingMultiplier());
        this.setPaddingTopAndBottom(this.mDefaultPaddingTop, this.mDefaultPaddingBottom);
        super.onMeasure(n, n2);
        final Layout layout = this.getLayout();
        boolean b2 = false;
        Label_0155: {
            if (layout != null && (this.mTriggerConditions & 0x1) > 0) {
                final int lineCount = layout.getLineCount();
                final int maxLines = this.getMaxLines();
                if (maxLines > 1 && lineCount == maxLines) {
                    b2 = true;
                    break Label_0155;
                }
            }
            b2 = false;
        }
        final int n6 = (int)this.getTextSize();
        int n9;
        if (b2) {
            final int mResizedTextSize = this.mResizedTextSize;
            int n7 = n5;
            if (mResizedTextSize != -1) {
                n7 = n5;
                if (n6 != mResizedTextSize) {
                    this.setTextSize(0, (float)mResizedTextSize);
                    n7 = 1;
                }
            }
            final float n8 = this.mDefaultLineSpacingExtra + this.mDefaultTextSize - this.mResizedTextSize;
            n9 = n7;
            if (this.mMaintainLineSpacing) {
                n9 = n7;
                if (this.getLineSpacingExtra() != n8) {
                    this.setLineSpacing(n8, this.getLineSpacingMultiplier());
                    n9 = 1;
                }
            }
            final int n10 = this.mDefaultPaddingTop + this.mResizedPaddingAdjustmentTop;
            final int n11 = this.mDefaultPaddingBottom + this.mResizedPaddingAdjustmentBottom;
            if (this.getPaddingTop() != n10 || this.getPaddingBottom() != n11) {
                this.setPaddingTopAndBottom(n10, n11);
                n9 = (b ? 1 : 0);
            }
        }
        else {
            int n12 = n4;
            if (this.mResizedTextSize != -1) {
                final int mDefaultTextSize = this.mDefaultTextSize;
                n12 = n4;
                if (n6 != mDefaultTextSize) {
                    this.setTextSize(0, (float)mDefaultTextSize);
                    n12 = 1;
                }
            }
            n9 = n12;
            if (this.mMaintainLineSpacing) {
                final float lineSpacingExtra = this.getLineSpacingExtra();
                final float mDefaultLineSpacingExtra = this.mDefaultLineSpacingExtra;
                n9 = n12;
                if (lineSpacingExtra != mDefaultLineSpacingExtra) {
                    this.setLineSpacing(mDefaultLineSpacingExtra, this.getLineSpacingMultiplier());
                    n9 = 1;
                }
            }
            if (this.getPaddingTop() != this.mDefaultPaddingTop || this.getPaddingBottom() != this.mDefaultPaddingBottom) {
                this.setPaddingTopAndBottom(this.mDefaultPaddingTop, this.mDefaultPaddingBottom);
                n9 = (b ? 1 : 0);
            }
        }
        if (n9 != 0) {
            super.onMeasure(n, n2);
        }
    }
    
    public void setCustomSelectionActionModeCallback(final ActionMode$Callback actionMode$Callback) {
        super.setCustomSelectionActionModeCallback(TextViewCompat.wrapCustomSelectionActionModeCallback(this, actionMode$Callback));
    }
}
