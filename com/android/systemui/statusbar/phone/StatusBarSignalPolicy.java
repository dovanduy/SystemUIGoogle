// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import java.util.Objects;
import android.util.ArraySet;
import com.android.systemui.R$string;
import android.telephony.SubscriptionInfo;
import java.util.List;
import java.util.Iterator;
import android.util.Log;
import com.android.systemui.R$drawable;
import com.android.systemui.Dependency;
import com.android.systemui.R$bool;
import java.util.ArrayList;
import android.os.Handler;
import android.content.Context;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.statusbar.policy.SecurityController;
import com.android.systemui.statusbar.policy.NetworkController;

public class StatusBarSignalPolicy implements SignalCallback, SecurityControllerCallback, Tunable
{
    private boolean mActivityEnabled;
    private boolean mBlockAirplane;
    private boolean mBlockEthernet;
    private boolean mBlockMobile;
    private boolean mBlockWifi;
    private final Context mContext;
    private boolean mForceBlockWifi;
    private final Handler mHandler;
    private final StatusBarIconController mIconController;
    private boolean mIsAirplaneMode;
    private ArrayList<MobileIconState> mMobileStates;
    private final NetworkController mNetworkController;
    private final SecurityController mSecurityController;
    private final String mSlotAirplane;
    private final String mSlotEthernet;
    private final String mSlotMobile;
    private final String mSlotVpn;
    private final String mSlotWifi;
    private WifiIconState mWifiIconState;
    
    public StatusBarSignalPolicy(final Context mContext, final StatusBarIconController mIconController) {
        this.mHandler = Handler.getMain();
        this.mIsAirplaneMode = false;
        this.mMobileStates = new ArrayList<MobileIconState>();
        this.mWifiIconState = new WifiIconState();
        this.mContext = mContext;
        this.mSlotAirplane = mContext.getString(17041279);
        this.mSlotMobile = this.mContext.getString(17041296);
        this.mSlotWifi = this.mContext.getString(17041311);
        this.mSlotEthernet = this.mContext.getString(17041289);
        this.mSlotVpn = this.mContext.getString(17041310);
        this.mActivityEnabled = this.mContext.getResources().getBoolean(R$bool.config_showActivity);
        this.mIconController = mIconController;
        this.mNetworkController = Dependency.get(NetworkController.class);
        this.mSecurityController = Dependency.get(SecurityController.class);
        Dependency.get(TunerService.class).addTunable((TunerService.Tunable)this, "icon_blacklist");
        this.mNetworkController.addCallback((NetworkController.SignalCallback)this);
        this.mSecurityController.addCallback((SecurityController.SecurityControllerCallback)this);
    }
    
    private int currentVpnIconId(final boolean b) {
        int n;
        if (b) {
            n = R$drawable.stat_sys_branded_vpn;
        }
        else {
            n = R$drawable.stat_sys_vpn_ic;
        }
        return n;
    }
    
    private MobileIconState getFirstMobileState() {
        if (this.mMobileStates.size() > 0) {
            return this.mMobileStates.get(0);
        }
        return null;
    }
    
    private MobileIconState getState(final int i) {
        for (final MobileIconState mobileIconState : this.mMobileStates) {
            if (mobileIconState.subId == i) {
                return mobileIconState;
            }
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Unexpected subscription ");
        sb.append(i);
        Log.e("StatusBarSignalPolicy", sb.toString());
        return null;
    }
    
    private boolean hasCorrectSubs(final List<SubscriptionInfo> list) {
        final int size = list.size();
        if (size != this.mMobileStates.size()) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            if (this.mMobileStates.get(i).subId != list.get(i).getSubscriptionId()) {
                return false;
            }
        }
        return true;
    }
    
    private void updateShowWifiSignalSpacer(final WifiIconState wifiIconState) {
        final MobileIconState firstMobileState = this.getFirstMobileState();
        wifiIconState.signalSpacerVisible = (firstMobileState != null && firstMobileState.typeId != 0);
    }
    
