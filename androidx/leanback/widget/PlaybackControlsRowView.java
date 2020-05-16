// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.view.View;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.LinearLayout;

class PlaybackControlsRowView extends LinearLayout
{
    private OnUnhandledKeyListener mOnUnhandledKeyListener;
    
    public PlaybackControlsRowView(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public PlaybackControlsRowView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
    }
    
    public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
        final boolean dispatchKeyEvent = super.dispatchKeyEvent(keyEvent);
        boolean b = true;
        if (dispatchKeyEvent) {
            return true;
        }
        final OnUnhandledKeyListener mOnUnhandledKeyListener = this.mOnUnhandledKeyListener;
        if (mOnUnhandledKeyListener == null || !mOnUnhandledKeyListener.onUnhandledKey(keyEvent)) {
            b = false;
        }
        return b;
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    protected boolean onRequestFocusInDescendants(final int n, final Rect rect) {
        final View focus = this.findFocus();
        return (focus != null && focus.requestFocus(n, rect)) || super.onRequestFocusInDescendants(n, rect);
    }
    
    public interface OnUnhandledKeyListener
    {
        boolean onUnhandledKey(final KeyEvent p0);
    }
}
