// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import com.android.systemui.Dependency;
import com.android.systemui.plugins.qs.QSTileView;
import java.util.Iterator;
import java.util.Collection;
import com.android.systemui.util.Utils;
import com.android.systemui.plugins.qs.QSTile;
import android.util.Log;
import com.android.systemui.plugins.qs.QS;
import android.view.View;
import java.util.ArrayList;
import com.android.systemui.tuner.TunerService;
import android.view.View$OnAttachStateChangeListener;
import android.view.View$OnLayoutChangeListener;

public class QSAnimator implements Callback, PageListener, Listener, View$OnLayoutChangeListener, View$OnAttachStateChangeListener, Tunable
{
    private final ArrayList<View> mAllViews;
    private boolean mAllowFancy;
    private TouchAnimator mBrightnessAnimator;
    private TouchAnimator mFirstPageAnimator;
    private TouchAnimator mFirstPageDelayedAnimator;
    private boolean mFullRows;
    private QSTileHost mHost;
    private float mLastPosition;
    private final Listener mNonFirstPageListener;
    private TouchAnimator mNonfirstPageAnimator;
    private TouchAnimator mNonfirstPageDelayedAnimator;
    private int mNumQuickTiles;
    private boolean mOnFirstPage;
    private boolean mOnKeyguard;
    private PagedTileLayout mPagedLayout;
    private final QS mQs;
    private final QSPanel mQsPanel;
    private final QuickQSPanel mQuickQsPanel;
    private final ArrayList<View> mQuickQsViews;
    private boolean mShowCollapsedOnKeyguard;
    private TouchAnimator mTranslationXAnimator;
    private TouchAnimator mTranslationYAnimator;
    private Runnable mUpdateAnimators;
    
    public QSAnimator(final QS mQs, final QuickQSPanel mQuickQsPanel, final QSPanel mQsPanel) {
        this.mAllViews = new ArrayList<View>();
        this.mQuickQsViews = new ArrayList<View>();
        this.mOnFirstPage = true;
        this.mNonFirstPageListener = new ListenerAdapter() {
            @Override
            public void onAnimationAtEnd() {
                QSAnimator.this.mQuickQsPanel.setVisibility(4);
            }
            
            @Override
            public void onAnimationStarted() {
                QSAnimator.this.mQuickQsPanel.setVisibility(0);
            }
        };
        this.mUpdateAnimators = new Runnable() {
            @Override
            public void run() {
                QSAnimator.this.updateAnimators();
                QSAnimator.this.setCurrentPosition();
            }
        };
        this.mQs = mQs;
        this.mQuickQsPanel = mQuickQsPanel;
        (this.mQsPanel = mQsPanel).addOnAttachStateChangeListener((View$OnAttachStateChangeListener)this);
        mQs.getView().addOnLayoutChangeListener((View$OnLayoutChangeListener)this);
        if (this.mQsPanel.isAttachedToWindow()) {
            this.onViewAttachedToWindow(null);
        }
        final QSPanel.QSTileLayout tileLayout = this.mQsPanel.getTileLayout();
        if (tileLayout instanceof PagedTileLayout) {
            this.mPagedLayout = (PagedTileLayout)tileLayout;
        }
        else {
            Log.w("QSAnimator", "QS Not using page layout");
        }
        mQsPanel.setPageListener(this);
    }
    
    private void clearAnimationState() {
        final int size = this.mAllViews.size();
        this.mQuickQsPanel.setAlpha(0.0f);
        for (int i = 0; i < size; ++i) {
            final View view = this.mAllViews.get(i);
            view.setAlpha(1.0f);
            view.setTranslationX(0.0f);
            view.setTranslationY(0.0f);
        }
        for (int size2 = this.mQuickQsViews.size(), j = 0; j < size2; ++j) {
            this.mQuickQsViews.get(j).setVisibility(0);
        }
    }
    
    private void getRelativePosition(final int[] array, final View view, final View view2) {
        array[0] = view.getWidth() / 2 + 0;
        array[1] = 0;
        this.getRelativePositionInt(array, view, view2);
    }
    
    private void getRelativePositionInt(final int[] array, final View view, final View view2) {
        if (view != view2) {
            if (view != null) {
                if (!(view instanceof TilePage)) {
                    array[0] += view.getLeft();
                    array[1] += view.getTop();
                }
                this.getRelativePositionInt(array, (View)view.getParent(), view2);
            }
        }
    }
    
