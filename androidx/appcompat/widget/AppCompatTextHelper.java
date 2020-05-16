// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.widget;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff$Mode;
import androidx.core.widget.TextViewCompat;
import androidx.core.widget.AutoSizeableTextView;
import java.util.Locale;
import android.os.LocaleList;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.content.res.Resources$NotFoundException;
import androidx.core.content.res.ResourcesCompat;
import java.lang.ref.WeakReference;
import androidx.appcompat.R$styleable;
import android.os.Build$VERSION;
import android.content.res.ColorStateList;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.TextView;
import android.graphics.Typeface;

class AppCompatTextHelper
{
    private boolean mAsyncFontPending;
    private final AppCompatTextViewAutoSizeHelper mAutoSizeTextHelper;
    private TintInfo mDrawableBottomTint;
    private TintInfo mDrawableEndTint;
    private TintInfo mDrawableLeftTint;
    private TintInfo mDrawableRightTint;
    private TintInfo mDrawableStartTint;
    private TintInfo mDrawableTint;
    private TintInfo mDrawableTopTint;
    private Typeface mFontTypeface;
    private int mFontWeight;
    private int mStyle;
    private final TextView mView;
    
    AppCompatTextHelper(final TextView mView) {
        this.mStyle = 0;
        this.mFontWeight = -1;
        this.mView = mView;
        this.mAutoSizeTextHelper = new AppCompatTextViewAutoSizeHelper(this.mView);
    }
    
    private void applyCompoundDrawableTint(final Drawable drawable, final TintInfo tintInfo) {
        if (drawable != null && tintInfo != null) {
            AppCompatDrawableManager.tintDrawable(drawable, tintInfo, this.mView.getDrawableState());
        }
    }
    
    private static TintInfo createTintInfo(final Context context, final AppCompatDrawableManager appCompatDrawableManager, final int n) {
        final ColorStateList tintList = appCompatDrawableManager.getTintList(context, n);
        if (tintList != null) {
            final TintInfo tintInfo = new TintInfo();
            tintInfo.mHasTintList = true;
            tintInfo.mTintList = tintList;
            return tintInfo;
        }
        return null;
    }
    
