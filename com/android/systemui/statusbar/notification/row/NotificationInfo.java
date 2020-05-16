// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.os.Handler;
import com.android.systemui.Dependency;
import android.os.Looper;
import android.metrics.LogMaker;
import android.content.pm.ActivityInfo;
import java.util.List;
import android.content.pm.ResolveInfo;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.widget.ImageView;
import android.content.pm.PackageManager$NameNotFoundException;
import android.app.NotificationChannelGroup;
import android.text.TextUtils;
import android.os.RemoteException;
import android.view.View;
import com.android.systemui.R$string;
import com.android.systemui.R$id;
import android.view.ViewGroup;
import android.transition.TransitionManager;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import java.util.Set;
import com.android.internal.annotations.VisibleForTesting;
import android.app.NotificationChannel;
import android.service.notification.StatusBarNotification;
import android.widget.TextView;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.View$OnClickListener;
import com.android.internal.logging.MetricsLogger;
import android.app.INotificationManager;
import android.widget.LinearLayout;

public class NotificationInfo extends LinearLayout implements GutsContent
{
    private String mAppName;
    private OnAppSettingsClickListener mAppSettingsClickListener;
    private int mAppUid;
    private ChannelEditorDialogController mChannelEditorDialogController;
    private Integer mChosenImportance;
    private String mDelegatePkg;
    private NotificationGuts mGutsContainer;
    private INotificationManager mINotificationManager;
    private boolean mIsDeviceProvisioned;
    private boolean mIsNonblockable;
    private boolean mIsSingleDefaultChannel;
    private MetricsLogger mMetricsLogger;
    private int mNumUniqueChannelsInRow;
    private View$OnClickListener mOnAlert;
    private View$OnClickListener mOnDismissSettings;
    private OnSettingsClickListener mOnSettingsClickListener;
    private View$OnClickListener mOnSilent;
    private String mPackageName;
    private Drawable mPkgIcon;
    private PackageManager mPm;
    private boolean mPresentingChannelEditorDialog;
    private boolean mPressedApply;
    private TextView mPriorityDescriptionView;
    private StatusBarNotification mSbn;
    private TextView mSilentDescriptionView;
    private NotificationChannel mSingleNotificationChannel;
    @VisibleForTesting
    boolean mSkipPost;
    private int mStartingChannelImportance;
    private Set<NotificationChannel> mUniqueChannelsInRow;
    private VisualStabilityManager mVisualStabilityManager;
    private boolean mWasShownHighPriority;
    
    public NotificationInfo(final Context context, final AttributeSet set) {
        super(context, set);
        this.mPresentingChannelEditorDialog = false;
        this.mSkipPost = false;
        this.mOnAlert = (View$OnClickListener)new _$$Lambda$NotificationInfo$_lxdNUTZhRsTq1qLdFuCftTaKsI(this);
        this.mOnSilent = (View$OnClickListener)new _$$Lambda$NotificationInfo$x1Q8n0IIdzsrzqhyaxjftYvWg5M(this);
        this.mOnDismissSettings = (View$OnClickListener)new _$$Lambda$NotificationInfo$p3qjyEUB89vA_NRs8XRVogtSM4k(this);
    }
    
