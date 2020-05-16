// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import android.content.DialogInterface;
import java.util.Objects;
import androidx.preference.Preference;
import android.app.AlertDialog$Builder;
import android.os.Bundle;
import android.app.Fragment;
import com.android.systemui.fragments.FragmentHostManager;
import android.view.View$OnClickListener;
import com.android.settingslib.Utils;
import com.android.systemui.R$id;
import android.widget.Toolbar;
import android.app.DialogFragment;
import android.view.View;
import android.app.Dialog;
import android.util.AttributeSet;
import android.content.Context;
import android.content.DialogInterface$OnClickListener;

public class RadioListPreference extends CustomListPreference
{
    private DialogInterface$OnClickListener mOnClickListener;
    private CharSequence mSummary;
    
    public RadioListPreference(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    @Override
    public CharSequence getSummary() {
        final CharSequence mSummary = this.mSummary;
        if (mSummary != null && !mSummary.toString().contains("%s")) {
            return this.mSummary;
        }
        return super.getSummary();
    }
    
    @Override
    protected void onDialogClosed(final boolean b) {
        super.onDialogClosed(b);
    }
    
    @Override
    protected Dialog onDialogCreated(final DialogFragment dialogFragment, final Dialog dialog) {
        final Dialog dialog2 = new Dialog(this.getContext(), 16974371);
        final Toolbar toolbar = (Toolbar)dialog2.findViewById(16908704);
        final View contentView = new View(this.getContext());
        contentView.setId(R$id.content);
        dialog2.setContentView(contentView);
        toolbar.setTitle(this.getTitle());
        toolbar.setNavigationIcon(Utils.getDrawable(dialog2.getContext(), 16843531));
        toolbar.setNavigationOnClickListener((View$OnClickListener)new _$$Lambda$RadioListPreference$4DEUOALD3KxT1NUXowELf_5ZJ2M(dialog2));
        final RadioFragment radioFragment = new RadioFragment();
        radioFragment.setPreference(this);
        FragmentHostManager.get(contentView).getFragmentManager().beginTransaction().add(16908290, (Fragment)radioFragment).commit();
        return dialog2;
    }
    
    @Override
    protected void onDialogStateRestored(final DialogFragment dialogFragment, final Dialog dialog, final Bundle bundle) {
        super.onDialogStateRestored(dialogFragment, dialog, bundle);
        final RadioFragment radioFragment = (RadioFragment)FragmentHostManager.get(dialog.findViewById(R$id.content)).getFragmentManager().findFragmentById(R$id.content);
        if (radioFragment != null) {
            radioFragment.setPreference(this);
        }
    }
    
    @Override
    protected void onPrepareDialogBuilder(final AlertDialog$Builder alertDialog$Builder, final DialogInterface$OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }
    
    @Override
    public void setSummary(final CharSequence charSequence) {
        super.setSummary(charSequence);
        this.mSummary = charSequence;
    }
    
    public static class RadioFragment extends TunerPreferenceFragment
    {
        private RadioListPreference mListPref;
        
        private void update() {
            final Context context = this.getPreferenceManager().getContext();
            final CharSequence[] entries = this.mListPref.getEntries();
            final CharSequence[] entryValues = this.mListPref.getEntryValues();
            final String value = this.mListPref.getValue();
            for (int i = 0; i < entries.length; ++i) {
                final CharSequence title = entries[i];
                final SelectablePreference selectablePreference = new SelectablePreference(context);
                this.getPreferenceScreen().addPreference(selectablePreference);
                selectablePreference.setTitle(title);
                selectablePreference.setChecked(Objects.equals(value, entryValues[i]));
                selectablePreference.setKey(String.valueOf(i));
            }
        }
        
        @Override
        public void onCreatePreferences(final Bundle bundle, final String s) {
            this.setPreferenceScreen(this.getPreferenceManager().createPreferenceScreen(this.getPreferenceManager().getContext()));
            if (this.mListPref != null) {
                this.update();
            }
        }
        
        @Override
        public boolean onPreferenceTreeClick(final Preference preference) {
            this.mListPref.mOnClickListener.onClick((DialogInterface)null, Integer.parseInt(preference.getKey()));
            return true;
        }
        
        public void setPreference(final RadioListPreference mListPref) {
            this.mListPref = mListPref;
            if (this.getPreferenceManager() != null) {
                this.update();
            }
        }
    }
}
