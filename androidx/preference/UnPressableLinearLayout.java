// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import android.util.AttributeSet;
import android.content.Context;
import android.widget.LinearLayout;

public class UnPressableLinearLayout extends LinearLayout
{
    public UnPressableLinearLayout(final Context context) {
        this(context, null);
    }
    
    public UnPressableLinearLayout(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    protected void dispatchSetPressed(final boolean b) {
    }
}
