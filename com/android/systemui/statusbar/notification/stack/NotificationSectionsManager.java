// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.stack;

import java.util.Iterator;
import java.util.ArrayList;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import java.util.List;
import com.android.systemui.R$string;
import com.android.systemui.R$layout;
import com.android.internal.annotations.VisibleForTesting;
import java.util.Objects;
import android.content.Intent;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import android.view.ViewGroup;
import android.view.View;
import com.android.systemui.statusbar.notification.row.StackScrollerDecorView;
import android.view.LayoutInflater;
import com.android.systemui.statusbar.notification.people.PeopleHubViewAdapter;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.NotificationSectionsFeatureManager;
import com.android.systemui.statusbar.notification.people.Subscription;
import android.view.View$OnClickListener;
import com.android.keyguard.KeyguardMediaPlayer;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.plugins.ActivityStarter;

public class NotificationSectionsManager implements SectionProvider
{
    private final ActivityStarter mActivityStarter;
    private SectionHeaderView mAlertingHeader;
    private final ConfigurationController mConfigurationController;
    private final ConfigurationController.ConfigurationListener mConfigurationListener;
    private SectionHeaderView mGentleHeader;
    private boolean mInitialized;
    private final KeyguardMediaPlayer mKeyguardMediaPlayer;
    private MediaHeaderView mMediaControlsView;
    private final int mNumberOfSections;
    private View$OnClickListener mOnClearGentleNotifsClickListener;
    private NotificationStackScrollLayout mParent;
    private Subscription mPeopleHubSubscription;
    private PeopleHubView mPeopleHubView;
    private boolean mPeopleHubVisible;
    private final NotificationSectionsFeatureManager mSectionsFeatureManager;
    private final StatusBarStateController mStatusBarStateController;
    
    NotificationSectionsManager(final ActivityStarter mActivityStarter, final StatusBarStateController mStatusBarStateController, final ConfigurationController mConfigurationController, final PeopleHubViewAdapter peopleHubViewAdapter, final KeyguardMediaPlayer mKeyguardMediaPlayer, final NotificationSectionsFeatureManager mSectionsFeatureManager) {
        this.mInitialized = false;
        this.mPeopleHubVisible = false;
        this.mConfigurationListener = new ConfigurationController.ConfigurationListener() {
            @Override
            public void onLocaleListChanged() {
                final NotificationSectionsManager this$0 = NotificationSectionsManager.this;
                this$0.reinflateViews(LayoutInflater.from(this$0.mParent.getContext()));
            }
        };
        this.mActivityStarter = mActivityStarter;
        this.mStatusBarStateController = mStatusBarStateController;
        this.mConfigurationController = mConfigurationController;
        this.mKeyguardMediaPlayer = mKeyguardMediaPlayer;
        this.mSectionsFeatureManager = mSectionsFeatureManager;
        this.mNumberOfSections = mSectionsFeatureManager.getNumberOfBuckets();
    }
    
    private void adjustHeaderVisibilityAndPosition(final int n, final StackScrollerDecorView stackScrollerDecorView, final int n2) {
        if (n == -1) {
            if (n2 != -1) {
                this.mParent.removeView((View)stackScrollerDecorView);
            }
        }
        else if (n2 == -1) {
            if (stackScrollerDecorView.getTransientContainer() != null) {
                stackScrollerDecorView.getTransientContainer().removeTransientView((View)stackScrollerDecorView);
                stackScrollerDecorView.setTransientContainer(null);
            }
            stackScrollerDecorView.setContentVisible(true);
            this.mParent.addView((View)stackScrollerDecorView, n);
        }
        else {
            this.mParent.changeViewPosition(stackScrollerDecorView, n);
        }
    }
    
    private void adjustViewPosition(final int n, final ExpandableView expandableView, final int n2) {
        if (n == -1) {
            if (n2 != -1) {
                this.mParent.removeView((View)expandableView);
            }
        }
        else if (n2 == -1) {
            if (expandableView.getTransientContainer() != null) {
                expandableView.getTransientContainer().removeTransientView((View)expandableView);
                expandableView.setTransientContainer(null);
            }
            this.mParent.addView((View)expandableView, n);
        }
        else {
            this.mParent.changeViewPosition(expandableView, n);
        }
    }
    
