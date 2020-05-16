// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.screenshot;

import android.content.Intent;
import android.app.Notification$BigTextStyle;
import com.android.systemui.SystemUI;
import android.os.Bundle;
import android.app.PendingIntent;
import android.os.UserHandle;
import android.app.admin.DevicePolicyManager;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import android.app.Notification$Builder;
import com.android.systemui.util.NotificationChannels;
import android.content.res.Resources$NotFoundException;
import com.android.systemui.R$dimen;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.content.res.Resources;
import android.app.Notification$BigPictureStyle;
import android.app.NotificationManager;
import android.content.Context;

public class ScreenshotNotificationsController
{
    private final Context mContext;
    private int mIconSize;
    private final NotificationManager mNotificationManager;
    private final Notification$BigPictureStyle mNotificationStyle;
    private int mPreviewHeight;
    private int mPreviewWidth;
    private final Resources mResources;
    
    ScreenshotNotificationsController(Context mContext, final WindowManager windowManager) {
        this.mContext = mContext;
        this.mResources = mContext.getResources();
        this.mNotificationManager = (NotificationManager)mContext.getSystemService("notification");
        this.mIconSize = this.mResources.getDimensionPixelSize(17104902);
        mContext = (Context)new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics((DisplayMetrics)mContext);
        int dimensionPixelSize;
        try {
            dimensionPixelSize = this.mResources.getDimensionPixelSize(R$dimen.notification_panel_width);
        }
        catch (Resources$NotFoundException ex) {
            dimensionPixelSize = 0;
        }
        int widthPixels = dimensionPixelSize;
        if (dimensionPixelSize <= 0) {
            widthPixels = ((DisplayMetrics)mContext).widthPixels;
        }
        this.mPreviewWidth = widthPixels;
        this.mPreviewHeight = this.mResources.getDimensionPixelSize(R$dimen.notification_max_height);
        this.mNotificationStyle = new Notification$BigPictureStyle();
    }
    
    static void cancelScreenshotNotification(final Context context) {
        ((NotificationManager)context.getSystemService("notification")).cancel(1);
    }
    
    public void notifyScreenshotError(final int n) {
        final Resources resources = this.mContext.getResources();
        final String string = resources.getString(n);
        final Notification$Builder setColor = new Notification$Builder(this.mContext, NotificationChannels.ALERTS).setTicker((CharSequence)resources.getString(R$string.screenshot_failed_title)).setContentTitle((CharSequence)resources.getString(R$string.screenshot_failed_title)).setContentText((CharSequence)string).setSmallIcon(R$drawable.stat_notify_image_error).setWhen(System.currentTimeMillis()).setVisibility(1).setCategory("err").setAutoCancel(true).setColor(this.mContext.getColor(17170460));
        final Intent adminSupportIntent = ((DevicePolicyManager)this.mContext.getSystemService("device_policy")).createAdminSupportIntent("policy_disable_screen_capture");
        if (adminSupportIntent != null) {
            setColor.setContentIntent(PendingIntent.getActivityAsUser(this.mContext, 0, adminSupportIntent, 0, (Bundle)null, UserHandle.CURRENT));
        }
        SystemUI.overrideNotificationAppName(this.mContext, setColor, true);
        this.mNotificationManager.notify(1, new Notification$BigTextStyle(setColor).bigText((CharSequence)string).build());
    }
}
