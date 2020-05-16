// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.customize;

import android.view.View$OnLayoutChangeListener;
import android.os.Bundle;
import android.view.MenuItem;
import com.android.internal.logging.UiEventLogger$UiEventEnum;
import android.view.ViewGroup$LayoutParams;
import android.widget.LinearLayout$LayoutParams;
import android.content.res.Configuration;
import java.util.Iterator;
import java.util.List;
import com.android.systemui.plugins.qs.QSTile;
import java.util.ArrayList;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import com.android.systemui.R$string;
import android.view.View$OnClickListener;
import android.util.TypedValue;
import com.android.systemui.R$id;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import com.android.internal.logging.UiEventLoggerImpl;
import android.view.ContextThemeWrapper;
import com.android.systemui.R$style;
import android.util.AttributeSet;
import android.content.Context;
import com.android.internal.logging.UiEventLogger;
import android.view.View;
import android.widget.Toolbar;
import com.android.systemui.keyguard.ScreenLifecycle;
import androidx.recyclerview.widget.RecyclerView;
import com.android.systemui.plugins.qs.QS;
import com.android.systemui.statusbar.phone.NotificationsQuickSettingsContainer;
import com.android.systemui.statusbar.phone.LightBarController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.qs.QSTileHost;
import android.animation.Animator$AnimatorListener;
import com.android.systemui.qs.QSDetailClipper;
import android.widget.Toolbar$OnMenuItemClickListener;
import android.widget.LinearLayout;

public class QSCustomizer extends LinearLayout implements Toolbar$OnMenuItemClickListener
{
    private boolean isShown;
    private final QSDetailClipper mClipper;
    private final Animator$AnimatorListener mCollapseAnimationListener;
    private boolean mCustomizing;
    private final Animator$AnimatorListener mExpandAnimationListener;
    private QSTileHost mHost;
    private boolean mIsShowingNavBackdrop;
    private final KeyguardStateController.Callback mKeyguardCallback;
    private KeyguardStateController mKeyguardStateController;
    private final LightBarController mLightBarController;
    private NotificationsQuickSettingsContainer mNotifQsContainer;
    private boolean mOpening;
    private QS mQs;
    private RecyclerView mRecyclerView;
    private final ScreenLifecycle mScreenLifecycle;
    private TileAdapter mTileAdapter;
    private final TileQueryHelper mTileQueryHelper;
    private Toolbar mToolbar;
    private final View mTransparentView;
    private UiEventLogger mUiEventLogger;
    private int mX;
    private int mY;
    
