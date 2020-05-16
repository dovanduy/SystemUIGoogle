// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import javax.inject.Provider;
import dagger.internal.Factory;

public final class ExpandableViewController_Factory implements Factory<ExpandableViewController>
{
    private final Provider<ExpandableView> viewProvider;
    
    public ExpandableViewController_Factory(final Provider<ExpandableView> viewProvider) {
        this.viewProvider = viewProvider;
    }
    
    public static ExpandableViewController_Factory create(final Provider<ExpandableView> provider) {
        return new ExpandableViewController_Factory(provider);
    }
    
    public static ExpandableViewController provideInstance(final Provider<ExpandableView> provider) {
        return new ExpandableViewController(provider.get());
    }
    
    @Override
    public ExpandableViewController get() {
        return provideInstance(this.viewProvider);
    }
}
