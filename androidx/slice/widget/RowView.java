// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.widget;

import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.SliceStructure;
import android.widget.FrameLayout;
import android.widget.AdapterView;
import android.app.PendingIntent$CanceledException;
import android.util.Log;
import android.content.Intent;
import android.widget.Button;
import android.view.ViewGroup$MarginLayoutParams;
import android.view.View$MeasureSpec;
import androidx.slice.view.R$plurals;
import android.text.style.StyleSpan;
import android.text.SpannableString;
import android.text.TextUtils;
import androidx.slice.view.R$string;
import android.widget.SpinnerAdapter;
import android.widget.ArrayAdapter;
import androidx.slice.core.SliceQuery;
import androidx.core.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.graphics.drawable.Drawable;
import androidx.versionedparcelable.CustomVersionedParcelable;
import android.view.ViewGroup$LayoutParams;
import android.widget.LinearLayout$LayoutParams;
import android.widget.ImageView;
import androidx.core.view.ViewCompat;
import androidx.slice.view.R$id;
import android.view.ViewGroup;
import androidx.slice.view.R$layout;
import android.view.LayoutInflater;
import androidx.slice.view.R$dimen;
import android.widget.SeekBar;
import java.util.HashSet;
import android.content.Context;
import android.os.Build$VERSION;
import android.widget.Spinner;
import java.util.ArrayList;
import android.widget.SeekBar$OnSeekBarChangeListener;
import androidx.slice.SliceItem;
import java.util.Set;
import android.widget.TextView;
import androidx.slice.core.SliceAction;
import java.util.List;
import android.os.Handler;
import android.widget.LinearLayout;
import androidx.slice.core.SliceActionImpl;
import android.util.ArrayMap;
import android.widget.ProgressBar;
import android.view.View;
import android.widget.AdapterView$OnItemSelectedListener;
import android.view.View$OnClickListener;

public class RowView extends SliceChildView implements View$OnClickListener, AdapterView$OnItemSelectedListener
{
    private static final boolean sCanSpecifyLargerRangeBarHeight;
    private View mActionDivider;
    private ProgressBar mActionSpinner;
    private ArrayMap<SliceActionImpl, SliceActionView> mActions;
    private boolean mAllowTwoLines;
    private View mBottomDivider;
    private LinearLayout mContent;
    private LinearLayout mEndContainer;
    Handler mHandler;
    private List<SliceAction> mHeaderActions;
    private int mIconSize;
    private int mImageSize;
    private boolean mIsHeader;
    boolean mIsRangeSliding;
    long mLastSentRangeUpdate;
    private TextView mLastUpdatedText;
    protected Set<SliceItem> mLoadingActions;
    private int mMeasuredRangeHeight;
    private TextView mPrimaryText;
    private ProgressBar mRangeBar;
    boolean mRangeHasPendingUpdate;
    private SliceItem mRangeItem;
    int mRangeMaxValue;
    int mRangeMinValue;
    Runnable mRangeUpdater;
    boolean mRangeUpdaterRunning;
    int mRangeValue;
    private LinearLayout mRootView;
    private SliceActionImpl mRowAction;
    RowContent mRowContent;
    int mRowIndex;
    private TextView mSecondaryText;
    private View mSeeMoreView;
    private SeekBar$OnSeekBarChangeListener mSeekBarChangeListener;
    private SliceItem mSelectionItem;
    private ArrayList<String> mSelectionOptionKeys;
    private ArrayList<CharSequence> mSelectionOptionValues;
    private Spinner mSelectionSpinner;
    boolean mShowActionSpinner;
    private LinearLayout mStartContainer;
    private SliceItem mStartItem;
    private LinearLayout mSubContent;
    private ArrayMap<SliceActionImpl, SliceActionView> mToggles;
    
    static {
        sCanSpecifyLargerRangeBarHeight = (Build$VERSION.SDK_INT >= 23);
    }
    
    public RowView(final Context context) {
        super(context);
        this.mToggles = (ArrayMap<SliceActionImpl, SliceActionView>)new ArrayMap();
        this.mActions = (ArrayMap<SliceActionImpl, SliceActionView>)new ArrayMap();
        this.mLoadingActions = new HashSet<SliceItem>();
        this.mRangeUpdater = new Runnable() {
            @Override
            public void run() {
                RowView.this.sendSliderValue();
                RowView.this.mRangeUpdaterRunning = false;
            }
        };
        this.mSeekBarChangeListener = (SeekBar$OnSeekBarChangeListener)new SeekBar$OnSeekBarChangeListener() {
            public void onProgressChanged(final SeekBar seekBar, final int n, final boolean b) {
                final RowView this$0 = RowView.this;
                this$0.mRangeValue = n + this$0.mRangeMinValue;
                final long currentTimeMillis = System.currentTimeMillis();
                final RowView this$2 = RowView.this;
                final long mLastSentRangeUpdate = this$2.mLastSentRangeUpdate;
                if (mLastSentRangeUpdate != 0L && currentTimeMillis - mLastSentRangeUpdate > 200L) {
                    this$2.mRangeUpdaterRunning = false;
                    this$2.mHandler.removeCallbacks(this$2.mRangeUpdater);
                    RowView.this.sendSliderValue();
                }
                else {
                    final RowView this$3 = RowView.this;
                    if (!this$3.mRangeUpdaterRunning) {
                        this$3.mRangeUpdaterRunning = true;
                        this$3.mHandler.postDelayed(this$3.mRangeUpdater, 200L);
                    }
                }
            }
            
            public void onStartTrackingTouch(final SeekBar seekBar) {
                RowView.this.mIsRangeSliding = true;
            }
            
            public void onStopTrackingTouch(final SeekBar seekBar) {
                final RowView this$0 = RowView.this;
                this$0.mIsRangeSliding = false;
                if (this$0.mRangeUpdaterRunning || this$0.mRangeHasPendingUpdate) {
                    final RowView this$2 = RowView.this;
                    this$2.mRangeUpdaterRunning = false;
                    this$2.mRangeHasPendingUpdate = false;
                    this$2.mHandler.removeCallbacks(this$2.mRangeUpdater);
                    final RowView this$3 = RowView.this;
                    final int progress = seekBar.getProgress();
                    final RowView this$4 = RowView.this;
                    this$3.mRangeValue = progress + this$4.mRangeMinValue;
                    this$4.sendSliderValue();
                }
            }
        };
        this.mIconSize = this.getContext().getResources().getDimensionPixelSize(R$dimen.abc_slice_icon_size);
        this.mImageSize = this.getContext().getResources().getDimensionPixelSize(R$dimen.abc_slice_small_image_size);
        this.addView((View)(this.mRootView = (LinearLayout)LayoutInflater.from(context).inflate(R$layout.abc_slice_small_template, (ViewGroup)this, false)));
        this.mStartContainer = (LinearLayout)this.findViewById(R$id.icon_frame);
        this.mContent = (LinearLayout)this.findViewById(16908290);
        this.mSubContent = (LinearLayout)this.findViewById(R$id.subcontent);
        this.mPrimaryText = (TextView)this.findViewById(16908310);
        this.mSecondaryText = (TextView)this.findViewById(16908304);
        this.mLastUpdatedText = (TextView)this.findViewById(R$id.last_updated);
        this.mBottomDivider = this.findViewById(R$id.bottom_divider);
        this.mActionDivider = this.findViewById(R$id.action_divider);
        this.mActionSpinner = (ProgressBar)this.findViewById(R$id.action_sent_indicator);
        SliceViewUtil.tintIndeterminateProgressBar(this.getContext(), this.mActionSpinner);
        this.mEndContainer = (LinearLayout)this.findViewById(16908312);
        ViewCompat.setImportantForAccessibility((View)this, 2);
        ViewCompat.setImportantForAccessibility((View)this.mContent, 2);
    }
    
