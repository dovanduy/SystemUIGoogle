// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import android.content.DialogInterface;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import com.android.internal.logging.MetricsLogger;
import android.view.MenuItem;
import androidx.preference.Preference;
import android.provider.Settings$Secure;
import android.os.Build;
import com.android.systemui.shared.plugins.PluginPrefs;
import com.android.systemui.R$xml;
import com.android.systemui.R$string;
import android.view.MenuInflater;
import android.view.Menu;
import android.os.Bundle;
import android.hardware.display.AmbientDisplayConfiguration;
import androidx.preference.PreferenceFragment;

public class TunerFragment extends PreferenceFragment
{
    private static final String[] DEBUG_ONLY;
    private static final CharSequence KEY_DOZE;
    
    static {
        KEY_DOZE = "doze";
        DEBUG_ONLY = new String[] { "nav_bar", "lockscreen", "picture_in_picture" };
    }
    
    private boolean alwaysOnAvailable() {
        return new AmbientDisplayConfiguration(this.getContext()).alwaysOnAvailable();
    }
    
    public void onActivityCreated(final Bundle bundle) {
        super.onActivityCreated(bundle);
        this.getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.setHasOptionsMenu(true);
    }
    
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater menuInflater) {
        menu.add(0, 2, 0, R$string.remove_from_settings);
    }
    
    @Override
    public void onCreatePreferences(final Bundle bundle, final String s) {
        this.addPreferencesFromResource(R$xml.tuner_prefs);
        if (!PluginPrefs.hasPlugins(this.getContext())) {
            this.getPreferenceScreen().removePreference(this.findPreference("plugins"));
        }
        if (!this.alwaysOnAvailable()) {
            this.getPreferenceScreen().removePreference(this.findPreference(TunerFragment.KEY_DOZE));
        }
        if (!Build.IS_DEBUGGABLE) {
            int n = 0;
            while (true) {
                final String[] debug_ONLY = TunerFragment.DEBUG_ONLY;
                if (n >= debug_ONLY.length) {
                    break;
                }
                final Preference preference = this.findPreference(debug_ONLY[n]);
                if (preference != null) {
                    this.getPreferenceScreen().removePreference(preference);
                }
                ++n;
            }
        }
        if (Settings$Secure.getInt(this.getContext().getContentResolver(), "seen_tuner_warning", 0) == 0 && this.getFragmentManager().findFragmentByTag("tuner_warning") == null) {
            new TunerWarningFragment().show(this.getFragmentManager(), "tuner_warning");
        }
    }
    
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        final int itemId = menuItem.getItemId();
        if (itemId == 2) {
            TunerService.showResetRequest(this.getContext(), new Runnable() {
                @Override
                public void run() {
                    if (TunerFragment.this.getActivity() != null) {
                        TunerFragment.this.getActivity().finish();
                    }
                }
            });
            return true;
        }
        if (itemId != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        this.getActivity().finish();
        return true;
    }
    
    public void onPause() {
        super.onPause();
        MetricsLogger.visibility(this.getContext(), 227, false);
    }
    
    public void onResume() {
        super.onResume();
        this.getActivity().setTitle(R$string.system_ui_tuner);
        MetricsLogger.visibility(this.getContext(), 227, true);
    }
    
    public static class TunerWarningFragment extends DialogFragment
    {
        public Dialog onCreateDialog(final Bundle bundle) {
            return (Dialog)new AlertDialog$Builder(this.getContext()).setTitle(R$string.tuner_warning_title).setMessage(R$string.tuner_warning).setPositiveButton(R$string.got_it, (DialogInterface$OnClickListener)new DialogInterface$OnClickListener() {
                public void onClick(final DialogInterface dialogInterface, final int n) {
                    Settings$Secure.putInt(TunerWarningFragment.this.getContext().getContentResolver(), "seen_tuner_warning", 1);
                }
            }).show();
        }
    }
}
