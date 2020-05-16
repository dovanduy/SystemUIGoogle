// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.widget;

import android.util.Log;
import android.database.Cursor;
import android.annotation.SuppressLint;
import android.view.View$OnClickListener;
import androidx.cursoradapter.widget.ResourceCursorAdapter;

@SuppressLint({ "RestrictedAPI" })
class SuggestionsAdapter extends ResourceCursorAdapter implements View$OnClickListener
{
    public static String getColumnString(final Cursor cursor, final String s) {
        return getStringOrNull(cursor, cursor.getColumnIndex(s));
    }
    
    private static String getStringOrNull(final Cursor cursor, final int n) {
        if (n == -1) {
            return null;
        }
        try {
            return cursor.getString(n);
        }
        catch (Exception ex) {
            Log.e("SuggestionsAdapter", "unexpected error retrieving valid column from cursor, did the remote process die?", (Throwable)ex);
            return null;
        }
    }
}
