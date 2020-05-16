// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.view.menu;

import android.view.SubMenu;
import androidx.core.internal.view.SupportSubMenu;
import android.view.MenuItem;
import androidx.core.internal.view.SupportMenuItem;
import androidx.collection.SimpleArrayMap;
import android.content.Context;

abstract class BaseMenuWrapper
{
    final Context mContext;
    private SimpleArrayMap<SupportMenuItem, MenuItem> mMenuItems;
    private SimpleArrayMap<SupportSubMenu, SubMenu> mSubMenus;
    
    BaseMenuWrapper(final Context mContext) {
        this.mContext = mContext;
    }
    
    final MenuItem getMenuItemWrapper(MenuItem menuItem) {
        Object o = menuItem;
        if (menuItem instanceof SupportMenuItem) {
            final SupportMenuItem supportMenuItem = (SupportMenuItem)menuItem;
            if (this.mMenuItems == null) {
                this.mMenuItems = new SimpleArrayMap<SupportMenuItem, MenuItem>();
            }
            menuItem = this.mMenuItems.get(menuItem);
            if ((o = menuItem) == null) {
                o = new MenuItemWrapperICS(this.mContext, supportMenuItem);
                this.mMenuItems.put(supportMenuItem, (MenuItem)o);
            }
        }
        return (MenuItem)o;
    }
    
    final SubMenu getSubMenuWrapper(SubMenu o) {
        if (o instanceof SupportSubMenu) {
            final SupportSubMenu supportSubMenu = (SupportSubMenu)o;
            if (this.mSubMenus == null) {
                this.mSubMenus = new SimpleArrayMap<SupportSubMenu, SubMenu>();
            }
            if ((o = this.mSubMenus.get(supportSubMenu)) == null) {
                o = new SubMenuWrapperICS(this.mContext, supportSubMenu);
                this.mSubMenus.put(supportSubMenu, (SubMenu)o);
            }
            return (SubMenu)o;
        }
        return (SubMenu)o;
    }
    
    final void internalClear() {
        final SimpleArrayMap<SupportMenuItem, MenuItem> mMenuItems = this.mMenuItems;
        if (mMenuItems != null) {
            mMenuItems.clear();
        }
        final SimpleArrayMap<SupportSubMenu, SubMenu> mSubMenus = this.mSubMenus;
        if (mSubMenus != null) {
            mSubMenus.clear();
        }
    }
    
    final void internalRemoveGroup(final int n) {
        if (this.mMenuItems == null) {
            return;
        }
        int n2;
        for (int i = 0; i < this.mMenuItems.size(); i = n2 + 1) {
            n2 = i;
            if (((MenuItem)this.mMenuItems.keyAt(i)).getGroupId() == n) {
                this.mMenuItems.removeAt(i);
                n2 = i - 1;
            }
        }
    }
    
    final void internalRemoveItem(final int n) {
        if (this.mMenuItems == null) {
            return;
        }
        for (int i = 0; i < this.mMenuItems.size(); ++i) {
            if (((MenuItem)this.mMenuItems.keyAt(i)).getItemId() == n) {
                this.mMenuItems.removeAt(i);
                break;
            }
        }
    }
}
