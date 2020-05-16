// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.util.Slog;
import android.os.UserHandle;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import android.content.pm.ShortcutManager;
import com.android.systemui.R$string;
import android.view.ViewGroup;
import android.transition.TransitionManager;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.os.Handler;
import com.android.systemui.Dependency;
import android.os.Looper;
import android.os.Bundle;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager$NameNotFoundException;
import android.app.Notification$MessagingStyle;
import android.app.Notification$MessagingStyle$Message;
import android.os.Parcelable;
import android.widget.ImageView;
import android.app.NotificationChannelGroup;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import com.android.systemui.R$id;
import android.util.AttributeSet;
import android.content.Context;
import com.android.internal.annotations.VisibleForTesting;
import android.content.pm.ShortcutInfo;
import android.service.notification.StatusBarNotification;
import android.content.pm.PackageManager;
import android.view.View$OnClickListener;
import android.app.NotificationChannel;
import com.android.settingslib.notification.ConversationIconFactory;
import android.app.INotificationManager;
import android.widget.TextView;
import android.widget.LinearLayout;

public class NotificationConversationInfo extends LinearLayout implements GutsContent
{
    private String mAppName;
    private int mAppUid;
    private String mConversationId;
    private TextView mDefaultDescriptionView;
    private String mDelegatePkg;
    private NotificationGuts mGutsContainer;
    private INotificationManager mINotificationManager;
    private ConversationIconFactory mIconFactory;
    private boolean mIsDeviceProvisioned;
    private NotificationChannel mNotificationChannel;
    private View$OnClickListener mOnDefaultClick;
    private View$OnClickListener mOnDone;
    private View$OnClickListener mOnFavoriteClick;
    private View$OnClickListener mOnMuteClick;
    private OnSettingsClickListener mOnSettingsClickListener;
    private String mPackageName;
    private PackageManager mPm;
    private TextView mPriorityDescriptionView;
    private StatusBarNotification mSbn;
    private int mSelectedAction;
    private ShortcutInfo mShortcutInfo;
    private TextView mSilentDescriptionView;
    @VisibleForTesting
    boolean mSkipPost;
    
    public NotificationConversationInfo(final Context context, final AttributeSet set) {
        super(context, set);
        this.mSelectedAction = -1;
        this.mSkipPost = false;
        this.mOnFavoriteClick = (View$OnClickListener)new _$$Lambda$NotificationConversationInfo$2f29XNzTuIQXwa_IUqXUdjSebnE(this);
        this.mOnDefaultClick = (View$OnClickListener)new _$$Lambda$NotificationConversationInfo$MMDb1SDKJPIzmFXTDLDSkJw5h7E(this);
        this.mOnMuteClick = (View$OnClickListener)new _$$Lambda$NotificationConversationInfo$b1cMFzfzYhzwNF5Nsg_2Oi0i80o(this);
        this.mOnDone = (View$OnClickListener)new _$$Lambda$NotificationConversationInfo$a2pWq_ojuRPeVF2IYicCGQXQa0w(this);
    }
    
    private void bindActions() {
        this.findViewById(R$id.priority).setOnClickListener(this.mOnFavoriteClick);
        this.findViewById(R$id.default_behavior).setOnClickListener(this.mOnDefaultClick);
        this.findViewById(R$id.silence).setOnClickListener(this.mOnMuteClick);
        final View viewById = this.findViewById(R$id.info);
        viewById.setOnClickListener(this.getSettingsOnClickListener());
        int visibility;
        if (viewById.hasOnClickListeners()) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        viewById.setVisibility(visibility);
        this.updateToggleActions(this.getSelectedAction(), false);
    }
    
