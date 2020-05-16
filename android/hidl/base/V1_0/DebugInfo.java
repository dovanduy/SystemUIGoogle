// 
// Decompiled by Procyon v0.5.36
// 

package android.hidl.base.V1_0;

import android.os.HwParcel;
import android.os.HwBlob;
import java.util.Objects;
import android.os.HidlSupport;

public final class DebugInfo
{
    public int arch;
    public int pid;
    public long ptr;
    
    public DebugInfo() {
        this.pid = 0;
        this.ptr = 0L;
        this.arch = 0;
    }
    
    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != DebugInfo.class) {
            return false;
        }
        final DebugInfo debugInfo = (DebugInfo)o;
        return this.pid == debugInfo.pid && this.ptr == debugInfo.ptr && this.arch == debugInfo.arch;
    }
    
    @Override
    public final int hashCode() {
        return Objects.hash(HidlSupport.deepHashCode((Object)this.pid), HidlSupport.deepHashCode((Object)this.ptr), HidlSupport.deepHashCode((Object)this.arch));
    }
    
    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(".pid = ");
        sb.append(this.pid);
        sb.append(", .ptr = ");
        sb.append(this.ptr);
        sb.append(", .arch = ");
        sb.append(Architecture.toString(this.arch));
        sb.append("}");
        return sb.toString();
    }
    
    public final void writeEmbeddedToBlob(final HwBlob hwBlob, final long n) {
        hwBlob.putInt32(0L + n, this.pid);
        hwBlob.putInt64(8L + n, this.ptr);
        hwBlob.putInt32(n + 16L, this.arch);
    }
    
    public final void writeToParcel(final HwParcel hwParcel) {
        final HwBlob hwBlob = new HwBlob(24);
        this.writeEmbeddedToBlob(hwBlob, 0L);
        hwParcel.writeBuffer(hwBlob);
    }
    
    public static final class Architecture
    {
        public static final String toString(final int i) {
            if (i == 0) {
                return "UNKNOWN";
            }
            if (i == 1) {
                return "IS_64BIT";
            }
            if (i == 2) {
                return "IS_32BIT";
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("0x");
            sb.append(Integer.toHexString(i));
            return sb.toString();
        }
    }
}
