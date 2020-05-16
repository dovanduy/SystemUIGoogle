// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tv;

import com.android.systemui.dagger.SystemUIRootComponent;
import android.content.Context;
import com.android.systemui.SystemUIFactory;

public class TvSystemUIFactory extends SystemUIFactory
{
    @Override
    protected SystemUIRootComponent buildSystemUIRootComponent(final Context context) {
        final TvSystemUIRootComponent.Builder builder = DaggerTvSystemUIRootComponent.builder();
        builder.context(context);
        return builder.build();
    }
}
