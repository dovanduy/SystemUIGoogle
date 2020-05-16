// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.app.PendingIntent;
import android.os.UserHandle;
import android.view.View;
import android.os.Handler;
import android.content.IntentFilter;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.app.AlertDialog;
import android.content.DialogInterface$OnClickListener;
import com.android.systemui.R$string;
import android.app.AlertDialog$Builder;
import android.util.Log;
import android.content.Intent;
import android.util.AttributeSet;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.telephony.euicc.EuiccManager;
import android.view.View$OnClickListener;
import android.widget.Button;

class KeyguardEsimArea extends Button implements View$OnClickListener
{
    private EuiccManager mEuiccManager;
    private BroadcastReceiver mReceiver;
    
    public KeyguardEsimArea(final Context context) {
        this(context, null);
    }
    
    public KeyguardEsimArea(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public KeyguardEsimArea(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 16974425);
    }
    
    public KeyguardEsimArea(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if ("com.android.keyguard.disable_esim".equals(intent.getAction())) {
                    final int resultCode = this.getResultCode();
                    if (resultCode != 0) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Error disabling esim, result code = ");
                        sb.append(resultCode);
                        Log.e("KeyguardEsimArea", sb.toString());
                        final AlertDialog create = new AlertDialog$Builder(KeyguardEsimArea.this.mContext).setMessage(R$string.error_disable_esim_msg).setTitle(R$string.error_disable_esim_title).setCancelable(false).setPositiveButton(R$string.ok, (DialogInterface$OnClickListener)null).create();
                        create.getWindow().setType(2009);
                        create.show();
                    }
                }
            }
        };
        this.mEuiccManager = (EuiccManager)context.getSystemService("euicc");
        this.setOnClickListener((View$OnClickListener)this);
    }
    
    public static boolean isEsimLocked(final Context context, final int n) {
        final boolean enabled = ((EuiccManager)context.getSystemService("euicc")).isEnabled();
        final boolean b = false;
        if (!enabled) {
            return false;
        }
        final SubscriptionInfo activeSubscriptionInfo = SubscriptionManager.from(context).getActiveSubscriptionInfo(n);
        boolean b2 = b;
        if (activeSubscriptionInfo != null) {
            b2 = b;
            if (activeSubscriptionInfo.isEmbedded()) {
                b2 = true;
            }
        }
        return b2;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        super.mContext.registerReceiver(this.mReceiver, new IntentFilter("com.android.keyguard.disable_esim"), "com.android.systemui.permission.SELF", (Handler)null);
    }
    
    public void onClick(final View view) {
        final Intent intent = new Intent("com.android.keyguard.disable_esim");
        intent.setPackage(super.mContext.getPackageName());
        this.mEuiccManager.switchToSubscription(-1, PendingIntent.getBroadcastAsUser(super.mContext, 0, intent, 134217728, UserHandle.SYSTEM));
    }
    
    protected void onDetachedFromWindow() {
        super.mContext.unregisterReceiver(this.mReceiver);
        super.onDetachedFromWindow();
    }
}
