// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.view.View;
import android.widget.ImageView;
import androidx.leanback.R$id;
import android.content.res.TypedArray;
import androidx.leanback.R$styleable;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.RelativeLayout;

class GuidanceStylingRelativeLayout extends RelativeLayout
{
    private float mTitleKeylinePercent;
    
    public GuidanceStylingRelativeLayout(final Context context) {
        this(context, null);
    }
    
    public GuidanceStylingRelativeLayout(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public GuidanceStylingRelativeLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mTitleKeylinePercent = getKeyLinePercent(context);
    }
    
    public static float getKeyLinePercent(final Context context) {
        final TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(R$styleable.LeanbackGuidedStepTheme);
        final float float1 = obtainStyledAttributes.getFloat(R$styleable.LeanbackGuidedStepTheme_guidedStepKeyline, 40.0f);
        obtainStyledAttributes.recycle();
        return float1;
    }
    
    protected void onLayout(final boolean b, int n, int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        final View viewById = this.getRootView().findViewById(R$id.guidance_title);
        final View viewById2 = this.getRootView().findViewById(R$id.guidance_breadcrumb);
        final View viewById3 = this.getRootView().findViewById(R$id.guidance_description);
        final ImageView imageView = (ImageView)this.getRootView().findViewById(R$id.guidance_icon);
        n2 = (int)(this.getMeasuredHeight() * this.mTitleKeylinePercent / 100.0f);
        if (viewById != null && viewById.getParent() == this) {
            n = n2 - viewById.getBaseline() - viewById2.getMeasuredHeight() - viewById.getPaddingTop() - viewById2.getTop();
            if (viewById2 != null && viewById2.getParent() == this) {
                viewById2.offsetTopAndBottom(n);
            }
            viewById.offsetTopAndBottom(n);
            if (viewById3 != null && viewById3.getParent() == this) {
                viewById3.offsetTopAndBottom(n);
            }
        }
        if (imageView != null && imageView.getParent() == this && imageView.getDrawable() != null) {
            imageView.offsetTopAndBottom(n2 - imageView.getMeasuredHeight() / 2);
        }
    }
}
