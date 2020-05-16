// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.text.TextUtils;
import android.app.Notification;
import android.app.RemoteInputHistoryItem;
import android.app.Notification$Builder;
import com.android.internal.annotations.VisibleForTesting;
import java.util.Set;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.systemui.R$id;
import android.service.notification.NotificationListenerService$RankingMap;
import android.net.Uri;
import com.android.systemui.statusbar.policy.RemoteInputView;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.internal.statusbar.IStatusBarService$Stub;
import android.os.ServiceManager;
import android.app.ActivityManager;
import android.os.SystemClock;
import android.app.Notification$Action;
import android.os.RemoteException;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import java.util.Objects;
import android.view.ViewGroup;
import android.util.Log;
import android.util.Pair;
import android.widget.RemoteViews;
import android.app.ActivityOptions;
import android.widget.RemoteViews$RemoteResponse;
import android.app.RemoteInput;
import android.app.PendingIntent;
import android.view.View;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import android.service.notification.StatusBarNotification;
import android.view.ViewParent;
import android.os.SystemProperties;
import android.os.UserManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import com.android.systemui.statusbar.policy.RemoteInputUriController;
import android.widget.RemoteViews$OnClickHandler;
import android.os.Handler;
import java.util.ArrayList;
import android.app.KeyguardManager;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.util.ArraySet;
import android.content.Context;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.Dumpable;

public class NotificationRemoteInputManager implements Dumpable
{
    public static final boolean ENABLE_REMOTE_INPUT;
    public static boolean FORCE_REMOTE_INPUT_HISTORY;
    protected IStatusBarService mBarService;
    protected Callback mCallback;
    protected final Context mContext;
    protected final ArraySet<NotificationEntry> mEntriesKeptForRemoteInputActive;
    private final NotificationEntryManager mEntryManager;
    private final KeyguardManager mKeyguardManager;
    protected final ArraySet<String> mKeysKeptForRemoteInputHistory;
    protected final ArrayList<NotificationLifetimeExtender> mLifetimeExtenders;
    private final NotificationLockscreenUserManager mLockscreenUserManager;
    private final Handler mMainHandler;
    protected NotificationLifetimeExtender.NotificationSafeToRemoveCallback mNotificationLifetimeFinishedCallback;
    private final RemoteViews$OnClickHandler mOnClickHandler;
    protected RemoteInputController mRemoteInputController;
    private final RemoteInputUriController mRemoteInputUriController;
    private final SmartReplyController mSmartReplyController;
    private final Lazy<StatusBar> mStatusBarLazy;
    private final StatusBarStateController mStatusBarStateController;
    private final UserManager mUserManager;
    
    static {
        ENABLE_REMOTE_INPUT = SystemProperties.getBoolean("debug.enable_remote_input", true);
        NotificationRemoteInputManager.FORCE_REMOTE_INPUT_HISTORY = SystemProperties.getBoolean("debug.force_remoteinput_history", true);
    }
    
