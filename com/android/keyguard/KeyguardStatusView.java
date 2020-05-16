// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.content.res.Resources;
import android.text.format.DateFormat;
import com.android.systemui.R$string;
import java.util.Locale;
import android.view.View$AccessibilityDelegate;
import android.view.View$OnClickListener;
import com.android.systemui.R$id;
import android.widget.LinearLayout;
import android.content.IntentFilter;
import java.io.Serializable;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import androidx.core.graphics.ColorUtils;
import android.text.TextUtils;
import com.android.systemui.Dependency;
import android.view.ViewGroup$LayoutParams;
import android.view.ViewGroup$MarginLayoutParams;
import android.os.RemoteException;
import android.util.Log;
import android.app.IStopUserCallback;
import com.android.systemui.R$dimen;
import android.app.ActivityManager;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.content.Intent;
import com.android.systemui.shared.system.SurfaceViewRequestReceiver;
import java.util.TimeZone;
import android.util.AttributeSet;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.view.View;
import android.widget.TextView;
import com.android.internal.widget.LockPatternUtils;
import android.app.IActivityManager;
import android.os.Handler;
import com.android.systemui.statusbar.policy.ConfigurationController;
import android.widget.GridLayout;

public class KeyguardStatusView extends GridLayout implements ConfigurationListener
{
    private KeyguardClockSwitch mClockView;
    private float mDarkAmount;
    private Handler mHandler;
    private final IActivityManager mIActivityManager;
    private int mIconTopMargin;
    private int mIconTopMarginWithHeader;
    private KeyguardUpdateMonitorCallback mInfoCallback;
    private KeyguardSliceView mKeyguardSlice;
    private final LockPatternUtils mLockPatternUtils;
    private TextView mLogoutView;
    private View mNotificationIcons;
    private TextView mOwnerInfo;
    private Runnable mPendingMarqueeStart;
    private boolean mPulsing;
    private boolean mShowingHeader;
    private int mTextColor;
    private final BroadcastReceiver mUniversalSmartspaceBroadcastReceiver;
    
    public KeyguardStatusView(final Context context) {
        this(context, null, 0);
    }
    
    public KeyguardStatusView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public KeyguardStatusView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mDarkAmount = 0.0f;
        this.mInfoCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onFinishedGoingToSleep(final int n) {
                KeyguardStatusView.this.setEnableMarquee(false);
            }
            
            @Override
            public void onKeyguardVisibilityChanged(final boolean b) {
                if (b) {
                    KeyguardStatusView.this.refreshTime();
                    KeyguardStatusView.this.updateOwnerInfo();
                    KeyguardStatusView.this.updateLogoutView();
                }
            }
            
            @Override
            public void onLogoutEnabledChanged() {
                KeyguardStatusView.this.updateLogoutView();
            }
            
            @Override
            public void onStartedWakingUp() {
                KeyguardStatusView.this.setEnableMarquee(true);
            }
            
            @Override
            public void onTimeChanged() {
                KeyguardStatusView.this.refreshTime();
            }
            
            @Override
            public void onTimeZoneChanged(final TimeZone timeZone) {
                KeyguardStatusView.this.updateTimeZone(timeZone);
            }
            
