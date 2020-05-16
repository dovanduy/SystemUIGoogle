// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.compat;

import android.os.Build$VERSION;
import androidx.versionedparcelable.VersionedParcelable;
import androidx.versionedparcelable.ParcelUtils;
import androidx.slice.SliceItemHolder;
import android.os.StrictMode$ThreadPolicy;
import android.os.StrictMode$ThreadPolicy$Builder;
import android.os.StrictMode;
import android.os.Binder;
import android.content.pm.ActivityInfo;
import java.util.List;
import android.os.RemoteException;
import android.os.Parcelable;
import android.content.pm.ResolveInfo;
import android.net.Uri$Builder;
import androidx.core.util.Preconditions;
import androidx.slice.Slice;
import android.content.Intent;
import java.util.Iterator;
import java.util.ArrayList;
import androidx.slice.SliceSpec;
import android.os.Bundle;
import android.content.ContentProviderClient;
import android.net.Uri;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import java.util.Collection;
import androidx.collection.ArraySet;
import java.util.Set;
import java.util.Collections;
import android.util.Log;
import android.os.Process;
import android.os.Looper;
import androidx.slice.SliceProvider;
import android.os.Handler;
import android.content.Context;

public class SliceProviderCompat
{
    private final Runnable mAnr;
    String mCallback;
    private final Context mContext;
    private final Handler mHandler;
    private CompatPermissionManager mPermissionManager;
    private CompatPinnedList mPinnedList;
    private final SliceProvider mProvider;
    
    public SliceProviderCompat(final SliceProvider mProvider, final CompatPermissionManager mPermissionManager, final Context mContext) {
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mAnr = new Runnable() {
            @Override
            public void run() {
                Process.sendSignal(Process.myPid(), 3);
                final StringBuilder sb = new StringBuilder();
                sb.append("Timed out while handling slice callback ");
                sb.append(SliceProviderCompat.this.mCallback);
                Log.wtf("SliceProviderCompat", sb.toString());
            }
        };
        this.mProvider = mProvider;
        this.mContext = mContext;
        final SharedPreferences sharedPreferences = mContext.getSharedPreferences("slice_data_all_slice_files", 0);
        final Set stringSet = sharedPreferences.getStringSet("slice_data_all_slice_files", (Set)Collections.emptySet());
        if (!stringSet.contains("slice_data_androidx.slice.compat.SliceProviderCompat")) {
            final ArraySet set = new ArraySet<String>(stringSet);
            set.add("slice_data_androidx.slice.compat.SliceProviderCompat");
            sharedPreferences.edit().putStringSet("slice_data_all_slice_files", (Set)set).commit();
        }
        this.mPinnedList = new CompatPinnedList(this.mContext, "slice_data_androidx.slice.compat.SliceProviderCompat");
        this.mPermissionManager = mPermissionManager;
    }
    
