// 
// Decompiled by Procyon v0.5.36
// 

package androidx.lifecycle;

import java.util.Iterator;
import androidx.savedstate.SavedStateRegistryOwner;
import androidx.savedstate.SavedStateRegistry;

final class SavedStateHandleController implements LifecycleEventObserver
{
    private final SavedStateHandle mHandle;
    private boolean mIsAttached;
    private final String mKey;
    
    static void attachHandleIfNeeded(final ViewModel viewModel, final SavedStateRegistry savedStateRegistry, final Lifecycle lifecycle) {
        final SavedStateHandleController savedStateHandleController = viewModel.getTag("androidx.lifecycle.savedstate.vm.tag");
        if (savedStateHandleController != null && !savedStateHandleController.isAttached()) {
            savedStateHandleController.attachToLifecycle(savedStateRegistry, lifecycle);
            tryToAddRecreator(savedStateRegistry, lifecycle);
        }
    }
    
    private static void tryToAddRecreator(final SavedStateRegistry savedStateRegistry, final Lifecycle lifecycle) {
        final Lifecycle.State currentState = lifecycle.getCurrentState();
        if (currentState != Lifecycle.State.INITIALIZED && !currentState.isAtLeast(Lifecycle.State.STARTED)) {
            lifecycle.addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(final LifecycleOwner lifecycleOwner, final Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_START) {
                        lifecycle.removeObserver(this);
                        savedStateRegistry.runOnNextRecreation((Class<? extends SavedStateRegistry.AutoRecreated>)OnRecreation.class);
                    }
                }
            });
        }
        else {
            savedStateRegistry.runOnNextRecreation((Class<? extends SavedStateRegistry.AutoRecreated>)OnRecreation.class);
        }
    }
    
    void attachToLifecycle(final SavedStateRegistry savedStateRegistry, final Lifecycle lifecycle) {
        if (!this.mIsAttached) {
            this.mIsAttached = true;
            lifecycle.addObserver(this);
            savedStateRegistry.registerSavedStateProvider(this.mKey, this.mHandle.savedStateProvider());
            return;
        }
        throw new IllegalStateException("Already attached to lifecycleOwner");
    }
    
    boolean isAttached() {
        return this.mIsAttached;
    }
    
    @Override
    public void onStateChanged(final LifecycleOwner lifecycleOwner, final Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            this.mIsAttached = false;
            lifecycleOwner.getLifecycle().removeObserver(this);
        }
    }
    
    static final class OnRecreation implements AutoRecreated
    {
        @Override
        public void onRecreated(final SavedStateRegistryOwner savedStateRegistryOwner) {
            if (savedStateRegistryOwner instanceof ViewModelStoreOwner) {
                final ViewModelStore viewModelStore = ((ViewModelStoreOwner)savedStateRegistryOwner).getViewModelStore();
                final SavedStateRegistry savedStateRegistry = savedStateRegistryOwner.getSavedStateRegistry();
                final Iterator<String> iterator = viewModelStore.keys().iterator();
                while (iterator.hasNext()) {
                    SavedStateHandleController.attachHandleIfNeeded(viewModelStore.get(iterator.next()), savedStateRegistry, savedStateRegistryOwner.getLifecycle());
                }
                if (!viewModelStore.keys().isEmpty()) {
                    savedStateRegistry.runOnNextRecreation((Class<? extends SavedStateRegistry.AutoRecreated>)OnRecreation.class);
                }
                return;
            }
            throw new IllegalStateException("Internal error: OnRecreation should be registered only on componentsthat implement ViewModelStoreOwner");
        }
    }
}
