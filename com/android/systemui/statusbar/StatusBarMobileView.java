// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.content.res.ColorStateList;
import com.android.internal.annotations.VisibleForTesting;
import android.graphics.Rect;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import com.android.systemui.R$dimen;
import android.service.notification.StatusBarNotification;
import android.graphics.drawable.Drawable;
import com.android.systemui.R$id;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.statusbar.phone.StatusBarSignalPolicy;
import android.widget.LinearLayout;
import com.android.settingslib.graph.SignalDrawable;
import android.view.View;
import android.widget.ImageView;
import com.android.systemui.DualToneHandler;
import com.android.systemui.plugins.DarkIconDispatcher;
import android.widget.FrameLayout;

public class StatusBarMobileView extends FrameLayout implements DarkReceiver, StatusIconDisplayable
{
    private StatusBarIconView mDotView;
    private DualToneHandler mDualToneHandler;
    private ImageView mIn;
    private View mInoutContainer;
    private ImageView mMobile;
    private SignalDrawable mMobileDrawable;
    private LinearLayout mMobileGroup;
    private ImageView mMobileRoaming;
    private View mMobileRoamingSpace;
    private ImageView mMobileType;
    private ImageView mOut;
    private String mSlot;
    private StatusBarSignalPolicy.MobileIconState mState;
    private int mVisibleState;
    
    public StatusBarMobileView(final Context context) {
        super(context);
        this.mVisibleState = -1;
    }
    