            @Override
            public void onUserSwitchComplete(final int n) {
                KeyguardStatusView.this.refreshFormat();
                KeyguardStatusView.this.updateOwnerInfo();
                KeyguardStatusView.this.updateLogoutView();
            }
        };
        this.mUniversalSmartspaceBroadcastReceiver = new BroadcastReceiver() {
            private final SurfaceViewRequestReceiver mReceiver = new SurfaceViewRequestReceiver();
            
            public void onReceive(final Context context, final Intent intent) {
                if ("com.android.systemui.REQUEST_SMARTSPACE_VIEW".equals(intent.getAction())) {
                    this.mReceiver.onReceive(context, intent.getBundleExtra("bundle_key"), View.inflate(KeyguardStatusView.this.mContext, R$layout.keyguard_status_area, (ViewGroup)null));
                }
            }
        };
        this.mIActivityManager = ActivityManager.getService();
        this.mLockPatternUtils = new LockPatternUtils(this.getContext());
        this.mHandler = new Handler();
        this.onDensityOrFontScaleChanged();
    }
    
    private void layoutOwnerInfo() {
        final TextView mOwnerInfo = this.mOwnerInfo;
        if (mOwnerInfo != null && mOwnerInfo.getVisibility() != 8) {
            this.mOwnerInfo.setAlpha(1.0f - this.mDarkAmount);
            final int scrollY = (int)((this.mOwnerInfo.getBottom() + this.mOwnerInfo.getPaddingBottom() - (this.mOwnerInfo.getTop() - this.mOwnerInfo.getPaddingTop())) * this.mDarkAmount);
            this.setBottom(this.getMeasuredHeight() - scrollY);
            final View mNotificationIcons = this.mNotificationIcons;
            if (mNotificationIcons != null) {
                mNotificationIcons.setScrollY(scrollY);
            }
        }
        else {
            final View mNotificationIcons2 = this.mNotificationIcons;
            if (mNotificationIcons2 != null) {
                mNotificationIcons2.setScrollY(0);
            }
        }
    }
    
    private void loadBottomMargin() {
        this.mIconTopMargin = this.getResources().getDimensionPixelSize(R$dimen.widget_vertical_padding);
        this.mIconTopMarginWithHeader = this.getResources().getDimensionPixelSize(R$dimen.widget_vertical_padding_with_header);
    }
    
    private void onLogoutClicked(final View view) {
        final int currentUser = KeyguardUpdateMonitor.getCurrentUser();
        try {
            this.mIActivityManager.switchUser(0);
            this.mIActivityManager.stopUser(currentUser, true, (IStopUserCallback)null);
        }
        catch (RemoteException ex) {
            Log.e("KeyguardStatusView", "Failed to logout user", (Throwable)ex);
        }
    }
    
    private void onSliceContentChanged() {
        final boolean hasHeader = this.mKeyguardSlice.hasHeader();
        this.mClockView.setKeyguardShowingHeader(hasHeader);
        if (this.mShowingHeader == hasHeader) {
            return;
        }
        this.mShowingHeader = hasHeader;
        final View mNotificationIcons = this.mNotificationIcons;
        if (mNotificationIcons != null) {
            final ViewGroup$MarginLayoutParams layoutParams = (ViewGroup$MarginLayoutParams)mNotificationIcons.getLayoutParams();
            final int leftMargin = layoutParams.leftMargin;
            int n;
            if (hasHeader) {
                n = this.mIconTopMarginWithHeader;
            }
            else {
                n = this.mIconTopMargin;
            }
            layoutParams.setMargins(leftMargin, n, layoutParams.rightMargin, layoutParams.bottomMargin);
            this.mNotificationIcons.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
        }
    }
    
    private void refreshFormat() {
        Patterns.update(super.mContext);
        this.mClockView.setFormat12Hour(Patterns.clockView12);
        this.mClockView.setFormat24Hour(Patterns.clockView24);
    }
    
    private void refreshTime() {
        this.mClockView.refresh();
    }
    
    private void setEnableMarquee(final boolean b) {
        if (b) {
            if (this.mPendingMarqueeStart == null) {
                final _$$Lambda$KeyguardStatusView$ps9yj97ShIVR2u2hJB8SKuKk_kQ mPendingMarqueeStart = new _$$Lambda$KeyguardStatusView$ps9yj97ShIVR2u2hJB8SKuKk_kQ(this);
                this.mPendingMarqueeStart = mPendingMarqueeStart;
                this.mHandler.postDelayed((Runnable)mPendingMarqueeStart, 2000L);
            }
        }
        else {
            final Runnable mPendingMarqueeStart2 = this.mPendingMarqueeStart;
            if (mPendingMarqueeStart2 != null) {
                this.mHandler.removeCallbacks(mPendingMarqueeStart2);
                this.mPendingMarqueeStart = null;
            }
            this.setEnableMarqueeImpl(false);
        }
    }
    
    private void setEnableMarqueeImpl(final boolean selected) {
        final TextView mOwnerInfo = this.mOwnerInfo;
        if (mOwnerInfo != null) {
            mOwnerInfo.setSelected(selected);
        }
    }
    
    private boolean shouldShowLogout() {
        return Dependency.get(KeyguardUpdateMonitor.class).isLogoutEnabled() && KeyguardUpdateMonitor.getCurrentUser() != 0;
    }
    
    private void updateDark() {
        final float mDarkAmount = this.mDarkAmount;
        float alpha = 1.0f;
        final int n = 0;
        final boolean b = mDarkAmount == 1.0f;
        final TextView mLogoutView = this.mLogoutView;
        if (mLogoutView != null) {
            if (b) {
                alpha = 0.0f;
            }
            mLogoutView.setAlpha(alpha);
        }
        final TextView mOwnerInfo = this.mOwnerInfo;
        if (mOwnerInfo != null) {
            final boolean empty = TextUtils.isEmpty(mOwnerInfo.getText());
            final TextView mOwnerInfo2 = this.mOwnerInfo;
            int visibility;
            if (empty ^ true) {
                visibility = n;
            }
            else {
                visibility = 8;
            }
            mOwnerInfo2.setVisibility(visibility);
            this.layoutOwnerInfo();
        }
        final int blendARGB = ColorUtils.blendARGB(this.mTextColor, -1, this.mDarkAmount);
        this.mKeyguardSlice.setDarkAmount(this.mDarkAmount);
        this.mClockView.setTextColor(blendARGB);
    }
    
    private void updateLogoutView() {
        final TextView mLogoutView = this.mLogoutView;
        if (mLogoutView == null) {
            return;
        }
        int visibility;
        if (this.shouldShowLogout()) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        mLogoutView.setVisibility(visibility);
        this.mLogoutView.setText((CharSequence)super.mContext.getResources().getString(17040245));
    }
    
    private void updateOwnerInfo() {
        if (this.mOwnerInfo == null) {
            return;
        }
        final String deviceOwnerInfo = this.mLockPatternUtils.getDeviceOwnerInfo();
        String ownerInfo;
        if ((ownerInfo = deviceOwnerInfo) == null) {
            ownerInfo = deviceOwnerInfo;
            if (this.mLockPatternUtils.isOwnerInfoEnabled(KeyguardUpdateMonitor.getCurrentUser())) {
                ownerInfo = this.mLockPatternUtils.getOwnerInfo(KeyguardUpdateMonitor.getCurrentUser());
            }
        }
        this.mOwnerInfo.setText((CharSequence)ownerInfo);
        this.updateDark();
    }
    
    private void updateTimeZone(final TimeZone timeZone) {
        this.mClockView.onTimeZoneChanged(timeZone);
    }
    
    public void dozeTimeTick() {
        this.refreshTime();
        this.mKeyguardSlice.refresh();
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("KeyguardStatusView:");
        final StringBuilder sb = new StringBuilder();
        sb.append("  mOwnerInfo: ");
        final TextView mOwnerInfo = this.mOwnerInfo;
        final boolean b = true;
        Serializable value;
        if (mOwnerInfo == null) {
            value = "null";
        }
        else {
            value = (mOwnerInfo.getVisibility() == 0);
        }
        sb.append(value);
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("  mPulsing: ");
        sb2.append(this.mPulsing);
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("  mDarkAmount: ");
        sb3.append(this.mDarkAmount);
        printWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("  mTextColor: ");
        sb4.append(Integer.toHexString(this.mTextColor));
        printWriter.println(sb4.toString());
        if (this.mLogoutView != null) {
            final StringBuilder sb5 = new StringBuilder();
            sb5.append("  logout visible: ");
            sb5.append(this.mLogoutView.getVisibility() == 0 && b);
            printWriter.println(sb5.toString());
        }
        final KeyguardClockSwitch mClockView = this.mClockView;
        if (mClockView != null) {
            mClockView.dump(fileDescriptor, printWriter, array);
        }
        final KeyguardSliceView mKeyguardSlice = this.mKeyguardSlice;
        if (mKeyguardSlice != null) {
            mKeyguardSlice.dump(fileDescriptor, printWriter, array);
        }
    }
    
    public int getClockPreferredY(final int n) {
        return this.mClockView.getPreferredY(n);
    }
    
    public float getClockTextSize() {
        return this.mClockView.getTextSize();
    }
    
    public int getLogoutButtonHeight() {
        final TextView mLogoutView = this.mLogoutView;
        int height = 0;
        if (mLogoutView == null) {
            return 0;
        }
        if (mLogoutView.getVisibility() == 0) {
            height = this.mLogoutView.getHeight();
        }
        return height;
    }
    
    public boolean hasCustomClock() {
        return this.mClockView.hasCustomClock();
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Dependency.get(KeyguardUpdateMonitor.class).registerCallback(this.mInfoCallback);
        Dependency.get(ConfigurationController.class).addCallback((ConfigurationController.ConfigurationListener)this);
        this.getContext().registerReceiver(this.mUniversalSmartspaceBroadcastReceiver, new IntentFilter("com.android.systemui.REQUEST_SMARTSPACE_VIEW"));
    }
    
    public void onDensityOrFontScaleChanged() {
        final KeyguardClockSwitch mClockView = this.mClockView;
        if (mClockView != null) {
            mClockView.setTextSize(0, (float)this.getResources().getDimensionPixelSize(R$dimen.widget_big_font_size));
        }
        final TextView mOwnerInfo = this.mOwnerInfo;
        if (mOwnerInfo != null) {
            mOwnerInfo.setTextSize(0, (float)this.getResources().getDimensionPixelSize(R$dimen.widget_label_font_size));
        }
        this.loadBottomMargin();
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Dependency.get(KeyguardUpdateMonitor.class).removeCallback(this.mInfoCallback);
        Dependency.get(ConfigurationController.class).removeCallback((ConfigurationController.ConfigurationListener)this);
        this.getContext().unregisterReceiver(this.mUniversalSmartspaceBroadcastReceiver);
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        final LinearLayout linearLayout = (LinearLayout)this.findViewById(R$id.status_view_container);
        this.mLogoutView = (TextView)this.findViewById(R$id.logout);
        this.mNotificationIcons = this.findViewById(R$id.clock_notification_icon_container);
        final TextView mLogoutView = this.mLogoutView;
        if (mLogoutView != null) {
            mLogoutView.setOnClickListener((View$OnClickListener)new _$$Lambda$KeyguardStatusView$Pryio69yVoRI9F153p5QiMZe_bw(this));
        }
        (this.mClockView = (KeyguardClockSwitch)this.findViewById(R$id.keyguard_clock_container)).setShowCurrentUserTime(true);
        if (KeyguardClockAccessibilityDelegate.isNeeded(super.mContext)) {
            this.mClockView.setAccessibilityDelegate((View$AccessibilityDelegate)new KeyguardClockAccessibilityDelegate(super.mContext));
        }
        this.mOwnerInfo = (TextView)this.findViewById(R$id.owner_info);
        this.mKeyguardSlice = (KeyguardSliceView)this.findViewById(R$id.keyguard_status_area);
        this.mTextColor = this.mClockView.getCurrentTextColor();
        this.mKeyguardSlice.setContentChangeListener(new _$$Lambda$KeyguardStatusView$Xo7rGDTjuOiD9nJpe80IUZ1ddFw(this));
        this.onSliceContentChanged();
        this.setEnableMarquee(Dependency.get(KeyguardUpdateMonitor.class).isDeviceInteractive());
        this.refreshFormat();
        this.updateOwnerInfo();
        this.updateLogoutView();
        this.updateDark();
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        this.layoutOwnerInfo();
    }
    
    public void onLocaleListChanged() {
        this.refreshFormat();
    }
    
    public void setDarkAmount(final float n) {
        if (this.mDarkAmount == n) {
            return;
        }
        this.mDarkAmount = n;
        this.mClockView.setDarkAmount(n);
        this.updateDark();
    }
    
    public void setHasVisibleNotifications(final boolean hasVisibleNotifications) {
        this.mClockView.setHasVisibleNotifications(hasVisibleNotifications);
    }
    
    public void setPulsing(final boolean mPulsing) {
        if (this.mPulsing == mPulsing) {
            return;
        }
        this.mPulsing = mPulsing;
    }
    
    private static final class Patterns
    {
        static String cacheKey;
        static String clockView12;
        static String clockView24;
        
        static void update(final Context context) {
            final Locale default1 = Locale.getDefault();
            final Resources resources = context.getResources();
            final String string = resources.getString(R$string.clock_12hr_format);
            final String string2 = resources.getString(R$string.clock_24hr_format);
            final StringBuilder sb = new StringBuilder();
            sb.append(default1.toString());
            sb.append(string);
            sb.append(string2);
            final String string3 = sb.toString();
            if (string3.equals(Patterns.cacheKey)) {
                return;
            }
            Patterns.clockView12 = DateFormat.getBestDateTimePattern(default1, string);
            if (!string.contains("a")) {
                Patterns.clockView12 = Patterns.clockView12.replaceAll("a", "").trim();
            }
            Patterns.clockView24 = (Patterns.clockView24 = DateFormat.getBestDateTimePattern(default1, string2)).replace(':', '\uee01');
            Patterns.clockView12 = Patterns.clockView12.replace(':', '\uee01');
            Patterns.cacheKey = string3;
        }
    }
}
