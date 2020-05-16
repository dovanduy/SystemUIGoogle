// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.ui;

import android.service.controls.actions.CommandAction;
import android.service.controls.actions.ControlAction;
import android.service.controls.actions.BooleanAction;
import android.app.PendingIntent;
import android.content.Context;
import android.service.controls.Control;
import android.app.PendingIntent$CanceledException;
import android.util.Log;
import android.content.Intent;
import android.provider.Settings$Secure;
import kotlin.jvm.internal.Intrinsics;

public final class ControlActionCoordinator
{
    public static final ControlActionCoordinator INSTANCE;
    private static Boolean useDetailDialog;
    
    static {
        INSTANCE = new ControlActionCoordinator();
    }
    
    private ControlActionCoordinator() {
    }
    
    public final void longPress(final ControlViewHolder controlViewHolder) {
        Intrinsics.checkParameterIsNotNull(controlViewHolder, "cvh");
        final Control control = controlViewHolder.getCws().getControl();
        if (control != null) {
            if (ControlActionCoordinator.useDetailDialog == null) {
                ControlActionCoordinator.useDetailDialog = (Settings$Secure.getInt(controlViewHolder.getContext().getContentResolver(), "systemui.controls_use_detail_panel", 0) != 0);
            }
            try {
                controlViewHolder.getLayout().performHapticFeedback(0);
                final Boolean useDetailDialog = ControlActionCoordinator.useDetailDialog;
                if (useDetailDialog == null) {
                    Intrinsics.throwNpe();
                    throw null;
                }
                if (useDetailDialog) {
                    final Context context = controlViewHolder.getContext();
                    final PendingIntent appIntent = control.getAppIntent();
                    Intrinsics.checkExpressionValueIsNotNull(appIntent, "it.getAppIntent()");
                    new DetailDialog(context, appIntent).show();
                }
                else {
                    control.getAppIntent().send();
                    controlViewHolder.getContext().sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
                }
            }
            catch (PendingIntent$CanceledException ex) {
                Log.e("ControlsUiController", "Error sending pending intent", (Throwable)ex);
                controlViewHolder.setTransientStatus("Error opening application");
            }
        }
    }
    
    public final void toggle(final ControlViewHolder controlViewHolder, final String s, final boolean b) {
        Intrinsics.checkParameterIsNotNull(controlViewHolder, "cvh");
        Intrinsics.checkParameterIsNotNull(s, "templateId");
        controlViewHolder.action((ControlAction)new BooleanAction(s, b ^ true));
        int level;
        if (b) {
            level = 0;
        }
        else {
            level = 10000;
        }
        controlViewHolder.getClipLayer().setLevel(level);
    }
    
    public final void touch(final ControlViewHolder controlViewHolder, final String s) {
        Intrinsics.checkParameterIsNotNull(controlViewHolder, "cvh");
        Intrinsics.checkParameterIsNotNull(s, "templateId");
        controlViewHolder.action((ControlAction)new CommandAction(s));
    }
}
