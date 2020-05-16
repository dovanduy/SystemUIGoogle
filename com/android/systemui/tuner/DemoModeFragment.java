// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import com.android.internal.logging.MetricsLogger;
import android.view.MenuItem;
import androidx.preference.PreferenceScreen;
import android.content.Context;
import com.android.systemui.R$string;
import android.os.Bundle;
import android.content.ContentResolver;
import android.os.Build$VERSION;
import android.content.Intent;
import android.provider.Settings$Global;
import android.os.Handler;
import android.os.Looper;
import androidx.preference.SwitchPreference;
import android.database.ContentObserver;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;

public class DemoModeFragment extends PreferenceFragment implements OnPreferenceChangeListener
{
    private static final String[] STATUS_ICONS;
    private final ContentObserver mDemoModeObserver;
    private SwitchPreference mEnabledSwitch;
    private SwitchPreference mOnSwitch;
    
    static {
        STATUS_ICONS = new String[] { "volume", "bluetooth", "location", "alarm", "zen", "sync", "tty", "eri", "mute", "speakerphone", "managed_profile" };
    }
    
    public DemoModeFragment() {
        this.mDemoModeObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            public void onChange(final boolean b) {
                DemoModeFragment.this.updateDemoModeEnabled();
                DemoModeFragment.this.updateDemoModeOn();
            }
        };
    }
    
    private void setGlobal(final String s, final int n) {
        Settings$Global.putInt(this.getContext().getContentResolver(), s, n);
    }
    
    private void startDemoMode() {
        final Intent intent = new Intent("com.android.systemui.demo");
        intent.putExtra("command", "enter");
        this.getContext().sendBroadcast(intent);
        intent.putExtra("command", "clock");
        int i = 0;
        String format;
        try {
            format = String.format("%02d00", Integer.valueOf(Build$VERSION.RELEASE_OR_CODENAME.split("\\.")[0]) % 24);
        }
        catch (IllegalArgumentException ex) {
            format = "1010";
        }
        intent.putExtra("hhmm", format);
        this.getContext().sendBroadcast(intent);
        intent.putExtra("command", "network");
        intent.putExtra("wifi", "show");
        intent.putExtra("mobile", "show");
        intent.putExtra("sims", "1");
        intent.putExtra("nosim", "false");
        intent.putExtra("level", "4");
        intent.putExtra("datatype", "lte");
        this.getContext().sendBroadcast(intent);
        intent.putExtra("fully", "true");
        this.getContext().sendBroadcast(intent);
        intent.putExtra("command", "battery");
        intent.putExtra("level", "100");
        intent.putExtra("plugged", "false");
        this.getContext().sendBroadcast(intent);
        intent.putExtra("command", "status");
        for (String[] status_ICONS = DemoModeFragment.STATUS_ICONS; i < status_ICONS.length; ++i) {
            intent.putExtra(status_ICONS[i], "hide");
        }
        this.getContext().sendBroadcast(intent);
        intent.putExtra("command", "notifications");
        intent.putExtra("visible", "false");
        this.getContext().sendBroadcast(intent);
        this.setGlobal("sysui_tuner_demo_on", 1);
    }
    
    private void stopDemoMode() {
        final Intent intent = new Intent("com.android.systemui.demo");
        intent.putExtra("command", "exit");
        this.getContext().sendBroadcast(intent);
        this.setGlobal("sysui_tuner_demo_on", 0);
    }
    
    private void updateDemoModeEnabled() {
        final ContentResolver contentResolver = this.getContext().getContentResolver();
        boolean b = false;
        if (Settings$Global.getInt(contentResolver, "sysui_demo_allowed", 0) != 0) {
            b = true;
        }
        this.mEnabledSwitch.setChecked(b);
        this.mOnSwitch.setEnabled(b);
    }
    
    private void updateDemoModeOn() {
        final ContentResolver contentResolver = this.getContext().getContentResolver();
        boolean checked = false;
        if (Settings$Global.getInt(contentResolver, "sysui_tuner_demo_on", 0) != 0) {
            checked = true;
        }
        this.mOnSwitch.setChecked(checked);
    }
    
    @Override
    public void onCreatePreferences(final Bundle bundle, final String s) {
        final Context context = this.getContext();
        (this.mEnabledSwitch = new SwitchPreference(context)).setTitle(R$string.enable_demo_mode);
        this.mEnabledSwitch.setOnPreferenceChangeListener((Preference.OnPreferenceChangeListener)this);
        (this.mOnSwitch = new SwitchPreference(context)).setTitle(R$string.show_demo_mode);
        this.mOnSwitch.setEnabled(false);
        this.mOnSwitch.setOnPreferenceChangeListener((Preference.OnPreferenceChangeListener)this);
        final PreferenceScreen preferenceScreen = this.getPreferenceManager().createPreferenceScreen(context);
        preferenceScreen.addPreference(this.mEnabledSwitch);
        preferenceScreen.addPreference(this.mOnSwitch);
        this.setPreferenceScreen(preferenceScreen);
        this.updateDemoModeEnabled();
        this.updateDemoModeOn();
        final ContentResolver contentResolver = this.getContext().getContentResolver();
        contentResolver.registerContentObserver(Settings$Global.getUriFor("sysui_demo_allowed"), false, this.mDemoModeObserver);
        contentResolver.registerContentObserver(Settings$Global.getUriFor("sysui_tuner_demo_on"), false, this.mDemoModeObserver);
        this.setHasOptionsMenu(true);
    }
    
    public void onDestroy() {
        this.getContext().getContentResolver().unregisterContentObserver(this.mDemoModeObserver);
        super.onDestroy();
    }
    
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            this.getFragmentManager().popBackStack();
        }
        return super.onOptionsItemSelected(menuItem);
    }
    
    public void onPause() {
        super.onPause();
        MetricsLogger.visibility(this.getContext(), 229, false);
    }
    
    @Override
    public boolean onPreferenceChange(final Preference preference, final Object o) {
        int n;
        if (o == Boolean.TRUE) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (preference == this.mEnabledSwitch) {
            if (n == 0) {
                this.mOnSwitch.setChecked(false);
                this.stopDemoMode();
            }
            MetricsLogger.action(this.getContext(), 235, (boolean)(n != 0));
            this.setGlobal("sysui_demo_allowed", n);
        }
        else {
            if (preference != this.mOnSwitch) {
                return false;
            }
            MetricsLogger.action(this.getContext(), 236, (boolean)(n != 0));
            if (n != 0) {
                this.startDemoMode();
            }
            else {
                this.stopDemoMode();
            }
        }
        return true;
    }
    
    public void onResume() {
        super.onResume();
        MetricsLogger.visibility(this.getContext(), 229, true);
    }
}
