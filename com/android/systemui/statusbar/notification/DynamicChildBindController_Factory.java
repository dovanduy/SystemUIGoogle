// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import com.android.systemui.statusbar.notification.row.RowContentBindStage;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class DynamicChildBindController_Factory implements Factory<DynamicChildBindController>
{
    private final Provider<RowContentBindStage> stageProvider;
    
    public DynamicChildBindController_Factory(final Provider<RowContentBindStage> stageProvider) {
        this.stageProvider = stageProvider;
    }
    
    public static DynamicChildBindController_Factory create(final Provider<RowContentBindStage> provider) {
        return new DynamicChildBindController_Factory(provider);
    }
    
    public static DynamicChildBindController provideInstance(final Provider<RowContentBindStage> provider) {
        return new DynamicChildBindController(provider.get());
    }
    
    @Override
    public DynamicChildBindController get() {
        return provideInstance(this.stageProvider);
    }
}
