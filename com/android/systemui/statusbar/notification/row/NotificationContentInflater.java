// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.content.pm.ApplicationInfo;
import android.content.ContextWrapper;
import android.app.Notification;
import com.android.systemui.statusbar.notification.MediaNotificationProcessor;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.InflationException;
import com.android.systemui.statusbar.InflationTask;
import android.os.AsyncTask;
import java.util.function.Consumer;
import com.android.systemui.util.Assert;
import com.android.internal.widget.ImageResolver;
import com.android.internal.widget.ImageMessageConsumer;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RemoteViews$OnViewAppliedListener;
import com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper;
import android.os.CancellationSignal$OnCancelListener;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViews$OnClickHandler;
import com.android.systemui.statusbar.policy.InflatedSmartReplies;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import android.content.Context;
import android.app.Notification$Builder;
import android.os.CancellationSignal;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.HashMap;
import com.android.systemui.statusbar.SmartReplyController;
import com.android.systemui.statusbar.policy.SmartReplyConstants;
import dagger.Lazy;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.ConversationNotificationProcessor;
import java.util.concurrent.Executor;
import com.android.internal.annotations.VisibleForTesting$Visibility;
import com.android.internal.annotations.VisibleForTesting;

@VisibleForTesting(visibility = VisibleForTesting$Visibility.PACKAGE)
public class NotificationContentInflater implements NotificationRowContentBinder
{
    private final Executor mBgExecutor;
    private final ConversationNotificationProcessor mConversationProcessor;
    private boolean mInflateSynchronously;
    private final NotificationRemoteInputManager mRemoteInputManager;
    private final NotifRemoteViewCache mRemoteViewCache;
    private final Lazy<SmartReplyConstants> mSmartReplyConstants;
    private final Lazy<SmartReplyController> mSmartReplyController;
    
    NotificationContentInflater(final NotifRemoteViewCache mRemoteViewCache, final NotificationRemoteInputManager mRemoteInputManager, final Lazy<SmartReplyConstants> mSmartReplyConstants, final Lazy<SmartReplyController> mSmartReplyController, final ConversationNotificationProcessor mConversationProcessor, final Executor mBgExecutor) {
        this.mInflateSynchronously = false;
        this.mRemoteViewCache = mRemoteViewCache;
        this.mRemoteInputManager = mRemoteInputManager;
        this.mSmartReplyConstants = mSmartReplyConstants;
        this.mSmartReplyController = mSmartReplyController;
        this.mConversationProcessor = mConversationProcessor;
        this.mBgExecutor = mBgExecutor;
    }
    
    static /* synthetic */ InflationProgress access$1700(final InflationProgress inflationProgress, final int n, final NotificationEntry notificationEntry, final Context context, final Context context2, final HeadsUpManager headsUpManager, final SmartReplyConstants smartReplyConstants, final SmartReplyController smartReplyController, final InflatedSmartReplies.SmartRepliesAndActions smartRepliesAndActions) {
        inflateSmartReplyViews(inflationProgress, n, notificationEntry, context, context2, headsUpManager, smartReplyConstants, smartReplyController, smartRepliesAndActions);
        return inflationProgress;
    }
    
