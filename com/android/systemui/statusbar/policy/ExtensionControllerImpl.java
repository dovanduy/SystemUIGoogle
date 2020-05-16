// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import java.util.Map;
import android.util.ArrayMap;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.plugins.Plugin;
import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.function.Consumer;
import java.util.List;
import java.util.Collections;
import java.util.function.ToIntFunction;
import java.util.Comparator;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.util.leak.LeakDetector;
import android.content.Context;

public class ExtensionControllerImpl implements ExtensionController
{
    private final Context mDefaultContext;
    private final LeakDetector mLeakDetector;
    private final PluginManager mPluginManager;
    private final TunerService mTunerService;
    
    public ExtensionControllerImpl(final Context mDefaultContext, final LeakDetector mLeakDetector, final PluginManager mPluginManager, final TunerService mTunerService, final ConfigurationController configurationController) {
        this.mDefaultContext = mDefaultContext;
        this.mLeakDetector = mLeakDetector;
        this.mPluginManager = mPluginManager;
        this.mTunerService = mTunerService;
    }
    
    public <T> ExtensionBuilder<T> newExtension(final Class<T> clazz) {
        return new ExtensionBuilder<T>();
    }
    
    private class ExtensionBuilder<T> implements ExtensionController.ExtensionBuilder<T>
    {
        private ExtensionImpl<T> mExtension;
        
        private ExtensionBuilder() {
            this.mExtension = (ExtensionImpl<T>)new ExtensionImpl();
        }
        
        @Override
        public Extension<T> build() {
            Collections.sort((List<Object>)((ExtensionImpl<Object>)this.mExtension).mProducers, Comparator.comparingInt((ToIntFunction<? super Object>)_$$Lambda$LO8p3lRLZXpohPDzojcJ_BVuMnk.INSTANCE));
            ((ExtensionImpl<Object>)this.mExtension).notifyChanged();
            return this.mExtension;
        }
        
        @Override
        public ExtensionController.ExtensionBuilder<T> withCallback(final Consumer<T> e) {
            ((ExtensionImpl<Object>)this.mExtension).mCallbacks.add(e);
            return this;
        }
        
        @Override
        public ExtensionController.ExtensionBuilder<T> withDefault(final Supplier<T> supplier) {
            this.mExtension.addDefault(supplier);
            return this;
        }
        
        @Override
        public <P extends T> ExtensionController.ExtensionBuilder<T> withPlugin(final Class<P> clazz) {
            this.withPlugin(clazz, PluginManager.Helper.getAction(clazz));
            return this;
        }
        
        public <P extends T> ExtensionController.ExtensionBuilder<T> withPlugin(final Class<P> clazz, final String s) {
            this.withPlugin(clazz, s, null);
            return this;
        }
        
        @Override
        public <P> ExtensionController.ExtensionBuilder<T> withPlugin(final Class<P> clazz, final String s, final PluginConverter<T, P> pluginConverter) {
            this.mExtension.addPlugin(s, clazz, pluginConverter);
            return this;
        }
        
        @Override
        public ExtensionController.ExtensionBuilder<T> withTunerFactory(final TunerFactory<T> tunerFactory) {
            this.mExtension.addTunerFactory(tunerFactory, tunerFactory.keys());
            return this;
        }
    }
    
    private class ExtensionImpl<T> implements Extension<T>
    {
        private final ArrayList<Consumer<T>> mCallbacks;
        private T mItem;
        private Context mPluginContext;
        private final ArrayList<Item<T>> mProducers;
        final /* synthetic */ ExtensionControllerImpl this$0;
        
        private ExtensionImpl() {
            this.mProducers = new ArrayList<Item<T>>();
            this.mCallbacks = new ArrayList<Consumer<T>>();
        }
        
        private void notifyChanged() {
            if (this.mItem != null) {
                ExtensionControllerImpl.this.mLeakDetector.trackGarbage(this.mItem);
            }
            this.mItem = null;
            final int n = 0;
            int index = 0;
            int i;
            while (true) {
                i = n;
                if (index >= this.mProducers.size()) {
                    break;
                }
                final Object value = this.mProducers.get(index).get();
                if (value != null) {
                    this.mItem = (T)value;
                    i = n;
                    break;
                }
                ++index;
            }
            while (i < this.mCallbacks.size()) {
                this.mCallbacks.get(i).accept(this.mItem);
                ++i;
            }
        }
        
