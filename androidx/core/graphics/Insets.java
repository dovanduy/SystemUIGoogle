// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.graphics;

import android.graphics.Rect;

public final class Insets
{
    public static final Insets NONE;
    public final int bottom;
    public final int left;
    public final int right;
    public final int top;
    
    static {
        NONE = new Insets(0, 0, 0, 0);
    }
    
    private Insets(final int left, final int top, final int right, final int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }
    
    public static Insets of(final int n, final int n2, final int n3, final int n4) {
        if (n == 0 && n2 == 0 && n3 == 0 && n4 == 0) {
            return Insets.NONE;
        }
        return new Insets(n, n2, n3, n4);
    }
    
    public static Insets of(final Rect rect) {
        return of(rect.left, rect.top, rect.right, rect.bottom);
    }
    
    public static Insets toCompatInsets(final android.graphics.Insets insets) {
        return of(insets.left, insets.top, insets.right, insets.bottom);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && Insets.class == o.getClass()) {
            final Insets insets = (Insets)o;
            return this.bottom == insets.bottom && this.left == insets.left && this.right == insets.right && this.top == insets.top;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return ((this.left * 31 + this.top) * 31 + this.right) * 31 + this.bottom;
    }
    
    public android.graphics.Insets toPlatformInsets() {
        return android.graphics.Insets.of(this.left, this.top, this.right, this.bottom);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Insets{left=");
        sb.append(this.left);
        sb.append(", top=");
        sb.append(this.top);
        sb.append(", right=");
        sb.append(this.right);
        sb.append(", bottom=");
        sb.append(this.bottom);
        sb.append('}');
        return sb.toString();
    }
}
