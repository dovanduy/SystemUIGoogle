// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.dagger;

import com.google.android.systemui.keyguard.KeyguardSliceProviderGoogle;
import com.android.systemui.dagger.SystemUIRootComponent;

public interface SystemUIGoogleRootComponent extends SystemUIRootComponent
{
    void inject(final KeyguardSliceProviderGoogle p0);
}
