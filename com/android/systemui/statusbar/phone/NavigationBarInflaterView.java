// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import java.util.Objects;
import android.util.Log;
import com.android.systemui.R$string;
import com.android.systemui.shared.system.QuickStepContract;
import android.content.res.Configuration;
import android.graphics.drawable.Icon;
import com.android.systemui.statusbar.policy.KeyButtonView;
import com.android.systemui.R$layout;
import com.android.systemui.R$id;
import android.widget.FrameLayout$LayoutParams;
import android.view.ViewGroup$LayoutParams;
import android.widget.LinearLayout$LayoutParams;
import android.widget.Space;
import android.widget.LinearLayout;
import android.view.ViewGroup;
import com.android.systemui.Dependency;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.recents.OverviewProxyService;
import android.view.View;
import android.view.LayoutInflater;
import com.android.internal.annotations.VisibleForTesting;
import android.util.SparseArray;
import android.widget.FrameLayout;

public class NavigationBarInflaterView extends FrameLayout implements ModeChangedListener
{
    private boolean mAlternativeOrder;
    @VisibleForTesting
    SparseArray<ButtonDispatcher> mButtonDispatchers;
    private String mCurrentLayout;
    protected FrameLayout mHorizontal;
    private boolean mIsVertical;
    protected LayoutInflater mLandscapeInflater;
    private View mLastLandscape;
    private View mLastPortrait;
    protected LayoutInflater mLayoutInflater;
    private int mNavBarMode;
    private OverviewProxyService mOverviewProxyService;
    private boolean mUsingCustomLayout;
    protected FrameLayout mVertical;
    
