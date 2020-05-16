// 
// Decompiled by Procyon v0.5.36
// 

package androidx.fragment.app;

import android.util.Log;
import java.util.List;
import android.view.View;
import android.view.ViewGroup;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;

class FragmentStore
{
    private final HashMap<String, FragmentStateManager> mActive;
    private final ArrayList<Fragment> mAdded;
    private FragmentManagerViewModel mNonConfig;
    
    FragmentStore() {
        this.mAdded = new ArrayList<Fragment>();
        this.mActive = new HashMap<String, FragmentStateManager>();
    }
    
    void addFragment(final Fragment obj) {
        if (!this.mAdded.contains(obj)) {
            synchronized (this.mAdded) {
                this.mAdded.add(obj);
                // monitorexit(this.mAdded)
                obj.mAdded = true;
                return;
            }
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Fragment already added: ");
        sb.append(obj);
        throw new IllegalStateException(sb.toString());
    }
    
    void burpActive() {
        this.mActive.values().removeAll(Collections.singleton((Object)null));
    }
    
    boolean containsActiveFragment(final String key) {
        return this.mActive.get(key) != null;
    }
    
    void dispatchStateChange(final int fragmentManagerState) {
        for (final FragmentStateManager fragmentStateManager : this.mActive.values()) {
            if (fragmentStateManager != null) {
                fragmentStateManager.setFragmentManagerState(fragmentManagerState);
            }
        }
    }
    
    void dump(final String s, final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        final StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb.append("    ");
        final String string = sb.toString();
        if (!this.mActive.isEmpty()) {
            printWriter.print(s);
            printWriter.print("Active Fragments:");
            for (final FragmentStateManager fragmentStateManager : this.mActive.values()) {
                printWriter.print(s);
                if (fragmentStateManager != null) {
                    final Fragment fragment = fragmentStateManager.getFragment();
                    printWriter.println(fragment);
                    fragment.dump(string, fileDescriptor, printWriter, array);
                }
                else {
                    printWriter.println("null");
                }
            }
        }
        final int size = this.mAdded.size();
        if (size > 0) {
            printWriter.print(s);
            printWriter.println("Added Fragments:");
            for (int i = 0; i < size; ++i) {
                final Fragment fragment2 = this.mAdded.get(i);
                printWriter.print(s);
                printWriter.print("  #");
                printWriter.print(i);
                printWriter.print(": ");
                printWriter.println(fragment2.toString());
            }
        }
    }
    
    Fragment findActiveFragment(final String key) {
        final FragmentStateManager fragmentStateManager = this.mActive.get(key);
        if (fragmentStateManager != null) {
            return fragmentStateManager.getFragment();
        }
        return null;
    }
    
    Fragment findFragmentById(final int n) {
        for (int i = this.mAdded.size() - 1; i >= 0; --i) {
            final Fragment fragment = this.mAdded.get(i);
            if (fragment != null && fragment.mFragmentId == n) {
                return fragment;
            }
        }
        for (final FragmentStateManager fragmentStateManager : this.mActive.values()) {
            if (fragmentStateManager != null) {
                final Fragment fragment2 = fragmentStateManager.getFragment();
                if (fragment2.mFragmentId == n) {
                    return fragment2;
                }
                continue;
            }
        }
        return null;
    }
    
    Fragment findFragmentByTag(final String s) {
        if (s != null) {
            for (int i = this.mAdded.size() - 1; i >= 0; --i) {
                final Fragment fragment = this.mAdded.get(i);
                if (fragment != null && s.equals(fragment.mTag)) {
                    return fragment;
                }
            }
        }
        if (s != null) {
            for (final FragmentStateManager fragmentStateManager : this.mActive.values()) {
                if (fragmentStateManager != null) {
                    final Fragment fragment2 = fragmentStateManager.getFragment();
                    if (s.equals(fragment2.mTag)) {
                        return fragment2;
                    }
                    continue;
                }
            }
        }
        return null;
    }
    
    Fragment findFragmentByWho(final String s) {
        for (final FragmentStateManager fragmentStateManager : this.mActive.values()) {
            if (fragmentStateManager != null) {
                final Fragment fragmentByWho = fragmentStateManager.getFragment().findFragmentByWho(s);
                if (fragmentByWho != null) {
                    return fragmentByWho;
                }
                continue;
            }
        }
        return null;
    }
    
    Fragment findFragmentUnder(Fragment o) {
        final ViewGroup mContainer = o.mContainer;
        final View mView = o.mView;
        if (mContainer != null) {
            if (mView != null) {
                for (int i = this.mAdded.indexOf(o) - 1; i >= 0; --i) {
                    o = this.mAdded.get(i);
                    if (o.mContainer == mContainer && o.mView != null) {
                        return o;
                    }
                }
            }
        }
        return null;
    }
    
    List<FragmentStateManager> getActiveFragmentStateManagers() {
        final ArrayList<FragmentStateManager> list = new ArrayList<FragmentStateManager>();
        for (final FragmentStateManager e : this.mActive.values()) {
            if (e != null) {
                list.add(e);
            }
        }
        return list;
    }
    
    List<Fragment> getActiveFragments() {
        final ArrayList<Fragment> list = new ArrayList<Fragment>();
        for (final FragmentStateManager fragmentStateManager : this.mActive.values()) {
            if (fragmentStateManager != null) {
                list.add(fragmentStateManager.getFragment());
            }
            else {
                list.add(null);
            }
        }
        return list;
    }
    
    FragmentStateManager getFragmentStateManager(final String key) {
        return this.mActive.get(key);
    }
    
    List<Fragment> getFragments() {
        if (this.mAdded.isEmpty()) {
            return Collections.emptyList();
        }
        synchronized (this.mAdded) {
            return new ArrayList<Fragment>(this.mAdded);
        }
    }
    
    FragmentManagerViewModel getNonConfig() {
        return this.mNonConfig;
    }
    
    void makeActive(final FragmentStateManager value) {
        final Fragment fragment = value.getFragment();
        if (this.containsActiveFragment(fragment.mWho)) {
            return;
        }
        this.mActive.put(fragment.mWho, value);
        if (fragment.mRetainInstanceChangedWhileDetached) {
            if (fragment.mRetainInstance) {
                this.mNonConfig.addRetainedFragment(fragment);
            }
            else {
                this.mNonConfig.removeRetainedFragment(fragment);
            }
            fragment.mRetainInstanceChangedWhileDetached = false;
        }
        if (FragmentManager.isLoggingEnabled(2)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Added fragment to active set ");
            sb.append(fragment);
            Log.v("FragmentManager", sb.toString());
        }
    }
    
    void makeInactive(final FragmentStateManager fragmentStateManager) {
        final Fragment fragment = fragmentStateManager.getFragment();
        if (fragment.mRetainInstance) {
            this.mNonConfig.removeRetainedFragment(fragment);
        }
        if (this.mActive.put(fragment.mWho, null) == null) {
            return;
        }
        if (FragmentManager.isLoggingEnabled(2)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Removed fragment from active set ");
            sb.append(fragment);
            Log.v("FragmentManager", sb.toString());
        }
    }
    
    void moveToExpectedState() {
        final Iterator<Fragment> iterator = this.mAdded.iterator();
        while (iterator.hasNext()) {
            final FragmentStateManager fragmentStateManager = this.mActive.get(iterator.next().mWho);
            if (fragmentStateManager != null) {
                fragmentStateManager.moveToExpectedState();
            }
        }
        for (final FragmentStateManager fragmentStateManager2 : this.mActive.values()) {
            if (fragmentStateManager2 != null) {
                fragmentStateManager2.moveToExpectedState();
                final Fragment fragment = fragmentStateManager2.getFragment();
                if (!fragment.mRemoving || fragment.isInBackStack()) {
                    continue;
                }
                this.makeInactive(fragmentStateManager2);
            }
        }
    }
    
    void removeFragment(final Fragment o) {
        synchronized (this.mAdded) {
            this.mAdded.remove(o);
            // monitorexit(this.mAdded)
            o.mAdded = false;
        }
    }
    
    void resetActiveFragments() {
        this.mActive.clear();
    }
    
    void restoreAddedFragments(final List<String> list) {
        this.mAdded.clear();
        if (list != null) {
            for (final String s : list) {
                final Fragment activeFragment = this.findActiveFragment(s);
                if (activeFragment == null) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("No instantiated fragment for (");
                    sb.append(s);
                    sb.append(")");
                    throw new IllegalStateException(sb.toString());
                }
                if (FragmentManager.isLoggingEnabled(2)) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("restoreSaveState: added (");
                    sb2.append(s);
                    sb2.append("): ");
                    sb2.append(activeFragment);
                    Log.v("FragmentManager", sb2.toString());
                }
                this.addFragment(activeFragment);
            }
        }
    }
    
    ArrayList<FragmentState> saveActiveFragments() {
        final ArrayList<FragmentState> list = new ArrayList<FragmentState>(this.mActive.size());
        for (final FragmentStateManager fragmentStateManager : this.mActive.values()) {
            if (fragmentStateManager != null) {
                final Fragment fragment = fragmentStateManager.getFragment();
                final FragmentState saveState = fragmentStateManager.saveState();
                list.add(saveState);
                if (!FragmentManager.isLoggingEnabled(2)) {
                    continue;
                }
                final StringBuilder sb = new StringBuilder();
                sb.append("Saved state of ");
                sb.append(fragment);
                sb.append(": ");
                sb.append(saveState.mSavedFragmentState);
                Log.v("FragmentManager", sb.toString());
            }
        }
        return list;
    }
    
    ArrayList<String> saveAddedFragments() {
        synchronized (this.mAdded) {
            if (this.mAdded.isEmpty()) {
                return null;
            }
            final ArrayList<String> list = new ArrayList<String>(this.mAdded.size());
            for (final Fragment obj : this.mAdded) {
                list.add(obj.mWho);
                if (FragmentManager.isLoggingEnabled(2)) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("saveAllState: adding fragment (");
                    sb.append(obj.mWho);
                    sb.append("): ");
                    sb.append(obj);
                    Log.v("FragmentManager", sb.toString());
                }
            }
            return list;
        }
    }
    
    void setNonConfig(final FragmentManagerViewModel mNonConfig) {
        this.mNonConfig = mNonConfig;
    }
}
