// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.graphics.Rect;
import android.graphics.Region;
import java.util.Optional;
import android.app.PendingIntent$CanceledException;
import android.util.Log;
import android.view.View;
import android.view.View$OnClickListener;
import com.android.systemui.R$id;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.app.PendingIntent;
import com.google.android.systemui.assist.uihints.input.TouchActionRegion;
import com.android.systemui.statusbar.policy.ConfigurationController;

public class IconController implements KeyboardInfoListener, ZerostateInfoListener, ConfigurationListener, TouchActionRegion
{
    private boolean mHasAccurateLuma;
    private final KeyboardIconView mKeyboardIcon;
    private boolean mKeyboardIconRequested;
    private PendingIntent mOnKeyboardIconTap;
    private PendingIntent mOnZerostateIconTap;
    private final ViewGroup mParent;
    private final ZeroStateIconView mZeroStateIcon;
    private boolean mZerostateIconRequested;
    
    IconController(final LayoutInflater layoutInflater, final ViewGroup mParent, final ConfigurationController configurationController) {
        this.mParent = mParent;
        (this.mKeyboardIcon = (KeyboardIconView)mParent.findViewById(R$id.keyboard)).setOnClickListener((View$OnClickListener)new _$$Lambda$IconController$WA0CZERzsbw72rfckarYEO2VWJ0(this));
        (this.mZeroStateIcon = (ZeroStateIconView)this.mParent.findViewById(R$id.zerostate)).setOnClickListener((View$OnClickListener)new _$$Lambda$IconController$dzD75QXVcUsTjIl1lWcvnU7lpQ0(this));
        configurationController.addCallback((ConfigurationController.ConfigurationListener)this);
    }
    
    private void maybeUpdateIconVisibility(final View view, final boolean b) {
        final int visibility = view.getVisibility();
        int n = 1;
        final int n2 = 0;
        final boolean b2 = visibility == 0;
        if (!b || !this.mHasAccurateLuma) {
            n = 0;
        }
        if ((b2 ? 1 : 0) != n) {
            int visibility2;
            if (n != 0) {
                visibility2 = n2;
            }
            else {
                visibility2 = 8;
            }
            view.setVisibility(visibility2);
        }
    }
    
    private void maybeUpdateKeyboardVisibility() {
        this.maybeUpdateIconVisibility((View)this.mKeyboardIcon, this.mKeyboardIconRequested);
    }
    
    private void maybeUpdateZerostateVisibility() {
        this.maybeUpdateIconVisibility((View)this.mZeroStateIcon, this.mZerostateIconRequested);
    }
    
    @Override
    public Optional<Region> getTouchActionRegion() {
        final Region value = new Region();
        if (this.mKeyboardIcon.getVisibility() == 0) {
            final Rect rect = new Rect();
            this.mKeyboardIcon.getHitRect(rect);
            value.union(rect);
        }
        if (this.mZeroStateIcon.getVisibility() == 0) {
            final Rect rect2 = new Rect();
            this.mZeroStateIcon.getHitRect(rect2);
            value.union(rect2);
        }
        Optional<Region> optional;
        if (value.isEmpty()) {
            optional = Optional.empty();
        }
        else {
            optional = Optional.of(value);
        }
        return optional;
    }
    
    boolean isRequested() {
        return this.mKeyboardIconRequested || this.mZerostateIconRequested;
    }
    
    boolean isVisible() {
        return this.mKeyboardIcon.getVisibility() == 0 || this.mZeroStateIcon.getVisibility() == 0;
    }
    
    @Override
    public void onDensityOrFontScaleChanged() {
        this.mKeyboardIcon.onDensityChanged();
        this.mZeroStateIcon.onDensityChanged();
    }
    
    @Override
    public void onHideKeyboard() {
        this.mKeyboardIconRequested = false;
        this.mOnKeyboardIconTap = null;
        this.maybeUpdateKeyboardVisibility();
    }
    
    @Override
    public void onHideZerostate() {
        this.mZerostateIconRequested = false;
        this.mOnZerostateIconTap = null;
        this.maybeUpdateZerostateVisibility();
    }
    
    @Override
    public void onShowKeyboard(final PendingIntent mOnKeyboardIconTap) {
        this.mKeyboardIconRequested = (mOnKeyboardIconTap != null);
        this.mOnKeyboardIconTap = mOnKeyboardIconTap;
        this.maybeUpdateKeyboardVisibility();
    }
    
    @Override
    public void onShowZerostate(final PendingIntent mOnZerostateIconTap) {
        this.mZerostateIconRequested = (mOnZerostateIconTap != null);
        this.mOnZerostateIconTap = mOnZerostateIconTap;
        this.maybeUpdateZerostateVisibility();
    }
    
    void setHasAccurateLuma(final boolean mHasAccurateLuma) {
        this.mHasAccurateLuma = mHasAccurateLuma;
        this.maybeUpdateKeyboardVisibility();
        this.maybeUpdateZerostateVisibility();
    }
    
    void setHasDarkBackground(final boolean b) {
        this.mKeyboardIcon.setHasDarkBackground(b);
        this.mZeroStateIcon.setHasDarkBackground(b);
    }
}
