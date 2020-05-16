// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.volume;

import java.util.Objects;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.widget.Button;
import android.widget.TextView;
import android.view.ViewGroup$LayoutParams;
import android.widget.LinearLayout$LayoutParams;
import android.view.View;
import android.util.AttributeSet;
import com.android.systemui.R$id;
import android.view.LayoutInflater;
import android.content.Context;
import android.view.View$OnClickListener;
import android.graphics.Typeface;
import android.widget.LinearLayout;

public class SegmentedButtons extends LinearLayout
{
    private static final int LABEL_RES_KEY;
    private static final Typeface MEDIUM;
    private static final Typeface REGULAR;
    private Callback mCallback;
    private final View$OnClickListener mClick;
    private final ConfigurableTexts mConfigurableTexts;
    private final Context mContext;
    protected final LayoutInflater mInflater;
    protected Object mSelectedValue;
    
    static {
        LABEL_RES_KEY = R$id.label;
        REGULAR = Typeface.create("sans-serif", 0);
        MEDIUM = Typeface.create("sans-serif-medium", 0);
    }
    
    public SegmentedButtons(final Context mContext, final AttributeSet set) {
        super(mContext, set);
        this.mClick = (View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                SegmentedButtons.this.setSelectedValue(view.getTag(), true);
            }
        };
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        this.setOrientation(0);
        this.mConfigurableTexts = new ConfigurableTexts(this.mContext);
    }
    
    private void fireInteraction() {
        final Callback mCallback = this.mCallback;
        if (mCallback != null) {
            ((Interaction.Callback)mCallback).onInteraction();
        }
    }
    
    private void fireOnSelected(final boolean b) {
        final Callback mCallback = this.mCallback;
        if (mCallback != null) {
            mCallback.onSelected(this.mSelectedValue, b);
        }
    }
    
    public void addButton(final int n, final int n2, final Object tag) {
        final Button inflateButton = this.inflateButton();
        inflateButton.setTag(SegmentedButtons.LABEL_RES_KEY, (Object)n);
        inflateButton.setText(n);
        inflateButton.setContentDescription((CharSequence)this.getResources().getString(n2));
        final LinearLayout$LayoutParams layoutParams = (LinearLayout$LayoutParams)inflateButton.getLayoutParams();
        if (this.getChildCount() == 0) {
            layoutParams.rightMargin = 0;
            layoutParams.leftMargin = 0;
        }
        inflateButton.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
        this.addView((View)inflateButton);
        inflateButton.setTag(tag);
        inflateButton.setOnClickListener(this.mClick);
        Interaction.register((View)inflateButton, (Interaction.Callback)new Interaction.Callback() {
            @Override
            public void onInteraction() {
                SegmentedButtons.this.fireInteraction();
            }
        });
        this.mConfigurableTexts.add((TextView)inflateButton, n);
    }
    
    public Object getSelectedValue() {
        return this.mSelectedValue;
    }
    
    public Button inflateButton() {
        return (Button)this.mInflater.inflate(R$layout.segmented_button, (ViewGroup)this, false);
    }
    
    public void setCallback(final Callback mCallback) {
        this.mCallback = mCallback;
    }
    
    protected void setSelectedStyle(final TextView textView, final boolean b) {
        Typeface typeface;
        if (b) {
            typeface = SegmentedButtons.MEDIUM;
        }
        else {
            typeface = SegmentedButtons.REGULAR;
        }
        textView.setTypeface(typeface);
    }
    
    public void setSelectedValue(final Object o, final boolean b) {
        if (Objects.equals(o, this.mSelectedValue)) {
            return;
        }
        this.mSelectedValue = o;
        for (int i = 0; i < this.getChildCount(); ++i) {
            final TextView textView = (TextView)this.getChildAt(i);
            final boolean equals = Objects.equals(this.mSelectedValue, textView.getTag());
            textView.setSelected(equals);
            this.setSelectedStyle(textView, equals);
        }
        this.fireOnSelected(b);
    }
    
    public void update() {
        this.mConfigurableTexts.update();
    }
    
    public interface Callback extends Interaction.Callback
    {
        void onSelected(final Object p0, final boolean p1);
    }
}
