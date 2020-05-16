// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.system;

import android.graphics.Rect;
import android.view.IPinnedStackController;
import android.view.DisplayInfo;
import android.content.ComponentName;
import java.util.Iterator;
import android.content.pm.ParceledListSlice;
import java.util.ArrayList;
import java.util.List;
import android.view.IPinnedStackListener$Stub;

public class PinnedStackListenerForwarder extends IPinnedStackListener$Stub
{
    private List<PinnedStackListener> mListeners;
    
    public PinnedStackListenerForwarder() {
        this.mListeners = new ArrayList<PinnedStackListener>();
    }
    
    public void addListener(final PinnedStackListener pinnedStackListener) {
        this.mListeners.add(pinnedStackListener);
    }
    
    public void onActionsChanged(final ParceledListSlice parceledListSlice) {
        final Iterator<PinnedStackListener> iterator = this.mListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onActionsChanged(parceledListSlice);
        }
    }
    
    public void onActivityHidden(final ComponentName componentName) {
        final Iterator<PinnedStackListener> iterator = this.mListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onActivityHidden(componentName);
        }
    }
    
    public void onAspectRatioChanged(final float n) {
        final Iterator<PinnedStackListener> iterator = this.mListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onAspectRatioChanged(n);
        }
    }
    
    public void onConfigurationChanged() {
        final Iterator<PinnedStackListener> iterator = this.mListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onConfigurationChanged();
        }
    }
    
    public void onDisplayInfoChanged(final DisplayInfo displayInfo) {
        final Iterator<PinnedStackListener> iterator = this.mListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onDisplayInfoChanged(displayInfo);
        }
    }
    
    public void onImeVisibilityChanged(final boolean b, final int n) {
        final Iterator<PinnedStackListener> iterator = this.mListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onImeVisibilityChanged(b, n);
        }
    }
    
    public void onListenerRegistered(final IPinnedStackController pinnedStackController) {
        final Iterator<PinnedStackListener> iterator = this.mListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onListenerRegistered(pinnedStackController);
        }
    }
    
    public void onMovementBoundsChanged(final Rect rect, final boolean b) {
        final Iterator<PinnedStackListener> iterator = this.mListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onMovementBoundsChanged(rect, b);
        }
    }
    
    public static class PinnedStackListener
    {
        public void onActionsChanged(final ParceledListSlice parceledListSlice) {
        }
        
        public void onActivityHidden(final ComponentName componentName) {
        }
        
        public void onAspectRatioChanged(final float n) {
        }
        
        public void onConfigurationChanged() {
        }
        
        public void onDisplayInfoChanged(final DisplayInfo displayInfo) {
        }
        
        public void onImeVisibilityChanged(final boolean b, final int n) {
        }
        
        public void onListenerRegistered(final IPinnedStackController pinnedStackController) {
        }
        
        public void onMovementBoundsChanged(final Rect rect, final boolean b) {
        }
    }
}
