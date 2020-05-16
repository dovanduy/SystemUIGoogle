// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.content.res;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.os.Looper;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import androidx.core.graphics.TypefaceCompat;
import android.os.Handler;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.content.Context;
import android.content.res.Resources$NotFoundException;
import android.os.Build$VERSION;
import android.graphics.drawable.Drawable;
import android.content.res.Resources$Theme;
import android.content.res.Resources;

public final class ResourcesCompat
{
    public static Drawable getDrawable(final Resources resources, final int n, final Resources$Theme resources$Theme) throws Resources$NotFoundException {
        if (Build$VERSION.SDK_INT >= 21) {
            return resources.getDrawable(n, resources$Theme);
        }
        return resources.getDrawable(n);
    }
    
    public static Typeface getFont(final Context context, final int n, final TypedValue typedValue, final int n2, final FontCallback fontCallback) throws Resources$NotFoundException {
        if (context.isRestricted()) {
            return null;
        }
        return loadFont(context, n, typedValue, n2, fontCallback, null, true);
    }
    
    private static Typeface loadFont(final Context context, final int i, final TypedValue typedValue, final int n, final FontCallback fontCallback, final Handler handler, final boolean b) {
        final Resources resources = context.getResources();
        resources.getValue(i, typedValue, true);
        final Typeface loadFont = loadFont(context, resources, typedValue, i, n, fontCallback, handler, b);
        if (loadFont == null && fontCallback == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Font resource ID #0x");
            sb.append(Integer.toHexString(i));
            sb.append(" could not be retrieved.");
            throw new Resources$NotFoundException(sb.toString());
        }
        return loadFont;
    }
    
    private static Typeface loadFont(final Context context, final Resources resources, TypedValue string, final int i, final int n, final FontCallback fontCallback, final Handler handler, final boolean b) {
        final CharSequence string2 = string.string;
        if (string2 == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Resource \"");
            sb.append(resources.getResourceName(i));
            sb.append("\" (");
            sb.append(Integer.toHexString(i));
            sb.append(") is not a Font: ");
            sb.append(string);
            throw new Resources$NotFoundException(sb.toString());
        }
        string = (TypedValue)string2.toString();
        if (!((String)string).startsWith("res/")) {
            if (fontCallback != null) {
                fontCallback.callbackFailAsync(-3, handler);
            }
            return null;
        }
        final Typeface fromCache = TypefaceCompat.findFromCache(resources, i, n);
        if (fromCache != null) {
            if (fontCallback != null) {
                fontCallback.callbackSuccessAsync(fromCache, handler);
            }
            return fromCache;
        }
        try {
            if (!((String)string).toLowerCase().endsWith(".xml")) {
                final Typeface fromResourcesFontFile = TypefaceCompat.createFromResourcesFontFile(context, resources, i, (String)string, n);
                if (fontCallback != null) {
                    if (fromResourcesFontFile != null) {
                        fontCallback.callbackSuccessAsync(fromResourcesFontFile, handler);
                    }
                    else {
                        fontCallback.callbackFailAsync(-3, handler);
                    }
                }
                return fromResourcesFontFile;
            }
            final FontResourcesParserCompat.FamilyResourceEntry parse = FontResourcesParserCompat.parse((XmlPullParser)resources.getXml(i), resources);
            if (parse == null) {
                Log.e("ResourcesCompat", "Failed to find font-family tag");
                if (fontCallback != null) {
                    fontCallback.callbackFailAsync(-3, handler);
                }
                return null;
            }
            return TypefaceCompat.createFromResourcesFamilyXml(context, parse, resources, i, n, fontCallback, handler, b);
        }
        catch (IOException ex) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Failed to read xml resource ");
            sb2.append((String)string);
            Log.e("ResourcesCompat", sb2.toString(), (Throwable)ex);
        }
        catch (XmlPullParserException ex2) {
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("Failed to parse xml resource ");
            sb3.append((String)string);
            Log.e("ResourcesCompat", sb3.toString(), (Throwable)ex2);
        }
        if (fontCallback != null) {
            fontCallback.callbackFailAsync(-3, handler);
        }
        return null;
    }
    
    public abstract static class FontCallback
    {
        public final void callbackFailAsync(final int n, final Handler handler) {
            Handler handler2 = handler;
            if (handler == null) {
                handler2 = new Handler(Looper.getMainLooper());
            }
            handler2.post((Runnable)new Runnable() {
                @Override
                public void run() {
                    FontCallback.this.onFontRetrievalFailed(n);
                }
            });
        }
        
        public final void callbackSuccessAsync(final Typeface typeface, final Handler handler) {
            Handler handler2 = handler;
            if (handler == null) {
                handler2 = new Handler(Looper.getMainLooper());
            }
            handler2.post((Runnable)new Runnable() {
                @Override
                public void run() {
                    FontCallback.this.onFontRetrieved(typeface);
                }
            });
        }
        
        public abstract void onFontRetrievalFailed(final int p0);
        
        public abstract void onFontRetrieved(final Typeface p0);
    }
    
    public static final class ThemeCompat
    {
        public static void rebase(final Resources$Theme resources$Theme) {
            final int sdk_INT = Build$VERSION.SDK_INT;
            if (sdk_INT >= 29) {
                ImplApi29.rebase(resources$Theme);
            }
            else if (sdk_INT >= 23) {
                ImplApi23.rebase(resources$Theme);
            }
        }
        
        static class ImplApi23
        {
            private static Method sRebaseMethod;
            private static boolean sRebaseMethodFetched;
            private static final Object sRebaseMethodLock;
            
            static {
                sRebaseMethodLock = new Object();
            }
            
            static void rebase(final Resources$Theme obj) {
                synchronized (ImplApi23.sRebaseMethodLock) {
                    if (!ImplApi23.sRebaseMethodFetched) {
                        try {
                            (ImplApi23.sRebaseMethod = Resources$Theme.class.getDeclaredMethod("rebase", (Class<?>[])new Class[0])).setAccessible(true);
                        }
                        catch (NoSuchMethodException ex) {
                            Log.i("ResourcesCompat", "Failed to retrieve rebase() method", (Throwable)ex);
                        }
                        ImplApi23.sRebaseMethodFetched = true;
                    }
                    if (ImplApi23.sRebaseMethod != null) {
                        try {
                            ImplApi23.sRebaseMethod.invoke(obj, new Object[0]);
                        }
                        catch (IllegalAccessException | InvocationTargetException ex2) {
                            final Throwable t;
                            Log.i("ResourcesCompat", "Failed to invoke rebase() method via reflection", t);
                            ImplApi23.sRebaseMethod = null;
                        }
                    }
                }
            }
        }
        
        static class ImplApi29
        {
            static void rebase(final Resources$Theme resources$Theme) {
                resources$Theme.rebase();
            }
        }
    }
}
