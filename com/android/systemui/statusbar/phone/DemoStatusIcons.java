// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.graphics.Rect;
import com.android.systemui.R$drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup$LayoutParams;
import android.service.notification.StatusBarNotification;
import com.android.internal.statusbar.StatusBarIcon;
import android.os.UserHandle;
import android.graphics.drawable.Icon;
import com.android.systemui.statusbar.StatusBarIconView;
import java.util.Iterator;
import com.android.systemui.statusbar.StatusIconDisplayable;
import android.widget.LinearLayout$LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.statusbar.StatusBarWifiView;
import android.widget.LinearLayout;
import com.android.systemui.statusbar.StatusBarMobileView;
import java.util.ArrayList;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.DemoMode;

public class DemoStatusIcons extends StatusIconContainer implements DemoMode, DarkReceiver
{
    private int mColor;
    private boolean mDemoMode;
    private final int mIconSize;
    private final ArrayList<StatusBarMobileView> mMobileViews;
    private final LinearLayout mStatusIcons;
    private StatusBarWifiView mWifiView;
    
    public DemoStatusIcons(final LinearLayout mStatusIcons, final int mIconSize) {
        super(mStatusIcons.getContext());
        this.mMobileViews = new ArrayList<StatusBarMobileView>();
        this.mStatusIcons = mStatusIcons;
        this.mIconSize = mIconSize;
        this.mColor = -1;
        if (mStatusIcons instanceof StatusIconContainer) {
            this.setShouldRestrictIcons(((StatusIconContainer)mStatusIcons).isRestrictingIcons());
        }
        else {
            this.setShouldRestrictIcons(false);
        }
        this.setLayoutParams(this.mStatusIcons.getLayoutParams());
        this.setPadding(this.mStatusIcons.getPaddingLeft(), this.mStatusIcons.getPaddingTop(), this.mStatusIcons.getPaddingRight(), this.mStatusIcons.getPaddingBottom());
        this.setOrientation(this.mStatusIcons.getOrientation());
        this.setGravity(16);
        final ViewGroup viewGroup = (ViewGroup)this.mStatusIcons.getParent();
        viewGroup.addView((View)this, viewGroup.indexOfChild((View)this.mStatusIcons));
    }
    
    private LinearLayout$LayoutParams createLayoutParams() {
        return new LinearLayout$LayoutParams(-2, this.mIconSize);
    }
    
    private StatusBarMobileView matchingMobileView(final StatusIconDisplayable statusIconDisplayable) {
        if (!(statusIconDisplayable instanceof StatusBarMobileView)) {
            return null;
        }
        final StatusBarMobileView statusBarMobileView = (StatusBarMobileView)statusIconDisplayable;
        for (final StatusBarMobileView statusBarMobileView2 : this.mMobileViews) {
            if (statusBarMobileView2.getState().subId == statusBarMobileView.getState().subId) {
                return statusBarMobileView2;
            }
        }
        return null;
    }
    
    private void updateColors() {
        for (int i = 0; i < this.getChildCount(); ++i) {
            final StatusIconDisplayable statusIconDisplayable = (StatusIconDisplayable)this.getChildAt(i);
            statusIconDisplayable.setStaticDrawableColor(this.mColor);
            statusIconDisplayable.setDecorColor(this.mColor);
        }
    }
    
    private void updateSlot(final String tag, final String s, final int n) {
        if (!this.mDemoMode) {
            return;
        }
        String packageName;
        if ((packageName = s) == null) {
            packageName = super.mContext.getPackageName();
        }
        while (true) {
            for (int i = 0; i < this.getChildCount(); ++i) {
                final View child = this.getChildAt(i);
                if (child instanceof StatusBarIconView) {
                    final StatusBarIconView statusBarIconView = (StatusBarIconView)child;
                    if (tag.equals(statusBarIconView.getTag())) {
                        if (n != 0) {
                            final StatusBarIcon statusBarIcon = statusBarIconView.getStatusBarIcon();
                            statusBarIcon.visible = true;
                            statusBarIcon.icon = Icon.createWithResource(statusBarIcon.icon.getResPackage(), n);
                            statusBarIconView.set(statusBarIcon);
                            statusBarIconView.updateDrawable();
                            return;
                        }
                        if (n == 0) {
                            if (i != -1) {
                                this.removeViewAt(i);
                            }
                            return;
                        }
                        final StatusBarIcon statusBarIcon2 = new StatusBarIcon(packageName, UserHandle.SYSTEM, n, 0, 0, (CharSequence)"Demo");
                        statusBarIcon2.visible = true;
                        final StatusBarIconView statusBarIconView2 = new StatusBarIconView(this.getContext(), tag, null, false);
                        statusBarIconView2.setTag((Object)tag);
                        statusBarIconView2.set(statusBarIcon2);
                        statusBarIconView2.setStaticDrawableColor(this.mColor);
                        statusBarIconView2.setDecorColor(this.mColor);
                        this.addView((View)statusBarIconView2, 0, (ViewGroup$LayoutParams)this.createLayoutParams());
                        return;
                    }
                }
            }
            int i = -1;
            continue;
        }
    }
    
