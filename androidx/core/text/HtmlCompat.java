// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.text;

import android.text.Html;
import android.os.Build$VERSION;
import android.text.Spanned;
import android.annotation.SuppressLint;

@SuppressLint({ "InlinedApi" })
public final class HtmlCompat
{
    public static Spanned fromHtml(final String s, final int n) {
        if (Build$VERSION.SDK_INT >= 24) {
            return Html.fromHtml(s, n);
        }
        return Html.fromHtml(s);
    }
    
    public static String toHtml(final Spanned spanned, final int n) {
        if (Build$VERSION.SDK_INT >= 24) {
            return Html.toHtml(spanned, n);
        }
        return Html.toHtml(spanned);
    }
}
