// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.app;

import androidx.appcompat.view.ActionMode;
import android.view.Window$Callback;
import android.view.ViewGroup$LayoutParams;
import android.view.View;
import androidx.appcompat.R$attr;
import android.util.TypedValue;
import android.os.Bundle;
import android.view.KeyEvent;
import android.content.Context;
import androidx.core.view.KeyEventDispatcher;
import android.app.Dialog;

public class AppCompatDialog extends Dialog implements AppCompatCallback
{
    private AppCompatDelegate mDelegate;
    private final KeyEventDispatcher.Component mKeyDispatcher;
    
    public AppCompatDialog(final Context context, final int n) {
        super(context, getThemeResId(context, n));
        this.mKeyDispatcher = new KeyEventDispatcher.Component() {
            @Override
            public boolean superDispatchKeyEvent(final KeyEvent keyEvent) {
                return AppCompatDialog.this.superDispatchKeyEvent(keyEvent);
            }
        };
        final AppCompatDelegate delegate = this.getDelegate();
        delegate.setTheme(getThemeResId(context, n));
        delegate.onCreate(null);
    }
    
    private static int getThemeResId(final Context context, final int n) {
        int resourceId = n;
        if (n == 0) {
            final TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(R$attr.dialogTheme, typedValue, true);
            resourceId = typedValue.resourceId;
        }
        return resourceId;
    }
    
    public void addContentView(final View view, final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        this.getDelegate().addContentView(view, viewGroup$LayoutParams);
    }
    
    public void dismiss() {
        super.dismiss();
        this.getDelegate().onDestroy();
    }
    
    public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
        return KeyEventDispatcher.dispatchKeyEvent(this.mKeyDispatcher, this.getWindow().getDecorView(), (Window$Callback)this, keyEvent);
    }
    
    public <T extends View> T findViewById(final int n) {
        return this.getDelegate().findViewById(n);
    }
    
    public AppCompatDelegate getDelegate() {
        if (this.mDelegate == null) {
            this.mDelegate = AppCompatDelegate.create(this, this);
        }
        return this.mDelegate;
    }
    
    public void invalidateOptionsMenu() {
        this.getDelegate().invalidateOptionsMenu();
    }
    
    protected void onCreate(final Bundle bundle) {
        this.getDelegate().installViewFactory();
        super.onCreate(bundle);
        this.getDelegate().onCreate(bundle);
    }
    
    protected void onStop() {
        super.onStop();
        this.getDelegate().onStop();
    }
    
    public void onSupportActionModeFinished(final ActionMode actionMode) {
    }
    
    public void onSupportActionModeStarted(final ActionMode actionMode) {
    }
    
    public ActionMode onWindowStartingSupportActionMode(final ActionMode.Callback callback) {
        return null;
    }
    
    public void setContentView(final int contentView) {
        this.getDelegate().setContentView(contentView);
    }
    
    public void setContentView(final View contentView) {
        this.getDelegate().setContentView(contentView);
    }
    
    public void setContentView(final View view, final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        this.getDelegate().setContentView(view, viewGroup$LayoutParams);
    }
    
    public void setTitle(final int title) {
        super.setTitle(title);
        this.getDelegate().setTitle(this.getContext().getString(title));
    }
    
    public void setTitle(final CharSequence charSequence) {
        super.setTitle(charSequence);
        this.getDelegate().setTitle(charSequence);
    }
    
    boolean superDispatchKeyEvent(final KeyEvent keyEvent) {
        return super.dispatchKeyEvent(keyEvent);
    }
    
    public boolean supportRequestWindowFeature(final int n) {
        return this.getDelegate().requestWindowFeature(n);
    }
}
