// 
// Decompiled by Procyon v0.5.36
// 

package androidx.viewpager2.adapter;

import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleEventObserver;

class FragmentStateAdapter$2 implements LifecycleEventObserver
{
    final /* synthetic */ FragmentStateAdapter this$0;
    final /* synthetic */ FragmentViewHolder val$holder;
    
    @Override
    public void onStateChanged(final LifecycleOwner lifecycleOwner, final Lifecycle.Event event) {
        if (this.this$0.shouldDelayFragmentTransactions()) {
            return;
        }
        lifecycleOwner.getLifecycle().removeObserver(this);
        if (ViewCompat.isAttachedToWindow((View)this.val$holder.getContainer())) {
            this.this$0.placeFragmentInViewHolder(this.val$holder);
        }
    }
}