    public void addDemoWifiView(final StatusBarSignalPolicy.WifiIconState wifiIconState) {
        Log.d("DemoStatusIcons", "addDemoWifiView: ");
        final StatusBarWifiView fromContext = StatusBarWifiView.fromContext(super.mContext, wifiIconState.slot);
        final int childCount = this.getChildCount();
        int n = 0;
        int n2;
        while (true) {
            n2 = childCount;
            if (n >= this.getChildCount()) {
                break;
            }
            if (this.getChildAt(n) instanceof StatusBarMobileView) {
                n2 = n;
                break;
            }
            ++n;
        }
        (this.mWifiView = fromContext).applyWifiState(wifiIconState);
        this.mWifiView.setStaticDrawableColor(this.mColor);
        this.addView((View)fromContext, n2, (ViewGroup$LayoutParams)this.createLayoutParams());
    }
    
    public void addMobileView(final StatusBarSignalPolicy.MobileIconState mobileIconState) {
        Log.d("DemoStatusIcons", "addMobileView: ");
        final StatusBarMobileView fromContext = StatusBarMobileView.fromContext(super.mContext, mobileIconState.slot);
        fromContext.applyMobileState(mobileIconState);
        fromContext.setStaticDrawableColor(this.mColor);
        this.mMobileViews.add(fromContext);
        this.addView((View)fromContext, this.getChildCount(), (ViewGroup$LayoutParams)this.createLayoutParams());
    }
    
