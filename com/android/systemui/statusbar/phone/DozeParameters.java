// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.R$integer;
import com.android.systemui.R$bool;
import android.util.MathUtils;
import android.os.SystemProperties;
import android.content.res.Resources;
import android.os.PowerManager;
import android.hardware.display.AmbientDisplayConfiguration;
import com.android.systemui.doze.AlwaysOnDisplayPolicy;
import com.android.systemui.tuner.TunerService;

public class DozeParameters implements Tunable, com.android.systemui.plugins.statusbar.DozeParameters
{
    public static final boolean FORCE_BLANKING;
    public static final boolean FORCE_NO_BLANKING;
    private final AlwaysOnDisplayPolicy mAlwaysOnPolicy;
    private final AmbientDisplayConfiguration mAmbientDisplayConfiguration;
    private boolean mControlScreenOffAnimation;
    private boolean mDozeAlwaysOn;
    private final PowerManager mPowerManager;
    private final Resources mResources;
    
    static {
        FORCE_NO_BLANKING = SystemProperties.getBoolean("debug.force_no_blanking", false);
        FORCE_BLANKING = SystemProperties.getBoolean("debug.force_blanking", false);
    }
    
    protected DozeParameters(final Resources mResources, final AmbientDisplayConfiguration mAmbientDisplayConfiguration, final AlwaysOnDisplayPolicy mAlwaysOnPolicy, final PowerManager mPowerManager, final TunerService tunerService) {
        this.mResources = mResources;
        this.mAmbientDisplayConfiguration = mAmbientDisplayConfiguration;
        this.mAlwaysOnPolicy = mAlwaysOnPolicy;
        final boolean mControlScreenOffAnimation = this.getDisplayNeedsBlanking() ^ true;
        this.mControlScreenOffAnimation = mControlScreenOffAnimation;
        (this.mPowerManager = mPowerManager).setDozeAfterScreenOff(mControlScreenOffAnimation ^ true);
        tunerService.addTunable((TunerService.Tunable)this, "doze_always_on", "accessibility_display_inversion_enabled");
    }
    
    private boolean getBoolean(final String s, final int n) {
        return SystemProperties.getBoolean(s, this.mResources.getBoolean(n));
    }
    
    private int getInt(final String s, final int n) {
        return MathUtils.constrain(SystemProperties.getInt(s, this.mResources.getInteger(n)), 0, 60000);
    }
    
    public boolean doubleTapReportsTouchCoordinates() {
        return this.mResources.getBoolean(R$bool.doze_double_tap_reports_touch_coordinates);
    }
    
    public boolean getAlwaysOn() {
        return this.mDozeAlwaysOn;
    }
    
    public boolean getDisplayNeedsBlanking() {
        return DozeParameters.FORCE_BLANKING || (!DozeParameters.FORCE_NO_BLANKING && this.mResources.getBoolean(17891415));
    }
    
    public boolean getDisplayStateSupported() {
        return this.getBoolean("doze.display.supported", R$bool.doze_display_state_supported);
    }
    
    public boolean getDozeSuspendDisplayStateSupported() {
        return this.mResources.getBoolean(R$bool.doze_suspend_display_state_supported);
    }
    
    public int getPickupVibrationThreshold() {
        return this.getInt("doze.pickup.vibration.threshold", R$integer.doze_pickup_vibration_threshold);
    }
    
    public AlwaysOnDisplayPolicy getPolicy() {
        return this.mAlwaysOnPolicy;
    }
    
    public boolean getProxCheckBeforePulse() {
        return this.getBoolean("doze.pulse.proxcheck", R$bool.doze_proximity_check_before_pulse);
    }
    
    public boolean getPulseOnSigMotion() {
        return this.getBoolean("doze.pulse.sigmotion", R$bool.doze_pulse_on_significant_motion);
    }
    
    public int getPulseVisibleDuration() {
        return this.getInt("doze.pulse.duration.visible", R$integer.doze_pulse_duration_visible);
    }
    
    public int getPulseVisibleDurationExtended() {
        return this.getPulseVisibleDuration() * 2;
    }
    
    public float getScreenBrightnessDoze() {
        return this.mResources.getInteger(17694884) / 255.0f;
    }
    
    public long getWallpaperAodDuration() {
        if (this.shouldControlScreenOff()) {
            return 2500L;
        }
        return this.mAlwaysOnPolicy.wallpaperVisibilityDuration;
    }
    
    public long getWallpaperFadeOutDuration() {
        return this.mAlwaysOnPolicy.wallpaperFadeOutDuration;
    }
    
    @Override
    public void onTuningChanged(final String s, final String s2) {
        this.mDozeAlwaysOn = this.mAmbientDisplayConfiguration.alwaysOnEnabled(-2);
    }
    
    public void setControlScreenOffAnimation(final boolean mControlScreenOffAnimation) {
        if (this.mControlScreenOffAnimation == mControlScreenOffAnimation) {
            return;
        }
        this.mControlScreenOffAnimation = mControlScreenOffAnimation;
        this.mPowerManager.setDozeAfterScreenOff(mControlScreenOffAnimation ^ true);
    }
    
    @Override
    public boolean shouldControlScreenOff() {
        return this.mControlScreenOffAnimation;
    }
}
