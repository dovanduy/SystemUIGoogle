// 
// Decompiled by Procyon v0.5.36
// 

package androidx.savedstate;

import java.util.Iterator;
import java.util.Map;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.Lifecycle;
import android.os.Bundle;
import androidx.arch.core.internal.SafeIterableMap;
import android.annotation.SuppressLint;

@SuppressLint({ "RestrictedApi" })
public final class SavedStateRegistry
{
    boolean mAllowingSavingState;
    private SafeIterableMap<String, SavedStateProvider> mComponents;
    private Recreator.SavedStateProvider mRecreatorProvider;
    private boolean mRestored;
    private Bundle mRestoredState;
    
    SavedStateRegistry() {
        this.mComponents = new SafeIterableMap<String, SavedStateProvider>();
        this.mAllowingSavingState = true;
    }
    
    public Bundle consumeRestoredStateForKey(final String s) {
        if (!this.mRestored) {
            throw new IllegalStateException("You can consumeRestoredStateForKey only after super.onCreate of corresponding component");
        }
        final Bundle mRestoredState = this.mRestoredState;
        if (mRestoredState != null) {
            final Bundle bundle = mRestoredState.getBundle(s);
            this.mRestoredState.remove(s);
            if (this.mRestoredState.isEmpty()) {
                this.mRestoredState = null;
            }
            return bundle;
        }
        return null;
    }
    
    void performRestore(final Lifecycle lifecycle, final Bundle bundle) {
        if (!this.mRestored) {
            if (bundle != null) {
                this.mRestoredState = bundle.getBundle("androidx.lifecycle.BundlableSavedStateRegistry.key");
            }
            lifecycle.addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(final LifecycleOwner lifecycleOwner, final Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_START) {
                        SavedStateRegistry.this.mAllowingSavingState = true;
                    }
                    else if (event == Lifecycle.Event.ON_STOP) {
                        SavedStateRegistry.this.mAllowingSavingState = false;
                    }
                }
            });
            this.mRestored = true;
            return;
        }
        throw new IllegalStateException("SavedStateRegistry was already restored.");
    }
    
    void performSave(final Bundle bundle) {
        final Bundle bundle2 = new Bundle();
        final Bundle mRestoredState = this.mRestoredState;
        if (mRestoredState != null) {
            bundle2.putAll(mRestoredState);
        }
        final SafeIterableMap.IteratorWithAdditions iteratorWithAdditions = this.mComponents.iteratorWithAdditions();
        while (iteratorWithAdditions.hasNext()) {
            final Map.Entry<String, V> entry = ((Iterator<Map.Entry<String, V>>)iteratorWithAdditions).next();
            bundle2.putBundle((String)entry.getKey(), ((SavedStateProvider)entry.getValue()).saveState());
        }
        bundle.putBundle("androidx.lifecycle.BundlableSavedStateRegistry.key", bundle2);
    }
    
    public void registerSavedStateProvider(final String s, final SavedStateProvider savedStateProvider) {
        if (this.mComponents.putIfAbsent(s, savedStateProvider) == null) {
            return;
        }
        throw new IllegalArgumentException("SavedStateProvider with the given key is already registered");
    }
    
    public void runOnNextRecreation(final Class<? extends AutoRecreated> clazz) {
        if (this.mAllowingSavingState) {
            if (this.mRecreatorProvider == null) {
                this.mRecreatorProvider = new Recreator.SavedStateProvider(this);
            }
            try {
                clazz.getDeclaredConstructor((Class<?>[])new Class[0]);
                this.mRecreatorProvider.add(clazz.getName());
                return;
            }
            catch (NoSuchMethodException cause) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Class");
                sb.append(clazz.getSimpleName());
                sb.append(" must have default constructor in order to be automatically recreated");
                throw new IllegalArgumentException(sb.toString(), cause);
            }
        }
        throw new IllegalStateException("Can not perform this action after onSaveInstanceState");
    }
    
    public interface AutoRecreated
    {
        void onRecreated(final SavedStateRegistryOwner p0);
    }
    
    public interface SavedStateProvider
    {
        Bundle saveState();
    }
}
