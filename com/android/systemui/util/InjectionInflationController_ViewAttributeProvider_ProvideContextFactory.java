// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util;

import dagger.internal.Preconditions;
import android.content.Context;
import dagger.internal.Factory;

public final class InjectionInflationController_ViewAttributeProvider_ProvideContextFactory implements Factory<Context>
{
    public static Context proxyProvideContext(final InjectionInflationController.ViewAttributeProvider viewAttributeProvider) {
        final Context provideContext = viewAttributeProvider.provideContext();
        Preconditions.checkNotNull(provideContext, "Cannot return null from a non-@Nullable @Provides method");
        return provideContext;
    }
}
