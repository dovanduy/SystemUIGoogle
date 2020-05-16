// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import java.util.Objects;
import com.android.systemui.statusbar.NotificationShelf;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.widget.ImageView;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.R$id;
import java.util.Iterator;
import android.view.ViewGroup$LayoutParams;
import java.util.Collection;
import com.android.internal.statusbar.StatusBarIcon;
import androidx.collection.ArrayMap;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import java.util.ArrayList;
import java.util.function.Function;
import com.android.systemui.statusbar.CrossFadeHelper;
import android.content.res.Resources;
import com.android.systemui.R$dimen;
import com.android.settingslib.Utils;
import com.android.systemui.R$attr;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.widget.FrameLayout$LayoutParams;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.graphics.Rect;
import com.android.systemui.statusbar.NotificationListener;
import android.view.ViewGroup;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.internal.util.ContrastColorUtil;
import android.content.Context;
import com.android.systemui.statusbar.StatusBarIconView;
import android.view.View;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.DarkIconDispatcher;

public class NotificationIconAreaController implements DarkReceiver, StateListener, WakeUpListener
{
    private boolean mAnimationsEnabled;
    private int mAodIconAppearTranslation;
    private int mAodIconTint;
    private NotificationIconContainer mAodIcons;
    private boolean mAodIconsVisible;
    private final KeyguardBypassController mBypassController;
    private NotificationIconContainer mCenteredIcon;
    protected View mCenteredIconArea;
    private int mCenteredIconTint;
    private StatusBarIconView mCenteredIconView;
    private Context mContext;
    private final ContrastColorUtil mContrastColorUtil;
    private final DozeParameters mDozeParameters;
    private int mIconHPadding;
    private int mIconSize;
    private int mIconTint;
    private final NotificationMediaManager mMediaManager;
    protected View mNotificationIconArea;
    private NotificationIconContainer mNotificationIcons;
    private ViewGroup mNotificationScrollLayout;
    final NotificationListener.NotificationSettingsListener mSettingsListener;
    private NotificationIconContainer mShelfIcons;
    private boolean mShowLowPriority;
    private StatusBar mStatusBar;
    private final StatusBarStateController mStatusBarStateController;
    private final Rect mTintArea;
    private final Runnable mUpdateStatusBarIcons;
    private final NotificationWakeUpCoordinator mWakeUpCoordinator;
    
    public NotificationIconAreaController(final Context mContext, final StatusBar mStatusBar, final StatusBarStateController mStatusBarStateController, final NotificationWakeUpCoordinator mWakeUpCoordinator, final KeyguardBypassController mBypassController, final NotificationMediaManager mMediaManager, final NotificationListener notificationListener, final DozeParameters mDozeParameters) {
        this.mUpdateStatusBarIcons = new _$$Lambda$NWCrb8vzuopzf5kAygkNeXndtBo(this);
        this.mIconTint = -1;
        this.mCenteredIconTint = -1;
        this.mTintArea = new Rect();
        this.mShowLowPriority = true;
        this.mSettingsListener = new NotificationListener.NotificationSettingsListener() {
            @Override
            public void onStatusBarIconsBehaviorChanged(final boolean b) {
                NotificationIconAreaController.this.mShowLowPriority = (b ^ true);
                if (NotificationIconAreaController.this.mNotificationScrollLayout != null) {
                    NotificationIconAreaController.this.updateStatusBarIcons();
                }
            }
        };
        this.mStatusBar = mStatusBar;
        this.mContrastColorUtil = ContrastColorUtil.getInstance(mContext);
        this.mContext = mContext;
        (this.mStatusBarStateController = mStatusBarStateController).addCallback((StatusBarStateController.StateListener)this);
        this.mMediaManager = mMediaManager;
        this.mDozeParameters = mDozeParameters;
        (this.mWakeUpCoordinator = mWakeUpCoordinator).addListener((NotificationWakeUpCoordinator.WakeUpListener)this);
        this.mBypassController = mBypassController;
        notificationListener.addNotificationSettingsListener(this.mSettingsListener);
        this.initializeNotificationAreaViews(mContext);
        this.reloadAodColor();
    }
    
