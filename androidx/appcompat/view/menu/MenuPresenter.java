// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.view.menu;

import android.content.Context;

public interface MenuPresenter
{
    boolean collapseItemActionView(final MenuBuilder p0, final MenuItemImpl p1);
    
    boolean expandItemActionView(final MenuBuilder p0, final MenuItemImpl p1);
    
    boolean flagActionItems();
    
    void initForMenu(final Context p0, final MenuBuilder p1);
    
    void onCloseMenu(final MenuBuilder p0, final boolean p1);
    
    boolean onSubMenuSelected(final SubMenuBuilder p0);
    
    void setCallback(final Callback p0);
    
    void updateMenuView(final boolean p0);
    
    public interface Callback
    {
        void onCloseMenu(final MenuBuilder p0, final boolean p1);
        
        boolean onOpenSubMenu(final MenuBuilder p0);
    }
}
