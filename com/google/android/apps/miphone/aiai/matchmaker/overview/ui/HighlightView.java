// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.ui;

import android.graphics.Canvas;
import java.util.Iterator;
import android.view.MotionEvent;
import android.view.View;
import android.os.Build$VERSION;
import android.graphics.Paint$Style;
import java.util.ArrayList;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View$OnTouchListener;
import android.graphics.RectF;
import java.util.List;
import android.graphics.Paint;
import android.widget.FrameLayout;

public final class HighlightView extends FrameLayout
{
    private final int backgroundColor;
    private final float highlightCornerRadius;
    private final Paint highlightPaint;
    private final List<RectF> highlights;
    private final List<View$OnTouchListener> listeners;
    
    public HighlightView(final Context context) {
        this(context, null);
    }
    
    public HighlightView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public HighlightView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public HighlightView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.highlights = new ArrayList<RectF>();
        this.listeners = new ArrayList<View$OnTouchListener>();
        this.backgroundColor = context.getColor(R$color.default_gleam_background_color);
        this.highlightCornerRadius = (float)context.getResources().getDimensionPixelSize(R$dimen.highlight_corner_radius);
        (this.highlightPaint = new Paint()).setStyle(Paint$Style.FILL);
        if (Build$VERSION.SDK_INT >= 29) {
            this.highlightPaint.setColor(context.getColor(R$color.default_gleam_highlight_color));
        }
        this.setWillNotDraw(false);
        this.setOnTouchListener((View$OnTouchListener)new View$OnTouchListener() {
            public boolean onTouch(final View view, final MotionEvent motionEvent) {
                final Iterator<View$OnTouchListener> iterator = HighlightView.this.listeners.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().onTouch(view, motionEvent)) {
                        return true;
                    }
                }
                return false;
            }
        });
    }
    
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(this.backgroundColor);
        for (int i = 0; i < this.highlights.size(); ++i) {
            final RectF rectF = this.highlights.get(i);
            final float highlightCornerRadius = this.highlightCornerRadius;
            canvas.drawRoundRect(rectF, highlightCornerRadius, highlightCornerRadius, this.highlightPaint);
        }
    }
}
