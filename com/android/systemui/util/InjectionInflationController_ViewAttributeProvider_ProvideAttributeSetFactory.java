// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util;

import dagger.internal.Preconditions;
import android.util.AttributeSet;
import dagger.internal.Factory;

public final class InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory implements Factory<AttributeSet>
{
    public static AttributeSet proxyProvideAttributeSet(final InjectionInflationController.ViewAttributeProvider viewAttributeProvider) {
        final AttributeSet provideAttributeSet = viewAttributeProvider.provideAttributeSet();
        Preconditions.checkNotNull(provideAttributeSet, "Cannot return null from a non-@Nullable @Provides method");
        return provideAttributeSet;
    }
}