    public NotificationRemoteInputManager(final Context mContext, final NotificationLockscreenUserManager mLockscreenUserManager, final SmartReplyController mSmartReplyController, final NotificationEntryManager mEntryManager, final Lazy<StatusBar> mStatusBarLazy, final StatusBarStateController mStatusBarStateController, final Handler mMainHandler, final RemoteInputUriController mRemoteInputUriController) {
        this.mKeysKeptForRemoteInputHistory = (ArraySet<String>)new ArraySet();
        this.mEntriesKeptForRemoteInputActive = (ArraySet<NotificationEntry>)new ArraySet();
        this.mLifetimeExtenders = new ArrayList<NotificationLifetimeExtender>();
        this.mOnClickHandler = (RemoteViews$OnClickHandler)new RemoteViews$OnClickHandler() {
            private StatusBarNotification getNotificationForParent(ViewParent parent) {
                while (parent != null) {
                    if (parent instanceof ExpandableNotificationRow) {
                        return ((ExpandableNotificationRow)parent).getEntry().getSbn();
                    }
                    parent = parent.getParent();
                }
                return null;
            }
            
            private boolean handleRemoteInput(final View view, final PendingIntent pendingIntent) {
                if (NotificationRemoteInputManager.this.mCallback.shouldHandleRemoteInput(view, pendingIntent)) {
                    return true;
                }
                final Object tag = view.getTag(16909327);
                RemoteInput[] array;
                if (tag instanceof RemoteInput[]) {
                    array = (RemoteInput[])tag;
                }
                else {
                    array = null;
                }
                if (array == null) {
                    return false;
                }
                final int length = array.length;
                RemoteInput remoteInput = null;
                for (final RemoteInput remoteInput2 : array) {
                    if (remoteInput2.getAllowFreeFormInput()) {
                        remoteInput = remoteInput2;
                    }
                }
                return remoteInput != null && NotificationRemoteInputManager.this.activateRemoteInput(view, array, remoteInput, pendingIntent, null);
            }
            
            private void logActionClick(final View view, final PendingIntent b) {
                final Integer n = (Integer)view.getTag(16909207);
                if (n == null) {
                    return;
                }
                final ViewParent parent = view.getParent();
                final StatusBarNotification notificationForParent = this.getNotificationForParent(parent);
                if (notificationForParent == null) {
                    Log.w("NotifRemoteInputManager", "Couldn't determine notification for click.");
                    return;
                }
                final String key = notificationForParent.getKey();
                int indexOfChild;
                if (view.getId() == 16908691 && parent != null && parent instanceof ViewGroup) {
                    indexOfChild = ((ViewGroup)parent).indexOfChild(view);
                }
                else {
                    indexOfChild = -1;
                }
                final int activeNotificationsCount = NotificationRemoteInputManager.this.mEntryManager.getActiveNotificationsCount();
                final int rank = NotificationRemoteInputManager.this.mEntryManager.getActiveNotificationUnfiltered(key).getRanking().getRank();
                final Notification$Action[] actions = notificationForParent.getNotification().actions;
                Label_0226: {
                    if (actions == null || n >= actions.length) {
                        break Label_0226;
                    }
                    final Notification$Action notification$Action = notificationForParent.getNotification().actions[n];
                    if (!Objects.equals(notification$Action.actionIntent, b)) {
                        Log.w("NotifRemoteInputManager", "actionIntent does not match");
                        return;
                    }
                    final NotificationVisibility obtain = NotificationVisibility.obtain(key, rank, activeNotificationsCount, true, NotificationLogger.getNotificationLocation(NotificationRemoteInputManager.this.mEntryManager.getActiveNotificationUnfiltered(key)));
                    try {
                        NotificationRemoteInputManager.this.mBarService.onNotificationActionClick(key, indexOfChild, notification$Action, obtain, false);
                        return;
                        Log.w("NotifRemoteInputManager", "statusBarNotification.getNotification().actions is null or invalid");
                    }
                    catch (RemoteException ex) {}
                }
            }
            
            public boolean onClickHandler(final View view, final PendingIntent pendingIntent, final RemoteViews$RemoteResponse remoteViews$RemoteResponse) {
                NotificationRemoteInputManager.this.mStatusBarLazy.get().wakeUpIfDozing(SystemClock.uptimeMillis(), view, "NOTIFICATION_CLICK");
                if (this.handleRemoteInput(view, pendingIntent)) {
                    return true;
                }
                this.logActionClick(view, pendingIntent);
                try {
                    ActivityManager.getService().resumeAppSwitches();
                    return NotificationRemoteInputManager.this.mCallback.handleRemoteViewClick(view, pendingIntent, new _$$Lambda$NotificationRemoteInputManager$1$9gPb9F64OW5Dxh7FkFJc_IgAVZQ(remoteViews$RemoteResponse, view, pendingIntent));
                }
                catch (RemoteException ex) {
                    return NotificationRemoteInputManager.this.mCallback.handleRemoteViewClick(view, pendingIntent, new _$$Lambda$NotificationRemoteInputManager$1$9gPb9F64OW5Dxh7FkFJc_IgAVZQ(remoteViews$RemoteResponse, view, pendingIntent));
                }
            }
        };
        this.mContext = mContext;
        this.mLockscreenUserManager = mLockscreenUserManager;
        this.mSmartReplyController = mSmartReplyController;
        this.mEntryManager = mEntryManager;
        this.mStatusBarLazy = mStatusBarLazy;
        this.mMainHandler = mMainHandler;
        this.mBarService = IStatusBarService$Stub.asInterface(ServiceManager.getService("statusbar"));
        this.mUserManager = (UserManager)this.mContext.getSystemService("user");
        this.addLifetimeExtenders();
        this.mKeyguardManager = (KeyguardManager)mContext.getSystemService((Class)KeyguardManager.class);
        this.mStatusBarStateController = mStatusBarStateController;
        this.mRemoteInputUriController = mRemoteInputUriController;
        mEntryManager.addNotificationEntryListener(new NotificationEntryListener() {
            @Override
            public void onEntryRemoved(final NotificationEntry notificationEntry, final NotificationVisibility notificationVisibility, final boolean b, final int n) {
                NotificationRemoteInputManager.this.mSmartReplyController.stopSending(notificationEntry);
                if (b && notificationEntry != null) {
                    NotificationRemoteInputManager.this.onPerformRemoveNotification(notificationEntry, notificationEntry.getKey());
                }
            }
            
            @Override
            public void onPreEntryUpdated(final NotificationEntry notificationEntry) {
                NotificationRemoteInputManager.this.mSmartReplyController.stopSending(notificationEntry);
            }
        });
    }
    