    private Integer getBucket(final View view) {
        if (view == this.mGentleHeader) {
            return 4;
        }
        if (view == this.mMediaControlsView) {
            return 1;
        }
        if (view == this.mPeopleHubView) {
            return 2;
        }
        if (view == this.mAlertingHeader) {
            return 3;
        }
        if (view instanceof ExpandableNotificationRow) {
            return ((ExpandableNotificationRow)view).getEntry().getBucket();
        }
        return null;
    }
    
    private boolean isUsingMultipleSections() {
        final int mNumberOfSections = this.mNumberOfSections;
        boolean b = true;
        if (mNumberOfSections <= 1) {
            b = false;
        }
        return b;
    }
    
    private void onClearGentleNotifsClick(final View view) {
        final View$OnClickListener mOnClearGentleNotifsClickListener = this.mOnClearGentleNotifsClickListener;
        if (mOnClearGentleNotifsClickListener != null) {
            mOnClearGentleNotifsClickListener.onClick(view);
        }
    }
    
    private void onGentleHeaderClick(final View view) {
        this.mActivityStarter.startActivity(new Intent("android.settings.NOTIFICATION_SETTINGS"), true, true, 536870912);
    }
    
    private <T extends ExpandableView> T reinflateView(final T t, final LayoutInflater layoutInflater, final int n) {
        int indexOfChild = 0;
        Label_0056: {
            if (t != null) {
                if (t.getTransientContainer() != null) {
                    t.getTransientContainer().removeView((View)this.mGentleHeader);
                }
                else if (t.getParent() != null) {
                    indexOfChild = this.mParent.indexOfChild((View)t);
                    this.mParent.removeView((View)t);
                    break Label_0056;
                }
            }
            indexOfChild = -1;
        }
        final ExpandableView expandableView = (ExpandableView)layoutInflater.inflate(n, (ViewGroup)this.mParent, false);
        if (indexOfChild != -1) {
            this.mParent.addView((View)expandableView, indexOfChild);
        }
        return (T)expandableView;
    }
    
    @Override
    public boolean beginsSection(final View view, final View view2) {
        return view == this.mGentleHeader || view == this.mMediaControlsView || view == this.mPeopleHubView || view == this.mAlertingHeader || !Objects.equals(this.getBucket(view), this.getBucket(view2));
    }
    
    NotificationSection[] createSectionsForBuckets() {
        final int[] notificationBuckets = this.mSectionsFeatureManager.getNotificationBuckets();
        final NotificationSection[] array = new NotificationSection[notificationBuckets.length];
        for (int i = 0; i < notificationBuckets.length; ++i) {
            array[i] = new NotificationSection((View)this.mParent, notificationBuckets[i]);
        }
        return array;
    }
    
    @VisibleForTesting
    ExpandableView getAlertingHeaderView() {
        return this.mAlertingHeader;
    }
    
    @VisibleForTesting
    ExpandableView getGentleHeaderView() {
        return this.mGentleHeader;
    }
    
    @VisibleForTesting
    ExpandableView getMediaControlsView() {
        return this.mMediaControlsView;
    }
    
    @VisibleForTesting
    ExpandableView getPeopleHeaderView() {
        return this.mPeopleHubView;
    }
    
    void hidePeopleRow() {
        this.mPeopleHubVisible = false;
        this.updateSectionBoundaries();
    }
    
    void initialize(final NotificationStackScrollLayout mParent, final LayoutInflater layoutInflater) {
        if (!this.mInitialized) {
            this.mInitialized = true;
            this.mParent = mParent;
            this.reinflateViews(layoutInflater);
            this.mConfigurationController.addCallback(this.mConfigurationListener);
            return;
        }
        throw new IllegalStateException("NotificationSectionsManager already initialized");
    }
    
