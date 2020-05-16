// 
// Decompiled by Procyon v0.5.36
// 

package vendor.google.wireless_charger.V1_0;

import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Objects;
import android.os.HidlSupport;

public final class DockInfo
{
    public boolean isGetInfoSupported;
    public String manufacturer;
    public int maxFwSize;
    public String model;
    public byte orientation;
    public String serial;
    public byte type;
    public FirmwareVersion version;
    
    public DockInfo() {
        this.manufacturer = new String();
        this.model = new String();
        this.serial = new String();
        this.maxFwSize = 0;
        this.isGetInfoSupported = false;
        this.version = new FirmwareVersion();
        this.orientation = 0;
        this.type = 0;
    }
    
    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != DockInfo.class) {
            return false;
        }
        final DockInfo dockInfo = (DockInfo)o;
        return HidlSupport.deepEquals((Object)this.manufacturer, (Object)dockInfo.manufacturer) && HidlSupport.deepEquals((Object)this.model, (Object)dockInfo.model) && HidlSupport.deepEquals((Object)this.serial, (Object)dockInfo.serial) && this.maxFwSize == dockInfo.maxFwSize && this.isGetInfoSupported == dockInfo.isGetInfoSupported && HidlSupport.deepEquals((Object)this.version, (Object)dockInfo.version) && this.orientation == dockInfo.orientation && this.type == dockInfo.type;
    }
    
    @Override
    public final int hashCode() {
        return Objects.hash(HidlSupport.deepHashCode((Object)this.manufacturer), HidlSupport.deepHashCode((Object)this.model), HidlSupport.deepHashCode((Object)this.serial), HidlSupport.deepHashCode((Object)this.maxFwSize), HidlSupport.deepHashCode((Object)this.isGetInfoSupported), HidlSupport.deepHashCode((Object)this.version), HidlSupport.deepHashCode((Object)this.orientation), HidlSupport.deepHashCode((Object)this.type));
    }
    
    public final void readEmbeddedFromParcel(final HwParcel hwParcel, final HwBlob hwBlob, final long n) {
        final long n2 = n + 0L;
        final String string = hwBlob.getString(n2);
        this.manufacturer = string;
        hwParcel.readEmbeddedBuffer((long)(string.getBytes().length + 1), hwBlob.handle(), n2 + 0L, false);
        final long n3 = n + 16L;
        final String string2 = hwBlob.getString(n3);
        this.model = string2;
        hwParcel.readEmbeddedBuffer((long)(string2.getBytes().length + 1), hwBlob.handle(), n3 + 0L, false);
        final long n4 = n + 32L;
        final String string3 = hwBlob.getString(n4);
        this.serial = string3;
        hwParcel.readEmbeddedBuffer((long)(string3.getBytes().length + 1), hwBlob.handle(), n4 + 0L, false);
        this.maxFwSize = hwBlob.getInt32(n + 48L);
        this.isGetInfoSupported = hwBlob.getBool(n + 52L);
        this.version.readEmbeddedFromParcel(hwParcel, hwBlob, n + 56L);
        this.orientation = hwBlob.getInt8(n + 80L);
        this.type = hwBlob.getInt8(n + 81L);
    }
    
    public final void readFromParcel(final HwParcel hwParcel) {
        this.readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(88L), 0L);
    }
    
    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(".manufacturer = ");
        sb.append(this.manufacturer);
        sb.append(", .model = ");
        sb.append(this.model);
        sb.append(", .serial = ");
        sb.append(this.serial);
        sb.append(", .maxFwSize = ");
        sb.append(this.maxFwSize);
        sb.append(", .isGetInfoSupported = ");
        sb.append(this.isGetInfoSupported);
        sb.append(", .version = ");
        sb.append(this.version);
        sb.append(", .orientation = ");
        sb.append(Orientation.toString(this.orientation));
        sb.append(", .type = ");
        sb.append(DockType.toString(this.type));
        sb.append("}");
        return sb.toString();
    }
}
