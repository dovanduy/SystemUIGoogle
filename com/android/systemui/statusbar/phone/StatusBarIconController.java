// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.widget.ImageView$ScaleType;
import android.os.Bundle;
import com.android.systemui.statusbar.StatusIconDisplayable;
import android.view.ViewGroup$LayoutParams;
import android.widget.ImageView;
import com.android.systemui.statusbar.StatusBarWifiView;
import com.android.systemui.statusbar.StatusBarMobileView;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.StatusBarIconView;
import android.view.View;
import android.view.View$OnAttachStateChangeListener;
import com.android.systemui.util.Utils;
import com.android.systemui.DemoMode;
import android.widget.LinearLayout$LayoutParams;
import com.android.systemui.Dependency;
import com.android.systemui.R$dimen;
import android.view.ViewGroup;
import com.android.systemui.statusbar.CommandQueue;
import android.widget.LinearLayout;
import com.android.systemui.plugins.DarkIconDispatcher;
import java.util.List;
import com.android.internal.statusbar.StatusBarIcon;
import android.text.TextUtils;
import com.android.systemui.R$array;
import android.util.ArraySet;
import android.content.Context;

public interface StatusBarIconController
{
    default ArraySet<String> getIconBlacklist(final Context context, String s) {
        final ArraySet set = new ArraySet();
        String[] array;
        if (s == null) {
            array = context.getResources().getStringArray(R$array.config_statusBarIconBlackList);
        }
        else {
            array = s.split(",");
        }
        for (int length = array.length, i = 0; i < length; ++i) {
            s = array[i];
            if (!TextUtils.isEmpty((CharSequence)s)) {
                set.add((Object)s);
            }
        }
        return (ArraySet<String>)set;
    }
    
    void addIconGroup(final IconManager p0);
    
    void removeAllIconsForSlot(final String p0);
    
    void removeIconGroup(final IconManager p0);
    
    void setExternalIcon(final String p0);
    
    void setIcon(final String p0, final int p1, final CharSequence p2);
    
    void setIcon(final String p0, final StatusBarIcon p1);
    
    void setIconVisibility(final String p0, final boolean p1);
    
    void setMobileIcons(final String p0, final List<StatusBarSignalPolicy.MobileIconState> p1);
    
    void setSignalIcon(final String p0, final StatusBarSignalPolicy.WifiIconState p1);
    
    public static class DarkIconManager extends IconManager
    {
        private final DarkIconDispatcher mDarkIconDispatcher;
        private int mIconHPadding;
        
        public DarkIconManager(final LinearLayout linearLayout, final CommandQueue commandQueue) {
            super((ViewGroup)linearLayout, commandQueue);
            this.mIconHPadding = super.mContext.getResources().getDimensionPixelSize(R$dimen.status_bar_icon_padding);
            this.mDarkIconDispatcher = Dependency.get(DarkIconDispatcher.class);
        }
        
        @Override
        protected DemoStatusIcons createDemoStatusIcons() {
            final DemoStatusIcons demoStatusIcons = super.createDemoStatusIcons();
            this.mDarkIconDispatcher.addDarkReceiver((DarkIconDispatcher.DarkReceiver)demoStatusIcons);
            return demoStatusIcons;
        }
        
        @Override
        protected void destroy() {
            for (int i = 0; i < super.mGroup.getChildCount(); ++i) {
                this.mDarkIconDispatcher.removeDarkReceiver((DarkIconDispatcher.DarkReceiver)super.mGroup.getChildAt(i));
            }
            super.mGroup.removeAllViews();
        }
        
        @Override
        protected void exitDemoMode() {
            this.mDarkIconDispatcher.removeDarkReceiver((DarkIconDispatcher.DarkReceiver)super.mDemoStatusIcons);
            super.exitDemoMode();
        }
        
        @Override
        protected LinearLayout$LayoutParams onCreateLayoutParams() {
            final LinearLayout$LayoutParams linearLayout$LayoutParams = new LinearLayout$LayoutParams(-2, super.mIconSize);
            final int mIconHPadding = this.mIconHPadding;
            linearLayout$LayoutParams.setMargins(mIconHPadding, 0, mIconHPadding, 0);
            return linearLayout$LayoutParams;
        }
        
        @Override
        protected void onIconAdded(final int n, final String s, final boolean b, final StatusBarIconHolder statusBarIconHolder) {
            this.mDarkIconDispatcher.addDarkReceiver((DarkIconDispatcher.DarkReceiver)((IconManager)this).addHolder(n, s, b, statusBarIconHolder));
        }
        
        @Override
        protected void onRemoveIcon(final int n) {
            this.mDarkIconDispatcher.removeDarkReceiver((DarkIconDispatcher.DarkReceiver)super.mGroup.getChildAt(n));
            super.onRemoveIcon(n);
        }
        