    void reinflateViews(final LayoutInflater layoutInflater) {
        (this.mGentleHeader = this.reinflateView(this.mGentleHeader, layoutInflater, R$layout.status_bar_notification_section_header)).setHeaderText(R$string.notification_section_header_gentle);
        this.mGentleHeader.setOnHeaderClickListener((View$OnClickListener)new _$$Lambda$NotificationSectionsManager$Lm4LNd4tUWZPNzSmZnkDovE_xCU(this));
        this.mGentleHeader.setOnClearAllClickListener((View$OnClickListener)new _$$Lambda$NotificationSectionsManager$BXFcLGpgdZnd7PRimoedNDlJa8o(this));
        (this.mAlertingHeader = this.reinflateView(this.mAlertingHeader, layoutInflater, R$layout.status_bar_notification_section_header)).setHeaderText(R$string.notification_section_header_alerting);
        this.mAlertingHeader.setOnHeaderClickListener((View$OnClickListener)new _$$Lambda$NotificationSectionsManager$Lm4LNd4tUWZPNzSmZnkDovE_xCU(this));
        final Subscription mPeopleHubSubscription = this.mPeopleHubSubscription;
        if (mPeopleHubSubscription != null) {
            mPeopleHubSubscription.unsubscribe();
        }
        this.mPeopleHubView = this.reinflateView(this.mPeopleHubView, layoutInflater, R$layout.people_strip);
        if (this.mMediaControlsView != null) {
            this.mKeyguardMediaPlayer.unbindView();
        }
        final MediaHeaderView mMediaControlsView = this.reinflateView(this.mMediaControlsView, layoutInflater, R$layout.keyguard_media_header);
        this.mMediaControlsView = mMediaControlsView;
        this.mKeyguardMediaPlayer.bindView((View)mMediaControlsView);
    }
    
    void setHeaderForegroundColor(final int foregroundColor) {
        this.mPeopleHubView.setTextColor(foregroundColor);
        this.mGentleHeader.setForegroundColor(foregroundColor);
        this.mAlertingHeader.setForegroundColor(foregroundColor);
    }
    
    void setOnClearGentleNotifsClickListener(final View$OnClickListener mOnClearGentleNotifsClickListener) {
        this.mOnClearGentleNotifsClickListener = mOnClearGentleNotifsClickListener;
    }
    
    @VisibleForTesting
    void setPeopleHubVisible(final boolean mPeopleHubVisible) {
        this.mPeopleHubVisible = mPeopleHubVisible;
    }
    
    boolean updateFirstAndLastViewsForAllSections(final NotificationSection[] array, final List<ActivatableNotificationView> list) {
        if (array.length > 0 && list.size() > 0) {
            final ArrayList<View> list2 = new ArrayList<View>();
            boolean b;
            for (int length = array.length, i = (b = false) ? 1 : 0; i < length; ++i) {
                final NotificationSection notificationSection = array[i];
                final int bucket = notificationSection.getBucket();
                list2.clear();
                for (final ActivatableNotificationView e : list) {
                    final Integer bucket2 = this.getBucket((View)e);
                    if (bucket2 == null) {
                        throw new IllegalArgumentException("Cannot find section bucket for view");
                    }
                    if (bucket2 == bucket) {
                        list2.add((View)e);
                    }
                    boolean b2;
                    boolean b3;
                    if (list2.size() >= 1) {
                        b2 = (b | notificationSection.setFirstVisibleChild((ActivatableNotificationView)list2.get(0)));
                        b3 = notificationSection.setLastVisibleChild((ActivatableNotificationView)list2.get(list2.size() - 1));
                    }
                    else {
                        b2 = (b | notificationSection.setFirstVisibleChild(null));
                        b3 = notificationSection.setLastVisibleChild(null);
                    }
                    b = (b2 | b3);
                }
            }
            return b;
        }
        for (final NotificationSection notificationSection2 : array) {
            notificationSection2.setFirstVisibleChild(null);
            notificationSection2.setLastVisibleChild(null);
        }
        return false;
    }
    
