// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.content.res.Resources;
import com.android.systemui.R$dimen;
import com.android.systemui.R$plurals;
import com.android.systemui.R$string;
import com.android.systemui.R$style;
import android.widget.TextView;
import android.view.View;
import com.android.systemui.R$layout;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.app.Notification;
import android.view.ViewGroup;
import android.content.Context;

public class HybridGroupManager
{
    private final Context mContext;
    private int mOverflowNumberColor;
    private int mOverflowNumberPadding;
    private float mOverflowNumberSize;
    private final ViewGroup mParent;
    
    public HybridGroupManager(final Context mContext, final ViewGroup mParent) {
        this.mContext = mContext;
        this.mParent = mParent;
        this.initDimens();
    }
    
    private HybridNotificationView bindFromNotificationWithStyle(final HybridNotificationView hybridNotificationView, final Notification notification, final int n) {
        HybridNotificationView inflateHybridViewWithStyle = hybridNotificationView;
        if (hybridNotificationView == null) {
            inflateHybridViewWithStyle = this.inflateHybridViewWithStyle(n);
        }
        inflateHybridViewWithStyle.bind(this.resolveTitle(notification), this.resolveText(notification));
        return inflateHybridViewWithStyle;
    }
    
    private HybridNotificationView inflateHybridViewWithStyle(final int n) {
        final HybridNotificationView hybridNotificationView = (HybridNotificationView)((LayoutInflater)new ContextThemeWrapper(this.mContext, n).getSystemService((Class)LayoutInflater.class)).inflate(R$layout.hybrid_notification, this.mParent, false);
        this.mParent.addView((View)hybridNotificationView);
        return hybridNotificationView;
    }
    
    private TextView inflateOverflowNumber() {
        final TextView textView = (TextView)((LayoutInflater)this.mContext.getSystemService((Class)LayoutInflater.class)).inflate(R$layout.hybrid_overflow_number, this.mParent, false);
        this.mParent.addView((View)textView);
        this.updateOverFlowNumberColor(textView);
        return textView;
    }
    
    private CharSequence resolveText(final Notification notification) {
        CharSequence charSequence;
        if ((charSequence = notification.extras.getCharSequence("android.text")) == null) {
            charSequence = notification.extras.getCharSequence("android.bigText");
        }
        return charSequence;
    }
    
    private CharSequence resolveTitle(final Notification notification) {
        CharSequence charSequence;
        if ((charSequence = notification.extras.getCharSequence("android.title")) == null) {
            charSequence = notification.extras.getCharSequence("android.title.big");
        }
        return charSequence;
    }
    
    private void updateOverFlowNumberColor(final TextView textView) {
        textView.setTextColor(this.mOverflowNumberColor);
    }
    
    public HybridNotificationView bindFromNotification(final HybridNotificationView hybridNotificationView, final Notification notification) {
        return this.bindFromNotificationWithStyle(hybridNotificationView, notification, R$style.HybridNotification);
    }
    
    public TextView bindOverflowNumber(final TextView textView, final int n) {
        TextView inflateOverflowNumber = textView;
        if (textView == null) {
            inflateOverflowNumber = this.inflateOverflowNumber();
        }
        final String string = this.mContext.getResources().getString(R$string.notification_group_overflow_indicator, new Object[] { n });
        if (!string.equals(inflateOverflowNumber.getText())) {
            inflateOverflowNumber.setText((CharSequence)string);
        }
        inflateOverflowNumber.setContentDescription((CharSequence)String.format(this.mContext.getResources().getQuantityString(R$plurals.notification_group_overflow_description, n), n));
        inflateOverflowNumber.setTextSize(0, this.mOverflowNumberSize);
        inflateOverflowNumber.setPaddingRelative(inflateOverflowNumber.getPaddingStart(), inflateOverflowNumber.getPaddingTop(), this.mOverflowNumberPadding, inflateOverflowNumber.getPaddingBottom());
        this.updateOverFlowNumberColor(inflateOverflowNumber);
        return inflateOverflowNumber;
    }
    
    public void initDimens() {
        final Resources resources = this.mContext.getResources();
        this.mOverflowNumberSize = (float)resources.getDimensionPixelSize(R$dimen.group_overflow_number_size);
        this.mOverflowNumberPadding = resources.getDimensionPixelSize(R$dimen.group_overflow_number_padding);
    }
    
    public void setOverflowNumberColor(final TextView textView, final int mOverflowNumberColor) {
        this.mOverflowNumberColor = mOverflowNumberColor;
        if (textView != null) {
            this.updateOverFlowNumberColor(textView);
        }
    }
}
