// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import java.util.Objects;
import java.util.BitSet;
import android.content.ContentResolver;
import android.provider.Settings$Global;
import android.text.Html;
import android.content.Intent;
import com.android.settingslib.graph.SignalDrawable;
import java.io.PrintWriter;
import android.text.TextUtils;
import android.util.Log;
import com.android.settingslib.Utils;
import com.android.settingslib.net.SignalStrengthUtil;
import android.telephony.SubscriptionManager;
import android.telephony.CellSignalStrength;
import java.util.List;
import android.telephony.CellSignalStrengthCdma;
import com.android.systemui.R$string;
import java.util.concurrent.Executor;
import android.os.Handler;
import java.util.HashMap;
import android.os.Looper;
import android.content.Context;
import android.telephony.TelephonyDisplayInfo;
import android.telephony.SubscriptionInfo;
import android.telephony.SignalStrength;
import android.telephony.ServiceState;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.database.ContentObserver;
import java.util.Map;
import com.android.internal.annotations.VisibleForTesting;

public class MobileSignalController extends SignalController<MobileState, MobileIconGroup>
{
    private NetworkControllerImpl.Config mConfig;
    private int mDataState;
    private MobileIconGroup mDefaultIcons;
    private final NetworkControllerImpl.SubscriptionDefaults mDefaults;
    @VisibleForTesting
    boolean mInflateSignalStrengths;
    private final String mNetworkNameDefault;
    private final String mNetworkNameSeparator;
    final Map<String, MobileIconGroup> mNetworkToIconLookup;
    private final ContentObserver mObserver;
    private final TelephonyManager mPhone;
    @VisibleForTesting
    final PhoneStateListener mPhoneStateListener;
    private ServiceState mServiceState;
    private SignalStrength mSignalStrength;
    final SubscriptionInfo mSubscriptionInfo;
    private TelephonyDisplayInfo mTelephonyDisplayInfo;
    
    public MobileSignalController(final Context context, final NetworkControllerImpl.Config mConfig, final boolean b, final TelephonyManager mPhone, final CallbackHandler callbackHandler, final NetworkControllerImpl networkControllerImpl, final SubscriptionInfo mSubscriptionInfo, final NetworkControllerImpl.SubscriptionDefaults mDefaults, final Looper looper) {
        final StringBuilder sb = new StringBuilder();
        sb.append("MobileSignalController(");
        sb.append(mSubscriptionInfo.getSubscriptionId());
        sb.append(")");
        super(sb.toString(), context, 0, callbackHandler, networkControllerImpl);
        this.mDataState = 0;
        this.mTelephonyDisplayInfo = new TelephonyDisplayInfo(0, 0);
        this.mInflateSignalStrengths = false;
        this.mNetworkToIconLookup = new HashMap<String, MobileIconGroup>();
        this.mConfig = mConfig;
        this.mPhone = mPhone;
        this.mDefaults = mDefaults;
        this.mSubscriptionInfo = mSubscriptionInfo;
        this.mPhoneStateListener = new MobilePhoneStateListener(new _$$Lambda$LfzJt661qZfn2w_6SYHFbD3aMy0(new Handler(looper)));
        this.mNetworkNameSeparator = this.getTextIfExists(R$string.status_bar_network_name_separator).toString();
        this.mNetworkNameDefault = this.getTextIfExists(17040441).toString();
        this.mapIconSets();
        String s;
        if (mSubscriptionInfo.getCarrierName() != null) {
            s = mSubscriptionInfo.getCarrierName().toString();
        }
        else {
            s = this.mNetworkNameDefault;
        }
        final State mLastState = super.mLastState;
        final MobileState mobileState = (MobileState)mLastState;
        final State mCurrentState = super.mCurrentState;
        ((MobileState)mCurrentState).networkName = s;
        mobileState.networkName = s;
        final MobileState mobileState2 = (MobileState)mLastState;
        ((MobileState)mCurrentState).networkNameData = s;
        mobileState2.networkNameData = s;
        final MobileState mobileState3 = (MobileState)mLastState;
        ((MobileState)mCurrentState).enabled = b;
        mobileState3.enabled = b;
        final MobileState mobileState4 = (MobileState)mLastState;
        final MobileState mobileState5 = (MobileState)mCurrentState;
        final MobileIconGroup mDefaultIcons = this.mDefaultIcons;
        mobileState5.iconGroup = mDefaultIcons;
        mobileState4.iconGroup = mDefaultIcons;
        this.updateDataSim();
        this.mObserver = new ContentObserver(new Handler(looper)) {
            public void onChange(final boolean b) {
                MobileSignalController.this.updateTelephony();
            }
        };
    }
    
