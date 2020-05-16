// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.view.menu;

import android.widget.FrameLayout;
import android.view.ActionProvider$VisibilityListener;
import android.view.MenuItem$OnMenuItemClickListener;
import android.view.MenuItem$OnActionExpandListener;
import android.util.Log;
import android.view.CollapsibleActionView;
import android.os.Build$VERSION;
import android.view.SubMenu;
import android.view.ContextMenu$ContextMenuInfo;
import android.content.Intent;
import android.graphics.PorterDuff$Mode;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ActionProvider;
import android.content.Context;
import androidx.core.internal.view.SupportMenuItem;
import java.lang.reflect.Method;
import android.view.MenuItem;

public class MenuItemWrapperICS extends BaseMenuWrapper implements MenuItem
{
    private Method mSetExclusiveCheckableMethod;
    private final SupportMenuItem mWrappedObject;
    
    public MenuItemWrapperICS(final Context context, final SupportMenuItem mWrappedObject) {
        super(context);
        if (mWrappedObject != null) {
            this.mWrappedObject = mWrappedObject;
            return;
        }
        throw new IllegalArgumentException("Wrapped Object can not be null.");
    }
    
    public boolean collapseActionView() {
        return this.mWrappedObject.collapseActionView();
    }
    
    public boolean expandActionView() {
        return this.mWrappedObject.expandActionView();
    }
    
    public ActionProvider getActionProvider() {
        final androidx.core.view.ActionProvider supportActionProvider = this.mWrappedObject.getSupportActionProvider();
        if (supportActionProvider instanceof ActionProviderWrapper) {
            return ((ActionProviderWrapper)supportActionProvider).mInner;
        }
        return null;
    }
    
    public View getActionView() {
        View view2;
        final View view = view2 = this.mWrappedObject.getActionView();
        if (view instanceof CollapsibleActionViewWrapper) {
            view2 = ((CollapsibleActionViewWrapper)view).getWrappedView();
        }
        return view2;
    }
    
    public int getAlphabeticModifiers() {
        return this.mWrappedObject.getAlphabeticModifiers();
    }
    
    public char getAlphabeticShortcut() {
        return ((MenuItem)this.mWrappedObject).getAlphabeticShortcut();
    }
    
    public CharSequence getContentDescription() {
        return this.mWrappedObject.getContentDescription();
    }
    
    public int getGroupId() {
        return ((MenuItem)this.mWrappedObject).getGroupId();
    }
    
    public Drawable getIcon() {
        return ((MenuItem)this.mWrappedObject).getIcon();
    }
    
    public ColorStateList getIconTintList() {
        return this.mWrappedObject.getIconTintList();
    }
    
    public PorterDuff$Mode getIconTintMode() {
        return this.mWrappedObject.getIconTintMode();
    }
    
    public Intent getIntent() {
        return ((MenuItem)this.mWrappedObject).getIntent();
    }
    
    public int getItemId() {
        return ((MenuItem)this.mWrappedObject).getItemId();
    }
    
    public ContextMenu$ContextMenuInfo getMenuInfo() {
        return ((MenuItem)this.mWrappedObject).getMenuInfo();
    }
    
    public int getNumericModifiers() {
        return this.mWrappedObject.getNumericModifiers();
    }
    
    public char getNumericShortcut() {
        return ((MenuItem)this.mWrappedObject).getNumericShortcut();
    }
    
    public int getOrder() {
        return ((MenuItem)this.mWrappedObject).getOrder();
    }
    
    public SubMenu getSubMenu() {
        return this.getSubMenuWrapper(((MenuItem)this.mWrappedObject).getSubMenu());
    }
    
    public CharSequence getTitle() {
        return ((MenuItem)this.mWrappedObject).getTitle();
    }
    
    public CharSequence getTitleCondensed() {
        return ((MenuItem)this.mWrappedObject).getTitleCondensed();
    }
    
    public CharSequence getTooltipText() {
        return this.mWrappedObject.getTooltipText();
    }
    
