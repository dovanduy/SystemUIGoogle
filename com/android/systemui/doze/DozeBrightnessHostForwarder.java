// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

public class DozeBrightnessHostForwarder extends Delegate
{
    private final DozeHost mHost;
    
    public DozeBrightnessHostForwarder(final Service service, final DozeHost mHost) {
        super(service);
        this.mHost = mHost;
    }
    
    @Override
    public void setDozeScreenBrightness(final int n) {
        super.setDozeScreenBrightness(n);
        this.mHost.setDozeScreenBrightness(n);
    }
}
