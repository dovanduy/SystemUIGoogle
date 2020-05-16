// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.log;

import kotlin.jvm.functions.Function1;
import java.util.Iterator;
import kotlin.Unit;
import com.android.systemui.dump.DumpManager;
import android.util.Log;
import java.io.PrintWriter;
import kotlin.jvm.internal.Intrinsics;
import java.util.ArrayDeque;

public final class LogBuffer
{
    private final ArrayDeque<LogMessageImpl> buffer;
    private final LogcatEchoTracker logcatEchoTracker;
    private final int maxLogs;
    private final String name;
    private final int poolSize;
    
    public LogBuffer(final String name, final int maxLogs, final int poolSize, final LogcatEchoTracker logcatEchoTracker) {
        Intrinsics.checkParameterIsNotNull(name, "name");
        Intrinsics.checkParameterIsNotNull(logcatEchoTracker, "logcatEchoTracker");
        this.name = name;
        this.maxLogs = maxLogs;
        this.poolSize = poolSize;
        this.logcatEchoTracker = logcatEchoTracker;
        this.buffer = new ArrayDeque<LogMessageImpl>();
    }
    
    private final void dumpMessage(final LogMessage logMessage, final PrintWriter printWriter) {
        printWriter.print(LogBufferKt.access$getDATE_FORMAT$p().format(logMessage.getTimestamp()));
        printWriter.print(" ");
        printWriter.print(logMessage.getLevel());
        printWriter.print(" ");
        printWriter.print(logMessage.getTag());
        printWriter.print(" ");
        printWriter.println(logMessage.getPrinter().invoke(logMessage));
    }
    
    private final void echoToLogcat(final LogMessage logMessage) {
        final String s = logMessage.getPrinter().invoke(logMessage);
        switch (LogBuffer$WhenMappings.$EnumSwitchMapping$0[logMessage.getLevel().ordinal()]) {
            case 6: {
                Log.wtf(logMessage.getTag(), s);
                break;
            }
            case 5: {
                Log.e(logMessage.getTag(), s);
                break;
            }
            case 4: {
                Log.w(logMessage.getTag(), s);
                break;
            }
            case 3: {
                Log.i(logMessage.getTag(), s);
                break;
            }
            case 2: {
                Log.d(logMessage.getTag(), s);
                break;
            }
            case 1: {
                Log.v(logMessage.getTag(), s);
                break;
            }
        }
    }
    
    public final void attach(final DumpManager dumpManager) {
        Intrinsics.checkParameterIsNotNull(dumpManager, "dumpManager");
        dumpManager.registerBuffer(this.name, this);
    }
    
    public final void dump(final PrintWriter printWriter, int n) {
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        final ArrayDeque<LogMessageImpl> buffer = this.buffer;
        // monitorenter(buffer)
        int n2 = 0;
        Label_0035: {
            if (n <= 0) {
                n = 0;
                break Label_0035;
            }
            try {
                n = this.buffer.size() - n;
                for (final LogMessageImpl logMessageImpl : this.buffer) {
                    if (n2 >= n) {
                        Intrinsics.checkExpressionValueIsNotNull(logMessageImpl, "message");
                        this.dumpMessage(logMessageImpl, printWriter);
                    }
                    ++n2;
                }
                final Unit instance = Unit.INSTANCE;
            }
            finally {
            }
            // monitorexit(buffer)
        }
    }
    
    public final LogMessageImpl obtain(final String s, final LogLevel logLevel, final Function1<? super LogMessage, String> function1) {
        Intrinsics.checkParameterIsNotNull(s, "tag");
        Intrinsics.checkParameterIsNotNull(logLevel, "level");
        Intrinsics.checkParameterIsNotNull(function1, "printer");
        synchronized (this.buffer) {
            LogMessageImpl create;
            if (this.buffer.size() > this.maxLogs - this.poolSize) {
                create = this.buffer.removeFirst();
            }
            else {
                create = LogMessageImpl.Factory.create();
            }
            // monitorexit(this.buffer)
            create.reset(s, logLevel, System.currentTimeMillis(), function1);
            Intrinsics.checkExpressionValueIsNotNull(create, "message");
            return create;
        }
    }
    
    public final void push(final LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "message");
        synchronized (this.buffer) {
            if (this.buffer.size() == this.maxLogs) {
                final StringBuilder sb = new StringBuilder();
                sb.append("LogBuffer ");
                sb.append(this.name);
                sb.append(" has exceeded its pool size");
                Log.e("LogBuffer", sb.toString());
                this.buffer.removeFirst();
            }
            this.buffer.add((LogMessageImpl)logMessage);
            if (this.logcatEchoTracker.isBufferLoggable(this.name, ((LogMessageImpl)logMessage).getLevel()) || this.logcatEchoTracker.isTagLoggable(((LogMessageImpl)logMessage).getTag(), ((LogMessageImpl)logMessage).getLevel())) {
                this.echoToLogcat(logMessage);
            }
            final Unit instance = Unit.INSTANCE;
        }
    }
}
