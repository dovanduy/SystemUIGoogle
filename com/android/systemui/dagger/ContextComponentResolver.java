// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import com.android.systemui.SystemUI;
import android.app.Service;
import com.android.systemui.recents.RecentsImplementation;
import android.content.BroadcastReceiver;
import android.app.Activity;
import javax.inject.Provider;
import java.util.Map;

public class ContextComponentResolver implements ContextComponentHelper
{
    private final Map<Class<?>, Provider<Activity>> mActivityCreators;
    private final Map<Class<?>, Provider<BroadcastReceiver>> mBroadcastReceiverCreators;
    private final Map<Class<?>, Provider<RecentsImplementation>> mRecentsCreators;
    private final Map<Class<?>, Provider<Service>> mServiceCreators;
    private final Map<Class<?>, Provider<SystemUI>> mSystemUICreators;
    
    ContextComponentResolver(final Map<Class<?>, Provider<Activity>> mActivityCreators, final Map<Class<?>, Provider<Service>> mServiceCreators, final Map<Class<?>, Provider<SystemUI>> mSystemUICreators, final Map<Class<?>, Provider<RecentsImplementation>> mRecentsCreators, final Map<Class<?>, Provider<BroadcastReceiver>> mBroadcastReceiverCreators) {
        this.mActivityCreators = mActivityCreators;
        this.mServiceCreators = mServiceCreators;
        this.mSystemUICreators = mSystemUICreators;
        this.mRecentsCreators = mRecentsCreators;
        this.mBroadcastReceiverCreators = mBroadcastReceiverCreators;
    }
    
    private <T> T resolve(final String className, final Map<Class<?>, Provider<T>> map) {
        final T t = null;
        try {
            final Provider<T> provider = map.get(Class.forName(className));
            T value;
            if (provider == null) {
                value = t;
            }
            else {
                value = provider.get();
            }
            return value;
        }
        catch (ClassNotFoundException ex) {
            return t;
        }
    }
    
    @Override
    public Activity resolveActivity(final String s) {
        return this.resolve(s, this.mActivityCreators);
    }
    
    @Override
    public BroadcastReceiver resolveBroadcastReceiver(final String s) {
        return this.resolve(s, this.mBroadcastReceiverCreators);
    }
    
    @Override
    public RecentsImplementation resolveRecents(final String s) {
        return this.resolve(s, this.mRecentsCreators);
    }
    
    @Override
    public Service resolveService(final String s) {
        return this.resolve(s, this.mServiceCreators);
    }
    
    @Override
    public SystemUI resolveSystemUI(final String s) {
        return this.resolve(s, this.mSystemUICreators);
    }
}
