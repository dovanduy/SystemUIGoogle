// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.customize;

import com.android.systemui.plugins.qs.QSTile;
import android.widget.TextView;
import com.android.systemui.plugins.qs.QSIconView;
import android.content.Context;
import com.android.systemui.qs.tileimpl.QSTileView;

public class CustomizeTileView extends QSTileView
{
    private boolean mShowAppLabel;
    
    public CustomizeTileView(final Context context, final QSIconView qsIconView) {
        super(context, qsIconView);
    }
    
    @Override
    protected boolean animationsEnabled() {
        return false;
    }
    
    public TextView getAppLabel() {
        return super.mSecondLine;
    }
    
    @Override
    protected void handleStateChanged(final QSTile.State state) {
        super.handleStateChanged(state);
        final TextView mSecondLine = super.mSecondLine;
        int visibility;
        if (this.mShowAppLabel) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        mSecondLine.setVisibility(visibility);
    }
    
    public boolean isLongClickable() {
        return false;
    }
    
    public void setShowAppLabel(final boolean b) {
        this.mShowAppLabel = b;
        final TextView mSecondLine = super.mSecondLine;
        int visibility;
        if (b) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        mSecondLine.setVisibility(visibility);
        super.mLabel.setSingleLine(b);
    }
}
