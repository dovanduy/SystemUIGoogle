// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.jvm.internal;

import java.io.Serializable;

public final class Ref$ObjectRef<T> implements Serializable
{
    public T element;
    
    @Override
    public String toString() {
        return String.valueOf(this.element);
    }
}