        @Override
        public void onSetIcon(final int n, final StatusBarIcon statusBarIcon) {
            super.onSetIcon(n, statusBarIcon);
            this.mDarkIconDispatcher.applyDark((DarkIconDispatcher.DarkReceiver)super.mGroup.getChildAt(n));
        }
    }
    
    public static class IconManager implements DemoMode
    {
        protected final Context mContext;
        protected DemoStatusIcons mDemoStatusIcons;
        protected boolean mDemoable;
        protected final ViewGroup mGroup;
        protected final int mIconSize;
        private boolean mIsInDemoMode;
        protected boolean mShouldLog;
        
        public IconManager(final ViewGroup mGroup, final CommandQueue commandQueue) {
            this.mShouldLog = false;
            this.mDemoable = true;
            this.mGroup = mGroup;
            final Context context = mGroup.getContext();
            this.mContext = context;
            this.mIconSize = context.getResources().getDimensionPixelSize(17105474);
            final Utils.DisableStateTracker disableStateTracker = new Utils.DisableStateTracker(0, 2, commandQueue);
            this.mGroup.addOnAttachStateChangeListener((View$OnAttachStateChangeListener)disableStateTracker);
            if (this.mGroup.isAttachedToWindow()) {
                disableStateTracker.onViewAttachedToWindow((View)this.mGroup);
            }
        }
        
        private StatusBarIconView onCreateStatusBarIconView(final String s, final boolean b) {
            return new StatusBarIconView(this.mContext, s, null, b);
        }
        
        private StatusBarMobileView onCreateStatusBarMobileView(final String s) {
            return StatusBarMobileView.fromContext(this.mContext, s);
        }
        
        private StatusBarWifiView onCreateStatusBarWifiView(final String s) {
            return StatusBarWifiView.fromContext(this.mContext, s);
        }
        
        private void setHeightAndCenter(final ImageView imageView, final int height) {
            final ViewGroup$LayoutParams layoutParams = imageView.getLayoutParams();
            layoutParams.height = height;
            if (layoutParams instanceof LinearLayout$LayoutParams) {
                ((LinearLayout$LayoutParams)layoutParams).gravity = 16;
            }
            imageView.setLayoutParams(layoutParams);
        }
        
        protected StatusIconDisplayable addHolder(final int n, final String s, final boolean b, final StatusBarIconHolder statusBarIconHolder) {
            final int type = statusBarIconHolder.getType();
            if (type == 0) {
                return this.addIcon(n, s, b, statusBarIconHolder.getIcon());
            }
            if (type == 1) {
                return this.addSignalIcon(n, s, statusBarIconHolder.getWifiState());
            }
            if (type != 2) {
                return null;
            }
            return this.addMobileIcon(n, s, statusBarIconHolder.getMobileState());
        }
        
        protected StatusBarIconView addIcon(final int n, final String s, final boolean b, final StatusBarIcon statusBarIcon) {
            final StatusBarIconView onCreateStatusBarIconView = this.onCreateStatusBarIconView(s, b);
            onCreateStatusBarIconView.set(statusBarIcon);
            this.mGroup.addView((View)onCreateStatusBarIconView, n, (ViewGroup$LayoutParams)this.onCreateLayoutParams());
            return onCreateStatusBarIconView;
        }
        
        protected StatusBarMobileView addMobileIcon(final int n, final String s, final StatusBarSignalPolicy.MobileIconState mobileIconState) {
            final StatusBarMobileView onCreateStatusBarMobileView = this.onCreateStatusBarMobileView(s);
            onCreateStatusBarMobileView.applyMobileState(mobileIconState);
            this.mGroup.addView((View)onCreateStatusBarMobileView, n, (ViewGroup$LayoutParams)this.onCreateLayoutParams());
            if (this.mIsInDemoMode) {
                this.mDemoStatusIcons.addMobileView(mobileIconState);
            }
            return onCreateStatusBarMobileView;
        }
        
        protected StatusBarWifiView addSignalIcon(final int n, final String s, final StatusBarSignalPolicy.WifiIconState wifiIconState) {
            final StatusBarWifiView onCreateStatusBarWifiView = this.onCreateStatusBarWifiView(s);
            onCreateStatusBarWifiView.applyWifiState(wifiIconState);
            this.mGroup.addView((View)onCreateStatusBarWifiView, n, (ViewGroup$LayoutParams)this.onCreateLayoutParams());
            if (this.mIsInDemoMode) {
                this.mDemoStatusIcons.addDemoWifiView(wifiIconState);
            }
            return onCreateStatusBarWifiView;
        }
        
        protected DemoStatusIcons createDemoStatusIcons() {
            return new DemoStatusIcons((LinearLayout)this.mGroup, this.mIconSize);
        }
        
        protected void destroy() {
            this.mGroup.removeAllViews();
        }
        
