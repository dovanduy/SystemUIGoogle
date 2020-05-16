// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.bubbles;

import android.graphics.drawable.Drawable;
import android.service.notification.StatusBarNotification;
import com.android.systemui.shared.system.SysUiStatsLog;
import android.content.Intent;
import android.view.View;
import android.app.Notification$BubbleMetadata;
import android.app.PendingIntent;
import java.util.Objects;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.os.AsyncTask$Status;
import android.os.UserHandle;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager$NameNotFoundException;
import android.content.res.Resources$NotFoundException;
import android.util.Log;
import android.content.Context;
import com.android.internal.annotations.VisibleForTesting$Visibility;
import com.android.internal.annotations.VisibleForTesting;
import android.content.pm.ShortcutInfo;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.graphics.Path;
import android.graphics.Bitmap;

class Bubble implements BubbleViewProvider
{
    private String mAppName;
    private Bitmap mBadgedImage;
    private int mDotColor;
    private Path mDotPath;
    private NotificationEntry mEntry;
    private BubbleExpandedView mExpandedView;
    private FlyoutMessage mFlyoutMessage;
    private final String mGroupId;
    private BadgedImageView mIconView;
    private boolean mInflateSynchronously;
    private BubbleViewInfoTask mInflationTask;
    private final String mKey;
    private long mLastAccessed;
    private long mLastUpdated;
    private ShortcutInfo mShortcutInfo;
    private boolean mShowBubbleUpdateDot;
    private boolean mSuppressFlyout;
    private BubbleController.NotificationSuppressionChangedListener mSuppressionListener;
    
    @VisibleForTesting(visibility = VisibleForTesting$Visibility.PRIVATE)
    Bubble(final NotificationEntry mEntry, final BubbleController.NotificationSuppressionChangedListener mSuppressionListener) {
        this.mShowBubbleUpdateDot = true;
        this.mEntry = mEntry;
        this.mKey = mEntry.getKey();
        this.mLastUpdated = mEntry.getSbn().getPostTime();
        this.mGroupId = groupId(mEntry);
        this.mSuppressionListener = mSuppressionListener;
    }
    
    private int getDimenForPackageUser(final Context context, int dimensionPixelSize, final String s, final int n) {
        final PackageManager packageManager = context.getPackageManager();
        if (s == null) {
            goto Label_0046;
        }
        int n2;
        if ((n2 = n) == -1) {
            n2 = 0;
        }
        try {
            dimensionPixelSize = packageManager.getResourcesForApplicationAsUser(s, n2).getDimensionPixelSize(dimensionPixelSize);
            return dimensionPixelSize;
        }
        catch (Resources$NotFoundException ex) {
            Log.e("Bubble", "Couldn't find desired height res id", (Throwable)ex);
        }
        catch (PackageManager$NameNotFoundException ex2) {
            goto Label_0046;
        }
    }
    
    public static String groupId(final NotificationEntry notificationEntry) {
        final UserHandle user = notificationEntry.getSbn().getUser();
        final StringBuilder sb = new StringBuilder();
        sb.append(user.getIdentifier());
        sb.append("|");
        sb.append(notificationEntry.getSbn().getPackageName());
        return sb.toString();
    }
    
    private boolean isBubbleLoading() {
        final BubbleViewInfoTask mInflationTask = this.mInflationTask;
        return mInflationTask != null && mInflationTask.getStatus() != AsyncTask$Status.FINISHED;
    }
    
    private boolean shouldSuppressNotification() {
        return this.mEntry.getBubbleMetadata() != null && this.mEntry.getBubbleMetadata().isNotificationSuppressed();
    }
    
