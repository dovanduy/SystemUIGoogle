// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import java.lang.ref.WeakReference;
import com.android.systemui.R$attr;
import android.content.res.TypedArray;
import com.android.systemui.R$style;
import android.text.TextUtils;
import android.os.SystemClock;
import com.android.systemui.R$id;
import android.view.View;
import com.android.systemui.Dependency;
import android.os.Looper;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.content.Context;
import android.os.Handler;
import android.content.res.ColorStateList;
import com.android.systemui.statusbar.policy.ConfigurationController;
import android.widget.TextView;

public class KeyguardMessageArea extends TextView implements SecurityMessageDisplay, ConfigurationListener
{
    private static final Object ANNOUNCE_TOKEN;
    private boolean mBouncerVisible;
    private final ConfigurationController mConfigurationController;
    private ColorStateList mDefaultColorState;
    private final Handler mHandler;
    private KeyguardUpdateMonitorCallback mInfoCallback;
    private CharSequence mMessage;
    private ColorStateList mNextMessageColorState;
    
    static {
        ANNOUNCE_TOKEN = new Object();
    }
    
    public KeyguardMessageArea(final Context context) {
        super(context, (AttributeSet)null);
        this.mNextMessageColorState = ColorStateList.valueOf(-1);
        this.mInfoCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onFinishedGoingToSleep(final int n) {
                KeyguardMessageArea.this.setSelected(false);
            }
            
            @Override
            public void onKeyguardBouncerChanged(final boolean b) {
                KeyguardMessageArea.this.mBouncerVisible = b;
                KeyguardMessageArea.this.update();
            }
            
            @Override
            public void onStartedWakingUp() {
                KeyguardMessageArea.this.setSelected(true);
            }
        };
        throw new IllegalStateException("This constructor should never be invoked");
    }
    
    public KeyguardMessageArea(final Context context, final AttributeSet set, final KeyguardUpdateMonitor keyguardUpdateMonitor, final ConfigurationController mConfigurationController) {
        super(context, set);
        this.mNextMessageColorState = ColorStateList.valueOf(-1);
        this.mInfoCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onFinishedGoingToSleep(final int n) {
                KeyguardMessageArea.this.setSelected(false);
            }
            
            @Override
            public void onKeyguardBouncerChanged(final boolean b) {
                KeyguardMessageArea.this.mBouncerVisible = b;
                KeyguardMessageArea.this.update();
            }
            
            @Override
            public void onStartedWakingUp() {
                KeyguardMessageArea.this.setSelected(true);
            }
        };
        this.setLayerType(2, (Paint)null);
        keyguardUpdateMonitor.registerCallback(this.mInfoCallback);
        this.mHandler = new Handler(Looper.myLooper());
        this.mConfigurationController = mConfigurationController;
        this.onThemeChanged();
    }
    
    public KeyguardMessageArea(final Context context, final AttributeSet set, final ConfigurationController configurationController) {
        this(context, set, Dependency.get(KeyguardUpdateMonitor.class), configurationController);
    }
    
    private void clearMessage() {
        this.mMessage = null;
        this.update();
    }
    
    public static KeyguardMessageArea findSecurityMessageDisplay(final View view) {
        KeyguardMessageArea keyguardMessageArea;
        if ((keyguardMessageArea = (KeyguardMessageArea)view.findViewById(R$id.keyguard_message_area)) == null) {
            keyguardMessageArea = (KeyguardMessageArea)view.getRootView().findViewById(R$id.keyguard_message_area);
        }
        if (keyguardMessageArea != null) {
            return keyguardMessageArea;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Can't find keyguard_message_area in ");
        sb.append(view.getClass());
        throw new RuntimeException(sb.toString());
    }
    
    private void securityMessageChanged(final CharSequence mMessage) {
        final Object announce_TOKEN = KeyguardMessageArea.ANNOUNCE_TOKEN;
        this.mMessage = mMessage;
        this.update();
        this.mHandler.removeCallbacksAndMessages(announce_TOKEN);
        this.mHandler.postAtTime((Runnable)new AnnounceRunnable((View)this, this.getText()), announce_TOKEN, SystemClock.uptimeMillis() + 250L);
    }
    
    private void update() {
        final CharSequence mMessage = this.mMessage;
        int visibility;
        if (!TextUtils.isEmpty(mMessage) && this.mBouncerVisible) {
            visibility = 0;
        }
        else {
            visibility = 4;
        }
        this.setVisibility(visibility);
        this.setText(mMessage);
        ColorStateList textColor = this.mDefaultColorState;
        if (this.mNextMessageColorState.getDefaultColor() != -1) {
            textColor = this.mNextMessageColorState;
            this.mNextMessageColorState = ColorStateList.valueOf(-1);
        }
        this.setTextColor(textColor);
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mConfigurationController.addCallback((ConfigurationController.ConfigurationListener)this);
        this.onThemeChanged();
    }
    
    public void onDensityOrFontScaleChanged() {
        final TypedArray obtainStyledAttributes = super.mContext.obtainStyledAttributes(R$style.Keyguard_TextView, new int[] { 16842901 });
        this.setTextSize(0, (float)obtainStyledAttributes.getDimensionPixelSize(0, 0));
        obtainStyledAttributes.recycle();
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mConfigurationController.removeCallback((ConfigurationController.ConfigurationListener)this);
    }
    
    protected void onFinishInflate() {
        this.setSelected(Dependency.get(KeyguardUpdateMonitor.class).isDeviceInteractive());
    }
    
    public void onThemeChanged() {
        final TypedArray obtainStyledAttributes = super.mContext.obtainStyledAttributes(new int[] { R$attr.wallpaperTextColor });
        final ColorStateList value = ColorStateList.valueOf(obtainStyledAttributes.getColor(0, -65536));
        obtainStyledAttributes.recycle();
        this.mDefaultColorState = value;
        this.update();
    }
    
    public void setMessage(final int n) {
        CharSequence text;
        if (n != 0) {
            text = this.getContext().getResources().getText(n);
        }
        else {
            text = null;
        }
        this.setMessage(text);
    }
    
    public void setMessage(final CharSequence charSequence) {
        if (!TextUtils.isEmpty(charSequence)) {
            this.securityMessageChanged(charSequence);
        }
        else {
            this.clearMessage();
        }
    }
    
    public void setNextMessageColor(final ColorStateList mNextMessageColorState) {
        this.mNextMessageColorState = mNextMessageColorState;
    }
    
    private static class AnnounceRunnable implements Runnable
    {
        private final WeakReference<View> mHost;
        private final CharSequence mTextToAnnounce;
        
        AnnounceRunnable(final View referent, final CharSequence mTextToAnnounce) {
            this.mHost = new WeakReference<View>(referent);
            this.mTextToAnnounce = mTextToAnnounce;
        }
        
        @Override
        public void run() {
            final View view = this.mHost.get();
            if (view != null) {
                view.announceForAccessibility(this.mTextToAnnounce);
            }
        }
    }
}
