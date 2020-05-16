// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.broadcast;

import kotlin.jvm.internal.Intrinsics;
import android.os.UserHandle;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import java.util.concurrent.Executor;

public final class ReceiverData
{
    private final Executor executor;
    private final IntentFilter filter;
    private final BroadcastReceiver receiver;
    private final UserHandle user;
    
    public ReceiverData(final BroadcastReceiver receiver, final IntentFilter filter, final Executor executor, final UserHandle user) {
        Intrinsics.checkParameterIsNotNull(receiver, "receiver");
        Intrinsics.checkParameterIsNotNull(filter, "filter");
        Intrinsics.checkParameterIsNotNull(executor, "executor");
        Intrinsics.checkParameterIsNotNull(user, "user");
        this.receiver = receiver;
        this.filter = filter;
        this.executor = executor;
        this.user = user;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this != o) {
            if (o instanceof ReceiverData) {
                final ReceiverData receiverData = (ReceiverData)o;
                if (Intrinsics.areEqual(this.receiver, receiverData.receiver) && Intrinsics.areEqual(this.filter, receiverData.filter) && Intrinsics.areEqual(this.executor, receiverData.executor) && Intrinsics.areEqual(this.user, receiverData.user)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public final Executor getExecutor() {
        return this.executor;
    }
    
    public final IntentFilter getFilter() {
        return this.filter;
    }
    
    public final BroadcastReceiver getReceiver() {
        return this.receiver;
    }
    
    public final UserHandle getUser() {
        return this.user;
    }
    
    @Override
    public int hashCode() {
        final BroadcastReceiver receiver = this.receiver;
        int hashCode = 0;
        int hashCode2;
        if (receiver != null) {
            hashCode2 = receiver.hashCode();
        }
        else {
            hashCode2 = 0;
        }
        final IntentFilter filter = this.filter;
        int hashCode3;
        if (filter != null) {
            hashCode3 = filter.hashCode();
        }
        else {
            hashCode3 = 0;
        }
        final Executor executor = this.executor;
        int hashCode4;
        if (executor != null) {
            hashCode4 = executor.hashCode();
        }
        else {
            hashCode4 = 0;
        }
        final UserHandle user = this.user;
        if (user != null) {
            hashCode = user.hashCode();
        }
        return ((hashCode2 * 31 + hashCode3) * 31 + hashCode4) * 31 + hashCode;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ReceiverData(receiver=");
        sb.append(this.receiver);
        sb.append(", filter=");
        sb.append(this.filter);
        sb.append(", executor=");
        sb.append(this.executor);
        sb.append(", user=");
        sb.append(this.user);
        sb.append(")");
        return sb.toString();
    }
}