    void cleanupViews() {
        final BubbleExpandedView mExpandedView = this.mExpandedView;
        if (mExpandedView != null) {
            mExpandedView.cleanUpExpandedState();
            this.mExpandedView = null;
        }
        this.mIconView = null;
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.print("key: ");
        printWriter.println(this.mKey);
        printWriter.print("  showInShade:   ");
        printWriter.println(this.showInShade());
        printWriter.print("  showDot:       ");
        printWriter.println(this.showDot());
        printWriter.print("  showFlyout:    ");
        printWriter.println(this.showFlyout());
        printWriter.print("  desiredHeight: ");
        printWriter.println(this.getDesiredHeightString());
        printWriter.print("  suppressNotif: ");
        printWriter.println(this.shouldSuppressNotification());
        printWriter.print("  autoExpand:    ");
        printWriter.println(this.shouldAutoExpand());
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof Bubble && Objects.equals(this.mKey, ((Bubble)o).mKey));
    }
    
    public String getAppName() {
        return this.mAppName;
    }
    
    @Override
    public Bitmap getBadgedImage() {
        return this.mBadgedImage;
    }
    
    PendingIntent getBubbleIntent() {
        final Notification$BubbleMetadata bubbleMetadata = this.mEntry.getBubbleMetadata();
        if (bubbleMetadata != null) {
            return bubbleMetadata.getIntent();
        }
        return null;
    }
    
    float getDesiredHeight(final Context context) {
        final Notification$BubbleMetadata bubbleMetadata = this.mEntry.getBubbleMetadata();
        if (bubbleMetadata.getDesiredHeightResId() != 0) {
            return (float)this.getDimenForPackageUser(context, bubbleMetadata.getDesiredHeightResId(), this.mEntry.getSbn().getPackageName(), this.mEntry.getSbn().getUser().getIdentifier());
        }
        return bubbleMetadata.getDesiredHeight() * context.getResources().getDisplayMetrics().density;
    }
    
    String getDesiredHeightString() {
        final Notification$BubbleMetadata bubbleMetadata = this.mEntry.getBubbleMetadata();
        if (bubbleMetadata.getDesiredHeightResId() != 0) {
            return String.valueOf(bubbleMetadata.getDesiredHeightResId());
        }
        return String.valueOf(bubbleMetadata.getDesiredHeight());
    }
    
    @Override
    public int getDisplayId() {
        final BubbleExpandedView mExpandedView = this.mExpandedView;
        int virtualDisplayId;
        if (mExpandedView != null) {
            virtualDisplayId = mExpandedView.getVirtualDisplayId();
        }
        else {
            virtualDisplayId = -1;
        }
        return virtualDisplayId;
    }
    
    @Override
    public int getDotColor() {
        return this.mDotColor;
    }
    
    @Override
    public Path getDotPath() {
        return this.mDotPath;
    }
    
    public NotificationEntry getEntry() {
        return this.mEntry;
    }
    
    @Override
    public BubbleExpandedView getExpandedView() {
        return this.mExpandedView;
    }
    
    FlyoutMessage getFlyoutMessage() {
        return this.mFlyoutMessage;
    }
    
    public String getGroupId() {
        return this.mGroupId;
    }
    
    public BadgedImageView getIconView() {
        return this.mIconView;
    }
    
    @Override
    public String getKey() {
        return this.mKey;
    }
    
    long getLastActivity() {
        return Math.max(this.mLastUpdated, this.mLastAccessed);
    }
    
    long getLastUpdateTime() {
        return this.mLastUpdated;
    }
    
    public String getPackageName() {
        return this.mEntry.getSbn().getPackageName();
    }
    
    Intent getSettingsIntent() {
        final Intent intent = new Intent("android.settings.APP_NOTIFICATION_BUBBLE_SETTINGS");
        intent.putExtra("android.provider.extra.APP_PACKAGE", this.getPackageName());
        intent.putExtra("app_uid", this.mEntry.getSbn().getUid());
        intent.addFlags(134217728);
        intent.addFlags(268435456);
        intent.addFlags(536870912);
        return intent;
    }
    
    public ShortcutInfo getShortcutInfo() {
        return this.mShortcutInfo;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.mKey);
    }
    
    void inflate(final BubbleViewInfoTask.Callback callback, final Context context, final BubbleStackView bubbleStackView, final BubbleIconFactory bubbleIconFactory) {
        if (this.isBubbleLoading()) {
            this.mInflationTask.cancel(true);
        }
        final BubbleViewInfoTask mInflationTask = new BubbleViewInfoTask(this, context, bubbleStackView, bubbleIconFactory, callback);
        this.mInflationTask = mInflationTask;
        if (this.mInflateSynchronously) {
            mInflationTask.onPostExecute(mInflationTask.doInBackground(new Void[0]));
        }
        else {
            mInflationTask.execute((Object[])new Void[0]);
        }
    }
    
    boolean isInflated() {
        return this.mIconView != null && this.mExpandedView != null;
    }
    
    boolean isOngoing() {
        return (this.mEntry.getSbn().getNotification().flags & 0x40) != 0x0;
    }
    
    @Override
    public void logUIEvent(final int n, final int n2, final float n3, final float n4, final int n5) {
        if (this.getEntry() != null && this.getEntry().getSbn() != null) {
            final StatusBarNotification sbn = this.getEntry().getSbn();
            SysUiStatsLog.write(149, sbn.getPackageName(), sbn.getNotification().getChannelId(), sbn.getId(), n5, n, n2, n3, n4, this.showInShade(), this.isOngoing(), false);
        }
        else {
            SysUiStatsLog.write(149, null, null, 0, 0, n, n2, n3, n4, false, false, false);
        }
    }
    
    void markAsAccessedAt(final long mLastAccessed) {
        this.mLastAccessed = mLastAccessed;
        this.setSuppressNotification(true);
        this.setShowDot(false);
    }
    
    void markUpdatedAt(final long mLastUpdated) {
        this.mLastUpdated = mLastUpdated;
    }
    
    @Override
    public void setContentVisibility(final boolean contentVisibility) {
        final BubbleExpandedView mExpandedView = this.mExpandedView;
        if (mExpandedView != null) {
            mExpandedView.setContentVisibility(contentVisibility);
        }
    }
    
    void setEntry(final NotificationEntry mEntry) {
        this.mEntry = mEntry;
        this.mLastUpdated = mEntry.getSbn().getPostTime();
    }
    
    @VisibleForTesting
    void setInflateSynchronously(final boolean mInflateSynchronously) {
        this.mInflateSynchronously = mInflateSynchronously;
    }
    
    void setShowDot(final boolean mShowBubbleUpdateDot) {
        this.mShowBubbleUpdateDot = mShowBubbleUpdateDot;
        final BadgedImageView mIconView = this.mIconView;
        if (mIconView != null) {
            mIconView.updateDotVisibility(true);
        }
    }
    
    void setSuppressFlyout(final boolean mSuppressFlyout) {
        this.mSuppressFlyout = mSuppressFlyout;
    }
    
    void setSuppressNotification(final boolean b) {
        final boolean showInShade = this.showInShade();
        final Notification$BubbleMetadata bubbleMetadata = this.mEntry.getBubbleMetadata();
        final int flags = bubbleMetadata.getFlags();
        int flags2;
        if (b) {
            flags2 = (flags | 0x2);
        }
        else {
            flags2 = (flags & 0xFFFFFFFD);
        }
        bubbleMetadata.setFlags(flags2);
        if (this.showInShade() != showInShade) {
            final BubbleController.NotificationSuppressionChangedListener mSuppressionListener = this.mSuppressionListener;
            if (mSuppressionListener != null) {
                mSuppressionListener.onBubbleNotificationSuppressionChange(this);
            }
        }
    }
    
    void setViewInfo(final BubbleViewInfoTask.BubbleViewInfo bubbleViewInfo) {
        if (!this.isInflated()) {
            this.mIconView = bubbleViewInfo.imageView;
            this.mExpandedView = bubbleViewInfo.expandedView;
        }
        this.mShortcutInfo = bubbleViewInfo.shortcutInfo;
        this.mAppName = bubbleViewInfo.appName;
        this.mFlyoutMessage = bubbleViewInfo.flyoutMessage;
        this.mBadgedImage = bubbleViewInfo.badgedBubbleImage;
        this.mDotColor = bubbleViewInfo.dotColor;
        this.mDotPath = bubbleViewInfo.dotPath;
        final BubbleExpandedView mExpandedView = this.mExpandedView;
        if (mExpandedView != null) {
            mExpandedView.update(this);
        }
        final BadgedImageView mIconView = this.mIconView;
        if (mIconView != null) {
            mIconView.setRenderedBubble(this);
        }
    }
    
    boolean shouldAutoExpand() {
        final Notification$BubbleMetadata bubbleMetadata = this.mEntry.getBubbleMetadata();
        return bubbleMetadata != null && bubbleMetadata.getAutoExpandBubble();
    }
    
    @Override
    public boolean showDot() {
        return this.mShowBubbleUpdateDot && !this.mEntry.shouldSuppressNotificationDot() && !this.shouldSuppressNotification();
    }
    
    boolean showFlyout() {
        return !this.mSuppressFlyout && !this.mEntry.shouldSuppressPeek() && !this.shouldSuppressNotification() && !this.mEntry.shouldSuppressNotificationList();
    }
    
    boolean showInShade() {
        return !this.shouldSuppressNotification() || !this.mEntry.isClearable();
    }
    
    void stopInflation() {
        final BubbleViewInfoTask mInflationTask = this.mInflationTask;
        if (mInflationTask == null) {
            return;
        }
        mInflationTask.cancel(true);
        this.cleanupViews();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Bubble{");
        sb.append(this.mKey);
        sb.append('}');
        return sb.toString();
    }
    
    boolean usingShortcutInfo() {
        return this.mEntry.getBubbleMetadata().getShortcutId() != null;
    }
    
    public static class FlyoutMessage
    {
        public boolean isGroupChat;
        public CharSequence message;
        public Drawable senderAvatar;
        public CharSequence senderName;
    }
}
