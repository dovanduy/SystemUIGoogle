// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.view.menu;

import java.util.ArrayList;
import android.view.ViewGroup;
import android.view.View;
import android.view.LayoutInflater;
import android.content.Context;

public abstract class BaseMenuPresenter implements MenuPresenter
{
    private Callback mCallback;
    protected Context mContext;
    private int mItemLayoutRes;
    protected MenuBuilder mMenu;
    private int mMenuLayoutRes;
    protected MenuView mMenuView;
    protected Context mSystemContext;
    protected LayoutInflater mSystemInflater;
    
    public BaseMenuPresenter(final Context mSystemContext, final int mMenuLayoutRes, final int mItemLayoutRes) {
        this.mSystemContext = mSystemContext;
        this.mSystemInflater = LayoutInflater.from(mSystemContext);
        this.mMenuLayoutRes = mMenuLayoutRes;
        this.mItemLayoutRes = mItemLayoutRes;
    }
    
    protected void addItemView(final View view, final int n) {
        final ViewGroup viewGroup = (ViewGroup)view.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(view);
        }
        ((ViewGroup)this.mMenuView).addView(view, n);
    }
    
    public abstract void bindItemView(final MenuItemImpl p0, final MenuView.ItemView p1);
    
    @Override
    public boolean collapseItemActionView(final MenuBuilder menuBuilder, final MenuItemImpl menuItemImpl) {
        return false;
    }
    
    public MenuView.ItemView createItemView(final ViewGroup viewGroup) {
        return (MenuView.ItemView)this.mSystemInflater.inflate(this.mItemLayoutRes, viewGroup, false);
    }
    
    @Override
    public boolean expandItemActionView(final MenuBuilder menuBuilder, final MenuItemImpl menuItemImpl) {
        return false;
    }
    
    protected boolean filterLeftoverView(final ViewGroup viewGroup, final int n) {
        viewGroup.removeViewAt(n);
        return true;
    }
    
    public Callback getCallback() {
        return this.mCallback;
    }
    
    public View getItemView(final MenuItemImpl menuItemImpl, final View view, final ViewGroup viewGroup) {
        MenuView.ItemView itemView;
        if (view instanceof MenuView.ItemView) {
            itemView = (MenuView.ItemView)view;
        }
        else {
            itemView = this.createItemView(viewGroup);
        }
        this.bindItemView(menuItemImpl, itemView);
        return (View)itemView;
    }
    
    public MenuView getMenuView(final ViewGroup viewGroup) {
        if (this.mMenuView == null) {
            (this.mMenuView = (MenuView)this.mSystemInflater.inflate(this.mMenuLayoutRes, viewGroup, false)).initialize(this.mMenu);
            this.updateMenuView(true);
        }
        return this.mMenuView;
    }
    
    @Override
    public void initForMenu(final Context mContext, final MenuBuilder mMenu) {
        LayoutInflater.from(this.mContext = mContext);
        this.mMenu = mMenu;
    }
    
    @Override
    public void onCloseMenu(final MenuBuilder menuBuilder, final boolean b) {
        final Callback mCallback = this.mCallback;
        if (mCallback != null) {
            mCallback.onCloseMenu(menuBuilder, b);
        }
    }
    
    @Override
    public boolean onSubMenuSelected(final SubMenuBuilder subMenuBuilder) {
        final Callback mCallback = this.mCallback;
        return mCallback != null && mCallback.onOpenSubMenu(subMenuBuilder);
    }
    
    @Override
    public void setCallback(final Callback mCallback) {
        this.mCallback = mCallback;
    }
    
    public void setId(final int n) {
    }
    
    public abstract boolean shouldIncludeItem(final int p0, final MenuItemImpl p1);
    
    @Override
    public void updateMenuView(final boolean b) {
        final ViewGroup viewGroup = (ViewGroup)this.mMenuView;
        if (viewGroup == null) {
            return;
        }
        final MenuBuilder mMenu = this.mMenu;
        int i = 0;
        if (mMenu != null) {
            mMenu.flagActionItems();
            final ArrayList<MenuItemImpl> visibleItems = this.mMenu.getVisibleItems();
            int n;
            for (int size = visibleItems.size(), j = i = 0; j < size; ++j, i = n) {
                final MenuItemImpl menuItemImpl = visibleItems.get(j);
                n = i;
                if (this.shouldIncludeItem(i, menuItemImpl)) {
                    final View child = viewGroup.getChildAt(i);
                    MenuItemImpl itemData;
                    if (child instanceof MenuView.ItemView) {
                        itemData = ((MenuView.ItemView)child).getItemData();
                    }
                    else {
                        itemData = null;
                    }
                    final View itemView = this.getItemView(menuItemImpl, child, viewGroup);
                    if (menuItemImpl != itemData) {
                        itemView.setPressed(false);
                        itemView.jumpDrawablesToCurrentState();
                    }
                    if (itemView != child) {
                        this.addItemView(itemView, i);
                    }
                    n = i + 1;
                }
            }
        }
        while (i < viewGroup.getChildCount()) {
            if (!this.filterLeftoverView(viewGroup, i)) {
                ++i;
            }
        }
    }
}
