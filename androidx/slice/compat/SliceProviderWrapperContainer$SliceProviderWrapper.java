// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.compat;

import java.util.Collection;
import android.app.PendingIntent;
import android.util.Log;
import androidx.slice.SliceConvert;
import android.app.slice.Slice;
import android.app.slice.SliceSpec;
import java.util.Set;
import android.os.Binder;
import android.content.Intent;
import android.os.Bundle;
import android.content.pm.ProviderInfo;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.app.slice.SliceManager;
import android.app.slice.SliceProvider;

public class SliceProviderWrapperContainer$SliceProviderWrapper extends SliceProvider
{
    private String[] mAutoGrantPermissions;
    private SliceManager mSliceManager;
    private androidx.slice.SliceProvider mSliceProvider;
    
    public SliceProviderWrapperContainer$SliceProviderWrapper(final androidx.slice.SliceProvider mSliceProvider, final String[] array) {
        super(array);
        String[] mAutoGrantPermissions = null;
        Label_0018: {
            if (array != null) {
                mAutoGrantPermissions = array;
                if (array.length != 0) {
                    break Label_0018;
                }
            }
            mAutoGrantPermissions = null;
        }
        this.mAutoGrantPermissions = mAutoGrantPermissions;
        this.mSliceProvider = mSliceProvider;
    }
    
    private void checkPermissions(final Uri uri) {
        if (uri != null) {
            for (final String s : this.mAutoGrantPermissions) {
                if (this.getContext().checkCallingPermission(s) == 0) {
                    this.mSliceManager.grantSlicePermission(s, uri);
                    this.getContext().getContentResolver().notifyChange(uri, (ContentObserver)null);
                    return;
                }
            }
        }
    }
    
    public void attachInfo(final Context context, final ProviderInfo providerInfo) {
        this.mSliceProvider.attachInfo(context, providerInfo);
        super.attachInfo(context, providerInfo);
        this.mSliceManager = (SliceManager)context.getSystemService((Class)SliceManager.class);
    }
    
    public Bundle call(final String anObject, final String s, final Bundle bundle) {
        if (this.mAutoGrantPermissions != null) {
            final boolean equals = "bind_slice".equals(anObject);
            final Uri uri = null;
            Uri uri2;
            if (equals) {
                uri2 = uri;
                if (bundle != null) {
                    uri2 = (Uri)bundle.getParcelable("slice_uri");
                }
            }
            else {
                uri2 = uri;
                if ("map_slice".equals(anObject)) {
                    final Intent intent = (Intent)bundle.getParcelable("slice_intent");
                    if (intent != null) {
                        this.onMapIntentToUri(intent);
                        throw null;
                    }
                    uri2 = uri;
                }
            }
            if (uri2 != null && this.mSliceManager.checkSlicePermission(uri2, Binder.getCallingPid(), Binder.getCallingUid()) != 0) {
                this.checkPermissions(uri2);
            }
        }
        if ("androidx.remotecallback.method.PROVIDER_CALLBACK".equals(anObject)) {
            return this.mSliceProvider.call(anObject, s, bundle);
        }
        return super.call(anObject, s, bundle);
    }
    
    public Slice onBindSlice(final Uri uri, final Set<SliceSpec> set) {
        androidx.slice.SliceProvider.setSpecs(SliceConvert.wrap(set));
        try {
            try {
                final Slice unwrap = SliceConvert.unwrap(this.mSliceProvider.onBindSlice(uri));
                androidx.slice.SliceProvider.setSpecs(null);
                return unwrap;
            }
            finally {}
        }
        catch (Exception ex) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Slice with URI ");
            sb.append(uri.toString());
            sb.append(" is invalid.");
            Log.wtf("SliceProviderWrapper", sb.toString(), (Throwable)ex);
            androidx.slice.SliceProvider.setSpecs(null);
            return null;
        }
        androidx.slice.SliceProvider.setSpecs(null);
    }
    
    public boolean onCreate() {
        return true;
    }
    
    public PendingIntent onCreatePermissionRequest(final Uri uri) {
        if (this.mAutoGrantPermissions != null) {
            this.checkPermissions(uri);
        }
        final PendingIntent onCreatePermissionRequest = this.mSliceProvider.onCreatePermissionRequest(uri, this.getCallingPackage());
        if (onCreatePermissionRequest != null) {
            return onCreatePermissionRequest;
        }
        return super.onCreatePermissionRequest(uri);
    }
    
    public Collection<Uri> onGetSliceDescendants(final Uri uri) {
        return this.mSliceProvider.onGetSliceDescendants(uri);
    }
    
    public Uri onMapIntentToUri(final Intent intent) {
        this.mSliceProvider.onMapIntentToUri(intent);
        throw null;
    }
    
    public void onSlicePinned(final Uri uri) {
        this.mSliceProvider.onSlicePinned(uri);
        this.mSliceProvider.handleSlicePinned(uri);
    }
    
    public void onSliceUnpinned(final Uri uri) {
        this.mSliceProvider.onSliceUnpinned(uri);
        this.mSliceProvider.handleSliceUnpinned(uri);
    }
}