    private static CancellationSignal apply(final Executor executor, final boolean b, final InflationProgress inflationProgress, final int n, final NotifRemoteViewCache notifRemoteViewCache, final NotificationEntry notificationEntry, final ExpandableNotificationRow expandableNotificationRow, final RemoteViews$OnClickHandler remoteViews$OnClickHandler, final InflationCallback inflationCallback) {
        final NotificationContentView privateLayout = expandableNotificationRow.getPrivateLayout();
        final NotificationContentView publicLayout = expandableNotificationRow.getPublicLayout();
        final HashMap<Integer, CancellationSignal> hashMap = new HashMap<Integer, CancellationSignal>();
        if ((n & 0x1) != 0x0) {
            applyRemoteView(executor, b, inflationProgress, n, 1, notifRemoteViewCache, notificationEntry, expandableNotificationRow, canReapplyRemoteView(inflationProgress.newContentView, notifRemoteViewCache.getCachedView(notificationEntry, 1)) ^ true, remoteViews$OnClickHandler, inflationCallback, privateLayout, privateLayout.getContractedChild(), privateLayout.getVisibleWrapper(0), hashMap, (ApplyCallback)new ApplyCallback() {
                @Override
                public RemoteViews getRemoteView() {
                    return inflationProgress.newContentView;
                }
                
                @Override
                public void setResultView(final View view) {
                    inflationProgress.inflatedContentView = view;
                }
            });
        }
        if ((n & 0x2) != 0x0 && inflationProgress.newExpandedView != null) {
            applyRemoteView(executor, b, inflationProgress, n, 2, notifRemoteViewCache, notificationEntry, expandableNotificationRow, canReapplyRemoteView(inflationProgress.newExpandedView, notifRemoteViewCache.getCachedView(notificationEntry, 2)) ^ true, remoteViews$OnClickHandler, inflationCallback, privateLayout, privateLayout.getExpandedChild(), privateLayout.getVisibleWrapper(1), hashMap, (ApplyCallback)new ApplyCallback() {
                @Override
                public RemoteViews getRemoteView() {
                    return inflationProgress.newExpandedView;
                }
                
                @Override
                public void setResultView(final View view) {
                    inflationProgress.inflatedExpandedView = view;
                }
            });
        }
        if ((n & 0x4) != 0x0 && inflationProgress.newHeadsUpView != null) {
            applyRemoteView(executor, b, inflationProgress, n, 4, notifRemoteViewCache, notificationEntry, expandableNotificationRow, canReapplyRemoteView(inflationProgress.newHeadsUpView, notifRemoteViewCache.getCachedView(notificationEntry, 4)) ^ true, remoteViews$OnClickHandler, inflationCallback, privateLayout, privateLayout.getHeadsUpChild(), privateLayout.getVisibleWrapper(2), hashMap, (ApplyCallback)new ApplyCallback() {
                @Override
                public RemoteViews getRemoteView() {
                    return inflationProgress.newHeadsUpView;
                }
                
                @Override
                public void setResultView(final View view) {
                    inflationProgress.inflatedHeadsUpView = view;
                }
            });
        }
        if ((n & 0x8) != 0x0) {
            applyRemoteView(executor, b, inflationProgress, n, 8, notifRemoteViewCache, notificationEntry, expandableNotificationRow, canReapplyRemoteView(inflationProgress.newPublicView, notifRemoteViewCache.getCachedView(notificationEntry, 8)) ^ true, remoteViews$OnClickHandler, inflationCallback, publicLayout, publicLayout.getContractedChild(), publicLayout.getVisibleWrapper(0), hashMap, (ApplyCallback)new ApplyCallback() {
                @Override
                public RemoteViews getRemoteView() {
                    return inflationProgress.newPublicView;
                }
                
                @Override
                public void setResultView(final View view) {
                    inflationProgress.inflatedPublicView = view;
                }
            });
        }
        finishIfDone(inflationProgress, n, notifRemoteViewCache, hashMap, inflationCallback, notificationEntry, expandableNotificationRow);
        final CancellationSignal cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener((CancellationSignal$OnCancelListener)new _$$Lambda$NotificationContentInflater$OEvtjvTsy_AuHJidkBGe8RqtYkc(hashMap));
        return cancellationSignal;
    }
    
