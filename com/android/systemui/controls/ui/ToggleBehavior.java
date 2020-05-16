// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.ui;

import android.view.View$OnClickListener;
import android.service.controls.templates.ControlTemplate;
import android.widget.TextView;
import com.android.systemui.R$id;
import android.graphics.drawable.LayerDrawable;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import android.service.controls.templates.ToggleTemplate;
import android.service.controls.Control;
import android.graphics.drawable.Drawable;

public final class ToggleBehavior implements Behavior
{
    public Drawable clipLayer;
    public Control control;
    public ControlViewHolder cvh;
    public ToggleTemplate template;
    
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
        final Control control3 = this.control;
        if (control3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("control");
            throw null;
        }
        final ControlTemplate controlTemplate = control3.getControlTemplate();
        if (controlTemplate == null) {
            throw new TypeCastException("null cannot be cast to non-null type android.service.controls.templates.ToggleTemplate");
        }
        this.template = (ToggleTemplate)controlTemplate;
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
        final ToggleTemplate template = this.template;
        if (template == null) {
            Intrinsics.throwUninitializedPropertyAccessException("template");
            throw null;
        }
        final boolean checked = template.isChecked();
        final Drawable clipLayer = this.clipLayer;
        if (clipLayer == null) {
            Intrinsics.throwUninitializedPropertyAccessException("clipLayer");
            throw null;
        }
        int level;
        if (checked) {
            level = 10000;
        }
        else {
            level = 0;
        }
        clipLayer.setLevel(level);
        final ControlViewHolder cvh3 = this.cvh;
        if (cvh3 != null) {
            ControlViewHolder.applyRenderInfo$frameworks__base__packages__SystemUI__android_common__SystemUI_core$default(cvh3, checked, 0, 2, null);
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("cvh");
        throw null;
    }
    
    public final ToggleTemplate getTemplate() {
        final ToggleTemplate template = this.template;
        if (template != null) {
            return template;
        }
        Intrinsics.throwUninitializedPropertyAccessException("template");
        throw null;
    }
    
    @Override
    public void initialize(final ControlViewHolder cvh) {
        Intrinsics.checkParameterIsNotNull(cvh, "cvh");
        ControlViewHolder.applyRenderInfo$frameworks__base__packages__SystemUI__android_common__SystemUI_core$default(this.cvh = cvh, false, 0, 2, null);
        cvh.getLayout().setOnClickListener((View$OnClickListener)new ToggleBehavior$initialize.ToggleBehavior$initialize$1(this, cvh));
    }
}
