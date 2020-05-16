// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.stack;

import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import android.animation.Animator;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.os.Handler;
import android.graphics.Rect;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import android.view.MotionEvent;
import com.android.systemui.plugins.FalsingManager;
import android.content.Context;
import android.view.View;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper;
import com.android.systemui.SwipeHelper;

class NotificationSwipeHelper extends SwipeHelper implements NotificationSwipeActionHelper
{
    @VisibleForTesting
    protected static final long COVER_MENU_DELAY = 4000L;
    private final NotificationCallback mCallback;
    private NotificationMenuRowPlugin mCurrMenuRow;
    private final Runnable mFalsingCheck;
    private boolean mIsExpanded;
    private View mMenuExposedView;
    private final NotificationMenuRowPlugin.OnMenuEventListener mMenuListener;
    private boolean mPulsing;
    private View mTranslatingParentView;
    
    NotificationSwipeHelper(final int n, final NotificationCallback mCallback, final Context context, final NotificationMenuRowPlugin.OnMenuEventListener mMenuListener, final FalsingManager falsingManager) {
        super(n, (Callback)mCallback, context, falsingManager);
        this.mMenuListener = mMenuListener;
        this.mCallback = mCallback;
        this.mFalsingCheck = new _$$Lambda$NotificationSwipeHelper$C9LXWH0up2brEDre5OaSo4akO38(this);
    }
    
    private void handleSwipeFromClosedState(final MotionEvent motionEvent, final View view, final float a, final NotificationMenuRowPlugin notificationMenuRowPlugin) {
        final boolean dismissGesture = this.isDismissGesture(motionEvent);
        final boolean towardsMenu = notificationMenuRowPlugin.isTowardsMenu(a);
        final float escapeVelocity = this.getEscapeVelocity();
        final float abs = Math.abs(a);
        final int n = 1;
        final boolean b = escapeVelocity <= abs;
        final double n2 = (double)(motionEvent.getEventTime() - motionEvent.getDownTime());
        final boolean b2 = !notificationMenuRowPlugin.canBeDismissed() && n2 >= 200.0;
        final boolean b3 = towardsMenu && !dismissGesture;
        final boolean b4 = !b || b2;
        final boolean b5 = this.swipedEnoughToShowMenu(notificationMenuRowPlugin) && b4;
        final boolean b6 = b && !towardsMenu && !dismissGesture;
        final boolean b7 = notificationMenuRowPlugin.shouldShowGutsOnSnapOpen() || (this.mIsExpanded && !this.mPulsing);
        final boolean b8 = b5 || (b6 && b7);
        final int menuSnapTarget = notificationMenuRowPlugin.getMenuSnapTarget();
        int n3;
        if (!this.isFalseGesture(motionEvent) && b8) {
            n3 = n;
        }
        else {
            n3 = 0;
        }
        if ((b3 || n3 != 0) && menuSnapTarget != 0) {
            this.snapOpen(view, menuSnapTarget, a);
            notificationMenuRowPlugin.onSnapOpen();
        }
        else if (this.isDismissGesture(motionEvent) && !towardsMenu) {
            this.dismiss(view, a);
            notificationMenuRowPlugin.onDismiss();
        }
        else {
            this.snapClosed(view, a);
            notificationMenuRowPlugin.onSnapClosed();
        }
    }
    
    private void handleSwipeFromOpenState(final MotionEvent motionEvent, final View view, final float n, final NotificationMenuRowPlugin notificationMenuRowPlugin) {
        final boolean dismissGesture = this.isDismissGesture(motionEvent);
        if (notificationMenuRowPlugin.isWithinSnapMenuThreshold() && !dismissGesture) {
            notificationMenuRowPlugin.onSnapOpen();
            this.snapOpen(view, notificationMenuRowPlugin.getMenuSnapTarget(), n);
        }
        else if (dismissGesture && !notificationMenuRowPlugin.shouldSnapBack()) {
            this.dismiss(view, n);
            notificationMenuRowPlugin.onDismiss();
        }
        else {
            this.snapClosed(view, n);
            notificationMenuRowPlugin.onSnapClosed();
        }
    }
    
    public static boolean isTouchInView(final MotionEvent motionEvent, final View view) {
        if (view == null) {
            return false;
        }
        int n;
        if (view instanceof ExpandableView) {
            n = ((ExpandableView)view).getActualHeight();
        }
        else {
            n = view.getHeight();
        }
        final int n2 = (int)motionEvent.getRawX();
        final int n3 = (int)motionEvent.getRawY();
        final int[] array = new int[2];
        view.getLocationOnScreen(array);
        final int n4 = array[0];
        final int n5 = array[1];
        return new Rect(n4, n5, view.getWidth() + n4, n + n5).contains(n2, n3);
    }
    
    private boolean swipedEnoughToShowMenu(final NotificationMenuRowPlugin notificationMenuRowPlugin) {
        return !this.swipedFarEnough() && notificationMenuRowPlugin.isSwipedEnoughToShowMenu();
    }
    
