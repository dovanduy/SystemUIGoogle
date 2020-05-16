// 
// Decompiled by Procyon v0.5.36
// 

package androidx.lifecycle;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.HashMap;

public class ViewModelStore
{
    private final HashMap<String, ViewModel> mMap;
    
    public ViewModelStore() {
        this.mMap = new HashMap<String, ViewModel>();
    }
    
    public final void clear() {
        final Iterator<ViewModel> iterator = this.mMap.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().clear();
        }
        this.mMap.clear();
    }
    
    final ViewModel get(final String key) {
        return this.mMap.get(key);
    }
    
    Set<String> keys() {
        return new HashSet<String>(this.mMap.keySet());
    }
    
    final void put(final String key, final ViewModel value) {
        final ViewModel viewModel = this.mMap.put(key, value);
        if (viewModel != null) {
            viewModel.onCleared();
        }
    }
}