        @Override
        public void addCallback(final Consumer<T> e) {
            this.mCallbacks.add(e);
        }
        
        public void addDefault(final Supplier<T> supplier) {
            this.mProducers.add(new Default<T>(supplier));
        }
        
        public <P> void addPlugin(final String s, final Class<P> clazz, final PluginConverter<T, P> pluginConverter) {
            this.mProducers.add(new PluginItem<Object>(s, (Class<Object>)clazz, (PluginConverter<T, Object>)pluginConverter));
        }
        
        public void addTunerFactory(final TunerFactory<T> tunerFactory, final String[] array) {
            this.mProducers.add(new TunerItem<T>(tunerFactory, array));
        }
        
        @Override
        public void clearItem(final boolean b) {
            if (b && this.mItem != null) {
                ExtensionControllerImpl.this.mLeakDetector.trackGarbage(this.mItem);
            }
            this.mItem = null;
        }
        
        @Override
        public void destroy() {
            for (int i = 0; i < this.mProducers.size(); ++i) {
                this.mProducers.get(i).destroy();
            }
        }
        
        @Override
        public T get() {
            return this.mItem;
        }
        
        @Override
        public Context getContext() {
            Context context = this.mPluginContext;
            if (context == null) {
                context = ExtensionControllerImpl.this.mDefaultContext;
            }
            return context;
        }
        
        private class Default<T> implements Item<T>
        {
            private final Supplier<T> mSupplier;
            
            public Default(final ExtensionImpl extensionImpl, final Supplier<T> mSupplier) {
                this.mSupplier = mSupplier;
            }
            
            @Override
            public void destroy() {
            }
            
            @Override
            public T get() {
                return this.mSupplier.get();
            }
            
            @Override
            public int sortOrder() {
                return 4;
            }
        }
        
        private class PluginItem<P extends Plugin> implements Item<T>, PluginListener<P>
        {
            private final PluginConverter<T, P> mConverter;
            private T mItem;
            
            public PluginItem(final String s, final Class<P> clazz, final PluginConverter<T, P> mConverter) {
                this.mConverter = mConverter;
                ExtensionImpl.this.this$0.mPluginManager.addPluginListener(s, (PluginListener<Plugin>)this, clazz);
            }
            
            @Override
            public void destroy() {
                ExtensionControllerImpl.this.mPluginManager.removePluginListener(this);
            }
            
            @Override
            public T get() {
                return this.mItem;
            }
            
            @Override
            public void onPluginConnected(final P mItem, final Context context) {
                ExtensionImpl.this.mPluginContext = context;
                final PluginConverter<T, P> mConverter = this.mConverter;
                if (mConverter != null) {
                    this.mItem = mConverter.getInterfaceFromPlugin(mItem);
                }
                else {
                    this.mItem = (T)mItem;
                }
                ExtensionImpl.this.notifyChanged();
            }
            
            @Override
            public void onPluginDisconnected(final P p) {
                ExtensionImpl.this.mPluginContext = null;
                this.mItem = null;
                ExtensionImpl.this.notifyChanged();
            }
            
            @Override
            public int sortOrder() {
                return 0;
            }
        }
        
        private class TunerItem<T> implements Item<T>, Tunable
        {
            private final TunerFactory<T> mFactory;
            private T mItem;
            private final ArrayMap<String, String> mSettings;
            
            public TunerItem(final TunerFactory<T> mFactory, final String... array) {
                this.mSettings = (ArrayMap<String, String>)new ArrayMap();
                this.mFactory = mFactory;
                ExtensionImpl.this.this$0.mTunerService.addTunable((TunerService.Tunable)this, array);
            }
            
            @Override
            public void destroy() {
                ExtensionControllerImpl.this.mTunerService.removeTunable((TunerService.Tunable)this);
            }
            
            @Override
            public T get() {
                return this.mItem;
            }
            
            @Override
            public void onTuningChanged(final String s, final String s2) {
                this.mSettings.put((Object)s, (Object)s2);
                this.mItem = this.mFactory.create((Map<String, String>)this.mSettings);
                ExtensionImpl.this.notifyChanged();
            }
            
            @Override
            public int sortOrder() {
                return 1;
            }
        }
    }
    
    private interface Item<T> extends Producer<T>
    {
        int sortOrder();
    }
    
    private interface Producer<T>
    {
        void destroy();
        
        T get();
    }
}
