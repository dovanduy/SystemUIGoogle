// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.globalactions;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.Objects;
import com.android.internal.statusbar.IStatusBarService$Stub;
import android.os.ServiceManager;
import android.os.RemoteException;
import android.content.Context;
import javax.inject.Provider;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.plugins.GlobalActions;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.SystemUI;

public class GlobalActionsComponent extends SystemUI implements Callbacks, GlobalActionsManager
{
    private IStatusBarService mBarService;
    private final CommandQueue mCommandQueue;
    private ExtensionController.Extension<GlobalActions> mExtension;
    private final ExtensionController mExtensionController;
    private final Provider<GlobalActions> mGlobalActionsProvider;
    private GlobalActions mPlugin;
    
    public GlobalActionsComponent(final Context context, final CommandQueue mCommandQueue, final ExtensionController mExtensionController, final Provider<GlobalActions> mGlobalActionsProvider) {
        super(context);
        this.mCommandQueue = mCommandQueue;
        this.mExtensionController = mExtensionController;
        this.mGlobalActionsProvider = mGlobalActionsProvider;
    }
    
    private void onExtensionCallback(final GlobalActions mPlugin) {
        final GlobalActions mPlugin2 = this.mPlugin;
        if (mPlugin2 != null) {
            mPlugin2.destroy();
        }
        this.mPlugin = mPlugin;
    }
    
    @Override
    public void handleShowGlobalActionsMenu() {
        this.mExtension.get().showGlobalActions((GlobalActions.GlobalActionsManager)this);
    }
    
    @Override
    public void handleShowShutdownUi(final boolean b, final String s) {
        this.mExtension.get().showShutdownUi(b, s);
    }
    
    @Override
    public void onGlobalActionsHidden() {
        try {
            this.mBarService.onGlobalActionsHidden();
        }
        catch (RemoteException ex) {}
    }
    
    @Override
    public void onGlobalActionsShown() {
        try {
            this.mBarService.onGlobalActionsShown();
        }
        catch (RemoteException ex) {}
    }
    
    @Override
    public void reboot(final boolean b) {
        try {
            this.mBarService.reboot(b);
        }
        catch (RemoteException ex) {}
    }
    
    @Override
    public void shutdown() {
        try {
            this.mBarService.shutdown();
        }
        catch (RemoteException ex) {}
    }
    
    @Override
    public void start() {
        this.mBarService = IStatusBarService$Stub.asInterface(ServiceManager.getService("statusbar"));
        final ExtensionController.ExtensionBuilder<GlobalActions> extension = this.mExtensionController.newExtension(GlobalActions.class);
        extension.withPlugin(GlobalActions.class);
        final Provider<GlobalActions> mGlobalActionsProvider = this.mGlobalActionsProvider;
        Objects.requireNonNull(mGlobalActionsProvider);
        extension.withDefault(new _$$Lambda$_JK3806lHhx7U5FWWTWu23JrO_A(mGlobalActionsProvider));
        extension.withCallback(new _$$Lambda$GlobalActionsComponent$bGplH0pcKhfpL1pOMBpgWKJntvw(this));
        final ExtensionController.Extension<GlobalActions> build = extension.build();
        this.mExtension = build;
        this.mPlugin = build.get();
        this.mCommandQueue.addCallback((CommandQueue.Callbacks)this);
    }
}
