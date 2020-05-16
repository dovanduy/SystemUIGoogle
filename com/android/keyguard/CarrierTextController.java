// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.content.res.Resources;
import android.content.Intent;
import com.android.settingslib.WirelessUtils;
import java.util.Objects;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.telephony.ServiceState;
import java.util.function.Supplier;
import com.android.systemui.DejankUtils;
import android.telephony.SubscriptionInfo;
import java.util.List;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import com.android.systemui.R$string;
import android.text.TextUtils;
import com.android.systemui.Dependency;
import android.util.Log;
import android.net.wifi.WifiManager;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import android.telephony.PhoneStateListener;
import android.os.Handler;
import android.content.Context;

public class CarrierTextController
{
    private int mActiveMobileDataSubscription;
    protected final KeyguardUpdateMonitorCallback mCallback;
    private CarrierTextCallback mCarrierTextCallback;
    private Context mContext;
    private final boolean mIsEmergencyCallCapable;
    protected KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final Handler mMainHandler;
    private PhoneStateListener mPhoneStateListener;
    private CharSequence mSeparator;
    private boolean mShowAirplaneMode;
    private boolean mShowMissingSim;
    private boolean[] mSimErrorState;
    private final int mSimSlotsNumber;
    private boolean mTelephonyCapable;
    private WakefulnessLifecycle mWakefulnessLifecycle;
    private final WakefulnessLifecycle.Observer mWakefulnessObserver;
    private WifiManager mWifiManager;
    
