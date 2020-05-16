// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import com.android.systemui.R$dimen;
import android.animation.Animator$AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import java.util.Collection;
import java.util.Set;
import android.os.Bundle;
import android.view.View$MeasureSpec;
import android.content.res.Configuration;
import java.util.Iterator;
import android.animation.TimeInterpolator;
import android.view.animation.OvershootInterpolator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.Animator;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.Scroller;
import java.util.ArrayList;
import android.graphics.Rect;
import android.animation.AnimatorSet;
import androidx.viewpager.widget.PagerAdapter;
import android.view.animation.Interpolator;
import androidx.viewpager.widget.ViewPager;

public class PagedTileLayout extends ViewPager implements QSTileLayout
{
    private static final Interpolator SCROLL_CUBIC;
    private final PagerAdapter mAdapter;
    private AnimatorSet mBounceAnimatorSet;
    private final Rect mClippingRect;
    private boolean mDistributeTiles;
    private int mHorizontalClipBound;
    private float mLastExpansion;
    private int mLastMaxHeight;
    private int mLayoutDirection;
    private int mLayoutOrientation;
    private boolean mListening;
    private final OnPageChangeListener mOnPageChangeListener;
    private PageIndicator mPageIndicator;
    private float mPageIndicatorPosition;
    private PageListener mPageListener;
    private int mPageToRestore;
    private final ArrayList<TilePage> mPages;
    private Scroller mScroller;
    private final ArrayList<TileRecord> mTiles;
    
    static {
        SCROLL_CUBIC = (Interpolator)_$$Lambda$PagedTileLayout$fHkBmUM3ca_ZV4_eDd9ap_VT7Ho.INSTANCE;
    }
    