    private void animateInAodIconTranslation() {
        this.mAodIcons.animate().setInterpolator((TimeInterpolator)Interpolators.DECELERATE_QUINT).translationY(0.0f).setDuration(200L).start();
    }
    
    private void applyNotificationIconsTint() {
        final int n = 0;
        int n2 = 0;
        int i;
        while (true) {
            i = n;
            if (n2 >= this.mNotificationIcons.getChildCount()) {
                break;
            }
            final StatusBarIconView statusBarIconView = (StatusBarIconView)this.mNotificationIcons.getChildAt(n2);
            if (statusBarIconView.getWidth() != 0) {
                this.updateTintForIcon(statusBarIconView, this.mIconTint);
            }
            else {
                statusBarIconView.executeOnLayout(new _$$Lambda$NotificationIconAreaController$kEHcYKNlJqRNuom7zI__dD3YiUQ(this, statusBarIconView));
            }
            ++n2;
        }
        while (i < this.mCenteredIcon.getChildCount()) {
            final StatusBarIconView statusBarIconView2 = (StatusBarIconView)this.mCenteredIcon.getChildAt(i);
            if (statusBarIconView2.getWidth() != 0) {
                this.updateTintForIcon(statusBarIconView2, this.mCenteredIconTint);
            }
            else {
                statusBarIconView2.executeOnLayout(new _$$Lambda$NotificationIconAreaController$DNX7QrLi_n7I734CPybT_ZrNpwI(this, statusBarIconView2));
            }
            ++i;
        }
        this.updateAodIconColors();
    }
    
    private FrameLayout$LayoutParams generateIconLayoutParams() {
        return new FrameLayout$LayoutParams(this.mIconSize + this.mIconHPadding * 2, this.getHeight());
    }
    
    private void reloadAodColor() {
        this.mAodIconTint = Utils.getColorAttrDefaultColor(this.mContext, R$attr.wallpaperTextColor);
    }
    
    private void reloadDimens(final Context context) {
        final Resources resources = context.getResources();
        this.mIconSize = resources.getDimensionPixelSize(17105474);
        this.mIconHPadding = resources.getDimensionPixelSize(R$dimen.status_bar_icon_padding);
        this.mAodIconAppearTranslation = resources.getDimensionPixelSize(R$dimen.shelf_appear_translation);
    }
    
    private void updateAnimations() {
        final int state = this.mStatusBarStateController.getState();
        final boolean b = true;
        final boolean b2 = state == 0;
        this.mAodIcons.setAnimationsEnabled(this.mAnimationsEnabled && !b2);
        this.mCenteredIcon.setAnimationsEnabled(this.mAnimationsEnabled && b2);
        this.mNotificationIcons.setAnimationsEnabled(this.mAnimationsEnabled && b2 && b);
    }
    
    private void updateAodIconColors() {
        for (int i = 0; i < this.mAodIcons.getChildCount(); ++i) {
            final StatusBarIconView statusBarIconView = (StatusBarIconView)this.mAodIcons.getChildAt(i);
            if (statusBarIconView.getWidth() != 0) {
                this.updateTintForIcon(statusBarIconView, this.mAodIconTint);
            }
            else {
                statusBarIconView.executeOnLayout(new _$$Lambda$NotificationIconAreaController$PUTDTipRCmrDLS4VQZByqHC4HFA(this, statusBarIconView));
            }
        }
    }
    
