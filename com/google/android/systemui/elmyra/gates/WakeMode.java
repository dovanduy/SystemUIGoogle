// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.gates;

import android.content.ContentResolver;
import android.net.Uri;
import java.util.function.Consumer;
import android.provider.Settings$Secure;
import android.content.Context;
import com.google.android.systemui.elmyra.UserContentObserver;

public class WakeMode extends PowerState
{
    private final UserContentObserver mSettingsObserver;
    private boolean mWakeSettingEnabled;
    
    public WakeMode(final Context context) {
        super(context);
        this.mSettingsObserver = new UserContentObserver(this.getContext(), Settings$Secure.getUriFor("assist_gesture_wake_enabled"), new _$$Lambda$WakeMode$lV6uvTzrddoc5zkk3T9UO9wzYhA(this), false);
    }
    
    private boolean isWakeSettingEnabled() {
        final ContentResolver contentResolver = this.getContext().getContentResolver();
        boolean b = true;
        if (Settings$Secure.getIntForUser(contentResolver, "assist_gesture_wake_enabled", 1, -2) == 0) {
            b = false;
        }
        return b;
    }
    
    private void updateWakeSetting() {
        final boolean wakeSettingEnabled = this.isWakeSettingEnabled();
        if (wakeSettingEnabled != this.mWakeSettingEnabled) {
            this.mWakeSettingEnabled = wakeSettingEnabled;
            this.notifyListener();
        }
    }
    
    @Override
    protected boolean isBlocked() {
        return !this.mWakeSettingEnabled && super.isBlocked();
    }
    
    @Override
    protected void onActivate() {
        this.mWakeSettingEnabled = this.isWakeSettingEnabled();
        this.mSettingsObserver.activate();
    }
    
    @Override
    protected void onDeactivate() {
        this.mSettingsObserver.deactivate();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [mWakeSettingEnabled -> ");
        sb.append(this.mWakeSettingEnabled);
        sb.append("]");
        return sb.toString();
    }
}
