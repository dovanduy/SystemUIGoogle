// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.graphics.drawable.Drawable;

public interface UserInfoController extends CallbackController<OnUserInfoChangedListener>
{
    void reloadUserInfo();
    
    public interface OnUserInfoChangedListener
    {
        void onUserInfoChanged(final String p0, final Drawable p1, final String p2);
    }
}
