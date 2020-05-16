// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.graphics.drawable;

import android.graphics.PorterDuff$Mode;
import android.content.res.ColorStateList;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.os.Build$VERSION;
import android.graphics.Outline;
import android.graphics.Rect;
import android.util.Log;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import java.lang.reflect.Method;

class WrappedDrawableApi21 extends WrappedDrawableApi14
{
    private static Method sIsProjectedDrawableMethod;
    
    WrappedDrawableApi21(final Drawable drawable) {
        super(drawable);
        this.findAndCacheIsProjectedDrawableMethod();
    }
    
    WrappedDrawableApi21(final WrappedDrawableState wrappedDrawableState, final Resources resources) {
        super(wrappedDrawableState, resources);
        this.findAndCacheIsProjectedDrawableMethod();
    }
    
    private void findAndCacheIsProjectedDrawableMethod() {
        if (WrappedDrawableApi21.sIsProjectedDrawableMethod == null) {
            try {
                WrappedDrawableApi21.sIsProjectedDrawableMethod = Drawable.class.getDeclaredMethod("isProjected", (Class<?>[])new Class[0]);
            }
            catch (Exception ex) {
                Log.w("WrappedDrawableApi21", "Failed to retrieve Drawable#isProjected() method", (Throwable)ex);
            }
        }
    }
    
    public Rect getDirtyBounds() {
        return super.mDrawable.getDirtyBounds();
    }
    
    public void getOutline(final Outline outline) {
        super.mDrawable.getOutline(outline);
    }
    
    @Override
    protected boolean isCompatTintEnabled() {
        final int sdk_INT = Build$VERSION.SDK_INT;
        boolean b = false;
        if (sdk_INT == 21) {
            final Drawable mDrawable = super.mDrawable;
            if (!(mDrawable instanceof GradientDrawable) && !(mDrawable instanceof DrawableContainer) && !(mDrawable instanceof InsetDrawable)) {
                b = b;
                if (!(mDrawable instanceof RippleDrawable)) {
                    return b;
                }
            }
            b = true;
        }
        return b;
    }
    
    public boolean isProjected() {
        final Drawable mDrawable = super.mDrawable;
        if (mDrawable != null) {
            final Method sIsProjectedDrawableMethod = WrappedDrawableApi21.sIsProjectedDrawableMethod;
            if (sIsProjectedDrawableMethod != null) {
                try {
                    return (boolean)sIsProjectedDrawableMethod.invoke(mDrawable, new Object[0]);
                }
                catch (Exception ex) {
                    Log.w("WrappedDrawableApi21", "Error calling Drawable#isProjected() method", (Throwable)ex);
                }
            }
        }
        return false;
    }
    
    public void setHotspot(final float n, final float n2) {
        super.mDrawable.setHotspot(n, n2);
    }
    
    public void setHotspotBounds(final int n, final int n2, final int n3, final int n4) {
        super.mDrawable.setHotspotBounds(n, n2, n3, n4);
    }
    
    @Override
    public boolean setState(final int[] state) {
        if (super.setState(state)) {
            this.invalidateSelf();
            return true;
        }
        return false;
    }
    
    @Override
    public void setTint(final int n) {
        if (this.isCompatTintEnabled()) {
            super.setTint(n);
        }
        else {
            super.mDrawable.setTint(n);
        }
    }
    
    @Override
    public void setTintList(final ColorStateList list) {
        if (this.isCompatTintEnabled()) {
            super.setTintList(list);
        }
        else {
            super.mDrawable.setTintList(list);
        }
    }
    
    @Override
    public void setTintMode(final PorterDuff$Mode porterDuff$Mode) {
        if (this.isCompatTintEnabled()) {
            super.setTintMode(porterDuff$Mode);
        }
        else {
            super.mDrawable.setTintMode(porterDuff$Mode);
        }
    }
}
