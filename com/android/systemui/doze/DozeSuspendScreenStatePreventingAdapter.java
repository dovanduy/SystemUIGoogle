// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

import com.android.systemui.statusbar.phone.DozeParameters;

public class DozeSuspendScreenStatePreventingAdapter extends Delegate
{
    DozeSuspendScreenStatePreventingAdapter(final Service service) {
        super(service);
    }
    
    private static boolean isNeeded(final DozeParameters dozeParameters) {
        return dozeParameters.getDozeSuspendDisplayStateSupported() ^ true;
    }
    
    public static Service wrapIfNeeded(final Service service, final DozeParameters dozeParameters) {
        Object o = service;
        if (isNeeded(dozeParameters)) {
            o = new DozeSuspendScreenStatePreventingAdapter(service);
        }
        return (Service)o;
    }
    
    @Override
    public void setDozeScreenState(final int n) {
        int dozeScreenState = n;
        if (n == 4) {
            dozeScreenState = 3;
        }
        super.setDozeScreenState(dozeScreenState);
    }
}