    @VisibleForTesting
    static void applyRemoteView(final Executor executor, final boolean b, final InflationProgress inflationProgress, final int n, final int n2, final NotifRemoteViewCache notifRemoteViewCache, final NotificationEntry notificationEntry, final ExpandableNotificationRow expandableNotificationRow, final boolean b2, final RemoteViews$OnClickHandler remoteViews$OnClickHandler, final InflationCallback inflationCallback, final NotificationContentView notificationContentView, final View view, final NotificationViewWrapper notificationViewWrapper, final HashMap<Integer, CancellationSignal> hashMap, final ApplyCallback applyCallback) {
        final RemoteViews remoteView = applyCallback.getRemoteView();
        if (!b) {
            final RemoteViews$OnViewAppliedListener remoteViews$OnViewAppliedListener = (RemoteViews$OnViewAppliedListener)new RemoteViews$OnViewAppliedListener() {
                public void onError(final Exception ex) {
                    try {
                        View view = view;
                        if (b2) {
                            view = remoteView.apply(inflationProgress.packageContext, (ViewGroup)notificationContentView, remoteViews$OnClickHandler);
                        }
                        else {
                            remoteView.reapply(inflationProgress.packageContext, view, remoteViews$OnClickHandler);
                        }
                        Log.wtf("NotifContentInflater", "Async Inflation failed but normal inflation finished normally.", (Throwable)ex);
                        this.onViewApplied(view);
                    }
                    catch (Exception ex2) {
                        hashMap.remove(n2);
                        handleInflationError(hashMap, ex, expandableNotificationRow.getEntry(), inflationCallback);
                    }
                }
                
                public void onViewApplied(final View resultView) {
                    if (b2) {
                        resultView.setIsRootNamespace(true);
                        applyCallback.setResultView(resultView);
                    }
                    else {
                        final NotificationViewWrapper val$existingWrapper = notificationViewWrapper;
                        if (val$existingWrapper != null) {
                            val$existingWrapper.onReinflated();
                        }
                    }
                    hashMap.remove(n2);
                    finishIfDone(inflationProgress, n, notifRemoteViewCache, hashMap, inflationCallback, notificationEntry, expandableNotificationRow);
                }
                
                public void onViewInflated(final View view) {
                    if (view instanceof ImageMessageConsumer) {
                        ((ImageMessageConsumer)view).setImageResolver((ImageResolver)expandableNotificationRow.getImageResolver());
                    }
                }
            };
            CancellationSignal value;
            if (b2) {
                value = remoteView.applyAsync(inflationProgress.packageContext, (ViewGroup)notificationContentView, executor, (RemoteViews$OnViewAppliedListener)remoteViews$OnViewAppliedListener, remoteViews$OnClickHandler);
            }
            else {
                value = remoteView.reapplyAsync(inflationProgress.packageContext, view, executor, (RemoteViews$OnViewAppliedListener)remoteViews$OnViewAppliedListener, remoteViews$OnClickHandler);
            }
            hashMap.put(n2, value);
            return;
        }
        while (true) {
            if (b2) {
                try {
                    final View apply = remoteView.apply(inflationProgress.packageContext, (ViewGroup)notificationContentView, remoteViews$OnClickHandler);
                    apply.setIsRootNamespace(true);
                    applyCallback.setResultView(apply);
                    return;
                    remoteView.reapply(inflationProgress.packageContext, view, remoteViews$OnClickHandler);
                    notificationViewWrapper.onReinflated();
                }
                catch (Exception ex) {
                    handleInflationError(hashMap, ex, expandableNotificationRow.getEntry(), inflationCallback);
                    hashMap.put(n2, new CancellationSignal());
                }
                return;
            }
            continue;
        }
    }
    
    @VisibleForTesting
    static boolean canReapplyRemoteView(final RemoteViews remoteViews, final RemoteViews remoteViews2) {
        final boolean b = true;
        if (remoteViews == null) {
            final boolean b2 = b;
            if (remoteViews2 == null) {
                return b2;
            }
        }
        return remoteViews != null && remoteViews2 != null && remoteViews2.getPackage() != null && remoteViews.getPackage() != null && remoteViews.getPackage().equals(remoteViews2.getPackage()) && remoteViews.getLayoutId() == remoteViews2.getLayoutId() && !remoteViews2.hasFlags(1) && b;
    }
    
