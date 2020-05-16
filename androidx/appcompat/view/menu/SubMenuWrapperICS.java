// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.view.menu;

import android.view.View;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import androidx.core.internal.view.SupportMenu;
import android.content.Context;
import androidx.core.internal.view.SupportSubMenu;
import android.view.SubMenu;

class SubMenuWrapperICS extends MenuWrapperICS implements SubMenu
{
    private final SupportSubMenu mSubMenu;
    
    SubMenuWrapperICS(final Context context, final SupportSubMenu mSubMenu) {
        super(context, mSubMenu);
        this.mSubMenu = mSubMenu;
    }
    
    public void clearHeader() {
        ((SubMenu)this.mSubMenu).clearHeader();
    }
    
    public MenuItem getItem() {
        return this.getMenuItemWrapper(((SubMenu)this.mSubMenu).getItem());
    }
    
    public SubMenu setHeaderIcon(final int headerIcon) {
        ((SubMenu)this.mSubMenu).setHeaderIcon(headerIcon);
        return (SubMenu)this;
    }
    
    public SubMenu setHeaderIcon(final Drawable headerIcon) {
        ((SubMenu)this.mSubMenu).setHeaderIcon(headerIcon);
        return (SubMenu)this;
    }
    
    public SubMenu setHeaderTitle(final int headerTitle) {
        ((SubMenu)this.mSubMenu).setHeaderTitle(headerTitle);
        return (SubMenu)this;
    }
    
    public SubMenu setHeaderTitle(final CharSequence headerTitle) {
        ((SubMenu)this.mSubMenu).setHeaderTitle(headerTitle);
        return (SubMenu)this;
    }
    
    public SubMenu setHeaderView(final View headerView) {
        ((SubMenu)this.mSubMenu).setHeaderView(headerView);
        return (SubMenu)this;
    }
    
    public SubMenu setIcon(final int icon) {
        ((SubMenu)this.mSubMenu).setIcon(icon);
        return (SubMenu)this;
    }
    
    public SubMenu setIcon(final Drawable icon) {
        ((SubMenu)this.mSubMenu).setIcon(icon);
        return (SubMenu)this;
    }
}
