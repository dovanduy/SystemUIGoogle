// 
// Decompiled by Procyon v0.5.36
// 

package vendor.google.wireless_charger.V1_0;

import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Objects;
import android.os.HidlSupport;
import java.util.ArrayList;

public final class KeyExchangeResponse
{
    public byte dockId;
    public ArrayList<Byte> dockPublicKey;
    
    public KeyExchangeResponse() {
        this.dockId = 0;
        this.dockPublicKey = new ArrayList<Byte>();
    }
    
    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != KeyExchangeResponse.class) {
            return false;
        }
        final KeyExchangeResponse keyExchangeResponse = (KeyExchangeResponse)o;
        return this.dockId == keyExchangeResponse.dockId && HidlSupport.deepEquals((Object)this.dockPublicKey, (Object)keyExchangeResponse.dockPublicKey);
    }
    
    @Override
    public final int hashCode() {
        return Objects.hash(HidlSupport.deepHashCode((Object)this.dockId), HidlSupport.deepHashCode((Object)this.dockPublicKey));
    }
    
    public final void readEmbeddedFromParcel(final HwParcel hwParcel, final HwBlob hwBlob, long n) {
        this.dockId = hwBlob.getInt8(n + 0L);
        n += 8L;
        final int int32 = hwBlob.getInt32(8L + n);
        final HwBlob embeddedBuffer = hwParcel.readEmbeddedBuffer((long)(int32 * 1), hwBlob.handle(), n + 0L, true);
        this.dockPublicKey.clear();
        for (int i = 0; i < int32; ++i) {
            this.dockPublicKey.add(embeddedBuffer.getInt8((long)(i * 1)));
        }
    }
    
    public final void readFromParcel(final HwParcel hwParcel) {
        this.readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(24L), 0L);
    }
    
    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(".dockId = ");
        sb.append(this.dockId);
        sb.append(", .dockPublicKey = ");
        sb.append(this.dockPublicKey);
        sb.append("}");
        return sb.toString();
    }
}
