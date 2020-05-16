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

public final class NotifBindPipelineLogger
{
    private final LogBuffer buffer;
    
    public NotifBindPipelineLogger(final LogBuffer buffer) {
        Intrinsics.checkParameterIsNotNull(buffer, "buffer");
        this.buffer = buffer;
    }
    
    public final void logFinishedPipeline(final String str1, final int int1) {
        Intrinsics.checkParameterIsNotNull(str1, "notifKey");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotifBindPipeline", LogLevel.INFO, (Function1<? super LogMessage, String>)NotifBindPipelineLogger$logFinishedPipeline.NotifBindPipelineLogger$logFinishedPipeline$2.INSTANCE);
        obtain.setStr1(str1);
        obtain.setInt1(int1);
        buffer.push(obtain);
    }
    
    public final void logManagedRow(final String str1) {
        Intrinsics.checkParameterIsNotNull(str1, "notifKey");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotifBindPipeline", LogLevel.INFO, (Function1<? super LogMessage, String>)NotifBindPipelineLogger$logManagedRow.NotifBindPipelineLogger$logManagedRow$2.INSTANCE);
        obtain.setStr1(str1);
        buffer.push(obtain);
    }
    
    public final void logRequestPipelineRun(final String str1) {
        Intrinsics.checkParameterIsNotNull(str1, "notifKey");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotifBindPipeline", LogLevel.INFO, (Function1<? super LogMessage, String>)NotifBindPipelineLogger$logRequestPipelineRun.NotifBindPipelineLogger$logRequestPipelineRun$2.INSTANCE);
        obtain.setStr1(str1);
        buffer.push(obtain);
    }
    
    public final void logStageSet(final String str1) {
        Intrinsics.checkParameterIsNotNull(str1, "stageName");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotifBindPipeline", LogLevel.INFO, (Function1<? super LogMessage, String>)NotifBindPipelineLogger$logStageSet.NotifBindPipelineLogger$logStageSet$2.INSTANCE);
        obtain.setStr1(str1);
        buffer.push(obtain);
    }
    
    public final void logStartPipeline(final String str1) {
        Intrinsics.checkParameterIsNotNull(str1, "notifKey");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotifBindPipeline", LogLevel.INFO, (Function1<? super LogMessage, String>)NotifBindPipelineLogger$logStartPipeline.NotifBindPipelineLogger$logStartPipeline$2.INSTANCE);
        obtain.setStr1(str1);
        buffer.push(obtain);
    }
}
