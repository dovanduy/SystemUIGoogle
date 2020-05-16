// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import android.content.Intent;
import android.util.Log;
import androidx.lifecycle.LifecycleOwner;
import com.android.systemui.qs.QSHost;
import com.android.systemui.screenrecord.RecordingController;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;

public class ScreenRecordTile extends QSTileImpl<BooleanState> implements RecordingStateChangeCallback
{
    private Callback mCallback;
    private RecordingController mController;
    private long mMillisUntilFinished;
    
    public ScreenRecordTile(final QSHost qsHost, final RecordingController mController) {
        super(qsHost);
        this.mMillisUntilFinished = 0L;
        final Callback mCallback = new Callback();
        this.mCallback = mCallback;
        (this.mController = mController).observe(this, (RecordingController.RecordingStateChangeCallback)mCallback);
    }
    
    private void cancelCountdown() {
        Log.d("ScreenRecordTile", "Cancelling countdown");
        this.mController.cancelCountdown();
    }
    
    private void startCountdown() {
        Log.d("ScreenRecordTile", "Starting countdown");
        this.getHost().collapsePanels();
        this.mController.launchRecordPrompt();
    }
    
    private void stopRecording() {
        Log.d("ScreenRecordTile", "Stopping recording from tile");
        this.mController.stopRecording();
    }
    
    @Override
    public Intent getLongClickIntent() {
        return null;
    }
    
    @Override
    public int getMetricsCategory() {
        return 0;
    }
    
    @Override
    public CharSequence getTileLabel() {
        return super.mContext.getString(R$string.quick_settings_screen_record_label);
    }
    
    @Override
    protected void handleClick() {
        if (this.mController.isStarting()) {
            this.cancelCountdown();
        }
        else if (this.mController.isRecording()) {
            this.stopRecording();
        }
        else {
            this.startCountdown();
        }
        this.refreshState();
    }
    
    @Override
    protected void handleUpdateState(final BooleanState booleanState, final Object o) {
        final boolean starting = this.mController.isStarting();
        final boolean recording = this.mController.isRecording();
        booleanState.value = (recording || starting);
        int state;
        if (!recording && !starting) {
            state = 1;
        }
        else {
            state = 2;
        }
        booleanState.state = state;
        if (recording) {
            booleanState.icon = ResourceIcon.get(R$drawable.ic_qs_screenrecord);
            booleanState.secondaryLabel = super.mContext.getString(R$string.quick_settings_screen_record_stop);
        }
        else if (starting) {
            final int i = (int)Math.floorDiv(this.mMillisUntilFinished + 500L, 1000L);
            booleanState.icon = ResourceIcon.get(R$drawable.ic_qs_screenrecord);
            booleanState.secondaryLabel = String.format("%d...", i);
        }
        else {
            booleanState.icon = ResourceIcon.get(R$drawable.ic_qs_screenrecord);
            booleanState.secondaryLabel = super.mContext.getString(R$string.quick_settings_screen_record_start);
        }
    }
    
    @Override
    public BooleanState newTileState() {
        final BooleanState booleanState = new QSTile.BooleanState();
        booleanState.label = super.mContext.getString(R$string.quick_settings_screen_record_label);
        booleanState.handlesLongClick = false;
        return booleanState;
    }
    
    private final class Callback implements RecordingStateChangeCallback
    {
        @Override
        public void onCountdown(final long n) {
            ScreenRecordTile.this.mMillisUntilFinished = n;
            ScreenRecordTile.this.refreshState();
        }
        
        @Override
        public void onCountdownEnd() {
            ScreenRecordTile.this.refreshState();
        }
        
        @Override
        public void onRecordingEnd() {
            ScreenRecordTile.this.refreshState();
        }
        
        @Override
        public void onRecordingStart() {
            ScreenRecordTile.this.refreshState();
        }
    }
}
