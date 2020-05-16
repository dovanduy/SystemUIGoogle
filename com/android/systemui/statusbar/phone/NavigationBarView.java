// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.animation.ValueAnimator;
import com.android.systemui.shared.system.WindowManagerWrapper;
import android.util.Log;
import android.animation.LayoutTransition$TransitionListener;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import android.view.View$MeasureSpec;
import com.android.systemui.stackdivider.Divider;
import android.graphics.Canvas;
import com.android.systemui.assist.AssistHandleViewController;
import com.android.systemui.statusbar.NavigationBarController;
import android.view.WindowInsets;
import com.android.internal.annotations.VisibleForTesting;
import java.io.FileDescriptor;
import android.graphics.Point;
import android.animation.LayoutTransition;
import android.graphics.Region$Op;
import android.view.MotionEvent;
import android.view.ViewGroup$LayoutParams;
import android.view.WindowManager;
import android.view.ViewGroup;
import android.view.WindowManager$LayoutParams;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.animation.ObjectAnimator;
import android.util.Property;
import android.animation.PropertyValuesHolder;
import android.view.ViewTreeObserver$InternalInsetsInfo;
import android.content.res.Resources;
import android.content.res.Resources$NotFoundException;
import android.view.Display;
import java.io.PrintWriter;
import com.android.systemui.util.Utils;
import com.android.systemui.R$dimen;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.R$style;
import com.android.systemui.R$drawable;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.Dependency;
import com.android.systemui.recents.Recents;
import android.os.Bundle;
import com.android.systemui.R$string;
import com.android.systemui.R$id;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo$AccessibilityAction;
import android.view.inputmethod.InputMethodManager;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.model.SysUiState;
import com.android.systemui.recents.RecentsOnboarding;
import android.view.View$AccessibilityDelegate;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.recents.OverviewProxyService;
import android.view.ViewTreeObserver$OnComputeInternalInsetsListener;
import android.view.View$OnClickListener;
import java.util.function.Consumer;
import com.android.systemui.statusbar.policy.DeadZone;
import android.view.View;
import android.content.res.Configuration;
import android.util.SparseArray;
import com.android.systemui.statusbar.policy.KeyButtonDrawable;
import android.graphics.Rect;
import android.graphics.Region;
import android.widget.FrameLayout;

public class NavigationBarView extends FrameLayout implements ModeChangedListener
{
    private final Region mActiveRegion;
    private Rect mBackButtonBounds;
    private KeyButtonDrawable mBackIcon;
    private final NavigationBarTransitions mBarTransitions;
    private final SparseArray<ButtonDispatcher> mButtonDispatchers;
    private Configuration mConfiguration;
    private final ContextualButtonGroup mContextualButtonGroup;
    private int mCurrentRotation;
    View mCurrentView;
    private final DeadZone mDeadZone;
    private boolean mDeadZoneConsuming;
    int mDisabledFlags;
    private KeyButtonDrawable mDockedIcon;
    private final Consumer<Boolean> mDockedListener;
    private boolean mDockedStackExists;
    private final EdgeBackGestureHandler mEdgeBackGestureHandler;
    private FloatingRotationButton mFloatingRotationButton;
    private Rect mHomeButtonBounds;
    private KeyButtonDrawable mHomeDefaultIcon;
    private View mHorizontal;
    private final View$OnClickListener mImeSwitcherClickListener;
    private boolean mImeVisible;
    private boolean mInCarMode;
    private boolean mIsVertical;
    private boolean mLayoutTransitionsEnabled;
    private int mNavBarMode;
    private final int mNavColorSampleMargin;
    int mNavigationIconHints;
    private NavigationBarInflaterView mNavigationInflaterView;
    private final ViewTreeObserver$OnComputeInternalInsetsListener mOnComputeInternalInsetsListener;
    private OnVerticalChangedListener mOnVerticalChangedListener;
    private final OverviewProxyService mOverviewProxyService;
    private NotificationPanelViewController mPanelView;
    private final PluginManager mPluginManager;
    private final View$AccessibilityDelegate mQuickStepAccessibilityDelegate;
    private KeyButtonDrawable mRecentIcon;
    private Rect mRecentsButtonBounds;
    private RecentsOnboarding mRecentsOnboarding;
    private final RegionSamplingHelper mRegionSamplingHelper;
    private Rect mRotationButtonBounds;
    private RotationButtonController mRotationButtonController;
    private Rect mSamplingBounds;
    private ScreenPinningNotify mScreenPinningNotify;
    private final SysUiState mSysUiFlagContainer;
    private Configuration mTmpLastConfiguration;
    private int[] mTmpPosition;
    private final NavTransitionListener mTransitionListener;
    private boolean mUseCarModeUi;
    private View mVertical;
    private boolean mWakeAndUnlocking;
    
