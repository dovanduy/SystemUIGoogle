// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.widget;

import androidx.core.widget.TextViewCompat;
import android.view.ActionMode$Callback;
import android.graphics.drawable.Drawable;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.textclassifier.TextClassifier;
import android.os.Build$VERSION;
import android.text.Editable;
import android.graphics.PorterDuff$Mode;
import android.content.res.ColorStateList;
import android.widget.TextView;
import android.view.View;
import androidx.appcompat.R$attr;
import android.util.AttributeSet;
import android.content.Context;
import androidx.core.view.TintableBackgroundView;
import android.widget.EditText;

public class AppCompatEditText extends EditText implements TintableBackgroundView
{
    private final AppCompatBackgroundHelper mBackgroundTintHelper;
    private final AppCompatTextClassifierHelper mTextClassifierHelper;
    private final AppCompatTextHelper mTextHelper;
    
    public AppCompatEditText(final Context context, final AttributeSet set) {
        this(context, set, R$attr.editTextStyle);
    }
    
    public AppCompatEditText(final Context context, final AttributeSet set, final int n) {
        super(TintContextWrapper.wrap(context), set, n);
        ThemeUtils.checkAppCompatTheme((View)this, this.getContext());
        (this.mBackgroundTintHelper = new AppCompatBackgroundHelper((View)this)).loadFromAttributes(set, n);
        (this.mTextHelper = new AppCompatTextHelper((TextView)this)).loadFromAttributes(set, n);
        this.mTextHelper.applyCompoundDrawablesTints();
        this.mTextClassifierHelper = new AppCompatTextClassifierHelper((TextView)this);
    }
    
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        final AppCompatBackgroundHelper mBackgroundTintHelper = this.mBackgroundTintHelper;
        if (mBackgroundTintHelper != null) {
            mBackgroundTintHelper.applySupportBackgroundTint();
        }
        final AppCompatTextHelper mTextHelper = this.mTextHelper;
        if (mTextHelper != null) {
            mTextHelper.applyCompoundDrawablesTints();
        }
    }
    
    public ColorStateList getSupportBackgroundTintList() {
        final AppCompatBackgroundHelper mBackgroundTintHelper = this.mBackgroundTintHelper;
        ColorStateList supportBackgroundTintList;
        if (mBackgroundTintHelper != null) {
            supportBackgroundTintList = mBackgroundTintHelper.getSupportBackgroundTintList();
        }
        else {
            supportBackgroundTintList = null;
        }
        return supportBackgroundTintList;
    }
    
    public PorterDuff$Mode getSupportBackgroundTintMode() {
        final AppCompatBackgroundHelper mBackgroundTintHelper = this.mBackgroundTintHelper;
        PorterDuff$Mode supportBackgroundTintMode;
        if (mBackgroundTintHelper != null) {
            supportBackgroundTintMode = mBackgroundTintHelper.getSupportBackgroundTintMode();
        }
        else {
            supportBackgroundTintMode = null;
        }
        return supportBackgroundTintMode;
    }
    
    public Editable getText() {
        if (Build$VERSION.SDK_INT >= 28) {
            return super.getText();
        }
        return super.getEditableText();
    }
    
    public TextClassifier getTextClassifier() {
        if (Build$VERSION.SDK_INT < 28) {
            final AppCompatTextClassifierHelper mTextClassifierHelper = this.mTextClassifierHelper;
            if (mTextClassifierHelper != null) {
                return mTextClassifierHelper.getTextClassifier();
            }
        }
        return super.getTextClassifier();
    }
    
    public InputConnection onCreateInputConnection(final EditorInfo editorInfo) {
        final InputConnection onCreateInputConnection = super.onCreateInputConnection(editorInfo);
        AppCompatHintHelper.onCreateInputConnection(onCreateInputConnection, editorInfo, (View)this);
        return onCreateInputConnection;
    }
    
    public void setBackgroundDrawable(final Drawable backgroundDrawable) {
        super.setBackgroundDrawable(backgroundDrawable);
        final AppCompatBackgroundHelper mBackgroundTintHelper = this.mBackgroundTintHelper;
        if (mBackgroundTintHelper != null) {
            mBackgroundTintHelper.onSetBackgroundDrawable(backgroundDrawable);
        }
    }
    
    public void setBackgroundResource(final int backgroundResource) {
        super.setBackgroundResource(backgroundResource);
        final AppCompatBackgroundHelper mBackgroundTintHelper = this.mBackgroundTintHelper;
        if (mBackgroundTintHelper != null) {
            mBackgroundTintHelper.onSetBackgroundResource(backgroundResource);
        }
    }
    
    public void setCustomSelectionActionModeCallback(final ActionMode$Callback actionMode$Callback) {
        super.setCustomSelectionActionModeCallback(TextViewCompat.wrapCustomSelectionActionModeCallback((TextView)this, actionMode$Callback));
    }
    
    public void setSupportBackgroundTintList(final ColorStateList supportBackgroundTintList) {
        final AppCompatBackgroundHelper mBackgroundTintHelper = this.mBackgroundTintHelper;
        if (mBackgroundTintHelper != null) {
            mBackgroundTintHelper.setSupportBackgroundTintList(supportBackgroundTintList);
        }
    }
    
    public void setSupportBackgroundTintMode(final PorterDuff$Mode supportBackgroundTintMode) {
        final AppCompatBackgroundHelper mBackgroundTintHelper = this.mBackgroundTintHelper;
        if (mBackgroundTintHelper != null) {
            mBackgroundTintHelper.setSupportBackgroundTintMode(supportBackgroundTintMode);
        }
    }
    
    public void setTextAppearance(final Context context, final int n) {
        super.setTextAppearance(context, n);
        final AppCompatTextHelper mTextHelper = this.mTextHelper;
        if (mTextHelper != null) {
            mTextHelper.onSetTextAppearance(context, n);
        }
    }
    
    public void setTextClassifier(final TextClassifier textClassifier) {
        if (Build$VERSION.SDK_INT < 28) {
            final AppCompatTextClassifierHelper mTextClassifierHelper = this.mTextClassifierHelper;
            if (mTextClassifierHelper != null) {
                mTextClassifierHelper.setTextClassifier(textClassifier);
                return;
            }
        }
        super.setTextClassifier(textClassifier);
    }
}
