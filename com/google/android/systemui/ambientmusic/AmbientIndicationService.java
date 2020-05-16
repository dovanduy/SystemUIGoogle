// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.ambientmusic;

import com.android.systemui.Dependency;
import android.os.UserHandle;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.SystemClock;
import android.app.PendingIntent;
import com.android.keyguard.KeyguardUpdateMonitor;
import android.util.Log;
import android.content.Intent;
import java.util.Objects;
import android.app.AlarmManager$OnAlarmListener;
import android.content.Context;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;

public class AmbientIndicationService extends BroadcastReceiver
{
    private final AlarmManager mAlarmManager;
    private final AmbientIndicationContainer mAmbientIndicationContainer;
    private final KeyguardUpdateMonitorCallback mCallback;
    private final Context mContext;
    private final AlarmManager$OnAlarmListener mHideIndicationListener;
    
    public AmbientIndicationService(final Context mContext, final AmbientIndicationContainer mAmbientIndicationContainer) {
        this.mCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onUserSwitchComplete(final int n) {
                AmbientIndicationService.this.onUserSwitched();
            }
        };
        this.mContext = mContext;
        this.mAmbientIndicationContainer = mAmbientIndicationContainer;
        this.mAlarmManager = (AlarmManager)mContext.getSystemService((Class)AlarmManager.class);
        final AmbientIndicationContainer mAmbientIndicationContainer2 = this.mAmbientIndicationContainer;
        Objects.requireNonNull(mAmbientIndicationContainer2);
        this.mHideIndicationListener = (AlarmManager$OnAlarmListener)new _$$Lambda$CT70lRm8UvIGQZviGm_5gLAghcs(mAmbientIndicationContainer2);
        this.start();
    }
    
    private boolean verifyAmbientApiVersion(final Intent intent) {
        final int intExtra = intent.getIntExtra("com.google.android.ambientindication.extra.VERSION", 0);
        if (intExtra != 1) {
            final StringBuilder sb = new StringBuilder();
            sb.append("AmbientIndicationApi.EXTRA_VERSION is ");
            sb.append(1);
            sb.append(", but received an intent with version ");
            sb.append(intExtra);
            sb.append(", dropping intent.");
            Log.e("AmbientIndication", sb.toString());
            return false;
        }
        return true;
    }
    
    int getCurrentUser() {
        return KeyguardUpdateMonitor.getCurrentUser();
    }
    
    boolean isForCurrentUser() {
        return this.getSendingUserId() == this.getCurrentUser() || this.getSendingUserId() == -1;
    }
    
    public void onReceive(final Context context, final Intent intent) {
        if (!this.isForCurrentUser()) {
            Log.i("AmbientIndication", "Suppressing ambient, not for this user.");
            return;
        }
        if (!this.verifyAmbientApiVersion(intent)) {
            return;
        }
        if (this.mAmbientIndicationContainer.isMediaPlaying()) {
            Log.i("AmbientIndication", "Suppressing ambient intent due to media playback.");
            return;
        }
        final String action = intent.getAction();
        int n = -1;
        final int hashCode = action.hashCode();
        if (hashCode != -1032272569) {
            if (hashCode == -1031945470) {
                if (action.equals("com.google.android.ambientindication.action.AMBIENT_INDICATION_SHOW")) {
                    n = 0;
                }
            }
        }
        else if (action.equals("com.google.android.ambientindication.action.AMBIENT_INDICATION_HIDE")) {
            n = 1;
        }
        if (n != 0) {
            if (n == 1) {
                this.mAlarmManager.cancel(this.mHideIndicationListener);
                this.mAmbientIndicationContainer.hideAmbientMusic();
                Log.i("AmbientIndication", "Hiding ambient indication.");
            }
        }
        else {
            final CharSequence charSequenceExtra = intent.getCharSequenceExtra("com.google.android.ambientindication.extra.TEXT");
            final PendingIntent pendingIntent = (PendingIntent)intent.getParcelableExtra("com.google.android.ambientindication.extra.OPEN_INTENT");
            final long min = Math.min(Math.max(intent.getLongExtra("com.google.android.ambientindication.extra.TTL_MILLIS", 180000L), 0L), 180000L);
            this.mAmbientIndicationContainer.setAmbientMusic(charSequenceExtra, pendingIntent, intent.getBooleanExtra("com.google.android.ambientindication.extra.SKIP_UNLOCK", false));
            this.mAlarmManager.setExact(2, SystemClock.elapsedRealtime() + min, "AmbientIndication", this.mHideIndicationListener, (Handler)null);
            Log.i("AmbientIndication", "Showing ambient indication.");
        }
    }
    
    void onUserSwitched() {
        this.mAmbientIndicationContainer.hideAmbientMusic();
    }
    
    void start() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.google.android.ambientindication.action.AMBIENT_INDICATION_SHOW");
        intentFilter.addAction("com.google.android.ambientindication.action.AMBIENT_INDICATION_HIDE");
        this.mContext.registerReceiverAsUser((BroadcastReceiver)this, UserHandle.ALL, intentFilter, "com.google.android.ambientindication.permission.AMBIENT_INDICATION", (Handler)null);
        Dependency.get(KeyguardUpdateMonitor.class).registerCallback(this.mCallback);
    }
}
