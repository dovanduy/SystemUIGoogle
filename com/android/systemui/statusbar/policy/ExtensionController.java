// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import java.util.Map;
import java.util.function.Supplier;
import android.content.Context;
import java.util.function.Consumer;

public interface ExtensionController
{
     <T> ExtensionBuilder<T> newExtension(final Class<T> p0);
    
    public interface Extension<T>
    {
        void addCallback(final Consumer<T> p0);
        
        void clearItem(final boolean p0);
        
        void destroy();
        
        T get();
        
        Context getContext();
    }
    
    public interface ExtensionBuilder<T>
    {
        Extension<T> build();
        
        ExtensionBuilder<T> withCallback(final Consumer<T> p0);
        
        ExtensionBuilder<T> withDefault(final Supplier<T> p0);
        
         <P extends T> ExtensionBuilder<T> withPlugin(final Class<P> p0);
        
         <P> ExtensionBuilder<T> withPlugin(final Class<P> p0, final String p1, final PluginConverter<T, P> p2);
        
        ExtensionBuilder<T> withTunerFactory(final TunerFactory<T> p0);
    }
    
    public interface PluginConverter<T, P>
    {
        T getInterfaceFromPlugin(final P p0);
    }
    
    public interface TunerFactory<T>
    {
        T create(final Map<String, String> p0);
        
        String[] keys();
    }
}
