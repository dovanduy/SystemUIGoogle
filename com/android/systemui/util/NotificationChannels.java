// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util;

import com.android.internal.annotations.VisibleForTesting;
import java.util.List;
import java.util.Arrays;
import android.media.AudioAttributes$Builder;
import android.net.Uri;
import android.provider.Settings$Global;
import android.app.NotificationChannel;
import com.android.systemui.R$string;
import android.app.NotificationManager;
import android.content.Context;
import com.android.systemui.SystemUI;

public class NotificationChannels extends SystemUI
{
    public static String ALERTS = "ALR";
    public static String BATTERY = "BAT";
    public static String GENERAL = "GEN";
    public static String HINTS = "HNT";
    public static String SCREENSHOTS_HEADSUP = "SCN_HEADSUP";
    public static String SCREENSHOTS_LEGACY = "SCN";
    public static String STORAGE = "DSK";
    public static String TVPIP = "TPP";
    
    public NotificationChannels(final Context context) {
        super(context);
    }
    
    public static void createAll(final Context context) {
        final NotificationManager notificationManager = (NotificationManager)context.getSystemService((Class)NotificationManager.class);
        final NotificationChannel notificationChannel = new NotificationChannel(NotificationChannels.BATTERY, (CharSequence)context.getString(R$string.notification_channel_battery), 5);
        final String string = Settings$Global.getString(context.getContentResolver(), "low_battery_sound");
        final StringBuilder sb = new StringBuilder();
        sb.append("file://");
        sb.append(string);
        notificationChannel.setSound(Uri.parse(sb.toString()), new AudioAttributes$Builder().setContentType(4).setUsage(10).build());
        notificationChannel.setBlockable(true);
        final NotificationChannel notificationChannel2 = new NotificationChannel(NotificationChannels.ALERTS, (CharSequence)context.getString(R$string.notification_channel_alerts), 4);
        final NotificationChannel notificationChannel3 = new NotificationChannel(NotificationChannels.GENERAL, (CharSequence)context.getString(R$string.notification_channel_general), 1);
        final String storage = NotificationChannels.STORAGE;
        final String string2 = context.getString(R$string.notification_channel_storage);
        int n;
        if (isTv(context)) {
            n = 3;
        }
        else {
            n = 2;
        }
        notificationManager.createNotificationChannels((List)Arrays.asList(notificationChannel2, notificationChannel3, new NotificationChannel(storage, (CharSequence)string2, n), createScreenshotChannel(context.getString(R$string.notification_channel_screenshot), notificationManager.getNotificationChannel(NotificationChannels.SCREENSHOTS_LEGACY)), notificationChannel, new NotificationChannel(NotificationChannels.HINTS, (CharSequence)context.getString(R$string.notification_channel_hints), 3)));
        notificationManager.deleteNotificationChannel(NotificationChannels.SCREENSHOTS_LEGACY);
        if (isTv(context)) {
            notificationManager.createNotificationChannel(new NotificationChannel(NotificationChannels.TVPIP, (CharSequence)context.getString(R$string.notification_channel_tv_pip), 5));
        }
    }
    
    @VisibleForTesting
    static NotificationChannel createScreenshotChannel(final String s, final NotificationChannel notificationChannel) {
        final NotificationChannel notificationChannel2 = new NotificationChannel(NotificationChannels.SCREENSHOTS_HEADSUP, (CharSequence)s, 4);
        notificationChannel2.setSound((Uri)null, new AudioAttributes$Builder().setUsage(5).build());
        notificationChannel2.setBlockable(true);
        if (notificationChannel != null) {
            final int userLockedFields = notificationChannel.getUserLockedFields();
            if ((userLockedFields & 0x4) != 0x0) {
                notificationChannel2.setImportance(notificationChannel.getImportance());
            }
            if ((userLockedFields & 0x20) != 0x0) {
                notificationChannel2.setSound(notificationChannel.getSound(), notificationChannel.getAudioAttributes());
            }
            if ((userLockedFields & 0x10) != 0x0) {
                notificationChannel2.setVibrationPattern(notificationChannel.getVibrationPattern());
            }
            if ((userLockedFields & 0x8) != 0x0) {
                notificationChannel2.setLightColor(notificationChannel.getLightColor());
            }
        }
        return notificationChannel2;
    }
    
    private static boolean isTv(final Context context) {
        return context.getPackageManager().hasSystemFeature("android.software.leanback");
    }
    
    @Override
    public void start() {
        createAll(super.mContext);
    }
}
