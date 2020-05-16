// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import javax.inject.Provider;
import dagger.internal.Factory;

public final class ExpandableOutlineViewController_Factory implements Factory<ExpandableOutlineViewController>
{
    private final Provider<ExpandableViewController> expandableViewControllerProvider;
    private final Provider<ExpandableOutlineView> viewProvider;
    
    public ExpandableOutlineViewController_Factory(final Provider<ExpandableOutlineView> viewProvider, final Provider<ExpandableViewController> expandableViewControllerProvider) {
        this.viewProvider = viewProvider;
        this.expandableViewControllerProvider = expandableViewControllerProvider;
    }
    
    public static ExpandableOutlineViewController_Factory create(final Provider<ExpandableOutlineView> provider, final Provider<ExpandableViewController> provider2) {
        return new ExpandableOutlineViewController_Factory(provider, provider2);
    }
    
    public static ExpandableOutlineViewController provideInstance(final Provider<ExpandableOutlineView> provider, final Provider<ExpandableViewController> provider2) {
        return new ExpandableOutlineViewController(provider.get(), provider2.get());
    }
    
    @Override
    public ExpandableOutlineViewController get() {
        return provideInstance(this.viewProvider, this.expandableViewControllerProvider);
    }
}
