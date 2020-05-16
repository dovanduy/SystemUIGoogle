// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import android.media.AudioSystem;
import android.util.SparseArray;
import android.content.ComponentName;
import android.os.VibrationEffect;
import android.media.AudioManager;
import android.os.Handler;
import com.android.systemui.plugins.annotations.ProvidesInterface;
import com.android.systemui.plugins.annotations.DependsOn;
import com.android.systemui.plugins.annotations.Dependencies;

@Dependencies({ @DependsOn(target = StreamState.class), @DependsOn(target = State.class), @DependsOn(target = Callbacks.class) })
@ProvidesInterface(version = 1)
public interface VolumeDialogController
{
    public static final int VERSION = 1;
    
    void addCallback(final Callbacks p0, final Handler p1);
    
    boolean areCaptionsEnabled();
    
    AudioManager getAudioManager();
    
    void getCaptionsComponentState(final boolean p0);
    
    void getState();
    
    boolean hasVibrator();
    
    boolean isCaptionStreamOptedOut();
    
    void notifyVisible(final boolean p0);
    
    void removeCallback(final Callbacks p0);
    
    void scheduleTouchFeedback();
    
    void setActiveStream(final int p0);
    
    void setCaptionsEnabled(final boolean p0);
    
    void setRingerMode(final int p0, final boolean p1);
    
    void setStreamVolume(final int p0, final int p1);
    
    void userActivity();
    
    void vibrate(final VibrationEffect p0);
    
    @ProvidesInterface(version = 1)
    public interface Callbacks
    {
        public static final int VERSION = 1;
        
        void onAccessibilityModeChanged(final Boolean p0);
        
        void onCaptionComponentStateChanged(final Boolean p0, final Boolean p1);
        
        void onConfigurationChanged();
        
        void onDismissRequested(final int p0);
        
        void onLayoutDirectionChanged(final int p0);
        
        void onScreenOff();
        
        void onShowRequested(final int p0);
        
        void onShowSafetyWarning(final int p0);
        
        void onShowSilentHint();
        
        void onShowVibrateHint();
        
        void onStateChanged(final State p0);
    }
    
    @ProvidesInterface(version = 1)
    public static final class State
    {
        public static int NO_ACTIVE_STREAM = -1;
        public static final int VERSION = 1;
        public int activeStream;
        public boolean disallowAlarms;
        public boolean disallowMedia;
        public boolean disallowRinger;
        public boolean disallowSystem;
        public ComponentName effectsSuppressor;
        public String effectsSuppressorName;
        public int ringerModeExternal;
        public int ringerModeInternal;
        public final SparseArray<StreamState> states;
        public int zenMode;
        
        public State() {
            this.states = (SparseArray<StreamState>)new SparseArray();
            this.activeStream = State.NO_ACTIVE_STREAM;
        }
        
        private static void sep(final StringBuilder sb, final int n) {
            if (n > 0) {
                sb.append('\n');
                for (int i = 0; i < n; ++i) {
                    sb.append(' ');
                }
            }
            else {
                sb.append(',');
            }
        }
        
        public State copy() {
            final State state = new State();
            for (int i = 0; i < this.states.size(); ++i) {
                state.states.put(this.states.keyAt(i), (Object)((StreamState)this.states.valueAt(i)).copy());
            }
            state.ringerModeExternal = this.ringerModeExternal;
            state.ringerModeInternal = this.ringerModeInternal;
            state.zenMode = this.zenMode;
            final ComponentName effectsSuppressor = this.effectsSuppressor;
            if (effectsSuppressor != null) {
                state.effectsSuppressor = effectsSuppressor.clone();
            }
            state.effectsSuppressorName = this.effectsSuppressorName;
            state.activeStream = this.activeStream;
            state.disallowAlarms = this.disallowAlarms;
            state.disallowMedia = this.disallowMedia;
            state.disallowSystem = this.disallowSystem;
            state.disallowRinger = this.disallowRinger;
            return state;
        }
        
        @Override
        public String toString() {
            return this.toString(0);
        }
        
        public String toString(final int n) {
            final StringBuilder sb = new StringBuilder("{");
            if (n > 0) {
                sep(sb, n);
            }
            for (int i = 0; i < this.states.size(); ++i) {
                if (i > 0) {
                    sep(sb, n);
                }
                final int key = this.states.keyAt(i);
                final StreamState streamState = (StreamState)this.states.valueAt(i);
                sb.append(AudioSystem.streamToString(key));
                sb.append(":");
                sb.append(streamState.level);
                sb.append('[');
                sb.append(streamState.levelMin);
                sb.append("..");
                sb.append(streamState.levelMax);
                sb.append(']');
                if (streamState.muted) {
                    sb.append(" [MUTED]");
                }
                if (streamState.dynamic) {
                    sb.append(" [DYNAMIC]");
                }
            }
            sep(sb, n);
            sb.append("ringerModeExternal:");
            sb.append(this.ringerModeExternal);
            sep(sb, n);
            sb.append("ringerModeInternal:");
            sb.append(this.ringerModeInternal);
            sep(sb, n);
            sb.append("zenMode:");
            sb.append(this.zenMode);
            sep(sb, n);
            sb.append("effectsSuppressor:");
            sb.append(this.effectsSuppressor);
            sep(sb, n);
            sb.append("effectsSuppressorName:");
            sb.append(this.effectsSuppressorName);
            sep(sb, n);
            sb.append("activeStream:");
            sb.append(this.activeStream);
            sep(sb, n);
            sb.append("disallowAlarms:");
            sb.append(this.disallowAlarms);
            sep(sb, n);
            sb.append("disallowMedia:");
            sb.append(this.disallowMedia);
            sep(sb, n);
            sb.append("disallowSystem:");
            sb.append(this.disallowSystem);
            sep(sb, n);
            sb.append("disallowRinger:");
            sb.append(this.disallowRinger);
            if (n > 0) {
                sep(sb, n);
            }
            sb.append('}');
            return sb.toString();
        }
    }
    
    @ProvidesInterface(version = 1)
    public static final class StreamState
    {
        public static final int VERSION = 1;
        public boolean dynamic;
        public int level;
        public int levelMax;
        public int levelMin;
        public boolean muteSupported;
        public boolean muted;
        public int name;
        public String remoteLabel;
        public boolean routedToBluetooth;
        
        public StreamState copy() {
            final StreamState streamState = new StreamState();
            streamState.dynamic = this.dynamic;
            streamState.level = this.level;
            streamState.levelMin = this.levelMin;
            streamState.levelMax = this.levelMax;
            streamState.muted = this.muted;
            streamState.muteSupported = this.muteSupported;
            streamState.name = this.name;
            streamState.remoteLabel = this.remoteLabel;
            streamState.routedToBluetooth = this.routedToBluetooth;
            return streamState;
        }
    }
}
