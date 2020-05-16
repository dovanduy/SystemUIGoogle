// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.widget.LinearLayout$LayoutParams;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import com.google.common.base.Preconditions;
import com.android.systemui.R$id;
import android.view.View$OnClickListener;
import android.app.PendingIntent$CanceledException;
import android.util.Log;
import android.view.View;
import android.app.PendingIntent;
import com.android.systemui.R$color;
import com.android.systemui.R$drawable;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.Space;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.graphics.drawable.Drawable;
import android.widget.FrameLayout;

public class ChipView extends FrameLayout
{
    private final Drawable BACKGROUND_DARK;
    private final Drawable BACKGROUND_LIGHT;
    private final int TEXT_COLOR_DARK;
    private final int TEXT_COLOR_LIGHT;
    private LinearLayout mChip;
    private ImageView mIconView;
    private TextView mLabelView;
    private Space mSpaceView;
    
    public ChipView(final Context context) {
        this(context, null);
    }
    
    public ChipView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public ChipView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public ChipView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.BACKGROUND_DARK = context.getDrawable(R$drawable.assist_chip_background_dark);
        this.BACKGROUND_LIGHT = context.getDrawable(R$drawable.assist_chip_background_light);
        this.TEXT_COLOR_DARK = context.getColor(R$color.assist_chip_text_dark);
        this.TEXT_COLOR_LIGHT = context.getColor(R$color.assist_chip_text_light);
    }
    
    private void setTapAction(final PendingIntent pendingIntent) {
        this.setOnClickListener((View$OnClickListener)new _$$Lambda$ChipView$r_ZsGRy3pJJ_MHCfVghF5de5ZRg(pendingIntent));
    }
    
    protected void onFinishInflate() {
        final LinearLayout linearLayout = (LinearLayout)this.findViewById(R$id.chip_background);
        Preconditions.checkNotNull(linearLayout);
        this.mChip = linearLayout;
        final ImageView imageView = (ImageView)this.findViewById(R$id.chip_icon);
        Preconditions.checkNotNull(imageView);
        this.mIconView = imageView;
        final TextView textView = (TextView)this.findViewById(R$id.chip_label);
        Preconditions.checkNotNull(textView);
        this.mLabelView = textView;
        final Space space = (Space)this.findViewById(R$id.chip_element_padding);
        Preconditions.checkNotNull(space);
        this.mSpaceView = space;
    }
    
    boolean setChip(final Bundle bundle) {
        final Icon icon = (Icon)bundle.getParcelable("icon");
        final String string = bundle.getString("label");
        if (icon == null && (string == null || string.length() == 0)) {
            Log.w("ChipView", "Neither icon nor label provided; ignoring chip");
            return false;
        }
        if (icon == null) {
            this.mIconView.setVisibility(8);
            this.mSpaceView.setVisibility(8);
            this.mLabelView.setText((CharSequence)string);
            final LinearLayout$LayoutParams linearLayout$LayoutParams = (LinearLayout$LayoutParams)this.mLabelView.getLayoutParams();
            final int rightMargin = linearLayout$LayoutParams.rightMargin;
            linearLayout$LayoutParams.setMargins(rightMargin, linearLayout$LayoutParams.topMargin, rightMargin, linearLayout$LayoutParams.bottomMargin);
        }
        else if (string != null && string.length() != 0) {
            this.mIconView.setImageIcon(icon);
            this.mLabelView.setText((CharSequence)string);
        }
        else {
            this.mLabelView.setVisibility(8);
            this.mSpaceView.setVisibility(8);
            this.mIconView.setImageIcon(icon);
            final LinearLayout$LayoutParams linearLayout$LayoutParams2 = (LinearLayout$LayoutParams)this.mIconView.getLayoutParams();
            final int leftMargin = linearLayout$LayoutParams2.leftMargin;
            linearLayout$LayoutParams2.setMargins(leftMargin, linearLayout$LayoutParams2.topMargin, leftMargin, linearLayout$LayoutParams2.bottomMargin);
        }
        if (bundle.getParcelable("tap_action") == null) {
            Log.w("ChipView", "No tap action provided; ignoring chip");
            return false;
        }
        this.setTapAction((PendingIntent)bundle.getParcelable("tap_action"));
        return true;
    }
    
    void setHasDarkBackground(final boolean b) {
        final LinearLayout mChip = this.mChip;
        Drawable background;
        if (b) {
            background = this.BACKGROUND_DARK;
        }
        else {
            background = this.BACKGROUND_LIGHT;
        }
        mChip.setBackground(background);
        final TextView mLabelView = this.mLabelView;
        int textColor;
        if (b) {
            textColor = this.TEXT_COLOR_DARK;
        }
        else {
            textColor = this.TEXT_COLOR_LIGHT;
        }
        mLabelView.setTextColor(textColor);
    }
    
    void updateTextSize(final float n) {
        this.mLabelView.setTextSize(0, n);
    }
}
