// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.widget;

import java.util.Set;
import android.view.View$MeasureSpec;
import android.app.PendingIntent$CanceledException;
import android.util.Log;
import android.content.Intent;
import androidx.slice.core.SliceActionImpl;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ColorDrawable;
import androidx.slice.view.R$dimen;
import android.view.ViewConfiguration;
import android.view.MotionEvent;
import androidx.slice.SliceItem;
import androidx.slice.core.SliceQuery;
import android.view.ViewGroup$LayoutParams;
import androidx.slice.view.R$style;
import android.view.View;
import androidx.slice.view.R$attr;
import android.util.AttributeSet;
import android.content.Context;
import androidx.slice.SliceMetadata;
import android.view.View$OnLongClickListener;
import android.os.Handler;
import java.util.List;
import androidx.slice.core.SliceAction;
import java.util.Comparator;
import android.view.View$OnClickListener;
import androidx.slice.Slice;
import androidx.lifecycle.Observer;
import android.view.ViewGroup;

public class SliceView extends ViewGroup implements Observer<Slice>, View$OnClickListener
{
    public static final Comparator<SliceAction> SLICE_ACTION_PRIORITY_COMPARATOR;
    private ActionRow mActionRow;
    private int mActionRowHeight;
    private List<SliceAction> mActions;
    int[] mClickInfo;
    private Slice mCurrentSlice;
    private boolean mCurrentSliceLoggedVisible;
    private SliceMetrics mCurrentSliceMetrics;
    SliceChildView mCurrentView;
    private int mDownX;
    private int mDownY;
    Handler mHandler;
    boolean mInLongpress;
    private int mLargeHeight;
    ListContent mListContent;
    View$OnLongClickListener mLongClickListener;
    Runnable mLongpressCheck;
    private int mMinTemplateHeight;
    private View$OnClickListener mOnClickListener;
    boolean mPressing;
    Runnable mRefreshLastUpdated;
    private int mShortcutSize;
    private boolean mShowActionDividers;
    private boolean mShowActions;
    private boolean mShowHeaderDivider;
    private boolean mShowLastUpdated;
    private boolean mShowTitleItems;
    SliceMetadata mSliceMetadata;
    private OnSliceActionListener mSliceObserver;
    private SliceStyle mSliceStyle;
    private int mThemeTintColor;
    private int mTouchSlopSquared;
    private SliceViewPolicy mViewPolicy;
    
    static {
        SLICE_ACTION_PRIORITY_COMPARATOR = new Comparator<SliceAction>() {
            @Override
            public int compare(final SliceAction sliceAction, final SliceAction sliceAction2) {
                final int priority = sliceAction.getPriority();
                final int priority2 = sliceAction2.getPriority();
                if (priority < 0 && priority2 < 0) {
                    return 0;
                }
                if (priority < 0) {
                    return 1;
                }
                if (priority2 < 0) {
                    return -1;
                }
                if (priority2 < priority) {
                    return 1;
                }
                if (priority2 > priority) {
                    return -1;
                }
                return 0;
            }
        };
    }
    
    public SliceView(final Context context, final AttributeSet set) {
        this(context, set, R$attr.sliceViewStyle);
    }
    
