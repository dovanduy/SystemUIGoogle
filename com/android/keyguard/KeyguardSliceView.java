// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.view.View$MeasureSpec;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import com.android.systemui.util.wakelock.KeepAwakeAnimationListener;
import android.view.animation.Animation$AnimationListener;
import android.widget.TextView$BufferType;
import android.text.TextUtils;
import android.text.TextUtils$TruncateAt;
import com.android.systemui.R$style;
import androidx.slice.widget.SliceLiveData;
import androidx.slice.SliceViewManager;
import android.util.Log;
import com.android.systemui.keyguard.KeyguardSliceProvider;
import com.android.settingslib.Utils;
import com.android.systemui.R$attr;
import com.android.systemui.R$id;
import android.view.Display;
import com.android.internal.graphics.ColorUtils;
import java.io.Serializable;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import androidx.slice.SliceItem;
import android.graphics.drawable.Drawable;
import androidx.slice.core.SliceQuery;
import android.view.ViewGroup$LayoutParams;
import android.widget.LinearLayout$LayoutParams;
import androidx.slice.widget.SliceContent;
import androidx.slice.widget.RowContent;
import java.util.ArrayList;
import androidx.slice.widget.ListContent;
import android.os.Trace;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import com.android.systemui.R$dimen;
import android.content.res.Resources;
import com.android.systemui.Dependency;
import android.util.AttributeSet;
import android.content.Context;
import com.android.internal.annotations.VisibleForTesting;
import android.widget.TextView;
import androidx.lifecycle.LiveData;
import android.animation.LayoutTransition;
import android.net.Uri;
import android.app.PendingIntent;
import android.view.View;
import java.util.HashMap;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.tuner.TunerService;
import androidx.slice.Slice;
import androidx.lifecycle.Observer;
import android.view.View$OnClickListener;
import android.widget.LinearLayout;

public class KeyguardSliceView extends LinearLayout implements View$OnClickListener, Observer<Slice>, Tunable, ConfigurationListener
{
    private final ActivityStarter mActivityStarter;
    private final HashMap<View, PendingIntent> mClickActions;
    private final ConfigurationController mConfigurationController;
    private Runnable mContentChangeListener;
    private float mDarkAmount;
    private int mDisplayId;
    private boolean mHasHeader;
    private int mIconSize;
    private int mIconSizeWithHeader;
    private Uri mKeyguardSliceUri;
    private final LayoutTransition mLayoutTransition;
    private LiveData<Slice> mLiveData;
    private Row mRow;
    private final int mRowPadding;
    private float mRowTextSize;
    private final int mRowWithHeaderPadding;
    private float mRowWithHeaderTextSize;
    private Slice mSlice;
    private int mTextColor;
    @VisibleForTesting
    TextView mTitle;
    private final TunerService mTunerService;
    
    public KeyguardSliceView(final Context context, final AttributeSet set) {
        this(context, set, Dependency.get(ActivityStarter.class), Dependency.get(ConfigurationController.class), Dependency.get(TunerService.class), context.getResources());
    }
    
    public KeyguardSliceView(final Context context, final AttributeSet set, final ActivityStarter mActivityStarter, final ConfigurationController mConfigurationController, final TunerService mTunerService, final Resources resources) {
        super(context, set);
        this.mDarkAmount = 0.0f;
        this.mDisplayId = -1;
        this.mTunerService = mTunerService;
        this.mClickActions = new HashMap<View, PendingIntent>();
        this.mRowPadding = resources.getDimensionPixelSize(R$dimen.subtitle_clock_padding);
        this.mRowWithHeaderPadding = resources.getDimensionPixelSize(R$dimen.header_subtitle_padding);
        this.mActivityStarter = mActivityStarter;
        this.mConfigurationController = mConfigurationController;
        (this.mLayoutTransition = new LayoutTransition()).setStagger(0, 275L);
        this.mLayoutTransition.setDuration(2, 550L);
        this.mLayoutTransition.setDuration(3, 275L);
        this.mLayoutTransition.disableTransitionType(0);
        this.mLayoutTransition.disableTransitionType(1);
        this.mLayoutTransition.setInterpolator(2, (TimeInterpolator)Interpolators.FAST_OUT_SLOW_IN);
        this.mLayoutTransition.setInterpolator(3, (TimeInterpolator)Interpolators.ALPHA_OUT);
        this.mLayoutTransition.setAnimateParentHierarchy(false);
    }
    
