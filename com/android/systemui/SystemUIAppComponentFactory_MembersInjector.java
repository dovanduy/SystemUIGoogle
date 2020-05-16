// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import com.android.systemui.dagger.ContextComponentHelper;

public final class SystemUIAppComponentFactory_MembersInjector implements Object<SystemUIAppComponentFactory>
{
    public static void injectMComponentHelper(final SystemUIAppComponentFactory systemUIAppComponentFactory, final ContextComponentHelper mComponentHelper) {
        systemUIAppComponentFactory.mComponentHelper = mComponentHelper;
    }
}
