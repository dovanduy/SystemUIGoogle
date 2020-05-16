// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.gates;

import com.android.systemui.Dependency;
import com.android.systemui.R$array;
import com.android.systemui.R$integer;
import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;

public class SystemKeyPress extends TransientGate
{
    private final int[] mBlockingKeys;
    private final CommandQueue mCommandQueue;
    private final CommandQueue.Callbacks mCommandQueueCallbacks;
    
    public SystemKeyPress(final Context context) {
        super(context, context.getResources().getInteger(R$integer.elmyra_system_key_gate_duration));
        this.mCommandQueueCallbacks = new CommandQueue.Callbacks() {
            @Override
            public void handleSystemKey(final int n) {
                for (int i = 0; i < SystemKeyPress.this.mBlockingKeys.length; ++i) {
                    if (SystemKeyPress.this.mBlockingKeys[i] == n) {
                        SystemKeyPress.this.block();
                        break;
                    }
                }
            }
        };
        this.mBlockingKeys = context.getResources().getIntArray(R$array.elmyra_blocking_system_keys);
        this.mCommandQueue = Dependency.get(CommandQueue.class);
    }
    
    @Override
    protected void onActivate() {
        this.mCommandQueue.addCallback(this.mCommandQueueCallbacks);
    }
    
    @Override
    protected void onDeactivate() {
        this.mCommandQueue.removeCallback(this.mCommandQueueCallbacks);
    }
}
