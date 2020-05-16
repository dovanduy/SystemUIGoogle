// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.media;

import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.media.MediaMetadata;
import android.media.session.MediaController$TransportControls;
import android.view.View$OnTouchListener;
import android.widget.SeekBar$OnSeekBarChangeListener;
import androidx.lifecycle.LiveData;
import kotlin.jvm.internal.Intrinsics;
import android.media.session.PlaybackState;
import android.media.session.MediaController;
import com.android.systemui.util.concurrency.DelayableExecutor;
import androidx.lifecycle.MutableLiveData;

public final class SeekBarViewModel
{
    private final MutableLiveData<Progress> _progress;
    private final DelayableExecutor bgExecutor;
    private MediaController controller;
    private boolean listening;
    private PlaybackState playbackState;
    
    public SeekBarViewModel(final DelayableExecutor bgExecutor) {
        Intrinsics.checkParameterIsNotNull(bgExecutor, "bgExecutor");
        this.bgExecutor = bgExecutor;
        final MutableLiveData<Progress> progress = new MutableLiveData<Progress>();
        progress.postValue(new Progress(false, false, null, null, null));
        this._progress = progress;
        this.listening = true;
    }
    
    private final Runnable checkPlaybackPosition() {
        final Runnable executeDelayed = this.bgExecutor.executeDelayed((Runnable)new SeekBarViewModel$checkPlaybackPosition.SeekBarViewModel$checkPlaybackPosition$1(this), 100L);
        Intrinsics.checkExpressionValueIsNotNull(executeDelayed, "bgExecutor.executeDelaye\u2026N_UPDATE_INTERVAL_MILLIS)");
        return executeDelayed;
    }
    
    private final boolean shouldPollPlaybackPosition() {
        final PlaybackState playbackState = this.playbackState;
        Integer value;
        if (playbackState != null) {
            value = playbackState.getState();
        }
        else {
            value = null;
        }
        boolean b = true;
        if (value == null || (value != 3 && value != 6 && value != 4 && value != 5) || !this.listening) {
            b = false;
        }
        return b;
    }
    
    public final LiveData<Progress> getProgress() {
        return this._progress;
    }
    
    public final SeekBar$OnSeekBarChangeListener getSeekBarListener() {
        return (SeekBar$OnSeekBarChangeListener)new SeekBarChangeListener(this, this.bgExecutor);
    }
    
    public final View$OnTouchListener getSeekBarTouchListener() {
        return (View$OnTouchListener)new SeekBarTouchListener();
    }
    
    public final void onSeek(final long n) {
        final MediaController controller = this.controller;
        if (controller != null) {
            final MediaController$TransportControls transportControls = controller.getTransportControls();
            if (transportControls != null) {
                transportControls.seekTo(n);
            }
        }
    }
    
    public final void setListening(final boolean b) {
        if (b) {
            this.checkPlaybackPosition();
        }
    }
    
    public final void updateController(MediaController controller, final int i) {
        this.controller = controller;
        Integer value = null;
        PlaybackState playbackState;
        if (controller != null) {
            playbackState = controller.getPlaybackState();
        }
        else {
            playbackState = null;
        }
        this.playbackState = playbackState;
        controller = this.controller;
        MediaMetadata metadata;
        if (controller != null) {
            metadata = controller.getMetadata();
        }
        else {
            metadata = null;
        }
        final PlaybackState playbackState2 = this.playbackState;
        long actions;
        if (playbackState2 != null) {
            actions = playbackState2.getActions();
        }
        else {
            actions = 0L;
        }
        final boolean b = (actions & 0x100L) != 0x0L;
        final PlaybackState playbackState3 = this.playbackState;
        Integer value2;
        if (playbackState3 != null) {
            value2 = (int)playbackState3.getPosition();
        }
        else {
            value2 = null;
        }
        if (metadata != null) {
            value = (int)metadata.getLong("android.media.metadata.DURATION");
        }
        this._progress.postValue(new Progress(value == null || value > 0, b, value2, value, i));
        if (this.shouldPollPlaybackPosition()) {
            this.checkPlaybackPosition();
        }
    }
    
    public static final class Progress
    {
        private final Integer color;
        private final Integer duration;
        private final Integer elapsedTime;
        private final boolean enabled;
        private final boolean seekAvailable;
        
        public Progress(final boolean enabled, final boolean seekAvailable, final Integer elapsedTime, final Integer duration, final Integer color) {
            this.enabled = enabled;
            this.seekAvailable = seekAvailable;
            this.elapsedTime = elapsedTime;
            this.duration = duration;
            this.color = color;
        }
        
