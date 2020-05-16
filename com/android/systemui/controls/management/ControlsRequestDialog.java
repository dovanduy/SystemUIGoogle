// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import com.android.systemui.settings.CurrentUserTracker;
import android.util.Log;
import android.os.Bundle;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.view.View;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.R$string;
import android.app.AlertDialog$Builder;
import com.android.systemui.R$dimen;
import android.widget.TextView;
import com.android.systemui.R$id;
import android.widget.ImageView;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.content.Context;
import com.android.systemui.controls.ui.RenderInfo;
import java.util.Iterator;
import java.util.List;
import com.android.systemui.controls.controller.ControlInfo;
import com.android.systemui.controls.controller.StructureInfo;
import java.util.Collection;
import kotlin.jvm.internal.Intrinsics;
import android.app.Dialog;
import com.android.systemui.controls.controller.ControlsController;
import android.service.controls.Control;
import android.content.ComponentName;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.content.DialogInterface$OnCancelListener;
import android.content.DialogInterface$OnClickListener;
import com.android.systemui.util.LifecycleActivity;

public final class ControlsRequestDialog extends LifecycleActivity implements DialogInterface$OnClickListener, DialogInterface$OnCancelListener
{
    private final BroadcastDispatcher broadcastDispatcher;
    private final ControlsRequestDialog$callback.ControlsRequestDialog$callback$1 callback;
    private ComponentName component;
    private Control control;
    private final ControlsController controller;
    private final ControlsListingController controlsListingController;
    private final ControlsRequestDialog$currentUserTracker.ControlsRequestDialog$currentUserTracker$1 currentUserTracker;
    private Dialog dialog;
    
    public ControlsRequestDialog(final ControlsController controller, final BroadcastDispatcher broadcastDispatcher, final ControlsListingController controlsListingController) {
        Intrinsics.checkParameterIsNotNull(controller, "controller");
        Intrinsics.checkParameterIsNotNull(broadcastDispatcher, "broadcastDispatcher");
        Intrinsics.checkParameterIsNotNull(controlsListingController, "controlsListingController");
        this.controller = controller;
        this.broadcastDispatcher = broadcastDispatcher;
        this.controlsListingController = controlsListingController;
        this.callback = new ControlsRequestDialog$callback.ControlsRequestDialog$callback$1();
        this.currentUserTracker = new ControlsRequestDialog$currentUserTracker.ControlsRequestDialog$currentUserTracker$1(this, this.broadcastDispatcher);
    }
    
    private final boolean isCurrentFavorite() {
        final ControlsController controller = this.controller;
        final ComponentName component = this.component;
        if (component != null) {
            final List<StructureInfo> favoritesForComponent = controller.getFavoritesForComponent(component);
            final boolean b = favoritesForComponent instanceof Collection;
            boolean b2 = true;
            if (!b || !favoritesForComponent.isEmpty()) {
                final Iterator<Object> iterator = favoritesForComponent.iterator();
                while (iterator.hasNext()) {
                    final List<ControlInfo> controls = iterator.next().getControls();
                    boolean b3 = false;
                    Label_0167: {
                        if (!(controls instanceof Collection) || !controls.isEmpty()) {
                            final Iterator<Object> iterator2 = controls.iterator();
                            while (iterator2.hasNext()) {
                                final String controlId = iterator2.next().getControlId();
                                final Control control = this.control;
                                if (control == null) {
                                    Intrinsics.throwUninitializedPropertyAccessException("control");
                                    throw null;
                                }
                                if (Intrinsics.areEqual(controlId, control.getControlId())) {
                                    b3 = true;
                                    break Label_0167;
                                }
                            }
                        }
                        b3 = false;
                    }
                    if (b3) {
                        return b2;
                    }
                }
            }
            b2 = false;
            return b2;
        }
        Intrinsics.throwUninitializedPropertyAccessException("component");
        throw null;
    }
    
    private final CharSequence verifyComponentAndGetLabel() {
        final ControlsListingController controlsListingController = this.controlsListingController;
        final ComponentName component = this.component;
        if (component != null) {
            return controlsListingController.getAppLabel(component);
        }
        Intrinsics.throwUninitializedPropertyAccessException("component");
        throw null;
    }
    
