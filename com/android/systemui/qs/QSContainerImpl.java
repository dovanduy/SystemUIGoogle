// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.view.View$MeasureSpec;
import android.view.ViewGroup$MarginLayoutParams;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Point;
import com.android.systemui.qs.customize.QSCustomizer;
import android.view.View;
import android.widget.FrameLayout;

public class QSContainerImpl extends FrameLayout
{
    private View mBackground;
    private View mBackgroundGradient;
    private QuickStatusBarHeader mHeader;
    private int mHeightOverride;
    private QSCustomizer mQSCustomizer;
    private View mQSDetail;
    private View mQSFooter;
    private QSPanel mQSPanel;
    private boolean mQsDisabled;
    private float mQsExpansion;
    private int mSideMargins;
    private final Point mSizePoint;
    private View mStatusBarBackground;
    
    public QSContainerImpl(final Context context, final AttributeSet set) {
        super(context, set);
        this.mSizePoint = new Point();
        this.mHeightOverride = -1;
    }
    
    private int getDisplayHeight() {
        if (this.mSizePoint.y == 0) {
            this.getDisplay().getRealSize(this.mSizePoint);
        }
        return this.mSizePoint.y;
    }
    
    private void setBackgroundGradientVisibility(final Configuration configuration) {
        final int orientation = configuration.orientation;
        int visibility = 4;
        if (orientation == 2) {
            this.mBackgroundGradient.setVisibility(4);
            this.mStatusBarBackground.setVisibility(4);
        }
        else {
            final View mBackgroundGradient = this.mBackgroundGradient;
            if (!this.mQsDisabled) {
                visibility = 0;
            }
            mBackgroundGradient.setVisibility(visibility);
            this.mStatusBarBackground.setVisibility(0);
        }
    }
    
    private void setMargins() {
        this.setMargins(this.mQSDetail);
        this.setMargins(this.mBackground);
        this.setMargins(this.mQSFooter);
        this.mQSPanel.setMargins(this.mSideMargins);
        this.mHeader.setMargins(this.mSideMargins);
    }
    
    private void setMargins(final View view) {
        final FrameLayout$LayoutParams frameLayout$LayoutParams = (FrameLayout$LayoutParams)view.getLayoutParams();
        final int mSideMargins = this.mSideMargins;
        frameLayout$LayoutParams.rightMargin = mSideMargins;
        frameLayout$LayoutParams.leftMargin = mSideMargins;
    }
    
    private void updateResources() {
        final FrameLayout$LayoutParams layoutParams = (FrameLayout$LayoutParams)this.mQSPanel.getLayoutParams();
        layoutParams.topMargin = super.mContext.getResources().getDimensionPixelSize(17105427);
        this.mQSPanel.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
    }
    
    protected int calculateContainerHeight() {
        int n = this.mHeightOverride;
        if (n == -1) {
            n = this.getMeasuredHeight();
        }
        int height;
        if (this.mQSCustomizer.isCustomizing()) {
            height = this.mQSCustomizer.getHeight();
        }
        else {
            height = this.mHeader.getHeight() + Math.round(this.mQsExpansion * (n - this.mHeader.getHeight()));
        }
        return height;
    }
    
    public void disable(int visibility, final int n, final boolean b) {
        boolean mQsDisabled = true;
        visibility = 0;
        if ((n & 0x1) == 0x0) {
            mQsDisabled = false;
        }
        if (mQsDisabled == this.mQsDisabled) {
            return;
        }
        this.mQsDisabled = mQsDisabled;
        this.setBackgroundGradientVisibility(this.getResources().getConfiguration());
        final View mBackground = this.mBackground;
        if (this.mQsDisabled) {
            visibility = 8;
        }
        mBackground.setVisibility(visibility);
    }
    
    protected void measureChildWithMargins(final View view, final int n, final int n2, final int n3, final int n4) {
        if (view != this.mQSPanel) {
            super.measureChildWithMargins(view, n, n2, n3, n4);
        }
    }
    
    protected void onConfigurationChanged(final Configuration backgroundGradientVisibility) {
        super.onConfigurationChanged(backgroundGradientVisibility);
        this.setBackgroundGradientVisibility(backgroundGradientVisibility);
        this.updateResources();
        this.mSizePoint.set(0, 0);
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mQSPanel = (QSPanel)this.findViewById(R$id.quick_settings_panel);
        this.mQSDetail = this.findViewById(R$id.qs_detail);
        this.mHeader = (QuickStatusBarHeader)this.findViewById(R$id.header);
        this.mQSCustomizer = (QSCustomizer)this.findViewById(R$id.qs_customize);
        this.mQSFooter = this.findViewById(R$id.qs_footer);
        this.mBackground = this.findViewById(R$id.quick_settings_background);
        this.mStatusBarBackground = this.findViewById(R$id.quick_settings_status_bar_background);
        this.mBackgroundGradient = this.findViewById(R$id.quick_settings_gradient_view);
        this.mSideMargins = this.getResources().getDimensionPixelSize(R$dimen.notification_side_paddings);
        this.setImportantForAccessibility(2);
        this.setMargins();
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        this.updateExpansion();
    }
    
    protected void onMeasure(final int n, int measuredWidth) {
        final Configuration configuration = this.getResources().getConfiguration();
        final boolean b = configuration.smallestScreenWidthDp >= 600 || configuration.orientation != 2;
        final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams = (ViewGroup$MarginLayoutParams)this.mQSPanel.getLayoutParams();
        final int n2 = measuredWidth = this.getDisplayHeight() - viewGroup$MarginLayoutParams.topMargin - viewGroup$MarginLayoutParams.bottomMargin - this.getPaddingBottom();
        if (b) {
            measuredWidth = n2 - this.getResources().getDimensionPixelSize(R$dimen.navigation_bar_height);
        }
        this.mQSPanel.measure(n, View$MeasureSpec.makeMeasureSpec(measuredWidth, 1073741824));
        measuredWidth = this.mQSPanel.getMeasuredWidth();
        super.onMeasure(View$MeasureSpec.makeMeasureSpec(measuredWidth, 1073741824), View$MeasureSpec.makeMeasureSpec(viewGroup$MarginLayoutParams.topMargin + viewGroup$MarginLayoutParams.bottomMargin + this.mQSPanel.getMeasuredHeight() + this.getPaddingBottom(), 1073741824));
        this.mQSCustomizer.measure(n, View$MeasureSpec.makeMeasureSpec(this.getDisplayHeight(), 1073741824));
    }
    
    public boolean performClick() {
        return true;
    }
    
    public void setExpansion(final float mQsExpansion) {
        this.mQsExpansion = mQsExpansion;
        this.updateExpansion();
    }
    
    public void setHeightOverride(final int mHeightOverride) {
        this.mHeightOverride = mHeightOverride;
        this.updateExpansion();
    }
    
    public void updateExpansion() {
        final int calculateContainerHeight = this.calculateContainerHeight();
        this.setBottom(this.getTop() + calculateContainerHeight);
        this.mQSDetail.setBottom(this.getTop() + calculateContainerHeight);
        final View mqsFooter = this.mQSFooter;
        mqsFooter.setTranslationY((float)(calculateContainerHeight - mqsFooter.getHeight()));
        this.mBackground.setTop(this.mQSPanel.getTop());
        this.mBackground.setBottom(calculateContainerHeight);
    }
}
