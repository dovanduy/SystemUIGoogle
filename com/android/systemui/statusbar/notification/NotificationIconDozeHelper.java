// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import com.android.systemui.R$integer;
import android.content.Context;

public class NotificationIconDozeHelper extends NotificationDozeHelper
{
    public NotificationIconDozeHelper(final Context context) {
        context.getResources().getInteger(R$integer.doze_small_icon_alpha);
    }
    
    public void setColor(final int n) {
    }
}
