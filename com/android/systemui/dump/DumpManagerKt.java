// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dump;

import kotlin.jvm.internal.SpreadBuilder;

public final class DumpManagerKt
{
    private static final String[] COMMANDS;
    private static final String[] PRIORITY_OPTIONS;
    private static final String[] RESERVED_NAMES;
    
    static {
        PRIORITY_OPTIONS = new String[] { "CRITICAL", "HIGH", "NORMAL" };
        COMMANDS = new String[] { "bugreport-critical", "bugreport-normal", "buffers", "dumpables" };
        final SpreadBuilder spreadBuilder = new SpreadBuilder(2);
        spreadBuilder.add("config");
        spreadBuilder.addSpread(DumpManagerKt.COMMANDS);
        RESERVED_NAMES = (String[])spreadBuilder.toArray(new String[spreadBuilder.size()]);
    }
}
