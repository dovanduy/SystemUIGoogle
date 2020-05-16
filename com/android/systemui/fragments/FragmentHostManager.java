// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.fragments;

import android.view.LayoutInflater;
import com.android.systemui.plugins.Plugin;
import java.lang.reflect.InvocationTargetException;
import android.app.Fragment$InstantiationException;
import java.lang.reflect.Method;
import android.util.ArrayMap;
import android.content.res.Configuration;
import java.util.function.Consumer;
import android.app.FragmentManagerNonConfig;
import android.os.Bundle;
import com.android.systemui.Dependency;
import com.android.systemui.util.leak.LeakDetector;
import android.app.FragmentManager;
import android.app.FragmentHostCallback;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.app.Fragment;
import android.os.Parcelable;
import android.os.Looper;
import android.view.View;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.FragmentManager$FragmentLifecycleCallbacks;
import android.os.Handler;
import android.app.FragmentController;
import android.content.Context;
import com.android.settingslib.applications.InterestingConfigChanges;

public class FragmentHostManager
{
    private final InterestingConfigChanges mConfigChanges;
    private final Context mContext;
    private FragmentController mFragments;
    private final Handler mHandler;
    private FragmentManager$FragmentLifecycleCallbacks mLifecycleCallbacks;
    private final HashMap<String, ArrayList<FragmentListener>> mListeners;
    private final FragmentService mManager;
    private final ExtensionFragmentManager mPlugins;
    private final View mRootView;
    
    FragmentHostManager(final FragmentService mManager, final View mRootView) {
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mListeners = new HashMap<String, ArrayList<FragmentListener>>();
        this.mConfigChanges = new InterestingConfigChanges(-1073741052);
        this.mPlugins = new ExtensionFragmentManager();
        final Context context = mRootView.getContext();
        this.mContext = context;
        this.mManager = mManager;
        this.mRootView = mRootView;
        this.mConfigChanges.applyNewConfig(context.getResources());
        this.createFragmentHost(null);
    }
    
    private void createFragmentHost(final Parcelable parcelable) {
        (this.mFragments = FragmentController.createController((FragmentHostCallback)new HostCallbacks())).attachHost((Fragment)null);
        this.mLifecycleCallbacks = new FragmentManager$FragmentLifecycleCallbacks() {
            public void onFragmentDestroyed(final FragmentManager fragmentManager, final Fragment fragment) {
                Dependency.get(LeakDetector.class).trackGarbage(fragment);
            }
            
            public void onFragmentViewCreated(final FragmentManager fragmentManager, final Fragment fragment, final View view, final Bundle bundle) {
                FragmentHostManager.this.onFragmentViewCreated(fragment);
            }
            
            public void onFragmentViewDestroyed(final FragmentManager fragmentManager, final Fragment fragment) {
                FragmentHostManager.this.onFragmentViewDestroyed(fragment);
            }
        };
        this.mFragments.getFragmentManager().registerFragmentLifecycleCallbacks(this.mLifecycleCallbacks, true);
        if (parcelable != null) {
            this.mFragments.restoreAllState(parcelable, (FragmentManagerNonConfig)null);
        }
        this.mFragments.dispatchCreate();
        this.mFragments.dispatchStart();
        this.mFragments.dispatchResume();
    }
    
    private Parcelable destroyFragmentHost() {
        this.mFragments.dispatchPause();
        final Parcelable saveAllState = this.mFragments.saveAllState();
        this.mFragments.dispatchStop();
        this.mFragments.dispatchDestroy();
        this.mFragments.getFragmentManager().unregisterFragmentLifecycleCallbacks(this.mLifecycleCallbacks);
        return saveAllState;
    }
    
    private void dump(final String s, final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
    }
    
    private <T extends View> T findViewById(final int n) {
        return (T)this.mRootView.findViewById(n);
    }
    
    public static FragmentHostManager get(final View view) {
        try {
            return Dependency.get(FragmentService.class).getFragmentHostManager(view);
        }
        catch (ClassCastException ex) {
            throw ex;
        }
    }
    
    private void onFragmentViewCreated(final Fragment fragment) {
        final String tag = fragment.getTag();
        final ArrayList<FragmentListener> list = this.mListeners.get(tag);
        if (list != null) {
            list.forEach(new _$$Lambda$FragmentHostManager$OsWXqtcfRJZBAvEEeN8CG6EN5T4(tag, fragment));
        }
    }
    
    private void onFragmentViewDestroyed(final Fragment fragment) {
        final String tag = fragment.getTag();
        final ArrayList<FragmentListener> list = this.mListeners.get(tag);
        if (list != null) {
            list.forEach(new _$$Lambda$FragmentHostManager$AcJHY99nHc_JEzu3q8ny_wMOZ4E(tag, fragment));
        }
    }
    
    public static void removeAndDestroy(final View view) {
        Dependency.get(FragmentService.class).removeAndDestroy(view);
    }
    