    public NavigationBarInflaterView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mNavBarMode = 0;
        this.createInflaters();
        this.mOverviewProxyService = Dependency.get(OverviewProxyService.class);
        this.mNavBarMode = Dependency.get(NavigationModeController.class).addListener((NavigationModeController.ModeChangedListener)this);
    }
    
    private void addAll(final ButtonDispatcher buttonDispatcher, final ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); ++i) {
            if (viewGroup.getChildAt(i).getId() == buttonDispatcher.getId()) {
                buttonDispatcher.addView(viewGroup.getChildAt(i));
            }
            if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                this.addAll(buttonDispatcher, (ViewGroup)viewGroup.getChildAt(i));
            }
        }
    }
    
    private void addGravitySpacer(final LinearLayout linearLayout) {
        linearLayout.addView((View)new Space(super.mContext), (ViewGroup$LayoutParams)new LinearLayout$LayoutParams(0, 0, 1.0f));
    }
    
    private void addToDispatchers(final View view) {
        final SparseArray<ButtonDispatcher> mButtonDispatchers = this.mButtonDispatchers;
        if (mButtonDispatchers != null) {
            final int indexOfKey = mButtonDispatchers.indexOfKey(view.getId());
            if (indexOfKey >= 0) {
                ((ButtonDispatcher)this.mButtonDispatchers.valueAt(indexOfKey)).addView(view);
            }
            if (view instanceof ViewGroup) {
                final ViewGroup viewGroup = (ViewGroup)view;
                for (int childCount = viewGroup.getChildCount(), i = 0; i < childCount; ++i) {
                    this.addToDispatchers(viewGroup.getChildAt(i));
                }
            }
        }
    }
    
    private View applySize(final View view, final String s, final boolean b, final boolean b2) {
        final String size = extractSize(s);
        if (size == null) {
            return view;
        }
        if (!size.contains("W") && !size.contains("A")) {
            final float float1 = Float.parseFloat(size);
            final ViewGroup$LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width *= (int)float1;
            return view;
        }
        final ReverseLinearLayout.ReverseRelativeLayout reverseRelativeLayout = new ReverseLinearLayout.ReverseRelativeLayout(super.mContext);
        final FrameLayout$LayoutParams frameLayout$LayoutParams = new FrameLayout$LayoutParams(view.getLayoutParams());
        int n;
        if (b) {
            if (b2) {
                n = 48;
            }
            else {
                n = 80;
            }
        }
        else if (b2) {
            n = 8388611;
        }
        else {
            n = 8388613;
        }
        if (size.endsWith("WC")) {
            n = 17;
        }
        else if (size.endsWith("C")) {
            n = 16;
        }
        reverseRelativeLayout.setDefaultGravity(n);
        reverseRelativeLayout.setGravity(n);
        reverseRelativeLayout.addView(view, (ViewGroup$LayoutParams)frameLayout$LayoutParams);
        if (size.contains("W")) {
            reverseRelativeLayout.setLayoutParams((ViewGroup$LayoutParams)new LinearLayout$LayoutParams(0, -1, Float.parseFloat(size.substring(0, size.indexOf("W")))));
        }
        else {
            reverseRelativeLayout.setLayoutParams((ViewGroup$LayoutParams)new LinearLayout$LayoutParams((int)convertDpToPx(super.mContext, Float.parseFloat(size.substring(0, size.indexOf("A")))), -1));
        }
        reverseRelativeLayout.setClipChildren(false);
        reverseRelativeLayout.setClipToPadding(false);
        return (View)reverseRelativeLayout;
    }
    
    private void clearAllChildren(final ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); ++i) {
            ((ViewGroup)viewGroup.getChildAt(i)).removeAllViews();
        }
    }
    
    private void clearViews() {
        if (this.mButtonDispatchers != null) {
            for (int i = 0; i < this.mButtonDispatchers.size(); ++i) {
                ((ButtonDispatcher)this.mButtonDispatchers.valueAt(i)).clear();
            }
        }
        this.clearAllChildren((ViewGroup)this.mHorizontal.findViewById(R$id.nav_buttons));
        this.clearAllChildren((ViewGroup)this.mVertical.findViewById(R$id.nav_buttons));
    }
    
    private static float convertDpToPx(final Context context, final float n) {
        return n * context.getResources().getDisplayMetrics().density;
    }
    
    private View createView(String anObject, final ViewGroup viewGroup, final LayoutInflater layoutInflater) {
        final String button = extractButton(anObject);
        if ("left".equals(button)) {
            anObject = extractButton("space");
        }
        else {
            anObject = button;
            if ("right".equals(button)) {
                anObject = extractButton("menu_ime");
            }
        }
        Object o;
        if ("home".equals(anObject)) {
            o = layoutInflater.inflate(R$layout.home, viewGroup, false);
        }
        else if ("back".equals(anObject)) {
            o = layoutInflater.inflate(R$layout.back, viewGroup, false);
        }
        else if ("recent".equals(anObject)) {
            o = layoutInflater.inflate(R$layout.recent_apps, viewGroup, false);
        }
        else if ("menu_ime".equals(anObject)) {
            o = layoutInflater.inflate(R$layout.menu_ime, viewGroup, false);
        }
        else if ("space".equals(anObject)) {
            o = layoutInflater.inflate(R$layout.nav_key_space, viewGroup, false);
        }
        else if ("clipboard".equals(anObject)) {
            o = layoutInflater.inflate(R$layout.clipboard, viewGroup, false);
        }
        else if ("contextual".equals(anObject)) {
            o = layoutInflater.inflate(R$layout.contextual, viewGroup, false);
        }
        else if ("home_handle".equals(anObject)) {
            o = layoutInflater.inflate(R$layout.home_handle, viewGroup, false);
        }
        else if ("ime_switcher".equals(anObject)) {
            o = layoutInflater.inflate(R$layout.ime_switcher, viewGroup, false);
        }
        else if (anObject.startsWith("key")) {
            final String image = extractImage(anObject);
            final int keycode = extractKeycode(anObject);
            o = layoutInflater.inflate(R$layout.custom_key, viewGroup, false);
            final KeyButtonView keyButtonView = (KeyButtonView)o;
            keyButtonView.setCode(keycode);
            if (image != null) {
                if (image.contains(":")) {
                    keyButtonView.loadAsync(Icon.createWithContentUri(image));
                }
                else if (image.contains("/")) {
                    final int index = image.indexOf(47);
                    keyButtonView.loadAsync(Icon.createWithResource(image.substring(0, index), Integer.parseInt(image.substring(index + 1))));
                }
            }
        }
        else {
            o = null;
        }
        return (View)o;
    }
    
    public static String extractButton(final String s) {
        if (!s.contains("[")) {
            return s;
        }
        return s.substring(0, s.indexOf("["));
    }
    
    public static String extractImage(final String s) {
        if (!s.contains(":")) {
            return null;
        }
        return s.substring(s.indexOf(":") + 1, s.indexOf(")"));
    }
    
    public static int extractKeycode(final String s) {
        if (!s.contains("(")) {
            return 1;
        }
        return Integer.parseInt(s.substring(s.indexOf("(") + 1, s.indexOf(":")));
    }
    
    public static String extractSize(final String s) {
        if (!s.contains("[")) {
            return null;
        }
        return s.substring(s.indexOf("[") + 1, s.indexOf("]"));
    }
    
    private void inflateButtons(final String[] array, final ViewGroup viewGroup, final boolean b, final boolean b2) {
        for (int i = 0; i < array.length; ++i) {
            this.inflateButton(array[i], viewGroup, b, b2);
        }
    }
    
    private void inflateChildren() {
        this.removeAllViews();
        this.addView((View)(this.mHorizontal = (FrameLayout)this.mLayoutInflater.inflate(R$layout.navigation_layout, (ViewGroup)this, false)));
        this.addView((View)(this.mVertical = (FrameLayout)this.mLayoutInflater.inflate(R$layout.navigation_layout_vertical, (ViewGroup)this, false)));
        this.updateAlternativeOrder();
    }
    
    private void initiallyFill(final ButtonDispatcher buttonDispatcher) {
        this.addAll(buttonDispatcher, (ViewGroup)this.mHorizontal.findViewById(R$id.ends_group));
        this.addAll(buttonDispatcher, (ViewGroup)this.mHorizontal.findViewById(R$id.center_group));
        this.addAll(buttonDispatcher, (ViewGroup)this.mVertical.findViewById(R$id.ends_group));
        this.addAll(buttonDispatcher, (ViewGroup)this.mVertical.findViewById(R$id.center_group));
    }
    
    private void updateAlternativeOrder() {
        this.updateAlternativeOrder(this.mHorizontal.findViewById(R$id.ends_group));
        this.updateAlternativeOrder(this.mHorizontal.findViewById(R$id.center_group));
        this.updateAlternativeOrder(this.mVertical.findViewById(R$id.ends_group));
        this.updateAlternativeOrder(this.mVertical.findViewById(R$id.center_group));
    }
    
    private void updateAlternativeOrder(final View view) {
        if (view instanceof ReverseLinearLayout) {
            ((ReverseLinearLayout)view).setAlternativeOrder(this.mAlternativeOrder);
        }
    }
    
    @VisibleForTesting
    void createInflaters() {
        this.mLayoutInflater = LayoutInflater.from(super.mContext);
        final Configuration configuration = new Configuration();
        configuration.setTo(super.mContext.getResources().getConfiguration());
        configuration.orientation = 2;
        this.mLandscapeInflater = LayoutInflater.from(super.mContext.createConfigurationContext(configuration));
    }
    
    protected String getDefaultLayout() {
        int n;
        if (QuickStepContract.isGesturalMode(this.mNavBarMode)) {
            n = R$string.config_navBarLayoutHandle;
        }
        else if (this.mOverviewProxyService.shouldShowSwipeUpUI()) {
            n = R$string.config_navBarLayoutQuickstep;
        }
        else {
            n = R$string.config_navBarLayout;
        }
        return this.getContext().getString(n);
    }
    
    protected View inflateButton(final String s, final ViewGroup viewGroup, final boolean b, final boolean b2) {
        LayoutInflater layoutInflater;
        if (b) {
            layoutInflater = this.mLandscapeInflater;
        }
        else {
            layoutInflater = this.mLayoutInflater;
        }
        final View view = this.createView(s, viewGroup, layoutInflater);
        if (view == null) {
            return null;
        }
        final View applySize = this.applySize(view, s, b, b2);
        viewGroup.addView(applySize);
        this.addToDispatchers(applySize);
        View view2;
        if (b) {
            view2 = this.mLastLandscape;
        }
        else {
            view2 = this.mLastPortrait;
        }
        View child;
        if (applySize instanceof ReverseLinearLayout.ReverseRelativeLayout) {
            child = ((ReverseLinearLayout.ReverseRelativeLayout)applySize).getChildAt(0);
        }
        else {
            child = applySize;
        }
        if (view2 != null) {
            child.setAccessibilityTraversalAfter(view2.getId());
        }
        if (b) {
            this.mLastLandscape = child;
        }
        else {
            this.mLastPortrait = child;
        }
        return applySize;
    }
    
    protected void inflateLayout(final String mCurrentLayout) {
        this.mCurrentLayout = mCurrentLayout;
        String defaultLayout = mCurrentLayout;
        if (mCurrentLayout == null) {
            defaultLayout = this.getDefaultLayout();
        }
        String[] array;
        if ((array = defaultLayout.split(";", 3)).length != 3) {
            Log.d("NavBarInflater", "Invalid layout.");
            array = this.getDefaultLayout().split(";", 3);
        }
        final String[] split = array[0].split(",");
        final String[] split2 = array[1].split(",");
        final String[] split3 = array[2].split(",");
        this.inflateButtons(split, (ViewGroup)this.mHorizontal.findViewById(R$id.ends_group), false, true);
        this.inflateButtons(split, (ViewGroup)this.mVertical.findViewById(R$id.ends_group), true, true);
        this.inflateButtons(split2, (ViewGroup)this.mHorizontal.findViewById(R$id.center_group), false, false);
        this.inflateButtons(split2, (ViewGroup)this.mVertical.findViewById(R$id.center_group), true, false);
        this.addGravitySpacer((LinearLayout)this.mHorizontal.findViewById(R$id.ends_group));
        this.addGravitySpacer((LinearLayout)this.mVertical.findViewById(R$id.ends_group));
        this.inflateButtons(split3, (ViewGroup)this.mHorizontal.findViewById(R$id.ends_group), false, false);
        this.inflateButtons(split3, (ViewGroup)this.mVertical.findViewById(R$id.ends_group), true, false);
        this.updateButtonDispatchersCurrentView();
    }
    
    protected void onDetachedFromWindow() {
        Dependency.get(NavigationModeController.class).removeListener((NavigationModeController.ModeChangedListener)this);
        super.onDetachedFromWindow();
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.inflateChildren();
        this.clearViews();
        this.inflateLayout(this.getDefaultLayout());
    }
    
    public void onLikelyDefaultLayoutChange() {
        if (this.mUsingCustomLayout) {
            return;
        }
        final String defaultLayout = this.getDefaultLayout();
        if (!Objects.equals(this.mCurrentLayout, defaultLayout)) {
            this.clearViews();
            this.inflateLayout(defaultLayout);
        }
    }
    
    public void onNavigationModeChanged(final int mNavBarMode) {
        this.mNavBarMode = mNavBarMode;
        this.onLikelyDefaultLayoutChange();
    }
    
    void setAlternativeOrder(final boolean mAlternativeOrder) {
        if (mAlternativeOrder != this.mAlternativeOrder) {
            this.mAlternativeOrder = mAlternativeOrder;
            this.updateAlternativeOrder();
        }
    }
    
    public void setButtonDispatchers(final SparseArray<ButtonDispatcher> mButtonDispatchers) {
        this.mButtonDispatchers = mButtonDispatchers;
        for (int i = 0; i < mButtonDispatchers.size(); ++i) {
            this.initiallyFill((ButtonDispatcher)mButtonDispatchers.valueAt(i));
        }
    }
    
    void setVertical(final boolean mIsVertical) {
        if (mIsVertical != this.mIsVertical) {
            this.mIsVertical = mIsVertical;
        }
    }
    
    void updateButtonDispatchersCurrentView() {
        if (this.mButtonDispatchers != null) {
            FrameLayout currentView;
            if (this.mIsVertical) {
                currentView = this.mVertical;
            }
            else {
                currentView = this.mHorizontal;
            }
            for (int i = 0; i < this.mButtonDispatchers.size(); ++i) {
                ((ButtonDispatcher)this.mButtonDispatchers.valueAt(i)).setCurrentView((View)currentView);
            }
        }
    }
}
