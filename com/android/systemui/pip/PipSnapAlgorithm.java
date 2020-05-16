// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip;

import android.graphics.PointF;
import android.util.Size;
import java.io.PrintWriter;
import android.graphics.Rect;
import android.content.res.Resources;
import android.content.Context;

public class PipSnapAlgorithm
{
    private final Context mContext;
    private final float mDefaultSizePercent;
    private final float mMaxAspectRatioForMinSize;
    private final float mMinAspectRatioForMinSize;
    private int mOrientation;
    
    public PipSnapAlgorithm(final Context mContext) {
        this.mOrientation = 0;
        final Resources resources = mContext.getResources();
        this.mContext = mContext;
        this.mDefaultSizePercent = resources.getFloat(17105072);
        final float float1 = resources.getFloat(17105070);
        this.mMaxAspectRatioForMinSize = float1;
        this.mMinAspectRatioForMinSize = 1.0f / float1;
        this.onConfigurationChanged();
    }
    
    public void applySnapFraction(final Rect rect, final Rect rect2, final float n) {
        if (n < 1.0f) {
            rect.offsetTo(rect2.left + (int)(n * rect2.width()), rect2.top);
        }
        else if (n < 2.0f) {
            rect.offsetTo(rect2.right, rect2.top + (int)((n - 1.0f) * rect2.height()));
        }
        else if (n < 3.0f) {
            rect.offsetTo(rect2.left + (int)((1.0f - (n - 2.0f)) * rect2.width()), rect2.bottom);
        }
        else {
            rect.offsetTo(rect2.left, rect2.top + (int)((1.0f - (n - 3.0f)) * rect2.height()));
        }
    }
    
    public void dump(final PrintWriter printWriter, final String s) {
        final StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb.append("  ");
        final String string = sb.toString();
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(s);
        sb2.append(PipSnapAlgorithm.class.getSimpleName());
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append(string);
        sb3.append("mOrientation=");
        sb3.append(this.mOrientation);
        printWriter.println(sb3.toString());
    }
    
    public void getMovementBounds(final Rect rect, final Rect rect2, final Rect rect3, final int n) {
        rect3.set(rect2);
        rect3.right = Math.max(rect2.left, rect2.right - rect.width());
        final int max = Math.max(rect2.top, rect2.bottom - rect.height());
        rect3.bottom = max;
        rect3.bottom = max - n;
    }
    
    public Size getSizeForAspectRatio(final float n, float a, int a2, int b) {
        a2 = (int)Math.max(a, Math.min(a2, b) * this.mDefaultSizePercent);
        Label_0115: {
            if (n > this.mMinAspectRatioForMinSize) {
                a = this.mMaxAspectRatioForMinSize;
                if (n <= a) {
                    final float n2 = (float)a2;
                    a = PointF.length(a * n2, n2);
                    a2 = (int)Math.round(Math.sqrt(a * a / (n * n + 1.0f)));
                    b = Math.round(a2 * n);
                    break Label_0115;
                }
            }
            if (n <= 1.0f) {
                b = Math.round(a2 / n);
                final int n3 = a2;
                return new Size(n3, b);
            }
            b = Math.round(a2 * n);
        }
        final int n3 = b;
        b = a2;
        return new Size(n3, b);
    }
    
    public Size getSizeForAspectRatio(final Size size, final float n, final float a) {
        int n2 = (int)Math.max(a, (float)Math.min(size.getWidth(), size.getHeight()));
        int round;
        if (n <= 1.0f) {
            round = Math.round(n2 / n);
        }
        else {
            final int round2 = Math.round(n2 * n);
            round = n2;
            n2 = round2;
        }
        return new Size(n2, round);
    }
    
    public float getSnapFraction(final Rect rect, final Rect rect2) {
        final Rect rect3 = new Rect();
        this.snapRectToClosestEdge(rect, rect2, rect3);
        final float n = (rect3.left - rect2.left) / (float)rect2.width();
        final float n2 = (rect3.top - rect2.top) / (float)rect2.height();
        final int top = rect3.top;
        if (top == rect2.top) {
            return n;
        }
        if (rect3.left == rect2.right) {
            return n2 + 1.0f;
        }
        if (top == rect2.bottom) {
            return 1.0f - n + 2.0f;
        }
        return 1.0f - n2 + 3.0f;
    }
    
    public void onConfigurationChanged() {
        this.mOrientation = this.mContext.getResources().getConfiguration().orientation;
    }
    
    public void snapRectToClosestEdge(final Rect rect, final Rect rect2, final Rect rect3) {
        final int max = Math.max(rect2.left, Math.min(rect2.right, rect.left));
        final int max2 = Math.max(rect2.top, Math.min(rect2.bottom, rect.top));
        rect3.set(rect);
        final int abs = Math.abs(rect.left - rect2.left);
        final int abs2 = Math.abs(rect.top - rect2.top);
        final int abs3 = Math.abs(rect2.right - rect.left);
        final int min = Math.min(Math.min(abs, abs3), Math.min(abs2, Math.abs(rect2.bottom - rect.top)));
        if (min == abs) {
            rect3.offsetTo(rect2.left, max2);
        }
        else if (min == abs2) {
            rect3.offsetTo(max, rect2.top);
        }
        else if (min == abs3) {
            rect3.offsetTo(rect2.right, max2);
        }
        else {
            rect3.offsetTo(max, rect2.bottom);
        }
    }
}
