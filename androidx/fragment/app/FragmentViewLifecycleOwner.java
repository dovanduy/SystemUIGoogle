// 
// Decompiled by Procyon v0.5.36
// 

package androidx.fragment.app;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LifecycleOwner;

class FragmentViewLifecycleOwner implements LifecycleOwner
{
    private LifecycleRegistry mLifecycleRegistry;
    
    FragmentViewLifecycleOwner() {
        this.mLifecycleRegistry = null;
    }
    
    @Override
    public Lifecycle getLifecycle() {
        this.initialize();
        return this.mLifecycleRegistry;
    }
    
    void handleLifecycleEvent(final Lifecycle.Event event) {
        this.mLifecycleRegistry.handleLifecycleEvent(event);
    }
    
    void initialize() {
        if (this.mLifecycleRegistry == null) {
            this.mLifecycleRegistry = new LifecycleRegistry(this);
        }
    }
    
    boolean isInitialized() {
        return this.mLifecycleRegistry != null;
    }
}
