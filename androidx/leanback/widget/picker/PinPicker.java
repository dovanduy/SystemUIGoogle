// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget.picker;

import java.util.List;
import java.util.ArrayList;
import android.view.KeyEvent;
import android.content.res.TypedArray;
import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.leanback.R$styleable;
import androidx.leanback.R$attr;
import android.util.AttributeSet;
import android.content.Context;

public class PinPicker extends Picker
{
    public PinPicker(final Context context, final AttributeSet set) {
        this(context, set, R$attr.pinPickerStyle);
    }
    
    public PinPicker(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.lbPinPicker, n, 0);
        ViewCompat.saveAttributeDataForStyleable((View)this, context, R$styleable.lbPinPicker, set, obtainStyledAttributes, n, 0);
        try {
            this.setSeparator(" ");
            this.setNumberOfColumns(obtainStyledAttributes.getInt(R$styleable.lbPinPicker_columnCount, 4));
        }
        finally {
            obtainStyledAttributes.recycle();
        }
    }
    
    @Override
    public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
        final int keyCode = keyEvent.getKeyCode();
        if (keyEvent.getAction() == 1 && keyCode >= 7 && keyCode <= 16) {
            this.setColumnValue(this.getSelectedColumn(), keyCode - 7, false);
            this.performClick();
            return true;
        }
        return super.dispatchKeyEvent(keyEvent);
    }
    
    public boolean performClick() {
        final int selectedColumn = this.getSelectedColumn();
        if (selectedColumn == this.getColumnsCount() - 1) {
            return super.performClick();
        }
        this.setSelectedColumn(selectedColumn + 1);
        return false;
    }
    
    public void setNumberOfColumns(final int initialCapacity) {
        final ArrayList<PickerColumn> columns = new ArrayList<PickerColumn>(initialCapacity);
        for (int i = 0; i < initialCapacity; ++i) {
            final PickerColumn pickerColumn = new PickerColumn();
            pickerColumn.setMinValue(0);
            pickerColumn.setMaxValue(9);
            pickerColumn.setLabelFormat("%d");
            columns.add(pickerColumn);
        }
        this.setColumns(columns);
    }
}
