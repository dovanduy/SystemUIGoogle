// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.view;

import android.view.LayoutInflater$Factory;
import android.os.Build$VERSION;
import android.util.Log;
import android.view.LayoutInflater$Factory2;
import android.view.LayoutInflater;
import java.lang.reflect.Field;

public final class LayoutInflaterCompat
{
    private static boolean sCheckedField;
    private static Field sLayoutInflaterFactory2Field;
    
    private static void forceSetFactory2(final LayoutInflater layoutInflater, final LayoutInflater$Factory2 value) {
        if (!LayoutInflaterCompat.sCheckedField) {
            try {
                (LayoutInflaterCompat.sLayoutInflaterFactory2Field = LayoutInflater.class.getDeclaredField("mFactory2")).setAccessible(true);
            }
            catch (NoSuchFieldException ex) {
                final StringBuilder sb = new StringBuilder();
                sb.append("forceSetFactory2 Could not find field 'mFactory2' on class ");
                sb.append(LayoutInflater.class.getName());
                sb.append("; inflation may have unexpected results.");
                Log.e("LayoutInflaterCompatHC", sb.toString(), (Throwable)ex);
            }
            LayoutInflaterCompat.sCheckedField = true;
        }
        final Field sLayoutInflaterFactory2Field = LayoutInflaterCompat.sLayoutInflaterFactory2Field;
        if (sLayoutInflaterFactory2Field != null) {
            try {
                sLayoutInflaterFactory2Field.set(layoutInflater, value);
            }
            catch (IllegalAccessException ex2) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("forceSetFactory2 could not set the Factory2 on LayoutInflater ");
                sb2.append(layoutInflater);
                sb2.append("; inflation may have unexpected results.");
                Log.e("LayoutInflaterCompatHC", sb2.toString(), (Throwable)ex2);
            }
        }
    }
    
    public static void setFactory2(final LayoutInflater layoutInflater, final LayoutInflater$Factory2 factory2) {
        layoutInflater.setFactory2(factory2);
        if (Build$VERSION.SDK_INT < 21) {
            final LayoutInflater$Factory factory3 = layoutInflater.getFactory();
            if (factory3 instanceof LayoutInflater$Factory2) {
                forceSetFactory2(layoutInflater, (LayoutInflater$Factory2)factory3);
            }
            else {
                forceSetFactory2(layoutInflater, factory2);
            }
        }
    }
}
