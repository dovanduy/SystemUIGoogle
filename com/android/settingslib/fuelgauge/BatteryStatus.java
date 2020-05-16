// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.fuelgauge;

import com.android.settingslib.R$integer;
import android.content.Context;
import android.content.Intent;

public class BatteryStatus
{
    public final int health;
    public final int level;
    public final int maxChargingWattage;
    public final int plugged;
    public final int status;
    
    public BatteryStatus(final int status, final int level, final int plugged, final int health, final int maxChargingWattage) {
        this.status = status;
        this.level = level;
        this.plugged = plugged;
        this.health = health;
        this.maxChargingWattage = maxChargingWattage;
    }
    
    public BatteryStatus(final Intent intent) {
        this.status = intent.getIntExtra("status", 1);
        this.plugged = intent.getIntExtra("plugged", 0);
        this.level = intent.getIntExtra("level", 0);
        this.health = intent.getIntExtra("health", 1);
        final int intExtra = intent.getIntExtra("max_charging_current", -1);
        int intExtra2;
        if ((intExtra2 = intent.getIntExtra("max_charging_voltage", -1)) <= 0) {
            intExtra2 = 5000000;
        }
        if (intExtra > 0) {
            this.maxChargingWattage = intExtra / 1000 * (intExtra2 / 1000);
        }
        else {
            this.maxChargingWattage = -1;
        }
    }
    
    public final int getChargingSpeed(final Context context) {
        final int integer = context.getResources().getInteger(R$integer.config_chargingSlowlyThreshold);
        final int integer2 = context.getResources().getInteger(R$integer.config_chargingFastThreshold);
        final int maxChargingWattage = this.maxChargingWattage;
        int n;
        if (maxChargingWattage <= 0) {
            n = -1;
        }
        else if (maxChargingWattage < integer) {
            n = 0;
        }
        else if (maxChargingWattage > integer2) {
            n = 2;
        }
        else {
            n = 1;
        }
        return n;
    }
    
    public boolean isCharged() {
        return this.status == 5 || this.level >= 100;
    }
    
    public boolean isPluggedIn() {
        final int plugged = this.plugged;
        boolean b2;
        final boolean b = b2 = true;
        if (plugged != 1) {
            b2 = b;
            if (plugged != 2) {
                b2 = (plugged == 4 && b);
            }
        }
        return b2;
    }
    
    public boolean isPluggedInWired() {
        final int plugged = this.plugged;
        boolean b = true;
        if (plugged != 1) {
            b = (plugged == 2 && b);
        }
        return b;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("BatteryStatus{status=");
        sb.append(this.status);
        sb.append(",level=");
        sb.append(this.level);
        sb.append(",plugged=");
        sb.append(this.plugged);
        sb.append(",health=");
        sb.append(this.health);
        sb.append(",maxChargingWattage=");
        sb.append(this.maxChargingWattage);
        sb.append("}");
        return sb.toString();
    }
}