    public PagedTileLayout(final Context context, final AttributeSet set) {
        super(context, set);
        this.mTiles = new ArrayList<TileRecord>();
        this.mPages = new ArrayList<TilePage>();
        this.mDistributeTiles = false;
        this.mPageToRestore = -1;
        this.mLastMaxHeight = -1;
        this.mOnPageChangeListener = new SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(final int n, final float n2, final int n3) {
                if (PagedTileLayout.this.mPageIndicator == null) {
                    return;
                }
                PagedTileLayout.this.mPageIndicatorPosition = n + n2;
                PagedTileLayout.this.mPageIndicator.setLocation(PagedTileLayout.this.mPageIndicatorPosition);
                if (PagedTileLayout.this.mPageListener != null) {
                    final PageListener access$300 = PagedTileLayout.this.mPageListener;
                    boolean b = true;
                    Label_0105: {
                        if (n3 == 0) {
                            if (PagedTileLayout.this.isLayoutRtl()) {
                                if (n == PagedTileLayout.this.mPages.size() - 1) {
                                    break Label_0105;
                                }
                            }
                            else if (n == 0) {
                                break Label_0105;
                            }
                        }
                        b = false;
                    }
                    access$300.onPageChanged(b);
                }
            }
            
            @Override
            public void onPageSelected(final int n) {
                PagedTileLayout.this.updateSelected();
                if (PagedTileLayout.this.mPageIndicator == null) {
                    return;
                }
                if (PagedTileLayout.this.mPageListener != null) {
                    final PageListener access$300 = PagedTileLayout.this.mPageListener;
                    final boolean layoutRtl = PagedTileLayout.this.isLayoutRtl();
                    boolean b = false;
                    Label_0077: {
                        if (layoutRtl) {
                            if (n != PagedTileLayout.this.mPages.size() - 1) {
                                break Label_0077;
                            }
                        }
                        else if (n != 0) {
                            break Label_0077;
                        }
                        b = true;
                    }
                    access$300.onPageChanged(b);
                }
            }
        };
        this.mAdapter = new PagerAdapter() {
            @Override
            public void destroyItem(final ViewGroup viewGroup, final int n, final Object o) {
                viewGroup.removeView((View)o);
                PagedTileLayout.this.updateListening();
            }
            
            @Override
            public int getCount() {
                return PagedTileLayout.this.mPages.size();
            }
            
            @Override
            public Object instantiateItem(final ViewGroup viewGroup, final int n) {
                int index = n;
                if (PagedTileLayout.this.isLayoutRtl()) {
                    index = PagedTileLayout.this.mPages.size() - 1 - n;
                }
                final ViewGroup viewGroup2 = PagedTileLayout.this.mPages.get(index);
                if (viewGroup2.getParent() != null) {
                    viewGroup.removeView((View)viewGroup2);
                }
                viewGroup.addView((View)viewGroup2);
                PagedTileLayout.this.updateListening();
                return viewGroup2;
            }
            
            @Override
            public boolean isViewFromObject(final View view, final Object o) {
                return view == o;
            }
        };
        this.mScroller = new Scroller(context, PagedTileLayout.SCROLL_CUBIC);
        this.setAdapter(this.mAdapter);
        this.setOnPageChangeListener(this.mOnPageChangeListener);
        this.setCurrentItem(0, false);
        this.mLayoutOrientation = this.getResources().getConfiguration().orientation;
        this.mLayoutDirection = this.getLayoutDirection();
        this.mClippingRect = new Rect();
    }
    
    private void distributeTiles() {
        this.emptyAndInflateOrRemovePages();
        final ArrayList<TilePage> mPages = this.mPages;
        int i = 0;
        final int maxTiles = mPages.get(0).maxTiles();
        final int size = this.mTiles.size();
        int index = 0;
        while (i < size) {
            final TileRecord tileRecord = this.mTiles.get(i);
            int index2 = index;
            if (this.mPages.get(index).mRecords.size() == maxTiles) {
                index2 = index + 1;
            }
            this.mPages.get(index2).addTile(tileRecord);
            ++i;
            index = index2;
        }
    }
    
    private void emptyAndInflateOrRemovePages() {
        final int size = this.mTiles.size();
        int max;
        final int n = max = Math.max(size / this.mPages.get(0).maxTiles(), 1);
        if (size > this.mPages.get(0).maxTiles() * n) {
            max = n + 1;
        }
        final int size2 = this.mPages.size();
        for (int i = 0; i < size2; ++i) {
            this.mPages.get(i).removeAllViews();
        }
        if (size2 == max) {
            return;
        }
        while (this.mPages.size() < max) {
            this.mPages.add((TilePage)LayoutInflater.from(this.getContext()).inflate(R$layout.qs_paged_page, (ViewGroup)this, false));
        }
        while (this.mPages.size() > max) {
            final ArrayList<TilePage> mPages = this.mPages;
            mPages.remove(mPages.size() - 1);
        }
        this.mPageIndicator.setNumPages(this.mPages.size());
        this.setAdapter(this.mAdapter);
        this.mAdapter.notifyDataSetChanged();
        final int mPageToRestore = this.mPageToRestore;
        if (mPageToRestore != -1) {
            this.setCurrentItem(mPageToRestore, false);
            this.mPageToRestore = -1;
        }
    }
    
    private int getCurrentPageNumber() {
        int currentItem = this.getCurrentItem();
        if (this.mLayoutDirection == 1) {
            currentItem = this.mPages.size() - 1 - currentItem;
        }
        return currentItem;
    }
    
    private static Animator setupBounceAnimator(final View view, final int n) {
        view.setAlpha(0.0f);
        view.setScaleX(0.0f);
        view.setScaleY(0.0f);
        final ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder((Object)view, new PropertyValuesHolder[] { PropertyValuesHolder.ofFloat(View.ALPHA, new float[] { 1.0f }), PropertyValuesHolder.ofFloat(View.SCALE_X, new float[] { 1.0f }), PropertyValuesHolder.ofFloat(View.SCALE_Y, new float[] { 1.0f }) });
        ofPropertyValuesHolder.setDuration(450L);
        ofPropertyValuesHolder.setStartDelay((long)(n * 85));
        ofPropertyValuesHolder.setInterpolator((TimeInterpolator)new OvershootInterpolator(1.3f));
        return (Animator)ofPropertyValuesHolder;
    }
    
    private void updateListening() {
        for (final TilePage tilePage : this.mPages) {
            tilePage.setListening(tilePage.getParent() != null && this.mListening);
        }
    }
    
    private void updateSelected() {
        final float mLastExpansion = this.mLastExpansion;
        if (mLastExpansion > 0.0f && mLastExpansion < 1.0f) {
            return;
        }
        final boolean b = this.mLastExpansion == 1.0f;
        this.setImportantForAccessibility(4);
        final int currentPageNumber = this.getCurrentPageNumber();
        for (int i = 0; i < this.mPages.size(); ++i) {
            this.mPages.get(i).setSelected(i == currentPageNumber && b);
        }
        this.setImportantForAccessibility(0);
    }
    
    @Override
    public void addTile(final TileRecord e) {
        this.mTiles.add(e);
        this.mDistributeTiles = true;
        this.requestLayout();
    }
    
    @Override
    public void computeScroll() {
        if (!this.mScroller.isFinished() && this.mScroller.computeScrollOffset()) {
            if (!this.isFakeDragging()) {
                this.beginFakeDrag();
            }
            this.fakeDragBy((float)(this.getScrollX() - this.mScroller.getCurrX()));
            this.postInvalidateOnAnimation();
            return;
        }
        if (this.isFakeDragging()) {
            this.endFakeDrag();
            this.mBounceAnimatorSet.start();
            this.setOffscreenPageLimit(1);
        }
        super.computeScroll();
    }
    
    public int getColumnCount() {
        if (this.mPages.size() == 0) {
            return 0;
        }
        return this.mPages.get(0).mColumns;
    }
    
    @Override
    public int getNumVisibleTiles() {
        if (this.mPages.size() == 0) {
            return 0;
        }
        return this.mPages.get(this.getCurrentPageNumber()).mRecords.size();
    }
    
    @Override
    public int getOffsetTop(final TileRecord tileRecord) {
        final ViewGroup viewGroup = (ViewGroup)tileRecord.tileView.getParent();
        if (viewGroup == null) {
            return 0;
        }
        return viewGroup.getTop() + this.getTop();
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        final int mLayoutOrientation = this.mLayoutOrientation;
        final int orientation = configuration.orientation;
        if (mLayoutOrientation != orientation) {
            this.mLayoutOrientation = orientation;
            this.setCurrentItem(0, false);
            this.mPageToRestore = 0;
        }
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mPages.add((TilePage)LayoutInflater.from(this.getContext()).inflate(R$layout.qs_paged_page, (ViewGroup)this, false));
        this.mAdapter.notifyDataSetChanged();
    }
    
    @Override
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        final Rect mClippingRect = this.mClippingRect;
        final int mHorizontalClipBound = this.mHorizontalClipBound;
        mClippingRect.set(mHorizontalClipBound, 0, n3 - n - mHorizontalClipBound, n4 - n2);
        this.setClipBounds(this.mClippingRect);
    }
    
    @Override
    protected void onMeasure(int i, int n) {
        final int size = this.mTiles.size();
        final boolean mDistributeTiles = this.mDistributeTiles;
        final int n2 = 0;
        if (mDistributeTiles || this.mLastMaxHeight != View$MeasureSpec.getSize(n)) {
            this.mLastMaxHeight = View$MeasureSpec.getSize(n);
            if (this.mPages.get(0).updateMaxRows(n, size) || this.mDistributeTiles) {
                this.mDistributeTiles = false;
                this.distributeTiles();
            }
            final int mRows = this.mPages.get(0).mRows;
            for (int j = 0; j < this.mPages.size(); ++j) {
                this.mPages.get(j).mRows = mRows;
            }
        }
        super.onMeasure(i, n);
        final int childCount = this.getChildCount();
        n = 0;
        int measuredHeight;
        int n3;
        for (i = n2; i < childCount; ++i, n = n3) {
            measuredHeight = this.getChildAt(i).getMeasuredHeight();
            if (measuredHeight > (n3 = n)) {
                n3 = measuredHeight;
            }
        }
        this.setMeasuredDimension(this.getMeasuredWidth(), n + this.getPaddingBottom());
    }
    
    public void onRtlPropertiesChanged(final int mLayoutDirection) {
        super.onRtlPropertiesChanged(mLayoutDirection);
        if (this.mLayoutDirection != mLayoutDirection) {
            this.mLayoutDirection = mLayoutDirection;
            this.setAdapter(this.mAdapter);
            this.setCurrentItem(0, false);
            this.mPageToRestore = 0;
        }
    }
    
    @Override
    public void removeTile(final TileRecord o) {
        if (this.mTiles.remove(o)) {
            this.mDistributeTiles = true;
            this.requestLayout();
        }
    }
    
    @Override
    public void restoreInstanceState(final Bundle bundle) {
        this.mPageToRestore = bundle.getInt("current_page", -1);
    }
    
    @Override
    public void saveInstanceState(final Bundle bundle) {
        bundle.putInt("current_page", this.getCurrentItem());
    }
    
    @Override
    public void setCurrentItem(final int n, final boolean b) {
        int n2 = n;
        if (this.isLayoutRtl()) {
            n2 = this.mPages.size() - 1 - n;
        }
        super.setCurrentItem(n2, b);
    }
    
    @Override
    public void setExpansion(final float mLastExpansion) {
        this.mLastExpansion = mLastExpansion;
        this.updateSelected();
    }
    
    @Override
    public void setListening(final boolean mListening) {
        if (this.mListening == mListening) {
            return;
        }
        this.mListening = mListening;
        this.updateListening();
    }
    
    public void setPageIndicator(final PageIndicator mPageIndicator) {
        (this.mPageIndicator = mPageIndicator).setNumPages(this.mPages.size());
        this.mPageIndicator.setLocation(this.mPageIndicatorPosition);
    }
    
    public void setPageListener(final PageListener mPageListener) {
        this.mPageListener = mPageListener;
    }
    
    public void startTileReveal(final Set<String> set, final Runnable runnable) {
        if (!set.isEmpty() && this.mPages.size() >= 2 && this.getScrollX() == 0) {
            if (this.beginFakeDrag()) {
                final int n = this.mPages.size() - 1;
                final TilePage tilePage = this.mPages.get(n);
                final ArrayList<Animator> list = new ArrayList<Animator>();
                for (final TileRecord tileRecord : tilePage.mRecords) {
                    if (set.contains(tileRecord.tile.getTileSpec())) {
                        list.add(setupBounceAnimator((View)tileRecord.tileView, list.size()));
                    }
                }
                if (list.isEmpty()) {
                    this.endFakeDrag();
                    return;
                }
                (this.mBounceAnimatorSet = new AnimatorSet()).playTogether((Collection)list);
                this.mBounceAnimatorSet.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                    public void onAnimationEnd(final Animator animator) {
                        PagedTileLayout.this.mBounceAnimatorSet = null;
                        runnable.run();
                    }
                });
                this.setOffscreenPageLimit(n);
                final int n2 = this.getWidth() * n;
                final Scroller mScroller = this.mScroller;
                final int scrollX = this.getScrollX();
                final int scrollY = this.getScrollY();
                int n3 = n2;
                if (this.isLayoutRtl()) {
                    n3 = -n2;
                }
                mScroller.startScroll(scrollX, scrollY, n3, 0, 750);
                this.postInvalidateOnAnimation();
            }
        }
    }
    
    @Override
    public boolean updateResources() {
        this.mHorizontalClipBound = this.getContext().getResources().getDimensionPixelSize(R$dimen.notification_side_paddings);
        final int dimensionPixelSize = this.getContext().getResources().getDimensionPixelSize(R$dimen.qs_paged_tile_layout_padding_bottom);
        int i = 0;
        this.setPadding(0, 0, 0, dimensionPixelSize);
        boolean b = false;
        while (i < this.mPages.size()) {
            b |= this.mPages.get(i).updateResources();
            ++i;
        }
        if (b) {
            this.mDistributeTiles = true;
            this.requestLayout();
        }
        return b;
    }
    
    public interface PageListener
    {
        void onPageChanged(final boolean p0);
    }
    
    public static class TilePage extends TileLayout
    {
        public TilePage(final Context context, final AttributeSet set) {
            super(context, set);
        }
        
        public int maxTiles() {
            return Math.max(super.mColumns * super.mRows, 1);
        }
        
        @Override
        public boolean updateResources() {
            final int dimensionPixelSize = this.getContext().getResources().getDimensionPixelSize(R$dimen.notification_side_paddings);
            this.setPadding(dimensionPixelSize, 0, dimensionPixelSize, 0);
            return super.updateResources();
        }
    }
}
