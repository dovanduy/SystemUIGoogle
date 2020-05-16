// 
// Decompiled by Procyon v0.5.36
// 

package vendor.google.wireless_charger.V1_2;

import android.os.HwBlob;
import android.os.HwParcel;
import android.os.IHwBinder$DeathRecipient;
import java.util.Collection;
import java.util.Arrays;
import android.os.HidlSupport;
import android.hidl.base.V1_0.DebugInfo;
import java.util.ArrayList;
import android.os.NativeHandle;
import android.os.HwBinder;
import android.os.RemoteException;
import android.os.IHwBinder;
import android.os.IHwInterface;

public interface IWirelessChargerRtxStatusCallback extends IHwInterface
{
    IHwBinder asBinder();
    
    void rtxStatusInfoChanged(final RtxStatusInfo p0) throws RemoteException;
    
    public abstract static class Stub extends HwBinder implements IWirelessChargerRtxStatusCallback
    {
        public IHwBinder asBinder() {
            return (IHwBinder)this;
        }
        
        public void debug(final NativeHandle nativeHandle, final ArrayList<String> list) {
        }
        
        public final DebugInfo getDebugInfo() {
            final DebugInfo debugInfo = new DebugInfo();
            debugInfo.pid = HidlSupport.getPidIfSharable();
            debugInfo.ptr = 0L;
            debugInfo.arch = 0;
            return debugInfo;
        }
        
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<byte[]>(Arrays.asList(new byte[][] { { 31, -61, -5, 52, -75, -23, 43, -97, -81, -10, -34, -35, 6, -29, -18, -126, 47, 34, 80, -50, 99, 33, 17, -14, -20, 25, -117, -52, -40, 93, -62, -120 }, { -20, 127, -41, -98, -48, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, -13, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76 } }));
        }
        
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<String>(Arrays.asList("vendor.google.wireless_charger@1.2::IWirelessChargerRtxStatusCallback", "android.hidl.base@1.0::IBase"));
        }
        
        public final String interfaceDescriptor() {
            return "vendor.google.wireless_charger@1.2::IWirelessChargerRtxStatusCallback";
        }
        
        public final boolean linkToDeath(final IHwBinder$DeathRecipient hwBinder$DeathRecipient, final long n) {
            return true;
        }
        
        public final void notifySyspropsChanged() {
            HwBinder.enableInstrumentation();
        }
        
        public void onTransact(int i, final HwParcel hwParcel, final HwParcel hwParcel2, int n) throws RemoteException {
            n = 0;
            switch (i) {
                case 257120595: {
                    hwParcel.enforceInterface("android.hidl.base@1.0::IBase");
                    this.notifySyspropsChanged();
                    break;
                }
                case 257049926: {
                    hwParcel.enforceInterface("android.hidl.base@1.0::IBase");
                    final DebugInfo debugInfo = this.getDebugInfo();
                    hwParcel2.writeStatus(0);
                    debugInfo.writeToParcel(hwParcel2);
                    hwParcel2.send();
                    break;
                }
                case 256921159: {
                    hwParcel.enforceInterface("android.hidl.base@1.0::IBase");
                    this.ping();
                    hwParcel2.writeStatus(0);
                    hwParcel2.send();
                    break;
                }
                case 256462420: {
                    hwParcel.enforceInterface("android.hidl.base@1.0::IBase");
                    this.setHALInstrumentation();
                    break;
                }
                case 256398152: {
                    hwParcel.enforceInterface("android.hidl.base@1.0::IBase");
                    final ArrayList<byte[]> hashChain = this.getHashChain();
                    hwParcel2.writeStatus(0);
                    final HwBlob hwBlob = new HwBlob(16);
                    final int size = hashChain.size();
                    hwBlob.putInt32(8L, size);
                    hwBlob.putBool(12L, false);
                    final HwBlob hwBlob2 = new HwBlob(size * 32);
                    long n2;
                    byte[] array;
                    for (i = n; i < size; ++i) {
                        n2 = i * 32;
                        array = hashChain.get(i);
                        if (array == null || array.length != 32) {
                            throw new IllegalArgumentException("Array element is not of the expected length");
                        }
                        hwBlob2.putInt8Array(n2, array);
                    }
                    hwBlob.putBlob(0L, hwBlob2);
                    hwParcel2.writeBuffer(hwBlob);
                    hwParcel2.send();
                    break;
                }
                case 256136003: {
                    hwParcel.enforceInterface("android.hidl.base@1.0::IBase");
                    final String interfaceDescriptor = this.interfaceDescriptor();
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeString(interfaceDescriptor);
                    hwParcel2.send();
                    break;
                }
                case 256131655: {
                    hwParcel.enforceInterface("android.hidl.base@1.0::IBase");
                    this.debug(hwParcel.readNativeHandle(), hwParcel.readStringVector());
                    hwParcel2.writeStatus(0);
                    hwParcel2.send();
                    break;
                }
                case 256067662: {
                    hwParcel.enforceInterface("android.hidl.base@1.0::IBase");
                    final ArrayList<String> interfaceChain = this.interfaceChain();
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeStringVector((ArrayList)interfaceChain);
                    hwParcel2.send();
                    break;
                }
                case 1: {
                    hwParcel.enforceInterface("vendor.google.wireless_charger@1.2::IWirelessChargerRtxStatusCallback");
                    final RtxStatusInfo rtxStatusInfo = new RtxStatusInfo();
                    rtxStatusInfo.readFromParcel(hwParcel);
                    this.rtxStatusInfoChanged(rtxStatusInfo);
                    break;
                }
            }
        }
        
        public final void ping() {
        }
        
        public IHwInterface queryLocalInterface(final String anObject) {
            if ("vendor.google.wireless_charger@1.2::IWirelessChargerRtxStatusCallback".equals(anObject)) {
                return (IHwInterface)this;
            }
            return null;
        }
        
        public final void setHALInstrumentation() {
        }
        
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(this.interfaceDescriptor());
            sb.append("@Stub");
            return sb.toString();
        }
        
        public final boolean unlinkToDeath(final IHwBinder$DeathRecipient hwBinder$DeathRecipient) {
            return true;
        }
    }
}