    void updateSectionBoundaries() {
        if (!this.isUsingMultipleSections()) {
            return;
        }
        final boolean b = this.mStatusBarStateController.getState() != 1;
        final boolean filteringEnabled = this.mSectionsFeatureManager.isFilteringEnabled();
        final boolean b2 = this.mStatusBarStateController.getState() == 1;
        final boolean mediaControlsEnabled = this.mSectionsFeatureManager.isMediaControlsEnabled();
        int n;
        if (b2 && mediaControlsEnabled) {
            n = 0;
        }
        else {
            n = -1;
        }
        final int childCount = this.mParent.getChildCount();
        int n2 = -1;
        int n3 = -1;
        int i = 0;
        int n4 = -1;
        int n5 = -1;
        int n6 = -1;
        int n7 = 0;
        int n8 = -1;
        boolean b3 = false;
        int n9 = -1;
        while (i < childCount) {
            final View child = this.mParent.getChildAt(i);
            Label_0554: {
                if (child == this.mMediaControlsView) {
                    n9 = i;
                }
                else if (child == this.mPeopleHubView) {
                    n8 = i;
                }
                else if (child == this.mAlertingHeader) {
                    n2 = i;
                }
                else if (child == this.mGentleHeader) {
                    n3 = i;
                }
                else if (child instanceof ExpandableNotificationRow) {
                    final int bucket = ((ExpandableNotificationRow)child).getEntry().getBucket();
                    int n14;
                    int n15;
                    int n16;
                    if (bucket != 0) {
                        if (bucket == 2) {
                            int n10 = n4;
                            if (b && (n10 = n4) == -1) {
                                int n11;
                                if (n8 != -1) {
                                    n11 = i - 1;
                                }
                                else {
                                    n11 = i;
                                }
                                int n12 = n11;
                                if (n2 != -1) {
                                    n12 = n11 - 1;
                                }
                                n10 = n12;
                                if (n3 != -1) {
                                    n10 = n12 - 1;
                                }
                            }
                            final int n13 = i;
                            b3 = true;
                            n4 = n10;
                            n7 = n13;
                            break Label_0554;
                        }
                        if (bucket != 3) {
                            if (bucket != 4) {
                                throw new IllegalStateException("Cannot find section bucket for view");
                            }
                            n14 = n;
                            n15 = n5;
                            n16 = n6;
                            if (b) {
                                n14 = n;
                                n15 = n5;
                                if ((n16 = n6) == -1) {
                                    if (n3 == -1) {
                                        n6 = (n7 = i);
                                        break Label_0554;
                                    }
                                    n16 = i - 1;
                                    n14 = n;
                                    n15 = n5;
                                }
                            }
                        }
                        else {
                            n14 = n;
                            n15 = n5;
                            n16 = n6;
                            if (b) {
                                n14 = n;
                                n15 = n5;
                                n16 = n6;
                                if (filteringEnabled) {
                                    n14 = n;
                                    n15 = n5;
                                    n16 = n6;
                                    if (n5 == -1) {
                                        int n17;
                                        if (n2 != -1) {
                                            n17 = i - 1;
                                        }
                                        else {
                                            n17 = i;
                                        }
                                        n14 = n;
                                        n15 = n17;
                                        n16 = n6;
                                        if (n3 != -1) {
                                            n15 = n17 - 1;
                                            n14 = n;
                                            n16 = n6;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else {
                        n14 = n;
                        n15 = n5;
                        n16 = n6;
                        if (n != -1) {
                            n14 = n + 1;
                            n16 = n6;
                            n15 = n5;
                        }
                    }
                    n7 = i;
                    n6 = n16;
                    n5 = n15;
                    n = n14;
                }
            }
            ++i;
        }
        int n18 = n4;
        if (b) {
            n18 = n4;
            if (filteringEnabled) {
                n18 = n4;
                if (this.mPeopleHubVisible && (n18 = n4) == -1) {
                    int n19;
                    if (n5 != -1) {
                        n19 = n5;
                    }
                    else if (n6 != -1) {
                        n19 = n6;
                    }
                    else {
                        n19 = n7;
                    }
                    n18 = n19;
                    if (n8 != -1 && n8 < (n18 = n19)) {
                        n18 = n19 - 1;
                    }
                }
            }
        }
        this.adjustHeaderVisibilityAndPosition(n6, this.mGentleHeader, n3);
        this.adjustHeaderVisibilityAndPosition(n5, this.mAlertingHeader, n2);
        this.adjustHeaderVisibilityAndPosition(n18, this.mPeopleHubView, n8);
        this.adjustViewPosition(n, this.mMediaControlsView, n9);
        this.mGentleHeader.setAreThereDismissableGentleNotifs(this.mParent.hasActiveClearableNotifications(2));
        this.mPeopleHubView.setCanSwipe(b && this.mPeopleHubVisible && !b3);
        if (n18 != n8) {
            this.mPeopleHubView.resetTranslation();
        }
    }
}
