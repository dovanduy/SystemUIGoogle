// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.content.res.ColorStateList;
import com.android.systemui.R$id;
import com.android.systemui.R$drawable;
import com.android.systemui.R$color;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.ImageView;
import android.widget.FrameLayout;

public class ZeroStateIconView extends FrameLayout
{
    private final int COLOR_DARK_BACKGROUND;
    private final int COLOR_LIGHT_BACKGROUND;
    private ImageView mZeroStateIcon;
    
    public ZeroStateIconView(final Context context) {
        this(context, null);
    }
    
    public ZeroStateIconView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public ZeroStateIconView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public ZeroStateIconView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.COLOR_DARK_BACKGROUND = this.getResources().getColor(R$color.transcription_icon_dark);
        this.COLOR_LIGHT_BACKGROUND = this.getResources().getColor(R$color.transcription_icon_light);
    }
    
    void onDensityChanged() {
        this.mZeroStateIcon.setImageDrawable(this.getContext().getDrawable(R$drawable.ic_explore));
    }
    
    protected void onFinishInflate() {
        this.mZeroStateIcon = (ImageView)this.findViewById(R$id.zerostate_icon_image);
    }
    
    public void setHasDarkBackground(final boolean b) {
        final ImageView mZeroStateIcon = this.mZeroStateIcon;
        int n;
        if (b) {
            n = this.COLOR_DARK_BACKGROUND;
        }
        else {
            n = this.COLOR_LIGHT_BACKGROUND;
        }
        mZeroStateIcon.setImageTintList(ColorStateList.valueOf(n));
    }
}
