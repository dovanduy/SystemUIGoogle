// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.widget;

import android.os.Build$VERSION;
import android.net.Uri;
import android.content.Context;

class SliceMetrics
{
    public static SliceMetrics getInstance(final Context context, final Uri uri) {
        if (Build$VERSION.SDK_INT >= 28) {
            return new SliceMetricsWrapper(context, uri);
        }
        return null;
    }
    
    protected abstract void logHidden();
    
    protected abstract void logTouch(final int p0, final Uri p1);
    
    protected abstract void logVisible();
}
