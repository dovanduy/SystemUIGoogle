// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.widget;

import androidx.core.graphics.TypefaceCompat;
import android.graphics.Typeface;
import android.view.ActionMode$Callback;
import androidx.appcompat.content.res.AppCompatResources;
import android.graphics.drawable.Drawable;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.EditorInfo;
import android.os.Build$VERSION;
import android.view.textclassifier.TextClassifier;
import android.graphics.PorterDuff$Mode;
import android.content.res.ColorStateList;
import android.annotation.SuppressLint;
import java.util.concurrent.ExecutionException;
import androidx.core.widget.TextViewCompat;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import androidx.core.text.PrecomputedTextCompat;
import java.util.concurrent.Future;
import androidx.core.widget.AutoSizeableTextView;
import androidx.core.widget.TintableCompoundDrawablesView;
import androidx.core.view.TintableBackgroundView;
import android.widget.TextView;

public class AppCompatTextView extends TextView implements TintableBackgroundView, TintableCompoundDrawablesView, AutoSizeableTextView
{
    private final AppCompatBackgroundHelper mBackgroundTintHelper;
    private Future<PrecomputedTextCompat> mPrecomputedTextFuture;
    private final AppCompatTextClassifierHelper mTextClassifierHelper;
    private final AppCompatTextHelper mTextHelper;
    
    public AppCompatTextView(final Context context) {
        this(context, null);
    }
    
    public AppCompatTextView(final Context context, final AttributeSet set) {
        this(context, set, 16842884);
    }
    
    public AppCompatTextView(final Context context, final AttributeSet set, final int n) {
        super(TintContextWrapper.wrap(context), set, n);
        ThemeUtils.checkAppCompatTheme((View)this, this.getContext());
        (this.mBackgroundTintHelper = new AppCompatBackgroundHelper((View)this)).loadFromAttributes(set, n);
        (this.mTextHelper = new AppCompatTextHelper(this)).loadFromAttributes(set, n);
        this.mTextHelper.applyCompoundDrawablesTints();
        this.mTextClassifierHelper = new AppCompatTextClassifierHelper(this);
    }
    
