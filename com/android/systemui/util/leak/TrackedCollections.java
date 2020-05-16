// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.leak;

import android.os.SystemClock;
import java.util.Iterator;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.function.Predicate;
import java.io.PrintWriter;
import java.util.Collection;

public class TrackedCollections
{
    private final WeakIdentityHashMap<Collection<?>, CollectionState> mCollections;
    
    public TrackedCollections() {
        this.mCollections = new WeakIdentityHashMap<Collection<?>, CollectionState>();
    }
    
    public void dump(final PrintWriter printWriter, final Predicate<Collection<?>> predicate) {
        synchronized (this) {
            for (final Map.Entry<WeakReference, V> entry : this.mCollections.entrySet()) {
                final Collection<?> collection = entry.getKey().get();
                if (predicate == null || (collection != null && predicate.test(collection))) {
                    ((CollectionState)entry.getValue()).dump(printWriter);
                    printWriter.println();
                }
            }
        }
    }
    
    public void track(final Collection<?> collection, final String tag) {
        synchronized (this) {
            CollectionState collectionState;
            if ((collectionState = this.mCollections.get(collection)) == null) {
                collectionState = new CollectionState();
                collectionState.tag = tag;
                collectionState.startUptime = SystemClock.uptimeMillis();
                this.mCollections.put(collection, collectionState);
            }
            if (collectionState.halfwayCount == -1 && SystemClock.uptimeMillis() - collectionState.startUptime > 1800000L) {
                collectionState.halfwayCount = collectionState.lastCount;
            }
            collectionState.lastCount = collection.size();
            collectionState.lastUptime = SystemClock.uptimeMillis();
        }
    }
    
    private static class CollectionState
    {
        int halfwayCount;
        int lastCount;
        long lastUptime;
        long startUptime;
        String tag;
        
        private CollectionState() {
            this.halfwayCount = -1;
            this.lastCount = -1;
        }
        
        private float ratePerHour(final long n, final int n2, final long n3, final int n4) {
            if (n < n3 && n2 >= 0 && n4 >= 0) {
                return (n4 - (float)n2) / (n3 - n) * 60.0f * 60000.0f;
            }
            return Float.NaN;
        }
        
        void dump(final PrintWriter printWriter) {
            final long uptimeMillis = SystemClock.uptimeMillis();
            final String tag = this.tag;
            final long startUptime = this.startUptime;
            printWriter.format("%s: %.2f (start-30min) / %.2f (30min-now) / %.2f (start-now) (growth rate in #/hour); %d (current size)", tag, this.ratePerHour(startUptime, 0, startUptime + 1800000L, this.halfwayCount), this.ratePerHour(this.startUptime + 1800000L, this.halfwayCount, uptimeMillis, this.lastCount), this.ratePerHour(this.startUptime, 0, uptimeMillis, this.lastCount), this.lastCount);
        }
    }
}
