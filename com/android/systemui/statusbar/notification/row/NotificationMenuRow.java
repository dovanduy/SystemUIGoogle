// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import com.android.systemui.statusbar.AlphaOptimizedImageView;
import com.android.systemui.R$color;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import android.graphics.Point;
import android.service.notification.StatusBarNotification;
import com.android.internal.annotations.VisibleForTesting;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.content.res.Resources;
import java.util.Collection;
import android.provider.Settings$Secure;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import android.view.ViewGroup;
import android.os.Looper;
import com.android.systemui.R$bool;
import android.util.ArrayMap;
import com.android.systemui.statusbar.notification.people.PeopleNotificationIdentifier;
import android.view.View;
import java.util.Map;
import android.widget.FrameLayout;
import java.util.ArrayList;
import android.os.Handler;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View$OnClickListener;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;

public class NotificationMenuRow implements NotificationMenuRowPlugin, View$OnClickListener, LayoutListener
{
    private float mAlpha;
    private boolean mAnimating;
    private MenuItem mAppOpsItem;
    private CheckForDrag mCheckForDrag;
    private Context mContext;
    private boolean mDismissing;
    private ValueAnimator mFadeAnimator;
    private Handler mHandler;
    private int mHorizSpaceForIcon;
    private int[] mIconLocation;
    private int mIconPadding;
    private boolean mIconsPlaced;
    private NotificationMenuItem mInfoItem;
    private boolean mIsForeground;
    private boolean mIsUserTouching;
    private ArrayList<MenuItem> mLeftMenuItems;
    private FrameLayout mMenuContainer;
    private boolean mMenuFadedIn;
    private final Map<View, MenuItem> mMenuItemsByView;
    private OnMenuEventListener mMenuListener;
    private boolean mMenuSnapped;
    private boolean mMenuSnappedOnLeft;
    private boolean mOnLeft;
    private ExpandableNotificationRow mParent;
    private int[] mParentLocation;
    private final PeopleNotificationIdentifier mPeopleNotificationIdentifier;
    private ArrayList<MenuItem> mRightMenuItems;
    private boolean mShouldShowMenu;
    private boolean mSnapping;
    private MenuItem mSnoozeItem;
    private float mTranslation;
    private int mVertSpaceForIcons;
    
    public NotificationMenuRow(final Context mContext, final PeopleNotificationIdentifier mPeopleNotificationIdentifier) {
        this.mMenuItemsByView = (Map<View, MenuItem>)new ArrayMap();
        this.mIconLocation = new int[2];
        this.mParentLocation = new int[2];
        this.mHorizSpaceForIcon = -1;
        this.mVertSpaceForIcons = -1;
        this.mIconPadding = -1;
        this.mAlpha = 0.0f;
        this.mContext = mContext;
        this.mShouldShowMenu = mContext.getResources().getBoolean(R$bool.config_showNotificationGear);
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mLeftMenuItems = new ArrayList<MenuItem>();
        this.mRightMenuItems = new ArrayList<MenuItem>();
        this.mPeopleNotificationIdentifier = mPeopleNotificationIdentifier;
    }
    
    private void addMenuView(final MenuItem menuItem, final ViewGroup viewGroup) {
        final View menuView = menuItem.getMenuView();
        if (menuView != null) {
            menuView.setAlpha(this.mAlpha);
            viewGroup.addView(menuView);
            menuView.setOnClickListener((View$OnClickListener)this);
            final FrameLayout$LayoutParams layoutParams = (FrameLayout$LayoutParams)menuView.getLayoutParams();
            final int mHorizSpaceForIcon = this.mHorizSpaceForIcon;
            layoutParams.width = mHorizSpaceForIcon;
            layoutParams.height = mHorizSpaceForIcon;
            menuView.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
        }
        this.mMenuItemsByView.put(menuView, menuItem);
    }
    
    static MenuItem createAppOpsItem(final Context context) {
        return new NotificationMenuItem(context, null, (NotificationGuts.GutsContent)LayoutInflater.from(context).inflate(R$layout.app_ops_info, (ViewGroup)null, false), -1);
    }
    
    static NotificationMenuItem createConversationItem(final Context context) {
        return new NotificationMenuItem(context, context.getResources().getString(R$string.notification_menu_gear_description), (NotificationGuts.GutsContent)LayoutInflater.from(context).inflate(R$layout.notification_conversation_info, (ViewGroup)null, false), R$drawable.ic_settings);
    }
    
