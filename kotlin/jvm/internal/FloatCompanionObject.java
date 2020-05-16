// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.jvm.internal;

public final class FloatCompanionObject
{
    public static final FloatCompanionObject INSTANCE;
    private static final float MAX_VALUE = Float.MAX_VALUE;
    
    static {
        INSTANCE = new FloatCompanionObject();
    }
    
    private FloatCompanionObject() {
    }
    
    public final float getMAX_VALUE() {
        return FloatCompanionObject.MAX_VALUE;
    }
}
