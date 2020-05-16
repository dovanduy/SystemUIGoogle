// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import com.android.systemui.log.LogMessageImpl;
import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import com.android.systemui.log.LogLevel;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.log.LogBuffer;

public final class RowContentBindStageLogger
{
    private final LogBuffer buffer;
    
    public RowContentBindStageLogger(final LogBuffer buffer) {
        Intrinsics.checkParameterIsNotNull(buffer, "buffer");
        this.buffer = buffer;
    }
    
    public final void logStageParams(final String str1, final String str2) {
        Intrinsics.checkParameterIsNotNull(str1, "notifKey");
        Intrinsics.checkParameterIsNotNull(str2, "stageParams");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("RowContentBindStage", LogLevel.INFO, (Function1<? super LogMessage, String>)RowContentBindStageLogger$logStageParams.RowContentBindStageLogger$logStageParams$2.INSTANCE);
        obtain.setStr1(str1);
        obtain.setStr2(str2);
        buffer.push(obtain);
    }
}
