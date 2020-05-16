// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.stack;

import java.util.ArrayList;
import java.util.HashMap;
import android.util.Log;
import com.android.systemui.statusbar.EmptyShadeView;
import com.android.systemui.statusbar.notification.row.FooterView;
import com.android.systemui.statusbar.NotificationShelf;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import android.content.res.Resources;
import com.android.systemui.R$bool;
import com.android.systemui.R$dimen;
import java.util.Iterator;
import java.util.List;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import android.view.View;
import android.content.Context;
import android.view.ViewGroup;

public class StackScrollAlgorithm
{
    private boolean mClipNotificationScrollToTop;
    private int mCollapsedSize;
    private int mGapHeight;
    private float mHeadsUpInset;
    private final ViewGroup mHostView;
    private int mIncreasedPaddingBetweenElements;
    private boolean mIsExpanded;
    private int mPaddingBetweenElements;
    private int mPinnedZTranslationExtra;
    private int mStatusBarHeight;
    private StackScrollAlgorithmState mTempAlgorithmState;
    
    public StackScrollAlgorithm(final Context context, final ViewGroup mHostView) {
        this.mTempAlgorithmState = new StackScrollAlgorithmState();
        this.mHostView = mHostView;
        this.initView(context);
    }
    
    private boolean childNeedsGapHeight(final SectionProvider sectionProvider, final StackScrollAlgorithmState stackScrollAlgorithmState, final int n, final View view, final View view2) {
        return sectionProvider.beginsSection(view, view2) && n > 0;
    }
    
    private void clampHunToMaxTranslation(final AmbientState ambientState, final ExpandableNotificationRow expandableNotificationRow, final ExpandableViewState expandableViewState) {
        final float min = Math.min(ambientState.getMaxHeadsUpTranslation(), ambientState.getInnerHeight() + ambientState.getTopPadding() + ambientState.getStackTranslation());
        final float min2 = Math.min(expandableViewState.yTranslation, min - expandableNotificationRow.getCollapsedHeight());
        expandableViewState.height = (int)Math.min((float)expandableViewState.height, min - min2);
        expandableViewState.yTranslation = min2;
    }
    
    private void clampHunToTop(final AmbientState ambientState, final ExpandableNotificationRow expandableNotificationRow, final ExpandableViewState expandableViewState) {
        final float max = Math.max(ambientState.getTopPadding() + ambientState.getStackTranslation(), expandableViewState.yTranslation);
        expandableViewState.height = (int)Math.max(expandableViewState.height - (max - expandableViewState.yTranslation), (float)expandableNotificationRow.getCollapsedHeight());
        expandableViewState.yTranslation = max;
    }
    
    private void clampPositionToShelf(final ExpandableView expandableView, final ExpandableViewState expandableViewState, final AmbientState ambientState) {
        if (ambientState.getShelf() == null) {
            return;
        }
        final int n = ambientState.getInnerHeight() - ambientState.getShelf().getIntrinsicHeight();
        if (ambientState.isAppearing() && !expandableView.isAboveShelf()) {
            expandableViewState.yTranslation = Math.max(expandableViewState.yTranslation, (float)n);
        }
        final float yTranslation = expandableViewState.yTranslation;
        final float b = (float)n;
        final float min = Math.min(yTranslation, b);
        expandableViewState.yTranslation = min;
        if (min >= b) {
            expandableViewState.hidden = (!expandableView.isExpandAnimationRunning() && !expandableView.hasExpandingChild());
            expandableViewState.inShelf = true;
            expandableViewState.headsUpIsVisible = false;
        }
    }
    
    private void getNotificationChildrenStates(final StackScrollAlgorithmState stackScrollAlgorithmState, final AmbientState ambientState) {
        for (int size = stackScrollAlgorithmState.visibleChildren.size(), i = 0; i < size; ++i) {
            final ExpandableView expandableView = stackScrollAlgorithmState.visibleChildren.get(i);
            if (expandableView instanceof ExpandableNotificationRow) {
                ((ExpandableNotificationRow)expandableView).updateChildrenStates(ambientState);
            }
        }
    }
    
    private float getPaddingForValue(final Float n) {
        if (n == null) {
            return (float)this.mPaddingBetweenElements;
        }
        if (n >= 0.0f) {
            return NotificationUtils.interpolate((float)this.mPaddingBetweenElements, (float)this.mIncreasedPaddingBetweenElements, n);
        }
        return NotificationUtils.interpolate(0.0f, (float)this.mPaddingBetweenElements, n + 1.0f);
    }
    