    private void bindConversationDetails() {
        ((TextView)this.findViewById(R$id.parent_channel_name)).setText(this.mNotificationChannel.getName());
        this.bindGroup();
        this.bindPackage();
        this.bindIcon(this.mNotificationChannel.isImportantConversation());
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
    
    private void bindGroup() {
        final NotificationChannel mNotificationChannel = this.mNotificationChannel;
        CharSequence name;
        Object viewById = name = null;
        while (true) {
            if (mNotificationChannel == null) {
                break Label_0058;
            }
            name = (CharSequence)viewById;
            if (mNotificationChannel.getGroup() == null) {
                break Label_0058;
            }
            try {
                final NotificationChannelGroup notificationChannelGroupForPackage = this.mINotificationManager.getNotificationChannelGroupForPackage(this.mNotificationChannel.getGroup(), this.mPackageName, this.mAppUid);
                name = (CharSequence)viewById;
                if (notificationChannelGroupForPackage != null) {
                    name = notificationChannelGroupForPackage.getName();
                }
                final TextView textView = (TextView)this.findViewById(R$id.group_name);
                viewById = this.findViewById(R$id.group_divider);
                if (name != null) {
                    textView.setText(name);
                    textView.setVisibility(0);
                    ((View)viewById).setVisibility(0);
                }
                else {
                    textView.setVisibility(8);
                    ((View)viewById).setVisibility(8);
                }
            }
            catch (RemoteException ex) {
                name = (CharSequence)viewById;
                continue;
            }
            break;
        }
    }
    
    private void bindHeader() {
        this.bindConversationDetails();
        this.bindDelegate();
    }
    
    private void bindIcon(final boolean b) {
        final ImageView imageView = (ImageView)this.findViewById(R$id.conversation_icon);
        final ShortcutInfo mShortcutInfo = this.mShortcutInfo;
        if (mShortcutInfo != null) {
            imageView.setImageDrawable(this.mIconFactory.getConversationDrawable(mShortcutInfo, this.mPackageName, this.mAppUid, b));
        }
        else if (this.mSbn.getNotification().extras.getBoolean("android.isGroupConversation", false)) {
            imageView.setImageDrawable(this.mPm.getDefaultActivityIcon());
        }
        else {
            final Notification$MessagingStyle$Message latestIncomingMessage = Notification$MessagingStyle.findLatestIncomingMessage(Notification$MessagingStyle$Message.getMessagesFromBundleArray((Parcelable[])this.mSbn.getNotification().extras.get("android.messages")));
            if (latestIncomingMessage.getSenderPerson().getIcon() != null) {
                imageView.setImageIcon(latestIncomingMessage.getSenderPerson().getIcon());
            }
            else {
                imageView.setImageDrawable(this.mPm.getDefaultActivityIcon());
            }
        }
    }
    
    private void bindPackage() {
        while (true) {
            try {
                final ApplicationInfo applicationInfo = this.mPm.getApplicationInfo(this.mPackageName, 795136);
                if (applicationInfo != null) {
                    this.mAppName = String.valueOf(this.mPm.getApplicationLabel(applicationInfo));
                }
                ((TextView)this.findViewById(R$id.pkg_name)).setText((CharSequence)this.mAppName);
            }
            catch (PackageManager$NameNotFoundException ex) {
                continue;
            }
            break;
        }
    }
    
    private String getName() {
        final ShortcutInfo mShortcutInfo = this.mShortcutInfo;
        if (mShortcutInfo != null) {
            return mShortcutInfo.getShortLabel().toString();
        }
        final Bundle extras = this.mSbn.getNotification().extras;
        String s;
        if (TextUtils.isEmpty((CharSequence)(s = extras.getString("android.conversationTitle")))) {
            s = extras.getString("android.title");
        }
        return s;
    }
    
    private View$OnClickListener getSettingsOnClickListener() {
        final int mAppUid = this.mAppUid;
        if (mAppUid >= 0 && this.mOnSettingsClickListener != null && this.mIsDeviceProvisioned) {
            return (View$OnClickListener)new _$$Lambda$NotificationConversationInfo$jd7IzkV9FIzPNu4O1qyUjmumXQA(this, mAppUid);
        }
        return null;
    }
    
    private void updateChannel() {
        new Handler((Looper)Dependency.get(Dependency.BG_LOOPER)).post((Runnable)new UpdateChannelRunnable(this.mINotificationManager, this.mPackageName, this.mAppUid, this.mSelectedAction, this.mNotificationChannel));
    }
    
    private void updateToggleActions(final int n, final boolean b) {
        final boolean b2 = true;
        if (b) {
            final TransitionSet set = new TransitionSet();
            set.setOrdering(0);
            set.addTransition((Transition)new Fade(2)).addTransition((Transition)new ChangeBounds()).addTransition(new Fade(1).setStartDelay(150L).setDuration(200L).setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN));
            set.setDuration(350L);
            set.setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN);
            TransitionManager.beginDelayedTransition((ViewGroup)this, (Transition)set);
        }
        final View viewById = this.findViewById(R$id.priority);
        final View viewById2 = this.findViewById(R$id.default_behavior);
        final View viewById3 = this.findViewById(R$id.silence);
        if (n != 0) {
            if (n != 2) {
                if (n != 4) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Unrecognized behavior: ");
                    sb.append(this.mSelectedAction);
                    throw new IllegalArgumentException(sb.toString());
                }
                this.mSilentDescriptionView.setVisibility(0);
                this.mDefaultDescriptionView.setVisibility(8);
                this.mPriorityDescriptionView.setVisibility(8);
                this.post(new _$$Lambda$NotificationConversationInfo$kfzFL_SnOxGZqEtBbC1UizD6_b0(viewById, viewById2, viewById3));
            }
            else {
                this.mPriorityDescriptionView.setVisibility(0);
                this.mDefaultDescriptionView.setVisibility(8);
                this.mSilentDescriptionView.setVisibility(8);
                this.post(new _$$Lambda$NotificationConversationInfo$B9YxfzUwTI8bD0EYToJb0uOXmZg(viewById, viewById2, viewById3));
            }
        }
        else {
            this.mDefaultDescriptionView.setVisibility(0);
            this.mSilentDescriptionView.setVisibility(8);
            this.mPriorityDescriptionView.setVisibility(8);
            this.post(new _$$Lambda$NotificationConversationInfo$u26KNDMKK01ES1xNd4IeWex6_38(viewById, viewById2, viewById3));
        }
        final boolean b3 = this.getSelectedAction() != n;
        final TextView textView = (TextView)this.findViewById(R$id.done);
        int text;
        if (b3) {
            text = R$string.inline_ok_button;
        }
        else {
            text = R$string.inline_done_button;
        }
        textView.setText(text);
        this.bindIcon(n == 2 && b2);
    }
    
    public void bindNotification(final ShortcutManager shortcutManager, final PackageManager mPm, final INotificationManager miNotificationManager, final VisualStabilityManager visualStabilityManager, final String mPackageName, final NotificationChannel mNotificationChannel, final NotificationEntry notificationEntry, final OnSettingsClickListener mOnSettingsClickListener, final OnSnoozeClickListener onSnoozeClickListener, final ConversationIconFactory mIconFactory, final boolean mIsDeviceProvisioned) {
        this.mSelectedAction = -1;
        this.mINotificationManager = miNotificationManager;
        this.mPackageName = mPackageName;
        final StatusBarNotification sbn = notificationEntry.getSbn();
        this.mSbn = sbn;
        this.mPm = mPm;
        this.mAppName = this.mPackageName;
        this.mOnSettingsClickListener = mOnSettingsClickListener;
        this.mNotificationChannel = mNotificationChannel;
        this.mAppUid = sbn.getUid();
        this.mDelegatePkg = this.mSbn.getOpPkg();
        this.mIsDeviceProvisioned = mIsDeviceProvisioned;
        this.mIconFactory = mIconFactory;
        this.mConversationId = this.mNotificationChannel.getConversationId();
        if (TextUtils.isEmpty((CharSequence)this.mNotificationChannel.getConversationId())) {
            this.mConversationId = this.mSbn.getShortcutId(super.mContext);
        }
        if (!TextUtils.isEmpty((CharSequence)this.mConversationId)) {
            this.mShortcutInfo = notificationEntry.getRanking().getShortcutInfo();
            this.createConversationChannelIfNeeded();
            this.bindHeader();
            this.bindActions();
            this.findViewById(R$id.done).setOnClickListener(this.mOnDone);
            return;
        }
        throw new IllegalArgumentException("Does not have required information");
    }
    
    @VisibleForTesting
    void closeControls(final View view, final boolean b) {
        final int[] array = new int[2];
        final int[] array2 = new int[2];
        this.mGutsContainer.getLocationOnScreen(array);
        view.getLocationOnScreen(array2);
        this.mGutsContainer.closeControls(array2[0] - array[0] + view.getWidth() / 2, array2[1] - array[1] + view.getHeight() / 2, b, false);
    }
    
    void createConversationChannelIfNeeded() {
        if (TextUtils.isEmpty((CharSequence)this.mNotificationChannel.getConversationId())) {
            try {
                this.mNotificationChannel.setName((CharSequence)super.mContext.getString(R$string.notification_summary_message_format, new Object[] { this.getName(), this.mNotificationChannel.getName() }));
                this.mINotificationManager.createConversationNotificationChannelForPackage(this.mPackageName, this.mAppUid, this.mSbn.getKey(), this.mNotificationChannel, this.mConversationId);
                this.mNotificationChannel = this.mINotificationManager.getConversationNotificationChannel(super.mContext.getOpPackageName(), UserHandle.getUserId(this.mAppUid), this.mPackageName, this.mNotificationChannel.getId(), false, this.mConversationId);
            }
            catch (RemoteException ex) {
                Slog.e("ConversationGuts", "Could not create conversation channel", (Throwable)ex);
            }
        }
    }
    
    public int getActualHeight() {
        return this.getHeight();
    }
    
    public View getContentView() {
        return (View)this;
    }
    
    int getSelectedAction() {
        if (this.mNotificationChannel.getImportance() <= 2 && this.mNotificationChannel.getImportance() > -1000) {
            return 4;
        }
        if (this.mNotificationChannel.isImportantConversation()) {
            return 2;
        }
        return 0;
    }
    
    public boolean handleCloseControls(final boolean b, final boolean b2) {
        if (b && this.mSelectedAction > -1) {
            this.updateChannel();
        }
        return false;
    }
    
    @VisibleForTesting
    public boolean isAnimating() {
        return false;
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mPriorityDescriptionView = (TextView)this.findViewById(R$id.priority_summary);
        this.mDefaultDescriptionView = (TextView)this.findViewById(R$id.default_summary);
        this.mSilentDescriptionView = (TextView)this.findViewById(R$id.silence_summary);
    }
    
    public void onFinishedClosing() {
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
        final int mSelectedAction = this.mSelectedAction;
        return mSelectedAction == 2 || mSelectedAction == 4;
    }
    
    public boolean willBeRemoved() {
        return false;
    }
    
    public interface OnSettingsClickListener
    {
        void onClick(final View p0, final NotificationChannel p1, final int p2);
    }
    
    public interface OnSnoozeClickListener
    {
    }
    
    class UpdateChannelRunnable implements Runnable
    {
        private final int mAction;
        private final String mAppPkg;
        private final int mAppUid;
        private NotificationChannel mChannelToUpdate;
        private final INotificationManager mINotificationManager;
        
        public UpdateChannelRunnable(final NotificationConversationInfo notificationConversationInfo, final INotificationManager miNotificationManager, final String mAppPkg, final int mAppUid, final int mAction, final NotificationChannel mChannelToUpdate) {
            this.mINotificationManager = miNotificationManager;
            this.mAppPkg = mAppPkg;
            this.mAppUid = mAppUid;
            this.mChannelToUpdate = mChannelToUpdate;
            this.mAction = mAction;
        }
        
        @Override
        public void run() {
            try {
                final int mAction = this.mAction;
                boolean importantConversation = false;
                if (mAction != 0) {
                    if (mAction != 2) {
                        if (mAction == 4) {
                            if (this.mChannelToUpdate.getImportance() == -1000 || this.mChannelToUpdate.getImportance() >= 3) {
                                this.mChannelToUpdate.setImportance(2);
                            }
                            if (this.mChannelToUpdate.isImportantConversation()) {
                                this.mChannelToUpdate.setImportantConversation(false);
                                this.mChannelToUpdate.setAllowBubbles(false);
                            }
                        }
                    }
                    else {
                        final NotificationChannel mChannelToUpdate = this.mChannelToUpdate;
                        if (!this.mChannelToUpdate.isImportantConversation()) {
                            importantConversation = true;
                        }
                        mChannelToUpdate.setImportantConversation(importantConversation);
                        if (this.mChannelToUpdate.isImportantConversation()) {
                            this.mChannelToUpdate.setAllowBubbles(true);
                        }
                        this.mChannelToUpdate.setImportance(Math.max(this.mChannelToUpdate.getOriginalImportance(), 3));
                    }
                }
                else {
                    this.mChannelToUpdate.setImportance(Math.max(this.mChannelToUpdate.getOriginalImportance(), 3));
                    if (this.mChannelToUpdate.isImportantConversation()) {
                        this.mChannelToUpdate.setImportantConversation(false);
                        this.mChannelToUpdate.setAllowBubbles(false);
                    }
                }
                this.mINotificationManager.updateNotificationChannelForPackage(this.mAppPkg, this.mAppUid, this.mChannelToUpdate);
            }
            catch (RemoteException ex) {
                Log.e("ConversationGuts", "Unable to update notification channel", (Throwable)ex);
            }
        }
    }
}
