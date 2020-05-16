// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.app;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.app.Application;
import java.lang.reflect.InvocationTargetException;
import android.app.Activity;
import android.content.Intent;

public class AppComponentFactory extends android.app.AppComponentFactory
{
    public final Activity instantiateActivity(final ClassLoader classLoader, final String s, final Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return CoreComponentFactory.checkCompatWrapper(this.instantiateActivityCompat(classLoader, s, intent));
    }
    
    public Activity instantiateActivityCompat(final ClassLoader loader, final String name, final Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        try {
            return (Activity)Class.forName(name, false, loader).asSubclass(Activity.class).getDeclaredConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        }
        catch (InvocationTargetException | NoSuchMethodException ex) {
            final Object cause;
            throw new RuntimeException("Couldn't call constructor", (Throwable)cause);
        }
    }
    
    public final Application instantiateApplication(final ClassLoader classLoader, final String s) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return CoreComponentFactory.checkCompatWrapper(this.instantiateApplicationCompat(classLoader, s));
    }
    
    public Application instantiateApplicationCompat(final ClassLoader loader, final String name) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        try {
            return (Application)Class.forName(name, false, loader).asSubclass(Application.class).getDeclaredConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        }
        catch (InvocationTargetException | NoSuchMethodException ex) {
            final Object cause;
            throw new RuntimeException("Couldn't call constructor", (Throwable)cause);
        }
    }
    
    public final ContentProvider instantiateProvider(final ClassLoader classLoader, final String s) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return CoreComponentFactory.checkCompatWrapper(this.instantiateProviderCompat(classLoader, s));
    }
    
    public ContentProvider instantiateProviderCompat(final ClassLoader loader, final String name) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        try {
            return (ContentProvider)Class.forName(name, false, loader).asSubclass(ContentProvider.class).getDeclaredConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        }
        catch (InvocationTargetException | NoSuchMethodException ex) {
            final Object cause;
            throw new RuntimeException("Couldn't call constructor", (Throwable)cause);
        }
    }
    
    public final BroadcastReceiver instantiateReceiver(final ClassLoader classLoader, final String s, final Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return CoreComponentFactory.checkCompatWrapper(this.instantiateReceiverCompat(classLoader, s, intent));
    }
    
    public BroadcastReceiver instantiateReceiverCompat(final ClassLoader loader, final String name, final Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        try {
            return (BroadcastReceiver)Class.forName(name, false, loader).asSubclass(BroadcastReceiver.class).getDeclaredConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        }
        catch (InvocationTargetException | NoSuchMethodException ex) {
            final Object cause;
            throw new RuntimeException("Couldn't call constructor", (Throwable)cause);
        }
    }
    
    public final Service instantiateService(final ClassLoader classLoader, final String s, final Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return CoreComponentFactory.checkCompatWrapper(this.instantiateServiceCompat(classLoader, s, intent));
    }
    
    public Service instantiateServiceCompat(final ClassLoader loader, final String name, final Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        try {
            return (Service)Class.forName(name, false, loader).asSubclass(Service.class).getDeclaredConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        }
        catch (InvocationTargetException | NoSuchMethodException ex) {
            final Object cause;
            throw new RuntimeException("Couldn't call constructor", (Throwable)cause);
        }
    }
}
