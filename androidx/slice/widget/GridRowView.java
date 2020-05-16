// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.widget;

import android.view.View$MeasureSpec;
import android.app.PendingIntent$CanceledException;
import android.util.Log;
import android.content.Intent;
import android.view.MotionEvent;
import android.os.Build$VERSION;
import androidx.slice.view.R$string;
import androidx.slice.view.R$id;
import android.widget.FrameLayout;
import android.graphics.drawable.Drawable;
import android.widget.TextView;
import android.view.LayoutInflater;
import androidx.slice.core.SliceQuery;
import android.widget.ImageView$ScaleType;
import android.widget.ImageView;
import java.util.Iterator;
import java.util.List;
import android.util.Pair;
import android.view.ViewGroup$MarginLayoutParams;
import android.widget.LinearLayout$LayoutParams;
import android.view.ViewGroup;
import androidx.slice.SliceItem;
import java.util.ArrayList;
import android.content.res.Resources;
import androidx.slice.view.R$dimen;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import android.util.AttributeSet;
import android.content.Context;
import androidx.slice.view.R$layout;
import android.widget.LinearLayout;
import android.view.ViewTreeObserver$OnPreDrawListener;
import android.view.View;
import android.view.View$OnTouchListener;
import android.view.View$OnClickListener;

public class GridRowView extends SliceChildView implements View$OnClickListener, View$OnTouchListener
{
    private static final int TEXT_LAYOUT;
    private static final int TITLE_TEXT_LAYOUT;
    private View mForeground;
    private GridContent mGridContent;
    private int mGutter;
    private int mIconSize;
    private int mLargeImageHeight;
    private int[] mLoc;
    boolean mMaxCellUpdateScheduled;
    int mMaxCells;
    private ViewTreeObserver$OnPreDrawListener mMaxCellsUpdater;
    private int mRowCount;
    private int mRowIndex;
    private int mSmallImageMinWidth;
    private int mSmallImageSize;
    private int mTextPadding;
    private LinearLayout mViewContainer;
    
    static {
        TITLE_TEXT_LAYOUT = R$layout.abc_slice_title;
        TEXT_LAYOUT = R$layout.abc_slice_secondary_text;
    }
    
    public GridRowView(final Context context) {
        this(context, null);
    }
    
