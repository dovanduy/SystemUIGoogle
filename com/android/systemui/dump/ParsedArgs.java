// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dump;

import kotlin.jvm.internal.Intrinsics;
import java.util.List;

final class ParsedArgs
{
    private String command;
    private String dumpPriority;
    private final List<String> nonFlagArgs;
    private final String[] rawArgs;
    private int tailLength;
    
    public ParsedArgs(final String[] rawArgs, final List<String> nonFlagArgs) {
        Intrinsics.checkParameterIsNotNull(rawArgs, "rawArgs");
        Intrinsics.checkParameterIsNotNull(nonFlagArgs, "nonFlagArgs");
        this.rawArgs = rawArgs;
        this.nonFlagArgs = nonFlagArgs;
    }
    
    public final String getCommand() {
        return this.command;
    }
    
    public final String getDumpPriority() {
        return this.dumpPriority;
    }
    
    public final List<String> getNonFlagArgs() {
        return this.nonFlagArgs;
    }
    
    public final String[] getRawArgs() {
        return this.rawArgs;
    }
    
    public final int getTailLength() {
        return this.tailLength;
    }
    
    public final void setCommand(final String command) {
        this.command = command;
    }
    
    public final void setDumpPriority(final String dumpPriority) {
        this.dumpPriority = dumpPriority;
    }
    
    public final void setTailLength(final int tailLength) {
        this.tailLength = tailLength;
    }
}
