// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.app;

import android.view.MenuItem;
import androidx.appcompat.view.SupportMenuInflater;
import android.view.MenuInflater;
import java.lang.ref.WeakReference;
import androidx.appcompat.view.menu.MenuBuilder;
import android.view.Menu;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.content.res.Configuration;
import android.view.ContextThemeWrapper;
import android.util.TypedValue;
import androidx.core.view.ViewPropertyAnimatorCompat;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.appcompat.R$attr;
import androidx.appcompat.R$styleable;
import androidx.appcompat.view.ActionBarPolicy;
import androidx.appcompat.R$id;
import androidx.appcompat.widget.Toolbar;
import android.app.Dialog;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;
import android.app.Activity;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import androidx.core.view.ViewPropertyAnimatorUpdateListener;
import androidx.appcompat.widget.ScrollingTabContainerView;
import java.util.ArrayList;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.DecorToolbar;
import androidx.appcompat.view.ViewPropertyAnimatorCompatSet;
import androidx.appcompat.widget.ActionBarContextView;
import android.content.Context;
import android.view.View;
import androidx.appcompat.widget.ActionBarContainer;
import android.view.animation.Interpolator;
import androidx.appcompat.widget.ActionBarOverlayLayout;

public class WindowDecorActionBar extends ActionBar implements ActionBarVisibilityCallback
{
    private static final Interpolator sHideInterpolator;
    private static final Interpolator sShowInterpolator;
    ActionModeImpl mActionMode;
    ActionBarContainer mContainerView;
    boolean mContentAnimations;
    View mContentView;
    Context mContext;
    ActionBarContextView mContextView;
    private int mCurWindowVisibility;
    ViewPropertyAnimatorCompatSet mCurrentShowAnim;
    DecorToolbar mDecorToolbar;
    ActionMode mDeferredDestroyActionMode;
    ActionMode.Callback mDeferredModeDestroyCallback;
    private boolean mDisplayHomeAsUpSet;
    private boolean mHasEmbeddedTabs;
    boolean mHiddenByApp;
    boolean mHiddenBySystem;
    final ViewPropertyAnimatorListener mHideListener;
    boolean mHideOnContentScroll;
    private boolean mLastMenuVisibility;
    private ArrayList<OnMenuVisibilityListener> mMenuVisibilityListeners;
    private boolean mNowShowing;
    ActionBarOverlayLayout mOverlayLayout;
    private boolean mShowHideAnimationEnabled;
    final ViewPropertyAnimatorListener mShowListener;
    private boolean mShowingForMode;
    ScrollingTabContainerView mTabScrollView;
    private Context mThemedContext;
    final ViewPropertyAnimatorUpdateListener mUpdateListener;
    
    static {
        sHideInterpolator = (Interpolator)new AccelerateInterpolator();
        sShowInterpolator = (Interpolator)new DecelerateInterpolator();
    }
    
