// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.ui;

import android.service.controls.templates.ControlTemplate;
import android.widget.TextView;
import com.android.systemui.R$id;
import android.graphics.drawable.LayerDrawable;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import android.service.controls.templates.TemperatureControlTemplate;
import android.service.controls.Control;
import android.graphics.drawable.Drawable;

public final class TemperatureControlBehavior implements Behavior
{
    public Drawable clipLayer;
    public Control control;
    public ControlViewHolder cvh;
    public TemperatureControlTemplate template;
    
    @Override
    public void bind(final ControlWithState controlWithState) {
        Intrinsics.checkParameterIsNotNull(controlWithState, "cws");
        final Control control = controlWithState.getControl();
        if (control == null) {
            Intrinsics.throwNpe();
            throw null;
        }
        this.control = control;
        final ControlViewHolder cvh = this.cvh;
        if (cvh == null) {
            Intrinsics.throwUninitializedPropertyAccessException("cvh");
            throw null;
        }
        final TextView status = cvh.getStatus();
        final Control control2 = this.control;
        if (control2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("control");
            throw null;
        }
        status.setText(control2.getStatusText());
        final ControlViewHolder cvh2 = this.cvh;
        if (cvh2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("cvh");
            throw null;
        }
        final Drawable background = cvh2.getLayout().getBackground();
        if (background == null) {
            throw new TypeCastException("null cannot be cast to non-null type android.graphics.drawable.LayerDrawable");
        }
        final Drawable drawableByLayerId = ((LayerDrawable)background).findDrawableByLayerId(R$id.clip_layer);
        Intrinsics.checkExpressionValueIsNotNull(drawableByLayerId, "ld.findDrawableByLayerId(R.id.clip_layer)");
        this.clipLayer = drawableByLayerId;
        final Control control3 = this.control;
        if (control3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("control");
            throw null;
        }
        final ControlTemplate controlTemplate = control3.getControlTemplate();
        if (controlTemplate == null) {
            throw new TypeCastException("null cannot be cast to non-null type android.service.controls.templates.TemperatureControlTemplate");
        }
        final TemperatureControlTemplate template = (TemperatureControlTemplate)controlTemplate;
        if ((this.template = template) == null) {
            Intrinsics.throwUninitializedPropertyAccessException("template");
            throw null;
        }
        final int currentActiveMode = template.getCurrentActiveMode();
        boolean b = true;
        int level = 0;
        if (currentActiveMode == 0 || currentActiveMode == 1) {
            b = false;
        }
        final Drawable clipLayer = this.clipLayer;
        if (clipLayer == null) {
            Intrinsics.throwUninitializedPropertyAccessException("clipLayer");
            throw null;
        }
        if (b) {
            level = 10000;
        }
        clipLayer.setLevel(level);
        final ControlViewHolder cvh3 = this.cvh;
        if (cvh3 != null) {
            cvh3.applyRenderInfo$frameworks__base__packages__SystemUI__android_common__SystemUI_core(b, currentActiveMode);
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("cvh");
        throw null;
    }
    
    @Override
    public void initialize(final ControlViewHolder cvh) {
        Intrinsics.checkParameterIsNotNull(cvh, "cvh");
        this.cvh = cvh;
    }
}
