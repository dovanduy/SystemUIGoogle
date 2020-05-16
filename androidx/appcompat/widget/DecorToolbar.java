// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.widget;

import androidx.core.view.ViewPropertyAnimatorCompat;
import android.view.Window$Callback;
import androidx.appcompat.view.menu.MenuPresenter;
import android.view.Menu;
import android.view.ViewGroup;
import android.content.Context;

public interface DecorToolbar
{
    boolean canShowOverflowMenu();
    
    void collapseActionView();
    
    void dismissPopupMenus();
    
    Context getContext();
    
    int getDisplayOptions();
    
    int getNavigationMode();
    
    ViewGroup getViewGroup();
    
    boolean hasExpandedActionView();
    
    boolean hideOverflowMenu();
    
    void initIndeterminateProgress();
    
    void initProgress();
    
    boolean isOverflowMenuShowPending();
    
    boolean isOverflowMenuShowing();
    
    void setCollapsible(final boolean p0);
    
    void setDisplayOptions(final int p0);
    
    void setEmbeddedTabView(final ScrollingTabContainerView p0);
    
    void setHomeButtonEnabled(final boolean p0);
    
    void setMenu(final Menu p0, final MenuPresenter.Callback p1);
    
    void setMenuPrepared();
    
    void setVisibility(final int p0);
    
    void setWindowCallback(final Window$Callback p0);
    
    void setWindowTitle(final CharSequence p0);
    
    ViewPropertyAnimatorCompat setupAnimatorToVisibility(final int p0, final long p1);
    
    boolean showOverflowMenu();
}