        public final Progress copy(final boolean b, final boolean b2, final Integer n, final Integer n2, final Integer n3) {
            return new Progress(b, b2, n, n2, n3);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this != o) {
                if (o instanceof Progress) {
                    final Progress progress = (Progress)o;
                    if (this.enabled == progress.enabled && this.seekAvailable == progress.seekAvailable && Intrinsics.areEqual(this.elapsedTime, progress.elapsedTime) && Intrinsics.areEqual(this.duration, progress.duration) && Intrinsics.areEqual(this.color, progress.color)) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        }
        
        public final Integer getColor() {
            return this.color;
        }
        
        public final Integer getDuration() {
            return this.duration;
        }
        
        public final Integer getElapsedTime() {
            return this.elapsedTime;
        }
        
        public final boolean getEnabled() {
            return this.enabled;
        }
        
        public final boolean getSeekAvailable() {
            return this.seekAvailable;
        }
        
        @Override
        public int hashCode() {
            final int enabled = this.enabled ? 1 : 0;
            int n = 1;
            int n2 = enabled;
            if (enabled != 0) {
                n2 = 1;
            }
            final int seekAvailable = this.seekAvailable ? 1 : 0;
            if (seekAvailable == 0) {
                n = seekAvailable;
            }
            final Integer elapsedTime = this.elapsedTime;
            int hashCode = 0;
            int hashCode2;
            if (elapsedTime != null) {
                hashCode2 = elapsedTime.hashCode();
            }
            else {
                hashCode2 = 0;
            }
            final Integer duration = this.duration;
            int hashCode3;
            if (duration != null) {
                hashCode3 = duration.hashCode();
            }
            else {
                hashCode3 = 0;
            }
            final Integer color = this.color;
            if (color != null) {
                hashCode = color.hashCode();
            }
            return (((n2 * 31 + n) * 31 + hashCode2) * 31 + hashCode3) * 31 + hashCode;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("Progress(enabled=");
            sb.append(this.enabled);
            sb.append(", seekAvailable=");
            sb.append(this.seekAvailable);
            sb.append(", elapsedTime=");
            sb.append(this.elapsedTime);
            sb.append(", duration=");
            sb.append(this.duration);
            sb.append(", color=");
            sb.append(this.color);
            sb.append(")");
            return sb.toString();
        }
    }
    
    private static final class SeekBarChangeListener implements SeekBar$OnSeekBarChangeListener
    {
        private final DelayableExecutor bgExecutor;
        private final SeekBarViewModel viewModel;
        
        public SeekBarChangeListener(final SeekBarViewModel viewModel, final DelayableExecutor bgExecutor) {
            Intrinsics.checkParameterIsNotNull(viewModel, "viewModel");
            Intrinsics.checkParameterIsNotNull(bgExecutor, "bgExecutor");
            this.viewModel = viewModel;
            this.bgExecutor = bgExecutor;
        }
        
        public final SeekBarViewModel getViewModel() {
            return this.viewModel;
        }
        
        public void onProgressChanged(final SeekBar seekBar, final int n, final boolean b) {
            Intrinsics.checkParameterIsNotNull(seekBar, "bar");
            if (b) {
                this.bgExecutor.execute((Runnable)new SeekBarViewModel$SeekBarChangeListener$onProgressChanged.SeekBarViewModel$SeekBarChangeListener$onProgressChanged$1(this, n));
            }
        }
        
        public void onStartTrackingTouch(final SeekBar seekBar) {
            Intrinsics.checkParameterIsNotNull(seekBar, "bar");
        }
        
        public void onStopTrackingTouch(final SeekBar seekBar) {
            Intrinsics.checkParameterIsNotNull(seekBar, "bar");
            this.bgExecutor.execute((Runnable)new SeekBarViewModel$SeekBarChangeListener$onStopTrackingTouch.SeekBarViewModel$SeekBarChangeListener$onStopTrackingTouch$1(this, (long)seekBar.getProgress()));
        }
    }
    
    private static final class SeekBarTouchListener implements View$OnTouchListener
    {
        public SeekBarTouchListener() {
        }
        
        public boolean onTouch(final View view, final MotionEvent motionEvent) {
            Intrinsics.checkParameterIsNotNull(view, "view");
            Intrinsics.checkParameterIsNotNull(motionEvent, "event");
            view.getParent().requestDisallowInterceptTouchEvent(true);
            return view.onTouchEvent(motionEvent);
        }
    }
}
