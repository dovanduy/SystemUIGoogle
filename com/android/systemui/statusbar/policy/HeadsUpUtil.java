// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.view.View;
import com.android.systemui.R$id;

public final class HeadsUpUtil
{
    private static final int TAG_CLICKED_NOTIFICATION;
    
    static {
        TAG_CLICKED_NOTIFICATION = R$id.is_clicked_heads_up_tag;
    }
    
    public static boolean isClickedHeadsUpNotification(final View view) {
        final Boolean b = (Boolean)view.getTag(HeadsUpUtil.TAG_CLICKED_NOTIFICATION);
        return b != null && b;
    }
    
    public static void setIsClickedHeadsUpNotification(final View view, final boolean b) {
        final int tag_CLICKED_NOTIFICATION = HeadsUpUtil.TAG_CLICKED_NOTIFICATION;
        Boolean true;
        if (b) {
            true = Boolean.TRUE;
        }
        else {
            true = null;
        }
        view.setTag(tag_CLICKED_NOTIFICATION, (Object)true);
    }
}