    private void addAction(final SliceActionImpl sliceActionImpl, final int n, final ViewGroup viewGroup, final boolean b) {
        final SliceActionView sliceActionView = new SliceActionView(this.getContext());
        viewGroup.addView((View)sliceActionView);
        if (viewGroup.getVisibility() == 8) {
            viewGroup.setVisibility(0);
        }
        final boolean toggle = sliceActionImpl.isToggle();
        int n2;
        if (toggle) {
            n2 = 3;
        }
        else {
            n2 = 0;
        }
        final EventInfo eventInfo = new EventInfo(this.getMode(), (toggle ^ true) ? 1 : 0, n2, this.mRowIndex);
        if (b) {
            eventInfo.setPosition(0, 0, 1);
        }
        sliceActionView.setAction(sliceActionImpl, eventInfo, super.mObserver, n, super.mLoadingListener);
        if (this.mLoadingActions.contains(sliceActionImpl.getSliceItem())) {
            sliceActionView.setLoading(true);
        }
        if (toggle) {
            this.mToggles.put((Object)sliceActionImpl, (Object)sliceActionView);
        }
        else {
            this.mActions.put((Object)sliceActionImpl, (Object)sliceActionView);
        }
    }
    
    private boolean addItem(SliceItem sliceItem, int height, final boolean b) {
        LinearLayout linearLayout;
        if (b) {
            linearLayout = this.mStartContainer;
        }
        else {
            linearLayout = this.mEndContainer;
        }
        final boolean equals = "slice".equals(sliceItem.getFormat());
        final boolean b2 = true;
        SliceItem sliceItem2 = null;
        Label_0118: {
            if (!equals) {
                sliceItem2 = sliceItem;
                if (!"action".equals(sliceItem.getFormat())) {
                    break Label_0118;
                }
            }
            if (sliceItem.hasHint("shortcut")) {
                this.addAction(new SliceActionImpl(sliceItem), height, (ViewGroup)linearLayout, b);
                return true;
            }
            if (sliceItem.getSlice().getItems().size() == 0) {
                return false;
            }
            sliceItem2 = sliceItem.getSlice().getItems().get(0);
        }
        final boolean equals2 = "image".equals(sliceItem2.getFormat());
        final TextView textView = null;
        CustomVersionedParcelable icon;
        if (equals2) {
            icon = sliceItem2.getIcon();
            sliceItem = null;
        }
        else if ("long".equals(sliceItem2.getFormat())) {
            sliceItem = sliceItem2;
            icon = null;
        }
        else {
            icon = (sliceItem = null);
        }
        TextView textView2;
        if (icon != null) {
            final boolean b3 = sliceItem2.hasHint("no_tint") ^ true;
            final boolean hasHint = sliceItem2.hasHint("raw");
            final float density = this.getResources().getDisplayMetrics().density;
            final ImageView imageView = new ImageView(this.getContext());
            final Drawable loadDrawable = ((IconCompat)icon).loadDrawable(this.getContext());
            imageView.setImageDrawable(loadDrawable);
            if (b3 && height != -1) {
                imageView.setColorFilter(height);
            }
            if (this.mIsRangeSliding) {
                ((ViewGroup)linearLayout).removeAllViews();
                ((ViewGroup)linearLayout).addView((View)imageView);
            }
            else {
                ((ViewGroup)linearLayout).addView((View)imageView);
            }
            final LinearLayout$LayoutParams layoutParams = (LinearLayout$LayoutParams)imageView.getLayoutParams();
            if (hasHint) {
                height = Math.round(loadDrawable.getIntrinsicWidth() / density);
            }
            else {
                height = this.mImageSize;
            }
            layoutParams.width = height;
            if (hasHint) {
                height = Math.round(loadDrawable.getIntrinsicHeight() / density);
            }
            else {
                height = this.mImageSize;
            }
            layoutParams.height = height;
            imageView.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
            if (b3) {
                height = this.mIconSize / 2;
            }
            else {
                height = 0;
            }
            imageView.setPadding(height, height, height, height);
            textView2 = (TextView)imageView;
        }
        else {
            textView2 = textView;
            if (sliceItem != null) {
                textView2 = new TextView(this.getContext());
                textView2.setText(SliceViewUtil.getTimestampString(this.getContext(), sliceItem2.getLong()));
                final SliceStyle mSliceStyle = super.mSliceStyle;
                if (mSliceStyle != null) {
                    textView2.setTextSize(0, (float)mSliceStyle.getSubtitleSize());
                    textView2.setTextColor(super.mSliceStyle.getSubtitleColor());
                }
                ((ViewGroup)linearLayout).addView((View)textView2);
            }
        }
        return textView2 != null && b2;
    }
    
