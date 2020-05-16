// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.provider.Settings$Secure;
import android.app.ActivityManager;
import android.os.Handler;
import android.content.Context;
import android.database.ContentObserver;

public abstract class SecureSetting extends ContentObserver
{
    private final Context mContext;
    private boolean mListening;
    private int mObservedValue;
    private final String mSettingName;
    private int mUserId;
    
    public SecureSetting(final Context mContext, final Handler handler, final String mSettingName) {
        super(handler);
        this.mObservedValue = 0;
        this.mContext = mContext;
        this.mSettingName = mSettingName;
        this.mUserId = ActivityManager.getCurrentUser();
    }
    
    public int getValue() {
        return Settings$Secure.getIntForUser(this.mContext.getContentResolver(), this.mSettingName, 0, this.mUserId);
    }
    
    protected abstract void handleValueChanged(final int p0, final boolean p1);
    
    public void onChange(final boolean b) {
        final int value = this.getValue();
        this.handleValueChanged(value, value != this.mObservedValue);
        this.mObservedValue = value;
    }
    
    public void setListening(final boolean mListening) {
        if (mListening == this.mListening) {
            return;
        }
        this.mListening = mListening;
        if (mListening) {
            this.mObservedValue = this.getValue();
            this.mContext.getContentResolver().registerContentObserver(Settings$Secure.getUriFor(this.mSettingName), false, (ContentObserver)this, this.mUserId);
        }
        else {
            this.mContext.getContentResolver().unregisterContentObserver((ContentObserver)this);
            this.mObservedValue = 0;
        }
    }
    
    public void setUserId(final int mUserId) {
        this.mUserId = mUserId;
        if (this.mListening) {
            this.setListening(false);
            this.setListening(true);
        }
    }
    
    public void setValue(final int n) {
        Settings$Secure.putIntForUser(this.mContext.getContentResolver(), this.mSettingName, n, this.mUserId);
    }
}
