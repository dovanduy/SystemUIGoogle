// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.view;

import android.view.MotionEvent;

public final class MotionEventCompat
{
    public static boolean isFromSource(final MotionEvent motionEvent, final int n) {
        return (motionEvent.getSource() & n) == n;
    }
}