    public QSCustomizer(final Context context, final AttributeSet set, final LightBarController mLightBarController, final KeyguardStateController mKeyguardStateController, final ScreenLifecycle mScreenLifecycle, final TileQueryHelper mTileQueryHelper) {
        super((Context)new ContextThemeWrapper(context, R$style.edit_theme), set);
        this.mUiEventLogger = (UiEventLogger)new UiEventLoggerImpl();
        this.mKeyguardCallback = new KeyguardStateController.Callback() {
            @Override
            public void onKeyguardShowingChanged() {
                if (!QSCustomizer.this.isAttachedToWindow()) {
                    return;
                }
                if (QSCustomizer.this.mKeyguardStateController.isShowing() && !QSCustomizer.this.mOpening) {
                    QSCustomizer.this.hide();
                }
            }
        };
        this.mExpandAnimationListener = (Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationCancel(final Animator animator) {
                QSCustomizer.this.mOpening = false;
                QSCustomizer.this.mNotifQsContainer.setCustomizerAnimating(false);
            }
            
            public void onAnimationEnd(final Animator animator) {
                if (QSCustomizer.this.isShown) {
                    QSCustomizer.this.setCustomizing(true);
                }
                QSCustomizer.this.mOpening = false;
                QSCustomizer.this.mNotifQsContainer.setCustomizerAnimating(false);
            }
        };
        this.mCollapseAnimationListener = (Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationCancel(final Animator animator) {
                if (!QSCustomizer.this.isShown) {
                    QSCustomizer.this.setVisibility(8);
                }
                QSCustomizer.this.mNotifQsContainer.setCustomizerAnimating(false);
            }
            
            public void onAnimationEnd(final Animator animator) {
                if (!QSCustomizer.this.isShown) {
                    QSCustomizer.this.setVisibility(8);
                }
                QSCustomizer.this.mNotifQsContainer.setCustomizerAnimating(false);
                QSCustomizer.this.mRecyclerView.setAdapter((RecyclerView.Adapter)QSCustomizer.this.mTileAdapter);
            }
        };
        LayoutInflater.from(this.getContext()).inflate(R$layout.qs_customize_panel_content, (ViewGroup)this);
        this.mClipper = new QSDetailClipper(this.findViewById(R$id.customize_container));
        this.mToolbar = (Toolbar)this.findViewById(16908704);
        final TypedValue typedValue = new TypedValue();
        super.mContext.getTheme().resolveAttribute(16843531, typedValue, true);
        this.mToolbar.setNavigationIcon(this.getResources().getDrawable(typedValue.resourceId, super.mContext.getTheme()));
        this.mToolbar.setNavigationOnClickListener((View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                QSCustomizer.this.hide();
            }
        });
        this.mToolbar.setOnMenuItemClickListener((Toolbar$OnMenuItemClickListener)this);
        this.mToolbar.getMenu().add(0, 1, 0, (CharSequence)super.mContext.getString(17041123));
        this.mToolbar.setTitle(R$string.qs_edit);
        this.mRecyclerView = (RecyclerView)this.findViewById(16908298);
        this.mTransparentView = this.findViewById(R$id.customizer_transparent_view);
        final TileAdapter tileAdapter = new TileAdapter(this.getContext());
        this.mTileAdapter = tileAdapter;
        (this.mTileQueryHelper = mTileQueryHelper).setListener((TileQueryHelper.TileStateListener)tileAdapter);
        this.mRecyclerView.setAdapter((RecyclerView.Adapter)this.mTileAdapter);
        this.mTileAdapter.getItemTouchHelper().attachToRecyclerView(this.mRecyclerView);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, this.getContext(), 3) {
            @Override
            public void onInitializeAccessibilityNodeInfoForItem(final Recycler recycler, final State state, final View view, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            }
        };
        layoutManager.setSpanSizeLookup(this.mTileAdapter.getSizeLookup());
        this.mRecyclerView.setLayoutManager((RecyclerView.LayoutManager)layoutManager);
        this.mRecyclerView.addItemDecoration(this.mTileAdapter.getItemDecoration());
        final DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        ((RecyclerView.ItemAnimator)itemAnimator).setMoveDuration(150L);
        this.mRecyclerView.setItemAnimator((RecyclerView.ItemAnimator)itemAnimator);
        this.mLightBarController = mLightBarController;
        this.mKeyguardStateController = mKeyguardStateController;
        this.mScreenLifecycle = mScreenLifecycle;
        this.updateNavBackDrop(this.getResources().getConfiguration());
    }
    
    private void queryTiles() {
        this.mTileQueryHelper.queryTiles(this.mHost);
    }
    
    private void reset() {
        this.mTileAdapter.resetTileSpecs(this.mHost, QSTileHost.getDefaultSpecs(super.mContext));
    }
    
    private void save() {
        if (this.mTileQueryHelper.isFinished()) {
            this.mTileAdapter.saveSpecs(this.mHost);
        }
    }
    
    private void setCustomizing(final boolean mCustomizing) {
        this.mCustomizing = mCustomizing;
        this.mQs.notifyCustomizeChanged();
    }
    
    private void setTileSpecs() {
        final ArrayList<String> tileSpecs = new ArrayList<String>();
        final Iterator<QSTile> iterator = this.mHost.getTiles().iterator();
        while (iterator.hasNext()) {
            tileSpecs.add(iterator.next().getTileSpec());
        }
        this.mTileAdapter.setTileSpecs(tileSpecs);
        this.mRecyclerView.setAdapter((RecyclerView.Adapter)this.mTileAdapter);
    }
    
    private void updateNavBackDrop(final Configuration configuration) {
        final View viewById = this.findViewById(R$id.nav_bar_background);
        final int smallestScreenWidthDp = configuration.smallestScreenWidthDp;
        int visibility = 0;
        final boolean mIsShowingNavBackdrop = smallestScreenWidthDp >= 600 || configuration.orientation != 2;
        this.mIsShowingNavBackdrop = mIsShowingNavBackdrop;
        if (viewById != null) {
            if (!mIsShowingNavBackdrop) {
                visibility = 8;
            }
            viewById.setVisibility(visibility);
        }
        this.updateNavColors();
    }
    
    private void updateNavColors() {
        this.mLightBarController.setQsCustomizing(this.mIsShowingNavBackdrop && this.isShown);
    }
    
    private void updateResources() {
        final LinearLayout$LayoutParams layoutParams = (LinearLayout$LayoutParams)this.mTransparentView.getLayoutParams();
        layoutParams.height = super.mContext.getResources().getDimensionPixelSize(17105427);
        this.mTransparentView.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
    }
    
    public void hide() {
        final boolean customizerAnimating = this.mScreenLifecycle.getScreenState() != 0;
        if (this.isShown) {
            this.mUiEventLogger.log((UiEventLogger$UiEventEnum)QSEditEvent.QS_EDIT_CLOSED);
            this.isShown = false;
            this.mToolbar.dismissPopupMenus();
            this.setCustomizing(false);
            this.save();
            if (customizerAnimating) {
                this.mClipper.animateCircularClip(this.mX, this.mY, false, this.mCollapseAnimationListener);
            }
            else {
                this.setVisibility(8);
            }
            this.mNotifQsContainer.setCustomizerAnimating(customizerAnimating);
            this.mNotifQsContainer.setCustomizerShowing(false);
            this.mKeyguardStateController.removeCallback(this.mKeyguardCallback);
            this.updateNavColors();
        }
    }
    
    public boolean isCustomizing() {
        return this.mCustomizing || this.mOpening;
    }
    
    public boolean isShown() {
        return this.isShown;
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.updateNavBackDrop(configuration);
        this.updateResources();
    }
    
    public boolean onMenuItemClick(final MenuItem menuItem) {
        if (menuItem.getItemId() == 1) {
            this.mUiEventLogger.log((UiEventLogger$UiEventEnum)QSEditEvent.QS_EDIT_RESET);
            this.reset();
        }
        return false;
    }
    
    public void restoreInstanceState(final Bundle bundle) {
        if (bundle.getBoolean("qs_customizing")) {
            this.setVisibility(0);
            this.addOnLayoutChangeListener((View$OnLayoutChangeListener)new View$OnLayoutChangeListener() {
                public void onLayoutChange(final View view, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
                    QSCustomizer.this.removeOnLayoutChangeListener((View$OnLayoutChangeListener)this);
                    QSCustomizer.this.showImmediately();
                }
            });
        }
    }
    
    public void saveInstanceState(final Bundle bundle) {
        if (this.isShown) {
            this.mKeyguardStateController.removeCallback(this.mKeyguardCallback);
        }
        bundle.putBoolean("qs_customizing", this.mCustomizing);
    }
    
    public void setContainer(final NotificationsQuickSettingsContainer mNotifQsContainer) {
        this.mNotifQsContainer = mNotifQsContainer;
    }
    
    public void setEditLocation(final int n, final int n2) {
        final int[] locationOnScreen = this.findViewById(R$id.customize_container).getLocationOnScreen();
        this.mX = n - locationOnScreen[0];
        this.mY = n2 - locationOnScreen[1];
    }
    
    public void setHost(final QSTileHost qsTileHost) {
        this.mHost = qsTileHost;
        this.mTileAdapter.setHost(qsTileHost);
    }
    
    public void setQs(final QS mQs) {
        this.mQs = mQs;
    }
    
    public void show(final int n, final int n2) {
        if (!this.isShown) {
            final int[] locationOnScreen = this.findViewById(R$id.customize_container).getLocationOnScreen();
            this.mX = n - locationOnScreen[0];
            this.mY = n2 - locationOnScreen[1];
            this.mUiEventLogger.log((UiEventLogger$UiEventEnum)QSEditEvent.QS_EDIT_OPEN);
            this.isShown = true;
            this.mOpening = true;
            this.setTileSpecs();
            this.setVisibility(0);
            this.mClipper.animateCircularClip(this.mX, this.mY, true, this.mExpandAnimationListener);
            this.queryTiles();
            this.mNotifQsContainer.setCustomizerAnimating(true);
            this.mNotifQsContainer.setCustomizerShowing(true);
            this.mKeyguardStateController.addCallback(this.mKeyguardCallback);
            this.updateNavColors();
        }
    }
    
    public void showImmediately() {
        if (!this.isShown) {
            this.setVisibility(0);
            this.mClipper.showBackground();
            this.isShown = true;
            this.setTileSpecs();
            this.setCustomizing(true);
            this.queryTiles();
            this.mNotifQsContainer.setCustomizerAnimating(false);
            this.mNotifQsContainer.setCustomizerShowing(true);
            this.mKeyguardStateController.addCallback(this.mKeyguardCallback);
            this.updateNavColors();
        }
    }
}
