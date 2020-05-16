// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

import dagger.internal.DoubleCheck;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.model.SysUiState;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class AssistHandleLikeHomeBehavior_Factory implements Factory<AssistHandleLikeHomeBehavior>
{
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<SysUiState> sysUiFlagContainerProvider;
    private final Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider;
    
    public AssistHandleLikeHomeBehavior_Factory(final Provider<StatusBarStateController> statusBarStateControllerProvider, final Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider, final Provider<SysUiState> sysUiFlagContainerProvider) {
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.wakefulnessLifecycleProvider = wakefulnessLifecycleProvider;
        this.sysUiFlagContainerProvider = sysUiFlagContainerProvider;
    }
    
    public static AssistHandleLikeHomeBehavior_Factory create(final Provider<StatusBarStateController> provider, final Provider<WakefulnessLifecycle> provider2, final Provider<SysUiState> provider3) {
        return new AssistHandleLikeHomeBehavior_Factory(provider, provider2, provider3);
    }
    
    public static AssistHandleLikeHomeBehavior provideInstance(final Provider<StatusBarStateController> provider, final Provider<WakefulnessLifecycle> provider2, final Provider<SysUiState> provider3) {
        return new AssistHandleLikeHomeBehavior(DoubleCheck.lazy(provider), DoubleCheck.lazy(provider2), DoubleCheck.lazy(provider3));
    }
    
    @Override
    public AssistHandleLikeHomeBehavior get() {
        return provideInstance(this.statusBarStateControllerProvider, this.wakefulnessLifecycleProvider, this.sysUiFlagContainerProvider);
    }
}