    private void checkDefaultData() {
        final State mCurrentState = super.mCurrentState;
        if (((MobileState)mCurrentState).iconGroup != TelephonyIcons.NOT_DEFAULT_DATA) {
            ((MobileState)mCurrentState).defaultDataOff = false;
            return;
        }
        ((MobileState)mCurrentState).defaultDataOff = super.mNetworkController.isDataControllerDisabled();
    }
    
    private final int getCdmaLevel() {
        final List cellSignalStrengths = this.mSignalStrength.getCellSignalStrengths((Class)CellSignalStrengthCdma.class);
        if (!cellSignalStrengths.isEmpty()) {
            return cellSignalStrengths.get(0).getLevel();
        }
        return 0;
    }
    
    private String getIconKey() {
        if (this.mTelephonyDisplayInfo.getOverrideNetworkType() == 0) {
            return this.toIconKey(this.mTelephonyDisplayInfo.getNetworkType());
        }
        return this.toDisplayIconKey(this.mTelephonyDisplayInfo.getOverrideNetworkType());
    }
    
    private int getNumLevels() {
        if (this.mInflateSignalStrengths) {
            return CellSignalStrength.getNumSignalStrengthLevels() + 1;
        }
        return CellSignalStrength.getNumSignalStrengthLevels();
    }
    
    private boolean isCarrierNetworkChangeActive() {
        return ((MobileState)super.mCurrentState).carrierNetworkChangeMode;
    }
    
    private boolean isCdma() {
        final SignalStrength mSignalStrength = this.mSignalStrength;
        return mSignalStrength != null && !mSignalStrength.isGsm();
    }
    
    private boolean isRoaming() {
        final boolean carrierNetworkChangeActive = this.isCarrierNetworkChangeActive();
        final boolean b = false;
        final boolean b2 = false;
        if (carrierNetworkChangeActive) {
            return false;
        }
        if (this.isCdma() && this.mServiceState != null) {
            final int eriIconMode = this.mPhone.getCdmaEriInformation().getEriIconMode();
            boolean b3 = b2;
            if (this.mPhone.getCdmaEriInformation().getEriIconIndex() != 1) {
                if (eriIconMode != 0) {
                    b3 = b2;
                    if (eriIconMode != 1) {
                        return b3;
                    }
                }
                b3 = true;
            }
            return b3;
        }
        final ServiceState mServiceState = this.mServiceState;
        boolean b4 = b;
        if (mServiceState != null) {
            b4 = b;
            if (mServiceState.getRoaming()) {
                b4 = true;
            }
        }
        return b4;
    }
    
