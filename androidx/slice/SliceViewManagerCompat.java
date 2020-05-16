// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice;

import android.net.Uri;
import androidx.slice.compat.SliceProviderCompat;
import androidx.slice.widget.SliceLiveData;
import android.content.Intent;
import android.content.Context;

class SliceViewManagerCompat extends SliceViewManagerBase
{
    SliceViewManagerCompat(final Context context) {
        super(context);
    }
    
    @Override
    public Slice bindSlice(final Intent intent) {
        return SliceProviderCompat.bindSlice(super.mContext, intent, SliceLiveData.SUPPORTED_SPECS);
    }
    
    @Override
    public Slice bindSlice(final Uri uri) {
        return SliceProviderCompat.bindSlice(super.mContext, uri, SliceLiveData.SUPPORTED_SPECS);
    }
    
    @Override
    public void pinSlice(final Uri uri) {
        SliceProviderCompat.pinSlice(super.mContext, uri, SliceLiveData.SUPPORTED_SPECS);
    }
    
    @Override
    public void unpinSlice(final Uri uri) {
        SliceProviderCompat.unpinSlice(super.mContext, uri, SliceLiveData.SUPPORTED_SPECS);
    }
}
