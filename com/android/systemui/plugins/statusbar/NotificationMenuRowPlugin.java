// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins.statusbar;

import android.view.MotionEvent;
import android.graphics.Point;
import android.view.View;
import java.util.ArrayList;
import android.content.Context;
import android.service.notification.StatusBarNotification;
import android.view.ViewGroup;
import com.android.systemui.plugins.annotations.ProvidesInterface;
import com.android.systemui.plugins.annotations.DependsOn;
import com.android.systemui.plugins.annotations.Dependencies;
import com.android.systemui.plugins.Plugin;

@Dependencies({ @DependsOn(target = OnMenuEventListener.class), @DependsOn(target = MenuItem.class), @DependsOn(target = NotificationSwipeActionHelper.class), @DependsOn(target = NotificationSwipeActionHelper.SnoozeOption.class) })
@ProvidesInterface(action = "com.android.systemui.action.PLUGIN_NOTIFICATION_MENU_ROW", version = 5)
public interface NotificationMenuRowPlugin extends Plugin
{
    public static final String ACTION = "com.android.systemui.action.PLUGIN_NOTIFICATION_MENU_ROW";
    public static final int VERSION = 5;
    
    boolean canBeDismissed();
    
    void createMenu(final ViewGroup p0, final StatusBarNotification p1);
    
    MenuItem getAppOpsMenuItem(final Context p0);
    
    MenuItem getLongpressMenuItem(final Context p0);
    
    ArrayList<MenuItem> getMenuItems(final Context p0);
    
    int getMenuSnapTarget();
    
    View getMenuView();
    
    default Point getRevealAnimationOrigin() {
        return new Point(0, 0);
    }
    
    MenuItem getSnoozeMenuItem(final Context p0);
    
    boolean isMenuVisible();
    
    boolean isSnappedAndOnSameSide();
    
    boolean isSwipedEnoughToShowMenu();
    
    boolean isTowardsMenu(final float p0);
    
    boolean isWithinSnapMenuThreshold();
    
    default MenuItem menuItemToExposeOnSnap() {
        return null;
    }
    
    default void onConfigurationChanged() {
    }
    
    void onDismiss();
    
    default boolean onInterceptTouchEvent(final View view, final MotionEvent motionEvent) {
        return false;
    }
    
    void onNotificationUpdated(final StatusBarNotification p0);
    
    void onParentHeightUpdate();
    
    void onParentTranslationUpdate(final float p0);
    
    void onSnapClosed();
    
    void onSnapOpen();
    
    void onTouchEnd();
    
    void onTouchMove(final float p0);
    
    void onTouchStart();
    
    void resetMenu();
    
    void setAppName(final String p0);
    
    default void setDismissRtl(final boolean b) {
    }
    
    void setMenuClickListener(final OnMenuEventListener p0);
    
    void setMenuItems(final ArrayList<MenuItem> p0);
    
    default boolean shouldShowGutsOnSnapOpen() {
        return false;
    }
    
    boolean shouldShowMenu();
    
    boolean shouldSnapBack();
    
    default boolean shouldUseDefaultMenuItems() {
        return false;
    }
    
    @ProvidesInterface(version = 1)
    public interface MenuItem
    {
        public static final int VERSION = 1;
        
        String getContentDescription();
        
        View getGutsView();
        
        View getMenuView();
    }
    
    @ProvidesInterface(version = 1)
    public interface OnMenuEventListener
    {
        public static final int VERSION = 1;
        
        void onMenuClicked(final View p0, final int p1, final int p2, final MenuItem p3);
        
        void onMenuReset(final View p0);
        
        void onMenuShown(final View p0);
    }
}
