// 
// Decompiled by Procyon v0.5.36
// 

package androidx.fragment.app;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import java.util.concurrent.atomic.AtomicReference;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.ActivityResultCallback;
import androidx.lifecycle.LifecycleEventObserver;

class Fragment$5 implements LifecycleEventObserver
{
    final /* synthetic */ Fragment this$0;
    final /* synthetic */ ActivityResultCallback val$callback;
    final /* synthetic */ ActivityResultContract val$contract;
    final /* synthetic */ String val$key;
    final /* synthetic */ AtomicReference val$ref;
    
    @Override
    public void onStateChanged(final LifecycleOwner lifecycleOwner, final Lifecycle.Event other) {
        if (Lifecycle.Event.ON_CREATE.equals(other)) {
            this.val$ref.set(this.this$0.getActivity().getActivityResultRegistry().registerActivityResultCallback(this.val$key, this.this$0, (ActivityResultContract<Object, Object>)this.val$contract, this.val$callback));
        }
    }
}
