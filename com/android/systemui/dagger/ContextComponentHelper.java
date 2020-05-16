// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dagger;

import com.android.systemui.SystemUI;
import android.app.Service;
import com.android.systemui.recents.RecentsImplementation;
import android.content.BroadcastReceiver;
import android.app.Activity;

public interface ContextComponentHelper
{
    Activity resolveActivity(final String p0);
    
    BroadcastReceiver resolveBroadcastReceiver(final String p0);
    
    RecentsImplementation resolveRecents(final String p0);
    
    Service resolveService(final String p0);
    
    SystemUI resolveSystemUI(final String p0);
}
