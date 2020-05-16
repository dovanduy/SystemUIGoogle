// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.core.lifecycle;

import androidx.lifecycle.OnLifecycleEvent;
import android.util.Log;
import com.android.settingslib.core.lifecycle.events.OnCreate;
import android.os.Bundle;
import com.android.settingslib.core.lifecycle.events.OnAttach;
import android.content.Context;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import java.util.ArrayList;
import androidx.lifecycle.LifecycleOwner;
import java.util.List;
import androidx.lifecycle.LifecycleRegistry;

public class Lifecycle extends LifecycleRegistry
{
    private final List<LifecycleObserver> mObservers;
    private final LifecycleProxy mProxy;
    
    public Lifecycle(final LifecycleOwner lifecycleOwner) {
        super(lifecycleOwner);
        this.mObservers = new ArrayList<LifecycleObserver>();
        this.addObserver(this.mProxy = new LifecycleProxy());
    }
    
    private void onDestroy() {
        for (int size = this.mObservers.size(), i = 0; i < size; ++i) {
            final LifecycleObserver lifecycleObserver = this.mObservers.get(i);
            if (lifecycleObserver instanceof OnDestroy) {
                ((OnDestroy)lifecycleObserver).onDestroy();
            }
        }
    }
    
    private void onPause() {
        for (int size = this.mObservers.size(), i = 0; i < size; ++i) {
            final LifecycleObserver lifecycleObserver = this.mObservers.get(i);
            if (lifecycleObserver instanceof OnPause) {
                ((OnPause)lifecycleObserver).onPause();
            }
        }
    }
    
    private void onResume() {
        for (int size = this.mObservers.size(), i = 0; i < size; ++i) {
            final LifecycleObserver lifecycleObserver = this.mObservers.get(i);
            if (lifecycleObserver instanceof OnResume) {
                ((OnResume)lifecycleObserver).onResume();
            }
        }
    }
    
    private void onStart() {
        for (int size = this.mObservers.size(), i = 0; i < size; ++i) {
            final LifecycleObserver lifecycleObserver = this.mObservers.get(i);
            if (lifecycleObserver instanceof OnStart) {
                ((OnStart)lifecycleObserver).onStart();
            }
        }
    }
    
    private void onStop() {
        for (int size = this.mObservers.size(), i = 0; i < size; ++i) {
            final LifecycleObserver lifecycleObserver = this.mObservers.get(i);
            if (lifecycleObserver instanceof OnStop) {
                ((OnStop)lifecycleObserver).onStop();
            }
        }
    }
    
    @Override
    public void addObserver(final androidx.lifecycle.LifecycleObserver lifecycleObserver) {
        ThreadUtils.ensureMainThread();
        super.addObserver(lifecycleObserver);
        if (lifecycleObserver instanceof LifecycleObserver) {
            this.mObservers.add((LifecycleObserver)lifecycleObserver);
        }
    }
    
    public void onAttach(final Context context) {
        for (int size = this.mObservers.size(), i = 0; i < size; ++i) {
            final LifecycleObserver lifecycleObserver = this.mObservers.get(i);
            if (lifecycleObserver instanceof OnAttach) {
                ((OnAttach)lifecycleObserver).onAttach();
            }
        }
    }
    
    public void onCreate(final Bundle bundle) {
        for (int size = this.mObservers.size(), i = 0; i < size; ++i) {
            final LifecycleObserver lifecycleObserver = this.mObservers.get(i);
            if (lifecycleObserver instanceof OnCreate) {
                ((OnCreate)lifecycleObserver).onCreate(bundle);
            }
        }
    }
    
    @Override
    public void removeObserver(final androidx.lifecycle.LifecycleObserver lifecycleObserver) {
        ThreadUtils.ensureMainThread();
        super.removeObserver(lifecycleObserver);
        if (lifecycleObserver instanceof LifecycleObserver) {
            this.mObservers.remove(lifecycleObserver);
        }
    }
    
    private class LifecycleProxy implements LifecycleObserver
    {
        @OnLifecycleEvent(Event.ON_ANY)
        public void onLifecycleEvent(final LifecycleOwner lifecycleOwner, final Event event) {
            switch (Lifecycle$1.$SwitchMap$androidx$lifecycle$Lifecycle$Event[event.ordinal()]) {
                case 7: {
                    Log.wtf("LifecycleObserver", "Should not receive an 'ANY' event!");
                    break;
                }
                case 6: {
                    Lifecycle.this.onDestroy();
                    break;
                }
                case 5: {
                    Lifecycle.this.onStop();
                    break;
                }
                case 4: {
                    Lifecycle.this.onPause();
                    break;
                }
                case 3: {
                    Lifecycle.this.onResume();
                    break;
                }
                case 2: {
                    Lifecycle.this.onStart();
                    break;
                }
            }
        }
    }
}
