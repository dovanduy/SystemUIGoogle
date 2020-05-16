// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.widget;

import androidx.core.widget.TextViewCompat;
import android.view.ActionMode$Callback;
import androidx.appcompat.content.res.AppCompatResources;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.CheckedTextView;

public class AppCompatCheckedTextView extends CheckedTextView
{
    private static final int[] TINT_ATTRS;
    private final AppCompatTextHelper mTextHelper;
    
    static {
        TINT_ATTRS = new int[] { 16843016 };
    }
    
    public AppCompatCheckedTextView(final Context context, final AttributeSet set) {
        this(context, set, 16843720);
    }
    
    public AppCompatCheckedTextView(final Context context, final AttributeSet set, final int n) {
        super(TintContextWrapper.wrap(context), set, n);
        ThemeUtils.checkAppCompatTheme((View)this, this.getContext());
        (this.mTextHelper = new AppCompatTextHelper((TextView)this)).loadFromAttributes(set, n);
        this.mTextHelper.applyCompoundDrawablesTints();
        final TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(this.getContext(), set, AppCompatCheckedTextView.TINT_ATTRS, n, 0);
        this.setCheckMarkDrawable(obtainStyledAttributes.getDrawable(0));
        obtainStyledAttributes.recycle();
    }
    
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        final AppCompatTextHelper mTextHelper = this.mTextHelper;
        if (mTextHelper != null) {
            mTextHelper.applyCompoundDrawablesTints();
        }
    }
    
    public InputConnection onCreateInputConnection(final EditorInfo editorInfo) {
        final InputConnection onCreateInputConnection = super.onCreateInputConnection(editorInfo);
        AppCompatHintHelper.onCreateInputConnection(onCreateInputConnection, editorInfo, (View)this);
        return onCreateInputConnection;
    }
    
    public void setCheckMarkDrawable(final int n) {
        this.setCheckMarkDrawable(AppCompatResources.getDrawable(this.getContext(), n));
    }
    
    public void setCustomSelectionActionModeCallback(final ActionMode$Callback actionMode$Callback) {
        super.setCustomSelectionActionModeCallback(TextViewCompat.wrapCustomSelectionActionModeCallback((TextView)this, actionMode$Callback));
    }
    
    public void setTextAppearance(final Context context, final int n) {
        super.setTextAppearance(context, n);
        final AppCompatTextHelper mTextHelper = this.mTextHelper;
        if (mTextHelper != null) {
            mTextHelper.onSetTextAppearance(context, n);
        }
    }
}
