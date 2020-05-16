// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection;

import android.os.Bundle;
import android.app.Person;
import android.app.Notification$MessagingStyle$Message;
import com.android.internal.util.ArrayUtils;
import android.app.RemoteInputHistoryItem;
import android.os.SystemClock;
import android.service.notification.SnoozeCriterion;
import android.app.Notification$Action;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifPromoter;
import com.android.systemui.statusbar.notification.row.NotificationGuts;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifFilter;
import com.android.internal.util.ContrastColorUtil;
import android.content.Context;
import java.util.Iterator;
import android.app.NotificationChannel;
import android.app.Notification;
import java.util.ArrayList;
import java.util.Objects;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import android.net.Uri;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.InflationTask;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRowController;
import android.service.notification.NotificationListenerService$Ranking;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifLifetimeExtender;
import com.android.systemui.statusbar.notification.icon.IconPack;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifDismissInterceptor;
import java.util.List;
import android.app.Notification$BubbleMetadata;
import android.util.ArraySet;

public final class NotificationEntry extends ListEntry
{
    public EditedSuggestionInfo editedSuggestionInfo;
    private boolean hasSentReply;
    public CharSequence headsUpStatusBarText;
    public CharSequence headsUpStatusBarTextPublic;
    private long initializationTime;
    private boolean interruption;
    private long lastFullScreenIntentLaunchTime;
    public long lastRemoteInputSent;
    public final ArraySet<Integer> mActiveAppOps;
    private boolean mAllowFgsDismissal;
    private boolean mAutoHeadsUp;
    private Notification$BubbleMetadata mBubbleMetadata;
    private int mBucket;
    private int mCachedContrastColor;
    private int mCachedContrastColorIsFor;
    int mCancellationReason;
    private Throwable mDebugThrowable;
    final List<NotifDismissInterceptor> mDismissInterceptors;
    private DismissState mDismissState;
    private IconPack mIcons;
    public Boolean mIsSystemNotification;
    private final String mKey;
    final List<NotifLifetimeExtender> mLifetimeExtenders;
    private List<OnSensitivityChangedListener> mOnSensitivityChangedListeners;
    private boolean mPulseSupressed;
    private NotificationListenerService$Ranking mRanking;
    private ExpandableNotificationRowController mRowController;
    private InflationTask mRunningTask;
    private StatusBarNotification mSbn;
    private boolean mSensitive;
    public String remoteInputMimeType;
    public CharSequence remoteInputText;
    public CharSequence remoteInputTextWhenReset;
    public Uri remoteInputUri;
    private ExpandableNotificationRow row;
    public int targetSdk;
    
    public NotificationEntry(final StatusBarNotification statusBarNotification, final NotificationListenerService$Ranking notificationListenerService$Ranking) {
        this(statusBarNotification, notificationListenerService$Ranking, false);
    }
    
    public NotificationEntry(final StatusBarNotification statusBarNotification, final NotificationListenerService$Ranking notificationListenerService$Ranking, final boolean mAllowFgsDismissal) {
        Objects.requireNonNull(statusBarNotification);
        final String key = statusBarNotification.getKey();
        Objects.requireNonNull(key);
        super(key);
        this.mLifetimeExtenders = new ArrayList<NotifLifetimeExtender>();
        this.mDismissInterceptors = new ArrayList<NotifDismissInterceptor>();
        this.mCancellationReason = -1;
        this.mDismissState = DismissState.NOT_DISMISSED;
        this.mIcons = IconPack.buildEmptyPack(null);
        this.lastFullScreenIntentLaunchTime = -2000L;
        this.mCachedContrastColor = 1;
        this.mCachedContrastColorIsFor = 1;
        this.mRunningTask = null;
        this.lastRemoteInputSent = -2000L;
        this.mActiveAppOps = (ArraySet<Integer>)new ArraySet(3);
        this.initializationTime = -1L;
        this.mSensitive = true;
        this.mOnSensitivityChangedListeners = new ArrayList<OnSensitivityChangedListener>();
        this.mBucket = 3;
        Objects.requireNonNull(notificationListenerService$Ranking);
        this.mKey = statusBarNotification.getKey();
        this.setSbn(statusBarNotification);
        this.setRanking(notificationListenerService$Ranking);
        this.mAllowFgsDismissal = mAllowFgsDismissal;
    }
    
    private static boolean isCategory(final String b, final Notification notification) {
        return Objects.equals(notification.category, b);
    }
    