    private void addRange() {
        if (this.mHandler == null) {
            this.mHandler = new Handler();
        }
        final boolean equals = "action".equals(this.mRangeItem.getFormat());
        final boolean b = this.mStartItem == null;
        Object mRangeBar;
        if (equals) {
            if (b) {
                mRangeBar = new SeekBar(this.getContext());
            }
            else {
                mRangeBar = LayoutInflater.from(this.getContext()).inflate(R$layout.abc_slice_seekbar_view, (ViewGroup)this, false);
            }
        }
        else {
            mRangeBar = new ProgressBar(this.getContext(), (AttributeSet)null, 16842872);
        }
        final Drawable wrap = DrawableCompat.wrap(((ProgressBar)mRangeBar).getProgressDrawable());
        final int mTintColor = super.mTintColor;
        if (mTintColor != -1 && wrap != null) {
            DrawableCompat.setTint(wrap, mTintColor);
            ((ProgressBar)mRangeBar).setProgressDrawable(wrap);
        }
        ((ProgressBar)mRangeBar).setMax(this.mRangeMaxValue - this.mRangeMinValue);
        ((ProgressBar)mRangeBar).setProgress(this.mRangeValue);
        ((ProgressBar)mRangeBar).setVisibility(0);
        if (this.mStartItem == null) {
            this.addView((View)mRangeBar);
        }
        else {
            this.mSubContent.setVisibility(8);
            this.mContent.addView((View)mRangeBar, 1);
        }
        this.mRangeBar = (ProgressBar)mRangeBar;
        if (equals) {
            final SliceItem inputRangeThumb = this.mRowContent.getInputRangeThumb();
            final SeekBar seekBar = (SeekBar)this.mRangeBar;
            if (inputRangeThumb != null && inputRangeThumb.getIcon() != null) {
                final Drawable loadDrawable = inputRangeThumb.getIcon().loadDrawable(this.getContext());
                if (loadDrawable != null) {
                    seekBar.setThumb(loadDrawable);
                }
            }
            final Drawable wrap2 = DrawableCompat.wrap(seekBar.getThumb());
            final int mTintColor2 = super.mTintColor;
            if (mTintColor2 != -1 && wrap2 != null) {
                DrawableCompat.setTint(wrap2, mTintColor2);
                seekBar.setThumb(wrap2);
            }
            seekBar.setOnSeekBarChangeListener(this.mSeekBarChangeListener);
        }
    }
    
    private void addSelection(SliceItem subtype) {
        if (this.mHandler == null) {
            this.mHandler = new Handler();
        }
        this.mSelectionOptionKeys = new ArrayList<String>();
        this.mSelectionOptionValues = new ArrayList<CharSequence>();
        final List<SliceItem> items = subtype.getSlice().getItems();
        for (int i = 0; i < items.size(); ++i) {
            final SliceItem sliceItem = items.get(i);
            if (sliceItem.hasHint("selection_option")) {
                subtype = SliceQuery.findSubtype(sliceItem, "text", "selection_option_key");
                final SliceItem subtype2 = SliceQuery.findSubtype(sliceItem, "text", "selection_option_value");
                if (subtype != null) {
                    if (subtype2 != null) {
                        this.mSelectionOptionKeys.add(subtype.getText().toString());
                        this.mSelectionOptionValues.add(subtype2.getSanitizedText());
                    }
                }
            }
        }
        this.mSelectionSpinner = (Spinner)LayoutInflater.from(this.getContext()).inflate(R$layout.abc_slice_row_selection, (ViewGroup)this, false);
        final ArrayAdapter adapter = new ArrayAdapter(this.getContext(), R$layout.abc_slice_row_selection_text, (List)this.mSelectionOptionValues);
        adapter.setDropDownViewResource(R$layout.abc_slice_row_selection_dropdown_text);
        this.mSelectionSpinner.setAdapter((SpinnerAdapter)adapter);
        this.addView((View)this.mSelectionSpinner);
        this.mSelectionSpinner.setOnItemSelectedListener((AdapterView$OnItemSelectedListener)this);
    }
    