    private void applyAlertingBehavior(int n, final boolean b) {
        final int n2 = 1;
        if (b) {
            final TransitionSet set = new TransitionSet();
            set.setOrdering(0);
            set.addTransition((Transition)new Fade(2)).addTransition((Transition)new ChangeBounds()).addTransition(new Fade(1).setStartDelay(150L).setDuration(200L).setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN));
            set.setDuration(350L);
            set.setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN);
            TransitionManager.beginDelayedTransition((ViewGroup)this, (Transition)set);
        }
        final View viewById = this.findViewById(R$id.alert);
        final View viewById2 = this.findViewById(R$id.silence);
        if (n != 0) {
            if (n != 1) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Unrecognized alerting behavior: ");
                sb.append(n);
                throw new IllegalArgumentException(sb.toString());
            }
            this.mSilentDescriptionView.setVisibility(0);
            this.mPriorityDescriptionView.setVisibility(8);
            this.post(new _$$Lambda$NotificationInfo$dODWYeWO6slLdAhUkqkR2B6YC2k(viewById, viewById2));
        }
        else {
            this.mPriorityDescriptionView.setVisibility(0);
            this.mSilentDescriptionView.setVisibility(8);
            this.post(new _$$Lambda$NotificationInfo$1cdINfsJSKDrnEWwLGSoTsBT_Kw(viewById, viewById2));
        }
        if (this.mWasShownHighPriority != (n == 0)) {
            n = n2;
        }
        else {
            n = 0;
        }
        final TextView textView = (TextView)this.findViewById(R$id.done);
        if (n != 0) {
            n = R$string.inline_ok_button;
        }
        else {
            n = R$string.inline_done_button;
        }
        textView.setText(n);
    }
    
    private void bindChannelDetails() throws RemoteException {
        this.bindName();
        this.bindGroup();
    }
    
    private void bindDelegate() {
        final TextView textView = (TextView)this.findViewById(R$id.delegate_name);
        if (!TextUtils.equals((CharSequence)this.mPackageName, (CharSequence)this.mDelegatePkg)) {
            textView.setVisibility(0);
        }
        else {
            textView.setVisibility(8);
        }
    }
    
    private void bindGroup() throws RemoteException {
        final NotificationChannel mSingleNotificationChannel = this.mSingleNotificationChannel;
        CharSequence name = null;
        Label_0055: {
            if (mSingleNotificationChannel != null && mSingleNotificationChannel.getGroup() != null) {
                final NotificationChannelGroup notificationChannelGroupForPackage = this.mINotificationManager.getNotificationChannelGroupForPackage(this.mSingleNotificationChannel.getGroup(), this.mPackageName, this.mAppUid);
                if (notificationChannelGroupForPackage != null) {
                    name = notificationChannelGroupForPackage.getName();
                    break Label_0055;
                }
            }
            name = null;
        }
        final TextView textView = (TextView)this.findViewById(R$id.group_name);
        final View viewById = this.findViewById(R$id.group_divider);
        if (name != null) {
            textView.setText(name);
            textView.setVisibility(0);
            viewById.setVisibility(0);
        }
        else {
            textView.setVisibility(8);
            viewById.setVisibility(8);
        }
    }
    
    private void bindHeader() {
        this.mPkgIcon = null;
        try {
            final ApplicationInfo applicationInfo = this.mPm.getApplicationInfo(this.mPackageName, 795136);
            if (applicationInfo != null) {
                this.mAppName = String.valueOf(this.mPm.getApplicationLabel(applicationInfo));
                this.mPkgIcon = this.mPm.getApplicationIcon(applicationInfo);
            }
        }
        catch (PackageManager$NameNotFoundException ex) {
            this.mPkgIcon = this.mPm.getDefaultActivityIcon();
        }
        ((ImageView)this.findViewById(R$id.pkg_icon)).setImageDrawable(this.mPkgIcon);
        ((TextView)this.findViewById(R$id.pkg_name)).setText((CharSequence)this.mAppName);
        this.bindDelegate();
        final View viewById = this.findViewById(R$id.app_settings);
        final Intent appSettingsIntent = this.getAppSettingsIntent(this.mPm, this.mPackageName, this.mSingleNotificationChannel, this.mSbn.getId(), this.mSbn.getTag());
        int visibility = 0;
        if (appSettingsIntent != null && !TextUtils.isEmpty(this.mSbn.getNotification().getSettingsText())) {
            viewById.setVisibility(0);
            viewById.setOnClickListener((View$OnClickListener)new _$$Lambda$NotificationInfo$1n0u5clDG1rrcb2QJPV4T7x9OY0(this, appSettingsIntent));
        }
        else {
            viewById.setVisibility(8);
        }
        final View viewById2 = this.findViewById(R$id.info);
        viewById2.setOnClickListener(this.getSettingsOnClickListener());
        if (!viewById2.hasOnClickListeners()) {
            visibility = 8;
        }
        viewById2.setVisibility(visibility);
    }
    
    private void bindInlineControls() {
        final boolean mIsNonblockable = this.mIsNonblockable;
        final int n = 8;
        if (mIsNonblockable) {
            this.findViewById(R$id.non_configurable_text).setVisibility(0);
            this.findViewById(R$id.non_configurable_multichannel_text).setVisibility(8);
            this.findViewById(R$id.interruptiveness_settings).setVisibility(8);
            ((TextView)this.findViewById(R$id.done)).setText(R$string.inline_done_button);
            this.findViewById(R$id.turn_off_notifications).setVisibility(8);
        }
        else if (this.mNumUniqueChannelsInRow > 1) {
            this.findViewById(R$id.non_configurable_text).setVisibility(8);
            this.findViewById(R$id.interruptiveness_settings).setVisibility(8);
            this.findViewById(R$id.non_configurable_multichannel_text).setVisibility(0);
        }
        else {
            this.findViewById(R$id.non_configurable_text).setVisibility(8);
            this.findViewById(R$id.non_configurable_multichannel_text).setVisibility(8);
            this.findViewById(R$id.interruptiveness_settings).setVisibility(0);
        }
        final View viewById = this.findViewById(R$id.turn_off_notifications);
        viewById.setOnClickListener(this.getTurnOffNotificationsClickListener());
        int visibility = n;
        if (viewById.hasOnClickListeners()) {
            visibility = n;
            if (!this.mIsNonblockable) {
                visibility = 0;
            }
        }
        viewById.setVisibility(visibility);
        this.findViewById(R$id.done).setOnClickListener(this.mOnDismissSettings);
        final View viewById2 = this.findViewById(R$id.silence);
        final View viewById3 = this.findViewById(R$id.alert);
        viewById2.setOnClickListener(this.mOnSilent);
        viewById3.setOnClickListener(this.mOnAlert);
        this.applyAlertingBehavior((this.mWasShownHighPriority ^ true) ? 1 : 0, false);
    }
    
    private void bindName() {
        final TextView textView = (TextView)this.findViewById(R$id.channel_name);
        if (!this.mIsSingleDefaultChannel && this.mNumUniqueChannelsInRow <= 1) {
            textView.setText(this.mSingleNotificationChannel.getName());
        }
        else {
            textView.setVisibility(8);
        }
    }
    
    private Intent getAppSettingsIntent(final PackageManager packageManager, final String package1, final NotificationChannel notificationChannel, final int n, final String s) {
        final Intent setPackage = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.NOTIFICATION_PREFERENCES").setPackage(package1);
        final List queryIntentActivities = packageManager.queryIntentActivities(setPackage, 65536);
        if (queryIntentActivities != null && queryIntentActivities.size() != 0 && queryIntentActivities.get(0) != null) {
            final ActivityInfo activityInfo = queryIntentActivities.get(0).activityInfo;
            setPackage.setClassName(activityInfo.packageName, activityInfo.name);
            if (notificationChannel != null) {
                setPackage.putExtra("android.intent.extra.CHANNEL_ID", notificationChannel.getId());
            }
            setPackage.putExtra("android.intent.extra.NOTIFICATION_ID", n);
            setPackage.putExtra("android.intent.extra.NOTIFICATION_TAG", s);
            return setPackage;
        }
        return null;
    }
    
    private LogMaker getLogMaker() {
        final StatusBarNotification mSbn = this.mSbn;
        LogMaker setCategory;
        if (mSbn == null) {
            setCategory = new LogMaker(1621);
        }
        else {
            setCategory = mSbn.getLogMaker().setCategory(1621);
        }
        return setCategory;
    }
    
    private View$OnClickListener getSettingsOnClickListener() {
        final int mAppUid = this.mAppUid;
        if (mAppUid >= 0 && this.mOnSettingsClickListener != null && this.mIsDeviceProvisioned) {
            return (View$OnClickListener)new _$$Lambda$NotificationInfo$hxpyY8vJ1JgmBKZbtsmY4xt8GSo(this, mAppUid);
        }
        return null;
    }
    
    private View$OnClickListener getTurnOffNotificationsClickListener() {
        return (View$OnClickListener)new _$$Lambda$NotificationInfo$qmudzW8HIf3NdQ5m_rRVs9_9Xwo(this);
    }
    
    private LogMaker importanceChangeLogMaker() {
        final Integer mChosenImportance = this.mChosenImportance;
        int i;
        if (mChosenImportance != null) {
            i = mChosenImportance;
        }
        else {
            i = this.mStartingChannelImportance;
        }
        return this.getLogMaker().setCategory(291).setType(4).setSubtype(i - this.mStartingChannelImportance);
    }
    
    private LogMaker notificationControlsLogMaker() {
        return this.getLogMaker().setCategory(204).setType(1).setSubtype(0);
    }
    
    private void saveImportance() {
        if (!this.mIsNonblockable) {
            if (this.mChosenImportance == null) {
                this.mChosenImportance = this.mStartingChannelImportance;
            }
            this.updateImportance();
        }
    }
    
    private void updateImportance() {
        if (this.mChosenImportance != null) {
            this.mMetricsLogger.write(this.importanceChangeLogMaker());
            int n2;
            final int n = n2 = this.mChosenImportance;
            Label_0083: {
                if (this.mStartingChannelImportance != -1000) {
                    if (!this.mWasShownHighPriority || this.mChosenImportance < 3) {
                        n2 = n;
                        if (this.mWasShownHighPriority) {
                            break Label_0083;
                        }
                        n2 = n;
                        if (this.mChosenImportance >= 3) {
                            break Label_0083;
                        }
                    }
                    n2 = this.mStartingChannelImportance;
                }
            }
            final Handler handler = new Handler((Looper)Dependency.get(Dependency.BG_LOOPER));
            final INotificationManager miNotificationManager = this.mINotificationManager;
            final String mPackageName = this.mPackageName;
            final int mAppUid = this.mAppUid;
            NotificationChannel mSingleNotificationChannel;
            if (this.mNumUniqueChannelsInRow == 1) {
                mSingleNotificationChannel = this.mSingleNotificationChannel;
            }
            else {
                mSingleNotificationChannel = null;
            }
            handler.post((Runnable)new UpdateImportanceRunnable(miNotificationManager, mPackageName, mAppUid, mSingleNotificationChannel, this.mStartingChannelImportance, n2));
            this.mVisualStabilityManager.temporarilyAllowReordering();
        }
    }
    
    public void bindNotification(final PackageManager mPm, final INotificationManager miNotificationManager, final VisualStabilityManager mVisualStabilityManager, final String mPackageName, final NotificationChannel mSingleNotificationChannel, final Set<NotificationChannel> mUniqueChannelsInRow, final NotificationEntry notificationEntry, final OnSettingsClickListener mOnSettingsClickListener, final OnAppSettingsClickListener mAppSettingsClickListener, final boolean mIsDeviceProvisioned, final boolean mIsNonblockable, final boolean mWasShownHighPriority) throws RemoteException {
        this.mINotificationManager = miNotificationManager;
        this.mMetricsLogger = Dependency.get(MetricsLogger.class);
        this.mVisualStabilityManager = mVisualStabilityManager;
        this.mChannelEditorDialogController = Dependency.get(ChannelEditorDialogController.class);
        this.mPackageName = mPackageName;
        this.mUniqueChannelsInRow = mUniqueChannelsInRow;
        this.mNumUniqueChannelsInRow = mUniqueChannelsInRow.size();
        this.mSbn = notificationEntry.getSbn();
        this.mPm = mPm;
        this.mAppSettingsClickListener = mAppSettingsClickListener;
        this.mAppName = this.mPackageName;
        this.mOnSettingsClickListener = mOnSettingsClickListener;
        this.mSingleNotificationChannel = mSingleNotificationChannel;
        this.mStartingChannelImportance = mSingleNotificationChannel.getImportance();
        this.mWasShownHighPriority = mWasShownHighPriority;
        this.mIsNonblockable = mIsNonblockable;
        this.mAppUid = this.mSbn.getUid();
        this.mDelegatePkg = this.mSbn.getOpPkg();
        this.mIsDeviceProvisioned = mIsDeviceProvisioned;
        final INotificationManager miNotificationManager2 = this.mINotificationManager;
        final int mAppUid = this.mAppUid;
        final boolean b = false;
        final int numNotificationChannelsForPackage = miNotificationManager2.getNumNotificationChannelsForPackage(mPackageName, mAppUid, false);
        final int mNumUniqueChannelsInRow = this.mNumUniqueChannelsInRow;
        if (mNumUniqueChannelsInRow != 0) {
            boolean mIsSingleDefaultChannel = b;
            if (mNumUniqueChannelsInRow == 1) {
                mIsSingleDefaultChannel = b;
                if (this.mSingleNotificationChannel.getId().equals("miscellaneous")) {
                    mIsSingleDefaultChannel = b;
                    if (numNotificationChannelsForPackage == 1) {
                        mIsSingleDefaultChannel = true;
                    }
                }
            }
            this.mIsSingleDefaultChannel = mIsSingleDefaultChannel;
            this.bindHeader();
            this.bindChannelDetails();
            this.bindInlineControls();
            this.mMetricsLogger.write(this.notificationControlsLogMaker());
            return;
        }
        throw new IllegalArgumentException("bindNotification requires at least one channel");
    }
    
    @VisibleForTesting
    void closeControls(final View view, final boolean b) {
        final int[] array = new int[2];
        final int[] array2 = new int[2];
        this.mGutsContainer.getLocationOnScreen(array);
        view.getLocationOnScreen(array2);
        this.mGutsContainer.closeControls(array2[0] - array[0] + view.getWidth() / 2, array2[1] - array[1] + view.getHeight() / 2, b, false);
    }
    
    public int getActualHeight() {
        return this.getHeight();
    }
    
    public View getContentView() {
        return (View)this;
    }
    
    public boolean handleCloseControls(final boolean b, final boolean b2) {
        if (this.mPresentingChannelEditorDialog) {
            final ChannelEditorDialogController mChannelEditorDialogController = this.mChannelEditorDialogController;
            if (mChannelEditorDialogController != null) {
                this.mPresentingChannelEditorDialog = false;
                mChannelEditorDialogController.setOnFinishListener(null);
                this.mChannelEditorDialogController.close();
            }
        }
        if (b) {
            this.saveImportance();
        }
        return false;
    }
    
    @VisibleForTesting
    public boolean isAnimating() {
        return false;
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mPriorityDescriptionView = (TextView)this.findViewById(R$id.alert_summary);
        this.mSilentDescriptionView = (TextView)this.findViewById(R$id.silence_summary);
    }
    
    public void onFinishedClosing() {
        final Integer mChosenImportance = this.mChosenImportance;
        if (mChosenImportance != null) {
            this.mStartingChannelImportance = mChosenImportance;
        }
        this.bindInlineControls();
        this.mMetricsLogger.write(this.notificationControlsLogMaker().setType(2));
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
    
    public boolean post(final Runnable runnable) {
        if (this.mSkipPost) {
            runnable.run();
            return true;
        }
        return super.post(runnable);
    }
    
    public void setGutsParent(final NotificationGuts mGutsContainer) {
        this.mGutsContainer = mGutsContainer;
    }
    
    public boolean shouldBeSaved() {
        return this.mPressedApply;
    }
    
    public boolean willBeRemoved() {
        return false;
    }
    
    public interface CheckSaveListener
    {
    }
    
    public interface OnAppSettingsClickListener
    {
        void onClick(final View p0, final Intent p1);
    }
    
    public interface OnSettingsClickListener
    {
        void onClick(final View p0, final NotificationChannel p1, final int p2);
    }
    
    private static class UpdateImportanceRunnable implements Runnable
    {
        private final int mAppUid;
        private final NotificationChannel mChannelToUpdate;
        private final int mCurrentImportance;
        private final INotificationManager mINotificationManager;
        private final int mNewImportance;
        private final String mPackageName;
        
        public UpdateImportanceRunnable(final INotificationManager miNotificationManager, final String mPackageName, final int mAppUid, final NotificationChannel mChannelToUpdate, final int mCurrentImportance, final int mNewImportance) {
            this.mINotificationManager = miNotificationManager;
            this.mPackageName = mPackageName;
            this.mAppUid = mAppUid;
            this.mChannelToUpdate = mChannelToUpdate;
            this.mCurrentImportance = mCurrentImportance;
            this.mNewImportance = mNewImportance;
        }
        
        @Override
        public void run() {
            try {
                if (this.mChannelToUpdate != null) {
                    this.mChannelToUpdate.setImportance(this.mNewImportance);
                    this.mChannelToUpdate.lockFields(4);
                    this.mINotificationManager.updateNotificationChannelForPackage(this.mPackageName, this.mAppUid, this.mChannelToUpdate);
                }
                else {
                    this.mINotificationManager.setNotificationsEnabledWithImportanceLockForPackage(this.mPackageName, this.mAppUid, this.mNewImportance >= this.mCurrentImportance);
                }
            }
            catch (RemoteException ex) {
                Log.e("InfoGuts", "Unable to update notification importance", (Throwable)ex);
            }
        }
    }
}