    private boolean isDismissable() {
        final boolean b = (this.mSbn.getNotification().flags & 0x2) != 0x0;
        final boolean b2 = (this.mSbn.getNotification().flags & 0x20) != 0x0;
        final boolean b3 = (this.mSbn.getNotification().flags & 0x40) != 0x0;
        if (this.mAllowFgsDismissal) {
            return !b2 || b || b3;
        }
        return this.mSbn.isClearable();
    }
    
    private static boolean isNotificationBlockedByPolicy(final Notification notification) {
        return isCategory("call", notification) || isCategory("msg", notification) || isCategory("alarm", notification) || isCategory("event", notification) || isCategory("reminder", notification);
    }
    
    private boolean shouldSuppressVisualEffect(final int n) {
        final boolean exemptFromDndVisualSuppression = this.isExemptFromDndVisualSuppression();
        boolean b = false;
        if (exemptFromDndVisualSuppression) {
            return false;
        }
        if ((this.getSuppressedVisualEffects() & n) != 0x0) {
            b = true;
        }
        return b;
    }
    
    public void abortTask() {
        final InflationTask mRunningTask = this.mRunningTask;
        if (mRunningTask != null) {
            mRunningTask.abort();
            this.mRunningTask = null;
        }
    }
    
    public void addOnSensitivityChangedListener(final OnSensitivityChangedListener onSensitivityChangedListener) {
        this.mOnSensitivityChangedListeners.add(onSensitivityChangedListener);
    }
    
    public boolean areChildrenExpanded() {
        final ExpandableNotificationRow row = this.row;
        return row != null && row.areChildrenExpanded();
    }
    
    public boolean areGutsExposed() {
        final ExpandableNotificationRow row = this.row;
        return row != null && row.getGuts() != null && this.row.getGuts().isExposed();
    }
    
    public boolean canBubble() {
        return this.mRanking.canBubble();
    }
    
    public void closeRemoteInput() {
        final ExpandableNotificationRow row = this.row;
        if (row != null) {
            row.closeRemoteInput();
        }
    }
    
    public Notification$BubbleMetadata getBubbleMetadata() {
        return this.mBubbleMetadata;
    }
    
    public int getBucket() {
        return this.mBucket;
    }
    
    public NotificationChannel getChannel() {
        return this.mRanking.getChannel();
    }
    
