// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.ui;

import android.view.View$AccessibilityDelegate;
import kotlin.jvm.JvmClassMappingKt;
import android.view.View$OnLongClickListener;
import android.content.res.ColorStateList;
import android.service.controls.Control;
import android.graphics.drawable.GradientDrawable;
import com.android.systemui.R$color;
import kotlin.Pair;
import android.content.ComponentName;
import android.service.controls.templates.TemperatureControlTemplate;
import android.service.controls.templates.ToggleRangeTemplate;
import android.service.controls.templates.StatelessTemplate;
import android.service.controls.templates.ToggleTemplate;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KClass;
import android.service.controls.templates.ControlTemplate;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.graphics.drawable.LayerDrawable;
import kotlin.TypeCastException;
import com.android.systemui.R$id;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.util.concurrency.DelayableExecutor;
import android.widget.TextView;
import android.view.ViewGroup;
import android.service.controls.actions.ControlAction;
import android.widget.ImageView;
import com.android.systemui.controls.controller.ControlsController;
import android.content.Context;
import android.graphics.drawable.ClipDrawable;

public final class ControlViewHolder
{
    private Behavior behavior;
    private Runnable cancelUpdate;
    private final ClipDrawable clipLayer;
    private final Context context;
    private final ControlsController controlsController;
    public ControlWithState cws;
    private final ImageView icon;
    private ControlAction lastAction;
    private final ViewGroup layout;
    private final TextView status;
    private final TextView subtitle;
    private final TextView title;
    private final DelayableExecutor uiExecutor;
    
