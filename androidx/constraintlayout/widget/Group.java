// 
// Decompiled by Procyon v0.5.36
// 

package androidx.constraintlayout.widget;

import android.view.View;
import android.os.Build$VERSION;
import android.util.AttributeSet;
import android.content.Context;

public class Group extends ConstraintHelper
{
    public Group(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    @Override
    protected void init(final AttributeSet set) {
        super.init(set);
        super.mUseViewMeasure = false;
    }
    
    @Override
    public void updatePostLayout(final ConstraintLayout constraintLayout) {
        final ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)this.getLayoutParams();
        layoutParams.widget.setWidth(0);
        layoutParams.widget.setHeight(0);
    }
    
    @Override
    public void updatePreLayout(final ConstraintLayout constraintLayout) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        final int visibility = this.getVisibility();
        float elevation;
        if (sdk_INT >= 21) {
            elevation = this.getElevation();
        }
        else {
            elevation = 0.0f;
        }
        for (int i = 0; i < super.mCount; ++i) {
            final View viewById = constraintLayout.getViewById(super.mIds[i]);
            if (viewById != null) {
                viewById.setVisibility(visibility);
                if (elevation > 0.0f && sdk_INT >= 21) {
                    viewById.setElevation(elevation);
                }
            }
        }
    }
}
