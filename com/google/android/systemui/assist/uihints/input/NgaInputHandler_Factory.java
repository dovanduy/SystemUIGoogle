// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints.input;

import com.google.android.systemui.assist.uihints.TouchInsideHandler;
import java.util.Set;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class NgaInputHandler_Factory implements Factory<NgaInputHandler>
{
    private final Provider<Set<TouchInsideRegion>> dismissablesProvider;
    private final Provider<TouchInsideHandler> touchInsideHandlerProvider;
    private final Provider<Set<TouchActionRegion>> touchablesProvider;
    
    public NgaInputHandler_Factory(final Provider<TouchInsideHandler> touchInsideHandlerProvider, final Provider<Set<TouchActionRegion>> touchablesProvider, final Provider<Set<TouchInsideRegion>> dismissablesProvider) {
        this.touchInsideHandlerProvider = touchInsideHandlerProvider;
        this.touchablesProvider = touchablesProvider;
        this.dismissablesProvider = dismissablesProvider;
    }
    
    public static NgaInputHandler_Factory create(final Provider<TouchInsideHandler> provider, final Provider<Set<TouchActionRegion>> provider2, final Provider<Set<TouchInsideRegion>> provider3) {
        return new NgaInputHandler_Factory(provider, provider2, provider3);
    }
    
    public static NgaInputHandler provideInstance(final Provider<TouchInsideHandler> provider, final Provider<Set<TouchActionRegion>> provider2, final Provider<Set<TouchInsideRegion>> provider3) {
        return new NgaInputHandler(provider.get(), provider2.get(), provider3.get());
    }
    
    @Override
    public NgaInputHandler get() {
        return provideInstance(this.touchInsideHandlerProvider, this.touchablesProvider, this.dismissablesProvider);
    }
}
