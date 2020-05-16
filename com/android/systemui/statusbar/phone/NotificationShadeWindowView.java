// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.content.res.TypedArray;
import com.android.systemui.R$styleable;
import android.graphics.Rect;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.Canvas;
import android.view.DisplayCutout;
import android.graphics.Insets;
import android.view.WindowInsets$Type;
import android.view.WindowInsets;
import com.android.systemui.R$id;
import android.widget.FrameLayout$LayoutParams;
import android.view.ActionMode$Callback;
import com.android.internal.view.FloatingActionMode;
import android.view.ActionMode$Callback2;
import android.view.SurfaceHolder$Callback2;
import android.view.InputQueue$Callback;
import android.view.MotionEvent;
import android.net.Uri;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.content.res.Configuration;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.WindowInsetsController;
import android.view.ViewGroup$LayoutParams;
import android.util.AttributeSet;
import android.content.Context;
import android.view.ViewTreeObserver$OnPreDrawListener;
import com.android.internal.widget.FloatingToolbar;
import android.view.View;
import android.view.ActionMode;
import android.view.Window;
import android.widget.FrameLayout;

public class NotificationShadeWindowView extends FrameLayout
{
    private Window mFakeWindow;
    private ActionMode mFloatingActionMode;
    private View mFloatingActionModeOriginatingView;
    private FloatingToolbar mFloatingToolbar;
    private ViewTreeObserver$OnPreDrawListener mFloatingToolbarPreDrawListener;
    private InteractionEventHandler mInteractionEventHandler;
    private int mLeftInset;
    private int mRightInset;
    
