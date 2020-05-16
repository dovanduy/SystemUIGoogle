// 
// Decompiled by Procyon v0.5.36
// 

package vendor.google.wireless_charger.V1_2;

import vendor.google.wireless_charger.V1_1.IWirelessChargerInfoCallback;
import vendor.google.wireless_charger.V1_0.KeyExchangeResponse;
import vendor.google.wireless_charger.V1_0.DockInfo;
import android.os.HidlSupport;
import android.os.HwParcel;
import java.util.Objects;
import android.os.IHwBinder$DeathRecipient;
import java.util.ArrayList;
import android.os.HwBinder;
import java.util.Iterator;
import android.os.IHwInterface;
import android.os.RemoteException;
import android.os.IHwBinder;

public interface IWirelessCharger extends vendor.google.wireless_charger.V1_1.IWirelessCharger
{
    default IWirelessCharger asInterface(final IHwBinder hwBinder) {
        if (hwBinder == null) {
            return null;
        }
        final IHwInterface queryLocalInterface = hwBinder.queryLocalInterface("vendor.google.wireless_charger@1.2::IWirelessCharger");
        if (queryLocalInterface != null && queryLocalInterface instanceof IWirelessCharger) {
            return (IWirelessCharger)queryLocalInterface;
        }
        final Proxy proxy = new Proxy(hwBinder);
        try {
            final Iterator<String> iterator = proxy.interfaceChain().iterator();
            while (iterator.hasNext()) {
                if (iterator.next().equals("vendor.google.wireless_charger@1.2::IWirelessCharger")) {
                    return proxy;
                }
            }
            return null;
        }
        catch (RemoteException ex) {
            return null;
        }
    }
    
    default IWirelessCharger getService() throws RemoteException {
        return getService("default");
    }
    
    default IWirelessCharger getService(final String s) throws RemoteException {
        return asInterface(HwBinder.getService("vendor.google.wireless_charger@1.2::IWirelessCharger", s));
    }
    
    ArrayList<String> interfaceChain() throws RemoteException;
    
    boolean isRtxModeOn() throws RemoteException;
    
    boolean isRtxSupported() throws RemoteException;
    
    boolean linkToDeath(final IHwBinder$DeathRecipient p0, final long p1) throws RemoteException;
    
    byte registerRtxCallback(final IWirelessChargerRtxStatusCallback p0) throws RemoteException;
    
    byte setRtxMode(final boolean p0) throws RemoteException;
    
    public static final class Proxy implements IWirelessCharger
    {
        private IHwBinder mRemote;
        
        public Proxy(final IHwBinder obj) {
            Objects.requireNonNull(obj);
            this.mRemote = obj;
        }
        
        public IHwBinder asBinder() {
            return this.mRemote;
        }
        
        @Override
        public void challenge(final byte b, ArrayList<Byte> hwParcel, final challengeCallback challengeCallback) throws RemoteException {
            final HwParcel hwParcel2 = new HwParcel();
            hwParcel2.writeInterfaceToken("vendor.google.wireless_charger@1.0::IWirelessCharger");
            hwParcel2.writeInt8(b);
            hwParcel2.writeInt8Vector((ArrayList)hwParcel);
            hwParcel = new HwParcel();
            try {
                this.mRemote.transact(4, hwParcel2, hwParcel, 0);
                hwParcel.verifySuccess();
                hwParcel2.releaseTemporaryStorage();
                challengeCallback.onValues(hwParcel.readInt8(), hwParcel.readInt8Vector());
            }
            finally {
                hwParcel.release();
            }
        }
        
        @Override
        public final boolean equals(final Object o) {
            return HidlSupport.interfacesEqual((IHwInterface)this, o);
        }
        
        @Override
        public void getInformation(final getInformationCallback getInformationCallback) throws RemoteException {
            final HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("vendor.google.wireless_charger@1.0::IWirelessCharger");
            final HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(2, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                final byte int8 = hwParcel2.readInt8();
                final DockInfo dockInfo = new DockInfo();
                dockInfo.readFromParcel(hwParcel2);
                getInformationCallback.onValues(int8, dockInfo);
            }
            finally {
                hwParcel2.release();
            }
        }
        
        @Override
        public final int hashCode() {
            return this.asBinder().hashCode();
        }
        
        @Override
        public ArrayList<String> interfaceChain() throws RemoteException {
            final HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hidl.base@1.0::IBase");
            final HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(256067662, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return (ArrayList<String>)hwParcel2.readStringVector();
            }
            finally {
                hwParcel2.release();
            }
        }
        
        public String interfaceDescriptor() throws RemoteException {
            final HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hidl.base@1.0::IBase");
            final HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(256136003, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readString();
            }
            finally {
                hwParcel2.release();
            }
        }
        