    private void updateAodIconsVisibility(final boolean b) {
        final boolean bypassEnabled = this.mBypassController.getBypassEnabled();
        boolean b2 = true;
        final int n = 0;
        int n2 = (bypassEnabled || this.mWakeUpCoordinator.getNotificationsFullyHidden()) ? 1 : 0;
        if (this.mStatusBarStateController.getState() != 1) {
            n2 = 0;
        }
        int mAodIconsVisible = n2;
        if (n2 != 0) {
            mAodIconsVisible = n2;
            if (this.mWakeUpCoordinator.isPulseExpanding()) {
                mAodIconsVisible = 0;
            }
        }
        if ((this.mAodIconsVisible ? 1 : 0) != mAodIconsVisible) {
            this.mAodIconsVisible = (mAodIconsVisible != 0);
            this.mAodIcons.animate().cancel();
            if (b) {
                if (this.mAodIcons.getVisibility() == 0) {
                    b2 = false;
                }
                if (this.mAodIconsVisible) {
                    if (b2) {
                        this.mAodIcons.setVisibility(0);
                        this.mAodIcons.setAlpha(1.0f);
                        this.appearAodIcons();
                    }
                    else {
                        this.animateInAodIconTranslation();
                        CrossFadeHelper.fadeIn((View)this.mAodIcons);
                    }
                }
                else {
                    this.animateInAodIconTranslation();
                    CrossFadeHelper.fadeOut((View)this.mAodIcons);
                }
            }
            else {
                this.mAodIcons.setAlpha(1.0f);
                this.mAodIcons.setTranslationY(0.0f);
                final NotificationIconContainer mAodIcons = this.mAodIcons;
                int visibility;
                if (mAodIconsVisible != 0) {
                    visibility = n;
                }
                else {
                    visibility = 4;
                }
                mAodIcons.setVisibility(visibility);
            }
        }
    }
    
    private void updateCenterIcon() {
        this.updateIconsForLayout((Function<NotificationEntry, StatusBarIconView>)_$$Lambda$NotificationIconAreaController$S6CJ2tXrA2ieNVmUpwBa8v9eeEY.INSTANCE, this.mCenteredIcon, false, true, false, false, false, false, false, true);
    }
    
    private void updateIconsForLayout(final Function<NotificationEntry, StatusBarIconView> function, final NotificationIconContainer notificationIconContainer, final boolean b, final boolean b2, final boolean b3, final boolean b4, final boolean b5, final boolean b6, final boolean b7, final boolean b8) {
        final ArrayList<StatusBarIconView> list = new ArrayList<StatusBarIconView>(this.mNotificationScrollLayout.getChildCount());
        for (int i = 0; i < this.mNotificationScrollLayout.getChildCount(); ++i) {
            final View child = this.mNotificationScrollLayout.getChildAt(i);
            if (child instanceof ExpandableNotificationRow) {
                final NotificationEntry entry = ((ExpandableNotificationRow)child).getEntry();
                if (this.shouldShowNotificationIcon(entry, b, b2, b3, b4, b5, b6, b7, b8)) {
                    final StatusBarIconView e = function.apply(entry);
                    if (e != null) {
                        list.add(e);
                    }
                }
            }
        }
        final ArrayMap<String, Object> replacingIcons = new ArrayMap<String, Object>();
        final ArrayList<StatusBarIconView> list2 = new ArrayList<StatusBarIconView>();
        for (int j = 0; j < notificationIconContainer.getChildCount(); ++j) {
            final View child2 = notificationIconContainer.getChildAt(j);
            if (child2 instanceof StatusBarIconView) {
                if (!list.contains(child2)) {
                    final StatusBarIconView e2 = (StatusBarIconView)child2;
                    final String groupKey = e2.getNotification().getGroupKey();
                    int n;
                    int index = n = 0;
                    int n2;
                    while (true) {
                        n2 = n;
                        if (index >= list.size()) {
                            break;
                        }
                        final StatusBarIconView statusBarIconView = list.get(index);
                        int n3 = n;
                        if (statusBarIconView.getSourceIcon().sameAs(e2.getSourceIcon())) {
                            n3 = n;
                            if (statusBarIconView.getNotification().getGroupKey().equals(groupKey)) {
                                if (n != 0) {
                                    n2 = 0;
                                    break;
                                }
                                n3 = 1;
                            }
                        }
                        ++index;
                        n = n3;
                    }
                    if (n2 != 0) {
                        ArrayList<StatusBarIcon> list3;
                        if ((list3 = replacingIcons.get(groupKey)) == null) {
                            list3 = new ArrayList<StatusBarIcon>();
                            replacingIcons.put(groupKey, list3);
                        }
                        list3.add(e2.getStatusBarIcon());
                    }
                    list2.add(e2);
                }
            }
        }
        final ArrayList<Object> list4 = new ArrayList<Object>();
        for (final String e3 : replacingIcons.keySet()) {
            if (replacingIcons.get(e3).size() != 1) {
                list4.add(e3);
            }
        }
        replacingIcons.removeAll(list4);
        notificationIconContainer.setReplacingIcons((ArrayMap<String, ArrayList<StatusBarIcon>>)replacingIcons);
        for (int size = list2.size(), k = 0; k < size; ++k) {
            notificationIconContainer.removeView((View)list2.get(k));
        }
        final FrameLayout$LayoutParams generateIconLayoutParams = this.generateIconLayoutParams();
        for (int l = 0; l < list.size(); ++l) {
            final StatusBarIconView statusBarIconView2 = list.get(l);
            notificationIconContainer.removeTransientView((View)statusBarIconView2);
            if (statusBarIconView2.getParent() == null) {
                if (b3) {
                    statusBarIconView2.setOnDismissListener(this.mUpdateStatusBarIcons);
                }
                notificationIconContainer.addView((View)statusBarIconView2, l, (ViewGroup$LayoutParams)generateIconLayoutParams);
            }
        }
        notificationIconContainer.setChangingViewPositions(true);
        for (int childCount = notificationIconContainer.getChildCount(), index2 = 0; index2 < childCount; ++index2) {
            final View child3 = notificationIconContainer.getChildAt(index2);
            final StatusBarIconView statusBarIconView3 = list.get(index2);
            if (child3 != statusBarIconView3) {
                notificationIconContainer.removeView((View)statusBarIconView3);
                notificationIconContainer.addView((View)statusBarIconView3, index2);
            }
        }
        notificationIconContainer.setChangingViewPositions(false);
        notificationIconContainer.setReplacingIcons(null);
    }
    
