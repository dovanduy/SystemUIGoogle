// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import android.view.View;
import android.os.Bundle;
import android.widget.EditText;

@Deprecated
public class EditTextPreferenceDialogFragment extends PreferenceDialogFragment
{
    private EditText mEditText;
    private CharSequence mText;
    
    @Deprecated
    public EditTextPreferenceDialogFragment() {
    }
    
    private EditTextPreference getEditTextPreference() {
        return (EditTextPreference)this.getPreference();
    }
    
    @Deprecated
    public static EditTextPreferenceDialogFragment newInstance(final String s) {
        final EditTextPreferenceDialogFragment editTextPreferenceDialogFragment = new EditTextPreferenceDialogFragment();
        final Bundle arguments = new Bundle(1);
        arguments.putString("key", s);
        editTextPreferenceDialogFragment.setArguments(arguments);
        return editTextPreferenceDialogFragment;
    }
    
    @Override
    protected boolean needInputMethod() {
        return true;
    }
    
    @Override
    protected void onBindDialogView(final View view) {
        super.onBindDialogView(view);
        (this.mEditText = (EditText)view.findViewById(16908291)).requestFocus();
        final EditText mEditText = this.mEditText;
        if (mEditText != null) {
            mEditText.setText(this.mText);
            final EditText mEditText2 = this.mEditText;
            mEditText2.setSelection(mEditText2.getText().length());
            return;
        }
        throw new IllegalStateException("Dialog view must contain an EditText with id @android:id/edit");
    }
    
    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        if (bundle == null) {
            this.mText = this.getEditTextPreference().getText();
        }
        else {
            this.mText = bundle.getCharSequence("EditTextPreferenceDialogFragment.text");
        }
    }
    
    @Deprecated
    @Override
    public void onDialogClosed(final boolean b) {
        if (b) {
            final String string = this.mEditText.getText().toString();
            if (this.getEditTextPreference().callChangeListener(string)) {
                this.getEditTextPreference().setText(string);
            }
        }
    }
    
    @Override
    public void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putCharSequence("EditTextPreferenceDialogFragment.text", this.mText);
    }
}
