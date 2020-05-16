// 
// Decompiled by Procyon v0.5.36
// 

package androidx.remotecallback;

import android.net.Uri$Builder;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;

public class ProviderRelayReceiver extends BroadcastReceiver
{
    public void onReceive(final Context context, final Intent intent) {
        if ("androidx.remotecallback.action.PROVIDER_RELAY".equals(intent.getAction())) {
            context.getContentResolver().call(new Uri$Builder().scheme("content").authority(intent.getStringExtra("androidx.remotecallback.extra.AUTHORITY")).build(), "androidx.remotecallback.method.PROVIDER_CALLBACK", (String)null, intent.getExtras());
        }
    }
}
