// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import androidx.core.widget.TextViewCompat;
import android.view.ActionMode$Callback;
import androidx.leanback.R$attr;
import android.util.AttributeSet;
import android.content.Context;
import android.annotation.SuppressLint;
import android.widget.TextView;

@SuppressLint({ "AppCompatCustomView" })
public final class RowHeaderView extends TextView
{
    public RowHeaderView(final Context context) {
        this(context, null);
    }
    
    public RowHeaderView(final Context context, final AttributeSet set) {
        this(context, set, R$attr.rowHeaderStyle);
    }
    
    public RowHeaderView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
    }
    
    public void setCustomSelectionActionModeCallback(final ActionMode$Callback actionMode$Callback) {
        super.setCustomSelectionActionModeCallback(TextViewCompat.wrapCustomSelectionActionModeCallback(this, actionMode$Callback));
    }
}
