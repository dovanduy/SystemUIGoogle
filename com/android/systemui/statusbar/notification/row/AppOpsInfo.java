// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.view.accessibility.AccessibilityEvent;
import com.android.systemui.R$string;
import android.view.View;
import android.graphics.drawable.Drawable;
import android.content.pm.ApplicationInfo;
import android.widget.ImageView;
import android.content.pm.PackageManager$NameNotFoundException;
import android.widget.TextView;
import com.android.systemui.R$id;
import android.util.AttributeSet;
import android.content.Context;
import android.service.notification.StatusBarNotification;
import android.content.pm.PackageManager;
import android.view.View$OnClickListener;
import com.android.internal.logging.MetricsLogger;
import android.util.ArraySet;
import android.widget.LinearLayout;

public class AppOpsInfo extends LinearLayout implements GutsContent
{
    private String mAppName;
    private ArraySet<Integer> mAppOps;
    private int mAppUid;
    private NotificationGuts mGutsContainer;
    private MetricsLogger mMetricsLogger;
    private View$OnClickListener mOnOk;
    private OnSettingsClickListener mOnSettingsClickListener;
    private String mPkg;
    private PackageManager mPm;
    private StatusBarNotification mSbn;
    
    public AppOpsInfo(final Context context, final AttributeSet set) {
        super(context, set);
        this.mOnOk = (View$OnClickListener)new _$$Lambda$AppOpsInfo$zS48CwL7b6UcUOuxgx7Zkw4dC1A(this);
    }
    
    private void bindButtons() {
        this.findViewById(R$id.settings).setOnClickListener((View$OnClickListener)new _$$Lambda$AppOpsInfo$MC_PUe5w52BX3b0kt9URHDzbSUA(this));
        ((TextView)this.findViewById(R$id.ok)).setOnClickListener(this.mOnOk);
    }
    
    private void bindHeader() {
        Drawable imageDrawable;
        try {
            final ApplicationInfo applicationInfo = this.mPm.getApplicationInfo(this.mPkg, 795136);
            if (applicationInfo != null) {
                this.mAppUid = this.mSbn.getUid();
                this.mAppName = String.valueOf(this.mPm.getApplicationLabel(applicationInfo));
                imageDrawable = this.mPm.getApplicationIcon(applicationInfo);
            }
            else {
                imageDrawable = null;
            }
        }
        catch (PackageManager$NameNotFoundException ex) {
            imageDrawable = this.mPm.getDefaultActivityIcon();
        }
        ((ImageView)this.findViewById(R$id.pkgicon)).setImageDrawable(imageDrawable);
        ((TextView)this.findViewById(R$id.pkgname)).setText((CharSequence)this.mAppName);
    }
    
    private void bindPrompt() {
        ((TextView)this.findViewById(R$id.prompt)).setText((CharSequence)this.getPrompt());
    }
    
    private void closeControls(final View view) {
        this.mMetricsLogger.visibility(1345, false);
        final int[] array = new int[2];
        final int[] array2 = new int[2];
        this.mGutsContainer.getLocationOnScreen(array);
        view.getLocationOnScreen(array2);
        this.mGutsContainer.closeControls(array2[0] - array[0] + view.getWidth() / 2, array2[1] - array[1] + view.getHeight() / 2, false, false);
    }
    
    private String getPrompt() {
        final ArraySet<Integer> mAppOps = this.mAppOps;
        if (mAppOps == null || mAppOps.size() == 0) {
            return "";
        }
        if (this.mAppOps.size() == 1) {
            if (this.mAppOps.contains((Object)26)) {
                return super.mContext.getString(R$string.appops_camera);
            }
            if (this.mAppOps.contains((Object)27)) {
                return super.mContext.getString(R$string.appops_microphone);
            }
            return super.mContext.getString(R$string.appops_overlay);
        }
        else {
            if (this.mAppOps.size() != 2) {
                return super.mContext.getString(R$string.appops_camera_mic_overlay);
            }
            if (!this.mAppOps.contains((Object)26)) {
                return super.mContext.getString(R$string.appops_mic_overlay);
            }
            if (this.mAppOps.contains((Object)27)) {
                return super.mContext.getString(R$string.appops_camera_mic);
            }
            return super.mContext.getString(R$string.appops_camera_overlay);
        }
    }
    
    public void bindGuts(final PackageManager mPm, final OnSettingsClickListener mOnSettingsClickListener, final StatusBarNotification mSbn, final ArraySet<Integer> mAppOps) {
        final String packageName = mSbn.getPackageName();
        this.mPkg = packageName;
        this.mSbn = mSbn;
        this.mPm = mPm;
        this.mAppName = packageName;
        this.mOnSettingsClickListener = mOnSettingsClickListener;
        this.mAppOps = mAppOps;
        this.bindHeader();
        this.bindPrompt();
        this.bindButtons();
        (this.mMetricsLogger = new MetricsLogger()).visibility(1345, true);
    }
    
    public int getActualHeight() {
        return this.getHeight();
    }
    
    public View getContentView() {
        return (View)this;
    }
    
    public boolean handleCloseControls(final boolean b, final boolean b2) {
        return false;
    }
    
    public void onInitializeAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        if (this.mGutsContainer != null && accessibilityEvent.getEventType() == 32) {
            if (this.mGutsContainer.isExposed()) {
                accessibilityEvent.getText().add(super.mContext.getString(R$string.notification_channel_controls_opened_accessibility, new Object[] { this.mAppName }));
            }
            else {
                accessibilityEvent.getText().add(super.mContext.getString(R$string.notification_channel_controls_closed_accessibility, new Object[] { this.mAppName }));
            }
        }
    }
    
    public void setGutsParent(final NotificationGuts mGutsContainer) {
        this.mGutsContainer = mGutsContainer;
    }
    
    public boolean shouldBeSaved() {
        return false;
    }
    
    public boolean willBeRemoved() {
        return false;
    }
    
    public interface OnSettingsClickListener
    {
        void onClick(final View p0, final String p1, final int p2, final ArraySet<Integer> p3);
    }
}
