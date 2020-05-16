// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.app.Application;
import android.app.Activity;
import android.content.Intent;
import android.content.ContentProvider;
import android.content.Context;
import com.android.systemui.dagger.ContextComponentHelper;
import androidx.core.app.AppComponentFactory;

public class SystemUIAppComponentFactory extends AppComponentFactory
{
    public ContextComponentHelper mComponentHelper;
    
    @Override
    public Activity instantiateActivityCompat(final ClassLoader classLoader, final String s, final Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (this.mComponentHelper == null) {
            SystemUIFactory.getInstance().getRootComponent().inject(this);
        }
        final Activity resolveActivity = this.mComponentHelper.resolveActivity(s);
        if (resolveActivity != null) {
            return resolveActivity;
        }
        return super.instantiateActivityCompat(classLoader, s, intent);
    }
    
    @Override
    public Application instantiateApplicationCompat(final ClassLoader classLoader, final String s) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        final Application instantiateApplicationCompat = super.instantiateApplicationCompat(classLoader, s);
        if (instantiateApplicationCompat instanceof ContextInitializer) {
            ((ContextInitializer)instantiateApplicationCompat).setContextAvailableCallback(new _$$Lambda$SystemUIAppComponentFactory$K7lft0lbYxYv1XYt4OjLQAAUcDg(this));
        }
        return instantiateApplicationCompat;
    }
    
    @Override
    public ContentProvider instantiateProviderCompat(final ClassLoader classLoader, final String s) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        final ContentProvider instantiateProviderCompat = super.instantiateProviderCompat(classLoader, s);
        if (instantiateProviderCompat instanceof ContextInitializer) {
            ((ContextInitializer)instantiateProviderCompat).setContextAvailableCallback(new _$$Lambda$SystemUIAppComponentFactory$pPiiW5zNbVk8McZrLK2oqhHWM0g(instantiateProviderCompat));
        }
        return instantiateProviderCompat;
    }
    
    @Override
    public BroadcastReceiver instantiateReceiverCompat(final ClassLoader classLoader, final String s, final Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (this.mComponentHelper == null) {
            SystemUIFactory.getInstance().getRootComponent().inject(this);
        }
        final BroadcastReceiver resolveBroadcastReceiver = this.mComponentHelper.resolveBroadcastReceiver(s);
        if (resolveBroadcastReceiver != null) {
            return resolveBroadcastReceiver;
        }
        return super.instantiateReceiverCompat(classLoader, s, intent);
    }
    
    @Override
    public Service instantiateServiceCompat(final ClassLoader classLoader, final String s, final Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (this.mComponentHelper == null) {
            SystemUIFactory.getInstance().getRootComponent().inject(this);
        }
        final Service resolveService = this.mComponentHelper.resolveService(s);
        if (resolveService != null) {
            return resolveService;
        }
        return super.instantiateServiceCompat(classLoader, s, intent);
    }
    
    public interface ContextAvailableCallback
    {
        void onContextAvailable(final Context p0);
    }
    
    public interface ContextInitializer
    {
        void setContextAvailableCallback(final ContextAvailableCallback p0);
    }
}
