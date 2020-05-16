// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.graphics.Canvas;
import android.view.ViewGroup$LayoutParams;
import android.widget.LinearLayout$LayoutParams;
import androidx.leanback.R$id;
import android.view.ViewGroup;
import androidx.leanback.R$layout;
import android.view.LayoutInflater;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;

final class RowContainerView extends LinearLayout
{
    private Drawable mForeground;
    private boolean mForegroundBoundsChanged;
    
    public RowContainerView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public RowContainerView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mForegroundBoundsChanged = true;
        this.setOrientation(1);
        LayoutInflater.from(context).inflate(R$layout.lb_row_container, (ViewGroup)this);
        final ViewGroup viewGroup = (ViewGroup)this.findViewById(R$id.lb_row_container_header_dock);
        this.setLayoutParams((ViewGroup$LayoutParams)new LinearLayout$LayoutParams(-2, -2));
    }
    
    public void draw(final Canvas canvas) {
        super.draw(canvas);
        final Drawable mForeground = this.mForeground;
        if (mForeground != null) {
            if (this.mForegroundBoundsChanged) {
                this.mForegroundBoundsChanged = false;
                mForeground.setBounds(0, 0, this.getWidth(), this.getHeight());
            }
            this.mForeground.draw(canvas);
        }
    }
    
    public Drawable getForeground() {
        return this.mForeground;
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    protected void onSizeChanged(final int n, final int n2, final int n3, final int n4) {
        super.onSizeChanged(n, n2, n3, n4);
        this.mForegroundBoundsChanged = true;
    }
    
    public void setForeground(final Drawable mForeground) {
        this.mForeground = mForeground;
        this.setWillNotDraw(mForeground == null);
        this.invalidate();
    }
}
