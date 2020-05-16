// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.plugins.ActivityStarter;

public interface KeyguardDismissHandler
{
    void executeWhenUnlocked(final ActivityStarter.OnDismissAction p0, final boolean p1);
}
