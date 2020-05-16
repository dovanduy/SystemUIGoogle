// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.content.Context;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.Gefingerpoken;

public class HeadsUpTouchHelper implements Gefingerpoken
{
    private Callback mCallback;
    private boolean mCollapseSnoozes;
    private HeadsUpManagerPhone mHeadsUpManager;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private NotificationPanelViewController mPanel;
    private ExpandableNotificationRow mPickedChild;
    private float mTouchSlop;
    private boolean mTouchingHeadsUpView;
    private boolean mTrackingHeadsUp;
    private int mTrackingPointer;
    
    public HeadsUpTouchHelper(final HeadsUpManagerPhone mHeadsUpManager, final Callback mCallback, final NotificationPanelViewController mPanel) {
        this.mHeadsUpManager = mHeadsUpManager;
        this.mCallback = mCallback;
        this.mPanel = mPanel;
        this.mTouchSlop = (float)ViewConfiguration.get(mCallback.getContext()).getScaledTouchSlop();
    }
    
    private void endMotion() {
        this.mTrackingPointer = -1;
        this.mPickedChild = null;
        this.mTouchingHeadsUpView = false;
    }
    
    private void setTrackingHeadsUp(final boolean b) {
        this.mTrackingHeadsUp = b;
        this.mHeadsUpManager.setTrackingHeadsUp(b);
        final NotificationPanelViewController mPanel = this.mPanel;
        ExpandableNotificationRow mPickedChild;
        if (b) {
            mPickedChild = this.mPickedChild;
        }
        else {
            mPickedChild = null;
        }
        mPanel.setTrackedHeadsUp(mPickedChild);
    }
    
    public boolean isTrackingHeadsUp() {
        return this.mTrackingHeadsUp;
    }
    
    public void notifyFling(final boolean b) {
        if (b && this.mCollapseSnoozes) {
            this.mHeadsUpManager.snooze();
        }
        this.mCollapseSnoozes = false;
    }
    
    @Override
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        final boolean mTouchingHeadsUpView = this.mTouchingHeadsUpView;
        boolean mCollapseSnoozes = false;
        if (!mTouchingHeadsUpView && motionEvent.getActionMasked() != 0) {
            return false;
        }
        int pointerIndex;
        if ((pointerIndex = motionEvent.findPointerIndex(this.mTrackingPointer)) < 0) {
            this.mTrackingPointer = motionEvent.getPointerId(0);
            pointerIndex = 0;
        }
        final float x = motionEvent.getX(pointerIndex);
        final float y = motionEvent.getY(pointerIndex);
        final int actionMasked = motionEvent.getActionMasked();
        final boolean b = true;
        int n = 1;
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked != 2) {
                    if (actionMasked != 3) {
                        if (actionMasked != 6) {
                            return false;
                        }
                        final int pointerId = motionEvent.getPointerId(motionEvent.getActionIndex());
                        if (this.mTrackingPointer == pointerId) {
                            if (motionEvent.getPointerId(0) != pointerId) {
                                n = 0;
                            }
                            this.mTrackingPointer = motionEvent.getPointerId(n);
                            this.mInitialTouchX = motionEvent.getX(n);
                            this.mInitialTouchY = motionEvent.getY(n);
                            return false;
                        }
                        return false;
                    }
                }
                else {
                    final float n2 = y - this.mInitialTouchY;
                    if (this.mTouchingHeadsUpView && Math.abs(n2) > this.mTouchSlop && Math.abs(n2) > Math.abs(x - this.mInitialTouchX)) {
                        this.setTrackingHeadsUp(true);
                        float panelScrimMinFraction = 0.0f;
                        if (n2 < 0.0f) {
                            mCollapseSnoozes = true;
                        }
                        this.mCollapseSnoozes = mCollapseSnoozes;
                        this.mInitialTouchX = x;
                        this.mInitialTouchY = y;
                        final int n3 = (int)(this.mPickedChild.getActualHeight() + this.mPickedChild.getTranslationY());
                        final float n4 = (float)this.mPanel.getMaxPanelHeight();
                        final NotificationPanelViewController mPanel = this.mPanel;
                        if (n4 > 0.0f) {
                            panelScrimMinFraction = n3 / n4;
                        }
                        mPanel.setPanelScrimMinFraction(panelScrimMinFraction);
                        this.mPanel.startExpandMotion(x, y, true, (float)n3);
                        this.mPanel.startExpandingFromPeek();
                        this.mHeadsUpManager.unpinAll(true);
                        this.mPanel.clearNotificationEffects();
                        this.endMotion();
                        return true;
                    }
                    return false;
                }
            }
            final ExpandableNotificationRow mPickedChild = this.mPickedChild;
            if (mPickedChild != null && this.mTouchingHeadsUpView && this.mHeadsUpManager.shouldSwallowClick(mPickedChild.getEntry().getSbn().getKey())) {
                this.endMotion();
                return true;
            }
            this.endMotion();
        }
        else {
            this.mInitialTouchY = y;
            this.mInitialTouchX = x;
            this.setTrackingHeadsUp(false);
            final ExpandableView childAtRawPosition = this.mCallback.getChildAtRawPosition(x, y);
            this.mTouchingHeadsUpView = false;
            if (childAtRawPosition instanceof ExpandableNotificationRow) {
                final ExpandableNotificationRow mPickedChild2 = (ExpandableNotificationRow)childAtRawPosition;
                final boolean mTouchingHeadsUpView2 = !this.mCallback.isExpanded() && mPickedChild2.isHeadsUp() && mPickedChild2.isPinned() && b;
                this.mTouchingHeadsUpView = mTouchingHeadsUpView2;
                if (mTouchingHeadsUpView2) {
                    this.mPickedChild = mPickedChild2;
                }
            }
            else if (childAtRawPosition == null && !this.mCallback.isExpanded()) {
                final NotificationEntry topEntry = this.mHeadsUpManager.getTopEntry();
                if (topEntry != null && topEntry.isRowPinned()) {
                    this.mPickedChild = topEntry.getRow();
                    this.mTouchingHeadsUpView = true;
                }
            }
        }
        return false;
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        if (!this.mTrackingHeadsUp) {
            return false;
        }
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 1 || actionMasked == 3) {
            this.endMotion();
            this.setTrackingHeadsUp(false);
        }
        return true;
    }
    
    public interface Callback
    {
        ExpandableView getChildAtRawPosition(final float p0, final float p1);
        
        Context getContext();
        
        boolean isExpanded();
    }
}
