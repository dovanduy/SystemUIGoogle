// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import android.widget.Switch;
import android.content.Intent;
import android.content.res.Resources;
import com.android.systemui.R$string;
import androidx.lifecycle.LifecycleOwner;
import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.RotationLockController;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;

public class RotationLockTile extends QSTileImpl<BooleanState>
{
    private final RotationLockController.RotationLockControllerCallback mCallback;
    private final RotationLockController mController;
    private final Icon mIcon;
    
    public RotationLockTile(final QSHost qsHost, final RotationLockController mController) {
        super(qsHost);
        this.mIcon = ResourceIcon.get(17302794);
        final RotationLockController.RotationLockControllerCallback mCallback = new RotationLockController.RotationLockControllerCallback() {
            @Override
            public void onRotationLockStateChanged(final boolean b, final boolean b2) {
                QSTileImpl.this.refreshState(b);
            }
        };
        this.mCallback = mCallback;
        (this.mController = mController).observe(this, (RotationLockController.RotationLockControllerCallback)mCallback);
    }
    
    private String getAccessibilityString(final boolean b) {
        return super.mContext.getString(R$string.accessibility_quick_settings_rotation);
    }
    
    public static boolean isCurrentOrientationLockPortrait(final RotationLockController rotationLockController, final Resources resources) {
        final int rotationLockOrientation = rotationLockController.getRotationLockOrientation();
        final boolean b = true;
        boolean b2 = true;
        if (rotationLockOrientation == 0) {
            if (resources.getConfiguration().orientation == 2) {
                b2 = false;
            }
            return b2;
        }
        return rotationLockOrientation != 2 && b;
    }
    
    @Override
    protected String composeChangeAnnouncement() {
        return this.getAccessibilityString(((BooleanState)super.mState).value);
    }
    
    @Override
    public Intent getLongClickIntent() {
        return new Intent("android.settings.DISPLAY_SETTINGS");
    }
    
    @Override
    public int getMetricsCategory() {
        return 123;
    }
    
    @Override
    public CharSequence getTileLabel() {
        return this.getState().label;
    }
    
    @Override
    protected void handleClick() {
        final boolean b = ((BooleanState)super.mState).value ^ true;
        this.mController.setRotationLocked(b ^ true);
        this.refreshState(b);
    }
    
    @Override
    protected void handleUpdateState(final BooleanState booleanState, final Object o) {
        final boolean rotationLocked = this.mController.isRotationLocked();
        booleanState.value = (rotationLocked ^ true);
        booleanState.label = super.mContext.getString(R$string.quick_settings_rotation_unlocked_label);
        booleanState.icon = this.mIcon;
        booleanState.contentDescription = this.getAccessibilityString(rotationLocked);
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        int state;
        if (booleanState.value) {
            state = 2;
        }
        else {
            state = 1;
        }
        booleanState.state = state;
    }
    
    @Override
    public BooleanState newTileState() {
        return new QSTile.BooleanState();
    }
}
