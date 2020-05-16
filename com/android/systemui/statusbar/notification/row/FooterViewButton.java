// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.view.ViewGroup;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.statusbar.AlphaOptimizedButton;

public class FooterViewButton extends AlphaOptimizedButton
{
    public FooterViewButton(final Context context) {
        this(context, null);
    }
    
    public FooterViewButton(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public FooterViewButton(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public FooterViewButton(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
    }
    
    public void getDrawingRect(final Rect rect) {
        super.getDrawingRect(rect);
        final float translationX = ((ViewGroup)super.mParent).getTranslationX();
        final float translationY = ((ViewGroup)super.mParent).getTranslationY();
        rect.left += (int)translationX;
        rect.right += (int)translationX;
        rect.top += (int)translationY;
        rect.bottom += (int)translationY;
    }
}