    static NotificationMenuItem createInfoItem(final Context context) {
        return new NotificationMenuItem(context, context.getResources().getString(R$string.notification_menu_gear_description), (NotificationGuts.GutsContent)LayoutInflater.from(context).inflate(R$layout.notification_info, (ViewGroup)null, false), R$drawable.ic_settings);
    }
    
    private void createMenuViews(final boolean b, final boolean mIsForeground) {
        this.mIsForeground = mIsForeground;
        final Resources resources = this.mContext.getResources();
        this.mHorizSpaceForIcon = resources.getDimensionPixelSize(R$dimen.notification_menu_icon_size);
        this.mVertSpaceForIcons = resources.getDimensionPixelSize(R$dimen.notification_min_height);
        this.mLeftMenuItems.clear();
        this.mRightMenuItems.clear();
        final int int1 = Settings$Secure.getInt(this.mContext.getContentResolver(), "show_notification_snooze", 0);
        boolean b2 = true;
        if (int1 != 1) {
            b2 = false;
        }
        if (!mIsForeground && b2) {
            this.mSnoozeItem = createSnoozeItem(this.mContext);
        }
        this.mAppOpsItem = createAppOpsItem(this.mContext);
        final NotificationEntry entry = this.mParent.getEntry();
        if (this.mPeopleNotificationIdentifier.getPeopleNotificationType(entry.getSbn(), entry.getRanking()) != 0) {
            this.mInfoItem = createConversationItem(this.mContext);
        }
        else {
            this.mInfoItem = createInfoItem(this.mContext);
        }
        if (!mIsForeground && b2) {
            this.mRightMenuItems.add(this.mSnoozeItem);
        }
        this.mRightMenuItems.add(this.mInfoItem);
        this.mRightMenuItems.add(this.mAppOpsItem);
        this.mLeftMenuItems.addAll(this.mRightMenuItems);
        this.populateMenuViews();
        if (b) {
            this.resetState(false);
        }
        else {
            this.mIconsPlaced = false;
            this.setMenuLocation();
            if (!this.mIsUserTouching) {
                this.onSnapOpen();
            }
        }
    }
    
    static MenuItem createSnoozeItem(final Context context) {
        return new NotificationMenuItem(context, context.getResources().getString(R$string.notification_menu_snooze_description), (NotificationGuts.GutsContent)LayoutInflater.from(context).inflate(R$layout.notification_snooze, (ViewGroup)null, false), R$drawable.ic_snooze);
    }
    