    private void mapIconSets() {
        this.mNetworkToIconLookup.clear();
        this.mNetworkToIconLookup.put(this.toIconKey(5), TelephonyIcons.THREE_G);
        this.mNetworkToIconLookup.put(this.toIconKey(6), TelephonyIcons.THREE_G);
        this.mNetworkToIconLookup.put(this.toIconKey(12), TelephonyIcons.THREE_G);
        this.mNetworkToIconLookup.put(this.toIconKey(14), TelephonyIcons.THREE_G);
        this.mNetworkToIconLookup.put(this.toIconKey(3), TelephonyIcons.THREE_G);
        this.mNetworkToIconLookup.put(this.toIconKey(17), TelephonyIcons.THREE_G);
        if (!this.mConfig.showAtLeast3G) {
            this.mNetworkToIconLookup.put(this.toIconKey(0), TelephonyIcons.UNKNOWN);
            this.mNetworkToIconLookup.put(this.toIconKey(2), TelephonyIcons.E);
            this.mNetworkToIconLookup.put(this.toIconKey(4), TelephonyIcons.ONE_X);
            this.mNetworkToIconLookup.put(this.toIconKey(7), TelephonyIcons.ONE_X);
            this.mDefaultIcons = TelephonyIcons.G;
        }
        else {
            this.mNetworkToIconLookup.put(this.toIconKey(0), TelephonyIcons.THREE_G);
            this.mNetworkToIconLookup.put(this.toIconKey(2), TelephonyIcons.THREE_G);
            this.mNetworkToIconLookup.put(this.toIconKey(4), TelephonyIcons.THREE_G);
            this.mNetworkToIconLookup.put(this.toIconKey(7), TelephonyIcons.THREE_G);
            this.mDefaultIcons = TelephonyIcons.THREE_G;
        }
        MobileIconGroup mobileIconGroup = TelephonyIcons.THREE_G;
        final NetworkControllerImpl.Config mConfig = this.mConfig;
        IconGroup h_PLUS = null;
        Label_0337: {
            if (mConfig.show4gFor3g) {
                mobileIconGroup = TelephonyIcons.FOUR_G;
            }
            else if (mConfig.hspaDataDistinguishable) {
                mobileIconGroup = TelephonyIcons.H;
                h_PLUS = TelephonyIcons.H_PLUS;
                break Label_0337;
            }
            h_PLUS = mobileIconGroup;
        }
        this.mNetworkToIconLookup.put(this.toIconKey(8), mobileIconGroup);
        this.mNetworkToIconLookup.put(this.toIconKey(9), mobileIconGroup);
        this.mNetworkToIconLookup.put(this.toIconKey(10), mobileIconGroup);
        this.mNetworkToIconLookup.put(this.toIconKey(15), (MobileIconGroup)h_PLUS);
        if (this.mConfig.show4gForLte) {
            this.mNetworkToIconLookup.put(this.toIconKey(13), TelephonyIcons.FOUR_G);
            if (this.mConfig.hideLtePlus) {
                this.mNetworkToIconLookup.put(this.toDisplayIconKey(1), TelephonyIcons.FOUR_G);
            }
            else {
                this.mNetworkToIconLookup.put(this.toDisplayIconKey(1), TelephonyIcons.FOUR_G_PLUS);
            }
        }
        else {
            this.mNetworkToIconLookup.put(this.toIconKey(13), TelephonyIcons.LTE);
            if (this.mConfig.hideLtePlus) {
                this.mNetworkToIconLookup.put(this.toDisplayIconKey(1), TelephonyIcons.LTE);
            }
            else {
                this.mNetworkToIconLookup.put(this.toDisplayIconKey(1), TelephonyIcons.LTE_PLUS);
            }
        }
        this.mNetworkToIconLookup.put(this.toIconKey(18), TelephonyIcons.WFC);
        this.mNetworkToIconLookup.put(this.toDisplayIconKey(2), TelephonyIcons.LTE_CA_5G_E);
        this.mNetworkToIconLookup.put(this.toDisplayIconKey(3), TelephonyIcons.NR_5G);
        this.mNetworkToIconLookup.put(this.toDisplayIconKey(4), TelephonyIcons.NR_5G_PLUS);
    }
    
