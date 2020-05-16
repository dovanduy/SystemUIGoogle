// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.power;

public final class BatteryStateSnapshot
{
    private final long averageTimeToDischargeMillis;
    private final int batteryLevel;
    private final int batteryStatus;
    private final int bucket;
    private final boolean isBasedOnUsage;
    private boolean isHybrid;
    private final boolean isLowWarningEnabled;
    private final boolean isPowerSaver;
    private final int lowLevelThreshold;
    private final long lowThresholdMillis;
    private final boolean plugged;
    private final int severeLevelThreshold;
    private final long severeThresholdMillis;
    private final long timeRemainingMillis;
    
    public BatteryStateSnapshot(final int n, final boolean b, final boolean b2, final int n2, final int n3, final int n4, final int n5) {
        final long n6 = -1;
        this(n, b, b2, n2, n3, n4, n5, n6, n6, n6, n6, false, true);
        this.isHybrid = false;
    }
    
    public BatteryStateSnapshot(final int batteryLevel, final boolean isPowerSaver, final boolean plugged, final int bucket, final int batteryStatus, final int severeLevelThreshold, final int lowLevelThreshold, final long timeRemainingMillis, final long averageTimeToDischargeMillis, final long severeThresholdMillis, final long lowThresholdMillis, final boolean isBasedOnUsage, final boolean isLowWarningEnabled) {
        this.batteryLevel = batteryLevel;
        this.isPowerSaver = isPowerSaver;
        this.plugged = plugged;
        this.bucket = bucket;
        this.batteryStatus = batteryStatus;
        this.severeLevelThreshold = severeLevelThreshold;
        this.lowLevelThreshold = lowLevelThreshold;
        this.timeRemainingMillis = timeRemainingMillis;
        this.averageTimeToDischargeMillis = averageTimeToDischargeMillis;
        this.severeThresholdMillis = severeThresholdMillis;
        this.lowThresholdMillis = lowThresholdMillis;
        this.isBasedOnUsage = isBasedOnUsage;
        this.isLowWarningEnabled = isLowWarningEnabled;
        this.isHybrid = true;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this != o) {
            if (o instanceof BatteryStateSnapshot) {
                final BatteryStateSnapshot batteryStateSnapshot = (BatteryStateSnapshot)o;
                if (this.batteryLevel == batteryStateSnapshot.batteryLevel && this.isPowerSaver == batteryStateSnapshot.isPowerSaver && this.plugged == batteryStateSnapshot.plugged && this.bucket == batteryStateSnapshot.bucket && this.batteryStatus == batteryStateSnapshot.batteryStatus && this.severeLevelThreshold == batteryStateSnapshot.severeLevelThreshold && this.lowLevelThreshold == batteryStateSnapshot.lowLevelThreshold && this.timeRemainingMillis == batteryStateSnapshot.timeRemainingMillis && this.averageTimeToDischargeMillis == batteryStateSnapshot.averageTimeToDischargeMillis && this.severeThresholdMillis == batteryStateSnapshot.severeThresholdMillis && this.lowThresholdMillis == batteryStateSnapshot.lowThresholdMillis && this.isBasedOnUsage == batteryStateSnapshot.isBasedOnUsage && this.isLowWarningEnabled == batteryStateSnapshot.isLowWarningEnabled) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public final long getAverageTimeToDischargeMillis() {
        return this.averageTimeToDischargeMillis;
    }
    
    public final int getBatteryLevel() {
        return this.batteryLevel;
    }
    
    public final int getBatteryStatus() {
        return this.batteryStatus;
    }
    
    public final int getBucket() {
        return this.bucket;
    }
    
    public final int getLowLevelThreshold() {
        return this.lowLevelThreshold;
    }
    
    public final long getLowThresholdMillis() {
        return this.lowThresholdMillis;
    }
    
    public final boolean getPlugged() {
        return this.plugged;
    }
    
    public final int getSevereLevelThreshold() {
        return this.severeLevelThreshold;
    }
    
    public final long getSevereThresholdMillis() {
        return this.severeThresholdMillis;
    }
    
    public final long getTimeRemainingMillis() {
        return this.timeRemainingMillis;
    }
    
    @Override
    public int hashCode() {
        final int hashCode = Integer.hashCode(this.batteryLevel);
        final int isPowerSaver = this.isPowerSaver ? 1 : 0;
        int n = 1;
        int n2 = isPowerSaver;
        if (isPowerSaver != 0) {
            n2 = 1;
        }
        int plugged;
        if ((plugged = (this.plugged ? 1 : 0)) != 0) {
            plugged = 1;
        }
        final int hashCode2 = Integer.hashCode(this.bucket);
        final int hashCode3 = Integer.hashCode(this.batteryStatus);
        final int hashCode4 = Integer.hashCode(this.severeLevelThreshold);
        final int hashCode5 = Integer.hashCode(this.lowLevelThreshold);
        final int hashCode6 = Long.hashCode(this.timeRemainingMillis);
        final int hashCode7 = Long.hashCode(this.averageTimeToDischargeMillis);
        final int hashCode8 = Long.hashCode(this.severeThresholdMillis);
        final int hashCode9 = Long.hashCode(this.lowThresholdMillis);
        int isBasedOnUsage;
        if ((isBasedOnUsage = (this.isBasedOnUsage ? 1 : 0)) != 0) {
            isBasedOnUsage = 1;
        }
        final int isLowWarningEnabled = this.isLowWarningEnabled ? 1 : 0;
        if (isLowWarningEnabled == 0) {
            n = isLowWarningEnabled;
        }
        return (((((((((((hashCode * 31 + n2) * 31 + plugged) * 31 + hashCode2) * 31 + hashCode3) * 31 + hashCode4) * 31 + hashCode5) * 31 + hashCode6) * 31 + hashCode7) * 31 + hashCode8) * 31 + hashCode9) * 31 + isBasedOnUsage) * 31 + n;
    }
    
    public final boolean isBasedOnUsage() {
        return this.isBasedOnUsage;
    }
    
    public final boolean isHybrid() {
        return this.isHybrid;
    }
    
    public final boolean isLowWarningEnabled() {
        return this.isLowWarningEnabled;
    }
    
    public final boolean isPowerSaver() {
        return this.isPowerSaver;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("BatteryStateSnapshot(batteryLevel=");
        sb.append(this.batteryLevel);
        sb.append(", isPowerSaver=");
        sb.append(this.isPowerSaver);
        sb.append(", plugged=");
        sb.append(this.plugged);
        sb.append(", bucket=");
        sb.append(this.bucket);
        sb.append(", batteryStatus=");
        sb.append(this.batteryStatus);
        sb.append(", severeLevelThreshold=");
        sb.append(this.severeLevelThreshold);
        sb.append(", lowLevelThreshold=");
        sb.append(this.lowLevelThreshold);
        sb.append(", timeRemainingMillis=");
        sb.append(this.timeRemainingMillis);
        sb.append(", averageTimeToDischargeMillis=");
        sb.append(this.averageTimeToDischargeMillis);
        sb.append(", severeThresholdMillis=");
        sb.append(this.severeThresholdMillis);
        sb.append(", lowThresholdMillis=");
        sb.append(this.lowThresholdMillis);
        sb.append(", isBasedOnUsage=");
        sb.append(this.isBasedOnUsage);
        sb.append(", isLowWarningEnabled=");
        sb.append(this.isLowWarningEnabled);
        sb.append(")");
        return sb.toString();
    }
}