    private void initAlgorithmState(final ViewGroup viewGroup, final StackScrollAlgorithmState stackScrollAlgorithmState, final AmbientState ambientState) {
        int i = 0;
        stackScrollAlgorithmState.scrollY = (int)(Math.max(0, ambientState.getScrollY()) + ambientState.getOverScrollAmount(false));
        final int childCount = viewGroup.getChildCount();
        stackScrollAlgorithmState.visibleChildren.clear();
        stackScrollAlgorithmState.visibleChildren.ensureCapacity(childCount);
        stackScrollAlgorithmState.paddingMap.clear();
        int n;
        if (ambientState.isDozing()) {
            if (ambientState.hasPulsingNotifications()) {
                n = 1;
            }
            else {
                n = 0;
            }
        }
        else {
            n = childCount;
        }
        int n2 = 0;
        ExpandableView expandableView = null;
        while (i < childCount) {
            final ExpandableView key = (ExpandableView)viewGroup.getChildAt(i);
            int n3 = n2;
            ExpandableView expandableView2 = expandableView;
            if (key.getVisibility() != 8) {
                if (key == ambientState.getShelf()) {
                    n3 = n2;
                    expandableView2 = expandableView;
                }
                else {
                    if (i >= n) {
                        expandableView = null;
                    }
                    int updateNotGoneIndex = this.updateNotGoneIndex(stackScrollAlgorithmState, n2, key);
                    final float increasedPaddingAmount = key.getIncreasedPaddingAmount();
                    final float n4 = fcmpl(increasedPaddingAmount, 0.0f);
                    if (n4 != 0) {
                        stackScrollAlgorithmState.paddingMap.put(key, increasedPaddingAmount);
                        if (expandableView != null) {
                            final Float n5 = stackScrollAlgorithmState.paddingMap.get(expandableView);
                            float f;
                            final float n6 = f = this.getPaddingForValue(increasedPaddingAmount);
                            if (n5 != null) {
                                final float paddingForValue = this.getPaddingForValue(n5);
                                if (n4 > 0) {
                                    f = NotificationUtils.interpolate(paddingForValue, n6, increasedPaddingAmount);
                                }
                                else {
                                    f = n6;
                                    if (n5 > 0.0f) {
                                        f = NotificationUtils.interpolate(n6, paddingForValue, n5);
                                    }
                                }
                            }
                            stackScrollAlgorithmState.paddingMap.put(expandableView, f);
                        }
                    }
                    else if (expandableView != null) {
                        stackScrollAlgorithmState.paddingMap.put(expandableView, this.getPaddingForValue(stackScrollAlgorithmState.paddingMap.get(expandableView)));
                    }
                    n3 = updateNotGoneIndex;
                    if (key instanceof ExpandableNotificationRow) {
                        final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)key;
                        final List<ExpandableNotificationRow> notificationChildren = expandableNotificationRow.getNotificationChildren();
                        n3 = updateNotGoneIndex;
                        if (expandableNotificationRow.isSummaryWithChildren()) {
                            n3 = updateNotGoneIndex;
                            if (notificationChildren != null) {
                                final Iterator<ExpandableNotificationRow> iterator = notificationChildren.iterator();
                                while (true) {
                                    n3 = updateNotGoneIndex;
                                    if (!iterator.hasNext()) {
                                        break;
                                    }
                                    final ExpandableNotificationRow expandableNotificationRow2 = iterator.next();
                                    if (expandableNotificationRow2.getVisibility() == 8) {
                                        continue;
                                    }
                                    expandableNotificationRow2.getViewState().notGoneIndex = updateNotGoneIndex;
                                    ++updateNotGoneIndex;
                                }
                            }
                        }
                    }
                    expandableView2 = key;
                }
            }
            ++i;
            n2 = n3;
            expandableView = expandableView2;
        }
        final ExpandableNotificationRow expandingNotification = ambientState.getExpandingNotification();
        int n7;
        if (expandingNotification != null) {
            if (expandingNotification.isChildInGroup()) {
                n7 = stackScrollAlgorithmState.visibleChildren.indexOf(expandingNotification.getNotificationParent());
            }
            else {
                n7 = stackScrollAlgorithmState.visibleChildren.indexOf(expandingNotification);
            }
        }
        else {
            n7 = -1;
        }
        stackScrollAlgorithmState.indexOfExpandingNotification = n7;
    }
    
    private void initConstants(final Context context) {
        final Resources resources = context.getResources();
        this.mPaddingBetweenElements = resources.getDimensionPixelSize(R$dimen.notification_divider_height);
        this.mIncreasedPaddingBetweenElements = resources.getDimensionPixelSize(R$dimen.notification_divider_height_increased);
        this.mCollapsedSize = resources.getDimensionPixelSize(R$dimen.notification_min_height);
        this.mStatusBarHeight = resources.getDimensionPixelSize(R$dimen.status_bar_height);
        this.mClipNotificationScrollToTop = resources.getBoolean(R$bool.config_clipNotificationScrollToTop);
        this.mHeadsUpInset = (float)(this.mStatusBarHeight + resources.getDimensionPixelSize(R$dimen.heads_up_status_bar_padding));
        this.mPinnedZTranslationExtra = resources.getDimensionPixelSize(R$dimen.heads_up_pinned_elevation);
        this.mGapHeight = resources.getDimensionPixelSize(R$dimen.notification_section_divider_height);
    }
    
    private void resetChildViewStates() {
        for (int childCount = this.mHostView.getChildCount(), i = 0; i < childCount; ++i) {
            ((ExpandableView)this.mHostView.getChildAt(i)).resetViewState();
        }
    }
    
    private void updateClipping(final StackScrollAlgorithmState stackScrollAlgorithmState, final AmbientState ambientState) {
        final boolean onKeyguard = ambientState.isOnKeyguard();
        float max = 0.0f;
        float a;
        if (!onKeyguard) {
            a = ambientState.getTopPadding() + ambientState.getStackTranslation() + ambientState.getExpandAnimationTopChange();
        }
        else {
            a = 0.0f;
        }
        final int size = stackScrollAlgorithmState.visibleChildren.size();
        int n = 1;
        for (int i = 0; i < size; ++i) {
            final ExpandableView expandableView = stackScrollAlgorithmState.visibleChildren.get(i);
            final ExpandableViewState viewState = expandableView.getViewState();
            float max2 = 0.0f;
            Label_0104: {
                if (expandableView.mustStayOnScreen()) {
                    max2 = max;
                    if (!viewState.headsUpIsVisible) {
                        break Label_0104;
                    }
                }
                max2 = Math.max(a, max);
            }
            final float yTranslation = viewState.yTranslation;
            final float n2 = (float)viewState.height;
            final boolean b = expandableView instanceof ExpandableNotificationRow && ((ExpandableNotificationRow)expandableView).isPinned();
            if (this.mClipNotificationScrollToTop && (!viewState.inShelf || (b && n == 0)) && yTranslation < max2) {
                viewState.clipTopAmount = (int)(max2 - yTranslation);
            }
            else {
                viewState.clipTopAmount = 0;
            }
            if (b) {
                n = 0;
            }
            max = max2;
            if (!expandableView.isTransparent()) {
                float b2;
                if (b) {
                    b2 = yTranslation;
                }
                else {
                    b2 = n2 + yTranslation;
                }
                max = Math.max(max2, b2);
            }
        }
    }
    
    private void updateDimmedActivatedHideSensitive(final AmbientState ambientState, final StackScrollAlgorithmState stackScrollAlgorithmState) {
        final boolean dimmed = ambientState.isDimmed();
        final boolean hideSensitive = ambientState.isHideSensitive();
        final ActivatableNotificationView activatedChild = ambientState.getActivatedChild();
        for (int size = stackScrollAlgorithmState.visibleChildren.size(), i = 0; i < size; ++i) {
            final ExpandableView expandableView = stackScrollAlgorithmState.visibleChildren.get(i);
            final ExpandableViewState viewState = expandableView.getViewState();
            viewState.dimmed = dimmed;
            viewState.hideSensitive = hideSensitive;
            final boolean b = activatedChild == expandableView;
            if (dimmed && b) {
                viewState.zTranslation += ambientState.getZDistanceBetweenElements() * 2.0f;
            }
        }
    }
    
    private void updateHeadsUpStates(final StackScrollAlgorithmState stackScrollAlgorithmState, final AmbientState ambientState) {
        final int size = stackScrollAlgorithmState.visibleChildren.size();
        ExpandableNotificationRow expandableNotificationRow = null;
        for (int i = 0; i < size; ++i) {
            final View view = (View)stackScrollAlgorithmState.visibleChildren.get(i);
            if (view instanceof ExpandableNotificationRow) {
                final ExpandableNotificationRow expandableNotificationRow2 = (ExpandableNotificationRow)view;
                if (expandableNotificationRow2.isHeadsUp()) {
                    final ExpandableViewState viewState = expandableNotificationRow2.getViewState();
                    boolean b = true;
                    ExpandableNotificationRow expandableNotificationRow3;
                    if ((expandableNotificationRow3 = expandableNotificationRow) == null) {
                        expandableNotificationRow3 = expandableNotificationRow;
                        if (expandableNotificationRow2.mustStayOnScreen()) {
                            expandableNotificationRow3 = expandableNotificationRow;
                            if (!viewState.headsUpIsVisible) {
                                viewState.location = 1;
                                expandableNotificationRow3 = expandableNotificationRow2;
                            }
                        }
                    }
                    if (expandableNotificationRow3 != expandableNotificationRow2) {
                        b = false;
                    }
                    final float yTranslation = viewState.yTranslation;
                    final float n = (float)viewState.height;
                    if (this.mIsExpanded && expandableNotificationRow2.mustStayOnScreen() && !viewState.headsUpIsVisible && !expandableNotificationRow2.showingPulsing()) {
                        this.clampHunToTop(ambientState, expandableNotificationRow2, viewState);
                        if (i == 0 && expandableNotificationRow2.isAboveShelf()) {
                            this.clampHunToMaxTranslation(ambientState, expandableNotificationRow2, viewState);
                            viewState.hidden = false;
                        }
                    }
                    if (expandableNotificationRow2.isPinned()) {
                        viewState.yTranslation = Math.max(viewState.yTranslation, this.mHeadsUpInset);
                        viewState.height = Math.max(expandableNotificationRow2.getIntrinsicHeight(), viewState.height);
                        viewState.hidden = false;
                        ExpandableViewState viewState2;
                        if (expandableNotificationRow3 == null) {
                            viewState2 = null;
                        }
                        else {
                            viewState2 = expandableNotificationRow3.getViewState();
                        }
                        if (viewState2 != null && !b && (!this.mIsExpanded || yTranslation + n > viewState2.yTranslation + viewState2.height)) {
                            final int intrinsicHeight = expandableNotificationRow2.getIntrinsicHeight();
                            viewState.height = intrinsicHeight;
                            viewState.yTranslation = Math.min(viewState2.yTranslation + viewState2.height - intrinsicHeight, viewState.yTranslation);
                        }
                        if (!this.mIsExpanded && b && ambientState.getScrollY() > 0) {
                            viewState.yTranslation -= ambientState.getScrollY();
                        }
                    }
                    expandableNotificationRow = expandableNotificationRow3;
                    if (expandableNotificationRow2.isHeadsUpAnimatingAway()) {
                        viewState.hidden = false;
                        expandableNotificationRow = expandableNotificationRow3;
                    }
                }
            }
        }
    }
    
    private int updateNotGoneIndex(final StackScrollAlgorithmState stackScrollAlgorithmState, final int notGoneIndex, final ExpandableView e) {
        e.getViewState().notGoneIndex = notGoneIndex;
        stackScrollAlgorithmState.visibleChildren.add(e);
        return notGoneIndex + 1;
    }
    
    private void updatePositionsForState(final StackScrollAlgorithmState stackScrollAlgorithmState, final AmbientState ambientState) {
        float updateChild = (float)(-stackScrollAlgorithmState.scrollY);
        for (int size = stackScrollAlgorithmState.visibleChildren.size(), i = 0; i < size; ++i) {
            updateChild = this.updateChild(i, stackScrollAlgorithmState, ambientState, updateChild, false);
        }
    }
    
    private void updatePulsingStates(final StackScrollAlgorithmState stackScrollAlgorithmState, final AmbientState ambientState) {
        for (int size = stackScrollAlgorithmState.visibleChildren.size(), i = 0; i < size; ++i) {
            final View view = (View)stackScrollAlgorithmState.visibleChildren.get(i);
            if (view instanceof ExpandableNotificationRow) {
                final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow)view;
                if (expandableNotificationRow.showingPulsing()) {
                    if (i != 0 || !ambientState.isPulseExpanding()) {
                        expandableNotificationRow.getViewState().hidden = false;
                    }
                }
            }
        }
    }
    
    private void updateShelfState(final AmbientState ambientState) {
        final NotificationShelf shelf = ambientState.getShelf();
        if (shelf != null) {
            shelf.updateState(ambientState);
        }
    }
    
    private void updateSpeedBumpState(final StackScrollAlgorithmState stackScrollAlgorithmState, final AmbientState ambientState) {
        final int size = stackScrollAlgorithmState.visibleChildren.size();
        final int speedBumpIndex = ambientState.getSpeedBumpIndex();
        for (int i = 0; i < size; ++i) {
            stackScrollAlgorithmState.visibleChildren.get(i).getViewState().belowSpeedBump = (i >= speedBumpIndex);
        }
    }
    
    private void updateZValuesForState(final StackScrollAlgorithmState stackScrollAlgorithmState, final AmbientState ambientState) {
        int i = stackScrollAlgorithmState.visibleChildren.size() - 1;
        float updateChildZValue = 0.0f;
        while (i >= 0) {
            updateChildZValue = this.updateChildZValue(i, updateChildZValue, stackScrollAlgorithmState, ambientState);
            --i;
        }
    }
    
    protected int getMaxAllowedChildHeight(final View view) {
        if (view instanceof ExpandableView) {
            return ((ExpandableView)view).getIntrinsicHeight();
        }
        int n;
        if (view == null) {
            n = this.mCollapsedSize;
        }
        else {
            n = view.getHeight();
        }
        return n;
    }
    
    protected int getPaddingAfterChild(final StackScrollAlgorithmState stackScrollAlgorithmState, final ExpandableView expandableView) {
        return stackScrollAlgorithmState.getPaddingAfterChild(expandableView);
    }
    
    public void initView(final Context context) {
        this.initConstants(context);
    }
    
    public void resetViewStates(final AmbientState ambientState) {
        final StackScrollAlgorithmState mTempAlgorithmState = this.mTempAlgorithmState;
        this.resetChildViewStates();
        this.initAlgorithmState(this.mHostView, mTempAlgorithmState, ambientState);
        this.updatePositionsForState(mTempAlgorithmState, ambientState);
        this.updateZValuesForState(mTempAlgorithmState, ambientState);
        this.updateHeadsUpStates(mTempAlgorithmState, ambientState);
        this.updatePulsingStates(mTempAlgorithmState, ambientState);
        this.updateDimmedActivatedHideSensitive(ambientState, mTempAlgorithmState);
        this.updateClipping(mTempAlgorithmState, ambientState);
        this.updateSpeedBumpState(mTempAlgorithmState, ambientState);
        this.updateShelfState(ambientState);
        this.getNotificationChildrenStates(mTempAlgorithmState, ambientState);
    }
    
    public void setIsExpanded(final boolean mIsExpanded) {
        this.mIsExpanded = mIsExpanded;
    }
    
    protected float updateChild(final int n, final StackScrollAlgorithmState stackScrollAlgorithmState, final AmbientState ambientState, float yTranslation, final boolean b) {
        final ExpandableView expandableView = stackScrollAlgorithmState.visibleChildren.get(n);
        Object o;
        if (n > 0) {
            o = stackScrollAlgorithmState.visibleChildren.get(n - 1);
        }
        else {
            o = null;
        }
        final boolean childNeedsGapHeight = this.childNeedsGapHeight(ambientState.getSectionProvider(), stackScrollAlgorithmState, n, (View)expandableView, (View)o);
        final ExpandableViewState viewState = expandableView.getViewState();
        boolean headsUpIsVisible = false;
        viewState.location = 0;
        if (childNeedsGapHeight && !b) {
            yTranslation += this.mGapHeight;
        }
        final int paddingAfterChild = this.getPaddingAfterChild(stackScrollAlgorithmState, expandableView);
        final int maxAllowedChildHeight = this.getMaxAllowedChildHeight((View)expandableView);
        if (b) {
            viewState.yTranslation = yTranslation - (maxAllowedChildHeight + paddingAfterChild);
            if (yTranslation <= 0.0f) {
                viewState.location = 2;
            }
        }
        else {
            viewState.yTranslation = yTranslation;
        }
        final boolean b2 = expandableView instanceof FooterView;
        final boolean b3 = expandableView instanceof EmptyShadeView;
        viewState.location = 4;
        float n2;
        yTranslation = (n2 = ambientState.getTopPadding() + ambientState.getStackTranslation());
        if (n <= stackScrollAlgorithmState.getIndexOfExpandingNotification()) {
            n2 = yTranslation + ambientState.getExpandAnimationTopChange();
        }
        if (expandableView.mustStayOnScreen()) {
            yTranslation = viewState.yTranslation;
            if (yTranslation >= 0.0f) {
                if (yTranslation + viewState.height + n2 < ambientState.getMaxHeadsUpTranslation()) {
                    headsUpIsVisible = true;
                }
                viewState.headsUpIsVisible = headsUpIsVisible;
            }
        }
        if (b2) {
            viewState.yTranslation = Math.min(viewState.yTranslation, (float)(ambientState.getInnerHeight() - maxAllowedChildHeight));
        }
        else if (b3) {
            viewState.yTranslation = ambientState.getInnerHeight() - maxAllowedChildHeight + ambientState.getStackTranslation() * 0.25f;
        }
        else {
            this.clampPositionToShelf(expandableView, viewState, ambientState);
        }
        if (b) {
            final float n3 = yTranslation = viewState.yTranslation;
            if (childNeedsGapHeight) {
                yTranslation = n3 - this.mGapHeight;
            }
        }
        else {
            yTranslation = viewState.yTranslation;
            final float n4 = yTranslation = paddingAfterChild + (yTranslation + maxAllowedChildHeight);
            if (n4 <= 0.0f) {
                viewState.location = 2;
                yTranslation = n4;
            }
        }
        if (viewState.location == 0) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Failed to assign location for child ");
            sb.append(n);
            Log.wtf("StackScrollAlgorithm", sb.toString());
        }
        viewState.yTranslation += n2;
        return yTranslation;
    }
    
    protected float updateChildZValue(int intrinsicHeight, float n, final StackScrollAlgorithmState stackScrollAlgorithmState, final AmbientState ambientState) {
        final ExpandableView expandableView = stackScrollAlgorithmState.visibleChildren.get(intrinsicHeight);
        final ExpandableViewState viewState = expandableView.getViewState();
        final int zDistanceBetweenElements = ambientState.getZDistanceBetweenElements();
        final float n2 = (float)ambientState.getBaseZHeight();
        if (expandableView.mustStayOnScreen() && !viewState.headsUpIsVisible && !ambientState.isDozingAndNotPulsing(expandableView) && viewState.yTranslation < ambientState.getTopPadding() + ambientState.getStackTranslation()) {
            if (n != 0.0f) {
                ++n;
            }
            else {
                n += Math.min(1.0f, (ambientState.getTopPadding() + ambientState.getStackTranslation() - viewState.yTranslation) / viewState.height);
            }
            viewState.zTranslation = n2 + zDistanceBetweenElements * n;
        }
        else if (intrinsicHeight == 0 && (expandableView.isAboveShelf() || expandableView.showingPulsing())) {
            if (ambientState.getShelf() == null) {
                intrinsicHeight = 0;
            }
            else {
                intrinsicHeight = ambientState.getShelf().getIntrinsicHeight();
            }
            final float n3 = ambientState.getInnerHeight() - intrinsicHeight + ambientState.getTopPadding() + ambientState.getStackTranslation();
            final float n4 = viewState.yTranslation + expandableView.getPinnedHeadsUpHeight() + this.mPaddingBetweenElements;
            if (n3 > n4) {
                viewState.zTranslation = n2;
            }
            else {
                viewState.zTranslation = n2 + Math.min((n4 - n3) / intrinsicHeight, 1.0f) * zDistanceBetweenElements;
            }
        }
        else {
            viewState.zTranslation = n2;
        }
        viewState.zTranslation += (1.0f - expandableView.getHeaderVisibleAmount()) * this.mPinnedZTranslationExtra;
        return n;
    }
    
    public interface SectionProvider
    {
        boolean beginsSection(final View p0, final View p1);
    }
    
    public class StackScrollAlgorithmState
    {
        private int indexOfExpandingNotification;
        public final HashMap<ExpandableView, Float> paddingMap;
        public int scrollY;
        public final ArrayList<ExpandableView> visibleChildren;
        
        public StackScrollAlgorithmState() {
            this.visibleChildren = new ArrayList<ExpandableView>();
            this.paddingMap = new HashMap<ExpandableView, Float>();
        }
        
        public int getIndexOfExpandingNotification() {
            return this.indexOfExpandingNotification;
        }
        
        public int getPaddingAfterChild(final ExpandableView key) {
            final Float n = this.paddingMap.get(key);
            if (n == null) {
                return StackScrollAlgorithm.this.mPaddingBetweenElements;
            }
            return (int)(float)n;
        }
    }
}
