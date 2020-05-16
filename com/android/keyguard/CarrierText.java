// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.content.res.TypedArray;
import java.util.Locale;
import android.text.method.SingleLineTransformationMethod;
import android.text.TextUtils$TruncateAt;
import android.view.View;
import com.android.systemui.Dependency;
import android.text.method.TransformationMethod;
import com.android.systemui.R$styleable;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.TextView;

public class CarrierText extends TextView
{
    private static CharSequence mSeparator;
    private CarrierTextController.CarrierTextCallback mCarrierTextCallback;
    private CarrierTextController mCarrierTextController;
    private boolean mShouldMarquee;
    private boolean mShowAirplaneMode;
    private boolean mShowMissingSim;
    
    public CarrierText(final Context context) {
        this(context, null);
    }
    
    public CarrierText(Context obtainStyledAttributes, final AttributeSet set) {
        super(obtainStyledAttributes, set);
        this.mCarrierTextCallback = new CarrierTextController.CarrierTextCallback() {
            @Override
            public void finishedWakingUp() {
                CarrierText.this.setSelected(true);
            }
            
            @Override
            public void startedGoingToSleep() {
                CarrierText.this.setSelected(false);
            }
            
            @Override
            public void updateCarrierInfo(final CarrierTextCallbackInfo carrierTextCallbackInfo) {
                CarrierText.this.setText(carrierTextCallbackInfo.carrierText);
            }
        };
        obtainStyledAttributes = (Context)obtainStyledAttributes.getTheme().obtainStyledAttributes(set, R$styleable.CarrierText, 0, 0);
        try {
            final boolean boolean1 = ((TypedArray)obtainStyledAttributes).getBoolean(R$styleable.CarrierText_allCaps, false);
            this.mShowAirplaneMode = ((TypedArray)obtainStyledAttributes).getBoolean(R$styleable.CarrierText_showAirplaneMode, false);
            this.mShowMissingSim = ((TypedArray)obtainStyledAttributes).getBoolean(R$styleable.CarrierText_showMissingSim, false);
            ((TypedArray)obtainStyledAttributes).recycle();
            this.setTransformationMethod((TransformationMethod)new CarrierTextTransformationMethod(super.mContext, boolean1));
        }
        finally {
            ((TypedArray)obtainStyledAttributes).recycle();
        }
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mCarrierTextController.setListening(this.mCarrierTextCallback);
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mCarrierTextController.setListening(null);
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mCarrierTextController = new CarrierTextController(super.mContext, CarrierText.mSeparator = this.getResources().getString(17040408), this.mShowAirplaneMode, this.mShowMissingSim);
        this.setSelected(this.mShouldMarquee = Dependency.get(KeyguardUpdateMonitor.class).isDeviceInteractive());
    }
    
    protected void onVisibilityChanged(final View view, final int n) {
        super.onVisibilityChanged(view, n);
        if (n == 0) {
            this.setEllipsize(TextUtils$TruncateAt.MARQUEE);
        }
        else {
            this.setEllipsize(TextUtils$TruncateAt.END);
        }
    }
    
    private class CarrierTextTransformationMethod extends SingleLineTransformationMethod
    {
        private final boolean mAllCaps;
        private final Locale mLocale;
        
        public CarrierTextTransformationMethod(final CarrierText carrierText, final Context context, final boolean mAllCaps) {
            this.mLocale = context.getResources().getConfiguration().locale;
            this.mAllCaps = mAllCaps;
        }
        
        public CharSequence getTransformation(CharSequence charSequence, final View view) {
            final CharSequence charSequence2 = charSequence = super.getTransformation(charSequence, view);
            if (this.mAllCaps && (charSequence = charSequence2) != null) {
                charSequence = charSequence2.toString().toUpperCase(this.mLocale);
            }
            return charSequence;
        }
    }
}
