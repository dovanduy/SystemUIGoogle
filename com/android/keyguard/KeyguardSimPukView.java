// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.view.View;
import com.android.systemui.R$id;
import android.telephony.SubscriptionInfo;
import android.content.res.TypedArray;
import android.content.res.Resources;
import android.util.Log;
import android.telephony.PinResult;
import android.content.res.ColorStateList;
import com.android.systemui.R$attr;
import android.telephony.TelephonyManager;
import android.telephony.SubscriptionManager;
import com.android.systemui.Dependency;
import android.app.Activity;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import com.android.systemui.R$plurals;
import com.android.systemui.R$string;
import android.app.Dialog;
import android.util.AttributeSet;
import android.content.Context;
import android.app.ProgressDialog;
import android.widget.ImageView;
import android.app.AlertDialog;

public class KeyguardSimPukView extends KeyguardPinBasedInputView
{
    private CheckSimPuk mCheckSimPukThread;
    private String mPinText;
    private String mPukText;
    private int mRemainingAttempts;
    private AlertDialog mRemainingAttemptsDialog;
    private boolean mShowDefaultMessage;
    private ImageView mSimImageView;
    private ProgressDialog mSimUnlockProgressDialog;
    private StateMachine mStateMachine;
    private int mSubId;
    KeyguardUpdateMonitorCallback mUpdateMonitorCallback;
    
    public KeyguardSimPukView(final Context context) {
        this(context, null);
    }
    
