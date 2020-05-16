// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

import com.android.systemui.statusbar.phone.DozeParameters;

public class DozeScreenStatePreventingAdapter extends Delegate
{
    DozeScreenStatePreventingAdapter(final Service service) {
        super(service);
    }
    
    private static boolean isNeeded(final DozeParameters dozeParameters) {
        return dozeParameters.getDisplayStateSupported() ^ true;
    }
    
    public static Service wrapIfNeeded(final Service service, final DozeParameters dozeParameters) {
        Object o = service;
        if (isNeeded(dozeParameters)) {
            o = new DozeScreenStatePreventingAdapter(service);
        }
        return (Service)o;
    }
    
    @Override
    public void setDozeScreenState(final int n) {
        int dozeScreenState;
        if (n == 3) {
            dozeScreenState = 2;
        }
        else if ((dozeScreenState = n) == 4) {
            dozeScreenState = 6;
        }
        super.setDozeScreenState(dozeScreenState);
    }
}
