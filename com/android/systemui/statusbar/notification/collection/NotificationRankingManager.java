// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection;

import android.app.Notification;
import kotlin.Unit;
import java.util.Objects;
import android.service.notification.NotificationListenerService$Ranking;
import android.service.notification.StatusBarNotification;
import java.util.Iterator;
import kotlin.jvm.functions.Function1;
import kotlin.sequences.SequencesKt;
import kotlin.collections.CollectionsKt;
import java.util.List;
import java.util.Collection;
import kotlin.jvm.functions.Function0;
import kotlin.LazyKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1;
import kotlin.reflect.KDeclarationContainer;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import com.android.systemui.statusbar.notification.NotificationSectionsFeatureManager;
import android.service.notification.NotificationListenerService$RankingMap;
import java.util.Comparator;
import com.android.systemui.statusbar.notification.people.PeopleNotificationIdentifier;
import com.android.systemui.statusbar.notification.NotificationFilter;
import com.android.systemui.statusbar.NotificationMediaManager;
import kotlin.Lazy;
import com.android.systemui.statusbar.notification.NotificationEntryManagerLogger;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import kotlin.reflect.KProperty;

public class NotificationRankingManager
{
    static final /* synthetic */ KProperty[] $$delegatedProperties;
    private final NotificationGroupManager groupManager;
    private final HeadsUpManager headsUpManager;
    private final HighPriorityProvider highPriorityProvider;
    private final NotificationEntryManagerLogger logger;
    private final Lazy mediaManager$delegate;
    private final dagger.Lazy<NotificationMediaManager> mediaManagerLazy;
    private final NotificationFilter notifFilter;
    private final PeopleNotificationIdentifier peopleNotificationIdentifier;
    private final Comparator<NotificationEntry> rankingComparator;
    private NotificationListenerService$RankingMap rankingMap;
    private final NotificationSectionsFeatureManager sectionsFeatureManager;
    
