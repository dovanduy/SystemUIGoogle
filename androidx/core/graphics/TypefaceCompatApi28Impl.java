// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.graphics;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Array;
import android.graphics.Typeface;

public class TypefaceCompatApi28Impl extends TypefaceCompatApi26Impl
{
    @Override
    protected Typeface createFromFamiliesWithDefault(final Object o) {
        try {
            final Object instance = Array.newInstance(super.mFontFamily, 1);
            Array.set(instance, 0, o);
            return (Typeface)super.mCreateFromFamiliesWithDefault.invoke(null, instance, "sans-serif", -1, -1);
        }
        catch (IllegalAccessException | InvocationTargetException ex) {
            final Object cause;
            throw new RuntimeException((Throwable)cause);
        }
    }
    
    @Override
    protected Method obtainCreateFromFamiliesWithDefaultMethod(final Class<?> componentType) throws NoSuchMethodException {
        final Class<?> class1 = Array.newInstance(componentType, 1).getClass();
        final Class<Integer> type = Integer.TYPE;
        final Method declaredMethod = Typeface.class.getDeclaredMethod("createFromFamiliesWithDefault", class1, String.class, type, type);
        declaredMethod.setAccessible(true);
        return declaredMethod;
    }
}
