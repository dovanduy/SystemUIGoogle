// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.view.accessibility.AccessibilityManager;
import android.content.Context;
import java.util.ArrayList;
import android.view.accessibility.AccessibilityManager$TouchExplorationStateChangeListener;
import android.view.accessibility.AccessibilityManager$AccessibilityStateChangeListener;

public class AccessibilityController implements AccessibilityManager$AccessibilityStateChangeListener, AccessibilityManager$TouchExplorationStateChangeListener
{
    private boolean mAccessibilityEnabled;
    private final ArrayList<AccessibilityStateChangedCallback> mChangeCallbacks;
    private boolean mTouchExplorationEnabled;
    
    public AccessibilityController(final Context context) {
        this.mChangeCallbacks = new ArrayList<AccessibilityStateChangedCallback>();
        final AccessibilityManager accessibilityManager = (AccessibilityManager)context.getSystemService("accessibility");
        accessibilityManager.addTouchExplorationStateChangeListener((AccessibilityManager$TouchExplorationStateChangeListener)this);
        accessibilityManager.addAccessibilityStateChangeListener((AccessibilityManager$AccessibilityStateChangeListener)this);
        this.mAccessibilityEnabled = accessibilityManager.isEnabled();
        this.mTouchExplorationEnabled = accessibilityManager.isTouchExplorationEnabled();
    }
    
    private void fireChanged() {
        for (int size = this.mChangeCallbacks.size(), i = 0; i < size; ++i) {
            this.mChangeCallbacks.get(i).onStateChanged(this.mAccessibilityEnabled, this.mTouchExplorationEnabled);
        }
    }
    
    public void addStateChangedCallback(final AccessibilityStateChangedCallback e) {
        this.mChangeCallbacks.add(e);
        e.onStateChanged(this.mAccessibilityEnabled, this.mTouchExplorationEnabled);
    }
    
    public boolean isAccessibilityEnabled() {
        return this.mAccessibilityEnabled;
    }
    
    public void onAccessibilityStateChanged(final boolean mAccessibilityEnabled) {
        this.mAccessibilityEnabled = mAccessibilityEnabled;
        this.fireChanged();
    }
    
    public void onTouchExplorationStateChanged(final boolean mTouchExplorationEnabled) {
        this.mTouchExplorationEnabled = mTouchExplorationEnabled;
        this.fireChanged();
    }
    
    public void removeStateChangedCallback(final AccessibilityStateChangedCallback o) {
        this.mChangeCallbacks.remove(o);
    }
    
    public interface AccessibilityStateChangedCallback
    {
        void onStateChanged(final boolean p0, final boolean p1);
    }
}
