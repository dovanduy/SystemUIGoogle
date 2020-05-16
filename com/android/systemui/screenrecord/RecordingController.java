// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.screenrecord;

import android.app.PendingIntent$CanceledException;
import android.content.Intent;
import android.content.ComponentName;
import java.util.Iterator;
import android.util.Log;
import android.app.PendingIntent;
import java.util.ArrayList;
import android.os.CountDownTimer;
import android.content.Context;
import com.android.systemui.statusbar.policy.CallbackController;

public class RecordingController implements CallbackController<RecordingStateChangeCallback>
{
    private final Context mContext;
    private CountDownTimer mCountDownTimer;
    private boolean mIsRecording;
    private boolean mIsStarting;
    private ArrayList<RecordingStateChangeCallback> mListeners;
    private PendingIntent mStopIntent;
    
    public RecordingController(final Context mContext) {
        this.mCountDownTimer = null;
        this.mListeners = new ArrayList<RecordingStateChangeCallback>();
        this.mContext = mContext;
    }
    
    @Override
    public void addCallback(final RecordingStateChangeCallback e) {
        this.mListeners.add(e);
    }
    
    public void cancelCountdown() {
        final CountDownTimer mCountDownTimer = this.mCountDownTimer;
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        else {
            Log.e("RecordingController", "Timer was null");
        }
        this.mIsStarting = false;
        final Iterator<RecordingStateChangeCallback> iterator = this.mListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onCountdownEnd();
        }
    }
    
    public boolean isRecording() {
        return this.mIsRecording;
    }
    
    public boolean isStarting() {
        return this.mIsStarting;
    }
    
    public void launchRecordPrompt() {
        final ComponentName component = new ComponentName("com.android.systemui", "com.android.systemui.screenrecord.ScreenRecordDialog");
        final Intent intent = new Intent();
        intent.setComponent(component);
        intent.setFlags(268435456);
        this.mContext.startActivity(intent);
    }
    
    @Override
    public void removeCallback(final RecordingStateChangeCallback o) {
        this.mListeners.remove(o);
    }
    
    public void startCountdown(final long n, final long n2, final PendingIntent pendingIntent, final PendingIntent mStopIntent) {
        this.mIsStarting = true;
        this.mStopIntent = mStopIntent;
        (this.mCountDownTimer = new CountDownTimer(n, n2) {
            public void onFinish() {
                RecordingController.this.mIsStarting = false;
                RecordingController.this.mIsRecording = true;
                final Iterator<RecordingStateChangeCallback> iterator = RecordingController.this.mListeners.iterator();
                while (iterator.hasNext()) {
                    iterator.next().onCountdownEnd();
                }
                try {
                    pendingIntent.send();
                    Log.d("RecordingController", "sent start intent");
                }
                catch (PendingIntent$CanceledException ex) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Pending intent was cancelled: ");
                    sb.append(ex.getMessage());
                    Log.e("RecordingController", sb.toString());
                }
            }
            
            public void onTick(final long n) {
                final Iterator<RecordingStateChangeCallback> iterator = RecordingController.this.mListeners.iterator();
                while (iterator.hasNext()) {
                    iterator.next().onCountdown(n);
                }
            }
        }).start();
    }
    
    public void stopRecording() {
        try {
            this.mStopIntent.send();
            this.updateState(false);
        }
        catch (PendingIntent$CanceledException ex) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Error stopping: ");
            sb.append(ex.getMessage());
            Log.e("RecordingController", sb.toString());
        }
    }
    
    public void updateState(final boolean mIsRecording) {
        this.mIsRecording = mIsRecording;
        for (final RecordingStateChangeCallback recordingStateChangeCallback : this.mListeners) {
            if (mIsRecording) {
                recordingStateChangeCallback.onRecordingStart();
            }
            else {
                recordingStateChangeCallback.onRecordingEnd();
            }
        }
    }
    
    public interface RecordingStateChangeCallback
    {
        default void onCountdown(final long n) {
        }
        
        default void onCountdownEnd() {
        }
        
        default void onRecordingEnd() {
        }
        
        default void onRecordingStart() {
        }
    }
}
