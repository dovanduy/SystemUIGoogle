// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip.tv;

import com.android.systemui.R$id;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.LinearLayout;

public class PipControlsView extends LinearLayout
{
    public PipControlsView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public PipControlsView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public PipControlsView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        ((LayoutInflater)this.getContext().getSystemService("layout_inflater")).inflate(R$layout.tv_pip_controls, (ViewGroup)this);
        this.setOrientation(0);
        this.setGravity(49);
    }
    
    PipControlButtonView getCloseButtonView() {
        return (PipControlButtonView)this.findViewById(R$id.close_button);
    }
    
    PipControlButtonView getFullButtonView() {
        return (PipControlButtonView)this.findViewById(R$id.full_button);
    }
    
    PipControlButtonView getPlayPauseButtonView() {
        return (PipControlButtonView)this.findViewById(R$id.play_pause_button);
    }
}
