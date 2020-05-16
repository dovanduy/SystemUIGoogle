// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.keyguard;

import java.util.function.Consumer;
import java.util.ArrayList;

public class Lifecycle<T>
{
    private ArrayList<T> mObservers;
    
    public Lifecycle() {
        this.mObservers = new ArrayList<T>();
    }
    
    public void addObserver(final T e) {
        this.mObservers.add(e);
    }
    
    public void dispatch(final Consumer<T> consumer) {
        for (int i = 0; i < this.mObservers.size(); ++i) {
            consumer.accept(this.mObservers.get(i));
        }
    }
    
    public void removeObserver(final T o) {
        this.mObservers.remove(o);
    }
}
