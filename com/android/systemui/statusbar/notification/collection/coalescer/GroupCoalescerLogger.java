// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coalescer;

import com.android.systemui.log.LogMessageImpl;
import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import com.android.systemui.log.LogLevel;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.log.LogBuffer;

public final class GroupCoalescerLogger
{
    private final LogBuffer buffer;
    
    public GroupCoalescerLogger(final LogBuffer buffer) {
        Intrinsics.checkParameterIsNotNull(buffer, "buffer");
        this.buffer = buffer;
    }
    
    public final void logEarlyEmit(final String str1, final String str2) {
        Intrinsics.checkParameterIsNotNull(str1, "modifiedKey");
        Intrinsics.checkParameterIsNotNull(str2, "groupKey");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("GroupCoalescer", LogLevel.DEBUG, (Function1<? super LogMessage, String>)GroupCoalescerLogger$logEarlyEmit.GroupCoalescerLogger$logEarlyEmit$2.INSTANCE);
        obtain.setStr1(str1);
        obtain.setStr2(str2);
        buffer.push(obtain);
    }
    
    public final void logEmitBatch(final String str1) {
        Intrinsics.checkParameterIsNotNull(str1, "groupKey");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("GroupCoalescer", LogLevel.DEBUG, (Function1<? super LogMessage, String>)GroupCoalescerLogger$logEmitBatch.GroupCoalescerLogger$logEmitBatch$2.INSTANCE);
        obtain.setStr1(str1);
        buffer.push(obtain);
    }
    
    public final void logEventCoalesced(final String str1) {
        Intrinsics.checkParameterIsNotNull(str1, "key");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("GroupCoalescer", LogLevel.INFO, (Function1<? super LogMessage, String>)GroupCoalescerLogger$logEventCoalesced.GroupCoalescerLogger$logEventCoalesced$2.INSTANCE);
        obtain.setStr1(str1);
        buffer.push(obtain);
    }
    
    public final void logMaxBatchTimeout(final String str1, final String str2) {
        Intrinsics.checkParameterIsNotNull(str1, "modifiedKey");
        Intrinsics.checkParameterIsNotNull(str2, "groupKey");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("GroupCoalescer", LogLevel.INFO, (Function1<? super LogMessage, String>)GroupCoalescerLogger$logMaxBatchTimeout.GroupCoalescerLogger$logMaxBatchTimeout$2.INSTANCE);
        obtain.setStr1(str1);
        obtain.setStr2(str2);
        buffer.push(obtain);
    }
    
    public final void logMissingRanking(final String str1) {
        Intrinsics.checkParameterIsNotNull(str1, "forKey");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("GroupCoalescer", LogLevel.WARNING, (Function1<? super LogMessage, String>)GroupCoalescerLogger$logMissingRanking.GroupCoalescerLogger$logMissingRanking$2.INSTANCE);
        obtain.setStr1(str1);
        buffer.push(obtain);
    }
}
