// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.view.MotionEvent;
import com.android.systemui.Gefingerpoken;
import android.view.View$OnTouchListener;
import android.view.View;
import java.util.Objects;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.phone.DoubleTapHelper;
import android.view.accessibility.AccessibilityManager;

public class ActivatableNotificationViewController
{
    private final AccessibilityManager mAccessibilityManager;
    private DoubleTapHelper mDoubleTapHelper;
    private final ExpandableOutlineViewController mExpandableOutlineViewController;
    private final FalsingManager mFalsingManager;
    private boolean mNeedsDimming;
    private TouchHandler mTouchHandler;
    private final ActivatableNotificationView mView;
    
    public ActivatableNotificationViewController(final ActivatableNotificationView mView, final ExpandableOutlineViewController mExpandableOutlineViewController, final AccessibilityManager mAccessibilityManager, final FalsingManager mFalsingManager) {
        this.mTouchHandler = new TouchHandler();
        this.mView = mView;
        this.mExpandableOutlineViewController = mExpandableOutlineViewController;
        this.mAccessibilityManager = mAccessibilityManager;
        this.mFalsingManager = mFalsingManager;
        mView.setOnActivatedListener((ActivatableNotificationView.OnActivatedListener)new ActivatableNotificationView.OnActivatedListener() {
            @Override
            public void onActivated(final ActivatableNotificationView activatableNotificationView) {
                ActivatableNotificationViewController.this.mFalsingManager.onNotificationActive();
            }
            
            @Override
            public void onActivationReset(final ActivatableNotificationView activatableNotificationView) {
            }
        });
    }
    
    public void init() {
        this.mExpandableOutlineViewController.init();
        final ActivatableNotificationView mView = this.mView;
        final _$$Lambda$ActivatableNotificationViewController$r3sTjOy8fdG9h_zptjU2waOJUhM $$Lambda$ActivatableNotificationViewController$r3sTjOy8fdG9h_zptjU2waOJUhM = new _$$Lambda$ActivatableNotificationViewController$r3sTjOy8fdG9h_zptjU2waOJUhM(this);
        final ActivatableNotificationView mView2 = this.mView;
        Objects.requireNonNull(mView2);
        final _$$Lambda$zPp39wwhGRQfVR8DLyh9H_uzUTY $$Lambda$zPp39wwhGRQfVR8DLyh9H_uzUTY = new _$$Lambda$zPp39wwhGRQfVR8DLyh9H_uzUTY(mView2);
        final ActivatableNotificationView mView3 = this.mView;
        Objects.requireNonNull(mView3);
        final _$$Lambda$ELE_e_9GisA3PeCbD7mpobFwmaM $$Lambda$ELE_e_9GisA3PeCbD7mpobFwmaM = new _$$Lambda$ELE_e_9GisA3PeCbD7mpobFwmaM(mView3);
        final FalsingManager mFalsingManager = this.mFalsingManager;
        Objects.requireNonNull(mFalsingManager);
        this.mDoubleTapHelper = new DoubleTapHelper((View)mView, (DoubleTapHelper.ActivationListener)$$Lambda$ActivatableNotificationViewController$r3sTjOy8fdG9h_zptjU2waOJUhM, (DoubleTapHelper.DoubleTapListener)$$Lambda$zPp39wwhGRQfVR8DLyh9H_uzUTY, (DoubleTapHelper.SlideBackListener)$$Lambda$ELE_e_9GisA3PeCbD7mpobFwmaM, (DoubleTapHelper.DoubleTapLogListener)new _$$Lambda$PkPBcaaRR8KHImTlnKW995Xmvx8(mFalsingManager));
        this.mView.setOnTouchListener((View$OnTouchListener)this.mTouchHandler);
        this.mView.setTouchHandler(this.mTouchHandler);
        this.mView.setOnDimmedListener((ActivatableNotificationView.OnDimmedListener)new _$$Lambda$ActivatableNotificationViewController$tnb8yJViiBqHZ1MPl8MWWadMlQ4(this));
        this.mView.setAccessibilityManager(this.mAccessibilityManager);
    }
    
    class TouchHandler implements Gefingerpoken, View$OnTouchListener
    {
        private boolean mBlockNextTouch;
        
        @Override
        public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
            if (ActivatableNotificationViewController.this.mNeedsDimming && motionEvent.getActionMasked() == 0 && ActivatableNotificationViewController.this.mView.disallowSingleClick(motionEvent) && !ActivatableNotificationViewController.this.mAccessibilityManager.isTouchExplorationEnabled()) {
                if (!ActivatableNotificationViewController.this.mView.isActive()) {
                    return true;
                }
                if (!ActivatableNotificationViewController.this.mDoubleTapHelper.isWithinDoubleTapSlop(motionEvent)) {
                    this.mBlockNextTouch = true;
                    ActivatableNotificationViewController.this.mView.makeInactive(true);
                    return true;
                }
            }
            return false;
        }
        
        public boolean onTouch(final View view, final MotionEvent motionEvent) {
            if (this.mBlockNextTouch) {
                this.mBlockNextTouch = false;
                return true;
            }
            return ActivatableNotificationViewController.this.mNeedsDimming && !ActivatableNotificationViewController.this.mAccessibilityManager.isTouchExplorationEnabled() && ActivatableNotificationViewController.this.mView.isInteractive() && (!ActivatableNotificationViewController.this.mNeedsDimming || ActivatableNotificationViewController.this.mView.isDimmed()) && ActivatableNotificationViewController.this.mDoubleTapHelper.onTouchEvent(motionEvent, ActivatableNotificationViewController.this.mView.getActualHeight());
        }
    }
}
