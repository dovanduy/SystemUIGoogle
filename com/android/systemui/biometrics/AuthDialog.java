// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.biometrics;

import android.view.WindowManager;
import android.os.Bundle;

public interface AuthDialog
{
    void animateToCredentialUI();
    
    void dismissFromSystemServer();
    
    void dismissWithoutCallback(final boolean p0);
    
    String getOpPackageName();
    
    boolean isAllowDeviceCredentials();
    
    void onAuthenticationFailed(final String p0);
    
    void onAuthenticationSucceeded();
    
    void onError(final String p0);
    
    void onHelp(final String p0);
    
    void onSaveState(final Bundle p0);
    
    void show(final WindowManager p0, final Bundle p1);
}
