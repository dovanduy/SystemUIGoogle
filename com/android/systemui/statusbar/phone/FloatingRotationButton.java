// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.graphics.drawable.Drawable$Callback;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup$LayoutParams;
import android.view.WindowManager$LayoutParams;
import android.view.View$OnHoverListener;
import android.view.View$OnClickListener;
import android.graphics.Color;
import com.android.systemui.R$drawable;
import com.android.settingslib.Utils;
import com.android.systemui.R$attr;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.content.res.Resources;
import com.android.systemui.R$dimen;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.view.WindowManager;
import com.android.systemui.statusbar.policy.KeyButtonView;
import com.android.systemui.statusbar.policy.KeyButtonDrawable;
import android.content.Context;

public class FloatingRotationButton implements RotationButton
{
    private boolean mCanShow;
    private final Context mContext;
    private final int mDiameter;
    private boolean mIsShowing;
    private KeyButtonDrawable mKeyButtonDrawable;
    private final KeyButtonView mKeyButtonView;
    private final int mMargin;
    private RotationButtonController mRotationButtonController;
    private final WindowManager mWindowManager;
    
    FloatingRotationButton(final Context mContext) {
        this.mCanShow = true;
        this.mContext = mContext;
        this.mWindowManager = (WindowManager)mContext.getSystemService("window");
        (this.mKeyButtonView = (KeyButtonView)LayoutInflater.from(this.mContext).inflate(R$layout.rotate_suggestion, (ViewGroup)null)).setVisibility(0);
        final Resources resources = this.mContext.getResources();
        this.mDiameter = resources.getDimensionPixelSize(R$dimen.floating_rotation_button_diameter);
        this.mMargin = Math.max(resources.getDimensionPixelSize(R$dimen.floating_rotation_button_min_margin), resources.getDimensionPixelSize(R$dimen.rounded_corner_content_padding));
    }
    
    @Override
    public View getCurrentView() {
        return (View)this.mKeyButtonView;
    }
    
    @Override
    public KeyButtonDrawable getImageDrawable() {
        final ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(this.mContext.getApplicationContext(), this.mRotationButtonController.getStyleRes());
        final int themeAttr = Utils.getThemeAttr((Context)contextThemeWrapper, R$attr.darkIconTheme);
        final ContextThemeWrapper contextThemeWrapper2 = new ContextThemeWrapper((Context)contextThemeWrapper, Utils.getThemeAttr((Context)contextThemeWrapper, R$attr.lightIconTheme));
        final int colorAttrDefaultColor = Utils.getColorAttrDefaultColor((Context)new ContextThemeWrapper((Context)contextThemeWrapper, themeAttr), R$attr.singleToneColor);
        return KeyButtonDrawable.create((Context)contextThemeWrapper2, Utils.getColorAttrDefaultColor((Context)contextThemeWrapper2, R$attr.singleToneColor), colorAttrDefaultColor, R$drawable.ic_sysbar_rotate_button, false, Color.valueOf((float)Color.red(colorAttrDefaultColor), (float)Color.green(colorAttrDefaultColor), (float)Color.blue(colorAttrDefaultColor), 0.92f));
    }
    
    @Override
    public boolean hide() {
        if (!this.mIsShowing) {
            return false;
        }
        this.mWindowManager.removeViewImmediate((View)this.mKeyButtonView);
        this.mIsShowing = false;
        return true;
    }
    
    @Override
    public boolean isVisible() {
        return this.mIsShowing;
    }
    
    @Override
    public void setCanShowRotationButton(final boolean mCanShow) {
        if (!(this.mCanShow = mCanShow)) {
            this.hide();
        }
    }
    
    @Override
    public void setDarkIntensity(final float darkIntensity) {
        this.mKeyButtonView.setDarkIntensity(darkIntensity);
    }
    
    @Override
    public void setOnClickListener(final View$OnClickListener onClickListener) {
        this.mKeyButtonView.setOnClickListener(onClickListener);
    }
    
    @Override
    public void setOnHoverListener(final View$OnHoverListener onHoverListener) {
        this.mKeyButtonView.setOnHoverListener(onHoverListener);
    }
    
    @Override
    public void setRotationButtonController(final RotationButtonController mRotationButtonController) {
        this.mRotationButtonController = mRotationButtonController;
    }
    
    @Override
    public boolean show() {
        if (this.mCanShow && !this.mIsShowing) {
            this.mIsShowing = true;
            final int mDiameter = this.mDiameter;
            final int mMargin = this.mMargin;
            final WindowManager$LayoutParams windowManager$LayoutParams = new WindowManager$LayoutParams(mDiameter, mDiameter, mMargin, mMargin, 2024, 8, -3);
            windowManager$LayoutParams.privateFlags |= 0x10;
            windowManager$LayoutParams.setTitle((CharSequence)"FloatingRotationButton");
            windowManager$LayoutParams.setFitInsetsTypes(0);
            final int rotation = this.mWindowManager.getDefaultDisplay().getRotation();
            if (rotation != 0) {
                if (rotation != 1) {
                    if (rotation != 2) {
                        if (rotation == 3) {
                            windowManager$LayoutParams.gravity = 51;
                        }
                    }
                    else {
                        windowManager$LayoutParams.gravity = 53;
                    }
                }
                else {
                    windowManager$LayoutParams.gravity = 85;
                }
            }
            else {
                windowManager$LayoutParams.gravity = 83;
            }
            this.updateIcon();
            this.mWindowManager.addView((View)this.mKeyButtonView, (ViewGroup$LayoutParams)windowManager$LayoutParams);
            final KeyButtonDrawable mKeyButtonDrawable = this.mKeyButtonDrawable;
            if (mKeyButtonDrawable != null && mKeyButtonDrawable.canAnimate()) {
                this.mKeyButtonDrawable.resetAnimation();
                this.mKeyButtonDrawable.startAnimation();
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void updateIcon() {
        if (!this.mIsShowing) {
            return;
        }
        final KeyButtonDrawable imageDrawable = this.getImageDrawable();
        this.mKeyButtonDrawable = imageDrawable;
        this.mKeyButtonView.setImageDrawable(imageDrawable);
        this.mKeyButtonDrawable.setCallback((Drawable$Callback)this.mKeyButtonView);
        final KeyButtonDrawable mKeyButtonDrawable = this.mKeyButtonDrawable;
        if (mKeyButtonDrawable != null && mKeyButtonDrawable.canAnimate()) {
            this.mKeyButtonDrawable.resetAnimation();
            this.mKeyButtonDrawable.startAnimation();
        }
    }
}
