// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice;

import android.database.Cursor;
import android.os.CancellationSignal;
import java.util.Collections;
import android.os.Process;
import androidx.slice.compat.CompatPermissionManager;
import androidx.slice.compat.SliceProviderWrapperContainer$SliceProviderWrapper;
import java.util.Collection;
import java.util.ArrayList;
import android.view.ContextThemeWrapper;
import android.util.TypedValue;
import java.util.Arrays;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.core.R$drawable;
import android.os.Build$VERSION;
import android.os.Bundle;
import android.content.ContentValues;
import android.content.pm.ProviderInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager$NameNotFoundException;
import androidx.slice.core.R$string;
import android.os.Parcelable;
import android.content.ComponentName;
import android.content.Intent;
import android.app.PendingIntent;
import android.net.Uri;
import java.util.List;
import android.content.Context;
import androidx.slice.compat.SliceProviderCompat;
import java.util.Set;
import androidx.core.app.CoreComponentFactory;
import android.content.ContentProvider;

public abstract class SliceProvider extends ContentProvider implements CompatWrapped
{
    private static Clock sClock;
    private static Set<SliceSpec> sSpecs;
    private String[] mAuthorities;
    private String mAuthority;
    private final String[] mAutoGrantPermissions;
    private SliceProviderCompat mCompat;
    private final Object mCompatLock;
    private Context mContext;
    private List<Uri> mPinnedSliceUris;
    private final Object mPinnedSliceUrisLock;
    
    public SliceProvider() {
        this.mContext = null;
        this.mCompatLock = new Object();
        this.mPinnedSliceUrisLock = new Object();
        this.mAutoGrantPermissions = new String[0];
    }
    
    private static PendingIntent createPermissionIntent(final Context context, final Uri uri, final String s) {
        final Intent intent = new Intent();
        intent.setComponent(new ComponentName(context.getPackageName(), "androidx.slice.compat.SlicePermissionActivity"));
        intent.putExtra("slice_uri", (Parcelable)uri);
        intent.putExtra("pkg", s);
        intent.putExtra("provider_pkg", context.getPackageName());
        intent.setData(uri.buildUpon().appendQueryParameter("package", s).build());
        return PendingIntent.getActivity(context, 0, intent, 0);
    }
    
    private static String getAuthorityWithoutUserId(final String s) {
        if (s == null) {
            return null;
        }
        return s.substring(s.lastIndexOf(64) + 1);
    }
    
    public static Clock getClock() {
        return SliceProvider.sClock;
    }
    
    public static Set<SliceSpec> getCurrentSpecs() {
        return SliceProvider.sSpecs;
    }
    
    private static CharSequence getPermissionString(final Context context, final String s) {
        final PackageManager packageManager = context.getPackageManager();
        try {
            return context.getString(R$string.abc_slices_permission_request, new Object[] { packageManager.getApplicationInfo(s, 0).loadLabel(packageManager), context.getApplicationInfo().loadLabel(packageManager) });
        }
        catch (PackageManager$NameNotFoundException cause) {
            throw new RuntimeException("Unknown calling app", (Throwable)cause);
        }
    }
    
    private SliceProviderCompat getSliceProviderCompat() {
        synchronized (this.mCompatLock) {
            if (this.mCompat == null) {
                this.mCompat = new SliceProviderCompat(this, this.onCreatePermissionManager(this.mAutoGrantPermissions), this.getContext());
            }
            // monitorexit(this.mCompatLock)
            return this.mCompat;
        }
    }
    
