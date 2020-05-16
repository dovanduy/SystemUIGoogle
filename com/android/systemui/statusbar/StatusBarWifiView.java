// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import com.android.systemui.R$dimen;
import android.service.notification.StatusBarNotification;
import com.android.systemui.R$id;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.LinearLayout;
import com.android.systemui.statusbar.phone.StatusBarSignalPolicy;
import android.widget.ImageView;
import android.view.View;
import com.android.systemui.plugins.DarkIconDispatcher;
import android.widget.FrameLayout;

public class StatusBarWifiView extends FrameLayout implements DarkReceiver, StatusIconDisplayable
{
    private View mAirplaneSpacer;
    private StatusBarIconView mDotView;
    private ImageView mIn;
    private View mInoutContainer;
    private ImageView mOut;
    private View mSignalSpacer;
    private String mSlot;
    private StatusBarSignalPolicy.WifiIconState mState;
    private int mVisibleState;
    private LinearLayout mWifiGroup;
    private ImageView mWifiIcon;
    
    public StatusBarWifiView(final Context context) {
        super(context);
        this.mVisibleState = -1;
    }
    
    public StatusBarWifiView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mVisibleState = -1;
    }
    
    public StatusBarWifiView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mVisibleState = -1;
    }
    
    public StatusBarWifiView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mVisibleState = -1;
    }
    
    public static StatusBarWifiView fromContext(final Context context, final String slot) {
        final StatusBarWifiView statusBarWifiView = (StatusBarWifiView)LayoutInflater.from(context).inflate(R$layout.status_bar_wifi_group, (ViewGroup)null);
        statusBarWifiView.setSlot(slot);
        statusBarWifiView.init();
        statusBarWifiView.setVisibleState(0);
        return statusBarWifiView;
    }
    
    private void init() {
        this.mWifiGroup = (LinearLayout)this.findViewById(R$id.wifi_group);
        this.mWifiIcon = (ImageView)this.findViewById(R$id.wifi_signal);
        this.mIn = (ImageView)this.findViewById(R$id.wifi_in);
        this.mOut = (ImageView)this.findViewById(R$id.wifi_out);
        this.mSignalSpacer = this.findViewById(R$id.wifi_signal_spacer);
        this.mAirplaneSpacer = this.findViewById(R$id.wifi_airplane_spacer);
        this.mInoutContainer = this.findViewById(R$id.inout_container);
        this.initDotView();
    }
    
    private void initDotView() {
        (this.mDotView = new StatusBarIconView(super.mContext, this.mSlot, null)).setVisibleState(1);
        final int dimensionPixelSize = super.mContext.getResources().getDimensionPixelSize(R$dimen.status_bar_icon_size);
        final FrameLayout$LayoutParams frameLayout$LayoutParams = new FrameLayout$LayoutParams(dimensionPixelSize, dimensionPixelSize);
        frameLayout$LayoutParams.gravity = 8388627;
        this.addView((View)this.mDotView, (ViewGroup$LayoutParams)frameLayout$LayoutParams);
    }
    
    private void initViewState() {
        this.setContentDescription((CharSequence)this.mState.contentDescription);
        final int resId = this.mState.resId;
        if (resId >= 0) {
            this.mWifiIcon.setImageDrawable(super.mContext.getDrawable(resId));
        }
        final ImageView mIn = this.mIn;
        final boolean activityIn = this.mState.activityIn;
        final int n = 0;
        int visibility;
        if (activityIn) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        mIn.setVisibility(visibility);
        final ImageView mOut = this.mOut;
        int visibility2;
        if (this.mState.activityOut) {
            visibility2 = 0;
        }
        else {
            visibility2 = 8;
        }
        mOut.setVisibility(visibility2);
        final View mInoutContainer = this.mInoutContainer;
        final StatusBarSignalPolicy.WifiIconState mState = this.mState;
        int visibility3;
        if (!mState.activityIn && !mState.activityOut) {
            visibility3 = 8;
        }
        else {
            visibility3 = 0;
        }
        mInoutContainer.setVisibility(visibility3);
        final View mAirplaneSpacer = this.mAirplaneSpacer;
        int visibility4;
        if (this.mState.airplaneSpacerVisible) {
            visibility4 = 0;
        }
        else {
            visibility4 = 8;
        }
        mAirplaneSpacer.setVisibility(visibility4);
        final View mSignalSpacer = this.mSignalSpacer;
        int visibility5;
        if (this.mState.signalSpacerVisible) {
            visibility5 = 0;
        }
        else {
            visibility5 = 8;
        }
        mSignalSpacer.setVisibility(visibility5);
        int visibility6;
        if (this.mState.visible) {
            visibility6 = n;
        }
        else {
            visibility6 = 8;
        }
        this.setVisibility(visibility6);
    }
    
    private boolean updateState(final StatusBarSignalPolicy.WifiIconState mState) {
        this.setContentDescription((CharSequence)mState.contentDescription);
        final int resId = this.mState.resId;
        final int resId2 = mState.resId;
        if (resId != resId2 && resId2 >= 0) {
            this.mWifiIcon.setImageDrawable(super.mContext.getDrawable(resId2));
        }
        final ImageView mIn = this.mIn;
        final boolean activityIn = mState.activityIn;
        final int n = 8;
        int visibility;
        if (activityIn) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        mIn.setVisibility(visibility);
        final ImageView mOut = this.mOut;
        int visibility2;
        if (mState.activityOut) {
            visibility2 = 0;
        }
        else {
            visibility2 = 8;
        }
        mOut.setVisibility(visibility2);
        final View mInoutContainer = this.mInoutContainer;
        int visibility3;
        if (!mState.activityIn && !mState.activityOut) {
            visibility3 = 8;
        }
        else {
            visibility3 = 0;
        }
        mInoutContainer.setVisibility(visibility3);
        final View mAirplaneSpacer = this.mAirplaneSpacer;
        int visibility4;
        if (mState.airplaneSpacerVisible) {
            visibility4 = 0;
        }
        else {
            visibility4 = 8;
        }
        mAirplaneSpacer.setVisibility(visibility4);
        final View mSignalSpacer = this.mSignalSpacer;
        int visibility5;
        if (mState.signalSpacerVisible) {
            visibility5 = 0;
        }
        else {
            visibility5 = 8;
        }
        mSignalSpacer.setVisibility(visibility5);
        final boolean activityIn2 = mState.activityIn;
        final StatusBarSignalPolicy.WifiIconState mState2 = this.mState;
        final boolean b = activityIn2 != mState2.activityIn || mState.activityOut != mState2.activityOut;
        final boolean visible = this.mState.visible;
        final boolean visible2 = mState.visible;
        boolean b2 = b;
        if (visible != visible2) {
            b2 = (b | true);
            int visibility6 = n;
            if (visible2) {
                visibility6 = 0;
            }
            this.setVisibility(visibility6);
        }
        this.mState = mState;
        return b2;
    }
    
    public void applyWifiState(final StatusBarSignalPolicy.WifiIconState wifiIconState) {
        final int n = 1;
        int n2 = 1;
        if (wifiIconState == null) {
            if (this.getVisibility() == 8) {
                n2 = 0;
            }
            this.setVisibility(8);
            this.mState = null;
        }
        else {
            final StatusBarSignalPolicy.WifiIconState mState = this.mState;
            if (mState == null) {
                this.mState = wifiIconState.copy();
                this.initViewState();
                n2 = n;
            }
            else {
                n2 = ((!mState.equals(wifiIconState) && this.updateState(wifiIconState.copy())) ? 1 : 0);
            }
        }
        if (n2 != 0) {
            this.requestLayout();
        }
    }
    
    public void getDrawingRect(final Rect rect) {
        super.getDrawingRect(rect);
        final float translationX = this.getTranslationX();
        final float translationY = this.getTranslationY();
        rect.left += (int)translationX;
        rect.right += (int)translationX;
        rect.top += (int)translationY;
        rect.bottom += (int)translationY;
    }
    
    public String getSlot() {
        return this.mSlot;
    }
    
    public int getVisibleState() {
        return this.mVisibleState;
    }
    
    public boolean isIconVisible() {
        final StatusBarSignalPolicy.WifiIconState mState = this.mState;
        return mState != null && mState.visible;
    }
    
    public void onDarkChanged(final Rect rect, final float n, int tint) {
        tint = DarkIconDispatcher.getTint(rect, (View)this, tint);
        final ColorStateList value = ColorStateList.valueOf(tint);
        this.mWifiIcon.setImageTintList(value);
        this.mIn.setImageTintList(value);
        this.mOut.setImageTintList(value);
        this.mDotView.setDecorColor(tint);
        this.mDotView.setIconColor(tint, false);
    }
    
    public void setDecorColor(final int decorColor) {
        this.mDotView.setDecorColor(decorColor);
    }
    
    public void setSlot(final String mSlot) {
        this.mSlot = mSlot;
    }
    
    public void setStaticDrawableColor(final int decorColor) {
        final ColorStateList value = ColorStateList.valueOf(decorColor);
        this.mWifiIcon.setImageTintList(value);
        this.mIn.setImageTintList(value);
        this.mOut.setImageTintList(value);
        this.mDotView.setDecorColor(decorColor);
    }
    
    public void setVisibleState(final int mVisibleState, final boolean b) {
        if (mVisibleState == this.mVisibleState) {
            return;
        }
        if ((this.mVisibleState = mVisibleState) != 0) {
            if (mVisibleState != 1) {
                this.mWifiGroup.setVisibility(8);
                this.mDotView.setVisibility(8);
            }
            else {
                this.mWifiGroup.setVisibility(8);
                this.mDotView.setVisibility(0);
            }
        }
        else {
            this.mWifiGroup.setVisibility(0);
            this.mDotView.setVisibility(8);
        }
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("StatusBarWifiView(slot=");
        sb.append(this.mSlot);
        sb.append(" state=");
        sb.append(this.mState);
        sb.append(")");
        return sb.toString();
    }
}