    private RemoteInputView findRemoteInputView(final View view) {
        if (view == null) {
            return null;
        }
        return (RemoteInputView)view.findViewWithTag(RemoteInputView.VIEW_TAG);
    }
    
    public boolean activateRemoteInput(final View view, final RemoteInput[] array, final RemoteInput remoteInput, final PendingIntent pendingIntent, final NotificationEntry.EditedSuggestionInfo editedSuggestionInfo) {
        ViewParent viewParent = view.getParent();
        RemoteInputView remoteInputView;
        ExpandableNotificationRow expandableNotificationRow;
        Object remoteInputView2;
        while (true) {
            remoteInputView = null;
            if (viewParent == null) {
                remoteInputView2 = (expandableNotificationRow = null);
                break;
            }
            if (viewParent instanceof View) {
                final View view2 = (View)viewParent;
                if (view2.isRootNamespace()) {
                    remoteInputView2 = this.findRemoteInputView(view2);
                    expandableNotificationRow = (ExpandableNotificationRow)view2.getTag(R$id.row_tag_for_content_view);
                    break;
                }
            }
            viewParent = viewParent.getParent();
        }
        if (expandableNotificationRow == null) {
            return false;
        }
        expandableNotificationRow.setUserExpanded(true);
        if (!this.mLockscreenUserManager.shouldAllowLockscreenRemoteInput()) {
            final int identifier = pendingIntent.getCreatorUserHandle().getIdentifier();
            if (this.mLockscreenUserManager.isLockscreenPublicMode(identifier) || this.mStatusBarStateController.getState() == 1) {
                this.mCallback.onLockedRemoteInput(expandableNotificationRow, view);
                return true;
            }
            if (this.mUserManager.getUserInfo(identifier).isManagedProfile() && this.mKeyguardManager.isDeviceLocked(identifier)) {
                this.mCallback.onLockedWorkRemoteInput(identifier, expandableNotificationRow, view);
                return true;
            }
        }
        if (remoteInputView2 != null && !((LinearLayout)remoteInputView2).isAttachedToWindow()) {
            remoteInputView2 = remoteInputView;
        }
        LinearLayout remoteInputView3;
        if ((remoteInputView3 = (LinearLayout)remoteInputView2) == null && (remoteInputView3 = this.findRemoteInputView(expandableNotificationRow.getPrivateLayout().getExpandedChild())) == null) {
            return false;
        }
        if (remoteInputView3 == expandableNotificationRow.getPrivateLayout().getExpandedRemoteInput() && !expandableNotificationRow.getPrivateLayout().getExpandedChild().isShown()) {
            this.mCallback.onMakeExpandedVisibleForRemoteInput(expandableNotificationRow, view);
            return true;
        }
        if (!remoteInputView3.isAttachedToWindow()) {
            return false;
        }
        int n;
        final int a = n = view.getWidth();
        if (view instanceof TextView) {
            final TextView textView = (TextView)view;
            n = a;
            if (textView.getLayout() != null) {
                n = Math.min(a, (int)textView.getLayout().getLineWidth(0) + (textView.getCompoundPaddingLeft() + textView.getCompoundPaddingRight()));
            }
        }
        final int n2 = view.getLeft() + n / 2;
        final int n3 = view.getTop() + view.getHeight() / 2;
        final int width = remoteInputView3.getWidth();
        final int n4 = remoteInputView3.getHeight() - n3;
        final int max = Math.max(n2 + n3, n2 + n4);
        final int n5 = width - n2;
        ((RemoteInputView)remoteInputView3).setRevealParameters(n2, n3, Math.max(max, Math.max(n5 + n3, n5 + n4)));
        ((RemoteInputView)remoteInputView3).setPendingIntent(pendingIntent);
        ((RemoteInputView)remoteInputView3).setRemoteInput(array, remoteInput, editedSuggestionInfo);
        ((RemoteInputView)remoteInputView3).focusAnimated();
        return true;
    }
    
