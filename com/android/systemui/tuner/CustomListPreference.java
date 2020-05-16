// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import android.content.DialogInterface;
import androidx.preference.ListPreferenceDialogFragment;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import android.os.Bundle;
import android.app.Dialog;
import android.app.DialogFragment;
import android.util.AttributeSet;
import android.content.Context;
import androidx.preference.ListPreference;

public class CustomListPreference extends ListPreference
{
    public CustomListPreference(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public CustomListPreference(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
    }
    
    protected boolean isAutoClosePreference() {
        return true;
    }
    
    protected void onDialogClosed(final boolean b) {
    }
    
    protected Dialog onDialogCreated(final DialogFragment dialogFragment, final Dialog dialog) {
        return dialog;
    }
    
    protected void onDialogStateRestored(final DialogFragment dialogFragment, final Dialog dialog, final Bundle bundle) {
    }
    
    protected void onPrepareDialogBuilder(final AlertDialog$Builder alertDialog$Builder, final DialogInterface$OnClickListener dialogInterface$OnClickListener) {
    }
    
    public static class CustomListPreferenceDialogFragment extends ListPreferenceDialogFragment
    {
        private int mClickedDialogEntryIndex;
        
        private String getValue() {
            final CustomListPreference customizablePreference = this.getCustomizablePreference();
            if (this.mClickedDialogEntryIndex >= 0 && customizablePreference.getEntryValues() != null) {
                return customizablePreference.getEntryValues()[this.mClickedDialogEntryIndex].toString();
            }
            return null;
        }
        
        public static ListPreferenceDialogFragment newInstance(final String s) {
            final CustomListPreferenceDialogFragment customListPreferenceDialogFragment = new CustomListPreferenceDialogFragment();
            final Bundle arguments = new Bundle(1);
            arguments.putString("key", s);
            customListPreferenceDialogFragment.setArguments(arguments);
            return customListPreferenceDialogFragment;
        }
        
        public CustomListPreference getCustomizablePreference() {
            return (CustomListPreference)this.getPreference();
        }
        
        protected DialogInterface$OnClickListener getOnItemClickListener() {
            return (DialogInterface$OnClickListener)new DialogInterface$OnClickListener() {
                public void onClick(final DialogInterface dialogInterface, final int clickedDialogEntryIndex) {
                    CustomListPreferenceDialogFragment.this.setClickedDialogEntryIndex(clickedDialogEntryIndex);
                    if (CustomListPreferenceDialogFragment.this.getCustomizablePreference().isAutoClosePreference()) {
                        CustomListPreferenceDialogFragment.this.onItemConfirmed();
                    }
                }
            };
        }
        
        public void onActivityCreated(final Bundle bundle) {
            super.onActivityCreated(bundle);
            this.getCustomizablePreference().onDialogStateRestored(this, this.getDialog(), bundle);
        }
        
        @Override
        public Dialog onCreateDialog(final Bundle bundle) {
            final Dialog onCreateDialog = super.onCreateDialog(bundle);
            if (bundle != null) {
                this.mClickedDialogEntryIndex = bundle.getInt("settings.CustomListPrefDialog.KEY_CLICKED_ENTRY_INDEX", this.mClickedDialogEntryIndex);
            }
            return this.getCustomizablePreference().onDialogCreated(this, onCreateDialog);
        }
        
        @Override
        public void onDialogClosed(final boolean b) {
            this.getCustomizablePreference().onDialogClosed(b);
            final CustomListPreference customizablePreference = this.getCustomizablePreference();
            final String value = this.getValue();
            if (b && value != null && customizablePreference.callChangeListener(value)) {
                customizablePreference.setValue(value);
            }
        }
        
        protected void onItemConfirmed() {
            this.onClick((DialogInterface)this.getDialog(), -1);
            this.getDialog().dismiss();
        }
        
        @Override
        protected void onPrepareDialogBuilder(final AlertDialog$Builder alertDialog$Builder) {
            super.onPrepareDialogBuilder(alertDialog$Builder);
            this.mClickedDialogEntryIndex = this.getCustomizablePreference().findIndexOfValue(this.getCustomizablePreference().getValue());
            this.getCustomizablePreference().onPrepareDialogBuilder(alertDialog$Builder, this.getOnItemClickListener());
            if (!this.getCustomizablePreference().isAutoClosePreference()) {
                alertDialog$Builder.setPositiveButton(17039370, (DialogInterface$OnClickListener)new DialogInterface$OnClickListener() {
                    public void onClick(final DialogInterface dialogInterface, final int n) {
                        CustomListPreferenceDialogFragment.this.onItemConfirmed();
                    }
                });
            }
        }
        
        @Override
        public void onSaveInstanceState(final Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putInt("settings.CustomListPrefDialog.KEY_CLICKED_ENTRY_INDEX", this.mClickedDialogEntryIndex);
        }
        
        protected void setClickedDialogEntryIndex(final int mClickedDialogEntryIndex) {
            this.mClickedDialogEntryIndex = mClickedDialogEntryIndex;
        }
    }
}
