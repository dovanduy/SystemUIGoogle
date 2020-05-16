// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.widget;

import android.content.res.Resources$NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Build$VERSION;
import android.content.Context;
import java.lang.ref.WeakReference;
import android.content.res.Resources;

public class VectorEnabledTintResources extends Resources
{
    private static boolean sCompatVectorFromResourcesEnabled = false;
    private final WeakReference<Context> mContextRef;
    
    public VectorEnabledTintResources(final Context referent, final Resources resources) {
        super(resources.getAssets(), resources.getDisplayMetrics(), resources.getConfiguration());
        this.mContextRef = new WeakReference<Context>(referent);
    }
    
    public static boolean isCompatVectorFromResourcesEnabled() {
        return VectorEnabledTintResources.sCompatVectorFromResourcesEnabled;
    }
    
    public static boolean shouldBeUsed() {
        return isCompatVectorFromResourcesEnabled() && Build$VERSION.SDK_INT <= 20;
    }
    
    public Drawable getDrawable(final int n) throws Resources$NotFoundException {
        final Context context = this.mContextRef.get();
        if (context != null) {
            return ResourceManagerInternal.get().onDrawableLoadedFromResources(context, this, n);
        }
        return super.getDrawable(n);
    }
    
    final Drawable superGetDrawable(final int n) {
        return super.getDrawable(n);
    }
}
