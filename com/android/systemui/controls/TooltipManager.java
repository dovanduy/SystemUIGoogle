// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls;

import android.graphics.Paint;
import android.view.ViewGroup$LayoutParams;
import android.view.ViewGroup$MarginLayoutParams;
import kotlin.TypeCastException;
import android.graphics.drawable.Drawable;
import android.graphics.PathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.drawable.shapes.Shape;
import android.graphics.drawable.ShapeDrawable;
import com.android.systemui.recents.TriangleShape;
import com.android.systemui.R$dimen;
import android.util.TypedValue;
import android.view.View$OnClickListener;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import com.android.systemui.Prefs;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import android.widget.TextView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import android.view.ViewGroup;
import android.view.View;

public final class TooltipManager
{
    private final View arrowView;
    private final boolean below;
    private final ViewGroup layout;
    private final int maxTimesShown;
    private final String preferenceName;
    private final Function1<Integer, Unit> preferenceStorer;
    private int shown;
    private final TextView textView;
    
    public TooltipManager(final Context context, final String preferenceName, int color, final boolean below) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(preferenceName, "preferenceName");
        this.preferenceName = preferenceName;
        this.maxTimesShown = color;
        this.below = below;
        this.shown = Prefs.getInt(context, preferenceName, 0);
        final View inflate = LayoutInflater.from(context).inflate(R$layout.controls_onboarding, (ViewGroup)null);
        if (inflate != null) {
            this.layout = (ViewGroup)inflate;
            this.preferenceStorer = (Function1<Integer, Unit>)new TooltipManager$preferenceStorer.TooltipManager$preferenceStorer$1(this, context);
            this.layout.setAlpha(0.0f);
            this.textView = (TextView)this.layout.requireViewById(R$id.onboarding_text);
            this.layout.requireViewById(R$id.dismiss).setOnClickListener((View$OnClickListener)new TooltipManager$$special$$inlined$apply$lambda.TooltipManager$$special$$inlined$apply$lambda$1(this));
            final View requireViewById = this.layout.requireViewById(R$id.arrow);
            final TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(16843829, typedValue, true);
            color = context.getResources().getColor(typedValue.resourceId, context.getTheme());
            final int dimensionPixelSize = context.getResources().getDimensionPixelSize(R$dimen.recents_onboarding_toast_arrow_corner_radius);
            final ViewGroup$LayoutParams layoutParams = requireViewById.getLayoutParams();
            final ShapeDrawable background = new ShapeDrawable((Shape)TriangleShape.create((float)layoutParams.width, (float)layoutParams.height, this.below));
            final Paint paint = background.getPaint();
            Intrinsics.checkExpressionValueIsNotNull(paint, "arrowPaint");
            paint.setColor(color);
            paint.setPathEffect((PathEffect)new CornerPathEffect((float)dimensionPixelSize));
            requireViewById.setBackground((Drawable)background);
            this.arrowView = requireViewById;
            if (!this.below) {
                this.layout.removeView(requireViewById);
                this.layout.addView(this.arrowView);
                final View arrowView = this.arrowView;
                Intrinsics.checkExpressionValueIsNotNull(arrowView, "arrowView");
                final ViewGroup$LayoutParams layoutParams2 = arrowView.getLayoutParams();
                if (layoutParams2 == null) {
                    throw new TypeCastException("null cannot be cast to non-null type android.view.ViewGroup.MarginLayoutParams");
                }
                final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams = (ViewGroup$MarginLayoutParams)layoutParams2;
                viewGroup$MarginLayoutParams.bottomMargin = viewGroup$MarginLayoutParams.topMargin;
                viewGroup$MarginLayoutParams.topMargin = 0;
            }
            return;
        }
        throw new TypeCastException("null cannot be cast to non-null type android.view.ViewGroup");
    }
    
    private final boolean shouldShow() {
        return this.shown < this.maxTimesShown;
    }
    
    public final ViewGroup getLayout() {
        return this.layout;
    }
    
    public final void hide(final boolean b) {
        if (this.layout.getAlpha() == 0.0f) {
            return;
        }
        this.layout.post((Runnable)new TooltipManager$hide.TooltipManager$hide$1(this, b));
    }
    
    public final void show(int i, final int n, final int n2) {
        if (!this.shouldShow()) {
            return;
        }
        this.textView.setText(i);
        i = this.shown + 1;
        this.shown = i;
        this.preferenceStorer.invoke(i);
        this.layout.post((Runnable)new TooltipManager$show.TooltipManager$show$1(this, n, n2));
    }
}
