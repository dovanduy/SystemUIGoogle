// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.os.UserHandle;
import android.app.ActivityOptions;
import android.util.Log;
import android.os.RemoteException;
import android.util.Slog;
import android.app.ActivityTaskManager;
import android.os.SystemClock;
import com.android.internal.logging.MetricsLogger;
import android.view.ViewConfiguration;
import android.view.MotionEvent;
import com.android.systemui.DejankUtils;
import android.view.View$OnLongClickListener;
import android.view.View$OnClickListener;
import android.content.res.Configuration;
import com.android.systemui.Dependency;
import android.view.View;
import android.telephony.TelephonyManager;
import android.telecom.TelecomManager;
import android.util.AttributeSet;
import android.content.Context;
import android.os.PowerManager;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.util.EmergencyAffordanceManager;
import android.widget.Button;

public class EmergencyButton extends Button
{
    private int mDownX;
    private int mDownY;
    private final EmergencyAffordanceManager mEmergencyAffordanceManager;
    private EmergencyButtonCallback mEmergencyButtonCallback;
    private final boolean mEnableEmergencyCallWhileSimLocked;
    KeyguardUpdateMonitorCallback mInfoCallback;
    private final boolean mIsVoiceCapable;
    private LockPatternUtils mLockPatternUtils;
    private boolean mLongPressWasDragged;
    private PowerManager mPowerManager;
    
    public EmergencyButton(final Context context) {
        this(context, null);
    }
    
    public EmergencyButton(final Context context, final AttributeSet set) {
        super(context, set);
        this.mInfoCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onPhoneStateChanged(final int n) {
                EmergencyButton.this.updateEmergencyCallButton();
            }
            
            @Override
            public void onSimStateChanged(final int n, final int n2, final int n3) {
                EmergencyButton.this.updateEmergencyCallButton();
            }
        };
        this.mIsVoiceCapable = this.getTelephonyManager().isVoiceCapable();
        this.mEnableEmergencyCallWhileSimLocked = super.mContext.getResources().getBoolean(17891456);
        this.mEmergencyAffordanceManager = new EmergencyAffordanceManager(context);
    }
    
    private TelecomManager getTelecommManager() {
        return (TelecomManager)super.mContext.getSystemService("telecom");
    }
    
    private TelephonyManager getTelephonyManager() {
        return (TelephonyManager)super.mContext.getSystemService("phone");
    }
    
    private boolean isInCall() {
        return this.getTelecommManager().isInCall();
    }
    
    private void resumeCall() {
        this.getTelecommManager().showInCallScreen(false);
    }
    
    private void updateEmergencyCallButton() {
        boolean b;
        if (this.mIsVoiceCapable) {
            if (this.isInCall()) {
                b = true;
            }
            else if (Dependency.get(KeyguardUpdateMonitor.class).isSimPinVoiceSecure()) {
                b = this.mEnableEmergencyCallWhileSimLocked;
            }
            else {
                b = this.mLockPatternUtils.isSecure(KeyguardUpdateMonitor.getCurrentUser());
            }
        }
        else {
            b = false;
        }
        if (b) {
            this.setVisibility(0);
            int text;
            if (this.isInCall()) {
                text = 17040469;
            }
            else {
                text = 17040442;
            }
            this.setText(text);
        }
        else {
            this.setVisibility(8);
        }
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Dependency.get(KeyguardUpdateMonitor.class).registerCallback(this.mInfoCallback);
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.updateEmergencyCallButton();
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Dependency.get(KeyguardUpdateMonitor.class).removeCallback(this.mInfoCallback);
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mLockPatternUtils = new LockPatternUtils(super.mContext);
        this.mPowerManager = (PowerManager)super.mContext.getSystemService("power");
        this.setOnClickListener((View$OnClickListener)new _$$Lambda$EmergencyButton$KTHEYrkUJc7xBxT3_mk1U_fqYZ8(this));
        if (this.mEmergencyAffordanceManager.needsEmergencyAffordance()) {
            this.setOnLongClickListener((View$OnLongClickListener)new _$$Lambda$EmergencyButton$lDso_ObwUd3nlVNy8pLMJXmgJO0(this));
        }
        DejankUtils.whitelistIpcs(new _$$Lambda$EmergencyButton$sZFrzLTp9wnvXsMSAmy28W4gnWQ(this));
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        final int mDownX = (int)motionEvent.getX();
        final int mDownY = (int)motionEvent.getY();
        if (motionEvent.getActionMasked() == 0) {
            this.mDownX = mDownX;
            this.mDownY = mDownY;
            this.mLongPressWasDragged = false;
        }
        else {
            final int abs = Math.abs(mDownX - this.mDownX);
            final int abs2 = Math.abs(mDownY - this.mDownY);
            final int scaledTouchSlop = ViewConfiguration.get(super.mContext).getScaledTouchSlop();
            if (Math.abs(abs2) > scaledTouchSlop || Math.abs(abs) > scaledTouchSlop) {
                this.mLongPressWasDragged = true;
            }
        }
        return super.onTouchEvent(motionEvent);
    }
    
    public boolean performLongClick() {
        return super.performLongClick();
    }
    
    public void setCallback(final EmergencyButtonCallback mEmergencyButtonCallback) {
        this.mEmergencyButtonCallback = mEmergencyButtonCallback;
    }
    
    public void takeEmergencyCallAction() {
        MetricsLogger.action(super.mContext, 200);
        final PowerManager mPowerManager = this.mPowerManager;
        if (mPowerManager != null) {
            mPowerManager.userActivity(SystemClock.uptimeMillis(), true);
        }
        try {
            ActivityTaskManager.getService().stopSystemLockTaskMode();
        }
        catch (RemoteException ex) {
            Slog.w("EmergencyButton", "Failed to stop app pinning");
        }
        if (this.isInCall()) {
            this.resumeCall();
            final EmergencyButtonCallback mEmergencyButtonCallback = this.mEmergencyButtonCallback;
            if (mEmergencyButtonCallback != null) {
                mEmergencyButtonCallback.onEmergencyButtonClickedWhenInCall();
            }
        }
        else {
            final KeyguardUpdateMonitor keyguardUpdateMonitor = Dependency.get(KeyguardUpdateMonitor.class);
            if (keyguardUpdateMonitor != null) {
                keyguardUpdateMonitor.reportEmergencyCallAction(true);
            }
            else {
                Log.w("EmergencyButton", "KeyguardUpdateMonitor was null, launching intent anyway.");
            }
            final TelecomManager telecommManager = this.getTelecommManager();
            if (telecommManager == null) {
                Log.wtf("EmergencyButton", "TelecomManager was null, cannot launch emergency dialer");
                return;
            }
            this.getContext().startActivityAsUser(telecommManager.createLaunchEmergencyDialerIntent((String)null).setFlags(343932928).putExtra("com.android.phone.EmergencyDialer.extra.ENTRY_TYPE", 1), ActivityOptions.makeCustomAnimation(this.getContext(), 0, 0).toBundle(), new UserHandle(KeyguardUpdateMonitor.getCurrentUser()));
        }
    }
    
    public interface EmergencyButtonCallback
    {
        void onEmergencyButtonClickedWhenInCall();
    }
}
