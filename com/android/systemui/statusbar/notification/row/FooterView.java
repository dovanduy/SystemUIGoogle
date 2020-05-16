// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.view.View$OnClickListener;
import com.android.systemui.R$string;
import android.content.res.Configuration;
import com.android.systemui.R$id;
import android.view.View;
import com.android.systemui.statusbar.notification.stack.ExpandableViewState;
import com.android.systemui.R$dimen;
import android.util.AttributeSet;
import android.content.Context;

public class FooterView extends StackScrollerDecorView
{
    private final int mClearAllTopPadding;
    private FooterViewButton mDismissButton;
    private FooterViewButton mManageButton;
    
    public FooterView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mClearAllTopPadding = context.getResources().getDimensionPixelSize(R$dimen.clear_all_padding_top);
    }
    
    public ExpandableViewState createExpandableViewState() {
        return new FooterViewState();
    }
    
    @Override
    protected View findContentView() {
        return this.findViewById(R$id.content);
    }
    
    @Override
    protected View findSecondaryView() {
        return this.findViewById(R$id.dismiss_text);
    }
    
    public boolean isOnEmptySpace(final float n, final float n2) {
        return n < super.mContent.getX() || n > super.mContent.getX() + super.mContent.getWidth() || n2 < super.mContent.getY() || n2 > super.mContent.getY() + super.mContent.getHeight();
    }
    
    @Override
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mDismissButton.setText(R$string.clear_all_notifications_text);
        this.mDismissButton.setContentDescription((CharSequence)super.mContext.getString(R$string.accessibility_clear_all));
        this.mManageButton.setText(R$string.manage_notifications_history_text);
        this.mManageButton.setContentDescription((CharSequence)super.mContext.getString(R$string.manage_notifications_history_text));
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mDismissButton = (FooterViewButton)this.findSecondaryView();
        this.mManageButton = (FooterViewButton)this.findViewById(R$id.manage_text);
    }
    
    public void setDismissButtonClickListener(final View$OnClickListener onClickListener) {
        this.mDismissButton.setOnClickListener(onClickListener);
    }
    
    public void setManageButtonClickListener(final View$OnClickListener onClickListener) {
        this.mManageButton.setOnClickListener(onClickListener);
    }
    
    public void setTextColor(final int n) {
        this.mManageButton.setTextColor(n);
        this.mDismissButton.setTextColor(n);
    }
    
    public class FooterViewState extends ExpandableViewState
    {
        @Override
        public void applyToView(final View view) {
            super.applyToView(view);
            if (view instanceof FooterView) {
                final FooterView footerView = (FooterView)view;
                final int clipTopAmount = super.clipTopAmount;
                final int access$000 = FooterView.this.mClearAllTopPadding;
                boolean contentVisible = true;
                if (clipTopAmount >= access$000 || !footerView.isVisible()) {
                    contentVisible = false;
                }
                footerView.setContentVisible(contentVisible);
            }
        }
    }
}
