// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import java.util.List;
import com.android.systemui.Dumpable;

public interface CastController extends CallbackController<Callback>, Dumpable
{
    List<CastDevice> getCastDevices();
    
    void setCurrentUserId(final int p0);
    
    void setDiscovering(final boolean p0);
    
    void startCasting(final CastDevice p0);
    
    void stopCasting(final CastDevice p0);
    
    public interface Callback
    {
        void onCastDevicesChanged();
    }
    
    public static final class CastDevice
    {
        public String id;
        public String name;
        public int state;
        public Object tag;
        
        public CastDevice() {
            this.state = 0;
        }
    }
}
