// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.globalactions;

import android.view.WindowManager$LayoutParams;
import android.view.Window;
import android.widget.TextView;
import android.widget.ProgressBar;
import com.android.settingslib.Utils;
import com.android.systemui.R$attr;
import android.graphics.drawable.Drawable;
import android.content.DialogInterface$OnShowListener;
import com.android.systemui.R$style;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.R$dimen;
import android.content.DialogInterface;
import android.app.Dialog;
import com.android.internal.colorextraction.drawable.ScrimDrawable;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.GlobalActionsPanelPlugin;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import dagger.Lazy;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import android.content.Context;
import com.android.systemui.statusbar.BlurUtils;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.plugins.GlobalActions;

public class GlobalActionsImpl implements GlobalActions, Callbacks
{
    private final BlurUtils mBlurUtils;
    private final CommandQueue mCommandQueue;
    private final Context mContext;
    private final DeviceProvisionedController mDeviceProvisionedController;
    private boolean mDisabled;
    private GlobalActionsDialog mGlobalActionsDialog;
    private final Lazy<GlobalActionsDialog> mGlobalActionsDialogLazy;
    private final KeyguardStateController mKeyguardStateController;
    private final ExtensionController.Extension<GlobalActionsPanelPlugin> mPanelExtension;
    
    public GlobalActionsImpl(final Context mContext, final CommandQueue mCommandQueue, final Lazy<GlobalActionsDialog> mGlobalActionsDialogLazy, final BlurUtils mBlurUtils) {
        this.mContext = mContext;
        this.mGlobalActionsDialogLazy = mGlobalActionsDialogLazy;
        this.mKeyguardStateController = Dependency.get(KeyguardStateController.class);
        this.mDeviceProvisionedController = Dependency.get(DeviceProvisionedController.class);
        this.mCommandQueue = mCommandQueue;
        this.mBlurUtils = mBlurUtils;
        mCommandQueue.addCallback((CommandQueue.Callbacks)this);
        final ExtensionController.ExtensionBuilder<GlobalActionsPanelPlugin> extension = Dependency.get(ExtensionController.class).newExtension(GlobalActionsPanelPlugin.class);
        extension.withPlugin(GlobalActionsPanelPlugin.class);
        this.mPanelExtension = extension.build();
    }
    
    private String getReasonMessage(final String s) {
        if (s != null && s.startsWith("recovery-update")) {
            return this.mContext.getString(17041101);
        }
        if (s != null && s.equals("recovery")) {
            return this.mContext.getString(17041097);
        }
        return null;
    }
    
    private int getRebootMessage(final boolean b, final String s) {
        if (s != null && s.startsWith("recovery-update")) {
            return 17041100;
        }
        if (s != null && s.equals("recovery")) {
            return 17041096;
        }
        if (b) {
            return 17041096;
        }
        return 17041241;
    }
    
    @Override
    public void destroy() {
        this.mCommandQueue.removeCallback((CommandQueue.Callbacks)this);
        final GlobalActionsDialog mGlobalActionsDialog = this.mGlobalActionsDialog;
        if (mGlobalActionsDialog != null) {
            mGlobalActionsDialog.destroy();
            this.mGlobalActionsDialog = null;
        }
    }
    
    @Override
    public void disable(final int n, final int n2, final int n3, final boolean b) {
        final boolean mDisabled = (n3 & 0x8) != 0x0;
        if (n == this.mContext.getDisplayId()) {
            if (mDisabled != this.mDisabled) {
                this.mDisabled = mDisabled;
                if (mDisabled) {
                    final GlobalActionsDialog mGlobalActionsDialog = this.mGlobalActionsDialog;
                    if (mGlobalActionsDialog != null) {
                        mGlobalActionsDialog.dismissDialog();
                    }
                }
            }
        }
    }
    
    @Override
    public void showGlobalActions(final GlobalActionsManager globalActionsManager) {
        if (this.mDisabled) {
            return;
        }
        (this.mGlobalActionsDialog = this.mGlobalActionsDialogLazy.get()).showDialog(this.mKeyguardStateController.isShowing(), this.mDeviceProvisionedController.isDeviceProvisioned(), this.mPanelExtension.get());
        Dependency.get(KeyguardUpdateMonitor.class).requestFaceAuth();
    }
    
    @Override
    public void showShutdownUi(final boolean b, String reasonMessage) {
        final ScrimDrawable backgroundDrawable = new ScrimDrawable();
        final Dialog dialog = new Dialog(this.mContext, R$style.Theme_SystemUI_Dialog_GlobalActions);
        dialog.setOnShowListener((DialogInterface$OnShowListener)new _$$Lambda$GlobalActionsImpl$FGsviRHv4VNB5P1rRHu2A1JPouY(this, backgroundDrawable, dialog));
        final Window window = dialog.getWindow();
        window.requestFeature(1);
        final WindowManager$LayoutParams attributes = window.getAttributes();
        attributes.systemUiVisibility |= 0x700;
        window.getDecorView();
        window.getAttributes().width = -1;
        window.getAttributes().height = -1;
        window.getAttributes().layoutInDisplayCutoutMode = 3;
        window.setType(2020);
        window.getAttributes().setFitInsetsTypes(0);
        window.clearFlags(2);
        window.addFlags(17629472);
        window.setBackgroundDrawable((Drawable)backgroundDrawable);
        window.setWindowAnimations(R$style.Animation_ShutdownUi);
        dialog.setContentView(17367295);
        dialog.setCancelable(false);
        final int colorAttrDefaultColor = Utils.getColorAttrDefaultColor(this.mContext, R$attr.wallpaperTextColor);
        ((ProgressBar)dialog.findViewById(16908301)).getIndeterminateDrawable().setTint(colorAttrDefaultColor);
        final TextView textView = (TextView)dialog.findViewById(16908308);
        final TextView textView2 = (TextView)dialog.findViewById(16908309);
        textView.setTextColor(colorAttrDefaultColor);
        textView2.setTextColor(colorAttrDefaultColor);
        textView2.setText(this.getRebootMessage(b, reasonMessage));
        reasonMessage = this.getReasonMessage(reasonMessage);
        if (reasonMessage != null) {
            textView.setVisibility(0);
            textView.setText((CharSequence)reasonMessage);
        }
        dialog.show();
    }
}
