// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.view.accessibility;

import android.os.Bundle;
import android.view.View;
import android.text.style.ClickableSpan;

public final class AccessibilityClickableSpanCompat extends ClickableSpan
{
    private final int mClickableSpanActionId;
    private final AccessibilityNodeInfoCompat mNodeInfoCompat;
    private final int mOriginalClickableSpanId;
    
    public AccessibilityClickableSpanCompat(final int mOriginalClickableSpanId, final AccessibilityNodeInfoCompat mNodeInfoCompat, final int mClickableSpanActionId) {
        this.mOriginalClickableSpanId = mOriginalClickableSpanId;
        this.mNodeInfoCompat = mNodeInfoCompat;
        this.mClickableSpanActionId = mClickableSpanActionId;
    }
    
    public void onClick(final View view) {
        final Bundle bundle = new Bundle();
        bundle.putInt("ACCESSIBILITY_CLICKABLE_SPAN_ID", this.mOriginalClickableSpanId);
        this.mNodeInfoCompat.performAction(this.mClickableSpanActionId, bundle);
    }
}
