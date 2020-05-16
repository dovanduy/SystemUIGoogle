// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.jvm.internal;

import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;

public final class ClassReference implements KClass<Object>, ClassBasedDeclarationContainer
{
    private final Class<?> jClass;
    
    public ClassReference(final Class<?> jClass) {
        Intrinsics.checkParameterIsNotNull(jClass, "jClass");
        this.jClass = jClass;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof ClassReference && Intrinsics.areEqual(JvmClassMappingKt.getJavaObjectType((KClass<Object>)this), JvmClassMappingKt.getJavaObjectType((KClass<Object>)o));
    }
    
    @Override
    public Class<?> getJClass() {
        return this.jClass;
    }
    
    @Override
    public int hashCode() {
        return JvmClassMappingKt.getJavaObjectType((KClass<Object>)this).hashCode();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getJClass().toString());
        sb.append(" (Kotlin reflection is not available)");
        return sb.toString();
    }
}