    public SliceView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mShowActions = false;
        this.mShowLastUpdated = true;
        this.mCurrentSliceLoggedVisible = false;
        this.mShowTitleItems = false;
        this.mShowHeaderDivider = false;
        this.mShowActionDividers = false;
        this.mThemeTintColor = -1;
        this.mLongpressCheck = new Runnable() {
            @Override
            public void run() {
                final SliceView this$0 = SliceView.this;
                if (this$0.mPressing) {
                    final View$OnLongClickListener mLongClickListener = this$0.mLongClickListener;
                    if (mLongClickListener != null) {
                        this$0.mInLongpress = true;
                        mLongClickListener.onLongClick((View)this$0);
                        SliceView.this.performHapticFeedback(0);
                    }
                }
            }
        };
        this.mRefreshLastUpdated = new Runnable() {
            @Override
            public void run() {
                final SliceMetadata mSliceMetadata = SliceView.this.mSliceMetadata;
                if (mSliceMetadata != null && mSliceMetadata.isExpired()) {
                    SliceView.this.mCurrentView.setShowLastUpdated(true);
                    final SliceView this$0 = SliceView.this;
                    this$0.mCurrentView.setSliceContent(this$0.mListContent);
                }
                SliceView.this.mHandler.postDelayed((Runnable)this, 60000L);
            }
        };
        this.init(context, set, n, R$style.Widget_SliceView);
    }
    
    private void applyConfigurations() {
        this.mCurrentView.setSliceActionListener(this.mSliceObserver);
        this.mCurrentView.setStyle(this.mSliceStyle);
        this.mCurrentView.setTint(this.getTintColor());
        final ListContent mListContent = this.mListContent;
        if (mListContent != null && mListContent.getLayoutDir() != -1) {
            this.mCurrentView.setLayoutDirection(this.mListContent.getLayoutDir());
        }
        else {
            this.mCurrentView.setLayoutDirection(2);
        }
    }
    
    private void configureViewPolicy(int maxHeight) {
        final ListContent mListContent = this.mListContent;
        if (mListContent != null && mListContent.isValid() && this.getMode() != 3) {
            if (maxHeight > 0 && maxHeight < this.mSliceStyle.getRowMaxHeight()) {
                final int mMinTemplateHeight = this.mMinTemplateHeight;
                int maxSmallHeight;
                if ((maxSmallHeight = maxHeight) <= mMinTemplateHeight) {
                    maxSmallHeight = mMinTemplateHeight;
                }
                this.mViewPolicy.setMaxSmallHeight(maxSmallHeight);
                maxHeight = maxSmallHeight;
            }
            else {
                this.mViewPolicy.setMaxSmallHeight(0);
            }
            this.mViewPolicy.setMaxHeight(maxHeight);
        }
    }
    
    private ViewGroup$LayoutParams getChildLp(final View view) {
        return new ViewGroup$LayoutParams(-1, -1);
    }
    
    private int getTintColor() {
        final int mThemeTintColor = this.mThemeTintColor;
        if (mThemeTintColor != -1) {
            return mThemeTintColor;
        }
        final SliceItem subtype = SliceQuery.findSubtype(this.mCurrentSlice, "int", "color");
        int n;
        if (subtype != null) {
            n = subtype.getInt();
        }
        else {
            n = SliceViewUtil.getColorAccent(this.getContext());
        }
        return n;
    }
    
    private boolean handleTouchForLongpress(final MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    final int n = (int)motionEvent.getRawX() - this.mDownX;
                    final int n2 = (int)motionEvent.getRawY() - this.mDownY;
                    if (n * n + n2 * n2 > this.mTouchSlopSquared) {
                        this.mPressing = false;
                        this.mHandler.removeCallbacks(this.mLongpressCheck);
                    }
                    return this.mInLongpress;
                }
                if (actionMasked != 3) {
                    return false;
                }
            }
            final boolean mInLongpress = this.mInLongpress;
            this.mPressing = false;
            this.mInLongpress = false;
            this.mHandler.removeCallbacks(this.mLongpressCheck);
            return mInLongpress;
        }
        this.mHandler.removeCallbacks(this.mLongpressCheck);
        this.mDownX = (int)motionEvent.getRawX();
        this.mDownY = (int)motionEvent.getRawY();
        this.mPressing = true;
        this.mInLongpress = false;
        this.mHandler.postDelayed(this.mLongpressCheck, (long)ViewConfiguration.getLongPressTimeout());
        return false;
    }
    
    private void init(final Context context, final AttributeSet set, int scaledTouchSlop, final int n) {
        final SliceStyle mSliceStyle = new SliceStyle(context, set, scaledTouchSlop, n);
        this.mSliceStyle = mSliceStyle;
        this.mThemeTintColor = mSliceStyle.getTintColor();
        this.mShortcutSize = this.getContext().getResources().getDimensionPixelSize(R$dimen.abc_slice_shortcut_size);
        this.mMinTemplateHeight = this.getContext().getResources().getDimensionPixelSize(R$dimen.abc_slice_row_min_height);
        this.mLargeHeight = this.getResources().getDimensionPixelSize(R$dimen.abc_slice_large_height);
        this.mActionRowHeight = this.getResources().getDimensionPixelSize(R$dimen.abc_slice_action_row_height);
        this.mViewPolicy = new SliceViewPolicy();
        (this.mCurrentView = new TemplateView(this.getContext())).setPolicy(this.mViewPolicy);
        final SliceChildView mCurrentView = this.mCurrentView;
        this.addView((View)mCurrentView, this.getChildLp((View)mCurrentView));
        this.applyConfigurations();
        (this.mActionRow = new ActionRow(this.getContext(), true)).setBackground((Drawable)new ColorDrawable(-1118482));
        final ActionRow mActionRow = this.mActionRow;
        this.addView((View)mActionRow, this.getChildLp((View)mActionRow));
        this.updateActions();
        scaledTouchSlop = ViewConfiguration.get(this.getContext()).getScaledTouchSlop();
        this.mTouchSlopSquared = scaledTouchSlop * scaledTouchSlop;
        this.mHandler = new Handler();
        this.setClipToPadding(false);
        super.setOnClickListener((View$OnClickListener)this);
    }
    
    private void initSliceMetrics(final Slice slice) {
        if (slice != null && slice.getUri() != null) {
            final Slice mCurrentSlice = this.mCurrentSlice;
            if (mCurrentSlice == null || !mCurrentSlice.getUri().equals((Object)slice.getUri())) {
                this.logSliceMetricsVisibilityChange(false);
                this.mCurrentSliceMetrics = SliceMetrics.getInstance(this.getContext(), slice.getUri());
            }
        }
        else {
            this.logSliceMetricsVisibilityChange(false);
            this.mCurrentSliceMetrics = null;
        }
    }
    
    private void logSliceMetricsOnTouch(final SliceItem sliceItem, final EventInfo eventInfo) {
        if (this.mCurrentSliceMetrics != null && sliceItem.getSlice() != null && sliceItem.getSlice().getUri() != null) {
            this.mCurrentSliceMetrics.logTouch(eventInfo.actionType, sliceItem.getSlice().getUri());
        }
    }
    
    private void logSliceMetricsVisibilityChange(final boolean b) {
        final SliceMetrics mCurrentSliceMetrics = this.mCurrentSliceMetrics;
        if (mCurrentSliceMetrics != null) {
            if (b && !this.mCurrentSliceLoggedVisible) {
                mCurrentSliceMetrics.logVisible();
                this.mCurrentSliceLoggedVisible = true;
            }
            if (!b && this.mCurrentSliceLoggedVisible) {
                this.mCurrentSliceMetrics.logHidden();
                this.mCurrentSliceLoggedVisible = false;
            }
        }
    }
    
    public static String modeToString(final int i) {
        if (i == 1) {
            return "MODE SMALL";
        }
        if (i == 2) {
            return "MODE LARGE";
        }
        if (i != 3) {
            final StringBuilder sb = new StringBuilder();
            sb.append("unknown mode: ");
            sb.append(i);
            return sb.toString();
        }
        return "MODE SHORTCUT";
    }
    
    private void refreshLastUpdatedLabel(final boolean b) {
        if (this.mShowLastUpdated) {
            final SliceMetadata mSliceMetadata = this.mSliceMetadata;
            if (mSliceMetadata != null && !mSliceMetadata.neverExpires()) {
                if (b) {
                    final Handler mHandler = this.mHandler;
                    final Runnable mRefreshLastUpdated = this.mRefreshLastUpdated;
                    final boolean expired = this.mSliceMetadata.isExpired();
                    long n = 60000L;
                    if (!expired) {
                        n = 60000L + this.mSliceMetadata.getTimeToExpiry();
                    }
                    mHandler.postDelayed(mRefreshLastUpdated, n);
                }
                else {
                    this.mHandler.removeCallbacks(this.mRefreshLastUpdated);
                }
            }
        }
    }
    
    private void updateActions() {
        if (this.mActions == null) {
            this.mActionRow.setVisibility(8);
            this.mCurrentView.setSliceActions(null);
            this.mCurrentView.setInsets(this.getPaddingStart(), this.getPaddingTop(), this.getPaddingEnd(), this.getPaddingBottom());
            return;
        }
        final ArrayList<SliceAction> list = new ArrayList<SliceAction>((Collection<? extends T>)this.mActions);
        Collections.sort((List<Object>)list, (Comparator<? super Object>)SliceView.SLICE_ACTION_PRIORITY_COMPARATOR);
        if (this.mShowActions && this.getMode() != 3 && this.mActions.size() >= 2) {
            this.mActionRow.setActions(list, this.getTintColor());
            this.mActionRow.setVisibility(0);
            this.mCurrentView.setSliceActions(null);
            this.mCurrentView.setInsets(this.getPaddingStart(), this.getPaddingTop(), this.getPaddingEnd(), 0);
            this.mActionRow.setPaddingRelative(this.getPaddingStart(), 0, this.getPaddingEnd(), this.getPaddingBottom());
        }
        else {
            this.mCurrentView.setSliceActions(list);
            this.mCurrentView.setInsets(this.getPaddingStart(), this.getPaddingTop(), this.getPaddingEnd(), this.getPaddingBottom());
            this.mActionRow.setVisibility(8);
        }
    }
    
    public int getMode() {
        return this.mViewPolicy.getMode();
    }
    
    public boolean isSliceViewClickable() {
        if (this.mOnClickListener == null) {
            final ListContent mListContent = this.mListContent;
            if (mListContent == null || mListContent.getShortcut(this.getContext()) == null) {
                return false;
            }
        }
        return true;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.isShown()) {
            this.logSliceMetricsVisibilityChange(true);
            this.refreshLastUpdatedLabel(true);
        }
    }
    
    public void onChanged(final Slice slice) {
        this.setSlice(slice);
    }
    
    public void onClick(final View view) {
        final ListContent mListContent = this.mListContent;
        if (mListContent != null && mListContent.getShortcut(this.getContext()) != null) {
            try {
                final SliceActionImpl sliceActionImpl = (SliceActionImpl)this.mListContent.getShortcut(this.getContext());
                final SliceItem actionItem = sliceActionImpl.getActionItem();
                if (actionItem != null && actionItem.fireActionInternal(this.getContext(), null)) {
                    this.mCurrentView.setActionLoading(sliceActionImpl.getSliceItem());
                }
                if (actionItem != null && this.mSliceObserver != null && this.mClickInfo != null && this.mClickInfo.length > 1) {
                    final EventInfo eventInfo = new EventInfo(this.getMode(), 3, this.mClickInfo[0], this.mClickInfo[1]);
                    this.mSliceObserver.onSliceAction(eventInfo, sliceActionImpl.getSliceItem());
                    this.logSliceMetricsOnTouch(sliceActionImpl.getSliceItem(), eventInfo);
                }
            }
            catch (PendingIntent$CanceledException ex) {
                Log.e("SliceView", "PendingIntent for slice cannot be sent", (Throwable)ex);
            }
        }
        else {
            final View$OnClickListener mOnClickListener = this.mOnClickListener;
            if (mOnClickListener != null) {
                mOnClickListener.onClick((View)this);
            }
        }
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.logSliceMetricsVisibilityChange(false);
        this.refreshLastUpdatedLabel(false);
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        return (this.mLongClickListener != null && this.handleTouchForLongpress(motionEvent)) || super.onInterceptTouchEvent(motionEvent);
    }
    
    protected void onLayout(final boolean b, int measuredHeight, final int n, final int n2, final int n3) {
        final SliceChildView mCurrentView = this.mCurrentView;
        ((View)mCurrentView).layout(0, 0, ((View)mCurrentView).getMeasuredWidth(), ((View)mCurrentView).getMeasuredHeight());
        if (this.mActionRow.getVisibility() != 8) {
            measuredHeight = ((View)mCurrentView).getMeasuredHeight();
            final ActionRow mActionRow = this.mActionRow;
            mActionRow.layout(0, measuredHeight, mActionRow.getMeasuredWidth(), this.mActionRow.getMeasuredHeight() + measuredHeight);
        }
    }
    
    protected void onMeasure(int n, int paddingBottom) {
        int size = View$MeasureSpec.getSize(n);
        if (3 == this.getMode()) {
            size = this.mShortcutSize + this.getPaddingLeft() + this.getPaddingRight();
        }
        n = this.mActionRow.getVisibility();
        final int n2 = 0;
        int mActionRowHeight;
        if (n != 8) {
            mActionRowHeight = this.mActionRowHeight;
        }
        else {
            mActionRowHeight = 0;
        }
        final int size2 = View$MeasureSpec.getSize(paddingBottom);
        final int mode = View$MeasureSpec.getMode(paddingBottom);
        final ViewGroup$LayoutParams layoutParams = this.getLayoutParams();
        if ((layoutParams != null && layoutParams.height == -2) || mode == 0) {
            n = -1;
        }
        else {
            n = size2;
        }
        this.configureViewPolicy(n);
        paddingBottom = (n = size2 - this.getPaddingTop() - this.getPaddingBottom());
        Label_0256: {
            if (mode != 1073741824) {
                final ListContent mListContent = this.mListContent;
                if (mListContent != null && mListContent.isValid()) {
                    Label_0166: {
                        if (this.getMode() == 3) {
                            n = this.mShortcutSize;
                        }
                        else {
                            n = this.mListContent.getHeight(this.mSliceStyle, this.mViewPolicy) + mActionRowHeight;
                            if (paddingBottom > n || mode == 0) {
                                break Label_0256;
                            }
                            if (this.getMode() == 2) {
                                n = this.mLargeHeight;
                                if (paddingBottom >= n + mActionRowHeight) {
                                    break Label_0166;
                                }
                            }
                            final int mMinTemplateHeight = this.mMinTemplateHeight;
                            if ((n = paddingBottom) <= mMinTemplateHeight) {
                                n = mMinTemplateHeight;
                            }
                            break Label_0256;
                        }
                    }
                    n += mActionRowHeight;
                }
                else {
                    n = mActionRowHeight;
                }
            }
        }
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(size, 1073741824);
        if (mActionRowHeight > 0) {
            paddingBottom = this.getPaddingBottom() + mActionRowHeight;
        }
        else {
            paddingBottom = 0;
        }
        this.mActionRow.measure(measureSpec, View$MeasureSpec.makeMeasureSpec(paddingBottom, 1073741824));
        final int paddingTop = this.getPaddingTop();
        if (mActionRowHeight > 0) {
            paddingBottom = n2;
        }
        else {
            paddingBottom = this.getPaddingBottom();
        }
        this.mCurrentView.measure(measureSpec, View$MeasureSpec.makeMeasureSpec(n + paddingTop + paddingBottom, 1073741824));
        this.setMeasuredDimension(size, this.mCurrentView.getMeasuredHeight() + this.mActionRow.getMeasuredHeight());
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        return (this.mLongClickListener != null && this.handleTouchForLongpress(motionEvent)) || super.onTouchEvent(motionEvent);
    }
    
    protected void onVisibilityChanged(final View view, final int n) {
        super.onVisibilityChanged(view, n);
        if (this.isAttachedToWindow()) {
            final boolean b = true;
            this.logSliceMetricsVisibilityChange(n == 0);
            this.refreshLastUpdatedLabel(n == 0 && b);
        }
    }
    
    protected void onWindowVisibilityChanged(final int n) {
        super.onWindowVisibilityChanged(n);
        final boolean b = true;
        this.logSliceMetricsVisibilityChange(n == 0);
        this.refreshLastUpdatedLabel(n == 0 && b);
    }
    
    public void setClickInfo(final int[] mClickInfo) {
        this.mClickInfo = mClickInfo;
    }
    
    public void setOnClickListener(final View$OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }
    
    public void setOnLongClickListener(final View$OnLongClickListener view$OnLongClickListener) {
        super.setOnLongClickListener(view$OnLongClickListener);
        this.mLongClickListener = view$OnLongClickListener;
    }
    
    public void setShowActionDividers(final boolean mShowActionDividers) {
        this.mShowActionDividers = mShowActionDividers;
        final ListContent mListContent = this.mListContent;
        if (mListContent != null) {
            mListContent.showActionDividers(mShowActionDividers);
        }
    }
    
    public void setShowHeaderDivider(final boolean mShowHeaderDivider) {
        this.mShowHeaderDivider = mShowHeaderDivider;
        final ListContent mListContent = this.mListContent;
        if (mListContent != null) {
            mListContent.showHeaderDivider(mShowHeaderDivider);
        }
    }
    
    public void setShowTitleItems(final boolean mShowTitleItems) {
        this.mShowTitleItems = mShowTitleItems;
        final ListContent mListContent = this.mListContent;
        if (mListContent != null) {
            mListContent.showTitleItems(mShowTitleItems);
        }
    }
    
    public void setSlice(final Slice mCurrentSlice) {
        LocationBasedViewTracker.trackInputFocused(this);
        LocationBasedViewTracker.trackA11yFocus(this);
        this.initSliceMetrics(mCurrentSlice);
        final boolean b = false;
        final boolean b2 = mCurrentSlice != null && this.mCurrentSlice != null && mCurrentSlice.getUri().equals((Object)this.mCurrentSlice.getUri());
        final SliceMetadata mSliceMetadata = this.mSliceMetadata;
        this.mCurrentSlice = mCurrentSlice;
        SliceMetadata from;
        if (mCurrentSlice != null) {
            from = SliceMetadata.from(this.getContext(), this.mCurrentSlice);
        }
        else {
            from = null;
        }
        this.mSliceMetadata = from;
        if (b2) {
            if (mSliceMetadata.getLoadingState() == 2 && from.getLoadingState() == 0) {
                return;
            }
        }
        else {
            this.mCurrentView.resetView();
        }
        final SliceMetadata mSliceMetadata2 = this.mSliceMetadata;
        ListContent listContent;
        if (mSliceMetadata2 != null) {
            listContent = mSliceMetadata2.getListContent();
        }
        else {
            listContent = null;
        }
        this.mListContent = listContent;
        if (this.mShowTitleItems) {
            this.showTitleItems(true);
        }
        if (this.mShowHeaderDivider) {
            this.showHeaderDivider(true);
        }
        if (this.mShowActionDividers) {
            this.showActionDividers(true);
        }
        final ListContent mListContent = this.mListContent;
        if (mListContent != null && mListContent.isValid()) {
            this.mCurrentView.setLoadingActions(null);
            this.mActions = this.mSliceMetadata.getSliceActions();
            this.mCurrentView.setLastUpdated(this.mSliceMetadata.getLastUpdatedTime());
            final SliceChildView mCurrentView = this.mCurrentView;
            boolean showLastUpdated = b;
            if (this.mShowLastUpdated) {
                showLastUpdated = b;
                if (this.mSliceMetadata.isExpired()) {
                    showLastUpdated = true;
                }
            }
            mCurrentView.setShowLastUpdated(showLastUpdated);
            this.mCurrentView.setAllowTwoLines(this.mSliceMetadata.isPermissionSlice());
            this.mCurrentView.setTint(this.getTintColor());
            if (this.mListContent.getLayoutDir() != -1) {
                this.mCurrentView.setLayoutDirection(this.mListContent.getLayoutDir());
            }
            else {
                this.mCurrentView.setLayoutDirection(2);
            }
            this.mCurrentView.setSliceContent(this.mListContent);
            this.updateActions();
            this.logSliceMetricsVisibilityChange(true);
            this.refreshLastUpdatedLabel(true);
            return;
        }
        this.mActions = null;
        this.mCurrentView.resetView();
        this.updateActions();
    }
    
    void setSliceViewPolicy(final SliceViewPolicy mViewPolicy) {
        this.mViewPolicy = mViewPolicy;
    }
    
    @Deprecated
    public void showActionDividers(final boolean showActionDividers) {
        this.setShowActionDividers(showActionDividers);
    }
    
    @Deprecated
    public void showHeaderDivider(final boolean showHeaderDivider) {
        this.setShowHeaderDivider(showHeaderDivider);
    }
    
    @Deprecated
    public void showTitleItems(final boolean showTitleItems) {
        this.setShowTitleItems(showTitleItems);
    }
    
    public interface OnSliceActionListener
    {
        void onSliceAction(final EventInfo p0, final SliceItem p1);
    }
}
