// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import android.content.DialogInterface;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import android.os.Bundle;

@Deprecated
public class ListPreferenceDialogFragment extends PreferenceDialogFragment
{
    int mClickedDialogEntryIndex;
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    
    @Deprecated
    public ListPreferenceDialogFragment() {
    }
    
    private ListPreference getListPreference() {
        return (ListPreference)this.getPreference();
    }
    
    @Deprecated
    public static ListPreferenceDialogFragment newInstance(final String s) {
        final ListPreferenceDialogFragment listPreferenceDialogFragment = new ListPreferenceDialogFragment();
        final Bundle arguments = new Bundle(1);
        arguments.putString("key", s);
        listPreferenceDialogFragment.setArguments(arguments);
        return listPreferenceDialogFragment;
    }
    
    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        if (bundle == null) {
            final ListPreference listPreference = this.getListPreference();
            if (listPreference.getEntries() == null || listPreference.getEntryValues() == null) {
                throw new IllegalStateException("ListPreference requires an entries array and an entryValues array.");
            }
            this.mClickedDialogEntryIndex = listPreference.findIndexOfValue(listPreference.getValue());
            this.mEntries = listPreference.getEntries();
            this.mEntryValues = listPreference.getEntryValues();
        }
        else {
            this.mClickedDialogEntryIndex = bundle.getInt("ListPreferenceDialogFragment.index", 0);
            this.mEntries = bundle.getCharSequenceArray("ListPreferenceDialogFragment.entries");
            this.mEntryValues = bundle.getCharSequenceArray("ListPreferenceDialogFragment.entryValues");
        }
    }
    
    @Deprecated
    @Override
    public void onDialogClosed(final boolean b) {
        final ListPreference listPreference = this.getListPreference();
        if (b) {
            final int mClickedDialogEntryIndex = this.mClickedDialogEntryIndex;
            if (mClickedDialogEntryIndex >= 0) {
                final String string = this.mEntryValues[mClickedDialogEntryIndex].toString();
                if (listPreference.callChangeListener(string)) {
                    listPreference.setValue(string);
                }
            }
        }
    }
    
    @Override
    protected void onPrepareDialogBuilder(final AlertDialog$Builder alertDialog$Builder) {
        super.onPrepareDialogBuilder(alertDialog$Builder);
        alertDialog$Builder.setSingleChoiceItems(this.mEntries, this.mClickedDialogEntryIndex, (DialogInterface$OnClickListener)new DialogInterface$OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int mClickedDialogEntryIndex) {
                final ListPreferenceDialogFragment this$0 = ListPreferenceDialogFragment.this;
                this$0.mClickedDialogEntryIndex = mClickedDialogEntryIndex;
                this$0.onClick(dialogInterface, -1);
                dialogInterface.dismiss();
            }
        });
        alertDialog$Builder.setPositiveButton((CharSequence)null, (DialogInterface$OnClickListener)null);
    }
    
    @Override
    public void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("ListPreferenceDialogFragment.index", this.mClickedDialogEntryIndex);
        bundle.putCharSequenceArray("ListPreferenceDialogFragment.entries", this.mEntries);
        bundle.putCharSequenceArray("ListPreferenceDialogFragment.entryValues", this.mEntryValues);
    }
}
