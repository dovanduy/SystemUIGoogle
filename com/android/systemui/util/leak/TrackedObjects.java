// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.leak;

import java.util.Collection;
import java.util.WeakHashMap;

public class TrackedObjects
{
    private final WeakHashMap<Class<?>, TrackedClass<?>> mTrackedClasses;
    private final TrackedCollections mTrackedCollections;
    
    public TrackedObjects(final TrackedCollections mTrackedCollections) {
        this.mTrackedClasses = new WeakHashMap<Class<?>, TrackedClass<?>>();
        this.mTrackedCollections = mTrackedCollections;
    }
    
    public static boolean isTrackedObject(final Collection<?> collection) {
        return collection instanceof TrackedClass;
    }
    
    public <T> void track(final T t) {
        synchronized (this) {
            final Class<?> class1 = t.getClass();
            TrackedClass<?> value;
            if ((value = this.mTrackedClasses.get(class1)) == null) {
                value = new TrackedClass<Object>();
                this.mTrackedClasses.put(class1, value);
            }
            value.track(t);
            this.mTrackedCollections.track(value, class1.getName());
        }
    }
    
    private static class TrackedClass<T> extends AbstractCollection<T>
    {
        final WeakIdentityHashMap<T, Void> instances;
        
        private TrackedClass() {
            this.instances = new WeakIdentityHashMap<T, Void>();
        }
        
        @Override
        public boolean isEmpty() {
            return this.instances.isEmpty();
        }
        
        @Override
        public int size() {
            return this.instances.size();
        }
        
        void track(final T t) {
            this.instances.put(t, null);
        }
    }
}
