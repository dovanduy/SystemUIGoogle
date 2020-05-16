// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import java.util.ArrayList;
import android.content.DialogInterface;
import android.content.DialogInterface$OnMultiChoiceClickListener;
import android.app.AlertDialog$Builder;
import java.util.Collection;
import android.os.Bundle;
import java.util.HashSet;
import java.util.Set;

@Deprecated
public class MultiSelectListPreferenceDialogFragment extends PreferenceDialogFragment
{
    CharSequence[] mEntries;
    CharSequence[] mEntryValues;
    Set<String> mNewValues;
    boolean mPreferenceChanged;
    
    @Deprecated
    public MultiSelectListPreferenceDialogFragment() {
        this.mNewValues = new HashSet<String>();
    }
    
    private MultiSelectListPreference getListPreference() {
        return (MultiSelectListPreference)this.getPreference();
    }
    
    @Deprecated
    public static MultiSelectListPreferenceDialogFragment newInstance(final String s) {
        final MultiSelectListPreferenceDialogFragment multiSelectListPreferenceDialogFragment = new MultiSelectListPreferenceDialogFragment();
        final Bundle arguments = new Bundle(1);
        arguments.putString("key", s);
        multiSelectListPreferenceDialogFragment.setArguments(arguments);
        return multiSelectListPreferenceDialogFragment;
    }
    
    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        if (bundle == null) {
            final MultiSelectListPreference listPreference = this.getListPreference();
            if (listPreference.getEntries() == null || listPreference.getEntryValues() == null) {
                throw new IllegalStateException("MultiSelectListPreference requires an entries array and an entryValues array.");
            }
            this.mNewValues.clear();
            this.mNewValues.addAll(listPreference.getValues());
            this.mPreferenceChanged = false;
            this.mEntries = listPreference.getEntries();
            this.mEntryValues = listPreference.getEntryValues();
        }
        else {
            this.mNewValues.clear();
            this.mNewValues.addAll(bundle.getStringArrayList("MultiSelectListPreferenceDialogFragment.values"));
            this.mPreferenceChanged = bundle.getBoolean("MultiSelectListPreferenceDialogFragment.changed", false);
            this.mEntries = bundle.getCharSequenceArray("MultiSelectListPreferenceDialogFragment.entries");
            this.mEntryValues = bundle.getCharSequenceArray("MultiSelectListPreferenceDialogFragment.entryValues");
        }
    }
    
    @Deprecated
    @Override
    public void onDialogClosed(final boolean b) {
        final MultiSelectListPreference listPreference = this.getListPreference();
        if (b && this.mPreferenceChanged) {
            final Set<String> mNewValues = this.mNewValues;
            if (listPreference.callChangeListener(mNewValues)) {
                listPreference.setValues(mNewValues);
            }
        }
        this.mPreferenceChanged = false;
    }
    
    @Override
    protected void onPrepareDialogBuilder(final AlertDialog$Builder alertDialog$Builder) {
        super.onPrepareDialogBuilder(alertDialog$Builder);
        final int length = this.mEntryValues.length;
        final boolean[] array = new boolean[length];
        for (int i = 0; i < length; ++i) {
            array[i] = this.mNewValues.contains(this.mEntryValues[i].toString());
        }
        alertDialog$Builder.setMultiChoiceItems(this.mEntries, array, (DialogInterface$OnMultiChoiceClickListener)new DialogInterface$OnMultiChoiceClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n, final boolean b) {
                if (b) {
                    final MultiSelectListPreferenceDialogFragment this$0 = MultiSelectListPreferenceDialogFragment.this;
                    this$0.mPreferenceChanged |= this$0.mNewValues.add(this$0.mEntryValues[n].toString());
                }
                else {
                    final MultiSelectListPreferenceDialogFragment this$2 = MultiSelectListPreferenceDialogFragment.this;
                    this$2.mPreferenceChanged |= this$2.mNewValues.remove(this$2.mEntryValues[n].toString());
                }
            }
        });
    }
    
    @Override
    public void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putStringArrayList("MultiSelectListPreferenceDialogFragment.values", new ArrayList((Collection<? extends E>)this.mNewValues));
        bundle.putBoolean("MultiSelectListPreferenceDialogFragment.changed", this.mPreferenceChanged);
        bundle.putCharSequenceArray("MultiSelectListPreferenceDialogFragment.entries", this.mEntries);
        bundle.putCharSequenceArray("MultiSelectListPreferenceDialogFragment.entryValues", this.mEntryValues);
    }
}
