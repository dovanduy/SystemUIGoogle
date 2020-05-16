// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import com.android.systemui.log.LogMessageImpl;
import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import com.android.systemui.log.LogLevel;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.log.LogBuffer;

public final class NotificationEntryManagerLogger
{
    private final LogBuffer buffer;
    
    public NotificationEntryManagerLogger(final LogBuffer buffer) {
        Intrinsics.checkParameterIsNotNull(buffer, "buffer");
        this.buffer = buffer;
    }
    
    public final void logFilterAndSort(final String str1) {
        Intrinsics.checkParameterIsNotNull(str1, "reason");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotificationEntryMgr", LogLevel.INFO, (Function1<? super LogMessage, String>)NotificationEntryManagerLogger$logFilterAndSort.NotificationEntryManagerLogger$logFilterAndSort$2.INSTANCE);
        obtain.setStr1(str1);
        buffer.push(obtain);
    }
    
    public final void logInflationAborted(final String str1, final String str2, final String str3) {
        Intrinsics.checkParameterIsNotNull(str1, "key");
        Intrinsics.checkParameterIsNotNull(str2, "status");
        Intrinsics.checkParameterIsNotNull(str3, "reason");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotificationEntryMgr", LogLevel.DEBUG, (Function1<? super LogMessage, String>)NotificationEntryManagerLogger$logInflationAborted.NotificationEntryManagerLogger$logInflationAborted$2.INSTANCE);
        obtain.setStr1(str1);
        obtain.setStr2(str2);
        obtain.setStr3(str3);
        buffer.push(obtain);
    }
    
    public final void logLifetimeExtended(final String str1, final String str2, final String str3) {
        Intrinsics.checkParameterIsNotNull(str1, "key");
        Intrinsics.checkParameterIsNotNull(str2, "extenderName");
        Intrinsics.checkParameterIsNotNull(str3, "status");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotificationEntryMgr", LogLevel.INFO, (Function1<? super LogMessage, String>)NotificationEntryManagerLogger$logLifetimeExtended.NotificationEntryManagerLogger$logLifetimeExtended$2.INSTANCE);
        obtain.setStr1(str1);
        obtain.setStr2(str2);
        obtain.setStr3(str3);
        buffer.push(obtain);
    }
    
    public final void logNotifAdded(final String str1) {
        Intrinsics.checkParameterIsNotNull(str1, "key");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotificationEntryMgr", LogLevel.INFO, (Function1<? super LogMessage, String>)NotificationEntryManagerLogger$logNotifAdded.NotificationEntryManagerLogger$logNotifAdded$2.INSTANCE);
        obtain.setStr1(str1);
        buffer.push(obtain);
    }
    
    public final void logNotifInflated(final String str1, final boolean bool1) {
        Intrinsics.checkParameterIsNotNull(str1, "key");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotificationEntryMgr", LogLevel.DEBUG, (Function1<? super LogMessage, String>)NotificationEntryManagerLogger$logNotifInflated.NotificationEntryManagerLogger$logNotifInflated$2.INSTANCE);
        obtain.setStr1(str1);
        obtain.setBool1(bool1);
        buffer.push(obtain);
    }
    
    public final void logNotifRemoved(final String str1, final boolean bool1) {
        Intrinsics.checkParameterIsNotNull(str1, "key");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotificationEntryMgr", LogLevel.INFO, (Function1<? super LogMessage, String>)NotificationEntryManagerLogger$logNotifRemoved.NotificationEntryManagerLogger$logNotifRemoved$2.INSTANCE);
        obtain.setStr1(str1);
        obtain.setBool1(bool1);
        buffer.push(obtain);
    }
    
    public final void logNotifUpdated(final String str1) {
        Intrinsics.checkParameterIsNotNull(str1, "key");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotificationEntryMgr", LogLevel.INFO, (Function1<? super LogMessage, String>)NotificationEntryManagerLogger$logNotifUpdated.NotificationEntryManagerLogger$logNotifUpdated$2.INSTANCE);
        obtain.setStr1(str1);
        buffer.push(obtain);
    }
    
    public final void logRemovalIntercepted(final String str1) {
        Intrinsics.checkParameterIsNotNull(str1, "key");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotificationEntryMgr", LogLevel.INFO, (Function1<? super LogMessage, String>)NotificationEntryManagerLogger$logRemovalIntercepted.NotificationEntryManagerLogger$logRemovalIntercepted$2.INSTANCE);
        obtain.setStr1(str1);
        buffer.push(obtain);
    }
}
