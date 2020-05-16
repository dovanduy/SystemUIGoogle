// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import android.content.Intent;
import android.net.Uri;
import com.android.systemui.Prefs;
import android.widget.Checkable;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.volume.ZenModePanel;
import com.android.systemui.statusbar.policy.ZenModeController;
import android.view.View;
import android.view.View$OnClickListener;
import android.widget.LinearLayout;

public class TunerZenModePanel extends LinearLayout implements View$OnClickListener
{
    private View mButtons;
    private ZenModeController mController;
    private View mDone;
    private View$OnClickListener mDoneListener;
    private View mHeaderSwitch;
    private View mMoreSettings;
    private final Runnable mUpdate;
    private int mZenMode;
    private ZenModePanel mZenModePanel;
    
    public TunerZenModePanel(final Context context, final AttributeSet set) {
        super(context, set);
        this.mUpdate = new Runnable() {
            @Override
            public void run() {
                TunerZenModePanel.this.updatePanel();
            }
        };
    }
    
    private void postUpdatePanel() {
        this.removeCallbacks(this.mUpdate);
        this.postDelayed(this.mUpdate, 40L);
    }
    
    private void updatePanel() {
        final int mZenMode = this.mZenMode;
        final int n = 0;
        final boolean checked = mZenMode != 0;
        ((Checkable)this.mHeaderSwitch.findViewById(16908311)).setChecked(checked);
        final ZenModePanel mZenModePanel = this.mZenModePanel;
        int visibility;
        if (checked) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        mZenModePanel.setVisibility(visibility);
        final View mButtons = this.mButtons;
        int visibility2;
        if (checked) {
            visibility2 = n;
        }
        else {
            visibility2 = 8;
        }
        mButtons.setVisibility(visibility2);
    }
    
    public void onClick(final View view) {
        if (view == this.mHeaderSwitch) {
            if (this.mZenMode == 0) {
                final int int1 = Prefs.getInt(super.mContext, "DndFavoriteZen", 3);
                this.mZenMode = int1;
                this.mController.setZen(int1, null, "TunerZenModePanel");
                this.postUpdatePanel();
            }
            else {
                this.mZenMode = 0;
                this.mController.setZen(0, null, "TunerZenModePanel");
                this.postUpdatePanel();
            }
        }
        else if (view == this.mMoreSettings) {
            final Intent intent = new Intent("android.settings.ZEN_MODE_SETTINGS");
            intent.addFlags(268435456);
            this.getContext().startActivity(intent);
        }
        else if (view == this.mDone) {
            this.setVisibility(8);
            this.mDoneListener.onClick(view);
        }
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
