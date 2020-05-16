// 
// Decompiled by Procyon v0.5.36
// 

package vendor.google.wireless_charger.V1_1;

import android.os.RemoteException;

public interface IWirelessCharger extends vendor.google.wireless_charger.V1_0.IWirelessCharger
{
    byte registerCallback(final IWirelessChargerInfoCallback p0) throws RemoteException;
}
