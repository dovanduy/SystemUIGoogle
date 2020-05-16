// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.telephony.SubscriptionInfo;
import android.content.Context;
import android.content.Intent;
import java.util.List;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.net.DataUsageController;
import com.android.systemui.DemoMode;

public interface NetworkController extends CallbackController<SignalCallback>, DemoMode
{
    void addCallback(final SignalCallback p0);
    
    AccessPointController getAccessPointController();
    
    DataSaverController getDataSaverController();
    
    DataUsageController getMobileDataController();
    
    String getMobileDataNetworkName();
    
    int getNumberSubscriptions();
    
    boolean hasEmergencyCryptKeeperText();
    
    boolean hasMobileDataFeature();
    
    boolean hasVoiceCallingFeature();
    
    boolean isRadioOn();
    
    void removeCallback(final SignalCallback p0);
    
    void setWifiEnabled(final boolean p0);
    
    public interface AccessPointController
    {
        void addAccessPointCallback(final AccessPointCallback p0);
        
        boolean canConfigWifi();
        
        boolean connect(final AccessPoint p0);
        
        int getIcon(final AccessPoint p0);
        
        void removeAccessPointCallback(final AccessPointCallback p0);
        
        void scanForAccessPoints();
        
        public interface AccessPointCallback
        {
            void onAccessPointsChanged(final List<AccessPoint> p0);
            
            void onSettingsActivityTriggered(final Intent p0);
        }
    }
    
    public interface EmergencyListener
    {
        void setEmergencyCallsOnly(final boolean p0);
    }
    
    public static class IconState
    {
        public final String contentDescription;
        public final int icon;
        public final boolean visible;
        
        public IconState(final boolean b, final int n, final int n2, final Context context) {
            this(b, n, context.getString(n2));
        }
        
        public IconState(final boolean visible, final int icon, final String contentDescription) {
            this.visible = visible;
            this.icon = icon;
            this.contentDescription = contentDescription;
        }
    }
    
    public interface SignalCallback
    {
        default void setEthernetIndicators(final IconState iconState) {
        }
        
        default void setIsAirplaneMode(final IconState iconState) {
        }
        
        default void setMobileDataEnabled(final boolean b) {
        }
        
        default void setMobileDataIndicators(final IconState iconState, final IconState iconState2, final int n, final int n2, final boolean b, final boolean b2, final CharSequence charSequence, final CharSequence charSequence2, final CharSequence charSequence3, final boolean b3, final int n3, final boolean b4) {
        }
        
        default void setNoSims(final boolean b, final boolean b2) {
        }
        
        default void setSubs(final List<SubscriptionInfo> list) {
        }
        
        default void setWifiIndicators(final boolean b, final IconState iconState, final IconState iconState2, final boolean b2, final boolean b3, final String s, final boolean b4, final String s2) {
        }
    }
}
