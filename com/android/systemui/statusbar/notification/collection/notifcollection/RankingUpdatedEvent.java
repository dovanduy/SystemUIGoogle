// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.notifcollection;

import kotlin.jvm.internal.Intrinsics;
import android.service.notification.NotificationListenerService$RankingMap;

public final class RankingUpdatedEvent extends NotifEvent
{
    private final NotificationListenerService$RankingMap rankingMap;
    
    public RankingUpdatedEvent(final NotificationListenerService$RankingMap rankingMap) {
        Intrinsics.checkParameterIsNotNull(rankingMap, "rankingMap");
        super(null);
        this.rankingMap = rankingMap;
    }
    
    @Override
    public void dispatchToListener(final NotifCollectionListener notifCollectionListener) {
        Intrinsics.checkParameterIsNotNull(notifCollectionListener, "listener");
        notifCollectionListener.onRankingUpdate(this.rankingMap);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof RankingUpdatedEvent && Intrinsics.areEqual(this.rankingMap, ((RankingUpdatedEvent)o).rankingMap));
    }
    
    @Override
    public int hashCode() {
        final NotificationListenerService$RankingMap rankingMap = this.rankingMap;
        int hashCode;
        if (rankingMap != null) {
            hashCode = rankingMap.hashCode();
        }
        else {
            hashCode = 0;
        }
        return hashCode;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("RankingUpdatedEvent(rankingMap=");
        sb.append(this.rankingMap);
        sb.append(")");
        return sb.toString();
    }
}
