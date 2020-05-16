// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls;

import kotlin.jvm.internal.Intrinsics;
import android.os.UserHandle;

public interface UserAwareController
{
    void changeUser(final UserHandle p0);
    
    int getCurrentUserId();
    
    public static final class DefaultImpls
    {
        public static void changeUser(final UserAwareController userAwareController, final UserHandle userHandle) {
            Intrinsics.checkParameterIsNotNull(userHandle, "newUser");
        }
    }
}
