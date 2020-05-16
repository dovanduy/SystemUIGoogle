// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.content.res.TypedArray;
import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.leanback.R$styleable;
import android.util.AttributeSet;
import android.content.Context;

public class VerticalGridView extends BaseGridView
{
    public VerticalGridView(final Context context) {
        this(context, null);
    }
    
    public VerticalGridView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public VerticalGridView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        super.mLayoutManager.setOrientation(1);
        this.initAttributes(context, set);
    }
    
    protected void initAttributes(final Context context, final AttributeSet set) {
        this.initBaseGridViewAttributes(context, set);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.lbVerticalGridView);
        ViewCompat.saveAttributeDataForStyleable((View)this, context, R$styleable.lbVerticalGridView, set, obtainStyledAttributes, 0, 0);
        this.setColumnWidth(obtainStyledAttributes);
        this.setNumColumns(obtainStyledAttributes.getInt(R$styleable.lbVerticalGridView_numberOfColumns, 1));
        obtainStyledAttributes.recycle();
    }
    
    public void setColumnWidth(final int rowHeight) {
        super.mLayoutManager.setRowHeight(rowHeight);
        this.requestLayout();
    }
    
    void setColumnWidth(final TypedArray typedArray) {
        if (typedArray.peekValue(R$styleable.lbVerticalGridView_columnWidth) != null) {
            this.setColumnWidth(typedArray.getLayoutDimension(R$styleable.lbVerticalGridView_columnWidth, 0));
        }
    }
    
    public void setNumColumns(final int numRows) {
        super.mLayoutManager.setNumRows(numRows);
        this.requestLayout();
    }
}