        @Override
        public void isDockPresent(final isDockPresentCallback isDockPresentCallback) throws RemoteException {
            final HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("vendor.google.wireless_charger@1.0::IWirelessCharger");
            final HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(1, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                isDockPresentCallback.onValues(hwParcel2.readBool(), hwParcel2.readInt8(), hwParcel2.readInt8(), hwParcel2.readBool(), hwParcel2.readInt32());
            }
            finally {
                hwParcel2.release();
            }
        }
        
        @Override
        public boolean isRtxModeOn() throws RemoteException {
            final HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("vendor.google.wireless_charger@1.2::IWirelessCharger");
            final HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(18, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readBool();
            }
            finally {
                hwParcel2.release();
            }
        }
        
        @Override
        public boolean isRtxSupported() throws RemoteException {
            final HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("vendor.google.wireless_charger@1.2::IWirelessCharger");
            final HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(17, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readBool();
            }
            finally {
                hwParcel2.release();
            }
        }
        
        @Override
        public void keyExchange(ArrayList<Byte> hwParcel, final keyExchangeCallback keyExchangeCallback) throws RemoteException {
            final HwParcel hwParcel2 = new HwParcel();
            hwParcel2.writeInterfaceToken("vendor.google.wireless_charger@1.0::IWirelessCharger");
            hwParcel2.writeInt8Vector((ArrayList)hwParcel);
            hwParcel = new HwParcel();
            try {
                this.mRemote.transact(3, hwParcel2, hwParcel, 0);
                hwParcel.verifySuccess();
                hwParcel2.releaseTemporaryStorage();
                final byte int8 = hwParcel.readInt8();
                final KeyExchangeResponse keyExchangeResponse = new KeyExchangeResponse();
                keyExchangeResponse.readFromParcel(hwParcel);
                keyExchangeCallback.onValues(int8, keyExchangeResponse);
            }
            finally {
                hwParcel.release();
            }
        }
        
        @Override
        public boolean linkToDeath(final IHwBinder$DeathRecipient hwBinder$DeathRecipient, final long n) throws RemoteException {
            return this.mRemote.linkToDeath(hwBinder$DeathRecipient, n);
        }
        
        @Override
        public byte registerCallback(IWirelessChargerInfoCallback wirelessChargerInfoCallback) throws RemoteException {
            final HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("vendor.google.wireless_charger@1.1::IWirelessCharger");
            IHwBinder binder;
            if (wirelessChargerInfoCallback == null) {
                binder = null;
            }
            else {
                binder = wirelessChargerInfoCallback.asBinder();
            }
            hwParcel.writeStrongBinder(binder);
            wirelessChargerInfoCallback = (IWirelessChargerInfoCallback)new HwParcel();
            try {
                this.mRemote.transact(12, hwParcel, (HwParcel)wirelessChargerInfoCallback, 0);
                ((HwParcel)wirelessChargerInfoCallback).verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return ((HwParcel)wirelessChargerInfoCallback).readInt8();
            }
            finally {
                ((HwParcel)wirelessChargerInfoCallback).release();
            }
        }
        
        @Override
        public byte registerRtxCallback(IWirelessChargerRtxStatusCallback wirelessChargerRtxStatusCallback) throws RemoteException {
            final HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("vendor.google.wireless_charger@1.2::IWirelessCharger");
            IHwBinder binder;
            if (wirelessChargerRtxStatusCallback == null) {
                binder = null;
            }
            else {
                binder = wirelessChargerRtxStatusCallback.asBinder();
            }
            hwParcel.writeStrongBinder(binder);
            wirelessChargerRtxStatusCallback = (IWirelessChargerRtxStatusCallback)new HwParcel();
            try {
                this.mRemote.transact(15, hwParcel, (HwParcel)wirelessChargerRtxStatusCallback, 0);
                ((HwParcel)wirelessChargerRtxStatusCallback).verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return ((HwParcel)wirelessChargerRtxStatusCallback).readInt8();
            }
            finally {
                ((HwParcel)wirelessChargerRtxStatusCallback).release();
            }
        }
        
        @Override
        public byte setRtxMode(final boolean b) throws RemoteException {
            final HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("vendor.google.wireless_charger@1.2::IWirelessCharger");
            hwParcel.writeBool(b);
            final HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(20, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readInt8();
            }
            finally {
                hwParcel2.release();
            }
        }
        
        @Override
        public String toString() {
            try {
                final StringBuilder sb = new StringBuilder();
                sb.append(this.interfaceDescriptor());
                sb.append("@Proxy");
                return sb.toString();
            }
            catch (RemoteException ex) {
                return "[class or subclass of vendor.google.wireless_charger@1.2::IWirelessCharger]@Proxy";
            }
        }
    }
}
