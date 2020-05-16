// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.app.PendingIntent$CanceledException;
import android.util.Log;
import android.content.Intent;
import android.app.PendingIntent;
import android.content.Context;

public class ColorChangeHandler implements ConfigInfoListener
{
    private final Context mContext;
    private boolean mIsDark;
    private PendingIntent mPendingIntent;
    
    ColorChangeHandler(final Context mContext) {
        this.mContext = mContext;
    }
    
    private void sendColor() {
        if (this.mPendingIntent == null) {
            return;
        }
        final Intent intent = new Intent();
        intent.putExtra("is_dark", this.mIsDark);
        try {
            this.mPendingIntent.send(this.mContext, 0, intent);
        }
        catch (PendingIntent$CanceledException ex) {
            Log.w("ColorChangeHandler", "SysUI assist UI color changed PendingIntent canceled");
        }
    }
    
    void onColorChange(final boolean mIsDark) {
        this.mIsDark = mIsDark;
        this.sendColor();
    }
    
    @Override
    public void onConfigInfo(final ConfigInfo configInfo) {
        this.mPendingIntent = configInfo.onColorChanged;
        this.sendColor();
    }
}
