// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice;

import android.content.ContentProviderClient;
import android.net.Uri;
import android.content.pm.PackageManager$NameNotFoundException;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import androidx.slice.widget.SliceLiveData;
import android.content.Context;
import android.app.slice.SliceSpec;
import java.util.Set;
import android.app.slice.SliceManager;
import androidx.collection.ArrayMap;

class SliceViewManagerWrapper extends SliceViewManagerBase
{
    private final ArrayMap<String, String> mCachedAuthorities;
    private final ArrayMap<String, Boolean> mCachedSuspendFlags;
    private final SliceManager mManager;
    private final Set<SliceSpec> mSpecs;
    
    SliceViewManagerWrapper(final Context context) {
        this(context, (SliceManager)context.getSystemService((Class)SliceManager.class));
    }
    
    SliceViewManagerWrapper(final Context context, final SliceManager mManager) {
        super(context);
        this.mCachedSuspendFlags = new ArrayMap<String, Boolean>();
        this.mCachedAuthorities = new ArrayMap<String, String>();
        this.mManager = mManager;
        this.mSpecs = SliceConvert.unwrap(SliceLiveData.SUPPORTED_SPECS);
    }
    
    private boolean isAuthoritySuspended(final String s) {
        String packageName;
        if ((packageName = this.mCachedAuthorities.get(s)) == null) {
            final ProviderInfo resolveContentProvider = super.mContext.getPackageManager().resolveContentProvider(s, 0);
            if (resolveContentProvider == null) {
                return false;
            }
            packageName = resolveContentProvider.packageName;
            this.mCachedAuthorities.put(s, packageName);
        }
        return this.isPackageSuspended(packageName);
    }
    
    private boolean isPackageSuspended(final Intent intent) {
        if (intent.getComponent() != null) {
            return this.isPackageSuspended(intent.getComponent().getPackageName());
        }
        if (intent.getPackage() != null) {
            return this.isPackageSuspended(intent.getPackage());
        }
        return intent.getData() != null && this.isAuthoritySuspended(intent.getData().getAuthority());
    }
    
    private boolean isPackageSuspended(final String s) {
        Boolean value;
        if ((value = this.mCachedSuspendFlags.get(s)) == null) {
            try {
                value = ((super.mContext.getPackageManager().getApplicationInfo(s, 0).flags & 0x40000000) != 0x0);
                this.mCachedSuspendFlags.put(s, value);
            }
            catch (PackageManager$NameNotFoundException ex) {
                return false;
            }
        }
        return value;
    }
    
    @Override
    public Slice bindSlice(final Intent intent) {
        if (this.isPackageSuspended(intent)) {
            return null;
        }
        return SliceConvert.wrap(this.mManager.bindSlice(intent, (Set)this.mSpecs), super.mContext);
    }
    
    @Override
    public Slice bindSlice(final Uri uri) {
        if (this.isAuthoritySuspended(uri.getAuthority())) {
            return null;
        }
        return SliceConvert.wrap(this.mManager.bindSlice(uri, (Set)this.mSpecs), super.mContext);
    }
    
    @Override
    public void pinSlice(final Uri obj) {
        try {
            this.mManager.pinSlice(obj, (Set)this.mSpecs);
        }
        catch (RuntimeException ex) {
            final ContentProviderClient acquireContentProviderClient = super.mContext.getContentResolver().acquireContentProviderClient(obj);
            if (acquireContentProviderClient == null) {
                final StringBuilder sb = new StringBuilder();
                sb.append("No provider found for ");
                sb.append(obj);
                throw new IllegalArgumentException(sb.toString());
            }
            acquireContentProviderClient.release();
            throw ex;
        }
    }
    
    @Override
    public void unpinSlice(final Uri uri) {
        try {
            this.mManager.unpinSlice(uri);
        }
        catch (IllegalStateException ex) {}
    }
}
