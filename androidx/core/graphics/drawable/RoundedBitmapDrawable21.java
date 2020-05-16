// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.graphics.drawable;

import android.view.Gravity;
import android.graphics.Rect;
import android.graphics.Outline;
import android.graphics.Bitmap;
import android.content.res.Resources;

class RoundedBitmapDrawable21 extends RoundedBitmapDrawable
{
    protected RoundedBitmapDrawable21(final Resources resources, final Bitmap bitmap) {
        super(resources, bitmap);
    }
    
    public void getOutline(final Outline outline) {
        this.updateDstRect();
        outline.setRoundRect(super.mDstRect, this.getCornerRadius());
    }
    
    @Override
    void gravityCompatApply(final int n, final int n2, final int n3, final Rect rect, final Rect rect2) {
        Gravity.apply(n, n2, n3, rect, rect2, 0);
    }
}