    private void cancelContentViewFrees(final ExpandableNotificationRow expandableNotificationRow, final int n) {
        if ((n & 0x1) != 0x0) {
            expandableNotificationRow.getPrivateLayout().removeContentInactiveRunnable(0);
        }
        if ((n & 0x2) != 0x0) {
            expandableNotificationRow.getPrivateLayout().removeContentInactiveRunnable(1);
        }
        if ((n & 0x4) != 0x0) {
            expandableNotificationRow.getPrivateLayout().removeContentInactiveRunnable(2);
        }
        if ((n & 0x8) != 0x0) {
            expandableNotificationRow.getPublicLayout().removeContentInactiveRunnable(0);
        }
    }
    
    private static RemoteViews createContentView(final Notification$Builder notification$Builder, final boolean b, final boolean b2) {
        if (b) {
            return notification$Builder.makeLowPriorityContentView(false);
        }
        return notification$Builder.createContentView(b2);
    }
    
    private static RemoteViews createExpandedView(final Notification$Builder notification$Builder, final boolean b) {
        final RemoteViews bigContentView = notification$Builder.createBigContentView();
        if (bigContentView != null) {
            return bigContentView;
        }
        if (b) {
            final RemoteViews contentView = notification$Builder.createContentView();
            Notification$Builder.makeHeaderExpanded(contentView);
            return contentView;
        }
        return null;
    }
    
    private static InflationProgress createRemoteViews(final int n, final Notification$Builder notification$Builder, final boolean b, final boolean b2, final boolean b3, final boolean b4, final Context packageContext) {
        final InflationProgress inflationProgress = new InflationProgress();
        final boolean b5 = b && !b2;
        if ((n & 0x1) != 0x0) {
            inflationProgress.newContentView = createContentView(notification$Builder, b5, b3);
        }
        if ((n & 0x2) != 0x0) {
            inflationProgress.newExpandedView = createExpandedView(notification$Builder, b5);
        }
        if ((n & 0x4) != 0x0) {
            inflationProgress.newHeadsUpView = notification$Builder.createHeadsUpContentView(b4);
        }
        if ((n & 0x8) != 0x0) {
            inflationProgress.newPublicView = notification$Builder.makePublicContentView(b5);
        }
        inflationProgress.packageContext = packageContext;
        inflationProgress.headsUpStatusBarText = notification$Builder.getHeadsUpStatusBarText(false);
        inflationProgress.headsUpStatusBarTextPublic = notification$Builder.getHeadsUpStatusBarText(true);
        return inflationProgress;
    }
    
