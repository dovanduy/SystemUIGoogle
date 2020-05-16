// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.view.View$OnClickListener;
import com.android.systemui.R$id;
import com.android.systemui.FontSizeUtils;
import android.content.res.Configuration;
import android.util.Pair;
import com.android.systemui.R$dimen;
import com.android.systemui.statusbar.phone.StatusBarWindowView;
import android.view.WindowInsets;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.R$string;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.Dependency;
import com.android.internal.logging.MetricsLogger;
import android.content.Intent;
import android.view.ViewPropertyAnimator;
import java.util.Objects;
import android.graphics.drawable.Animatable;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.util.AttributeSet;
import android.content.Context;
import android.view.ViewStub;
import android.widget.Switch;
import android.widget.ImageView;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.util.SparseArray;
import android.widget.TextView;
import android.view.ViewGroup;
import com.android.systemui.plugins.qs.DetailAdapter;
import android.widget.LinearLayout;

public class QSDetail extends LinearLayout
{
    private boolean mAnimatingOpen;
    private QSDetailClipper mClipper;
    private boolean mClosingDetail;
    private DetailAdapter mDetailAdapter;
    private ViewGroup mDetailContent;
    protected TextView mDetailDoneButton;
    protected TextView mDetailSettingsButton;
    private final SparseArray<View> mDetailViews;
    private View mFooter;
    private boolean mFullyExpanded;
    private QuickStatusBarHeader mHeader;
    private final AnimatorListenerAdapter mHideGridContentWhenDone;
    private int mOpenX;
    private int mOpenY;
    protected View mQsDetailHeader;
    protected ImageView mQsDetailHeaderProgress;
    private Switch mQsDetailHeaderSwitch;
    private ViewStub mQsDetailHeaderSwitchStub;
    protected TextView mQsDetailHeaderTitle;
    private QSPanel mQsPanel;
    protected Callback mQsPanelCallback;
    private boolean mScanState;
    private boolean mSwitchState;
    private final AnimatorListenerAdapter mTeardownDetailWhenDone;
    private boolean mTriggeredExpand;
    
