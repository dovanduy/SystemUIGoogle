// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.widget;

import androidx.slice.core.SliceAction;
import java.util.Set;
import androidx.slice.SliceItem;
import android.view.View$MeasureSpec;
import android.os.Build$VERSION;
import android.view.MotionEvent;
import java.util.List;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import java.util.ArrayList;

public class TemplateView extends SliceChildView implements PolicyChangeListener
{
    private final SliceAdapter mAdapter;
    private ArrayList<SliceContent> mDisplayedItems;
    private int mDisplayedItemsHeight;
    private final View mForeground;
    private ListContent mListContent;
    private int[] mLoc;
    private SliceView mParent;
    private final RecyclerView mRecyclerView;
    
    public TemplateView(final Context context) {
        super(context);
        this.mDisplayedItems = new ArrayList<SliceContent>();
        this.mDisplayedItemsHeight = 0;
        this.mLoc = new int[2];
        (this.mRecyclerView = new RecyclerView(this.getContext())).setLayoutManager((RecyclerView.LayoutManager)new LinearLayoutManager(this.getContext()));
        final SliceAdapter sliceAdapter = new SliceAdapter(context);
        this.mAdapter = sliceAdapter;
        this.mRecyclerView.setAdapter((RecyclerView.Adapter)sliceAdapter);
        this.addView((View)this.mRecyclerView);
        (this.mForeground = new View(this.getContext())).setBackground(SliceViewUtil.getDrawable(this.getContext(), 16843534));
        this.addView(this.mForeground);
        final FrameLayout$LayoutParams layoutParams = (FrameLayout$LayoutParams)this.mForeground.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.mForeground.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
    }
    
    private void updateDisplayedItems(final int n) {
        final ListContent mListContent = this.mListContent;
        if (mListContent != null && mListContent.isValid()) {
            final ArrayList<SliceContent> rowItems = this.mListContent.getRowItems(n, super.mSliceStyle, super.mViewPolicy);
            this.mDisplayedItems = rowItems;
            this.mDisplayedItemsHeight = ListContent.getListHeight(rowItems, super.mSliceStyle, super.mViewPolicy);
            this.mAdapter.setSliceItems(this.mDisplayedItems, super.mTintColor, super.mViewPolicy.getMode());
            this.updateOverscroll();
            return;
        }
        this.resetView();
    }
    
    private void updateOverscroll() {
        final int mDisplayedItemsHeight = this.mDisplayedItemsHeight;
        final int measuredHeight = this.getMeasuredHeight();
        final int n = 1;
        final boolean b = mDisplayedItemsHeight > measuredHeight;
        final RecyclerView mRecyclerView = this.mRecyclerView;
        int overScrollMode;
        if (super.mViewPolicy.isScrollable() && b) {
            overScrollMode = n;
        }
        else {
            overScrollMode = 2;
        }
        mRecyclerView.setOverScrollMode(overScrollMode);
    }
    
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        final SliceView mParent = (SliceView)this.getParent();
        this.mParent = mParent;
        this.mAdapter.setParents(mParent, this);
    }
    
    public void onForegroundActivated(final MotionEvent motionEvent) {
        final SliceView mParent = this.mParent;
        if (mParent != null && !mParent.isSliceViewClickable()) {
            this.mForeground.setPressed(false);
            return;
        }
        if (Build$VERSION.SDK_INT >= 21) {
            this.mForeground.getLocationOnScreen(this.mLoc);
            this.mForeground.getBackground().setHotspot((float)(int)(motionEvent.getRawX() - this.mLoc[0]), (float)(int)(motionEvent.getRawY() - this.mLoc[1]));
        }
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.mForeground.setPressed(true);
        }
        else if (actionMasked == 3 || actionMasked == 1 || actionMasked == 2) {
            this.mForeground.setPressed(false);
        }
    }
    
    @Override
    public void onMaxHeightChanged(final int n) {
        final ListContent mListContent = this.mListContent;
        if (mListContent != null) {
            this.updateDisplayedItems(mListContent.getHeight(super.mSliceStyle, super.mViewPolicy));
        }
    }
    
    @Override
    public void onMaxSmallChanged(final int n) {
        final SliceAdapter mAdapter = this.mAdapter;
        if (mAdapter != null) {
            mAdapter.notifyHeaderChanged();
        }
    }
    
    protected void onMeasure(final int n, final int n2) {
        final int size = View$MeasureSpec.getSize(n2);
        if (!super.mViewPolicy.isScrollable() && this.mDisplayedItems.size() > 0 && this.mDisplayedItemsHeight != size) {
            this.updateDisplayedItems(size);
        }
        super.onMeasure(n, n2);
    }
    
    @Override
    public void resetView() {
        this.mDisplayedItemsHeight = 0;
        this.mDisplayedItems.clear();
        this.mAdapter.setSliceItems(null, -1, this.getMode());
        this.mListContent = null;
    }
    
    @Override
    public void setActionLoading(final SliceItem sliceItem) {
        this.mAdapter.onSliceActionLoading(sliceItem, 0);
    }
    
    @Override
    public void setAllowTwoLines(final boolean allowTwoLines) {
        this.mAdapter.setAllowTwoLines(allowTwoLines);
    }
    
    @Override
    public void setInsets(final int n, final int n2, final int n3, final int n4) {
        super.setInsets(n, n2, n3, n4);
        this.mAdapter.setInsets(n, n2, n3, n4);
    }
    
    @Override
    public void setLastUpdated(final long n) {
        super.setLastUpdated(n);
        this.mAdapter.setLastUpdated(n);
    }
    
    @Override
    public void setLoadingActions(final Set<SliceItem> loadingActions) {
        this.mAdapter.setLoadingActions(loadingActions);
    }
    
    @Override
    public void setPolicy(final SliceViewPolicy sliceViewPolicy) {
        super.setPolicy(sliceViewPolicy);
        this.mAdapter.setPolicy(sliceViewPolicy);
        sliceViewPolicy.setListener((SliceViewPolicy.PolicyChangeListener)this);
    }
    
    @Override
    public void setShowLastUpdated(final boolean b) {
        super.setShowLastUpdated(b);
        this.mAdapter.setShowLastUpdated(b);
    }
    
    @Override
    public void setSliceActionListener(final SliceView.OnSliceActionListener onSliceActionListener) {
        super.mObserver = onSliceActionListener;
        final SliceAdapter mAdapter = this.mAdapter;
        if (mAdapter != null) {
            mAdapter.setSliceObserver(onSliceActionListener);
        }
    }
    
    @Override
    public void setSliceActions(final List<SliceAction> sliceActions) {
        this.mAdapter.setSliceActions(sliceActions);
    }
    
    @Override
    public void setSliceContent(final ListContent mListContent) {
        this.mListContent = mListContent;
        this.updateDisplayedItems(mListContent.getHeight(super.mSliceStyle, super.mViewPolicy));
    }
    
    @Override
    public void setStyle(final SliceStyle sliceStyle) {
        super.setStyle(sliceStyle);
        this.mAdapter.setStyle(sliceStyle);
    }
    
    @Override
    public void setTint(final int tint) {
        super.setTint(tint);
        this.updateDisplayedItems(this.getMeasuredHeight());
    }
}