    private static ProviderHolder acquireClient(final ContentResolver contentResolver, final Uri obj) {
        final ContentProviderClient acquireUnstableContentProviderClient = contentResolver.acquireUnstableContentProviderClient(obj);
        if (acquireUnstableContentProviderClient != null) {
            return new ProviderHolder(acquireUnstableContentProviderClient);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("No provider found for ");
        sb.append(obj);
        throw new IllegalArgumentException(sb.toString());
    }
    
    public static void addSpecs(final Bundle bundle, final Set<SliceSpec> set) {
        final ArrayList<String> list = new ArrayList<String>();
        final ArrayList<Integer> list2 = new ArrayList<Integer>();
        for (final SliceSpec sliceSpec : set) {
            list.add(sliceSpec.getType());
            list2.add(sliceSpec.getRevision());
        }
        bundle.putStringArrayList("specs", (ArrayList)list);
        bundle.putIntegerArrayList("revs", (ArrayList)list2);
    }
    
    public static Slice bindSlice(final Context context, final Intent intent, final Set<SliceSpec> set) {
        Preconditions.checkNotNull(intent, "intent");
        Preconditions.checkArgument(intent.getComponent() != null || intent.getPackage() != null || intent.getData() != null, String.format("Slice intent must be explicit %s", intent));
        final ContentResolver contentResolver = context.getContentResolver();
        final Uri data = intent.getData();
        if (data != null && "vnd.android.slice".equals(contentResolver.getType(data))) {
            return bindSlice(context, data, set);
        }
        final Intent intent2 = new Intent(intent);
        if (!intent2.hasCategory("android.app.slice.category.SLICE")) {
            intent2.addCategory("android.app.slice.category.SLICE");
        }
        final List queryIntentContentProviders = context.getPackageManager().queryIntentContentProviders(intent2, 0);
        if (queryIntentContentProviders == null || queryIntentContentProviders.isEmpty()) {
            final ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(intent, 128);
            if (resolveActivity != null) {
                final ActivityInfo activityInfo = resolveActivity.activityInfo;
                if (activityInfo != null) {
                    final Bundle metaData = activityInfo.metaData;
                    if (metaData != null && metaData.containsKey("android.metadata.SLICE_URI")) {
                        return bindSlice(context, Uri.parse(resolveActivity.activityInfo.metaData.getString("android.metadata.SLICE_URI")), set);
                    }
                }
            }
            return null;
        }
        final Uri build = new Uri$Builder().scheme("content").authority(queryIntentContentProviders.get(0).providerInfo.authority).build();
        final ProviderHolder acquireClient = acquireClient(contentResolver, build);
        if (acquireClient.mProvider == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Unknown URI ");
            sb.append(build);
            throw new IllegalArgumentException(sb.toString());
        }
        try {
            try {
                final Bundle bundle = new Bundle();
                bundle.putParcelable("slice_intent", (Parcelable)intent);
                addSpecs(bundle, set);
                final Slice slice = parseSlice(context, acquireClient.mProvider.call("map_slice", "supports_versioned_parcelable", bundle));
                acquireClient.close();
                return slice;
            }
            finally {}
        }
        catch (RemoteException ex) {
            Log.e("SliceProviderCompat", "Unable to bind slice", (Throwable)ex);
            acquireClient.close();
            return null;
        }
        acquireClient.close();
    }
    
    public static Slice bindSlice(final Context context, final Uri obj, final Set<SliceSpec> set) {
        final ProviderHolder acquireClient = acquireClient(context.getContentResolver(), obj);
        if (acquireClient.mProvider == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Unknown URI ");
            sb.append(obj);
            throw new IllegalArgumentException(sb.toString());
        }
        try {
            try {
                final Bundle bundle = new Bundle();
                bundle.putParcelable("slice_uri", (Parcelable)obj);
                addSpecs(bundle, set);
                final Slice slice = parseSlice(context, acquireClient.mProvider.call("bind_slice", "supports_versioned_parcelable", bundle));
                acquireClient.close();
                return slice;
            }
            finally {}
        }
        catch (RemoteException ex) {
            Log.e("SliceProviderCompat", "Unable to bind slice", (Throwable)ex);
            acquireClient.close();
            return null;
        }
        acquireClient.close();
    }
    
    private Context getContext() {
        return this.mContext;
    }
    
    public static List<Uri> getPinnedSlices(final Context context) {
        final ArrayList<Uri> list = new ArrayList<Uri>();
        final Iterator<String> iterator = context.getSharedPreferences("slice_data_all_slice_files", 0).getStringSet("slice_data_all_slice_files", (Set)Collections.emptySet()).iterator();
        while (iterator.hasNext()) {
            list.addAll(new CompatPinnedList(context, iterator.next()).getPinnedSlices());
        }
        return list;
    }
    
    public static Set<SliceSpec> getPinnedSpecs(Context acquireClient, final Uri obj) {
        acquireClient = (Context)acquireClient(acquireClient.getContentResolver(), obj);
        if (((ProviderHolder)acquireClient).mProvider == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Unknown URI ");
            sb.append(obj);
            throw new IllegalArgumentException(sb.toString());
        }
        try {
            try {
                final Bundle bundle = new Bundle();
                bundle.putParcelable("slice_uri", (Parcelable)obj);
                final Bundle call = ((ProviderHolder)acquireClient).mProvider.call("get_specs", "supports_versioned_parcelable", bundle);
                if (call != null) {
                    final Set<SliceSpec> specs = getSpecs(call);
                    ((ProviderHolder)acquireClient).close();
                    return specs;
                }
            }
            finally {}
        }
        catch (RemoteException ex) {
            Log.e("SliceProviderCompat", "Unable to get pinned specs", (Throwable)ex);
        }
        ((ProviderHolder)acquireClient).close();
        return null;
        ((ProviderHolder)acquireClient).close();
    }
    
    public static Set<SliceSpec> getSpecs(final Bundle bundle) {
        final ArraySet<SliceSpec> set = new ArraySet<SliceSpec>();
        final ArrayList stringArrayList = bundle.getStringArrayList("specs");
        final ArrayList integerArrayList = bundle.getIntegerArrayList("revs");
        if (stringArrayList != null && integerArrayList != null) {
            for (int i = 0; i < stringArrayList.size(); ++i) {
                set.add(new SliceSpec(stringArrayList.get(i), integerArrayList.get(i)));
            }
        }
        return set;
    }
    
    public static void grantSlicePermission(Context acquireClient, final String s, final String s2, final Uri uri) {
        final ContentResolver contentResolver = acquireClient.getContentResolver();
        try {
            acquireClient = (Context)acquireClient(contentResolver, uri);
            try {
                final Bundle bundle = new Bundle();
                bundle.putParcelable("slice_uri", (Parcelable)uri);
                bundle.putString("provider_pkg", s);
                bundle.putString("pkg", s2);
                ((ProviderHolder)acquireClient).mProvider.call("grant_perms", "supports_versioned_parcelable", bundle);
                if (acquireClient != null) {
                    ((ProviderHolder)acquireClient).close();
                }
            }
            finally {
                if (acquireClient != null) {
                    try {
                        ((ProviderHolder)acquireClient).close();
                    }
                    finally {
                        final Throwable exception;
                        ((Throwable)s).addSuppressed(exception);
                    }
                }
            }
        }
        catch (RemoteException ex) {
            Log.e("SliceProviderCompat", "Unable to get slice descendants", (Throwable)ex);
        }
    }
    
    private Slice handleBindSlice(final Uri uri, final Set<SliceSpec> set, String nameForUid) {
        if (nameForUid == null) {
            nameForUid = this.getContext().getPackageManager().getNameForUid(Binder.getCallingUid());
        }
        if (this.mPermissionManager.checkSlicePermission(uri, Binder.getCallingPid(), Binder.getCallingUid()) != 0) {
            return this.mProvider.createPermissionSlice(uri, nameForUid);
        }
        return this.onBindSliceStrict(uri, set);
    }
    
    private Collection<Uri> handleGetDescendants(final Uri uri) {
        this.mCallback = "onGetSliceDescendants";
        return this.mProvider.onGetSliceDescendants(uri);
    }
    
    private void handleSlicePinned(final Uri uri) {
        this.mCallback = "onSlicePinned";
        this.mHandler.postDelayed(this.mAnr, 2000L);
        try {
            this.mProvider.onSlicePinned(uri);
            this.mProvider.handleSlicePinned(uri);
        }
        finally {
            this.mHandler.removeCallbacks(this.mAnr);
        }
    }
    
    private void handleSliceUnpinned(final Uri uri) {
        this.mCallback = "onSliceUnpinned";
        this.mHandler.postDelayed(this.mAnr, 2000L);
        try {
            this.mProvider.onSliceUnpinned(uri);
            this.mProvider.handleSliceUnpinned(uri);
        }
        finally {
            this.mHandler.removeCallbacks(this.mAnr);
        }
    }
    
    private Slice onBindSliceStrict(final Uri uri, final Set<SliceSpec> specs) {
        final StrictMode$ThreadPolicy threadPolicy = StrictMode.getThreadPolicy();
        this.mCallback = "onBindSlice";
        this.mHandler.postDelayed(this.mAnr, 2000L);
        try {
            StrictMode.setThreadPolicy(new StrictMode$ThreadPolicy$Builder().detectAll().penaltyDeath().build());
            SliceProvider.setSpecs(specs);
            try {
                try {
                    final Slice onBindSlice = this.mProvider.onBindSlice(uri);
                    SliceProvider.setSpecs(null);
                    this.mHandler.removeCallbacks(this.mAnr);
                    return onBindSlice;
                }
                finally {}
            }
            catch (Exception ex) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Slice with URI ");
                sb.append(uri.toString());
                sb.append(" is invalid.");
                Log.wtf("SliceProviderCompat", sb.toString(), (Throwable)ex);
                SliceProvider.setSpecs(null);
                this.mHandler.removeCallbacks(this.mAnr);
                return null;
            }
            SliceProvider.setSpecs(null);
            this.mHandler.removeCallbacks(this.mAnr);
        }
        finally {
            StrictMode.setThreadPolicy(threadPolicy);
        }
    }
    
