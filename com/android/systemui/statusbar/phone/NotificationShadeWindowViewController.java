// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.view.ViewGroup$OnHierarchyChangeListener;
import android.media.session.MediaSessionLegacyHelper;
import android.view.KeyEvent;
import android.view.GestureDetector$OnGestureListener;
import android.view.GestureDetector$SimpleOnGestureListener;
import com.android.internal.annotations.VisibleForTesting;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.view.MotionEvent;
import android.os.SystemClock;
import android.hardware.display.AmbientDisplayConfiguration;
import com.android.systemui.R$id;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.doze.DozeLog;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.PulseExpansionHandler;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.util.InjectionInflationController;
import com.android.systemui.tuner.TunerService;
import android.graphics.RectF;
import com.android.systemui.statusbar.SuperStatusBarViewFactory;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import android.view.GestureDetector;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.DragDownHelper;
import com.android.systemui.dock.DockManager;
import com.android.systemui.statusbar.NotificationShadeDepthController;
import android.view.View;

public class NotificationShadeWindowViewController
{
    private PhoneStatusBarTransitions mBarTransitions;
    private View mBrightnessMirror;
    private final NotificationShadeDepthController mDepthController;
    private final DockManager mDockManager;
    private boolean mDoubleTapEnabled;
    private DragDownHelper mDragDownHelper;
    private boolean mExpandAnimationPending;
    private boolean mExpandAnimationRunning;
    private boolean mExpandingBelowNotch;
    private final FalsingManager mFalsingManager;
    private GestureDetector mGestureDetector;
    private boolean mIsTrackingBarGesture;
    private final NotificationPanelViewController mNotificationPanelViewController;
    private StatusBar mService;
    private boolean mSingleTapEnabled;
    private NotificationStackScrollLayout mStackScrollLayout;
    private final SysuiStatusBarStateController mStatusBarStateController;
    private PhoneStatusBarView mStatusBarView;
    private final SuperStatusBarViewFactory mStatusBarViewFactory;
    private int[] mTempLocation;
    private RectF mTempRect;
    private boolean mTouchActive;
    private boolean mTouchCancelled;
    private final TunerService mTunerService;
    private final NotificationShadeWindowView mView;
    
    public NotificationShadeWindowViewController(final InjectionInflationController injectionInflationController, final NotificationWakeUpCoordinator notificationWakeUpCoordinator, final PulseExpansionHandler pulseExpansionHandler, final DynamicPrivacyController dynamicPrivacyController, final KeyguardBypassController keyguardBypassController, final FalsingManager mFalsingManager, final PluginManager pluginManager, final TunerService mTunerService, final NotificationLockscreenUserManager notificationLockscreenUserManager, final NotificationEntryManager notificationEntryManager, final KeyguardStateController keyguardStateController, final SysuiStatusBarStateController mStatusBarStateController, final DozeLog dozeLog, final DozeParameters dozeParameters, final CommandQueue commandQueue, final ShadeController shadeController, final DockManager mDockManager, final NotificationShadeDepthController mDepthController, final NotificationShadeWindowView mView, final NotificationPanelViewController mNotificationPanelViewController, final SuperStatusBarViewFactory mStatusBarViewFactory) {
        this.mTempLocation = new int[2];
        this.mTempRect = new RectF();
        this.mIsTrackingBarGesture = false;
        this.mFalsingManager = mFalsingManager;
        this.mTunerService = mTunerService;
        this.mStatusBarStateController = mStatusBarStateController;
        this.mView = mView;
        this.mDockManager = mDockManager;
        this.mNotificationPanelViewController = mNotificationPanelViewController;
        this.mDepthController = mDepthController;
        this.mStatusBarViewFactory = mStatusBarViewFactory;
        this.mBrightnessMirror = mView.findViewById(R$id.brightness_mirror);
    }
    
    private boolean isIntersecting(final View view, final float n, final float n2) {
        final int[] locationOnScreen = view.getLocationOnScreen();
        this.mTempLocation = locationOnScreen;
        this.mTempRect.set((float)locationOnScreen[0], (float)locationOnScreen[1], (float)(locationOnScreen[0] + view.getWidth()), (float)(this.mTempLocation[1] + view.getHeight()));
        return this.mTempRect.contains(n, n2);
    }
    
