// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.widget;

import android.net.Uri;
import android.content.Context;

class SliceMetricsWrapper extends SliceMetrics
{
    private final android.app.slice.SliceMetrics mSliceMetrics;
    
    SliceMetricsWrapper(final Context context, final Uri uri) {
        this.mSliceMetrics = new android.app.slice.SliceMetrics(context, uri);
    }
    
    @Override
    protected void logHidden() {
        this.mSliceMetrics.logHidden();
    }
    
    @Override
    protected void logTouch(final int n, final Uri uri) {
        this.mSliceMetrics.logTouch(n, uri);
    }
    
    @Override
    protected void logVisible() {
        this.mSliceMetrics.logVisible();
    }
}