    public StatusBarMobileView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mVisibleState = -1;
    }
    
    public StatusBarMobileView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mVisibleState = -1;
    }
    
    public StatusBarMobileView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mVisibleState = -1;
    }
    
    public static StatusBarMobileView fromContext(final Context context, final String slot) {
        final StatusBarMobileView statusBarMobileView = (StatusBarMobileView)LayoutInflater.from(context).inflate(R$layout.status_bar_mobile_signal_group, (ViewGroup)null);
        statusBarMobileView.setSlot(slot);
        statusBarMobileView.init();
        statusBarMobileView.setVisibleState(0);
        return statusBarMobileView;
    }
    
    private void init() {
        this.mDualToneHandler = new DualToneHandler(this.getContext());
        this.mMobileGroup = (LinearLayout)this.findViewById(R$id.mobile_group);
        this.mMobile = (ImageView)this.findViewById(R$id.mobile_signal);
        this.mMobileType = (ImageView)this.findViewById(R$id.mobile_type);
        this.mMobileRoaming = (ImageView)this.findViewById(R$id.mobile_roaming);
        this.mMobileRoamingSpace = this.findViewById(R$id.mobile_roaming_space);
        this.mIn = (ImageView)this.findViewById(R$id.mobile_in);
        this.mOut = (ImageView)this.findViewById(R$id.mobile_out);
        this.mInoutContainer = this.findViewById(R$id.inout_container);
        final SignalDrawable signalDrawable = new SignalDrawable(this.getContext());
        this.mMobileDrawable = signalDrawable;
        this.mMobile.setImageDrawable((Drawable)signalDrawable);
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
        final boolean visible = this.mState.visible;
        final int n = 8;
        if (!visible) {
            this.mMobileGroup.setVisibility(8);
        }
        else {
            this.mMobileGroup.setVisibility(0);
        }
        this.mMobileDrawable.setLevel(this.mState.strengthId);
        final StatusBarSignalPolicy.MobileIconState mState = this.mState;
        if (mState.typeId > 0) {
            this.mMobileType.setContentDescription(mState.typeContentDescription);
            this.mMobileType.setImageResource(this.mState.typeId);
            this.mMobileType.setVisibility(0);
        }
        else {
            this.mMobileType.setVisibility(8);
        }
        final ImageView mMobileRoaming = this.mMobileRoaming;
        int visibility;
        if (this.mState.roaming) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        mMobileRoaming.setVisibility(visibility);
        final View mMobileRoamingSpace = this.mMobileRoamingSpace;
        int visibility2;
        if (this.mState.roaming) {
            visibility2 = 0;
        }
        else {
            visibility2 = 8;
        }
        mMobileRoamingSpace.setVisibility(visibility2);
        final ImageView mIn = this.mIn;
        int visibility3;
        if (this.mState.activityIn) {
            visibility3 = 0;
        }
        else {
            visibility3 = 8;
        }
        mIn.setVisibility(visibility3);
        final ImageView mOut = this.mOut;
        int visibility4;
        if (this.mState.activityOut) {
            visibility4 = 0;
        }
        else {
            visibility4 = 8;
        }
        mOut.setVisibility(visibility4);
        final View mInoutContainer = this.mInoutContainer;
        final StatusBarSignalPolicy.MobileIconState mState2 = this.mState;
        int visibility5 = 0;
        Label_0275: {
            if (!mState2.activityIn) {
                visibility5 = n;
                if (!mState2.activityOut) {
                    break Label_0275;
                }
            }
            visibility5 = 0;
        }
        mInoutContainer.setVisibility(visibility5);
    }
    
    private boolean updateState(final StatusBarSignalPolicy.MobileIconState mState) {
        this.setContentDescription((CharSequence)mState.contentDescription);
        final boolean visible = this.mState.visible;
        final boolean visible2 = mState.visible;
        final boolean b = true;
        final int n = 8;
        int n2;
        if (visible != visible2) {
            final LinearLayout mMobileGroup = this.mMobileGroup;
            int visibility;
            if (visible2) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            mMobileGroup.setVisibility(visibility);
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        final int strengthId = this.mState.strengthId;
        final int strengthId2 = mState.strengthId;
        if (strengthId != strengthId2) {
            this.mMobileDrawable.setLevel(strengthId2);
        }
        final int typeId = this.mState.typeId;
        final int typeId2 = mState.typeId;
        int n3 = n2;
        if (typeId != typeId2) {
            n3 = (n2 | ((typeId2 == 0 || typeId == 0) ? 1 : 0));
            if (mState.typeId != 0) {
                this.mMobileType.setContentDescription(mState.typeContentDescription);
                this.mMobileType.setImageResource(mState.typeId);
                this.mMobileType.setVisibility(0);
            }
            else {
                this.mMobileType.setVisibility(8);
            }
        }
        final ImageView mMobileRoaming = this.mMobileRoaming;
        int visibility2;
        if (mState.roaming) {
            visibility2 = 0;
        }
        else {
            visibility2 = 8;
        }
        mMobileRoaming.setVisibility(visibility2);
        final View mMobileRoamingSpace = this.mMobileRoamingSpace;
        int visibility3;
        if (mState.roaming) {
            visibility3 = 0;
        }
        else {
            visibility3 = 8;
        }
        mMobileRoamingSpace.setVisibility(visibility3);
        final ImageView mIn = this.mIn;
        int visibility4;
        if (mState.activityIn) {
            visibility4 = 0;
        }
        else {
            visibility4 = 8;
        }
        mIn.setVisibility(visibility4);
        final ImageView mOut = this.mOut;
        int visibility5;
        if (mState.activityOut) {
            visibility5 = 0;
        }
        else {
            visibility5 = 8;
        }
        mOut.setVisibility(visibility5);
        final View mInoutContainer = this.mInoutContainer;
        int visibility6 = 0;
        Label_0352: {
            if (!mState.activityIn) {
                visibility6 = n;
                if (!mState.activityOut) {
                    break Label_0352;
                }
            }
            visibility6 = 0;
        }
        mInoutContainer.setVisibility(visibility6);
        final boolean roaming = mState.roaming;
        final StatusBarSignalPolicy.MobileIconState mState2 = this.mState;
        int n4 = b ? 1 : 0;
        if (roaming == mState2.roaming) {
            n4 = (b ? 1 : 0);
            if (mState.activityIn == mState2.activityIn) {
                n4 = ((mState.activityOut != mState2.activityOut && b) ? 1 : 0);
            }
        }
        this.mState = mState;
        return (n3 | n4) != 0x0;
    }
    
    public void applyMobileState(final StatusBarSignalPolicy.MobileIconState mobileIconState) {
        final int n = 1;
        int n2 = 1;
        if (mobileIconState == null) {
            if (this.getVisibility() == 8) {
                n2 = 0;
            }
            this.setVisibility(8);
            this.mState = null;
        }
        else {
            final StatusBarSignalPolicy.MobileIconState mState = this.mState;
            if (mState == null) {
                this.mState = mobileIconState.copy();
                this.initViewState();
                n2 = n;
            }
            else {
                n2 = ((!mState.equals(mobileIconState) && this.updateState(mobileIconState.copy())) ? 1 : 0);
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
    
    @VisibleForTesting
    public StatusBarSignalPolicy.MobileIconState getState() {
        return this.mState;
    }
    
    public int getVisibleState() {
        return this.mVisibleState;
    }
    
    public boolean isIconVisible() {
        return this.mState.visible;
    }
    
    public void onDarkChanged(final Rect rect, float n, final int decorColor) {
        if (!DarkIconDispatcher.isInArea(rect, (View)this)) {
            n = 0.0f;
        }
        this.mMobileDrawable.setTintList(ColorStateList.valueOf(this.mDualToneHandler.getSingleColor(n)));
        final ColorStateList value = ColorStateList.valueOf(DarkIconDispatcher.getTint(rect, (View)this, decorColor));
        this.mIn.setImageTintList(value);
        this.mOut.setImageTintList(value);
        this.mMobileType.setImageTintList(value);
        this.mMobileRoaming.setImageTintList(value);
        this.mDotView.setDecorColor(decorColor);
        this.mDotView.setIconColor(decorColor, false);
    }
    
    public void setDecorColor(final int decorColor) {
        this.mDotView.setDecorColor(decorColor);
    }
    
    public void setSlot(final String mSlot) {
        this.mSlot = mSlot;
    }
    
    public void setStaticDrawableColor(final int decorColor) {
        final ColorStateList value = ColorStateList.valueOf(decorColor);
        float n;
        if (decorColor == -1) {
            n = 0.0f;
        }
        else {
            n = 1.0f;
        }
        this.mMobileDrawable.setTintList(ColorStateList.valueOf(this.mDualToneHandler.getSingleColor(n)));
        this.mIn.setImageTintList(value);
        this.mOut.setImageTintList(value);
        this.mMobileType.setImageTintList(value);
        this.mMobileRoaming.setImageTintList(value);
        this.mDotView.setDecorColor(decorColor);
    }
    
    public void setVisibleState(final int mVisibleState, final boolean b) {
        if (mVisibleState == this.mVisibleState) {
            return;
        }
        if ((this.mVisibleState = mVisibleState) != 0) {
            if (mVisibleState != 1) {
                this.mMobileGroup.setVisibility(4);
                this.mDotView.setVisibility(4);
            }
            else {
                this.mMobileGroup.setVisibility(4);
                this.mDotView.setVisibility(0);
            }
        }
        else {
            this.mMobileGroup.setVisibility(0);
            this.mDotView.setVisibility(8);
        }
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("StatusBarMobileView(slot=");
        sb.append(this.mSlot);
        sb.append(" state=");
        sb.append(this.mState);
        sb.append(")");
        return sb.toString();
    }
}