    private void updateVpn() {
        final boolean vpnEnabled = this.mSecurityController.isVpnEnabled();
        this.mIconController.setIcon(this.mSlotVpn, this.currentVpnIconId(this.mSecurityController.isVpnBranded()), this.mContext.getResources().getString(R$string.accessibility_vpn_on));
        this.mIconController.setIconVisibility(this.mSlotVpn, vpnEnabled);
    }
    
    private void updateWifiIconWithState(final WifiIconState wifiIconState) {
        if (wifiIconState.visible && wifiIconState.resId > 0) {
            this.mIconController.setSignalIcon(this.mSlotWifi, wifiIconState);
            this.mIconController.setIconVisibility(this.mSlotWifi, true);
        }
        else {
            this.mIconController.setIconVisibility(this.mSlotWifi, false);
        }
    }
    
    @Override
    public void onStateChanged() {
        this.mHandler.post((Runnable)new _$$Lambda$StatusBarSignalPolicy$UsBELiDs0GJjQ8hYeagcWJmxhFc(this));
    }
    
    @Override
    public void onTuningChanged(final String anObject, final String s) {
        if (!"icon_blacklist".equals(anObject)) {
            return;
        }
        final ArraySet<String> iconBlacklist = StatusBarIconController.getIconBlacklist(this.mContext, s);
        final boolean contains = iconBlacklist.contains((Object)this.mSlotAirplane);
        final boolean contains2 = iconBlacklist.contains((Object)this.mSlotMobile);
        final boolean contains3 = iconBlacklist.contains((Object)this.mSlotWifi);
        final boolean contains4 = iconBlacklist.contains((Object)this.mSlotEthernet);
        if (contains != this.mBlockAirplane || contains2 != this.mBlockMobile || contains4 != this.mBlockEthernet || contains3 != this.mBlockWifi) {
            this.mBlockAirplane = contains;
            this.mBlockMobile = contains2;
            this.mBlockEthernet = contains4;
            this.mBlockWifi = (contains3 || this.mForceBlockWifi);
            this.mNetworkController.removeCallback((NetworkController.SignalCallback)this);
            this.mNetworkController.addCallback((NetworkController.SignalCallback)this);
        }
    }
    
    @Override
    public void setEthernetIndicators(final IconState iconState) {
        if (iconState.visible) {
            final boolean mBlockEthernet = this.mBlockEthernet;
        }
        final int icon = iconState.icon;
        final String contentDescription = iconState.contentDescription;
        if (icon > 0) {
            this.mIconController.setIcon(this.mSlotEthernet, icon, contentDescription);
            this.mIconController.setIconVisibility(this.mSlotEthernet, true);
        }
        else {
            this.mIconController.setIconVisibility(this.mSlotEthernet, false);
        }
    }
    
    @Override
    public void setIsAirplaneMode(final IconState iconState) {
        final boolean mIsAirplaneMode = iconState.visible && !this.mBlockAirplane;
        this.mIsAirplaneMode = mIsAirplaneMode;
        final int icon = iconState.icon;
        final String contentDescription = iconState.contentDescription;
        if (mIsAirplaneMode && icon > 0) {
            this.mIconController.setIcon(this.mSlotAirplane, icon, contentDescription);
            this.mIconController.setIconVisibility(this.mSlotAirplane, true);
        }
        else {
            this.mIconController.setIconVisibility(this.mSlotAirplane, false);
        }
    }
    
    @Override
    public void setMobileDataEnabled(final boolean b) {
    }
    
