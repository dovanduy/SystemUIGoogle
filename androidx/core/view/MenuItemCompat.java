// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.view;

import android.graphics.PorterDuff$Mode;
import android.content.res.ColorStateList;
import android.os.Build$VERSION;
import android.util.Log;
import androidx.core.internal.view.SupportMenuItem;
import android.view.MenuItem;

public final class MenuItemCompat
{
    public static MenuItem setActionProvider(final MenuItem menuItem, final ActionProvider supportActionProvider) {
        if (menuItem instanceof SupportMenuItem) {
            return (MenuItem)((SupportMenuItem)menuItem).setSupportActionProvider(supportActionProvider);
        }
        Log.w("MenuItemCompat", "setActionProvider: item does not implement SupportMenuItem; ignoring");
        return menuItem;
    }
    
    public static void setAlphabeticShortcut(final MenuItem menuItem, final char c, final int n) {
        if (menuItem instanceof SupportMenuItem) {
            ((SupportMenuItem)menuItem).setAlphabeticShortcut(c, n);
        }
        else if (Build$VERSION.SDK_INT >= 26) {
            menuItem.setAlphabeticShortcut(c, n);
        }
    }
    
    public static void setContentDescription(final MenuItem menuItem, final CharSequence charSequence) {
        if (menuItem instanceof SupportMenuItem) {
            ((SupportMenuItem)menuItem).setContentDescription(charSequence);
        }
        else if (Build$VERSION.SDK_INT >= 26) {
            menuItem.setContentDescription(charSequence);
        }
    }
    
    public static void setIconTintList(final MenuItem menuItem, final ColorStateList list) {
        if (menuItem instanceof SupportMenuItem) {
            ((SupportMenuItem)menuItem).setIconTintList(list);
        }
        else if (Build$VERSION.SDK_INT >= 26) {
            menuItem.setIconTintList(list);
        }
    }
    
    public static void setIconTintMode(final MenuItem menuItem, final PorterDuff$Mode porterDuff$Mode) {
        if (menuItem instanceof SupportMenuItem) {
            ((SupportMenuItem)menuItem).setIconTintMode(porterDuff$Mode);
        }
        else if (Build$VERSION.SDK_INT >= 26) {
            menuItem.setIconTintMode(porterDuff$Mode);
        }
    }
    
    public static void setNumericShortcut(final MenuItem menuItem, final char c, final int n) {
        if (menuItem instanceof SupportMenuItem) {
            ((SupportMenuItem)menuItem).setNumericShortcut(c, n);
        }
        else if (Build$VERSION.SDK_INT >= 26) {
            menuItem.setNumericShortcut(c, n);
        }
    }
    
    public static void setTooltipText(final MenuItem menuItem, final CharSequence charSequence) {
        if (menuItem instanceof SupportMenuItem) {
            ((SupportMenuItem)menuItem).setTooltipText(charSequence);
        }
        else if (Build$VERSION.SDK_INT >= 26) {
            menuItem.setTooltipText(charSequence);
        }
    }
}
