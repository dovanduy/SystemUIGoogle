// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.graphics;

import android.content.ContentResolver;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import androidx.core.provider.FontsContractCompat;
import android.os.CancellationSignal;
import android.content.res.Resources;
import androidx.core.content.res.FontResourcesParserCompat;
import android.content.Context;
import java.lang.reflect.GenericDeclaration;
import android.util.Log;
import android.system.ErrnoException;
import android.system.OsConstants;
import android.system.Os;
import java.io.File;
import android.os.ParcelFileDescriptor;
import java.lang.reflect.Array;
import android.graphics.Typeface;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

class TypefaceCompatApi21Impl extends TypefaceCompatBaseImpl
{
    private static Method sAddFontWeightStyle;
    private static Method sCreateFromFamiliesWithDefault;
    private static Class<?> sFontFamily;
    private static Constructor<?> sFontFamilyCtor;
    private static boolean sHasInitBeenCalled = false;
    
    private static boolean addFontWeightStyle(final Object obj, final String s, final int i, final boolean b) {
        init();
        try {
            return (boolean)TypefaceCompatApi21Impl.sAddFontWeightStyle.invoke(obj, s, i, b);
        }
        catch (IllegalAccessException | InvocationTargetException ex) {
            final Object cause;
            throw new RuntimeException((Throwable)cause);
        }
    }
    
    private static Typeface createFromFamiliesWithDefault(final Object o) {
        init();
        try {
            final Object instance = Array.newInstance(TypefaceCompatApi21Impl.sFontFamily, 1);
            Array.set(instance, 0, o);
            return (Typeface)TypefaceCompatApi21Impl.sCreateFromFamiliesWithDefault.invoke(null, instance);
        }
        catch (IllegalAccessException | InvocationTargetException ex) {
            final Object cause;
            throw new RuntimeException((Throwable)cause);
        }
    }
    
    private File getFile(final ParcelFileDescriptor parcelFileDescriptor) {
        try {
            final StringBuilder sb = new StringBuilder();
            sb.append("/proc/self/fd/");
            sb.append(parcelFileDescriptor.getFd());
            final String readlink = Os.readlink(sb.toString());
            if (OsConstants.S_ISREG(Os.stat(readlink).st_mode)) {
                return new File(readlink);
            }
            return null;
        }
        catch (ErrnoException ex) {
            return null;
        }
    }
    
    private static void init() {
        if (TypefaceCompatApi21Impl.sHasInitBeenCalled) {
            return;
        }
        TypefaceCompatApi21Impl.sHasInitBeenCalled = true;
        final Constructor<?> constructor = null;
        GenericDeclaration forName;
        Constructor<?> constructor2;
        GenericDeclaration method;
        GenericDeclaration method2;
        try {
            forName = Class.forName("android.graphics.FontFamily");
            constructor2 = ((Class<?>)forName).getConstructor((Class<?>[])new Class[0]);
            method = ((Class)forName).getMethod("addFontWeightStyle", String.class, Integer.TYPE, Boolean.TYPE);
            method2 = Typeface.class.getMethod("createFromFamiliesWithDefault", Array.newInstance((Class<?>)forName, 1).getClass());
        }
        catch (ClassNotFoundException | NoSuchMethodException ex3) {
            final NoSuchMethodException ex2;
            final NoSuchMethodException ex = ex2;
            Log.e("TypefaceCompatApi21Impl", ex.getClass().getName(), (Throwable)ex);
            method2 = null;
            method = (forName = method2);
            constructor2 = constructor;
        }
        TypefaceCompatApi21Impl.sFontFamilyCtor = constructor2;
        TypefaceCompatApi21Impl.sFontFamily = (Class<?>)forName;
        TypefaceCompatApi21Impl.sAddFontWeightStyle = (Method)method;
        TypefaceCompatApi21Impl.sCreateFromFamiliesWithDefault = (Method)method2;
    }
    
    private static Object newFamily() {
        init();
        try {
            return TypefaceCompatApi21Impl.sFontFamilyCtor.newInstance(new Object[0]);
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException ex) {
            final Object cause;
            throw new RuntimeException((Throwable)cause);
        }
    }
    
    @Override
    public Typeface createFromFontFamilyFilesResourceEntry(final Context context, FontResourcesParserCompat.FontFamilyFilesResourceEntry tempFile, final Resources resources, int i) {
        final Object family = newFamily();
        final FontResourcesParserCompat.FontFileResourceEntry[] entries = tempFile.getEntries();
        final int length = entries.length;
        i = 0;
        while (i < length) {
            final FontResourcesParserCompat.FontFileResourceEntry fontFileResourceEntry = entries[i];
            tempFile = (FontResourcesParserCompat.FontFamilyFilesResourceEntry)TypefaceCompatUtil.getTempFile(context);
            if (tempFile == null) {
                return null;
            }
            try {
                if (!TypefaceCompatUtil.copyToFile((File)tempFile, resources, fontFileResourceEntry.getResourceId())) {
                    return null;
                }
                if (!addFontWeightStyle(family, ((File)tempFile).getPath(), fontFileResourceEntry.getWeight(), fontFileResourceEntry.isItalic())) {
                    return null;
                }
                ((File)tempFile).delete();
                ++i;
                continue;
            }
            catch (RuntimeException ex) {
                return null;
            }
            finally {
                ((File)tempFile).delete();
            }
            break;
        }
        return createFromFamiliesWithDefault(family);
    }
    
    @Override
    public Typeface createFromFontInfo(final Context context, CancellationSignal openFileDescriptor, FontsContractCompat.FontInfo[] array, final int n) {
        if (array.length < 1) {
            return null;
        }
        final FontsContractCompat.FontInfo bestInfo = this.findBestInfo(array, n);
        final ContentResolver contentResolver = context.getContentResolver();
        try {
            openFileDescriptor = (CancellationSignal)contentResolver.openFileDescriptor(bestInfo.getUri(), "r", openFileDescriptor);
            if (openFileDescriptor == null) {
                if (openFileDescriptor != null) {
                    ((ParcelFileDescriptor)openFileDescriptor).close();
                }
                return null;
            }
            try {
                final File file = this.getFile((ParcelFileDescriptor)openFileDescriptor);
                if (file != null && file.canRead()) {
                    final Typeface fromFile = Typeface.createFromFile(file);
                    if (openFileDescriptor != null) {
                        ((ParcelFileDescriptor)openFileDescriptor).close();
                    }
                    return fromFile;
                }
                array = (FontsContractCompat.FontInfo[])(Object)new FileInputStream(((ParcelFileDescriptor)openFileDescriptor).getFileDescriptor());
                try {
                    final Typeface fromInputStream = super.createFromInputStream(context, (InputStream)(Object)array);
                    ((FileInputStream)(Object)array).close();
                    if (openFileDescriptor != null) {
                        ((ParcelFileDescriptor)openFileDescriptor).close();
                    }
                    return fromInputStream;
                }
                finally {
                    try {
                        ((FileInputStream)(Object)array).close();
                    }
                    finally {
                        final Throwable exception;
                        ((Throwable)context).addSuppressed(exception);
                    }
                }
            }
            finally {
                if (openFileDescriptor != null) {
                    try {
                        ((ParcelFileDescriptor)openFileDescriptor).close();
                    }
                    finally {
                        final Throwable exception2;
                        ((Throwable)context).addSuppressed(exception2);
                    }
                }
            }
        }
        catch (IOException ex) {
            return null;
        }
    }
}
