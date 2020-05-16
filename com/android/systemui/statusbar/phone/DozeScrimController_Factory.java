// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.doze.DozeLog;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class DozeScrimController_Factory implements Factory<DozeScrimController>
{
    private final Provider<DozeLog> dozeLogProvider;
    private final Provider<DozeParameters> dozeParametersProvider;
    
    public DozeScrimController_Factory(final Provider<DozeParameters> dozeParametersProvider, final Provider<DozeLog> dozeLogProvider) {
        this.dozeParametersProvider = dozeParametersProvider;
        this.dozeLogProvider = dozeLogProvider;
    }
    
    public static DozeScrimController_Factory create(final Provider<DozeParameters> provider, final Provider<DozeLog> provider2) {
        return new DozeScrimController_Factory(provider, provider2);
    }
    
    public static DozeScrimController provideInstance(final Provider<DozeParameters> provider, final Provider<DozeLog> provider2) {
        return new DozeScrimController(provider.get(), provider2.get());
    }
    
    @Override
    public DozeScrimController get() {
        return provideInstance(this.dozeParametersProvider, this.dozeLogProvider);
    }
}
