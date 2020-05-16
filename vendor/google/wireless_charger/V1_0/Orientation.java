// 
// Decompiled by Procyon v0.5.36
// 

package vendor.google.wireless_charger.V1_0;

public final class Orientation
{
    public static final String toString(final byte x) {
        if (x == 0) {
            return "ARBITRARY";
        }
        if (x == 1) {
            return "LANDSCAPE";
        }
        if (x == 2) {
            return "PORTRAIT";
        }
        if (x == 3) {
            return "BOTH";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("0x");
        sb.append(Integer.toHexString(Byte.toUnsignedInt(x)));
        return sb.toString();
    }
}
