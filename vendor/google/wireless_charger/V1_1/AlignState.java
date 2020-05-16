// 
// Decompiled by Procyon v0.5.36
// 

package vendor.google.wireless_charger.V1_1;

public final class AlignState
{
    public static final String toString(final byte x) {
        if (x == 0) {
            return "CHECKING";
        }
        if (x == 1) {
            return "MOVE2CENTER";
        }
        if (x == 2) {
            return "OK";
        }
        if (x == 3) {
            return "ERROR";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("0x");
        sb.append(Integer.toHexString(Byte.toUnsignedInt(x)));
        return sb.toString();
    }
}
