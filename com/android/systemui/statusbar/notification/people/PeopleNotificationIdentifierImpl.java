// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.people;

import android.app.NotificationChannel;
import android.service.notification.NotificationListenerService$Ranking;
import java.util.Iterator;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.ArrayList;
import kotlin.jvm.functions.Function1;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import kotlin.collections.CollectionsKt;
import android.service.notification.StatusBarNotification;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.statusbar.phone.NotificationGroupManager;

public final class PeopleNotificationIdentifierImpl implements PeopleNotificationIdentifier
{
    private final NotificationGroupManager groupManager;
    private final NotificationPersonExtractor personExtractor;
    
    public PeopleNotificationIdentifierImpl(final NotificationPersonExtractor personExtractor, final NotificationGroupManager groupManager) {
        Intrinsics.checkParameterIsNotNull(personExtractor, "personExtractor");
        Intrinsics.checkParameterIsNotNull(groupManager, "groupManager");
        this.personExtractor = personExtractor;
        this.groupManager = groupManager;
    }
    
    private final int extractPersonTypeInfo(final StatusBarNotification statusBarNotification) {
        return this.personExtractor.isPersonNotification(statusBarNotification) ? 1 : 0;
    }
    
    private final int getPeopleTypeOfSummary(final StatusBarNotification statusBarNotification) {
        final boolean summaryOfGroup = this.groupManager.isSummaryOfGroup(statusBarNotification);
        final int n = 0;
        int n2 = 0;
        if (!summaryOfGroup) {
            return 0;
        }
        final ArrayList<NotificationEntry> children = this.groupManager.getChildren(statusBarNotification);
        int upperBound = n;
        if (children != null) {
            final Sequence<? extends T> sequence = CollectionsKt.asSequence((Iterable<? extends T>)children);
            upperBound = n;
            if (sequence != null) {
                final Sequence<Object> map = SequencesKt.map((Sequence<?>)sequence, (Function1<? super Object, ?>)new PeopleNotificationIdentifierImpl$getPeopleTypeOfSummary$childTypes.PeopleNotificationIdentifierImpl$getPeopleTypeOfSummary$childTypes$1(this));
                upperBound = n;
                if (map != null) {
                    final Iterator<Number> iterator = map.iterator();
                    do {
                        upperBound = n2;
                        if (!iterator.hasNext()) {
                            break;
                        }
                        upperBound = this.upperBound(n2, iterator.next().intValue());
                    } while ((n2 = upperBound) != 2);
                }
            }
        }
        return upperBound;
    }
    
    private final int getPersonTypeInfo(final NotificationListenerService$Ranking notificationListenerService$Ranking) {
        final boolean conversation = notificationListenerService$Ranking.isConversation();
        final boolean b = true;
        int n;
        if (!conversation) {
            n = 0;
        }
        else {
            final NotificationChannel channel = notificationListenerService$Ranking.getChannel();
            n = (b ? 1 : 0);
            if (channel != null) {
                n = (b ? 1 : 0);
                if (channel.isImportantConversation()) {
                    n = 2;
                }
            }
        }
        return n;
    }
    
    private final int upperBound(final int a, final int b) {
        return Math.max(a, b);
    }
    
    @Override
    public int getPeopleNotificationType(final StatusBarNotification statusBarNotification, final NotificationListenerService$Ranking notificationListenerService$Ranking) {
        Intrinsics.checkParameterIsNotNull(statusBarNotification, "sbn");
        Intrinsics.checkParameterIsNotNull(notificationListenerService$Ranking, "ranking");
        final int personTypeInfo = this.getPersonTypeInfo(notificationListenerService$Ranking);
        int upperBound = 2;
        if (personTypeInfo != 2) {
            final int upperBound2 = this.upperBound(personTypeInfo, this.extractPersonTypeInfo(statusBarNotification));
            if (upperBound2 != 2) {
                upperBound = this.upperBound(upperBound2, this.getPeopleTypeOfSummary(statusBarNotification));
            }
        }
        return upperBound;
    }
}
