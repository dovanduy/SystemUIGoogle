// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.face;

import android.content.Intent;
import android.hardware.face.FaceManager$RemovalCallback;
import android.hardware.face.Face;
import android.util.Log;
import android.hardware.face.FaceManager;
import android.content.DialogInterface;
import android.content.DialogInterface$OnClickListener;
import com.android.systemui.R$string;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import android.app.Dialog;
import android.content.Context;

class FaceNotificationDialogFactory
{
    static Dialog createReenrollDialog(final Context context) {
        final SystemUIDialog systemUIDialog = new SystemUIDialog(context);
        systemUIDialog.setTitle((CharSequence)context.getString(R$string.face_reenroll_dialog_title));
        systemUIDialog.setMessage((CharSequence)context.getString(R$string.face_reenroll_dialog_content));
        systemUIDialog.setPositiveButton(R$string.face_reenroll_dialog_confirm, (DialogInterface$OnClickListener)new _$$Lambda$FaceNotificationDialogFactory$SFpfsh8DYX4qrLFYVa7F66Qyw3w(context));
        systemUIDialog.setNegativeButton(R$string.face_reenroll_dialog_cancel, (DialogInterface$OnClickListener)_$$Lambda$FaceNotificationDialogFactory$ANAG9nE7_GNJRcbPxX_2zLgUycA.INSTANCE);
        return (Dialog)systemUIDialog;
    }
    
    private static Dialog createReenrollFailureDialog(final Context context) {
        final SystemUIDialog systemUIDialog = new SystemUIDialog(context);
        systemUIDialog.setMessage(context.getText(R$string.face_reenroll_failure_dialog_content));
        systemUIDialog.setPositiveButton(R$string.ok, (DialogInterface$OnClickListener)_$$Lambda$FaceNotificationDialogFactory$7zJqTeZCSXyg4d2Ga0nGEgfLdTI.INSTANCE);
        return (Dialog)systemUIDialog;
    }
    
    private static void onReenrollDialogConfirm(final Context context) {
        final FaceManager faceManager = (FaceManager)context.getSystemService((Class)FaceManager.class);
        if (faceManager == null) {
            Log.e("FaceNotificationDialogF", "Not launching enrollment. Face manager was null!");
            createReenrollFailureDialog(context).show();
            return;
        }
        faceManager.remove(new Face((CharSequence)"", 0, 0L), context.getUserId(), (FaceManager$RemovalCallback)new FaceManager$RemovalCallback() {
            boolean mDidShowFailureDialog;
            
            public void onRemovalError(final Face face, final int n, final CharSequence charSequence) {
                Log.e("FaceNotificationDialogF", "Not launching enrollment. Failed to remove existing face(s).");
                if (!this.mDidShowFailureDialog) {
                    this.mDidShowFailureDialog = true;
                    createReenrollFailureDialog(context).show();
                }
            }
            
            public void onRemovalSucceeded(final Face face, final int n) {
                if (!this.mDidShowFailureDialog && n == 0) {
                    final Intent intent = new Intent("android.settings.BIOMETRIC_ENROLL");
                    intent.setPackage("com.android.settings");
                    intent.setFlags(268435456);
                    context.startActivity(intent);
                }
            }
        });
    }
}
