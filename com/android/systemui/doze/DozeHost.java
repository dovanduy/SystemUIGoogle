// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

public interface DozeHost
{
    void addCallback(final Callback p0);
    
    void cancelGentleSleep();
    
    void dozeTimeTick();
    
    void extendPulse(final int p0);
    
    boolean isBlockingDoze();
    
    boolean isDozeSuppressed();
    
    boolean isPowerSaveActive();
    
    boolean isProvisioned();
    
    boolean isPulsingBlocked();
    
    void onIgnoreTouchWhilePulsing(final boolean p0);
    
    void onSlpiTap(final float p0, final float p1);
    
    void prepareForGentleSleep(final Runnable p0);
    
    void pulseWhileDozing(final PulseCallback p0, final int p1);
    
    void removeCallback(final Callback p0);
    
    void setAnimateScreenOff(final boolean p0);
    
    void setAnimateWakeup(final boolean p0);
    
    default void setAodDimmingScrim(final float n) {
    }
    
    void setDozeScreenBrightness(final int p0);
    
    void startDozing();
    
    void stopDozing();
    
    public interface Callback
    {
        default void onDozeSuppressedChanged(final boolean b) {
        }
        
        default void onNotificationAlerted(final Runnable runnable) {
        }
        
        default void onPowerSaveChanged(final boolean b) {
        }
    }
    
    public interface PulseCallback
    {
        void onPulseFinished();
        
        void onPulseStarted();
    }
}