    @Override
    public void dispatchDemoCommand(String s, final Bundle bundle) {
        final boolean mDemoMode = this.mDemoMode;
        final int n = 0;
        if (!mDemoMode && s.equals("enter")) {
            this.mDemoMode = true;
            this.mStatusIcons.setVisibility(8);
            this.setVisibility(0);
        }
        else if (this.mDemoMode && s.equals("exit")) {
            this.mDemoMode = false;
            this.mStatusIcons.setVisibility(0);
            this.setVisibility(8);
        }
        else if (this.mDemoMode && s.equals("status")) {
            s = bundle.getString("volume");
            if (s != null) {
                int stat_sys_ringer_vibrate;
                if (s.equals("vibrate")) {
                    stat_sys_ringer_vibrate = R$drawable.stat_sys_ringer_vibrate;
                }
                else {
                    stat_sys_ringer_vibrate = 0;
                }
                this.updateSlot("volume", null, stat_sys_ringer_vibrate);
            }
            s = bundle.getString("zen");
            if (s != null) {
                int stat_sys_dnd;
                if (s.equals("dnd")) {
                    stat_sys_dnd = R$drawable.stat_sys_dnd;
                }
                else {
                    stat_sys_dnd = 0;
                }
                this.updateSlot("zen", null, stat_sys_dnd);
            }
            s = bundle.getString("bluetooth");
            if (s != null) {
                int stat_sys_data_bluetooth_connected;
                if (s.equals("connected")) {
                    stat_sys_data_bluetooth_connected = R$drawable.stat_sys_data_bluetooth_connected;
                }
                else {
                    stat_sys_data_bluetooth_connected = 0;
                }
                this.updateSlot("bluetooth", null, stat_sys_data_bluetooth_connected);
            }
            s = bundle.getString("location");
            if (s != null) {
                int n2;
                if (s.equals("show")) {
                    n2 = 17303123;
                }
                else {
                    n2 = 0;
                }
                this.updateSlot("location", null, n2);
            }
            s = bundle.getString("alarm");
            if (s != null) {
                int stat_sys_alarm;
                if (s.equals("show")) {
                    stat_sys_alarm = R$drawable.stat_sys_alarm;
                }
                else {
                    stat_sys_alarm = 0;
                }
                this.updateSlot("alarm_clock", null, stat_sys_alarm);
            }
            s = bundle.getString("tty");
            if (s != null) {
                int stat_sys_tty_mode;
                if (s.equals("show")) {
                    stat_sys_tty_mode = R$drawable.stat_sys_tty_mode;
                }
                else {
                    stat_sys_tty_mode = 0;
                }
                this.updateSlot("tty", null, stat_sys_tty_mode);
            }
            s = bundle.getString("mute");
            if (s != null) {
                int n3;
                if (s.equals("show")) {
                    n3 = 17301622;
                }
                else {
                    n3 = 0;
                }
                this.updateSlot("mute", null, n3);
            }
            s = bundle.getString("speakerphone");
            if (s != null) {
                int n4;
                if (s.equals("show")) {
                    n4 = 17301639;
                }
                else {
                    n4 = 0;
                }
                this.updateSlot("speakerphone", null, n4);
            }
            s = bundle.getString("cast");
            if (s != null) {
                int stat_sys_cast;
                if (s.equals("show")) {
                    stat_sys_cast = R$drawable.stat_sys_cast;
                }
                else {
                    stat_sys_cast = 0;
                }
                this.updateSlot("cast", null, stat_sys_cast);
            }
            s = bundle.getString("hotspot");
            if (s != null) {
                int stat_sys_hotspot = n;
                if (s.equals("show")) {
                    stat_sys_hotspot = R$drawable.stat_sys_hotspot;
                }
                this.updateSlot("hotspot", null, stat_sys_hotspot);
            }
        }
    }
    
    @Override
    public void onDarkChanged(final Rect rect, final float n, final int n2) {
        this.setColor(DarkIconDispatcher.getTint(rect, (View)this.mStatusIcons, n2));
        final StatusBarWifiView mWifiView = this.mWifiView;
        if (mWifiView != null) {
            mWifiView.onDarkChanged(rect, n, n2);
        }
        final Iterator<StatusBarMobileView> iterator = this.mMobileViews.iterator();
        while (iterator.hasNext()) {
            iterator.next().onDarkChanged(rect, n, n2);
        }
    }
    
    public void onRemoveIcon(final StatusIconDisplayable statusIconDisplayable) {
        if (statusIconDisplayable.getSlot().equals("wifi")) {
            this.removeView((View)this.mWifiView);
            this.mWifiView = null;
        }
        else {
            final StatusBarMobileView matchingMobileView = this.matchingMobileView(statusIconDisplayable);
            if (matchingMobileView != null) {
                this.removeView((View)matchingMobileView);
                this.mMobileViews.remove(matchingMobileView);
            }
        }
    }
    
    public void remove() {
        this.mMobileViews.clear();
        ((ViewGroup)this.getParent()).removeView((View)this);
    }
    
    public void setColor(final int mColor) {
        this.mColor = mColor;
        this.updateColors();
    }
    
    public void updateMobileState(final StatusBarSignalPolicy.MobileIconState mobileIconState) {
        Log.d("DemoStatusIcons", "updateMobileState: ");
        for (int i = 0; i < this.mMobileViews.size(); ++i) {
            final StatusBarMobileView statusBarMobileView = this.mMobileViews.get(i);
            if (statusBarMobileView.getState().subId == mobileIconState.subId) {
                statusBarMobileView.applyMobileState(mobileIconState);
                return;
            }
        }
        this.addMobileView(mobileIconState);
    }
    
    public void updateWifiState(final StatusBarSignalPolicy.WifiIconState wifiIconState) {
        Log.d("DemoStatusIcons", "updateWifiState: ");
        final StatusBarWifiView mWifiView = this.mWifiView;
        if (mWifiView == null) {
            this.addDemoWifiView(wifiIconState);
        }
        else {
            mWifiView.applyWifiState(wifiIconState);
        }
    }
}
