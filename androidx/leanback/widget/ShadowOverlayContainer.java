// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import androidx.leanback.R$dimen;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.FrameLayout;

public class ShadowOverlayContainer extends FrameLayout
{
    private static final Rect sTempRect;
    private boolean mInitialized;
    int mOverlayColor;
    private Paint mOverlayPaint;
    private View mWrappedView;
    
    static {
        sTempRect = new Rect();
    }
    
    public ShadowOverlayContainer(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public ShadowOverlayContainer(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.useStaticShadow();
        this.useDynamicShadow();
    }
    
    public static boolean supportsDynamicShadow() {
        return ShadowHelper.supportsDynamicShadow();
    }
    
    public static boolean supportsShadow() {
        return StaticShadowHelper.supportsShadow();
    }
    
    public void draw(final Canvas canvas) {
        super.draw(canvas);
        if (this.mOverlayPaint != null && this.mOverlayColor != 0) {
            canvas.drawRect((float)this.mWrappedView.getLeft(), (float)this.mWrappedView.getTop(), (float)this.mWrappedView.getRight(), (float)this.mWrappedView.getBottom(), this.mOverlayPaint);
        }
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        if (b) {
            final View mWrappedView = this.mWrappedView;
            if (mWrappedView != null) {
                ShadowOverlayContainer.sTempRect.left = (int)mWrappedView.getPivotX();
                ShadowOverlayContainer.sTempRect.top = (int)this.mWrappedView.getPivotY();
                this.offsetDescendantRectToMyCoords(this.mWrappedView, ShadowOverlayContainer.sTempRect);
                this.setPivotX((float)ShadowOverlayContainer.sTempRect.left);
                this.setPivotY((float)ShadowOverlayContainer.sTempRect.top);
            }
        }
    }
    
    public void useDynamicShadow() {
        this.useDynamicShadow(this.getResources().getDimension(R$dimen.lb_material_shadow_normal_z), this.getResources().getDimension(R$dimen.lb_material_shadow_focused_z));
    }
    
    public void useDynamicShadow(final float n, final float n2) {
        if (!this.mInitialized) {
            supportsDynamicShadow();
            return;
        }
        throw new IllegalStateException("Already initialized");
    }
    
    public void useStaticShadow() {
        if (!this.mInitialized) {
            supportsShadow();
            return;
        }
        throw new IllegalStateException("Already initialized");
    }
}
