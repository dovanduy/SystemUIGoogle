// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.graphics.drawable;

import androidx.core.view.GravityCompat;
import android.graphics.Rect;
import android.os.Build$VERSION;
import android.graphics.Bitmap;
import android.content.res.Resources;

public final class RoundedBitmapDrawableFactory
{
    public static RoundedBitmapDrawable create(final Resources resources, final Bitmap bitmap) {
        if (Build$VERSION.SDK_INT >= 21) {
            return new RoundedBitmapDrawable21(resources, bitmap);
        }
        return new DefaultRoundedBitmapDrawable(resources, bitmap);
    }
    
    private static class DefaultRoundedBitmapDrawable extends RoundedBitmapDrawable
    {
        DefaultRoundedBitmapDrawable(final Resources resources, final Bitmap bitmap) {
            super(resources, bitmap);
        }
        
        @Override
        void gravityCompatApply(final int n, final int n2, final int n3, final Rect rect, final Rect rect2) {
            GravityCompat.apply(n, n2, n3, rect, rect2, 0);
        }
    }
}
