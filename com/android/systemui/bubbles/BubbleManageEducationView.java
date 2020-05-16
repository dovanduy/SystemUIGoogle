// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.bubbles;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.Shape;
import android.graphics.drawable.ShapeDrawable;
import com.android.systemui.recents.TriangleShape;
import com.android.systemui.R$dimen;
import com.android.internal.util.ContrastColorUtil;
import android.widget.TextView;
import com.android.systemui.R$id;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

public class BubbleManageEducationView extends LinearLayout
{
    private View mManageView;
    private View mPointerView;
    
    public BubbleManageEducationView(final Context context) {
        this(context, null);
    }
    
    public BubbleManageEducationView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public BubbleManageEducationView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public BubbleManageEducationView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
    }
    
    public int getManageViewHeight() {
        return this.mManageView.getHeight();
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mManageView = this.findViewById(R$id.manage_education_view);
        final TypedArray obtainStyledAttributes = super.mContext.obtainStyledAttributes(new int[] { 16843829, 16842809 });
        final int color = obtainStyledAttributes.getColor(0, -16777216);
        final int color2 = obtainStyledAttributes.getColor(1, -1);
        obtainStyledAttributes.recycle();
        ((TextView)this.findViewById(R$id.user_education_description)).setTextColor(ContrastColorUtil.ensureTextContrast(color2, color, true));
        final Resources resources = this.getResources();
        final ShapeDrawable background = new ShapeDrawable((Shape)TriangleShape.create((float)resources.getDimensionPixelSize(R$dimen.bubble_pointer_width), (float)resources.getDimensionPixelSize(R$dimen.bubble_pointer_height), false));
        background.setTint(color);
        (this.mPointerView = this.findViewById(R$id.user_education_pointer)).setBackground((Drawable)background);
    }
    
    public void setManageViewPosition(final int n, final int n2) {
        this.mManageView.setTranslationX((float)n);
        this.mManageView.setTranslationY((float)n2);
    }
    
    public void setPointerPosition(final int n) {
        final View mPointerView = this.mPointerView;
        mPointerView.setTranslationX((float)(n - mPointerView.getWidth() / 2));
    }
}