    public boolean hasSubMenu() {
        return ((MenuItem)this.mWrappedObject).hasSubMenu();
    }
    
    public boolean isActionViewExpanded() {
        return this.mWrappedObject.isActionViewExpanded();
    }
    
    public boolean isCheckable() {
        return ((MenuItem)this.mWrappedObject).isCheckable();
    }
    
    public boolean isChecked() {
        return ((MenuItem)this.mWrappedObject).isChecked();
    }
    
    public boolean isEnabled() {
        return ((MenuItem)this.mWrappedObject).isEnabled();
    }
    
    public boolean isVisible() {
        return ((MenuItem)this.mWrappedObject).isVisible();
    }
    
    public MenuItem setActionProvider(final ActionProvider actionProvider) {
        androidx.core.view.ActionProvider supportActionProvider;
        if (Build$VERSION.SDK_INT >= 16) {
            supportActionProvider = new ActionProviderWrapperJB(super.mContext, actionProvider);
        }
        else {
            supportActionProvider = new ActionProviderWrapper(super.mContext, actionProvider);
        }
        final SupportMenuItem mWrappedObject = this.mWrappedObject;
        if (actionProvider == null) {
            supportActionProvider = null;
        }
        mWrappedObject.setSupportActionProvider(supportActionProvider);
        return (MenuItem)this;
    }
    
    public MenuItem setActionView(final int actionView) {
        this.mWrappedObject.setActionView(actionView);
        final View actionView2 = this.mWrappedObject.getActionView();
        if (actionView2 instanceof CollapsibleActionView) {
            this.mWrappedObject.setActionView((View)new CollapsibleActionViewWrapper(actionView2));
        }
        return (MenuItem)this;
    }
    
    public MenuItem setActionView(final View view) {
        Object actionView = view;
        if (view instanceof CollapsibleActionView) {
            actionView = new CollapsibleActionViewWrapper(view);
        }
        this.mWrappedObject.setActionView((View)actionView);
        return (MenuItem)this;
    }
    
    public MenuItem setAlphabeticShortcut(final char alphabeticShortcut) {
        ((MenuItem)this.mWrappedObject).setAlphabeticShortcut(alphabeticShortcut);
        return (MenuItem)this;
    }
    
    public MenuItem setAlphabeticShortcut(final char c, final int n) {
        this.mWrappedObject.setAlphabeticShortcut(c, n);
        return (MenuItem)this;
    }
    
    public MenuItem setCheckable(final boolean checkable) {
        ((MenuItem)this.mWrappedObject).setCheckable(checkable);
        return (MenuItem)this;
    }
    
    public MenuItem setChecked(final boolean checked) {
        ((MenuItem)this.mWrappedObject).setChecked(checked);
        return (MenuItem)this;
    }
    
    public MenuItem setContentDescription(final CharSequence contentDescription) {
        this.mWrappedObject.setContentDescription(contentDescription);
        return (MenuItem)this;
    }
    
    public MenuItem setEnabled(final boolean enabled) {
        ((MenuItem)this.mWrappedObject).setEnabled(enabled);
        return (MenuItem)this;
    }
    
    public void setExclusiveCheckable(final boolean b) {
        try {
            if (this.mSetExclusiveCheckableMethod == null) {
                this.mSetExclusiveCheckableMethod = this.mWrappedObject.getClass().getDeclaredMethod("setExclusiveCheckable", Boolean.TYPE);
            }
            this.mSetExclusiveCheckableMethod.invoke(this.mWrappedObject, b);
        }
        catch (Exception ex) {
            Log.w("MenuItemWrapper", "Error while calling setExclusiveCheckable", (Throwable)ex);
        }
    }
    
    public MenuItem setIcon(final int icon) {
        ((MenuItem)this.mWrappedObject).setIcon(icon);
        return (MenuItem)this;
    }
    
    public MenuItem setIcon(final Drawable icon) {
        ((MenuItem)this.mWrappedObject).setIcon(icon);
        return (MenuItem)this;
    }
    
