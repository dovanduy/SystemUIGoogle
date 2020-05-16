// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.keyguard;

import com.google.android.systemui.smartspace.SmartSpaceController;

public final class KeyguardSliceProviderGoogle_MembersInjector implements Object<KeyguardSliceProviderGoogle>
{
    public static void injectMSmartSpaceController(final KeyguardSliceProviderGoogle keyguardSliceProviderGoogle, final SmartSpaceController mSmartSpaceController) {
        keyguardSliceProviderGoogle.mSmartSpaceController = mSmartSpaceController;
    }
}
