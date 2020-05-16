// 
// Decompiled by Procyon v0.5.36
// 

package androidx.lifecycle;

import java.util.Iterator;
import java.util.Map;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.internal.SafeIterableMap;

public abstract class LiveData<T>
{
    static final Object NOT_SET;
    int mActiveCount;
    private volatile Object mData;
    final Object mDataLock;
    private boolean mDispatchInvalidated;
    private boolean mDispatchingValue;
    private SafeIterableMap<Observer<? super T>, ObserverWrapper> mObservers;
    volatile Object mPendingData;
    private final Runnable mPostValueRunnable;
    private int mVersion;
    
    static {
        NOT_SET = new Object();
    }
    
    public LiveData() {
        final Object not_SET = LiveData.NOT_SET;
        this.mDataLock = new Object();
        this.mObservers = new SafeIterableMap<Observer<? super T>, ObserverWrapper>();
        this.mActiveCount = 0;
        this.mPendingData = not_SET;
        this.mPostValueRunnable = new Runnable() {
            @Override
            public void run() {
                synchronized (LiveData.this.mDataLock) {
                    final Object mPendingData = LiveData.this.mPendingData;
                    LiveData.this.mPendingData = LiveData.NOT_SET;
                    // monitorexit(this.this$0.mDataLock)
                    LiveData.this.setValue(mPendingData);
                }
            }
        };
        this.mData = not_SET;
        this.mVersion = -1;
    }
    
