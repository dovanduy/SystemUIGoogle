// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.feedback;

import com.google.android.systemui.columbus.sensors.GestureSensor;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import android.media.AudioAttributes$Builder;
import android.os.Vibrator;
import android.os.VibrationEffect;
import android.media.AudioAttributes;

public final class HapticClick implements FeedbackEffect
{
    private static final AudioAttributes SONIFICATION_AUDIO_ATTRIBUTES;
    private final VibrationEffect progressVibrationEffect;
    private final VibrationEffect resolveVibrationEffect;
    private final Vibrator vibrator;
    
    static {
        SONIFICATION_AUDIO_ATTRIBUTES = new AudioAttributes$Builder().setContentType(4).setUsage(13).build();
    }
    
    public HapticClick(final Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.vibrator = (Vibrator)context.getSystemService("vibrator");
        this.progressVibrationEffect = VibrationEffect.get(0);
        this.resolveVibrationEffect = VibrationEffect.get(5);
    }
    
    @Override
    public void onProgress(final int n, final GestureSensor.DetectionProperties detectionProperties) {
        if (detectionProperties == null || !detectionProperties.isHapticConsumed()) {
            final Vibrator vibrator = this.vibrator;
            if (vibrator != null) {
                if (n == 3) {
                    vibrator.vibrate(this.resolveVibrationEffect, HapticClick.SONIFICATION_AUDIO_ATTRIBUTES);
                }
                else if (n == 1) {
                    vibrator.vibrate(this.progressVibrationEffect, HapticClick.SONIFICATION_AUDIO_ATTRIBUTES);
                }
            }
        }
    }
}
