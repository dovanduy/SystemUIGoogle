// 
// Decompiled by Procyon v0.5.36
// 

package androidx.lifecycle;

public interface LifecycleEventObserver extends LifecycleObserver
{
    void onStateChanged(final LifecycleOwner p0, final Lifecycle.Event p1);
}
