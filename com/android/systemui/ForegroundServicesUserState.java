// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import java.util.Arrays;
import android.util.ArraySet;
import android.util.ArrayMap;

public class ForegroundServicesUserState
{
    private ArrayMap<String, ArraySet<Integer>> mAppOps;
    private ArrayMap<String, ArraySet<String>> mImportantNotifications;
    private String[] mRunning;
    private long mServiceStartTime;
    private ArrayMap<String, ArraySet<String>> mStandardLayoutNotifications;
    
    public ForegroundServicesUserState() {
        this.mRunning = null;
        this.mServiceStartTime = 0L;
        this.mImportantNotifications = (ArrayMap<String, ArraySet<String>>)new ArrayMap(1);
        this.mStandardLayoutNotifications = (ArrayMap<String, ArraySet<String>>)new ArrayMap(1);
        this.mAppOps = (ArrayMap<String, ArraySet<Integer>>)new ArrayMap(1);
    }
    
    public void addImportantNotification(final String s, final String s2) {
        this.addNotification(this.mImportantNotifications, s, s2);
    }
    
    public void addNotification(final ArrayMap<String, ArraySet<String>> arrayMap, final String s, final String s2) {
        if (arrayMap.get((Object)s) == null) {
            arrayMap.put((Object)s, (Object)new ArraySet());
        }
        ((ArraySet)arrayMap.get((Object)s)).add((Object)s2);
    }
    
    public void addOp(final String s, final int i) {
        if (this.mAppOps.get((Object)s) == null) {
            this.mAppOps.put((Object)s, (Object)new ArraySet(3));
        }
        ((ArraySet)this.mAppOps.get((Object)s)).add((Object)i);
    }
    
    public void addStandardLayoutNotification(final String s, final String s2) {
        this.addNotification(this.mStandardLayoutNotifications, s, s2);
    }
    
    public ArraySet<Integer> getFeatures(final String s) {
        return (ArraySet<Integer>)this.mAppOps.get((Object)s);
    }
    
    public String getStandardLayoutKey(final String s) {
        final ArraySet set = (ArraySet)this.mStandardLayoutNotifications.get((Object)s);
        if (set != null && set.size() != 0) {
            return (String)set.valueAt(0);
        }
        return null;
    }
    
    public boolean isDisclosureNeeded() {
        if (this.mRunning != null && System.currentTimeMillis() - this.mServiceStartTime >= 5000L) {
            final String[] mRunning = this.mRunning;
            for (int length = mRunning.length, i = 0; i < length; ++i) {
                final ArraySet set = (ArraySet)this.mImportantNotifications.get((Object)mRunning[i]);
                if (set == null || set.size() == 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean removeImportantNotification(final String s, final String s2) {
        return this.removeNotification(this.mImportantNotifications, s, s2);
    }
    
    public boolean removeNotification(final ArrayMap<String, ArraySet<String>> arrayMap, final String s, final String s2) {
        final ArraySet set = (ArraySet)arrayMap.get((Object)s);
        boolean remove;
        if (set == null) {
            remove = false;
        }
        else {
            remove = set.remove((Object)s2);
            if (set.size() == 0) {
                arrayMap.remove((Object)s);
            }
        }
        return remove;
    }
    
    public boolean removeNotification(final String s, final String s2) {
        return this.removeStandardLayoutNotification(s, s2) | (this.removeImportantNotification(s, s2) | false);
    }
    
    public boolean removeOp(final String s, final int i) {
        final ArraySet set = (ArraySet)this.mAppOps.get((Object)s);
        boolean remove;
        if (set == null) {
            remove = false;
        }
        else {
            remove = set.remove((Object)i);
            if (set.size() == 0) {
                this.mAppOps.remove((Object)s);
            }
        }
        return remove;
    }
    
    public boolean removeStandardLayoutNotification(final String s, final String s2) {
        return this.removeNotification(this.mStandardLayoutNotifications, s, s2);
    }
    
    public void setRunningServices(String[] array, final long mServiceStartTime) {
        if (array != null) {
            array = Arrays.copyOf(array, array.length);
        }
        else {
            array = null;
        }
        this.mRunning = array;
        this.mServiceStartTime = mServiceStartTime;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("UserServices{mRunning=");
        sb.append(Arrays.toString(this.mRunning));
        sb.append(", mServiceStartTime=");
        sb.append(this.mServiceStartTime);
        sb.append(", mImportantNotifications=");
        sb.append(this.mImportantNotifications);
        sb.append(", mStandardLayoutNotifications=");
        sb.append(this.mStandardLayoutNotifications);
        sb.append('}');
        return sb.toString();
    }
}
