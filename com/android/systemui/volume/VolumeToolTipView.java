// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.volume;

import android.graphics.Paint;
import android.view.ViewGroup$LayoutParams;
import android.view.View;
import android.graphics.drawable.Drawable;
import android.graphics.PathEffect;
import android.graphics.CornerPathEffect;
import com.android.systemui.R$dimen;
import androidx.core.content.ContextCompat;
import android.util.TypedValue;
import android.graphics.drawable.shapes.Shape;
import android.graphics.drawable.ShapeDrawable;
import com.android.systemui.recents.TriangleShape;
import com.android.systemui.R$id;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.LinearLayout;

public class VolumeToolTipView extends LinearLayout
{
    public VolumeToolTipView(final Context context) {
        super(context);
    }
    
    public VolumeToolTipView(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public VolumeToolTipView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
    }
    
    public VolumeToolTipView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
    }
    
    private void drawArrow() {
        final View viewById = this.findViewById(R$id.arrow);
        final ViewGroup$LayoutParams layoutParams = viewById.getLayoutParams();
        final ShapeDrawable background = new ShapeDrawable((Shape)TriangleShape.createHorizontal((float)layoutParams.width, (float)layoutParams.height, false));
        final Paint paint = background.getPaint();
        final TypedValue typedValue = new TypedValue();
        this.getContext().getTheme().resolveAttribute(16843829, typedValue, true);
        paint.setColor(ContextCompat.getColor(this.getContext(), typedValue.resourceId));
        paint.setPathEffect((PathEffect)new CornerPathEffect(this.getResources().getDimension(R$dimen.volume_tool_tip_arrow_corner_radius)));
        viewById.setBackground((Drawable)background);
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.drawArrow();
    }
}
