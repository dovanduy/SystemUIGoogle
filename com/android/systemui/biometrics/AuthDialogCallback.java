// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.biometrics;

public interface AuthDialogCallback
{
    void onDeviceCredentialPressed();
    
    void onDismissed(final int p0, final byte[] p1);
    
    void onSystemEvent(final int p0);
    
    void onTryAgainPressed();
}
