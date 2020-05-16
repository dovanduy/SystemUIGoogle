// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coalescer;

import kotlin.jvm.internal.Intrinsics;
import android.service.notification.StatusBarNotification;
import android.service.notification.NotificationListenerService$Ranking;

public final class CoalescedEvent
{
    private EventBatch batch;
    private final String key;
    private int position;
    private NotificationListenerService$Ranking ranking;
    private StatusBarNotification sbn;
    
    public CoalescedEvent(final String key, final int position, final StatusBarNotification sbn, final NotificationListenerService$Ranking ranking, final EventBatch batch) {
        Intrinsics.checkParameterIsNotNull(key, "key");
        Intrinsics.checkParameterIsNotNull(sbn, "sbn");
        Intrinsics.checkParameterIsNotNull(ranking, "ranking");
        this.key = key;
        this.position = position;
        this.sbn = sbn;
        this.ranking = ranking;
        this.batch = batch;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this != o) {
            if (o instanceof CoalescedEvent) {
                final CoalescedEvent coalescedEvent = (CoalescedEvent)o;
                if (Intrinsics.areEqual(this.key, coalescedEvent.key) && this.position == coalescedEvent.position && Intrinsics.areEqual(this.sbn, coalescedEvent.sbn) && Intrinsics.areEqual(this.ranking, coalescedEvent.ranking) && Intrinsics.areEqual(this.batch, coalescedEvent.batch)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public final EventBatch getBatch() {
        return this.batch;
    }
    
    public final String getKey() {
        return this.key;
    }
    
    public final int getPosition() {
        return this.position;
    }
    
    public final NotificationListenerService$Ranking getRanking() {
        return this.ranking;
    }
    
    public final StatusBarNotification getSbn() {
        return this.sbn;
    }
    
    @Override
    public int hashCode() {
        final String key = this.key;
        int hashCode = 0;
        int hashCode2;
        if (key != null) {
            hashCode2 = key.hashCode();
        }
        else {
            hashCode2 = 0;
        }
        final int hashCode3 = Integer.hashCode(this.position);
        final StatusBarNotification sbn = this.sbn;
        int hashCode4;
        if (sbn != null) {
            hashCode4 = sbn.hashCode();
        }
        else {
            hashCode4 = 0;
        }
        final NotificationListenerService$Ranking ranking = this.ranking;
        int hashCode5;
        if (ranking != null) {
            hashCode5 = ranking.hashCode();
        }
        else {
            hashCode5 = 0;
        }
        final EventBatch batch = this.batch;
        if (batch != null) {
            hashCode = batch.hashCode();
        }
        return (((hashCode2 * 31 + hashCode3) * 31 + hashCode4) * 31 + hashCode5) * 31 + hashCode;
    }
    
    public final void setBatch(final EventBatch batch) {
        this.batch = batch;
    }
    
    public final void setRanking(final NotificationListenerService$Ranking ranking) {
        Intrinsics.checkParameterIsNotNull(ranking, "<set-?>");
        this.ranking = ranking;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CoalescedEvent(key=");
        sb.append(this.key);
        sb.append(')');
        return sb.toString();
    }
}