    protected void addLifetimeExtenders() {
        this.mLifetimeExtenders.add(new RemoteInputHistoryExtender());
        this.mLifetimeExtenders.add(new SmartReplyHistoryExtender());
        this.mLifetimeExtenders.add(new RemoteInputActiveExtender());
    }
    
    public void bindRow(final ExpandableNotificationRow expandableNotificationRow) {
        expandableNotificationRow.setRemoteInputController(this.mRemoteInputController);
    }
    
    public void checkRemoteInputOutside(final MotionEvent motionEvent) {
        if (motionEvent.getAction() == 4 && motionEvent.getX() == 0.0f && motionEvent.getY() == 0.0f && this.mRemoteInputController.isRemoteInputActive()) {
            this.mRemoteInputController.closeRemoteInputs();
        }
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("NotificationRemoteInputManager state:");
        printWriter.print("  mKeysKeptForRemoteInputHistory: ");
        printWriter.println(this.mKeysKeptForRemoteInputHistory);
        printWriter.print("  mEntriesKeptForRemoteInputActive: ");
        printWriter.println(this.mEntriesKeptForRemoteInputActive);
    }
    
    public RemoteInputController getController() {
        return this.mRemoteInputController;
    }
    
    @VisibleForTesting
    public Set<NotificationEntry> getEntriesKeptForRemoteInputActive() {
        return (Set<NotificationEntry>)this.mEntriesKeptForRemoteInputActive;
    }
    
    public ArrayList<NotificationLifetimeExtender> getLifetimeExtenders() {
        return this.mLifetimeExtenders;
    }
    
    public RemoteViews$OnClickHandler getRemoteViewsOnClickHandler() {
        return this.mOnClickHandler;
    }
    
    public boolean isNotificationKeptForRemoteInputHistory(final String s) {
        return this.mKeysKeptForRemoteInputHistory.contains((Object)s);
    }
    
