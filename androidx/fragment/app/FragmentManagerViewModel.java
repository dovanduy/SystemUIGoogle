// 
// Decompiled by Procyon v0.5.36
// 

package androidx.fragment.app;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import android.util.Log;
import androidx.lifecycle.ViewModelStore;
import java.util.HashMap;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModel;

final class FragmentManagerViewModel extends ViewModel
{
    private static final ViewModelProvider.Factory FACTORY;
    private final HashMap<String, FragmentManagerViewModel> mChildNonConfigs;
    private boolean mHasBeenCleared;
    private boolean mHasSavedSnapshot;
    private boolean mIsStateSaved;
    private final HashMap<String, Fragment> mRetainedFragments;
    private final boolean mStateAutomaticallySaved;
    private final HashMap<String, ViewModelStore> mViewModelStores;
    
    static {
        FACTORY = new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(final Class<T> clazz) {
                return (T)new FragmentManagerViewModel(true);
            }
        };
    }
    
    FragmentManagerViewModel(final boolean mStateAutomaticallySaved) {
        this.mRetainedFragments = new HashMap<String, Fragment>();
        this.mChildNonConfigs = new HashMap<String, FragmentManagerViewModel>();
        this.mViewModelStores = new HashMap<String, ViewModelStore>();
        this.mHasBeenCleared = false;
        this.mHasSavedSnapshot = false;
        this.mIsStateSaved = false;
        this.mStateAutomaticallySaved = mStateAutomaticallySaved;
    }
    
    static FragmentManagerViewModel getInstance(final ViewModelStore viewModelStore) {
        return new ViewModelProvider(viewModelStore, FragmentManagerViewModel.FACTORY).get(FragmentManagerViewModel.class);
    }
    
    void addRetainedFragment(final Fragment fragment) {
        if (this.mIsStateSaved) {
            if (FragmentManager.isLoggingEnabled(2)) {
                Log.v("FragmentManager", "Ignoring addRetainedFragment as the state is already saved");
            }
            return;
        }
        if (this.mRetainedFragments.containsKey(fragment.mWho)) {
            return;
        }
        this.mRetainedFragments.put(fragment.mWho, fragment);
        if (FragmentManager.isLoggingEnabled(2)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Updating retained Fragments: Added ");
            sb.append(fragment);
            Log.v("FragmentManager", sb.toString());
        }
    }
    
    void clearNonConfigState(final Fragment obj) {
        if (FragmentManager.isLoggingEnabled(3)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Clearing non-config state for ");
            sb.append(obj);
            Log.d("FragmentManager", sb.toString());
        }
        final FragmentManagerViewModel fragmentManagerViewModel = this.mChildNonConfigs.get(obj.mWho);
        if (fragmentManagerViewModel != null) {
            fragmentManagerViewModel.onCleared();
            this.mChildNonConfigs.remove(obj.mWho);
        }
        final ViewModelStore viewModelStore = this.mViewModelStores.get(obj.mWho);
        if (viewModelStore != null) {
            viewModelStore.clear();
            this.mViewModelStores.remove(obj.mWho);
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = true;
        if (this == o) {
            return true;
        }
        if (o != null && FragmentManagerViewModel.class == o.getClass()) {
            final FragmentManagerViewModel fragmentManagerViewModel = (FragmentManagerViewModel)o;
            if (!this.mRetainedFragments.equals(fragmentManagerViewModel.mRetainedFragments) || !this.mChildNonConfigs.equals(fragmentManagerViewModel.mChildNonConfigs) || !this.mViewModelStores.equals(fragmentManagerViewModel.mViewModelStores)) {
                b = false;
            }
            return b;
        }
        return false;
    }
    
    Fragment findRetainedFragmentByWho(final String key) {
        return this.mRetainedFragments.get(key);
    }
    
    FragmentManagerViewModel getChildNonConfig(final Fragment fragment) {
        FragmentManagerViewModel value;
        if ((value = this.mChildNonConfigs.get(fragment.mWho)) == null) {
            value = new FragmentManagerViewModel(this.mStateAutomaticallySaved);
            this.mChildNonConfigs.put(fragment.mWho, value);
        }
        return value;
    }
    
    Collection<Fragment> getRetainedFragments() {
        return new ArrayList<Fragment>(this.mRetainedFragments.values());
    }
    
    ViewModelStore getViewModelStore(final Fragment fragment) {
        ViewModelStore value;
        if ((value = this.mViewModelStores.get(fragment.mWho)) == null) {
            value = new ViewModelStore();
            this.mViewModelStores.put(fragment.mWho, value);
        }
        return value;
    }
    
    @Override
    public int hashCode() {
        return (this.mRetainedFragments.hashCode() * 31 + this.mChildNonConfigs.hashCode()) * 31 + this.mViewModelStores.hashCode();
    }
    
    boolean isCleared() {
        return this.mHasBeenCleared;
    }
    
    @Override
    protected void onCleared() {
        if (FragmentManager.isLoggingEnabled(3)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("onCleared called for ");
            sb.append(this);
            Log.d("FragmentManager", sb.toString());
        }
        this.mHasBeenCleared = true;
    }
    
    void removeRetainedFragment(final Fragment obj) {
        if (this.mIsStateSaved) {
            if (FragmentManager.isLoggingEnabled(2)) {
                Log.v("FragmentManager", "Ignoring removeRetainedFragment as the state is already saved");
            }
            return;
        }
        if (this.mRetainedFragments.remove(obj.mWho) != null && FragmentManager.isLoggingEnabled(2)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Updating retained Fragments: Removed ");
            sb.append(obj);
            Log.v("FragmentManager", sb.toString());
        }
    }
    
    void setIsStateSaved(final boolean mIsStateSaved) {
        this.mIsStateSaved = mIsStateSaved;
    }
    
    boolean shouldDestroy(final Fragment fragment) {
        if (!this.mRetainedFragments.containsKey(fragment.mWho)) {
            return true;
        }
        if (this.mStateAutomaticallySaved) {
            return this.mHasBeenCleared;
        }
        return this.mHasSavedSnapshot ^ true;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FragmentManagerViewModel{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append("} Fragments (");
        final Iterator<Fragment> iterator = this.mRetainedFragments.values().iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(") Child Non Config (");
        final Iterator<String> iterator2 = this.mChildNonConfigs.keySet().iterator();
        while (iterator2.hasNext()) {
            sb.append(iterator2.next());
            if (iterator2.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(") ViewModelStores (");
        final Iterator<String> iterator3 = this.mViewModelStores.keySet().iterator();
        while (iterator3.hasNext()) {
            sb.append(iterator3.next());
            if (iterator3.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(')');
        return sb.toString();
    }
}
