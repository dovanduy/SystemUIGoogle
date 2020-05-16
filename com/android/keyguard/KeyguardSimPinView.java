// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.view.View;
import android.content.res.Configuration;
import com.android.systemui.R$id;
import android.telephony.PinResult;
import android.telephony.SubscriptionInfo;
import android.content.res.TypedArray;
import android.content.res.Resources;
import android.content.res.ColorStateList;
import com.android.systemui.R$attr;
import android.telephony.TelephonyManager;
import android.telephony.SubscriptionManager;
import com.android.systemui.Dependency;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import com.android.systemui.R$plurals;
import com.android.systemui.R$string;
import android.app.Dialog;
import android.util.Log;
import android.util.AttributeSet;
import android.content.Context;
import android.app.ProgressDialog;
import android.widget.ImageView;
import android.app.AlertDialog;

public class KeyguardSimPinView extends KeyguardPinBasedInputView
{
    private CheckSimPin mCheckSimPinThread;
    private int mRemainingAttempts;
    private AlertDialog mRemainingAttemptsDialog;
    private boolean mShowDefaultMessage;
    private ImageView mSimImageView;
    private ProgressDialog mSimUnlockProgressDialog;
    private int mSubId;
    KeyguardUpdateMonitorCallback mUpdateMonitorCallback;
    
    public KeyguardSimPinView(final Context context) {
        this(context, null);
    }
    