    public void clearCurrentMenuRow() {
        this.setCurrentMenuRow(null);
    }
    
    public void clearExposedMenuView() {
        this.setExposedMenuView(null);
    }
    
    public void clearTranslatingParentView() {
        this.setTranslatingParentView(null);
    }
    
    @Override
    public void dismiss(final View view, final float n) {
        this.dismissChild(view, n, this.swipedFastEnough() ^ true);
    }
    
    @Override
    public void dismissChild(final View view, final float n, final boolean b) {
        this.superDismissChild(view, n, b);
        if (this.mCallback.shouldDismissQuickly()) {
            this.mCallback.handleChildViewDismissed(view);
        }
        this.mCallback.onDismiss();
        this.handleMenuCoveredOrDismissed();
    }
    
    public NotificationMenuRowPlugin getCurrentMenuRow() {
        return this.mCurrMenuRow;
    }
    
    @VisibleForTesting
    @Override
    protected float getEscapeVelocity() {
        return super.getEscapeVelocity();
    }
    
    public View getExposedMenuView() {
        return this.mMenuExposedView;
    }
    
    @VisibleForTesting
    protected Runnable getFalsingCheck() {
        return this.mFalsingCheck;
    }
    
    @VisibleForTesting
    protected Handler getHandler() {
        return super.mHandler;
    }
    
    @Override
    public float getMinDismissVelocity() {
        return this.getEscapeVelocity();
    }
    
    public View getTranslatingParentView() {
        return this.mTranslatingParentView;
    }
    
    public float getTranslation(final View view) {
        if (view instanceof SwipeableView) {
            return ((SwipeableView)view).getTranslation();
        }
        return 0.0f;
    }
    
    public Animator getViewTranslationAnimator(final View view, final float n, final ValueAnimator$AnimatorUpdateListener valueAnimator$AnimatorUpdateListener) {
        if (view instanceof ExpandableNotificationRow) {
            return ((ExpandableNotificationRow)view).getTranslateViewAnimator(n, valueAnimator$AnimatorUpdateListener);
        }
        return this.superGetViewTranslationAnimator(view, n, valueAnimator$AnimatorUpdateListener);
    }
    
    @VisibleForTesting
    protected void handleMenuCoveredOrDismissed() {
        final View exposedMenuView = this.getExposedMenuView();
        if (exposedMenuView != null && exposedMenuView == this.mTranslatingParentView) {
            this.clearExposedMenuView();
        }
    }
    
    @VisibleForTesting
    protected void handleMenuRowSwipe(final MotionEvent motionEvent, final View view, final float n, final NotificationMenuRowPlugin notificationMenuRowPlugin) {
        if (!notificationMenuRowPlugin.shouldShowMenu()) {
            if (this.isDismissGesture(motionEvent)) {
                this.dismiss(view, n);
            }
            else {
                this.snapClosed(view, n);
                notificationMenuRowPlugin.onSnapClosed();
            }
            return;
        }
        if (notificationMenuRowPlugin.isSnappedAndOnSameSide()) {
            this.handleSwipeFromOpenState(motionEvent, view, n, notificationMenuRowPlugin);
        }
        else {
            this.handleSwipeFromClosedState(motionEvent, view, n, notificationMenuRowPlugin);
        }
    }
    
    public boolean handleUpEvent(final MotionEvent motionEvent, final View view, final float n, final float n2) {
        final NotificationMenuRowPlugin currentMenuRow = this.getCurrentMenuRow();
        if (currentMenuRow != null) {
            currentMenuRow.onTouchEnd();
            this.handleMenuRowSwipe(motionEvent, view, n, currentMenuRow);
            return true;
        }
        return false;
    }
    
    @VisibleForTesting
    protected void initializeRow(final SwipeableView swipeableView) {
        if (swipeableView.hasFinishedInitialization()) {
            final NotificationMenuRowPlugin menu = swipeableView.createMenu();
            if ((this.mCurrMenuRow = menu) != null) {
                menu.setMenuClickListener(this.mMenuListener);
                this.mCurrMenuRow.onTouchStart();
            }
        }
    }
    
    @Override
    protected void onChildSnappedBack(final View view, final float n) {
        final NotificationMenuRowPlugin mCurrMenuRow = this.mCurrMenuRow;
        if (mCurrMenuRow != null && n == 0.0f) {
            mCurrMenuRow.resetMenu();
            this.clearCurrentMenuRow();
        }
    }
    
    @Override
    public void onDownUpdate(final View mTranslatingParentView, final MotionEvent motionEvent) {
        this.mTranslatingParentView = mTranslatingParentView;
        final NotificationMenuRowPlugin currentMenuRow = this.getCurrentMenuRow();
        if (currentMenuRow != null) {
            currentMenuRow.onTouchStart();
        }
        this.clearCurrentMenuRow();
        this.getHandler().removeCallbacks(this.getFalsingCheck());
        this.resetExposedMenuView(true, false);
        if (mTranslatingParentView instanceof SwipeableView) {
            this.initializeRow((SwipeableView)mTranslatingParentView);
        }
    }
    