    public ControlViewHolder(final ViewGroup layout, final ControlsController controlsController, final DelayableExecutor uiExecutor, final DelayableExecutor delayableExecutor) {
        Intrinsics.checkParameterIsNotNull(layout, "layout");
        Intrinsics.checkParameterIsNotNull(controlsController, "controlsController");
        Intrinsics.checkParameterIsNotNull(uiExecutor, "uiExecutor");
        Intrinsics.checkParameterIsNotNull(delayableExecutor, "bgExecutor");
        this.layout = layout;
        this.controlsController = controlsController;
        this.uiExecutor = uiExecutor;
        final View requireViewById = layout.requireViewById(R$id.icon);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById, "layout.requireViewById(R.id.icon)");
        this.icon = (ImageView)requireViewById;
        final View requireViewById2 = this.layout.requireViewById(R$id.status);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById2, "layout.requireViewById(R.id.status)");
        this.status = (TextView)requireViewById2;
        final View requireViewById3 = this.layout.requireViewById(R$id.title);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById3, "layout.requireViewById(R.id.title)");
        this.title = (TextView)requireViewById3;
        final View requireViewById4 = this.layout.requireViewById(R$id.subtitle);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById4, "layout.requireViewById(R.id.subtitle)");
        this.subtitle = (TextView)requireViewById4;
        final Context context = this.layout.getContext();
        Intrinsics.checkExpressionValueIsNotNull(context, "layout.getContext()");
        this.context = context;
        final Drawable background = this.layout.getBackground();
        if (background == null) {
            throw new TypeCastException("null cannot be cast to non-null type android.graphics.drawable.LayerDrawable");
        }
        final LayerDrawable layerDrawable = (LayerDrawable)background;
        layerDrawable.mutate();
        final Drawable drawableByLayerId = layerDrawable.findDrawableByLayerId(R$id.clip_layer);
        if (drawableByLayerId != null) {
            this.clipLayer = (ClipDrawable)drawableByLayerId;
            this.status.setSelected(true);
            return;
        }
        throw new TypeCastException("null cannot be cast to non-null type android.graphics.drawable.ClipDrawable");
    }
    
    private final KClass<? extends Behavior> findBehavior(final int n, final ControlTemplate controlTemplate) {
        KClass kClass;
        if (n == 0) {
            kClass = Reflection.getOrCreateKotlinClass(UnknownBehavior.class);
        }
        else if (controlTemplate instanceof ToggleTemplate) {
            kClass = Reflection.getOrCreateKotlinClass(ToggleBehavior.class);
        }
        else if (controlTemplate instanceof StatelessTemplate) {
            kClass = Reflection.getOrCreateKotlinClass(TouchBehavior.class);
        }
        else if (controlTemplate instanceof ToggleRangeTemplate) {
            kClass = Reflection.getOrCreateKotlinClass(ToggleRangeBehavior.class);
        }
        else if (controlTemplate instanceof TemperatureControlTemplate) {
            kClass = Reflection.getOrCreateKotlinClass(TemperatureControlBehavior.class);
        }
        else {
            kClass = Reflection.getOrCreateKotlinClass(DefaultBehavior.class);
        }
        return (KClass<? extends Behavior>)kClass;
    }
    
    private final void setEnabled(final boolean b) {
        this.status.setEnabled(b);
        this.icon.setEnabled(b);
    }
    
    public final void action(final ControlAction lastAction) {
        Intrinsics.checkParameterIsNotNull(lastAction, "action");
        this.lastAction = lastAction;
        final ControlsController controlsController = this.controlsController;
        final ControlWithState cws = this.cws;
        if (cws == null) {
            Intrinsics.throwUninitializedPropertyAccessException("cws");
            throw null;
        }
        final ComponentName componentName = cws.getComponentName();
        final ControlWithState cws2 = this.cws;
        if (cws2 != null) {
            controlsController.action(componentName, cws2.getCi(), lastAction);
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("cws");
        throw null;
    }
    
    public final void actionResponse(final int n) {
    }
    
    public final void applyRenderInfo$frameworks__base__packages__SystemUI__android_common__SystemUI_core(final boolean enabled, int intValue) {
        this.setEnabled(enabled);
        final ControlWithState cws = this.cws;
        if (cws == null) {
            Intrinsics.throwUninitializedPropertyAccessException("cws");
            throw null;
        }
        final Control control = cws.getControl();
        int n;
        if (control != null) {
            n = control.getDeviceType();
        }
        else {
            final ControlWithState cws2 = this.cws;
            if (cws2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("cws");
                throw null;
            }
            n = cws2.getCi().getDeviceType();
        }
        final RenderInfo.Companion companion = RenderInfo.Companion;
        final Context context = this.context;
        final ControlWithState cws3 = this.cws;
        if (cws3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("cws");
            throw null;
        }
        final RenderInfo lookup = companion.lookup(context, cws3.getComponentName(), n, enabled, intValue);
        final ColorStateList colorStateList = this.context.getResources().getColorStateList(lookup.getForeground(), this.context.getTheme());
        Pair<Integer, Integer> pair;
        if (enabled) {
            pair = new Pair<Integer, Integer>(lookup.getEnabledBackground(), 51);
        }
        else {
            pair = new Pair<Integer, Integer>(R$color.control_default_background, 255);
        }
        final int intValue2 = pair.component1().intValue();
        intValue = pair.component2().intValue();
        this.status.setTextColor(colorStateList);
        this.icon.setImageDrawable(lookup.getIcon());
        if (n != 52) {
            this.icon.setImageTintList(colorStateList);
        }
        final Drawable drawable = this.clipLayer.getDrawable();
        if (drawable != null) {
            final GradientDrawable gradientDrawable = (GradientDrawable)drawable;
            gradientDrawable.setColor(this.context.getResources().getColor(intValue2, this.context.getTheme()));
            gradientDrawable.setAlpha(intValue);
            return;
        }
        throw new TypeCastException("null cannot be cast to non-null type android.graphics.drawable.GradientDrawable");
    }
    
    public final void bindData(final ControlWithState cws) {
        Intrinsics.checkParameterIsNotNull(cws, "cws");
        this.cws = cws;
        final Runnable cancelUpdate = this.cancelUpdate;
        if (cancelUpdate != null) {
            cancelUpdate.run();
        }
        final Control control = cws.getControl();
        Pair<Number, Object> pair;
        if (control != null) {
            this.title.setText(control.getTitle());
            this.subtitle.setText(control.getSubtitle());
            pair = new Pair<Number, Object>(control.getStatus(), control.getControlTemplate());
        }
        else {
            this.title.setText(cws.getCi().getControlTitle());
            this.subtitle.setText(cws.getCi().getControlSubtitle());
            pair = new Pair<Number, Object>(0, ControlTemplate.NO_TEMPLATE);
        }
        final int intValue = pair.component1().intValue();
        final ControlTemplate controlTemplate = pair.component2();
        if (cws.getControl() != null) {
            this.layout.setClickable(true);
            this.layout.setOnLongClickListener((View$OnLongClickListener)new ControlViewHolder$bindData$$inlined$let$lambda.ControlViewHolder$bindData$$inlined$let$lambda$1(this));
        }
        Intrinsics.checkExpressionValueIsNotNull(controlTemplate, "template");
        final KClass<? extends Behavior> behavior = this.findBehavior(intValue, controlTemplate);
        final Behavior behavior2 = this.behavior;
        Label_0260: {
            if (behavior2 != null) {
                if (behavior2 == null) {
                    Intrinsics.throwNpe();
                    throw null;
                }
                if (!(Intrinsics.areEqual(Reflection.getOrCreateKotlinClass(behavior2.getClass()), behavior) ^ true)) {
                    break Label_0260;
                }
            }
            final Behavior behavior3 = JvmClassMappingKt.getJavaClass(behavior).newInstance();
            if ((this.behavior = behavior3) != null) {
                behavior3.initialize(this);
            }
            this.layout.setAccessibilityDelegate((View$AccessibilityDelegate)null);
        }
        final Behavior behavior4 = this.behavior;
        if (behavior4 != null) {
            behavior4.bind(cws);
        }
        final ViewGroup layout = this.layout;
        final StringBuilder sb = new StringBuilder();
        sb.append(this.title.getText());
        sb.append(' ');
        sb.append(this.subtitle.getText());
        sb.append(' ');
        sb.append(this.status.getText());
        layout.setContentDescription((CharSequence)sb.toString());
    }
    
    public final ClipDrawable getClipLayer() {
        return this.clipLayer;
    }
    
    public final Context getContext() {
        return this.context;
    }
    
    public final ControlWithState getCws() {
        final ControlWithState cws = this.cws;
        if (cws != null) {
            return cws;
        }
        Intrinsics.throwUninitializedPropertyAccessException("cws");
        throw null;
    }
    
    public final ControlAction getLastAction() {
        return this.lastAction;
    }
    
    public final ViewGroup getLayout() {
        return this.layout;
    }
    
    public final TextView getStatus() {
        return this.status;
    }
    
    public final TextView getTitle() {
        return this.title;
    }
    
    public final void setTransientStatus(final String text) {
        Intrinsics.checkParameterIsNotNull(text, "tempStatus");
        this.cancelUpdate = this.uiExecutor.executeDelayed((Runnable)new ControlViewHolder$setTransientStatus.ControlViewHolder$setTransientStatus$1(this, this.status.getText()), 3000L);
        this.status.setText((CharSequence)text);
    }
}