    private void updateShelfIcons() {
        this.updateIconsForLayout((Function<NotificationEntry, StatusBarIconView>)_$$Lambda$NotificationIconAreaController$afpYK1wAP1i0HTFHOa1jb1wzzAQ.INSTANCE, this.mShelfIcons, true, true, false, false, false, false, false, false);
    }
    
    private void updateTintForIcon(final StatusBarIconView statusBarIconView, final int decorColor) {
        final boolean equals = Boolean.TRUE.equals(statusBarIconView.getTag(R$id.icon_is_pre_L));
        int tint = 0;
        if (!equals || NotificationUtils.isGrayscale(statusBarIconView, this.mContrastColorUtil)) {
            tint = DarkIconDispatcher.getTint(this.mTintArea, (View)statusBarIconView, decorColor);
        }
        statusBarIconView.setStaticDrawableColor(tint);
        statusBarIconView.setDecorColor(decorColor);
    }
    
    public void appearAodIcons() {
        if (this.mDozeParameters.shouldControlScreenOff()) {
            this.mAodIcons.setTranslationY((float)(-this.mAodIconAppearTranslation));
            this.mAodIcons.setAlpha(0.0f);
            this.animateInAodIconTranslation();
            this.mAodIcons.animate().alpha(1.0f).setInterpolator((TimeInterpolator)Interpolators.LINEAR).setDuration(200L).start();
        }
    }
    
    public View getCenteredNotificationAreaView() {
        return this.mCenteredIconArea;
    }
    
    protected int getHeight() {
        return this.mStatusBar.getStatusBarHeight();
    }
    
    public View getNotificationInnerAreaView() {
        return this.mNotificationIconArea;
    }
    
    protected View inflateIconArea(final LayoutInflater layoutInflater) {
        return layoutInflater.inflate(R$layout.notification_icon_area, (ViewGroup)null);
    }
    
