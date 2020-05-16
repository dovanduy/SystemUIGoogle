// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.ui;

import android.widget.TextView;
import kotlin.jvm.internal.Intrinsics;

public final class UnknownBehavior implements Behavior
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
        final ControlViewHolder cvh2 = this.cvh;
        if (cvh2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("cvh");
            throw null;
        }
        status.setText((CharSequence)cvh2.getContext().getString(17040425));
        final ControlViewHolder cvh3 = this.cvh;
        if (cvh3 != null) {
            ControlViewHolder.applyRenderInfo$frameworks__base__packages__SystemUI__android_common__SystemUI_core$default(cvh3, false, 0, 2, null);
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
