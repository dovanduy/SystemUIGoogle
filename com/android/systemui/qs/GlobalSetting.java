// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.provider.Settings$Global;
import android.os.Handler;
import android.content.Context;
import android.database.ContentObserver;

public abstract class GlobalSetting extends ContentObserver
{
    private final Context mContext;
    private final String mSettingName;
    
    public GlobalSetting(final Context mContext, final Handler handler, final String mSettingName) {
        super(handler);
        this.mContext = mContext;
        this.mSettingName = mSettingName;
    }
    
    public int getValue() {
        return Settings$Global.getInt(this.mContext.getContentResolver(), this.mSettingName, 0);
    }
    
    protected abstract void handleValueChanged(final int p0);
    
    public void onChange(final boolean b) {
        this.handleValueChanged(this.getValue());
    }
    
    public void setListening(final boolean b) {
        if (b) {
            this.mContext.getContentResolver().registerContentObserver(Settings$Global.getUriFor(this.mSettingName), false, (ContentObserver)this);
        }
        else {
            this.mContext.getContentResolver().unregisterContentObserver((ContentObserver)this);
        }
    }
}