        @Override
        public void dispatchDemoCommand(final String s, final Bundle bundle) {
            if (!this.mDemoable) {
                return;
            }
            if (s.equals("exit")) {
                final DemoStatusIcons mDemoStatusIcons = this.mDemoStatusIcons;
                if (mDemoStatusIcons != null) {
                    mDemoStatusIcons.dispatchDemoCommand(s, bundle);
                    this.exitDemoMode();
                }
                this.mIsInDemoMode = false;
            }
            else {
                if (this.mDemoStatusIcons == null) {
                    this.mIsInDemoMode = true;
                    this.mDemoStatusIcons = this.createDemoStatusIcons();
                }
                this.mDemoStatusIcons.dispatchDemoCommand(s, bundle);
            }
        }
        
        protected void exitDemoMode() {
            this.mDemoStatusIcons.remove();
            this.mDemoStatusIcons = null;
        }
        
        public boolean isDemoable() {
            return this.mDemoable;
        }
        
        protected LinearLayout$LayoutParams onCreateLayoutParams() {
            return new LinearLayout$LayoutParams(-2, this.mIconSize);
        }
        
        protected void onIconAdded(final int n, final String s, final boolean b, final StatusBarIconHolder statusBarIconHolder) {
            this.addHolder(n, s, b, statusBarIconHolder);
        }
        
        protected void onIconExternal(final int n, final int n2) {
            final ImageView imageView = (ImageView)this.mGroup.getChildAt(n);
            imageView.setScaleType(ImageView$ScaleType.FIT_CENTER);
            imageView.setAdjustViewBounds(true);
            this.setHeightAndCenter(imageView, n2);
        }
        
        protected void onRemoveIcon(final int n) {
            if (this.mIsInDemoMode) {
                this.mDemoStatusIcons.onRemoveIcon((StatusIconDisplayable)this.mGroup.getChildAt(n));
            }
            this.mGroup.removeViewAt(n);
        }
        
        public void onSetIcon(final int n, final StatusBarIcon statusBarIcon) {
            ((StatusBarIconView)this.mGroup.getChildAt(n)).set(statusBarIcon);
        }
        
        public void onSetIconHolder(final int n, final StatusBarIconHolder statusBarIconHolder) {
            final int type = statusBarIconHolder.getType();
            if (type == 0) {
                this.onSetIcon(n, statusBarIconHolder.getIcon());
                return;
            }
            if (type != 1) {
                if (type == 2) {
                    this.onSetMobileIcon(n, statusBarIconHolder.getMobileState());
                }
                return;
            }
            this.onSetSignalIcon(n, statusBarIconHolder.getWifiState());
        }
        
        public void onSetMobileIcon(final int n, final StatusBarSignalPolicy.MobileIconState mobileIconState) {
            final StatusBarMobileView statusBarMobileView = (StatusBarMobileView)this.mGroup.getChildAt(n);
            if (statusBarMobileView != null) {
                statusBarMobileView.applyMobileState(mobileIconState);
            }
            if (this.mIsInDemoMode) {
                this.mDemoStatusIcons.updateMobileState(mobileIconState);
            }
        }
        
        public void onSetSignalIcon(final int n, final StatusBarSignalPolicy.WifiIconState wifiIconState) {
            final StatusBarWifiView statusBarWifiView = (StatusBarWifiView)this.mGroup.getChildAt(n);
            if (statusBarWifiView != null) {
                statusBarWifiView.applyWifiState(wifiIconState);
            }
            if (this.mIsInDemoMode) {
                this.mDemoStatusIcons.updateWifiState(wifiIconState);
            }
        }
        
        public void setShouldLog(final boolean mShouldLog) {
            this.mShouldLog = mShouldLog;
        }
        
        public boolean shouldLog() {
            return this.mShouldLog;
        }
    }
    
    public static class TintedIconManager extends IconManager
    {
        private int mColor;
        
        public TintedIconManager(final ViewGroup viewGroup, final CommandQueue commandQueue) {
            super(viewGroup, commandQueue);
        }
        
        @Override
        protected DemoStatusIcons createDemoStatusIcons() {
            final DemoStatusIcons demoStatusIcons = super.createDemoStatusIcons();
            demoStatusIcons.setColor(this.mColor);
            return demoStatusIcons;
        }
        
        @Override
        protected void onIconAdded(final int n, final String s, final boolean b, final StatusBarIconHolder statusBarIconHolder) {
            final StatusIconDisplayable addHolder = ((IconManager)this).addHolder(n, s, b, statusBarIconHolder);
            addHolder.setStaticDrawableColor(this.mColor);
            addHolder.setDecorColor(this.mColor);
        }
        
        public void setTint(int i) {
            this.mColor = i;
            View child;
            StatusIconDisplayable statusIconDisplayable;
            for (i = 0; i < super.mGroup.getChildCount(); ++i) {
                child = super.mGroup.getChildAt(i);
                if (child instanceof StatusIconDisplayable) {
                    statusIconDisplayable = (StatusIconDisplayable)child;
                    statusIconDisplayable.setStaticDrawableColor(this.mColor);
                    statusIconDisplayable.setDecorColor(this.mColor);
                }
            }
        }
    }
}
