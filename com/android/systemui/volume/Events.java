// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.volume;

import com.android.systemui.plugins.VolumeDialogController;
import android.util.Log;
import android.media.AudioSystem;
import com.android.internal.logging.UiEventLogger$UiEventEnum;
import java.util.Arrays;
import com.android.internal.logging.UiEventLoggerImpl;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;

public class Events
{
    public static final String[] DISMISS_REASONS;
    private static final String[] EVENT_TAGS;
    public static final String[] SHOW_REASONS;
    private static final String TAG;
    public static Callback sCallback;
    @VisibleForTesting
    static MetricsLogger sLegacyLogger;
    @VisibleForTesting
    static UiEventLogger sUiEventLogger;
    
    static {
        TAG = Util.logTag(Events.class);
        EVENT_TAGS = new String[] { "show_dialog", "dismiss_dialog", "active_stream_changed", "expand", "key", "collection_started", "collection_stopped", "icon_click", "settings_click", "touch_level_changed", "level_changed", "internal_ringer_mode_changed", "external_ringer_mode_changed", "zen_mode_changed", "suppressor_changed", "mute_changed", "touch_level_done", "zen_mode_config_changed", "ringer_toggle", "show_usb_overheat_alarm", "dismiss_usb_overheat_alarm", "odi_captions_click", "odi_captions_tooltip_click" };
        DISMISS_REASONS = new String[] { "unknown", "touch_outside", "volume_controller", "timeout", "screen_off", "settings_clicked", "done_clicked", "a11y_stream_changed", "output_chooser", "usb_temperature_below_threshold" };
        SHOW_REASONS = new String[] { "unknown", "volume_changed", "remote_volume_changed", "usb_temperature_above_threshold" };
        Events.sLegacyLogger = new MetricsLogger();
        Events.sUiEventLogger = (UiEventLogger)new UiEventLoggerImpl();
    }
    
    private static String iconStateToString(final int i) {
        if (i == 1) {
            return "unmute";
        }
        if (i == 2) {
            return "mute";
        }
        if (i != 3) {
            final StringBuilder sb = new StringBuilder();
            sb.append("unknown_state_");
            sb.append(i);
            return sb.toString();
        }
        return "vibrate";
    }
    
