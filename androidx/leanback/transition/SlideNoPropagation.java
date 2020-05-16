// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.transition;

import android.transition.TransitionPropagation;
import android.util.AttributeSet;
import android.content.Context;
import android.transition.Slide;

public class SlideNoPropagation extends Slide
{
    public SlideNoPropagation() {
    }
    
    public SlideNoPropagation(final int n) {
        super(n);
    }
    
    public SlideNoPropagation(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public void setSlideEdge(final int slideEdge) {
        super.setSlideEdge(slideEdge);
        this.setPropagation((TransitionPropagation)null);
    }
}