    private boolean isIconInAnimatedRow(final int n) {
        final PagedTileLayout mPagedLayout = this.mPagedLayout;
        boolean b = false;
        if (mPagedLayout == null) {
            return false;
        }
        final int columnCount = mPagedLayout.getColumnCount();
        if (n < (this.mNumQuickTiles + columnCount - 1) / columnCount * columnCount) {
            b = true;
        }
        return b;
    }
    
    private void setCurrentPosition() {
        this.setPosition(this.mLastPosition);
    }
    
    private void updateAnimators() {
        final Builder builder = new TouchAnimator.Builder();
        final Builder builder2 = new TouchAnimator.Builder();
        final Builder builder3 = new TouchAnimator.Builder();
        if (this.mQsPanel.getHost() == null) {
            return;
        }
        final Collection<QSTile> tiles = this.mQsPanel.getHost().getTiles();
        final int[] array = new int[2];
        final int[] array2 = new int[2];
        this.clearAnimationState();
        this.mAllViews.clear();
        this.mQuickQsViews.clear();
        final QSPanel.QSTileLayout tileLayout = this.mQsPanel.getTileLayout();
        this.mAllViews.add((View)tileLayout);
        int measuredHeight;
        if (this.mQs.getView() != null) {
            measuredHeight = this.mQs.getView().getMeasuredHeight();
        }
        else {
            measuredHeight = 0;
        }
        int measuredWidth;
        if (this.mQs.getView() != null) {
            measuredWidth = this.mQs.getView().getMeasuredWidth();
        }
        else {
            measuredWidth = 0;
        }
        final int n = measuredHeight - this.mQs.getHeader().getBottom() + this.mQs.getHeader().getPaddingBottom();
        float n2 = (float)n;
        builder.addFloat(tileLayout, "translationY", n2, 0.0f);
        final Iterator<QSTile> iterator = tiles.iterator();
        int n4;
        int n3 = n4 = 0;
        while (iterator.hasNext()) {
            final QSTile qsTile = iterator.next();
            final QSTileView tileView = this.mQsPanel.getTileView(qsTile);
            if (tileView == null) {
                final StringBuilder sb = new StringBuilder();
                sb.append("tileView is null ");
                sb.append(qsTile.getTileSpec());
                Log.e("QSAnimator", sb.toString());
            }
            else {
                final View iconView = tileView.getIcon().getIconView();
                final int n5 = n;
                final View view = this.mQs.getView();
                final float n6 = n2;
                final int numVisibleTiles = this.mQuickQsPanel.getTileLayout().getNumVisibleTiles();
                final int n7 = measuredWidth;
                if (n3 < numVisibleTiles && this.mAllowFancy) {
                    final QSTileView tileView2 = this.mQuickQsPanel.getTileView(qsTile);
                    if (tileView2 == null) {
                        continue;
                    }
                    final int n8 = array[0];
                    this.getRelativePosition(array, tileView2.getIcon().getIconView(), view);
                    this.getRelativePosition(array2, iconView, view);
                    final int n9 = array2[0] - array[0];
                    final int n10 = array2[1] - array[1];
                    final int n11 = array[0] - n8;
                    if (n3 < tileLayout.getNumVisibleTiles()) {
                        builder2.addFloat(tileView2, "translationX", 0.0f, (float)n9);
                        builder3.addFloat(tileView2, "translationY", 0.0f, (float)n10);
                        builder2.addFloat(tileView, "translationX", (float)(-n9), 0.0f);
                        builder3.addFloat(tileView, "translationY", (float)(-n10), 0.0f);
                    }
                    else {
                        builder.addFloat(tileView2, "alpha", 1.0f, 0.0f);
                        builder3.addFloat(tileView2, "translationY", 0.0f, (float)n10);
                        int n12;
                        if (this.mQsPanel.isLayoutRtl()) {
                            n12 = n9 - n7;
                        }
                        else {
                            n12 = n9 + n7;
                        }
                        builder2.addFloat(tileView2, "translationX", 0.0f, (float)n12);
                    }
                    this.mQuickQsViews.add(tileView.getIconWithBackground());
                    this.mAllViews.add((View)tileView.getIcon());
                    this.mAllViews.add((View)tileView2);
                    n4 = n11;
                }
                else if (this.mFullRows && this.isIconInAnimatedRow(n3)) {
                    array[0] += n4;
                    this.getRelativePosition(array2, iconView, view);
                    final int n13 = array2[0];
                    final int n14 = array[0];
                    final int n15 = array2[1];
                    final int n16 = array[1];
                    builder.addFloat(tileView, "translationY", n6, 0.0f);
                    builder2.addFloat(tileView, "translationX", (float)(-(n13 - n14)), 0.0f);
                    final float n17 = (float)(-(n15 - n16));
                    builder3.addFloat(tileView, "translationY", n17, 0.0f);
                    builder3.addFloat(iconView, "translationY", n17, 0.0f);
                    this.mAllViews.add(iconView);
                }
                else {
                    builder.addFloat(tileView, "alpha", 0.0f, 1.0f);
                    builder.addFloat(tileView, "translationY", (float)(-n5), 0.0f);
                }
                this.mAllViews.add((View)tileView);
                ++n3;
                n2 = n6;
                measuredWidth = n7;
            }
        }
        if (Utils.useQsMediaPlayer(this.mQsPanel.getContext())) {
            final View mediaPanel = this.mQsPanel.getMediaPanel();
            final View view2 = this.mQuickQsPanel.getMediaPlayer().getView();
            builder2.addFloat(mediaPanel, "alpha", 0.0f, 1.0f);
            builder2.addFloat(view2, "alpha", 1.0f, 0.0f);
        }
        if (this.mAllowFancy) {
            final View brightnessView = this.mQsPanel.getBrightnessView();
            if (brightnessView != null) {
                builder.addFloat(brightnessView, "translationY", n2, 0.0f);
                final TouchAnimator.Builder builder4 = new TouchAnimator.Builder();
                builder4.addFloat(brightnessView, "alpha", 0.0f, 1.0f);
                builder4.setStartDelay(0.5f);
                this.mBrightnessAnimator = builder4.build();
                this.mAllViews.add(brightnessView);
            }
            else {
                this.mBrightnessAnimator = null;
            }
            builder.setListener(this);
            this.mFirstPageAnimator = builder.build();
            final TouchAnimator.Builder builder5 = new TouchAnimator.Builder();
            builder5.setStartDelay(0.86f);
            builder5.addFloat(tileLayout, "alpha", 0.0f, 1.0f);
            builder5.addFloat(this.mQsPanel.getDivider(), "alpha", 0.0f, 1.0f);
            builder5.addFloat(this.mQsPanel.getFooter().getView(), "alpha", 0.0f, 1.0f);
            this.mFirstPageDelayedAnimator = builder5.build();
            this.mAllViews.add(this.mQsPanel.getDivider());
            this.mAllViews.add(this.mQsPanel.getFooter().getView());
            float n18;
            if (tiles.size() <= 3) {
                n18 = 1.0f;
            }
            else if (tiles.size() <= 6) {
                n18 = 0.4f;
            }
            else {
                n18 = 0.0f;
            }
            final PathInterpolatorBuilder pathInterpolatorBuilder = new PathInterpolatorBuilder(0.0f, 0.0f, n18, 1.0f);
            builder2.setInterpolator(pathInterpolatorBuilder.getXInterpolator());
            builder3.setInterpolator(pathInterpolatorBuilder.getYInterpolator());
            this.mTranslationXAnimator = builder2.build();
            this.mTranslationYAnimator = builder3.build();
        }
        final TouchAnimator.Builder builder6 = new TouchAnimator.Builder();
        builder6.addFloat(this.mQuickQsPanel, "alpha", 1.0f, 0.0f);
        builder6.addFloat(this.mQsPanel.getDivider(), "alpha", 0.0f, 1.0f);
        builder6.setListener(this.mNonFirstPageListener);
        builder6.setEndDelay(0.5f);
        this.mNonfirstPageAnimator = builder6.build();
        final TouchAnimator.Builder builder7 = new TouchAnimator.Builder();
        builder7.setStartDelay(0.14f);
        builder7.addFloat(tileLayout, "alpha", 0.0f, 1.0f);
        this.mNonfirstPageDelayedAnimator = builder7.build();
    }
    
