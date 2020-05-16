// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import android.view.View;

public class TunablePadding implements Tunable
{
    private final int mDefaultSize;
    private final float mDensity;
    private final int mFlags;
    private final View mView;
    
    private int getPadding(int n, final int n2) {
        if ((this.mFlags & n2) == 0x0) {
            n = 0;
        }
        return n;
    }
    
    @Override
    public void onTuningChanged(final String s, final String s2) {
        int mDefaultSize;
        int n = mDefaultSize = this.mDefaultSize;
        while (true) {
            if (s2 == null) {
                break Label_0033;
            }
            try {
                mDefaultSize = (int)(Integer.parseInt(s2) * this.mDensity);
                final boolean layoutRtl = this.mView.isLayoutRtl();
                int n2 = 2;
                if (layoutRtl) {
                    n = 2;
                }
                else {
                    n = 1;
                }
                if (this.mView.isLayoutRtl()) {
                    n2 = 1;
                }
                this.mView.setPadding(this.getPadding(mDefaultSize, n), this.getPadding(mDefaultSize, 4), this.getPadding(mDefaultSize, n2), this.getPadding(mDefaultSize, 8));
            }
            catch (NumberFormatException ex) {
                mDefaultSize = n;
                continue;
            }
            break;
        }
    }
    
    public static class TunablePaddingService
    {
        public TunablePaddingService(final TunerService tunerService) {
        }
    }
}
