// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.view.WindowInsets;
import android.graphics.ColorFilter;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.FrameLayout;

public class KeyguardPreviewContainer extends FrameLayout
{
    private Drawable mBlackBarDrawable;
    
    public KeyguardPreviewContainer(final Context context, final AttributeSet set) {
        super(context, set);
        this.setBackground(this.mBlackBarDrawable = new Drawable() {
            public void draw(final Canvas canvas) {
                canvas.save();
                canvas.clipRect(0, KeyguardPreviewContainer.this.getHeight() - KeyguardPreviewContainer.this.getPaddingBottom(), KeyguardPreviewContainer.this.getWidth(), KeyguardPreviewContainer.this.getHeight());
                canvas.drawColor(-16777216);
                canvas.restore();
            }
            
            public int getOpacity() {
                return -1;
            }
            
            public void setAlpha(final int n) {
            }
            
            public void setColorFilter(final ColorFilter colorFilter) {
            }
        });
    }
    
    public WindowInsets onApplyWindowInsets(final WindowInsets windowInsets) {
        this.setPadding(0, 0, 0, windowInsets.getStableInsetBottom());
        return super.onApplyWindowInsets(windowInsets);
    }
}
