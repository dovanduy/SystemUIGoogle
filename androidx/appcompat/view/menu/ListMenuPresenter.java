// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.view.menu;

import java.util.ArrayList;
import android.widget.BaseAdapter;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.view.ContextThemeWrapper;
import androidx.appcompat.R$layout;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.view.LayoutInflater;
import android.content.Context;
import android.widget.AdapterView$OnItemClickListener;

public class ListMenuPresenter implements MenuPresenter, AdapterView$OnItemClickListener
{
    MenuAdapter mAdapter;
    private Callback mCallback;
    Context mContext;
    LayoutInflater mInflater;
    int mItemIndexOffset;
    int mItemLayoutRes;
    MenuBuilder mMenu;
    ExpandedMenuView mMenuView;
    int mThemeRes;
    
    public ListMenuPresenter(final int mItemLayoutRes, final int mThemeRes) {
        this.mItemLayoutRes = mItemLayoutRes;
        this.mThemeRes = mThemeRes;
    }
    
    public ListMenuPresenter(final Context mContext, final int n) {
        this(n, 0);
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
    }
    
    @Override
    public boolean collapseItemActionView(final MenuBuilder menuBuilder, final MenuItemImpl menuItemImpl) {
        return false;
    }
    
    @Override
    public boolean expandItemActionView(final MenuBuilder menuBuilder, final MenuItemImpl menuItemImpl) {
        return false;
    }
    
    @Override
    public boolean flagActionItems() {
        return false;
    }
    
    public ListAdapter getAdapter() {
        if (this.mAdapter == null) {
            this.mAdapter = new MenuAdapter();
        }
        return (ListAdapter)this.mAdapter;
    }
    
    public MenuView getMenuView(final ViewGroup viewGroup) {
        if (this.mMenuView == null) {
            this.mMenuView = (ExpandedMenuView)this.mInflater.inflate(R$layout.abc_expanded_menu_layout, viewGroup, false);
            if (this.mAdapter == null) {
                this.mAdapter = new MenuAdapter();
            }
            this.mMenuView.setAdapter((ListAdapter)this.mAdapter);
            this.mMenuView.setOnItemClickListener((AdapterView$OnItemClickListener)this);
        }
        return this.mMenuView;
    }
    
    @Override
    public void initForMenu(final Context mContext, final MenuBuilder mMenu) {
        if (this.mThemeRes != 0) {
            final ContextThemeWrapper mContext2 = new ContextThemeWrapper(mContext, this.mThemeRes);
            this.mContext = (Context)mContext2;
            this.mInflater = LayoutInflater.from((Context)mContext2);
        }
        else if (this.mContext != null) {
            this.mContext = mContext;
            if (this.mInflater == null) {
                this.mInflater = LayoutInflater.from(mContext);
            }
        }
        this.mMenu = mMenu;
        final MenuAdapter mAdapter = this.mAdapter;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
    
    @Override
    public void onCloseMenu(final MenuBuilder menuBuilder, final boolean b) {
        final Callback mCallback = this.mCallback;
        if (mCallback != null) {
            mCallback.onCloseMenu(menuBuilder, b);
        }
    }
    
    public void onItemClick(final AdapterView<?> adapterView, final View view, final int n, final long n2) {
        this.mMenu.performItemAction((MenuItem)this.mAdapter.getItem(n), this, 0);
    }
    
    @Override
    public boolean onSubMenuSelected(final SubMenuBuilder subMenuBuilder) {
        if (!subMenuBuilder.hasVisibleItems()) {
            return false;
        }
        new MenuDialogHelper(subMenuBuilder).show(null);
        final Callback mCallback = this.mCallback;
        if (mCallback != null) {
            mCallback.onOpenSubMenu(subMenuBuilder);
        }
        return true;
    }
    
    @Override
    public void setCallback(final Callback mCallback) {
        this.mCallback = mCallback;
    }
    
    @Override
    public void updateMenuView(final boolean b) {
        final MenuAdapter mAdapter = this.mAdapter;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
    
    private class MenuAdapter extends BaseAdapter
    {
        private int mExpandedIndex;
        
        public MenuAdapter() {
            this.mExpandedIndex = -1;
            this.findExpandedIndex();
        }
        
        void findExpandedIndex() {
            final MenuItemImpl expandedItem = ListMenuPresenter.this.mMenu.getExpandedItem();
            if (expandedItem != null) {
                final ArrayList<MenuItemImpl> nonActionItems = ListMenuPresenter.this.mMenu.getNonActionItems();
                for (int size = nonActionItems.size(), i = 0; i < size; ++i) {
                    if (nonActionItems.get(i) == expandedItem) {
                        this.mExpandedIndex = i;
                        return;
                    }
                }
            }
            this.mExpandedIndex = -1;
        }
        
        public int getCount() {
            final int n = ListMenuPresenter.this.mMenu.getNonActionItems().size() - ListMenuPresenter.this.mItemIndexOffset;
            if (this.mExpandedIndex < 0) {
                return n;
            }
            return n - 1;
        }
        
        public MenuItemImpl getItem(int index) {
            final ArrayList<MenuItemImpl> nonActionItems = ListMenuPresenter.this.mMenu.getNonActionItems();
            final int n = index + ListMenuPresenter.this.mItemIndexOffset;
            final int mExpandedIndex = this.mExpandedIndex;
            index = n;
            if (mExpandedIndex >= 0 && (index = n) >= mExpandedIndex) {
                index = n + 1;
            }
            return nonActionItems.get(index);
        }
        
        public long getItemId(final int n) {
            return n;
        }
        
        public View getView(final int n, final View view, final ViewGroup viewGroup) {
            View inflate = view;
            if (view == null) {
                final ListMenuPresenter this$0 = ListMenuPresenter.this;
                inflate = this$0.mInflater.inflate(this$0.mItemLayoutRes, viewGroup, false);
            }
            ((MenuView.ItemView)inflate).initialize(this.getItem(n), 0);
            return inflate;
        }
        
        public void notifyDataSetChanged() {
            this.findExpandedIndex();
            super.notifyDataSetChanged();
        }
    }
}
