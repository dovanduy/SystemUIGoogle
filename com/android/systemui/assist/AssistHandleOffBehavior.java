// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

import android.content.Context;

final class AssistHandleOffBehavior implements BehaviorController
{
    @Override
    public void onModeActivated(final Context context, final AssistHandleCallbacks assistHandleCallbacks) {
        assistHandleCallbacks.hide();
    }
}
