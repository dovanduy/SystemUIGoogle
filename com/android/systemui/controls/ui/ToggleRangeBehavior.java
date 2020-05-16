// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.ui;

import android.view.MotionEvent;
import android.view.GestureDetector$SimpleOnGestureListener;
import android.view.View$OnTouchListener;
import android.view.GestureDetector$OnGestureListener;
import android.view.GestureDetector;
import android.view.View;
import android.service.controls.actions.ControlAction;
import android.service.controls.actions.FloatAction;
import android.service.controls.templates.ControlTemplate;
import android.view.View$AccessibilityDelegate;
import com.android.systemui.R$id;
import android.graphics.drawable.LayerDrawable;
import kotlin.TypeCastException;
import com.android.systemui.R$dimen;
import java.util.IllegalFormatException;
import android.util.Log;
import kotlin.jvm.internal.Intrinsics;
import java.util.Arrays;
import kotlin.jvm.internal.StringCompanionObject;
import android.service.controls.templates.ToggleRangeTemplate;
import android.widget.TextView;
import android.service.controls.templates.RangeTemplate;
import android.service.controls.Control;
import android.content.Context;
import android.graphics.drawable.Drawable;

public final class ToggleRangeBehavior implements Behavior
{
    public Drawable clipLayer;
    public Context context;
    public Control control;
    private String currentRangeValue;
    private CharSequence currentStatusText;
    public ControlViewHolder cvh;
    public RangeTemplate rangeTemplate;
    public TextView status;
    public ToggleRangeTemplate template;
    
    public ToggleRangeBehavior() {
        this.currentStatusText = "";
        this.currentRangeValue = "";
    }
    
    private final String format(String format, final String s, final float f) {
        try {
            final StringCompanionObject instance = StringCompanionObject.INSTANCE;
            format = String.format(format, Arrays.copyOf(new Object[] { f }, 1));
            Intrinsics.checkExpressionValueIsNotNull(format, "java.lang.String.format(format, *args)");
        }
        catch (IllegalFormatException ex) {
            Log.w("ControlsUiController", "Illegal format in range template", (Throwable)ex);
            format = "";
            if (!Intrinsics.areEqual(s, "")) {
                format = this.format(s, "", f);
            }
        }
        return format;
    }
    
    private final float levelToRangeValue(final int n) {
        final float n2 = n / (float)10000;
        final RangeTemplate rangeTemplate = this.rangeTemplate;
        if (rangeTemplate == null) {
            Intrinsics.throwUninitializedPropertyAccessException("rangeTemplate");
            throw null;
        }
        final float minValue = rangeTemplate.getMinValue();
        final RangeTemplate rangeTemplate2 = this.rangeTemplate;
        if (rangeTemplate2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("rangeTemplate");
            throw null;
        }
        final float maxValue = rangeTemplate2.getMaxValue();
        final RangeTemplate rangeTemplate3 = this.rangeTemplate;
        if (rangeTemplate3 != null) {
            return minValue + n2 * (maxValue - rangeTemplate3.getMinValue());
        }
        Intrinsics.throwUninitializedPropertyAccessException("rangeTemplate");
        throw null;
    }
    
