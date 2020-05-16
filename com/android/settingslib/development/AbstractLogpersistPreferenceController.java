// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.development;

import com.android.settingslib.R$array;
import android.os.SystemProperties;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import androidx.preference.ListPreference;
import com.android.settingslib.core.ConfirmationDialogController;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.core.lifecycle.events.OnCreate;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import androidx.preference.Preference;

public abstract class AbstractLogpersistPreferenceController extends DeveloperOptionsPreferenceController implements OnPreferenceChangeListener, LifecycleObserver, OnCreate, OnDestroy, ConfirmationDialogController
{
    static final String ACTUAL_LOGPERSIST_PROPERTY = "logd.logpersistd";
    static final String ACTUAL_LOGPERSIST_PROPERTY_BUFFER = "logd.logpersistd.buffer";
    static final String SELECT_LOGPERSIST_PROPERTY_SERVICE = "logcatd";
    private ListPreference mLogpersist;
    private boolean mLogpersistCleared;
    private final BroadcastReceiver mReceiver;
    
    @Override
    public void onCreate(final Bundle bundle) {
        LocalBroadcastManager.getInstance(super.mContext).registerReceiver(this.mReceiver, new IntentFilter("com.android.settingslib.development.AbstractLogdSizePreferenceController.LOGD_SIZE_UPDATED"));
    }
    
    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(super.mContext).unregisterReceiver(this.mReceiver);
    }
    
    @Override
    public boolean onPreferenceChange(final Preference preference, final Object o) {
        if (preference == this.mLogpersist) {
            this.writeLogpersistOption(o, false);
            return true;
        }
        return false;
    }
    
    protected void setLogpersistOff(final boolean b) {
        SystemProperties.set("persist.logd.logpersistd.buffer", "");
        SystemProperties.set("logd.logpersistd.buffer", "");
        SystemProperties.set("persist.logd.logpersistd", "");
        String s;
        if (b) {
            s = "";
        }
        else {
            s = "stop";
        }
        SystemProperties.set("logd.logpersistd", s);
        SystemPropPoker.getInstance().poke();
        if (b) {
            this.updateLogpersistValues();
            return;
        }
        int n = 0;
    Label_0092_Outer:
        while (true) {
            if (n >= 3) {
                return;
            }
            final String value = SystemProperties.get("logd.logpersistd");
            if (value == null) {
                return;
            }
            if (value.equals("")) {
                return;
            }
            while (true) {
                try {
                    Thread.sleep(100L);
                    ++n;
                    continue Label_0092_Outer;
                }
                catch (InterruptedException ex) {
                    continue;
                }
                break;
            }
            break;
        }
    }
    
    public void updateLogpersistValues() {
        if (this.mLogpersist == null) {
            return;
        }
        String value;
        if ((value = SystemProperties.get("logd.logpersistd")) == null) {
            value = "";
        }
        final String value2 = SystemProperties.get("logd.logpersistd.buffer");
        String s = null;
        Label_0045: {
            if (value2 != null) {
                s = value2;
                if (value2.length() != 0) {
                    break Label_0045;
                }
            }
            s = "all";
        }
        int n = 0;
        Label_0186: {
            if (value.equals("logcatd")) {
                if (s.equals("kernel")) {
                    n = 3;
                }
                else {
                    if (!s.equals("all") && !s.contains("radio") && s.contains("security") && s.contains("kernel")) {
                        final int n2 = n = 2;
                        if (s.contains("default")) {
                            break Label_0186;
                        }
                        int n3 = 0;
                        while (true) {
                            n = n2;
                            if (n3 >= 4) {
                                break Label_0186;
                            }
                            if (!s.contains((new String[] { "main", "events", "system", "crash" })[n3])) {
                                break;
                            }
                            ++n3;
                        }
                    }
                    n = 1;
                }
            }
            else {
                n = 0;
            }
        }
        this.mLogpersist.setValue(super.mContext.getResources().getStringArray(R$array.select_logpersist_values)[n]);
        this.mLogpersist.setSummary(super.mContext.getResources().getStringArray(R$array.select_logpersist_summaries)[n]);
        if (n != 0) {
            this.mLogpersistCleared = false;
        }
        else if (!this.mLogpersistCleared) {
            SystemProperties.set("logd.logpersistd", "clear");
            SystemPropPoker.getInstance().poke();
            this.mLogpersistCleared = true;
        }
    }
    
    public void writeLogpersistOption(final Object o, final boolean b) {
        if (this.mLogpersist == null) {
            return;
        }
        final String value = SystemProperties.get("persist.log.tag");
        Object o2 = o;
        boolean b2 = b;
        if (value != null) {
            o2 = o;
            b2 = b;
            if (value.startsWith("Settings")) {
                o2 = null;
                b2 = true;
            }
        }
        int n = 0;
        Label_0164: {
            if (o2 == null || o2.toString().equals("")) {
                break Label_0164;
            }
            final String value2 = SystemProperties.get("logd.logpersistd.buffer");
            if (value2 != null && !value2.equals(o2.toString())) {
                this.setLogpersistOff(false);
            }
            SystemProperties.set("persist.logd.logpersistd.buffer", o2.toString());
            SystemProperties.set("persist.logd.logpersistd", "logcatd");
            SystemPropPoker.getInstance().poke();
        Label_0153_Outer:
            while (true) {
                Label_0159: {
                    if (n >= 3) {
                        break Label_0159;
                    }
                    final String value3 = SystemProperties.get("logd.logpersistd");
                    if (value3 != null && value3.equals("logcatd")) {
                        break Label_0159;
                    }
                Block_12_Outer:
                    while (true) {
                        try {
                            Thread.sleep(100L);
                            ++n;
                            continue Label_0153_Outer;
                        Block_13_Outer:
                            while (true) {
                                while (true) {
                                    this.setLogpersistOff(true);
                                    return;
                                    this.mLogpersistCleared = false;
                                    continue Block_12_Outer;
                                }
                            Block_15:
                                while (true) {
                                    final String value4 = SystemProperties.get("logd.logpersistd");
                                    break Block_15;
                                    Label_0177: {
                                        continue;
                                    }
                                }
                                this.showConfirmationDialog(this.mLogpersist);
                                return;
                                this.updateLogpersistValues();
                                return;
                                continue Block_13_Outer;
                            }
                        }
                        // iftrue(Label_0214:, value4 == null || !value4.equals((Object)"logcatd"))
                        // iftrue(Label_0214:, this.mLogpersistCleared)
                        // iftrue(Label_0177:, !b2)
                        catch (InterruptedException ex) {
                            continue;
                        }
                        break;
                    }
                }
                break;
            }
        }
    }
}
