// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.coroutines.jvm.internal;

import kotlin.jvm.internal.Intrinsics;
import java.lang.reflect.Method;

final class ModuleNameRetriever
{
    public static final ModuleNameRetriever INSTANCE;
    public static Cache cache;
    private static final Cache notOnJava9;
    
    static {
        INSTANCE = new ModuleNameRetriever();
        notOnJava9 = new Cache(null, null, null);
    }
    
    private ModuleNameRetriever() {
    }
    
    private final Cache buildCache(final BaseContinuationImpl baseContinuationImpl) {
        try {
            return ModuleNameRetriever.cache = new Cache(Class.class.getDeclaredMethod("getModule", (Class<?>[])new Class[0]), baseContinuationImpl.getClass().getClassLoader().loadClass("java.lang.Module").getDeclaredMethod("getDescriptor", (Class<?>[])new Class[0]), baseContinuationImpl.getClass().getClassLoader().loadClass("java.lang.module.ModuleDescriptor").getDeclaredMethod("name", (Class<?>[])new Class[0]));
        }
        catch (Exception ex) {
            return ModuleNameRetriever.cache = ModuleNameRetriever.notOnJava9;
        }
    }
    
    public final String getModuleName(final BaseContinuationImpl baseContinuationImpl) {
        Intrinsics.checkParameterIsNotNull(baseContinuationImpl, "continuation");
        Cache cache = ModuleNameRetriever.cache;
        if (cache == null) {
            cache = this.buildCache(baseContinuationImpl);
        }
        final Cache notOnJava9 = ModuleNameRetriever.notOnJava9;
        final String s = null;
        final String s2 = null;
        if (cache == notOnJava9) {
            return null;
        }
        final Method getModuleMethod = cache.getModuleMethod;
        String s3 = s;
        if (getModuleMethod != null) {
            final Object invoke = getModuleMethod.invoke(baseContinuationImpl.getClass(), new Object[0]);
            s3 = s;
            if (invoke != null) {
                final Method getDescriptorMethod = cache.getDescriptorMethod;
                s3 = s;
                if (getDescriptorMethod != null) {
                    final Object invoke2 = getDescriptorMethod.invoke(invoke, new Object[0]);
                    s3 = s;
                    if (invoke2 != null) {
                        final Method nameMethod = cache.nameMethod;
                        Object invoke3;
                        if (nameMethod != null) {
                            invoke3 = nameMethod.invoke(invoke2, new Object[0]);
                        }
                        else {
                            invoke3 = null;
                        }
                        if (!(invoke3 instanceof String)) {
                            invoke3 = s2;
                        }
                        s3 = (String)invoke3;
                    }
                }
            }
        }
        return s3;
    }
    
    private static final class Cache
    {
        public final Method getDescriptorMethod;
        public final Method getModuleMethod;
        public final Method nameMethod;
        
        public Cache(final Method getModuleMethod, final Method getDescriptorMethod, final Method nameMethod) {
            this.getModuleMethod = getModuleMethod;
            this.getDescriptorMethod = getDescriptorMethod;
            this.nameMethod = nameMethod;
        }
    }
}
