// 
// Decompiled by Procyon v0.5.36
// 

package androidx.fragment.app;

import android.view.View;
import android.content.Context;
import java.util.Iterator;
import android.os.Bundle;
import java.util.concurrent.CopyOnWriteArrayList;

class FragmentLifecycleCallbacksDispatcher
{
    private final FragmentManager mFragmentManager;
    private final CopyOnWriteArrayList<FragmentLifecycleCallbacksHolder> mLifecycleCallbacks;
    
    FragmentLifecycleCallbacksDispatcher(final FragmentManager mFragmentManager) {
        this.mLifecycleCallbacks = new CopyOnWriteArrayList<FragmentLifecycleCallbacksHolder>();
        this.mFragmentManager = mFragmentManager;
    }
    
    void dispatchOnFragmentActivityCreated(final Fragment fragment, final Bundle bundle, final boolean b) {
        final Fragment parent = this.mFragmentManager.getParent();
        if (parent != null) {
            parent.getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentActivityCreated(fragment, bundle, true);
        }
        for (final FragmentLifecycleCallbacksHolder fragmentLifecycleCallbacksHolder : this.mLifecycleCallbacks) {
            if (!b || fragmentLifecycleCallbacksHolder.mRecursive) {
                fragmentLifecycleCallbacksHolder.mCallback.onFragmentActivityCreated(this.mFragmentManager, fragment, bundle);
            }
        }
    }
    
    void dispatchOnFragmentAttached(final Fragment fragment, final boolean b) {
        final Context context = this.mFragmentManager.getHost().getContext();
        final Fragment parent = this.mFragmentManager.getParent();
        if (parent != null) {
            parent.getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentAttached(fragment, true);
        }
        for (final FragmentLifecycleCallbacksHolder fragmentLifecycleCallbacksHolder : this.mLifecycleCallbacks) {
            if (!b || fragmentLifecycleCallbacksHolder.mRecursive) {
                fragmentLifecycleCallbacksHolder.mCallback.onFragmentAttached(this.mFragmentManager, fragment, context);
            }
        }
    }
    
    void dispatchOnFragmentCreated(final Fragment fragment, final Bundle bundle, final boolean b) {
        final Fragment parent = this.mFragmentManager.getParent();
        if (parent != null) {
            parent.getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentCreated(fragment, bundle, true);
        }
        for (final FragmentLifecycleCallbacksHolder fragmentLifecycleCallbacksHolder : this.mLifecycleCallbacks) {
            if (!b || fragmentLifecycleCallbacksHolder.mRecursive) {
                fragmentLifecycleCallbacksHolder.mCallback.onFragmentCreated(this.mFragmentManager, fragment, bundle);
            }
        }
    }
    
    void dispatchOnFragmentDestroyed(final Fragment fragment, final boolean b) {
        final Fragment parent = this.mFragmentManager.getParent();
        if (parent != null) {
            parent.getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentDestroyed(fragment, true);
        }
        for (final FragmentLifecycleCallbacksHolder fragmentLifecycleCallbacksHolder : this.mLifecycleCallbacks) {
            if (!b || fragmentLifecycleCallbacksHolder.mRecursive) {
                fragmentLifecycleCallbacksHolder.mCallback.onFragmentDestroyed(this.mFragmentManager, fragment);
            }
        }
    }
    
    void dispatchOnFragmentDetached(final Fragment fragment, final boolean b) {
        final Fragment parent = this.mFragmentManager.getParent();
        if (parent != null) {
            parent.getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentDetached(fragment, true);
        }
        for (final FragmentLifecycleCallbacksHolder fragmentLifecycleCallbacksHolder : this.mLifecycleCallbacks) {
            if (!b || fragmentLifecycleCallbacksHolder.mRecursive) {
                fragmentLifecycleCallbacksHolder.mCallback.onFragmentDetached(this.mFragmentManager, fragment);
            }
        }
    }
    
    void dispatchOnFragmentPaused(final Fragment fragment, final boolean b) {
        final Fragment parent = this.mFragmentManager.getParent();
        if (parent != null) {
            parent.getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentPaused(fragment, true);
        }
        for (final FragmentLifecycleCallbacksHolder fragmentLifecycleCallbacksHolder : this.mLifecycleCallbacks) {
            if (!b || fragmentLifecycleCallbacksHolder.mRecursive) {
                fragmentLifecycleCallbacksHolder.mCallback.onFragmentPaused(this.mFragmentManager, fragment);
            }
        }
    }
    
    void dispatchOnFragmentPreAttached(final Fragment fragment, final boolean b) {
        final Context context = this.mFragmentManager.getHost().getContext();
        final Fragment parent = this.mFragmentManager.getParent();
        if (parent != null) {
            parent.getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentPreAttached(fragment, true);
        }
        for (final FragmentLifecycleCallbacksHolder fragmentLifecycleCallbacksHolder : this.mLifecycleCallbacks) {
            if (!b || fragmentLifecycleCallbacksHolder.mRecursive) {
                fragmentLifecycleCallbacksHolder.mCallback.onFragmentPreAttached(this.mFragmentManager, fragment, context);
            }
        }
    }
    
