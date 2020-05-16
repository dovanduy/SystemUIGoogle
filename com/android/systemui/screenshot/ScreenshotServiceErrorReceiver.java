// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.screenshot;

import com.android.systemui.R$string;
import android.view.WindowManager;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;

public class ScreenshotServiceErrorReceiver extends BroadcastReceiver
{
    public void onReceive(final Context context, final Intent intent) {
        new ScreenshotNotificationsController(context, (WindowManager)context.getSystemService("window")).notifyScreenshotError(R$string.screenshot_failed_to_save_unknown_text);
    }
}
