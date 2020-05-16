// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.volume;

import java.util.Objects;
import android.widget.TextView;
import android.media.session.PlaybackState;
import android.media.session.MediaController$PlaybackInfo;
import android.media.MediaMetadata;
import android.telephony.TelephonyManager;
import android.content.Context;

public class Util
{
    private static final int[] AUDIO_MANAGER_FLAGS;
    private static final String[] AUDIO_MANAGER_FLAG_NAMES;
    
    static {
        AUDIO_MANAGER_FLAGS = new int[] { 1, 16, 4, 2, 8, 2048, 128, 4096, 1024 };
        AUDIO_MANAGER_FLAG_NAMES = new String[] { "SHOW_UI", "VIBRATE", "PLAY_SOUND", "ALLOW_RINGER_MODES", "REMOVE_SOUND_AND_VIBRATE", "SHOW_VIBRATE_HINT", "SHOW_SILENT_HINT", "FROM_KEY", "SHOW_UI_WARNINGS" };
    }
    
    public static String audioManagerFlagsToString(final int n) {
        return bitFieldToString(n, Util.AUDIO_MANAGER_FLAGS, Util.AUDIO_MANAGER_FLAG_NAMES);
    }
    
    protected static String bitFieldToString(int i, final int[] array, final String[] array2) {
        if (i == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (int j = 0; j < array.length; ++j) {
            if ((array[j] & i) != 0x0) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(array2[j]);
            }
            i &= array[j];
        }
        if (i != 0) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append("UNKNOWN_");
            sb.append(i);
        }
        return sb.toString();
    }
    
    private static CharSequence emptyToNull(final CharSequence charSequence) {
        if (charSequence != null) {
            final CharSequence charSequence2 = charSequence;
            if (charSequence.length() != 0) {
                return charSequence2;
            }
        }
        return null;
    }
    
    public static boolean isVoiceCapable(final Context context) {
        final TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService("phone");
        return telephonyManager != null && telephonyManager.isVoiceCapable();
    }
    
    public static String logTag(final Class<?> clazz) {
        final StringBuilder sb = new StringBuilder();
        sb.append("vol.");
        sb.append(clazz.getSimpleName());
        String s = sb.toString();
        if (s.length() >= 23) {
            s = s.substring(0, 23);
        }
        return s;
    }
    
    public static String mediaMetadataToString(final MediaMetadata mediaMetadata) {
        if (mediaMetadata == null) {
            return null;
        }
        return mediaMetadata.getDescription().toString();
    }
    
    public static String playbackInfoToString(final MediaController$PlaybackInfo mediaController$PlaybackInfo) {
        if (mediaController$PlaybackInfo == null) {
            return null;
        }
        return String.format("PlaybackInfo[vol=%s,max=%s,type=%s,vc=%s],atts=%s", mediaController$PlaybackInfo.getCurrentVolume(), mediaController$PlaybackInfo.getMaxVolume(), playbackInfoTypeToString(mediaController$PlaybackInfo.getPlaybackType()), volumeProviderControlToString(mediaController$PlaybackInfo.getVolumeControl()), mediaController$PlaybackInfo.getAudioAttributes());
    }
    
    public static String playbackInfoTypeToString(final int i) {
        if (i == 1) {
            return "LOCAL";
        }
        if (i != 2) {
            final StringBuilder sb = new StringBuilder();
            sb.append("UNKNOWN_");
            sb.append(i);
            return sb.toString();
        }
        return "REMOTE";
    }
    
    public static String playbackStateStateToString(final int i) {
        if (i == 0) {
            return "STATE_NONE";
        }
        if (i == 1) {
            return "STATE_STOPPED";
        }
        if (i == 2) {
            return "STATE_PAUSED";
        }
        if (i != 3) {
            final StringBuilder sb = new StringBuilder();
            sb.append("UNKNOWN_");
            sb.append(i);
            return sb.toString();
        }
        return "STATE_PLAYING";
    }
    
    public static String playbackStateToString(final PlaybackState obj) {
        if (obj == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(playbackStateStateToString(obj.getState()));
        sb.append(" ");
        sb.append(obj);
        return sb.toString();
    }
    
    public static boolean setText(final TextView textView, final CharSequence text) {
        if (Objects.equals(emptyToNull(textView.getText()), emptyToNull(text))) {
            return false;
        }
        textView.setText(text);
        return true;
    }
    
    public static String volumeProviderControlToString(final int i) {
        if (i == 0) {
            return "VOLUME_CONTROL_FIXED";
        }
        if (i == 1) {
            return "VOLUME_CONTROL_RELATIVE";
        }
        if (i != 2) {
            final StringBuilder sb = new StringBuilder();
            sb.append("VOLUME_CONTROL_UNKNOWN_");
            sb.append(i);
            return sb.toString();
        }
        return "VOLUME_CONTROL_ABSOLUTE";
    }
}
