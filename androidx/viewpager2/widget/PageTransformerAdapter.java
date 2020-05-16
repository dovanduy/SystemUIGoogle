// 
// Decompiled by Procyon v0.5.36
// 

package androidx.viewpager2.widget;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import java.util.Locale;
import androidx.recyclerview.widget.LinearLayoutManager;

final class PageTransformerAdapter extends OnPageChangeCallback
{
    private final LinearLayoutManager mLayoutManager;
    private PageTransformer mPageTransformer;
    
    PageTransformerAdapter(final LinearLayoutManager mLayoutManager) {
        this.mLayoutManager = mLayoutManager;
    }
    
    @Override
    public void onPageScrollStateChanged(final int n) {
    }
    
    @Override
    public void onPageScrolled(final int n, float n2, int i) {
        if (this.mPageTransformer == null) {
            return;
        }
        n2 = -n2;
        View child;
        for (i = 0; i < ((RecyclerView.LayoutManager)this.mLayoutManager).getChildCount(); ++i) {
            child = ((RecyclerView.LayoutManager)this.mLayoutManager).getChildAt(i);
            if (child == null) {
                throw new IllegalStateException(String.format(Locale.US, "LayoutManager returned a null child at pos %d/%d while transforming pages", i, ((RecyclerView.LayoutManager)this.mLayoutManager).getChildCount()));
            }
            this.mPageTransformer.transformPage(child, ((RecyclerView.LayoutManager)this.mLayoutManager).getPosition(child) - n + n2);
        }
    }
    
    @Override
    public void onPageSelected(final int n) {
    }
}
