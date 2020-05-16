// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.graphics;

import android.net.Uri;
import androidx.collection.SimpleArrayMap;
import androidx.core.provider.FontsContractCompat;
import android.os.CancellationSignal;
import android.content.res.Resources;
import androidx.core.content.res.FontResourcesParserCompat;
import android.content.Context;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.GenericDeclaration;
import android.util.Log;
import java.lang.reflect.Array;
import android.graphics.Typeface;
import java.util.List;
import java.nio.ByteBuffer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

class TypefaceCompatApi24Impl extends TypefaceCompatBaseImpl
{
    private static final Method sAddFontWeightStyle;
    private static final Method sCreateFromFamiliesWithDefault;
    private static final Class<?> sFontFamily;
    private static final Constructor<?> sFontFamilyCtor;
    
    static {
        final Constructor<?> constructor = null;
        GenericDeclaration forName;
        Constructor<?> constructor2;
        Method method2;
        Method sAddFontWeightStyle2;
        try {
            forName = Class.forName("android.graphics.FontFamily");
            constructor2 = ((Class<?>)forName).getConstructor((Class<?>[])new Class[0]);
            final Method method = ((Class)forName).getMethod("addFontWeightStyle", ByteBuffer.class, Integer.TYPE, List.class, Integer.TYPE, Boolean.TYPE);
            method2 = Typeface.class.getMethod("createFromFamiliesWithDefault", Array.newInstance((Class<?>)forName, 1).getClass());
            sAddFontWeightStyle2 = method;
        }
        catch (ClassNotFoundException | NoSuchMethodException ex3) {
            final NoSuchMethodException ex2;
            final NoSuchMethodException ex = ex2;
            Log.e("TypefaceCompatApi24Impl", ex.getClass().getName(), (Throwable)ex);
            final Method method3 = null;
            sAddFontWeightStyle2 = (method2 = method3);
            forName = method3;
            constructor2 = constructor;
        }
        sFontFamilyCtor = constructor2;
        sFontFamily = (Class)forName;
        sAddFontWeightStyle = sAddFontWeightStyle2;
        sCreateFromFamiliesWithDefault = method2;
    }
    
    private static boolean addFontWeightStyle(final Object obj, final ByteBuffer byteBuffer, final int i, final int j, final boolean b) {
        try {
            return (boolean)TypefaceCompatApi24Impl.sAddFontWeightStyle.invoke(obj, byteBuffer, i, null, j, b);
        }
        catch (IllegalAccessException | InvocationTargetException ex) {
            return false;
        }
    }
    
    private static Typeface createFromFamiliesWithDefault(final Object o) {
        try {
            final Object instance = Array.newInstance(TypefaceCompatApi24Impl.sFontFamily, 1);
            Array.set(instance, 0, o);
            return (Typeface)TypefaceCompatApi24Impl.sCreateFromFamiliesWithDefault.invoke(null, instance);
        }
        catch (IllegalAccessException | InvocationTargetException ex) {
            return null;
        }
    }
    
    public static boolean isUsable() {
        if (TypefaceCompatApi24Impl.sAddFontWeightStyle == null) {
            Log.w("TypefaceCompatApi24Impl", "Unable to collect necessary private methods.Fallback to legacy implementation.");
        }
        return TypefaceCompatApi24Impl.sAddFontWeightStyle != null;
    }
    
    private static Object newFamily() {
        try {
            return TypefaceCompatApi24Impl.sFontFamilyCtor.newInstance(new Object[0]);
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException ex) {
            return null;
        }
    }
    
    @Override
    public Typeface createFromFontFamilyFilesResourceEntry(final Context context, final FontResourcesParserCompat.FontFamilyFilesResourceEntry fontFamilyFilesResourceEntry, final Resources resources, int i) {
        final Object family = newFamily();
        if (family == null) {
            return null;
        }
        final FontResourcesParserCompat.FontFileResourceEntry[] entries = fontFamilyFilesResourceEntry.getEntries();
        int length;
        FontResourcesParserCompat.FontFileResourceEntry fontFileResourceEntry;
        ByteBuffer copyToDirectBuffer;
        for (length = entries.length, i = 0; i < length; ++i) {
            fontFileResourceEntry = entries[i];
            copyToDirectBuffer = TypefaceCompatUtil.copyToDirectBuffer(context, resources, fontFileResourceEntry.getResourceId());
            if (copyToDirectBuffer == null) {
                return null;
            }
            if (!addFontWeightStyle(family, copyToDirectBuffer, fontFileResourceEntry.getTtcIndex(), fontFileResourceEntry.getWeight(), fontFileResourceEntry.isItalic())) {
                return null;
            }
        }
        return createFromFamiliesWithDefault(family);
    }
    
    @Override
    public Typeface createFromFontInfo(final Context context, final CancellationSignal cancellationSignal, final FontsContractCompat.FontInfo[] array, final int n) {
        final Object family = newFamily();
        if (family == null) {
            return null;
        }
        final SimpleArrayMap<Uri, ByteBuffer> simpleArrayMap = new SimpleArrayMap<Uri, ByteBuffer>();
        for (final FontsContractCompat.FontInfo fontInfo : array) {
            final Uri uri = fontInfo.getUri();
            ByteBuffer mmap;
            if ((mmap = simpleArrayMap.get(uri)) == null) {
                mmap = TypefaceCompatUtil.mmap(context, cancellationSignal, uri);
                simpleArrayMap.put(uri, mmap);
            }
            if (mmap == null) {
                return null;
            }
            if (!addFontWeightStyle(family, mmap, fontInfo.getTtcIndex(), fontInfo.getWeight(), fontInfo.isItalic())) {
                return null;
            }
        }
        final Typeface fromFamiliesWithDefault = createFromFamiliesWithDefault(family);
        if (fromFamiliesWithDefault == null) {
            return null;
        }
        return Typeface.create(fromFamiliesWithDefault, n);
    }
}
