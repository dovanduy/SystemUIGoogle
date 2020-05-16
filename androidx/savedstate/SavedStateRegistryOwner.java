// 
// Decompiled by Procyon v0.5.36
// 

package androidx.savedstate;

import androidx.lifecycle.LifecycleOwner;

public interface SavedStateRegistryOwner extends LifecycleOwner
{
    SavedStateRegistry getSavedStateRegistry();
}
