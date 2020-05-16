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

public class KeyboardIconView extends FrameLayout
{
    private final int COLOR_DARK_BACKGROUND;
    private final int COLOR_LIGHT_BACKGROUND;
    private ImageView mKeyboardIcon;
    
    public KeyboardIconView(final Context context) {
        this(context, null);
    }
    
    public KeyboardIconView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public KeyboardIconView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public KeyboardIconView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.COLOR_DARK_BACKGROUND = this.getResources().getColor(R$color.transcription_icon_dark);
        this.COLOR_LIGHT_BACKGROUND = this.getResources().getColor(R$color.transcription_icon_light);
    }
    
    void onDensityChanged() {
        this.mKeyboardIcon.setImageDrawable(this.getContext().getDrawable(R$drawable.ic_keyboard));
    }
    
    protected void onFinishInflate() {
        this.mKeyboardIcon = (ImageView)this.findViewById(R$id.keyboard_icon_image);
    }
    
    public void setHasDarkBackground(final boolean b) {
        final ImageView mKeyboardIcon = this.mKeyboardIcon;
        int n;
        if (b) {
            n = this.COLOR_DARK_BACKGROUND;
        }
        else {
            n = this.COLOR_LIGHT_BACKGROUND;
        }
        mKeyboardIcon.setImageTintList(ColorStateList.valueOf(n));
    }
}
