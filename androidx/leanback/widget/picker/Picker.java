// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget.picker;

import java.util.Arrays;
import android.widget.TextView;
import android.text.TextUtils;
import java.util.Collection;
import android.graphics.Rect;
import androidx.leanback.R$dimen;
import android.view.KeyEvent;
import android.view.ViewGroup$LayoutParams;
import android.animation.TimeInterpolator;
import android.content.res.TypedArray;
import androidx.leanback.R$id;
import android.view.LayoutInflater;
import android.view.animation.DecelerateInterpolator;
import androidx.leanback.R$layout;
import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.leanback.R$styleable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.leanback.R$attr;
import android.util.AttributeSet;
import android.content.Context;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import java.util.ArrayList;
import androidx.leanback.widget.VerticalGridView;
import java.util.List;
import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import android.widget.FrameLayout;

public class Picker extends FrameLayout
{
    private int mAlphaAnimDuration;
    private final OnChildViewHolderSelectedListener mColumnChangeListener;
    final List<VerticalGridView> mColumnViews;
    ArrayList<PickerColumn> mColumns;
    private Interpolator mDecelerateInterpolator;
    private float mFocusedAlpha;
    private float mInvisibleColumnAlpha;
    private ArrayList<PickerValueListener> mListeners;
    private int mPickerItemLayoutId;
    private int mPickerItemTextViewId;
    private ViewGroup mPickerView;
    private int mSelectedColumn;
    private List<CharSequence> mSeparators;
    private float mUnfocusedAlpha;
    private float mVisibleColumnAlpha;
    private float mVisibleItemsActivated;
    
    public Picker(final Context context, final AttributeSet set) {
        this(context, set, R$attr.pickerStyle);
    }
    