    public CarrierTextController(final Context mContext, final CharSequence mSeparator, final boolean mShowAirplaneMode, final boolean mShowMissingSim) {
        this.mWakefulnessObserver = new WakefulnessLifecycle.Observer() {
            @Override
            public void onFinishedWakingUp() {
                if (CarrierTextController.this.mCarrierTextCallback != null) {
                    CarrierTextController.this.mCarrierTextCallback.finishedWakingUp();
                }
            }
            
            @Override
            public void onStartedGoingToSleep() {
                if (CarrierTextController.this.mCarrierTextCallback != null) {
                    CarrierTextController.this.mCarrierTextCallback.startedGoingToSleep();
                }
            }
        };
        this.mCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onRefreshCarrierInfo() {
                CarrierTextController.this.updateCarrierText();
            }
            
            @Override
            public void onSimStateChanged(final int n, final int i, final int n2) {
                if (i >= 0 && i < CarrierTextController.this.mSimSlotsNumber) {
                    if (CarrierTextController.this.getStatusForIccState(n2) == StatusMode.SimIoError) {
                        CarrierTextController.this.mSimErrorState[i] = true;
                        CarrierTextController.this.updateCarrierText();
                    }
                    else if (CarrierTextController.this.mSimErrorState[i]) {
                        CarrierTextController.this.mSimErrorState[i] = false;
                        CarrierTextController.this.updateCarrierText();
                    }
                    return;
                }
                final StringBuilder sb = new StringBuilder();
                sb.append("onSimStateChanged() - slotId invalid: ");
                sb.append(i);
                sb.append(" mTelephonyCapable: ");
                sb.append(Boolean.toString(CarrierTextController.this.mTelephonyCapable));
                Log.d("CarrierTextController", sb.toString());
            }
            
            @Override
            public void onTelephonyCapable(final boolean b) {
                CarrierTextController.this.mTelephonyCapable = b;
                CarrierTextController.this.updateCarrierText();
            }
        };
        this.mPhoneStateListener = new PhoneStateListener() {
            public void onActiveDataSubscriptionIdChanged(final int n) {
                CarrierTextController.this.mActiveMobileDataSubscription = n;
                final CarrierTextController this$0 = CarrierTextController.this;
                if (this$0.mKeyguardUpdateMonitor != null) {
                    this$0.updateCarrierText();
                }
            }
        };
        this.mContext = mContext;
        this.mIsEmergencyCallCapable = this.getTelephonyManager().isVoiceCapable();
        this.mShowAirplaneMode = mShowAirplaneMode;
        this.mShowMissingSim = mShowMissingSim;
        this.mWifiManager = (WifiManager)mContext.getSystemService("wifi");
        this.mSeparator = mSeparator;
        this.mWakefulnessLifecycle = Dependency.get(WakefulnessLifecycle.class);
        final int supportedModemCount = this.getTelephonyManager().getSupportedModemCount();
        this.mSimSlotsNumber = supportedModemCount;
        this.mSimErrorState = new boolean[supportedModemCount];
        this.mMainHandler = Dependency.get(Dependency.MAIN_HANDLER);
    }
    
    private static CharSequence concatenate(final CharSequence s, final CharSequence s2, final CharSequence s3) {
        final boolean b = TextUtils.isEmpty(s) ^ true;
        final boolean b2 = TextUtils.isEmpty(s2) ^ true;
        if (b && b2) {
            final StringBuilder sb = new StringBuilder();
            sb.append(s);
            sb.append(s3);
            sb.append(s2);
            return sb.toString();
        }
        if (b) {
            return s;
        }
        if (b2) {
            return s2;
        }
        return "";
    }
    
    private String getAirplaneModeMessage() {
        String string;
        if (this.mShowAirplaneMode) {
            string = this.getContext().getString(R$string.airplane_mode);
        }
        else {
            string = "";
        }
        return string;
    }
    
    private CharSequence getCarrierTextForSimState(final int n, final CharSequence charSequence) {
        final StatusMode statusForIccState = this.getStatusForIccState(n);
        CharSequence charSequence2 = charSequence;
        switch (CarrierTextController$4.$SwitchMap$com$android$keyguard$CarrierTextController$StatusMode[statusForIccState.ordinal()]) {
            default: {
                charSequence2 = null;
                return charSequence2;
            }
            case 1: {
                return charSequence2;
            }
            case 9: {
                charSequence2 = this.makeCarrierStringOnEmergencyCapable(this.getContext().getText(R$string.keyguard_sim_error_message_short), charSequence);
                return charSequence2;
            }
            case 8: {
                charSequence2 = this.makeCarrierStringOnLocked(this.getContext().getText(R$string.keyguard_sim_puk_locked_message), charSequence);
                return charSequence2;
            }
            case 7: {
                charSequence2 = this.makeCarrierStringOnLocked(this.getContext().getText(R$string.keyguard_sim_locked_message), charSequence);
                return charSequence2;
            }
            case 5: {
                charSequence2 = this.makeCarrierStringOnEmergencyCapable(this.getContext().getText(R$string.keyguard_permanent_disabled_sim_message_short), charSequence);
                return charSequence2;
            }
            case 3: {
                charSequence2 = this.makeCarrierStringOnEmergencyCapable(this.mContext.getText(R$string.keyguard_network_locked_message), charSequence);
                return charSequence2;
            }
            case 2: {
                charSequence2 = "";
                return charSequence2;
            }
        }
    }
    
    private Context getContext() {
        return this.mContext;
    }
    
    private String getMissingSimMessage() {
        String string;
        if (this.mShowMissingSim && this.mTelephonyCapable) {
            string = this.getContext().getString(R$string.keyguard_missing_sim_message_short);
        }
        else {
            string = "";
        }
        return string;
    }
    
    private StatusMode getStatusForIccState(int n) {
        final boolean deviceProvisioned = Dependency.get(KeyguardUpdateMonitor.class).isDeviceProvisioned();
        final boolean b = true;
        int n2 = 0;
        Label_0041: {
            if (!deviceProvisioned) {
                n2 = (b ? 1 : 0);
                if (n == 1) {
                    break Label_0041;
                }
                if (n == 7) {
                    n2 = (b ? 1 : 0);
                    break Label_0041;
                }
            }
            n2 = 0;
        }
        if (n2 != 0) {
            n = 4;
        }
        switch (n) {
            default: {
                return StatusMode.SimUnknown;
            }
            case 8: {
                return StatusMode.SimIoError;
            }
            case 7: {
                return StatusMode.SimPermDisabled;
            }
            case 6: {
                return StatusMode.SimNotReady;
            }
            case 5: {
                return StatusMode.Normal;
            }
            case 4: {
                return StatusMode.SimMissingLocked;
            }
            case 3: {
                return StatusMode.SimPukLocked;
            }
            case 2: {
                return StatusMode.SimLocked;
            }
            case 1: {
                return StatusMode.SimMissing;
            }
            case 0: {
                return StatusMode.SimUnknown;
            }
        }
    }
    
    private TelephonyManager getTelephonyManager() {
        return (TelephonyManager)this.mContext.getSystemService("phone");
    }
    
    private static CharSequence joinNotEmpty(final CharSequence s, final CharSequence[] array) {
        final int length = array.length;
        if (length == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            if (!TextUtils.isEmpty(array[i])) {
                if (!TextUtils.isEmpty((CharSequence)sb)) {
                    sb.append(s);
                }
                sb.append(array[i]);
            }
        }
        return sb.toString();
    }
    
    private CharSequence makeCarrierStringOnEmergencyCapable(final CharSequence charSequence, final CharSequence charSequence2) {
        if (this.mIsEmergencyCallCapable) {
            return concatenate(charSequence, charSequence2, this.mSeparator);
        }
        return charSequence;
    }
    
    private CharSequence makeCarrierStringOnLocked(final CharSequence charSequence, final CharSequence charSequence2) {
        final boolean b = TextUtils.isEmpty(charSequence) ^ true;
        final boolean b2 = TextUtils.isEmpty(charSequence2) ^ true;
        if (b && b2) {
            return this.mContext.getString(R$string.keyguard_carrier_name_with_sim_locked_template, new Object[] { charSequence2, charSequence });
        }
        if (b) {
            return charSequence;
        }
        if (b2) {
            return charSequence2;
        }
        return "";
    }
    
    private CharSequence updateCarrierTextWithSimIoError(CharSequence concatenate, final CharSequence[] array, final int[] array2, final boolean b) {
        final CharSequence carrierTextForSimState = this.getCarrierTextForSimState(8, "");
        for (int i = 0; i < this.getTelephonyManager().getActiveModemCount(); ++i) {
            if (this.mSimErrorState[i]) {
                if (b) {
                    return concatenate(carrierTextForSimState, this.getContext().getText(17040085), this.mSeparator);
                }
                if (array2[i] != -1) {
                    final int n = array2[i];
                    array[n] = concatenate(carrierTextForSimState, array[n], this.mSeparator);
                }
                else {
                    concatenate = concatenate(concatenate, carrierTextForSimState, this.mSeparator);
                }
            }
        }
        return concatenate;
    }
    
    protected List<SubscriptionInfo> getSubscriptionInfo() {
        return this.mKeyguardUpdateMonitor.getFilteredSubscriptionInfo(false);
    }
    
    protected void postToCallback(final CarrierTextCallbackInfo carrierTextCallbackInfo) {
        final CarrierTextCallback mCarrierTextCallback = this.mCarrierTextCallback;
        if (mCarrierTextCallback != null) {
            this.mMainHandler.post((Runnable)new _$$Lambda$CarrierTextController$c1krVbvH_4C_yBHdPvDOdVv9s78(mCarrierTextCallback, carrierTextCallbackInfo));
        }
    }
    
    public void setListening(final CarrierTextCallback mCarrierTextCallback) {
        final TelephonyManager telephonyManager = this.getTelephonyManager();
        if (mCarrierTextCallback != null) {
            this.mCarrierTextCallback = mCarrierTextCallback;
            if (DejankUtils.whitelistIpcs((Supplier<Boolean>)new _$$Lambda$CarrierTextController$ly4yO4Tyqr_OEuIqmIfvbcpoLyo(this))) {
                this.mKeyguardUpdateMonitor = Dependency.get(KeyguardUpdateMonitor.class);
                this.mMainHandler.post((Runnable)new _$$Lambda$CarrierTextController$phs0Q__5jRinz4Ru0JpWV_Xv7h4(this));
                this.mWakefulnessLifecycle.addObserver(this.mWakefulnessObserver);
                telephonyManager.listen(this.mPhoneStateListener, 4194304);
            }
            else {
                this.mKeyguardUpdateMonitor = null;
                mCarrierTextCallback.updateCarrierInfo(new CarrierTextCallbackInfo("", null, false, null));
            }
        }
        else {
            this.mCarrierTextCallback = null;
            if (this.mKeyguardUpdateMonitor != null) {
                this.mMainHandler.post((Runnable)new _$$Lambda$CarrierTextController$IJnJPOiuGfvkH1OTV6_1JKaFv4Q(this));
                this.mWakefulnessLifecycle.removeObserver(this.mWakefulnessObserver);
            }
            telephonyManager.listen(this.mPhoneStateListener, 0);
        }
    }
    
    protected void updateCarrierText() {
        final List<SubscriptionInfo> subscriptionInfo = this.getSubscriptionInfo();
        final int size = subscriptionInfo.size();
        final int[] array = new int[size];
        final int[] array2 = new int[this.mSimSlotsNumber];
        for (int i = 0; i < this.mSimSlotsNumber; ++i) {
            array2[i] = -1;
        }
        final CharSequence[] array3 = new CharSequence[size];
        int n2;
        int n = n2 = 0;
        boolean b = true;
        String s;
        while (true) {
            s = "";
            if (n >= size) {
                break;
            }
            final int subscriptionId = subscriptionInfo.get(n).getSubscriptionId();
            array3[n] = "";
            array[n] = subscriptionId;
            array2[subscriptionInfo.get(n).getSimSlotIndex()] = n;
            final int simState = this.mKeyguardUpdateMonitor.getSimState(subscriptionId);
            final CharSequence carrierTextForSimState = this.getCarrierTextForSimState(simState, subscriptionInfo.get(n).getCarrierName());
            if (carrierTextForSimState != null) {
                array3[n] = carrierTextForSimState;
                b = false;
            }
            int n3 = n2;
            Label_0279: {
                if (simState == 5) {
                    final ServiceState serviceState = this.mKeyguardUpdateMonitor.mServiceStates.get(subscriptionId);
                    n3 = n2;
                    if (serviceState != null) {
                        n3 = n2;
                        if (serviceState.getDataRegistrationState() == 0) {
                            if (serviceState.getRilDataRadioTechnology() == 18) {
                                n3 = n2;
                                if (!this.mWifiManager.isWifiEnabled()) {
                                    break Label_0279;
                                }
                                n3 = n2;
                                if (this.mWifiManager.getConnectionInfo() == null) {
                                    break Label_0279;
                                }
                                n3 = n2;
                                if (this.mWifiManager.getConnectionInfo().getBSSID() == null) {
                                    break Label_0279;
                                }
                            }
                            n3 = 1;
                        }
                    }
                }
            }
            ++n;
            n2 = n3;
        }
        CharSequence charSequence2;
        final CharSequence charSequence = charSequence2 = null;
        if (b) {
            charSequence2 = charSequence;
            if (n2 == 0) {
                if (size != 0) {
                    charSequence2 = this.makeCarrierStringOnEmergencyCapable(this.getMissingSimMessage(), subscriptionInfo.get(0).getCarrierName());
                }
                else {
                    CharSequence a = this.getContext().getText(17040085);
                    final Intent registerReceiver = this.getContext().registerReceiver((BroadcastReceiver)null, new IntentFilter("android.telephony.action.SERVICE_PROVIDERS_UPDATED"));
                    if (registerReceiver != null) {
                        String stringExtra;
                        if (registerReceiver.getBooleanExtra("android.telephony.extra.SHOW_SPN", false)) {
                            stringExtra = registerReceiver.getStringExtra("android.telephony.extra.SPN");
                        }
                        else {
                            stringExtra = "";
                        }
                        a = s;
                        if (registerReceiver.getBooleanExtra("android.telephony.extra.SHOW_PLMN", false)) {
                            a = registerReceiver.getStringExtra("android.telephony.extra.PLMN");
                        }
                        if (!Objects.equals(a, stringExtra)) {
                            a = concatenate(a, stringExtra, this.mSeparator);
                        }
                    }
                    charSequence2 = this.makeCarrierStringOnEmergencyCapable(this.getMissingSimMessage(), a);
                }
            }
        }
        CharSequence joinNotEmpty = charSequence2;
        if (TextUtils.isEmpty(charSequence2)) {
            joinNotEmpty = joinNotEmpty(this.mSeparator, array3);
        }
        CharSequence charSequence3 = this.updateCarrierTextWithSimIoError(joinNotEmpty, array3, array2, b);
        boolean b2;
        if (n2 == 0 && WirelessUtils.isAirplaneModeOn(this.mContext)) {
            charSequence3 = this.getAirplaneModeMessage();
            b2 = true;
        }
        else {
            b2 = false;
        }
        this.postToCallback(new CarrierTextCallbackInfo(charSequence3, array3, true ^ b, array, b2));
    }
    
    public static class Builder
    {
        private final Context mContext;
        private final String mSeparator;
        private boolean mShowAirplaneMode;
        private boolean mShowMissingSim;
        
        public Builder(final Context mContext, final Resources resources) {
            this.mContext = mContext;
            this.mSeparator = resources.getString(17040408);
        }
        
        public CarrierTextController build() {
            return new CarrierTextController(this.mContext, this.mSeparator, this.mShowAirplaneMode, this.mShowMissingSim);
        }
        
        public Builder setShowAirplaneMode(final boolean mShowAirplaneMode) {
            this.mShowAirplaneMode = mShowAirplaneMode;
            return this;
        }
        
        public Builder setShowMissingSim(final boolean mShowMissingSim) {
            this.mShowMissingSim = mShowMissingSim;
            return this;
        }
    }
    
    public interface CarrierTextCallback
    {
        default void finishedWakingUp() {
        }
        
        default void startedGoingToSleep() {
        }
        
        default void updateCarrierInfo(final CarrierTextCallbackInfo carrierTextCallbackInfo) {
        }
    }
    
    public static final class CarrierTextCallbackInfo
    {
        public boolean airplaneMode;
        public final boolean anySimReady;
        public final CharSequence carrierText;
        public final CharSequence[] listOfCarriers;
        public final int[] subscriptionIds;
        
        public CarrierTextCallbackInfo(final CharSequence charSequence, final CharSequence[] array, final boolean b, final int[] array2) {
            this(charSequence, array, b, array2, false);
        }
        
        public CarrierTextCallbackInfo(final CharSequence carrierText, final CharSequence[] listOfCarriers, final boolean anySimReady, final int[] subscriptionIds, final boolean airplaneMode) {
            this.carrierText = carrierText;
            this.listOfCarriers = listOfCarriers;
            this.anySimReady = anySimReady;
            this.subscriptionIds = subscriptionIds;
            this.airplaneMode = airplaneMode;
        }
    }
    
    private enum StatusMode
    {
        NetworkLocked, 
        Normal, 
        SimIoError, 
        SimLocked, 
        SimMissing, 
        SimMissingLocked, 
        SimNotReady, 
        SimPermDisabled, 
        SimPukLocked, 
        SimUnknown;
    }
}