    public static String logEvent(final int n, final Object... a) {
        if (n >= Events.EVENT_TAGS.length) {
            return "";
        }
        final StringBuilder sb = new StringBuilder("writeEvent ");
        sb.append(Events.EVENT_TAGS[n]);
        if (a != null && a.length != 0) {
            sb.append(" ");
            switch (n) {
                default: {
                    sb.append(Arrays.asList(a));
                    break;
                }
                case 20: {
                    Events.sLegacyLogger.hidden(1457);
                    Events.sUiEventLogger.log((UiEventLogger$UiEventEnum)VolumeDialogEvent.USB_OVERHEAT_ALARM_DISMISSED);
                    if (a.length > 1) {
                        final Boolean obj = (Boolean)a[1];
                        Events.sLegacyLogger.histogram("dismiss_usb_overheat_alarm", (int)(((boolean)obj) ? 1 : 0));
                        sb.append(Events.DISMISS_REASONS[(int)a[0]]);
                        sb.append(" keyguard=");
                        sb.append(obj);
                        break;
                    }
                    break;
                }
                case 19: {
                    Events.sLegacyLogger.visible(1457);
                    Events.sUiEventLogger.log((UiEventLogger$UiEventEnum)VolumeDialogEvent.USB_OVERHEAT_ALARM);
                    if (a.length > 1) {
                        final Boolean obj2 = (Boolean)a[1];
                        Events.sLegacyLogger.histogram("show_usb_overheat_alarm", (int)(((boolean)obj2) ? 1 : 0));
                        sb.append(Events.SHOW_REASONS[(int)a[0]]);
                        sb.append(" keyguard=");
                        sb.append(obj2);
                        break;
                    }
                    break;
                }
                case 18: {
                    final Integer n2 = (Integer)a[0];
                    Events.sLegacyLogger.action(1385, (int)n2);
                    Events.sUiEventLogger.log((UiEventLogger$UiEventEnum)VolumeDialogEvent.fromRingerMode(n2));
                    sb.append(ringerModeToString(n2));
                    break;
                }
                case 14: {
                    if (a.length > 1) {
                        sb.append(a[0]);
                        sb.append(' ');
                        sb.append(a[1]);
                        break;
                    }
                    break;
                }
                case 13: {
                    final Integer n4 = (Integer)a[0];
                    sb.append(zenModeToString(n4));
                    Events.sUiEventLogger.log((UiEventLogger$UiEventEnum)ZenModeEvent.fromZenMode(n4));
                    break;
                }
                case 12: {
                    Events.sLegacyLogger.action(213, (int)a[0]);
                }
                case 11: {
                    sb.append(ringerModeToString((int)a[0]));
                    break;
                }
                case 16: {
                    if (a.length > 1) {
                        final Integer n3 = (Integer)a[1];
                        Events.sLegacyLogger.action(209, (int)n3);
                        Events.sUiEventLogger.log((UiEventLogger$UiEventEnum)VolumeDialogEvent.fromSliderLevel(n3));
                    }
                }
                case 9:
                case 10:
                case 15: {
                    if (a.length > 1) {
                        sb.append(AudioSystem.streamToString((int)a[0]));
                        sb.append(' ');
                        sb.append(a[1]);
                        break;
                    }
                    break;
                }
                case 7: {
                    if (a.length > 1) {
                        final Integer n5 = (Integer)a[0];
                        Events.sLegacyLogger.action(212, (int)n5);
                        final Integer n6 = (Integer)a[1];
                        Events.sUiEventLogger.log((UiEventLogger$UiEventEnum)VolumeDialogEvent.fromIconState(n6));
                        sb.append(AudioSystem.streamToString((int)n5));
                        sb.append(' ');
                        sb.append(iconStateToString(n6));
                        break;
                    }
                    break;
                }
                case 4: {
                    if (a.length > 1) {
                        final Integer n7 = (Integer)a[0];
                        Events.sLegacyLogger.action(211, (int)n7);
                        final Integer obj3 = (Integer)a[1];
                        Events.sUiEventLogger.log((UiEventLogger$UiEventEnum)VolumeDialogEvent.fromKeyLevel(obj3));
                        sb.append(AudioSystem.streamToString((int)n7));
                        sb.append(' ');
                        sb.append(obj3);
                        break;
                    }
                    break;
                }
                case 3: {
                    final Boolean obj4 = (Boolean)a[0];
                    Events.sLegacyLogger.visibility(208, (boolean)obj4);
                    final UiEventLogger sUiEventLogger = Events.sUiEventLogger;
                    VolumeDialogEvent volumeDialogEvent;
                    if (obj4) {
                        volumeDialogEvent = VolumeDialogEvent.VOLUME_DIALOG_EXPAND_DETAILS;
                    }
                    else {
                        volumeDialogEvent = VolumeDialogEvent.VOLUME_DIALOG_COLLAPSE_DETAILS;
                    }
                    sUiEventLogger.log((UiEventLogger$UiEventEnum)volumeDialogEvent);
                    sb.append(obj4);
                    break;
                }
                case 2: {
                    final Integer n8 = (Integer)a[0];
                    Events.sLegacyLogger.action(210, (int)n8);
                    Events.sUiEventLogger.log((UiEventLogger$UiEventEnum)VolumeDialogEvent.VOLUME_DIALOG_ACTIVE_STREAM_CHANGED);
                    sb.append(AudioSystem.streamToString((int)n8));
                    break;
                }
                case 1: {
                    Events.sLegacyLogger.hidden(207);
                    final Integer n9 = (Integer)a[0];
                    Events.sUiEventLogger.log((UiEventLogger$UiEventEnum)VolumeDialogCloseEvent.fromReason(n9));
                    sb.append(Events.DISMISS_REASONS[n9]);
                    break;
                }
                case 0: {
                    Events.sLegacyLogger.visible(207);
                    if (a.length > 1) {
                        final Integer n10 = (Integer)a[0];
                        final Boolean obj5 = (Boolean)a[1];
                        Events.sLegacyLogger.histogram("volume_from_keyguard", (int)(((boolean)obj5) ? 1 : 0));
                        Events.sUiEventLogger.log((UiEventLogger$UiEventEnum)VolumeDialogOpenEvent.fromReasons(n10));
                        sb.append(Events.SHOW_REASONS[n10]);
                        sb.append(" keyguard=");
                        sb.append(obj5);
                        break;
                    }
                    break;
                }
            }
            return sb.toString();
        }
        if (n == 8) {
            Events.sLegacyLogger.action(1386);
            Events.sUiEventLogger.log((UiEventLogger$UiEventEnum)VolumeDialogEvent.VOLUME_DIALOG_SETTINGS_CLICK);
        }
        return sb.toString();
    }
    