    public Picker(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mColumnViews = new ArrayList<VerticalGridView>();
        this.mVisibleItemsActivated = 3.0f;
        this.mSelectedColumn = 0;
        this.mSeparators = new ArrayList<CharSequence>();
        this.mColumnChangeListener = new OnChildViewHolderSelectedListener() {
            @Override
            public void onChildViewHolderSelected(final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final int n, int minValue) {
                final int index = Picker.this.mColumnViews.indexOf(recyclerView);
                Picker.this.updateColumnAlpha(index, true);
                if (viewHolder != null) {
                    minValue = Picker.this.mColumns.get(index).getMinValue();
                    Picker.this.onColumnValueChanged(index, minValue + n);
                }
            }
        };
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.lbPicker, n, 0);
        ViewCompat.saveAttributeDataForStyleable((View)this, context, R$styleable.lbPicker, set, obtainStyledAttributes, n, 0);
        this.mPickerItemLayoutId = obtainStyledAttributes.getResourceId(R$styleable.lbPicker_pickerItemLayout, R$layout.lb_picker_item);
        this.mPickerItemTextViewId = obtainStyledAttributes.getResourceId(R$styleable.lbPicker_pickerItemTextViewId, 0);
        obtainStyledAttributes.recycle();
        this.setEnabled(true);
        this.setDescendantFocusability(262144);
        this.mFocusedAlpha = 1.0f;
        this.mUnfocusedAlpha = 1.0f;
        this.mVisibleColumnAlpha = 0.5f;
        this.mInvisibleColumnAlpha = 0.0f;
        this.mAlphaAnimDuration = 200;
        this.mDecelerateInterpolator = (Interpolator)new DecelerateInterpolator(2.5f);
        this.mPickerView = (ViewGroup)((ViewGroup)LayoutInflater.from(this.getContext()).inflate(R$layout.lb_picker, (ViewGroup)this, true)).findViewById(R$id.picker);
    }
    
    private void notifyValueChanged(final int n) {
        final ArrayList<PickerValueListener> mListeners = this.mListeners;
        if (mListeners != null) {
            for (int i = mListeners.size() - 1; i >= 0; --i) {
                this.mListeners.get(i).onValueChanged(this, n);
            }
        }
    }
    
    private void setOrAnimateAlpha(final View view, final boolean b, final float alpha, final float alpha2, final Interpolator interpolator) {
        view.animate().cancel();
        if (!b) {
            view.setAlpha(alpha);
        }
        else {
            if (alpha2 >= 0.0f) {
                view.setAlpha(alpha2);
            }
            view.animate().alpha(alpha).setDuration((long)this.mAlphaAnimDuration).setInterpolator((TimeInterpolator)interpolator).start();
        }
    }
    
    private void updateColumnSize() {
        for (int i = 0; i < this.getColumnsCount(); ++i) {
            this.updateColumnSize(this.mColumnViews.get(i));
        }
    }
    
    private void updateColumnSize(final VerticalGridView verticalGridView) {
        final ViewGroup$LayoutParams layoutParams = verticalGridView.getLayoutParams();
        float n;
        if (this.isActivated()) {
            n = this.getActivatedVisibleItemCount();
        }
        else {
            n = this.getVisibleItemCount();
        }
        layoutParams.height = (int)(this.getPickerItemHeightPixels() * n + verticalGridView.getVerticalSpacing() * (n - 1.0f));
        verticalGridView.setLayoutParams(layoutParams);
    }
    
    private void updateItemFocusable() {
        final boolean activated = this.isActivated();
        for (int i = 0; i < this.getColumnsCount(); ++i) {
            final VerticalGridView verticalGridView = this.mColumnViews.get(i);
            for (int j = 0; j < verticalGridView.getChildCount(); ++j) {
                verticalGridView.getChildAt(j).setFocusable(activated);
            }
        }
    }
    
    public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
        if (!this.isActivated()) {
            return super.dispatchKeyEvent(keyEvent);
        }
        final int keyCode = keyEvent.getKeyCode();
        if (keyCode != 23 && keyCode != 66) {
            return super.dispatchKeyEvent(keyEvent);
        }
        if (keyEvent.getAction() == 1) {
            this.performClick();
        }
        return true;
    }
    
    public float getActivatedVisibleItemCount() {
        return this.mVisibleItemsActivated;
    }
    
    public PickerColumn getColumnAt(final int index) {
        final ArrayList<PickerColumn> mColumns = this.mColumns;
        if (mColumns == null) {
            return null;
        }
        return mColumns.get(index);
    }
    
    public int getColumnsCount() {
        final ArrayList<PickerColumn> mColumns = this.mColumns;
        if (mColumns == null) {
            return 0;
        }
        return mColumns.size();
    }
    
    protected int getPickerItemHeightPixels() {
        return this.getContext().getResources().getDimensionPixelSize(R$dimen.picker_item_height);
    }
    
    public final int getPickerItemLayoutId() {
        return this.mPickerItemLayoutId;
    }
    
    public final int getPickerItemTextViewId() {
        return this.mPickerItemTextViewId;
    }
    
    public int getSelectedColumn() {
        return this.mSelectedColumn;
    }
    
    public float getVisibleItemCount() {
        return 1.0f;
    }
    
    public void onColumnValueChanged(final int index, final int currentValue) {
        final PickerColumn pickerColumn = this.mColumns.get(index);
        if (pickerColumn.getCurrentValue() != currentValue) {
            pickerColumn.setCurrentValue(currentValue);
            this.notifyValueChanged(index);
        }
    }
    
    protected boolean onRequestFocusInDescendants(final int n, final Rect rect) {
        final int selectedColumn = this.getSelectedColumn();
        return selectedColumn >= 0 && selectedColumn < this.mColumnViews.size() && this.mColumnViews.get(selectedColumn).requestFocus(n, rect);
    }
    
    public void requestChildFocus(final View view, final View view2) {
        super.requestChildFocus(view, view2);
        for (int i = 0; i < this.mColumnViews.size(); ++i) {
            if (this.mColumnViews.get(i).hasFocus()) {
                this.setSelectedColumn(i);
            }
        }
    }
    
    public void setActivated(final boolean focusable) {
        if (focusable == this.isActivated()) {
            super.setActivated(focusable);
            return;
        }
        super.setActivated(focusable);
        final boolean hasFocus = this.hasFocus();
        final int selectedColumn = this.getSelectedColumn();
        this.setDescendantFocusability(131072);
        if (!focusable && hasFocus && this.isFocusable()) {
            this.requestFocus();
        }
        for (int i = 0; i < this.getColumnsCount(); ++i) {
            this.mColumnViews.get(i).setFocusable(focusable);
        }
        this.updateColumnSize();
        this.updateItemFocusable();
        if (focusable && hasFocus && selectedColumn >= 0) {
            this.mColumnViews.get(selectedColumn).requestFocus();
        }
        this.setDescendantFocusability(262144);
    }
    
    public void setColumnAt(final int index, final PickerColumn element) {
        this.mColumns.set(index, element);
        final VerticalGridView verticalGridView = this.mColumnViews.get(index);
        final PickerScrollArrayAdapter pickerScrollArrayAdapter = (PickerScrollArrayAdapter)verticalGridView.getAdapter();
        if (pickerScrollArrayAdapter != null) {
            ((RecyclerView.Adapter)pickerScrollArrayAdapter).notifyDataSetChanged();
        }
        verticalGridView.setSelectedPosition(element.getCurrentValue() - element.getMinValue());
    }
    
    public void setColumnValue(int n, final int currentValue, final boolean b) {
        final PickerColumn pickerColumn = this.mColumns.get(n);
        if (pickerColumn.getCurrentValue() != currentValue) {
            pickerColumn.setCurrentValue(currentValue);
            this.notifyValueChanged(n);
            final VerticalGridView verticalGridView = this.mColumnViews.get(n);
            if (verticalGridView != null) {
                n = currentValue - this.mColumns.get(n).getMinValue();
                if (b) {
                    verticalGridView.setSelectedPositionSmooth(n);
                }
                else {
                    verticalGridView.setSelectedPosition(n);
                }
            }
        }
    }
    
    public void setColumns(final List<PickerColumn> c) {
        if (this.mSeparators.size() != 0) {
            if (this.mSeparators.size() == 1) {
                final CharSequence charSequence = this.mSeparators.get(0);
                this.mSeparators.clear();
                this.mSeparators.add("");
                for (int i = 0; i < c.size() - 1; ++i) {
                    this.mSeparators.add(charSequence);
                }
                this.mSeparators.add("");
            }
            else if (this.mSeparators.size() != c.size() + 1) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Separators size: ");
                sb.append(this.mSeparators.size());
                sb.append(" mustequal the size of columns: ");
                sb.append(c.size());
                sb.append(" + 1");
                throw new IllegalStateException(sb.toString());
            }
            this.mColumnViews.clear();
            this.mPickerView.removeAllViews();
            final ArrayList mColumns = new ArrayList<PickerColumn>((Collection<? extends E>)c);
            this.mColumns = (ArrayList<PickerColumn>)mColumns;
            if (this.mSelectedColumn > mColumns.size() - 1) {
                this.mSelectedColumn = this.mColumns.size() - 1;
            }
            final LayoutInflater from = LayoutInflater.from(this.getContext());
            final int columnsCount = this.getColumnsCount();
            if (!TextUtils.isEmpty((CharSequence)this.mSeparators.get(0))) {
                final TextView textView = (TextView)from.inflate(R$layout.lb_picker_separator, this.mPickerView, false);
                textView.setText((CharSequence)this.mSeparators.get(0));
                this.mPickerView.addView((View)textView);
            }
            int n;
            for (int j = 0; j < columnsCount; j = n) {
                final VerticalGridView verticalGridView = (VerticalGridView)from.inflate(R$layout.lb_picker_column, this.mPickerView, false);
                this.updateColumnSize(verticalGridView);
                verticalGridView.setWindowAlignment(0);
                verticalGridView.setHasFixedSize(false);
                verticalGridView.setFocusable(this.isActivated());
                verticalGridView.setItemViewCacheSize(0);
                this.mColumnViews.add(verticalGridView);
                this.mPickerView.addView((View)verticalGridView);
                final List<CharSequence> mSeparators = this.mSeparators;
                n = j + 1;
                if (!TextUtils.isEmpty((CharSequence)mSeparators.get(n))) {
                    final TextView textView2 = (TextView)from.inflate(R$layout.lb_picker_separator, this.mPickerView, false);
                    textView2.setText((CharSequence)this.mSeparators.get(n));
                    this.mPickerView.addView((View)textView2);
                }
                verticalGridView.setAdapter((RecyclerView.Adapter)new PickerScrollArrayAdapter(this.getPickerItemLayoutId(), this.getPickerItemTextViewId(), j));
                verticalGridView.setOnChildViewHolderSelectedListener(this.mColumnChangeListener);
            }
            return;
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("Separators size is: ");
        sb2.append(this.mSeparators.size());
        sb2.append(". At least one separator must be provided");
        throw new IllegalStateException(sb2.toString());
    }
    
    void setOrAnimateAlpha(final View view, final boolean b, int n, final boolean b2) {
        if (n != this.mSelectedColumn && this.hasFocus()) {
            n = 0;
        }
        else {
            n = 1;
        }
        if (b) {
            if (n != 0) {
                this.setOrAnimateAlpha(view, b2, this.mFocusedAlpha, -1.0f, this.mDecelerateInterpolator);
            }
            else {
                this.setOrAnimateAlpha(view, b2, this.mUnfocusedAlpha, -1.0f, this.mDecelerateInterpolator);
            }
        }
        else if (n != 0) {
            this.setOrAnimateAlpha(view, b2, this.mVisibleColumnAlpha, -1.0f, this.mDecelerateInterpolator);
        }
        else {
            this.setOrAnimateAlpha(view, b2, this.mInvisibleColumnAlpha, -1.0f, this.mDecelerateInterpolator);
        }
    }
    
    public void setSelectedColumn(final int mSelectedColumn) {
        if (this.mSelectedColumn != mSelectedColumn) {
            this.mSelectedColumn = mSelectedColumn;
            for (int i = 0; i < this.mColumnViews.size(); ++i) {
                this.updateColumnAlpha(i, true);
            }
        }
        final VerticalGridView verticalGridView = this.mColumnViews.get(mSelectedColumn);
        if (this.hasFocus() && !verticalGridView.hasFocus()) {
            verticalGridView.requestFocus();
        }
    }
    
    public final void setSeparator(final CharSequence charSequence) {
        this.setSeparators(Arrays.asList(charSequence));
    }
    
    public final void setSeparators(final List<CharSequence> list) {
        this.mSeparators.clear();
        this.mSeparators.addAll(list);
    }
    
    void updateColumnAlpha(final int n, final boolean b) {
        final VerticalGridView verticalGridView = this.mColumnViews.get(n);
        final int selectedPosition = verticalGridView.getSelectedPosition();
        for (int i = 0; i < verticalGridView.getAdapter().getItemCount(); ++i) {
            final View viewByPosition = verticalGridView.getLayoutManager().findViewByPosition(i);
            if (viewByPosition != null) {
                this.setOrAnimateAlpha(viewByPosition, selectedPosition == i, n, b);
            }
        }
    }
    
    class PickerScrollArrayAdapter extends Adapter<Picker.ViewHolder>
    {
        private final int mColIndex;
        private PickerColumn mData;
        private final int mResource;
        private final int mTextViewResourceId;
        
        PickerScrollArrayAdapter(final int mResource, final int mTextViewResourceId, final int n) {
            this.mResource = mResource;
            this.mColIndex = n;
            this.mTextViewResourceId = mTextViewResourceId;
            this.mData = Picker.this.mColumns.get(n);
        }
        
        @Override
        public int getItemCount() {
            final PickerColumn mData = this.mData;
            int count;
            if (mData == null) {
                count = 0;
            }
            else {
                count = mData.getCount();
            }
            return count;
        }
        
        public void onBindViewHolder(final Picker.ViewHolder viewHolder, final int n) {
            final TextView textView = viewHolder.textView;
            if (textView != null) {
                final PickerColumn mData = this.mData;
                if (mData != null) {
                    textView.setText(mData.getLabelFor(mData.getMinValue() + n));
                }
            }
            final Picker this$0 = Picker.this;
            this$0.setOrAnimateAlpha(viewHolder.itemView, this$0.mColumnViews.get(this.mColIndex).getSelectedPosition() == n, this.mColIndex, false);
        }
        
        public Picker.ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int mTextViewResourceId) {
            final View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(this.mResource, viewGroup, false);
            mTextViewResourceId = this.mTextViewResourceId;
            TextView textView;
            if (mTextViewResourceId != 0) {
                textView = (TextView)inflate.findViewById(mTextViewResourceId);
            }
            else {
                textView = (TextView)inflate;
            }
            return new Picker.ViewHolder(inflate, textView);
        }
        
        public void onViewAttachedToWindow(final Picker.ViewHolder viewHolder) {
            viewHolder.itemView.setFocusable(Picker.this.isActivated());
        }
    }
    
    public interface PickerValueListener
    {
        void onValueChanged(final Picker p0, final int p1);
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder
    {
        final TextView textView;
        
        ViewHolder(final View view, final TextView textView) {
            super(view);
            this.textView = textView;
        }
    }
}
