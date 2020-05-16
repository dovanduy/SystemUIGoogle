// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.internal.view;

import androidx.core.view.ActionProvider;
import android.graphics.PorterDuff$Mode;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.MenuItem;

public interface SupportMenuItem extends MenuItem
{
    boolean collapseActionView();
    
    boolean expandActionView();
    
    View getActionView();
    
    int getAlphabeticModifiers();
    
    CharSequence getContentDescription();
    
    ColorStateList getIconTintList();
    
    PorterDuff$Mode getIconTintMode();
    
    int getNumericModifiers();
    
    ActionProvider getSupportActionProvider();
    
    CharSequence getTooltipText();
    
    boolean isActionViewExpanded();
    
    MenuItem setActionView(final int p0);
    
    MenuItem setActionView(final View p0);
    
    MenuItem setAlphabeticShortcut(final char p0, final int p1);
    
    SupportMenuItem setContentDescription(final CharSequence p0);
    
    MenuItem setIconTintList(final ColorStateList p0);
    
    MenuItem setIconTintMode(final PorterDuff$Mode p0);
    
    MenuItem setNumericShortcut(final char p0, final int p1);
    
    MenuItem setShortcut(final char p0, final char p1, final int p2, final int p3);
    
    void setShowAsAction(final int p0);
    
    MenuItem setShowAsActionFlags(final int p0);
    
    SupportMenuItem setSupportActionProvider(final ActionProvider p0);
    
    SupportMenuItem setTooltipText(final CharSequence p0);
}
