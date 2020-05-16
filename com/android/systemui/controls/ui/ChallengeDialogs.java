// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.ui;

import android.view.Window;
import android.content.DialogInterface$OnShowListener;
import com.android.systemui.R$layout;
import android.app.AlertDialog;
import android.content.DialogInterface$OnClickListener;
import com.android.systemui.R$string;
import android.app.AlertDialog$Builder;
import android.util.Log;
import kotlin.jvm.internal.Intrinsics;
import android.app.Dialog;
import android.service.controls.actions.ModeAction;
import android.service.controls.actions.CommandAction;
import android.service.controls.actions.FloatAction;
import android.service.controls.actions.BooleanAction;
import android.widget.EditText;
import android.service.controls.actions.ControlAction;

public final class ChallengeDialogs
{
    public static final ChallengeDialogs INSTANCE;
    
    static {
        INSTANCE = new ChallengeDialogs();
    }
    
    private ChallengeDialogs() {
    }
    
    private final ControlAction addChallengeValue(final ControlAction obj, final String s) {
        final String templateId = obj.getTemplateId();
        Object o;
        if (obj instanceof BooleanAction) {
            o = new BooleanAction(templateId, ((BooleanAction)obj).getNewState(), s);
        }
        else if (obj instanceof FloatAction) {
            o = new FloatAction(templateId, ((FloatAction)obj).getNewValue(), s);
        }
        else if (obj instanceof CommandAction) {
            o = new CommandAction(templateId, s);
        }
        else {
            if (!(obj instanceof ModeAction)) {
                final StringBuilder sb = new StringBuilder();
                sb.append("'action' is not a known type: ");
                sb.append(obj);
                throw new IllegalStateException(sb.toString());
            }
            o = new ModeAction(templateId, ((ModeAction)obj).getNewMode(), s);
        }
        return (ControlAction)o;
    }
    
    private final void setInputType(final EditText editText, final boolean b) {
        if (b) {
            editText.setInputType(129);
        }
        else {
            editText.setInputType(18);
        }
    }
    
    public final Dialog createConfirmationDialog(final ControlViewHolder controlViewHolder) {
        Intrinsics.checkParameterIsNotNull(controlViewHolder, "cvh");
        final ControlAction lastAction = controlViewHolder.getLastAction();
        if (lastAction == null) {
            Log.e("ControlsUiController", "Confirmation Dialog attempted but no last action is set. Will not show");
            return null;
        }
        final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(controlViewHolder.getContext(), 16974545);
        alertDialog$Builder.setMessage((CharSequence)controlViewHolder.getContext().getResources().getString(R$string.controls_confirmation_message, new Object[] { controlViewHolder.getTitle().getText() }));
        alertDialog$Builder.setPositiveButton(17039370, (DialogInterface$OnClickListener)new ChallengeDialogs$createConfirmationDialog$$inlined$apply$lambda.ChallengeDialogs$createConfirmationDialog$$inlined$apply$lambda$1(controlViewHolder, lastAction));
        alertDialog$Builder.setNegativeButton(17039360, (DialogInterface$OnClickListener)ChallengeDialogs$createConfirmationDialog$builder$1.ChallengeDialogs$createConfirmationDialog$builder$1$2.INSTANCE);
        final AlertDialog create = alertDialog$Builder.create();
        create.getWindow().setType(2020);
        return (Dialog)create;
    }
    
    public final Dialog createPinDialog(final ControlViewHolder controlViewHolder, final boolean b) {
        Intrinsics.checkParameterIsNotNull(controlViewHolder, "cvh");
        final ControlAction lastAction = controlViewHolder.getLastAction();
        if (lastAction == null) {
            Log.e("ControlsUiController", "PIN Dialog attempted but no last action is set. Will not show");
            return null;
        }
        final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(controlViewHolder.getContext(), 16974545);
        alertDialog$Builder.setTitle((CharSequence)controlViewHolder.getContext().getResources().getString(R$string.controls_pin_verify, new Object[] { controlViewHolder.getTitle().getText() }));
        alertDialog$Builder.setView(R$layout.controls_dialog_pin);
        alertDialog$Builder.setPositiveButton(17039370, (DialogInterface$OnClickListener)new ChallengeDialogs$createPinDialog$$inlined$apply$lambda.ChallengeDialogs$createPinDialog$$inlined$apply$lambda$1(controlViewHolder, lastAction));
        alertDialog$Builder.setNegativeButton(17039360, (DialogInterface$OnClickListener)ChallengeDialogs$createPinDialog$builder$1.ChallengeDialogs$createPinDialog$builder$1$2.INSTANCE);
        final AlertDialog create = alertDialog$Builder.create();
        final Window window = create.getWindow();
        window.setType(2020);
        window.setSoftInputMode(4);
        create.setOnShowListener((DialogInterface$OnShowListener)new ChallengeDialogs$createPinDialog$$inlined$apply$lambda.ChallengeDialogs$createPinDialog$$inlined$apply$lambda$2(create, b));
        return (Dialog)create;
    }
}