    public void onPanelCollapsed() {
        for (int i = 0; i < this.mEntriesKeptForRemoteInputActive.size(); ++i) {
            final NotificationEntry notificationEntry = (NotificationEntry)this.mEntriesKeptForRemoteInputActive.valueAt(i);
            this.mRemoteInputController.removeRemoteInput(notificationEntry, null);
            final NotificationLifetimeExtender.NotificationSafeToRemoveCallback mNotificationLifetimeFinishedCallback = this.mNotificationLifetimeFinishedCallback;
            if (mNotificationLifetimeFinishedCallback != null) {
                mNotificationLifetimeFinishedCallback.onSafeToRemove(notificationEntry.getKey());
            }
        }
        this.mEntriesKeptForRemoteInputActive.clear();
    }
    
    @VisibleForTesting
    void onPerformRemoveNotification(final NotificationEntry notificationEntry, final String s) {
        if (this.mKeysKeptForRemoteInputHistory.contains((Object)s)) {
            this.mKeysKeptForRemoteInputHistory.remove((Object)s);
        }
        if (this.mRemoteInputController.isRemoteInputActive(notificationEntry)) {
            this.mRemoteInputController.removeRemoteInput(notificationEntry, null);
        }
    }
    
    @VisibleForTesting
    StatusBarNotification rebuildNotificationForCanceledSmartReplies(final NotificationEntry notificationEntry) {
        return this.rebuildNotificationWithRemoteInput(notificationEntry, null, false, null, null);
    }
    
    @VisibleForTesting
    StatusBarNotification rebuildNotificationWithRemoteInput(final NotificationEntry notificationEntry, final CharSequence charSequence, final boolean showRemoteInputSpinner, final String s, final Uri uri) {
        final StatusBarNotification sbn = notificationEntry.getSbn();
        final Notification$Builder recoverBuilder = Notification$Builder.recoverBuilder(this.mContext, sbn.getNotification().clone());
        if (charSequence != null || uri != null) {
            final RemoteInputHistoryItem[] array = (RemoteInputHistoryItem[])sbn.getNotification().extras.getParcelableArray("android.remoteInputHistoryItems");
            RemoteInputHistoryItem[] remoteInputHistory;
            if (array == null) {
                remoteInputHistory = new RemoteInputHistoryItem[] { null };
            }
            else {
                remoteInputHistory = new RemoteInputHistoryItem[array.length + 1];
                System.arraycopy(array, 0, remoteInputHistory, 1, array.length);
            }
            RemoteInputHistoryItem remoteInputHistoryItem;
            if (uri != null) {
                remoteInputHistoryItem = new RemoteInputHistoryItem(s, uri, charSequence);
            }
            else {
                remoteInputHistoryItem = new RemoteInputHistoryItem(charSequence);
            }
            remoteInputHistory[0] = remoteInputHistoryItem;
            recoverBuilder.setRemoteInputHistory(remoteInputHistory);
        }
        recoverBuilder.setShowRemoteInputSpinner(showRemoteInputSpinner);
        recoverBuilder.setHideSmartReplies(true);
        final Notification build = recoverBuilder.build();
        build.contentView = sbn.getNotification().contentView;
        build.bigContentView = sbn.getNotification().bigContentView;
        build.headsUpContentView = sbn.getNotification().headsUpContentView;
        return new StatusBarNotification(sbn.getPackageName(), sbn.getOpPkg(), sbn.getId(), sbn.getTag(), sbn.getUid(), sbn.getInitialPid(), build, sbn.getUser(), sbn.getOverrideGroupKey(), sbn.getPostTime());
    }
    
