// 
// Decompiled by Procyon v0.5.36
// 

package androidx.savedstate;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.Lifecycle;
import android.os.Bundle;

public final class SavedStateRegistryController
{
    private final SavedStateRegistryOwner mOwner;
    private final SavedStateRegistry mRegistry;
    
    private SavedStateRegistryController(final SavedStateRegistryOwner mOwner) {
        this.mOwner = mOwner;
        this.mRegistry = new SavedStateRegistry();
    }
    
    public static SavedStateRegistryController create(final SavedStateRegistryOwner savedStateRegistryOwner) {
        return new SavedStateRegistryController(savedStateRegistryOwner);
    }
    
    public SavedStateRegistry getSavedStateRegistry() {
        return this.mRegistry;
    }
    
    public void performRestore(final Bundle bundle) {
        final Lifecycle lifecycle = this.mOwner.getLifecycle();
        if (lifecycle.getCurrentState() == Lifecycle.State.INITIALIZED) {
            lifecycle.addObserver(new Recreator(this.mOwner));
            this.mRegistry.performRestore(lifecycle, bundle);
            return;
        }
        throw new IllegalStateException("Restarter must be created only during owner's initialization stage");
    }
    
    public void performSave(final Bundle bundle) {
        this.mRegistry.performSave(bundle);
    }
}
