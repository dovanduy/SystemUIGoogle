// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.app;

import android.content.DialogInterface$OnDismissListener;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.content.DialogInterface$OnClickListener;
import android.widget.ListAdapter;
import android.content.DialogInterface$OnKeyListener;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.R$attr;
import android.util.TypedValue;
import android.content.Context;
import android.content.DialogInterface;

public class AlertDialog extends AppCompatDialog implements DialogInterface
{
    final AlertController mAlert;
    
    protected AlertDialog(final Context context, final int n) {
        super(context, resolveDialogTheme(context, n));
        this.mAlert = new AlertController(this.getContext(), this, this.getWindow());
    }
    
    static int resolveDialogTheme(final Context context, final int n) {
        if ((n >>> 24 & 0xFF) >= 1) {
            return n;
        }
        final TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R$attr.alertDialogTheme, typedValue, true);
        return typedValue.resourceId;
    }
    
    public ListView getListView() {
        return this.mAlert.getListView();
    }
    
    @Override
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.mAlert.installContent();
    }
    
    public boolean onKeyDown(final int n, final KeyEvent keyEvent) {
        return this.mAlert.onKeyDown(n, keyEvent) || super.onKeyDown(n, keyEvent);
    }
    
    public boolean onKeyUp(final int n, final KeyEvent keyEvent) {
        return this.mAlert.onKeyUp(n, keyEvent) || super.onKeyUp(n, keyEvent);
    }
    
    @Override
    public void setTitle(final CharSequence charSequence) {
        super.setTitle(charSequence);
        this.mAlert.setTitle(charSequence);
    }
    
    public static class Builder
    {
        private final AlertController.AlertParams P;
        private final int mTheme;
        
        public Builder(final Context context) {
            this(context, AlertDialog.resolveDialogTheme(context, 0));
        }
        
        public Builder(final Context context, final int mTheme) {
            this.P = new AlertController.AlertParams((Context)new ContextThemeWrapper(context, AlertDialog.resolveDialogTheme(context, mTheme)));
            this.mTheme = mTheme;
        }
        
        public AlertDialog create() {
            final AlertDialog alertDialog = new AlertDialog(this.P.mContext, this.mTheme);
            this.P.apply(alertDialog.mAlert);
            alertDialog.setCancelable(this.P.mCancelable);
            if (this.P.mCancelable) {
                alertDialog.setCanceledOnTouchOutside(true);
            }
            alertDialog.setOnCancelListener(this.P.mOnCancelListener);
            alertDialog.setOnDismissListener(this.P.mOnDismissListener);
            final DialogInterface$OnKeyListener mOnKeyListener = this.P.mOnKeyListener;
            if (mOnKeyListener != null) {
                alertDialog.setOnKeyListener(mOnKeyListener);
            }
            return alertDialog;
        }
        
        public Context getContext() {
            return this.P.mContext;
        }
        
        public Builder setAdapter(final ListAdapter mAdapter, final DialogInterface$OnClickListener mOnClickListener) {
            final AlertController.AlertParams p2 = this.P;
            p2.mAdapter = mAdapter;
            p2.mOnClickListener = mOnClickListener;
            return this;
        }
        
        public Builder setCustomTitle(final View mCustomTitleView) {
            this.P.mCustomTitleView = mCustomTitleView;
            return this;
        }
        
        public Builder setIcon(final Drawable mIcon) {
            this.P.mIcon = mIcon;
            return this;
        }
        
        public Builder setNegativeButton(final int n, final DialogInterface$OnClickListener mNegativeButtonListener) {
            final AlertController.AlertParams p2 = this.P;
            p2.mNegativeButtonText = p2.mContext.getText(n);
            this.P.mNegativeButtonListener = mNegativeButtonListener;
            return this;
        }
        
        public Builder setOnDismissListener(final DialogInterface$OnDismissListener mOnDismissListener) {
            this.P.mOnDismissListener = mOnDismissListener;
            return this;
        }
        
        public Builder setOnKeyListener(final DialogInterface$OnKeyListener mOnKeyListener) {
            this.P.mOnKeyListener = mOnKeyListener;
            return this;
        }
        
        public Builder setPositiveButton(final int n, final DialogInterface$OnClickListener mPositiveButtonListener) {
            final AlertController.AlertParams p2 = this.P;
            p2.mPositiveButtonText = p2.mContext.getText(n);
            this.P.mPositiveButtonListener = mPositiveButtonListener;
            return this;
        }
        
        public Builder setSingleChoiceItems(final ListAdapter mAdapter, final int mCheckedItem, final DialogInterface$OnClickListener mOnClickListener) {
            final AlertController.AlertParams p3 = this.P;
            p3.mAdapter = mAdapter;
            p3.mOnClickListener = mOnClickListener;
            p3.mCheckedItem = mCheckedItem;
            p3.mIsSingleChoice = true;
            return this;
        }
        
        public Builder setTitle(final CharSequence mTitle) {
            this.P.mTitle = mTitle;
            return this;
        }
        
        public Builder setView(final int mViewLayoutResId) {
            final AlertController.AlertParams p = this.P;
            p.mView = null;
            p.mViewLayoutResId = mViewLayoutResId;
            p.mViewSpacingSpecified = false;
            return this;
        }
        
        public AlertDialog show() {
            final AlertDialog create = this.create();
            create.show();
            return create;
        }
    }
}