    private void consumeTextFutureAndSetBlocking() {
        final Future<PrecomputedTextCompat> mPrecomputedTextFuture = this.mPrecomputedTextFuture;
        if (mPrecomputedTextFuture == null) {
            return;
        }
        try {
            this.mPrecomputedTextFuture = null;
            TextViewCompat.setPrecomputedText(this, mPrecomputedTextFuture.get());
        }
        catch (InterruptedException | ExecutionException ex) {}
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
    
    public int getAutoSizeMaxTextSize() {
        if (AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE) {
            return super.getAutoSizeMaxTextSize();
        }
        final AppCompatTextHelper mTextHelper = this.mTextHelper;
        if (mTextHelper != null) {
            return mTextHelper.getAutoSizeMaxTextSize();
        }
        return -1;
    }
    
    public int getAutoSizeMinTextSize() {
        if (AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE) {
            return super.getAutoSizeMinTextSize();
        }
        final AppCompatTextHelper mTextHelper = this.mTextHelper;
        if (mTextHelper != null) {
            return mTextHelper.getAutoSizeMinTextSize();
        }
        return -1;
    }
    
    public int getAutoSizeStepGranularity() {
        if (AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE) {
            return super.getAutoSizeStepGranularity();
        }
        final AppCompatTextHelper mTextHelper = this.mTextHelper;
        if (mTextHelper != null) {
            return mTextHelper.getAutoSizeStepGranularity();
        }
        return -1;
    }
    
    public int[] getAutoSizeTextAvailableSizes() {
        if (AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE) {
            return super.getAutoSizeTextAvailableSizes();
        }
        final AppCompatTextHelper mTextHelper = this.mTextHelper;
        if (mTextHelper != null) {
            return mTextHelper.getAutoSizeTextAvailableSizes();
        }
        return new int[0];
    }
    
    @SuppressLint({ "WrongConstant" })
    public int getAutoSizeTextType() {
        final boolean platform_SUPPORTS_AUTOSIZE = AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE;
        int n = 0;
        if (platform_SUPPORTS_AUTOSIZE) {
            if (super.getAutoSizeTextType() == 1) {
                n = 1;
            }
            return n;
        }
        final AppCompatTextHelper mTextHelper = this.mTextHelper;
        if (mTextHelper != null) {
            return mTextHelper.getAutoSizeTextType();
        }
        return 0;
    }
    
    public int getFirstBaselineToTopHeight() {
        return TextViewCompat.getFirstBaselineToTopHeight(this);
    }
    
    public int getLastBaselineToBottomHeight() {
        return TextViewCompat.getLastBaselineToBottomHeight(this);
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
    
    public CharSequence getText() {
        this.consumeTextFutureAndSetBlocking();
        return super.getText();
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
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        final AppCompatTextHelper mTextHelper = this.mTextHelper;
        if (mTextHelper != null) {
            mTextHelper.onLayout(b, n, n2, n3, n4);
        }
    }
    
    protected void onMeasure(final int n, final int n2) {
        this.consumeTextFutureAndSetBlocking();
        super.onMeasure(n, n2);
    }
    
    protected void onTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
        super.onTextChanged(charSequence, n, n2, n3);
        final AppCompatTextHelper mTextHelper = this.mTextHelper;
        if (mTextHelper != null && !AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE && mTextHelper.isAutoSizeEnabled()) {
            this.mTextHelper.autoSizeText();
        }
    }
    
    public void setAutoSizeTextTypeUniformWithConfiguration(final int n, final int n2, final int n3, final int n4) throws IllegalArgumentException {
        if (AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE) {
            super.setAutoSizeTextTypeUniformWithConfiguration(n, n2, n3, n4);
        }
        else {
            final AppCompatTextHelper mTextHelper = this.mTextHelper;
            if (mTextHelper != null) {
                mTextHelper.setAutoSizeTextTypeUniformWithConfiguration(n, n2, n3, n4);
            }
        }
    }
    
    public void setAutoSizeTextTypeUniformWithPresetSizes(final int[] array, final int n) throws IllegalArgumentException {
        if (AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE) {
            super.setAutoSizeTextTypeUniformWithPresetSizes(array, n);
        }
        else {
            final AppCompatTextHelper mTextHelper = this.mTextHelper;
            if (mTextHelper != null) {
                mTextHelper.setAutoSizeTextTypeUniformWithPresetSizes(array, n);
            }
        }
    }
    
    public void setAutoSizeTextTypeWithDefaults(final int n) {
        if (AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE) {
            super.setAutoSizeTextTypeWithDefaults(n);
        }
        else {
            final AppCompatTextHelper mTextHelper = this.mTextHelper;
            if (mTextHelper != null) {
                mTextHelper.setAutoSizeTextTypeWithDefaults(n);
            }
        }
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
    
    public void setCompoundDrawables(final Drawable drawable, final Drawable drawable2, final Drawable drawable3, final Drawable drawable4) {
        super.setCompoundDrawables(drawable, drawable2, drawable3, drawable4);
        final AppCompatTextHelper mTextHelper = this.mTextHelper;
        if (mTextHelper != null) {
            mTextHelper.onSetCompoundDrawables();
        }
    }
    
    public void setCompoundDrawablesRelative(final Drawable drawable, final Drawable drawable2, final Drawable drawable3, final Drawable drawable4) {
        super.setCompoundDrawablesRelative(drawable, drawable2, drawable3, drawable4);
        final AppCompatTextHelper mTextHelper = this.mTextHelper;
        if (mTextHelper != null) {
            mTextHelper.onSetCompoundDrawables();
        }
    }
    
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(final int n, final int n2, final int n3, final int n4) {
        final Context context = this.getContext();
        Drawable drawable = null;
        Drawable drawable2;
        if (n != 0) {
            drawable2 = AppCompatResources.getDrawable(context, n);
        }
        else {
            drawable2 = null;
        }
        Drawable drawable3;
        if (n2 != 0) {
            drawable3 = AppCompatResources.getDrawable(context, n2);
        }
        else {
            drawable3 = null;
        }
        Drawable drawable4;
        if (n3 != 0) {
            drawable4 = AppCompatResources.getDrawable(context, n3);
        }
        else {
            drawable4 = null;
        }
        if (n4 != 0) {
            drawable = AppCompatResources.getDrawable(context, n4);
        }
        this.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable2, drawable3, drawable4, drawable);
        final AppCompatTextHelper mTextHelper = this.mTextHelper;
        if (mTextHelper != null) {
            mTextHelper.onSetCompoundDrawables();
        }
    }
    
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(final Drawable drawable, final Drawable drawable2, final Drawable drawable3, final Drawable drawable4) {
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, drawable2, drawable3, drawable4);
        final AppCompatTextHelper mTextHelper = this.mTextHelper;
        if (mTextHelper != null) {
            mTextHelper.onSetCompoundDrawables();
        }
    }
    
