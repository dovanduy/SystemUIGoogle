// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.accessibility;

import android.database.ContentObserver;
import android.provider.Settings$SettingNotFoundException;
import android.provider.Settings$Secure;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import com.android.systemui.SystemUI;

public class WindowMagnification extends SystemUI
{
    private final Handler mHandler;
    private Configuration mLastConfiguration;
    private WindowMagnificationController mWindowMagnificationController;
    
    public WindowMagnification(final Context context, final Handler mHandler) {
        super(context);
        this.mHandler = mHandler;
        this.mLastConfiguration = new Configuration(context.getResources().getConfiguration());
    }
    
    private void disableMagnification() {
        final WindowMagnificationController mWindowMagnificationController = this.mWindowMagnificationController;
        if (mWindowMagnificationController != null) {
            mWindowMagnificationController.deleteWindowMagnification();
        }
        this.mWindowMagnificationController = null;
    }
    
    private void enableMagnification() {
        if (this.mWindowMagnificationController == null) {
            this.mWindowMagnificationController = new WindowMagnificationController(super.mContext, null);
        }
        this.mWindowMagnificationController.createWindowMagnification();
    }
    
    private void updateWindowMagnification() {
        try {
            if (Settings$Secure.getInt(super.mContext.getContentResolver(), "window_magnification") != 0) {
                this.enableMagnification();
            }
            else {
                this.disableMagnification();
            }
        }
        catch (Settings$SettingNotFoundException ex) {
            this.disableMagnification();
        }
    }
    
    public void onConfigurationChanged(final Configuration to) {
        final int diff = to.diff(this.mLastConfiguration);
        if ((diff & 0x1000) == 0x0) {
            return;
        }
        this.mLastConfiguration.setTo(to);
        final WindowMagnificationController mWindowMagnificationController = this.mWindowMagnificationController;
        if (mWindowMagnificationController != null) {
            mWindowMagnificationController.onConfigurationChanged(diff);
        }
    }
    
    @Override
    public void start() {
        super.mContext.getContentResolver().registerContentObserver(Settings$Secure.getUriFor("window_magnification"), true, (ContentObserver)new ContentObserver(this.mHandler) {
            public void onChange(final boolean b) {
                WindowMagnification.this.updateWindowMagnification();
            }
        });
    }
}
