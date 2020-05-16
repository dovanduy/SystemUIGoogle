// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import android.os.Parcelable;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.app.AlertDialog;
import android.app.Activity;
import android.content.Context;
import android.app.AlertDialog$Builder;
import android.graphics.drawable.Drawable;
import android.app.Fragment;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Bitmap$Config;
import android.os.Bundle;
import android.content.DialogInterface;
import android.widget.TextView;
import android.text.TextUtils;
import android.view.View;
import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.content.DialogInterface$OnClickListener;
import android.app.DialogFragment;

@Deprecated
public abstract class PreferenceDialogFragment extends DialogFragment implements DialogInterface$OnClickListener
{
    private BitmapDrawable mDialogIcon;
    private int mDialogLayoutRes;
    private CharSequence mDialogMessage;
    private CharSequence mDialogTitle;
    private CharSequence mNegativeButtonText;
    private CharSequence mPositiveButtonText;
    private DialogPreference mPreference;
    private int mWhichButtonClicked;
    
    @Deprecated
    public PreferenceDialogFragment() {
    }
    
    private void requestInputMethod(final Dialog dialog) {
        dialog.getWindow().setSoftInputMode(5);
    }
    
    @Deprecated
    public DialogPreference getPreference() {
        if (this.mPreference == null) {
            this.mPreference = ((DialogPreference.TargetFragment)this.getTargetFragment()).findPreference(this.getArguments().getString("key"));
        }
        return this.mPreference;
    }
    
    protected boolean needInputMethod() {
        return false;
    }
    
    @Deprecated
    protected void onBindDialogView(View viewById) {
        viewById = viewById.findViewById(16908299);
        if (viewById != null) {
            final CharSequence mDialogMessage = this.mDialogMessage;
            int visibility = 8;
            if (!TextUtils.isEmpty(mDialogMessage)) {
                if (viewById instanceof TextView) {
                    ((TextView)viewById).setText(mDialogMessage);
                }
                visibility = 0;
            }
            if (viewById.getVisibility() != visibility) {
                viewById.setVisibility(visibility);
            }
        }
    }
    
    @Deprecated
    public void onClick(final DialogInterface dialogInterface, final int mWhichButtonClicked) {
        this.mWhichButtonClicked = mWhichButtonClicked;
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        final Fragment targetFragment = this.getTargetFragment();
        if (targetFragment instanceof DialogPreference.TargetFragment) {
            final DialogPreference.TargetFragment targetFragment2 = (DialogPreference.TargetFragment)targetFragment;
            final String string = this.getArguments().getString("key");
            if (bundle == null) {
                final DialogPreference mPreference = targetFragment2.findPreference(string);
                this.mPreference = mPreference;
                this.mDialogTitle = mPreference.getDialogTitle();
                this.mPositiveButtonText = this.mPreference.getPositiveButtonText();
                this.mNegativeButtonText = this.mPreference.getNegativeButtonText();
                this.mDialogMessage = this.mPreference.getDialogMessage();
                this.mDialogLayoutRes = this.mPreference.getDialogLayoutResource();
                final Drawable dialogIcon = this.mPreference.getDialogIcon();
                if (dialogIcon != null && !(dialogIcon instanceof BitmapDrawable)) {
                    final Bitmap bitmap = Bitmap.createBitmap(dialogIcon.getIntrinsicWidth(), dialogIcon.getIntrinsicHeight(), Bitmap$Config.ARGB_8888);
                    final Canvas canvas = new Canvas(bitmap);
                    dialogIcon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    dialogIcon.draw(canvas);
                    this.mDialogIcon = new BitmapDrawable(this.getResources(), bitmap);
                }
                else {
                    this.mDialogIcon = (BitmapDrawable)dialogIcon;
                }
            }
            else {
                this.mDialogTitle = bundle.getCharSequence("PreferenceDialogFragment.title");
                this.mPositiveButtonText = bundle.getCharSequence("PreferenceDialogFragment.positiveText");
                this.mNegativeButtonText = bundle.getCharSequence("PreferenceDialogFragment.negativeText");
                this.mDialogMessage = bundle.getCharSequence("PreferenceDialogFragment.message");
                this.mDialogLayoutRes = bundle.getInt("PreferenceDialogFragment.layout", 0);
                final Bitmap bitmap2 = (Bitmap)bundle.getParcelable("PreferenceDialogFragment.icon");
                if (bitmap2 != null) {
                    this.mDialogIcon = new BitmapDrawable(this.getResources(), bitmap2);
                }
            }
            return;
        }
        throw new IllegalStateException("Target fragment must implement TargetFragment interface");
    }
    
    public Dialog onCreateDialog(final Bundle bundle) {
        final Activity activity = this.getActivity();
        this.mWhichButtonClicked = -2;
        final AlertDialog$Builder setNegativeButton = new AlertDialog$Builder((Context)activity).setTitle(this.mDialogTitle).setIcon((Drawable)this.mDialogIcon).setPositiveButton(this.mPositiveButtonText, (DialogInterface$OnClickListener)this).setNegativeButton(this.mNegativeButtonText, (DialogInterface$OnClickListener)this);
        final View onCreateDialogView = this.onCreateDialogView((Context)activity);
        if (onCreateDialogView != null) {
            this.onBindDialogView(onCreateDialogView);
            setNegativeButton.setView(onCreateDialogView);
        }
        else {
            setNegativeButton.setMessage(this.mDialogMessage);
        }
        this.onPrepareDialogBuilder(setNegativeButton);
        final AlertDialog create = setNegativeButton.create();
        if (this.needInputMethod()) {
            this.requestInputMethod((Dialog)create);
        }
        return (Dialog)create;
    }
    
    @Deprecated
    protected View onCreateDialogView(final Context context) {
        final int mDialogLayoutRes = this.mDialogLayoutRes;
        if (mDialogLayoutRes == 0) {
            return null;
        }
        return LayoutInflater.from(context).inflate(mDialogLayoutRes, (ViewGroup)null);
    }
    
    @Deprecated
    public abstract void onDialogClosed(final boolean p0);
    
    public void onDismiss(final DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        this.onDialogClosed(this.mWhichButtonClicked == -1);
    }
    
    @Deprecated
    protected void onPrepareDialogBuilder(final AlertDialog$Builder alertDialog$Builder) {
    }
    
    public void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putCharSequence("PreferenceDialogFragment.title", this.mDialogTitle);
        bundle.putCharSequence("PreferenceDialogFragment.positiveText", this.mPositiveButtonText);
        bundle.putCharSequence("PreferenceDialogFragment.negativeText", this.mNegativeButtonText);
        bundle.putCharSequence("PreferenceDialogFragment.message", this.mDialogMessage);
        bundle.putInt("PreferenceDialogFragment.layout", this.mDialogLayoutRes);
        final BitmapDrawable mDialogIcon = this.mDialogIcon;
        if (mDialogIcon != null) {
            bundle.putParcelable("PreferenceDialogFragment.icon", (Parcelable)mDialogIcon.getBitmap());
        }
    }
}
