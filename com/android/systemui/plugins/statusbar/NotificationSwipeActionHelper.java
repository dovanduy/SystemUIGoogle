// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins.statusbar;

import android.service.notification.SnoozeCriterion;
import android.view.accessibility.AccessibilityNodeInfo$AccessibilityAction;
import android.service.notification.StatusBarNotification;
import android.view.MotionEvent;
import android.view.View;
import com.android.systemui.plugins.annotations.ProvidesInterface;
import com.android.systemui.plugins.annotations.DependsOn;

@DependsOn(target = SnoozeOption.class)
@ProvidesInterface(version = 1)
public interface NotificationSwipeActionHelper
{
    public static final String ACTION = "com.android.systemui.action.PLUGIN_NOTIFICATION_SWIPE_ACTION";
    public static final int VERSION = 1;
    
    void dismiss(final View p0, final float p1);
    
    float getMinDismissVelocity();
    
    boolean isDismissGesture(final MotionEvent p0);
    
    boolean isFalseGesture(final MotionEvent p0);
    
    void snapOpen(final View p0, final int p1, final float p2);
    
    void snooze(final StatusBarNotification p0, final int p1);
    
    void snooze(final StatusBarNotification p0, final SnoozeOption p1);
    
    boolean swipedFarEnough(final float p0, final float p1);
    
    boolean swipedFastEnough(final float p0, final float p1);
    
    @ProvidesInterface(version = 2)
    public interface SnoozeOption
    {
        public static final int VERSION = 2;
        
        AccessibilityNodeInfo$AccessibilityAction getAccessibilityAction();
        
        CharSequence getConfirmation();
        
        CharSequence getDescription();
        
        int getMinutesToSnoozeFor();
        
        SnoozeCriterion getSnoozeCriterion();
    }
}
