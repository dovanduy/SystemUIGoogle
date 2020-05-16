// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.comparisons;

class ComparisonsKt__ComparisonsKt
{
    public static <T extends Comparable<?>> int compareValues(final T t, final T t2) {
        if (t == t2) {
            return 0;
        }
        if (t == null) {
            return -1;
        }
        if (t2 == null) {
            return 1;
        }
        return t.compareTo(t2);
    }
}
