// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.content.res.Configuration;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.os.Bundle;
import android.app.Notification$Builder;
import android.content.Context;

public abstract class SystemUI implements Dumpable
{
    protected final Context mContext;
    
    public SystemUI(final Context mContext) {
        this.mContext = mContext;
    }
    
    public static void overrideNotificationAppName(final Context context, final Notification$Builder notification$Builder, final boolean b) {
        final Bundle bundle = new Bundle();
        String s;
        if (b) {
            s = context.getString(17040673);
        }
        else {
            s = context.getString(17040672);
        }
        bundle.putString("android.substName", s);
        notification$Builder.addExtras(bundle);
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
    }
    
    protected void onBootCompleted() {
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
    }
    
    public abstract void start();
}