    public void cancelCurrentTouch() {
        if (this.mTouchActive) {
            final long uptimeMillis = SystemClock.uptimeMillis();
            final MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
            obtain.setSource(4098);
            this.mView.dispatchTouchEvent(obtain);
            obtain.recycle();
            this.mTouchCancelled = true;
        }
    }
    
    public void cancelExpandHelper() {
        final NotificationStackScrollLayout mStackScrollLayout = this.mStackScrollLayout;
        if (mStackScrollLayout != null) {
            mStackScrollLayout.cancelExpandHelper();
        }
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.print("  mExpandAnimationPending=");
        printWriter.println(this.mExpandAnimationPending);
        printWriter.print("  mExpandAnimationRunning=");
        printWriter.println(this.mExpandAnimationRunning);
        printWriter.print("  mTouchCancelled=");
        printWriter.println(this.mTouchCancelled);
        printWriter.print("  mTouchActive=");
        printWriter.println(this.mTouchActive);
    }
    
    public PhoneStatusBarTransitions getBarTransitions() {
        return this.mBarTransitions;
    }
    
    public NotificationShadeWindowView getView() {
        return this.mView;
    }
    
    @VisibleForTesting
    void setDragDownHelper(final DragDownHelper mDragDownHelper) {
        this.mDragDownHelper = mDragDownHelper;
    }
    
    public void setExpandAnimationPending(final boolean mExpandAnimationPending) {
        this.mExpandAnimationPending = mExpandAnimationPending;
    }
    
    public void setExpandAnimationRunning(final boolean mExpandAnimationRunning) {
        this.mExpandAnimationRunning = mExpandAnimationRunning;
    }
    
    public void setService(final StatusBar mService) {
        this.mService = mService;
    }
    
    public void setStatusBarView(final PhoneStatusBarView mStatusBarView) {
        this.mStatusBarView = mStatusBarView;
        if (mStatusBarView != null) {
            final SuperStatusBarViewFactory mStatusBarViewFactory = this.mStatusBarViewFactory;
            if (mStatusBarViewFactory != null) {
                this.mBarTransitions = new PhoneStatusBarTransitions(mStatusBarView, mStatusBarViewFactory.getStatusBarWindowView().findViewById(R$id.status_bar_container));
            }
        }
    }
    
    public void setTouchActive(final boolean mTouchActive) {
        this.mTouchActive = mTouchActive;
    }
    
