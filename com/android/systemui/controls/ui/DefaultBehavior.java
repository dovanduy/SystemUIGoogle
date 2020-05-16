// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.ui;

import android.service.controls.Control;
import android.widget.TextView;
import kotlin.jvm.internal.Intrinsics;

public final class DefaultBehavior implements Behavior
{
    public ControlViewHolder cvh;
    
    @Override
    public void bind(final ControlWithState controlWithState) {
        Intrinsics.checkParameterIsNotNull(controlWithState, "cws");
        final ControlViewHolder cvh = this.cvh;
        if (cvh == null) {
            Intrinsics.throwUninitializedPropertyAccessException("cvh");
            throw null;
        }
        final TextView status = cvh.getStatus();
        final Control control = controlWithState.getControl();
        CharSequence statusText = null;
        Label_0044: {
            if (control != null) {
                statusText = control.getStatusText();
                if (statusText != null) {
                    break Label_0044;
                }
            }
            statusText = "";
        }
        status.setText(statusText);
        final ControlViewHolder cvh2 = this.cvh;
        if (cvh2 != null) {
            ControlViewHolder.applyRenderInfo$frameworks__base__packages__SystemUI__android_common__SystemUI_core$default(cvh2, false, 0, 2, null);
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
