// 
// Decompiled by Procyon v0.5.36
// 

package vendor.google.wireless_charger.V1_2;

public final class rtxModeType
{
    public static final String toString(final byte x) {
        if (x == 0) {
            return "DISABLED";
        }
        if (x == 1) {
            return "ACTIVE";
        }
        if (x == 2) {
            return "AVAILABLE";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("0x");
        sb.append(Integer.toHexString(Byte.toUnsignedInt(x)));
        return sb.toString();
    }
}
