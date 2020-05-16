// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.fuelgauge;

import android.util.Slog;
import android.util.KeyValueListParser;
import android.content.ContentResolver;
import android.provider.Settings$Global;
import android.os.PowerManager;
import android.provider.Settings$Secure;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class BatterySaverUtils
{
    private static Intent getSystemUiBroadcast(final String s, final Bundle bundle) {
        final Intent intent = new Intent(s);
        intent.setFlags(268435456);
        intent.setPackage("com.android.systemui");
        intent.putExtras(bundle);
        return intent;
    }
    
    public static boolean maybeShowBatterySaverConfirmation(final Context context, final Bundle bundle) {
        if (Settings$Secure.getInt(context.getContentResolver(), "low_power_warning_acknowledged", 0) != 0) {
            return false;
        }
        context.sendBroadcast(getSystemUiBroadcast("PNW.startSaverConfirmation", bundle));
        return true;
    }
    
    private static void setBatterySaverConfirmationAcknowledged(final Context context) {
        Settings$Secure.putIntForUser(context.getContentResolver(), "low_power_warning_acknowledged", 1, -2);
    }
    
    public static boolean setPowerSaveMode(final Context batterySaverConfirmationAcknowledged, final boolean powerSaveModeEnabled, final boolean b) {
        synchronized (BatterySaverUtils.class) {
            final ContentResolver contentResolver = batterySaverConfirmationAcknowledged.getContentResolver();
            final Bundle bundle = new Bundle(1);
            bundle.putBoolean("extra_confirm_only", false);
            if (powerSaveModeEnabled && b && maybeShowBatterySaverConfirmation(batterySaverConfirmationAcknowledged, bundle)) {
                return false;
            }
            if (powerSaveModeEnabled && !b) {
                setBatterySaverConfirmationAcknowledged(batterySaverConfirmationAcknowledged);
            }
            if (((PowerManager)batterySaverConfirmationAcknowledged.getSystemService((Class)PowerManager.class)).setPowerSaveModeEnabled(powerSaveModeEnabled)) {
                if (powerSaveModeEnabled) {
                    final int n = Settings$Secure.getInt(contentResolver, "low_power_manual_activation_count", 0) + 1;
                    Settings$Secure.putInt(contentResolver, "low_power_manual_activation_count", n);
                    final Parameters parameters = new Parameters(batterySaverConfirmationAcknowledged);
                    if (n >= parameters.startNth && n <= parameters.endNth && Settings$Global.getInt(contentResolver, "low_power_trigger_level", 0) == 0 && Settings$Secure.getInt(contentResolver, "suppress_auto_battery_saver_suggestion", 0) == 0) {
                        showAutoBatterySaverSuggestion(batterySaverConfirmationAcknowledged, bundle);
                    }
                }
                return true;
            }
            return false;
        }
    }
    
    private static void showAutoBatterySaverSuggestion(final Context context, final Bundle bundle) {
        context.sendBroadcast(getSystemUiBroadcast("PNW.autoSaverSuggestion", bundle));
    }
    
    public static void suppressAutoBatterySaver(final Context context) {
        Settings$Secure.putInt(context.getContentResolver(), "suppress_auto_battery_saver_suggestion", 1);
    }
    
    private static class Parameters
    {
        public final int endNth;
        private final Context mContext;
        public final int startNth;
        
        public Parameters(Context string) {
            this.mContext = string;
            string = (Context)Settings$Global.getString(string.getContentResolver(), "low_power_mode_suggestion_params");
            final KeyValueListParser keyValueListParser = new KeyValueListParser(',');
            try {
                keyValueListParser.setString((String)string);
            }
            catch (IllegalArgumentException ex) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Bad constants: ");
                sb.append((String)string);
                Slog.wtf("BatterySaverUtils", sb.toString());
            }
            this.startNth = keyValueListParser.getInt("start_nth", 4);
            this.endNth = keyValueListParser.getInt("end_nth", 8);
        }
    }
}
