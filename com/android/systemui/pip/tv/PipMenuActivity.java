// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip.tv;

import android.app.RemoteAction;
import java.util.List;
import java.util.Collections;
import android.content.Intent;
import android.content.pm.ParceledListSlice;
import android.content.Context;
import android.animation.AnimatorInflater;
import com.android.systemui.R$anim;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import android.os.Bundle;
import com.android.systemui.pip.tv.dagger.TvPipComponent;
import android.animation.Animator;
import android.app.Activity;

public class PipMenuActivity extends Activity implements Listener
{
    private Animator mFadeInAnimation;
    private Animator mFadeOutAnimation;
    private final TvPipComponent.Builder mPipComponentBuilder;
    private PipControlsViewController mPipControlsViewController;
    private final PipManager mPipManager;
    private boolean mRestorePipSizeWhenClose;
    private TvPipComponent mTvPipComponent;
    
    public PipMenuActivity(final TvPipComponent.Builder mPipComponentBuilder, final PipManager mPipManager) {
        this.mPipComponentBuilder = mPipComponentBuilder;
        this.mPipManager = mPipManager;
    }
    
    private void restorePipAndFinish() {
        if (this.mRestorePipSizeWhenClose) {
            this.mPipManager.resizePinnedStack(1);
        }
        this.finish();
    }
    
    public void finish() {
        super.finish();
    }
    
    public void onBackPressed() {
        this.restorePipAndFinish();
    }
    
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        if (!this.mPipManager.isPipShown()) {
            this.finish();
        }
        this.setContentView(R$layout.tv_pip_menu);
        final TvPipComponent build = this.mPipComponentBuilder.pipControlsView((PipControlsView)this.findViewById(R$id.pip_controls)).build();
        this.mTvPipComponent = build;
        this.mPipControlsViewController = build.getPipControlsViewController();
        this.mPipManager.addListener((PipManager.Listener)this);
        this.mRestorePipSizeWhenClose = true;
        (this.mFadeInAnimation = AnimatorInflater.loadAnimator((Context)this, R$anim.tv_pip_menu_fade_in_animation)).setTarget((Object)this.mPipControlsViewController.getView());
        (this.mFadeOutAnimation = AnimatorInflater.loadAnimator((Context)this, R$anim.tv_pip_menu_fade_out_animation)).setTarget((Object)this.mPipControlsViewController.getView());
        this.onPipMenuActionsChanged((ParceledListSlice)this.getIntent().getParcelableExtra("custom_actions"));
    }
    
    protected void onDestroy() {
        super.onDestroy();
        this.mPipManager.removeListener((PipManager.Listener)this);
        this.mPipManager.resumePipResizing(1);
    }
    
    public void onMoveToFullscreen() {
        this.mRestorePipSizeWhenClose = false;
        this.finish();
    }
    
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        this.onPipMenuActionsChanged((ParceledListSlice)this.getIntent().getParcelableExtra("custom_actions"));
    }
    
    public void onPause() {
        super.onPause();
        this.mFadeOutAnimation.start();
        this.restorePipAndFinish();
    }
    
    public void onPipActivityClosed() {
        this.finish();
    }
    
    public void onPipEntered() {
    }
    
    public void onPipMenuActionsChanged(final ParceledListSlice parceledListSlice) {
        final boolean b = parceledListSlice != null && !parceledListSlice.getList().isEmpty();
        final PipControlsViewController mPipControlsViewController = this.mPipControlsViewController;
        List actions;
        if (b) {
            actions = parceledListSlice.getList();
        }
        else {
            actions = Collections.EMPTY_LIST;
        }
        mPipControlsViewController.setActions(actions);
    }
    
    public void onPipResizeAboutToStart() {
        this.finish();
        this.mPipManager.suspendPipResizing(1);
    }
    
    public void onResume() {
        super.onResume();
        this.mFadeInAnimation.start();
    }
    
    public void onShowPipMenu() {
    }
}