    private static Slice parseSlice(final Context p0, final Bundle p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: ifnonnull       6
        //     4: aconst_null    
        //     5: areturn        
        //     6: getstatic       androidx/slice/SliceItemHolder.sSerializeLock:Ljava/lang/Object;
        //     9: astore_2       
        //    10: aload_2        
        //    11: monitorenter   
        //    12: new             Landroidx/slice/compat/SliceProviderCompat$2;
        //    15: astore_3       
        //    16: aload_3        
        //    17: aload_0        
        //    18: invokespecial   androidx/slice/compat/SliceProviderCompat$2.<init>:(Landroid/content/Context;)V
        //    21: aload_3        
        //    22: putstatic       androidx/slice/SliceItemHolder.sHandler:Landroidx/slice/SliceItemHolder$HolderHandler;
        //    25: aload_1        
        //    26: ldc             Landroidx/slice/compat/SliceProviderCompat;.class
        //    28: invokevirtual   java/lang/Class.getClassLoader:()Ljava/lang/ClassLoader;
        //    31: invokevirtual   android/os/Bundle.setClassLoader:(Ljava/lang/ClassLoader;)V
        //    34: aload_1        
        //    35: ldc_w           "slice"
        //    38: invokevirtual   android/os/Bundle.getParcelable:(Ljava/lang/String;)Landroid/os/Parcelable;
        //    41: astore_1       
        //    42: aload_1        
        //    43: ifnonnull       54
        //    46: aconst_null    
        //    47: putstatic       androidx/slice/SliceItemHolder.sHandler:Landroidx/slice/SliceItemHolder$HolderHandler;
        //    50: aload_2        
        //    51: monitorexit    
        //    52: aconst_null    
        //    53: areturn        
        //    54: aload_1        
        //    55: instanceof      Landroid/os/Bundle;
        //    58: ifeq            81
        //    61: new             Landroidx/slice/Slice;
        //    64: astore_0       
        //    65: aload_0        
        //    66: aload_1        
        //    67: checkcast       Landroid/os/Bundle;
        //    70: invokespecial   androidx/slice/Slice.<init>:(Landroid/os/Bundle;)V
        //    73: aconst_null    
        //    74: putstatic       androidx/slice/SliceItemHolder.sHandler:Landroidx/slice/SliceItemHolder$HolderHandler;
        //    77: aload_2        
        //    78: monitorexit    
        //    79: aload_0        
        //    80: areturn        
        //    81: aload_1        
        //    82: invokestatic    androidx/versionedparcelable/ParcelUtils.fromParcelable:(Landroid/os/Parcelable;)Landroidx/versionedparcelable/VersionedParcelable;
        //    85: checkcast       Landroidx/slice/Slice;
        //    88: astore_0       
        //    89: aconst_null    
        //    90: putstatic       androidx/slice/SliceItemHolder.sHandler:Landroidx/slice/SliceItemHolder$HolderHandler;
        //    93: aload_2        
        //    94: monitorexit    
        //    95: aload_0        
        //    96: areturn        
        //    97: astore_0       
        //    98: aconst_null    
        //    99: putstatic       androidx/slice/SliceItemHolder.sHandler:Landroidx/slice/SliceItemHolder$HolderHandler;
        //   102: aload_0        
        //   103: athrow         
        //   104: astore_0       
        //   105: aload_2        
        //   106: monitorexit    
        //   107: aload_0        
        //   108: athrow         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  12     42     97     104    Any
        //  46     52     104    109    Any
        //  54     73     97     104    Any
        //  73     79     104    109    Any
        //  81     89     97     104    Any
        //  89     95     104    109    Any
        //  98     104    104    109    Any
        //  105    107    104    109    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0054:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2596)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public static void pinSlice(final Context context, final Uri obj, final Set<SliceSpec> set) {
        final ProviderHolder acquireClient = acquireClient(context.getContentResolver(), obj);
        if (acquireClient.mProvider == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Unknown URI ");
            sb.append(obj);
            throw new IllegalArgumentException(sb.toString());
        }
        try {
            try {
                final Bundle bundle = new Bundle();
                bundle.putParcelable("slice_uri", (Parcelable)obj);
                bundle.putString("pkg", context.getPackageName());
                addSpecs(bundle, set);
                acquireClient.mProvider.call("pin_slice", "supports_versioned_parcelable", bundle);
            }
            finally {}
        }
        catch (RemoteException ex) {
            Log.e("SliceProviderCompat", "Unable to pin slice", (Throwable)ex);
        }
        acquireClient.close();
        return;
        acquireClient.close();
    }
    
