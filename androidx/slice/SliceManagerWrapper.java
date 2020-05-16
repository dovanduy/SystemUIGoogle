// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice;

import android.os.Build$VERSION;
import java.util.Set;
import java.util.List;
import android.net.Uri$Builder;
import android.net.Uri;
import android.os.UserHandle;
import java.lang.reflect.InvocationTargetException;
import android.os.Process;
import android.content.Context;

class SliceManagerWrapper extends SliceManager
{
    private final android.app.slice.SliceManager mManager;
    
    SliceManagerWrapper(final android.app.slice.SliceManager mManager) {
        this.mManager = mManager;
    }
    
    SliceManagerWrapper(final Context context) {
        this((android.app.slice.SliceManager)context.getSystemService((Class)android.app.slice.SliceManager.class));
    }
    
    private int getCurrentUserId() {
        final UserHandle myUserHandle = Process.myUserHandle();
        try {
            return (int)myUserHandle.getClass().getDeclaredMethod("getIdentifier", (Class<?>[])new Class[0]).invoke(myUserHandle, new Object[0]);
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            return 0;
        }
    }
    
    private Uri maybeAddCurrentUserId(final Uri uri) {
        if (uri != null && !uri.getAuthority().contains("@")) {
            final String authority = uri.getAuthority();
            final Uri$Builder buildUpon = uri.buildUpon();
            final StringBuilder sb = new StringBuilder();
            sb.append(this.getCurrentUserId());
            sb.append("@");
            sb.append(authority);
            return buildUpon.encodedAuthority(sb.toString()).build();
        }
        return uri;
    }
    
    @Override
    public List<Uri> getPinnedSlices() {
        return (List<Uri>)this.mManager.getPinnedSlices();
    }
    
    @Override
    public Set<SliceSpec> getPinnedSpecs(final Uri uri) {
        Uri maybeAddCurrentUserId = uri;
        if (Build$VERSION.SDK_INT == 28) {
            maybeAddCurrentUserId = this.maybeAddCurrentUserId(uri);
        }
        return SliceConvert.wrap(this.mManager.getPinnedSpecs(maybeAddCurrentUserId));
    }
}
