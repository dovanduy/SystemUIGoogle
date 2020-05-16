// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.face;

import com.android.systemui.Dependency;
import com.android.keyguard.KeyguardUpdateMonitor;
import android.app.Notification;
import android.app.Notification$Builder;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.os.UserHandle;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.app.NotificationManager;
import com.android.systemui.R$string;
import android.util.Log;
import android.hardware.biometrics.BiometricSourceType;
import android.os.Looper;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import android.os.Handler;
import android.content.Context;

public class FaceNotificationService
{
    private FaceNotificationBroadcastReceiver mBroadcastReceiver;
    private Context mContext;
    private final Handler mHandler;
    private final KeyguardUpdateMonitorCallback mKeyguardUpdateMonitorCallback;
    private boolean mNotificationQueued;
    
    public FaceNotificationService(final Context mContext) {
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mKeyguardUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onBiometricError(final int n, final String s, final BiometricSourceType biometricSourceType) {
                if (n == 1004) {
                    FaceNotificationSettings.updateReenrollSetting(FaceNotificationService.this.mContext, 3);
                }
            }
            
            @Override
            public void onBiometricHelp(final int n, final String s, final BiometricSourceType biometricSourceType) {
                if (n == 13) {
                    FaceNotificationSettings.updateReenrollSetting(FaceNotificationService.this.mContext, 1);
                }
            }
            
            @Override
            public void onUserUnlocked() {
                if (FaceNotificationService.this.mNotificationQueued) {
                    Log.d("FaceNotificationService", "Not showing notification; already queued.");
                    return;
                }
                if (FaceNotificationSettings.isReenrollRequired(FaceNotificationService.this.mContext)) {
                    FaceNotificationService.this.queueReenrollNotification();
                }
            }
        };
        this.mContext = mContext;
        this.start();
    }
    
    private void queueReenrollNotification() {
        this.mNotificationQueued = true;
        this.mHandler.postDelayed((Runnable)new _$$Lambda$FaceNotificationService$JhW5dvuUBJ8x7Gr9JzyT8BPPrU4(this, this.mContext.getString(R$string.face_reenroll_notification_title), this.mContext.getString(R$string.face_reenroll_notification_content)), 10000L);
    }
    
    private void showNotification(final String str, final CharSequence contentTitle, final CharSequence contentText) {
        this.mNotificationQueued = false;
        final NotificationManager notificationManager = (NotificationManager)this.mContext.getSystemService((Class)NotificationManager.class);
        if (notificationManager == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Failed to show notification ");
            sb.append(str);
            sb.append(". Notification manager is null!");
            Log.e("FaceNotificationService", sb.toString());
            return;
        }
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(str);
        intentFilter.addAction("face_action_notification_dismissed");
        this.mContext.registerReceiver((BroadcastReceiver)this.mBroadcastReceiver, intentFilter);
        final PendingIntent broadcastAsUser = PendingIntent.getBroadcastAsUser(this.mContext, 0, new Intent(str), 0, UserHandle.CURRENT);
        final PendingIntent broadcastAsUser2 = PendingIntent.getBroadcastAsUser(this.mContext, 0, new Intent("face_action_notification_dismissed"), 0, UserHandle.CURRENT);
        final String string = this.mContext.getString(R$string.face_notification_name);
        final NotificationChannel notificationChannel = new NotificationChannel("FaceHiPriNotificationChannel", (CharSequence)string, 4);
        final Notification build = new Notification$Builder(this.mContext, "FaceHiPriNotificationChannel").setCategory("sys").setSmallIcon(17302460).setContentTitle(contentTitle).setContentText(contentText).setSubText((CharSequence)string).setContentIntent(broadcastAsUser).setDeleteIntent(broadcastAsUser2).setAutoCancel(true).setLocalOnly(true).setOnlyAlertOnce(true).setVisibility(-1).build();
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notifyAsUser("FaceNotificationService", 1, build, UserHandle.CURRENT);
    }
    
    private void start() {
        Dependency.get(KeyguardUpdateMonitor.class).registerCallback(this.mKeyguardUpdateMonitorCallback);
        this.mBroadcastReceiver = new FaceNotificationBroadcastReceiver(this.mContext);
    }
}