    public final void beginUpdateRange() {
        final TextView status = this.status;
        if (status == null) {
            Intrinsics.throwUninitializedPropertyAccessException("status");
            throw null;
        }
        final Context context = this.context;
        if (context != null) {
            status.setTextSize(0, (float)context.getResources().getDimensionPixelSize(R$dimen.control_status_expanded));
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("context");
        throw null;
    }
    
    @Override
    public void bind(final ControlWithState controlWithState) {
        Intrinsics.checkParameterIsNotNull(controlWithState, "cws");
        final Control control = controlWithState.getControl();
        if (control == null) {
            Intrinsics.throwNpe();
            throw null;
        }
        if ((this.control = control) == null) {
            Intrinsics.throwUninitializedPropertyAccessException("control");
            throw null;
        }
        final CharSequence statusText = control.getStatusText();
        Intrinsics.checkExpressionValueIsNotNull(statusText, "control.getStatusText()");
        this.currentStatusText = statusText;
        final TextView status = this.status;
        if (status == null) {
            Intrinsics.throwUninitializedPropertyAccessException("status");
            throw null;
        }
        status.setText(statusText);
        final ControlViewHolder cvh = this.cvh;
        if (cvh == null) {
            Intrinsics.throwUninitializedPropertyAccessException("cvh");
            throw null;
        }
        final Drawable background = cvh.getLayout().getBackground();
        if (background == null) {
            throw new TypeCastException("null cannot be cast to non-null type android.graphics.drawable.LayerDrawable");
        }
        final Drawable drawableByLayerId = ((LayerDrawable)background).findDrawableByLayerId(R$id.clip_layer);
        Intrinsics.checkExpressionValueIsNotNull(drawableByLayerId, "ld.findDrawableByLayerId(R.id.clip_layer)");
        if ((this.clipLayer = drawableByLayerId) == null) {
            Intrinsics.throwUninitializedPropertyAccessException("clipLayer");
            throw null;
        }
        drawableByLayerId.setLevel(0);
        final Control control2 = this.control;
        if (control2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("control");
            throw null;
        }
        final ControlTemplate controlTemplate = control2.getControlTemplate();
        if (controlTemplate == null) {
            throw new TypeCastException("null cannot be cast to non-null type android.service.controls.templates.ToggleRangeTemplate");
        }
        final ToggleRangeTemplate template = (ToggleRangeTemplate)controlTemplate;
        if ((this.template = template) == null) {
            Intrinsics.throwUninitializedPropertyAccessException("template");
            throw null;
        }
        final RangeTemplate range = template.getRange();
        Intrinsics.checkExpressionValueIsNotNull(range, "template.getRange()");
        this.rangeTemplate = range;
        final ToggleRangeTemplate template2 = this.template;
        if (template2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("template");
            throw null;
        }
        final boolean checked = template2.isChecked();
        final RangeTemplate rangeTemplate = this.rangeTemplate;
        if (rangeTemplate == null) {
            Intrinsics.throwUninitializedPropertyAccessException("rangeTemplate");
            throw null;
        }
        final float currentValue = rangeTemplate.getCurrentValue();
        final RangeTemplate rangeTemplate2 = this.rangeTemplate;
        if (rangeTemplate2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("rangeTemplate");
            throw null;
        }
        final float maxValue = rangeTemplate2.getMaxValue();
        final RangeTemplate rangeTemplate3 = this.rangeTemplate;
        if (rangeTemplate3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("rangeTemplate");
            throw null;
        }
        this.updateRange(currentValue / (maxValue - rangeTemplate3.getMinValue()), checked, false);
        final ControlViewHolder cvh2 = this.cvh;
        if (cvh2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("cvh");
            throw null;
        }
        ControlViewHolder.applyRenderInfo$frameworks__base__packages__SystemUI__android_common__SystemUI_core$default(cvh2, checked, 0, 2, null);
        final ControlViewHolder cvh3 = this.cvh;
        if (cvh3 != null) {
            cvh3.getLayout().setAccessibilityDelegate((View$AccessibilityDelegate)new ToggleRangeBehavior$bind.ToggleRangeBehavior$bind$1(this));
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("cvh");
        throw null;
    }
    
    public final void endUpdateRange() {
        final TextView status = this.status;
        if (status == null) {
            Intrinsics.throwUninitializedPropertyAccessException("status");
            throw null;
        }
        final Context context = this.context;
        if (context == null) {
            Intrinsics.throwUninitializedPropertyAccessException("context");
            throw null;
        }
        status.setTextSize(0, (float)context.getResources().getDimensionPixelSize(R$dimen.control_status_normal));
        final TextView status2 = this.status;
        if (status2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("status");
            throw null;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(this.currentStatusText);
        sb.append(' ');
        sb.append(this.currentRangeValue);
        status2.setText((CharSequence)sb.toString());
        final ControlViewHolder cvh = this.cvh;
        if (cvh == null) {
            Intrinsics.throwUninitializedPropertyAccessException("cvh");
            throw null;
        }
        final RangeTemplate rangeTemplate = this.rangeTemplate;
        if (rangeTemplate == null) {
            Intrinsics.throwUninitializedPropertyAccessException("rangeTemplate");
            throw null;
        }
        final String templateId = rangeTemplate.getTemplateId();
        final Drawable clipLayer = this.clipLayer;
        if (clipLayer != null) {
            cvh.action((ControlAction)new FloatAction(templateId, this.findNearestStep(this.levelToRangeValue(clipLayer.getLevel()))));
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("clipLayer");
        throw null;
    }
    
    public final float findNearestStep(final float n) {
        final RangeTemplate rangeTemplate = this.rangeTemplate;
        if (rangeTemplate == null) {
            Intrinsics.throwUninitializedPropertyAccessException("rangeTemplate");
            throw null;
        }
        float minValue = rangeTemplate.getMinValue();
        float n2 = 1000.0f;
        while (true) {
            final RangeTemplate rangeTemplate2 = this.rangeTemplate;
            if (rangeTemplate2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("rangeTemplate");
                throw null;
            }
            if (minValue <= rangeTemplate2.getMaxValue()) {
                final float abs = Math.abs(n - minValue);
                if (abs < n2) {
                    final RangeTemplate rangeTemplate3 = this.rangeTemplate;
                    if (rangeTemplate3 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("rangeTemplate");
                        throw null;
                    }
                    minValue += rangeTemplate3.getStepValue();
                    n2 = abs;
                }
                else {
                    final RangeTemplate rangeTemplate4 = this.rangeTemplate;
                    if (rangeTemplate4 != null) {
                        return minValue - rangeTemplate4.getStepValue();
                    }
                    Intrinsics.throwUninitializedPropertyAccessException("rangeTemplate");
                    throw null;
                }
            }
            else {
                final RangeTemplate rangeTemplate5 = this.rangeTemplate;
                if (rangeTemplate5 != null) {
                    return rangeTemplate5.getMaxValue();
                }
                Intrinsics.throwUninitializedPropertyAccessException("rangeTemplate");
                throw null;
            }
        }
    }
    
    public final Drawable getClipLayer() {
        final Drawable clipLayer = this.clipLayer;
        if (clipLayer != null) {
            return clipLayer;
        }
        Intrinsics.throwUninitializedPropertyAccessException("clipLayer");
        throw null;
    }
    
    public final ControlViewHolder getCvh() {
        final ControlViewHolder cvh = this.cvh;
        if (cvh != null) {
            return cvh;
        }
        Intrinsics.throwUninitializedPropertyAccessException("cvh");
        throw null;
    }
    
    public final RangeTemplate getRangeTemplate() {
        final RangeTemplate rangeTemplate = this.rangeTemplate;
        if (rangeTemplate != null) {
            return rangeTemplate;
        }
        Intrinsics.throwUninitializedPropertyAccessException("rangeTemplate");
        throw null;
    }
    
    public final ToggleRangeTemplate getTemplate() {
        final ToggleRangeTemplate template = this.template;
        if (template != null) {
            return template;
        }
        Intrinsics.throwUninitializedPropertyAccessException("template");
        throw null;
    }
    
    @Override
    public void initialize(final ControlViewHolder cvh) {
        Intrinsics.checkParameterIsNotNull(cvh, "cvh");
        this.cvh = cvh;
        final TextView status = cvh.getStatus();
        this.status = status;
        if (status == null) {
            Intrinsics.throwUninitializedPropertyAccessException("status");
            throw null;
        }
        final Context context = status.getContext();
        Intrinsics.checkExpressionValueIsNotNull(context, "status.getContext()");
        this.context = context;
        ControlViewHolder.applyRenderInfo$frameworks__base__packages__SystemUI__android_common__SystemUI_core$default(cvh, false, 0, 2, null);
        final ToggleRangeGestureListener toggleRangeGestureListener = new ToggleRangeGestureListener((View)cvh.getLayout());
        final Context context2 = this.context;
        if (context2 != null) {
            cvh.getLayout().setOnTouchListener((View$OnTouchListener)new ToggleRangeBehavior$initialize.ToggleRangeBehavior$initialize$1(this, new GestureDetector(context2, (GestureDetector$OnGestureListener)toggleRangeGestureListener), toggleRangeGestureListener));
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("context");
        throw null;
    }
    
    public final void updateRange(float levelToRangeValue, final boolean b, final boolean b2) {
        int n;
        if (b) {
            n = (int)(10000 * levelToRangeValue);
        }
        else {
            n = 0;
        }
        final Drawable clipLayer = this.clipLayer;
        if (clipLayer == null) {
            Intrinsics.throwUninitializedPropertyAccessException("clipLayer");
            throw null;
        }
        final int max = Math.max(0, Math.min(10000, clipLayer.getLevel() + n));
        final Drawable clipLayer2 = this.clipLayer;
        if (clipLayer2 != null) {
            clipLayer2.setLevel(max);
            if (b) {
                final Drawable clipLayer3 = this.clipLayer;
                if (clipLayer3 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("clipLayer");
                    throw null;
                }
                levelToRangeValue = this.levelToRangeValue(clipLayer3.getLevel());
                final RangeTemplate rangeTemplate = this.rangeTemplate;
                if (rangeTemplate == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("rangeTemplate");
                    throw null;
                }
                String s = this.format(rangeTemplate.getFormatString().toString(), "%.1f", levelToRangeValue);
                this.currentRangeValue = s;
                if (!b2) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append(this.currentStatusText);
                    sb.append(' ');
                    sb.append(this.currentRangeValue);
                    s = sb.toString();
                }
                final TextView status = this.status;
                if (status == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("status");
                    throw null;
                }
                status.setText((CharSequence)s);
            }
            else {
                final TextView status2 = this.status;
                if (status2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("status");
                    throw null;
                }
                status2.setText(this.currentStatusText);
            }
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("clipLayer");
        throw null;
    }
    
    public final class ToggleRangeGestureListener extends GestureDetector$SimpleOnGestureListener
    {
        private boolean isDragging;
        private final View v;
        
        public ToggleRangeGestureListener(final View v) {
            Intrinsics.checkParameterIsNotNull(v, "v");
            this.v = v;
        }
        
        public final boolean isDragging() {
            return this.isDragging;
        }
        
        public boolean onDown(final MotionEvent motionEvent) {
            Intrinsics.checkParameterIsNotNull(motionEvent, "e");
            return true;
        }
        
        public void onLongPress(final MotionEvent motionEvent) {
            Intrinsics.checkParameterIsNotNull(motionEvent, "e");
            ControlActionCoordinator.INSTANCE.longPress(ToggleRangeBehavior.this.getCvh());
        }
        
        public boolean onScroll(final MotionEvent motionEvent, final MotionEvent motionEvent2, final float n, final float n2) {
            Intrinsics.checkParameterIsNotNull(motionEvent, "e1");
            Intrinsics.checkParameterIsNotNull(motionEvent2, "e2");
            if (!this.isDragging) {
                this.v.getParent().requestDisallowInterceptTouchEvent(true);
                ToggleRangeBehavior.this.beginUpdateRange();
                this.isDragging = true;
            }
            ToggleRangeBehavior.this.updateRange(-n / this.v.getWidth(), true, true);
            return true;
        }
        
        public boolean onSingleTapUp(final MotionEvent motionEvent) {
            Intrinsics.checkParameterIsNotNull(motionEvent, "e");
            final ToggleRangeBehavior this$0 = ToggleRangeBehavior.this;
            final ControlActionCoordinator instance = ControlActionCoordinator.INSTANCE;
            final ControlViewHolder cvh = this$0.getCvh();
            final String templateId = this$0.getTemplate().getTemplateId();
            Intrinsics.checkExpressionValueIsNotNull(templateId, "th.template.getTemplateId()");
            instance.toggle(cvh, templateId, this$0.getTemplate().isChecked());
            return true;
        }
        
        public final void setDragging(final boolean isDragging) {
            this.isDragging = isDragging;
        }
    }
}
