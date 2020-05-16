// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.view.inputmethod;

import android.os.Bundle;
import android.os.Build$VERSION;
import android.view.inputmethod.EditorInfo;

public final class EditorInfoCompat
{
    private static final String[] EMPTY_STRING_ARRAY;
    
    static {
        EMPTY_STRING_ARRAY = new String[0];
    }
    
    public static String[] getContentMimeTypes(final EditorInfo editorInfo) {
        String[] empty_STRING_ARRAY = EditorInfoCompat.EMPTY_STRING_ARRAY;
        if (Build$VERSION.SDK_INT >= 25) {
            final String[] contentMimeTypes = editorInfo.contentMimeTypes;
            if (contentMimeTypes != null) {
                empty_STRING_ARRAY = contentMimeTypes;
            }
            return empty_STRING_ARRAY;
        }
        final Bundle extras = editorInfo.extras;
        if (extras == null) {
            return empty_STRING_ARRAY;
        }
        String[] array;
        if ((array = extras.getStringArray("androidx.core.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES")) == null) {
            array = editorInfo.extras.getStringArray("android.support.v13.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES");
        }
        if (array != null) {
            empty_STRING_ARRAY = array;
        }
        return empty_STRING_ARRAY;
    }
    
    public static void setContentMimeTypes(final EditorInfo editorInfo, final String[] contentMimeTypes) {
        if (Build$VERSION.SDK_INT >= 25) {
            editorInfo.contentMimeTypes = contentMimeTypes;
        }
        else {
            if (editorInfo.extras == null) {
                editorInfo.extras = new Bundle();
            }
            editorInfo.extras.putStringArray("androidx.core.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES", contentMimeTypes);
            editorInfo.extras.putStringArray("android.support.v13.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES", contentMimeTypes);
        }
    }
}
