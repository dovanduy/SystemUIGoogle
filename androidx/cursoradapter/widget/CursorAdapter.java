// 
// Decompiled by Procyon v0.5.36
// 

package androidx.cursoradapter.widget;

import android.database.Cursor;
import android.widget.Filterable;
import android.widget.BaseAdapter;

public abstract class CursorAdapter extends BaseAdapter implements Filterable
{
    public abstract CharSequence convertToString(final Cursor p0);
    
    public abstract Cursor getCursor();
}