    @Override
    public void setMobileDataIndicators(final IconState iconState, final IconState iconState2, final int typeId, int typeId2, final boolean b, final boolean b2, final CharSequence typeContentDescription, final CharSequence charSequence, final CharSequence charSequence2, final boolean b3, final int n, final boolean roaming) {
        final MobileIconState state = this.getState(n);
        if (state == null) {
            return;
        }
        typeId2 = state.typeId;
        final boolean b4 = true;
        if (typeId != typeId2 && (typeId == 0 || typeId2 == 0)) {
            typeId2 = 1;
        }
        else {
            typeId2 = 0;
        }
        state.visible = (iconState.visible && !this.mBlockMobile);
        state.strengthId = iconState.icon;
        state.typeId = typeId;
        state.contentDescription = iconState.contentDescription;
        state.typeContentDescription = typeContentDescription;
        state.roaming = roaming;
        state.activityIn = (b && this.mActivityEnabled);
        state.activityOut = (b2 && this.mActivityEnabled && b4);
        this.mIconController.setMobileIcons(this.mSlotMobile, copyStates(this.mMobileStates));
        if (typeId2 != 0) {
            final WifiIconState copy = this.mWifiIconState.copy();
            this.updateShowWifiSignalSpacer(copy);
            if (!Objects.equals(copy, this.mWifiIconState)) {
                this.updateWifiIconWithState(copy);
                this.mWifiIconState = copy;
            }
        }
    }
    
    @Override
    public void setNoSims(final boolean b, final boolean b2) {
    }
    
    @Override
    public void setSubs(final List<SubscriptionInfo> list) {
        if (this.hasCorrectSubs(list)) {
            return;
        }
        this.mIconController.removeAllIconsForSlot(this.mSlotMobile);
        this.mMobileStates.clear();
        for (int size = list.size(), i = 0; i < size; ++i) {
            this.mMobileStates.add(new MobileIconState(list.get(i).getSubscriptionId()));
        }
    }
    
    @Override
    public void setWifiIndicators(final boolean b, final IconState iconState, final IconState iconState2, final boolean b2, final boolean b3, final String s, final boolean b4, final String s2) {
        final boolean visible = iconState.visible;
        final boolean b5 = true;
        final boolean visible2 = visible && !this.mBlockWifi;
        final boolean activityIn = b2 && this.mActivityEnabled && visible2;
        final boolean activityOut = b3 && this.mActivityEnabled && visible2;
        final WifiIconState copy = this.mWifiIconState.copy();
        copy.visible = visible2;
        copy.resId = iconState.icon;
        copy.activityIn = activityIn;
        copy.activityOut = activityOut;
        ((SignalIconState)copy).slot = this.mSlotWifi;
        copy.airplaneSpacerVisible = this.mIsAirplaneMode;
        copy.contentDescription = iconState.contentDescription;
        final MobileIconState firstMobileState = this.getFirstMobileState();
        copy.signalSpacerVisible = (firstMobileState != null && firstMobileState.typeId != 0 && b5);
        this.updateWifiIconWithState(copy);
        this.mWifiIconState = copy;
    }
    
    public static class MobileIconState extends SignalIconState
    {
        public boolean needsLeadingPadding;
        public boolean roaming;
        public int strengthId;
        public int subId;
        public CharSequence typeContentDescription;
        public int typeId;
        
        private MobileIconState(final int subId) {
            this.subId = subId;
        }
        
        private static List<MobileIconState> copyStates(final List<MobileIconState> list) {
            final ArrayList<MobileIconState> list2 = new ArrayList<MobileIconState>();
            for (final MobileIconState mobileIconState : list) {
                final MobileIconState e = new MobileIconState(mobileIconState.subId);
                mobileIconState.copyTo(e);
                list2.add(e);
            }
            return list2;
        }
        
        public MobileIconState copy() {
            final MobileIconState mobileIconState = new MobileIconState(this.subId);
            this.copyTo(mobileIconState);
            return mobileIconState;
        }
        
        public void copyTo(final MobileIconState mobileIconState) {
            super.copyTo((SignalIconState)mobileIconState);
            mobileIconState.subId = this.subId;
            mobileIconState.strengthId = this.strengthId;
            mobileIconState.typeId = this.typeId;
            mobileIconState.roaming = this.roaming;
            mobileIconState.needsLeadingPadding = this.needsLeadingPadding;
            mobileIconState.typeContentDescription = this.typeContentDescription;
        }
        