    static {
        final PropertyReference1Impl propertyReference1Impl = new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(NotificationRankingManager.class), "mediaManager", "getMediaManager()Lcom/android/systemui/statusbar/NotificationMediaManager;");
        Reflection.property1(propertyReference1Impl);
        $$delegatedProperties = new KProperty[] { propertyReference1Impl };
    }
    
    public NotificationRankingManager(final dagger.Lazy<NotificationMediaManager> mediaManagerLazy, final NotificationGroupManager groupManager, final HeadsUpManager headsUpManager, final NotificationFilter notifFilter, final NotificationEntryManagerLogger logger, final NotificationSectionsFeatureManager sectionsFeatureManager, final PeopleNotificationIdentifier peopleNotificationIdentifier, final HighPriorityProvider highPriorityProvider) {
        Intrinsics.checkParameterIsNotNull(mediaManagerLazy, "mediaManagerLazy");
        Intrinsics.checkParameterIsNotNull(groupManager, "groupManager");
        Intrinsics.checkParameterIsNotNull(headsUpManager, "headsUpManager");
        Intrinsics.checkParameterIsNotNull(notifFilter, "notifFilter");
        Intrinsics.checkParameterIsNotNull(logger, "logger");
        Intrinsics.checkParameterIsNotNull(sectionsFeatureManager, "sectionsFeatureManager");
        Intrinsics.checkParameterIsNotNull(peopleNotificationIdentifier, "peopleNotificationIdentifier");
        Intrinsics.checkParameterIsNotNull(highPriorityProvider, "highPriorityProvider");
        this.mediaManagerLazy = mediaManagerLazy;
        this.groupManager = groupManager;
        this.headsUpManager = headsUpManager;
        this.notifFilter = notifFilter;
        this.logger = logger;
        this.sectionsFeatureManager = sectionsFeatureManager;
        this.peopleNotificationIdentifier = peopleNotificationIdentifier;
        this.highPriorityProvider = highPriorityProvider;
        this.mediaManager$delegate = LazyKt.lazy((Function0<?>)new NotificationRankingManager$mediaManager.NotificationRankingManager$mediaManager$2(this));
        this.rankingComparator = (Comparator<NotificationEntry>)new NotificationRankingManager$rankingComparator.NotificationRankingManager$rankingComparator$1(this);
    }
    
    private final void assignBucketForEntry(final NotificationEntry notificationEntry) {
        this.setBucket(notificationEntry, notificationEntry.isRowHeadsUp(), this.isImportantMedia(notificationEntry), NotificationRankingManagerKt.access$isSystemMax(notificationEntry));
    }
    
    private final List<NotificationEntry> filterAndSortLocked(final Collection<NotificationEntry> collection, final String s) {
        this.logger.logFilterAndSort(s);
        final List<NotificationEntry> list = SequencesKt.toList(SequencesKt.sortedWith(SequencesKt.filterNot(CollectionsKt.asSequence((Iterable<? extends NotificationEntry>)collection), (Function1<? super NotificationEntry, Boolean>)new NotificationRankingManager$filterAndSortLocked$filtered.NotificationRankingManager$filterAndSortLocked$filtered$1(this.notifFilter)), (Comparator<? super NotificationEntry>)this.rankingComparator));
        final Iterator<NotificationEntry> iterator = list.iterator();
        while (iterator.hasNext()) {
            this.assignBucketForEntry(iterator.next());
        }
        return list;
    }
    
    private final NotificationMediaManager getMediaManager() {
        final Lazy mediaManager$delegate = this.mediaManager$delegate;
        final KProperty kProperty = NotificationRankingManager.$$delegatedProperties[0];
        return mediaManager$delegate.getValue();
    }
    
    private final int getPeopleNotificationType(final NotificationEntry notificationEntry) {
        final PeopleNotificationIdentifier peopleNotificationIdentifier = this.peopleNotificationIdentifier;
        final StatusBarNotification sbn = notificationEntry.getSbn();
        Intrinsics.checkExpressionValueIsNotNull(sbn, "sbn");
        final NotificationListenerService$Ranking ranking = notificationEntry.getRanking();
        Intrinsics.checkExpressionValueIsNotNull(ranking, "ranking");
        return peopleNotificationIdentifier.getPeopleNotificationType(sbn, ranking);
    }
    
    private final boolean getUsePeopleFiltering() {
        return this.sectionsFeatureManager.isFilteringEnabled();
    }
    
    private final boolean isHighPriority(final NotificationEntry notificationEntry) {
        return this.highPriorityProvider.isHighPriority(notificationEntry);
    }
    
    private final boolean isImportantMedia(final NotificationEntry notificationEntry) {
        final NotificationListenerService$Ranking ranking = notificationEntry.getRanking();
        Intrinsics.checkExpressionValueIsNotNull(ranking, "entry.ranking");
        final int importance = ranking.getImportance();
        final String key = notificationEntry.getKey();
        final NotificationMediaManager mediaManager = this.getMediaManager();
        Intrinsics.checkExpressionValueIsNotNull(mediaManager, "mediaManager");
        final boolean equal = Intrinsics.areEqual(key, mediaManager.getMediaNotificationKey());
        boolean b = true;
        if (!equal || importance <= 1) {
            b = false;
        }
        return b;
    }
    
    private final void setBucket(final NotificationEntry notificationEntry, final boolean b, final boolean b2, final boolean b3) {
        if (this.getUsePeopleFiltering() && b) {
            notificationEntry.setBucket(0);
        }
        else if (this.getUsePeopleFiltering() && this.getPeopleNotificationType(notificationEntry) != 0) {
            notificationEntry.setBucket(2);
        }
        else if (!b && !b2 && !b3 && !this.isHighPriority(notificationEntry)) {
            notificationEntry.setBucket(4);
        }
        else {
            notificationEntry.setBucket(3);
        }
    }
    
    private final void updateRankingForEntries(final Iterable<NotificationEntry> iterable) {
        final NotificationListenerService$RankingMap rankingMap = this.rankingMap;
        if (rankingMap != null) {
            synchronized (iterable) {
                for (final NotificationEntry notificationEntry : iterable) {
                    final NotificationListenerService$Ranking ranking = new NotificationListenerService$Ranking();
                    if (!rankingMap.getRanking(notificationEntry.getKey(), ranking)) {
                        continue;
                    }
                    notificationEntry.setRanking(ranking);
                    final String overrideGroupKey = ranking.getOverrideGroupKey();
                    final StatusBarNotification sbn = notificationEntry.getSbn();
                    Intrinsics.checkExpressionValueIsNotNull(sbn, "entry.sbn");
                    if (Objects.equals(sbn.getOverrideGroupKey(), overrideGroupKey)) {
                        continue;
                    }
                    final StatusBarNotification sbn2 = notificationEntry.getSbn();
                    Intrinsics.checkExpressionValueIsNotNull(sbn2, "entry.sbn");
                    final String groupKey = sbn2.getGroupKey();
                    final StatusBarNotification sbn3 = notificationEntry.getSbn();
                    Intrinsics.checkExpressionValueIsNotNull(sbn3, "entry.sbn");
                    final boolean group = sbn3.isGroup();
                    final StatusBarNotification sbn4 = notificationEntry.getSbn();
                    Intrinsics.checkExpressionValueIsNotNull(sbn4, "entry.sbn");
                    final Notification notification = sbn4.getNotification();
                    Intrinsics.checkExpressionValueIsNotNull(notification, "entry.sbn.notification");
                    final boolean groupSummary = notification.isGroupSummary();
                    final StatusBarNotification sbn5 = notificationEntry.getSbn();
                    Intrinsics.checkExpressionValueIsNotNull(sbn5, "entry.sbn");
                    sbn5.setOverrideGroupKey(overrideGroupKey);
                    this.groupManager.onEntryUpdated(notificationEntry, groupKey, group, groupSummary);
                }
                final Unit instance = Unit.INSTANCE;
            }
        }
    }
    
    public final NotificationListenerService$RankingMap getRankingMap() {
        return this.rankingMap;
    }
    
    public final List<NotificationEntry> updateRanking(final NotificationListenerService$RankingMap rankingMap, final Collection<NotificationEntry> collection, final String s) {
        Intrinsics.checkParameterIsNotNull(collection, "entries");
        Intrinsics.checkParameterIsNotNull(s, "reason");
        if (rankingMap != null) {
            this.rankingMap = rankingMap;
            this.updateRankingForEntries(collection);
        }
        synchronized (this) {
            return this.filterAndSortLocked(collection, s);
        }
    }
}
