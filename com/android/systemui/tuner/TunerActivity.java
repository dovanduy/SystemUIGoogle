// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import androidx.preference.PreferenceScreen;
import android.app.FragmentTransaction;
import android.util.Log;
import androidx.preference.Preference;
import android.view.MenuItem;
import java.util.function.Consumer;
import com.android.systemui.Dependency;
import android.app.Fragment;
import com.android.systemui.R$id;
import android.widget.Toolbar;
import com.android.systemui.R$layout;
import android.os.Bundle;
import com.android.systemui.fragments.FragmentService;
import androidx.preference.PreferenceFragment;
import android.app.Activity;

public class TunerActivity extends Activity implements OnPreferenceStartFragmentCallback, OnPreferenceStartScreenCallback
{
    TunerActivity() {
    }
    
    public void onBackPressed() {
        if (!this.getFragmentManager().popBackStackImmediate()) {
            super.onBackPressed();
        }
    }
    
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.getWindow().addFlags(Integer.MIN_VALUE);
        boolean b = true;
        this.requestWindowFeature(1);
        this.setContentView(R$layout.tuner_activity);
        final Toolbar actionBar = (Toolbar)this.findViewById(R$id.action_bar);
        if (actionBar != null) {
            this.setActionBar(actionBar);
        }
        if (this.getFragmentManager().findFragmentByTag("tuner") == null) {
            final String action = this.getIntent().getAction();
            if (action == null || !action.equals("com.android.settings.action.DEMO_MODE")) {
                b = false;
            }
            PreferenceFragment preferenceFragment;
            if (b) {
                preferenceFragment = new DemoModeFragment();
            }
            else {
                preferenceFragment = new TunerFragment();
            }
            this.getFragmentManager().beginTransaction().replace(R$id.content_frame, (Fragment)preferenceFragment, "tuner").commit();
        }
    }
    
    protected void onDestroy() {
        super.onDestroy();
        Dependency.destroy(FragmentService.class, (Consumer<FragmentService>)_$$Lambda$TunerActivity$RI23eCWQLUIRemsdYo0hJRYd5ug.INSTANCE);
    }
    
    public boolean onMenuItemSelected(final int n, final MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            this.onBackPressed();
            return true;
        }
        return super.onMenuItemSelected(n, menuItem);
    }
    
    public boolean onPreferenceStartFragment(final PreferenceFragment preferenceFragment, final Preference preference) {
        try {
            final Fragment fragment = (Fragment)Class.forName(preference.getFragment()).newInstance();
            final Bundle arguments = new Bundle(1);
            arguments.putString("androidx.preference.PreferenceFragmentCompat.PREFERENCE_ROOT", preference.getKey());
            fragment.setArguments(arguments);
            final FragmentTransaction beginTransaction = this.getFragmentManager().beginTransaction();
            this.setTitle(preference.getTitle());
            beginTransaction.replace(R$id.content_frame, fragment);
            beginTransaction.addToBackStack("PreferenceFragment");
            beginTransaction.commit();
            return true;
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            final Throwable t;
            Log.d("TunerActivity", "Problem launching fragment", t);
            return false;
        }
    }
    
    public boolean onPreferenceStartScreen(final PreferenceFragment preferenceFragment, final PreferenceScreen preferenceScreen) {
        final FragmentTransaction beginTransaction = this.getFragmentManager().beginTransaction();
        final SubSettingsFragment subSettingsFragment = new SubSettingsFragment();
        final Bundle arguments = new Bundle(1);
        arguments.putString("androidx.preference.PreferenceFragmentCompat.PREFERENCE_ROOT", preferenceScreen.getKey());
        subSettingsFragment.setArguments(arguments);
        subSettingsFragment.setTargetFragment((Fragment)preferenceFragment, 0);
        beginTransaction.replace(R$id.content_frame, (Fragment)subSettingsFragment);
        beginTransaction.addToBackStack("PreferenceFragment");
        beginTransaction.commit();
        return true;
    }
    
    public static class SubSettingsFragment extends PreferenceFragment
    {
        private PreferenceScreen mParentScreen;
        
        @Override
        public void onCreatePreferences(final Bundle bundle, final String s) {
            this.mParentScreen = ((PreferenceFragment)this.getTargetFragment()).getPreferenceScreen().findPreference(s);
            final PreferenceScreen preferenceScreen = this.getPreferenceManager().createPreferenceScreen(this.getPreferenceManager().getContext());
            this.setPreferenceScreen(preferenceScreen);
            while (this.mParentScreen.getPreferenceCount() > 0) {
                final Preference preference = this.mParentScreen.getPreference(0);
                this.mParentScreen.removePreference(preference);
                preferenceScreen.addPreference(preference);
            }
        }
        
        public void onDestroy() {
            super.onDestroy();
            final PreferenceScreen preferenceScreen = this.getPreferenceScreen();
            while (preferenceScreen.getPreferenceCount() > 0) {
                final Preference preference = preferenceScreen.getPreference(0);
                preferenceScreen.removePreference(preference);
                this.mParentScreen.addPreference(preference);
            }
        }
    }
}
