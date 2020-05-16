// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.compat;

import java.util.ArrayList;
import java.util.List;
import android.os.SystemClock;
import java.util.Collection;
import android.text.TextUtils;
import android.content.SharedPreferences$Editor;
import android.content.SharedPreferences;
import androidx.collection.ArraySet;
import android.net.Uri;
import java.util.Iterator;
import androidx.core.util.ObjectsCompat;
import androidx.slice.SliceSpec;
import java.util.Set;
import android.content.Context;

public class CompatPinnedList
{
    private final Context mContext;
    private final String mPrefsName;
    
    public CompatPinnedList(final Context mContext, final String mPrefsName) {
        this.mContext = mContext;
        this.mPrefsName = mPrefsName;
    }
    
    private static SliceSpec findSpec(final Set<SliceSpec> set, final String s) {
        for (final SliceSpec sliceSpec : set) {
            if (ObjectsCompat.equals(sliceSpec.getType(), s)) {
                return sliceSpec;
            }
        }
        return null;
    }
    
    private Set<String> getPins(final Uri uri) {
        final SharedPreferences prefs = this.getPrefs();
        final StringBuilder sb = new StringBuilder();
        sb.append("pinned_");
        sb.append(uri.toString());
        return (Set<String>)prefs.getStringSet(sb.toString(), (Set)new ArraySet());
    }
    
    private SharedPreferences getPrefs() {
        final SharedPreferences sharedPreferences = this.mContext.getSharedPreferences(this.mPrefsName, 0);
        final long long1 = sharedPreferences.getLong("last_boot", 0L);
        final long bootTime = this.getBootTime();
        if (Math.abs(long1 - bootTime) > 2000L) {
            sharedPreferences.edit().clear().putLong("last_boot", bootTime).apply();
        }
        return sharedPreferences;
    }
    
    private static ArraySet<SliceSpec> mergeSpecs(final ArraySet<SliceSpec> set, final Set<SliceSpec> set2) {
        int n3;
        for (int i = 0; i < set.size(); i = n3 + 1) {
            final SliceSpec sliceSpec = set.valueAt(i);
            final SliceSpec spec = findSpec(set2, sliceSpec.getType());
            int n2;
            if (spec == null) {
                final int n = i - 1;
                set.removeAt(i);
                n2 = n;
            }
            else {
                n3 = i;
                if (spec.getRevision() >= sliceSpec.getRevision()) {
                    continue;
                }
                final int n4 = i - 1;
                set.removeAt(i);
                set.add(spec);
                n2 = n4;
            }
            n3 = n2;
        }
        return set;
    }
    
    private void setPins(final Uri uri, final Set<String> set) {
        final SharedPreferences$Editor edit = this.getPrefs().edit();
        final StringBuilder sb = new StringBuilder();
        sb.append("pinned_");
        sb.append(uri.toString());
        edit.putStringSet(sb.toString(), (Set)set).apply();
    }
    
    private void setSpecs(final Uri uri, final ArraySet<SliceSpec> set) {
        final String[] array = new String[set.size()];
        final String[] array2 = new String[set.size()];
        for (int i = 0; i < set.size(); ++i) {
            array[i] = set.valueAt(i).getType();
            array2[i] = String.valueOf(set.valueAt(i).getRevision());
        }
        final SharedPreferences$Editor edit = this.getPrefs().edit();
        final StringBuilder sb = new StringBuilder();
        sb.append("spec_names_");
        sb.append(uri.toString());
        final SharedPreferences$Editor putString = edit.putString(sb.toString(), TextUtils.join((CharSequence)",", (Object[])array));
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("spec_revs_");
        sb2.append(uri.toString());
        putString.putString(sb2.toString(), TextUtils.join((CharSequence)",", (Object[])array2)).apply();
    }
    
    public boolean addPin(final Uri uri, final String s, final Set<SliceSpec> set) {
        synchronized (this) {
            final Set<String> pins = this.getPins(uri);
            final boolean empty = pins.isEmpty();
            pins.add(s);
            this.setPins(uri, pins);
            if (empty) {
                this.setSpecs(uri, new ArraySet<SliceSpec>(set));
            }
            else {
                final ArraySet<SliceSpec> specs = this.getSpecs(uri);
                mergeSpecs(specs, set);
                this.setSpecs(uri, specs);
            }
            return empty;
        }
    }
    
    protected long getBootTime() {
        return System.currentTimeMillis() - SystemClock.elapsedRealtime();
    }
    
    public List<Uri> getPinnedSlices() {
        final ArrayList<Uri> list = new ArrayList<Uri>();
        for (final String s : this.getPrefs().getAll().keySet()) {
            if (s.startsWith("pinned_")) {
                final Uri parse = Uri.parse(s.substring(7));
                if (this.getPins(parse).isEmpty()) {
                    continue;
                }
                list.add(parse);
            }
        }
        return list;
    }
    
    public ArraySet<SliceSpec> getSpecs(final Uri uri) {
        synchronized (this) {
            final ArraySet<SliceSpec> set = new ArraySet<SliceSpec>();
            final SharedPreferences prefs = this.getPrefs();
            final StringBuilder sb = new StringBuilder();
            sb.append("spec_names_");
            sb.append(uri.toString());
            final String string = prefs.getString(sb.toString(), (String)null);
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("spec_revs_");
            sb2.append(uri.toString());
            final String string2 = prefs.getString(sb2.toString(), (String)null);
            if (TextUtils.isEmpty((CharSequence)string) || TextUtils.isEmpty((CharSequence)string2)) {
                return new ArraySet<SliceSpec>();
            }
            final String[] split = string.split(",", -1);
            final String[] split2 = string2.split(",", -1);
            if (split.length != split2.length) {
                return new ArraySet<SliceSpec>();
            }
            for (int i = 0; i < split.length; ++i) {
                set.add(new SliceSpec(split[i], Integer.parseInt(split2[i])));
            }
            return set;
        }
    }
    
    public boolean removePin(final Uri uri, final String s) {
        synchronized (this) {
            final Set<String> pins = this.getPins(uri);
            final boolean empty = pins.isEmpty();
            boolean b = false;
            if (!empty && pins.contains(s)) {
                pins.remove(s);
                this.setPins(uri, pins);
                this.setSpecs(uri, new ArraySet<SliceSpec>());
                if (pins.size() == 0) {
                    b = true;
                }
                return b;
            }
            return false;
        }
    }
}