    private boolean matchesOurAuthorities(final String s) {
        final String mAuthority = this.mAuthority;
        if (mAuthority != null) {
            return mAuthority.equals(s);
        }
        final String[] mAuthorities = this.mAuthorities;
        if (mAuthorities != null) {
            for (int length = mAuthorities.length, i = 0; i < length; ++i) {
                if (this.mAuthorities[i].equals(s)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private void setAuthorities(final String mAuthority) {
        if (mAuthority != null) {
            if (mAuthority.indexOf(59) == -1) {
                this.mAuthority = mAuthority;
                this.mAuthorities = null;
            }
            else {
                this.mAuthority = null;
                this.mAuthorities = mAuthority.split(";");
            }
        }
    }
    
    public static void setSpecs(final Set<SliceSpec> sSpecs) {
        SliceProvider.sSpecs = sSpecs;
    }
    
    public void attachInfo(final Context mContext, final ProviderInfo providerInfo) {
        super.attachInfo(mContext, providerInfo);
        if (this.mContext == null) {
            this.mContext = mContext;
            if (providerInfo != null) {
                this.setAuthorities(providerInfo.authority);
            }
        }
    }
    
    public final int bulkInsert(final Uri uri, final ContentValues[] array) {
        return 0;
    }
    
    public Bundle call(final String s, final String s2, final Bundle bundle) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT < 19 || sdk_INT >= 28) {
            return null;
        }
        if (bundle == null) {
            return null;
        }
        return this.getSliceProviderCompat().call(s, s2, bundle);
    }
    
    public final Uri canonicalize(final Uri uri) {
        return null;
    }
    
    public Slice createPermissionSlice(final Uri uri, final String s) {
        final Context context = this.getContext();
        PendingIntent pendingIntent;
        if ((pendingIntent = this.onCreatePermissionRequest(uri, s)) == null) {
            pendingIntent = createPermissionIntent(context, uri, s);
        }
        final Slice.Builder builder = new Slice.Builder(uri);
        final Slice.Builder builder2 = new Slice.Builder(builder);
        builder2.addIcon(IconCompat.createWithResource(context, R$drawable.abc_ic_permission), null, new String[0]);
        builder2.addHints(Arrays.asList("title", "shortcut"));
        builder2.addAction(pendingIntent, new Slice.Builder(builder).build(), null);
        final TypedValue typedValue = new TypedValue();
        new ContextThemeWrapper(context, 16974123).getTheme().resolveAttribute(16843829, typedValue, true);
        final int data = typedValue.data;
        final Slice.Builder builder3 = new Slice.Builder(uri.buildUpon().appendPath("permission").build());
        builder3.addIcon(IconCompat.createWithResource(context, R$drawable.abc_ic_arrow_forward), null, new String[0]);
        builder3.addText(getPermissionString(context, s), null, new String[0]);
        builder3.addInt(data, "color", new String[0]);
        builder3.addSubSlice(builder2.build(), null);
        builder.addSubSlice(builder3.build(), null);
        builder.addHints(Arrays.asList("permission_request"));
        return builder.build();
    }
    
    public final int delete(final Uri uri, final String s, final String[] array) {
        return 0;
    }
    
    public List<Uri> getPinnedSlices() {
        synchronized (this.mPinnedSliceUrisLock) {
            if (this.mPinnedSliceUris == null) {
                this.mPinnedSliceUris = new ArrayList<Uri>(SliceManager.getInstance(this.getContext()).getPinnedSlices());
            }
            // monitorexit(this.mPinnedSliceUrisLock)
            return this.mPinnedSliceUris;
        }
    }
    
    public final String getType(final Uri uri) {
        if (Build$VERSION.SDK_INT < 19) {
            return null;
        }
        return "vnd.android.slice";
    }
    
    public Object getWrapper() {
        if (Build$VERSION.SDK_INT >= 28) {
            return new SliceProviderWrapperContainer$SliceProviderWrapper(this, this.mAutoGrantPermissions);
        }
        return null;
    }
    
    public void handleSlicePinned(final Uri uri) {
        final List<Uri> pinnedSlices = this.getPinnedSlices();
        if (!pinnedSlices.contains(uri)) {
            pinnedSlices.add(uri);
        }
    }
    
    public void handleSliceUnpinned(final Uri uri) {
        final List<Uri> pinnedSlices = this.getPinnedSlices();
        if (pinnedSlices.contains(uri)) {
            pinnedSlices.remove(uri);
        }
    }
    
    public final Uri insert(final Uri uri, final ContentValues contentValues) {
        return null;
    }
    
    public abstract Slice onBindSlice(final Uri p0);
    
    public final boolean onCreate() {
        return Build$VERSION.SDK_INT >= 19 && this.onCreateSliceProvider();
    }
    
    protected CompatPermissionManager onCreatePermissionManager(final String[] array) {
        final Context context = this.getContext();
        final StringBuilder sb = new StringBuilder();
        sb.append("slice_perms_");
        sb.append(this.getClass().getName());
        return new CompatPermissionManager(context, sb.toString(), Process.myUid(), array);
    }
    
    public PendingIntent onCreatePermissionRequest(final Uri uri, final String s) {
        return null;
    }
    
    public abstract boolean onCreateSliceProvider();
    
    public Collection<Uri> onGetSliceDescendants(final Uri uri) {
        return (Collection<Uri>)Collections.emptyList();
    }
    
    public Uri onMapIntentToUri(final Intent intent) {
        throw new UnsupportedOperationException("This provider has not implemented intent to uri mapping");
    }
    
    public void onSlicePinned(final Uri uri) {
    }
    
    public void onSliceUnpinned(final Uri uri) {
    }
    
    public final Cursor query(final Uri uri, final String[] array, final Bundle bundle, final CancellationSignal cancellationSignal) {
        return null;
    }
    
    public final Cursor query(final Uri uri, final String[] array, final String s, final String[] array2, final String s2) {
        return null;
    }
    
    public final Cursor query(final Uri uri, final String[] array, final String s, final String[] array2, final String s2, final CancellationSignal cancellationSignal) {
        return null;
    }
    
    public final int update(final Uri uri, final ContentValues contentValues, final String s, final String[] array) {
        return 0;
    }
    
    public void validateIncomingAuthority(String s) throws SecurityException {
        if (!this.matchesOurAuthorities(getAuthorityWithoutUserId(s))) {
            final StringBuilder sb = new StringBuilder();
            sb.append("The authority ");
            sb.append(s);
            sb.append(" does not match the one of the contentProvider: ");
            s = sb.toString();
            if (this.mAuthority != null) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append(s);
                sb2.append(this.mAuthority);
                s = sb2.toString();
            }
            else {
                final StringBuilder sb3 = new StringBuilder();
                sb3.append(s);
                sb3.append(Arrays.toString(this.mAuthorities));
                s = sb3.toString();
            }
            throw new SecurityException(s);
        }
    }
}
