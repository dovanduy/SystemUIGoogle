// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice;

import java.util.Set;
import androidx.slice.compat.SliceProviderCompat;
import android.net.Uri;
import java.util.List;
import android.content.Context;

class SliceManagerCompat extends SliceManager
{
    private final Context mContext;
    
    SliceManagerCompat(final Context mContext) {
        this.mContext = mContext;
    }
    
    @Override
    public List<Uri> getPinnedSlices() {
        return SliceProviderCompat.getPinnedSlices(this.mContext);
    }
    
    @Override
    public Set<SliceSpec> getPinnedSpecs(final Uri uri) {
        return SliceProviderCompat.getPinnedSpecs(this.mContext, uri);
    }
}