    public void setUpWithCallback(final Callback mCallback, final RemoteInputController.Delegate delegate) {
        this.mCallback = mCallback;
        (this.mRemoteInputController = new RemoteInputController(delegate, this.mRemoteInputUriController)).addCallback((RemoteInputController.Callback)new RemoteInputController.Callback() {
            @Override
            public void onRemoteInputSent(final NotificationEntry notificationEntry) {
                if (NotificationRemoteInputManager.FORCE_REMOTE_INPUT_HISTORY && NotificationRemoteInputManager.this.isNotificationKeptForRemoteInputHistory(notificationEntry.getKey())) {
                    NotificationRemoteInputManager.this.mNotificationLifetimeFinishedCallback.onSafeToRemove(notificationEntry.getKey());
                }
                else if (NotificationRemoteInputManager.this.mEntriesKeptForRemoteInputActive.contains((Object)notificationEntry)) {
                    NotificationRemoteInputManager.this.mMainHandler.postDelayed((Runnable)new _$$Lambda$NotificationRemoteInputManager$3$4_sgjm8NgJs8c5OYAKLP29ZAlfg(this, notificationEntry), 200L);
                }
                try {
                    NotificationRemoteInputManager.this.mBarService.onNotificationDirectReplied(notificationEntry.getSbn().getKey());
                    if (notificationEntry.editedSuggestionInfo != null) {
                        NotificationRemoteInputManager.this.mBarService.onNotificationSmartReplySent(notificationEntry.getSbn().getKey(), notificationEntry.editedSuggestionInfo.index, notificationEntry.editedSuggestionInfo.originalText, NotificationLogger.getNotificationLocation(notificationEntry).toMetricsEventEnum(), !TextUtils.equals(notificationEntry.remoteInputText, notificationEntry.editedSuggestionInfo.originalText));
                    }
                }
                catch (RemoteException ex) {}
            }
        });
        this.mSmartReplyController.setCallback((SmartReplyController.Callback)new _$$Lambda$NotificationRemoteInputManager$Nf_J1NPWba8TQAi27Yt_XiB5drE(this));
    }
    
    public boolean shouldKeepForRemoteInputHistory(final NotificationEntry notificationEntry) {
        final boolean force_REMOTE_INPUT_HISTORY = NotificationRemoteInputManager.FORCE_REMOTE_INPUT_HISTORY;
        boolean b = false;
        if (!force_REMOTE_INPUT_HISTORY) {
            return false;
        }
        if (this.mRemoteInputController.isSpinning(notificationEntry.getKey()) || notificationEntry.hasJustSentRemoteInput()) {
            b = true;
        }
        return b;
    }
    
    public boolean shouldKeepForSmartReplyHistory(final NotificationEntry notificationEntry) {
        return NotificationRemoteInputManager.FORCE_REMOTE_INPUT_HISTORY && this.mSmartReplyController.isSendingSmartReply(notificationEntry.getKey());
    }
    
    public interface Callback
    {
        boolean handleRemoteViewClick(final View p0, final PendingIntent p1, final ClickHandler p2);
        
        void onLockedRemoteInput(final ExpandableNotificationRow p0, final View p1);
        
        void onLockedWorkRemoteInput(final int p0, final ExpandableNotificationRow p1, final View p2);
        
        void onMakeExpandedVisibleForRemoteInput(final ExpandableNotificationRow p0, final View p1);
        
        boolean shouldHandleRemoteInput(final View p0, final PendingIntent p1);
    }
    
    public interface ClickHandler
    {
        boolean handleClick();
    }
    
    protected class RemoteInputActiveExtender extends RemoteInputExtender
    {
        @Override
        public void setShouldManageLifetime(final NotificationEntry notificationEntry, final boolean b) {
            if (b) {
                if (Log.isLoggable("NotifRemoteInputManager", 3)) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Keeping notification around while remote input active ");
                    sb.append(notificationEntry.getKey());
                    Log.d("NotifRemoteInputManager", sb.toString());
                }
                NotificationRemoteInputManager.this.mEntriesKeptForRemoteInputActive.add((Object)notificationEntry);
            }
            else {
                NotificationRemoteInputManager.this.mEntriesKeptForRemoteInputActive.remove((Object)notificationEntry);
            }
        }
        