    static void assertMainThread(final String str) {
        if (ArchTaskExecutor.getInstance().isMainThread()) {
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Cannot invoke ");
        sb.append(str);
        sb.append(" on a background thread");
        throw new IllegalStateException(sb.toString());
    }
    
    private void considerNotify(final ObserverWrapper observerWrapper) {
        if (!observerWrapper.mActive) {
            return;
        }
        if (!observerWrapper.shouldBeActive()) {
            observerWrapper.activeStateChanged(false);
            return;
        }
        final int mLastVersion = observerWrapper.mLastVersion;
        final int mVersion = this.mVersion;
        if (mLastVersion >= mVersion) {
            return;
        }
        observerWrapper.mLastVersion = mVersion;
        observerWrapper.mObserver.onChanged((Object)this.mData);
    }
    
    void dispatchingValue(ObserverWrapper observerWrapper) {
        if (this.mDispatchingValue) {
            this.mDispatchInvalidated = true;
            return;
        }
        this.mDispatchingValue = true;
        do {
            this.mDispatchInvalidated = false;
            ObserverWrapper observerWrapper2 = null;
            Label_0086: {
                if (observerWrapper != null) {
                    this.considerNotify(observerWrapper);
                    observerWrapper2 = null;
                }
                else {
                    final SafeIterableMap.IteratorWithAdditions iteratorWithAdditions = this.mObservers.iteratorWithAdditions();
                    do {
                        observerWrapper2 = observerWrapper;
                        if (!iteratorWithAdditions.hasNext()) {
                            break Label_0086;
                        }
                        this.considerNotify(((Iterator<Map.Entry<K, ObserverWrapper>>)iteratorWithAdditions).next().getValue());
                    } while (!this.mDispatchInvalidated);
                    observerWrapper2 = observerWrapper;
                }
            }
            observerWrapper = observerWrapper2;
        } while (this.mDispatchInvalidated);
        this.mDispatchingValue = false;
    }
    
    public T getValue() {
        final Object mData = this.mData;
        if (mData != LiveData.NOT_SET) {
            return (T)mData;
        }
        return null;
    }
    
    public boolean hasActiveObservers() {
        return this.mActiveCount > 0;
    }
    
    public void observe(final LifecycleOwner lifecycleOwner, final Observer<? super T> observer) {
        assertMainThread("observe");
        if (lifecycleOwner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            return;
        }
        final LifecycleBoundObserver lifecycleBoundObserver = new LifecycleBoundObserver(lifecycleOwner, observer);
        final ObserverWrapper observerWrapper = this.mObservers.putIfAbsent(observer, (ObserverWrapper)lifecycleBoundObserver);
        if (observerWrapper != null && !observerWrapper.isAttachedTo(lifecycleOwner)) {
            throw new IllegalArgumentException("Cannot add the same observer with different lifecycles");
        }
        if (observerWrapper != null) {
            return;
        }
        lifecycleOwner.getLifecycle().addObserver(lifecycleBoundObserver);
    }
    
    public void observeForever(final Observer<? super T> observer) {
        assertMainThread("observeForever");
        final AlwaysActiveObserver alwaysActiveObserver = new AlwaysActiveObserver(observer);
        final ObserverWrapper observerWrapper = this.mObservers.putIfAbsent(observer, (ObserverWrapper)alwaysActiveObserver);
        if (observerWrapper instanceof LifecycleBoundObserver) {
            throw new IllegalArgumentException("Cannot add the same observer with different lifecycles");
        }
        if (observerWrapper != null) {
            return;
        }
        ((ObserverWrapper)alwaysActiveObserver).activeStateChanged(true);
    }
    
    protected void onActive() {
    }
    
    protected void onInactive() {
    }
    
    protected void postValue(final T mPendingData) {
        synchronized (this.mDataLock) {
            final boolean b = this.mPendingData == LiveData.NOT_SET;
            this.mPendingData = mPendingData;
            // monitorexit(this.mDataLock)
            if (!b) {
                return;
            }
            ArchTaskExecutor.getInstance().postToMainThread(this.mPostValueRunnable);
        }
    }
    
    public void removeObserver(final Observer<? super T> observer) {
        assertMainThread("removeObserver");
        final ObserverWrapper observerWrapper = this.mObservers.remove(observer);
        if (observerWrapper == null) {
            return;
        }
        observerWrapper.detachObserver();
        observerWrapper.activeStateChanged(false);
    }
    
    protected void setValue(final T mData) {
        assertMainThread("setValue");
        ++this.mVersion;
        this.mData = mData;
        this.dispatchingValue(null);
    }
    
    private class AlwaysActiveObserver extends ObserverWrapper
    {
        AlwaysActiveObserver(final LiveData liveData, final Observer<? super T> observer) {
            super(observer);
        }
        
        @Override
        boolean shouldBeActive() {
            return true;
        }
    }
    
    class LifecycleBoundObserver extends ObserverWrapper implements LifecycleEventObserver
    {
        final LifecycleOwner mOwner;
        
        LifecycleBoundObserver(final LifecycleOwner mOwner, final Observer<? super T> observer) {
            super(observer);
            this.mOwner = mOwner;
        }
        
        @Override
        void detachObserver() {
            this.mOwner.getLifecycle().removeObserver(this);
        }
        
        @Override
        boolean isAttachedTo(final LifecycleOwner lifecycleOwner) {
            return this.mOwner == lifecycleOwner;
        }
        
        @Override
        public void onStateChanged(final LifecycleOwner lifecycleOwner, final Lifecycle.Event event) {
            if (this.mOwner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
                LiveData.this.removeObserver(super.mObserver);
                return;
            }
            ((ObserverWrapper)this).activeStateChanged(this.shouldBeActive());
        }
        
        @Override
        boolean shouldBeActive() {
            return this.mOwner.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED);
        }
    }
    
    private abstract class ObserverWrapper
    {
        boolean mActive;
        int mLastVersion;
        final Observer<? super T> mObserver;
        
        ObserverWrapper(final Observer<? super T> mObserver) {
            this.mLastVersion = -1;
            this.mObserver = mObserver;
        }
        
        void activeStateChanged(final boolean mActive) {
            if (mActive == this.mActive) {
                return;
            }
            this.mActive = mActive;
            final int mActiveCount = LiveData.this.mActiveCount;
            int n = 1;
            final boolean b = mActiveCount == 0;
            final LiveData this$0 = LiveData.this;
            final int mActiveCount2 = this$0.mActiveCount;
            if (!this.mActive) {
                n = -1;
            }
            this$0.mActiveCount = mActiveCount2 + n;
            if (b && this.mActive) {
                LiveData.this.onActive();
            }
            final LiveData this$2 = LiveData.this;
            if (this$2.mActiveCount == 0 && !this.mActive) {
                this$2.onInactive();
            }
            if (this.mActive) {
                LiveData.this.dispatchingValue(this);
            }
        }
        
        void detachObserver() {
        }
        
        boolean isAttachedTo(final LifecycleOwner lifecycleOwner) {
            return false;
        }
        
        abstract boolean shouldBeActive();
    }
}
