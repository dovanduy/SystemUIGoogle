// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice;

import android.net.Uri;
import android.content.Intent;
import android.os.Build$VERSION;
import android.content.Context;

public abstract class SliceViewManager
{
    SliceViewManager() {
    }
    
    public static SliceViewManager getInstance(final Context context) {
        if (Build$VERSION.SDK_INT >= 28) {
            return new SliceViewManagerWrapper(context);
        }
        return new SliceViewManagerCompat(context);
    }
    
    public abstract Slice bindSlice(final Intent p0);
    
    public abstract Slice bindSlice(final Uri p0);
    
    public abstract void pinSlice(final Uri p0);
    
    public abstract void registerSliceCallback(final Uri p0, final SliceCallback p1);
    
    public abstract void unpinSlice(final Uri p0);
    
    public abstract void unregisterSliceCallback(final Uri p0, final SliceCallback p1);
    
    public interface SliceCallback
    {
        void onSliceUpdated(final Slice p0);
    }
}
