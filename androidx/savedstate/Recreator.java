// 
// Decompiled by Procyon v0.5.36
// 

package androidx.savedstate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;
import android.os.Bundle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import java.lang.reflect.Constructor;
import android.annotation.SuppressLint;
import androidx.lifecycle.LifecycleEventObserver;

@SuppressLint({ "RestrictedApi" })
final class Recreator implements LifecycleEventObserver
{
    private final SavedStateRegistryOwner mOwner;
    
    Recreator(final SavedStateRegistryOwner mOwner) {
        this.mOwner = mOwner;
    }
    
    private void reflectiveNew(String str) {
        try {
            Object subclass = Class.forName(str, false, Recreator.class.getClassLoader()).asSubclass(SavedStateRegistry.AutoRecreated.class);
            try {
                final Constructor<SavedStateRegistry.AutoRecreated> declaredConstructor = ((Class<SavedStateRegistry.AutoRecreated>)subclass).getDeclaredConstructor((Class<?>[])new Class[0]);
                declaredConstructor.setAccessible(true);
                try {
                    subclass = declaredConstructor.newInstance(new Object[0]);
                    ((SavedStateRegistry.AutoRecreated)subclass).onRecreated(this.mOwner);
                }
                catch (Exception cause) {
                    subclass = new StringBuilder();
                    ((StringBuilder)subclass).append("Failed to instantiate ");
                    ((StringBuilder)subclass).append(str);
                    throw new RuntimeException(((StringBuilder)subclass).toString(), cause);
                }
            }
            catch (NoSuchMethodException cause2) {
                str = (String)new StringBuilder();
                ((StringBuilder)str).append("Class");
                ((StringBuilder)str).append(((Class)subclass).getSimpleName());
                ((StringBuilder)str).append(" must have default constructor in order to be automatically recreated");
                throw new IllegalStateException(((StringBuilder)str).toString(), cause2);
            }
        }
        catch (ClassNotFoundException cause3) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Class ");
            sb.append(str);
            sb.append(" wasn't found");
            throw new RuntimeException(sb.toString(), cause3);
        }
    }
    
    @Override
    public void onStateChanged(final LifecycleOwner lifecycleOwner, final Lifecycle.Event event) {
        if (event != Lifecycle.Event.ON_CREATE) {
            throw new AssertionError((Object)"Next event must be ON_CREATE");
        }
        lifecycleOwner.getLifecycle().removeObserver(this);
        final Bundle consumeRestoredStateForKey = this.mOwner.getSavedStateRegistry().consumeRestoredStateForKey("androidx.savedstate.Restarter");
        if (consumeRestoredStateForKey == null) {
            return;
        }
        final ArrayList stringArrayList = consumeRestoredStateForKey.getStringArrayList("classes_to_restore");
        if (stringArrayList != null) {
            final Iterator<String> iterator = stringArrayList.iterator();
            while (iterator.hasNext()) {
                this.reflectiveNew(iterator.next());
            }
            return;
        }
        throw new IllegalStateException("Bundle with restored state for the component \"androidx.savedstate.Restarter\" must contain list of strings by the key \"classes_to_restore\"");
    }
    
    static final class SavedStateProvider implements SavedStateRegistry.SavedStateProvider
    {
        final Set<String> mClasses;
        
        SavedStateProvider(final SavedStateRegistry savedStateRegistry) {
            this.mClasses = new HashSet<String>();
            savedStateRegistry.registerSavedStateProvider("androidx.savedstate.Restarter", (SavedStateRegistry.SavedStateProvider)this);
        }
        
        void add(final String s) {
            this.mClasses.add(s);
        }
        
        @Override
        public Bundle saveState() {
            final Bundle bundle = new Bundle();
            bundle.putStringArrayList("classes_to_restore", new ArrayList((Collection<? extends E>)this.mClasses));
            return bundle;
        }
    }
}
