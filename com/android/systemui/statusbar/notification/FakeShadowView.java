// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import com.android.systemui.R$dimen;
import android.graphics.Outline;
import android.view.ViewOutlineProvider;
import android.view.ViewGroup$LayoutParams;
import android.widget.LinearLayout$LayoutParams;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import com.android.systemui.statusbar.AlphaOptimizedFrameLayout;

public class FakeShadowView extends AlphaOptimizedFrameLayout
{
    private View mFakeShadow;
    private float mOutlineAlpha;
    private final int mShadowMinHeight;
    
    public FakeShadowView(final Context context) {
        this(context, null);
    }
    
    public FakeShadowView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public FakeShadowView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public FakeShadowView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        (this.mFakeShadow = new View(context)).setVisibility(4);
        this.mFakeShadow.setLayoutParams((ViewGroup$LayoutParams)new LinearLayout$LayoutParams(-1, (int)(this.getResources().getDisplayMetrics().density * 48.0f)));
        this.mFakeShadow.setOutlineProvider((ViewOutlineProvider)new ViewOutlineProvider() {
            public void getOutline(final View view, final Outline outline) {
                outline.setRect(0, 0, FakeShadowView.this.getWidth(), FakeShadowView.this.mFakeShadow.getHeight());
                outline.setAlpha(FakeShadowView.this.mOutlineAlpha);
            }
        });
        this.addView(this.mFakeShadow);
        this.mShadowMinHeight = Math.max(1, context.getResources().getDimensionPixelSize(R$dimen.notification_divider_height));
    }
    
    public void setFakeShadowTranslationZ(float max, final float mOutlineAlpha, final int n, final int n2) {
        if (max == 0.0f) {
            this.mFakeShadow.setVisibility(4);
        }
        else {
            this.mFakeShadow.setVisibility(0);
            max = Math.max((float)this.mShadowMinHeight, max);
            this.mFakeShadow.setTranslationZ(max);
            this.mFakeShadow.setTranslationX((float)n2);
            final View mFakeShadow = this.mFakeShadow;
            mFakeShadow.setTranslationY((float)(n - mFakeShadow.getHeight()));
            if (mOutlineAlpha != this.mOutlineAlpha) {
                this.mOutlineAlpha = mOutlineAlpha;
                this.mFakeShadow.invalidateOutline();
            }
        }
    }
}
