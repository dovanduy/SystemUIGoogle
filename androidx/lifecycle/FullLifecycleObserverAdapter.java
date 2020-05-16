// 
// Decompiled by Procyon v0.5.36
// 

package androidx.lifecycle;

class FullLifecycleObserverAdapter implements LifecycleEventObserver
{
    private final FullLifecycleObserver mFullLifecycleObserver;
    private final LifecycleEventObserver mLifecycleEventObserver;
    
    FullLifecycleObserverAdapter(final FullLifecycleObserver mFullLifecycleObserver, final LifecycleEventObserver mLifecycleEventObserver) {
        this.mFullLifecycleObserver = mFullLifecycleObserver;
        this.mLifecycleEventObserver = mLifecycleEventObserver;
    }
    
    @Override
    public void onStateChanged(final LifecycleOwner lifecycleOwner, final Lifecycle.Event event) {
        switch (FullLifecycleObserverAdapter$1.$SwitchMap$androidx$lifecycle$Lifecycle$Event[event.ordinal()]) {
            case 7: {
                throw new IllegalArgumentException("ON_ANY must not been send by anybody");
            }
            case 6: {
                this.mFullLifecycleObserver.onDestroy(lifecycleOwner);
                break;
            }
            case 5: {
                this.mFullLifecycleObserver.onStop(lifecycleOwner);
                break;
            }
            case 4: {
                this.mFullLifecycleObserver.onPause(lifecycleOwner);
                break;
            }
            case 3: {
                this.mFullLifecycleObserver.onResume(lifecycleOwner);
                break;
            }
            case 2: {
                this.mFullLifecycleObserver.onStart(lifecycleOwner);
                break;
            }
            case 1: {
                this.mFullLifecycleObserver.onCreate(lifecycleOwner);
                break;
            }
        }
        final LifecycleEventObserver mLifecycleEventObserver = this.mLifecycleEventObserver;
        if (mLifecycleEventObserver != null) {
            mLifecycleEventObserver.onStateChanged(lifecycleOwner, event);
        }
    }
}