    void dispatchOnFragmentPreCreated(final Fragment fragment, final Bundle bundle, final boolean b) {
        final Fragment parent = this.mFragmentManager.getParent();
        if (parent != null) {
            parent.getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentPreCreated(fragment, bundle, true);
        }
        for (final FragmentLifecycleCallbacksHolder fragmentLifecycleCallbacksHolder : this.mLifecycleCallbacks) {
            if (!b || fragmentLifecycleCallbacksHolder.mRecursive) {
                fragmentLifecycleCallbacksHolder.mCallback.onFragmentPreCreated(this.mFragmentManager, fragment, bundle);
            }
        }
    }
    
    void dispatchOnFragmentResumed(final Fragment fragment, final boolean b) {
        final Fragment parent = this.mFragmentManager.getParent();
        if (parent != null) {
            parent.getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentResumed(fragment, true);
        }
        for (final FragmentLifecycleCallbacksHolder fragmentLifecycleCallbacksHolder : this.mLifecycleCallbacks) {
            if (!b || fragmentLifecycleCallbacksHolder.mRecursive) {
                fragmentLifecycleCallbacksHolder.mCallback.onFragmentResumed(this.mFragmentManager, fragment);
            }
        }
    }
    
    void dispatchOnFragmentSaveInstanceState(final Fragment fragment, final Bundle bundle, final boolean b) {
        final Fragment parent = this.mFragmentManager.getParent();
        if (parent != null) {
            parent.getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentSaveInstanceState(fragment, bundle, true);
        }
        for (final FragmentLifecycleCallbacksHolder fragmentLifecycleCallbacksHolder : this.mLifecycleCallbacks) {
            if (!b || fragmentLifecycleCallbacksHolder.mRecursive) {
                fragmentLifecycleCallbacksHolder.mCallback.onFragmentSaveInstanceState(this.mFragmentManager, fragment, bundle);
            }
        }
    }
    
    void dispatchOnFragmentStarted(final Fragment fragment, final boolean b) {
        final Fragment parent = this.mFragmentManager.getParent();
        if (parent != null) {
            parent.getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentStarted(fragment, true);
        }
        for (final FragmentLifecycleCallbacksHolder fragmentLifecycleCallbacksHolder : this.mLifecycleCallbacks) {
            if (!b || fragmentLifecycleCallbacksHolder.mRecursive) {
                fragmentLifecycleCallbacksHolder.mCallback.onFragmentStarted(this.mFragmentManager, fragment);
            }
        }
    }
    
    void dispatchOnFragmentStopped(final Fragment fragment, final boolean b) {
        final Fragment parent = this.mFragmentManager.getParent();
        if (parent != null) {
            parent.getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentStopped(fragment, true);
        }
        for (final FragmentLifecycleCallbacksHolder fragmentLifecycleCallbacksHolder : this.mLifecycleCallbacks) {
            if (!b || fragmentLifecycleCallbacksHolder.mRecursive) {
                fragmentLifecycleCallbacksHolder.mCallback.onFragmentStopped(this.mFragmentManager, fragment);
            }
        }
    }
    
    void dispatchOnFragmentViewCreated(final Fragment fragment, final View view, final Bundle bundle, final boolean b) {
        final Fragment parent = this.mFragmentManager.getParent();
        if (parent != null) {
            parent.getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentViewCreated(fragment, view, bundle, true);
        }
        for (final FragmentLifecycleCallbacksHolder fragmentLifecycleCallbacksHolder : this.mLifecycleCallbacks) {
            if (!b || fragmentLifecycleCallbacksHolder.mRecursive) {
                fragmentLifecycleCallbacksHolder.mCallback.onFragmentViewCreated(this.mFragmentManager, fragment, view, bundle);
            }
        }
    }
    
    void dispatchOnFragmentViewDestroyed(final Fragment fragment, final boolean b) {
        final Fragment parent = this.mFragmentManager.getParent();
        if (parent != null) {
            parent.getParentFragmentManager().getLifecycleCallbacksDispatcher().dispatchOnFragmentViewDestroyed(fragment, true);
        }
        for (final FragmentLifecycleCallbacksHolder fragmentLifecycleCallbacksHolder : this.mLifecycleCallbacks) {
            if (!b || fragmentLifecycleCallbacksHolder.mRecursive) {
                fragmentLifecycleCallbacksHolder.mCallback.onFragmentViewDestroyed(this.mFragmentManager, fragment);
            }
        }
    }
    
    private static final class FragmentLifecycleCallbacksHolder
    {
        final FragmentManager.FragmentLifecycleCallbacks mCallback;
        final boolean mRecursive;
    }
}