    private void fadeInMenu(final float n) {
        if (!this.mDismissing) {
            if (!this.mAnimating) {
                if (this.isMenuLocationChange()) {
                    this.setMenuAlpha(0.0f);
                }
                final float mTranslation = this.mTranslation;
                final boolean b = mTranslation > 0.0f;
                this.setMenuLocation();
                (this.mFadeAnimator = ValueAnimator.ofFloat(new float[] { this.mAlpha, 1.0f })).addUpdateListener((ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
                    public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                        final float abs = Math.abs(mTranslation);
                        if (((b && mTranslation <= n) || (!b && abs <= n)) && !NotificationMenuRow.this.mMenuFadedIn) {
                            NotificationMenuRow.this.setMenuAlpha((float)valueAnimator.getAnimatedValue());
                        }
                    }
                });
                this.mFadeAnimator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                    public void onAnimationCancel(final Animator animator) {
                        NotificationMenuRow.this.setMenuAlpha(0.0f);
                    }
                    
                    public void onAnimationEnd(final Animator animator) {
                        final NotificationMenuRow this$0 = NotificationMenuRow.this;
                        boolean b = false;
                        this$0.mAnimating = false;
                        final NotificationMenuRow this$2 = NotificationMenuRow.this;
                        if (this$2.mAlpha == 1.0f) {
                            b = true;
                        }
                        this$2.mMenuFadedIn = b;
                    }
                    
                    public void onAnimationStart(final Animator animator) {
                        NotificationMenuRow.this.mAnimating = true;
                    }
                });
                this.mFadeAnimator.setInterpolator((TimeInterpolator)Interpolators.ALPHA_IN);
                this.mFadeAnimator.setDuration(200L);
                this.mFadeAnimator.start();
            }
        }
    }
    
    private boolean isMenuLocationChange() {
        final boolean b = this.mTranslation > this.mIconPadding;
        final boolean b2 = this.mTranslation < -this.mIconPadding;
        return (this.isMenuOnLeft() && b2) || (!this.isMenuOnLeft() && b);
    }
    
    private void populateMenuViews() {
        final FrameLayout mMenuContainer = this.mMenuContainer;
        if (mMenuContainer != null) {
            mMenuContainer.removeAllViews();
            this.mMenuItemsByView.clear();
        }
        else {
            this.mMenuContainer = new FrameLayout(this.mContext);
        }
        ArrayList<MenuItem> list;
        if (this.mOnLeft) {
            list = this.mLeftMenuItems;
        }
        else {
            list = this.mRightMenuItems;
        }
        for (int i = 0; i < list.size(); ++i) {
            this.addMenuView((MenuItem)list.get(i), (ViewGroup)this.mMenuContainer);
        }
    }
    
    private void resetState(final boolean b) {
        this.setMenuAlpha(0.0f);
        this.mIconsPlaced = false;
        this.mMenuFadedIn = false;
        this.mAnimating = false;
        this.mSnapping = false;
        this.mDismissing = false;
        this.mMenuSnapped = false;
        this.setMenuLocation();
        final OnMenuEventListener mMenuListener = this.mMenuListener;
        if (mMenuListener != null && b) {
            mMenuListener.onMenuReset((View)this.mParent);
        }
    }
    
    private void setAppName(final String s, final ArrayList<MenuItem> list) {
        final Resources resources = this.mContext.getResources();
        for (int size = list.size(), i = 0; i < size; ++i) {
            final MenuItem menuItem = list.get(i);
            final String format = String.format(resources.getString(R$string.notification_menu_accessibility), s, menuItem.getContentDescription());
            final View menuView = menuItem.getMenuView();
            if (menuView != null) {
                menuView.setContentDescription((CharSequence)format);
            }
        }
    }
    
    private void setMenuLocation() {
        final float mTranslation = this.mTranslation;
        int i = 0;
        boolean mOnLeft;
        if (mTranslation > 0.0f) {
            mOnLeft = true;
        }
        else {
            mOnLeft = false;
        }
        if ((!this.mIconsPlaced || mOnLeft != this.isMenuOnLeft()) && !this.isSnapping()) {
            final FrameLayout mMenuContainer = this.mMenuContainer;
            if (mMenuContainer != null) {
                if (mMenuContainer.isAttachedToWindow()) {
                    if (this.mOnLeft != (this.mOnLeft = mOnLeft)) {
                        this.populateMenuViews();
                    }
                    while (i < this.mMenuContainer.getChildCount()) {
                        final View child = this.mMenuContainer.getChildAt(i);
                        final float n = (float)(this.mHorizSpaceForIcon * i);
                        final int width = this.mParent.getWidth();
                        final int mHorizSpaceForIcon = this.mHorizSpaceForIcon;
                        ++i;
                        float x = (float)(width - mHorizSpaceForIcon * i);
                        if (mOnLeft) {
                            x = n;
                        }
                        child.setX(x);
                    }
                    this.mIconsPlaced = true;
                }
            }
        }
    }
    
    @VisibleForTesting
    protected void beginDrag() {
        this.mSnapping = false;
        final ValueAnimator mFadeAnimator = this.mFadeAnimator;
        if (mFadeAnimator != null) {
            mFadeAnimator.cancel();
        }
        this.mHandler.removeCallbacks((Runnable)this.mCheckForDrag);
        this.mCheckForDrag = null;
        this.mIsUserTouching = true;
    }
    
    @Override
    public boolean canBeDismissed() {
        return this.getParent().canViewBeDismissed();
    }
    
    @VisibleForTesting
    protected void cancelDrag() {
        final ValueAnimator mFadeAnimator = this.mFadeAnimator;
        if (mFadeAnimator != null) {
            mFadeAnimator.cancel();
        }
        this.mHandler.removeCallbacks((Runnable)this.mCheckForDrag);
    }
    
    @Override
    public void createMenu(final ViewGroup viewGroup, final StatusBarNotification statusBarNotification) {
        this.mParent = (ExpandableNotificationRow)viewGroup;
        this.createMenuViews(true, statusBarNotification != null && (statusBarNotification.getNotification().flags & 0x40) != 0x0);
    }
    
    @Override
    public MenuItem getAppOpsMenuItem(final Context context) {
        return this.mAppOpsItem;
    }
    
    @VisibleForTesting
    protected float getDismissThreshold() {
        return this.getParent().getWidth() * 0.6f;
    }
    
    @Override
    public MenuItem getLongpressMenuItem(final Context context) {
        return this.mInfoItem;
    }
    
    @VisibleForTesting
    protected float getMaximumSwipeDistance() {
        return this.mHorizSpaceForIcon * 0.2f;
    }
    
    @Override
    public ArrayList<MenuItem> getMenuItems(final Context context) {
        ArrayList<MenuItem> list;
        if (this.mOnLeft) {
            list = this.mLeftMenuItems;
        }
        else {
            list = this.mRightMenuItems;
        }
        return list;
    }
    
    @Override
    public int getMenuSnapTarget() {
        final boolean menuOnLeft = this.isMenuOnLeft();
        int spaceForMenu = this.getSpaceForMenu();
        if (!menuOnLeft) {
            spaceForMenu = -spaceForMenu;
        }
        return spaceForMenu;
    }
    
    @Override
    public View getMenuView() {
        return (View)this.mMenuContainer;
    }
    
    @VisibleForTesting
    protected float getMinimumSwipeDistance() {
        float n;
        if (this.getParent().canViewBeDismissed()) {
            n = 0.25f;
        }
        else {
            n = 0.15f;
        }
        return this.mHorizSpaceForIcon * n;
    }
    
    @VisibleForTesting
    protected ExpandableNotificationRow getParent() {
        return this.mParent;
    }
    
    @Override
    public Point getRevealAnimationOrigin() {
        final View menuView = this.mInfoItem.getMenuView();
        final int n = menuView.getLeft() + menuView.getPaddingLeft() + menuView.getWidth() / 2;
        final int n2 = menuView.getTop() + menuView.getPaddingTop() + menuView.getHeight() / 2;
        if (this.isMenuOnLeft()) {
            return new Point(n, n2);
        }
        return new Point(this.mParent.getRight() - n, n2);
    }
    
    @VisibleForTesting
    protected float getSnapBackThreshold() {
        return this.getSpaceForMenu() - this.getMaximumSwipeDistance();
    }
    
    @Override
    public MenuItem getSnoozeMenuItem(final Context context) {
        return this.mSnoozeItem;
    }
    
    @VisibleForTesting
    protected int getSpaceForMenu() {
        return this.mHorizSpaceForIcon * this.mMenuContainer.getChildCount();
    }
    
    @VisibleForTesting
    protected float getTranslation() {
        return this.mTranslation;
    }
    
    @VisibleForTesting
    protected boolean isDismissing() {
        return this.mDismissing;
    }
    
    @VisibleForTesting
    protected boolean isMenuOnLeft() {
        return this.mOnLeft;
    }
    
    @VisibleForTesting
    protected boolean isMenuSnapped() {
        return this.mMenuSnapped;
    }
    
    @VisibleForTesting
    protected boolean isMenuSnappedOnLeft() {
        return this.mMenuSnappedOnLeft;
    }
    
    @Override
    public boolean isMenuVisible() {
        return this.mAlpha > 0.0f;
    }
    
    @Override
    public boolean isSnappedAndOnSameSide() {
        return this.isMenuSnapped() && this.isMenuVisible() && this.isMenuSnappedOnLeft() == this.isMenuOnLeft();
    }
    
    @VisibleForTesting
    protected boolean isSnapping() {
        return this.mSnapping;
    }
    
    @Override
    public boolean isSwipedEnoughToShowMenu() {
        final float minimumSwipeDistance = this.getMinimumSwipeDistance();
        final float translation = this.getTranslation();
        return this.isMenuVisible() && (this.isMenuOnLeft() ? (translation > minimumSwipeDistance) : (translation < -minimumSwipeDistance));
    }
    
    @Override
    public boolean isTowardsMenu(final float n) {
        return this.isMenuVisible() && ((this.isMenuOnLeft() && n <= 0.0f) || (!this.isMenuOnLeft() && n >= 0.0f));
    }
    
    @VisibleForTesting
    protected boolean isUserTouching() {
        return this.mIsUserTouching;
    }
    
    @Override
    public boolean isWithinSnapMenuThreshold() {
        final float translation = this.getTranslation();
        final float snapBackThreshold = this.getSnapBackThreshold();
        final float dismissThreshold = this.getDismissThreshold();
        final boolean menuOnLeft = this.isMenuOnLeft();
        boolean b = true;
        if (menuOnLeft) {
            if (translation > snapBackThreshold && translation < dismissThreshold) {
                return b;
            }
        }
        else if (translation < -snapBackThreshold && translation > -dismissThreshold) {
            return b;
        }
        b = false;
        return b;
    }
    
    @Override
    public MenuItem menuItemToExposeOnSnap() {
        return null;
    }
    
    public void onClick(final View view) {
        if (this.mMenuListener == null) {
            return;
        }
        view.getLocationOnScreen(this.mIconLocation);
        this.mParent.getLocationOnScreen(this.mParentLocation);
        final int n = this.mHorizSpaceForIcon / 2;
        final int n2 = view.getHeight() / 2;
        final int[] mIconLocation = this.mIconLocation;
        final int n3 = mIconLocation[0];
        final int[] mParentLocation = this.mParentLocation;
        final int n4 = mParentLocation[0];
        final int n5 = mIconLocation[1];
        final int n6 = mParentLocation[1];
        if (this.mMenuItemsByView.containsKey(view)) {
            this.mMenuListener.onMenuClicked((View)this.mParent, n3 - n4 + n, n5 - n6 + n2, this.mMenuItemsByView.get(view));
        }
    }
    
    @Override
    public void onConfigurationChanged() {
        this.mParent.setLayoutListener((ExpandableNotificationRow.LayoutListener)this);
    }
    
    @Override
    public void onDismiss() {
        this.cancelDrag();
        this.mMenuSnapped = false;
        this.mDismissing = true;
    }
    
    public void onLayout() {
        this.mIconsPlaced = false;
        this.setMenuLocation();
        this.mParent.removeListener();
    }
    
    @Override
    public void onNotificationUpdated(final StatusBarNotification statusBarNotification) {
        if (this.mMenuContainer == null) {
            return;
        }
        final boolean menuVisible = this.isMenuVisible();
        boolean b = true;
        if ((statusBarNotification.getNotification().flags & 0x40) == 0x0) {
            b = false;
        }
        this.createMenuViews(menuVisible ^ true, b);
    }
    
    @Override
    public void onParentHeightUpdate() {
        if (this.mParent != null && (!this.mLeftMenuItems.isEmpty() || !this.mRightMenuItems.isEmpty())) {
            if (this.mMenuContainer != null) {
                final int actualHeight = this.mParent.getActualHeight();
                final int mVertSpaceForIcons = this.mVertSpaceForIcons;
                float translationY;
                if (actualHeight < mVertSpaceForIcons) {
                    translationY = (float)(actualHeight / 2 - this.mHorizSpaceForIcon / 2);
                }
                else {
                    translationY = (float)((mVertSpaceForIcons - this.mHorizSpaceForIcon) / 2);
                }
                this.mMenuContainer.setTranslationY(translationY);
            }
        }
    }
    
    @Override
    public void onParentTranslationUpdate(float menuAlpha) {
        this.mTranslation = menuAlpha;
        if (!this.mAnimating) {
            if (this.mMenuFadedIn) {
                final float n = this.mParent.getWidth() * 0.3f;
                final float abs = Math.abs(menuAlpha);
                menuAlpha = 0.0f;
                if (abs != 0.0f) {
                    if (abs <= n) {
                        menuAlpha = 1.0f;
                    }
                    else {
                        menuAlpha = 1.0f - (abs - n) / (this.mParent.getWidth() - n);
                    }
                }
                this.setMenuAlpha(menuAlpha);
            }
        }
    }
    
    @Override
    public void onSnapClosed() {
        this.cancelDrag();
        this.mMenuSnapped = false;
        this.mSnapping = true;
    }
    
    @Override
    public void onSnapOpen() {
        this.mMenuSnapped = true;
        this.mMenuSnappedOnLeft = this.isMenuOnLeft();
        if (this.mAlpha == 0.0f) {
            final ExpandableNotificationRow mParent = this.mParent;
            if (mParent != null) {
                this.fadeInMenu((float)mParent.getWidth());
            }
        }
        final OnMenuEventListener mMenuListener = this.mMenuListener;
        if (mMenuListener != null) {
            mMenuListener.onMenuShown((View)this.getParent());
        }
    }
    
    @Override
    public void onTouchEnd() {
        this.mIsUserTouching = false;
    }
    
    @Override
    public void onTouchMove(final float n) {
        this.mSnapping = false;
        if (!this.isTowardsMenu(n) && this.isMenuLocationChange()) {
            this.mMenuSnapped = false;
            if (!this.mHandler.hasCallbacks((Runnable)this.mCheckForDrag)) {
                this.mCheckForDrag = null;
            }
            else {
                this.setMenuAlpha(0.0f);
                this.setMenuLocation();
            }
        }
        if (this.mShouldShowMenu && !NotificationStackScrollLayout.isPinnedHeadsUp((View)this.getParent()) && !this.mParent.areGutsExposed() && !this.mParent.showingPulsing()) {
            final CheckForDrag mCheckForDrag = this.mCheckForDrag;
            if (mCheckForDrag == null || !this.mHandler.hasCallbacks((Runnable)mCheckForDrag)) {
                final CheckForDrag mCheckForDrag2 = new CheckForDrag();
                this.mCheckForDrag = mCheckForDrag2;
                this.mHandler.postDelayed((Runnable)mCheckForDrag2, 60L);
            }
        }
    }
    
    @Override
    public void onTouchStart() {
        this.beginDrag();
    }
    
    @Override
    public void resetMenu() {
        this.resetState(true);
    }
    
    @Override
    public void setAppName(final String s) {
        if (s == null) {
            return;
        }
        this.setAppName(s, this.mLeftMenuItems);
        this.setAppName(s, this.mRightMenuItems);
    }
    
    @Override
    public void setDismissRtl(final boolean b) {
        if (this.mMenuContainer != null) {
            this.createMenuViews(true, this.mIsForeground);
        }
    }
    
    @VisibleForTesting
    protected void setMenuAlpha(final float mAlpha) {
        this.mAlpha = mAlpha;
        final FrameLayout mMenuContainer = this.mMenuContainer;
        if (mMenuContainer == null) {
            return;
        }
        int i = 0;
        if (mAlpha == 0.0f) {
            this.mMenuFadedIn = false;
            mMenuContainer.setVisibility(4);
        }
        else {
            mMenuContainer.setVisibility(0);
        }
        while (i < this.mMenuContainer.getChildCount()) {
            this.mMenuContainer.getChildAt(i).setAlpha(this.mAlpha);
            ++i;
        }
    }
    
    @Override
    public void setMenuClickListener(final OnMenuEventListener mMenuListener) {
        this.mMenuListener = mMenuListener;
    }
    
    @Override
    public void setMenuItems(final ArrayList<MenuItem> list) {
    }
    
    @Override
    public boolean shouldShowGutsOnSnapOpen() {
        return false;
    }
    
    @Override
    public boolean shouldShowMenu() {
        return this.mShouldShowMenu;
    }
    
    @Override
    public boolean shouldSnapBack() {
        final float translation = this.getTranslation();
        final float snapBackThreshold = this.getSnapBackThreshold();
        final boolean menuOnLeft = this.isMenuOnLeft();
        boolean b = true;
        if (menuOnLeft) {
            if (translation < snapBackThreshold) {
                return b;
            }
        }
        else if (translation > -snapBackThreshold) {
            return b;
        }
        b = false;
        return b;
    }
    
    private final class CheckForDrag implements Runnable
    {
        @Override
        public void run() {
            final float abs = Math.abs(NotificationMenuRow.this.mTranslation);
            final float n = (float)NotificationMenuRow.this.getSpaceForMenu();
            final float n2 = NotificationMenuRow.this.mParent.getWidth() * 0.4f;
            if ((!NotificationMenuRow.this.isMenuVisible() || NotificationMenuRow.this.isMenuLocationChange()) && abs >= n * 0.4 && abs < n2) {
                NotificationMenuRow.this.fadeInMenu(n2);
            }
        }
    }
    
    public static class NotificationMenuItem implements MenuItem
    {
        String mContentDescription;
        NotificationGuts.GutsContent mGutsContent;
        View mMenuView;
        
        public NotificationMenuItem(final Context context, final String mContentDescription, final NotificationGuts.GutsContent mGutsContent, final int n) {
            final Resources resources = context.getResources();
            final int dimensionPixelSize = resources.getDimensionPixelSize(R$dimen.notification_menu_icon_padding);
            final int color = resources.getColor(R$color.notification_gear_color);
            if (n >= 0) {
                final AlphaOptimizedImageView mMenuView = new AlphaOptimizedImageView(context);
                mMenuView.setPadding(dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize);
                mMenuView.setImageDrawable(context.getResources().getDrawable(n));
                mMenuView.setColorFilter(color);
                mMenuView.setAlpha(1.0f);
                this.mMenuView = (View)mMenuView;
            }
            this.mContentDescription = mContentDescription;
            this.mGutsContent = mGutsContent;
        }
        
        @Override
        public String getContentDescription() {
            return this.mContentDescription;
        }
        
        @Override
        public View getGutsView() {
            return this.mGutsContent.getContentView();
        }
        
        @Override
        public View getMenuView() {
            return this.mMenuView;
        }
    }
}
