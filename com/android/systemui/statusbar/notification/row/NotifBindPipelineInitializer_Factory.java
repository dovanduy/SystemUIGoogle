// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import javax.inject.Provider;
import dagger.internal.Factory;

public final class NotifBindPipelineInitializer_Factory implements Factory<NotifBindPipelineInitializer>
{
    private final Provider<NotifBindPipeline> pipelineProvider;
    private final Provider<RowContentBindStage> stageProvider;
    
    public NotifBindPipelineInitializer_Factory(final Provider<NotifBindPipeline> pipelineProvider, final Provider<RowContentBindStage> stageProvider) {
        this.pipelineProvider = pipelineProvider;
        this.stageProvider = stageProvider;
    }
    
    public static NotifBindPipelineInitializer_Factory create(final Provider<NotifBindPipeline> provider, final Provider<RowContentBindStage> provider2) {
        return new NotifBindPipelineInitializer_Factory(provider, provider2);
    }
    
    public static NotifBindPipelineInitializer provideInstance(final Provider<NotifBindPipeline> provider, final Provider<RowContentBindStage> provider2) {
        return new NotifBindPipelineInitializer(provider.get(), provider2.get());
    }
    
    @Override
    public NotifBindPipelineInitializer get() {
        return provideInstance(this.pipelineProvider, this.stageProvider);
    }
}
