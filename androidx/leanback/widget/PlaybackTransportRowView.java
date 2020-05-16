// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import androidx.leanback.R$id;
import android.graphics.Rect;
import android.view.FocusFinder;
import android.view.ViewGroup;
import android.view.View;
import android.view.KeyEvent;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.LinearLayout;

public class PlaybackTransportRowView extends LinearLayout
{
    private OnUnhandledKeyListener mOnUnhandledKeyListener;
    
    public PlaybackTransportRowView(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public PlaybackTransportRowView(final Context context, final AttributeSet set, final int n) {
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
    
    public View focusSearch(final View view, final int n) {
        if (view != null) {
            if (n == 33) {
                for (int i = this.indexOfChild(this.getFocusedChild()) - 1; i >= 0; --i) {
                    final View child = this.getChildAt(i);
                    if (child.hasFocusable()) {
                        return child;
                    }
                }
            }
            else if (n == 130) {
                int indexOfChild = this.indexOfChild(this.getFocusedChild());
                while (++indexOfChild < this.getChildCount()) {
                    final View child2 = this.getChildAt(indexOfChild);
                    if (child2.hasFocusable()) {
                        return child2;
                    }
                }
            }
            else if ((n == 17 || n == 66) && this.getFocusedChild() instanceof ViewGroup) {
                return FocusFinder.getInstance().findNextFocus((ViewGroup)this.getFocusedChild(), view, n);
            }
        }
        return super.focusSearch(view, n);
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    protected boolean onRequestFocusInDescendants(final int n, final Rect rect) {
        final View focus = this.findFocus();
        if (focus != null && focus.requestFocus(n, rect)) {
            return true;
        }
        final View viewById = this.findViewById(R$id.playback_progress);
        return (viewById != null && viewById.isFocusable() && viewById.requestFocus(n, rect)) || super.onRequestFocusInDescendants(n, rect);
    }
    
    public interface OnUnhandledKeyListener
    {
        boolean onUnhandledKey(final KeyEvent p0);
    }
}
