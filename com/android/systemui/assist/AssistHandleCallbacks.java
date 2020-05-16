// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist;

public interface AssistHandleCallbacks
{
    void hide();
    
    void showAndGo();
    
    void showAndGoDelayed(final long p0, final boolean p1);
    
    void showAndStay();
}