    public List<NotificationEntry> getChildren() {
        final ExpandableNotificationRow row = this.row;
        if (row == null) {
            return null;
        }
        final List<ExpandableNotificationRow> notificationChildren = row.getNotificationChildren();
        if (notificationChildren == null) {
            return null;
        }
        final ArrayList<NotificationEntry> list = new ArrayList<NotificationEntry>();
        final Iterator<ExpandableNotificationRow> iterator = notificationChildren.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().getEntry());
        }
        return list;
    }
    
    public int getContrastedColor(final Context context, final boolean b, int resolveContrastColor) {
        int color;
        if (b) {
            color = 0;
        }
        else {
            color = this.mSbn.getNotification().color;
        }
        if (this.mCachedContrastColorIsFor == color) {
            final int mCachedContrastColor = this.mCachedContrastColor;
            if (mCachedContrastColor != 1) {
                return mCachedContrastColor;
            }
        }
        resolveContrastColor = ContrastColorUtil.resolveContrastColor(context, color, resolveContrastColor);
        this.mCachedContrastColorIsFor = color;
        return this.mCachedContrastColor = resolveContrastColor;
    }
    
    public Throwable getDebugThrowable() {
        return this.mDebugThrowable;
    }
    
    public DismissState getDismissState() {
        return this.mDismissState;
    }
    
    public NotifFilter getExcludingFilter() {
        return this.getAttachState().getExcludingFilter();
    }
    
    public NotificationGuts getGuts() {
        final ExpandableNotificationRow row = this.row;
        if (row != null) {
            return row.getGuts();
        }
        return null;
    }
    
    public ExpandableNotificationRow getHeadsUpAnimationView() {
        return this.row;
    }
    
    public IconPack getIcons() {
        return this.mIcons;
    }
    
    public int getImportance() {
        return this.mRanking.getImportance();
    }
    
    @Override
    public String getKey() {
        return this.mKey;
    }
    
    public long getLastAudiblyAlertedMs() {
        return this.mRanking.getLastAudiblyAlertedMillis();
    }
    
    public NotifPromoter getNotifPromoter() {
        return this.getAttachState().getPromoter();
    }
    
    public NotificationListenerService$Ranking getRanking() {
        return this.mRanking;
    }
    
    @Override
    public NotificationEntry getRepresentativeEntry() {
        return this;
    }
    
    public ExpandableNotificationRow getRow() {
        return this.row;
    }
    
    public ExpandableNotificationRowController getRowController() {
        return this.mRowController;
    }
    
    @VisibleForTesting
    public InflationTask getRunningTask() {
        return this.mRunningTask;
    }
    
    public StatusBarNotification getSbn() {
        return this.mSbn;
    }
    
    public List<Notification$Action> getSmartActions() {
        return (List<Notification$Action>)this.mRanking.getSmartActions();
    }
    
    public List<CharSequence> getSmartReplies() {
        return (List<CharSequence>)this.mRanking.getSmartReplies();
    }
    
    public List<SnoozeCriterion> getSnoozeCriteria() {
        return (List<SnoozeCriterion>)this.mRanking.getSnoozeCriteria();
    }
    
    public int getSuppressedVisualEffects() {
        return this.mRanking.getSuppressedVisualEffects();
    }
    
    public int getUserSentiment() {
        return this.mRanking.getUserSentiment();
    }
    
    public boolean hasFinishedInitialization() {
        return this.initializationTime == -1L || SystemClock.elapsedRealtime() > this.initializationTime + 400L;
    }
    
    public boolean hasInterrupted() {
        return this.interruption;
    }
    
    public boolean hasJustLaunchedFullScreenIntent() {
        return SystemClock.elapsedRealtime() < this.lastFullScreenIntentLaunchTime + 2000L;
    }
    
    public boolean hasJustSentRemoteInput() {
        return SystemClock.elapsedRealtime() < this.lastRemoteInputSent + 500L;
    }
    
    public boolean isAmbient() {
        return this.mRanking.isAmbient();
    }
    
    public boolean isAutoHeadsUp() {
        return this.mAutoHeadsUp;
    }
    
    public boolean isBubble() {
        return (this.mSbn.getNotification().flags & 0x1000) != 0x0;
    }
    
    public boolean isChildInGroup() {
        final ExpandableNotificationRow row = this.row;
        return row != null && row.isChildInGroup();
    }
    
    public boolean isClearable() {
        if (!this.isDismissable()) {
            return false;
        }
        final List<NotificationEntry> children = this.getChildren();
        if (children != null && children.size() > 0) {
            for (int i = 0; i < children.size(); ++i) {
                if (!children.get(i).isDismissable()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @VisibleForTesting
    boolean isExemptFromDndVisualSuppression() {
        if (isNotificationBlockedByPolicy(this.mSbn.getNotification())) {
            return false;
        }
        if ((this.mSbn.getNotification().flags & 0x40) != 0x0) {
            return true;
        }
        if (this.mSbn.getNotification().isMediaNotification()) {
            return true;
        }
        final Boolean mIsSystemNotification = this.mIsSystemNotification;
        return mIsSystemNotification != null && mIsSystemNotification;
    }
    
    public boolean isGroupNotFullyVisible() {
        final ExpandableNotificationRow row = this.row;
        return row == null || row.isGroupNotFullyVisible();
    }
    
    public boolean isLastMessageFromReply() {
        if (!this.hasSentReply) {
            return false;
        }
        final Bundle extras = this.mSbn.getNotification().extras;
        if (!ArrayUtils.isEmpty((Object[])extras.getParcelableArray("android.remoteInputHistoryItems"))) {
            return true;
        }
        final List messagesFromBundleArray = Notification$MessagingStyle$Message.getMessagesFromBundleArray(extras.getParcelableArray("android.messages"));
        if (messagesFromBundleArray != null && !messagesFromBundleArray.isEmpty()) {
            final Notification$MessagingStyle$Message notification$MessagingStyle$Message = messagesFromBundleArray.get(messagesFromBundleArray.size() - 1);
            if (notification$MessagingStyle$Message != null) {
                final Person senderPerson = notification$MessagingStyle$Message.getSenderPerson();
                return senderPerson == null || Objects.equals(extras.getParcelable("android.messagingUser"), senderPerson);
            }
        }
        return false;
    }
    
    public boolean isMediaNotification() {
        final ExpandableNotificationRow row = this.row;
        return row != null && row.isMediaRow();
    }
    
    public boolean isPinnedAndExpanded() {
        final ExpandableNotificationRow row = this.row;
        return row != null && row.isPinnedAndExpanded();
    }
    
    public boolean isPulseSuppressed() {
        return this.mPulseSupressed;
    }
    
    public boolean isRemoved() {
        final ExpandableNotificationRow row = this.row;
        return row == null || row.isRemoved();
    }
    
    public boolean isRowDismissed() {
        final ExpandableNotificationRow row = this.row;
        return row != null && row.isDismissed();
    }
    
    public boolean isRowHeadsUp() {
        final ExpandableNotificationRow row = this.row;
        return row != null && row.isHeadsUp();
    }
    
    public boolean isRowPinned() {
        final ExpandableNotificationRow row = this.row;
        return row != null && row.isPinned();
    }
    
    public boolean isRowRemoved() {
        final ExpandableNotificationRow row = this.row;
        return row != null && row.isRemoved();
    }
    
    public boolean isSensitive() {
        return this.mSensitive;
    }
    
    public boolean isSummaryWithChildren() {
        final ExpandableNotificationRow row = this.row;
        return row != null && row.isSummaryWithChildren();
    }
    
    public boolean isTopLevelChild() {
        final ExpandableNotificationRow row = this.row;
        return row != null && row.isTopLevelChild();
    }
    
    public boolean mustStayOnScreen() {
        final ExpandableNotificationRow row = this.row;
        return row != null && row.mustStayOnScreen();
    }
    
    public void notifyFullScreenIntentLaunched() {
        this.setInterruption();
        this.lastFullScreenIntentLaunchTime = SystemClock.elapsedRealtime();
    }
    
    public void notifyHeightChanged(final boolean b) {
        final ExpandableNotificationRow row = this.row;
        if (row != null) {
            row.notifyHeightChanged(b);
        }
    }
    
    public void onDensityOrFontScaleChanged() {
        final ExpandableNotificationRow row = this.row;
        if (row != null) {
            row.onDensityOrFontScaleChanged();
        }
    }
    
    public void onInflationTaskFinished() {
        this.mRunningTask = null;
    }
    
    public void onRemoteInputInserted() {
        this.lastRemoteInputSent = -2000L;
        this.remoteInputTextWhenReset = null;
    }
    
    public void removeOnSensitivityChangedListener(final OnSensitivityChangedListener onSensitivityChangedListener) {
        this.mOnSensitivityChangedListeners.remove(onSensitivityChangedListener);
    }
    
    public void removeRow() {
        final ExpandableNotificationRow row = this.row;
        if (row != null) {
            row.setRemoved();
        }
    }
    
    public void reset() {
        final ExpandableNotificationRow row = this.row;
        if (row != null) {
            row.reset();
        }
    }
    
    public void resetUserExpansion() {
        final ExpandableNotificationRow row = this.row;
        if (row != null) {
            row.resetUserExpansion();
        }
    }
    
    public boolean rowExists() {
        return this.row != null;
    }
    
    public void sendAccessibilityEvent(final int n) {
        final ExpandableNotificationRow row = this.row;
        if (row != null) {
            row.sendAccessibilityEvent(n);
        }
    }
    
    public void setAutoHeadsUp(final boolean mAutoHeadsUp) {
        this.mAutoHeadsUp = mAutoHeadsUp;
    }
    
    public void setBubbleMetadata(final Notification$BubbleMetadata mBubbleMetadata) {
        this.mBubbleMetadata = mBubbleMetadata;
    }
    
    public void setBucket(final int mBucket) {
        this.mBucket = mBucket;
    }
    
    public void setDebugThrowable(final Throwable mDebugThrowable) {
        this.mDebugThrowable = mDebugThrowable;
    }
    
    void setDismissState(final DismissState obj) {
        Objects.requireNonNull(obj);
        this.mDismissState = obj;
    }
    
    public boolean setFlagBubble(final boolean b) {
        final boolean bubble = this.isBubble();
        if (!b) {
            final Notification notification = this.mSbn.getNotification();
            notification.flags &= 0xFFFFEFFF;
        }
        else if (this.mBubbleMetadata != null && this.canBubble()) {
            final Notification notification2 = this.mSbn.getNotification();
            notification2.flags |= 0x1000;
        }
        return bubble != this.isBubble();
    }
    
    public void setGroupExpansionChanging(final boolean groupExpansionChanging) {
        final ExpandableNotificationRow row = this.row;
        if (row != null) {
            row.setGroupExpansionChanging(groupExpansionChanging);
        }
    }
    
    public void setHasSentReply() {
        this.hasSentReply = true;
    }
    
    public void setHeadsUp(final boolean headsUp) {
        final ExpandableNotificationRow row = this.row;
        if (row != null) {
            row.setHeadsUp(headsUp);
        }
    }
    
    public void setHeadsUpAnimatingAway(final boolean headsUpAnimatingAway) {
        final ExpandableNotificationRow row = this.row;
        if (row != null) {
            row.setHeadsUpAnimatingAway(headsUpAnimatingAway);
        }
    }
    
    public void setHeadsUpIsVisible() {
        final ExpandableNotificationRow row = this.row;
        if (row != null) {
            row.setHeadsUpIsVisible();
        }
    }
    
    public void setIcons(final IconPack mIcons) {
        this.mIcons = mIcons;
    }
    
    public void setInflationTask(final InflationTask mRunningTask) {
        this.abortTask();
        this.mRunningTask = mRunningTask;
    }
    
    public void setInitializationTime(final long initializationTime) {
        if (this.initializationTime == -1L) {
            this.initializationTime = initializationTime;
        }
    }
    
    public void setInterruption() {
        this.interruption = true;
    }
    
    public void setKeepInParent(final boolean keepInParent) {
        final ExpandableNotificationRow row = this.row;
        if (row != null) {
            row.setKeepInParent(keepInParent);
        }
    }
    
    public void setPulseSuppressed(final boolean mPulseSupressed) {
        this.mPulseSupressed = mPulseSupressed;
    }
    
    public void setRanking(final NotificationListenerService$Ranking notificationListenerService$Ranking) {
        Objects.requireNonNull(notificationListenerService$Ranking);
        Objects.requireNonNull(notificationListenerService$Ranking.getKey());
        if (notificationListenerService$Ranking.getKey().equals(this.mKey)) {
            this.mRanking = notificationListenerService$Ranking;
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("New key ");
        sb.append(notificationListenerService$Ranking.getKey());
        sb.append(" doesn't match existing key ");
        sb.append(this.mKey);
        throw new IllegalArgumentException(sb.toString());
    }
    
    public void setRow(final ExpandableNotificationRow row) {
        this.row = row;
    }
    
    public void setRowController(final ExpandableNotificationRowController mRowController) {
        this.mRowController = mRowController;
    }
    
    public void setRowPinned(final boolean pinned) {
        final ExpandableNotificationRow row = this.row;
        if (row != null) {
            row.setPinned(pinned);
        }
    }
    
    public void setSbn(final StatusBarNotification statusBarNotification) {
        Objects.requireNonNull(statusBarNotification);
        Objects.requireNonNull(statusBarNotification.getKey());
        if (statusBarNotification.getKey().equals(this.mKey)) {
            this.mSbn = statusBarNotification;
            this.mBubbleMetadata = statusBarNotification.getNotification().getBubbleMetadata();
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("New key ");
        sb.append(statusBarNotification.getKey());
        sb.append(" doesn't match existing key ");
        sb.append(this.mKey);
        throw new IllegalArgumentException(sb.toString());
    }
    
    public void setSensitive(final boolean mSensitive, final boolean b) {
        this.getRow().setSensitive(mSensitive, b);
        if (mSensitive != this.mSensitive) {
            this.mSensitive = mSensitive;
            for (int i = 0; i < this.mOnSensitivityChangedListeners.size(); ++i) {
                this.mOnSensitivityChangedListeners.get(i).onSensitivityChanged(this);
            }
        }
    }
    
    public void setUserExpanded(final boolean b, final boolean b2) {
        final ExpandableNotificationRow row = this.row;
        if (row != null) {
            row.setUserExpanded(b, b2);
        }
    }
    
    public void setUserLocked(final boolean userLocked) {
        final ExpandableNotificationRow row = this.row;
        if (row != null) {
            row.setUserLocked(userLocked);
        }
    }
    
    public boolean shouldSuppressAmbient() {
        return this.shouldSuppressVisualEffect(128);
    }
    
    public boolean shouldSuppressFullScreenIntent() {
        return this.shouldSuppressVisualEffect(4);
    }
    
    public boolean shouldSuppressNotificationDot() {
        return this.shouldSuppressVisualEffect(64);
    }
    
    public boolean shouldSuppressNotificationList() {
        return this.shouldSuppressVisualEffect(256);
    }
    
    public boolean shouldSuppressPeek() {
        return this.shouldSuppressVisualEffect(16);
    }
    
    public boolean shouldSuppressStatusBar() {
        return this.shouldSuppressVisualEffect(32);
    }
    
    public boolean showingPulsing() {
        final ExpandableNotificationRow row = this.row;
        return row != null && row.showingPulsing();
    }
    
    public enum DismissState
    {
        DISMISSED, 
        NOT_DISMISSED, 
        PARENT_DISMISSED;
    }
    
    public static class EditedSuggestionInfo
    {
        public final int index;
        public final CharSequence originalText;
        
        public EditedSuggestionInfo(final CharSequence originalText, final int index) {
            this.originalText = originalText;
            this.index = index;
        }
    }
    
    public interface OnSensitivityChangedListener
    {
        void onSensitivityChanged(final NotificationEntry p0);
    }
}
