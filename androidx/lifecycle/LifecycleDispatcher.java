// 
// Decompiled by Procyon v0.5.36
// 

package androidx.lifecycle;

import android.os.Bundle;
import android.app.Activity;
import android.app.Application$ActivityLifecycleCallbacks;
import android.app.Application;
import android.content.Context;
import java.util.concurrent.atomic.AtomicBoolean;

class LifecycleDispatcher
{
    private static AtomicBoolean sInitialized;
    
    static {
        LifecycleDispatcher.sInitialized = new AtomicBoolean(false);
    }
    
    static void init(final Context context) {
        if (LifecycleDispatcher.sInitialized.getAndSet(true)) {
            return;
        }
        ((Application)context.getApplicationContext()).registerActivityLifecycleCallbacks((Application$ActivityLifecycleCallbacks)new DispatcherActivityCallback());
    }
    
    static class DispatcherActivityCallback extends EmptyActivityLifecycleCallbacks
    {
        @Override
        public void onActivityCreated(final Activity activity, final Bundle bundle) {
            ReportFragment.injectIfNeededIn(activity);
        }
        
        @Override
        public void onActivitySaveInstanceState(final Activity activity, final Bundle bundle) {
        }
        
        @Override
        public void onActivityStopped(final Activity activity) {
        }
    }
}
