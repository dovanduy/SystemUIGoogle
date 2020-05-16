// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.smartspace;

import android.util.Log;

public class SmartSpaceData
{
    SmartSpaceCard mCurrentCard;
    SmartSpaceCard mWeatherCard;
    
    public void clear() {
        this.mWeatherCard = null;
        this.mCurrentCard = null;
    }
    
    public SmartSpaceCard getCurrentCard() {
        return this.mCurrentCard;
    }
    
    public long getExpirationRemainingMillis() {
        final long currentTimeMillis = System.currentTimeMillis();
        long n;
        if (this.hasCurrent() && this.hasWeather()) {
            n = Math.min(this.mCurrentCard.getExpiration(), this.mWeatherCard.getExpiration());
        }
        else if (this.hasCurrent()) {
            n = this.mCurrentCard.getExpiration();
        }
        else {
            if (!this.hasWeather()) {
                return 0L;
            }
            n = this.mWeatherCard.getExpiration();
        }
        return n - currentTimeMillis;
    }
    
    public long getExpiresAtMillis() {
        if (this.hasCurrent() && this.hasWeather()) {
            return Math.min(this.mCurrentCard.getExpiration(), this.mWeatherCard.getExpiration());
        }
        if (this.hasCurrent()) {
            return this.mCurrentCard.getExpiration();
        }
        if (this.hasWeather()) {
            return this.mWeatherCard.getExpiration();
        }
        return 0L;
    }
    
    public SmartSpaceCard getWeatherCard() {
        return this.mWeatherCard;
    }
    
    public boolean handleExpire() {
        final boolean hasWeather = this.hasWeather();
        final boolean b = true;
        boolean b2;
        if (hasWeather && this.mWeatherCard.isExpired()) {
            if (SmartSpaceController.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("weather expired ");
                sb.append(this.mWeatherCard.getExpiration());
                Log.d("SmartspaceData", sb.toString());
            }
            this.mWeatherCard = null;
            b2 = true;
        }
        else {
            b2 = false;
        }
        if (this.hasCurrent() && this.mCurrentCard.isExpired()) {
            if (SmartSpaceController.DEBUG) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("current expired ");
                sb2.append(this.mCurrentCard.getExpiration());
                Log.d("SmartspaceData", sb2.toString());
            }
            this.mCurrentCard = null;
            b2 = b;
        }
        return b2;
    }
    
    public boolean hasCurrent() {
        return this.mCurrentCard != null;
    }
    
    public boolean hasWeather() {
        return this.mWeatherCard != null;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(this.mCurrentCard);
        sb.append(",");
        sb.append(this.mWeatherCard);
        sb.append("}");
        return sb.toString();
    }
}
