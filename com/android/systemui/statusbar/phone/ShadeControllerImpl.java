// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import java.util.Collection;
import android.view.ViewTreeObserver$OnGlobalLayoutListener;
import android.view.View;
import android.util.Log;
import com.android.systemui.statusbar.NotificationPresenter;
import android.view.WindowManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import java.util.ArrayList;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.assist.AssistManager;
import dagger.Lazy;

public class ShadeControllerImpl implements ShadeController
{
    private final Lazy<AssistManager> mAssistManagerLazy;
    private final Lazy<BubbleController> mBubbleControllerLazy;
    private final CommandQueue mCommandQueue;
    private final int mDisplayId;
    protected final NotificationShadeWindowController mNotificationShadeWindowController;
    private final ArrayList<Runnable> mPostCollapseRunnables;
    private final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    protected final Lazy<StatusBar> mStatusBarLazy;
    private final StatusBarStateController mStatusBarStateController;
    
    public ShadeControllerImpl(final CommandQueue mCommandQueue, final StatusBarStateController mStatusBarStateController, final NotificationShadeWindowController mNotificationShadeWindowController, final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager, final WindowManager windowManager, final Lazy<StatusBar> mStatusBarLazy, final Lazy<AssistManager> mAssistManagerLazy, final Lazy<BubbleController> mBubbleControllerLazy) {
        this.mPostCollapseRunnables = new ArrayList<Runnable>();
        this.mCommandQueue = mCommandQueue;
        this.mStatusBarStateController = mStatusBarStateController;
        this.mNotificationShadeWindowController = mNotificationShadeWindowController;
        this.mStatusBarKeyguardViewManager = mStatusBarKeyguardViewManager;
        this.mDisplayId = windowManager.getDefaultDisplay().getDisplayId();
        this.mStatusBarLazy = mStatusBarLazy;
        this.mAssistManagerLazy = mAssistManagerLazy;
        this.mBubbleControllerLazy = mBubbleControllerLazy;
    }
    
    private NotificationPanelViewController getNotificationPanelViewController() {
        return this.getStatusBar().getPanelController();
    }
    
    private NotificationPresenter getPresenter() {
        return this.getStatusBar().getPresenter();
    }
    
    private StatusBar getStatusBar() {
        return this.mStatusBarLazy.get();
    }
    
    @Override
    public void addPostCollapseAction(final Runnable e) {
        this.mPostCollapseRunnables.add(e);
    }
    
    @Override
    public void animateCollapsePanels() {
        this.animateCollapsePanels(0);
    }
    
    @Override
    public void animateCollapsePanels(final int n) {
        this.animateCollapsePanels(n, false, false, 1.0f);
    }
    
    @Override
    public void animateCollapsePanels(final int n, final boolean b) {
        this.animateCollapsePanels(n, b, false, 1.0f);
    }
    
    @Override
    public void animateCollapsePanels(final int n, final boolean b, final boolean b2) {
        this.animateCollapsePanels(n, b, b2, 1.0f);
    }
    
    @Override
    public void animateCollapsePanels(final int n, final boolean b, final boolean b2, final float n2) {
        if (!b && this.mStatusBarStateController.getState() != 0) {
            this.runPostCollapseRunnables();
            return;
        }
        if ((n & 0x2) == 0x0) {
            this.getStatusBar().postHideRecentApps();
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("NotificationShadeWindow: ");
        sb.append(this.getNotificationShadeWindowView());
        sb.append(" canPanelBeCollapsed(): ");
        sb.append(this.getNotificationPanelViewController().canPanelBeCollapsed());
        Log.v("ShadeControllerImpl", sb.toString());
        if (this.getNotificationShadeWindowView() != null && this.getNotificationPanelViewController().canPanelBeCollapsed()) {
            this.mNotificationShadeWindowController.setNotificationShadeFocusable(false);
            this.getStatusBar().getNotificationShadeWindowViewController().cancelExpandHelper();
            this.getStatusBarView().collapsePanel(true, b2, n2);
        }
        else {
            this.mBubbleControllerLazy.get().collapseStack();
        }
    }
    
    @Override
    public boolean closeShadeIfOpen() {
        if (!this.getNotificationPanelViewController().isFullyCollapsed()) {
            this.mCommandQueue.animateCollapsePanels(2, true);
            this.getStatusBar().visibilityChanged(false);
            this.mAssistManagerLazy.get().hideAssist();
        }
        return false;
    }
    
    @Override
    public void collapsePanel(final boolean b) {
        if (b) {
            if (!this.collapsePanel()) {
                this.runPostCollapseRunnables();
            }
        }
        else if (!this.getPresenter().isPresenterFullyCollapsed()) {
            this.getStatusBar().instantCollapseNotificationPanel();
            this.getStatusBar().visibilityChanged(false);
        }
        else {
            this.runPostCollapseRunnables();
        }
    }
    
    @Override
    public boolean collapsePanel() {
        if (!this.getNotificationPanelViewController().isFullyCollapsed()) {
            this.animateCollapsePanels(2, true, true);
            this.getStatusBar().visibilityChanged(false);
            return true;
        }
        return false;
    }
    
    protected NotificationShadeWindowView getNotificationShadeWindowView() {
        return this.getStatusBar().getNotificationShadeWindowView();
    }
    
    protected PhoneStatusBarView getStatusBarView() {
        return (PhoneStatusBarView)this.getStatusBar().getStatusBarView();
    }
    
    @Override
    public void goToLockedShade(final View view) {
        this.getStatusBar().goToLockedShade(view);
    }
    
    @Override
    public void instantExpandNotificationsPanel() {
        this.getStatusBar().makeExpandedVisible(true);
        this.getNotificationPanelViewController().expand(false);
        this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, false);
    }
    
    @Override
    public void postOnShadeExpanded(final Runnable runnable) {
        this.getNotificationPanelViewController().addOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)new ViewTreeObserver$OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if (ShadeControllerImpl.this.getStatusBar().getNotificationShadeWindowView().isVisibleToUser()) {
                    ShadeControllerImpl.this.getNotificationPanelViewController().removeOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)this);
                    ShadeControllerImpl.this.getNotificationPanelViewController().getView().post(runnable);
                }
            }
        });
    }
    
    @Override
    public void runPostCollapseRunnables() {
        final ArrayList<Runnable> list = new ArrayList<Runnable>(this.mPostCollapseRunnables);
        this.mPostCollapseRunnables.clear();
        for (int size = list.size(), i = 0; i < size; ++i) {
            list.get(i).run();
        }
        this.mStatusBarKeyguardViewManager.readyForKeyguardDone();
    }
}
