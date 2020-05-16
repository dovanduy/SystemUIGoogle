// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.app;

import android.os.Bundle;
import android.content.res.Configuration;
import android.view.MenuInflater;
import android.content.Context;
import android.view.ViewGroup$LayoutParams;
import android.view.View;
import java.util.Iterator;
import android.app.Dialog;
import android.app.Activity;
import java.lang.ref.WeakReference;
import androidx.collection.ArraySet;

public abstract class AppCompatDelegate
{
    private static final ArraySet<WeakReference<AppCompatDelegate>> sActiveDelegates;
    private static final Object sActiveDelegatesLock;
    private static int sDefaultNightMode = -100;
    
    static {
        sActiveDelegates = new ArraySet<WeakReference<AppCompatDelegate>>();
        sActiveDelegatesLock = new Object();
    }
    
    AppCompatDelegate() {
    }
    
    static void addActiveDelegate(final AppCompatDelegate referent) {
        synchronized (AppCompatDelegate.sActiveDelegatesLock) {
            removeDelegateFromActives(referent);
            AppCompatDelegate.sActiveDelegates.add(new WeakReference<AppCompatDelegate>(referent));
        }
    }
    
    public static AppCompatDelegate create(final Activity activity, final AppCompatCallback appCompatCallback) {
        return new AppCompatDelegateImpl(activity, appCompatCallback);
    }
    
    public static AppCompatDelegate create(final Dialog dialog, final AppCompatCallback appCompatCallback) {
        return new AppCompatDelegateImpl(dialog, appCompatCallback);
    }
    
    public static int getDefaultNightMode() {
        return AppCompatDelegate.sDefaultNightMode;
    }
    
    static void removeActiveDelegate(final AppCompatDelegate appCompatDelegate) {
        synchronized (AppCompatDelegate.sActiveDelegatesLock) {
            removeDelegateFromActives(appCompatDelegate);
        }
    }
    
    private static void removeDelegateFromActives(final AppCompatDelegate appCompatDelegate) {
        synchronized (AppCompatDelegate.sActiveDelegatesLock) {
            final Iterator<WeakReference<AppCompatDelegate>> iterator = AppCompatDelegate.sActiveDelegates.iterator();
            while (iterator.hasNext()) {
                final AppCompatDelegate appCompatDelegate2 = iterator.next().get();
                if (appCompatDelegate2 == appCompatDelegate || appCompatDelegate2 == null) {
                    iterator.remove();
                }
            }
        }
    }
    
    public abstract void addContentView(final View p0, final ViewGroup$LayoutParams p1);
    
    @Deprecated
    public void attachBaseContext(final Context context) {
    }
    
    public Context attachBaseContext2(final Context context) {
        this.attachBaseContext(context);
        return context;
    }
    
    public abstract <T extends View> T findViewById(final int p0);
    
    public int getLocalNightMode() {
        return -100;
    }
    
    public abstract MenuInflater getMenuInflater();
    
    public abstract ActionBar getSupportActionBar();
    
    public abstract void installViewFactory();
    
    public abstract void invalidateOptionsMenu();
    
    public abstract void onConfigurationChanged(final Configuration p0);
    
    public abstract void onCreate(final Bundle p0);
    
    public abstract void onDestroy();
    
    public abstract void onPostCreate(final Bundle p0);
    
    public abstract void onPostResume();
    
    public abstract void onSaveInstanceState(final Bundle p0);
    
    public abstract void onStart();
    
    public abstract void onStop();
    
    public abstract boolean requestWindowFeature(final int p0);
    
    public abstract void setContentView(final int p0);
    
    public abstract void setContentView(final View p0);
    
    public abstract void setContentView(final View p0, final ViewGroup$LayoutParams p1);
    
    public void setTheme(final int n) {
    }
    
    public abstract void setTitle(final CharSequence p0);
}
