// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.ui;

import android.view.WindowManager$LayoutParams;
import android.view.Window;
import android.view.View;
import com.android.systemui.R$id;
import android.view.ViewGroup;
import android.util.AttributeSet;
import com.android.systemui.R$layout;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import android.app.ActivityView$StateCallback;
import android.app.PendingIntent;
import android.app.ActivityView;
import android.app.Dialog;

public final class DetailDialog extends Dialog
{
    private ActivityView activityView;
    private final PendingIntent intent;
    private final ActivityView$StateCallback stateCallback;
    
    public DetailDialog(final Context context, final PendingIntent intent) {
        Intrinsics.checkParameterIsNotNull(context, "parentContext");
        Intrinsics.checkParameterIsNotNull(intent, "intent");
        super(context);
        this.intent = intent;
        this.stateCallback = (ActivityView$StateCallback)new DetailDialog$stateCallback.DetailDialog$stateCallback$1(this);
        final Window window = this.getWindow();
        if (window != null) {
            this.setWindowParams(window);
        }
        this.setContentView(R$layout.controls_detail_dialog);
        this.activityView = new ActivityView(this.getContext(), (AttributeSet)null, 0, false);
        ((ViewGroup)this.requireViewById(R$id.controls_activity_view)).addView((View)this.activityView);
    }
    
    private final void setWindowParams(final Window window) {
        window.requestFeature(1);
        window.getDecorView();
        window.getAttributes().systemUiVisibility = (window.getAttributes().systemUiVisibility | 0x400 | 0x100);
        window.setLayout(-1, -1);
        window.clearFlags(2);
        window.addFlags(16843008);
        window.setType(2020);
        window.getAttributes().setFitInsetsTypes(0);
    }
    
    public void dismiss() {
        this.activityView.release();
        super.dismiss();
    }
    
    public final PendingIntent getIntent() {
        return this.intent;
    }
    
    public void show() {
        final Window window = this.getWindow();
        WindowManager$LayoutParams attributes;
        if (window != null) {
            attributes = window.getAttributes();
        }
        else {
            attributes = null;
        }
        if (attributes != null) {
            attributes.layoutInDisplayCutoutMode = 3;
        }
        final Window window2 = this.getWindow();
        if (window2 != null) {
            window2.setAttributes(attributes);
        }
        this.activityView.setCallback(this.stateCallback);
        super.show();
    }
}