    private void showSlice() {
        Trace.beginSection("KeyguardSliceView#showSlice");
        final Slice mSlice = this.mSlice;
        final int n = 8;
        final int n2 = 0;
        if (mSlice == null) {
            this.mTitle.setVisibility(8);
            this.mRow.setVisibility(8);
            this.mHasHeader = false;
            final Runnable mContentChangeListener = this.mContentChangeListener;
            if (mContentChangeListener != null) {
                mContentChangeListener.run();
            }
            Trace.endSection();
            return;
        }
        this.mClickActions.clear();
        final ListContent listContent = new ListContent(this.getContext(), this.mSlice);
        final RowContent header = listContent.getHeader();
        this.mHasHeader = (header != null && !header.getSliceItem().hasHint("list_item"));
        final ArrayList<RowContent> list = new ArrayList<RowContent>();
        for (int i = 0; i < listContent.getRowItems().size(); ++i) {
            final SliceContent sliceContent = listContent.getRowItems().get(i);
            if (!"content://com.android.systemui.keyguard/action".equals(sliceContent.getSliceItem().getSlice().getUri().toString())) {
                list.add((RowContent)sliceContent);
            }
        }
        if (!this.mHasHeader) {
            this.mTitle.setVisibility(8);
        }
        else {
            this.mTitle.setVisibility(0);
            final RowContent header2 = listContent.getHeader();
            final SliceItem titleItem = header2.getTitleItem();
            CharSequence text;
            if (titleItem != null) {
                text = titleItem.getText();
            }
            else {
                text = null;
            }
            this.mTitle.setText(text);
            if (header2.getPrimaryAction() != null && header2.getPrimaryAction().getAction() != null) {
                this.mClickActions.put((View)this.mTitle, header2.getPrimaryAction().getAction());
            }
        }
        final int size = list.size();
        final int textColor = this.getTextColor();
        int mHasHeader = this.mHasHeader ? 1 : 0;
        final Row mRow = this.mRow;
        int visibility = n;
        if (size > 0) {
            visibility = 0;
        }
        mRow.setVisibility(visibility);
        final LinearLayout$LayoutParams layoutParams = (LinearLayout$LayoutParams)this.mRow.getLayoutParams();
        int topMargin;
        if (this.mHasHeader) {
            topMargin = this.mRowWithHeaderPadding;
        }
        else {
            topMargin = this.mRowPadding;
        }
        layoutParams.topMargin = topMargin;
        this.mRow.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
        int j;
        while (true) {
            j = n2;
            if (mHasHeader >= size) {
                break;
            }
            final RowContent rowContent = list.get(mHasHeader);
            final SliceItem sliceItem = rowContent.getSliceItem();
            final Uri uri = sliceItem.getSlice().getUri();
            KeyguardSliceTextView key;
            if ((key = (KeyguardSliceTextView)this.mRow.findViewWithTag((Object)uri)) == null) {
                key = new KeyguardSliceTextView(super.mContext);
                key.setTextColor(textColor);
                key.setTag((Object)uri);
                this.mRow.addView((View)key, mHasHeader - (this.mHasHeader ? 1 : 0));
            }
            PendingIntent action;
            if (rowContent.getPrimaryAction() != null) {
                action = rowContent.getPrimaryAction().getAction();
            }
            else {
                action = null;
            }
            this.mClickActions.put((View)key, action);
            final SliceItem titleItem2 = rowContent.getTitleItem();
            CharSequence text2;
            if (titleItem2 == null) {
                text2 = null;
            }
            else {
                text2 = titleItem2.getText();
            }
            key.setText(text2);
            key.setContentDescription(rowContent.getContentDescription());
            float n3;
            if (this.mHasHeader) {
                n3 = this.mRowWithHeaderTextSize;
            }
            else {
                n3 = this.mRowTextSize;
            }
            key.setTextSize(0, n3);
            final SliceItem find = SliceQuery.find(sliceItem.getSlice(), "image");
            Drawable loadDrawable;
            if (find != null) {
                int n4;
                if (this.mHasHeader) {
                    n4 = this.mIconSizeWithHeader;
                }
                else {
                    n4 = this.mIconSize;
                }
                final Drawable drawable = loadDrawable = find.getIcon().loadDrawable(super.mContext);
                if (drawable != null) {
                    drawable.setBounds(0, 0, Math.max((int)(drawable.getIntrinsicWidth() / (float)drawable.getIntrinsicHeight() * n4), 1), n4);
                    loadDrawable = drawable;
                }
            }
            else {
                loadDrawable = null;
            }
            key.setCompoundDrawables(loadDrawable, null, null, null);
            key.setOnClickListener((View$OnClickListener)this);
            key.setClickable(action != null);
            ++mHasHeader;
        }
        while (j < this.mRow.getChildCount()) {
            final View child = this.mRow.getChildAt(j);
            int n5 = j;
            if (!this.mClickActions.containsKey(child)) {
                this.mRow.removeView(child);
                n5 = j - 1;
            }
            j = n5 + 1;
        }
        final Runnable mContentChangeListener2 = this.mContentChangeListener;
        if (mContentChangeListener2 != null) {
            mContentChangeListener2.run();
        }
        Trace.endSection();
    }
    