    private String toDisplayIconKey(final int n) {
        if (n == 1) {
            final StringBuilder sb = new StringBuilder();
            sb.append(this.toIconKey(13));
            sb.append("_CA");
            return sb.toString();
        }
        if (n == 2) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(this.toIconKey(13));
            sb2.append("_CA_Plus");
            return sb2.toString();
        }
        if (n == 3) {
            return "5G";
        }
        if (n != 4) {
            return "unsupported";
        }
        return "5G_Plus";
    }
    
    private String toIconKey(final int i) {
        return Integer.toString(i);
    }
    
    private void updateDataSim() {
        final int activeDataSubId = this.mDefaults.getActiveDataSubId();
        final boolean validSubscriptionId = SubscriptionManager.isValidSubscriptionId(activeDataSubId);
        boolean dataSim = true;
        if (validSubscriptionId) {
            final MobileState mobileState = (MobileState)super.mCurrentState;
            if (activeDataSubId != this.mSubscriptionInfo.getSubscriptionId()) {
                dataSim = false;
            }
            mobileState.dataSim = dataSim;
        }
        else {
            ((MobileState)super.mCurrentState).dataSim = true;
        }
    }
    
    private void updateInflateSignalStrength() {
        this.mInflateSignalStrengths = SignalStrengthUtil.shouldInflateSignalStrength(super.mContext, this.mSubscriptionInfo.getSubscriptionId());
    }
    
    private final void updateTelephony() {
        if (SignalController.DEBUG) {
            final String mTag = super.mTag;
            final StringBuilder sb = new StringBuilder();
            sb.append("updateTelephonySignalStrength: hasService=");
            sb.append(Utils.isInService(this.mServiceState));
            sb.append(" ss=");
            sb.append(this.mSignalStrength);
            sb.append(" displayInfo=");
            sb.append(this.mTelephonyDisplayInfo);
            Log.d(mTag, sb.toString());
        }
        this.checkDefaultData();
        final MobileState mobileState = (MobileState)super.mCurrentState;
        final boolean inService = Utils.isInService(this.mServiceState);
        final boolean b = true;
        mobileState.connected = (inService && this.mSignalStrength != null);
        if (((MobileState)super.mCurrentState).connected) {
            if (!this.mSignalStrength.isGsm() && this.mConfig.alwaysShowCdmaRssi) {
                ((MobileState)super.mCurrentState).level = this.getCdmaLevel();
            }
            else {
                ((MobileState)super.mCurrentState).level = this.mSignalStrength.getLevel();
            }
        }
        final String iconKey = this.getIconKey();
        if (this.mNetworkToIconLookup.get(iconKey) != null) {
            ((MobileState)super.mCurrentState).iconGroup = this.mNetworkToIconLookup.get(iconKey);
        }
        else {
            ((MobileState)super.mCurrentState).iconGroup = this.mDefaultIcons;
        }
        final State mCurrentState = super.mCurrentState;
        ((MobileState)mCurrentState).dataConnected = (((MobileState)mCurrentState).connected && this.mDataState == 2 && b);
        ((MobileState)super.mCurrentState).roaming = this.isRoaming();
        if (this.isCarrierNetworkChangeActive()) {
            ((MobileState)super.mCurrentState).iconGroup = TelephonyIcons.CARRIER_NETWORK_CHANGE;
        }
        else if (this.isDataDisabled() && !this.mConfig.alwaysShowDataRatIcon) {
            if (this.mSubscriptionInfo.getSubscriptionId() != this.mDefaults.getDefaultDataSubId()) {
                ((MobileState)super.mCurrentState).iconGroup = TelephonyIcons.NOT_DEFAULT_DATA;
            }
            else {
                ((MobileState)super.mCurrentState).iconGroup = TelephonyIcons.DATA_DISABLED;
            }
        }
        final boolean emergencyOnly = this.isEmergencyOnly();
        final State mCurrentState2 = super.mCurrentState;
        if (emergencyOnly != ((MobileState)mCurrentState2).isEmergency) {
            ((MobileState)mCurrentState2).isEmergency = this.isEmergencyOnly();
            super.mNetworkController.recalculateEmergency();
        }
        if (((MobileState)super.mCurrentState).networkName.equals(this.mNetworkNameDefault)) {
            final ServiceState mServiceState = this.mServiceState;
            if (mServiceState != null && !TextUtils.isEmpty((CharSequence)mServiceState.getOperatorAlphaShort())) {
                ((MobileState)super.mCurrentState).networkName = this.mServiceState.getOperatorAlphaShort();
            }
        }
        if (((MobileState)super.mCurrentState).networkNameData.equals(this.mNetworkNameDefault)) {
            final ServiceState mServiceState2 = this.mServiceState;
            if (mServiceState2 != null && ((MobileState)super.mCurrentState).dataSim && !TextUtils.isEmpty((CharSequence)mServiceState2.getOperatorAlphaShort())) {
                ((MobileState)super.mCurrentState).networkNameData = this.mServiceState.getOperatorAlphaShort();
            }
        }
        this.notifyListenersIfNecessary();
    }
    
    @Override
    protected MobileState cleanState() {
        return new MobileState();
    }
    
    @Override
    public void dump(final PrintWriter printWriter) {
        super.dump(printWriter);
        final StringBuilder sb = new StringBuilder();
        sb.append("  mSubscription=");
        sb.append(this.mSubscriptionInfo);
        sb.append(",");
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("  mServiceState=");
        sb2.append(this.mServiceState);
        sb2.append(",");
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("  mSignalStrength=");
        sb3.append(this.mSignalStrength);
        sb3.append(",");
        printWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("  mTelephonyDisplayInfo=");
        sb4.append(this.mTelephonyDisplayInfo);
        sb4.append(",");
        printWriter.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append("  mDataState=");
        sb5.append(this.mDataState);
        sb5.append(",");
        printWriter.println(sb5.toString());
        final StringBuilder sb6 = new StringBuilder();
        sb6.append("  mInflateSignalStrengths=");
        sb6.append(this.mInflateSignalStrengths);
        sb6.append(",");
        printWriter.println(sb6.toString());
        final StringBuilder sb7 = new StringBuilder();
        sb7.append("  isDataDisabled=");
        sb7.append(this.isDataDisabled());
        sb7.append(",");
        printWriter.println(sb7.toString());
    }
    
    @Override
    public int getCurrentIconId() {
        final State mCurrentState = super.mCurrentState;
        if (((MobileState)mCurrentState).iconGroup == TelephonyIcons.CARRIER_NETWORK_CHANGE) {
            return SignalDrawable.getCarrierChangeState(this.getNumLevels());
        }
        final boolean connected = ((MobileState)mCurrentState).connected;
        boolean b = false;
        if (connected) {
            int level = ((MobileState)mCurrentState).level;
            if (this.mInflateSignalStrengths) {
                ++level;
            }
            final State mCurrentState2 = super.mCurrentState;
            final boolean b2 = ((MobileState)mCurrentState2).userSetup && (((MobileState)mCurrentState2).iconGroup == TelephonyIcons.DATA_DISABLED || (((MobileState)mCurrentState2).iconGroup == TelephonyIcons.NOT_DEFAULT_DATA && ((MobileState)mCurrentState2).defaultDataOff));
            final boolean b3 = ((MobileState)super.mCurrentState).inetCondition == 0;
            if (b2 || b3) {
                b = true;
            }
            return SignalDrawable.getState(level, this.getNumLevels(), b);
        }
        if (((MobileState)mCurrentState).enabled) {
            return SignalDrawable.getEmptyState(this.getNumLevels());
        }
        return 0;
    }
    
    @Override
    public int getQsCurrentIconId() {
        return this.getCurrentIconId();
    }
    
    public void handleBroadcast(final Intent intent) {
        final String action = intent.getAction();
        if (action.equals("android.telephony.action.SERVICE_PROVIDERS_UPDATED")) {
            this.updateNetworkName(intent.getBooleanExtra("android.telephony.extra.SHOW_SPN", false), intent.getStringExtra("android.telephony.extra.SPN"), intent.getStringExtra("android.telephony.extra.DATA_SPN"), intent.getBooleanExtra("android.telephony.extra.SHOW_PLMN", false), intent.getStringExtra("android.telephony.extra.PLMN"));
            this.notifyListenersIfNecessary();
        }
        else if (action.equals("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED")) {
            this.updateDataSim();
            this.notifyListenersIfNecessary();
        }
    }
    
    boolean isDataDisabled() {
        return this.mPhone.isDataConnectionAllowed() ^ true;
    }
    
    public boolean isEmergencyOnly() {
        final ServiceState mServiceState = this.mServiceState;
        return mServiceState != null && mServiceState.isEmergencyOnly();
    }
    
    @Override
    public void notifyListeners(final NetworkController.SignalCallback signalCallback) {
        final MobileIconGroup mobileIconGroup = ((SignalController<T, MobileIconGroup>)this).getIcons();
        final String string = this.getTextIfExists(this.getContentDescription()).toString();
        final CharSequence textIfExists = this.getTextIfExists(mobileIconGroup.mDataContentDescription);
        String s = Html.fromHtml(textIfExists.toString(), 0).toString();
        if (((MobileState)super.mCurrentState).inetCondition == 0) {
            s = super.mContext.getString(R$string.data_connection_no_internet);
        }
        final State mCurrentState = super.mCurrentState;
        final IconGroup iconGroup = ((MobileState)mCurrentState).iconGroup;
        final MobileIconGroup data_DISABLED = TelephonyIcons.DATA_DISABLED;
        final boolean b = true;
        final boolean b2 = (iconGroup == data_DISABLED || ((MobileState)mCurrentState).iconGroup == TelephonyIcons.NOT_DEFAULT_DATA) && ((MobileState)super.mCurrentState).userSetup;
        final boolean b3 = ((MobileState)super.mCurrentState).dataConnected || b2;
        final State mCurrentState2 = super.mCurrentState;
        final NetworkController.IconState iconState = new NetworkController.IconState(((MobileState)mCurrentState2).enabled && !((MobileState)mCurrentState2).airplaneMode, this.getCurrentIconId(), string);
        final boolean dataSim = ((MobileState)super.mCurrentState).dataSim;
        Object o = null;
        CharSequence networkName = null;
        int mQsDataType;
        if (dataSim) {
            if (!b3 && !this.mConfig.alwaysShowDataRatIcon) {
                mQsDataType = 0;
            }
            else {
                mQsDataType = mobileIconGroup.mQsDataType;
            }
            final State mCurrentState3 = super.mCurrentState;
            o = new NetworkController.IconState(((MobileState)mCurrentState3).enabled && !((MobileState)mCurrentState3).isEmergency, this.getQsCurrentIconId(), string);
            final State mCurrentState4 = super.mCurrentState;
            if (!((MobileState)mCurrentState4).isEmergency) {
                networkName = ((MobileState)mCurrentState4).networkName;
            }
        }
        else {
            mQsDataType = 0;
            networkName = null;
        }
        final State mCurrentState5 = super.mCurrentState;
        final boolean b4 = ((MobileState)mCurrentState5).dataConnected && !((MobileState)mCurrentState5).carrierNetworkChangeMode && ((MobileState)mCurrentState5).activityIn;
        final State mCurrentState6 = super.mCurrentState;
        final boolean b5 = ((MobileState)mCurrentState6).dataConnected && !((MobileState)mCurrentState6).carrierNetworkChangeMode && ((MobileState)mCurrentState6).activityOut;
        boolean b6 = b;
        if (!((MobileState)super.mCurrentState).isDefault) {
            b6 = (b2 && b);
        }
        int mDataType;
        if (!(b3 & b6) && !this.mConfig.alwaysShowDataRatIcon) {
            mDataType = 0;
        }
        else {
            mDataType = mobileIconGroup.mDataType;
        }
        signalCallback.setMobileDataIndicators(iconState, (NetworkController.IconState)o, mDataType, mQsDataType, b4, b5, s, textIfExists, networkName, mobileIconGroup.mIsWide, this.mSubscriptionInfo.getSubscriptionId(), ((MobileState)super.mCurrentState).roaming);
    }
    
    void onMobileDataChanged() {
        this.checkDefaultData();
        this.notifyListenersIfNecessary();
    }
    
    public void registerListener() {
        this.mPhone.listen(this.mPhoneStateListener, 5308897);
        super.mContext.getContentResolver().registerContentObserver(Settings$Global.getUriFor("mobile_data"), true, this.mObserver);
        final ContentResolver contentResolver = super.mContext.getContentResolver();
        final StringBuilder sb = new StringBuilder();
        sb.append("mobile_data");
        sb.append(this.mSubscriptionInfo.getSubscriptionId());
        contentResolver.registerContentObserver(Settings$Global.getUriFor(sb.toString()), true, this.mObserver);
    }
    
    @VisibleForTesting
    void setActivity(final int n) {
        final MobileState mobileState = (MobileState)super.mCurrentState;
        final boolean b = false;
        mobileState.activityIn = (n == 3 || n == 1);
        final MobileState mobileState2 = (MobileState)super.mCurrentState;
        boolean activityOut = false;
        Label_0062: {
            if (n != 3) {
                activityOut = b;
                if (n != 2) {
                    break Label_0062;
                }
            }
            activityOut = true;
        }
        mobileState2.activityOut = activityOut;
        this.notifyListenersIfNecessary();
    }
    
    public void setAirplaneMode(final boolean airplaneMode) {
        ((MobileState)super.mCurrentState).airplaneMode = airplaneMode;
        this.notifyListenersIfNecessary();
    }
    
    public void setCarrierNetworkChangeMode(final boolean carrierNetworkChangeMode) {
        ((MobileState)super.mCurrentState).carrierNetworkChangeMode = carrierNetworkChangeMode;
        this.updateTelephony();
    }
    
    public void setConfiguration(final NetworkControllerImpl.Config mConfig) {
        this.mConfig = mConfig;
        this.updateInflateSignalStrength();
        this.mapIconSets();
        this.updateTelephony();
    }
    
    public void setUserSetupComplete(final boolean userSetup) {
        ((MobileState)super.mCurrentState).userSetup = userSetup;
        this.notifyListenersIfNecessary();
    }
    
    public void unregisterListener() {
        this.mPhone.listen(this.mPhoneStateListener, 0);
        super.mContext.getContentResolver().unregisterContentObserver(this.mObserver);
    }
    
    @Override
    public void updateConnectivity(final BitSet set, final BitSet set2) {
        final boolean value = set2.get(super.mTransportType);
        ((MobileState)super.mCurrentState).isDefault = set.get(super.mTransportType);
        final State mCurrentState = super.mCurrentState;
        ((MobileState)mCurrentState).inetCondition = ((value || !((MobileState)mCurrentState).isDefault) ? 1 : 0);
        this.notifyListenersIfNecessary();
    }
    
    void updateNetworkName(final boolean b, final String s, final String s2, final boolean b2, final String str) {
        if (SignalController.CHATTY) {
            final StringBuilder sb = new StringBuilder();
            sb.append("updateNetworkName showSpn=");
            sb.append(b);
            sb.append(" spn=");
            sb.append(s);
            sb.append(" dataSpn=");
            sb.append(s2);
            sb.append(" showPlmn=");
            sb.append(b2);
            sb.append(" plmn=");
            sb.append(str);
            Log.d("CarrierLabel", sb.toString());
        }
        final StringBuilder sb2 = new StringBuilder();
        final StringBuilder sb3 = new StringBuilder();
        if (b2 && str != null) {
            sb2.append(str);
            sb3.append(str);
        }
        if (b && s != null) {
            if (sb2.length() != 0) {
                sb2.append(this.mNetworkNameSeparator);
            }
            sb2.append(s);
        }
        if (sb2.length() != 0) {
            ((MobileState)super.mCurrentState).networkName = sb2.toString();
        }
        else {
            ((MobileState)super.mCurrentState).networkName = this.mNetworkNameDefault;
        }
        if (b && s2 != null) {
            if (sb3.length() != 0) {
                sb3.append(this.mNetworkNameSeparator);
            }
            sb3.append(s2);
        }
        if (sb3.length() != 0) {
            ((MobileState)super.mCurrentState).networkNameData = sb3.toString();
        }
        else {
            ((MobileState)super.mCurrentState).networkNameData = this.mNetworkNameDefault;
        }
    }
    
    static class MobileIconGroup extends IconGroup
    {
        final int mDataContentDescription;
        final int mDataType;
        final boolean mIsWide;
        final int mQsDataType;
        
        public MobileIconGroup(final String s, final int[][] array, final int[][] array2, final int[] array3, final int n, final int n2, final int n3, final int n4, final int n5, final int mDataContentDescription, final int n6, final boolean mIsWide) {
            super(s, array, array2, array3, n, n2, n3, n4, n5);
            this.mDataContentDescription = mDataContentDescription;
            this.mDataType = n6;
            this.mIsWide = mIsWide;
            this.mQsDataType = n6;
        }
    }
    
    class MobilePhoneStateListener extends PhoneStateListener
    {
        public MobilePhoneStateListener(final Executor executor) {
            super(executor);
        }
        
        public void onActiveDataSubscriptionIdChanged(final int i) {
            if (SignalController.DEBUG) {
                final String mTag = MobileSignalController.this.mTag;
                final StringBuilder sb = new StringBuilder();
                sb.append("onActiveDataSubscriptionIdChanged: subId=");
                sb.append(i);
                Log.d(mTag, sb.toString());
            }
            MobileSignalController.this.updateDataSim();
            MobileSignalController.this.updateTelephony();
        }
        
        public void onCarrierNetworkChange(final boolean b) {
            if (SignalController.DEBUG) {
                final String mTag = MobileSignalController.this.mTag;
                final StringBuilder sb = new StringBuilder();
                sb.append("onCarrierNetworkChange: active=");
                sb.append(b);
                Log.d(mTag, sb.toString());
            }
            final MobileSignalController this$0 = MobileSignalController.this;
            ((MobileState)this$0.mCurrentState).carrierNetworkChangeMode = b;
            this$0.updateTelephony();
        }
        
        public void onDataActivity(final int n) {
            if (SignalController.DEBUG) {
                final String mTag = MobileSignalController.this.mTag;
                final StringBuilder sb = new StringBuilder();
                sb.append("onDataActivity: direction=");
                sb.append(n);
                Log.d(mTag, sb.toString());
            }
            MobileSignalController.this.setActivity(n);
        }
        
        public void onDataConnectionStateChanged(final int i, final int j) {
            if (SignalController.DEBUG) {
                final String mTag = MobileSignalController.this.mTag;
                final StringBuilder sb = new StringBuilder();
                sb.append("onDataConnectionStateChanged: state=");
                sb.append(i);
                sb.append(" type=");
                sb.append(j);
                Log.d(mTag, sb.toString());
            }
            MobileSignalController.this.mDataState = i;
            if (j != MobileSignalController.this.mTelephonyDisplayInfo.getNetworkType()) {
                MobileSignalController.this.mTelephonyDisplayInfo = new TelephonyDisplayInfo(j, 0);
            }
            MobileSignalController.this.updateTelephony();
        }
        
        public void onDisplayInfoChanged(final TelephonyDisplayInfo obj) {
            if (SignalController.DEBUG) {
                final String mTag = MobileSignalController.this.mTag;
                final StringBuilder sb = new StringBuilder();
                sb.append("onDisplayInfoChanged: telephonyDisplayInfo=");
                sb.append(obj);
                Log.d(mTag, sb.toString());
            }
            MobileSignalController.this.mTelephonyDisplayInfo = obj;
            MobileSignalController.this.updateTelephony();
        }
        
        public void onServiceStateChanged(final ServiceState serviceState) {
            if (SignalController.DEBUG) {
                final String mTag = MobileSignalController.this.mTag;
                final StringBuilder sb = new StringBuilder();
                sb.append("onServiceStateChanged voiceState=");
                sb.append(serviceState.getState());
                sb.append(" dataState=");
                sb.append(serviceState.getDataRegistrationState());
                Log.d(mTag, sb.toString());
            }
            MobileSignalController.this.mServiceState = serviceState;
        }
        
        public void onSignalStrengthsChanged(final SignalStrength obj) {
            if (SignalController.DEBUG) {
                final String mTag = MobileSignalController.this.mTag;
                final StringBuilder sb = new StringBuilder();
                sb.append("onSignalStrengthsChanged signalStrength=");
                sb.append(obj);
                String string;
                if (obj == null) {
                    string = "";
                }
                else {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append(" level=");
                    sb2.append(obj.getLevel());
                    string = sb2.toString();
                }
                sb.append(string);
                Log.d(mTag, sb.toString());
            }
            MobileSignalController.this.mSignalStrength = obj;
            MobileSignalController.this.updateTelephony();
        }
    }
    
    static class MobileState extends State
    {
        boolean airplaneMode;
        boolean carrierNetworkChangeMode;
        boolean dataConnected;
        boolean dataSim;
        boolean defaultDataOff;
        boolean isDefault;
        boolean isEmergency;
        String networkName;
        String networkNameData;
        boolean roaming;
        boolean userSetup;
        
        @Override
        public void copyFrom(final State state) {
            super.copyFrom(state);
            final MobileState mobileState = (MobileState)state;
            this.dataSim = mobileState.dataSim;
            this.networkName = mobileState.networkName;
            this.networkNameData = mobileState.networkNameData;
            this.dataConnected = mobileState.dataConnected;
            this.isDefault = mobileState.isDefault;
            this.isEmergency = mobileState.isEmergency;
            this.airplaneMode = mobileState.airplaneMode;
            this.carrierNetworkChangeMode = mobileState.carrierNetworkChangeMode;
            this.userSetup = mobileState.userSetup;
            this.roaming = mobileState.roaming;
            this.defaultDataOff = mobileState.defaultDataOff;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (super.equals(o)) {
                final MobileState mobileState = (MobileState)o;
                if (Objects.equals(mobileState.networkName, this.networkName) && Objects.equals(mobileState.networkNameData, this.networkNameData) && mobileState.dataSim == this.dataSim && mobileState.dataConnected == this.dataConnected && mobileState.isEmergency == this.isEmergency && mobileState.airplaneMode == this.airplaneMode && mobileState.carrierNetworkChangeMode == this.carrierNetworkChangeMode && mobileState.userSetup == this.userSetup && mobileState.isDefault == this.isDefault && mobileState.roaming == this.roaming && mobileState.defaultDataOff == this.defaultDataOff) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        protected void toString(final StringBuilder sb) {
            super.toString(sb);
            sb.append(',');
            sb.append("dataSim=");
            sb.append(this.dataSim);
            sb.append(',');
            sb.append("networkName=");
            sb.append(this.networkName);
            sb.append(',');
            sb.append("networkNameData=");
            sb.append(this.networkNameData);
            sb.append(',');
            sb.append("dataConnected=");
            sb.append(this.dataConnected);
            sb.append(',');
            sb.append("roaming=");
            sb.append(this.roaming);
            sb.append(',');
            sb.append("isDefault=");
            sb.append(this.isDefault);
            sb.append(',');
            sb.append("isEmergency=");
            sb.append(this.isEmergency);
            sb.append(',');
            sb.append("airplaneMode=");
            sb.append(this.airplaneMode);
            sb.append(',');
            sb.append("carrierNetworkChangeMode=");
            sb.append(this.carrierNetworkChangeMode);
            sb.append(',');
            sb.append("userSetup=");
            sb.append(this.userSetup);
            sb.append(',');
            sb.append("defaultDataOff=");
            sb.append(this.defaultDataOff);
        }
    }
}
