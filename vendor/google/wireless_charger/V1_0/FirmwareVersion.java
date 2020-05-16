// 
// Decompiled by Procyon v0.5.36
// 

package vendor.google.wireless_charger.V1_0;

import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Objects;
import android.os.HidlSupport;

public final class FirmwareVersion
{
    public String extra;
    public int major;
    public int minor;
    
    public FirmwareVersion() {
        this.major = 0;
        this.minor = 0;
        this.extra = new String();
    }
    
    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != FirmwareVersion.class) {
            return false;
        }
        final FirmwareVersion firmwareVersion = (FirmwareVersion)o;
        return this.major == firmwareVersion.major && this.minor == firmwareVersion.minor && HidlSupport.deepEquals((Object)this.extra, (Object)firmwareVersion.extra);
    }
    
    @Override
    public final int hashCode() {
        return Objects.hash(HidlSupport.deepHashCode((Object)this.major), HidlSupport.deepHashCode((Object)this.minor), HidlSupport.deepHashCode((Object)this.extra));
    }
    
    public final void readEmbeddedFromParcel(final HwParcel hwParcel, final HwBlob hwBlob, long n) {
        this.major = hwBlob.getInt32(n + 0L);
        this.minor = hwBlob.getInt32(4L + n);
        n += 8L;
        final String string = hwBlob.getString(n);
        this.extra = string;
        hwParcel.readEmbeddedBuffer((long)(string.getBytes().length + 1), hwBlob.handle(), n + 0L, false);
    }
    
    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(".major = ");
        sb.append(this.major);
        sb.append(", .minor = ");
        sb.append(this.minor);
        sb.append(", .extra = ");
        sb.append(this.extra);
        sb.append("}");
        return sb.toString();
    }
}
