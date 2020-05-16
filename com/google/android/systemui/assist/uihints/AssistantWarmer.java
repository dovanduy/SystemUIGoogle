// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import kotlin.jvm.internal.Intrinsics;
import android.content.Context;

public final class AssistantWarmer implements WarmingListener
{
    private final Context context;
    private boolean primed;
    private WarmingRequest request;
    
    public AssistantWarmer(final Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.context = context;
    }
    
    public final void onInvocationProgress(final float n) {
        boolean b = true;
        Label_0083: {
            if (n >= 1.0f) {
                this.primed = false;
            }
            else {
                if (n <= 0.0f && this.primed) {
                    this.primed = false;
                    break Label_0083;
                }
                final WarmingRequest request = this.request;
                float threshold;
                if (request != null) {
                    threshold = request.getThreshold();
                }
                else {
                    threshold = 0.1f;
                }
                if (n > threshold && !this.primed) {
                    this.primed = true;
                    break Label_0083;
                }
            }
            b = false;
        }
        if (b) {
            final WarmingRequest request2 = this.request;
            if (request2 != null) {
                request2.notify(this.context, this.primed);
            }
        }
    }
    
    @Override
    public void onWarmingRequest(final WarmingRequest request) {
        Intrinsics.checkParameterIsNotNull(request, "request");
        this.request = request;
    }
}
