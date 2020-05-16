// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.settings;

import com.android.internal.logging.MetricsLogger;
import android.view.KeyEvent;
import android.view.Window;
import com.android.systemui.R$id;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.content.Context;
import android.view.LayoutInflater;
import android.os.Bundle;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.app.Activity;

public class BrightnessDialog extends Activity
{
    private BrightnessController mBrightnessController;
    private final BroadcastDispatcher mBroadcastDispatcher;
    
    public BrightnessDialog(final BroadcastDispatcher mBroadcastDispatcher) {
        this.mBroadcastDispatcher = mBroadcastDispatcher;
    }
    
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        final Window window = this.getWindow();
        window.setGravity(48);
        window.clearFlags(2);
        window.requestFeature(1);
        this.setContentView(LayoutInflater.from((Context)this).inflate(R$layout.quick_settings_brightness_dialog, (ViewGroup)null));
        this.mBrightnessController = new BrightnessController((Context)this, (ToggleSlider)this.findViewById(R$id.brightness_slider), this.mBroadcastDispatcher);
    }
    
    public boolean onKeyDown(final int n, final KeyEvent keyEvent) {
        if (n == 25 || n == 24 || n == 164) {
            this.finish();
        }
        return super.onKeyDown(n, keyEvent);
    }
    
    protected void onStart() {
        super.onStart();
        this.mBrightnessController.registerCallbacks();
        MetricsLogger.visible((Context)this, 220);
    }
    
    protected void onStop() {
        super.onStop();
        MetricsLogger.hidden((Context)this, 220);
        this.mBrightnessController.unregisterCallbacks();
    }
}
