// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.smartspace;

import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import android.os.UserHandle;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager$NameNotFoundException;
import android.util.Log;
import android.content.Intent;
import android.content.Context;
import com.android.systemui.smartspace.nano.SmartspaceProto$SmartspaceUpdate;
import android.content.BroadcastReceiver;

public class SmartSpaceBroadcastReceiver extends BroadcastReceiver
{
    private final SmartSpaceController mController;
    
    public SmartSpaceBroadcastReceiver(final SmartSpaceController mController) {
        this.mController = mController;
    }
    
    private void notify(final SmartspaceProto$SmartspaceUpdate.SmartspaceCard smartspaceCard, final Context context, final Intent intent, final boolean b) {
        final long currentTimeMillis = System.currentTimeMillis();
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo("com.google.android.googlequicksearchbox", 0);
        }
        catch (PackageManager$NameNotFoundException ex) {
            Log.w("SmartSpaceReceiver", "Cannot find GSA", (Throwable)ex);
            packageInfo = null;
        }
        this.mController.onNewCard(new NewCardInfo(smartspaceCard, intent, b, currentTimeMillis, packageInfo));
    }
    
    public void onReceive(final Context context, final Intent intent) {
        if (SmartSpaceController.DEBUG) {
            Log.d("SmartSpaceReceiver", "receiving update");
        }
        final int myUserId = UserHandle.myUserId();
        if (myUserId == 0) {
            if (!intent.hasExtra("uid")) {
                intent.putExtra("uid", myUserId);
            }
            final byte[] byteArrayExtra = intent.getByteArrayExtra("com.google.android.apps.nexuslauncher.extra.SMARTSPACE_CARD");
            if (byteArrayExtra != null) {
                final SmartspaceProto$SmartspaceUpdate smartspaceProto$SmartspaceUpdate = new SmartspaceProto$SmartspaceUpdate();
                try {
                    MessageNano.mergeFrom(smartspaceProto$SmartspaceUpdate, byteArrayExtra);
                    for (final SmartspaceProto$SmartspaceUpdate.SmartspaceCard smartspaceCard : smartspaceProto$SmartspaceUpdate.card) {
                        final boolean b = smartspaceCard.cardPriority == 1;
                        final boolean b2 = smartspaceCard.cardPriority == 2;
                        if (!b && !b2) {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("unrecognized card priority: ");
                            sb.append(smartspaceCard.cardPriority);
                            Log.w("SmartSpaceReceiver", sb.toString());
                        }
                        else {
                            this.notify(smartspaceCard, context, intent, b);
                        }
                    }
                    return;
                }
                catch (InvalidProtocolBufferNanoException ex) {
                    Log.e("SmartSpaceReceiver", "proto", (Throwable)ex);
                    return;
                }
            }
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("receiving update with no proto: ");
            sb2.append(intent.getExtras());
            Log.e("SmartSpaceReceiver", sb2.toString());
            return;
        }
        if (intent.getBooleanExtra("rebroadcast", false)) {
            return;
        }
        intent.putExtra("rebroadcast", true);
        intent.putExtra("uid", myUserId);
        context.sendBroadcastAsUser(intent, UserHandle.ALL);
    }
}
