// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import android.widget.SpinnerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.util.AttributeSet;
import android.widget.Spinner;
import android.widget.AdapterView$OnItemSelectedListener;
import android.content.Context;
import android.widget.ArrayAdapter;

public class DropDownPreference extends ListPreference
{
    private final ArrayAdapter mAdapter;
    private final Context mContext;
    private final AdapterView$OnItemSelectedListener mItemSelectedListener;
    private Spinner mSpinner;
    
    public DropDownPreference(final Context context) {
        this(context, null);
    }
    
    public DropDownPreference(final Context context, final AttributeSet set) {
        this(context, set, R$attr.dropdownPreferenceStyle);
    }
    
    public DropDownPreference(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public DropDownPreference(final Context mContext, final AttributeSet set, final int n, final int n2) {
        super(mContext, set, n, n2);
        this.mItemSelectedListener = (AdapterView$OnItemSelectedListener)new AdapterView$OnItemSelectedListener() {
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int n, final long n2) {
                if (n >= 0) {
                    final String string = DropDownPreference.this.getEntryValues()[n].toString();
                    if (!string.equals(DropDownPreference.this.getValue()) && DropDownPreference.this.callChangeListener(string)) {
                        DropDownPreference.this.setValue(string);
                    }
                }
            }
            
            public void onNothingSelected(final AdapterView<?> adapterView) {
            }
        };
        this.mContext = mContext;
        this.mAdapter = this.createAdapter();
        this.updateEntries();
    }
    
    private int findSpinnerIndexOfValue(final String s) {
        final CharSequence[] entryValues = this.getEntryValues();
        if (s != null && entryValues != null) {
            for (int i = entryValues.length - 1; i >= 0; --i) {
                if (TextUtils.equals((CharSequence)entryValues[i].toString(), (CharSequence)s)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    private void updateEntries() {
        this.mAdapter.clear();
        if (this.getEntries() != null) {
            final CharSequence[] entries = this.getEntries();
            for (int length = entries.length, i = 0; i < length; ++i) {
                this.mAdapter.add((Object)entries[i].toString());
            }
        }
    }
    
    protected ArrayAdapter createAdapter() {
        return new ArrayAdapter(this.mContext, 17367049);
    }
    
    @Override
    protected void notifyChanged() {
        super.notifyChanged();
        final ArrayAdapter mAdapter = this.mAdapter;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
    
    @Override
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        (this.mSpinner = (Spinner)preferenceViewHolder.itemView.findViewById(R$id.spinner)).setAdapter((SpinnerAdapter)this.mAdapter);
        this.mSpinner.setOnItemSelectedListener(this.mItemSelectedListener);
        this.mSpinner.setSelection(this.findSpinnerIndexOfValue(this.getValue()));
        super.onBindViewHolder(preferenceViewHolder);
    }
    
    @Override
    protected void onClick() {
        this.mSpinner.performClick();
    }
    
    @Override
    public void setEntries(final CharSequence[] entries) {
        super.setEntries(entries);
        this.updateEntries();
    }
}
