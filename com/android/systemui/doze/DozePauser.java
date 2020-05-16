// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

import android.app.AlarmManager$OnAlarmListener;
import android.app.AlarmManager;
import android.os.Handler;
import com.android.systemui.util.AlarmTimeout;

public class DozePauser implements Part
{
    public static final String TAG = "DozePauser";
    private final DozeMachine mMachine;
    private final AlarmTimeout mPauseTimeout;
    private final AlwaysOnDisplayPolicy mPolicy;
    
    public DozePauser(final Handler handler, final DozeMachine mMachine, final AlarmManager alarmManager, final AlwaysOnDisplayPolicy mPolicy) {
        this.mMachine = mMachine;
        this.mPauseTimeout = new AlarmTimeout(alarmManager, (AlarmManager$OnAlarmListener)new _$$Lambda$DozePauser$RaYrBg9_HgEkLP8ozxXkVSg4K5c(this), DozePauser.TAG, handler);
        this.mPolicy = mPolicy;
    }
    
    private void onTimeout() {
        this.mMachine.requestState(State.DOZE_AOD_PAUSED);
    }
    
    @Override
    public void transitionTo(final State state, final State state2) {
        if (DozePauser$1.$SwitchMap$com$android$systemui$doze$DozeMachine$State[state2.ordinal()] != 1) {
            this.mPauseTimeout.cancel();
        }
        else {
            this.mPauseTimeout.schedule(this.mPolicy.proxScreenOffDelayMs, 1);
        }
    }
}