    public NotificationShadeWindowView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mRightInset = 0;
        this.mLeftInset = 0;
        this.mFakeWindow = new Window(super.mContext) {
            public void addContentView(final View view, final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            }
            
            public void alwaysReadCloseOnTouchAttr() {
            }
            
            public void clearContentView() {
            }
            
            public void closeAllPanels() {
            }
            
            public void closePanel(final int n) {
            }
            
            public View getCurrentFocus() {
                return null;
            }
            
            public View getDecorView() {
                return (View)NotificationShadeWindowView.this;
            }
            
            public WindowInsetsController getInsetsController() {
                return null;
            }
            
            public LayoutInflater getLayoutInflater() {
                return null;
            }
            
            public int getNavigationBarColor() {
                return 0;
            }
            
            public int getStatusBarColor() {
                return 0;
            }
            
            public int getVolumeControlStream() {
                return 0;
            }
            
            public void invalidatePanelMenu(final int n) {
            }
            
            public boolean isFloating() {
                return false;
            }
            
            public boolean isShortcutKey(final int n, final KeyEvent keyEvent) {
                return false;
            }
            
            protected void onActive() {
            }
            
            public void onConfigurationChanged(final Configuration configuration) {
            }
            
            public void onMultiWindowModeChanged() {
            }
            
            public void onPictureInPictureModeChanged(final boolean b) {
            }
            
            public void openPanel(final int n, final KeyEvent keyEvent) {
            }
            
            public View peekDecorView() {
                return null;
            }
            
            public boolean performContextMenuIdentifierAction(final int n, final int n2) {
                return false;
            }
            
            public boolean performPanelIdentifierAction(final int n, final int n2, final int n3) {
                return false;
            }
            
            public boolean performPanelShortcut(final int n, final int n2, final KeyEvent keyEvent, final int n3) {
                return false;
            }
            
            public void reportActivityRelaunched() {
            }
            
            public void restoreHierarchyState(final Bundle bundle) {
            }
            
            public Bundle saveHierarchyState() {
                return null;
            }
            
            public void setBackgroundDrawable(final Drawable drawable) {
            }
            
            public void setChildDrawable(final int n, final Drawable drawable) {
            }
            
            public void setChildInt(final int n, final int n2) {
            }
            
            public void setContentView(final int n) {
            }
            
            public void setContentView(final View view) {
            }
            
            public void setContentView(final View view, final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            }
            
            public void setDecorCaptionShade(final int n) {
            }
            
            public void setFeatureDrawable(final int n, final Drawable drawable) {
            }
            
            public void setFeatureDrawableAlpha(final int n, final int n2) {
            }
            
            public void setFeatureDrawableResource(final int n, final int n2) {
            }
            
            public void setFeatureDrawableUri(final int n, final Uri uri) {
            }
            
            public void setFeatureInt(final int n, final int n2) {
            }
            
            public void setNavigationBarColor(final int n) {
            }
            
            public void setResizingCaptionDrawable(final Drawable drawable) {
            }
            
            public void setStatusBarColor(final int n) {
            }
            
            public void setTitle(final CharSequence charSequence) {
            }
            
            public void setTitleColor(final int n) {
            }
            
            public void setVolumeControlStream(final int n) {
            }
            
            public boolean superDispatchGenericMotionEvent(final MotionEvent motionEvent) {
                return false;
            }
            
            public boolean superDispatchKeyEvent(final KeyEvent keyEvent) {
                return false;
            }
            
            public boolean superDispatchKeyShortcutEvent(final KeyEvent keyEvent) {
                return false;
            }
            
            public boolean superDispatchTouchEvent(final MotionEvent motionEvent) {
                return false;
            }
            
            public boolean superDispatchTrackballEvent(final MotionEvent motionEvent) {
                return false;
            }
            
            public void takeInputQueue(final InputQueue$Callback inputQueue$Callback) {
            }
            
            public void takeKeyEvents(final boolean b) {
            }
            
            public void takeSurface(final SurfaceHolder$Callback2 surfaceHolder$Callback2) {
            }
            
            public void togglePanel(final int n, final KeyEvent keyEvent) {
            }
        };
        this.setMotionEventSplittingEnabled(false);
    }
    
    private void applyMargins() {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child.getLayoutParams() instanceof LayoutParams) {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                if (!layoutParams.ignoreRightInset && (layoutParams.rightMargin != this.mRightInset || layoutParams.leftMargin != this.mLeftInset)) {
                    layoutParams.rightMargin = this.mRightInset;
                    layoutParams.leftMargin = this.mLeftInset;
                    child.requestLayout();
                }
            }
        }
    }
    
    private void cleanupFloatingActionModeViews() {
        final FloatingToolbar mFloatingToolbar = this.mFloatingToolbar;
        if (mFloatingToolbar != null) {
            mFloatingToolbar.dismiss();
            this.mFloatingToolbar = null;
        }
        final View mFloatingActionModeOriginatingView = this.mFloatingActionModeOriginatingView;
        if (mFloatingActionModeOriginatingView != null) {
            if (this.mFloatingToolbarPreDrawListener != null) {
                mFloatingActionModeOriginatingView.getViewTreeObserver().removeOnPreDrawListener(this.mFloatingToolbarPreDrawListener);
                this.mFloatingToolbarPreDrawListener = null;
            }
            this.mFloatingActionModeOriginatingView = null;
        }
    }
    
    private ActionMode createFloatingActionMode(final View mFloatingActionModeOriginatingView, final ActionMode$Callback2 actionMode$Callback2) {
        final ActionMode mFloatingActionMode = this.mFloatingActionMode;
        if (mFloatingActionMode != null) {
            mFloatingActionMode.finish();
        }
        this.cleanupFloatingActionModeViews();
        this.mFloatingToolbar = new FloatingToolbar(this.mFakeWindow);
        final FloatingActionMode floatingActionMode = new FloatingActionMode(super.mContext, actionMode$Callback2, mFloatingActionModeOriginatingView, this.mFloatingToolbar);
        this.mFloatingActionModeOriginatingView = mFloatingActionModeOriginatingView;
        this.mFloatingToolbarPreDrawListener = (ViewTreeObserver$OnPreDrawListener)new ViewTreeObserver$OnPreDrawListener(this) {
            public boolean onPreDraw() {
                floatingActionMode.updateViewLocationInWindow();
                return true;
            }
        };
        return (ActionMode)floatingActionMode;
    }
    
    private void setHandledFloatingActionMode(final ActionMode mFloatingActionMode) {
        (this.mFloatingActionMode = mFloatingActionMode).invalidate();
        this.mFloatingActionModeOriginatingView.getViewTreeObserver().addOnPreDrawListener(this.mFloatingToolbarPreDrawListener);
    }
    
    private ActionMode startActionMode(final View view, final ActionMode$Callback actionMode$Callback, final int n) {
        final ActionModeCallback2Wrapper actionModeCallback2Wrapper = new ActionModeCallback2Wrapper(actionMode$Callback);
        ActionMode floatingActionMode = this.createFloatingActionMode(view, actionModeCallback2Wrapper);
        if (floatingActionMode != null && actionModeCallback2Wrapper.onCreateActionMode(floatingActionMode, floatingActionMode.getMenu())) {
            this.setHandledFloatingActionMode(floatingActionMode);
        }
        else {
            floatingActionMode = null;
        }
        return floatingActionMode;
    }
    
    public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
        return this.mInteractionEventHandler.interceptMediaKey(keyEvent) || super.dispatchKeyEvent(keyEvent) || this.mInteractionEventHandler.dispatchKeyEvent(keyEvent);
    }
    
    public boolean dispatchTouchEvent(final MotionEvent motionEvent) {
        final Boolean handleDispatchTouchEvent = this.mInteractionEventHandler.handleDispatchTouchEvent(motionEvent);
        boolean b;
        if (handleDispatchTouchEvent != null) {
            b = handleDispatchTouchEvent;
        }
        else {
            b = super.dispatchTouchEvent(motionEvent);
        }
        return b;
    }
    
    protected FrameLayout$LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-1, -1);
    }
    
    public FrameLayout$LayoutParams generateLayoutParams(final AttributeSet set) {
        return new LayoutParams(this.getContext(), set);
    }
    
    public NotificationPanelView getNotificationPanelView() {
        return (NotificationPanelView)this.findViewById(R$id.notification_panel);
    }
    
    public WindowInsets onApplyWindowInsets(final WindowInsets windowInsets) {
        final Insets insetsIgnoringVisibility = windowInsets.getInsetsIgnoringVisibility(WindowInsets$Type.systemBars());
        final boolean fitsSystemWindows = this.getFitsSystemWindows();
        final boolean b = true;
        final boolean b2 = true;
        if (fitsSystemWindows) {
            int n = b2 ? 1 : 0;
            if (insetsIgnoringVisibility.top == this.getPaddingTop()) {
                if (insetsIgnoringVisibility.bottom != this.getPaddingBottom()) {
                    n = (b2 ? 1 : 0);
                }
                else {
                    n = 0;
                }
            }
            if (n != 0) {
                this.setPadding(0, 0, 0, 0);
            }
        }
        else {
            int n2 = b ? 1 : 0;
            if (this.getPaddingLeft() == 0) {
                n2 = (b ? 1 : 0);
                if (this.getPaddingRight() == 0) {
                    n2 = (b ? 1 : 0);
                    if (this.getPaddingTop() == 0) {
                        if (this.getPaddingBottom() != 0) {
                            n2 = (b ? 1 : 0);
                        }
                        else {
                            n2 = 0;
                        }
                    }
                }
            }
            if (n2 != 0) {
                this.setPadding(0, 0, 0, 0);
            }
        }
        this.mLeftInset = 0;
        this.mRightInset = 0;
        final DisplayCutout displayCutout = this.getRootWindowInsets().getDisplayCutout();
        if (displayCutout != null) {
            this.mLeftInset = displayCutout.getSafeInsetLeft();
            this.mRightInset = displayCutout.getSafeInsetRight();
        }
        this.mLeftInset = Math.max(insetsIgnoringVisibility.left, this.mLeftInset);
        this.mRightInset = Math.max(insetsIgnoringVisibility.right, this.mRightInset);
        this.applyMargins();
        return windowInsets;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.setWillNotDraw(true);
    }
    
    public void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        boolean b;
        if (!(b = this.mInteractionEventHandler.shouldInterceptTouchEvent(motionEvent))) {
            b = super.onInterceptTouchEvent(motionEvent);
        }
        if (b) {
            this.mInteractionEventHandler.didIntercept(motionEvent);
        }
        return b;
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        boolean b;
        if (!(b = this.mInteractionEventHandler.handleTouchEvent(motionEvent))) {
            b = super.onTouchEvent(motionEvent);
        }
        if (!b) {
            this.mInteractionEventHandler.didNotHandleTouchEvent(motionEvent);
        }
        return b;
    }
    
    protected void setInteractionEventHandler(final InteractionEventHandler mInteractionEventHandler) {
        this.mInteractionEventHandler = mInteractionEventHandler;
    }
    
    public ActionMode startActionModeForChild(final View view, final ActionMode$Callback actionMode$Callback, final int n) {
        if (n == 1) {
            return this.startActionMode(view, actionMode$Callback, n);
        }
        return super.startActionModeForChild(view, actionMode$Callback, n);
    }
    
    private class ActionModeCallback2Wrapper extends ActionMode$Callback2
    {
        private final ActionMode$Callback mWrapped;
        
        ActionModeCallback2Wrapper(final ActionMode$Callback mWrapped) {
            this.mWrapped = mWrapped;
        }
        
        public boolean onActionItemClicked(final ActionMode actionMode, final MenuItem menuItem) {
            return this.mWrapped.onActionItemClicked(actionMode, menuItem);
        }
        
        public boolean onCreateActionMode(final ActionMode actionMode, final Menu menu) {
            return this.mWrapped.onCreateActionMode(actionMode, menu);
        }
        
        public void onDestroyActionMode(final ActionMode actionMode) {
            this.mWrapped.onDestroyActionMode(actionMode);
            if (actionMode == NotificationShadeWindowView.this.mFloatingActionMode) {
                NotificationShadeWindowView.this.cleanupFloatingActionModeViews();
                NotificationShadeWindowView.this.mFloatingActionMode = null;
            }
            NotificationShadeWindowView.this.requestFitSystemWindows();
        }
        
        public void onGetContentRect(final ActionMode actionMode, final View view, final Rect rect) {
            final ActionMode$Callback mWrapped = this.mWrapped;
            if (mWrapped instanceof ActionMode$Callback2) {
                ((ActionMode$Callback2)mWrapped).onGetContentRect(actionMode, view, rect);
            }
            else {
                super.onGetContentRect(actionMode, view, rect);
            }
        }
        
        public boolean onPrepareActionMode(final ActionMode actionMode, final Menu menu) {
            NotificationShadeWindowView.this.requestFitSystemWindows();
            return this.mWrapped.onPrepareActionMode(actionMode, menu);
        }
    }
    
    interface InteractionEventHandler
    {
        void didIntercept(final MotionEvent p0);
        
        void didNotHandleTouchEvent(final MotionEvent p0);
        
        boolean dispatchKeyEvent(final KeyEvent p0);
        
        Boolean handleDispatchTouchEvent(final MotionEvent p0);
        
        boolean handleTouchEvent(final MotionEvent p0);
        
        boolean interceptMediaKey(final KeyEvent p0);
        
        boolean shouldInterceptTouchEvent(final MotionEvent p0);
    }
    
    class LayoutParams extends FrameLayout$LayoutParams
    {
        public boolean ignoreRightInset;
        
        LayoutParams(final NotificationShadeWindowView notificationShadeWindowView, final int n, final int n2) {
            super(n, n2);
        }
        
        LayoutParams(final NotificationShadeWindowView notificationShadeWindowView, final Context context, final AttributeSet set) {
            super(context, set);
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.StatusBarWindowView_Layout);
            this.ignoreRightInset = obtainStyledAttributes.getBoolean(R$styleable.StatusBarWindowView_Layout_ignoreRightInset, false);
            obtainStyledAttributes.recycle();
        }
    }
}
