// 
// Decompiled by Procyon v0.5.36
// 

package androidx.viewpager2.adapter;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import android.os.Handler;
import androidx.lifecycle.LifecycleEventObserver;

class FragmentStateAdapter$5 implements LifecycleEventObserver
{
    final /* synthetic */ Handler val$handler;
    final /* synthetic */ Runnable val$runnable;
    
    @Override
    public void onStateChanged(final LifecycleOwner lifecycleOwner, final Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            this.val$handler.removeCallbacks(this.val$runnable);
            lifecycleOwner.getLifecycle().removeObserver(this);
        }
    }
}