    private void updateQQSVisibility() {
        final QuickQSPanel mQuickQsPanel = this.mQuickQsPanel;
        int visibility;
        if (this.mOnKeyguard && !this.mShowCollapsedOnKeyguard) {
            visibility = 4;
        }
        else {
            visibility = 0;
        }
        mQuickQsPanel.setVisibility(visibility);
    }
    
    @Override
    public void onAnimationAtEnd() {
        this.mQuickQsPanel.setVisibility(4);
        for (int size = this.mQuickQsViews.size(), i = 0; i < size; ++i) {
            this.mQuickQsViews.get(i).setVisibility(0);
        }
    }
    
    @Override
    public void onAnimationAtStart() {
        this.mQuickQsPanel.setVisibility(0);
    }
    
    @Override
    public void onAnimationStarted() {
        this.updateQQSVisibility();
        if (this.mOnFirstPage) {
            for (int size = this.mQuickQsViews.size(), i = 0; i < size; ++i) {
                this.mQuickQsViews.get(i).setVisibility(4);
            }
        }
    }
    
    public void onLayoutChange(final View view, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        this.mQsPanel.post(this.mUpdateAnimators);
    }
    
    @Override
    public void onPageChanged(final boolean mOnFirstPage) {
        if (this.mOnFirstPage == mOnFirstPage) {
            return;
        }
        if (!mOnFirstPage) {
            this.clearAnimationState();
        }
        this.mOnFirstPage = mOnFirstPage;
    }
    