    public NavigationBarView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mCurrentView = null;
        this.mCurrentRotation = -1;
        this.mDisabledFlags = 0;
        this.mNavigationIconHints = 0;
        this.mHomeButtonBounds = new Rect();
        this.mBackButtonBounds = new Rect();
        this.mRecentsButtonBounds = new Rect();
        this.mRotationButtonBounds = new Rect();
        this.mActiveRegion = new Region();
        this.mTmpPosition = new int[2];
        this.mDeadZoneConsuming = false;
        this.mTransitionListener = new NavTransitionListener();
        this.mLayoutTransitionsEnabled = true;
        this.mUseCarModeUi = false;
        this.mInCarMode = false;
        this.mButtonDispatchers = (SparseArray<ButtonDispatcher>)new SparseArray();
        this.mSamplingBounds = new Rect();
        this.mImeSwitcherClickListener = (View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                ((InputMethodManager)NavigationBarView.this.mContext.getSystemService((Class)InputMethodManager.class)).showInputMethodPickerFromSystem(true, NavigationBarView.this.getContext().getDisplayId());
            }
        };
        this.mQuickStepAccessibilityDelegate = new View$AccessibilityDelegate() {
            private AccessibilityNodeInfo$AccessibilityAction mToggleOverviewAction;
            
            public void onInitializeAccessibilityNodeInfo(final View view, final AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                if (this.mToggleOverviewAction == null) {
                    this.mToggleOverviewAction = new AccessibilityNodeInfo$AccessibilityAction(R$id.action_toggle_overview, (CharSequence)NavigationBarView.this.getContext().getString(R$string.quick_step_accessibility_toggle_overview));
                }
                accessibilityNodeInfo.addAction(this.mToggleOverviewAction);
            }
            
            public boolean performAccessibilityAction(final View view, final int n, final Bundle bundle) {
                if (n == R$id.action_toggle_overview) {
                    Dependency.get(Recents.class).toggleRecentApps();
                    return true;
                }
                return super.performAccessibilityAction(view, n, bundle);
            }
        };
        this.mOnComputeInternalInsetsListener = (ViewTreeObserver$OnComputeInternalInsetsListener)new _$$Lambda$NavigationBarView$khIxhJwBd7pJnFFXnq8zupcHrv8(this);
        this.mDockedListener = (Consumer<Boolean>)new _$$Lambda$NavigationBarView$3_rm_LYAhHXvCBhrsX10ry5w8OA(this);
        this.mIsVertical = false;
        final int addListener = Dependency.get(NavigationModeController.class).addListener((NavigationModeController.ModeChangedListener)this);
        this.mNavBarMode = addListener;
        final boolean gesturalMode = QuickStepContract.isGesturalMode(addListener);
        this.mSysUiFlagContainer = Dependency.get(SysUiState.class);
        this.mPluginManager = Dependency.get(PluginManager.class);
        this.mContextualButtonGroup = new ContextualButtonGroup(R$id.menu_container);
        final ContextualButton contextualButton = new ContextualButton(R$id.ime_switcher, R$drawable.ic_ime_switcher_default);
        final RotationContextButton rotationContextButton = new RotationContextButton(R$id.rotate_suggestion, R$drawable.ic_sysbar_rotate_button);
        final ContextualButton contextualButton2 = new ContextualButton(R$id.accessibility_button, R$drawable.ic_sysbar_accessibility_button);
        this.mContextualButtonGroup.addButton(contextualButton);
        if (!gesturalMode) {
            this.mContextualButtonGroup.addButton(rotationContextButton);
        }
        this.mContextualButtonGroup.addButton(contextualButton2);
        final OverviewProxyService mOverviewProxyService = Dependency.get(OverviewProxyService.class);
        this.mOverviewProxyService = mOverviewProxyService;
        this.mRecentsOnboarding = new RecentsOnboarding(context, mOverviewProxyService);
        RotationButton mFloatingRotationButton = new FloatingRotationButton(context);
        this.mFloatingRotationButton = (FloatingRotationButton)mFloatingRotationButton;
        final int rotateButtonCCWStart90 = R$style.RotateButtonCCWStart90;
        if (!gesturalMode) {
            mFloatingRotationButton = rotationContextButton;
        }
        this.mRotationButtonController = new RotationButtonController(context, rotateButtonCCWStart90, mFloatingRotationButton);
        final ContextualButton contextualButton3 = new ContextualButton(R$id.back, 0);
        this.mConfiguration = new Configuration();
        this.mTmpLastConfiguration = new Configuration();
        this.mConfiguration.updateFrom(context.getResources().getConfiguration());
        this.mScreenPinningNotify = new ScreenPinningNotify(super.mContext);
        this.mBarTransitions = new NavigationBarTransitions(this, Dependency.get(CommandQueue.class));
        this.mButtonDispatchers.put(R$id.back, (Object)contextualButton3);
        final SparseArray<ButtonDispatcher> mButtonDispatchers = this.mButtonDispatchers;
        final int home = R$id.home;
        mButtonDispatchers.put(home, (Object)new ButtonDispatcher(home));
        final SparseArray<ButtonDispatcher> mButtonDispatchers2 = this.mButtonDispatchers;
        final int home_handle = R$id.home_handle;
        mButtonDispatchers2.put(home_handle, (Object)new ButtonDispatcher(home_handle));
        final SparseArray<ButtonDispatcher> mButtonDispatchers3 = this.mButtonDispatchers;
        final int recent_apps = R$id.recent_apps;
        mButtonDispatchers3.put(recent_apps, (Object)new ButtonDispatcher(recent_apps));
        this.mButtonDispatchers.put(R$id.ime_switcher, (Object)contextualButton);
        this.mButtonDispatchers.put(R$id.accessibility_button, (Object)contextualButton2);
        this.mButtonDispatchers.put(R$id.rotate_suggestion, (Object)rotationContextButton);
        this.mButtonDispatchers.put(R$id.menu_container, (Object)this.mContextualButtonGroup);
        this.mDeadZone = new DeadZone(this);
        this.mNavColorSampleMargin = this.getResources().getDimensionPixelSize(R$dimen.navigation_handle_sample_horizontal_margin);
        this.mEdgeBackGestureHandler = new EdgeBackGestureHandler(context, this.mOverviewProxyService, this.mSysUiFlagContainer, this.mPluginManager);
        this.mRegionSamplingHelper = new RegionSamplingHelper((View)this, (RegionSamplingHelper.SamplingCallback)new RegionSamplingHelper.SamplingCallback() {
            @Override
            public Rect getSampledRegion(final View view) {
                NavigationBarView.this.updateSamplingRect();
                return NavigationBarView.this.mSamplingBounds;
            }
            
            @Override
            public boolean isSamplingEnabled() {
                return Utils.isGesturalModeOnDefaultDisplay(NavigationBarView.this.getContext(), NavigationBarView.this.mNavBarMode);
            }
            
            @Override
            public void onRegionDarknessChanged(final boolean b) {
                NavigationBarView.this.getLightTransitionsController().setIconsDark(b ^ true, true);
            }
        });
    }
    
    private int chooseNavigationIconDrawableRes(int n, final int n2) {
        if (this.mOverviewProxyService.shouldShowSwipeUpUI()) {
            n = n2;
        }
        return n;
    }
    
    private static void dumpButton(final PrintWriter printWriter, final String str, final ButtonDispatcher buttonDispatcher) {
        final StringBuilder sb = new StringBuilder();
        sb.append("      ");
        sb.append(str);
        sb.append(": ");
        printWriter.print(sb.toString());
        if (buttonDispatcher == null) {
            printWriter.print("null");
        }
        else {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(visibilityToString(buttonDispatcher.getVisibility()));
            sb2.append(" alpha=");
            sb2.append(buttonDispatcher.getAlpha());
            printWriter.print(sb2.toString());
        }
        printWriter.println();
    }
    
    private Display getContextDisplay() {
        return this.getContext().getDisplay();
    }
    
    private KeyButtonDrawable getDrawable(final int n) {
        return KeyButtonDrawable.create(super.mContext, n, true);
    }
    
    private int getNavBarHeight() {
        int n;
        if (this.mIsVertical) {
            n = this.getResources().getDimensionPixelSize(17105326);
        }
        else {
            n = this.getResources().getDimensionPixelSize(17105324);
        }
        return n;
    }
    
    private String getResourceName(final int n) {
        if (n != 0) {
            final Resources resources = this.getContext().getResources();
            try {
                return resources.getResourceName(n);
            }
            catch (Resources$NotFoundException ex) {
                return "(unknown)";
            }
        }
        return "(null)";
    }
    
    private void notifyVerticalChangedListener(final boolean b) {
        final OnVerticalChangedListener mOnVerticalChangedListener = this.mOnVerticalChangedListener;
        if (mOnVerticalChangedListener != null) {
            mOnVerticalChangedListener.onVerticalChanged(b);
        }
    }
    
    private void onImeVisibilityChanged(final boolean mImeVisible) {
        if (!mImeVisible) {
            this.mTransitionListener.onBackAltCleared();
        }
        this.mImeVisible = mImeVisible;
        this.mRotationButtonController.getRotationButton().setCanShowRotationButton(this.mImeVisible ^ true);
    }
    
    private void orientBackButton(final KeyButtonDrawable keyButtonDrawable) {
        final boolean b = (this.mNavigationIconHints & 0x1) != 0x0;
        final boolean b2 = this.mConfiguration.getLayoutDirection() == 1;
        final float n = 0.0f;
        float rotation;
        if (b) {
            int n2;
            if (b2) {
                n2 = 90;
            }
            else {
                n2 = -90;
            }
            rotation = (float)n2;
        }
        else {
            rotation = 0.0f;
        }
        if (keyButtonDrawable.getRotation() == rotation) {
            return;
        }
        if (QuickStepContract.isGesturalMode(this.mNavBarMode)) {
            keyButtonDrawable.setRotation(rotation);
            return;
        }
        float n3 = n;
        if (!this.mOverviewProxyService.shouldShowSwipeUpUI()) {
            n3 = n;
            if (!this.mIsVertical) {
                n3 = n;
                if (b) {
                    n3 = -this.getResources().getDimension(R$dimen.navbar_back_button_ime_offset);
                }
            }
        }
        final ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder((Object)keyButtonDrawable, new PropertyValuesHolder[] { PropertyValuesHolder.ofFloat((Property)KeyButtonDrawable.KEY_DRAWABLE_ROTATE, new float[] { rotation }), PropertyValuesHolder.ofFloat((Property)KeyButtonDrawable.KEY_DRAWABLE_TRANSLATE_Y, new float[] { n3 }) });
        ofPropertyValuesHolder.setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN);
        ofPropertyValuesHolder.setDuration(200L);
        ofPropertyValuesHolder.start();
    }
    
    private void orientHomeButton(final KeyButtonDrawable keyButtonDrawable) {
        float rotation;
        if (this.mIsVertical) {
            rotation = 90.0f;
        }
        else {
            rotation = 0.0f;
        }
        keyButtonDrawable.setRotation(rotation);
    }
    
    private void reloadNavIcons() {
        this.updateIcons(Configuration.EMPTY);
    }
    
    private void resetViews() {
        this.mHorizontal.setVisibility(8);
        this.mVertical.setVisibility(8);
    }
    
    private void setSlippery(final boolean b) {
        this.setWindowFlag(536870912, b);
    }
    
    private void setUpSwipeUpOnboarding(final boolean b) {
        if (b) {
            this.mRecentsOnboarding.onConnectedToLauncher();
        }
        else {
            this.mRecentsOnboarding.onDisconnectedFromLauncher();
        }
    }
    
    private void setUseFadingAnimations(final boolean b) {
        final WindowManager$LayoutParams windowManager$LayoutParams = (WindowManager$LayoutParams)((ViewGroup)this.getParent()).getLayoutParams();
        if (windowManager$LayoutParams != null) {
            final boolean b2 = windowManager$LayoutParams.windowAnimations != 0;
            if (!b2 && b) {
                windowManager$LayoutParams.windowAnimations = R$style.Animation_NavigationBarFadeIn;
            }
            else {
                if (!b2 || b) {
                    return;
                }
                windowManager$LayoutParams.windowAnimations = 0;
            }
            ((WindowManager)this.getContext().getSystemService("window")).updateViewLayout((View)this.getParent(), (ViewGroup$LayoutParams)windowManager$LayoutParams);
        }
    }
    
    private void setWindowFlag(final int n, final boolean b) {
        final ViewGroup viewGroup = (ViewGroup)this.getParent();
        if (viewGroup == null) {
            return;
        }
        final WindowManager$LayoutParams windowManager$LayoutParams = (WindowManager$LayoutParams)viewGroup.getLayoutParams();
        if (windowManager$LayoutParams != null) {
            if (b != ((windowManager$LayoutParams.flags & n) != 0x0)) {
                if (b) {
                    windowManager$LayoutParams.flags |= n;
                }
                else {
                    windowManager$LayoutParams.flags &= n;
                }
                ((WindowManager)this.getContext().getSystemService("window")).updateViewLayout((View)viewGroup, (ViewGroup$LayoutParams)windowManager$LayoutParams);
            }
        }
    }
    
    private boolean shouldDeadZoneConsumeTouchEvents(final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.mDeadZoneConsuming = false;
        }
        if (!this.mDeadZone.onTouchEvent(motionEvent) && !this.mDeadZoneConsuming) {
            return false;
        }
        if (actionMasked != 0) {
            if (actionMasked == 1 || actionMasked == 3) {
                this.updateSlippery();
                this.mDeadZoneConsuming = false;
            }
        }
        else {
            this.setSlippery(true);
            this.mDeadZoneConsuming = true;
        }
        return true;
    }
    
    private void updateButtonLocation(final ButtonDispatcher buttonDispatcher, final Rect rect, final boolean b) {
        final View currentView = buttonDispatcher.getCurrentView();
        if (currentView == null) {
            rect.setEmpty();
            return;
        }
        final float translationX = currentView.getTranslationX();
        final float translationY = currentView.getTranslationY();
        currentView.setTranslationX(0.0f);
        currentView.setTranslationY(0.0f);
        if (b) {
            currentView.getLocationOnScreen(this.mTmpPosition);
            final int[] mTmpPosition = this.mTmpPosition;
            rect.set(mTmpPosition[0], mTmpPosition[1], mTmpPosition[0] + currentView.getMeasuredWidth(), this.mTmpPosition[1] + currentView.getMeasuredHeight());
            this.mActiveRegion.op(rect, Region$Op.UNION);
        }
        currentView.getLocationInWindow(this.mTmpPosition);
        final int[] mTmpPosition2 = this.mTmpPosition;
        rect.set(mTmpPosition2[0], mTmpPosition2[1], mTmpPosition2[0] + currentView.getMeasuredWidth(), this.mTmpPosition[1] + currentView.getMeasuredHeight());
        currentView.setTranslationX(translationX);
        currentView.setTranslationY(translationY);
    }
    
    private boolean updateCarMode() {
        final Configuration mConfiguration = this.mConfiguration;
        if (mConfiguration != null) {
            final boolean mInCarMode = (mConfiguration.uiMode & 0xF) == 0x3;
            if (mInCarMode != this.mInCarMode) {
                this.mInCarMode = mInCarMode;
                this.mUseCarModeUi = false;
            }
        }
        return false;
    }
    
    private void updateCurrentView() {
        this.resetViews();
        View mCurrentView;
        if (this.mIsVertical) {
            mCurrentView = this.mVertical;
        }
        else {
            mCurrentView = this.mHorizontal;
        }
        this.mCurrentView = mCurrentView;
        boolean alternativeOrder = false;
        mCurrentView.setVisibility(0);
        this.mNavigationInflaterView.setVertical(this.mIsVertical);
        final int rotation = this.getContextDisplay().getRotation();
        this.mCurrentRotation = rotation;
        final NavigationBarInflaterView mNavigationInflaterView = this.mNavigationInflaterView;
        if (rotation == 1) {
            alternativeOrder = true;
        }
        mNavigationInflaterView.setAlternativeOrder(alternativeOrder);
        this.mNavigationInflaterView.updateButtonDispatchersCurrentView();
        this.updateLayoutTransitionsEnabled();
    }
    
    private void updateIcons(final Configuration configuration) {
        final int orientation = configuration.orientation;
        final int orientation2 = this.mConfiguration.orientation;
        boolean b = true;
        final boolean b2 = orientation != orientation2;
        final boolean b3 = configuration.densityDpi != this.mConfiguration.densityDpi;
        if (configuration.getLayoutDirection() == this.mConfiguration.getLayoutDirection()) {
            b = false;
        }
        if (b2 || b3) {
            this.mDockedIcon = this.getDrawable(R$drawable.ic_sysbar_docked);
            this.mHomeDefaultIcon = this.getHomeDrawable();
        }
        if (b3 || b) {
            this.mRecentIcon = this.getDrawable(R$drawable.ic_sysbar_recent);
            this.mContextualButtonGroup.updateIcons();
        }
        if (b2 || b3 || b) {
            this.mBackIcon = this.getBackDrawable();
        }
    }
    
    private void updateLayoutTransitionsEnabled() {
        final boolean b = !this.mWakeAndUnlocking && this.mLayoutTransitionsEnabled;
        final LayoutTransition layoutTransition = ((ViewGroup)this.getCurrentView().findViewById(R$id.nav_buttons)).getLayoutTransition();
        if (layoutTransition != null) {
            if (b) {
                layoutTransition.enableTransitionType(2);
                layoutTransition.enableTransitionType(3);
                layoutTransition.enableTransitionType(0);
                layoutTransition.enableTransitionType(1);
            }
            else {
                layoutTransition.disableTransitionType(2);
                layoutTransition.disableTransitionType(3);
                layoutTransition.disableTransitionType(0);
                layoutTransition.disableTransitionType(1);
            }
        }
    }
    
    private void updateOrientationViews() {
        this.mHorizontal = this.findViewById(R$id.horizontal);
        this.mVertical = this.findViewById(R$id.vertical);
        this.updateCurrentView();
    }
    
    private void updateRecentsIcon() {
        final KeyButtonDrawable mDockedIcon = this.mDockedIcon;
        float rotation;
        if (this.mDockedStackExists && this.mIsVertical) {
            rotation = 90.0f;
        }
        else {
            rotation = 0.0f;
        }
        mDockedIcon.setRotation(rotation);
        final ButtonDispatcher recentsButton = this.getRecentsButton();
        KeyButtonDrawable imageDrawable;
        if (this.mDockedStackExists) {
            imageDrawable = this.mDockedIcon;
        }
        else {
            imageDrawable = this.mRecentIcon;
        }
        recentsButton.setImageDrawable(imageDrawable);
        this.mBarTransitions.reapplyDarkIntensity();
    }
    
    private void updateSamplingRect() {
        this.mSamplingBounds.setEmpty();
        final View currentView = this.getHomeHandle().getCurrentView();
        if (currentView != null) {
            final int[] array = new int[2];
            currentView.getLocationOnScreen(array);
            final Point point = new Point();
            currentView.getContext().getDisplay().getRealSize(point);
            this.mSamplingBounds.set(new Rect(array[0] - this.mNavColorSampleMargin, point.y - this.getNavBarHeight(), array[0] + currentView.getWidth() + this.mNavColorSampleMargin, point.y));
        }
    }
    
    private static String visibilityToString(final int n) {
        if (n == 4) {
            return "INVISIBLE";
        }
        if (n != 8) {
            return "VISIBLE";
        }
        return "GONE";
    }
    
    public void abortCurrentGesture() {
        this.getHomeButton().abortCurrentGesture();
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("NavigationBarView {");
        final Rect rect = new Rect();
        final Point point = new Point();
        this.getContextDisplay().getRealSize(point);
        final StringBuilder sb = new StringBuilder();
        sb.append("      this: ");
        sb.append(StatusBar.viewInfo((View)this));
        sb.append(" ");
        sb.append(visibilityToString(this.getVisibility()));
        printWriter.println(String.format(sb.toString(), new Object[0]));
        this.getWindowVisibleDisplayFrame(rect);
        final boolean b = rect.right > point.x || rect.bottom > point.y;
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("      window: ");
        sb2.append(rect.toShortString());
        sb2.append(" ");
        sb2.append(visibilityToString(this.getWindowVisibility()));
        String str;
        if (b) {
            str = " OFFSCREEN!";
        }
        else {
            str = "";
        }
        sb2.append(str);
        printWriter.println(sb2.toString());
        printWriter.println(String.format("      mCurrentView: id=%s (%dx%d) %s %f", this.getResourceName(this.getCurrentView().getId()), this.getCurrentView().getWidth(), this.getCurrentView().getHeight(), visibilityToString(this.getCurrentView().getVisibility()), this.getCurrentView().getAlpha()));
        final int mDisabledFlags = this.mDisabledFlags;
        String s;
        if (this.mIsVertical) {
            s = "true";
        }
        else {
            s = "false";
        }
        printWriter.println(String.format("      disabled=0x%08x vertical=%s darkIntensity=%.2f", mDisabledFlags, s, this.getLightTransitionsController().getCurrentDarkIntensity()));
        dumpButton(printWriter, "back", this.getBackButton());
        dumpButton(printWriter, "home", this.getHomeButton());
        dumpButton(printWriter, "rcnt", this.getRecentsButton());
        dumpButton(printWriter, "rota", this.getRotateSuggestionButton());
        dumpButton(printWriter, "a11y", this.getAccessibilityButton());
        printWriter.println("    }");
        this.mContextualButtonGroup.dump(printWriter);
        this.mRecentsOnboarding.dump(printWriter);
        this.mRegionSamplingHelper.dump(printWriter);
        this.mEdgeBackGestureHandler.dump(printWriter);
    }
    
    public ButtonDispatcher getAccessibilityButton() {
        return (ButtonDispatcher)this.mButtonDispatchers.get(R$id.accessibility_button);
    }
    
    public ButtonDispatcher getBackButton() {
        return (ButtonDispatcher)this.mButtonDispatchers.get(R$id.back);
    }
    
    public KeyButtonDrawable getBackDrawable() {
        final KeyButtonDrawable drawable = this.getDrawable(this.getBackDrawableRes());
        this.orientBackButton(drawable);
        return drawable;
    }
    
    public int getBackDrawableRes() {
        return this.chooseNavigationIconDrawableRes(R$drawable.ic_sysbar_back, R$drawable.ic_sysbar_back_quick_step);
    }
    
    public NavigationBarTransitions getBarTransitions() {
        return this.mBarTransitions;
    }
    
    public SparseArray<ButtonDispatcher> getButtonDispatchers() {
        return this.mButtonDispatchers;
    }
    
    public View getCurrentView() {
        return this.mCurrentView;
    }
    
    public ButtonDispatcher getHomeButton() {
        return (ButtonDispatcher)this.mButtonDispatchers.get(R$id.home);
    }
    
    public KeyButtonDrawable getHomeDrawable() {
        KeyButtonDrawable keyButtonDrawable;
        if (this.mOverviewProxyService.shouldShowSwipeUpUI()) {
            keyButtonDrawable = this.getDrawable(R$drawable.ic_sysbar_home_quick_step);
        }
        else {
            keyButtonDrawable = this.getDrawable(R$drawable.ic_sysbar_home);
        }
        this.orientHomeButton(keyButtonDrawable);
        return keyButtonDrawable;
    }
    
    public ButtonDispatcher getHomeHandle() {
        return (ButtonDispatcher)this.mButtonDispatchers.get(R$id.home_handle);
    }
    
    public ButtonDispatcher getImeSwitchButton() {
        return (ButtonDispatcher)this.mButtonDispatchers.get(R$id.ime_switcher);
    }
    
    public LightBarTransitionsController getLightTransitionsController() {
        return this.mBarTransitions.getLightTransitionsController();
    }
    
    public ButtonDispatcher getRecentsButton() {
        return (ButtonDispatcher)this.mButtonDispatchers.get(R$id.recent_apps);
    }
    
    public RotationContextButton getRotateSuggestionButton() {
        return (RotationContextButton)this.mButtonDispatchers.get(R$id.rotate_suggestion);
    }
    
    public RotationButtonController getRotationButtonController() {
        return this.mRotationButtonController;
    }
    
    void hideRecentsOnboarding() {
        this.mRecentsOnboarding.hide(true);
    }
    
    public boolean isOverviewEnabled() {
        return (this.mDisabledFlags & 0x1000000) == 0x0;
    }
    
    public boolean isQuickStepSwipeUpEnabled() {
        return this.mOverviewProxyService.shouldShowSwipeUpUI() && this.isOverviewEnabled();
    }
    
    @VisibleForTesting
    boolean isRecentsButtonDisabled() {
        return this.mUseCarModeUi || !this.isOverviewEnabled() || this.getContext().getDisplayId() != 0;
    }
    
    public boolean isRecentsButtonVisible() {
        return this.getRecentsButton().getVisibility() == 0;
    }
    
    boolean needsReorient(final int n) {
        return this.mCurrentRotation != n;
    }
    
    public WindowInsets onApplyWindowInsets(final WindowInsets windowInsets) {
        final int systemWindowInsetLeft = windowInsets.getSystemWindowInsetLeft();
        final int systemWindowInsetRight = windowInsets.getSystemWindowInsetRight();
        this.setPadding(systemWindowInsetLeft, windowInsets.getSystemWindowInsetTop(), systemWindowInsetRight, windowInsets.getSystemWindowInsetBottom());
        this.mEdgeBackGestureHandler.setInsets(systemWindowInsetLeft, systemWindowInsetRight);
        final boolean b = !QuickStepContract.isGesturalMode(this.mNavBarMode) || windowInsets.getSystemWindowInsetBottom() == 0;
        this.setClipChildren(b);
        this.setClipToPadding(b);
        final NavigationBarController navigationBarController = Dependency.get(NavigationBarController.class);
        AssistHandleViewController assistHandlerViewController;
        if (navigationBarController == null) {
            assistHandlerViewController = null;
        }
        else {
            assistHandlerViewController = navigationBarController.getAssistHandlerViewController();
        }
        if (assistHandlerViewController != null) {
            assistHandlerViewController.setBottomOffset(windowInsets.getSystemWindowInsetBottom());
        }
        return super.onApplyWindowInsets(windowInsets);
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.requestApplyInsets();
        this.reorient();
        this.onNavigationModeChanged(this.mNavBarMode);
        this.setUpSwipeUpOnboarding(this.isQuickStepSwipeUpEnabled());
        final RotationButtonController mRotationButtonController = this.mRotationButtonController;
        if (mRotationButtonController != null) {
            mRotationButtonController.registerListeners();
        }
        this.mEdgeBackGestureHandler.onNavBarAttached();
        this.getViewTreeObserver().addOnComputeInternalInsetsListener(this.mOnComputeInternalInsetsListener);
    }
    
    void onBarTransition(final int n) {
        if (n == 4) {
            this.mRegionSamplingHelper.stop();
            this.getLightTransitionsController().setIconsDark(false, true);
        }
        else {
            this.mRegionSamplingHelper.start(this.mSamplingBounds);
        }
    }
    
    protected void onConfigurationChanged(Configuration mTmpLastConfiguration) {
        super.onConfigurationChanged(mTmpLastConfiguration);
        this.mTmpLastConfiguration.updateFrom(this.mConfiguration);
        this.mConfiguration.updateFrom(mTmpLastConfiguration);
        final boolean updateCarMode = this.updateCarMode();
        this.updateIcons(this.mTmpLastConfiguration);
        this.updateRecentsIcon();
        this.mRecentsOnboarding.onConfigurationChanged(this.mConfiguration);
        if (!updateCarMode) {
            mTmpLastConfiguration = this.mTmpLastConfiguration;
            if (mTmpLastConfiguration.densityDpi == this.mConfiguration.densityDpi && mTmpLastConfiguration.getLayoutDirection() == this.mConfiguration.getLayoutDirection()) {
                return;
            }
        }
        this.updateNavButtonIcons();
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Dependency.get(NavigationModeController.class).removeListener((NavigationModeController.ModeChangedListener)this);
        int i = 0;
        this.setUpSwipeUpOnboarding(false);
        while (i < this.mButtonDispatchers.size()) {
            ((ButtonDispatcher)this.mButtonDispatchers.valueAt(i)).onDestroy();
            ++i;
        }
        final RotationButtonController mRotationButtonController = this.mRotationButtonController;
        if (mRotationButtonController != null) {
            mRotationButtonController.unregisterListeners();
        }
        this.mEdgeBackGestureHandler.onNavBarDetached();
        this.getViewTreeObserver().removeOnComputeInternalInsetsListener(this.mOnComputeInternalInsetsListener);
    }
    
    protected void onDraw(final Canvas canvas) {
        this.mDeadZone.onDraw(canvas);
        super.onDraw(canvas);
    }
    
    public void onFinishInflate() {
        (this.mNavigationInflaterView = (NavigationBarInflaterView)this.findViewById(R$id.navigation_inflater)).setButtonDispatchers(this.mButtonDispatchers);
        this.getImeSwitchButton().setOnClickListener(this.mImeSwitcherClickListener);
        Dependency.get(Divider.class).registerInSplitScreenListener(this.mDockedListener);
        this.updateOrientationViews();
        this.reloadNavIcons();
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        return this.shouldDeadZoneConsumeTouchEvents(motionEvent) || super.onInterceptTouchEvent(motionEvent);
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        this.mActiveRegion.setEmpty();
        this.updateButtonLocation(this.getBackButton(), this.mBackButtonBounds, true);
        this.updateButtonLocation(this.getHomeButton(), this.mHomeButtonBounds, false);
        this.updateButtonLocation(this.getRecentsButton(), this.mRecentsButtonBounds, false);
        this.updateButtonLocation(this.getRotateSuggestionButton(), this.mRotationButtonBounds, true);
        this.mOverviewProxyService.onActiveNavBarRegionChanges(this.mActiveRegion);
        this.mRecentsOnboarding.setNavBarHeight(this.getMeasuredHeight());
    }
    
    protected void onMeasure(final int n, final int n2) {
        final int size = View$MeasureSpec.getSize(n);
        final int size2 = View$MeasureSpec.getSize(n2);
        final boolean mIsVertical = size > 0 && size2 > size && !QuickStepContract.isGesturalMode(this.mNavBarMode);
        if (mIsVertical != this.mIsVertical) {
            this.mIsVertical = mIsVertical;
            this.reorient();
            this.notifyVerticalChangedListener(mIsVertical);
        }
        if (QuickStepContract.isGesturalMode(this.mNavBarMode)) {
            int n3;
            if (this.mIsVertical) {
                n3 = this.getResources().getDimensionPixelSize(17105326);
            }
            else {
                n3 = this.getResources().getDimensionPixelSize(17105324);
            }
            this.mBarTransitions.setBackgroundFrame(new Rect(0, this.getResources().getDimensionPixelSize(17105321) - n3, size, size2));
        }
        super.onMeasure(n, n2);
    }
    
    public void onNavigationModeChanged(final int mNavBarMode) {
        final Context currentUserContext = Dependency.get(NavigationModeController.class).getCurrentUserContext();
        this.mNavBarMode = mNavBarMode;
        this.mBarTransitions.onNavigationModeChanged(mNavBarMode);
        this.mEdgeBackGestureHandler.onNavigationModeChanged(this.mNavBarMode, currentUserContext);
        this.mRecentsOnboarding.onNavigationModeChanged(this.mNavBarMode);
        this.getRotateSuggestionButton().onNavigationModeChanged(this.mNavBarMode);
        if (QuickStepContract.isGesturalMode(this.mNavBarMode)) {
            this.mRegionSamplingHelper.start(this.mSamplingBounds);
        }
        else {
            this.mRegionSamplingHelper.stop();
        }
    }
    
    public void onScreenStateChanged(final boolean b) {
        if (b) {
            if (Utils.isGesturalModeOnDefaultDisplay(this.getContext(), this.mNavBarMode)) {
                this.mRegionSamplingHelper.start(this.mSamplingBounds);
            }
        }
        else {
            this.mRegionSamplingHelper.stop();
        }
    }
    
    public void onStatusBarPanelStateChanged() {
        this.updateSlippery();
        this.updatePanelSystemUiStateFlags();
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        this.shouldDeadZoneConsumeTouchEvents(motionEvent);
        return super.onTouchEvent(motionEvent);
    }
    
    void onTransientStateChanged(final boolean b) {
        this.mEdgeBackGestureHandler.onNavBarTransientStateChanged(b);
    }
    
    public void reorient() {
        this.updateCurrentView();
        ((NavigationBarFrame)this.getRootView()).setDeadZone(this.mDeadZone);
        this.mDeadZone.onConfigurationChanged(this.mCurrentRotation);
        this.mBarTransitions.init();
        if (!this.isLayoutDirectionResolved()) {
            this.resolveLayoutDirection();
        }
        this.updateNavButtonIcons();
        this.getHomeButton().setVertical(this.mIsVertical);
    }
    
    public void setAccessibilityButtonState(final boolean b, final boolean longClickable) {
        this.getAccessibilityButton().setLongClickable(longClickable);
        this.mContextualButtonGroup.setButtonVisibility(R$id.accessibility_button, b);
    }
    
    public void setComponents(final NotificationPanelViewController mPanelView) {
        this.mPanelView = mPanelView;
        this.updatePanelSystemUiStateFlags();
    }
    
    public void setDisabledFlags(final int mDisabledFlags) {
        if (this.mDisabledFlags == mDisabledFlags) {
            return;
        }
        final boolean overviewEnabled = this.isOverviewEnabled();
        this.mDisabledFlags = mDisabledFlags;
        if (!overviewEnabled && this.isOverviewEnabled()) {
            this.reloadNavIcons();
        }
        this.updateNavButtonIcons();
        this.updateSlippery();
        this.setUpSwipeUpOnboarding(this.isQuickStepSwipeUpEnabled());
        this.updateDisabledSystemUiStateFlags();
    }
    
    public void setLayoutDirection(final int layoutDirection) {
        this.reloadNavIcons();
        super.setLayoutDirection(layoutDirection);
    }
    
    public void setLayoutTransitionsEnabled(final boolean mLayoutTransitionsEnabled) {
        this.mLayoutTransitionsEnabled = mLayoutTransitionsEnabled;
        this.updateLayoutTransitionsEnabled();
    }
    
    public void setNavigationIconHints(final int mNavigationIconHints) {
        if (mNavigationIconHints == this.mNavigationIconHints) {
            return;
        }
        boolean b = false;
        final boolean b2 = (mNavigationIconHints & 0x1) != 0x0;
        if ((this.mNavigationIconHints & 0x1) != 0x0) {
            b = true;
        }
        if (b2 != b) {
            this.onImeVisibilityChanged(b2);
        }
        this.mNavigationIconHints = mNavigationIconHints;
        this.updateNavButtonIcons();
    }
    
    public void setOnVerticalChangedListener(final OnVerticalChangedListener mOnVerticalChangedListener) {
        this.mOnVerticalChangedListener = mOnVerticalChangedListener;
        this.notifyVerticalChangedListener(this.mIsVertical);
    }
    
    public void setWakeAndUnlocking(final boolean b) {
        this.setUseFadingAnimations(b);
        this.mWakeAndUnlocking = b;
        this.updateLayoutTransitionsEnabled();
    }
    
    public void setWindowVisible(final boolean windowVisible) {
        this.mRegionSamplingHelper.setWindowVisible(windowVisible);
        this.mRotationButtonController.onNavigationBarWindowVisibilityChange(windowVisible);
    }
    
    public void showPinningEnterExitToast(final boolean b) {
        if (b) {
            this.mScreenPinningNotify.showPinningStartToast();
        }
        else {
            this.mScreenPinningNotify.showPinningExitToast();
        }
    }
    
    public void showPinningEscapeToast() {
        this.mScreenPinningNotify.showEscapeToast(this.mNavBarMode == 2, this.isRecentsButtonVisible());
    }
    
    public void updateDisabledSystemUiStateFlags() {
        final int displayId = super.mContext.getDisplayId();
        final SysUiState mSysUiFlagContainer = this.mSysUiFlagContainer;
        final boolean screenPinningActive = ActivityManagerWrapper.getInstance().isScreenPinningActive();
        final boolean b = true;
        mSysUiFlagContainer.setFlag(1, screenPinningActive);
        mSysUiFlagContainer.setFlag(128, (this.mDisabledFlags & 0x1000000) != 0x0);
        mSysUiFlagContainer.setFlag(256, (this.mDisabledFlags & 0x200000) != 0x0);
        mSysUiFlagContainer.setFlag(1024, (this.mDisabledFlags & 0x2000000) != 0x0 && b);
        mSysUiFlagContainer.commitUpdate(displayId);
    }
    
    public void updateNavButtonIcons() {
        final int mNavigationIconHints = this.mNavigationIconHints;
        final int n = 0;
        final boolean b = (mNavigationIconHints & 0x1) != 0x0;
        final KeyButtonDrawable mBackIcon = this.mBackIcon;
        this.orientBackButton(mBackIcon);
        final KeyButtonDrawable mHomeDefaultIcon = this.mHomeDefaultIcon;
        if (!this.mUseCarModeUi) {
            this.orientHomeButton(mHomeDefaultIcon);
        }
        this.getHomeButton().setImageDrawable(mHomeDefaultIcon);
        this.getBackButton().setImageDrawable(mBackIcon);
        this.updateRecentsIcon();
        this.mContextualButtonGroup.setButtonVisibility(R$id.ime_switcher, (this.mNavigationIconHints & 0x2) != 0x0);
        this.mBarTransitions.reapplyDarkIntensity();
        final boolean b2 = QuickStepContract.isGesturalMode(this.mNavBarMode) || (this.mDisabledFlags & 0x200000) != 0x0;
        boolean recentsButtonDisabled = this.isRecentsButtonDisabled();
        final boolean b3 = recentsButtonDisabled && (0x200000 & this.mDisabledFlags) != 0x0;
        final boolean b4 = !b && (QuickStepContract.isGesturalMode(this.mNavBarMode) || (this.mDisabledFlags & 0x400000) != 0x0);
        final boolean screenPinningActive = ActivityManagerWrapper.getInstance().isScreenPinningActive();
        boolean b6;
        boolean b7;
        if (this.mOverviewProxyService.isEnabled()) {
            final boolean b5 = recentsButtonDisabled | (true ^ QuickStepContract.isLegacyMode(this.mNavBarMode));
            b6 = b4;
            b7 = b2;
            recentsButtonDisabled = b5;
            if (screenPinningActive) {
                b6 = b4;
                b7 = b2;
                recentsButtonDisabled = b5;
                if (!QuickStepContract.isGesturalMode(this.mNavBarMode)) {
                    b6 = (b7 = false);
                    recentsButtonDisabled = b5;
                }
            }
        }
        else {
            b6 = b4;
            b7 = b2;
            if (screenPinningActive) {
                b6 = (recentsButtonDisabled = false);
                b7 = b2;
            }
        }
        final ViewGroup viewGroup = (ViewGroup)this.getCurrentView().findViewById(R$id.nav_buttons);
        if (viewGroup != null) {
            final LayoutTransition layoutTransition = viewGroup.getLayoutTransition();
            if (layoutTransition != null && !layoutTransition.getTransitionListeners().contains(this.mTransitionListener)) {
                layoutTransition.addTransitionListener((LayoutTransition$TransitionListener)this.mTransitionListener);
            }
        }
        final ButtonDispatcher backButton = this.getBackButton();
        int visibility;
        if (b6) {
            visibility = 4;
        }
        else {
            visibility = 0;
        }
        backButton.setVisibility(visibility);
        final ButtonDispatcher homeButton = this.getHomeButton();
        int visibility2;
        if (b7) {
            visibility2 = 4;
        }
        else {
            visibility2 = 0;
        }
        homeButton.setVisibility(visibility2);
        final ButtonDispatcher recentsButton = this.getRecentsButton();
        int visibility3;
        if (recentsButtonDisabled) {
            visibility3 = 4;
        }
        else {
            visibility3 = 0;
        }
        recentsButton.setVisibility(visibility3);
        final ButtonDispatcher homeHandle = this.getHomeHandle();
        int visibility4 = n;
        if (b3) {
            visibility4 = 4;
        }
        homeHandle.setVisibility(visibility4);
    }
    
    public void updatePanelSystemUiStateFlags() {
        final int displayId = super.mContext.getDisplayId();
        final StringBuilder sb = new StringBuilder();
        sb.append("Updating panel sysui state flags: panelView=");
        sb.append(this.mPanelView);
        Log.d("StatusBar/NavBarView", sb.toString());
        if (this.mPanelView != null) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Updating panel sysui state flags: fullyExpanded=");
            sb2.append(this.mPanelView.isFullyExpanded());
            sb2.append(" inQs=");
            sb2.append(this.mPanelView.isInSettings());
            Log.d("StatusBar/NavBarView", sb2.toString());
            final SysUiState mSysUiFlagContainer = this.mSysUiFlagContainer;
            mSysUiFlagContainer.setFlag(4, this.mPanelView.isFullyExpanded() && !this.mPanelView.isInSettings());
            mSysUiFlagContainer.setFlag(2048, this.mPanelView.isInSettings());
            mSysUiFlagContainer.commitUpdate(displayId);
        }
    }
    
    public void updateSlippery() {
        this.setSlippery(!this.isQuickStepSwipeUpEnabled() || (this.mPanelView.isFullyExpanded() && !this.mPanelView.isCollapsing()));
    }
    
    public void updateStates() {
        final boolean shouldShowSwipeUpUI = this.mOverviewProxyService.shouldShowSwipeUpUI();
        final NavigationBarInflaterView mNavigationInflaterView = this.mNavigationInflaterView;
        if (mNavigationInflaterView != null) {
            mNavigationInflaterView.onLikelyDefaultLayoutChange();
        }
        this.updateSlippery();
        this.reloadNavIcons();
        this.updateNavButtonIcons();
        this.setUpSwipeUpOnboarding(this.isQuickStepSwipeUpEnabled());
        WindowManagerWrapper.getInstance().setNavBarVirtualKeyHapticFeedbackEnabled(shouldShowSwipeUpUI ^ true);
        final ButtonDispatcher homeButton = this.getHomeButton();
        View$AccessibilityDelegate mQuickStepAccessibilityDelegate;
        if (shouldShowSwipeUpUI) {
            mQuickStepAccessibilityDelegate = this.mQuickStepAccessibilityDelegate;
        }
        else {
            mQuickStepAccessibilityDelegate = null;
        }
        homeButton.setAccessibilityDelegate(mQuickStepAccessibilityDelegate);
    }
    
    private class NavTransitionListener implements LayoutTransition$TransitionListener
    {
        private boolean mBackTransitioning;
        private long mDuration;
        private boolean mHomeAppearing;
        private TimeInterpolator mInterpolator;
        private long mStartDelay;
        
        public void endTransition(final LayoutTransition layoutTransition, final ViewGroup viewGroup, final View view, final int n) {
            if (view.getId() == R$id.back) {
                this.mBackTransitioning = false;
            }
            else if (view.getId() == R$id.home && n == 2) {
                this.mHomeAppearing = false;
            }
        }
        
        public void onBackAltCleared() {
            final ButtonDispatcher backButton = NavigationBarView.this.getBackButton();
            if (!this.mBackTransitioning && backButton.getVisibility() == 0 && this.mHomeAppearing && NavigationBarView.this.getHomeButton().getAlpha() == 0.0f) {
                NavigationBarView.this.getBackButton().setAlpha(0.0f);
                final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)backButton, "alpha", new float[] { 0.0f, 1.0f });
                ((ValueAnimator)ofFloat).setStartDelay(this.mStartDelay);
                ((ValueAnimator)ofFloat).setDuration(this.mDuration);
                ((ValueAnimator)ofFloat).setInterpolator(this.mInterpolator);
                ((ValueAnimator)ofFloat).start();
            }
        }
        
        public void startTransition(final LayoutTransition layoutTransition, final ViewGroup viewGroup, final View view, final int n) {
            if (view.getId() == R$id.back) {
                this.mBackTransitioning = true;
            }
            else if (view.getId() == R$id.home && n == 2) {
                this.mHomeAppearing = true;
                this.mStartDelay = layoutTransition.getStartDelay(n);
                this.mDuration = layoutTransition.getDuration(n);
                this.mInterpolator = layoutTransition.getInterpolator(n);
            }
        }
    }
    
    public interface OnVerticalChangedListener
    {
        void onVerticalChanged(final boolean p0);
    }
}
