// 
// Decompiled by Procyon v0.5.36
// 

package androidx.activity;

import android.view.View;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import android.view.inputmethod.InputMethodManager;
import android.app.Activity;
import java.lang.reflect.Field;
import androidx.lifecycle.LifecycleEventObserver;

final class ImmLeaksCleaner implements LifecycleEventObserver
{
    private static Field sHField;
    private static Field sNextServedViewField;
    private static int sReflectedFieldsInitialized;
    private static Field sServedViewField;
    private Activity mActivity;
    
    ImmLeaksCleaner(final Activity mActivity) {
        this.mActivity = mActivity;
    }
    
    private static void initializeReflectiveFields() {
        try {
            ImmLeaksCleaner.sReflectedFieldsInitialized = 2;
            (ImmLeaksCleaner.sServedViewField = InputMethodManager.class.getDeclaredField("mServedView")).setAccessible(true);
            (ImmLeaksCleaner.sNextServedViewField = InputMethodManager.class.getDeclaredField("mNextServedView")).setAccessible(true);
            (ImmLeaksCleaner.sHField = InputMethodManager.class.getDeclaredField("mH")).setAccessible(true);
            ImmLeaksCleaner.sReflectedFieldsInitialized = 1;
        }
        catch (NoSuchFieldException ex) {}
    }
    
    @Override
    public void onStateChanged(LifecycleOwner value, final Lifecycle.Event event) {
        if (event != Lifecycle.Event.ON_DESTROY) {
            return;
        }
        if (ImmLeaksCleaner.sReflectedFieldsInitialized == 0) {
            initializeReflectiveFields();
        }
        if (ImmLeaksCleaner.sReflectedFieldsInitialized != 1) {
            return;
        }
        final InputMethodManager obj = (InputMethodManager)this.mActivity.getSystemService("input_method");
        try {
            value = (LifecycleOwner)ImmLeaksCleaner.sHField.get(obj);
            if (value == null) {
                return;
            }
            // monitorenter(value)
            try {
                try {
                    final View view = (View)ImmLeaksCleaner.sServedViewField.get(obj);
                    if (view == null) {
                        // monitorexit(value)
                        return;
                    }
                    if (view.isAttachedToWindow()) {
                        // monitorexit(value)
                        return;
                    }
                    try {
                        ImmLeaksCleaner.sNextServedViewField.set(obj, null);
                        // monitorexit(value)
                        obj.isActive();
                    }
                    catch (IllegalAccessException ex) {
                    }
                    // monitorexit(value)
                }
                finally {
                }
                // monitorexit(value)
            }
            catch (ClassCastException ex2) {}
            catch (IllegalAccessException ex3) {}
        }
        catch (IllegalAccessException ex4) {}
    }
}
