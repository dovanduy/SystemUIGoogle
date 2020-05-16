// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification;

import android.text.TextUtils;
import android.view.View;
import android.text.Spanned;
import android.text.Layout;
import android.widget.TextView;
import android.util.Pools$SimplePool;

public class TextViewTransformState extends TransformState
{
    private static Pools$SimplePool<TextViewTransformState> sInstancePool;
    private TextView mText;
    
    static {
        TextViewTransformState.sInstancePool = (Pools$SimplePool<TextViewTransformState>)new Pools$SimplePool(40);
    }
    
    private int getEllipsisCount() {
        final Layout layout = this.mText.getLayout();
        if (layout != null && layout.getLineCount() > 0) {
            return layout.getEllipsisCount(0);
        }
        return 0;
    }
    
    private boolean hasSameSpans(final TextViewTransformState textViewTransformState) {
        final TextView mText = this.mText;
        final boolean b = mText instanceof Spanned;
        if (b != textViewTransformState.mText instanceof Spanned) {
            return false;
        }
        if (!b) {
            return true;
        }
        final Spanned spanned = (Spanned)mText;
        final Object[] spans = spanned.getSpans(0, spanned.length(), (Class)Object.class);
        final Spanned spanned2 = (Spanned)textViewTransformState.mText;
        final Object[] spans2 = spanned2.getSpans(0, spanned2.length(), (Class)Object.class);
        if (spans.length != spans2.length) {
            return false;
        }
        for (int i = 0; i < spans.length; ++i) {
            final Object o = spans[i];
            final Object o2 = spans2[i];
            if (!o.getClass().equals(o2.getClass())) {
                return false;
            }
            if (spanned.getSpanStart(o) != spanned2.getSpanStart(o2) || spanned.getSpanEnd(o) != spanned2.getSpanEnd(o2)) {
                return false;
            }
        }
        return true;
    }
    
    public static TextViewTransformState obtain() {
        final TextViewTransformState textViewTransformState = (TextViewTransformState)TextViewTransformState.sInstancePool.acquire();
        if (textViewTransformState != null) {
            return textViewTransformState;
        }
        return new TextViewTransformState();
    }
    
    @Override
    protected int getViewHeight() {
        return this.mText.getLineHeight();
    }
    
    @Override
    protected int getViewWidth() {
        final Layout layout = this.mText.getLayout();
        if (layout != null) {
            return (int)layout.getLineWidth(0);
        }
        return super.getViewWidth();
    }
    
    @Override
    public void initFrom(final View view, final TransformInfo transformInfo) {
        super.initFrom(view, transformInfo);
        this.mText = (TextView)view;
    }
    
    @Override
    public void recycle() {
        super.recycle();
        TextViewTransformState.sInstancePool.release((Object)this);
    }
    
    @Override
    protected void reset() {
        super.reset();
        this.mText = null;
    }
    
    @Override
    protected boolean sameAs(final TransformState transformState) {
        final boolean sameAs = super.sameAs(transformState);
        boolean b = true;
        if (sameAs) {
            return true;
        }
        if (transformState instanceof TextViewTransformState) {
            final TextViewTransformState textViewTransformState = (TextViewTransformState)transformState;
            if (TextUtils.equals(textViewTransformState.mText.getText(), this.mText.getText())) {
                if (this.getEllipsisCount() != textViewTransformState.getEllipsisCount() || this.mText.getLineCount() != textViewTransformState.mText.getLineCount() || !this.hasSameSpans(textViewTransformState)) {
                    b = false;
                }
                return b;
            }
        }
        return false;
    }
    
    @Override
    protected boolean transformScale(final TransformState transformState) {
        final boolean b = transformState instanceof TextViewTransformState;
        final boolean b2 = false;
        if (!b) {
            return false;
        }
        final TextViewTransformState textViewTransformState = (TextViewTransformState)transformState;
        if (!TextUtils.equals(this.mText.getText(), textViewTransformState.mText.getText())) {
            return false;
        }
        final int lineCount = this.mText.getLineCount();
        boolean b3 = b2;
        if (lineCount == 1) {
            b3 = b2;
            if (lineCount == textViewTransformState.mText.getLineCount()) {
                b3 = b2;
                if (this.getEllipsisCount() == textViewTransformState.getEllipsisCount()) {
                    b3 = b2;
                    if (this.getViewHeight() != textViewTransformState.getViewHeight()) {
                        b3 = true;
                    }
                }
            }
        }
        return b3;
    }
}
