// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.deviceinfo;

import android.os.Message;
import java.lang.ref.WeakReference;
import android.text.format.DateUtils;
import android.os.SystemClock;
import androidx.preference.Preference;
import android.os.Handler;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.AbstractPreferenceController;

public abstract class AbstractUptimePreferenceController extends AbstractPreferenceController implements LifecycleObserver, OnStart, OnStop
{
    static final String KEY_UPTIME = "up_time";
    private Handler mHandler;
    private Preference mUptime;
    
    private Handler getHandler() {
        if (this.mHandler == null) {
            this.mHandler = new MyHandler(this);
        }
        return this.mHandler;
    }
    
    private void updateTimes() {
        this.mUptime.setSummary(DateUtils.formatElapsedTime(SystemClock.elapsedRealtime() / 1000L));
    }
    
    @Override
    public void onStart() {
        this.getHandler().sendEmptyMessage(500);
    }
    
    @Override
    public void onStop() {
        this.getHandler().removeMessages(500);
    }
    
    private static class MyHandler extends Handler
    {
        private WeakReference<AbstractUptimePreferenceController> mStatus;
        
        public MyHandler(final AbstractUptimePreferenceController referent) {
            this.mStatus = new WeakReference<AbstractUptimePreferenceController>(referent);
        }
        
        public void handleMessage(final Message message) {
            final AbstractUptimePreferenceController abstractUptimePreferenceController = this.mStatus.get();
            if (abstractUptimePreferenceController == null) {
                return;
            }
            if (message.what == 500) {
                abstractUptimePreferenceController.updateTimes();
                this.sendEmptyMessageDelayed(500, 1000L);
                return;
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("Unknown message ");
            sb.append(message.what);
            throw new IllegalStateException(sb.toString());
        }
    }
}
