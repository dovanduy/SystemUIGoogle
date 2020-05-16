// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.fragments;

import com.android.systemui.qs.QSFragment;
import com.android.systemui.statusbar.phone.NavigationBarFragment;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.app.Fragment;
import java.util.Iterator;
import android.content.res.Configuration;
import com.android.systemui.dagger.SystemUIRootComponent;
import java.lang.reflect.Method;
import android.view.View;
import android.util.ArrayMap;
import android.os.Handler;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.Dumpable;

public class FragmentService implements Dumpable
{
    private ConfigurationController.ConfigurationListener mConfigurationListener;
    private final FragmentCreator mFragmentCreator;
    private final Handler mHandler;
    private final ArrayMap<View, FragmentHostState> mHosts;
    private final ArrayMap<String, Method> mInjectionMap;
    
    public FragmentService(final SystemUIRootComponent systemUIRootComponent, final ConfigurationController configurationController) {
        this.mHosts = (ArrayMap<View, FragmentHostState>)new ArrayMap();
        this.mInjectionMap = (ArrayMap<String, Method>)new ArrayMap();
        this.mHandler = new Handler();
        this.mConfigurationListener = new ConfigurationController.ConfigurationListener() {
            @Override
            public void onConfigChanged(final Configuration configuration) {
                final Iterator<FragmentHostState> iterator = FragmentService.this.mHosts.values().iterator();
                while (iterator.hasNext()) {
                    iterator.next().sendConfigurationChange(configuration);
                }
            }
        };
        this.mFragmentCreator = systemUIRootComponent.createFragmentCreator();
        this.initInjectionMap();
        configurationController.addCallback(this.mConfigurationListener);
    }
    
    private void initInjectionMap() {
        for (final Method method : FragmentCreator.class.getDeclaredMethods()) {
            if (Fragment.class.isAssignableFrom(method.getReturnType()) && (method.getModifiers() & 0x1) != 0x0) {
                this.mInjectionMap.put((Object)method.getReturnType().getName(), (Object)method);
            }
        }
    }
    
    public void destroyAll() {
        final Iterator<FragmentHostState> iterator = this.mHosts.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().mFragmentHostManager.destroy();
        }
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("Dumping fragments:");
        final Iterator<FragmentHostState> iterator = this.mHosts.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().mFragmentHostManager.getFragmentManager().dump("  ", fileDescriptor, printWriter, array);
        }
    }
    
    FragmentCreator getFragmentCreator() {
        return this.mFragmentCreator;
    }
    
    public FragmentHostManager getFragmentHostManager(final View view) {
        final View rootView = view.getRootView();
        FragmentHostState fragmentHostState;
        if ((fragmentHostState = (FragmentHostState)this.mHosts.get((Object)rootView)) == null) {
            fragmentHostState = new FragmentHostState(rootView);
            this.mHosts.put((Object)rootView, (Object)fragmentHostState);
        }
        return fragmentHostState.getFragmentHostManager();
    }
    
    ArrayMap<String, Method> getInjectionMap() {
        return this.mInjectionMap;
    }
    
    public void removeAndDestroy(final View view) {
        final FragmentHostState fragmentHostState = (FragmentHostState)this.mHosts.remove((Object)view.getRootView());
        if (fragmentHostState != null) {
            fragmentHostState.mFragmentHostManager.destroy();
        }
    }
    
    public interface FragmentCreator
    {
        NavigationBarFragment createNavigationBarFragment();
        
        QSFragment createQSFragment();
    }
    
    private class FragmentHostState
    {
        private FragmentHostManager mFragmentHostManager;
        private final View mView;
        
        public FragmentHostState(final View mView) {
            this.mView = mView;
            this.mFragmentHostManager = new FragmentHostManager(FragmentService.this, mView);
        }
        
        private void handleSendConfigurationChange(final Configuration configuration) {
            this.mFragmentHostManager.onConfigurationChanged(configuration);
        }
        
        public FragmentHostManager getFragmentHostManager() {
            return this.mFragmentHostManager;
        }
        
        public void sendConfigurationChange(final Configuration configuration) {
            FragmentService.this.mHandler.post((Runnable)new _$$Lambda$FragmentService$FragmentHostState$kEJEvu5Mq9Z5e9srOLcsFn7Glto(this, configuration));
        }
    }
}
