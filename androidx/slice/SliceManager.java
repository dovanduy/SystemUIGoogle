// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice;

import java.util.Set;
import android.net.Uri;
import java.util.List;
import android.os.Build$VERSION;
import android.content.Context;

public abstract class SliceManager
{
    SliceManager() {
    }
    
    public static SliceManager getInstance(final Context context) {
        if (Build$VERSION.SDK_INT >= 28) {
            return new SliceManagerWrapper(context);
        }
        return new SliceManagerCompat(context);
    }
    
    public abstract List<Uri> getPinnedSlices();
    
    public abstract Set<SliceSpec> getPinnedSpecs(final Uri p0);
}
