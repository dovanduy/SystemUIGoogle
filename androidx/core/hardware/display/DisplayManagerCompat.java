// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.hardware.display;

import android.content.Context;
import java.util.WeakHashMap;

public final class DisplayManagerCompat
{
    private static final WeakHashMap<Context, DisplayManagerCompat> sInstances;
    
    static {
        sInstances = new WeakHashMap<Context, DisplayManagerCompat>();
    }
    
    private DisplayManagerCompat(final Context context) {
    }
    
    public static DisplayManagerCompat getInstance(final Context context) {
        synchronized (DisplayManagerCompat.sInstances) {
            DisplayManagerCompat value;
            if ((value = DisplayManagerCompat.sInstances.get(context)) == null) {
                value = new DisplayManagerCompat(context);
                DisplayManagerCompat.sInstances.put(context, value);
            }
            return value;
        }
    }
}
