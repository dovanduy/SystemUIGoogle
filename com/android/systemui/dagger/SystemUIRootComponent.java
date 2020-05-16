// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import com.android.systemui.BootCompleteCacheImpl;
import com.android.systemui.keyguard.KeyguardSliceProvider;
import com.android.systemui.SystemUIAppComponentFactory;
import android.content.ContentProvider;
import com.android.systemui.InitController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.util.InjectionInflationController;
import com.android.systemui.fragments.FragmentService;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.Dependency;

public interface SystemUIRootComponent
{
    Dependency.DependencyInjector createDependency();
    
    DumpManager createDumpManager();
    
    FragmentService.FragmentCreator createFragmentCreator();
    
    InjectionInflationController.ViewCreator createViewCreator();
    
    ConfigurationController getConfigurationController();
    
    ContextComponentHelper getContextComponentHelper();
    
    InitController getInitController();
    
    void inject(final ContentProvider p0);
    
    void inject(final SystemUIAppComponentFactory p0);
    
    void inject(final KeyguardSliceProvider p0);
    
    BootCompleteCacheImpl provideBootCacheImpl();
}