    public static void unpinSlice(final Context context, final Uri obj, final Set<SliceSpec> set) {
        final ProviderHolder acquireClient = acquireClient(context.getContentResolver(), obj);
        if (acquireClient.mProvider == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Unknown URI ");
            sb.append(obj);
            throw new IllegalArgumentException(sb.toString());
        }
        try {
            try {
                final Bundle bundle = new Bundle();
                bundle.putParcelable("slice_uri", (Parcelable)obj);
                bundle.putString("pkg", context.getPackageName());
                addSpecs(bundle, set);
                acquireClient.mProvider.call("unpin_slice", "supports_versioned_parcelable", bundle);
            }
            finally {}
        }
        catch (RemoteException ex) {
            Log.e("SliceProviderCompat", "Unable to unpin slice", (Throwable)ex);
        }
        acquireClient.close();
        return;
        acquireClient.close();
    }
    
    public Bundle call(final String s, String anObject, Bundle bundle) {
        final boolean equals = s.equals("bind_slice");
        final Parcelable parcelable = null;
        final Parcelable parcelable2 = null;
        if (equals) {
            final Uri uri = (Uri)bundle.getParcelable("slice_uri");
            this.mProvider.validateIncomingAuthority(uri.getAuthority());
            final Slice handleBindSlice = this.handleBindSlice(uri, getSpecs(bundle), this.getCallingPackage());
            bundle = new Bundle();
            if ("supports_versioned_parcelable".equals(anObject)) {
                anObject = (String)SliceItemHolder.sSerializeLock;
                // monitorenter(anObject)
                Parcelable parcelable3 = parcelable2;
                Label_0095: {
                    if (handleBindSlice == null) {
                        break Label_0095;
                    }
                    try {
                        parcelable3 = ParcelUtils.toParcelable(handleBindSlice);
                        bundle.putParcelable("slice", parcelable3);
                        return bundle;
                    }
                    finally {
                    }
                    // monitorexit(anObject)
                }
            }
            Object bundle2 = parcelable;
            if (handleBindSlice != null) {
                bundle2 = handleBindSlice.toBundle();
            }
            bundle.putParcelable("slice", (Parcelable)bundle2);
            return bundle;
        }
        if (s.equals("map_slice")) {
            this.mProvider.onMapIntentToUri((Intent)bundle.getParcelable("slice_intent"));
            throw null;
        }
        if (s.equals("map_only")) {
            this.mProvider.onMapIntentToUri((Intent)bundle.getParcelable("slice_intent"));
            throw null;
        }
        if (s.equals("pin_slice")) {
            final Uri uri2 = (Uri)bundle.getParcelable("slice_uri");
            this.mProvider.validateIncomingAuthority(uri2.getAuthority());
            if (this.mPinnedList.addPin(uri2, bundle.getString("pkg"), getSpecs(bundle))) {
                this.handleSlicePinned(uri2);
            }
            return null;
        }
        if (s.equals("unpin_slice")) {
            final Uri uri3 = (Uri)bundle.getParcelable("slice_uri");
            this.mProvider.validateIncomingAuthority(uri3.getAuthority());
            anObject = bundle.getString("pkg");
            if (this.mPinnedList.removePin(uri3, anObject)) {
                this.handleSliceUnpinned(uri3);
            }
            return null;
        }
        if (s.equals("get_specs")) {
            final Uri obj = (Uri)bundle.getParcelable("slice_uri");
            this.mProvider.validateIncomingAuthority(obj.getAuthority());
            final Bundle bundle3 = new Bundle();
            final ArraySet<SliceSpec> specs = this.mPinnedList.getSpecs(obj);
            if (specs.size() != 0) {
                addSpecs(bundle3, specs);
                return bundle3;
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(obj);
            sb.append(" is not pinned");
            throw new IllegalStateException(sb.toString());
        }
        else {
            if (s.equals("get_descendants")) {
                final Uri uri4 = (Uri)bundle.getParcelable("slice_uri");
                this.mProvider.validateIncomingAuthority(uri4.getAuthority());
                final Bundle bundle4 = new Bundle();
                bundle4.putParcelableArrayList("slice_descendants", new ArrayList((Collection<? extends E>)this.handleGetDescendants(uri4)));
                return bundle4;
            }
            if (s.equals("check_perms")) {
                final Uri uri5 = (Uri)bundle.getParcelable("slice_uri");
                this.mProvider.validateIncomingAuthority(uri5.getAuthority());
                final int int1 = bundle.getInt("pid");
                final int int2 = bundle.getInt("uid");
                final Bundle bundle5 = new Bundle();
                bundle5.putInt("result", this.mPermissionManager.checkSlicePermission(uri5, int1, int2));
                return bundle5;
            }
            if (s.equals("grant_perms")) {
                final Uri uri6 = (Uri)bundle.getParcelable("slice_uri");
                this.mProvider.validateIncomingAuthority(uri6.getAuthority());
                anObject = bundle.getString("pkg");
                if (Binder.getCallingUid() != Process.myUid()) {
                    throw new SecurityException("Only the owning process can manage slice permissions");
                }
                this.mPermissionManager.grantSlicePermission(uri6, anObject);
            }
            else if (s.equals("revoke_perms")) {
                final Uri uri7 = (Uri)bundle.getParcelable("slice_uri");
                this.mProvider.validateIncomingAuthority(uri7.getAuthority());
                anObject = bundle.getString("pkg");
                if (Binder.getCallingUid() != Process.myUid()) {
                    throw new SecurityException("Only the owning process can manage slice permissions");
                }
                this.mPermissionManager.revokeSlicePermission(uri7, anObject);
            }
            return null;
        }
    }
    
    public String getCallingPackage() {
        return this.mProvider.getCallingPackage();
    }
    
    private static class ProviderHolder implements AutoCloseable
    {
        final ContentProviderClient mProvider;
        
        ProviderHolder(final ContentProviderClient mProvider) {
            this.mProvider = mProvider;
        }
        
        @Override
        public void close() {
            final ContentProviderClient mProvider = this.mProvider;
            if (mProvider == null) {
                return;
            }
            if (Build$VERSION.SDK_INT >= 24) {
                mProvider.close();
            }
            else {
                mProvider.release();
            }
        }
    }
}