    private void updateTextColors() {
        final int textColor = this.getTextColor();
        this.mTitle.setTextColor(textColor);
        for (int childCount = this.mRow.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.mRow.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView)child).setTextColor(textColor);
            }
        }
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("KeyguardSliceView:");
        final StringBuilder sb = new StringBuilder();
        sb.append("  mClickActions: ");
        sb.append(this.mClickActions);
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("  mTitle: ");
        final TextView mTitle = this.mTitle;
        final boolean b = true;
        final String s = "null";
        Serializable value;
        if (mTitle == null) {
            value = "null";
        }
        else {
            value = (mTitle.getVisibility() == 0);
        }
        sb2.append(value);
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("  mRow: ");
        final Row mRow = this.mRow;
        Serializable value2;
        if (mRow == null) {
            value2 = s;
        }
        else {
            value2 = (mRow.getVisibility() == 0 && b);
        }
        sb3.append(value2);
        printWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("  mTextColor: ");
        sb4.append(Integer.toHexString(this.mTextColor));
        printWriter.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append("  mDarkAmount: ");
        sb5.append(this.mDarkAmount);
        printWriter.println(sb5.toString());
        final StringBuilder sb6 = new StringBuilder();
        sb6.append("  mSlice: ");
        sb6.append(this.mSlice);
        printWriter.println(sb6.toString());
        final StringBuilder sb7 = new StringBuilder();
        sb7.append("  mHasHeader: ");
        sb7.append(this.mHasHeader);
        printWriter.println(sb7.toString());
    }
    
    @VisibleForTesting
    int getTextColor() {
        return ColorUtils.blendARGB(this.mTextColor, -1, this.mDarkAmount);
    }
    
    public boolean hasHeader() {
        return this.mHasHeader;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final Display display = this.getDisplay();
        if (display != null) {
            this.mDisplayId = display.getDisplayId();
        }
        this.mTunerService.addTunable((TunerService.Tunable)this, "keyguard_slice_uri");
        if (this.mDisplayId == 0) {
            this.mLiveData.observeForever(this);
        }
        this.mConfigurationController.addCallback((ConfigurationController.ConfigurationListener)this);
    }
    
    public void onChanged(final Slice mSlice) {
        this.mSlice = mSlice;
        this.showSlice();
    }
    
    public void onClick(final View key) {
        final PendingIntent pendingIntent = this.mClickActions.get(key);
        if (pendingIntent != null) {
            this.mActivityStarter.startPendingIntentDismissingKeyguard(pendingIntent);
        }
    }
    
    public void onDensityOrFontScaleChanged() {
        this.mIconSize = super.mContext.getResources().getDimensionPixelSize(R$dimen.widget_icon_size);
        this.mIconSizeWithHeader = (int)super.mContext.getResources().getDimension(R$dimen.header_icon_size);
        this.mRowTextSize = (float)super.mContext.getResources().getDimensionPixelSize(R$dimen.widget_label_font_size);
        this.mRowWithHeaderTextSize = (float)super.mContext.getResources().getDimensionPixelSize(R$dimen.header_row_font_size);
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mDisplayId == 0) {
            this.mLiveData.removeObserver(this);
        }
        this.mTunerService.removeTunable((TunerService.Tunable)this);
        this.mConfigurationController.removeCallback((ConfigurationController.ConfigurationListener)this);
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mTitle = (TextView)this.findViewById(R$id.title);
        this.mRow = (Row)this.findViewById(R$id.row);
        this.mTextColor = Utils.getColorAttrDefaultColor(super.mContext, R$attr.wallpaperTextColor);
        this.mIconSize = (int)super.mContext.getResources().getDimension(R$dimen.widget_icon_size);
        this.mIconSizeWithHeader = (int)super.mContext.getResources().getDimension(R$dimen.header_icon_size);
        this.mRowTextSize = (float)super.mContext.getResources().getDimensionPixelSize(R$dimen.widget_label_font_size);
        this.mRowWithHeaderTextSize = (float)super.mContext.getResources().getDimensionPixelSize(R$dimen.header_row_font_size);
        this.mTitle.setOnClickListener((View$OnClickListener)this);
        this.mTitle.setBreakStrategy(2);
    }
    
    public void onTuningChanged(final String s, final String s2) {
        this.setupUri(s2);
    }
    
    public void onVisibilityAggregated(final boolean b) {
        super.onVisibilityAggregated(b);
        LayoutTransition mLayoutTransition;
        if (b) {
            mLayoutTransition = this.mLayoutTransition;
        }
        else {
            mLayoutTransition = null;
        }
        this.setLayoutTransition(mLayoutTransition);
    }
    
    public void refresh() {
        Trace.beginSection("KeyguardSliceView#refresh");
        Slice slice;
        if ("content://com.android.systemui.keyguard/main".equals(this.mKeyguardSliceUri.toString())) {
            final KeyguardSliceProvider attachedInstance = KeyguardSliceProvider.getAttachedInstance();
            if (attachedInstance != null) {
                slice = attachedInstance.onBindSlice(this.mKeyguardSliceUri);
            }
            else {
                Log.w("KeyguardSliceView", "Keyguard slice not bound yet?");
                slice = null;
            }
        }
        else {
            slice = SliceViewManager.getInstance(this.getContext()).bindSlice(this.mKeyguardSliceUri);
        }
        this.onChanged(slice);
        Trace.endSection();
    }
    
    public void setContentChangeListener(final Runnable mContentChangeListener) {
        this.mContentChangeListener = mContentChangeListener;
    }
    
    public void setDarkAmount(final float n) {
        this.mDarkAmount = n;
        this.mRow.setDarkAmount(n);
        this.updateTextColors();
    }
    
    @VisibleForTesting
    void setTextColor(final int mTextColor) {
        this.mTextColor = mTextColor;
        this.updateTextColors();
    }
    
    public void setupUri(final String s) {
        String s2 = s;
        if (s == null) {
            s2 = "content://com.android.systemui.keyguard/main";
        }
        final boolean b = false;
        final LiveData<Slice> mLiveData = this.mLiveData;
        int n = b ? 1 : 0;
        if (mLiveData != null) {
            n = (b ? 1 : 0);
            if (mLiveData.hasActiveObservers()) {
                n = 1;
                this.mLiveData.removeObserver(this);
            }
        }
        final Uri parse = Uri.parse(s2);
        this.mKeyguardSliceUri = parse;
        final LiveData<Slice> fromUri = SliceLiveData.fromUri(super.mContext, parse);
        this.mLiveData = fromUri;
        if (n != 0) {
            fromUri.observeForever(this);
        }
    }
    
    @VisibleForTesting
    static class KeyguardSliceTextView extends TextView implements ConfigurationListener
    {
        private static int sStyleId;
        
        static {
            KeyguardSliceTextView.sStyleId = R$style.TextAppearance_Keyguard_Secondary;
        }
        
        KeyguardSliceTextView(final Context context) {
            super(context, (AttributeSet)null, 0, KeyguardSliceTextView.sStyleId);
            this.onDensityOrFontScaleChanged();
            this.setEllipsize(TextUtils$TruncateAt.END);
        }
        
        private void updateDrawableColors() {
            final int currentTextColor = this.getCurrentTextColor();
            for (final Drawable drawable : this.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setTint(currentTextColor);
                }
            }
        }
        
        private void updatePadding() {
            final boolean empty = TextUtils.isEmpty(this.getText());
            int n = 1;
            final int n2 = (int)this.getContext().getResources().getDimension(R$dimen.widget_horizontal_padding) / 2;
            if (!(empty ^ true)) {
                n = -1;
            }
            this.setPadding(n2, 0, n * n2, 0);
            this.setCompoundDrawablePadding((int)super.mContext.getResources().getDimension(R$dimen.widget_icon_padding));
        }
        
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            Dependency.get(ConfigurationController.class).addCallback((ConfigurationController.ConfigurationListener)this);
        }
        
        public void onDensityOrFontScaleChanged() {
            this.updatePadding();
        }
        
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            Dependency.get(ConfigurationController.class).removeCallback((ConfigurationController.ConfigurationListener)this);
        }
        
        public void onOverlayChanged() {
            this.setTextAppearance(KeyguardSliceTextView.sStyleId);
        }
        
        public void setCompoundDrawables(final Drawable drawable, final Drawable drawable2, final Drawable drawable3, final Drawable drawable4) {
            super.setCompoundDrawables(drawable, drawable2, drawable3, drawable4);
            this.updateDrawableColors();
            this.updatePadding();
        }
        
        public void setText(final CharSequence charSequence, final TextView$BufferType textView$BufferType) {
            super.setText(charSequence, textView$BufferType);
            this.updatePadding();
        }
        
        public void setTextColor(final int textColor) {
            super.setTextColor(textColor);
            this.updateDrawableColors();
        }
    }
    
    public static class Row extends LinearLayout
    {
        private float mDarkAmount;
        private final Animation$AnimationListener mKeepAwakeListener;
        private LayoutTransition mLayoutTransition;
        
        public Row(final Context context) {
            this(context, null);
        }
        
        public Row(final Context context, final AttributeSet set) {
            this(context, set, 0);
        }
        
        public Row(final Context context, final AttributeSet set, final int n) {
            this(context, set, n, 0);
        }
        
        public Row(final Context context, final AttributeSet set, final int n, final int n2) {
            super(context, set, n, n2);
            this.mKeepAwakeListener = (Animation$AnimationListener)new KeepAwakeAnimationListener(super.mContext);
        }
        
        public boolean hasOverlappingRendering() {
            return false;
        }
        
        protected void onFinishInflate() {
            (this.mLayoutTransition = new LayoutTransition()).setDuration(550L);
            final ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder((Object)null, new PropertyValuesHolder[] { PropertyValuesHolder.ofInt("left", new int[] { 0, 1 }), PropertyValuesHolder.ofInt("right", new int[] { 0, 1 }) });
            this.mLayoutTransition.setAnimator(0, (Animator)ofPropertyValuesHolder);
            this.mLayoutTransition.setAnimator(1, (Animator)ofPropertyValuesHolder);
            this.mLayoutTransition.setInterpolator(0, (TimeInterpolator)Interpolators.ACCELERATE_DECELERATE);
            this.mLayoutTransition.setInterpolator(1, (TimeInterpolator)Interpolators.ACCELERATE_DECELERATE);
            this.mLayoutTransition.setStartDelay(0, 550L);
            this.mLayoutTransition.setStartDelay(1, 550L);
            this.mLayoutTransition.setAnimator(2, (Animator)ObjectAnimator.ofFloat((Object)null, "alpha", new float[] { 0.0f, 1.0f }));
            this.mLayoutTransition.setInterpolator(2, (TimeInterpolator)Interpolators.ALPHA_IN);
            final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)null, "alpha", new float[] { 1.0f, 0.0f });
            this.mLayoutTransition.setInterpolator(3, (TimeInterpolator)Interpolators.ALPHA_OUT);
            this.mLayoutTransition.setDuration(3, 137L);
            this.mLayoutTransition.setAnimator(3, (Animator)ofFloat);
            this.mLayoutTransition.setAnimateParentHierarchy(false);
        }
        
        protected void onMeasure(final int n, final int n2) {
            final int size = View$MeasureSpec.getSize(n);
            for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
                final View child = this.getChildAt(i);
                if (child instanceof KeyguardSliceTextView) {
                    ((KeyguardSliceTextView)child).setMaxWidth(size / childCount);
                }
            }
            super.onMeasure(n, n2);
        }
        
        public void onVisibilityAggregated(final boolean b) {
            super.onVisibilityAggregated(b);
            LayoutTransition mLayoutTransition;
            if (b) {
                mLayoutTransition = this.mLayoutTransition;
            }
            else {
                mLayoutTransition = null;
            }
            this.setLayoutTransition(mLayoutTransition);
        }
        
        public void setDarkAmount(final float mDarkAmount) {
            boolean b = true;
            final boolean b2 = mDarkAmount != 0.0f;
            if (this.mDarkAmount == 0.0f) {
                b = false;
            }
            if (b2 == b) {
                return;
            }
            this.mDarkAmount = mDarkAmount;
            Animation$AnimationListener mKeepAwakeListener;
            if (b2) {
                mKeepAwakeListener = null;
            }
            else {
                mKeepAwakeListener = this.mKeepAwakeListener;
            }
            this.setLayoutAnimationListener(mKeepAwakeListener);
        }
    }
}