    public void onRtlChanged() {
        this.updateAnimators();
    }
    
    @Override
    public void onTilesChanged() {
        this.mQsPanel.post(this.mUpdateAnimators);
    }
    
    public void onTuningChanged(final String anObject, final String s) {
        if ("sysui_qs_fancy_anim".equals(anObject)) {
            if (!(this.mAllowFancy = TunerService.parseIntegerSwitch(s, true))) {
                this.clearAnimationState();
            }
        }
        else if ("sysui_qs_move_whole_rows".equals(anObject)) {
            this.mFullRows = TunerService.parseIntegerSwitch(s, true);
        }
        else if ("sysui_qqs_count".equals(anObject)) {
            this.mNumQuickTiles = QuickQSPanel.parseNumTiles(s);
            this.clearAnimationState();
        }
        this.updateAnimators();
    }
    
    public void onViewAttachedToWindow(final View view) {
        Dependency.get(TunerService.class).addTunable((TunerService.Tunable)this, "sysui_qs_fancy_anim", "sysui_qs_move_whole_rows", "sysui_qqs_count");
    }
    
    public void onViewDetachedFromWindow(final View view) {
        final QSTileHost mHost = this.mHost;
        if (mHost != null) {
            mHost.removeCallback(this);
        }
        Dependency.get(TunerService.class).removeTunable((TunerService.Tunable)this);
    }
    
    public void setHost(final QSTileHost mHost) {
        (this.mHost = mHost).addCallback(this);
        this.updateAnimators();
    }
    
    public void setOnKeyguard(final boolean mOnKeyguard) {
        this.mOnKeyguard = mOnKeyguard;
        this.updateQQSVisibility();
        if (this.mOnKeyguard) {
            this.clearAnimationState();
        }
    }
    
    public void setPosition(float n) {
        if (this.mFirstPageAnimator == null) {
            return;
        }
        if (this.mOnKeyguard) {
            if (this.mShowCollapsedOnKeyguard) {
                n = 0.0f;
            }
            else {
                n = 1.0f;
            }
        }
        this.mLastPosition = n;
        if (this.mOnFirstPage && this.mAllowFancy) {
            this.mQuickQsPanel.setAlpha(1.0f);
            this.mFirstPageAnimator.setPosition(n);
            this.mFirstPageDelayedAnimator.setPosition(n);
            this.mTranslationXAnimator.setPosition(n);
            this.mTranslationYAnimator.setPosition(n);
            final TouchAnimator mBrightnessAnimator = this.mBrightnessAnimator;
            if (mBrightnessAnimator != null) {
                mBrightnessAnimator.setPosition(n);
            }
        }
        else {
            this.mNonfirstPageAnimator.setPosition(n);
            this.mNonfirstPageDelayedAnimator.setPosition(n);
        }
    }
    
    void setShowCollapsedOnKeyguard(final boolean mShowCollapsedOnKeyguard) {
        this.mShowCollapsedOnKeyguard = mShowCollapsedOnKeyguard;
        this.updateQQSVisibility();
        this.setCurrentPosition();
    }
}