    public KeyguardSimPinView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mSimUnlockProgressDialog = null;
        this.mShowDefaultMessage = true;
        this.mRemainingAttempts = -1;
        this.mSubId = -1;
        this.mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onSimStateChanged(final int i, final int n, final int j) {
                final StringBuilder sb = new StringBuilder();
                sb.append("onSimStateChanged(subId=");
                sb.append(i);
                sb.append(",state=");
                sb.append(j);
                sb.append(")");
                Log.v("KeyguardSimPinView", sb.toString());
                if (j != 5) {
                    KeyguardSimPinView.this.resetState();
                }
                else {
                    KeyguardSimPinView.this.mRemainingAttempts = -1;
                    KeyguardSimPinView.this.resetState();
                }
            }
        };
    }
    
    private String getPinPasswordErrorMessage(final int n, final boolean b) {
        String s;
        if (n == 0) {
            s = this.getContext().getString(R$string.kg_password_wrong_pin_code_pukked);
        }
        else if (n > 0) {
            int n2;
            if (b) {
                n2 = R$plurals.kg_password_default_pin_message;
            }
            else {
                n2 = R$plurals.kg_password_wrong_pin_code;
            }
            s = this.getContext().getResources().getQuantityString(n2, n, new Object[] { n });
        }
        else {
            int n3;
            if (b) {
                n3 = R$string.kg_sim_pin_instructions;
            }
            else {
                n3 = R$string.kg_password_pin_failed;
            }
            s = this.getContext().getString(n3);
        }
        String string = s;
        if (KeyguardEsimArea.isEsimLocked(super.mContext, this.mSubId)) {
            string = this.getResources().getString(R$string.kg_sim_lock_esim_instructions, new Object[] { s });
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("getPinPasswordErrorMessage: attemptsRemaining=");
        sb.append(n);
        sb.append(" displayMessage=");
        sb.append(string);
        Log.d("KeyguardSimPinView", sb.toString());
        return string;
    }
    
    private Dialog getSimRemainingAttemptsDialog(final int n) {
        final String pinPasswordErrorMessage = this.getPinPasswordErrorMessage(n, false);
        final AlertDialog mRemainingAttemptsDialog = this.mRemainingAttemptsDialog;
        if (mRemainingAttemptsDialog == null) {
            final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(super.mContext);
            alertDialog$Builder.setMessage((CharSequence)pinPasswordErrorMessage);
            alertDialog$Builder.setCancelable(false);
            alertDialog$Builder.setNeutralButton(R$string.ok, (DialogInterface$OnClickListener)null);
            final AlertDialog create = alertDialog$Builder.create();
            this.mRemainingAttemptsDialog = create;
            create.getWindow().setType(2009);
        }
        else {
            mRemainingAttemptsDialog.setMessage((CharSequence)pinPasswordErrorMessage);
        }
        return (Dialog)this.mRemainingAttemptsDialog;
    }
    
    private Dialog getSimUnlockProgressDialog() {
        if (this.mSimUnlockProgressDialog == null) {
            (this.mSimUnlockProgressDialog = new ProgressDialog(super.mContext)).setMessage((CharSequence)super.mContext.getString(R$string.kg_sim_unlock_progress_dialog_message));
            this.mSimUnlockProgressDialog.setIndeterminate(true);
            this.mSimUnlockProgressDialog.setCancelable(false);
            this.mSimUnlockProgressDialog.getWindow().setType(2009);
        }
        return (Dialog)this.mSimUnlockProgressDialog;
    }
    
    private void handleSubInfoChangeIfNeeded() {
        final int nextSubIdForState = Dependency.get(KeyguardUpdateMonitor.class).getNextSubIdForState(2);
        if (nextSubIdForState != this.mSubId && SubscriptionManager.isValidSubscriptionId(nextSubIdForState)) {
            this.mSubId = nextSubIdForState;
            this.mShowDefaultMessage = true;
            this.mRemainingAttempts = -1;
        }
    }
    
    private void setLockedSimMessage() {
        final boolean esimLocked = KeyguardEsimArea.isEsimLocked(super.mContext, this.mSubId);
        final TelephonyManager telephonyManager = (TelephonyManager)super.mContext.getSystemService("phone");
        int activeModemCount;
        if (telephonyManager != null) {
            activeModemCount = telephonyManager.getActiveModemCount();
        }
        else {
            activeModemCount = 1;
        }
        final Resources resources = this.getResources();
        final TypedArray obtainStyledAttributes = super.mContext.obtainStyledAttributes(new int[] { R$attr.wallpaperTextColor });
        final int color = obtainStyledAttributes.getColor(0, -1);
        obtainStyledAttributes.recycle();
        String s;
        int iconTint;
        if (activeModemCount < 2) {
            s = resources.getString(R$string.kg_sim_pin_instructions);
            iconTint = color;
        }
        else {
            final SubscriptionInfo subscriptionInfoForSubId = Dependency.get(KeyguardUpdateMonitor.class).getSubscriptionInfoForSubId(this.mSubId);
            CharSequence displayName;
            if (subscriptionInfoForSubId != null) {
                displayName = subscriptionInfoForSubId.getDisplayName();
            }
            else {
                displayName = "";
            }
            s = resources.getString(R$string.kg_sim_pin_instructions_multi, new Object[] { displayName });
            iconTint = color;
            if (subscriptionInfoForSubId != null) {
                iconTint = subscriptionInfoForSubId.getIconTint();
            }
        }
        String string = s;
        if (esimLocked) {
            string = resources.getString(R$string.kg_sim_lock_esim_instructions, new Object[] { s });
        }
        if (super.mSecurityMessageDisplay != null && this.getVisibility() == 0) {
            super.mSecurityMessageDisplay.setMessage(string);
        }
        this.mSimImageView.setImageTintList(ColorStateList.valueOf(iconTint));
    }
    
    private void showDefaultMessage() {
        this.setLockedSimMessage();
        if (this.mRemainingAttempts >= 0) {
            return;
        }
        new CheckSimPin("", this.mSubId) {
            @Override
            void onSimCheckResponse(final PinResult pinResult) {
                final StringBuilder sb = new StringBuilder();
                sb.append("onSimCheckResponse  dummy One result ");
                sb.append(pinResult.toString());
                Log.d("KeyguardSimPinView", sb.toString());
                if (pinResult.getAttemptsRemaining() >= 0) {
                    KeyguardSimPinView.this.mRemainingAttempts = pinResult.getAttemptsRemaining();
                    KeyguardSimPinView.this.setLockedSimMessage();
                }
            }
        }.start();
    }
    
    @Override
    protected int getPasswordTextViewId() {
        return R$id.simPinEntry;
    }
    
    @Override
    protected int getPromptReasonStringRes(final int n) {
        return 0;
    }
    
    @Override
    public CharSequence getTitle() {
        return this.getContext().getString(17040359);
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.resetState();
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final View mEcaView = super.mEcaView;
        if (mEcaView instanceof EmergencyCarrierArea) {
            ((EmergencyCarrierArea)mEcaView).setCarrierTextVisible(true);
        }
        this.mSimImageView = (ImageView)this.findViewById(R$id.keyguard_sim);
    }
    
    @Override
    public void onPause() {
        final ProgressDialog mSimUnlockProgressDialog = this.mSimUnlockProgressDialog;
        if (mSimUnlockProgressDialog != null) {
            mSimUnlockProgressDialog.dismiss();
            this.mSimUnlockProgressDialog = null;
        }
        Dependency.get(KeyguardUpdateMonitor.class).removeCallback(this.mUpdateMonitorCallback);
    }
    
    @Override
    public void onResume(final int n) {
        super.onResume(n);
        Dependency.get(KeyguardUpdateMonitor.class).registerCallback(this.mUpdateMonitorCallback);
        this.resetState();
    }
    
    public void resetState() {
        super.resetState();
        Log.v("KeyguardSimPinView", "Resetting state");
        this.handleSubInfoChangeIfNeeded();
        if (this.mShowDefaultMessage) {
            this.showDefaultMessage();
        }
        final boolean esimLocked = KeyguardEsimArea.isEsimLocked(super.mContext, this.mSubId);
        final KeyguardEsimArea keyguardEsimArea = (KeyguardEsimArea)this.findViewById(R$id.keyguard_esim_area);
        int visibility;
        if (esimLocked) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        keyguardEsimArea.setVisibility(visibility);
    }
    
    @Override
    protected boolean shouldLockout(final long n) {
        return false;
    }
    
    public void startAppearAnimation() {
    }
    
    @Override
    public boolean startDisappearAnimation(final Runnable runnable) {
        return false;
    }
    
    @Override
    protected void verifyPasswordAndUnlock() {
        if (super.mPasswordEntry.getText().length() < 4) {
            super.mSecurityMessageDisplay.setMessage(R$string.kg_invalid_sim_pin_hint);
            this.resetPasswordText(true, true);
            super.mCallback.userActivity();
            return;
        }
        this.getSimUnlockProgressDialog().show();
        if (this.mCheckSimPinThread == null) {
            (this.mCheckSimPinThread = (CheckSimPin)new CheckSimPin(super.mPasswordEntry.getText(), this.mSubId) {
                @Override
                void onSimCheckResponse(final PinResult pinResult) {
                    KeyguardSimPinView.this.post((Runnable)new Runnable() {
                        @Override
                        public void run() {
                            KeyguardSimPinView.this.mRemainingAttempts = pinResult.getAttemptsRemaining();
                            if (KeyguardSimPinView.this.mSimUnlockProgressDialog != null) {
                                KeyguardSimPinView.this.mSimUnlockProgressDialog.hide();
                            }
                            KeyguardSimPinView.this.resetPasswordText(true, pinResult.getType() != 0);
                            if (pinResult.getType() == 0) {
                                Dependency.get(KeyguardUpdateMonitor.class).reportSimUnlocked(KeyguardSimPinView.this.mSubId);
                                KeyguardSimPinView.this.mRemainingAttempts = -1;
                                KeyguardSimPinView.this.mShowDefaultMessage = true;
                                final KeyguardSecurityCallback mCallback = KeyguardSimPinView.this.mCallback;
                                if (mCallback != null) {
                                    mCallback.dismiss(true, KeyguardUpdateMonitor.getCurrentUser());
                                }
                            }
                            else {
                                KeyguardSimPinView.this.mShowDefaultMessage = false;
                                if (pinResult.getType() == 1) {
                                    if (pinResult.getAttemptsRemaining() <= 2) {
                                        KeyguardSimPinView.this.getSimRemainingAttemptsDialog(pinResult.getAttemptsRemaining()).show();
                                    }
                                    else {
                                        final KeyguardSimPinView this$0 = KeyguardSimPinView.this;
                                        this$0.mSecurityMessageDisplay.setMessage(this$0.getPinPasswordErrorMessage(pinResult.getAttemptsRemaining(), false));
                                    }
                                }
                                else {
                                    final KeyguardSimPinView this$2 = KeyguardSimPinView.this;
                                    this$2.mSecurityMessageDisplay.setMessage(this$2.getContext().getString(R$string.kg_password_pin_failed));
                                }
                                final StringBuilder sb = new StringBuilder();
                                sb.append("verifyPasswordAndUnlock  CheckSimPin.onSimCheckResponse: ");
                                sb.append(pinResult);
                                sb.append(" attemptsRemaining=");
                                sb.append(pinResult.getAttemptsRemaining());
                                Log.d("KeyguardSimPinView", sb.toString());
                            }
                            KeyguardSimPinView.this.mCallback.userActivity();
                            KeyguardSimPinView.this.mCheckSimPinThread = null;
                        }
                    });
                }
            }).start();
        }
    }
    
    private abstract class CheckSimPin extends Thread
    {
        private final String mPin;
        private int mSubId;
        
        protected CheckSimPin(final String mPin, final int mSubId) {
            this.mPin = mPin;
            this.mSubId = mSubId;
        }
        
        abstract void onSimCheckResponse(final PinResult p0);
        
        @Override
        public void run() {
            final StringBuilder sb = new StringBuilder();
            sb.append("call supplyPinReportResultForSubscriber(subid=");
            sb.append(this.mSubId);
            sb.append(")");
            Log.v("KeyguardSimPinView", sb.toString());
            final PinResult supplyPinReportPinResult = ((TelephonyManager)KeyguardSimPinView.this.mContext.getSystemService("phone")).createForSubscriptionId(this.mSubId).supplyPinReportPinResult(this.mPin);
            if (supplyPinReportPinResult == null) {
                Log.e("KeyguardSimPinView", "Error result for supplyPinReportResult.");
                KeyguardSimPinView.this.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        CheckSimPin.this.onSimCheckResponse(PinResult.getDefaultFailedResult());
                    }
                });
            }
            else {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("supplyPinReportResult returned: ");
                sb2.append(supplyPinReportPinResult.toString());
                Log.v("KeyguardSimPinView", sb2.toString());
                KeyguardSimPinView.this.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        CheckSimPin.this.onSimCheckResponse(supplyPinReportPinResult);
                    }
                });
            }
        }
    }
}
