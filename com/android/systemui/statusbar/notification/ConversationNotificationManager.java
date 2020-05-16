// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import android.app.Notification;
import kotlin.Pair;
import java.util.Map;
import kotlin.collections.MapsKt;
import android.app.Notification$Builder;
import java.util.function.BiFunction;
import com.android.systemui.statusbar.notification.row.NotificationContentView;
import android.app.NotificationChannel;
import android.service.notification.StatusBarNotification;
import java.util.Iterator;
import java.util.Set;
import com.android.internal.widget.ConversationLayout;
import kotlin.sequences.Sequence;
import kotlin.collections.ArraysKt;
import kotlin.jvm.functions.Function1;
import kotlin.sequences.SequencesKt;
import kotlin.collections.CollectionsKt;
import android.service.notification.NotificationListenerService$RankingMap;
import com.android.internal.statusbar.NotificationVisibility;
import android.service.notification.NotificationListenerService$Ranking;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import kotlin.jvm.internal.Intrinsics;
import java.util.concurrent.ConcurrentHashMap;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import android.content.Context;

public final class ConversationNotificationManager
{
    private final Context context;
    private boolean notifPanelCollapsed;
    private final NotificationEntryManager notificationEntryManager;
    private final NotificationGroupManager notificationGroupManager;
    private final ConcurrentHashMap<String, ConversationState> states;
    
