// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.leak;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Map;
import android.util.ArrayMap;
import java.io.PrintWriter;
import java.util.Iterator;
import android.os.SystemClock;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.HashSet;

public class TrackedGarbage
{
    private final HashSet<LeakReference> mGarbage;
    private final ReferenceQueue<Object> mRefQueue;
    private final TrackedCollections mTrackedCollections;
    
    public TrackedGarbage(final TrackedCollections mTrackedCollections) {
        this.mGarbage = new HashSet<LeakReference>();
        this.mRefQueue = new ReferenceQueue<Object>();
        this.mTrackedCollections = mTrackedCollections;
    }
    
    private void cleanUp() {
        while (true) {
            final Reference<?> poll = this.mRefQueue.poll();
            if (poll == null) {
                break;
            }
            this.mGarbage.remove(poll);
        }
    }
    
    private boolean isOld(final long n, final long n2) {
        return n + 60000L < n2;
    }
    
    public int countOldGarbage() {
        synchronized (this) {
            this.cleanUp();
            final long uptimeMillis = SystemClock.uptimeMillis();
            int n = 0;
            final Iterator<LeakReference> iterator = this.mGarbage.iterator();
            while (iterator.hasNext()) {
                if (this.isOld(iterator.next().createdUptimeMillis, uptimeMillis)) {
                    ++n;
                }
            }
            return n;
        }
    }
    
    public void dump(final PrintWriter printWriter) {
        synchronized (this) {
            this.cleanUp();
            final long uptimeMillis = SystemClock.uptimeMillis();
            final ArrayMap arrayMap = new ArrayMap();
            final ArrayMap arrayMap2 = new ArrayMap();
            for (final LeakReference leakReference : this.mGarbage) {
                arrayMap.put((Object)leakReference.clazz, (Object)((int)arrayMap.getOrDefault((Object)leakReference.clazz, (Object)0) + 1));
                if (this.isOld(leakReference.createdUptimeMillis, uptimeMillis)) {
                    arrayMap2.put((Object)leakReference.clazz, (Object)((int)arrayMap2.getOrDefault((Object)leakReference.clazz, (Object)0) + 1));
                }
            }
            for (final Map.Entry<Object, V> entry : arrayMap.entrySet()) {
                printWriter.print(entry.getKey().getName());
                printWriter.print(": ");
                printWriter.print(entry.getValue());
                printWriter.print(" total, ");
                printWriter.print(arrayMap2.getOrDefault(entry.getKey(), (Object)0));
                printWriter.print(" old");
                printWriter.println();
            }
        }
    }
    
    public void track(final Object o) {
        synchronized (this) {
            this.cleanUp();
            this.mGarbage.add(new LeakReference(o, this.mRefQueue));
            this.mTrackedCollections.track(this.mGarbage, "Garbage");
        }
    }
    
    private static class LeakReference extends WeakReference<Object>
    {
        private final Class<?> clazz;
        private final long createdUptimeMillis;
        
        LeakReference(final Object referent, final ReferenceQueue<Object> q) {
            super(referent, q);
            this.clazz = referent.getClass();
            this.createdUptimeMillis = SystemClock.uptimeMillis();
        }
    }
}