    public void initAodIcons() {
        final boolean b = this.mAodIcons != null;
        if (b) {
            this.mAodIcons.setAnimationsEnabled(false);
            this.mAodIcons.removeAllViews();
        }
        (this.mAodIcons = (NotificationIconContainer)this.mStatusBar.getNotificationShadeWindowView().findViewById(R$id.clock_notification_icon_container)).setOnLockScreen(true);
        this.updateAodIconsVisibility(false);
        this.updateAnimations();
        if (b) {
            this.updateAodNotificationIcons();
        }
    }
    
    protected void initializeNotificationAreaViews(final Context context) {
        this.reloadDimens(context);
        final LayoutInflater from = LayoutInflater.from(context);
        final View inflateIconArea = this.inflateIconArea(from);
        this.mNotificationIconArea = inflateIconArea;
        this.mNotificationIcons = (NotificationIconContainer)inflateIconArea.findViewById(R$id.notificationIcons);
        this.mNotificationScrollLayout = this.mStatusBar.getNotificationScrollLayout();
        final View inflate = from.inflate(R$layout.center_icon_area, (ViewGroup)null);
        this.mCenteredIconArea = inflate;
        this.mCenteredIcon = (NotificationIconContainer)inflate.findViewById(R$id.centeredIcon);
        this.initAodIcons();
    }
    
    @Override
    public void onDarkChanged(final Rect rect, final float n, final int n2) {
        if (rect == null) {
            this.mTintArea.setEmpty();
        }
        else {
            this.mTintArea.set(rect);
        }
        final View mNotificationIconArea = this.mNotificationIconArea;
        if (mNotificationIconArea != null) {
            if (DarkIconDispatcher.isInArea(rect, mNotificationIconArea)) {
                this.mIconTint = n2;
            }
        }
        else {
            this.mIconTint = n2;
        }
        final View mCenteredIconArea = this.mCenteredIconArea;
        if (mCenteredIconArea != null) {
            if (DarkIconDispatcher.isInArea(rect, mCenteredIconArea)) {
                this.mCenteredIconTint = n2;
            }
        }
        else {
            this.mCenteredIconTint = n2;
        }
        this.applyNotificationIconsTint();
    }
    
    public void onDensityOrFontScaleChanged(final Context context) {
        this.reloadDimens(context);
        final FrameLayout$LayoutParams generateIconLayoutParams = this.generateIconLayoutParams();
        final int n = 0;
        for (int i = 0; i < this.mNotificationIcons.getChildCount(); ++i) {
            this.mNotificationIcons.getChildAt(i).setLayoutParams((ViewGroup$LayoutParams)generateIconLayoutParams);
        }
        for (int j = 0; j < this.mShelfIcons.getChildCount(); ++j) {
            this.mShelfIcons.getChildAt(j).setLayoutParams((ViewGroup$LayoutParams)generateIconLayoutParams);
        }
        int n2 = 0;
        int k;
        while (true) {
            k = n;
            if (n2 >= this.mCenteredIcon.getChildCount()) {
                break;
            }
            this.mCenteredIcon.getChildAt(n2).setLayoutParams((ViewGroup$LayoutParams)generateIconLayoutParams);
            ++n2;
        }
        while (k < this.mAodIcons.getChildCount()) {
            this.mAodIcons.getChildAt(k).setLayoutParams((ViewGroup$LayoutParams)generateIconLayoutParams);
            ++k;
        }
    }
    
    @Override
    public void onDozingChanged(final boolean b) {
        this.mAodIcons.setDozing(b, this.mDozeParameters.getAlwaysOn() && !this.mDozeParameters.getDisplayNeedsBlanking(), 0L);
    }
    
    @Override
    public void onFullyHiddenChanged(final boolean b) {
        final boolean bypassEnabled = this.mBypassController.getBypassEnabled();
        boolean b2 = true;
        boolean b3 = true;
        if (!bypassEnabled) {
            if (!this.mDozeParameters.getAlwaysOn() || this.mDozeParameters.getDisplayNeedsBlanking()) {
                b3 = false;
            }
            b2 = (b3 & b);
        }
        this.updateAodIconsVisibility(b2);
        this.updateAodNotificationIcons();
    }
    
