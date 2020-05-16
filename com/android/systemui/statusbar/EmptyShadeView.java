// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.content.res.Configuration;
import com.android.systemui.R$id;
import android.view.View;
import com.android.systemui.statusbar.notification.stack.ExpandableViewState;
import com.android.systemui.R$string;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.TextView;
import com.android.systemui.statusbar.notification.row.StackScrollerDecorView;

public class EmptyShadeView extends StackScrollerDecorView
{
    private TextView mEmptyText;
    private int mText;
    
    public EmptyShadeView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mText = R$string.empty_shade_text;
    }
    
    public ExpandableViewState createExpandableViewState() {
        return new EmptyShadeViewState();
    }
    
    @Override
    protected View findContentView() {
        return this.findViewById(R$id.no_notifications);
    }
    
    @Override
    protected View findSecondaryView() {
        return null;
    }
    
    public int getTextResource() {
        return this.mText;
    }
    
    @Override
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mEmptyText.setText(this.mText);
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mEmptyText = (TextView)this.findContentView();
    }
    
    public void setText(final int n) {
        this.mText = n;
        this.mEmptyText.setText(n);
    }
    
    public void setTextColor(final int textColor) {
        this.mEmptyText.setTextColor(textColor);
    }
    
    public class EmptyShadeViewState extends ExpandableViewState
    {
        @Override
        public void applyToView(final View view) {
            super.applyToView(view);
            if (view instanceof EmptyShadeView) {
                final EmptyShadeView emptyShadeView = (EmptyShadeView)view;
                final float n = (float)super.clipTopAmount;
                final float n2 = (float)EmptyShadeView.this.mEmptyText.getPaddingTop();
                boolean contentVisible = true;
                if (n > n2 * 0.6f || !emptyShadeView.isVisible()) {
                    contentVisible = false;
                }
                emptyShadeView.setContentVisible(contentVisible);
            }
        }
    }
}