    private static String ringerModeToString(final int n) {
        if (n == 0) {
            return "silent";
        }
        if (n == 1) {
            return "vibrate";
        }
        if (n != 2) {
            return "unknown";
        }
        return "normal";
    }
    
    public static void writeEvent(final int n, final Object... array) {
        final long currentTimeMillis = System.currentTimeMillis();
        Log.i(Events.TAG, logEvent(n, array));
        final Callback sCallback = Events.sCallback;
        if (sCallback != null) {
            sCallback.writeEvent(currentTimeMillis, n, array);
        }
    }
    
    public static void writeState(final long n, final VolumeDialogController.State state) {
        final Callback sCallback = Events.sCallback;
        if (sCallback != null) {
            sCallback.writeState(n, state);
        }
    }
    
    private static String zenModeToString(final int n) {
        if (n == 0) {
            return "off";
        }
        if (n == 1) {
            return "important_interruptions";
        }
        if (n == 2) {
            return "no_interruptions";
        }
        if (n != 3) {
            return "unknown";
        }
        return "alarms";
    }
    
    public interface Callback
    {
        void writeEvent(final long p0, final int p1, final Object[] p2);
        
        void writeState(final long p0, final VolumeDialogController.State p1);
    }
    
    @VisibleForTesting
    public enum VolumeDialogCloseEvent implements UiEventLogger$UiEventEnum
    {
        INVALID(0), 
        VOLUME_DIALOG_DISMISS_SCREEN_OFF(137), 
        VOLUME_DIALOG_DISMISS_SETTINGS(138), 
        VOLUME_DIALOG_DISMISS_STREAM_GONE(140), 
        VOLUME_DIALOG_DISMISS_SYSTEM(135), 
        VOLUME_DIALOG_DISMISS_TIMEOUT(136), 
        VOLUME_DIALOG_DISMISS_TOUCH_OUTSIDE(134), 
        VOLUME_DIALOG_DISMISS_USB_TEMP_ALARM_CHANGED(142);
        
        private final int mId;
        
        private VolumeDialogCloseEvent(final int mId) {
            this.mId = mId;
        }
        
        static VolumeDialogCloseEvent fromReason(final int n) {
            if (n == 1) {
                return VolumeDialogCloseEvent.VOLUME_DIALOG_DISMISS_TOUCH_OUTSIDE;
            }
            if (n == 2) {
                return VolumeDialogCloseEvent.VOLUME_DIALOG_DISMISS_SYSTEM;
            }
            if (n == 3) {
                return VolumeDialogCloseEvent.VOLUME_DIALOG_DISMISS_TIMEOUT;
            }
            if (n == 4) {
                return VolumeDialogCloseEvent.VOLUME_DIALOG_DISMISS_SCREEN_OFF;
            }
            if (n == 5) {
                return VolumeDialogCloseEvent.VOLUME_DIALOG_DISMISS_SETTINGS;
            }
            if (n == 7) {
                return VolumeDialogCloseEvent.VOLUME_DIALOG_DISMISS_STREAM_GONE;
            }
            if (n != 9) {
                return VolumeDialogCloseEvent.INVALID;
            }
            return VolumeDialogCloseEvent.VOLUME_DIALOG_DISMISS_USB_TEMP_ALARM_CHANGED;
        }
        
        public int getId() {
            return this.mId;
        }
    }
    
    @VisibleForTesting
    public enum VolumeDialogEvent implements UiEventLogger$UiEventEnum
    {
        INVALID(0), 
        RINGER_MODE_NORMAL(334), 
        RINGER_MODE_SILENT(154), 
        RINGER_MODE_VIBRATE(155), 
        USB_OVERHEAT_ALARM(160), 
        USB_OVERHEAT_ALARM_DISMISSED(161), 
        VOLUME_DIALOG_ACTIVE_STREAM_CHANGED(146), 
        VOLUME_DIALOG_COLLAPSE_DETAILS(145), 
        VOLUME_DIALOG_EXPAND_DETAILS(144), 
        VOLUME_DIALOG_MUTE_STREAM(147), 
        VOLUME_DIALOG_SETTINGS_CLICK(143), 
        VOLUME_DIALOG_SLIDER(150), 
        VOLUME_DIALOG_SLIDER_TO_ZERO(151), 
        VOLUME_DIALOG_TO_VIBRATE_STREAM(149), 
        VOLUME_DIALOG_UNMUTE_STREAM(148), 
        VOLUME_KEY(153), 
        VOLUME_KEY_TO_ZERO(152);
        
