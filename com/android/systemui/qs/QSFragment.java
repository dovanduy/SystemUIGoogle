// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.view.View$OnClickListener;
import com.android.systemui.statusbar.phone.NotificationsQuickSettingsContainer;
import com.android.systemui.R$id;
import android.view.MotionEvent;
import com.android.systemui.R$layout;
import android.content.Context;
import android.view.ContextThemeWrapper;
import com.android.systemui.R$style;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.content.res.Configuration;
import android.widget.FrameLayout$LayoutParams;
import android.view.View;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.view.ViewTreeObserver$OnPreDrawListener;
import com.android.systemui.statusbar.policy.RemoteInputQuickSettingsDisabler;
import android.graphics.Rect;
import com.android.systemui.qs.customize.QSCustomizer;
import com.android.systemui.util.InjectionInflationController;
import android.animation.Animator$AnimatorListener;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.plugins.qs.QS;
import com.android.systemui.util.LifecycleFragment;

public class QSFragment extends LifecycleFragment implements QS, Callbacks, StateListener
{
    private final Animator$AnimatorListener mAnimateHeaderSlidingInListener;
    private QSContainerImpl mContainer;
    private long mDelay;
    private QSFooter mFooter;
    protected QuickStatusBarHeader mHeader;
    private boolean mHeaderAnimating;
    private final QSTileHost mHost;
    private final InjectionInflationController mInjectionInflater;
    private boolean mLastKeyguardAndExpanded;
    private float mLastQSExpansion;
    private int mLayoutDirection;
    private boolean mListening;
    private HeightListener mPanelView;
    private QSAnimator mQSAnimator;
    private QSContainerImplController mQSContainerImplController;
    private final QSContainerImplController.Builder mQSContainerImplControllerBuilder;
    private QSCustomizer mQSCustomizer;
    private QSDetail mQSDetail;
    protected QSPanel mQSPanel;
    private final Rect mQsBounds;
    private boolean mQsDisabled;
    private boolean mQsExpanded;
    private final RemoteInputQuickSettingsDisabler mRemoteInputQuickSettingsDisabler;
    private boolean mShowCollapsedOnKeyguard;
    private boolean mStackScrollerOverscrolling;
    private final ViewTreeObserver$OnPreDrawListener mStartHeaderSlidingIn;
    private int mState;
    private final StatusBarStateController mStatusBarStateController;
    
