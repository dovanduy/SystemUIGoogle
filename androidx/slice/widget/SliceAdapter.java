// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.widget;

import androidx.slice.core.SliceQuery;
import android.view.MotionEvent;
import android.view.View$OnClickListener;
import android.view.View$OnTouchListener;
import androidx.collection.ArrayMap;
import java.util.Iterator;
import android.view.ViewGroup$LayoutParams;
import android.view.ViewGroup;
import androidx.slice.view.R$layout;
import android.view.LayoutInflater;
import android.view.View;
import java.util.HashSet;
import java.util.ArrayList;
import androidx.slice.core.SliceAction;
import java.util.List;
import androidx.slice.SliceItem;
import java.util.Set;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

public class SliceAdapter extends Adapter<SliceViewHolder> implements SliceActionLoadingListener
{
    boolean mAllowTwoLines;
    int mColor;
    final Context mContext;
    private final IdGenerator mIdGen;
    int mInsetBottom;
    int mInsetEnd;
    int mInsetStart;
    int mInsetTop;
    long mLastUpdated;
    Set<SliceItem> mLoadingActions;
    SliceView mParent;
    SliceViewPolicy mPolicy;
    boolean mShowLastUpdated;
    List<SliceAction> mSliceActions;
    SliceView.OnSliceActionListener mSliceObserver;
    SliceStyle mSliceStyle;
    private List<SliceWrapper> mSlices;
    TemplateView mTemplateView;
    
    public SliceAdapter(final Context mContext) {
        this.mIdGen = new IdGenerator();
        this.mSlices = new ArrayList<SliceWrapper>();
        this.mLoadingActions = new HashSet<SliceItem>();
        this.mContext = mContext;
        ((RecyclerView.Adapter)this).setHasStableIds(true);
    }
    
    private View inflateForType(final int n) {
        if (n == 3) {
            return LayoutInflater.from(this.mContext).inflate(R$layout.abc_slice_grid, (ViewGroup)null);
        }
        if (n == 4) {
            return LayoutInflater.from(this.mContext).inflate(R$layout.abc_slice_message, (ViewGroup)null);
        }
        if (n != 5) {
            return (View)new RowView(this.mContext);
        }
        return LayoutInflater.from(this.mContext).inflate(R$layout.abc_slice_message_local, (ViewGroup)null);
    }
    
    @Override
    public int getItemCount() {
        return this.mSlices.size();
    }
    
    @Override
    public long getItemId(final int n) {
        return this.mSlices.get(n).mId;
    }
    
    @Override
    public int getItemViewType(final int n) {
        return this.mSlices.get(n).mType;
    }
    
    public void notifyHeaderChanged() {
        if (this.getItemCount() > 0) {
            ((RecyclerView.Adapter)this).notifyItemChanged(0);
        }
    }
    
    public void onBindViewHolder(final SliceViewHolder sliceViewHolder, final int n) {
        sliceViewHolder.bind(this.mSlices.get(n).mItem, n);
    }
    
