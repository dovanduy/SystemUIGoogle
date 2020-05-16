// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.controller;

import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.controls.ControlStatus;
import java.util.List;

public final class ControlsControllerKt
{
    public static final ControlsController.LoadData createLoadDataObject(final List<ControlStatus> list, final List<String> list2, final boolean b) {
        Intrinsics.checkParameterIsNotNull(list, "allControls");
        Intrinsics.checkParameterIsNotNull(list2, "favorites");
        return (ControlsController.LoadData)new ControlsControllerKt$createLoadDataObject.ControlsControllerKt$createLoadDataObject$1((List)list, (List)list2, b);
    }
}