    public QSFragment(final RemoteInputQuickSettingsDisabler mRemoteInputQuickSettingsDisabler, final InjectionInflationController mInjectionInflater, final QSTileHost mHost, final StatusBarStateController mStatusBarStateController, final CommandQueue commandQueue, final QSContainerImplController.Builder mqsContainerImplControllerBuilder) {
        this.mQsBounds = new Rect();
        this.mLastQSExpansion = -1.0f;
        this.mStartHeaderSlidingIn = (ViewTreeObserver$OnPreDrawListener)new ViewTreeObserver$OnPreDrawListener() {
            public boolean onPreDraw() {
                QSFragment.this.getView().getViewTreeObserver().removeOnPreDrawListener((ViewTreeObserver$OnPreDrawListener)this);
                QSFragment.this.getView().animate().translationY(0.0f).setStartDelay(QSFragment.this.mDelay).setDuration(448L).setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN).setListener(QSFragment.this.mAnimateHeaderSlidingInListener).start();
                return true;
            }
        };
        this.mAnimateHeaderSlidingInListener = (Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                QSFragment.this.mHeaderAnimating = false;
                QSFragment.this.updateQsState();
            }
        };
        this.mRemoteInputQuickSettingsDisabler = mRemoteInputQuickSettingsDisabler;
        this.mInjectionInflater = mInjectionInflater;
        this.mQSContainerImplControllerBuilder = mqsContainerImplControllerBuilder;
        commandQueue.observe(this.getLifecycle(), (CommandQueue.Callbacks)this);
        this.mHost = mHost;
        this.mStatusBarStateController = mStatusBarStateController;
    }
    
    private boolean headerWillBeAnimating() {
        final int mState = this.mState;
        boolean b = true;
        if (mState != 1 || !this.mShowCollapsedOnKeyguard || this.isKeyguardShowing()) {
            b = false;
        }
        return b;
    }
    
    private boolean isKeyguardShowing() {
        final int state = this.mStatusBarStateController.getState();
        boolean b = true;
        if (state != 1) {
            b = false;
        }
        return b;
    }
    
    private void setEditLocation(final View view) {
        final View viewById = view.findViewById(16908291);
        final int[] locationOnScreen = viewById.getLocationOnScreen();
        this.mQSCustomizer.setEditLocation(locationOnScreen[0] + viewById.getWidth() / 2, locationOnScreen[1] + viewById.getHeight() / 2);
    }
    
    private void setKeyguardShowing(final boolean b) {
        this.mLastQSExpansion = -1.0f;
        final QSAnimator mqsAnimator = this.mQSAnimator;
        if (mqsAnimator != null) {
            mqsAnimator.setOnKeyguard(b);
        }
        this.mFooter.setKeyguardShowing(b);
        this.updateQsState();
    }
    
    private void updateQsState() {
        final boolean mQsExpanded = this.mQsExpanded;
        final boolean b = true;
        final int n = 0;
        final boolean b2 = mQsExpanded || this.mStackScrollerOverscrolling || this.mHeaderAnimating;
        this.mQSPanel.setExpanded(this.mQsExpanded);
        this.mQSDetail.setExpanded(this.mQsExpanded);
        final boolean keyguardShowing = this.isKeyguardShowing();
        final QuickStatusBarHeader mHeader = this.mHeader;
        int visibility;
        if (!this.mQsExpanded && keyguardShowing && !this.mHeaderAnimating && !this.mShowCollapsedOnKeyguard) {
            visibility = 4;
        }
        else {
            visibility = 0;
        }
        mHeader.setVisibility(visibility);
        this.mHeader.setExpanded((keyguardShowing && !this.mHeaderAnimating && !this.mShowCollapsedOnKeyguard) || (this.mQsExpanded && !this.mStackScrollerOverscrolling));
        final QSFooter mFooter = this.mFooter;
        int visibility2;
        if (!this.mQsDisabled && (this.mQsExpanded || !keyguardShowing || this.mHeaderAnimating || this.mShowCollapsedOnKeyguard)) {
            visibility2 = 0;
        }
        else {
            visibility2 = 4;
        }
        mFooter.setVisibility(visibility2);
        final QSFooter mFooter2 = this.mFooter;
        boolean expanded = false;
        Label_0275: {
            if (keyguardShowing && !this.mHeaderAnimating) {
                expanded = b;
                if (!this.mShowCollapsedOnKeyguard) {
                    break Label_0275;
                }
            }
            expanded = (this.mQsExpanded && !this.mStackScrollerOverscrolling && b);
        }
        mFooter2.setExpanded(expanded);
        final QSPanel mqsPanel = this.mQSPanel;
        int visibility3;
        if (!this.mQsDisabled && b2) {
            visibility3 = n;
        }
        else {
            visibility3 = 4;
        }
        mqsPanel.setVisibility(visibility3);
    }
    
    @Override
    public void animateHeaderSlidingIn(final long mDelay) {
        if (!this.mQsExpanded && this.getView().getTranslationY() != 0.0f) {
            this.mHeaderAnimating = true;
            this.mDelay = mDelay;
            this.getView().getViewTreeObserver().addOnPreDrawListener(this.mStartHeaderSlidingIn);
        }
    }
    
    @Override
    public void animateHeaderSlidingOut() {
        if (this.getView().getY() == -this.mHeader.getHeight()) {
            return;
        }
        this.mHeaderAnimating = true;
        this.getView().animate().y((float)(-this.mHeader.getHeight())).setStartDelay(0L).setDuration(360L).setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN).setListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                if (QSFragment.this.getView() != null) {
                    QSFragment.this.getView().animate().setListener((Animator$AnimatorListener)null);
                }
                QSFragment.this.mHeaderAnimating = false;
                QSFragment.this.updateQsState();
            }
        }).start();
    }
    
    @Override
    public void closeDetail() {
        this.mQSPanel.closeDetail();
    }
    
    @Override
    public void disable(int adjustDisableFlags, final int n, final int n2, final boolean b) {
        if (adjustDisableFlags != this.getContext().getDisplayId()) {
            return;
        }
        adjustDisableFlags = this.mRemoteInputQuickSettingsDisabler.adjustDisableFlags(n2);
        final boolean mQsDisabled = (adjustDisableFlags & 0x1) != 0x0;
        if (mQsDisabled == this.mQsDisabled) {
            return;
        }
        this.mQsDisabled = mQsDisabled;
        this.mContainer.disable(n, adjustDisableFlags, b);
        this.mHeader.disable(n, adjustDisableFlags, b);
        this.mFooter.disable(n, adjustDisableFlags, b);
        this.updateQsState();
    }
    
    @Override
    public int getDesiredHeight() {
        if (this.mQSCustomizer.isCustomizing()) {
            return this.getView().getHeight();
        }
        if (this.mQSDetail.isClosingDetail()) {
            final FrameLayout$LayoutParams frameLayout$LayoutParams = (FrameLayout$LayoutParams)this.mQSPanel.getLayoutParams();
            return frameLayout$LayoutParams.topMargin + frameLayout$LayoutParams.bottomMargin + this.mQSPanel.getMeasuredHeight() + this.getView().getPaddingBottom();
        }
        return this.getView().getMeasuredHeight();
    }
    
    @Override
    public View getHeader() {
        return (View)this.mHeader;
    }
    
    @Override
    public int getQsMinExpansionHeight() {
        return this.mHeader.getHeight();
    }
    
    public QSPanel getQsPanel() {
        return this.mQSPanel;
    }
    
    @Override
    public void hideImmediately() {
        this.getView().animate().cancel();
        this.getView().setY((float)(-this.mHeader.getHeight()));
    }
    
    @Override
    public boolean isCustomizing() {
        return this.mQSCustomizer.isCustomizing();
    }
    
    boolean isExpanded() {
        return this.mQsExpanded;
    }
    
    boolean isListening() {
        return this.mListening;
    }
    
    @Override
    public boolean isShowingDetail() {
        return this.mQSPanel.isShowingCustomize() || this.mQSDetail.isShowingDetail();
    }
    
    @Override
    public void notifyCustomizeChanged() {
        this.mContainer.updateExpansion();
        final QSPanel mqsPanel = this.mQSPanel;
        final boolean customizing = this.mQSCustomizer.isCustomizing();
        final int n = 0;
        int visibility;
        if (!customizing) {
            visibility = 0;
        }
        else {
            visibility = 4;
        }
        mqsPanel.setVisibility(visibility);
        final QSFooter mFooter = this.mFooter;
        int visibility2;
        if (!this.mQSCustomizer.isCustomizing()) {
            visibility2 = n;
        }
        else {
            visibility2 = 4;
        }
        mFooter.setVisibility(visibility2);
        this.mPanelView.onQsHeightChanged();
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.setEditLocation(this.getView());
        if (configuration.getLayoutDirection() != this.mLayoutDirection) {
            this.mLayoutDirection = configuration.getLayoutDirection();
            final QSAnimator mqsAnimator = this.mQSAnimator;
            if (mqsAnimator != null) {
                mqsAnimator.onRtlChanged();
            }
        }
    }
    
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        return this.mInjectionInflater.injectable(layoutInflater.cloneInContext((Context)new ContextThemeWrapper(this.getContext(), R$style.qs_theme))).inflate(R$layout.qs_panel, viewGroup, false);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mStatusBarStateController.removeCallback((StatusBarStateController.StateListener)this);
        if (this.mListening) {
            this.setListening(false);
        }
    }
    
    @Override
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        return this.isCustomizing();
    }
    
    public void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("expanded", this.mQsExpanded);
        bundle.putBoolean("listening", this.mListening);
        this.mQSCustomizer.saveInstanceState(bundle);
        if (this.mQsExpanded) {
            this.mQSPanel.getTileLayout().saveInstanceState(bundle);
        }
    }
    
    @Override
    public void onStateChanged(final int mState) {
        this.mState = mState;
        boolean keyguardShowing = true;
        if (mState != 1) {
            keyguardShowing = false;
        }
        this.setKeyguardShowing(keyguardShowing);
    }
    
    public void onViewCreated(final View editLocation, final Bundle bundle) {
        super.onViewCreated(editLocation, bundle);
        this.mQSPanel = (QSPanel)editLocation.findViewById(R$id.quick_settings_panel);
        this.mQSDetail = (QSDetail)editLocation.findViewById(R$id.qs_detail);
        this.mHeader = (QuickStatusBarHeader)editLocation.findViewById(R$id.header);
        this.mFooter = (QSFooter)editLocation.findViewById(R$id.qs_footer);
        this.mContainer = (QSContainerImpl)editLocation.findViewById(R$id.quick_settings_container);
        final QSContainerImplController.Builder mqsContainerImplControllerBuilder = this.mQSContainerImplControllerBuilder;
        mqsContainerImplControllerBuilder.setQSContainerImpl((QSContainerImpl)editLocation);
        this.mQSContainerImplController = mqsContainerImplControllerBuilder.build();
        this.mQSDetail.setQsPanel(this.mQSPanel, this.mHeader, (View)this.mFooter);
        this.mQSAnimator = new QSAnimator(this, (QuickQSPanel)this.mHeader.findViewById(R$id.quick_qs_panel), this.mQSPanel);
        (this.mQSCustomizer = (QSCustomizer)editLocation.findViewById(R$id.qs_customize)).setQs(this);
        if (bundle != null) {
            this.setExpanded(bundle.getBoolean("expanded"));
            this.setListening(bundle.getBoolean("listening"));
            this.setEditLocation(editLocation);
            this.mQSCustomizer.restoreInstanceState(bundle);
            if (this.mQsExpanded) {
                this.mQSPanel.getTileLayout().restoreInstanceState(bundle);
            }
        }
        this.setHost(this.mHost);
        this.mStatusBarStateController.addCallback((StatusBarStateController.StateListener)this);
        this.onStateChanged(this.mStatusBarStateController.getState());
    }
    
    @Override
    public void setContainer(final ViewGroup viewGroup) {
        if (viewGroup instanceof NotificationsQuickSettingsContainer) {
            this.mQSCustomizer.setContainer((NotificationsQuickSettingsContainer)viewGroup);
        }
    }
    
    @Override
    public void setExpandClickListener(final View$OnClickListener expandClickListener) {
        this.mFooter.setExpandClickListener(expandClickListener);
    }
    
    @Override
    public void setExpanded(final boolean mQsExpanded) {
        this.mQsExpanded = mQsExpanded;
        this.mQSPanel.setListening(this.mListening, mQsExpanded);
        this.updateQsState();
    }
    
    @Override
    public void setHasNotifications(final boolean b) {
    }
    
    @Override
    public void setHeaderClickable(final boolean b) {
    }
    
    @Override
    public void setHeaderListening(final boolean b) {
        this.mHeader.setListening(b);
        this.mFooter.setListening(b);
    }
    
    @Override
    public void setHeightOverride(final int heightOverride) {
        this.mContainer.setHeightOverride(heightOverride);
    }
    
    public void setHost(final QSTileHost qsTileHost) {
        this.mQSPanel.setHost(qsTileHost, this.mQSCustomizer);
        this.mHeader.setQSPanel(this.mQSPanel);
        this.mFooter.setQSPanel(this.mQSPanel);
        this.mQSDetail.setHost(qsTileHost);
        final QSAnimator mqsAnimator = this.mQSAnimator;
        if (mqsAnimator != null) {
            mqsAnimator.setHost(qsTileHost);
        }
    }
    
    @Override
    public void setListening(final boolean b) {
        this.mListening = b;
        this.mQSContainerImplController.setListening(b);
        this.mHeader.setListening(b);
        this.mFooter.setListening(b);
        this.mQSPanel.setListening(this.mListening, this.mQsExpanded);
    }
    
    @Override
    public void setOverscrolling(final boolean mStackScrollerOverscrolling) {
        this.mStackScrollerOverscrolling = mStackScrollerOverscrolling;
        this.updateQsState();
    }
    
    @Override
    public void setPanelView(final HeightListener mPanelView) {
        this.mPanelView = mPanelView;
    }
    
    @Override
    public void setQsExpansion(final float position, float n) {
        this.mContainer.setExpansion(position);
        final float n2 = 1.0f;
        final float n3 = position - 1.0f;
        final boolean keyguardShowing = this.isKeyguardShowing();
        boolean fullyExpanded = true;
        final boolean mLastKeyguardAndExpanded = keyguardShowing && !this.mShowCollapsedOnKeyguard;
        if (!this.mHeaderAnimating && !this.headerWillBeAnimating()) {
            final View view = this.getView();
            if (mLastKeyguardAndExpanded) {
                n = this.mHeader.getHeight() * n3;
            }
            view.setTranslationY(n);
        }
        if (position == this.mLastQSExpansion && this.mLastKeyguardAndExpanded == mLastKeyguardAndExpanded) {
            return;
        }
        this.mLastQSExpansion = position;
        this.mLastKeyguardAndExpanded = mLastKeyguardAndExpanded;
        if (position != 1.0f) {
            fullyExpanded = false;
        }
        final float translationY = n3 * (this.mQSPanel.getBottom() - this.mHeader.getBottom() + this.mHeader.getPaddingBottom() + this.mFooter.getHeight());
        this.mHeader.setExpansion(mLastKeyguardAndExpanded, position, translationY);
        final QSFooter mFooter = this.mFooter;
        if (mLastKeyguardAndExpanded) {
            n = n2;
        }
        else {
            n = position;
        }
        mFooter.setExpansion(n);
        this.mQSPanel.getQsTileRevealController().setExpansion(position);
        this.mQSPanel.getTileLayout().setExpansion(position);
        this.mQSPanel.setTranslationY(translationY);
        this.mQSDetail.setFullyExpanded(fullyExpanded);
        if (fullyExpanded) {
            this.mQSPanel.setClipBounds((Rect)null);
        }
        else {
            this.mQsBounds.top = (int)(-this.mQSPanel.getTranslationY());
            this.mQsBounds.right = this.mQSPanel.getWidth();
            this.mQsBounds.bottom = this.mQSPanel.getHeight();
            this.mQSPanel.setClipBounds(this.mQsBounds);
        }
        final QSAnimator mqsAnimator = this.mQSAnimator;
        if (mqsAnimator != null) {
            mqsAnimator.setPosition(position);
        }
    }
    
    @Override
    public void setShowCollapsedOnKeyguard(final boolean b) {
        if (b != this.mShowCollapsedOnKeyguard) {
            this.mShowCollapsedOnKeyguard = b;
            this.updateQsState();
            final QSAnimator mqsAnimator = this.mQSAnimator;
            if (mqsAnimator != null) {
                mqsAnimator.setShowCollapsedOnKeyguard(b);
            }
            if (!b && this.isKeyguardShowing()) {
                this.setQsExpansion(this.mLastQSExpansion, 0.0f);
            }
        }
    }
}
