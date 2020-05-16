// 
// Decompiled by Procyon v0.5.36
// 

package vendor.google.wireless_charger.V1_2;

import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Objects;
import android.os.HidlSupport;

public final class RtxStatusInfo
{
    public int acctype;
    public boolean chg_s;
    public int iout;
    public int level;
    public byte mode;
    public byte reason;
    public int vout;
    
    public RtxStatusInfo() {
        this.mode = 0;
        this.acctype = 0;
        this.chg_s = false;
        this.vout = 0;
        this.iout = 0;
        this.level = 0;
        this.reason = 0;
    }
    
    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != RtxStatusInfo.class) {
            return false;
        }
        final RtxStatusInfo rtxStatusInfo = (RtxStatusInfo)o;
        return this.mode == rtxStatusInfo.mode && this.acctype == rtxStatusInfo.acctype && this.chg_s == rtxStatusInfo.chg_s && this.vout == rtxStatusInfo.vout && this.iout == rtxStatusInfo.iout && this.level == rtxStatusInfo.level && this.reason == rtxStatusInfo.reason;
    }
    
    @Override
    public final int hashCode() {
        return Objects.hash(HidlSupport.deepHashCode((Object)this.mode), HidlSupport.deepHashCode((Object)this.acctype), HidlSupport.deepHashCode((Object)this.chg_s), HidlSupport.deepHashCode((Object)this.vout), HidlSupport.deepHashCode((Object)this.iout), HidlSupport.deepHashCode((Object)this.level), HidlSupport.deepHashCode((Object)this.reason));
    }
    
    public final void readEmbeddedFromParcel(final HwParcel hwParcel, final HwBlob hwBlob, final long n) {
        this.mode = hwBlob.getInt8(0L + n);
        this.acctype = hwBlob.getInt32(4L + n);
        this.chg_s = hwBlob.getBool(8L + n);
        this.vout = hwBlob.getInt32(12L + n);
        this.iout = hwBlob.getInt32(16L + n);
        this.level = hwBlob.getInt32(20L + n);
        this.reason = hwBlob.getInt8(n + 24L);
    }
    
    public final void readFromParcel(final HwParcel hwParcel) {
        this.readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(28L), 0L);
    }
    
    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(".mode = ");
        sb.append(rtxModeType.toString(this.mode));
        sb.append(", .acctype = ");
        sb.append(this.acctype);
        sb.append(", .chg_s = ");
        sb.append(this.chg_s);
        sb.append(", .vout = ");
        sb.append(this.vout);
        sb.append(", .iout = ");
        sb.append(this.iout);
        sb.append(", .level = ");
        sb.append(this.level);
        sb.append(", .reason = ");
        sb.append(rtxReasonType.toString(this.reason));
        sb.append("}");
        return sb.toString();
    }
}