    public WindowDecorActionBar(final Activity activity, final boolean b) {
        new ArrayList();
        this.mMenuVisibilityListeners = new ArrayList<OnMenuVisibilityListener>();
        this.mCurWindowVisibility = 0;
        this.mContentAnimations = true;
        this.mNowShowing = true;
        this.mHideListener = new ViewPropertyAnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(View mContentView) {
                final WindowDecorActionBar this$0 = WindowDecorActionBar.this;
                if (this$0.mContentAnimations) {
                    mContentView = this$0.mContentView;
                    if (mContentView != null) {
                        mContentView.setTranslationY(0.0f);
                        WindowDecorActionBar.this.mContainerView.setTranslationY(0.0f);
                    }
                }
                WindowDecorActionBar.this.mContainerView.setVisibility(8);
                WindowDecorActionBar.this.mContainerView.setTransitioning(false);
                final WindowDecorActionBar this$2 = WindowDecorActionBar.this;
                this$2.mCurrentShowAnim = null;
                this$2.completeDeferredDestroyActionMode();
                final ActionBarOverlayLayout mOverlayLayout = WindowDecorActionBar.this.mOverlayLayout;
                if (mOverlayLayout != null) {
                    ViewCompat.requestApplyInsets((View)mOverlayLayout);
                }
            }
        };
        this.mShowListener = new ViewPropertyAnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final View view) {
                final WindowDecorActionBar this$0 = WindowDecorActionBar.this;
                this$0.mCurrentShowAnim = null;
                this$0.mContainerView.requestLayout();
            }
        };
        this.mUpdateListener = new ViewPropertyAnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final View view) {
                ((View)WindowDecorActionBar.this.mContainerView.getParent()).invalidate();
            }
        };
        final View decorView = activity.getWindow().getDecorView();
        this.init(decorView);
        if (!b) {
            this.mContentView = decorView.findViewById(16908290);
        }
    }
    
    public WindowDecorActionBar(final Dialog dialog) {
        new ArrayList();
        this.mMenuVisibilityListeners = new ArrayList<OnMenuVisibilityListener>();
        this.mCurWindowVisibility = 0;
        this.mContentAnimations = true;
        this.mNowShowing = true;
        this.mHideListener = new ViewPropertyAnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(View mContentView) {
                final WindowDecorActionBar this$0 = WindowDecorActionBar.this;
                if (this$0.mContentAnimations) {
                    mContentView = this$0.mContentView;
                    if (mContentView != null) {
                        mContentView.setTranslationY(0.0f);
                        WindowDecorActionBar.this.mContainerView.setTranslationY(0.0f);
                    }
                }
                WindowDecorActionBar.this.mContainerView.setVisibility(8);
                WindowDecorActionBar.this.mContainerView.setTransitioning(false);
                final WindowDecorActionBar this$2 = WindowDecorActionBar.this;
                this$2.mCurrentShowAnim = null;
                this$2.completeDeferredDestroyActionMode();
                final ActionBarOverlayLayout mOverlayLayout = WindowDecorActionBar.this.mOverlayLayout;
                if (mOverlayLayout != null) {
                    ViewCompat.requestApplyInsets((View)mOverlayLayout);
                }
            }
        };
        this.mShowListener = new ViewPropertyAnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final View view) {
                final WindowDecorActionBar this$0 = WindowDecorActionBar.this;
                this$0.mCurrentShowAnim = null;
                this$0.mContainerView.requestLayout();
            }
        };
        this.mUpdateListener = new ViewPropertyAnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final View view) {
                ((View)WindowDecorActionBar.this.mContainerView.getParent()).invalidate();
            }
        };
        this.init(dialog.getWindow().getDecorView());
    }
    
    static boolean checkShowingFlags(final boolean b, final boolean b2, final boolean b3) {
        return b3 || (!b && !b2);
    }
    
    private DecorToolbar getDecorToolbar(final View view) {
        if (view instanceof DecorToolbar) {
            return (DecorToolbar)view;
        }
        if (view instanceof Toolbar) {
            return ((Toolbar)view).getWrapper();
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Can't make a decor toolbar out of ");
        String simpleName;
        if (view != null) {
            simpleName = view.getClass().getSimpleName();
        }
        else {
            simpleName = "null";
        }
        sb.append(simpleName);
        throw new IllegalStateException(sb.toString());
    }
    
    private void hideForActionMode() {
        if (this.mShowingForMode) {
            this.mShowingForMode = false;
            final ActionBarOverlayLayout mOverlayLayout = this.mOverlayLayout;
            if (mOverlayLayout != null) {
                mOverlayLayout.setShowingForActionMode(false);
            }
            this.updateVisibility(false);
        }
    }
    
    private void init(final View view) {
        final ActionBarOverlayLayout mOverlayLayout = (ActionBarOverlayLayout)view.findViewById(R$id.decor_content_parent);
        this.mOverlayLayout = mOverlayLayout;
        if (mOverlayLayout != null) {
            mOverlayLayout.setActionBarVisibilityCallback((ActionBarOverlayLayout.ActionBarVisibilityCallback)this);
        }
        this.mDecorToolbar = this.getDecorToolbar(view.findViewById(R$id.action_bar));
        this.mContextView = (ActionBarContextView)view.findViewById(R$id.action_context_bar);
        final ActionBarContainer mContainerView = (ActionBarContainer)view.findViewById(R$id.action_bar_container);
        this.mContainerView = mContainerView;
        final DecorToolbar mDecorToolbar = this.mDecorToolbar;
        if (mDecorToolbar != null && this.mContextView != null && mContainerView != null) {
            this.mContext = mDecorToolbar.getContext();
            final boolean b = (this.mDecorToolbar.getDisplayOptions() & 0x4) != 0x0;
            if (b) {
                this.mDisplayHomeAsUpSet = true;
            }
            final ActionBarPolicy value = ActionBarPolicy.get(this.mContext);
            this.setHomeButtonEnabled(value.enableHomeButtonByDefault() || b);
            this.setHasEmbeddedTabs(value.hasEmbeddedTabs());
            final TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes((AttributeSet)null, R$styleable.ActionBar, R$attr.actionBarStyle, 0);
            if (obtainStyledAttributes.getBoolean(R$styleable.ActionBar_hideOnContentScroll, false)) {
                this.setHideOnContentScrollEnabled(true);
            }
            final int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(R$styleable.ActionBar_elevation, 0);
            if (dimensionPixelSize != 0) {
                this.setElevation((float)dimensionPixelSize);
            }
            obtainStyledAttributes.recycle();
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(WindowDecorActionBar.class.getSimpleName());
        sb.append(" can only be used with a compatible window decor layout");
        throw new IllegalStateException(sb.toString());
    }
    
    private void setHasEmbeddedTabs(final boolean mHasEmbeddedTabs) {
        if (!(this.mHasEmbeddedTabs = mHasEmbeddedTabs)) {
            this.mDecorToolbar.setEmbeddedTabView(null);
            this.mContainerView.setTabContainer(this.mTabScrollView);
        }
        else {
            this.mContainerView.setTabContainer(null);
            this.mDecorToolbar.setEmbeddedTabView(this.mTabScrollView);
        }
        final int navigationMode = this.getNavigationMode();
        final boolean b = true;
        final boolean b2 = navigationMode == 2;
        final ScrollingTabContainerView mTabScrollView = this.mTabScrollView;
        if (mTabScrollView != null) {
            if (b2) {
                mTabScrollView.setVisibility(0);
                final ActionBarOverlayLayout mOverlayLayout = this.mOverlayLayout;
                if (mOverlayLayout != null) {
                    ViewCompat.requestApplyInsets((View)mOverlayLayout);
                }
            }
            else {
                mTabScrollView.setVisibility(8);
            }
        }
        this.mDecorToolbar.setCollapsible(!this.mHasEmbeddedTabs && b2);
        this.mOverlayLayout.setHasNonEmbeddedTabs(!this.mHasEmbeddedTabs && b2 && b);
    }
    
    private boolean shouldAnimateContextView() {
        return ViewCompat.isLaidOut((View)this.mContainerView);
    }
    
    private void showForActionMode() {
        if (!this.mShowingForMode) {
            this.mShowingForMode = true;
            final ActionBarOverlayLayout mOverlayLayout = this.mOverlayLayout;
            if (mOverlayLayout != null) {
                mOverlayLayout.setShowingForActionMode(true);
            }
            this.updateVisibility(false);
        }
    }
    
    private void updateVisibility(final boolean b) {
        if (checkShowingFlags(this.mHiddenByApp, this.mHiddenBySystem, this.mShowingForMode)) {
            if (!this.mNowShowing) {
                this.mNowShowing = true;
                this.doShow(b);
            }
        }
        else if (this.mNowShowing) {
            this.mNowShowing = false;
            this.doHide(b);
        }
    }
    
    public void animateToMode(final boolean b) {
        if (b) {
            this.showForActionMode();
        }
        else {
            this.hideForActionMode();
        }
        if (this.shouldAnimateContextView()) {
            ViewPropertyAnimatorCompat viewPropertyAnimatorCompat;
            ViewPropertyAnimatorCompat viewPropertyAnimatorCompat2;
            if (b) {
                viewPropertyAnimatorCompat = this.mDecorToolbar.setupAnimatorToVisibility(4, 100L);
                viewPropertyAnimatorCompat2 = this.mContextView.setupAnimatorToVisibility(0, 200L);
            }
            else {
                viewPropertyAnimatorCompat2 = this.mDecorToolbar.setupAnimatorToVisibility(0, 200L);
                viewPropertyAnimatorCompat = this.mContextView.setupAnimatorToVisibility(8, 100L);
            }
            final ViewPropertyAnimatorCompatSet set = new ViewPropertyAnimatorCompatSet();
            set.playSequentially(viewPropertyAnimatorCompat, viewPropertyAnimatorCompat2);
            set.start();
        }
        else if (b) {
            this.mDecorToolbar.setVisibility(4);
            this.mContextView.setVisibility(0);
        }
        else {
            this.mDecorToolbar.setVisibility(0);
            this.mContextView.setVisibility(8);
        }
    }
    
    @Override
    public boolean collapseActionView() {
        final DecorToolbar mDecorToolbar = this.mDecorToolbar;
        if (mDecorToolbar != null && mDecorToolbar.hasExpandedActionView()) {
            this.mDecorToolbar.collapseActionView();
            return true;
        }
        return false;
    }
    
    void completeDeferredDestroyActionMode() {
        final ActionMode.Callback mDeferredModeDestroyCallback = this.mDeferredModeDestroyCallback;
        if (mDeferredModeDestroyCallback != null) {
            mDeferredModeDestroyCallback.onDestroyActionMode(this.mDeferredDestroyActionMode);
            this.mDeferredDestroyActionMode = null;
            this.mDeferredModeDestroyCallback = null;
        }
    }
    
    @Override
    public void dispatchMenuVisibilityChanged(final boolean mLastMenuVisibility) {
        if (mLastMenuVisibility == this.mLastMenuVisibility) {
            return;
        }
        this.mLastMenuVisibility = mLastMenuVisibility;
        for (int size = this.mMenuVisibilityListeners.size(), i = 0; i < size; ++i) {
            this.mMenuVisibilityListeners.get(i).onMenuVisibilityChanged(mLastMenuVisibility);
        }
    }
    
    public void doHide(final boolean b) {
        final ViewPropertyAnimatorCompatSet mCurrentShowAnim = this.mCurrentShowAnim;
        if (mCurrentShowAnim != null) {
            mCurrentShowAnim.cancel();
        }
        if (this.mCurWindowVisibility == 0 && (this.mShowHideAnimationEnabled || b)) {
            this.mContainerView.setAlpha(1.0f);
            this.mContainerView.setTransitioning(true);
            final ViewPropertyAnimatorCompatSet mCurrentShowAnim2 = new ViewPropertyAnimatorCompatSet();
            float n2;
            final float n = n2 = (float)(-this.mContainerView.getHeight());
            if (b) {
                final int[] array2;
                final int[] array = array2 = new int[2];
                array2[1] = (array2[0] = 0);
                this.mContainerView.getLocationInWindow(array);
                n2 = n - array[1];
            }
            final ViewPropertyAnimatorCompat animate = ViewCompat.animate((View)this.mContainerView);
            animate.translationY(n2);
            animate.setUpdateListener(this.mUpdateListener);
            mCurrentShowAnim2.play(animate);
            if (this.mContentAnimations) {
                final View mContentView = this.mContentView;
                if (mContentView != null) {
                    final ViewPropertyAnimatorCompat animate2 = ViewCompat.animate(mContentView);
                    animate2.translationY(n2);
                    mCurrentShowAnim2.play(animate2);
                }
            }
            mCurrentShowAnim2.setInterpolator(WindowDecorActionBar.sHideInterpolator);
            mCurrentShowAnim2.setDuration(250L);
            mCurrentShowAnim2.setListener(this.mHideListener);
            (this.mCurrentShowAnim = mCurrentShowAnim2).start();
        }
        else {
            this.mHideListener.onAnimationEnd(null);
        }
    }
    
    public void doShow(final boolean b) {
        final ViewPropertyAnimatorCompatSet mCurrentShowAnim = this.mCurrentShowAnim;
        if (mCurrentShowAnim != null) {
            mCurrentShowAnim.cancel();
        }
        this.mContainerView.setVisibility(0);
        if (this.mCurWindowVisibility == 0 && (this.mShowHideAnimationEnabled || b)) {
            this.mContainerView.setTranslationY(0.0f);
            float n2;
            final float n = n2 = (float)(-this.mContainerView.getHeight());
            if (b) {
                final int[] array2;
                final int[] array = array2 = new int[2];
                array2[1] = (array2[0] = 0);
                this.mContainerView.getLocationInWindow(array);
                n2 = n - array[1];
            }
            this.mContainerView.setTranslationY(n2);
            final ViewPropertyAnimatorCompatSet mCurrentShowAnim2 = new ViewPropertyAnimatorCompatSet();
            final ViewPropertyAnimatorCompat animate = ViewCompat.animate((View)this.mContainerView);
            animate.translationY(0.0f);
            animate.setUpdateListener(this.mUpdateListener);
            mCurrentShowAnim2.play(animate);
            if (this.mContentAnimations) {
                final View mContentView = this.mContentView;
                if (mContentView != null) {
                    mContentView.setTranslationY(n2);
                    final ViewPropertyAnimatorCompat animate2 = ViewCompat.animate(this.mContentView);
                    animate2.translationY(0.0f);
                    mCurrentShowAnim2.play(animate2);
                }
            }
            mCurrentShowAnim2.setInterpolator(WindowDecorActionBar.sShowInterpolator);
            mCurrentShowAnim2.setDuration(250L);
            mCurrentShowAnim2.setListener(this.mShowListener);
            (this.mCurrentShowAnim = mCurrentShowAnim2).start();
        }
        else {
            this.mContainerView.setAlpha(1.0f);
            this.mContainerView.setTranslationY(0.0f);
            if (this.mContentAnimations) {
                final View mContentView2 = this.mContentView;
                if (mContentView2 != null) {
                    mContentView2.setTranslationY(0.0f);
                }
            }
            this.mShowListener.onAnimationEnd(null);
        }
        final ActionBarOverlayLayout mOverlayLayout = this.mOverlayLayout;
        if (mOverlayLayout != null) {
            ViewCompat.requestApplyInsets((View)mOverlayLayout);
        }
    }
    
    @Override
    public void enableContentAnimations(final boolean mContentAnimations) {
        this.mContentAnimations = mContentAnimations;
    }
    
    @Override
    public int getDisplayOptions() {
        return this.mDecorToolbar.getDisplayOptions();
    }
    
    public int getNavigationMode() {
        return this.mDecorToolbar.getNavigationMode();
    }
    
    @Override
    public Context getThemedContext() {
        if (this.mThemedContext == null) {
            final TypedValue typedValue = new TypedValue();
            this.mContext.getTheme().resolveAttribute(R$attr.actionBarWidgetTheme, typedValue, true);
            final int resourceId = typedValue.resourceId;
            if (resourceId != 0) {
                this.mThemedContext = (Context)new ContextThemeWrapper(this.mContext, resourceId);
            }
            else {
                this.mThemedContext = this.mContext;
            }
        }
        return this.mThemedContext;
    }
    
    @Override
    public void hideForSystem() {
        if (!this.mHiddenBySystem) {
            this.updateVisibility(this.mHiddenBySystem = true);
        }
    }
    
    @Override
    public void onConfigurationChanged(final Configuration configuration) {
        this.setHasEmbeddedTabs(ActionBarPolicy.get(this.mContext).hasEmbeddedTabs());
    }
    
    @Override
    public void onContentScrollStarted() {
        final ViewPropertyAnimatorCompatSet mCurrentShowAnim = this.mCurrentShowAnim;
        if (mCurrentShowAnim != null) {
            mCurrentShowAnim.cancel();
            this.mCurrentShowAnim = null;
        }
    }
    
    @Override
    public void onContentScrollStopped() {
    }
    
    @Override
    public boolean onKeyShortcut(final int n, final KeyEvent keyEvent) {
        final ActionModeImpl mActionMode = this.mActionMode;
        if (mActionMode == null) {
            return false;
        }
        final Menu menu = mActionMode.getMenu();
        if (menu != null) {
            int deviceId;
            if (keyEvent != null) {
                deviceId = keyEvent.getDeviceId();
            }
            else {
                deviceId = -1;
            }
            final int keyboardType = KeyCharacterMap.load(deviceId).getKeyboardType();
            boolean qwertyMode = true;
            if (keyboardType == 1) {
                qwertyMode = false;
            }
            menu.setQwertyMode(qwertyMode);
            return menu.performShortcut(n, keyEvent, 0);
        }
        return false;
    }
    
    @Override
    public void onWindowVisibilityChanged(final int mCurWindowVisibility) {
        this.mCurWindowVisibility = mCurWindowVisibility;
    }
    
    @Override
    public void setDefaultDisplayHomeAsUpEnabled(final boolean displayHomeAsUpEnabled) {
        if (!this.mDisplayHomeAsUpSet) {
            this.setDisplayHomeAsUpEnabled(displayHomeAsUpEnabled);
        }
    }
    
    public void setDisplayHomeAsUpEnabled(final boolean b) {
        int n;
        if (b) {
            n = 4;
        }
        else {
            n = 0;
        }
        this.setDisplayOptions(n, 4);
    }
    
    public void setDisplayOptions(final int n, final int n2) {
        final int displayOptions = this.mDecorToolbar.getDisplayOptions();
        if ((n2 & 0x4) != 0x0) {
            this.mDisplayHomeAsUpSet = true;
        }
        this.mDecorToolbar.setDisplayOptions((n & n2) | (n2 & displayOptions));
    }
    
    public void setElevation(final float n) {
        ViewCompat.setElevation((View)this.mContainerView, n);
    }
    
    public void setHideOnContentScrollEnabled(final boolean b) {
        if (b && !this.mOverlayLayout.isInOverlayMode()) {
            throw new IllegalStateException("Action bar must be in overlay mode (Window.FEATURE_OVERLAY_ACTION_BAR) to enable hide on content scroll");
        }
        this.mHideOnContentScroll = b;
        this.mOverlayLayout.setHideOnContentScrollEnabled(b);
    }
    
    public void setHomeButtonEnabled(final boolean homeButtonEnabled) {
        this.mDecorToolbar.setHomeButtonEnabled(homeButtonEnabled);
    }
    
    @Override
    public void setShowHideAnimationEnabled(final boolean mShowHideAnimationEnabled) {
        if (!(this.mShowHideAnimationEnabled = mShowHideAnimationEnabled)) {
            final ViewPropertyAnimatorCompatSet mCurrentShowAnim = this.mCurrentShowAnim;
            if (mCurrentShowAnim != null) {
                mCurrentShowAnim.cancel();
            }
        }
    }
    
    @Override
    public void setWindowTitle(final CharSequence windowTitle) {
        this.mDecorToolbar.setWindowTitle(windowTitle);
    }
    
    @Override
    public void showForSystem() {
        if (this.mHiddenBySystem) {
            this.mHiddenBySystem = false;
            this.updateVisibility(true);
        }
    }
    
    @Override
    public ActionMode startActionMode(final ActionMode.Callback callback) {
        final ActionModeImpl mActionMode = this.mActionMode;
        if (mActionMode != null) {
            mActionMode.finish();
        }
        this.mOverlayLayout.setHideOnContentScrollEnabled(false);
        this.mContextView.killMode();
        final ActionModeImpl mActionMode2 = new ActionModeImpl(this.mContextView.getContext(), callback);
        if (mActionMode2.dispatchOnCreate()) {
            (this.mActionMode = mActionMode2).invalidate();
            this.mContextView.initForMode(mActionMode2);
            this.animateToMode(true);
            this.mContextView.sendAccessibilityEvent(32);
            return mActionMode2;
        }
        return null;
    }
    
    public class ActionModeImpl extends ActionMode implements MenuBuilder.Callback
    {
        private final Context mActionModeContext;
        private ActionMode.Callback mCallback;
        private WeakReference<View> mCustomView;
        private final MenuBuilder mMenu;
        
        public ActionModeImpl(final Context mActionModeContext, final ActionMode.Callback mCallback) {
            this.mActionModeContext = mActionModeContext;
            this.mCallback = mCallback;
            final MenuBuilder mMenu = new MenuBuilder(mActionModeContext);
            mMenu.setDefaultShowAsAction(1);
            (this.mMenu = mMenu).setCallback((MenuBuilder.Callback)this);
        }
        
        public boolean dispatchOnCreate() {
            this.mMenu.stopDispatchingItemsChanged();
            try {
                return this.mCallback.onCreateActionMode(this, (Menu)this.mMenu);
            }
            finally {
                this.mMenu.startDispatchingItemsChanged();
            }
        }
        
        @Override
        public void finish() {
            final WindowDecorActionBar this$0 = WindowDecorActionBar.this;
            if (this$0.mActionMode != this) {
                return;
            }
            if (!WindowDecorActionBar.checkShowingFlags(this$0.mHiddenByApp, this$0.mHiddenBySystem, false)) {
                final WindowDecorActionBar this$2 = WindowDecorActionBar.this;
                this$2.mDeferredDestroyActionMode = this;
                this$2.mDeferredModeDestroyCallback = this.mCallback;
            }
            else {
                this.mCallback.onDestroyActionMode(this);
            }
            this.mCallback = null;
            WindowDecorActionBar.this.animateToMode(false);
            WindowDecorActionBar.this.mContextView.closeMode();
            WindowDecorActionBar.this.mDecorToolbar.getViewGroup().sendAccessibilityEvent(32);
            final WindowDecorActionBar this$3 = WindowDecorActionBar.this;
            this$3.mOverlayLayout.setHideOnContentScrollEnabled(this$3.mHideOnContentScroll);
            WindowDecorActionBar.this.mActionMode = null;
        }
        
        @Override
        public View getCustomView() {
            final WeakReference<View> mCustomView = this.mCustomView;
            View view;
            if (mCustomView != null) {
                view = mCustomView.get();
            }
            else {
                view = null;
            }
            return view;
        }
        
        @Override
        public Menu getMenu() {
            return (Menu)this.mMenu;
        }
        
        @Override
        public MenuInflater getMenuInflater() {
            return new SupportMenuInflater(this.mActionModeContext);
        }
        
        @Override
        public CharSequence getSubtitle() {
            return WindowDecorActionBar.this.mContextView.getSubtitle();
        }
        
        @Override
        public CharSequence getTitle() {
            return WindowDecorActionBar.this.mContextView.getTitle();
        }
        
        @Override
        public void invalidate() {
            if (WindowDecorActionBar.this.mActionMode != this) {
                return;
            }
            this.mMenu.stopDispatchingItemsChanged();
            try {
                this.mCallback.onPrepareActionMode(this, (Menu)this.mMenu);
            }
            finally {
                this.mMenu.startDispatchingItemsChanged();
            }
        }
        
        @Override
        public boolean isTitleOptional() {
            return WindowDecorActionBar.this.mContextView.isTitleOptional();
        }
        
        @Override
        public boolean onMenuItemSelected(final MenuBuilder menuBuilder, final MenuItem menuItem) {
            final ActionMode.Callback mCallback = this.mCallback;
            return mCallback != null && mCallback.onActionItemClicked(this, menuItem);
        }
        
        @Override
        public void onMenuModeChange(final MenuBuilder menuBuilder) {
            if (this.mCallback == null) {
                return;
            }
            this.invalidate();
            WindowDecorActionBar.this.mContextView.showOverflowMenu();
        }
        
        @Override
        public void setCustomView(final View view) {
            WindowDecorActionBar.this.mContextView.setCustomView(view);
            this.mCustomView = new WeakReference<View>(view);
        }
        
        @Override
        public void setSubtitle(final int n) {
            this.setSubtitle(WindowDecorActionBar.this.mContext.getResources().getString(n));
        }
        
        @Override
        public void setSubtitle(final CharSequence subtitle) {
            WindowDecorActionBar.this.mContextView.setSubtitle(subtitle);
        }
        
        @Override
        public void setTitle(final int n) {
            this.setTitle(WindowDecorActionBar.this.mContext.getResources().getString(n));
        }
        
        @Override
        public void setTitle(final CharSequence title) {
            WindowDecorActionBar.this.mContextView.setTitle(title);
        }
        
        @Override
        public void setTitleOptionalHint(final boolean b) {
            super.setTitleOptionalHint(b);
            WindowDecorActionBar.this.mContextView.setTitleOptional(b);
        }
    }
}
