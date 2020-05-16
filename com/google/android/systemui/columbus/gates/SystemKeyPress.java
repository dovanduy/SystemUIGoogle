// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.gates;

import kotlin.jvm.internal.Intrinsics;
import android.os.Handler;
import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import java.util.Set;

public final class SystemKeyPress extends TransientGate
{
    private final Set<Integer> blockingKeys;
    private final CommandQueue commandQueue;
    private final SystemKeyPress$commandQueueCallbacks.SystemKeyPress$commandQueueCallbacks$1 commandQueueCallbacks;
    private final long gateDuration;
    
    public SystemKeyPress(final Context context, final Handler handler, final CommandQueue commandQueue, final long gateDuration, final Set<Integer> blockingKeys) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(handler, "handler");
        Intrinsics.checkParameterIsNotNull(commandQueue, "commandQueue");
        Intrinsics.checkParameterIsNotNull(blockingKeys, "blockingKeys");
        super(context, handler);
        this.commandQueue = commandQueue;
        this.gateDuration = gateDuration;
        this.blockingKeys = blockingKeys;
        this.commandQueueCallbacks = new SystemKeyPress$commandQueueCallbacks.SystemKeyPress$commandQueueCallbacks$1(this);
    }
    
    @Override
    protected void onActivate() {
        this.commandQueue.addCallback((CommandQueue.Callbacks)this.commandQueueCallbacks);
    }
    
    @Override
    protected void onDeactivate() {
        this.commandQueue.removeCallback((CommandQueue.Callbacks)this.commandQueueCallbacks);
    }
}
