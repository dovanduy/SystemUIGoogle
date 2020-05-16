// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

import android.content.res.Resources;
import com.android.systemui.R$array;
import android.util.Log;
import android.provider.Settings$Global;
import android.os.Handler;
import android.net.Uri;
import android.database.ContentObserver;
import android.util.KeyValueListParser;
import android.content.Context;

public class AlwaysOnDisplayPolicy
{
    public int[] dimmingScrimArray;
    private final Context mContext;
    private final KeyValueListParser mParser;
    private SettingsObserver mSettingsObserver;
    public long proxCooldownPeriodMs;
    public long proxCooldownTriggerMs;
    public long proxScreenOffDelayMs;
    public int[] screenBrightnessArray;
    public long wallpaperFadeOutDuration;
    public long wallpaperVisibilityDuration;
    
    public AlwaysOnDisplayPolicy(Context applicationContext) {
        applicationContext = applicationContext.getApplicationContext();
        this.mContext = applicationContext;
        this.mParser = new KeyValueListParser(',');
        (this.mSettingsObserver = new SettingsObserver(applicationContext.getMainThreadHandler())).observe();
    }
    
    private final class SettingsObserver extends ContentObserver
    {
        private final Uri ALWAYS_ON_DISPLAY_CONSTANTS_URI;
        
        SettingsObserver(final Handler handler) {
            super(handler);
            this.ALWAYS_ON_DISPLAY_CONSTANTS_URI = Settings$Global.getUriFor("always_on_display_constants");
        }
        
        void observe() {
            AlwaysOnDisplayPolicy.this.mContext.getContentResolver().registerContentObserver(this.ALWAYS_ON_DISPLAY_CONSTANTS_URI, false, (ContentObserver)this, -1);
            this.update(null);
        }
        
        public void onChange(final boolean b, final Uri uri) {
            this.update(uri);
        }
        
        public void update(Uri resources) {
            if (resources == null || this.ALWAYS_ON_DISPLAY_CONSTANTS_URI.equals((Object)resources)) {
                resources = (Uri)AlwaysOnDisplayPolicy.this.mContext.getResources();
                final String string = Settings$Global.getString(AlwaysOnDisplayPolicy.this.mContext.getContentResolver(), "always_on_display_constants");
                try {
                    AlwaysOnDisplayPolicy.this.mParser.setString(string);
                }
                catch (IllegalArgumentException ex) {
                    Log.e("AlwaysOnDisplayPolicy", "Bad AOD constants");
                }
                final AlwaysOnDisplayPolicy this$0 = AlwaysOnDisplayPolicy.this;
                this$0.proxScreenOffDelayMs = this$0.mParser.getLong("prox_screen_off_delay", 10000L);
                final AlwaysOnDisplayPolicy this$2 = AlwaysOnDisplayPolicy.this;
                this$2.proxCooldownTriggerMs = this$2.mParser.getLong("prox_cooldown_trigger", 2000L);
                final AlwaysOnDisplayPolicy this$3 = AlwaysOnDisplayPolicy.this;
                this$3.proxCooldownPeriodMs = this$3.mParser.getLong("prox_cooldown_period", 5000L);
                final AlwaysOnDisplayPolicy this$4 = AlwaysOnDisplayPolicy.this;
                this$4.wallpaperFadeOutDuration = this$4.mParser.getLong("wallpaper_fade_out_duration", 400L);
                final AlwaysOnDisplayPolicy this$5 = AlwaysOnDisplayPolicy.this;
                this$5.wallpaperVisibilityDuration = this$5.mParser.getLong("wallpaper_visibility_timeout", 60000L);
                final AlwaysOnDisplayPolicy this$6 = AlwaysOnDisplayPolicy.this;
                this$6.screenBrightnessArray = this$6.mParser.getIntArray("screen_brightness_array", ((Resources)resources).getIntArray(R$array.config_doze_brightness_sensor_to_brightness));
                final AlwaysOnDisplayPolicy this$7 = AlwaysOnDisplayPolicy.this;
                this$7.dimmingScrimArray = this$7.mParser.getIntArray("dimming_scrim_array", ((Resources)resources).getIntArray(R$array.config_doze_brightness_sensor_to_scrim_opacity));
            }
        }
    }
}