    private void addSubtitle(final boolean b) {
        final RowContent mRowContent = this.mRowContent;
        if (mRowContent != null) {
            if (mRowContent.getRange() == null || this.mStartItem == null) {
                final int mode = this.getMode();
                final boolean b2 = true;
                SliceItem sliceItem;
                if (mode == 1) {
                    sliceItem = this.mRowContent.getSummaryItem();
                }
                else {
                    sliceItem = this.mRowContent.getSubtitleItem();
                }
                final boolean mShowLastUpdated = super.mShowLastUpdated;
                CharSequence sanitizedText = null;
                String string = null;
                Label_0124: {
                    if (mShowLastUpdated) {
                        final long mLastUpdated = super.mLastUpdated;
                        if (mLastUpdated != -1L) {
                            final CharSequence relativeTimeString = this.getRelativeTimeString(mLastUpdated);
                            if (relativeTimeString != null) {
                                string = this.getResources().getString(R$string.abc_slice_updated, new Object[] { relativeTimeString });
                                break Label_0124;
                            }
                        }
                    }
                    string = null;
                }
                if (sliceItem != null) {
                    sanitizedText = sliceItem.getSanitizedText();
                }
                final boolean b3 = !TextUtils.isEmpty(sanitizedText) || (sliceItem != null && sliceItem.hasHint("partial"));
                if (b3) {
                    this.mSecondaryText.setText(sanitizedText);
                    final SliceStyle mSliceStyle = super.mSliceStyle;
                    if (mSliceStyle != null) {
                        final TextView mSecondaryText = this.mSecondaryText;
                        int n;
                        if (this.mIsHeader) {
                            n = mSliceStyle.getHeaderSubtitleSize();
                        }
                        else {
                            n = mSliceStyle.getSubtitleSize();
                        }
                        mSecondaryText.setTextSize(0, (float)n);
                        this.mSecondaryText.setTextColor(super.mSliceStyle.getSubtitleColor());
                        int n2;
                        if (this.mIsHeader) {
                            n2 = super.mSliceStyle.getVerticalHeaderTextPadding();
                        }
                        else {
                            n2 = super.mSliceStyle.getVerticalTextPadding();
                        }
                        this.mSecondaryText.setPadding(0, n2, 0, 0);
                    }
                }
                final int n3 = 2;
                String s;
                if ((s = string) != null) {
                    String string2 = string;
                    if (!TextUtils.isEmpty(sanitizedText)) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append(" · ");
                        sb.append((Object)string);
                        string2 = sb.toString();
                    }
                    final SpannableString text = new SpannableString((CharSequence)string2);
                    text.setSpan((Object)new StyleSpan(2), 0, string2.length(), 0);
                    this.mLastUpdatedText.setText((CharSequence)text);
                    final SliceStyle mSliceStyle2 = super.mSliceStyle;
                    s = string2;
                    if (mSliceStyle2 != null) {
                        final TextView mLastUpdatedText = this.mLastUpdatedText;
                        int n4;
                        if (this.mIsHeader) {
                            n4 = mSliceStyle2.getHeaderSubtitleSize();
                        }
                        else {
                            n4 = mSliceStyle2.getSubtitleSize();
                        }
                        mLastUpdatedText.setTextSize(0, (float)n4);
                        this.mLastUpdatedText.setTextColor(super.mSliceStyle.getSubtitleColor());
                        s = string2;
                    }
                }
                final TextView mLastUpdatedText2 = this.mLastUpdatedText;
                final boolean empty = TextUtils.isEmpty((CharSequence)s);
                final int n5 = 8;
                int visibility;
                if (empty) {
                    visibility = 8;
                }
                else {
                    visibility = 0;
                }
                mLastUpdatedText2.setVisibility(visibility);
                final TextView mSecondaryText2 = this.mSecondaryText;
                int visibility2 = n5;
                if (b3) {
                    visibility2 = 0;
                }
                mSecondaryText2.setVisibility(visibility2);
                int maxLines;
                if ((this.mRowIndex > 0 || this.mAllowTwoLines) && !b && b3 && TextUtils.isEmpty((CharSequence)s)) {
                    maxLines = n3;
                }
                else {
                    maxLines = 1;
                }
                this.mSecondaryText.setSingleLine(maxLines == 1 && b2);
                this.mSecondaryText.setMaxLines(maxLines);
                this.mSecondaryText.requestLayout();
                this.mLastUpdatedText.requestLayout();
            }
        }
    }
    
    private void applyRowStyle() {
        final SliceStyle mSliceStyle = super.mSliceStyle;
        if (mSliceStyle != null) {
            if (mSliceStyle.getRowStyle() != null) {
                final RowStyle rowStyle = super.mSliceStyle.getRowStyle();
                this.setViewSidePaddings((View)this.mStartContainer, rowStyle.getTitleItemStartPadding(), rowStyle.getTitleItemEndPadding());
                this.setViewSidePaddings((View)this.mContent, rowStyle.getContentStartPadding(), rowStyle.getContentEndPadding());
                this.setViewSidePaddings((View)this.mPrimaryText, rowStyle.getTitleStartPadding(), rowStyle.getTitleEndPadding());
                this.setViewSidePaddings((View)this.mSubContent, rowStyle.getSubContentStartPadding(), rowStyle.getSubContentEndPadding());
                this.setViewSidePaddings((View)this.mEndContainer, rowStyle.getEndItemStartPadding(), rowStyle.getEndItemEndPadding());
                this.setViewSideMargins(this.mBottomDivider, rowStyle.getBottomDividerStartPadding(), rowStyle.getBottomDividerEndPadding());
                this.setViewHeight(this.mActionDivider, rowStyle.getActionDividerHeight());
            }
        }
    }
    
    private CharSequence getRelativeTimeString(long n) {
        n = System.currentTimeMillis() - n;
        if (n > 31449600000L) {
            final int i = (int)(n / 31449600000L);
            return this.getResources().getQuantityString(R$plurals.abc_slice_duration_years, i, new Object[] { i });
        }
        if (n > 86400000L) {
            final int j = (int)(n / 86400000L);
            return this.getResources().getQuantityString(R$plurals.abc_slice_duration_days, j, new Object[] { j });
        }
        if (n > 60000L) {
            final int k = (int)(n / 60000L);
            return this.getResources().getQuantityString(R$plurals.abc_slice_duration_min, k, new Object[] { k });
        }
        return null;
    }
    
    private int getRowContentHeight() {
        int height;
        final int n = height = this.mRowContent.getHeight(super.mSliceStyle, super.mViewPolicy);
        if (this.mRangeBar != null) {
            height = n;
            if (this.mStartItem == null) {
                height = n - super.mSliceStyle.getRowRangeHeight();
            }
        }
        int n2 = height;
        if (this.mSelectionSpinner != null) {
            n2 = height - super.mSliceStyle.getRowSelectionHeight();
        }
        return n2;
    }
    
    private void measureChildWithExactHeight(final View view, final int n, final int n2) {
        this.measureChild(view, n, View$MeasureSpec.makeMeasureSpec(n2 + super.mInsetTop + super.mInsetBottom, 1073741824));
    }
    
    private void populateViews(final boolean b) {
        final boolean b2 = b && this.mIsRangeSliding;
        if (!b2) {
            this.resetViewState();
        }
        if (this.mRowContent.getLayoutDir() != -1) {
            this.setLayoutDirection(this.mRowContent.getLayoutDir());
        }
        if (this.mRowContent.isDefaultSeeMore()) {
            this.showSeeMore();
            return;
        }
        final CharSequence contentDescription = this.mRowContent.getContentDescription();
        if (contentDescription != null) {
            this.mContent.setContentDescription(contentDescription);
        }
        final SliceItem startItem = this.mRowContent.getStartItem();
        this.mStartItem = startItem;
        boolean addItem;
        if (addItem = (startItem != null && (this.mRowIndex > 0 || this.mRowContent.hasTitleItems()))) {
            addItem = this.addItem(this.mStartItem, super.mTintColor, true);
        }
        final LinearLayout mStartContainer = this.mStartContainer;
        final int n = 8;
        int visibility;
        if (addItem) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        mStartContainer.setVisibility(visibility);
        final SliceItem titleItem = this.mRowContent.getTitleItem();
        if (titleItem != null) {
            this.mPrimaryText.setText(titleItem.getSanitizedText());
        }
        final SliceStyle mSliceStyle = super.mSliceStyle;
        if (mSliceStyle != null) {
            final TextView mPrimaryText = this.mPrimaryText;
            int n2;
            if (this.mIsHeader) {
                n2 = mSliceStyle.getHeaderTitleSize();
            }
            else {
                n2 = mSliceStyle.getTitleSize();
            }
            mPrimaryText.setTextSize(0, (float)n2);
            this.mPrimaryText.setTextColor(super.mSliceStyle.getTitleColor());
        }
        final TextView mPrimaryText2 = this.mPrimaryText;
        int visibility2;
        if (titleItem != null) {
            visibility2 = 0;
        }
        else {
            visibility2 = 8;
        }
        mPrimaryText2.setVisibility(visibility2);
        this.addSubtitle(titleItem != null);
        final View mBottomDivider = this.mBottomDivider;
        int visibility3 = n;
        if (this.mRowContent.hasBottomDivider()) {
            visibility3 = 0;
        }
        mBottomDivider.setVisibility(visibility3);
        final SliceItem primaryAction = this.mRowContent.getPrimaryAction();
        if (primaryAction != null && primaryAction != this.mStartItem) {
            final SliceActionImpl mRowAction = new SliceActionImpl(primaryAction);
            this.mRowAction = mRowAction;
            if (mRowAction.isToggle()) {
                this.addAction(this.mRowAction, super.mTintColor, (ViewGroup)this.mEndContainer, false);
                this.setViewClickable((View)this.mRootView, true);
                return;
            }
        }
        final SliceItem range = this.mRowContent.getRange();
        if (range != null) {
            if (this.mRowAction != null) {
                this.setViewClickable((View)this.mRootView, true);
            }
            this.mRangeItem = range;
            if (!b2) {
                this.setRangeBounds();
                this.addRange();
            }
            if (this.mStartItem == null) {
                return;
            }
        }
        final SliceItem selection = this.mRowContent.getSelection();
        if (selection != null) {
            this.addSelection(this.mSelectionItem = selection);
            return;
        }
        this.updateEndItems();
        this.updateActionSpinner();
    }
    
    private void resetViewState() {
        this.mRootView.setVisibility(0);
        this.setLayoutDirection(2);
        this.setViewClickable((View)this.mRootView, false);
        this.setViewClickable((View)this.mContent, false);
        this.mStartContainer.removeAllViews();
        this.mEndContainer.removeAllViews();
        this.mEndContainer.setVisibility(8);
        this.mPrimaryText.setText((CharSequence)null);
        this.mSecondaryText.setText((CharSequence)null);
        this.mLastUpdatedText.setText((CharSequence)null);
        this.mLastUpdatedText.setVisibility(8);
        this.mToggles.clear();
        this.mActions.clear();
        this.mRowAction = null;
        this.mBottomDivider.setVisibility(8);
        this.mActionDivider.setVisibility(8);
        final View mSeeMoreView = this.mSeeMoreView;
        if (mSeeMoreView != null) {
            this.mRootView.removeView(mSeeMoreView);
            this.mSeeMoreView = null;
        }
        this.mIsRangeSliding = false;
        this.mRangeHasPendingUpdate = false;
        this.mRangeItem = null;
        this.mRangeMinValue = 0;
        this.mRangeMaxValue = 0;
        this.mRangeValue = 0;
        this.mLastSentRangeUpdate = 0L;
        this.mHandler = null;
        final ProgressBar mRangeBar = this.mRangeBar;
        if (mRangeBar != null) {
            if (this.mStartItem == null) {
                this.removeView((View)mRangeBar);
            }
            else {
                this.mContent.removeView((View)mRangeBar);
            }
            this.mRangeBar = null;
        }
        this.mSubContent.setVisibility(0);
        this.mStartItem = null;
        this.mActionSpinner.setVisibility(8);
        final Spinner mSelectionSpinner = this.mSelectionSpinner;
        if (mSelectionSpinner != null) {
            this.removeView((View)mSelectionSpinner);
            this.mSelectionSpinner = null;
        }
        this.mSelectionItem = null;
    }
    
    private void setRangeBounds() {
        final SliceItem subtype = SliceQuery.findSubtype(this.mRangeItem, "int", "min");
        final int n = 0;
        int int1;
        if (subtype != null) {
            int1 = subtype.getInt();
        }
        else {
            int1 = 0;
        }
        this.mRangeMinValue = int1;
        final SliceItem subtype2 = SliceQuery.findSubtype(this.mRangeItem, "int", "max");
        int int2 = 100;
        if (subtype2 != null) {
            int2 = subtype2.getInt();
        }
        this.mRangeMaxValue = int2;
        final SliceItem subtype3 = SliceQuery.findSubtype(this.mRangeItem, "int", "value");
        int mRangeValue = n;
        if (subtype3 != null) {
            mRangeValue = subtype3.getInt() - int1;
        }
        this.mRangeValue = mRangeValue;
    }
    
    private void setViewClickable(final View view, final boolean clickable) {
        final Drawable drawable = null;
        Object onClickListener;
        if (clickable) {
            onClickListener = this;
        }
        else {
            onClickListener = null;
        }
        view.setOnClickListener((View$OnClickListener)onClickListener);
        Drawable drawable2 = drawable;
        if (clickable) {
            drawable2 = SliceViewUtil.getDrawable(this.getContext(), 16843534);
        }
        view.setBackground(drawable2);
        view.setClickable(clickable);
    }
    
    private void setViewHeight(final View view, final int height) {
        if (view != null && height >= 0) {
            final ViewGroup$LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = height;
            view.setLayoutParams(layoutParams);
        }
    }
    
    private void setViewSideMargins(final View view, final int marginStart, final int marginEnd) {
        final boolean b = marginStart < 0 && marginEnd < 0;
        if (view != null) {
            if (!b) {
                final ViewGroup$MarginLayoutParams layoutParams = (ViewGroup$MarginLayoutParams)view.getLayoutParams();
                if (marginStart >= 0) {
                    layoutParams.setMarginStart(marginStart);
                }
                if (marginEnd >= 0) {
                    layoutParams.setMarginEnd(marginEnd);
                }
                view.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
            }
        }
    }
    
    private void setViewSidePaddings(final View view, int paddingStart, int paddingEnd) {
        final boolean b = paddingStart < 0 && paddingEnd < 0;
        if (view != null) {
            if (!b) {
                if (paddingStart < 0) {
                    paddingStart = view.getPaddingStart();
                }
                final int paddingTop = view.getPaddingTop();
                if (paddingEnd < 0) {
                    paddingEnd = view.getPaddingEnd();
                }
                view.setPaddingRelative(paddingStart, paddingTop, paddingEnd, view.getPaddingBottom());
            }
        }
    }
    
    private void showSeeMore() {
        final Button mSeeMoreView = (Button)LayoutInflater.from(this.getContext()).inflate(R$layout.abc_slice_row_show_more, (ViewGroup)this, false);
        mSeeMoreView.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                try {
                    if (RowView.this.mObserver != null) {
                        RowView.this.mObserver.onSliceAction(new EventInfo(RowView.this.getMode(), 4, 0, RowView.this.mRowIndex), RowView.this.mRowContent.getSliceItem());
                    }
                    RowView.this.mShowActionSpinner = RowView.this.mRowContent.getSliceItem().fireActionInternal(RowView.this.getContext(), null);
                    if (RowView.this.mShowActionSpinner) {
                        if (RowView.this.mLoadingListener != null) {
                            RowView.this.mLoadingListener.onSliceActionLoading(RowView.this.mRowContent.getSliceItem(), RowView.this.mRowIndex);
                        }
                        RowView.this.mLoadingActions.add(RowView.this.mRowContent.getSliceItem());
                        mSeeMoreView.setVisibility(8);
                    }
                    RowView.this.updateActionSpinner();
                }
                catch (PendingIntent$CanceledException ex) {
                    Log.e("RowView", "PendingIntent for slice cannot be sent", (Throwable)ex);
                }
            }
        });
        final int mTintColor = super.mTintColor;
        if (mTintColor != -1) {
            mSeeMoreView.setTextColor(mTintColor);
        }
        this.mSeeMoreView = (View)mSeeMoreView;
        this.mRootView.addView((View)mSeeMoreView);
        if (this.mLoadingActions.contains(this.mRowContent.getSliceItem())) {
            this.mShowActionSpinner = true;
            mSeeMoreView.setVisibility(8);
            this.updateActionSpinner();
        }
    }
    
    private void updateEndItems() {
        final RowContent mRowContent = this.mRowContent;
        if (mRowContent != null) {
            if (mRowContent.getRange() == null || this.mStartItem != null) {
                this.mEndContainer.removeAllViews();
                Object endItems = this.mRowContent.getEndItems();
                final List<SliceAction> mHeaderActions = this.mHeaderActions;
                if (mHeaderActions != null) {
                    endItems = mHeaderActions;
                }
                if (this.mRowIndex == 0 && this.mStartItem != null && ((List)endItems).isEmpty() && !this.mRowContent.hasTitleItems()) {
                    ((List<SliceItem>)endItems).add(this.mStartItem);
                }
                SliceItem sliceItem = null;
                final int n = 0;
                final int n2;
                int i = n2 = 0;
                int n4;
                int n3 = n4 = n2;
                int n5 = n2;
                while (i < ((List)endItems).size()) {
                    SliceItem sliceItem2;
                    if (((List<Object>)endItems).get(i) instanceof SliceItem) {
                        sliceItem2 = ((List<SliceItem>)endItems).get(i);
                    }
                    else {
                        sliceItem2 = ((List<SliceActionImpl>)endItems).get(i).getSliceItem();
                    }
                    SliceItem sliceItem3 = sliceItem;
                    int n6 = n5;
                    int n7 = n4;
                    int n8 = n3;
                    if (n5 < 3) {
                        sliceItem3 = sliceItem;
                        n6 = n5;
                        n7 = n4;
                        n8 = n3;
                        if (this.addItem(sliceItem2, super.mTintColor, false)) {
                            SliceItem sliceItem4;
                            if ((sliceItem4 = sliceItem) == null) {
                                sliceItem4 = sliceItem;
                                if (SliceQuery.find(sliceItem2, "action") != null) {
                                    sliceItem4 = sliceItem2;
                                }
                            }
                            final int n9 = n5 + 1;
                            sliceItem3 = sliceItem4;
                            n6 = n9;
                            n7 = n4;
                            n8 = n3;
                            if (n9 == 1) {
                                final boolean b = !this.mToggles.isEmpty() && SliceQuery.find(sliceItem2.getSlice(), "image") == null;
                                if (((List)endItems).size() == 1 && SliceQuery.find(sliceItem2, "action") != null) {
                                    n8 = 1;
                                    sliceItem3 = sliceItem4;
                                    n6 = n9;
                                    n7 = (b ? 1 : 0);
                                }
                                else {
                                    n8 = 0;
                                    n7 = (b ? 1 : 0);
                                    n6 = n9;
                                    sliceItem3 = sliceItem4;
                                }
                            }
                        }
                    }
                    ++i;
                    sliceItem = sliceItem3;
                    n5 = n6;
                    n4 = n7;
                    n3 = n8;
                }
                final LinearLayout mEndContainer = this.mEndContainer;
                final int n10 = 8;
                int visibility;
                if (n5 > 0) {
                    visibility = 0;
                }
                else {
                    visibility = 8;
                }
                mEndContainer.setVisibility(visibility);
                final View mActionDivider = this.mActionDivider;
                int visibility2 = n10;
                Label_0471: {
                    if (this.mRowAction != null) {
                        if (n4 == 0) {
                            visibility2 = n10;
                            if (!this.mRowContent.hasActionDivider()) {
                                break Label_0471;
                            }
                            visibility2 = n10;
                            if (n3 == 0) {
                                break Label_0471;
                            }
                        }
                        visibility2 = 0;
                    }
                }
                mActionDivider.setVisibility(visibility2);
                final SliceItem mStartItem = this.mStartItem;
                final boolean b2 = mStartItem != null && SliceQuery.find(mStartItem, "action") != null;
                final boolean b3 = sliceItem != null;
                boolean b4 = false;
                Label_0653: {
                    if (this.mRowAction != null) {
                        this.setViewClickable((View)this.mRootView, true);
                    }
                    else if (b3 != b2 && (n5 == 1 || b2)) {
                        if (!this.mToggles.isEmpty()) {
                            this.mRowAction = this.mToggles.keySet().iterator().next();
                        }
                        else if (!this.mActions.isEmpty() && this.mActions.size() == 1) {
                            this.mRowAction = ((SliceActionView)this.mActions.valueAt(0)).getAction();
                        }
                        this.setViewClickable((View)this.mRootView, true);
                        b4 = true;
                        break Label_0653;
                    }
                    b4 = false;
                }
                final SliceActionImpl mRowAction = this.mRowAction;
                if (mRowAction != null && !b4 && this.mLoadingActions.contains(mRowAction.getSliceItem())) {
                    this.mShowActionSpinner = true;
                }
                final LinearLayout mRootView = this.mRootView;
                int n11;
                if (mRootView.isClickable() && this.mToggles.isEmpty() && this.mActions.isEmpty()) {
                    n11 = n;
                }
                else {
                    n11 = 2;
                }
                ViewCompat.setImportantForAccessibility((View)mRootView, n11);
            }
        }
    }
    
    public void onClick(final View view) {
        final SliceActionImpl mRowAction = this.mRowAction;
        if (mRowAction != null) {
            if (mRowAction.getActionItem() != null) {
                SliceActionView sliceActionView;
                if (this.mRowAction.isToggle()) {
                    sliceActionView = (SliceActionView)this.mToggles.get((Object)this.mRowAction);
                }
                else {
                    sliceActionView = (SliceActionView)this.mActions.get((Object)this.mRowAction);
                }
                if (sliceActionView != null && !(view instanceof SliceActionView)) {
                    sliceActionView.sendAction();
                }
                else if (this.mRowIndex == 0) {
                    this.performClick();
                }
                else {
                    try {
                        this.mShowActionSpinner = this.mRowAction.getActionItem().fireActionInternal(this.getContext(), null);
                        if (super.mObserver != null) {
                            super.mObserver.onSliceAction(new EventInfo(this.getMode(), 3, 0, this.mRowIndex), this.mRowAction.getSliceItem());
                        }
                        if (this.mShowActionSpinner && super.mLoadingListener != null) {
                            super.mLoadingListener.onSliceActionLoading(this.mRowAction.getSliceItem(), this.mRowIndex);
                            this.mLoadingActions.add(this.mRowAction.getSliceItem());
                        }
                        this.updateActionSpinner();
                    }
                    catch (PendingIntent$CanceledException ex) {
                        Log.e("RowView", "PendingIntent for slice cannot be sent", (Throwable)ex);
                    }
                }
            }
        }
    }
    
    public void onItemSelected(final AdapterView<?> adapterView, final View view, final int index, final long n) {
        if (this.mSelectionItem != null && adapterView == this.mSelectionSpinner && index >= 0) {
            if (index < this.mSelectionOptionKeys.size()) {
                if (super.mObserver != null) {
                    super.mObserver.onSliceAction(new EventInfo(this.getMode(), 5, 6, this.mRowIndex), this.mSelectionItem);
                }
                final String s = this.mSelectionOptionKeys.get(index);
                try {
                    if (this.mSelectionItem.fireActionInternal(this.getContext(), new Intent().addFlags(268435456).putExtra("android.app.slice.extra.SELECTION", s))) {
                        this.mShowActionSpinner = true;
                        if (super.mLoadingListener != null) {
                            super.mLoadingListener.onSliceActionLoading(this.mRowAction.getSliceItem(), this.mRowIndex);
                            this.mLoadingActions.add(this.mRowAction.getSliceItem());
                        }
                        this.updateActionSpinner();
                    }
                }
                catch (PendingIntent$CanceledException ex) {
                    Log.e("RowView", "PendingIntent for slice cannot be sent", (Throwable)ex);
                }
            }
        }
    }
    
    protected void onLayout(final boolean b, int paddingLeft, int n, int n2, final int n3) {
        paddingLeft = this.getPaddingLeft();
        final LinearLayout mRootView = this.mRootView;
        mRootView.layout(paddingLeft, super.mInsetTop, mRootView.getMeasuredWidth() + paddingLeft, this.getRowContentHeight() + super.mInsetTop);
        if (this.mRangeBar != null && this.mStartItem == null) {
            n = (super.mSliceStyle.getRowRangeHeight() - this.mMeasuredRangeHeight) / 2;
            n2 = this.getRowContentHeight() + n + super.mInsetTop;
            n = this.mMeasuredRangeHeight;
            final ProgressBar mRangeBar = this.mRangeBar;
            mRangeBar.layout(paddingLeft, n2, mRangeBar.getMeasuredWidth() + paddingLeft, n + n2);
        }
        else if (this.mSelectionSpinner != null) {
            n2 = this.getRowContentHeight() + super.mInsetTop;
            n = this.mSelectionSpinner.getMeasuredHeight();
            final Spinner mSelectionSpinner = this.mSelectionSpinner;
            mSelectionSpinner.layout(paddingLeft, n2, mSelectionSpinner.getMeasuredWidth() + paddingLeft, n + n2);
        }
    }
    
    protected void onMeasure(final int n, int n2) {
        n2 = this.getRowContentHeight();
        int measuredWidth;
        if (n2 != 0) {
            this.mRootView.setVisibility(0);
            this.measureChildWithExactHeight((View)this.mRootView, n, n2);
            measuredWidth = this.mRootView.getMeasuredWidth();
        }
        else {
            this.mRootView.setVisibility(8);
            measuredWidth = 0;
        }
        final ProgressBar mRangeBar = this.mRangeBar;
        if (mRangeBar != null && this.mStartItem == null) {
            if (RowView.sCanSpecifyLargerRangeBarHeight) {
                this.measureChildWithExactHeight((View)mRangeBar, n, super.mSliceStyle.getRowRangeHeight());
            }
            else {
                this.measureChild((View)mRangeBar, n, View$MeasureSpec.makeMeasureSpec(0, 0));
            }
            this.mMeasuredRangeHeight = this.mRangeBar.getMeasuredHeight();
            n2 = Math.max(measuredWidth, this.mRangeBar.getMeasuredWidth());
        }
        else {
            final Spinner mSelectionSpinner = this.mSelectionSpinner;
            n2 = measuredWidth;
            if (mSelectionSpinner != null) {
                this.measureChildWithExactHeight((View)mSelectionSpinner, n, super.mSliceStyle.getRowSelectionHeight());
                n2 = Math.max(measuredWidth, this.mSelectionSpinner.getMeasuredWidth());
            }
        }
        final int max = Math.max(n2 + super.mInsetStart + super.mInsetEnd, this.getSuggestedMinimumWidth());
        final RowContent mRowContent = this.mRowContent;
        if (mRowContent != null) {
            n2 = mRowContent.getHeight(super.mSliceStyle, super.mViewPolicy);
        }
        else {
            n2 = 0;
        }
        this.setMeasuredDimension(FrameLayout.resolveSizeAndState(max, n, 0), n2 + super.mInsetTop + super.mInsetBottom);
    }
    
    public void onNothingSelected(final AdapterView<?> adapterView) {
    }
    
    @Override
    public void resetView() {
        this.mRowContent = null;
        this.mLoadingActions.clear();
        this.resetViewState();
    }
    
    void sendSliderValue() {
        if (this.mRangeItem == null) {
            return;
        }
        try {
            this.mLastSentRangeUpdate = System.currentTimeMillis();
            this.mRangeItem.fireAction(this.getContext(), new Intent().addFlags(268435456).putExtra("android.app.slice.extra.RANGE_VALUE", this.mRangeValue));
            if (super.mObserver != null) {
                final EventInfo eventInfo = new EventInfo(this.getMode(), 2, 4, this.mRowIndex);
                eventInfo.state = this.mRangeValue;
                super.mObserver.onSliceAction(eventInfo, this.mRangeItem);
            }
        }
        catch (PendingIntent$CanceledException ex) {
            Log.e("RowView", "PendingIntent for slice cannot be sent", (Throwable)ex);
        }
    }
    
    @Override
    public void setAllowTwoLines(final boolean mAllowTwoLines) {
        this.mAllowTwoLines = mAllowTwoLines;
        if (this.mRowContent != null) {
            this.populateViews(true);
        }
    }
    
    @Override
    public void setInsets(final int n, final int n2, final int n3, final int n4) {
        super.setInsets(n, n2, n3, n4);
        this.setPadding(n, n2, n3, n4);
    }
    
    @Override
    public void setLastUpdated(final long lastUpdated) {
        super.setLastUpdated(lastUpdated);
        final RowContent mRowContent = this.mRowContent;
        if (mRowContent != null) {
            this.addSubtitle(mRowContent.getTitleItem() != null && TextUtils.isEmpty(this.mRowContent.getTitleItem().getSanitizedText()));
        }
    }
    
    @Override
    public void setLoadingActions(final Set<SliceItem> mLoadingActions) {
        if (mLoadingActions == null) {
            this.mLoadingActions.clear();
            this.mShowActionSpinner = false;
        }
        else {
            this.mLoadingActions = mLoadingActions;
        }
        this.updateEndItems();
        this.updateActionSpinner();
    }
    
    @Override
    public void setShowLastUpdated(final boolean showLastUpdated) {
        super.setShowLastUpdated(showLastUpdated);
        if (this.mRowContent != null) {
            this.populateViews(true);
        }
    }
    
    @Override
    public void setSliceActions(final List<SliceAction> mHeaderActions) {
        this.mHeaderActions = mHeaderActions;
        if (this.mRowContent != null) {
            this.updateEndItems();
        }
    }
    
    @Override
    public void setSliceItem(final SliceContent sliceContent, final boolean mIsHeader, final int mRowIndex, int n, final SliceView.OnSliceActionListener sliceActionListener) {
        this.setSliceActionListener(sliceActionListener);
        boolean b = false;
        Label_0157: {
            if (sliceContent != null) {
                final RowContent mRowContent = this.mRowContent;
                if (mRowContent != null && mRowContent.isValid()) {
                    final RowContent mRowContent2 = this.mRowContent;
                    SliceStructure sliceStructure;
                    if (mRowContent2 != null) {
                        sliceStructure = new SliceStructure(mRowContent2.getSliceItem());
                    }
                    else {
                        sliceStructure = null;
                    }
                    final SliceStructure sliceStructure2 = new SliceStructure(sliceContent.getSliceItem().getSlice());
                    b = true;
                    if (sliceStructure != null && sliceStructure.equals(sliceStructure2)) {
                        n = 1;
                    }
                    else {
                        n = 0;
                    }
                    if (sliceStructure != null && sliceStructure.getUri() != null && sliceStructure.getUri().equals((Object)sliceStructure2.getUri()) && n != 0) {
                        break Label_0157;
                    }
                }
            }
            b = false;
        }
        this.mShowActionSpinner = false;
        this.mIsHeader = mIsHeader;
        this.mRowContent = (RowContent)sliceContent;
        this.mRowIndex = mRowIndex;
        this.populateViews(b);
    }
    
    @Override
    public void setStyle(final SliceStyle style) {
        super.setStyle(style);
        this.applyRowStyle();
    }
    
    @Override
    public void setTint(final int tint) {
        super.setTint(tint);
        if (this.mRowContent != null) {
            this.populateViews(true);
        }
    }
    
    void updateActionSpinner() {
        final ProgressBar mActionSpinner = this.mActionSpinner;
        int visibility;
        if (this.mShowActionSpinner) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        mActionSpinner.setVisibility(visibility);
    }
}