    private static boolean finishIfDone(final InflationProgress inflationProgress, final int n, final NotifRemoteViewCache notifRemoteViewCache, final HashMap<Integer, CancellationSignal> hashMap, final InflationCallback inflationCallback, final NotificationEntry notificationEntry, final ExpandableNotificationRow expandableNotificationRow) {
        Assert.isMainThread();
        final NotificationContentView privateLayout = expandableNotificationRow.getPrivateLayout();
        final NotificationContentView publicLayout = expandableNotificationRow.getPublicLayout();
        final boolean empty = hashMap.isEmpty();
        boolean expandable = false;
        if (empty) {
            if ((n & 0x1) != 0x0) {
                if (inflationProgress.inflatedContentView != null) {
                    privateLayout.setContractedChild(inflationProgress.inflatedContentView);
                    notifRemoteViewCache.putCachedView(notificationEntry, 1, inflationProgress.newContentView);
                }
                else if (notifRemoteViewCache.hasCachedView(notificationEntry, 1)) {
                    notifRemoteViewCache.putCachedView(notificationEntry, 1, inflationProgress.newContentView);
                }
            }
            if ((n & 0x2) != 0x0) {
                if (inflationProgress.inflatedExpandedView != null) {
                    privateLayout.setExpandedChild(inflationProgress.inflatedExpandedView);
                    notifRemoteViewCache.putCachedView(notificationEntry, 2, inflationProgress.newExpandedView);
                }
                else if (inflationProgress.newExpandedView == null) {
                    privateLayout.setExpandedChild(null);
                    notifRemoteViewCache.removeCachedView(notificationEntry, 2);
                }
                else if (notifRemoteViewCache.hasCachedView(notificationEntry, 2)) {
                    notifRemoteViewCache.putCachedView(notificationEntry, 2, inflationProgress.newExpandedView);
                }
                if (inflationProgress.newExpandedView != null) {
                    privateLayout.setExpandedInflatedSmartReplies(inflationProgress.expandedInflatedSmartReplies);
                }
                else {
                    privateLayout.setExpandedInflatedSmartReplies(null);
                }
                if (inflationProgress.newExpandedView != null) {
                    expandable = true;
                }
                expandableNotificationRow.setExpandable(expandable);
            }
            if ((n & 0x4) != 0x0) {
                if (inflationProgress.inflatedHeadsUpView != null) {
                    privateLayout.setHeadsUpChild(inflationProgress.inflatedHeadsUpView);
                    notifRemoteViewCache.putCachedView(notificationEntry, 4, inflationProgress.newHeadsUpView);
                }
                else if (inflationProgress.newHeadsUpView == null) {
                    privateLayout.setHeadsUpChild(null);
                    notifRemoteViewCache.removeCachedView(notificationEntry, 4);
                }
                else if (notifRemoteViewCache.hasCachedView(notificationEntry, 4)) {
                    notifRemoteViewCache.putCachedView(notificationEntry, 4, inflationProgress.newHeadsUpView);
                }
                if (inflationProgress.newHeadsUpView != null) {
                    privateLayout.setHeadsUpInflatedSmartReplies(inflationProgress.headsUpInflatedSmartReplies);
                }
                else {
                    privateLayout.setHeadsUpInflatedSmartReplies(null);
                }
            }
            if ((n & 0x8) != 0x0) {
                if (inflationProgress.inflatedPublicView != null) {
                    publicLayout.setContractedChild(inflationProgress.inflatedPublicView);
                    notifRemoteViewCache.putCachedView(notificationEntry, 8, inflationProgress.newPublicView);
                }
                else if (notifRemoteViewCache.hasCachedView(notificationEntry, 8)) {
                    notifRemoteViewCache.putCachedView(notificationEntry, 8, inflationProgress.newPublicView);
                }
            }
            notificationEntry.headsUpStatusBarText = inflationProgress.headsUpStatusBarText;
            notificationEntry.headsUpStatusBarTextPublic = inflationProgress.headsUpStatusBarTextPublic;
            if (inflationCallback != null) {
                inflationCallback.onAsyncInflationFinished(notificationEntry);
            }
            return true;
        }
        return false;
    }
    
    private void freeNotificationView(final NotificationEntry notificationEntry, final ExpandableNotificationRow expandableNotificationRow, final int n) {
        if (n != 1) {
            if (n != 2) {
                if (n != 4) {
                    if (n == 8) {
                        expandableNotificationRow.getPublicLayout().performWhenContentInactive(0, new _$$Lambda$NotificationContentInflater$rC0T9d2J8ihtZ7IYiND323QEy9I(this, expandableNotificationRow, notificationEntry));
                    }
                }
                else {
                    expandableNotificationRow.getPrivateLayout().performWhenContentInactive(2, new _$$Lambda$NotificationContentInflater$Ti73O4iq7m_j5nfG6KVanlUEnpY(this, expandableNotificationRow, notificationEntry));
                }
            }
            else {
                expandableNotificationRow.getPrivateLayout().performWhenContentInactive(1, new _$$Lambda$NotificationContentInflater$FvWiyXhpHqM8g_4pJO_S1w2v23M(this, expandableNotificationRow, notificationEntry));
            }
        }
        else {
            expandableNotificationRow.getPrivateLayout().performWhenContentInactive(0, new _$$Lambda$NotificationContentInflater$ILEJDQPFCuM8cozWhrmF0DOW6HA(this, expandableNotificationRow, notificationEntry));
        }
    }
    
    private static void handleInflationError(final HashMap<Integer, CancellationSignal> hashMap, final Exception ex, final NotificationEntry notificationEntry, final InflationCallback inflationCallback) {
        Assert.isMainThread();
        hashMap.values().forEach((Consumer<? super Object>)_$$Lambda$POlPJz26zF5Nt5Z2kVGSqFxN8Co.INSTANCE);
        if (inflationCallback != null) {
            inflationCallback.handleInflationException(notificationEntry, ex);
        }
    }
    
