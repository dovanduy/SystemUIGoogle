// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.screenrecord;

import android.view.Window;
import android.view.View$OnClickListener;
import com.android.systemui.R$id;
import android.widget.Button;
import com.android.systemui.R$layout;
import android.os.Bundle;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.view.View;
import android.widget.Switch;
import android.app.Activity;

public class ScreenRecordDialog extends Activity
{
    private Switch mAudioSwitch;
    private final RecordingController mController;
    private Switch mTapsSwitch;
    
    public ScreenRecordDialog(final RecordingController mController) {
        this.mController = mController;
    }
    
    private void requestScreenCapture() {
        this.mController.startCountdown(3000L, 1000L, PendingIntent.getForegroundService((Context)this, 2, RecordingService.getStartIntent((Context)this, -1, null, this.mAudioSwitch.isChecked(), this.mTapsSwitch.isChecked()), 134217728), PendingIntent.getService((Context)this, 2, RecordingService.getStopIntent((Context)this), 134217728));
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        final Window window = this.getWindow();
        window.getDecorView();
        window.setLayout(-1, -2);
        window.setGravity(48);
        this.setContentView(R$layout.screen_record_dialog);
        ((Button)this.findViewById(R$id.button_cancel)).setOnClickListener((View$OnClickListener)new _$$Lambda$ScreenRecordDialog$UwuybAZfzEbq_KArO9WeoPnEStk(this));
        ((Button)this.findViewById(R$id.button_start)).setOnClickListener((View$OnClickListener)new _$$Lambda$ScreenRecordDialog$PtlgQ6bdLH8Q6JnpPzk4xxbDTtg(this));
        this.mAudioSwitch = (Switch)this.findViewById(R$id.screenrecord_audio_switch);
        this.mTapsSwitch = (Switch)this.findViewById(R$id.screenrecord_taps_switch);
    }
}
