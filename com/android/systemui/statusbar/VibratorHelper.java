// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.os.AsyncTask;
import android.content.ContentResolver;
import android.os.VibrationEffect;
import android.provider.Settings$System;
import android.os.Handler;
import android.media.AudioAttributes$Builder;
import android.os.Vibrator;
import android.database.ContentObserver;
import android.content.Context;
import android.media.AudioAttributes;

public class VibratorHelper
{
    private static final AudioAttributes STATUS_BAR_VIBRATION_ATTRIBUTES;
    private final Context mContext;
    private boolean mHapticFeedbackEnabled;
    private final ContentObserver mVibrationObserver;
    private final Vibrator mVibrator;
    
    static {
        STATUS_BAR_VIBRATION_ATTRIBUTES = new AudioAttributes$Builder().setContentType(4).setUsage(13).build();
    }
    
    public VibratorHelper(final Context mContext) {
        this.mVibrationObserver = new ContentObserver(Handler.getMain()) {
            public void onChange(final boolean b) {
                VibratorHelper.this.updateHapticFeedBackEnabled();
            }
        };
        this.mContext = mContext;
        this.mVibrator = (Vibrator)mContext.getSystemService((Class)Vibrator.class);
        this.mContext.getContentResolver().registerContentObserver(Settings$System.getUriFor("haptic_feedback_enabled"), true, this.mVibrationObserver);
        this.mVibrationObserver.onChange(false);
    }
    
    private void updateHapticFeedBackEnabled() {
        final ContentResolver contentResolver = this.mContext.getContentResolver();
        boolean mHapticFeedbackEnabled = false;
        if (Settings$System.getIntForUser(contentResolver, "haptic_feedback_enabled", 0, -2) != 0) {
            mHapticFeedbackEnabled = true;
        }
        this.mHapticFeedbackEnabled = mHapticFeedbackEnabled;
    }
    
    public void vibrate(final int n) {
        if (this.mHapticFeedbackEnabled) {
            AsyncTask.execute((Runnable)new _$$Lambda$VibratorHelper$_aLryVlYLKeF6vrqCqBn9qjn6bQ(this, n));
        }
    }
}