    private static InflationProgress inflateSmartReplyViews(final InflationProgress inflationProgress, final int n, final NotificationEntry notificationEntry, final Context context, final Context context2, final HeadsUpManager headsUpManager, final SmartReplyConstants smartReplyConstants, final SmartReplyController smartReplyController, final InflatedSmartReplies.SmartRepliesAndActions smartRepliesAndActions) {
        if ((n & 0x2) != 0x0 && inflationProgress.newExpandedView != null) {
            inflationProgress.expandedInflatedSmartReplies = InflatedSmartReplies.inflate(context, context2, notificationEntry, smartReplyConstants, smartReplyController, headsUpManager, smartRepliesAndActions);
        }
        if ((n & 0x4) != 0x0 && inflationProgress.newHeadsUpView != null) {
            inflationProgress.headsUpInflatedSmartReplies = InflatedSmartReplies.inflate(context, context2, notificationEntry, smartReplyConstants, smartReplyController, headsUpManager, smartRepliesAndActions);
        }
        return inflationProgress;
    }
    
    @Override
    public void bindContent(final NotificationEntry notificationEntry, final ExpandableNotificationRow expandableNotificationRow, final int n, final BindParams bindParams, final boolean b, final InflationCallback inflationCallback) {
        if (expandableNotificationRow.isRemoved()) {
            return;
        }
        expandableNotificationRow.getImageResolver().preloadImages(notificationEntry.getSbn().getNotification());
        if (b) {
            this.mRemoteViewCache.clearCache(notificationEntry);
        }
        this.cancelContentViewFrees(expandableNotificationRow, n);
        final AsyncInflationTask asyncInflationTask = new AsyncInflationTask(this.mBgExecutor, this.mInflateSynchronously, n, this.mRemoteViewCache, notificationEntry, (SmartReplyConstants)this.mSmartReplyConstants.get(), (SmartReplyController)this.mSmartReplyController.get(), this.mConversationProcessor, expandableNotificationRow, bindParams.isLowPriority, bindParams.isChildInGroup, bindParams.usesIncreasedHeight, bindParams.usesIncreasedHeadsUpHeight, inflationCallback, this.mRemoteInputManager.getRemoteViewsOnClickHandler());
        if (this.mInflateSynchronously) {
            asyncInflationTask.onPostExecute(asyncInflationTask.doInBackground(new Void[0]));
        }
        else {
            asyncInflationTask.executeOnExecutor(this.mBgExecutor, (Object[])new Void[0]);
        }
    }
    
    @Override
    public void cancelBind(final NotificationEntry notificationEntry, final ExpandableNotificationRow expandableNotificationRow) {
        notificationEntry.abortTask();
    }
    
    @VisibleForTesting
    InflationProgress inflateNotificationViews(final NotificationEntry notificationEntry, final ExpandableNotificationRow expandableNotificationRow, final BindParams bindParams, final boolean b, final int n, final Notification$Builder notification$Builder, final Context context) {
        final InflationProgress remoteViews = createRemoteViews(n, notification$Builder, bindParams.isLowPriority, bindParams.isChildInGroup, bindParams.usesIncreasedHeight, bindParams.usesIncreasedHeadsUpHeight, context);
        inflateSmartReplyViews(remoteViews, n, notificationEntry, expandableNotificationRow.getContext(), context, expandableNotificationRow.getHeadsUpManager(), this.mSmartReplyConstants.get(), this.mSmartReplyController.get(), expandableNotificationRow.getExistingSmartRepliesAndActions());
        apply(this.mBgExecutor, b, remoteViews, n, this.mRemoteViewCache, notificationEntry, expandableNotificationRow, this.mRemoteInputManager.getRemoteViewsOnClickHandler(), null);
        return remoteViews;
    }
    
    @VisibleForTesting
    public void setInflateSynchronously(final boolean mInflateSynchronously) {
        this.mInflateSynchronously = mInflateSynchronously;
    }
    