    public final Dialog createDialog(final CharSequence charSequence) {
        Intrinsics.checkParameterIsNotNull(charSequence, "label");
        final RenderInfo.Companion companion = RenderInfo.Companion;
        final ComponentName component = this.component;
        if (component == null) {
            Intrinsics.throwUninitializedPropertyAccessException("component");
            throw null;
        }
        final Control control = this.control;
        if (control == null) {
            Intrinsics.throwUninitializedPropertyAccessException("control");
            throw null;
        }
        final RenderInfo lookup$default = RenderInfo.Companion.lookup$default(companion, (Context)this, component, control.getDeviceType(), true, 0, 16, null);
        final View inflate = LayoutInflater.from((Context)this).inflate(R$layout.controls_dialog, (ViewGroup)null);
        final ImageView imageView = (ImageView)inflate.requireViewById(R$id.icon);
        imageView.setImageDrawable(lookup$default.getIcon());
        final Context context = imageView.getContext();
        Intrinsics.checkExpressionValueIsNotNull(context, "context");
        final Resources resources = context.getResources();
        final int foreground = lookup$default.getForeground();
        final Context context2 = imageView.getContext();
        Intrinsics.checkExpressionValueIsNotNull(context2, "context");
        imageView.setImageTintList(resources.getColorStateList(foreground, context2.getTheme()));
        final View requireViewById = inflate.requireViewById(R$id.title);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById, "requireViewById<TextView>(R.id.title)");
        final TextView textView = (TextView)requireViewById;
        final Control control2 = this.control;
        if (control2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("control");
            throw null;
        }
        textView.setText(control2.getTitle());
        final View requireViewById2 = inflate.requireViewById(R$id.subtitle);
        Intrinsics.checkExpressionValueIsNotNull(requireViewById2, "requireViewById<TextView>(R.id.subtitle)");
        final TextView textView2 = (TextView)requireViewById2;
        final Control control3 = this.control;
        if (control3 != null) {
            textView2.setText(control3.getSubtitle());
            final View requireViewById3 = inflate.requireViewById(R$id.control);
            Intrinsics.checkExpressionValueIsNotNull(requireViewById3, "requireViewById<View>(R.id.control)");
            requireViewById3.setElevation(inflate.getResources().getFloat(R$dimen.control_card_elevation));
            final AlertDialog create = new AlertDialog$Builder((Context)this).setTitle((CharSequence)this.getString(R$string.controls_dialog_title)).setMessage((CharSequence)this.getString(R$string.controls_dialog_message, new Object[] { charSequence })).setPositiveButton(R$string.controls_dialog_ok, (DialogInterface$OnClickListener)this).setNegativeButton(17039360, (DialogInterface$OnClickListener)this).setOnCancelListener((DialogInterface$OnCancelListener)this).setView(inflate).create();
            SystemUIDialog.registerDismissListener((Dialog)create);
            create.setCanceledOnTouchOutside(true);
            Intrinsics.checkExpressionValueIsNotNull(create, "dialog");
            return (Dialog)create;
        }
        Intrinsics.throwUninitializedPropertyAccessException("control");
        throw null;
    }
    
    public void onCancel(final DialogInterface dialogInterface) {
        this.finish();
    }
    
    public void onClick(final DialogInterface dialogInterface, final int n) {
        if (n == -1) {
            final ControlsController controller = this.controller;
            final ComponentName componentName = this.getComponentName();
            Intrinsics.checkExpressionValueIsNotNull(componentName, "componentName");
            final Control control = this.control;
            if (control == null) {
                Intrinsics.throwUninitializedPropertyAccessException("control");
                throw null;
            }
            CharSequence structure = control.getStructure();
            if (structure == null) {
                structure = "";
            }
            final Control control2 = this.control;
            if (control2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("control");
                throw null;
            }
            final String controlId = control2.getControlId();
            Intrinsics.checkExpressionValueIsNotNull(controlId, "control.controlId");
            final Control control3 = this.control;
            if (control3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("control");
                throw null;
            }
            final CharSequence title = control3.getTitle();
            Intrinsics.checkExpressionValueIsNotNull(title, "control.title");
            final Control control4 = this.control;
            if (control4 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("control");
                throw null;
            }
            final CharSequence subtitle = control4.getSubtitle();
            Intrinsics.checkExpressionValueIsNotNull(subtitle, "control.subtitle");
            final Control control5 = this.control;
            if (control5 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("control");
                throw null;
            }
            controller.addFavorite(componentName, structure, new ControlInfo(controlId, title, subtitle, control5.getDeviceType()));
        }
        this.finish();
    }
    
    @Override
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        if (!this.controller.getAvailable()) {
            Log.w("ControlsRequestDialog", "Quick Controls not available for this user ");
            this.finish();
        }
        ((CurrentUserTracker)this.currentUserTracker).startTracking();
        this.controlsListingController.addCallback((ControlsListingController.ControlsListingCallback)this.callback);
        final int intExtra = this.getIntent().getIntExtra("android.intent.extra.USER_ID", -10000);
        final int currentUserId = this.controller.getCurrentUserId();
        if (intExtra != currentUserId) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Current user (");
            sb.append(currentUserId);
            sb.append(") different from request user (");
            sb.append(intExtra);
            sb.append(')');
            Log.w("ControlsRequestDialog", sb.toString());
            this.finish();
        }
        final ComponentName component = (ComponentName)this.getIntent().getParcelableExtra("android.intent.extra.COMPONENT_NAME");
        if (component == null) {
            Log.e("ControlsRequestDialog", "Request did not contain componentName");
            this.finish();
            return;
        }
        this.component = component;
        final Control control = (Control)this.getIntent().getParcelableExtra("android.service.controls.extra.CONTROL");
        if (control != null) {
            this.control = control;
            return;
        }
        Log.e("ControlsRequestDialog", "Request did not contain control");
        this.finish();
    }
    
    @Override
    protected void onDestroy() {
        final Dialog dialog = this.dialog;
        if (dialog != null) {
            dialog.dismiss();
        }
        ((CurrentUserTracker)this.currentUserTracker).stopTracking();
        this.controlsListingController.removeCallback((ControlsListingController.ControlsListingCallback)this.callback);
        super.onDestroy();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        final CharSequence verifyComponentAndGetLabel = this.verifyComponentAndGetLabel();
        if (verifyComponentAndGetLabel != null) {
            if (this.isCurrentFavorite()) {
                final StringBuilder sb = new StringBuilder();
                sb.append("The control ");
                final Control control = this.control;
                if (control == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("control");
                    throw null;
                }
                sb.append(control.getTitle());
                sb.append(" is already a favorite");
                Log.w("ControlsRequestDialog", sb.toString());
                this.finish();
            }
            final Dialog dialog = this.createDialog(verifyComponentAndGetLabel);
            if ((this.dialog = dialog) != null) {
                dialog.show();
            }
            return;
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("The component specified (");
        final ComponentName component = this.component;
        if (component != null) {
            sb2.append(component.flattenToString());
            sb2.append(' ');
            sb2.append("is not a valid ControlsProviderService");
            Log.e("ControlsRequestDialog", sb2.toString());
            this.finish();
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("component");
        throw null;
    }
}