    public MenuItem setIconTintList(final ColorStateList iconTintList) {
        this.mWrappedObject.setIconTintList(iconTintList);
        return (MenuItem)this;
    }
    
    public MenuItem setIconTintMode(final PorterDuff$Mode iconTintMode) {
        this.mWrappedObject.setIconTintMode(iconTintMode);
        return (MenuItem)this;
    }
    
    public MenuItem setIntent(final Intent intent) {
        ((MenuItem)this.mWrappedObject).setIntent(intent);
        return (MenuItem)this;
    }
    
    public MenuItem setNumericShortcut(final char numericShortcut) {
        ((MenuItem)this.mWrappedObject).setNumericShortcut(numericShortcut);
        return (MenuItem)this;
    }
    
    public MenuItem setNumericShortcut(final char c, final int n) {
        this.mWrappedObject.setNumericShortcut(c, n);
        return (MenuItem)this;
    }
    
    public MenuItem setOnActionExpandListener(final MenuItem$OnActionExpandListener menuItem$OnActionExpandListener) {
        final SupportMenuItem mWrappedObject = this.mWrappedObject;
        Object onActionExpandListener;
        if (menuItem$OnActionExpandListener != null) {
            onActionExpandListener = new OnActionExpandListenerWrapper(menuItem$OnActionExpandListener);
        }
        else {
            onActionExpandListener = null;
        }
        ((MenuItem)mWrappedObject).setOnActionExpandListener((MenuItem$OnActionExpandListener)onActionExpandListener);
        return (MenuItem)this;
    }
    
    public MenuItem setOnMenuItemClickListener(final MenuItem$OnMenuItemClickListener menuItem$OnMenuItemClickListener) {
        final SupportMenuItem mWrappedObject = this.mWrappedObject;
        Object onMenuItemClickListener;
        if (menuItem$OnMenuItemClickListener != null) {
            onMenuItemClickListener = new OnMenuItemClickListenerWrapper(menuItem$OnMenuItemClickListener);
        }
        else {
            onMenuItemClickListener = null;
        }
        ((MenuItem)mWrappedObject).setOnMenuItemClickListener((MenuItem$OnMenuItemClickListener)onMenuItemClickListener);
        return (MenuItem)this;
    }
    
    public MenuItem setShortcut(final char c, final char c2) {
        ((MenuItem)this.mWrappedObject).setShortcut(c, c2);
        return (MenuItem)this;
    }
    
    public MenuItem setShortcut(final char c, final char c2, final int n, final int n2) {
        this.mWrappedObject.setShortcut(c, c2, n, n2);
        return (MenuItem)this;
    }
    
    public void setShowAsAction(final int showAsAction) {
        this.mWrappedObject.setShowAsAction(showAsAction);
    }
    
    public MenuItem setShowAsActionFlags(final int showAsActionFlags) {
        this.mWrappedObject.setShowAsActionFlags(showAsActionFlags);
        return (MenuItem)this;
    }
    
    public MenuItem setTitle(final int title) {
        ((MenuItem)this.mWrappedObject).setTitle(title);
        return (MenuItem)this;
    }
    
    public MenuItem setTitle(final CharSequence title) {
        ((MenuItem)this.mWrappedObject).setTitle(title);
        return (MenuItem)this;
    }
    
    public MenuItem setTitleCondensed(final CharSequence titleCondensed) {
        ((MenuItem)this.mWrappedObject).setTitleCondensed(titleCondensed);
        return (MenuItem)this;
    }
    
    public MenuItem setTooltipText(final CharSequence tooltipText) {
        this.mWrappedObject.setTooltipText(tooltipText);
        return (MenuItem)this;
    }
    
    public MenuItem setVisible(final boolean visible) {
        return ((MenuItem)this.mWrappedObject).setVisible(visible);
    }
    
    private class ActionProviderWrapper extends ActionProvider
    {
        final android.view.ActionProvider mInner;
        
