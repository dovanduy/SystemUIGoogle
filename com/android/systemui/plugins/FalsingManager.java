// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import android.net.Uri;
import android.view.MotionEvent;
import java.io.PrintWriter;
import com.android.systemui.plugins.annotations.ProvidesInterface;

@ProvidesInterface(version = 3)
public interface FalsingManager
{
    public static final int VERSION = 3;
    
    void cleanup();
    
    void dump(final PrintWriter p0);
    
    boolean isClassifierEnabled();
    
    boolean isFalseTouch();
    
    boolean isReportingEnabled();
    
    boolean isUnlockingDisabled();
    
    void onAffordanceSwipingAborted();
    
    void onAffordanceSwipingStarted(final boolean p0);
    
    void onBouncerHidden();
    
    void onBouncerShown();
    
    void onCameraHintStarted();
    
    void onCameraOn();
    
    void onExpansionFromPulseStopped();
    
    void onLeftAffordanceHintStarted();
    
    void onLeftAffordanceOn();
    
    void onNotificationActive();
    
    void onNotificationDismissed();
    
    void onNotificationDoubleTap(final boolean p0, final float p1, final float p2);
    
    void onNotificatonStartDismissing();
    
    void onNotificatonStartDraggingDown();
    
    void onNotificatonStopDismissing();
    
    void onNotificatonStopDraggingDown();
    
    void onQsDown();
    
    void onScreenOff();
    
    void onScreenOnFromTouch();
    
    void onScreenTurningOn();
    
    void onStartExpandingFromPulse();
    
    void onSuccessfulUnlock();
    
    void onTouchEvent(final MotionEvent p0, final int p1, final int p2);
    
    void onTrackingStarted(final boolean p0);
    
    void onTrackingStopped();
    
    void onUnlockHintStarted();
    
    Uri reportRejectedTouch();
    
    void setNotificationExpanded();
    
    void setQsExpanded(final boolean p0);
    
    void setShowingAod(final boolean p0);
    
    boolean shouldEnforceBouncer();
}
