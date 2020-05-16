// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.view.menu;

public interface MenuView
{
    void initialize(final MenuBuilder p0);
    
    public interface ItemView
    {
        MenuItemImpl getItemData();
        
        void initialize(final MenuItemImpl p0, final int p1);
        
        boolean prefersCondensedTitle();
    }
}