    public void setCompoundDrawablesWithIntrinsicBounds(final int n, final int n2, final int n3, final int n4) {
        final Context context = this.getContext();
        Drawable drawable = null;
        Drawable drawable2;
        if (n != 0) {
            drawable2 = AppCompatResources.getDrawable(context, n);
        }
        else {
            drawable2 = null;
        }
        Drawable drawable3;
        if (n2 != 0) {
            drawable3 = AppCompatResources.getDrawable(context, n2);
        }
        else {
            drawable3 = null;
        }
        Drawable drawable4;
        if (n3 != 0) {
            drawable4 = AppCompatResources.getDrawable(context, n3);
        }
        else {
            drawable4 = null;
        }
        if (n4 != 0) {
            drawable = AppCompatResources.getDrawable(context, n4);
        }
        this.setCompoundDrawablesWithIntrinsicBounds(drawable2, drawable3, drawable4, drawable);
        final AppCompatTextHelper mTextHelper = this.mTextHelper;
        if (mTextHelper != null) {
            mTextHelper.onSetCompoundDrawables();
        }
    }
    
    public void setCompoundDrawablesWithIntrinsicBounds(final Drawable drawable, final Drawable drawable2, final Drawable drawable3, final Drawable drawable4) {
        super.setCompoundDrawablesWithIntrinsicBounds(drawable, drawable2, drawable3, drawable4);
        final AppCompatTextHelper mTextHelper = this.mTextHelper;
        if (mTextHelper != null) {
            mTextHelper.onSetCompoundDrawables();
        }
    }
    
    public void setCustomSelectionActionModeCallback(final ActionMode$Callback actionMode$Callback) {
        super.setCustomSelectionActionModeCallback(TextViewCompat.wrapCustomSelectionActionModeCallback(this, actionMode$Callback));
    }
    
    public void setFirstBaselineToTopHeight(final int firstBaselineToTopHeight) {
        if (Build$VERSION.SDK_INT >= 28) {
            super.setFirstBaselineToTopHeight(firstBaselineToTopHeight);
        }
        else {
            TextViewCompat.setFirstBaselineToTopHeight(this, firstBaselineToTopHeight);
        }
    }
    
    public void setLastBaselineToBottomHeight(final int lastBaselineToBottomHeight) {
        if (Build$VERSION.SDK_INT >= 28) {
            super.setLastBaselineToBottomHeight(lastBaselineToBottomHeight);
        }
        else {
            TextViewCompat.setLastBaselineToBottomHeight(this, lastBaselineToBottomHeight);
        }
    }
    
    public void setLineHeight(final int n) {
        TextViewCompat.setLineHeight(this, n);
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
    
    public void setSupportCompoundDrawablesTintList(final ColorStateList compoundDrawableTintList) {
        this.mTextHelper.setCompoundDrawableTintList(compoundDrawableTintList);
        this.mTextHelper.applyCompoundDrawablesTints();
    }
    
    public void setSupportCompoundDrawablesTintMode(final PorterDuff$Mode compoundDrawableTintMode) {
        this.mTextHelper.setCompoundDrawableTintMode(compoundDrawableTintMode);
        this.mTextHelper.applyCompoundDrawablesTints();
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
    
    public void setTextSize(final int n, final float n2) {
        if (AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE) {
            super.setTextSize(n, n2);
        }
        else {
            final AppCompatTextHelper mTextHelper = this.mTextHelper;
            if (mTextHelper != null) {
                mTextHelper.setTextSize(n, n2);
            }
        }
    }
    
    public void setTypeface(Typeface typeface, final int n) {
        Typeface create;
        if (typeface != null && n > 0) {
            create = TypefaceCompat.create(this.getContext(), typeface, n);
        }
        else {
            create = null;
        }
        if (create != null) {
            typeface = create;
        }
        super.setTypeface(typeface, n);
    }
}
