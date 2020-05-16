// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.os.IBinder;
import android.content.Intent;
import android.app.Service;

public class SystemUISecondaryUserService extends Service
{
    public IBinder onBind(final Intent intent) {
        return null;
    }
    
    public void onCreate() {
        super.onCreate();
        ((SystemUIApplication)this.getApplication()).startSecondaryUserServicesIfNeeded();
    }
}
