// 
// Decompiled by Procyon v0.5.36
// 

package androidx.viewpager2.adapter;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleEventObserver;

class FragmentStateAdapter$FragmentMaxLifecycleEnforcer$3 implements LifecycleEventObserver
{
    final /* synthetic */ FragmentStateAdapter.FragmentMaxLifecycleEnforcer this$1;
    
    @Override
    public void onStateChanged(final LifecycleOwner lifecycleOwner, final Lifecycle.Event event) {
        this.this$1.updateFragmentMaxLifecycle(false);
    }
}