    @Override
    public void unbindContent(final NotificationEntry notificationEntry, final ExpandableNotificationRow expandableNotificationRow, int i) {
        for (int n = 1; i != 0; i &= n, n <<= 1) {
            if ((i & n) != 0x0) {
                this.freeNotificationView(notificationEntry, expandableNotificationRow, n);
            }
        }
    }
    
    @VisibleForTesting
    abstract static class ApplyCallback
    {
        public abstract RemoteViews getRemoteView();
        
        public abstract void setResultView(final View p0);
    }
    
    public static class AsyncInflationTask extends AsyncTask<Void, Void, InflationProgress> implements InflationCallback, InflationTask
    {
        private final Executor mBgExecutor;
        private final InflationCallback mCallback;
        private CancellationSignal mCancellationSignal;
        private final Context mContext;
        private final ConversationNotificationProcessor mConversationProcessor;
        private final NotificationEntry mEntry;
        private Exception mError;
        private final boolean mInflateSynchronously;
        private final boolean mIsChildInGroup;
        private final boolean mIsLowPriority;
        private final int mReInflateFlags;
        private final NotifRemoteViewCache mRemoteViewCache;
        private RemoteViews$OnClickHandler mRemoteViewClickHandler;
        private ExpandableNotificationRow mRow;
        private final SmartReplyConstants mSmartReplyConstants;
        private final SmartReplyController mSmartReplyController;
        private final boolean mUsesIncreasedHeadsUpHeight;
        private final boolean mUsesIncreasedHeight;
        
        private AsyncInflationTask(final Executor mBgExecutor, final boolean mInflateSynchronously, final int mReInflateFlags, final NotifRemoteViewCache mRemoteViewCache, final NotificationEntry mEntry, final SmartReplyConstants mSmartReplyConstants, final SmartReplyController mSmartReplyController, final ConversationNotificationProcessor mConversationProcessor, final ExpandableNotificationRow mRow, final boolean mIsLowPriority, final boolean mIsChildInGroup, final boolean mUsesIncreasedHeight, final boolean mUsesIncreasedHeadsUpHeight, final InflationCallback mCallback, final RemoteViews$OnClickHandler mRemoteViewClickHandler) {
            this.mEntry = mEntry;
            this.mRow = mRow;
            this.mSmartReplyConstants = mSmartReplyConstants;
            this.mSmartReplyController = mSmartReplyController;
            this.mBgExecutor = mBgExecutor;
            this.mInflateSynchronously = mInflateSynchronously;
            this.mReInflateFlags = mReInflateFlags;
            this.mRemoteViewCache = mRemoteViewCache;
            this.mContext = mRow.getContext();
            this.mIsLowPriority = mIsLowPriority;
            this.mIsChildInGroup = mIsChildInGroup;
            this.mUsesIncreasedHeight = mUsesIncreasedHeight;
            this.mUsesIncreasedHeadsUpHeight = mUsesIncreasedHeadsUpHeight;
            this.mRemoteViewClickHandler = mRemoteViewClickHandler;
            this.mCallback = mCallback;
            this.mConversationProcessor = mConversationProcessor;
            mEntry.setInflationTask(this);
        }
        
        private void handleError(final Exception obj) {
            this.mEntry.onInflationTaskFinished();
            final StatusBarNotification sbn = this.mEntry.getSbn();
            final StringBuilder sb = new StringBuilder();
            sb.append(sbn.getPackageName());
            sb.append("/0x");
            sb.append(Integer.toHexString(sbn.getId()));
            final String string = sb.toString();
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("couldn't inflate view for notification ");
            sb2.append(string);
            Log.e("StatusBar", sb2.toString(), (Throwable)obj);
            final InflationCallback mCallback = this.mCallback;
            if (mCallback != null) {
                final NotificationEntry entry = this.mRow.getEntry();
                final StringBuilder sb3 = new StringBuilder();
                sb3.append("Couldn't inflate contentViews");
                sb3.append(obj);
                mCallback.handleInflationException(entry, new InflationException(sb3.toString()));
            }
        }
        
