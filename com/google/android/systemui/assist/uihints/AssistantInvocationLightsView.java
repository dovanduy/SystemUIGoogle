// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.util.MathUtils;
import com.android.systemui.assist.ui.PerimeterPathGuide;
import com.android.systemui.assist.ui.PathSpecCornerPathRenderer;
import com.android.systemui.assist.ui.CornerPathRenderer;
import android.content.res.Resources;
import com.android.systemui.R$color;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.assist.ui.InvocationLightsView;

public class AssistantInvocationLightsView extends InvocationLightsView
{
    private final int mColorBlue;
    private final int mColorGreen;
    private final int mColorRed;
    private final int mColorYellow;
    
    public AssistantInvocationLightsView(final Context context) {
        this(context, null);
    }
    
    public AssistantInvocationLightsView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public AssistantInvocationLightsView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public AssistantInvocationLightsView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        final Resources resources = context.getResources();
        this.mColorRed = resources.getColor(R$color.edge_light_red);
        this.mColorYellow = resources.getColor(R$color.edge_light_yellow);
        this.mColorBlue = resources.getColor(R$color.edge_light_blue);
        this.mColorGreen = resources.getColor(R$color.edge_light_green);
    }
    
    @Override
    protected CornerPathRenderer createCornerPathRenderer(final Context context) {
        return new PathSpecCornerPathRenderer(context);
    }
    
    @Override
    public void onInvocationProgress(float n) {
        if (n <= 1.0f) {
            super.onInvocationProgress(n);
        }
        else {
            final float n2 = super.mGuide.getRegionWidth(PerimeterPathGuide.Region.BOTTOM_LEFT) * 0.6f / 2.0f;
            final float n3 = super.mGuide.getRegionWidth(PerimeterPathGuide.Region.BOTTOM) / 4.0f;
            final float lerp = MathUtils.lerp(n2, n3, 1.0f - (n - 1.0f));
            this.setLight(0, n3 - lerp, n3);
            n = 2.0f * n3;
            this.setLight(1, n3, n);
            final float n4 = n3 * 3.0f;
            this.setLight(2, n, n4);
            this.setLight(3, n4, lerp + n4);
            this.setVisibility(0);
        }
        this.invalidate();
    }
    
    public void setGoogleAssistant(final boolean b) {
        if (b) {
            this.setColors(this.mColorBlue, this.mColorRed, this.mColorYellow, this.mColorGreen);
        }
        else {
            this.setColors(null);
        }
    }
}
