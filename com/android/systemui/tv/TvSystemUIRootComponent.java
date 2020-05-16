// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tv;

import android.content.Context;
import com.android.systemui.dagger.SystemUIRootComponent;

public interface TvSystemUIRootComponent extends SystemUIRootComponent
{
    public interface Builder
    {
        TvSystemUIRootComponent build();
        
        Builder context(final Context p0);
    }
}
