// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.graphics.Rect;
import android.view.View;
import android.view.KeyEvent;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View$OnKeyListener;
import android.widget.FrameLayout;

public class BrowseFrameLayout extends FrameLayout
{
    private OnFocusSearchListener mListener;
    private OnChildFocusListener mOnChildFocusListener;
    private View$OnKeyListener mOnDispatchKeyListener;
    
    public BrowseFrameLayout(final Context context) {
        this(context, null, 0);
    }
    
    public BrowseFrameLayout(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public BrowseFrameLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
    }
    
    public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
        final boolean dispatchKeyEvent = super.dispatchKeyEvent(keyEvent);
        final View$OnKeyListener mOnDispatchKeyListener = this.mOnDispatchKeyListener;
        if (mOnDispatchKeyListener != null && !dispatchKeyEvent) {
            return mOnDispatchKeyListener.onKey(this.getRootView(), keyEvent.getKeyCode(), keyEvent);
        }
        return dispatchKeyEvent;
    }
    
    public View focusSearch(final View view, final int n) {
        final OnFocusSearchListener mListener = this.mListener;
        if (mListener != null) {
            final View onFocusSearch = mListener.onFocusSearch(view, n);
            if (onFocusSearch != null) {
                return onFocusSearch;
            }
        }
        return super.focusSearch(view, n);
    }
    
    protected boolean onRequestFocusInDescendants(final int n, final Rect rect) {
        final OnChildFocusListener mOnChildFocusListener = this.mOnChildFocusListener;
        return (mOnChildFocusListener != null && mOnChildFocusListener.onRequestFocusInDescendants(n, rect)) || super.onRequestFocusInDescendants(n, rect);
    }
    
    public void requestChildFocus(final View view, final View view2) {
        final OnChildFocusListener mOnChildFocusListener = this.mOnChildFocusListener;
        if (mOnChildFocusListener != null) {
            mOnChildFocusListener.onRequestChildFocus(view, view2);
        }
        super.requestChildFocus(view, view2);
    }
    
    public interface OnChildFocusListener
    {
        void onRequestChildFocus(final View p0, final View p1);
        
        boolean onRequestFocusInDescendants(final int p0, final Rect p1);
    }
    
    public interface OnFocusSearchListener
    {
        View onFocusSearch(final View p0, final int p1);
    }
}