        @Override
        public boolean equals(final Object o) {
            boolean b2;
            final boolean b = b2 = false;
            if (o != null) {
                if (MobileIconState.class != o.getClass()) {
                    b2 = b;
                }
                else {
                    if (!super.equals(o)) {
                        return false;
                    }
                    final MobileIconState mobileIconState = (MobileIconState)o;
                    b2 = b;
                    if (this.subId == mobileIconState.subId) {
                        b2 = b;
                        if (this.strengthId == mobileIconState.strengthId) {
                            b2 = b;
                            if (this.typeId == mobileIconState.typeId) {
                                b2 = b;
                                if (this.roaming == mobileIconState.roaming) {
                                    b2 = b;
                                    if (this.needsLeadingPadding == mobileIconState.needsLeadingPadding) {
                                        b2 = b;
                                        if (Objects.equals(this.typeContentDescription, mobileIconState.typeContentDescription)) {
                                            b2 = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return b2;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), this.subId, this.strengthId, this.typeId, this.roaming, this.needsLeadingPadding, this.typeContentDescription);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("MobileIconState(subId=");
            sb.append(this.subId);
            sb.append(", strengthId=");
            sb.append(this.strengthId);
            sb.append(", roaming=");
            sb.append(this.roaming);
            sb.append(", typeId=");
            sb.append(this.typeId);
            sb.append(", visible=");
            sb.append(this.visible);
            sb.append(")");
            return sb.toString();
        }
    }
    
    private abstract static class SignalIconState
    {
        public boolean activityIn;
        public boolean activityOut;
        public String contentDescription;
        public String slot;
        public boolean visible;
        
        protected void copyTo(final SignalIconState signalIconState) {
            signalIconState.visible = this.visible;
            signalIconState.activityIn = this.activityIn;
            signalIconState.activityOut = this.activityOut;
            signalIconState.slot = this.slot;
            signalIconState.contentDescription = this.contentDescription;
        }
        
        @Override
        public boolean equals(final Object o) {
            boolean b2;
            final boolean b = b2 = false;
            if (o != null) {
                if (this.getClass() != o.getClass()) {
                    b2 = b;
                }
                else {
                    final SignalIconState signalIconState = (SignalIconState)o;
                    b2 = b;
                    if (this.visible == signalIconState.visible) {
                        b2 = b;
                        if (this.activityOut == signalIconState.activityOut) {
                            b2 = b;
                            if (this.activityIn == signalIconState.activityIn) {
                                b2 = b;
                                if (Objects.equals(this.contentDescription, signalIconState.contentDescription)) {
                                    b2 = b;
                                    if (Objects.equals(this.slot, signalIconState.slot)) {
                                        b2 = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return b2;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.visible, this.activityOut, this.slot);
        }
    }
    
    public static class WifiIconState extends SignalIconState
    {
        public boolean airplaneSpacerVisible;
        public int resId;
        public boolean signalSpacerVisible;
        
        public WifiIconState copy() {
            final WifiIconState wifiIconState = new WifiIconState();
            this.copyTo(wifiIconState);
            return wifiIconState;
        }
        
        public void copyTo(final WifiIconState wifiIconState) {
            super.copyTo((SignalIconState)wifiIconState);
            wifiIconState.resId = this.resId;
            wifiIconState.airplaneSpacerVisible = this.airplaneSpacerVisible;
            wifiIconState.signalSpacerVisible = this.signalSpacerVisible;
        }
        
        @Override
        public boolean equals(final Object o) {
            boolean b2;
            final boolean b = b2 = false;
            if (o != null) {
                if (WifiIconState.class != o.getClass()) {
                    b2 = b;
                }
                else {
                    if (!super.equals(o)) {
                        return false;
                    }
                    final WifiIconState wifiIconState = (WifiIconState)o;
                    b2 = b;
                    if (this.resId == wifiIconState.resId) {
                        b2 = b;
                        if (this.airplaneSpacerVisible == wifiIconState.airplaneSpacerVisible) {
                            b2 = b;
                            if (this.signalSpacerVisible == wifiIconState.signalSpacerVisible) {
                                b2 = true;
                            }
                        }
                    }
                }
            }
            return b2;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), this.resId, this.airplaneSpacerVisible, this.signalSpacerVisible);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("WifiIconState(resId=");
            sb.append(this.resId);
            sb.append(", visible=");
            sb.append(this.visible);
            sb.append(")");
            return sb.toString();
        }
    }
}
