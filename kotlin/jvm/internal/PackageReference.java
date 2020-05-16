// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.jvm.internal;

public final class PackageReference implements ClassBasedDeclarationContainer
{
    private final Class<?> jClass;
    
    public PackageReference(final Class<?> jClass, final String s) {
        Intrinsics.checkParameterIsNotNull(jClass, "jClass");
        Intrinsics.checkParameterIsNotNull(s, "moduleName");
        this.jClass = jClass;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof PackageReference && Intrinsics.areEqual(this.getJClass(), ((PackageReference)o).getJClass());
    }
    
    @Override
    public Class<?> getJClass() {
        return this.jClass;
    }
    
    @Override
    public int hashCode() {
        return this.getJClass().hashCode();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getJClass().toString());
        sb.append(" (Kotlin reflection is not available)");
        return sb.toString();
    }
}
