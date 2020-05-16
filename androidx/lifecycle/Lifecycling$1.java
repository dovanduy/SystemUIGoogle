// 
// Decompiled by Procyon v0.5.36
// 

package androidx.lifecycle;

class Lifecycling$1 implements LifecycleEventObserver
{
    final /* synthetic */ LifecycleEventObserver val$observer;
    
    @Override
    public void onStateChanged(final LifecycleOwner lifecycleOwner, final Lifecycle.Event event) {
        this.val$observer.onStateChanged(lifecycleOwner, event);
    }
}
