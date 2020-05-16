// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice;

public class SliceSpecs
{
    public static final SliceSpec BASIC;
    public static final SliceSpec LIST;
    public static final SliceSpec LIST_V2;
    
    static {
        BASIC = new SliceSpec("androidx.slice.BASIC", 1);
        LIST = new SliceSpec("androidx.slice.LIST", 1);
        LIST_V2 = new SliceSpec("androidx.slice.LIST", 2);
    }
}
