// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import com.android.systemui.plugins.FalsingManager;
import android.view.accessibility.AccessibilityManager;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class ActivatableNotificationViewController_Factory implements Factory<ActivatableNotificationViewController>
{
    private final Provider<AccessibilityManager> accessibilityManagerProvider;
    private final Provider<ExpandableOutlineViewController> expandableOutlineViewControllerProvider;
    private final Provider<FalsingManager> falsingManagerProvider;
    private final Provider<ActivatableNotificationView> viewProvider;
    
    public ActivatableNotificationViewController_Factory(final Provider<ActivatableNotificationView> viewProvider, final Provider<ExpandableOutlineViewController> expandableOutlineViewControllerProvider, final Provider<AccessibilityManager> accessibilityManagerProvider, final Provider<FalsingManager> falsingManagerProvider) {
        this.viewProvider = viewProvider;
        this.expandableOutlineViewControllerProvider = expandableOutlineViewControllerProvider;
        this.accessibilityManagerProvider = accessibilityManagerProvider;
        this.falsingManagerProvider = falsingManagerProvider;
    }
    
    public static ActivatableNotificationViewController_Factory create(final Provider<ActivatableNotificationView> provider, final Provider<ExpandableOutlineViewController> provider2, final Provider<AccessibilityManager> provider3, final Provider<FalsingManager> provider4) {
        return new ActivatableNotificationViewController_Factory(provider, provider2, provider3, provider4);
    }
    
    public static ActivatableNotificationViewController provideInstance(final Provider<ActivatableNotificationView> provider, final Provider<ExpandableOutlineViewController> provider2, final Provider<AccessibilityManager> provider3, final Provider<FalsingManager> provider4) {
        return new ActivatableNotificationViewController(provider.get(), provider2.get(), provider3.get(), provider4.get());
    }
    
    @Override
    public ActivatableNotificationViewController get() {
        return provideInstance(this.viewProvider, this.expandableOutlineViewControllerProvider, this.accessibilityManagerProvider, this.falsingManagerProvider);
    }
}