    public GridRowView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mMaxCells = -1;
        this.mLoc = new int[2];
        this.mMaxCellsUpdater = (ViewTreeObserver$OnPreDrawListener)new ViewTreeObserver$OnPreDrawListener() {
            public boolean onPreDraw() {
                final GridRowView this$0 = GridRowView.this;
                this$0.mMaxCells = this$0.getMaxCells();
                GridRowView.this.populateViews();
                GridRowView.this.getViewTreeObserver().removeOnPreDrawListener((ViewTreeObserver$OnPreDrawListener)this);
                GridRowView.this.mMaxCellUpdateScheduled = false;
                return true;
            }
        };
        final Resources resources = this.getContext().getResources();
        (this.mViewContainer = new LinearLayout(this.getContext())).setOrientation(0);
        this.addView((View)this.mViewContainer, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-1, -1));
        this.mViewContainer.setGravity(16);
        this.mIconSize = resources.getDimensionPixelSize(R$dimen.abc_slice_icon_size);
        this.mSmallImageSize = resources.getDimensionPixelSize(R$dimen.abc_slice_small_image_size);
        this.mLargeImageHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_grid_image_only_height);
        this.mSmallImageMinWidth = resources.getDimensionPixelSize(R$dimen.abc_slice_grid_image_min_width);
        this.mGutter = resources.getDimensionPixelSize(R$dimen.abc_slice_grid_gutter);
        this.mTextPadding = resources.getDimensionPixelSize(R$dimen.abc_slice_grid_text_padding);
        this.addView(this.mForeground = new View(this.getContext()), (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-1, -1));
    }
    
    private void addCell(final GridContent.CellContent cellContent, final int n, final int n2) {
        int n3;
        if (this.getMode() == 1 && this.mGridContent.hasImage()) {
            n3 = 1;
        }
        else {
            n3 = 2;
        }
        final LinearLayout linearLayout = new LinearLayout(this.getContext());
        linearLayout.setOrientation(1);
        linearLayout.setGravity(1);
        final ArrayList<SliceItem> cellItems = cellContent.getCellItems();
        final SliceItem contentIntent = cellContent.getContentIntent();
        final boolean b = cellItems.size() == 1;
        final String s = "text";
        List<SliceItem> list;
        if (!b && this.getMode() == 1) {
            list = new ArrayList<SliceItem>();
            for (final SliceItem sliceItem : cellItems) {
                if ("text".equals(sliceItem.getFormat())) {
                    list.add(sliceItem);
                }
            }
            final Iterator<SliceItem> iterator2 = list.iterator();
            while (list.size() > n3) {
                if (!iterator2.next().hasAnyHints("title", "large")) {
                    iterator2.remove();
                }
            }
        }
        else {
            list = null;
        }
        int n4 = 0;
        int n5 = 0;
        int i = 0;
        final SliceItem sliceItem2 = null;
        boolean b2 = false;
        final List<SliceItem> list2 = list;
        SliceItem sliceItem3 = sliceItem2;
        while (i < cellItems.size()) {
            final SliceItem sliceItem4 = cellItems.get(i);
            final String format = sliceItem4.getFormat();
            final int determinePadding = this.determinePadding(sliceItem3);
            Label_0419: {
                if (n5 < n3 && (s.equals(format) || "long".equals(format))) {
                    if (list2 != null && !list2.contains(sliceItem4)) {
                        break Label_0419;
                    }
                    if (!this.addItem(sliceItem4, super.mTintColor, (ViewGroup)linearLayout, determinePadding, b)) {
                        break Label_0419;
                    }
                    ++n5;
                }
                else {
                    final int n6 = n4;
                    if (n6 >= 1 || !"image".equals(sliceItem4.getFormat()) || !this.addItem(sliceItem4, super.mTintColor, (ViewGroup)linearLayout, 0, b)) {
                        break Label_0419;
                    }
                    n4 = n6 + 1;
                }
                sliceItem3 = sliceItem4;
                b2 = true;
            }
            ++i;
        }
        if (b2) {
            final CharSequence contentDescription = cellContent.getContentDescription();
            if (contentDescription != null) {
                linearLayout.setContentDescription(contentDescription);
            }
            this.mViewContainer.addView((View)linearLayout, (ViewGroup$LayoutParams)new LinearLayout$LayoutParams(0, -2, 1.0f));
            if (n != n2 - 1) {
                final ViewGroup$MarginLayoutParams layoutParams = (ViewGroup$MarginLayoutParams)linearLayout.getLayoutParams();
                layoutParams.setMarginEnd(this.mGutter);
                linearLayout.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
            }
            if (contentIntent != null) {
                final EventInfo eventInfo = new EventInfo(this.getMode(), 1, 1, this.mRowIndex);
                eventInfo.setPosition(2, n, n2);
                linearLayout.setTag((Object)new Pair((Object)contentIntent, (Object)eventInfo));
                this.makeClickable((View)linearLayout, true);
            }
        }
    }
    
    private boolean addItem(final SliceItem sliceItem, int n, final ViewGroup viewGroup, int n2, final boolean b) {
        final String format = sliceItem.getFormat();
        final boolean equals = "text".equals(format);
        final boolean b2 = true;
        final View view = null;
        Object o;
        if (!equals && !"long".equals(format)) {
            o = view;
            if ("image".equals(format)) {
                o = view;
                if (sliceItem.getIcon() != null) {
                    final Drawable loadDrawable = sliceItem.getIcon().loadDrawable(this.getContext());
                    o = view;
                    if (loadDrawable != null) {
                        final ImageView imageView = new ImageView(this.getContext());
                        imageView.setImageDrawable(loadDrawable);
                        LinearLayout$LayoutParams linearLayout$LayoutParams;
                        if (sliceItem.hasHint("raw")) {
                            imageView.setScaleType(ImageView$ScaleType.CENTER_INSIDE);
                            linearLayout$LayoutParams = new LinearLayout$LayoutParams(-1, -2);
                        }
                        else if (sliceItem.hasHint("large")) {
                            imageView.setScaleType(ImageView$ScaleType.CENTER_CROP);
                            if (b) {
                                n2 = -1;
                            }
                            else {
                                n2 = this.mLargeImageHeight;
                            }
                            linearLayout$LayoutParams = new LinearLayout$LayoutParams(-1, n2);
                        }
                        else {
                            final boolean b3 = sliceItem.hasHint("no_tint") ^ true;
                            if (!b3) {
                                n2 = this.mSmallImageSize;
                            }
                            else {
                                n2 = this.mIconSize;
                            }
                            ImageView$ScaleType scaleType;
                            if (b3) {
                                scaleType = ImageView$ScaleType.CENTER_INSIDE;
                            }
                            else {
                                scaleType = ImageView$ScaleType.CENTER_CROP;
                            }
                            imageView.setScaleType(scaleType);
                            linearLayout$LayoutParams = new LinearLayout$LayoutParams(n2, n2);
                        }
                        if (n != -1 && !sliceItem.hasHint("no_tint")) {
                            imageView.setColorFilter(n);
                        }
                        viewGroup.addView((View)imageView, (ViewGroup$LayoutParams)linearLayout$LayoutParams);
                        o = imageView;
                    }
                }
            }
        }
        else {
            final boolean hasAnyHints = SliceQuery.hasAnyHints(sliceItem, "large", "title");
            final LayoutInflater from = LayoutInflater.from(this.getContext());
            if (hasAnyHints) {
                n = GridRowView.TITLE_TEXT_LAYOUT;
            }
            else {
                n = GridRowView.TEXT_LAYOUT;
            }
            o = from.inflate(n, (ViewGroup)null);
            final SliceStyle mSliceStyle = super.mSliceStyle;
            if (mSliceStyle != null) {
                if (hasAnyHints) {
                    n = mSliceStyle.getGridTitleSize();
                }
                else {
                    n = mSliceStyle.getGridSubtitleSize();
                }
                ((TextView)o).setTextSize(0, (float)n);
                if (hasAnyHints) {
                    n = super.mSliceStyle.getTitleColor();
                }
                else {
                    n = super.mSliceStyle.getSubtitleColor();
                }
                ((TextView)o).setTextColor(n);
            }
            CharSequence text;
            if ("long".equals(format)) {
                text = SliceViewUtil.getTimestampString(this.getContext(), sliceItem.getLong());
            }
            else {
                text = sliceItem.getSanitizedText();
            }
            ((TextView)o).setText(text);
            viewGroup.addView((View)o);
            ((TextView)o).setPadding(0, n2, 0, 0);
        }
        return o != null && b2;
    }
    
    private void addSeeMoreCount(final int i) {
        final LinearLayout mViewContainer = this.mViewContainer;
        final View child = mViewContainer.getChildAt(mViewContainer.getChildCount() - 1);
        this.mViewContainer.removeView(child);
        final SliceItem seeMoreItem = this.mGridContent.getSeeMoreItem();
        final int childCount = this.mViewContainer.getChildCount();
        final int mMaxCells = this.mMaxCells;
        if (("slice".equals(seeMoreItem.getFormat()) || "action".equals(seeMoreItem.getFormat())) && seeMoreItem.getSlice().getItems().size() > 0) {
            this.addCell(new GridContent.CellContent(seeMoreItem), childCount, mMaxCells);
            return;
        }
        final LayoutInflater from = LayoutInflater.from(this.getContext());
        Object o;
        TextView textView;
        if (this.mGridContent.isAllImages()) {
            o = from.inflate(R$layout.abc_slice_grid_see_more_overlay, (ViewGroup)this.mViewContainer, false);
            ((ViewGroup)o).addView(child, 0, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-1, -1));
            textView = (TextView)((ViewGroup)o).findViewById(R$id.text_see_more_count);
        }
        else {
            final LinearLayout linearLayout = (LinearLayout)from.inflate(R$layout.abc_slice_grid_see_more, (ViewGroup)this.mViewContainer, false);
            final TextView textView2 = (TextView)((ViewGroup)linearLayout).findViewById(R$id.text_see_more_count);
            final TextView textView3 = (TextView)((ViewGroup)linearLayout).findViewById(R$id.text_see_more);
            final SliceStyle mSliceStyle = super.mSliceStyle;
            textView = textView2;
            o = linearLayout;
            if (mSliceStyle != null) {
                textView3.setTextSize(0, (float)mSliceStyle.getGridTitleSize());
                textView3.setTextColor(super.mSliceStyle.getTitleColor());
                o = linearLayout;
                textView = textView2;
            }
        }
        this.mViewContainer.addView((View)o, (ViewGroup$LayoutParams)new LinearLayout$LayoutParams(0, -1, 1.0f));
        textView.setText((CharSequence)this.getResources().getString(R$string.abc_slice_more_content, new Object[] { i }));
        final EventInfo eventInfo = new EventInfo(this.getMode(), 4, 1, this.mRowIndex);
        eventInfo.setPosition(2, childCount, mMaxCells);
        ((ViewGroup)o).setTag((Object)new Pair((Object)seeMoreItem, (Object)eventInfo));
        this.makeClickable((View)o, true);
    }
    
    private int determinePadding(final SliceItem sliceItem) {
        int verticalGridTextPadding = 0;
        if (sliceItem == null) {
            return 0;
        }
        if ("image".equals(sliceItem.getFormat())) {
            return this.mTextPadding;
        }
        if (!"text".equals(sliceItem.getFormat()) && !"long".equals(sliceItem.getFormat())) {
            return 0;
        }
        final SliceStyle mSliceStyle = super.mSliceStyle;
        if (mSliceStyle != null) {
            verticalGridTextPadding = mSliceStyle.getVerticalGridTextPadding();
        }
        return verticalGridTextPadding;
    }
    
    private int getExtraBottomPadding() {
        final GridContent mGridContent = this.mGridContent;
        int gridBottomPadding;
        final int n = gridBottomPadding = 0;
        if (mGridContent != null) {
            gridBottomPadding = n;
            if (mGridContent.isAllImages()) {
                if (this.mRowIndex != this.mRowCount - 1) {
                    gridBottomPadding = n;
                    if (this.getMode() != 1) {
                        return gridBottomPadding;
                    }
                }
                final SliceStyle mSliceStyle = super.mSliceStyle;
                gridBottomPadding = n;
                if (mSliceStyle != null) {
                    gridBottomPadding = mSliceStyle.getGridBottomPadding();
                }
            }
        }
        return gridBottomPadding;
    }
    
    private int getExtraTopPadding() {
        final GridContent mGridContent = this.mGridContent;
        int gridTopPadding;
        final int n = gridTopPadding = 0;
        if (mGridContent != null) {
            gridTopPadding = n;
            if (mGridContent.isAllImages()) {
                gridTopPadding = n;
                if (this.mRowIndex == 0) {
                    final SliceStyle mSliceStyle = super.mSliceStyle;
                    gridTopPadding = n;
                    if (mSliceStyle != null) {
                        gridTopPadding = mSliceStyle.getGridTopPadding();
                    }
                }
            }
        }
        return gridTopPadding;
    }
    
    private void makeClickable(final View view, final boolean clickable) {
        final Drawable drawable = null;
        Object onClickListener;
        if (clickable) {
            onClickListener = this;
        }
        else {
            onClickListener = null;
        }
        view.setOnClickListener((View$OnClickListener)onClickListener);
        int n = 16843534;
        if (Build$VERSION.SDK_INT >= 21) {
            n = 16843868;
        }
        Drawable drawable2 = drawable;
        if (clickable) {
            drawable2 = SliceViewUtil.getDrawable(this.getContext(), n);
        }
        view.setBackground(drawable2);
        view.setClickable(clickable);
    }
    
    private void makeEntireGridClickable(final boolean clickable) {
        final LinearLayout mViewContainer = this.mViewContainer;
        final Drawable drawable = null;
        Object onTouchListener;
        if (clickable) {
            onTouchListener = this;
        }
        else {
            onTouchListener = null;
        }
        mViewContainer.setOnTouchListener((View$OnTouchListener)onTouchListener);
        final LinearLayout mViewContainer2 = this.mViewContainer;
        Object onClickListener;
        if (clickable) {
            onClickListener = this;
        }
        else {
            onClickListener = null;
        }
        mViewContainer2.setOnClickListener((View$OnClickListener)onClickListener);
        final View mForeground = this.mForeground;
        Drawable drawable2 = drawable;
        if (clickable) {
            drawable2 = SliceViewUtil.getDrawable(this.getContext(), 16843534);
        }
        mForeground.setBackground(drawable2);
        this.mViewContainer.setClickable(clickable);
    }
    
    private void onForegroundActivated(final MotionEvent motionEvent) {
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
    
    private boolean scheduleMaxCellsUpdate() {
        final GridContent mGridContent = this.mGridContent;
        if (mGridContent == null || !mGridContent.isValid()) {
            return true;
        }
        if (this.getWidth() == 0) {
            this.mMaxCellUpdateScheduled = true;
            this.getViewTreeObserver().addOnPreDrawListener(this.mMaxCellsUpdater);
            return true;
        }
        this.mMaxCells = this.getMaxCells();
        return false;
    }
    
    int getMaxCells() {
        final GridContent mGridContent = this.mGridContent;
        if (mGridContent != null && mGridContent.isValid() && this.getWidth() != 0) {
            final int size = this.mGridContent.getGridContent().size();
            int n = 1;
            if (size > 1) {
                int n2;
                if (this.mGridContent.getLargestImageMode() == 2) {
                    n2 = this.mLargeImageHeight;
                }
                else {
                    n2 = this.mSmallImageMinWidth;
                }
                n = this.getWidth() / (n2 + this.mGutter);
            }
            return n;
        }
        return -1;
    }
    
    public void onClick(final View view) {
        final Pair pair = (Pair)view.getTag();
        final SliceItem sliceItem = (SliceItem)pair.first;
        final EventInfo eventInfo = (EventInfo)pair.second;
        if (sliceItem != null) {
            final SliceItem find = SliceQuery.find(sliceItem, "action", null, (String)null);
            if (find != null) {
                try {
                    find.fireAction(null, null);
                    if (super.mObserver != null) {
                        super.mObserver.onSliceAction(eventInfo, find);
                    }
                }
                catch (PendingIntent$CanceledException ex) {
                    Log.e("GridRowView", "PendingIntent for slice cannot be sent", (Throwable)ex);
                }
            }
        }
    }
    
    protected void onMeasure(final int n, int measureSpec) {
        final int height = this.mGridContent.getHeight(super.mSliceStyle, super.mViewPolicy) + super.mInsetTop + super.mInsetBottom;
        measureSpec = View$MeasureSpec.makeMeasureSpec(height, 1073741824);
        this.mViewContainer.getLayoutParams().height = height;
        super.onMeasure(n, measureSpec);
    }
    
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        this.onForegroundActivated(motionEvent);
        return false;
    }
    
    void populateViews() {
        final GridContent mGridContent = this.mGridContent;
        if (mGridContent == null || !mGridContent.isValid()) {
            this.resetView();
            return;
        }
        if (this.scheduleMaxCellsUpdate()) {
            return;
        }
        if (this.mGridContent.getLayoutDir() != -1) {
            this.setLayoutDirection(this.mGridContent.getLayoutDir());
        }
        final SliceItem contentIntent = this.mGridContent.getContentIntent();
        boolean b = true;
        if (contentIntent != null) {
            this.mViewContainer.setTag((Object)new Pair((Object)this.mGridContent.getContentIntent(), (Object)new EventInfo(this.getMode(), 3, 1, this.mRowIndex)));
            this.makeEntireGridClickable(true);
        }
        final CharSequence contentDescription = this.mGridContent.getContentDescription();
        if (contentDescription != null) {
            this.mViewContainer.setContentDescription(contentDescription);
        }
        final ArrayList<GridContent.CellContent> gridContent = this.mGridContent.getGridContent();
        if (this.mGridContent.getLargestImageMode() == 2) {
            this.mViewContainer.setGravity(48);
        }
        else {
            this.mViewContainer.setGravity(16);
        }
        final int mMaxCells = this.mMaxCells;
        final SliceItem seeMoreItem = this.mGridContent.getSeeMoreItem();
        int i = 0;
        if (seeMoreItem == null) {
            b = false;
        }
        while (i < gridContent.size()) {
            if (this.mViewContainer.getChildCount() >= mMaxCells) {
                if (b) {
                    this.addSeeMoreCount(gridContent.size() - mMaxCells);
                    break;
                }
                break;
            }
            else {
                this.addCell(gridContent.get(i), i, Math.min(gridContent.size(), mMaxCells));
                ++i;
            }
        }
    }
    
    @Override
    public void resetView() {
        if (this.mMaxCellUpdateScheduled) {
            this.mMaxCellUpdateScheduled = false;
            this.getViewTreeObserver().removeOnPreDrawListener(this.mMaxCellsUpdater);
        }
        this.mViewContainer.removeAllViews();
        this.setLayoutDirection(2);
        this.makeEntireGridClickable(false);
    }
    
    @Override
    public void setInsets(final int n, final int n2, final int n3, final int n4) {
        super.setInsets(n, n2, n3, n4);
        this.mViewContainer.setPadding(n, n2 + this.getExtraTopPadding(), n3, n4 + this.getExtraBottomPadding());
    }
    
    @Override
    public void setSliceItem(final SliceContent sliceContent, final boolean b, final int mRowIndex, final int mRowCount, final SliceView.OnSliceActionListener sliceActionListener) {
        this.resetView();
        this.setSliceActionListener(sliceActionListener);
        this.mRowIndex = mRowIndex;
        this.mRowCount = mRowCount;
        this.mGridContent = (GridContent)sliceContent;
        if (!this.scheduleMaxCellsUpdate()) {
            this.populateViews();
        }
        this.mViewContainer.setPadding(super.mInsetStart, super.mInsetTop + this.getExtraTopPadding(), super.mInsetEnd, super.mInsetBottom + this.getExtraBottomPadding());
    }
    
    @Override
    public void setTint(final int tint) {
        super.setTint(tint);
        if (this.mGridContent != null) {
            this.resetView();
            this.populateViews();
        }
    }
}