    public SliceViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
        final View inflateForType = this.inflateForType(n);
        inflateForType.setLayoutParams(new ViewGroup$LayoutParams(-1, -2));
        return new SliceViewHolder(inflateForType);
    }
    
    @Override
    public void onSliceActionLoading(final SliceItem sliceItem, final int n) {
        this.mLoadingActions.add(sliceItem);
        if (this.getItemCount() > n) {
            ((RecyclerView.Adapter)this).notifyItemChanged(n);
        }
        else {
            ((RecyclerView.Adapter)this).notifyDataSetChanged();
        }
    }
    
    public void setAllowTwoLines(final boolean mAllowTwoLines) {
        this.mAllowTwoLines = mAllowTwoLines;
        this.notifyHeaderChanged();
    }
    
    public void setInsets(final int mInsetStart, final int mInsetTop, final int mInsetEnd, final int mInsetBottom) {
        this.mInsetStart = mInsetStart;
        this.mInsetTop = mInsetTop;
        this.mInsetEnd = mInsetEnd;
        this.mInsetBottom = mInsetBottom;
    }
    
    public void setLastUpdated(final long mLastUpdated) {
        if (this.mLastUpdated != mLastUpdated) {
            this.mLastUpdated = mLastUpdated;
            this.notifyHeaderChanged();
        }
    }
    
    public void setLoadingActions(final Set<SliceItem> mLoadingActions) {
        if (mLoadingActions == null) {
            this.mLoadingActions.clear();
        }
        else {
            this.mLoadingActions = mLoadingActions;
        }
        ((RecyclerView.Adapter)this).notifyDataSetChanged();
    }
    
    public void setParents(final SliceView mParent, final TemplateView mTemplateView) {
        this.mParent = mParent;
        this.mTemplateView = mTemplateView;
    }
    
    public void setPolicy(final SliceViewPolicy mPolicy) {
        this.mPolicy = mPolicy;
    }
    
    public void setShowLastUpdated(final boolean mShowLastUpdated) {
        if (this.mShowLastUpdated != mShowLastUpdated) {
            this.mShowLastUpdated = mShowLastUpdated;
            this.notifyHeaderChanged();
        }
    }
    
    public void setSliceActions(final List<SliceAction> mSliceActions) {
        this.mSliceActions = mSliceActions;
        this.notifyHeaderChanged();
    }
    
    public void setSliceItems(final List<SliceContent> list, final int mColor, final int n) {
        if (list == null) {
            this.mLoadingActions.clear();
            this.mSlices.clear();
        }
        else {
            this.mIdGen.resetUsage();
            this.mSlices = new ArrayList<SliceWrapper>(list.size());
            final Iterator<SliceContent> iterator = list.iterator();
            while (iterator.hasNext()) {
                this.mSlices.add(new SliceWrapper(iterator.next(), this.mIdGen, n));
            }
        }
        this.mColor = mColor;
        ((RecyclerView.Adapter)this).notifyDataSetChanged();
    }
    
    public void setSliceObserver(final SliceView.OnSliceActionListener mSliceObserver) {
        this.mSliceObserver = mSliceObserver;
    }
    
    public void setStyle(final SliceStyle mSliceStyle) {
        this.mSliceStyle = mSliceStyle;
        ((RecyclerView.Adapter)this).notifyDataSetChanged();
    }
    
    private static class IdGenerator
    {
        private final ArrayMap<String, Long> mCurrentIds;
        private long mNextLong;
        private final ArrayMap<String, Integer> mUsedIds;
        
        IdGenerator() {
            this.mNextLong = 0L;
            this.mCurrentIds = new ArrayMap<String, Long>();
            this.mUsedIds = new ArrayMap<String, Integer>();
        }
        
        private String genString(final SliceItem sliceItem) {
            if (!"slice".equals(sliceItem.getFormat()) && !"action".equals(sliceItem.getFormat())) {
                return sliceItem.toString();
            }
            return String.valueOf(sliceItem.getSlice().getItems().size());
        }
        
        public long getId(final SliceItem sliceItem) {
            final String genString = this.genString(sliceItem);
            if (!this.mCurrentIds.containsKey(genString)) {
                final ArrayMap<String, Long> mCurrentIds = this.mCurrentIds;
                final long mNextLong = this.mNextLong;
                this.mNextLong = 1L + mNextLong;
                mCurrentIds.put(genString, mNextLong);
            }
            final long longValue = this.mCurrentIds.get(genString);
            final Integer n = this.mUsedIds.get(genString);
            int intValue;
            if (n != null) {
                intValue = n;
            }
            else {
                intValue = 0;
            }
            this.mUsedIds.put(genString, intValue + 1);
            return longValue + intValue * 10000;
        }
        
        public void resetUsage() {
            this.mUsedIds.clear();
        }
    }
    
    public class SliceViewHolder extends ViewHolder implements View$OnTouchListener, View$OnClickListener
    {
        public final SliceChildView mSliceChildView;
        
        public SliceViewHolder(final View view) {
            super(view);
            SliceChildView mSliceChildView;
            if (view instanceof SliceChildView) {
                mSliceChildView = (SliceChildView)view;
            }
            else {
                mSliceChildView = null;
            }
            this.mSliceChildView = mSliceChildView;
        }
        
        void bind(final SliceContent sliceContent, final int n) {
            final SliceChildView mSliceChildView = this.mSliceChildView;
            if (mSliceChildView != null) {
                if (sliceContent != null) {
                    mSliceChildView.setOnClickListener((View$OnClickListener)this);
                    this.mSliceChildView.setOnTouchListener((View$OnTouchListener)this);
                    this.mSliceChildView.setSliceActionLoadingListener(SliceAdapter.this);
                    final boolean b = n == 0;
                    this.mSliceChildView.setLoadingActions(SliceAdapter.this.mLoadingActions);
                    this.mSliceChildView.setPolicy(SliceAdapter.this.mPolicy);
                    this.mSliceChildView.setTint(SliceAdapter.this.mColor);
                    this.mSliceChildView.setStyle(SliceAdapter.this.mSliceStyle);
                    this.mSliceChildView.setShowLastUpdated(b && SliceAdapter.this.mShowLastUpdated);
                    final SliceChildView mSliceChildView2 = this.mSliceChildView;
                    long mLastUpdated;
                    if (b) {
                        mLastUpdated = SliceAdapter.this.mLastUpdated;
                    }
                    else {
                        mLastUpdated = -1L;
                    }
                    mSliceChildView2.setLastUpdated(mLastUpdated);
                    int mInsetTop;
                    if (n == 0) {
                        mInsetTop = SliceAdapter.this.mInsetTop;
                    }
                    else {
                        mInsetTop = 0;
                    }
                    int mInsetBottom;
                    if (n == SliceAdapter.this.getItemCount() - 1) {
                        mInsetBottom = SliceAdapter.this.mInsetBottom;
                    }
                    else {
                        mInsetBottom = 0;
                    }
                    final SliceChildView mSliceChildView3 = this.mSliceChildView;
                    final SliceAdapter this$0 = SliceAdapter.this;
                    mSliceChildView3.setInsets(this$0.mInsetStart, mInsetTop, this$0.mInsetEnd, mInsetBottom);
                    this.mSliceChildView.setAllowTwoLines(SliceAdapter.this.mAllowTwoLines);
                    final SliceChildView mSliceChildView4 = this.mSliceChildView;
                    List<SliceAction> mSliceActions;
                    if (b) {
                        mSliceActions = SliceAdapter.this.mSliceActions;
                    }
                    else {
                        mSliceActions = null;
                    }
                    mSliceChildView4.setSliceActions(mSliceActions);
                    this.mSliceChildView.setSliceItem(sliceContent, b, n, SliceAdapter.this.getItemCount(), SliceAdapter.this.mSliceObserver);
                    this.mSliceChildView.setTag((Object)new int[] { ListContent.getRowType(sliceContent, b, SliceAdapter.this.mSliceActions), n });
                }
            }
        }
        
        public void onClick(final View view) {
            final SliceView mParent = SliceAdapter.this.mParent;
            if (mParent != null) {
                mParent.setClickInfo((int[])view.getTag());
                SliceAdapter.this.mParent.performClick();
            }
        }
        
        public boolean onTouch(final View view, final MotionEvent motionEvent) {
            final TemplateView mTemplateView = SliceAdapter.this.mTemplateView;
            if (mTemplateView != null) {
                mTemplateView.onForegroundActivated(motionEvent);
            }
            return false;
        }
    }
    
    protected static class SliceWrapper
    {
        final long mId;
        final SliceContent mItem;
        final int mType;
        
        public SliceWrapper(final SliceContent mItem, final IdGenerator idGenerator, final int n) {
            this.mItem = mItem;
            this.mType = getFormat(mItem.getSliceItem());
            this.mId = idGenerator.getId(mItem.getSliceItem());
        }
        
        public static int getFormat(final SliceItem sliceItem) {
            if ("message".equals(sliceItem.getSubType())) {
                if (SliceQuery.findSubtype(sliceItem, null, "source") != null) {
                    return 4;
                }
                return 5;
            }
            else {
                if (sliceItem.hasHint("horizontal")) {
                    return 3;
                }
                if (!sliceItem.hasHint("list_item")) {
                    return 2;
                }
                return 1;
            }
        }
    }
}
