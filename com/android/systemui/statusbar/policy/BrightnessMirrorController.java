// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.content.res.Resources;
import android.view.ViewGroup$LayoutParams;
import com.android.systemui.R$integer;
import com.android.systemui.R$dimen;
import android.widget.FrameLayout$LayoutParams;
import java.util.Objects;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import com.android.systemui.R$id;
import java.util.function.Consumer;
import com.android.systemui.statusbar.phone.NotificationShadeWindowView;
import com.android.systemui.statusbar.phone.NotificationPanelViewController;
import com.android.systemui.statusbar.NotificationShadeDepthController;
import android.util.ArraySet;
import android.view.View;

public class BrightnessMirrorController implements CallbackController<BrightnessMirrorListener>
{
    private View mBrightnessMirror;
    private final ArraySet<BrightnessMirrorListener> mBrightnessMirrorListeners;
    private final NotificationShadeDepthController mDepthController;
    private final int[] mInt2Cache;
    private final NotificationPanelViewController mNotificationPanel;
    private final NotificationShadeWindowView mStatusBarWindow;
    private final Consumer<Boolean> mVisibilityCallback;
    
    public BrightnessMirrorController(final NotificationShadeWindowView mStatusBarWindow, final NotificationPanelViewController mNotificationPanel, final NotificationShadeDepthController mDepthController, final Consumer<Boolean> mVisibilityCallback) {
        this.mBrightnessMirrorListeners = (ArraySet<BrightnessMirrorListener>)new ArraySet();
        this.mInt2Cache = new int[2];
        this.mStatusBarWindow = mStatusBarWindow;
        this.mBrightnessMirror = mStatusBarWindow.findViewById(R$id.brightness_mirror);
        this.mNotificationPanel = mNotificationPanel;
        this.mDepthController = mDepthController;
        mNotificationPanel.setPanelAlphaEndAction(new _$$Lambda$BrightnessMirrorController$6Ez050oVQOhwQ3Mf_NjJAvUx4_k(this));
        this.mVisibilityCallback = mVisibilityCallback;
    }
    
    private void reinflate() {
        final int indexOfChild = this.mStatusBarWindow.indexOfChild(this.mBrightnessMirror);
        this.mStatusBarWindow.removeView(this.mBrightnessMirror);
        final LayoutInflater from = LayoutInflater.from(this.mBrightnessMirror.getContext());
        final int brightness_mirror = R$layout.brightness_mirror;
        final NotificationShadeWindowView mStatusBarWindow = this.mStatusBarWindow;
        int i = 0;
        final View inflate = from.inflate(brightness_mirror, (ViewGroup)mStatusBarWindow, false);
        this.mBrightnessMirror = inflate;
        this.mStatusBarWindow.addView(inflate, indexOfChild);
        while (i < this.mBrightnessMirrorListeners.size()) {
            ((BrightnessMirrorListener)this.mBrightnessMirrorListeners.valueAt(i)).onBrightnessMirrorReinflated(this.mBrightnessMirror);
            ++i;
        }
    }
    
    @Override
    public void addCallback(final BrightnessMirrorListener obj) {
        Objects.requireNonNull(obj);
        this.mBrightnessMirrorListeners.add((Object)obj);
    }
    
    public View getMirror() {
        return this.mBrightnessMirror;
    }
    
    public void hideMirror() {
        this.mVisibilityCallback.accept(Boolean.FALSE);
        this.mNotificationPanel.setPanelAlpha(255, true);
        this.mDepthController.setBrightnessMirrorVisible(false);
    }
    
    public void onDensityOrFontScaleChanged() {
        this.reinflate();
    }
    
    public void onOverlayChanged() {
        this.reinflate();
    }
    
    public void onUiModeChanged() {
        this.reinflate();
    }
    
    @Override
    public void removeCallback(final BrightnessMirrorListener brightnessMirrorListener) {
        this.mBrightnessMirrorListeners.remove((Object)brightnessMirrorListener);
    }
    
    public void setLocation(final View view) {
        view.getLocationInWindow(this.mInt2Cache);
        final int n = this.mInt2Cache[0];
        final int n2 = view.getWidth() / 2;
        final int n3 = this.mInt2Cache[1];
        final int n4 = view.getHeight() / 2;
        this.mBrightnessMirror.setTranslationX(0.0f);
        this.mBrightnessMirror.setTranslationY(0.0f);
        this.mBrightnessMirror.getLocationInWindow(this.mInt2Cache);
        final int n5 = this.mInt2Cache[0];
        final int n6 = this.mBrightnessMirror.getWidth() / 2;
        final int n7 = this.mInt2Cache[1];
        final int n8 = this.mBrightnessMirror.getHeight() / 2;
        this.mBrightnessMirror.setTranslationX((float)(n + n2 - (n5 + n6)));
        this.mBrightnessMirror.setTranslationY((float)(n3 + n4 - (n7 + n8)));
    }
    
    public void showMirror() {
        this.mBrightnessMirror.setVisibility(0);
        this.mVisibilityCallback.accept(Boolean.TRUE);
        this.mNotificationPanel.setPanelAlpha(0, true);
        this.mDepthController.setBrightnessMirrorVisible(true);
    }
    
    public void updateResources() {
        final FrameLayout$LayoutParams layoutParams = (FrameLayout$LayoutParams)this.mBrightnessMirror.getLayoutParams();
        final Resources resources = this.mBrightnessMirror.getResources();
        layoutParams.width = resources.getDimensionPixelSize(R$dimen.qs_panel_width);
        layoutParams.height = resources.getDimensionPixelSize(R$dimen.brightness_mirror_height);
        layoutParams.gravity = resources.getInteger(R$integer.notification_panel_layout_gravity);
        this.mBrightnessMirror.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
    }
    
    public interface BrightnessMirrorListener
    {
        void onBrightnessMirrorReinflated(final View p0);
    }
}
