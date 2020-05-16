// 
// Decompiled by Procyon v0.5.36
// 

package vendor.google.wireless_charger.V1_2;

public final class rtxReasonType
{
    public static final String toString(final byte x) {
        if (x == 0) {
            return "NONE";
        }
        if (x == 1) {
            return "BATTLOW";
        }
        if (x == 2) {
            return "OVERHEAT";
        }
        if (x == 15) {
            return "UNKNOWN";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("0x");
        sb.append(Integer.toHexString(Byte.toUnsignedInt(x)));
        return sb.toString();
    }
}
