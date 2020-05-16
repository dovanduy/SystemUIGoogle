// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.inputmethod;

import android.content.ActivityNotFoundException;
import android.widget.Toast;
import android.util.Log;
import android.content.DialogInterface$OnCancelListener;
import android.content.DialogInterface$OnClickListener;
import com.android.settingslib.R$string;
import android.app.AlertDialog$Builder;
import android.content.DialogInterface;
import com.android.internal.annotations.VisibleForTesting;
import android.content.Intent;
import android.text.TextUtils;
import android.content.Context;
import android.view.inputmethod.InputMethodInfo;
import android.app.AlertDialog;
import androidx.preference.Preference;
import com.android.settingslib.RestrictedSwitchPreference;

public class InputMethodPreference extends RestrictedSwitchPreference implements OnPreferenceClickListener, OnPreferenceChangeListener
{
    private static final String TAG;
    private AlertDialog mDialog;
    private final InputMethodInfo mImi;
    private final OnSavePreferenceListener mOnSaveListener;
    
    static {
        TAG = InputMethodPreference.class.getSimpleName();
    }
    
    @VisibleForTesting
    InputMethodPreference(final Context context, final InputMethodInfo mImi, final CharSequence title, final boolean b, final OnSavePreferenceListener mOnSaveListener) {
        super(context);
        this.mDialog = null;
        this.setPersistent(false);
        this.mImi = mImi;
        this.mOnSaveListener = mOnSaveListener;
        this.setSwitchTextOn("");
        this.setSwitchTextOff("");
        this.setKey(mImi.getId());
        this.setTitle(title);
        final String settingsActivity = mImi.getSettingsActivity();
        if (TextUtils.isEmpty((CharSequence)settingsActivity)) {
            this.setIntent(null);
        }
        else {
            final Intent intent = new Intent("android.intent.action.MAIN");
            intent.setClassName(mImi.getPackageName(), settingsActivity);
            this.setIntent(intent);
        }
        InputMethodSettingValuesWrapper.getInstance(context);
        if (mImi.isSystem()) {
            InputMethodAndSubtypeUtil.isValidNonAuxAsciiCapableIme(mImi);
        }
        this.setOnPreferenceClickListener((OnPreferenceClickListener)this);
        this.setOnPreferenceChangeListener((OnPreferenceChangeListener)this);
    }
    
    private boolean isImeEnabler() {
        return this.getWidgetLayoutResource() != 0;
    }
    
    private boolean isTv() {
        return (this.getContext().getResources().getConfiguration().uiMode & 0xF) == 0x4;
    }
    
    private void setCheckedInternal(final boolean checked) {
        super.setChecked(checked);
        this.mOnSaveListener.onSaveInputMethodPreference(this);
        this.notifyChanged();
    }
    
    private void showDirectBootWarnDialog() {
        final AlertDialog mDialog = this.mDialog;
        if (mDialog != null && mDialog.isShowing()) {
            this.mDialog.dismiss();
        }
        final Context context = this.getContext();
        final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(context);
        alertDialog$Builder.setCancelable(true);
        alertDialog$Builder.setMessage(context.getText(R$string.direct_boot_unaware_dialog_message));
        alertDialog$Builder.setPositiveButton(17039370, (DialogInterface$OnClickListener)new _$$Lambda$InputMethodPreference$_R1WCgG1LabBNKieYWiJs9NnYv4(this));
        alertDialog$Builder.setNegativeButton(17039360, (DialogInterface$OnClickListener)new _$$Lambda$InputMethodPreference$8Yu3IA81uQ9mforg_QOtWUG_Sj4(this));
        (this.mDialog = alertDialog$Builder.create()).show();
    }
    
    private void showSecurityWarnDialog() {
        final AlertDialog mDialog = this.mDialog;
        if (mDialog != null && mDialog.isShowing()) {
            this.mDialog.dismiss();
        }
        final Context context = this.getContext();
        final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(context);
        alertDialog$Builder.setCancelable(true);
        alertDialog$Builder.setTitle(17039380);
        alertDialog$Builder.setMessage((CharSequence)context.getString(R$string.ime_security_warning, new Object[] { this.mImi.getServiceInfo().applicationInfo.loadLabel(context.getPackageManager()) }));
        alertDialog$Builder.setPositiveButton(17039370, (DialogInterface$OnClickListener)new _$$Lambda$InputMethodPreference$pHt4_6FWRQ9Ts6PuJy_AB14MhJc(this));
        alertDialog$Builder.setNegativeButton(17039360, (DialogInterface$OnClickListener)new _$$Lambda$InputMethodPreference$HH5dtwzFZv06UNDXJAO6Cyx4kxo(this));
        alertDialog$Builder.setOnCancelListener((DialogInterface$OnCancelListener)new _$$Lambda$InputMethodPreference$hpUUW_Jm1ATEk1_GeQASyreqYZI(this));
        (this.mDialog = alertDialog$Builder.create()).show();
    }
    
    @Override
    public boolean onPreferenceChange(final Preference preference, final Object o) {
        if (!this.isImeEnabler()) {
            return false;
        }
        if (this.isChecked()) {
            this.setCheckedInternal(false);
            return false;
        }
        if (this.mImi.isSystem()) {
            if (!this.mImi.getServiceInfo().directBootAware && !this.isTv()) {
                if (!this.isTv()) {
                    this.showDirectBootWarnDialog();
                }
            }
            else {
                this.setCheckedInternal(true);
            }
        }
        else {
            this.showSecurityWarnDialog();
        }
        return false;
    }
    
    @Override
    public boolean onPreferenceClick(Preference context) {
        if (this.isImeEnabler()) {
            return true;
        }
        context = (Preference)this.getContext();
        try {
            final Intent intent = this.getIntent();
            if (intent != null) {
                ((Context)context).startActivity(intent);
            }
        }
        catch (ActivityNotFoundException ex) {
            Log.d(InputMethodPreference.TAG, "IME's Settings Activity Not Found", (Throwable)ex);
            Toast.makeText((Context)context, (CharSequence)((Context)context).getString(R$string.failed_to_open_app_settings_toast, new Object[] { this.mImi.loadLabel(((Context)context).getPackageManager()) }), 1).show();
        }
        return true;
    }
    
    public interface OnSavePreferenceListener
    {
        void onSaveInputMethodPreference(final InputMethodPreference p0);
    }
}
