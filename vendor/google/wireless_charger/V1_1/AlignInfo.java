// 
// Decompiled by Procyon v0.5.36
// 

package vendor.google.wireless_charger.V1_1;

import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Objects;
import android.os.HidlSupport;

public final class AlignInfo
{
    public byte alignPct;
    public byte alignState;
    public int alignX;
    public int alignY;
    
    public AlignInfo() {
        this.alignState = 0;
        this.alignPct = 0;
        this.alignX = 0;
        this.alignY = 0;
    }
    
    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != AlignInfo.class) {
            return false;
        }
        final AlignInfo alignInfo = (AlignInfo)o;
        return this.alignState == alignInfo.alignState && this.alignPct == alignInfo.alignPct && this.alignX == alignInfo.alignX && this.alignY == alignInfo.alignY;
    }
    
    @Override
    public final int hashCode() {
        return Objects.hash(HidlSupport.deepHashCode((Object)this.alignState), HidlSupport.deepHashCode((Object)this.alignPct), HidlSupport.deepHashCode((Object)this.alignX), HidlSupport.deepHashCode((Object)this.alignY));
    }
    
    public final void readEmbeddedFromParcel(final HwParcel hwParcel, final HwBlob hwBlob, final long n) {
        this.alignState = hwBlob.getInt8(0L + n);
        this.alignPct = hwBlob.getInt8(1L + n);
        this.alignX = hwBlob.getInt32(4L + n);
        this.alignY = hwBlob.getInt32(n + 8L);
    }
    
    public final void readFromParcel(final HwParcel hwParcel) {
        this.readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(12L), 0L);
    }
    
    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(".alignState = ");
        sb.append(AlignState.toString(this.alignState));
        sb.append(", .alignPct = ");
        sb.append(this.alignPct);
        sb.append(", .alignX = ");
        sb.append(this.alignX);
        sb.append(", .alignY = ");
        sb.append(this.alignY);
        sb.append("}");
        return sb.toString();
    }
}
