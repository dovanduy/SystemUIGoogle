// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tileimpl;

import com.android.systemui.FontSizeUtils;
import com.android.systemui.R$dimen;
import android.content.res.Configuration;
import android.view.View$OnLongClickListener;
import android.view.View$OnClickListener;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import java.util.Objects;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import com.android.settingslib.Utils;
import com.android.systemui.plugins.qs.QSIconView;
import android.content.Context;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View;
import android.content.res.ColorStateList;

public class QSTileView extends QSTileBaseView
{
    private ColorStateList mColorLabelDefault;
    private ColorStateList mColorLabelUnavailable;
    private View mExpandIndicator;
    private View mExpandSpace;
    protected TextView mLabel;
    private ViewGroup mLabelContainer;
    private ImageView mPadLock;
    protected TextView mSecondLine;
    private int mState;
    
    public QSTileView(final Context context, final QSIconView qsIconView) {
        this(context, qsIconView, false);
    }
    
    public QSTileView(final Context context, final QSIconView qsIconView, final boolean b) {
        super(context, qsIconView, b);
        this.setClipChildren(false);
        this.setClipToPadding(false);
        this.setClickable(true);
        this.setId(View.generateViewId());
        this.createLabel();
        this.setOrientation(1);
        this.setGravity(49);
        this.mColorLabelDefault = Utils.getColorAttr(this.getContext(), 16842806);
        this.mColorLabelUnavailable = Utils.getColorAttr(this.getContext(), 16842808);
    }
    
    protected void createLabel() {
        (this.mLabelContainer = (ViewGroup)LayoutInflater.from(this.getContext()).inflate(R$layout.qs_tile_label, (ViewGroup)this, false)).setClipChildren(false);
        this.mLabelContainer.setClipToPadding(false);
        this.mLabel = (TextView)this.mLabelContainer.findViewById(R$id.tile_label);
        this.mPadLock = (ImageView)this.mLabelContainer.findViewById(R$id.restricted_padlock);
        this.mLabelContainer.findViewById(R$id.underline);
        this.mExpandIndicator = this.mLabelContainer.findViewById(R$id.expand_indicator);
        this.mExpandSpace = this.mLabelContainer.findViewById(R$id.expand_space);
        this.mSecondLine = (TextView)this.mLabelContainer.findViewById(R$id.app_label);
        this.addView((View)this.mLabelContainer);
    }
    
    @Override
    public int getDetailY() {
        return this.getTop() + this.mLabelContainer.getTop() + this.mLabelContainer.getHeight() / 2;
    }
    
    @Override
    protected void handleStateChanged(final QSTile.State state) {
        super.handleStateChanged(state);
        if (!Objects.equals(this.mLabel.getText(), state.label) || this.mState != state.state) {
            final TextView mLabel = this.mLabel;
            ColorStateList textColor;
            if (state.state == 0) {
                textColor = this.mColorLabelUnavailable;
            }
            else {
                textColor = this.mColorLabelDefault;
            }
            mLabel.setTextColor(textColor);
            this.mState = state.state;
            this.mLabel.setText(state.label);
        }
        final boolean equals = Objects.equals(this.mSecondLine.getText(), state.secondaryLabel);
        final int n = 0;
        if (!equals) {
            this.mSecondLine.setText(state.secondaryLabel);
            final TextView mSecondLine = this.mSecondLine;
            int visibility;
            if (TextUtils.isEmpty(state.secondaryLabel)) {
                visibility = 8;
            }
            else {
                visibility = 0;
            }
            mSecondLine.setVisibility(visibility);
        }
        this.mExpandIndicator.setVisibility(8);
        this.mExpandSpace.setVisibility(8);
        this.mLabelContainer.setContentDescription((CharSequence)null);
        if (this.mLabelContainer.isClickable()) {
            this.mLabelContainer.setClickable(false);
            this.mLabelContainer.setLongClickable(false);
            this.mLabelContainer.setBackground((Drawable)null);
        }
        this.mLabel.setEnabled(state.disabledByPolicy ^ true);
        final ImageView mPadLock = this.mPadLock;
        int visibility2;
        if (state.disabledByPolicy) {
            visibility2 = n;
        }
        else {
            visibility2 = 8;
        }
        mPadLock.setVisibility(visibility2);
    }
    
    @Override
    public void init(final View$OnClickListener view$OnClickListener, final View$OnClickListener onClickListener, final View$OnLongClickListener onLongClickListener) {
        super.init(view$OnClickListener, onClickListener, onLongClickListener);
        this.mLabelContainer.setOnClickListener(onClickListener);
        this.mLabelContainer.setOnLongClickListener(onLongClickListener);
        this.mLabelContainer.setClickable(false);
        this.mLabelContainer.setLongClickable(false);
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        FontSizeUtils.updateFontSize(this.mLabel, R$dimen.qs_tile_text_size);
        FontSizeUtils.updateFontSize(this.mSecondLine, R$dimen.qs_tile_text_size);
    }
    
    protected void onMeasure(final int n, final int n2) {
        super.onMeasure(n, n2);
        if (this.mLabel.getLineCount() > 2 || (!TextUtils.isEmpty(this.mSecondLine.getText()) && this.mSecondLine.getLineHeight() > this.mSecondLine.getHeight())) {
            this.mLabel.setSingleLine();
            super.onMeasure(n, n2);
        }
    }
}