        ActionProviderWrapper(final Context context, final android.view.ActionProvider mInner) {
            super(context);
            this.mInner = mInner;
        }
        
        @Override
        public boolean hasSubMenu() {
            return this.mInner.hasSubMenu();
        }
        
        @Override
        public View onCreateActionView() {
            return this.mInner.onCreateActionView();
        }
        
        @Override
        public boolean onPerformDefaultAction() {
            return this.mInner.onPerformDefaultAction();
        }
        
        @Override
        public void onPrepareSubMenu(final SubMenu subMenu) {
            this.mInner.onPrepareSubMenu(MenuItemWrapperICS.this.getSubMenuWrapper(subMenu));
        }
    }
    
    private class ActionProviderWrapperJB extends ActionProviderWrapper implements ActionProvider$VisibilityListener
    {
        private VisibilityListener mListener;
        
        ActionProviderWrapperJB(final MenuItemWrapperICS menuItemWrapperICS, final Context context, final android.view.ActionProvider actionProvider) {
            menuItemWrapperICS.super(context, actionProvider);
        }
        
        public boolean isVisible() {
            return super.mInner.isVisible();
        }
        
        public void onActionProviderVisibilityChanged(final boolean b) {
            final VisibilityListener mListener = this.mListener;
            if (mListener != null) {
                mListener.onActionProviderVisibilityChanged(b);
            }
        }
        
        public View onCreateActionView(final MenuItem menuItem) {
            return super.mInner.onCreateActionView(menuItem);
        }
        
        public boolean overridesItemVisibility() {
            return super.mInner.overridesItemVisibility();
        }
        
        public void refreshVisibility() {
            super.mInner.refreshVisibility();
        }
        
        public void setVisibilityListener(final VisibilityListener mListener) {
            this.mListener = mListener;
            final android.view.ActionProvider mInner = super.mInner;
            Object visibilityListener;
            if (mListener != null) {
                visibilityListener = this;
            }
            else {
                visibilityListener = null;
            }
            mInner.setVisibilityListener((ActionProvider$VisibilityListener)visibilityListener);
        }
    }
    
    static class CollapsibleActionViewWrapper extends FrameLayout implements CollapsibleActionView
    {
        final android.view.CollapsibleActionView mWrappedView;
        
        CollapsibleActionViewWrapper(final View view) {
            super(view.getContext());
            this.mWrappedView = (android.view.CollapsibleActionView)view;
            this.addView(view);
        }
        
        View getWrappedView() {
            return (View)this.mWrappedView;
        }
        
        public void onActionViewCollapsed() {
            this.mWrappedView.onActionViewCollapsed();
        }
        
        public void onActionViewExpanded() {
            this.mWrappedView.onActionViewExpanded();
        }
    }
    
    private class OnActionExpandListenerWrapper implements MenuItem$OnActionExpandListener
    {
        private final MenuItem$OnActionExpandListener mObject;
        
        OnActionExpandListenerWrapper(final MenuItem$OnActionExpandListener mObject) {
            this.mObject = mObject;
        }
        
        public boolean onMenuItemActionCollapse(final MenuItem menuItem) {
            return this.mObject.onMenuItemActionCollapse(MenuItemWrapperICS.this.getMenuItemWrapper(menuItem));
        }
        
        public boolean onMenuItemActionExpand(final MenuItem menuItem) {
            return this.mObject.onMenuItemActionExpand(MenuItemWrapperICS.this.getMenuItemWrapper(menuItem));
        }
    }
    
    private class OnMenuItemClickListenerWrapper implements MenuItem$OnMenuItemClickListener
    {
        private final MenuItem$OnMenuItemClickListener mObject;
        
        OnMenuItemClickListenerWrapper(final MenuItem$OnMenuItemClickListener mObject) {
            this.mObject = mObject;
        }
        
        public boolean onMenuItemClick(final MenuItem menuItem) {
            return this.mObject.onMenuItemClick(MenuItemWrapperICS.this.getMenuItemWrapper(menuItem));
        }
    }
}
