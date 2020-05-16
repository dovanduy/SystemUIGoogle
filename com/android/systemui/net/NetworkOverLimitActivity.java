// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.net;

import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface$OnDismissListener;
import android.content.DialogInterface;
import android.content.DialogInterface$OnClickListener;
import android.content.Context;
import android.app.AlertDialog$Builder;
import android.os.Bundle;
import android.net.INetworkPolicyManager;
import android.os.RemoteException;
import android.util.Log;
import android.net.INetworkPolicyManager$Stub;
import android.os.ServiceManager;
import com.android.systemui.R$string;
import android.net.NetworkTemplate;
import android.app.Activity;

public class NetworkOverLimitActivity extends Activity
{
    private static int getLimitedDialogTitleForTemplate(final NetworkTemplate networkTemplate) {
        if (networkTemplate.getMatchRule() != 1) {
            return R$string.data_usage_disabled_dialog_title;
        }
        return R$string.data_usage_disabled_dialog_mobile_title;
    }
    
    private void snoozePolicy(final NetworkTemplate networkTemplate) {
        final INetworkPolicyManager interface1 = INetworkPolicyManager$Stub.asInterface(ServiceManager.getService("netpolicy"));
        try {
            interface1.snoozeLimit(networkTemplate);
        }
        catch (RemoteException ex) {
            Log.w("NetworkOverLimitActivity", "problem snoozing network policy", (Throwable)ex);
        }
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        final NetworkTemplate networkTemplate = (NetworkTemplate)this.getIntent().getParcelableExtra("android.net.NETWORK_TEMPLATE");
        final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder((Context)this);
        alertDialog$Builder.setTitle(getLimitedDialogTitleForTemplate(networkTemplate));
        alertDialog$Builder.setMessage(R$string.data_usage_disabled_dialog);
        alertDialog$Builder.setPositiveButton(17039370, (DialogInterface$OnClickListener)null);
        alertDialog$Builder.setNegativeButton(R$string.data_usage_disabled_dialog_enable, (DialogInterface$OnClickListener)new DialogInterface$OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                NetworkOverLimitActivity.this.snoozePolicy(networkTemplate);
            }
        });
        final AlertDialog create = alertDialog$Builder.create();
        ((Dialog)create).getWindow().setType(2003);
        ((Dialog)create).setOnDismissListener((DialogInterface$OnDismissListener)new DialogInterface$OnDismissListener() {
            public void onDismiss(final DialogInterface dialogInterface) {
                NetworkOverLimitActivity.this.finish();
            }
        });
        ((Dialog)create).show();
    }
}
