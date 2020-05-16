// 
// Decompiled by Procyon v0.5.36
// 

package vendor.google.wireless_charger.V1_0;

public final class DockType
{
    public static final String toString(final byte x) {
        if (x == 0) {
            return "DESKTOP_DOCK";
        }
        if (x == 1) {
            return "DESKTOP_PAD";
        }
        if (x == 2) {
            return "AUTOMOBILE_DOCK";
        }
        if (x == 3) {
            return "AUTOMOBILE_PAD";
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
