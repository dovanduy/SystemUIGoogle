// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import android.content.res.TypedArray;
import androidx.core.content.res.TypedArrayUtils;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.drawable.Drawable;

public abstract class DialogPreference extends Preference
{
    private Drawable mDialogIcon;
    private int mDialogLayoutResId;
    private CharSequence mDialogMessage;
    private CharSequence mDialogTitle;
    private CharSequence mNegativeButtonText;
    private CharSequence mPositiveButtonText;
    
    public DialogPreference(final Context context) {
        this(context, null);
    }
    
    public DialogPreference(final Context context, final AttributeSet set) {
        this(context, set, TypedArrayUtils.getAttr(context, R$attr.dialogPreferenceStyle, 16842897));
    }
    
    public DialogPreference(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public DialogPreference(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.DialogPreference, n, n2);
        final String string = TypedArrayUtils.getString(obtainStyledAttributes, R$styleable.DialogPreference_dialogTitle, R$styleable.DialogPreference_android_dialogTitle);
        this.mDialogTitle = string;
        if (string == null) {
            this.mDialogTitle = this.getTitle();
        }
        this.mDialogMessage = TypedArrayUtils.getString(obtainStyledAttributes, R$styleable.DialogPreference_dialogMessage, R$styleable.DialogPreference_android_dialogMessage);
        this.mDialogIcon = TypedArrayUtils.getDrawable(obtainStyledAttributes, R$styleable.DialogPreference_dialogIcon, R$styleable.DialogPreference_android_dialogIcon);
        this.mPositiveButtonText = TypedArrayUtils.getString(obtainStyledAttributes, R$styleable.DialogPreference_positiveButtonText, R$styleable.DialogPreference_android_positiveButtonText);
        this.mNegativeButtonText = TypedArrayUtils.getString(obtainStyledAttributes, R$styleable.DialogPreference_negativeButtonText, R$styleable.DialogPreference_android_negativeButtonText);
        this.mDialogLayoutResId = TypedArrayUtils.getResourceId(obtainStyledAttributes, R$styleable.DialogPreference_dialogLayout, R$styleable.DialogPreference_android_dialogLayout, 0);
        obtainStyledAttributes.recycle();
    }
    
    public Drawable getDialogIcon() {
        return this.mDialogIcon;
    }
    
    public int getDialogLayoutResource() {
        return this.mDialogLayoutResId;
    }
    
    public CharSequence getDialogMessage() {
        return this.mDialogMessage;
    }
    
    public CharSequence getDialogTitle() {
        return this.mDialogTitle;
    }
    
    public CharSequence getNegativeButtonText() {
        return this.mNegativeButtonText;
    }
    
    public CharSequence getPositiveButtonText() {
        return this.mPositiveButtonText;
    }
    
    @Override
    protected void onClick() {
        this.getPreferenceManager().showDialog(this);
    }
    
    public interface TargetFragment
    {
         <T extends Preference> T findPreference(final CharSequence p0);
    }
}