    private void setCompoundDrawables(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4, Drawable drawable5, Drawable drawable6) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT >= 17 && (drawable5 != null || drawable6 != null)) {
            final Drawable[] compoundDrawablesRelative = this.mView.getCompoundDrawablesRelative();
            final TextView mView = this.mView;
            if (drawable5 == null) {
                drawable5 = compoundDrawablesRelative[0];
            }
            if (drawable2 == null) {
                drawable2 = compoundDrawablesRelative[1];
            }
            if (drawable6 == null) {
                drawable6 = compoundDrawablesRelative[2];
            }
            if (drawable4 == null) {
                drawable4 = compoundDrawablesRelative[3];
            }
            mView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable5, drawable2, drawable6, drawable4);
        }
        else if (drawable != null || drawable2 != null || drawable3 != null || drawable4 != null) {
            if (sdk_INT >= 17) {
                final Drawable[] compoundDrawablesRelative2 = this.mView.getCompoundDrawablesRelative();
                if (compoundDrawablesRelative2[0] != null || compoundDrawablesRelative2[2] != null) {
                    final TextView mView2 = this.mView;
                    drawable = compoundDrawablesRelative2[0];
                    if (drawable2 == null) {
                        drawable2 = compoundDrawablesRelative2[1];
                    }
                    drawable6 = compoundDrawablesRelative2[2];
                    if (drawable4 == null) {
                        drawable4 = compoundDrawablesRelative2[3];
                    }
                    mView2.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, drawable2, drawable6, drawable4);
                    return;
                }
            }
            final Drawable[] compoundDrawables = this.mView.getCompoundDrawables();
            final TextView mView3 = this.mView;
            if (drawable == null) {
                drawable = compoundDrawables[0];
            }
            if (drawable2 == null) {
                drawable2 = compoundDrawables[1];
            }
            if (drawable3 == null) {
                drawable3 = compoundDrawables[2];
            }
            if (drawable4 == null) {
                drawable4 = compoundDrawables[3];
            }
            mView3.setCompoundDrawablesWithIntrinsicBounds(drawable, drawable2, drawable3, drawable4);
        }
    }
    
    private void setCompoundTints() {
        final TintInfo mDrawableTint = this.mDrawableTint;
        this.mDrawableLeftTint = mDrawableTint;
        this.mDrawableTopTint = mDrawableTint;
        this.mDrawableRightTint = mDrawableTint;
        this.mDrawableBottomTint = mDrawableTint;
        this.mDrawableStartTint = mDrawableTint;
        this.mDrawableEndTint = mDrawableTint;
    }
    
    private void setTextSizeInternal(final int n, final float n2) {
        this.mAutoSizeTextHelper.setTextSizeInternal(n, n2);
    }
    
    private void updateTypefaceAndStyle(final Context context, final TintTypedArray tintTypedArray) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        this.mStyle = tintTypedArray.getInt(R$styleable.TextAppearance_android_textStyle, this.mStyle);
        final boolean b = false;
        if (sdk_INT >= 28 && (this.mFontWeight = tintTypedArray.getInt(R$styleable.TextAppearance_android_textFontWeight, -1)) != -1) {
            this.mStyle = ((this.mStyle & 0x2) | 0x0);
        }
        if (!tintTypedArray.hasValue(R$styleable.TextAppearance_android_fontFamily) && !tintTypedArray.hasValue(R$styleable.TextAppearance_fontFamily)) {
            if (tintTypedArray.hasValue(R$styleable.TextAppearance_android_typeface)) {
                this.mAsyncFontPending = false;
                final int int1 = tintTypedArray.getInt(R$styleable.TextAppearance_android_typeface, 1);
                if (int1 != 1) {
                    if (int1 != 2) {
                        if (int1 == 3) {
                            this.mFontTypeface = Typeface.MONOSPACE;
                        }
                    }
                    else {
                        this.mFontTypeface = Typeface.SERIF;
                    }
                }
                else {
                    this.mFontTypeface = Typeface.SANS_SERIF;
                }
            }
            return;
        }
        this.mFontTypeface = null;
        int n;
        if (tintTypedArray.hasValue(R$styleable.TextAppearance_fontFamily)) {
            n = R$styleable.TextAppearance_fontFamily;
        }
        else {
            n = R$styleable.TextAppearance_android_fontFamily;
        }
        final int mFontWeight = this.mFontWeight;
        final int mStyle = this.mStyle;
        while (true) {
            if (context.isRestricted()) {
                break Label_0332;
            }
            final ResourcesCompat.FontCallback fontCallback = new ResourcesCompat.FontCallback() {
                final /* synthetic */ WeakReference val$textViewWeak = new WeakReference((T)AppCompatTextHelper.this.mView);
                
                @Override
                public void onFontRetrievalFailed(final int n) {
                }
                
                @Override
                public void onFontRetrieved(final Typeface typeface) {
                    Typeface create = typeface;
                    if (Build$VERSION.SDK_INT >= 28) {
                        final int val$fontWeight = mFontWeight;
                        create = typeface;
                        if (val$fontWeight != -1) {
                            create = Typeface.create(typeface, val$fontWeight, (mStyle & 0x2) != 0x0);
                        }
                    }
                    AppCompatTextHelper.this.onAsyncTypefaceReceived(this.val$textViewWeak, create);
                }
            };
            try {
                final Typeface font = tintTypedArray.getFont(n, this.mStyle, fontCallback);
                if (font != null) {
                    if (sdk_INT >= 28 && this.mFontWeight != -1) {
                        this.mFontTypeface = Typeface.create(Typeface.create(font, 0), this.mFontWeight, (this.mStyle & 0x2) != 0x0);
                    }
                    else {
                        this.mFontTypeface = font;
                    }
                }
                this.mAsyncFontPending = (this.mFontTypeface == null);
                if (this.mFontTypeface == null) {
                    final String string = tintTypedArray.getString(n);
                    if (string != null) {
                        if (sdk_INT >= 28 && this.mFontWeight != -1) {
                            final Typeface create = Typeface.create(string, 0);
                            n = this.mFontWeight;
                            boolean b2 = b;
                            if ((this.mStyle & 0x2) != 0x0) {
                                b2 = true;
                            }
                            this.mFontTypeface = Typeface.create(create, n, b2);
                        }
                        else {
                            this.mFontTypeface = Typeface.create(string, this.mStyle);
                        }
                    }
                }
            }
            catch (UnsupportedOperationException | Resources$NotFoundException ex) {
                continue;
            }
            break;
        }
    }
    
    void applyCompoundDrawablesTints() {
        if (this.mDrawableLeftTint != null || this.mDrawableTopTint != null || this.mDrawableRightTint != null || this.mDrawableBottomTint != null) {
            final Drawable[] compoundDrawables = this.mView.getCompoundDrawables();
            this.applyCompoundDrawableTint(compoundDrawables[0], this.mDrawableLeftTint);
            this.applyCompoundDrawableTint(compoundDrawables[1], this.mDrawableTopTint);
            this.applyCompoundDrawableTint(compoundDrawables[2], this.mDrawableRightTint);
            this.applyCompoundDrawableTint(compoundDrawables[3], this.mDrawableBottomTint);
        }
        if (Build$VERSION.SDK_INT >= 17 && (this.mDrawableStartTint != null || this.mDrawableEndTint != null)) {
            final Drawable[] compoundDrawablesRelative = this.mView.getCompoundDrawablesRelative();
            this.applyCompoundDrawableTint(compoundDrawablesRelative[0], this.mDrawableStartTint);
            this.applyCompoundDrawableTint(compoundDrawablesRelative[2], this.mDrawableEndTint);
        }
    }
    
    void autoSizeText() {
        this.mAutoSizeTextHelper.autoSizeText();
    }
    
    int getAutoSizeMaxTextSize() {
        return this.mAutoSizeTextHelper.getAutoSizeMaxTextSize();
    }
    
    int getAutoSizeMinTextSize() {
        return this.mAutoSizeTextHelper.getAutoSizeMinTextSize();
    }
    
    int getAutoSizeStepGranularity() {
        return this.mAutoSizeTextHelper.getAutoSizeStepGranularity();
    }
    
    int[] getAutoSizeTextAvailableSizes() {
        return this.mAutoSizeTextHelper.getAutoSizeTextAvailableSizes();
    }
    
    int getAutoSizeTextType() {
        return this.mAutoSizeTextHelper.getAutoSizeTextType();
    }
    
    boolean isAutoSizeEnabled() {
        return this.mAutoSizeTextHelper.isAutoSizeEnabled();
    }
    
    @SuppressLint({ "NewApi" })
    void loadFromAttributes(final AttributeSet set, int n) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        final Context context = this.mView.getContext();
        final AppCompatDrawableManager value = AppCompatDrawableManager.get();
        final TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, set, R$styleable.AppCompatTextHelper, n, 0);
        final TextView mView = this.mView;
        ViewCompat.saveAttributeDataForStyleable((View)mView, mView.getContext(), R$styleable.AppCompatTextHelper, set, obtainStyledAttributes.getWrappedTypeArray(), n, 0);
        final int resourceId = obtainStyledAttributes.getResourceId(R$styleable.AppCompatTextHelper_android_textAppearance, -1);
        if (obtainStyledAttributes.hasValue(R$styleable.AppCompatTextHelper_android_drawableLeft)) {
            this.mDrawableLeftTint = createTintInfo(context, value, obtainStyledAttributes.getResourceId(R$styleable.AppCompatTextHelper_android_drawableLeft, 0));
        }
        if (obtainStyledAttributes.hasValue(R$styleable.AppCompatTextHelper_android_drawableTop)) {
            this.mDrawableTopTint = createTintInfo(context, value, obtainStyledAttributes.getResourceId(R$styleable.AppCompatTextHelper_android_drawableTop, 0));
        }
        if (obtainStyledAttributes.hasValue(R$styleable.AppCompatTextHelper_android_drawableRight)) {
            this.mDrawableRightTint = createTintInfo(context, value, obtainStyledAttributes.getResourceId(R$styleable.AppCompatTextHelper_android_drawableRight, 0));
        }
        if (obtainStyledAttributes.hasValue(R$styleable.AppCompatTextHelper_android_drawableBottom)) {
            this.mDrawableBottomTint = createTintInfo(context, value, obtainStyledAttributes.getResourceId(R$styleable.AppCompatTextHelper_android_drawableBottom, 0));
        }
        if (sdk_INT >= 17) {
            if (obtainStyledAttributes.hasValue(R$styleable.AppCompatTextHelper_android_drawableStart)) {
                this.mDrawableStartTint = createTintInfo(context, value, obtainStyledAttributes.getResourceId(R$styleable.AppCompatTextHelper_android_drawableStart, 0));
            }
            if (obtainStyledAttributes.hasValue(R$styleable.AppCompatTextHelper_android_drawableEnd)) {
                this.mDrawableEndTint = createTintInfo(context, value, obtainStyledAttributes.getResourceId(R$styleable.AppCompatTextHelper_android_drawableEnd, 0));
            }
        }
        obtainStyledAttributes.recycle();
        final boolean b = this.mView.getTransformationMethod() instanceof PasswordTransformationMethod;
        int allCaps;
        int n2;
        ColorStateList colorStateList3;
        ColorStateList colorStateList4;
        String string2;
        ColorStateList colorStateList5;
        String string3;
        if (resourceId != -1) {
            final TintTypedArray obtainStyledAttributes2 = TintTypedArray.obtainStyledAttributes(context, resourceId, R$styleable.TextAppearance);
            if (!b && obtainStyledAttributes2.hasValue(R$styleable.TextAppearance_textAllCaps)) {
                allCaps = (obtainStyledAttributes2.getBoolean(R$styleable.TextAppearance_textAllCaps, false) ? 1 : 0);
                n2 = 1;
            }
            else {
                allCaps = (n2 = 0);
            }
            this.updateTypefaceAndStyle(context, obtainStyledAttributes2);
            ColorStateList list = null;
            Label_0457: {
                ColorStateList list2;
                if (sdk_INT < 23) {
                    ColorStateList colorStateList;
                    if (obtainStyledAttributes2.hasValue(R$styleable.TextAppearance_android_textColor)) {
                        colorStateList = obtainStyledAttributes2.getColorStateList(R$styleable.TextAppearance_android_textColor);
                    }
                    else {
                        colorStateList = null;
                    }
                    ColorStateList colorStateList2;
                    if (obtainStyledAttributes2.hasValue(R$styleable.TextAppearance_android_textColorHint)) {
                        colorStateList2 = obtainStyledAttributes2.getColorStateList(R$styleable.TextAppearance_android_textColorHint);
                    }
                    else {
                        colorStateList2 = null;
                    }
                    list = colorStateList;
                    list2 = colorStateList2;
                    if (obtainStyledAttributes2.hasValue(R$styleable.TextAppearance_android_textColorLink)) {
                        colorStateList3 = obtainStyledAttributes2.getColorStateList(R$styleable.TextAppearance_android_textColorLink);
                        list = colorStateList;
                        colorStateList4 = colorStateList2;
                        break Label_0457;
                    }
                }
                else {
                    list = null;
                    list2 = null;
                }
                colorStateList3 = null;
                colorStateList4 = list2;
            }
            String string;
            if (obtainStyledAttributes2.hasValue(R$styleable.TextAppearance_textLocale)) {
                string = obtainStyledAttributes2.getString(R$styleable.TextAppearance_textLocale);
            }
            else {
                string = null;
            }
            if (sdk_INT >= 26 && obtainStyledAttributes2.hasValue(R$styleable.TextAppearance_fontVariationSettings)) {
                string2 = obtainStyledAttributes2.getString(R$styleable.TextAppearance_fontVariationSettings);
            }
            else {
                string2 = null;
            }
            obtainStyledAttributes2.recycle();
            colorStateList5 = list;
            string3 = string;
        }
        else {
            allCaps = (n2 = 0);
            string2 = null;
            colorStateList5 = null;
            string3 = null;
            colorStateList4 = null;
            colorStateList3 = null;
        }
        final TintTypedArray obtainStyledAttributes3 = TintTypedArray.obtainStyledAttributes(context, set, R$styleable.TextAppearance, n, 0);
        if (!b && obtainStyledAttributes3.hasValue(R$styleable.TextAppearance_textAllCaps)) {
            allCaps = (obtainStyledAttributes3.getBoolean(R$styleable.TextAppearance_textAllCaps, false) ? 1 : 0);
            n2 = 1;
        }
        ColorStateList textColor = colorStateList5;
        ColorStateList hintTextColor = colorStateList4;
        ColorStateList colorStateList6 = colorStateList3;
        if (sdk_INT < 23) {
            if (obtainStyledAttributes3.hasValue(R$styleable.TextAppearance_android_textColor)) {
                colorStateList5 = obtainStyledAttributes3.getColorStateList(R$styleable.TextAppearance_android_textColor);
            }
            if (obtainStyledAttributes3.hasValue(R$styleable.TextAppearance_android_textColorHint)) {
                colorStateList4 = obtainStyledAttributes3.getColorStateList(R$styleable.TextAppearance_android_textColorHint);
            }
            textColor = colorStateList5;
            hintTextColor = colorStateList4;
            colorStateList6 = colorStateList3;
            if (obtainStyledAttributes3.hasValue(R$styleable.TextAppearance_android_textColorLink)) {
                colorStateList6 = obtainStyledAttributes3.getColorStateList(R$styleable.TextAppearance_android_textColorLink);
                hintTextColor = colorStateList4;
                textColor = colorStateList5;
            }
        }
        if (obtainStyledAttributes3.hasValue(R$styleable.TextAppearance_textLocale)) {
            string3 = obtainStyledAttributes3.getString(R$styleable.TextAppearance_textLocale);
        }
        String string4 = string2;
        if (sdk_INT >= 26) {
            string4 = string2;
            if (obtainStyledAttributes3.hasValue(R$styleable.TextAppearance_fontVariationSettings)) {
                string4 = obtainStyledAttributes3.getString(R$styleable.TextAppearance_fontVariationSettings);
            }
        }
        if (sdk_INT >= 28 && obtainStyledAttributes3.hasValue(R$styleable.TextAppearance_android_textSize) && obtainStyledAttributes3.getDimensionPixelSize(R$styleable.TextAppearance_android_textSize, -1) == 0) {
            this.mView.setTextSize(0, 0.0f);
        }
        this.updateTypefaceAndStyle(context, obtainStyledAttributes3);
        obtainStyledAttributes3.recycle();
        if (textColor != null) {
            this.mView.setTextColor(textColor);
        }
        if (hintTextColor != null) {
            this.mView.setHintTextColor(hintTextColor);
        }
        if (colorStateList6 != null) {
            this.mView.setLinkTextColor(colorStateList6);
        }
        if (!b && n2 != 0) {
            this.setAllCaps((boolean)(allCaps != 0));
        }
        final Typeface mFontTypeface = this.mFontTypeface;
        if (mFontTypeface != null) {
            if (this.mFontWeight == -1) {
                this.mView.setTypeface(mFontTypeface, this.mStyle);
            }
            else {
                this.mView.setTypeface(mFontTypeface);
            }
        }
        if (string4 != null) {
            this.mView.setFontVariationSettings(string4);
        }
        if (string3 != null) {
            if (sdk_INT >= 24) {
                this.mView.setTextLocales(LocaleList.forLanguageTags(string3));
            }
            else if (sdk_INT >= 21) {
                this.mView.setTextLocale(Locale.forLanguageTag(string3.substring(0, string3.indexOf(44))));
            }
        }
        this.mAutoSizeTextHelper.loadFromAttributes(set, n);
        if (AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE && this.mAutoSizeTextHelper.getAutoSizeTextType() != 0) {
            final int[] autoSizeTextAvailableSizes = this.mAutoSizeTextHelper.getAutoSizeTextAvailableSizes();
            if (autoSizeTextAvailableSizes.length > 0) {
                if (this.mView.getAutoSizeStepGranularity() != -1.0f) {
                    this.mView.setAutoSizeTextTypeUniformWithConfiguration(this.mAutoSizeTextHelper.getAutoSizeMinTextSize(), this.mAutoSizeTextHelper.getAutoSizeMaxTextSize(), this.mAutoSizeTextHelper.getAutoSizeStepGranularity(), 0);
                }
                else {
                    this.mView.setAutoSizeTextTypeUniformWithPresetSizes(autoSizeTextAvailableSizes, 0);
                }
            }
        }
        final TintTypedArray obtainStyledAttributes4 = TintTypedArray.obtainStyledAttributes(context, set, R$styleable.AppCompatTextView);
        n = obtainStyledAttributes4.getResourceId(R$styleable.AppCompatTextView_drawableLeftCompat, -1);
        Drawable drawable;
        if (n != -1) {
            drawable = value.getDrawable(context, n);
        }
        else {
            drawable = null;
        }
        n = obtainStyledAttributes4.getResourceId(R$styleable.AppCompatTextView_drawableTopCompat, -1);
        Drawable drawable2;
        if (n != -1) {
            drawable2 = value.getDrawable(context, n);
        }
        else {
            drawable2 = null;
        }
        n = obtainStyledAttributes4.getResourceId(R$styleable.AppCompatTextView_drawableRightCompat, -1);
        Drawable drawable3;
        if (n != -1) {
            drawable3 = value.getDrawable(context, n);
        }
        else {
            drawable3 = null;
        }
        n = obtainStyledAttributes4.getResourceId(R$styleable.AppCompatTextView_drawableBottomCompat, -1);
        Drawable drawable4;
        if (n != -1) {
            drawable4 = value.getDrawable(context, n);
        }
        else {
            drawable4 = null;
        }
        n = obtainStyledAttributes4.getResourceId(R$styleable.AppCompatTextView_drawableStartCompat, -1);
        Drawable drawable5;
        if (n != -1) {
            drawable5 = value.getDrawable(context, n);
        }
        else {
            drawable5 = null;
        }
        n = obtainStyledAttributes4.getResourceId(R$styleable.AppCompatTextView_drawableEndCompat, -1);
        Drawable drawable6;
        if (n != -1) {
            drawable6 = value.getDrawable(context, n);
        }
        else {
            drawable6 = null;
        }
        this.setCompoundDrawables(drawable, drawable2, drawable3, drawable4, drawable5, drawable6);
        if (obtainStyledAttributes4.hasValue(R$styleable.AppCompatTextView_drawableTint)) {
            TextViewCompat.setCompoundDrawableTintList(this.mView, obtainStyledAttributes4.getColorStateList(R$styleable.AppCompatTextView_drawableTint));
        }
        if (obtainStyledAttributes4.hasValue(R$styleable.AppCompatTextView_drawableTintMode)) {
            TextViewCompat.setCompoundDrawableTintMode(this.mView, DrawableUtils.parseTintMode(obtainStyledAttributes4.getInt(R$styleable.AppCompatTextView_drawableTintMode, -1), null));
        }
        final int dimensionPixelSize = obtainStyledAttributes4.getDimensionPixelSize(R$styleable.AppCompatTextView_firstBaselineToTopHeight, -1);
        n = obtainStyledAttributes4.getDimensionPixelSize(R$styleable.AppCompatTextView_lastBaselineToBottomHeight, -1);
        final int dimensionPixelSize2 = obtainStyledAttributes4.getDimensionPixelSize(R$styleable.AppCompatTextView_lineHeight, -1);
        obtainStyledAttributes4.recycle();
        if (dimensionPixelSize != -1) {
            TextViewCompat.setFirstBaselineToTopHeight(this.mView, dimensionPixelSize);
        }
        if (n != -1) {
            TextViewCompat.setLastBaselineToBottomHeight(this.mView, n);
        }
        if (dimensionPixelSize2 != -1) {
            TextViewCompat.setLineHeight(this.mView, dimensionPixelSize2);
        }
    }
    
    void onAsyncTypefaceReceived(final WeakReference<TextView> weakReference, final Typeface mFontTypeface) {
        if (this.mAsyncFontPending) {
            this.mFontTypeface = mFontTypeface;
            final TextView textView = weakReference.get();
            if (textView != null) {
                textView.setTypeface(mFontTypeface, this.mStyle);
            }
        }
    }
    
    void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        if (!AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE) {
            this.autoSizeText();
        }
    }
    
    void onSetCompoundDrawables() {
        this.applyCompoundDrawablesTints();
    }
    
    void onSetTextAppearance(final Context context, final int n) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        final TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, n, R$styleable.TextAppearance);
        if (obtainStyledAttributes.hasValue(R$styleable.TextAppearance_textAllCaps)) {
            this.setAllCaps(obtainStyledAttributes.getBoolean(R$styleable.TextAppearance_textAllCaps, false));
        }
        if (sdk_INT < 23 && obtainStyledAttributes.hasValue(R$styleable.TextAppearance_android_textColor)) {
            final ColorStateList colorStateList = obtainStyledAttributes.getColorStateList(R$styleable.TextAppearance_android_textColor);
            if (colorStateList != null) {
                this.mView.setTextColor(colorStateList);
            }
        }
        if (obtainStyledAttributes.hasValue(R$styleable.TextAppearance_android_textSize) && obtainStyledAttributes.getDimensionPixelSize(R$styleable.TextAppearance_android_textSize, -1) == 0) {
            this.mView.setTextSize(0, 0.0f);
        }
        this.updateTypefaceAndStyle(context, obtainStyledAttributes);
        if (sdk_INT >= 26 && obtainStyledAttributes.hasValue(R$styleable.TextAppearance_fontVariationSettings)) {
            final String string = obtainStyledAttributes.getString(R$styleable.TextAppearance_fontVariationSettings);
            if (string != null) {
                this.mView.setFontVariationSettings(string);
            }
        }
        obtainStyledAttributes.recycle();
        final Typeface mFontTypeface = this.mFontTypeface;
        if (mFontTypeface != null) {
            this.mView.setTypeface(mFontTypeface, this.mStyle);
        }
    }
    
    void setAllCaps(final boolean allCaps) {
        this.mView.setAllCaps(allCaps);
    }
    
    void setAutoSizeTextTypeUniformWithConfiguration(final int n, final int n2, final int n3, final int n4) throws IllegalArgumentException {
        this.mAutoSizeTextHelper.setAutoSizeTextTypeUniformWithConfiguration(n, n2, n3, n4);
    }
    
    void setAutoSizeTextTypeUniformWithPresetSizes(final int[] array, final int n) throws IllegalArgumentException {
        this.mAutoSizeTextHelper.setAutoSizeTextTypeUniformWithPresetSizes(array, n);
    }
    
    void setAutoSizeTextTypeWithDefaults(final int autoSizeTextTypeWithDefaults) {
        this.mAutoSizeTextHelper.setAutoSizeTextTypeWithDefaults(autoSizeTextTypeWithDefaults);
    }
    
    void setCompoundDrawableTintList(final ColorStateList mTintList) {
        if (this.mDrawableTint == null) {
            this.mDrawableTint = new TintInfo();
        }
        final TintInfo mDrawableTint = this.mDrawableTint;
        mDrawableTint.mTintList = mTintList;
        mDrawableTint.mHasTintList = (mTintList != null);
        this.setCompoundTints();
    }
    
    void setCompoundDrawableTintMode(final PorterDuff$Mode mTintMode) {
        if (this.mDrawableTint == null) {
            this.mDrawableTint = new TintInfo();
        }
        final TintInfo mDrawableTint = this.mDrawableTint;
        mDrawableTint.mTintMode = mTintMode;
        mDrawableTint.mHasTintMode = (mTintMode != null);
        this.setCompoundTints();
    }
    
    void setTextSize(final int n, final float n2) {
        if (!AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE && !this.isAutoSizeEnabled()) {
            this.setTextSizeInternal(n, n2);
        }
    }
}
