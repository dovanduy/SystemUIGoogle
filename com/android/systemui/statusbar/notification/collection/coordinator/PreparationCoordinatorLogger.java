// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.log.LogMessageImpl;
import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import com.android.systemui.log.LogLevel;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.log.LogBuffer;

public final class PreparationCoordinatorLogger
{
    private final LogBuffer buffer;
    
    public PreparationCoordinatorLogger(final LogBuffer buffer) {
        Intrinsics.checkParameterIsNotNull(buffer, "buffer");
        this.buffer = buffer;
    }
    
    public final void logInflationAborted(final String str1, final String str2) {
        Intrinsics.checkParameterIsNotNull(str1, "key");
        Intrinsics.checkParameterIsNotNull(str2, "reason");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("PreparationCoordinator", LogLevel.DEBUG, (Function1<? super LogMessage, String>)PreparationCoordinatorLogger$logInflationAborted.PreparationCoordinatorLogger$logInflationAborted$2.INSTANCE);
        obtain.setStr1(str1);
        obtain.setStr2(str2);
        buffer.push(obtain);
    }
    
    public final void logNotifInflated(final String str1) {
        Intrinsics.checkParameterIsNotNull(str1, "key");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("PreparationCoordinator", LogLevel.DEBUG, (Function1<? super LogMessage, String>)PreparationCoordinatorLogger$logNotifInflated.PreparationCoordinatorLogger$logNotifInflated$2.INSTANCE);
        obtain.setStr1(str1);
        buffer.push(obtain);
    }
}