    @Override
    public void onPulseExpansionChanged(final boolean b) {
        if (b) {
            this.updateAodIconsVisibility(true);
        }
    }
    
    @Override
    public void onStateChanged(final int n) {
        this.updateAodIconsVisibility(false);
        this.updateAnimations();
    }
    
    public void onThemeChanged() {
        this.reloadAodColor();
        this.updateAodIconColors();
    }
    
    public void setAnimationsEnabled(final boolean mAnimationsEnabled) {
        this.mAnimationsEnabled = mAnimationsEnabled;
        this.updateAnimations();
    }
    
    public void setIsolatedIconLocation(final Rect rect, final boolean b) {
        this.mNotificationIcons.setIsolatedIconLocation(rect, b);
    }
    
    public void setupShelf(final NotificationShelf notificationShelf) {
        this.mShelfIcons = notificationShelf.getShelfIcons();
        notificationShelf.setCollapsedIcons(this.mNotificationIcons);
    }
    
    boolean shouldShouldLowPriorityIcons() {
        return this.mShowLowPriority;
    }
    
    protected boolean shouldShowNotificationIcon(final NotificationEntry notificationEntry, final boolean b, final boolean b2, final boolean b3, final boolean b4, final boolean b5, final boolean b6, final boolean b7, final boolean b8) {
        final boolean b9 = this.mCenteredIconView != null && notificationEntry.getIcons().getCenteredIcon() != null && Objects.equals(notificationEntry.getIcons().getCenteredIcon(), this.mCenteredIconView);
        if (b8) {
            return b9;
        }
        return (!b6 || !b9 || notificationEntry.isRowHeadsUp()) && (!notificationEntry.getRanking().isAmbient() || b) && (!b5 || !notificationEntry.getKey().equals(this.mMediaManager.getMediaNotificationKey())) && (b2 || notificationEntry.getImportance() >= 3) && notificationEntry.isTopLevelChild() && notificationEntry.getRow().getVisibility() != 8 && (!notificationEntry.isRowDismissed() || !b3) && (!b4 || !notificationEntry.isLastMessageFromReply()) && (b || !notificationEntry.shouldSuppressStatusBar()) && (!b7 || !notificationEntry.showingPulsing() || (this.mWakeUpCoordinator.getNotificationsFullyHidden() && notificationEntry.isPulseSuppressed()));
    }
    
    public void showIconCentered(final NotificationEntry notificationEntry) {
        StatusBarIconView centeredIcon;
        if (notificationEntry == null) {
            centeredIcon = null;
        }
        else {
            centeredIcon = notificationEntry.getIcons().getCenteredIcon();
        }
        if (!Objects.equals(this.mCenteredIconView, centeredIcon)) {
            this.mCenteredIconView = centeredIcon;
            this.updateNotificationIcons();
        }
    }
    
    public void showIconIsolated(final StatusBarIconView statusBarIconView, final boolean b) {
        this.mNotificationIcons.showIconIsolated(statusBarIconView, b);
    }
    
    public void updateAodNotificationIcons() {
        this.updateIconsForLayout((Function<NotificationEntry, StatusBarIconView>)_$$Lambda$NotificationIconAreaController$b7MkWJaTAeTosmR_aU3q7JZNLpI.INSTANCE, this.mAodIcons, false, true, true, true, true, true, this.mBypassController.getBypassEnabled(), false);
    }
    
    public void updateNotificationIcons() {
        this.updateStatusBarIcons();
        this.updateShelfIcons();
        this.updateCenterIcon();
        this.updateAodNotificationIcons();
        this.applyNotificationIconsTint();
    }
    
    public void updateStatusBarIcons() {
        this.updateIconsForLayout((Function<NotificationEntry, StatusBarIconView>)_$$Lambda$NotificationIconAreaController$ujxUr_qwlryo8PHBzga56kRshsA.INSTANCE, this.mNotificationIcons, false, this.mShowLowPriority, true, true, false, true, false, false);
    }
}
