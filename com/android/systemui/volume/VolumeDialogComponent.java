// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.volume;

import com.android.systemui.qs.tiles.DndTile;
import android.content.res.Configuration;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.os.Bundle;
import com.android.systemui.plugins.ActivityStarter;
import android.content.Intent;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.plugins.VolumeDialogController;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.PluginDependencyProvider;
import android.media.VolumePolicy;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.plugins.VolumeDialog;
import android.content.Context;
import com.android.settingslib.applications.InterestingConfigChanges;
import com.android.systemui.tuner.TunerService;

public class VolumeDialogComponent implements VolumeComponent, Tunable, UserActivityListener
{
    private final InterestingConfigChanges mConfigChanges;
    protected final Context mContext;
    private final VolumeDialogControllerImpl mController;
    private VolumeDialog mDialog;
    private final KeyguardViewMediator mKeyguardViewMediator;
    private final VolumeDialog.Callback mVolumeDialogCallback;
    private VolumePolicy mVolumePolicy;
    
    public VolumeDialogComponent(final Context mContext, final KeyguardViewMediator mKeyguardViewMediator, final VolumeDialogControllerImpl mController) {
        this.mConfigChanges = new InterestingConfigChanges(-1073741308);
        this.mVolumePolicy = new VolumePolicy(false, false, false, 400);
        this.mVolumeDialogCallback = new VolumeDialog.Callback() {
            @Override
            public void onZenPrioritySettingsClicked() {
                VolumeDialogComponent.this.startSettings(ZenModePanel.ZEN_PRIORITY_SETTINGS);
            }
            
            @Override
            public void onZenSettingsClicked() {
                VolumeDialogComponent.this.startSettings(ZenModePanel.ZEN_SETTINGS);
            }
        };
        this.mContext = mContext;
        this.mKeyguardViewMediator = mKeyguardViewMediator;
        (this.mController = mController).setUserActivityListener((VolumeDialogControllerImpl.UserActivityListener)this);
        Dependency.get(PluginDependencyProvider.class).allowPluginDependency(VolumeDialogController.class);
        final ExtensionController.ExtensionBuilder<VolumeDialog> extension = Dependency.get(ExtensionController.class).newExtension(VolumeDialog.class);
        extension.withPlugin(VolumeDialog.class);
        extension.withDefault(new _$$Lambda$5eQ6FmuY0CORdNfZebXQAtrsfI4(this));
        extension.withCallback(new _$$Lambda$VolumeDialogComponent$vZvGMkdhFGTZ9hLE1BnozIW6Wb0(this));
        extension.build();
        this.applyConfiguration();
        Dependency.get(TunerService.class).addTunable((TunerService.Tunable)this, "sysui_volume_down_silent", "sysui_volume_up_silent", "sysui_do_not_disturb");
    }
    
    private void applyConfiguration() {
        this.mController.setVolumePolicy(this.mVolumePolicy);
        this.mController.showDndTile(true);
    }
    
    private void setVolumePolicy(final boolean b, final boolean b2, final boolean b3, final int n) {
        final VolumePolicy volumePolicy = new VolumePolicy(b, b2, b3, n);
        this.mVolumePolicy = volumePolicy;
        this.mController.setVolumePolicy(volumePolicy);
    }
    
    private void startSettings(final Intent intent) {
        Dependency.get(ActivityStarter.class).startActivity(intent, true, true);
    }
    
    protected VolumeDialog createDefault() {
        final VolumeDialogImpl volumeDialogImpl = new VolumeDialogImpl(this.mContext);
        volumeDialogImpl.setStreamImportant(1, false);
        volumeDialogImpl.setAutomute(true);
        volumeDialogImpl.setSilentMode(false);
        return volumeDialogImpl;
    }
    
    @Override
    public void dismissNow() {
        this.mController.dismiss();
    }
    
    @Override
    public void dispatchDemoCommand(final String s, final Bundle bundle) {
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        if (this.mConfigChanges.applyNewConfig(this.mContext.getResources())) {
            this.mController.mCallbacks.onConfigurationChanged();
        }
    }
    
    @Override
    public void onTuningChanged(final String anObject, final String s) {
        final VolumePolicy mVolumePolicy = this.mVolumePolicy;
        final boolean volumeDownToEnterSilent = mVolumePolicy.volumeDownToEnterSilent;
        final boolean volumeUpToExitSilent = mVolumePolicy.volumeUpToExitSilent;
        boolean b = mVolumePolicy.doNotDisturbWhenSilent;
        boolean integerSwitch;
        boolean integerSwitch2;
        if ("sysui_volume_down_silent".equals(anObject)) {
            integerSwitch = TunerService.parseIntegerSwitch(s, false);
            integerSwitch2 = volumeUpToExitSilent;
        }
        else if ("sysui_volume_up_silent".equals(anObject)) {
            integerSwitch2 = TunerService.parseIntegerSwitch(s, false);
            integerSwitch = volumeDownToEnterSilent;
        }
        else {
            integerSwitch = volumeDownToEnterSilent;
            integerSwitch2 = volumeUpToExitSilent;
            if ("sysui_do_not_disturb".equals(anObject)) {
                b = TunerService.parseIntegerSwitch(s, false);
                integerSwitch2 = volumeUpToExitSilent;
                integerSwitch = volumeDownToEnterSilent;
            }
        }
        this.setVolumePolicy(integerSwitch, integerSwitch2, b, this.mVolumePolicy.vibrateToSilentDebounce);
    }
    
    @Override
    public void onUserActivity() {
        this.mKeyguardViewMediator.userActivity();
    }
    
    public void register() {
        this.mController.register();
        DndTile.setCombinedIcon(this.mContext, true);
    }
    
    void setEnableDialogs(final boolean b, final boolean b2) {
        this.mController.setEnableDialogs(b, b2);
    }
}
