// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.volume;

import com.android.systemui.R$bool;
import android.content.res.Configuration;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.systemui.qs.tiles.DndTile;
import android.os.Handler;
import android.content.Context;
import android.util.Log;
import com.android.systemui.SystemUI;

public class VolumeUI extends SystemUI
{
    private static boolean LOGD;
    private boolean mEnabled;
    private VolumeDialogComponent mVolumeComponent;
    
    static {
        VolumeUI.LOGD = Log.isLoggable("VolumeUI", 3);
    }
    
    public VolumeUI(final Context context, final VolumeDialogComponent mVolumeComponent) {
        super(context);
        new Handler();
        this.mVolumeComponent = mVolumeComponent;
    }
    
    private void setDefaultVolumeController() {
        DndTile.setVisible(super.mContext, true);
        if (VolumeUI.LOGD) {
            Log.d("VolumeUI", "Registering default volume controller");
        }
        this.mVolumeComponent.register();
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.print("mEnabled=");
        printWriter.println(this.mEnabled);
        if (!this.mEnabled) {
            return;
        }
        this.mVolumeComponent.dump(fileDescriptor, printWriter, array);
    }
    
    @Override
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (!this.mEnabled) {
            return;
        }
        this.mVolumeComponent.onConfigurationChanged(configuration);
    }
    
    @Override
    public void start() {
        final boolean boolean1 = super.mContext.getResources().getBoolean(R$bool.enable_volume_ui);
        final boolean boolean2 = super.mContext.getResources().getBoolean(R$bool.enable_safety_warning);
        if (!(this.mEnabled = (boolean1 || boolean2))) {
            return;
        }
        this.mVolumeComponent.setEnableDialogs(boolean1, boolean2);
        this.setDefaultVolumeController();
    }
}