        @Override
        public boolean shouldExtendLifetime(final NotificationEntry notificationEntry) {
            return NotificationRemoteInputManager.this.mRemoteInputController.isRemoteInputActive(notificationEntry);
        }
    }
    
    protected abstract class RemoteInputExtender implements NotificationLifetimeExtender
    {
        @Override
        public void setCallback(final NotificationSafeToRemoveCallback mNotificationLifetimeFinishedCallback) {
            final NotificationRemoteInputManager this$0 = NotificationRemoteInputManager.this;
            if (this$0.mNotificationLifetimeFinishedCallback == null) {
                this$0.mNotificationLifetimeFinishedCallback = mNotificationLifetimeFinishedCallback;
            }
        }
    }
    
    protected class RemoteInputHistoryExtender extends RemoteInputExtender
    {
        @Override
        public void setShouldManageLifetime(final NotificationEntry notificationEntry, final boolean b) {
            if (b) {
                CharSequence charSequence;
                if (TextUtils.isEmpty(charSequence = notificationEntry.remoteInputText)) {
                    charSequence = notificationEntry.remoteInputTextWhenReset;
                }
                final StatusBarNotification rebuildNotificationWithRemoteInput = NotificationRemoteInputManager.this.rebuildNotificationWithRemoteInput(notificationEntry, charSequence, false, notificationEntry.remoteInputMimeType, notificationEntry.remoteInputUri);
                notificationEntry.onRemoteInputInserted();
                if (rebuildNotificationWithRemoteInput == null) {
                    return;
                }
                NotificationRemoteInputManager.this.mEntryManager.updateNotification(rebuildNotificationWithRemoteInput, null);
                if (notificationEntry.isRemoved()) {
                    return;
                }
                if (Log.isLoggable("NotifRemoteInputManager", 3)) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Keeping notification around after sending remote input ");
                    sb.append(notificationEntry.getKey());
                    Log.d("NotifRemoteInputManager", sb.toString());
                }
                NotificationRemoteInputManager.this.mKeysKeptForRemoteInputHistory.add((Object)notificationEntry.getKey());
            }
            else {
                NotificationRemoteInputManager.this.mKeysKeptForRemoteInputHistory.remove((Object)notificationEntry.getKey());
            }
        }
        
        @Override
        public boolean shouldExtendLifetime(final NotificationEntry notificationEntry) {
            return NotificationRemoteInputManager.this.shouldKeepForRemoteInputHistory(notificationEntry);
        }
    }
    
    protected class SmartReplyHistoryExtender extends RemoteInputExtender
    {
        @Override
        public void setShouldManageLifetime(final NotificationEntry notificationEntry, final boolean b) {
            if (b) {
                final StatusBarNotification rebuildNotificationForCanceledSmartReplies = NotificationRemoteInputManager.this.rebuildNotificationForCanceledSmartReplies(notificationEntry);
                if (rebuildNotificationForCanceledSmartReplies == null) {
                    return;
                }
                NotificationRemoteInputManager.this.mEntryManager.updateNotification(rebuildNotificationForCanceledSmartReplies, null);
                if (notificationEntry.isRemoved()) {
                    return;
                }
                if (Log.isLoggable("NotifRemoteInputManager", 3)) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Keeping notification around after sending smart reply ");
                    sb.append(notificationEntry.getKey());
                    Log.d("NotifRemoteInputManager", sb.toString());
                }
                NotificationRemoteInputManager.this.mKeysKeptForRemoteInputHistory.add((Object)notificationEntry.getKey());
            }
            else {
                NotificationRemoteInputManager.this.mKeysKeptForRemoteInputHistory.remove((Object)notificationEntry.getKey());
                NotificationRemoteInputManager.this.mSmartReplyController.stopSending(notificationEntry);
            }
        }
        
        @Override
        public boolean shouldExtendLifetime(final NotificationEntry notificationEntry) {
            return NotificationRemoteInputManager.this.shouldKeepForSmartReplyHistory(notificationEntry);
        }
    }
}
