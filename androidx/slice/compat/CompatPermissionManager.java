// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.compat;

import java.util.Objects;
import android.text.TextUtils;
import java.util.Iterator;
import androidx.collection.ArraySet;
import android.annotation.SuppressLint;
import android.content.SharedPreferences$Editor;
import android.content.SharedPreferences;
import java.util.Set;
import java.util.Collections;
import java.util.List;
import android.net.Uri;
import android.content.Context;

public class CompatPermissionManager
{
    private final String[] mAutoGrantPermissions;
    private final Context mContext;
    private final int mMyUid;
    private final String mPrefsName;
    
    public CompatPermissionManager(final Context mContext, final String mPrefsName, final int mMyUid, final String[] mAutoGrantPermissions) {
        this.mContext = mContext;
        this.mPrefsName = mPrefsName;
        this.mMyUid = mMyUid;
        this.mAutoGrantPermissions = mAutoGrantPermissions;
    }
    
    private int checkSlicePermission(final Uri uri, final String s) {
        int n;
        if (this.getPermissionState(s, uri.getAuthority()).hasAccess(uri.getPathSegments())) {
            n = 0;
        }
        else {
            n = -1;
        }
        return n;
    }
    
    private PermissionState getPermissionState(final String str, final String str2) {
        final StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("_");
        sb.append(str2);
        final String string = sb.toString();
        final Set stringSet = this.getPrefs().getStringSet(string, (Set)Collections.emptySet());
        final SharedPreferences prefs = this.getPrefs();
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(string);
        sb2.append("_all");
        return new PermissionState(stringSet, string, prefs.getBoolean(sb2.toString(), false));
    }
    
    private SharedPreferences getPrefs() {
        return this.mContext.getSharedPreferences(this.mPrefsName, 0);
    }
    
    private void persist(final PermissionState permissionState) {
        synchronized (this) {
            final SharedPreferences$Editor putStringSet = this.getPrefs().edit().putStringSet(permissionState.getKey(), (Set)permissionState.toPersistable());
            final StringBuilder sb = new StringBuilder();
            sb.append(permissionState.getKey());
            sb.append("_all");
            putStringSet.putBoolean(sb.toString(), permissionState.hasAllPermissions()).apply();
        }
    }
    
    @SuppressLint({ "WrongConstant" })
    public int checkSlicePermission(final Uri uri, int i, int length) {
        if (length == this.mMyUid) {
            return 0;
        }
        final String[] packagesForUid = this.mContext.getPackageManager().getPackagesForUid(length);
        for (int length2 = packagesForUid.length, j = 0; j < length2; ++j) {
            if (this.checkSlicePermission(uri, packagesForUid[j]) == 0) {
                return 0;
            }
        }
        final String[] mAutoGrantPermissions = this.mAutoGrantPermissions;
        for (int length3 = mAutoGrantPermissions.length, k = 0; k < length3; ++k) {
            if (this.mContext.checkPermission(mAutoGrantPermissions[k], i, length) == 0) {
                for (length = packagesForUid.length, i = 0; i < length; ++i) {
                    this.grantSlicePermission(uri, packagesForUid[i]);
                }
                return 0;
            }
        }
        return this.mContext.checkUriPermission(uri, i, length, 2);
    }
    
    public void grantSlicePermission(final Uri uri, final String s) {
        final PermissionState permissionState = this.getPermissionState(s, uri.getAuthority());
        if (permissionState.addPath(uri.getPathSegments())) {
            this.persist(permissionState);
        }
    }
    
    public void revokeSlicePermission(final Uri uri, final String s) {
        final PermissionState permissionState = this.getPermissionState(s, uri.getAuthority());
        if (permissionState.removePath(uri.getPathSegments())) {
            this.persist(permissionState);
        }
    }
    
    public static class PermissionState
    {
        private final String mKey;
        private final ArraySet<String[]> mPaths;
        
        PermissionState(final Set<String> set, final String mKey, final boolean b) {
            final ArraySet<String[]> mPaths = new ArraySet<String[]>();
            this.mPaths = mPaths;
            if (b) {
                mPaths.add(new String[0]);
            }
            else {
                final Iterator<String> iterator = set.iterator();
                while (iterator.hasNext()) {
                    this.mPaths.add(this.decodeSegments(iterator.next()));
                }
            }
            this.mKey = mKey;
        }
        
        private String[] decodeSegments(final String s) {
            final String[] split = s.split("/", -1);
            for (int i = 0; i < split.length; ++i) {
                split[i] = Uri.decode(split[i]);
            }
            return split;
        }
        
        private String encodeSegments(final String[] array) {
            final String[] array2 = new String[array.length];
            for (int i = 0; i < array.length; ++i) {
                array2[i] = Uri.encode(array[i]);
            }
            return TextUtils.join((CharSequence)"/", (Object[])array2);
        }
        
        private boolean isPathPrefixMatch(final String[] array, final String[] array2) {
            final int length = array.length;
            if (array2.length < length) {
                return false;
            }
            for (int i = 0; i < length; ++i) {
                if (!Objects.equals(array2[i], array[i])) {
                    return false;
                }
            }
            return true;
        }
        
        boolean addPath(final List<String> list) {
            final String[] array = list.toArray(new String[list.size()]);
            for (int i = this.mPaths.size() - 1; i >= 0; --i) {
                final String[] array2 = this.mPaths.valueAt(i);
                if (this.isPathPrefixMatch(array2, array)) {
                    return false;
                }
                if (this.isPathPrefixMatch(array, array2)) {
                    this.mPaths.removeAt(i);
                }
            }
            this.mPaths.add(array);
            return true;
        }
        
        public String getKey() {
            return this.mKey;
        }
        
        public boolean hasAccess(final List<String> list) {
            final String[] array = list.toArray(new String[list.size()]);
            final Iterator<String[]> iterator = this.mPaths.iterator();
            while (iterator.hasNext()) {
                if (this.isPathPrefixMatch(iterator.next(), array)) {
                    return true;
                }
            }
            return false;
        }
        
        public boolean hasAllPermissions() {
            return this.hasAccess(Collections.emptyList());
        }
        
        boolean removePath(final List<String> list) {
            final String[] array = list.toArray(new String[list.size()]);
            int i = this.mPaths.size() - 1;
            boolean b = false;
            while (i >= 0) {
                if (this.isPathPrefixMatch(array, this.mPaths.valueAt(i))) {
                    this.mPaths.removeAt(i);
                    b = true;
                }
                --i;
            }
            return b;
        }
        
        public Set<String> toPersistable() {
            final ArraySet<String> set = new ArraySet<String>();
            final Iterator<String[]> iterator = this.mPaths.iterator();
            while (iterator.hasNext()) {
                set.add(this.encodeSegments(iterator.next()));
            }
            return set;
        }
    }
}
