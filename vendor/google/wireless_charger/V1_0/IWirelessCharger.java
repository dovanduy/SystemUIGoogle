// 
// Decompiled by Procyon v0.5.36
// 

package vendor.google.wireless_charger.V1_0;

import android.os.RemoteException;
import java.util.ArrayList;
import android.os.IHwInterface;

public interface IWirelessCharger extends IHwInterface
{
    void challenge(final byte p0, final ArrayList<Byte> p1, final challengeCallback p2) throws RemoteException;
    
    void getInformation(final getInformationCallback p0) throws RemoteException;
    
    void isDockPresent(final isDockPresentCallback p0) throws RemoteException;
    
    void keyExchange(final ArrayList<Byte> p0, final keyExchangeCallback p1) throws RemoteException;
    
    @FunctionalInterface
    public interface challengeCallback
    {
        void onValues(final byte p0, final ArrayList<Byte> p1);
    }
    
    @FunctionalInterface
    public interface getInformationCallback
    {
        void onValues(final byte p0, final DockInfo p1);
    }
    
    @FunctionalInterface
    public interface isDockPresentCallback
    {
        void onValues(final boolean p0, final byte p1, final byte p2, final boolean p3, final int p4);
    }
    
    @FunctionalInterface
    public interface keyExchangeCallback
    {
        void onValues(final byte p0, final KeyExchangeResponse p1);
    }
}