        public void abort() {
            this.cancel(true);
            final CancellationSignal mCancellationSignal = this.mCancellationSignal;
            if (mCancellationSignal != null) {
                mCancellationSignal.cancel();
            }
        }
        
        protected InflationProgress doInBackground(final Void... array) {
            try {
                final StatusBarNotification sbn = this.mEntry.getSbn();
                final Notification$Builder recoverBuilder = Notification$Builder.recoverBuilder(this.mContext, sbn.getNotification());
                final Context packageContext = sbn.getPackageContext(this.mContext);
                Object o;
                if (recoverBuilder.usesTemplate()) {
                    o = new RtlEnabledContext(packageContext);
                }
                else {
                    o = packageContext;
                }
                final Notification notification = sbn.getNotification();
                if (notification.isMediaNotification()) {
                    new MediaNotificationProcessor(this.mContext, (Context)o).processNotification(notification, recoverBuilder);
                }
                if (this.mEntry.getRanking().isConversation()) {
                    this.mConversationProcessor.processNotification(this.mEntry, recoverBuilder);
                }
                final InflationProgress access$1600 = createRemoteViews(this.mReInflateFlags, recoverBuilder, this.mIsLowPriority, this.mIsChildInGroup, this.mUsesIncreasedHeight, this.mUsesIncreasedHeadsUpHeight, (Context)o);
                NotificationContentInflater.access$1700(access$1600, this.mReInflateFlags, this.mEntry, this.mRow.getContext(), (Context)o, this.mRow.getHeadsUpManager(), this.mSmartReplyConstants, this.mSmartReplyController, this.mRow.getExistingSmartRepliesAndActions());
                return access$1600;
            }
            catch (Exception mError) {
                this.mError = mError;
                return null;
            }
        }
        
        @VisibleForTesting
        public int getReInflateFlags() {
            return this.mReInflateFlags;
        }
        
        public void handleInflationException(final NotificationEntry notificationEntry, final Exception ex) {
            this.handleError(ex);
        }
        
        public void onAsyncInflationFinished(final NotificationEntry notificationEntry) {
            this.mEntry.onInflationTaskFinished();
            this.mRow.onNotificationUpdated();
            final InflationCallback mCallback = this.mCallback;
            if (mCallback != null) {
                mCallback.onAsyncInflationFinished(this.mEntry);
            }
            this.mRow.getImageResolver().purgeCache();
        }
        
        protected void onPostExecute(final InflationProgress inflationProgress) {
            final Exception mError = this.mError;
            if (mError == null) {
                this.mCancellationSignal = apply(this.mBgExecutor, this.mInflateSynchronously, inflationProgress, this.mReInflateFlags, this.mRemoteViewCache, this.mEntry, this.mRow, this.mRemoteViewClickHandler, this);
            }
            else {
                this.handleError(mError);
            }
        }
        
        private class RtlEnabledContext extends ContextWrapper
        {
            private RtlEnabledContext(final AsyncInflationTask asyncInflationTask, final Context context) {
                super(context);
            }
            
            public ApplicationInfo getApplicationInfo() {
                final ApplicationInfo applicationInfo = super.getApplicationInfo();
                applicationInfo.flags |= 0x400000;
                return applicationInfo;
            }
        }
    }
    
    @VisibleForTesting
    static class InflationProgress
    {
        private InflatedSmartReplies expandedInflatedSmartReplies;
        private InflatedSmartReplies headsUpInflatedSmartReplies;
        private CharSequence headsUpStatusBarText;
        private CharSequence headsUpStatusBarTextPublic;
        private View inflatedContentView;
        private View inflatedExpandedView;
        private View inflatedHeadsUpView;
        private View inflatedPublicView;
        private RemoteViews newContentView;
        private RemoteViews newExpandedView;
        private RemoteViews newHeadsUpView;
        private RemoteViews newPublicView;
        @VisibleForTesting
        Context packageContext;
    }
}