    public QSDetail(final Context context, final AttributeSet set) {
        super(context, set);
        this.mDetailViews = (SparseArray<View>)new SparseArray();
        this.mQsPanelCallback = (Callback)new Callback() {
            @Override
            public void onScanStateChanged(final boolean b) {
                QSDetail.this.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        QSDetail.this.handleScanStateChanged(b);
                    }
                });
            }
            
            @Override
            public void onShowingDetail(final DetailAdapter detailAdapter, final int n, final int n2) {
                QSDetail.this.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        if (QSDetail.this.isAttachedToWindow()) {
                            QSDetail.this.handleShowingDetail(detailAdapter, n, n2, false);
                        }
                    }
                });
            }
            
            @Override
            public void onToggleStateChanged(final boolean b) {
                QSDetail.this.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        final QSDetail this$0 = QSDetail.this;
                        this$0.handleToggleStateChanged(b, this$0.mDetailAdapter != null && QSDetail.this.mDetailAdapter.getToggleEnabled());
                    }
                });
            }
        };
        this.mHideGridContentWhenDone = new AnimatorListenerAdapter() {
            public void onAnimationCancel(final Animator animator) {
                animator.removeListener((Animator$AnimatorListener)this);
                QSDetail.this.mAnimatingOpen = false;
                QSDetail.this.checkPendingAnimations();
            }
            
            public void onAnimationEnd(final Animator animator) {
                if (QSDetail.this.mDetailAdapter != null) {
                    QSDetail.this.mQsPanel.setGridContentVisibility(false);
                    QSDetail.this.mHeader.setVisibility(4);
                    QSDetail.this.mFooter.setVisibility(4);
                }
                QSDetail.this.mAnimatingOpen = false;
                QSDetail.this.checkPendingAnimations();
            }
        };
        this.mTeardownDetailWhenDone = new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                QSDetail.this.mDetailContent.removeAllViews();
                QSDetail.this.setVisibility(4);
                QSDetail.this.mClosingDetail = false;
            }
        };
    }
    
    private void checkPendingAnimations() {
        final boolean mSwitchState = this.mSwitchState;
        final DetailAdapter mDetailAdapter = this.mDetailAdapter;
        this.handleToggleStateChanged(mSwitchState, mDetailAdapter != null && mDetailAdapter.getToggleEnabled());
    }
    
    private void handleScanStateChanged(final boolean mScanState) {
        if (this.mScanState == mScanState) {
            return;
        }
        this.mScanState = mScanState;
        final Animatable animatable = (Animatable)this.mQsDetailHeaderProgress.getDrawable();
        if (mScanState) {
            this.mQsDetailHeaderProgress.animate().cancel();
            final ViewPropertyAnimator alpha = this.mQsDetailHeaderProgress.animate().alpha(1.0f);
            Objects.requireNonNull(animatable);
            alpha.withEndAction((Runnable)new _$$Lambda$dWuG3P2xqsast1TFpf_9V5OJbdM(animatable)).start();
        }
        else {
            this.mQsDetailHeaderProgress.animate().cancel();
            final ViewPropertyAnimator alpha2 = this.mQsDetailHeaderProgress.animate().alpha(0.0f);
            Objects.requireNonNull(animatable);
            alpha2.withEndAction((Runnable)new _$$Lambda$uWzoJtW0gRQtylxIzOBLYDei0eA(animatable)).start();
        }
    }
    
    private void handleToggleStateChanged(final boolean b, final boolean b2) {
        this.mSwitchState = b;
        if (this.mAnimatingOpen) {
            return;
        }
        final Switch mQsDetailHeaderSwitch = this.mQsDetailHeaderSwitch;
        if (mQsDetailHeaderSwitch != null) {
            mQsDetailHeaderSwitch.setChecked(b);
        }
        this.mQsDetailHeader.setEnabled(b2);
        final Switch mQsDetailHeaderSwitch2 = this.mQsDetailHeaderSwitch;
        if (mQsDetailHeaderSwitch2 != null) {
            mQsDetailHeaderSwitch2.setEnabled(b2);
        }
    }
    
    private void updateDetailText() {
        this.mDetailDoneButton.setText(R$string.quick_settings_done);
        this.mDetailSettingsButton.setText(R$string.quick_settings_more_settings);
    }
    
    protected void animateDetailVisibleDiff(final int n, final int n2, final boolean b, final Animator$AnimatorListener listener) {
        if (b) {
            final DetailAdapter mDetailAdapter = this.mDetailAdapter;
            final boolean b2 = true;
            this.mAnimatingOpen = (mDetailAdapter != null);
            if (!this.mFullyExpanded && this.mDetailAdapter == null) {
                this.animate().alpha(0.0f).setDuration(300L).setListener(listener).start();
            }
            else {
                this.setAlpha(1.0f);
                this.mClipper.animateCircularClip(n, n2, this.mDetailAdapter != null && b2, listener);
            }
        }
    }
    
    public void handleShowingDetail(final DetailAdapter mDetailAdapter, int mOpenX, int mOpenY, final boolean b) {
        final boolean clickable = mDetailAdapter != null;
        this.setClickable(clickable);
        if (clickable) {
            this.setupDetailHeader(mDetailAdapter);
            if (b && !this.mFullyExpanded) {
                this.mTriggeredExpand = true;
                Dependency.get(CommandQueue.class).animateExpandSettingsPanel(null);
            }
            else {
                this.mTriggeredExpand = false;
            }
            this.mOpenX = mOpenX;
            this.mOpenY = mOpenY;
        }
        else {
            final int mOpenX2 = this.mOpenX;
            final int mOpenY2 = this.mOpenY;
            mOpenX = mOpenX2;
            mOpenY = mOpenY2;
            if (b) {
                mOpenX = mOpenX2;
                mOpenY = mOpenY2;
                if (this.mTriggeredExpand) {
                    Dependency.get(CommandQueue.class).animateCollapsePanels();
                    this.mTriggeredExpand = false;
                    mOpenY = mOpenY2;
                    mOpenX = mOpenX2;
                }
            }
        }
        final boolean b2 = this.mDetailAdapter != null != (mDetailAdapter != null);
        if (!b2 && this.mDetailAdapter == mDetailAdapter) {
            return;
        }
        AnimatorListenerAdapter animatorListenerAdapter;
        if (mDetailAdapter != null) {
            final int metricsCategory = mDetailAdapter.getMetricsCategory();
            final View detailView = mDetailAdapter.createDetailView(super.mContext, (View)this.mDetailViews.get(metricsCategory), this.mDetailContent);
            if (detailView == null) {
                throw new IllegalStateException("Must return detail view");
            }
            this.setupDetailFooter(mDetailAdapter);
            this.mDetailContent.removeAllViews();
            this.mDetailContent.addView(detailView);
            this.mDetailViews.put(metricsCategory, (Object)detailView);
            Dependency.get(MetricsLogger.class).visible(mDetailAdapter.getMetricsCategory());
            this.announceForAccessibility((CharSequence)super.mContext.getString(R$string.accessibility_quick_settings_detail, new Object[] { mDetailAdapter.getTitle() }));
            this.mDetailAdapter = mDetailAdapter;
            animatorListenerAdapter = this.mHideGridContentWhenDone;
            this.setVisibility(0);
        }
        else {
            if (this.mDetailAdapter != null) {
                Dependency.get(MetricsLogger.class).hidden(this.mDetailAdapter.getMetricsCategory());
            }
            this.mClosingDetail = true;
            this.mDetailAdapter = null;
            animatorListenerAdapter = this.mTeardownDetailWhenDone;
            this.mHeader.setVisibility(0);
            this.mFooter.setVisibility(0);
            this.mQsPanel.setGridContentVisibility(true);
            this.mQsPanelCallback.onScanStateChanged(false);
        }
        this.sendAccessibilityEvent(32);
        this.animateDetailVisibleDiff(mOpenX, mOpenY, b2, (Animator$AnimatorListener)animatorListenerAdapter);
    }
    
    public boolean isClosingDetail() {
        return this.mClosingDetail;
    }
    
    public boolean isShowingDetail() {
        return this.mDetailAdapter != null;
    }
    
    public WindowInsets onApplyWindowInsets(final WindowInsets windowInsets) {
        final Pair<Integer, Integer> cornerCutoutMargins = StatusBarWindowView.cornerCutoutMargins(windowInsets.getDisplayCutout(), this.getDisplay());
        if (cornerCutoutMargins == null) {
            this.mQsDetailHeader.setPaddingRelative(this.getResources().getDimensionPixelSize(R$dimen.qs_detail_header_padding), this.getPaddingTop(), this.getResources().getDimensionPixelSize(R$dimen.qs_detail_header_padding), this.getPaddingBottom());
        }
        else {
            this.mQsDetailHeader.setPadding((int)cornerCutoutMargins.first, this.getPaddingTop(), (int)cornerCutoutMargins.second, this.getPaddingBottom());
        }
        return super.onApplyWindowInsets(windowInsets);
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        FontSizeUtils.updateFontSize(this.mDetailDoneButton, R$dimen.qs_detail_button_text_size);
        FontSizeUtils.updateFontSize(this.mDetailSettingsButton, R$dimen.qs_detail_button_text_size);
        for (int i = 0; i < this.mDetailViews.size(); ++i) {
            ((View)this.mDetailViews.valueAt(i)).dispatchConfigurationChanged(configuration);
        }
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mDetailContent = (ViewGroup)this.findViewById(16908290);
        this.mDetailSettingsButton = (TextView)this.findViewById(16908314);
        this.mDetailDoneButton = (TextView)this.findViewById(16908313);
        final View viewById = this.findViewById(R$id.qs_detail_header);
        this.mQsDetailHeader = viewById;
        this.mQsDetailHeaderTitle = (TextView)viewById.findViewById(16908310);
        this.mQsDetailHeaderSwitchStub = (ViewStub)this.mQsDetailHeader.findViewById(R$id.toggle_stub);
        this.mQsDetailHeaderProgress = (ImageView)this.findViewById(R$id.qs_detail_header_progress);
        this.updateDetailText();
        this.mClipper = new QSDetailClipper((View)this);
        this.mDetailDoneButton.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                final QSDetail this$0 = QSDetail.this;
                this$0.announceForAccessibility((CharSequence)this$0.mContext.getString(R$string.accessibility_desc_quick_settings));
                QSDetail.this.mQsPanel.closeDetail();
            }
        });
    }
    
    public void setExpanded(final boolean b) {
        if (!b) {
            this.mTriggeredExpand = false;
        }
    }
    
    public void setFullyExpanded(final boolean mFullyExpanded) {
        this.mFullyExpanded = mFullyExpanded;
    }
    
    public void setHost(final QSTileHost qsTileHost) {
    }
    
    public void setQsPanel(final QSPanel mQsPanel, final QuickStatusBarHeader mHeader, final View mFooter) {
        this.mQsPanel = mQsPanel;
        this.mHeader = mHeader;
        this.mFooter = mFooter;
        mHeader.setCallback(this.mQsPanelCallback);
        this.mQsPanel.setCallback(this.mQsPanelCallback);
    }
    
    protected void setupDetailFooter(final DetailAdapter detailAdapter) {
        final Intent settingsIntent = detailAdapter.getSettingsIntent();
        final TextView mDetailSettingsButton = this.mDetailSettingsButton;
        int visibility;
        if (settingsIntent != null) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        mDetailSettingsButton.setVisibility(visibility);
        this.mDetailSettingsButton.setOnClickListener((View$OnClickListener)new _$$Lambda$QSDetail$NHQwfesA2Z6J0e0FBlLg3IIEATQ(detailAdapter, settingsIntent));
    }
    
    protected void setupDetailHeader(final DetailAdapter detailAdapter) {
        this.mQsDetailHeaderTitle.setText(detailAdapter.getTitle());
        final Boolean toggleState = detailAdapter.getToggleState();
        if (toggleState == null) {
            final Switch mQsDetailHeaderSwitch = this.mQsDetailHeaderSwitch;
            if (mQsDetailHeaderSwitch != null) {
                mQsDetailHeaderSwitch.setVisibility(4);
            }
            this.mQsDetailHeader.setClickable(false);
        }
        else {
            if (this.mQsDetailHeaderSwitch == null) {
                this.mQsDetailHeaderSwitch = (Switch)this.mQsDetailHeaderSwitchStub.inflate();
            }
            this.mQsDetailHeaderSwitch.setVisibility(0);
            this.handleToggleStateChanged(toggleState, detailAdapter.getToggleEnabled());
            this.mQsDetailHeader.setClickable(true);
            this.mQsDetailHeader.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
                public void onClick(final View view) {
                    final boolean b = QSDetail.this.mQsDetailHeaderSwitch.isChecked() ^ true;
                    QSDetail.this.mQsDetailHeaderSwitch.setChecked(b);
                    detailAdapter.setToggleState(b);
                }
            });
        }
    }
    
    public interface Callback
    {
        void onScanStateChanged(final boolean p0);
        
        void onShowingDetail(final DetailAdapter p0, final int p1, final int p2);
        
        void onToggleStateChanged(final boolean p0);
    }
}
