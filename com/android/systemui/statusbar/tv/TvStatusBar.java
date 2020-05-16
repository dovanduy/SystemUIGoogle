// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.tv;

import com.android.internal.statusbar.IStatusBarService;
import android.os.RemoteException;
import com.android.systemui.statusbar.tv.micdisclosure.AudioRecordingDisclosureBar;
import com.android.internal.statusbar.IStatusBar;
import com.android.internal.statusbar.IStatusBarService$Stub;
import android.os.ServiceManager;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.content.Intent;
import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.SystemUI;

public class TvStatusBar extends SystemUI implements Callbacks
{
    private final CommandQueue mCommandQueue;
    
    public TvStatusBar(final Context context, final CommandQueue mCommandQueue) {
        super(context);
        this.mCommandQueue = mCommandQueue;
    }
    
    private void startSystemActivity(final Intent intent) {
        final ResolveInfo resolveActivity = super.mContext.getPackageManager().resolveActivity(intent, 1048576);
        if (resolveActivity != null) {
            final ActivityInfo activityInfo = resolveActivity.activityInfo;
            if (activityInfo != null) {
                intent.setPackage(activityInfo.packageName);
                super.mContext.startActivityAsUser(intent, UserHandle.CURRENT);
            }
        }
    }
    
    @Override
    public void animateExpandNotificationsPanel() {
        this.startSystemActivity(new Intent("com.android.tv.action.OPEN_NOTIFICATIONS_PANEL"));
    }
    
    @Override
    public void start() {
        final IStatusBarService interface1 = IStatusBarService$Stub.asInterface(ServiceManager.getService("statusbar"));
        this.mCommandQueue.addCallback((CommandQueue.Callbacks)this);
        while (true) {
            try {
                interface1.registerStatusBar((IStatusBar)this.mCommandQueue);
                new AudioRecordingDisclosureBar(super.mContext);
            }
            catch (RemoteException ex) {
                continue;
            }
            break;
        }
    }
}
