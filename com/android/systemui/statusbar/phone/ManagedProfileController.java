// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.policy.CallbackController;

public interface ManagedProfileController extends CallbackController<Callback>
{
    boolean hasActiveProfile();
    
    boolean isWorkModeEnabled();
    
    void setWorkModeEnabled(final boolean p0);
    
    public interface Callback
    {
        void onManagedProfileChanged();
        
        void onManagedProfileRemoved();
    }
}
