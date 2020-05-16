// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.animation;

import android.view.animation.Interpolator;
import android.content.Context;

public class DisappearAnimationUtils extends AppearAnimationUtils
{
    private static final RowTranslationScaler ROW_TRANSLATION_SCALER;
    
    static {
        ROW_TRANSLATION_SCALER = new RowTranslationScaler() {
            @Override
            public float getRowTranslationScale(final int n, final int n2) {
                return (float)(Math.pow(n2 - n, 2.0) / n2);
            }
        };
    }
    
    public DisappearAnimationUtils(final Context context, final long n, final float n2, final float n3, final Interpolator interpolator) {
        this(context, n, n2, n3, interpolator, DisappearAnimationUtils.ROW_TRANSLATION_SCALER);
    }
    
    public DisappearAnimationUtils(final Context context, final long n, final float n2, final float n3, final Interpolator interpolator, final RowTranslationScaler mRowTranslationScaler) {
        super(context, n, n2, n3, interpolator);
        super.mRowTranslationScaler = mRowTranslationScaler;
        super.mAppearing = false;
    }
    
    @Override
    protected long calculateDelay(final int n, final int n2) {
        return (long)((n * 60 + n2 * (Math.pow(n, 0.4) + 0.4) * 10.0) * super.mDelayScale);
    }
}
