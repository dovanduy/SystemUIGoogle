// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.text.TextUtils;
import java.util.Arrays;
import java.util.Collections;
import android.provider.Settings$Secure;
import com.android.systemui.Prefs;
import java.util.Collection;
import android.os.Handler;
import com.android.internal.annotations.VisibleForTesting;
import android.database.ContentObserver;
import android.content.Context;
import android.util.ArraySet;

public class AutoAddTracker
{
    private static final String[][] CONVERT_PREFS;
    private final ArraySet<String> mAutoAdded;
    private final Context mContext;
    @VisibleForTesting
    protected final ContentObserver mObserver;
    
    static {
        CONVERT_PREFS = new String[][] { { "QsHotspotAdded", "hotspot" }, { "QsDataSaverAdded", "saver" }, { "QsInvertColorsAdded", "inversion" }, { "QsWorkAdded", "work" }, { "QsNightDisplayAdded", "night" } };
    }
    
    public AutoAddTracker(final Context mContext) {
        this.mObserver = new ContentObserver(new Handler()) {
            public void onChange(final boolean b) {
                AutoAddTracker.this.mAutoAdded.addAll(AutoAddTracker.this.getAdded());
            }
        };
        this.mContext = mContext;
        this.mAutoAdded = (ArraySet<String>)new ArraySet((Collection)this.getAdded());
        for (final String[] array : AutoAddTracker.CONVERT_PREFS) {
            if (Prefs.getBoolean(mContext, array[0], false)) {
                this.setTileAdded(array[1]);
                Prefs.remove(mContext, array[0]);
            }
        }
        this.mContext.getContentResolver().registerContentObserver(Settings$Secure.getUriFor("qs_auto_tiles"), false, this.mObserver);
    }
    
    private Collection<String> getAdded() {
        final String string = Settings$Secure.getString(this.mContext.getContentResolver(), "qs_auto_tiles");
        if (string == null) {
            return (Collection<String>)Collections.emptyList();
        }
        return Arrays.asList(string.split(","));
    }
    
    private void saveTiles() {
        Settings$Secure.putString(this.mContext.getContentResolver(), "qs_auto_tiles", TextUtils.join((CharSequence)",", (Iterable)this.mAutoAdded));
    }
    
    public boolean isAdded(final String s) {
        return this.mAutoAdded.contains((Object)s);
    }
    
    public void setTileAdded(final String s) {
        if (this.mAutoAdded.add((Object)s)) {
            this.saveTiles();
        }
    }
    
    public void setTileRemoved(final String s) {
        if (this.mAutoAdded.remove((Object)s)) {
            this.saveTiles();
        }
    }
}