    public ConversationNotificationManager(final NotificationEntryManager notificationEntryManager, final NotificationGroupManager notificationGroupManager, final Context context) {
        Intrinsics.checkParameterIsNotNull(notificationEntryManager, "notificationEntryManager");
        Intrinsics.checkParameterIsNotNull(notificationGroupManager, "notificationGroupManager");
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.notificationEntryManager = notificationEntryManager;
        this.notificationGroupManager = notificationGroupManager;
        this.context = context;
        this.states = new ConcurrentHashMap<String, ConversationState>();
        this.notifPanelCollapsed = true;
        this.notificationEntryManager.addNotificationEntryListener(new NotificationEntryListener() {
            final /* synthetic */ ConversationNotificationManager this$0;
            
            @Override
            public void onEntryInflated(final NotificationEntry notificationEntry) {
                Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
                final NotificationListenerService$Ranking ranking = notificationEntry.getRanking();
                Intrinsics.checkExpressionValueIsNotNull(ranking, "entry.ranking");
                if (!ranking.isConversation()) {
                    return;
                }
                final ConversationNotificationManager$1$onEntryInflated.ConversationNotificationManager$1$onEntryInflated$1 conversationNotificationManager$1$onEntryInflated$1 = new ConversationNotificationManager$1$onEntryInflated.ConversationNotificationManager$1$onEntryInflated$1(this, notificationEntry);
                final ExpandableNotificationRow row = notificationEntry.getRow();
                if (row != null) {
                    row.setOnExpansionChangedListener((ExpandableNotificationRow.OnExpansionChangedListener)new ConversationNotificationManager$1$onEntryInflated.ConversationNotificationManager$1$onEntryInflated$2(notificationEntry, conversationNotificationManager$1$onEntryInflated$1));
                }
                final ExpandableNotificationRow row2 = notificationEntry.getRow();
                boolean b = true;
                if (row2 == null || !row2.isExpanded()) {
                    b = false;
                }
                conversationNotificationManager$1$onEntryInflated$1.invoke(b);
            }
            
            @Override
            public void onEntryReinflated(final NotificationEntry notificationEntry) {
                Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
                this.onEntryInflated(notificationEntry);
            }
            
            @Override
            public void onEntryRemoved(final NotificationEntry notificationEntry, final NotificationVisibility notificationVisibility, final boolean b, final int n) {
                Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
                ConversationNotificationManager.this.removeTrackedEntry(notificationEntry);
            }
            
            @Override
            public void onNotificationRankingUpdated(final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
                Intrinsics.checkParameterIsNotNull(notificationListenerService$RankingMap, "rankingMap");
                final ConversationNotificationManager$1$onNotificationRankingUpdated.ConversationNotificationManager$1$onNotificationRankingUpdated$1 instance = ConversationNotificationManager$1$onNotificationRankingUpdated.ConversationNotificationManager$1$onNotificationRankingUpdated$1.INSTANCE;
                final NotificationListenerService$Ranking notificationListenerService$Ranking = new NotificationListenerService$Ranking();
                final Set keySet = ConversationNotificationManager.access$getStates$p(this.this$0).keySet();
                Intrinsics.checkExpressionValueIsNotNull(keySet, "states.keys");
                for (final NotificationEntry notificationEntry : SequencesKt.mapNotNull(CollectionsKt.asSequence((Iterable<?>)keySet), (Function1<? super Object, ? extends NotificationEntry>)new ConversationNotificationManager$1$onNotificationRankingUpdated.ConversationNotificationManager$1$onNotificationRankingUpdated$2(this))) {
                    final StatusBarNotification sbn = notificationEntry.getSbn();
                    Intrinsics.checkExpressionValueIsNotNull(sbn, "entry.sbn");
                    if (notificationListenerService$RankingMap.getRanking(sbn.getKey(), notificationListenerService$Ranking) && notificationListenerService$Ranking.isConversation()) {
                        final NotificationChannel channel = notificationListenerService$Ranking.getChannel();
                        Intrinsics.checkExpressionValueIsNotNull(channel, "ranking.channel");
                        final boolean importantConversation = channel.isImportantConversation();
                        final boolean b = false;
                        int n = 0;
                        final ExpandableNotificationRow row = notificationEntry.getRow();
                        int n2 = b ? 1 : 0;
                        if (row != null) {
                            final NotificationContentView[] layouts = row.getLayouts();
                            n2 = (b ? 1 : 0);
                            if (layouts != null) {
                                final Sequence<NotificationContentView> sequence = ArraysKt.asSequence(layouts);
                                n2 = (b ? 1 : 0);
                                if (sequence != null) {
                                    final Sequence<Object> flatMap = SequencesKt.flatMap((Sequence<?>)sequence, (Function1<? super Object, ? extends Sequence<?>>)ConversationNotificationManager$1$onNotificationRankingUpdated$3.ConversationNotificationManager$1$onNotificationRankingUpdated$3$1.INSTANCE);
                                    n2 = (b ? 1 : 0);
                                    if (flatMap != null) {
                                        final Sequence<Object> mapNotNull = SequencesKt.mapNotNull((Sequence<?>)flatMap, (Function1<? super Object, ?>)ConversationNotificationManager$1$onNotificationRankingUpdated$3.ConversationNotificationManager$1$onNotificationRankingUpdated$3$2.INSTANCE);
                                        n2 = (b ? 1 : 0);
                                        if (mapNotNull != null) {
                                            final Iterator<ConversationLayout> iterator2 = mapNotNull.iterator();
                                            while (true) {
                                                n2 = n;
                                                if (!iterator2.hasNext()) {
                                                    break;
                                                }
                                                final ConversationLayout conversationLayout = iterator2.next();
                                                if (importantConversation == conversationLayout.isImportantConversation()) {
                                                    continue;
                                                }
                                                conversationLayout.setIsImportantConversation(importantConversation);
                                                n = 1;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (n2 == 0) {
                            continue;
                        }
                        ConversationNotificationManager.access$getNotificationGroupManager$p(this.this$0).updateIsolation(notificationEntry);
                    }
                }
            }
        });
    }
    
    public static final /* synthetic */ NotificationGroupManager access$getNotificationGroupManager$p(final ConversationNotificationManager conversationNotificationManager) {
        return conversationNotificationManager.notificationGroupManager;
    }
    
    public static final /* synthetic */ ConcurrentHashMap access$getStates$p(final ConversationNotificationManager conversationNotificationManager) {
        return conversationNotificationManager.states;
    }
    
    private final void removeTrackedEntry(final NotificationEntry notificationEntry) {
        this.states.remove(notificationEntry.getKey());
    }
    
    private final void resetBadgeUi(final ExpandableNotificationRow expandableNotificationRow) {
        final NotificationContentView[] layouts = expandableNotificationRow.getLayouts();
        Object o = null;
        Label_0025: {
            if (layouts != null) {
                o = ArraysKt.asSequence(layouts);
                if (o != null) {
                    break Label_0025;
                }
            }
            o = SequencesKt.emptySequence();
        }
        final Iterator<ConversationLayout> iterator = SequencesKt.mapNotNull(SequencesKt.flatMap((Sequence<?>)o, (Function1<? super Object, ? extends Sequence<?>>)ConversationNotificationManager$resetBadgeUi.ConversationNotificationManager$resetBadgeUi$1.INSTANCE), (Function1<? super Object, ? extends ConversationLayout>)ConversationNotificationManager$resetBadgeUi.ConversationNotificationManager$resetBadgeUi$2.INSTANCE).iterator();
        while (iterator.hasNext()) {
            iterator.next().setUnreadCount(0);
        }
    }
    
    private final void resetCount(final String key) {
        this.states.compute(key, (BiFunction<? super String, ? super ConversationState, ? extends ConversationState>)ConversationNotificationManager$resetCount.ConversationNotificationManager$resetCount$1.INSTANCE);
    }
    
    public final int getUnreadCount(final NotificationEntry notificationEntry, final Notification$Builder notification$Builder) {
        Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
        Intrinsics.checkParameterIsNotNull(notification$Builder, "recoveredBuilder");
        final ConversationState compute = this.states.compute(notificationEntry.getKey(), (BiFunction<? super String, ? super ConversationState, ? extends ConversationState>)new ConversationNotificationManager$getUnreadCount.ConversationNotificationManager$getUnreadCount$1(this, notification$Builder, notificationEntry));
        if (compute != null) {
            return compute.getUnreadCount();
        }
        Intrinsics.throwNpe();
        throw null;
    }
    
    public final void onNotificationPanelExpandStateChanged(final boolean notifPanelCollapsed) {
        this.notifPanelCollapsed = notifPanelCollapsed;
        if (notifPanelCollapsed) {
            return;
        }
        final Map<Object, Object> map = MapsKt.toMap(SequencesKt.mapNotNull((Sequence<?>)MapsKt.asSequence((Map<?, ?>)this.states), (Function1<? super Object, ? extends Pair<?, ?>>)new ConversationNotificationManager$onNotificationPanelExpandStateChanged$expanded.ConversationNotificationManager$onNotificationPanelExpandStateChanged$expanded$1(this)));
        this.states.replaceAll((BiFunction<? super String, ? super ConversationState, ? extends ConversationState>)new ConversationNotificationManager$onNotificationPanelExpandStateChanged.ConversationNotificationManager$onNotificationPanelExpandStateChanged$1((Map)map));
        final Iterator<ExpandableNotificationRow> iterator = SequencesKt.mapNotNull(CollectionsKt.asSequence((Iterable<?>)map.values()), (Function1<? super Object, ? extends ExpandableNotificationRow>)ConversationNotificationManager$onNotificationPanelExpandStateChanged.ConversationNotificationManager$onNotificationPanelExpandStateChanged$2.INSTANCE).iterator();
        while (iterator.hasNext()) {
            this.resetBadgeUi(iterator.next());
        }
    }
    
    private static final class ConversationState
    {
        private final Notification notification;
        private final int unreadCount;
        
        public ConversationState(final int unreadCount, final Notification notification) {
            Intrinsics.checkParameterIsNotNull(notification, "notification");
            this.unreadCount = unreadCount;
            this.notification = notification;
        }
        
        public final ConversationState copy(final int n, final Notification notification) {
            Intrinsics.checkParameterIsNotNull(notification, "notification");
            return new ConversationState(n, notification);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this != o) {
                if (o instanceof ConversationState) {
                    final ConversationState conversationState = (ConversationState)o;
                    if (this.unreadCount == conversationState.unreadCount && Intrinsics.areEqual(this.notification, conversationState.notification)) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        }
        
        public final Notification getNotification() {
            return this.notification;
        }
        
        public final int getUnreadCount() {
            return this.unreadCount;
        }
        
        @Override
        public int hashCode() {
            final int hashCode = Integer.hashCode(this.unreadCount);
            final Notification notification = this.notification;
            int hashCode2;
            if (notification != null) {
                hashCode2 = notification.hashCode();
            }
            else {
                hashCode2 = 0;
            }
            return hashCode * 31 + hashCode2;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("ConversationState(unreadCount=");
            sb.append(this.unreadCount);
            sb.append(", notification=");
            sb.append(this.notification);
            sb.append(")");
            return sb.toString();
        }
    }
}