    public KeyguardSimPukView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mSimUnlockProgressDialog = null;
        this.mShowDefaultMessage = true;
        this.mRemainingAttempts = -1;
        this.mStateMachine = new StateMachine();
        this.mSubId = -1;
        this.mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onSimStateChanged(final int n, final int n2, final int n3) {
                if (n3 != 5) {
                    KeyguardSimPukView.this.resetState();
                }
                else {
                    KeyguardSimPukView.this.mRemainingAttempts = -1;
                    KeyguardSimPukView.this.mShowDefaultMessage = true;
                    final KeyguardSecurityCallback mCallback = KeyguardSimPukView.this.mCallback;
                    if (mCallback != null) {
                        mCallback.dismiss(true, KeyguardUpdateMonitor.getCurrentUser());
                    }
                }
            }
        };
    }
    
    private boolean checkPin() {
        final int length = super.mPasswordEntry.getText().length();
        if (length >= 4 && length <= 8) {
            this.mPinText = super.mPasswordEntry.getText();
            return true;
        }
        return false;
    }
    
    private boolean checkPuk() {
        if (super.mPasswordEntry.getText().length() == 8) {
            this.mPukText = super.mPasswordEntry.getText();
            return true;
        }
        return false;
    }
    
    private String getPukPasswordErrorMessage(int i, final boolean b) {
        String s;
        if (i == 0) {
            s = this.getContext().getString(R$string.kg_password_wrong_puk_code_dead);
        }
        else if (i > 0) {
            int n;
            if (b) {
                n = R$plurals.kg_password_default_puk_message;
            }
            else {
                n = R$plurals.kg_password_wrong_puk_code;
            }
            s = this.getContext().getResources().getQuantityString(n, i, new Object[] { i });
        }
        else {
            if (b) {
                i = R$string.kg_puk_enter_puk_hint;
            }
            else {
                i = R$string.kg_password_puk_failed;
            }
            s = this.getContext().getString(i);
        }
        String string = s;
        if (KeyguardEsimArea.isEsimLocked(super.mContext, this.mSubId)) {
            string = this.getResources().getString(R$string.kg_sim_lock_esim_instructions, new Object[] { s });
        }
        return string;
    }
    
    private Dialog getPukRemainingAttemptsDialog(final int n) {
        final String pukPasswordErrorMessage = this.getPukPasswordErrorMessage(n, false);
        final AlertDialog mRemainingAttemptsDialog = this.mRemainingAttemptsDialog;
        if (mRemainingAttemptsDialog == null) {
            final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(super.mContext);
            alertDialog$Builder.setMessage((CharSequence)pukPasswordErrorMessage);
            alertDialog$Builder.setCancelable(false);
            alertDialog$Builder.setNeutralButton(R$string.ok, (DialogInterface$OnClickListener)null);
            final AlertDialog create = alertDialog$Builder.create();
            this.mRemainingAttemptsDialog = create;
            create.getWindow().setType(2009);
        }
        else {
            mRemainingAttemptsDialog.setMessage((CharSequence)pukPasswordErrorMessage);
        }
        return (Dialog)this.mRemainingAttemptsDialog;
    }
    
    private Dialog getSimUnlockProgressDialog() {
        if (this.mSimUnlockProgressDialog == null) {
            (this.mSimUnlockProgressDialog = new ProgressDialog(super.mContext)).setMessage((CharSequence)super.mContext.getString(R$string.kg_sim_unlock_progress_dialog_message));
            this.mSimUnlockProgressDialog.setIndeterminate(true);
            this.mSimUnlockProgressDialog.setCancelable(false);
            if (!(super.mContext instanceof Activity)) {
                this.mSimUnlockProgressDialog.getWindow().setType(2009);
            }
        }
        return (Dialog)this.mSimUnlockProgressDialog;
    }
    
    private void handleSubInfoChangeIfNeeded() {
        final int nextSubIdForState = Dependency.get(KeyguardUpdateMonitor.class).getNextSubIdForState(3);
        if (nextSubIdForState != this.mSubId && SubscriptionManager.isValidSubscriptionId(nextSubIdForState)) {
            this.mSubId = nextSubIdForState;
            this.mShowDefaultMessage = true;
            this.mRemainingAttempts = -1;
        }
    }
    
    private void showDefaultMessage() {
        final int mRemainingAttempts = this.mRemainingAttempts;
        if (mRemainingAttempts >= 0) {
            super.mSecurityMessageDisplay.setMessage(this.getPukPasswordErrorMessage(mRemainingAttempts, true));
            return;
        }
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
            s = resources.getString(R$string.kg_puk_enter_puk_hint);
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
            s = resources.getString(R$string.kg_puk_enter_puk_hint_multi, new Object[] { displayName });
            iconTint = color;
            if (subscriptionInfoForSubId != null) {
                iconTint = subscriptionInfoForSubId.getIconTint();
            }
        }
        String string = s;
        if (esimLocked) {
            string = resources.getString(R$string.kg_sim_lock_esim_instructions, new Object[] { s });
        }
        final SecurityMessageDisplay mSecurityMessageDisplay = super.mSecurityMessageDisplay;
        if (mSecurityMessageDisplay != null) {
            mSecurityMessageDisplay.setMessage(string);
        }
        this.mSimImageView.setImageTintList(ColorStateList.valueOf(iconTint));
        new CheckSimPuk("", "", this.mSubId) {
            @Override
            void onSimLockChangedResponse(final PinResult pinResult) {
                if (pinResult == null) {
                    Log.e("KeyguardSimPukView", "onSimCheckResponse, pin result is NULL");
                }
                else {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("onSimCheckResponse  dummy One result ");
                    sb.append(pinResult.toString());
                    Log.d("KeyguardSimPukView", sb.toString());
                    if (pinResult.getAttemptsRemaining() >= 0) {
                        KeyguardSimPukView.this.mRemainingAttempts = pinResult.getAttemptsRemaining();
                        final KeyguardSimPukView this$0 = KeyguardSimPukView.this;
                        this$0.mSecurityMessageDisplay.setMessage(this$0.getPukPasswordErrorMessage(pinResult.getAttemptsRemaining(), true));
                    }
                }
            }
        }.start();
    }
    
    private void updateSim() {
        this.getSimUnlockProgressDialog().show();
        if (this.mCheckSimPukThread == null) {
            (this.mCheckSimPukThread = (CheckSimPuk)new CheckSimPuk(this.mPukText, this.mPinText, this.mSubId) {
                @Override
                void onSimLockChangedResponse(final PinResult pinResult) {
                    KeyguardSimPukView.this.post((Runnable)new Runnable() {
                        @Override
                        public void run() {
                            if (KeyguardSimPukView.this.mSimUnlockProgressDialog != null) {
                                KeyguardSimPukView.this.mSimUnlockProgressDialog.hide();
                            }
                            KeyguardSimPukView.this.resetPasswordText(true, pinResult.getType() != 0);
                            if (pinResult.getType() == 0) {
                                Dependency.get(KeyguardUpdateMonitor.class).reportSimUnlocked(KeyguardSimPukView.this.mSubId);
                                KeyguardSimPukView.this.mRemainingAttempts = -1;
                                KeyguardSimPukView.this.mShowDefaultMessage = true;
                                final KeyguardSecurityCallback mCallback = KeyguardSimPukView.this.mCallback;
                                if (mCallback != null) {
                                    mCallback.dismiss(true, KeyguardUpdateMonitor.getCurrentUser());
                                }
                            }
                            else {
                                KeyguardSimPukView.this.mShowDefaultMessage = false;
                                if (pinResult.getType() == 1) {
                                    final KeyguardSimPukView this$0 = KeyguardSimPukView.this;
                                    this$0.mSecurityMessageDisplay.setMessage(this$0.getPukPasswordErrorMessage(pinResult.getAttemptsRemaining(), false));
                                    if (pinResult.getAttemptsRemaining() <= 2) {
                                        KeyguardSimPukView.this.getPukRemainingAttemptsDialog(pinResult.getAttemptsRemaining()).show();
                                    }
                                    else {
                                        final KeyguardSimPukView this$2 = KeyguardSimPukView.this;
                                        this$2.mSecurityMessageDisplay.setMessage(this$2.getPukPasswordErrorMessage(pinResult.getAttemptsRemaining(), false));
                                    }
                                }
                                else {
                                    final KeyguardSimPukView this$3 = KeyguardSimPukView.this;
                                    this$3.mSecurityMessageDisplay.setMessage(this$3.getContext().getString(R$string.kg_password_puk_failed));
                                }
                                KeyguardSimPukView.this.mStateMachine.reset();
                            }
                            KeyguardSimPukView.this.mCheckSimPukThread = null;
                        }
                    });
                }
            }).start();
        }
    }
    
    public boolean confirmPin() {
        return this.mPinText.equals(super.mPasswordEntry.getText());
    }
    
    @Override
    protected int getPasswordTextViewId() {
        return R$id.pukEntry;
    }
    
    @Override
    protected int getPromptReasonStringRes(final int n) {
        return 0;
    }
    
    @Override
    public CharSequence getTitle() {
        return this.getContext().getString(17040360);
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Dependency.get(KeyguardUpdateMonitor.class).registerCallback(this.mUpdateMonitorCallback);
        this.resetState();
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Dependency.get(KeyguardUpdateMonitor.class).removeCallback(this.mUpdateMonitorCallback);
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
    }
    
    public void resetState() {
        super.resetState();
        this.mStateMachine.reset();
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
        this.mStateMachine.next();
    }
    
    private abstract class CheckSimPuk extends Thread
    {
        private final String mPin;
        private final String mPuk;
        private final int mSubId;
        
        protected CheckSimPuk(final String mPuk, final String mPin, final int mSubId) {
            this.mPuk = mPuk;
            this.mPin = mPin;
            this.mSubId = mSubId;
        }
        
        abstract void onSimLockChangedResponse(final PinResult p0);
        
        @Override
        public void run() {
            final PinResult supplyPukReportPinResult = ((TelephonyManager)KeyguardSimPukView.this.mContext.getSystemService("phone")).createForSubscriptionId(this.mSubId).supplyPukReportPinResult(this.mPuk, this.mPin);
            if (supplyPukReportPinResult == null) {
                Log.e("KeyguardSimPukView", "Error result for supplyPukReportResult.");
                KeyguardSimPukView.this.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        CheckSimPuk.this.onSimLockChangedResponse(PinResult.getDefaultFailedResult());
                    }
                });
            }
            else {
                KeyguardSimPukView.this.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        CheckSimPuk.this.onSimLockChangedResponse(supplyPukReportPinResult);
                    }
                });
            }
        }
    }
    
    private class StateMachine
    {
        private int state;
        
        private StateMachine() {
            this.state = 0;
        }
        
        public void next() {
            final int state = this.state;
            int message;
            if (state == 0) {
                if (KeyguardSimPukView.this.checkPuk()) {
                    this.state = 1;
                    message = R$string.kg_puk_enter_pin_hint;
                }
                else {
                    message = R$string.kg_invalid_sim_puk_hint;
                }
            }
            else if (state == 1) {
                if (KeyguardSimPukView.this.checkPin()) {
                    this.state = 2;
                    message = R$string.kg_enter_confirm_pin_hint;
                }
                else {
                    message = R$string.kg_invalid_sim_pin_hint;
                }
            }
            else if (state == 2) {
                if (KeyguardSimPukView.this.confirmPin()) {
                    this.state = 3;
                    message = R$string.keyguard_sim_unlock_progress_dialog_message;
                    KeyguardSimPukView.this.updateSim();
                }
                else {
                    this.state = 1;
                    message = R$string.kg_invalid_confirm_pin_hint;
                }
            }
            else {
                message = 0;
            }
            KeyguardSimPukView.this.resetPasswordText(true, true);
            if (message != 0) {
                KeyguardSimPukView.this.mSecurityMessageDisplay.setMessage(message);
            }
        }
        
        void reset() {
            KeyguardSimPukView.this.mPinText = "";
            KeyguardSimPukView.this.mPukText = "";
            int visibility = 0;
            this.state = 0;
            KeyguardSimPukView.this.handleSubInfoChangeIfNeeded();
            if (KeyguardSimPukView.this.mShowDefaultMessage) {
                KeyguardSimPukView.this.showDefaultMessage();
            }
            final boolean esimLocked = KeyguardEsimArea.isEsimLocked(KeyguardSimPukView.this.mContext, KeyguardSimPukView.this.mSubId);
            final KeyguardEsimArea keyguardEsimArea = (KeyguardEsimArea)KeyguardSimPukView.this.findViewById(R$id.keyguard_esim_area);
            if (!esimLocked) {
                visibility = 8;
            }
            keyguardEsimArea.setVisibility(visibility);
            KeyguardSimPukView.this.mPasswordEntry.requestFocus();
        }
    }
}
