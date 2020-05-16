// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import com.google.android.systemui.columbus.sensors.GestureSensor;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.content.Intent;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import android.os.Handler;
import android.content.Context;
import android.media.AudioManager;

public final class ManageMedia extends Action
{
    private final AudioManager audioManager;
    
    public ManageMedia(final Context context, final Handler handler) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(handler, "handler");
        final AudioManager audioManager = null;
        super(context, null);
        Object systemService = context.getSystemService("audio");
        if (!(systemService instanceof AudioManager)) {
            systemService = audioManager;
        }
        this.audioManager = (AudioManager)systemService;
    }
    
    private final void sendPlayPauseKeyEvent(final int n) {
        this.getContext().sendOrderedBroadcast(new Intent("android.intent.action.MEDIA_BUTTON").putExtra("android.intent.extra.KEY_EVENT", (Parcelable)new KeyEvent(n, 85)), (String)null);
    }
    
    @Override
    public boolean isAvailable() {
        return this.audioManager != null;
    }
    
    @Override
    public void onProgress(final int n, final GestureSensor.DetectionProperties detectionProperties) {
        if (n == 3) {
            this.onTrigger();
        }
    }
    
    @Override
    public void onTrigger() {
        if (this.audioManager != null) {
            this.sendPlayPauseKeyEvent(0);
            this.sendPlayPauseKeyEvent(1);
        }
    }
}
