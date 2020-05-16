// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

public interface SensorPrivacyController extends CallbackController<OnSensorPrivacyChangedListener>
{
    boolean isSensorPrivacyEnabled();
    
    public interface OnSensorPrivacyChangedListener
    {
        void onSensorPrivacyChanged(final boolean p0);
    }
}
