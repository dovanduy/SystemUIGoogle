// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import dagger.internal.Preconditions;
import com.google.android.systemui.columbus.feedback.UserActivity;
import com.google.android.systemui.columbus.feedback.NavUndimEffect;
import com.google.android.systemui.columbus.feedback.HapticClick;
import javax.inject.Provider;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import java.util.Set;
import dagger.internal.Factory;

public final class ColumbusModule_ProvideColumbusEffectsFactory implements Factory<Set<FeedbackEffect>>
{
    private final Provider<HapticClick> hapticClickProvider;
    private final Provider<NavUndimEffect> navUndimEffectProvider;
    private final Provider<UserActivity> userActivityProvider;
    
    public ColumbusModule_ProvideColumbusEffectsFactory(final Provider<HapticClick> hapticClickProvider, final Provider<NavUndimEffect> navUndimEffectProvider, final Provider<UserActivity> userActivityProvider) {
        this.hapticClickProvider = hapticClickProvider;
        this.navUndimEffectProvider = navUndimEffectProvider;
        this.userActivityProvider = userActivityProvider;
    }
    
    public static ColumbusModule_ProvideColumbusEffectsFactory create(final Provider<HapticClick> provider, final Provider<NavUndimEffect> provider2, final Provider<UserActivity> provider3) {
        return new ColumbusModule_ProvideColumbusEffectsFactory(provider, provider2, provider3);
    }
    
    public static Set<FeedbackEffect> provideInstance(final Provider<HapticClick> provider, final Provider<NavUndimEffect> provider2, final Provider<UserActivity> provider3) {
        return proxyProvideColumbusEffects(provider.get(), provider2.get(), provider3.get());
    }
    
    public static Set<FeedbackEffect> proxyProvideColumbusEffects(final HapticClick hapticClick, final NavUndimEffect navUndimEffect, final UserActivity userActivity) {
        final Set<FeedbackEffect> provideColumbusEffects = ColumbusModule.provideColumbusEffects(hapticClick, navUndimEffect, userActivity);
        Preconditions.checkNotNull(provideColumbusEffects, "Cannot return null from a non-@Nullable @Provides method");
        return provideColumbusEffects;
    }
    
    @Override
    public Set<FeedbackEffect> get() {
        return provideInstance(this.hapticClickProvider, this.navUndimEffectProvider, this.userActivityProvider);
    }
}
