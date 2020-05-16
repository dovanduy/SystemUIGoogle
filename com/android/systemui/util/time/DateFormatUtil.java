// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.time;

import android.text.format.DateFormat;
import android.app.ActivityManager;
import android.content.Context;

public class DateFormatUtil
{
    private final Context mContext;
    
    public DateFormatUtil(final Context mContext) {
        this.mContext = mContext;
    }
    
    public boolean is24HourFormat() {
        return DateFormat.is24HourFormat(this.mContext, ActivityManager.getCurrentUser());
    }
}