    public FragmentHostManager addTagListener(final String s, final FragmentListener e) {
        ArrayList<FragmentListener> value;
        if ((value = this.mListeners.get(s)) == null) {
            value = new ArrayList<FragmentListener>();
            this.mListeners.put(s, value);
        }
        value.add(e);
        final Fragment fragmentByTag = this.getFragmentManager().findFragmentByTag(s);
        if (fragmentByTag != null && fragmentByTag.getView() != null) {
            e.onFragmentViewCreated(s, fragmentByTag);
        }
        return this;
    }
    
    public <T> T create(final Class<T> clazz) {
        return (T)this.mPlugins.instantiate(this.mContext, clazz.getName(), null);
    }
    
    void destroy() {
        this.mFragments.dispatchDestroy();
    }
    
    ExtensionFragmentManager getExtensionManager() {
        return this.mPlugins;
    }
    
    public FragmentManager getFragmentManager() {
        return this.mFragments.getFragmentManager();
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        if (this.mConfigChanges.applyNewConfig(this.mContext.getResources())) {
            this.reloadFragments();
        }
        else {
            this.mFragments.dispatchConfigurationChanged(configuration);
        }
    }
    
    public void reloadFragments() {
        this.createFragmentHost(this.destroyFragmentHost());
    }
    
    public void removeTagListener(final String s, final FragmentListener o) {
        final ArrayList<FragmentListener> list = this.mListeners.get(s);
        if (list != null && list.remove(o) && list.size() == 0) {
            this.mListeners.remove(s);
        }
    }
    
    class ExtensionFragmentManager
    {
        private final ArrayMap<String, Context> mExtensionLookup;
        
        ExtensionFragmentManager() {
            this.mExtensionLookup = (ArrayMap<String, Context>)new ArrayMap();
        }
        
        private Fragment instantiateWithInjections(final Context context, final String s, final Bundle arguments) {
            final Method method = (Method)FragmentHostManager.this.mManager.getInjectionMap().get((Object)s);
            if (method != null) {
                try {
                    final Fragment fragment = (Fragment)method.invoke(FragmentHostManager.this.mManager.getFragmentCreator(), new Object[0]);
                    if (arguments != null) {
                        arguments.setClassLoader(fragment.getClass().getClassLoader());
                        fragment.setArguments(arguments);
                    }
                    return fragment;
                }
                catch (InvocationTargetException ex) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Unable to instantiate ");
                    sb.append(s);
                    throw new Fragment$InstantiationException(sb.toString(), (Exception)ex);
                }
                catch (IllegalAccessException ex2) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("Unable to instantiate ");
                    sb2.append(s);
                    throw new Fragment$InstantiationException(sb2.toString(), (Exception)ex2);
                }
            }
            return Fragment.instantiate(context, s, arguments);
        }
        
        Fragment instantiate(final Context context, final String s, final Bundle bundle) {
            final Context context2 = (Context)this.mExtensionLookup.get((Object)s);
            if (context2 != null) {
                final Fragment instantiateWithInjections = this.instantiateWithInjections(context2, s, bundle);
                if (instantiateWithInjections instanceof Plugin) {
                    ((Plugin)instantiateWithInjections).onCreate(FragmentHostManager.this.mContext, context2);
                }
                return instantiateWithInjections;
            }
            return this.instantiateWithInjections(context, s, bundle);
        }
        
        public void setCurrentExtension(final int n, final String s, final String s2, final String s3, final Context context) {
            if (s2 != null) {
                this.mExtensionLookup.remove((Object)s2);
            }
            this.mExtensionLookup.put((Object)s3, (Object)context);
            FragmentHostManager.this.getFragmentManager().beginTransaction().replace(n, this.instantiate(context, s3, null), s).commit();
            FragmentHostManager.this.reloadFragments();
        }
    }
    
    public interface FragmentListener
    {
        void onFragmentViewCreated(final String p0, final Fragment p1);
        
        default void onFragmentViewDestroyed(final String s, final Fragment fragment) {
        }
    }
    
    class HostCallbacks extends FragmentHostCallback<FragmentHostManager>
    {
        public HostCallbacks() {
            super(FragmentHostManager.this.mContext, FragmentHostManager.this.mHandler, 0);
        }
        
        public Fragment instantiate(final Context context, final String s, final Bundle bundle) {
            return FragmentHostManager.this.mPlugins.instantiate(context, s, bundle);
        }
        
        public void onAttachFragment(final Fragment fragment) {
        }
        
        public void onDump(final String s, final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
            FragmentHostManager.this.dump(s, fileDescriptor, printWriter, array);
        }
        
        public <T extends View> T onFindViewById(final int n) {
            return (T)FragmentHostManager.this.findViewById(n);
        }
        
        public FragmentHostManager onGetHost() {
            return FragmentHostManager.this;
        }
        
        public LayoutInflater onGetLayoutInflater() {
            return LayoutInflater.from(FragmentHostManager.this.mContext);
        }
        
        public int onGetWindowAnimations() {
            return 0;
        }
        
        public boolean onHasView() {
            return true;
        }
        
        public boolean onHasWindowAnimations() {
            return false;
        }
        
        public boolean onShouldSaveFragmentState(final Fragment fragment) {
            return true;
        }
        
        public boolean onUseFragmentManagerInflaterFactory() {
            return true;
        }
    }
}