        private final int mId;
        
        private VolumeDialogEvent(final int mId) {
            this.mId = mId;
        }
        
        static VolumeDialogEvent fromIconState(final int n) {
            if (n == 1) {
                return VolumeDialogEvent.VOLUME_DIALOG_UNMUTE_STREAM;
            }
            if (n == 2) {
                return VolumeDialogEvent.VOLUME_DIALOG_MUTE_STREAM;
            }
            if (n != 3) {
                return VolumeDialogEvent.INVALID;
            }
            return VolumeDialogEvent.VOLUME_DIALOG_TO_VIBRATE_STREAM;
        }
        
        static VolumeDialogEvent fromKeyLevel(final int n) {
            VolumeDialogEvent volumeDialogEvent;
            if (n == 0) {
                volumeDialogEvent = VolumeDialogEvent.VOLUME_KEY_TO_ZERO;
            }
            else {
                volumeDialogEvent = VolumeDialogEvent.VOLUME_KEY;
            }
            return volumeDialogEvent;
        }
        
        static VolumeDialogEvent fromRingerMode(final int n) {
            if (n == 0) {
                return VolumeDialogEvent.RINGER_MODE_SILENT;
            }
            if (n == 1) {
                return VolumeDialogEvent.RINGER_MODE_VIBRATE;
            }
            if (n != 2) {
                return VolumeDialogEvent.INVALID;
            }
            return VolumeDialogEvent.RINGER_MODE_NORMAL;
        }
        
        static VolumeDialogEvent fromSliderLevel(final int n) {
            VolumeDialogEvent volumeDialogEvent;
            if (n == 0) {
                volumeDialogEvent = VolumeDialogEvent.VOLUME_DIALOG_SLIDER_TO_ZERO;
            }
            else {
                volumeDialogEvent = VolumeDialogEvent.VOLUME_DIALOG_SLIDER;
            }
            return volumeDialogEvent;
        }
        
        public int getId() {
            return this.mId;
        }
    }
    
    @VisibleForTesting
    public enum VolumeDialogOpenEvent implements UiEventLogger$UiEventEnum
    {
        INVALID(0), 
        VOLUME_DIALOG_SHOW_REMOTE_VOLUME_CHANGED(129), 
        VOLUME_DIALOG_SHOW_USB_TEMP_ALARM_CHANGED(130), 
        VOLUME_DIALOG_SHOW_VOLUME_CHANGED(128);
        
        private final int mId;
        
        private VolumeDialogOpenEvent(final int mId) {
            this.mId = mId;
        }
        
        static VolumeDialogOpenEvent fromReasons(final int n) {
            if (n == 1) {
                return VolumeDialogOpenEvent.VOLUME_DIALOG_SHOW_VOLUME_CHANGED;
            }
            if (n == 2) {
                return VolumeDialogOpenEvent.VOLUME_DIALOG_SHOW_REMOTE_VOLUME_CHANGED;
            }
            if (n != 3) {
                return VolumeDialogOpenEvent.INVALID;
            }
            return VolumeDialogOpenEvent.VOLUME_DIALOG_SHOW_USB_TEMP_ALARM_CHANGED;
        }
        
        public int getId() {
            return this.mId;
        }
    }
    
    @VisibleForTesting
    public enum ZenModeEvent implements UiEventLogger$UiEventEnum
    {
        INVALID(0), 
        ZEN_MODE_ALARMS_ONLY(158), 
        ZEN_MODE_IMPORTANT_ONLY(157), 
        ZEN_MODE_NO_INTERRUPTIONS(159), 
        ZEN_MODE_OFF(335);
        
        private final int mId;
        
        private ZenModeEvent(final int mId) {
            this.mId = mId;
        }
        
        static ZenModeEvent fromZenMode(final int n) {
            if (n == 0) {
                return ZenModeEvent.ZEN_MODE_OFF;
            }
            if (n == 1) {
                return ZenModeEvent.ZEN_MODE_IMPORTANT_ONLY;
            }
            if (n == 2) {
                return ZenModeEvent.ZEN_MODE_NO_INTERRUPTIONS;
            }
            if (n != 3) {
                return ZenModeEvent.INVALID;
            }
            return ZenModeEvent.ZEN_MODE_ALARMS_ONLY;
        }
        
        public int getId() {
            return this.mId;
        }
    }
}