    public void onMenuShown(final View view) {
        this.setExposedMenuView(this.getTranslatingParentView());
        ((Callback)this.mCallback).onDragCancelled(view);
        final Handler handler = this.getHandler();
        if (((Callback)this.mCallback).isAntiFalsingNeeded()) {
            handler.removeCallbacks(this.getFalsingCheck());
            handler.postDelayed(this.getFalsingCheck(), 4000L);
        }
    }
    
    public void onMoveUpdate(final View view, final MotionEvent motionEvent, final float n, final float n2) {
        this.getHandler().removeCallbacks(this.getFalsingCheck());
        final NotificationMenuRowPlugin currentMenuRow = this.getCurrentMenuRow();
        if (currentMenuRow != null) {
            currentMenuRow.onTouchMove(n2);
        }
    }
    
    public void resetExposedMenuView(final boolean b, final boolean b2) {
        if (!this.shouldResetMenu(b2)) {
            return;
        }
        final View exposedMenuView = this.getExposedMenuView();
        if (b) {
            final Animator viewTranslationAnimator = this.getViewTranslationAnimator(exposedMenuView, 0.0f, null);
            if (viewTranslationAnimator != null) {
                viewTranslationAnimator.start();
            }
        }
        else if (exposedMenuView instanceof SwipeableView) {
            final SwipeableView swipeableView = (SwipeableView)exposedMenuView;
            if (!swipeableView.isRemoved()) {
                swipeableView.resetTranslation();
            }
        }
        this.clearExposedMenuView();
    }
    
    public void setCurrentMenuRow(final NotificationMenuRowPlugin mCurrMenuRow) {
        this.mCurrMenuRow = mCurrMenuRow;
    }
    
    public void setExposedMenuView(final View mMenuExposedView) {
        this.mMenuExposedView = mMenuExposedView;
    }
    
    public void setIsExpanded(final boolean mIsExpanded) {
        this.mIsExpanded = mIsExpanded;
    }
    
    public void setPulsing(final boolean mPulsing) {
        this.mPulsing = mPulsing;
    }
    
    @VisibleForTesting
    protected void setTranslatingParentView(final View mTranslatingParentView) {
        this.mTranslatingParentView = mTranslatingParentView;
    }
    
    public void setTranslation(final View view, final float translation) {
        if (view instanceof SwipeableView) {
            ((SwipeableView)view).setTranslation(translation);
        }
    }
    
    @VisibleForTesting
    protected boolean shouldResetMenu(final boolean b) {
        final View mMenuExposedView = this.mMenuExposedView;
        return mMenuExposedView != null && (b || mMenuExposedView != this.mTranslatingParentView);
    }
    
    @Override
    public void snapChild(final View view, final float n, final float n2) {
        this.superSnapChild(view, n, n2);
        ((Callback)this.mCallback).onDragCancelled(view);
        if (n == 0.0f) {
            this.handleMenuCoveredOrDismissed();
        }
    }
    
    @VisibleForTesting
    protected void snapClosed(final View view, final float n) {
        this.snapChild(view, 0.0f, n);
    }
    
    @Override
    public void snapOpen(final View view, final int n, final float n2) {
        this.snapChild(view, (float)n, n2);
    }
    
    @Override
    public void snooze(final StatusBarNotification statusBarNotification, final int n) {
        this.mCallback.onSnooze(statusBarNotification, n);
    }
    
    @Override
    public void snooze(final StatusBarNotification statusBarNotification, final SnoozeOption snoozeOption) {
        this.mCallback.onSnooze(statusBarNotification, snoozeOption);
    }
    
    @VisibleForTesting
    protected void superDismissChild(final View view, final float n, final boolean b) {
        super.dismissChild(view, n, b);
    }
    
    @VisibleForTesting
    protected Animator superGetViewTranslationAnimator(final View view, final float n, final ValueAnimator$AnimatorUpdateListener valueAnimator$AnimatorUpdateListener) {
        return super.getViewTranslationAnimator(view, n, valueAnimator$AnimatorUpdateListener);
    }
    
    @VisibleForTesting
    protected void superSnapChild(final View view, final float n, final float n2) {
        super.snapChild(view, n, n2);
    }
    
    @VisibleForTesting
    @Override
    protected boolean swipedFarEnough() {
        return super.swipedFarEnough();
    }
    
    @Override
    public boolean swipedFarEnough(final float n, final float n2) {
        return this.swipedFarEnough();
    }
    
    @VisibleForTesting
    @Override
    protected boolean swipedFastEnough() {
        return super.swipedFastEnough();
    }
    
    @Override
    public boolean swipedFastEnough(final float n, final float n2) {
        return this.swipedFastEnough();
    }
    
    public interface NotificationCallback extends Callback
    {
        void handleChildViewDismissed(final View p0);
        
        void onDismiss();
        
        void onSnooze(final StatusBarNotification p0, final int p1);
        
        void onSnooze(final StatusBarNotification p0, final SnoozeOption p1);
        
        boolean shouldDismissQuickly();
    }
}