    public void setupExpandedStatusBar() {
        this.mStackScrollLayout = (NotificationStackScrollLayout)this.mView.findViewById(R$id.notification_stack_scroller);
        this.mTunerService.addTunable((TunerService.Tunable)new _$$Lambda$NotificationShadeWindowViewController$Glv88_5_kOTqMlucxR_8golqdvI(this), "doze_pulse_on_double_tap", "doze_tap_gesture");
        this.mGestureDetector = new GestureDetector(this.mView.getContext(), (GestureDetector$OnGestureListener)new GestureDetector$SimpleOnGestureListener() {
            public boolean onDoubleTap(final MotionEvent motionEvent) {
                if (!NotificationShadeWindowViewController.this.mDoubleTapEnabled && !NotificationShadeWindowViewController.this.mSingleTapEnabled) {
                    return false;
                }
                NotificationShadeWindowViewController.this.mService.wakeUpIfDozing(SystemClock.uptimeMillis(), (View)NotificationShadeWindowViewController.this.mView, "DOUBLE_TAP");
                return true;
            }
            
            public boolean onSingleTapConfirmed(final MotionEvent motionEvent) {
                if (NotificationShadeWindowViewController.this.mSingleTapEnabled && !NotificationShadeWindowViewController.this.mDockManager.isDocked()) {
                    NotificationShadeWindowViewController.this.mService.wakeUpIfDozing(SystemClock.uptimeMillis(), (View)NotificationShadeWindowViewController.this.mView, "SINGLE_TAP");
                    return true;
                }
                return false;
            }
        });
        this.mView.setInteractionEventHandler((NotificationShadeWindowView.InteractionEventHandler)new NotificationShadeWindowView.InteractionEventHandler() {
            @Override
            public void didIntercept(MotionEvent obtain) {
                obtain = MotionEvent.obtain(obtain);
                obtain.setAction(3);
                NotificationShadeWindowViewController.this.mStackScrollLayout.onInterceptTouchEvent(obtain);
                NotificationShadeWindowViewController.this.mNotificationPanelViewController.getView().onInterceptTouchEvent(obtain);
                obtain.recycle();
            }
            
            @Override
            public void didNotHandleTouchEvent(final MotionEvent motionEvent) {
                final int actionMasked = motionEvent.getActionMasked();
                if (actionMasked == 1 || actionMasked == 3) {
                    NotificationShadeWindowViewController.this.mService.setInteracting(1, false);
                }
            }
            
            @Override
            public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
                final boolean b = keyEvent.getAction() == 0;
                final int keyCode = keyEvent.getKeyCode();
                if (keyCode != 4) {
                    if (keyCode != 62) {
                        if (keyCode != 82) {
                            if (keyCode == 24 || keyCode == 25) {
                                if (NotificationShadeWindowViewController.this.mStatusBarStateController.isDozing()) {
                                    MediaSessionLegacyHelper.getHelper(NotificationShadeWindowViewController.this.mView.getContext()).sendVolumeKeyEvent(keyEvent, Integer.MIN_VALUE, true);
                                    return true;
                                }
                            }
                        }
                        else if (!b) {
                            return NotificationShadeWindowViewController.this.mService.onMenuPressed();
                        }
                    }
                    else if (!b) {
                        return NotificationShadeWindowViewController.this.mService.onSpacePressed();
                    }
                    return false;
                }
                if (!b) {
                    NotificationShadeWindowViewController.this.mService.onBackPressed();
                }
                return true;
            }
            
            @Override
            public Boolean handleDispatchTouchEvent(final MotionEvent motionEvent) {
                final Boolean false = Boolean.FALSE;
                final boolean b = motionEvent.getActionMasked() == 0;
                final boolean b2 = motionEvent.getActionMasked() == 1;
                final boolean b3 = motionEvent.getActionMasked() == 3;
                final boolean access$500 = NotificationShadeWindowViewController.this.mExpandingBelowNotch;
                if (b2 || b3) {
                    NotificationShadeWindowViewController.this.mExpandingBelowNotch = false;
                }
                if (!b3 && NotificationShadeWindowViewController.this.mService.shouldIgnoreTouch()) {
                    return false;
                }
                if (b && NotificationShadeWindowViewController.this.mNotificationPanelViewController.isFullyCollapsed()) {
                    NotificationShadeWindowViewController.this.mNotificationPanelViewController.startExpandLatencyTracking();
                }
                if (b) {
                    NotificationShadeWindowViewController.this.setTouchActive(true);
                    NotificationShadeWindowViewController.this.mTouchCancelled = false;
                }
                else if (motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 3) {
                    NotificationShadeWindowViewController.this.setTouchActive(false);
                }
                if (NotificationShadeWindowViewController.this.mTouchCancelled || NotificationShadeWindowViewController.this.mExpandAnimationRunning || NotificationShadeWindowViewController.this.mExpandAnimationPending) {
                    return false;
                }
                NotificationShadeWindowViewController.this.mFalsingManager.onTouchEvent(motionEvent, NotificationShadeWindowViewController.this.mView.getWidth(), NotificationShadeWindowViewController.this.mView.getHeight());
                NotificationShadeWindowViewController.this.mGestureDetector.onTouchEvent(motionEvent);
                if (NotificationShadeWindowViewController.this.mBrightnessMirror != null && NotificationShadeWindowViewController.this.mBrightnessMirror.getVisibility() == 0 && motionEvent.getActionMasked() == 5) {
                    return false;
                }
                if (b) {
                    NotificationShadeWindowViewController.this.mStackScrollLayout.closeControlsIfOutsideTouch(motionEvent);
                }
                if (NotificationShadeWindowViewController.this.mStatusBarStateController.isDozing()) {
                    NotificationShadeWindowViewController.this.mService.mDozeScrimController.extendPulse();
                }
                boolean b4 = access$500;
                if (b) {
                    b4 = access$500;
                    if (motionEvent.getY() >= NotificationShadeWindowViewController.this.mView.getBottom()) {
                        NotificationShadeWindowViewController.this.mExpandingBelowNotch = true;
                        b4 = true;
                    }
                }
                if (b4) {
                    return NotificationShadeWindowViewController.this.mStatusBarView.dispatchTouchEvent(motionEvent);
                }
                if (!NotificationShadeWindowViewController.this.mIsTrackingBarGesture && b && NotificationShadeWindowViewController.this.mNotificationPanelViewController.isFullyCollapsed()) {
                    final float rawX = motionEvent.getRawX();
                    final float rawY = motionEvent.getRawY();
                    final NotificationShadeWindowViewController this$0 = NotificationShadeWindowViewController.this;
                    if (this$0.isIntersecting((View)this$0.mStatusBarView, rawX, rawY)) {
                        if (NotificationShadeWindowViewController.this.mService.isSameStatusBarState(0)) {
                            NotificationShadeWindowViewController.this.mIsTrackingBarGesture = true;
                            return NotificationShadeWindowViewController.this.mStatusBarView.dispatchTouchEvent(motionEvent);
                        }
                        return Boolean.TRUE;
                    }
                }
                else if (NotificationShadeWindowViewController.this.mIsTrackingBarGesture) {
                    final boolean dispatchTouchEvent = NotificationShadeWindowViewController.this.mStatusBarView.dispatchTouchEvent(motionEvent);
                    if (b2 || b3) {
                        NotificationShadeWindowViewController.this.mIsTrackingBarGesture = false;
                    }
                    return dispatchTouchEvent;
                }
                return null;
            }
            
            @Override
            public boolean handleTouchEvent(final MotionEvent motionEvent) {
                boolean onTouchEvent = NotificationShadeWindowViewController.this.mStatusBarStateController.isDozing() && (NotificationShadeWindowViewController.this.mService.isPulsing() ^ true);
                if ((NotificationShadeWindowViewController.this.mDragDownHelper.isDragDownEnabled() && !onTouchEvent) || NotificationShadeWindowViewController.this.mDragDownHelper.isDraggingDown()) {
                    onTouchEvent = NotificationShadeWindowViewController.this.mDragDownHelper.onTouchEvent(motionEvent);
                }
                return onTouchEvent;
            }
            
            @Override
            public boolean interceptMediaKey(final KeyEvent keyEvent) {
                return NotificationShadeWindowViewController.this.mService.interceptMediaKey(keyEvent);
            }
            
            @Override
            public boolean shouldInterceptTouchEvent(final MotionEvent motionEvent) {
                if (NotificationShadeWindowViewController.this.mStatusBarStateController.isDozing() && !NotificationShadeWindowViewController.this.mService.isPulsing() && !NotificationShadeWindowViewController.this.mDockManager.isDocked()) {
                    return true;
                }
                boolean onInterceptTouchEvent;
                final boolean b = onInterceptTouchEvent = false;
                if (NotificationShadeWindowViewController.this.mNotificationPanelViewController.isFullyExpanded()) {
                    onInterceptTouchEvent = b;
                    if (NotificationShadeWindowViewController.this.mDragDownHelper.isDragDownEnabled()) {
                        onInterceptTouchEvent = b;
                        if (!NotificationShadeWindowViewController.this.mService.isBouncerShowing()) {
                            onInterceptTouchEvent = b;
                            if (!NotificationShadeWindowViewController.this.mStatusBarStateController.isDozing()) {
                                onInterceptTouchEvent = NotificationShadeWindowViewController.this.mDragDownHelper.onInterceptTouchEvent(motionEvent);
                            }
                        }
                    }
                }
                return onInterceptTouchEvent;
            }
        });
        this.mView.setOnHierarchyChangeListener((ViewGroup$OnHierarchyChangeListener)new ViewGroup$OnHierarchyChangeListener() {
            public void onChildViewAdded(final View view, final View view2) {
                if (view2.getId() == R$id.brightness_mirror) {
                    NotificationShadeWindowViewController.this.mBrightnessMirror = view2;
                }
            }
            
            public void onChildViewRemoved(final View view, final View view2) {
            }
        });
        this.setDragDownHelper(new DragDownHelper(this.mView.getContext(), (View)this.mView, this.mStackScrollLayout.getExpandHelperCallback(), this.mStackScrollLayout.getDragDownCallback(), this.mFalsingManager));
        this.mDepthController.setRoot((View)this.mView);
        this.mNotificationPanelViewController.addExpansionListener(this.mDepthController);
    }
}
