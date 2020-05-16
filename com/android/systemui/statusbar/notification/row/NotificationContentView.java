// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.util.ArraySet;
import java.util.Iterator;
import com.android.systemui.R$id;
import android.view.View$MeasureSpec;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.R$dimen;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.view.MotionEvent;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.statusbar.notification.row.wrapper.NotificationCustomViewWrapper;
import android.widget.ImageView;
import android.view.NotificationHeaderView;
import android.widget.LinearLayout;
import android.app.Notification$Action;
import java.util.function.Consumer;
import com.android.internal.util.ContrastColorUtil;
import com.android.systemui.R$color;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.TransformableView;
import com.android.systemui.Dependency;
import android.view.ViewGroup;
import android.util.AttributeSet;
import android.content.Context;
import android.util.Log;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.SmartReplyController;
import com.android.systemui.statusbar.policy.SmartReplyConstants;
import com.android.systemui.statusbar.RemoteInputController;
import android.app.PendingIntent;
import android.util.ArrayMap;
import com.android.systemui.statusbar.MediaTransferManager;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.policy.SmartReplyView;
import android.view.View$OnClickListener;
import android.view.ViewTreeObserver$OnPreDrawListener;
import com.android.systemui.statusbar.policy.InflatedSmartReplies;
import com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper;
import android.view.View;
import android.graphics.Rect;
import com.android.systemui.statusbar.policy.RemoteInputView;
import android.widget.FrameLayout;

public class NotificationContentView extends FrameLayout
{
    private static final boolean DEBUG;
    private boolean mAnimate;
    private int mAnimationStartVisibleType;
    private boolean mBeforeN;
    private RemoteInputView mCachedExpandedRemoteInput;
    private RemoteInputView mCachedHeadsUpRemoteInput;
    private int mClipBottomAmount;
    private final Rect mClipBounds;
    private boolean mClipToActualHeight;
    private int mClipTopAmount;
    private ExpandableNotificationRow mContainingNotification;
    private int mContentHeight;
    private int mContentHeightAtAnimationStart;
    private View mContractedChild;
    private NotificationViewWrapper mContractedWrapper;
    private InflatedSmartReplies.SmartRepliesAndActions mCurrentSmartRepliesAndActions;
    private final ViewTreeObserver$OnPreDrawListener mEnableAnimationPredrawListener;
    private View$OnClickListener mExpandClickListener;
    private boolean mExpandable;
    private View mExpandedChild;
    private InflatedSmartReplies mExpandedInflatedSmartReplies;
    private RemoteInputView mExpandedRemoteInput;
    private SmartReplyView mExpandedSmartReplyView;
    private Runnable mExpandedVisibleListener;
    private NotificationViewWrapper mExpandedWrapper;
    private boolean mFocusOnVisibilityChange;
    private boolean mForceSelectNextLayout;
    private NotificationGroupManager mGroupManager;
    private boolean mHeadsUpAnimatingAway;
    private View mHeadsUpChild;
    private int mHeadsUpHeight;
    private InflatedSmartReplies mHeadsUpInflatedSmartReplies;
    private RemoteInputView mHeadsUpRemoteInput;
    private SmartReplyView mHeadsUpSmartReplyView;
    private NotificationViewWrapper mHeadsUpWrapper;
    private HybridGroupManager mHybridGroupManager;
    private boolean mIsChildInGroup;
    private boolean mIsContentExpandable;
    private boolean mIsHeadsUp;
    private boolean mLegacy;
    private MediaTransferManager mMediaTransferManager;
    private int mMinContractedHeight;
    private int mNotificationContentMarginEnd;
    private int mNotificationMaxHeight;
    private final ArrayMap<View, Runnable> mOnContentViewInactiveListeners;
    private PendingIntent mPreviousExpandedRemoteInputIntent;
    private PendingIntent mPreviousHeadsUpRemoteInputIntent;
    private RemoteInputController mRemoteInputController;
    private boolean mRemoteInputVisible;
    private boolean mShelfIconVisible;
    private HybridNotificationView mSingleLineView;
    private int mSingleLineWidthIndention;
    private int mSmallHeight;
    private SmartReplyConstants mSmartReplyConstants;
    private SmartReplyController mSmartReplyController;
    private StatusBarNotification mStatusBarNotification;
    private int mTransformationStartVisibleType;
    private int mUnrestrictedContentHeight;
    private boolean mUserExpanding;
    private int mVisibleType;
    
    static {
        DEBUG = Log.isLoggable("NotificationContentView", 3);
    }
    
    public NotificationContentView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mClipBounds = new Rect();
        this.mVisibleType = -1;
        this.mOnContentViewInactiveListeners = (ArrayMap<View, Runnable>)new ArrayMap();
        this.mEnableAnimationPredrawListener = (ViewTreeObserver$OnPreDrawListener)new ViewTreeObserver$OnPreDrawListener() {
            public boolean onPreDraw() {
                NotificationContentView.this.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        NotificationContentView.this.mAnimate = true;
                    }
                });
                NotificationContentView.this.getViewTreeObserver().removeOnPreDrawListener((ViewTreeObserver$OnPreDrawListener)this);
                return true;
            }
        };
        this.mClipToActualHeight = true;
        this.mAnimationStartVisibleType = -1;
        this.mForceSelectNextLayout = true;
        this.mContentHeightAtAnimationStart = -1;
        this.mHybridGroupManager = new HybridGroupManager(this.getContext(), (ViewGroup)this);
        this.mMediaTransferManager = new MediaTransferManager(this.getContext());
        this.mSmartReplyConstants = Dependency.get(SmartReplyConstants.class);
        this.mSmartReplyController = Dependency.get(SmartReplyController.class);
        this.initView();
    }
    
    private void animateToVisibleType(final int n) {
        final TransformableView transformableViewForVisibleType = this.getTransformableViewForVisibleType(n);
        final TransformableView transformableViewForVisibleType2 = this.getTransformableViewForVisibleType(this.mVisibleType);
        if (transformableViewForVisibleType != transformableViewForVisibleType2 && transformableViewForVisibleType2 != null) {
            this.mAnimationStartVisibleType = this.mVisibleType;
            transformableViewForVisibleType.transformFrom(transformableViewForVisibleType2);
            this.getViewForVisibleType(n).setVisibility(0);
            transformableViewForVisibleType2.transformTo(transformableViewForVisibleType, new Runnable() {
                @Override
                public void run() {
                    final TransformableView val$hiddenView = transformableViewForVisibleType2;
                    final NotificationContentView this$0 = NotificationContentView.this;
                    if (val$hiddenView != this$0.getTransformableViewForVisibleType(this$0.mVisibleType)) {
                        transformableViewForVisibleType2.setVisible(false);
                    }
                    NotificationContentView.this.mAnimationStartVisibleType = -1;
                }
            });
            this.fireExpandedVisibleListenerIfVisible();
            return;
        }
        transformableViewForVisibleType.setVisible(true);
    }
    
    private void applyMediaTransfer(final NotificationEntry notificationEntry) {
        if (!notificationEntry.isMediaNotification()) {
            return;
        }
        final View mExpandedChild = this.mExpandedChild;
        if (mExpandedChild != null && mExpandedChild instanceof ViewGroup) {
            this.mMediaTransferManager.applyMediaTransferView((ViewGroup)mExpandedChild, notificationEntry);
        }
        final View mContractedChild = this.mContractedChild;
        if (mContractedChild != null && mContractedChild instanceof ViewGroup) {
            this.mMediaTransferManager.applyMediaTransferView((ViewGroup)mContractedChild, notificationEntry);
        }
    }
    
    private RemoteInputView applyRemoteInput(final View view, final NotificationEntry notificationEntry, final boolean b, final PendingIntent pendingIntent, RemoteInputView inflate, final NotificationViewWrapper wrapper) {
        final View viewById = view.findViewById(16908717);
        if (viewById instanceof FrameLayout) {
            final RemoteInputView remoteInputView = (RemoteInputView)view.findViewWithTag(RemoteInputView.VIEW_TAG);
            if (remoteInputView != null) {
                remoteInputView.onNotificationUpdateOrReset();
            }
            if (remoteInputView == null && b) {
                final FrameLayout frameLayout = (FrameLayout)viewById;
                if (inflate == null) {
                    inflate = RemoteInputView.inflate(super.mContext, (ViewGroup)frameLayout, notificationEntry, this.mRemoteInputController);
                    inflate.setVisibility(4);
                    ((ViewGroup)frameLayout).addView((View)inflate, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-1, -1));
                }
                else {
                    ((ViewGroup)frameLayout).addView((View)inflate);
                    inflate.dispatchFinishTemporaryDetach();
                    inflate.requestFocus();
                }
            }
            else {
                inflate = remoteInputView;
            }
            if (b) {
                int n;
                if ((n = notificationEntry.getSbn().getNotification().color) == 0) {
                    n = super.mContext.getColor(R$color.default_remote_input_background);
                }
                inflate.setBackgroundColor(ContrastColorUtil.ensureTextBackgroundColor(n, super.mContext.getColor(R$color.remote_input_text_enabled), super.mContext.getColor(R$color.remote_input_hint)));
                inflate.setWrapper(wrapper);
                inflate.setOnVisibilityChangedListener(new _$$Lambda$GC_EXjlJWjwU2u0y95DlTq2QVf0(this));
                if (pendingIntent != null || inflate.isActive()) {
                    final Notification$Action[] actions = notificationEntry.getSbn().getNotification().actions;
                    if (pendingIntent != null) {
                        inflate.setPendingIntent(pendingIntent);
                    }
                    if (inflate.updatePendingIntentFromActions(actions)) {
                        if (!inflate.isActive()) {
                            inflate.focus();
                        }
                    }
                    else if (inflate.isActive()) {
                        inflate.close();
                    }
                }
            }
            return inflate;
        }
        return null;
    }
    
    private void applyRemoteInput(final NotificationEntry notificationEntry, final boolean b) {
        final View mExpandedChild = this.mExpandedChild;
        if (mExpandedChild != null) {
            this.mExpandedRemoteInput = this.applyRemoteInput(mExpandedChild, notificationEntry, b, this.mPreviousExpandedRemoteInputIntent, this.mCachedExpandedRemoteInput, this.mExpandedWrapper);
        }
        else {
            this.mExpandedRemoteInput = null;
        }
        final RemoteInputView mCachedExpandedRemoteInput = this.mCachedExpandedRemoteInput;
        if (mCachedExpandedRemoteInput != null && mCachedExpandedRemoteInput != this.mExpandedRemoteInput) {
            mCachedExpandedRemoteInput.dispatchFinishTemporaryDetach();
        }
        this.mCachedExpandedRemoteInput = null;
        final View mHeadsUpChild = this.mHeadsUpChild;
        if (mHeadsUpChild != null) {
            this.mHeadsUpRemoteInput = this.applyRemoteInput(mHeadsUpChild, notificationEntry, b, this.mPreviousHeadsUpRemoteInputIntent, this.mCachedHeadsUpRemoteInput, this.mHeadsUpWrapper);
        }
        else {
            this.mHeadsUpRemoteInput = null;
        }
        final RemoteInputView mCachedHeadsUpRemoteInput = this.mCachedHeadsUpRemoteInput;
        if (mCachedHeadsUpRemoteInput != null && mCachedHeadsUpRemoteInput != this.mHeadsUpRemoteInput) {
            mCachedHeadsUpRemoteInput.dispatchFinishTemporaryDetach();
        }
        this.mCachedHeadsUpRemoteInput = null;
    }
    
    private void applyRemoteInputAndSmartReply(final NotificationEntry notificationEntry) {
        if (this.mRemoteInputController == null) {
            return;
        }
        this.applyRemoteInput(notificationEntry, InflatedSmartReplies.hasFreeformRemoteInput(notificationEntry));
        if (this.mExpandedInflatedSmartReplies == null && this.mHeadsUpInflatedSmartReplies == null) {
            if (NotificationContentView.DEBUG) {
                Log.d("NotificationContentView", "Both expanded, and heads-up InflatedSmartReplies are null, don't add smart replies.");
            }
            return;
        }
        final InflatedSmartReplies mExpandedInflatedSmartReplies = this.mExpandedInflatedSmartReplies;
        InflatedSmartReplies.SmartRepliesAndActions mCurrentSmartRepliesAndActions;
        if (mExpandedInflatedSmartReplies != null) {
            mCurrentSmartRepliesAndActions = mExpandedInflatedSmartReplies.getSmartRepliesAndActions();
        }
        else {
            mCurrentSmartRepliesAndActions = this.mHeadsUpInflatedSmartReplies.getSmartRepliesAndActions();
        }
        this.mCurrentSmartRepliesAndActions = mCurrentSmartRepliesAndActions;
        if (NotificationContentView.DEBUG) {
            final String key = notificationEntry.getSbn().getKey();
            int size = 0;
            final SmartReplyView.SmartActions smartActions = this.mCurrentSmartRepliesAndActions.smartActions;
            int size2;
            if (smartActions == null) {
                size2 = 0;
            }
            else {
                size2 = smartActions.actions.size();
            }
            final SmartReplyView.SmartReplies smartReplies = this.mCurrentSmartRepliesAndActions.smartReplies;
            if (smartReplies != null) {
                size = smartReplies.choices.size();
            }
            Log.d("NotificationContentView", String.format("Adding suggestions for %s, %d actions, and %d replies.", key, size2, size));
        }
        this.applySmartReplyView(this.mCurrentSmartRepliesAndActions, notificationEntry);
    }
    
    private SmartReplyView applySmartReplyView(View viewById, final InflatedSmartReplies.SmartRepliesAndActions smartRepliesAndActions, final NotificationEntry notificationEntry, final InflatedSmartReplies inflatedSmartReplies) {
        viewById = viewById.findViewById(16909430);
        final boolean b = viewById instanceof LinearLayout;
        final SmartReplyView smartReplyView = null;
        if (!b) {
            return null;
        }
        final LinearLayout linearLayout = (LinearLayout)viewById;
        if (!InflatedSmartReplies.shouldShowSmartReplyView(notificationEntry, smartRepliesAndActions)) {
            linearLayout.setVisibility(8);
            return null;
        }
        if (linearLayout.getChildCount() == 1 && linearLayout.getChildAt(0) instanceof SmartReplyView) {
            linearLayout.removeAllViews();
        }
        SmartReplyView smartReplyView2 = smartReplyView;
        if (linearLayout.getChildCount() == 0) {
            smartReplyView2 = smartReplyView;
            if (inflatedSmartReplies != null) {
                smartReplyView2 = smartReplyView;
                if (inflatedSmartReplies.getSmartReplyView() != null) {
                    smartReplyView2 = inflatedSmartReplies.getSmartReplyView();
                    linearLayout.addView((View)smartReplyView2);
                }
            }
        }
        if (smartReplyView2 != null) {
            smartReplyView2.resetSmartSuggestions((View)linearLayout);
            smartReplyView2.addPreInflatedButtons(inflatedSmartReplies.getSmartSuggestionButtons());
            smartReplyView2.setBackgroundTintColor(notificationEntry.getRow().getCurrentBackgroundTint());
            linearLayout.setVisibility(0);
        }
        return smartReplyView2;
    }
    
    private void applySmartReplyView(final InflatedSmartReplies.SmartRepliesAndActions smartRepliesAndActions, final NotificationEntry notificationEntry) {
        final View mExpandedChild = this.mExpandedChild;
        if (mExpandedChild != null && (this.mExpandedSmartReplyView = this.applySmartReplyView(mExpandedChild, smartRepliesAndActions, notificationEntry, this.mExpandedInflatedSmartReplies)) != null && (smartRepliesAndActions.smartReplies != null || smartRepliesAndActions.smartActions != null)) {
            final SmartReplyView.SmartReplies smartReplies = smartRepliesAndActions.smartReplies;
            final boolean b = false;
            int size;
            if (smartReplies == null) {
                size = 0;
            }
            else {
                size = smartReplies.choices.size();
            }
            final SmartReplyView.SmartActions smartActions = smartRepliesAndActions.smartActions;
            int size2;
            if (smartActions == null) {
                size2 = 0;
            }
            else {
                size2 = smartActions.actions.size();
            }
            final SmartReplyView.SmartReplies smartReplies2 = smartRepliesAndActions.smartReplies;
            boolean b2;
            if (smartReplies2 == null) {
                b2 = smartRepliesAndActions.smartActions.fromAssistant;
            }
            else {
                b2 = smartReplies2.fromAssistant;
            }
            final SmartReplyView.SmartReplies smartReplies3 = smartRepliesAndActions.smartReplies;
            boolean b3 = b;
            if (smartReplies3 != null) {
                b3 = b;
                if (this.mSmartReplyConstants.getEffectiveEditChoicesBeforeSending(smartReplies3.remoteInput.getEditChoicesBeforeSending())) {
                    b3 = true;
                }
            }
            this.mSmartReplyController.smartSuggestionsAdded(notificationEntry, size, size2, b2, b3);
        }
        if (this.mHeadsUpChild != null && this.mSmartReplyConstants.getShowInHeadsUp()) {
            this.mHeadsUpSmartReplyView = this.applySmartReplyView(this.mHeadsUpChild, smartRepliesAndActions, notificationEntry, this.mHeadsUpInflatedSmartReplies);
        }
    }
    
    private float calculateTransformationAmount() {
        final int viewHeight = this.getViewHeight(this.mTransformationStartVisibleType);
        final int viewHeight2 = this.getViewHeight(this.mVisibleType);
        final int abs = Math.abs(this.mContentHeight - viewHeight);
        final int abs2 = Math.abs(viewHeight2 - viewHeight);
        if (abs2 == 0) {
            final StringBuilder sb = new StringBuilder();
            sb.append("the total transformation distance is 0\n StartType: ");
            sb.append(this.mTransformationStartVisibleType);
            sb.append(" height: ");
            sb.append(viewHeight);
            sb.append("\n VisibleType: ");
            sb.append(this.mVisibleType);
            sb.append(" height: ");
            sb.append(viewHeight2);
            sb.append("\n mContentHeight: ");
            sb.append(this.mContentHeight);
            Log.wtf("NotificationContentView", sb.toString());
            return 1.0f;
        }
        return Math.min(1.0f, abs / (float)abs2);
    }
    
    private void fireExpandedVisibleListenerIfVisible() {
        if (this.mExpandedVisibleListener != null && this.mExpandedChild != null && this.isShown() && this.mExpandedChild.getVisibility() == 0) {
            final Runnable mExpandedVisibleListener = this.mExpandedVisibleListener;
            this.mExpandedVisibleListener = null;
            mExpandedVisibleListener.run();
        }
    }
    
    private void focusExpandButtonIfNecessary() {
        if (this.mFocusOnVisibilityChange) {
            final NotificationHeaderView visibleNotificationHeader = this.getVisibleNotificationHeader();
            if (visibleNotificationHeader != null) {
                final ImageView expandButton = visibleNotificationHeader.getExpandButton();
                if (expandButton != null) {
                    expandButton.requestAccessibilityFocus();
                }
            }
            this.mFocusOnVisibilityChange = false;
        }
    }
    
    private void forceUpdateVisibilities() {
        this.forceUpdateVisibility(0, this.mContractedChild, this.mContractedWrapper);
        this.forceUpdateVisibility(1, this.mExpandedChild, this.mExpandedWrapper);
        this.forceUpdateVisibility(2, this.mHeadsUpChild, this.mHeadsUpWrapper);
        final HybridNotificationView mSingleLineView = this.mSingleLineView;
        this.forceUpdateVisibility(3, (View)mSingleLineView, mSingleLineView);
        this.fireExpandedVisibleListenerIfVisible();
        this.mAnimationStartVisibleType = -1;
    }
    
    private void forceUpdateVisibility(int n, final View view, final TransformableView transformableView) {
        if (view == null) {
            return;
        }
        if (this.mVisibleType != n && this.mTransformationStartVisibleType != n) {
            n = 0;
        }
        else {
            n = 1;
        }
        if (n == 0) {
            view.setVisibility(4);
        }
        else {
            transformableView.setVisible(true);
        }
    }
    
    private int getExtraRemoteInputHeight(final RemoteInputView remoteInputView) {
        if (remoteInputView != null && (remoteInputView.isActive() || remoteInputView.isSending())) {
            return this.getResources().getDimensionPixelSize(17105342);
        }
        return 0;
    }
    
    private int getMinContentHeightHint() {
        if (this.mIsChildInGroup && this.isVisibleOrTransitioning(3)) {
            return super.mContext.getResources().getDimensionPixelSize(17105333);
        }
        if (this.mHeadsUpChild != null && this.mExpandedChild != null) {
            final boolean b = this.isTransitioningFromTo(2, 1) || this.isTransitioningFromTo(1, 2);
            final boolean b2 = !this.isVisibleOrTransitioning(0) && (this.mIsHeadsUp || this.mHeadsUpAnimatingAway) && this.mContainingNotification.canShowHeadsUp();
            if (b || b2) {
                return Math.min(this.getViewHeight(2), this.getViewHeight(1));
            }
        }
        if (this.mVisibleType == 1) {
            final int mContentHeightAtAnimationStart = this.mContentHeightAtAnimationStart;
            if (mContentHeightAtAnimationStart != -1 && this.mExpandedChild != null) {
                return Math.min(mContentHeightAtAnimationStart, this.getViewHeight(1));
            }
        }
        int a;
        if (this.mHeadsUpChild != null && this.isVisibleOrTransitioning(2)) {
            a = this.getViewHeight(2);
        }
        else if (this.mExpandedChild != null) {
            a = this.getViewHeight(1);
        }
        else if (this.mContractedChild != null) {
            a = this.getViewHeight(0) + super.mContext.getResources().getDimensionPixelSize(17105333);
        }
        else {
            a = this.getMinHeight();
        }
        int min = a;
        if (this.mExpandedChild != null) {
            min = a;
            if (this.isVisibleOrTransitioning(1)) {
                min = Math.min(a, this.getViewHeight(1));
            }
        }
        return min;
    }
    
    private RemoteInputView getRemoteInputForView(final View view) {
        if (view == this.mExpandedChild) {
            return this.mExpandedRemoteInput;
        }
        if (view == this.mHeadsUpChild) {
            return this.mHeadsUpRemoteInput;
        }
        return null;
    }
    
    private TransformableView getTransformableViewForVisibleType(final int n) {
        if (n == 1) {
            return this.mExpandedWrapper;
        }
        if (n == 2) {
            return this.mHeadsUpWrapper;
        }
        if (n != 3) {
            return this.mContractedWrapper;
        }
        return this.mSingleLineView;
    }
    
    private View getViewForVisibleType(final int n) {
        if (n == 1) {
            return this.mExpandedChild;
        }
        if (n == 2) {
            return this.mHeadsUpChild;
        }
        if (n != 3) {
            return this.mContractedChild;
        }
        return (View)this.mSingleLineView;
    }
    
    private int getViewHeight(final int n) {
        return this.getViewHeight(n, false);
    }
    
    private int getViewHeight(int n, final boolean b) {
        final View viewForVisibleType = this.getViewForVisibleType(n);
        final int height = viewForVisibleType.getHeight();
        final NotificationViewWrapper wrapperForView = this.getWrapperForView(viewForVisibleType);
        n = height;
        if (wrapperForView != null) {
            n = height + wrapperForView.getHeaderTranslation(b);
        }
        return n;
    }
    
    private int getVisualTypeForHeight(final float n) {
        final boolean b = this.mExpandedChild == null;
        if (!b && n == this.getViewHeight(1)) {
            return 1;
        }
        if (!this.mUserExpanding && this.mIsChildInGroup && !this.isGroupExpanded()) {
            return 3;
        }
        if ((!this.mIsHeadsUp && !this.mHeadsUpAnimatingAway) || this.mHeadsUpChild == null || !this.mContainingNotification.canShowHeadsUp()) {
            if (!b) {
                if (this.mContractedChild != null && n <= this.getViewHeight(0)) {
                    if (!this.mIsChildInGroup || this.isGroupExpanded()) {
                        return 0;
                    }
                    if (!this.mContainingNotification.isExpanded(true)) {
                        return 0;
                    }
                }
                if (!b) {
                    return 1;
                }
                return -1;
            }
            return 0;
        }
        if (n > this.getViewHeight(2) && !b) {
            return 1;
        }
        return 2;
    }
    
    private NotificationViewWrapper getWrapperForView(final View view) {
        if (view == this.mContractedChild) {
            return this.mContractedWrapper;
        }
        if (view == this.mExpandedChild) {
            return this.mExpandedWrapper;
        }
        if (view == this.mHeadsUpChild) {
            return this.mHeadsUpWrapper;
        }
        return null;
    }
    
    private boolean isContentViewInactive(final View view) {
        final boolean b = true;
        if (view == null) {
            return true;
        }
        boolean b2 = b;
        if (this.isShown()) {
            b2 = (view.getVisibility() != 0 && this.getViewForVisibleType(this.mVisibleType) != view && b);
        }
        return b2;
    }
    
    private boolean isGroupExpanded() {
        return this.mGroupManager.isGroupExpanded(this.mStatusBarNotification);
    }
    
    private boolean isTransitioningFromTo(final int n, final int n2) {
        return (this.mTransformationStartVisibleType == n || this.mAnimationStartVisibleType == n) && this.mVisibleType == n2;
    }
    
    private boolean isVisibleOrTransitioning(final int n) {
        return this.mVisibleType == n || this.mTransformationStartVisibleType == n || this.mAnimationStartVisibleType == n;
    }
    
    private void selectLayout(final boolean b, final boolean b2) {
        if (this.mContractedChild == null) {
            return;
        }
        if (this.mUserExpanding) {
            this.updateContentTransformation();
        }
        else {
            final int calculateVisibleType = this.calculateVisibleType();
            final boolean b3 = calculateVisibleType != this.mVisibleType;
            if (b3 || b2) {
                final View viewForVisibleType = this.getViewForVisibleType(calculateVisibleType);
                if (viewForVisibleType != null) {
                    viewForVisibleType.setVisibility(0);
                    this.transferRemoteInputFocus(calculateVisibleType);
                }
                if (b && ((calculateVisibleType == 1 && this.mExpandedChild != null) || (calculateVisibleType == 2 && this.mHeadsUpChild != null) || (calculateVisibleType == 3 && this.mSingleLineView != null) || calculateVisibleType == 0)) {
                    this.animateToVisibleType(calculateVisibleType);
                }
                else {
                    this.updateViewVisibilities(calculateVisibleType);
                }
                this.mVisibleType = calculateVisibleType;
                if (b3) {
                    this.focusExpandButtonIfNecessary();
                }
                final NotificationViewWrapper visibleWrapper = this.getVisibleWrapper(calculateVisibleType);
                if (visibleWrapper != null) {
                    visibleWrapper.setContentHeight(this.mUnrestrictedContentHeight, this.getMinContentHeightHint());
                }
                this.updateBackgroundColor(b);
            }
        }
    }
    
    private void setVisible(final boolean b) {
        if (b) {
            this.getViewTreeObserver().removeOnPreDrawListener(this.mEnableAnimationPredrawListener);
            this.getViewTreeObserver().addOnPreDrawListener(this.mEnableAnimationPredrawListener);
        }
        else {
            this.getViewTreeObserver().removeOnPreDrawListener(this.mEnableAnimationPredrawListener);
            this.mAnimate = false;
        }
    }
    
    private boolean shouldClipToRounding(final int n, final boolean b, final boolean b2) {
        final NotificationViewWrapper visibleWrapper = this.getVisibleWrapper(n);
        return visibleWrapper != null && visibleWrapper.shouldClipToRounding(b, b2);
    }
    
    private boolean shouldContractedBeFixedSize() {
        return this.mBeforeN && this.mContractedWrapper instanceof NotificationCustomViewWrapper;
    }
    
    private void transferRemoteInputFocus(final int n) {
        if (n == 2 && this.mHeadsUpRemoteInput != null) {
            final RemoteInputView mExpandedRemoteInput = this.mExpandedRemoteInput;
            if (mExpandedRemoteInput != null && mExpandedRemoteInput.isActive()) {
                this.mHeadsUpRemoteInput.stealFocusFrom(this.mExpandedRemoteInput);
            }
        }
        if (n == 1 && this.mExpandedRemoteInput != null) {
            final RemoteInputView mHeadsUpRemoteInput = this.mHeadsUpRemoteInput;
            if (mHeadsUpRemoteInput != null && mHeadsUpRemoteInput.isActive()) {
                this.mExpandedRemoteInput.stealFocusFrom(this.mHeadsUpRemoteInput);
            }
        }
    }
    
    private void updateAllSingleLineViews() {
        this.updateSingleLineView();
    }
    
    private void updateBackgroundTransformation(final float n) {
        final int backgroundColor = this.getBackgroundColor(this.mVisibleType);
        final int backgroundColor2 = this.getBackgroundColor(this.mTransformationStartVisibleType);
        int interpolateColors = backgroundColor;
        if (backgroundColor != backgroundColor2) {
            int backgroundColorWithoutTint;
            if ((backgroundColorWithoutTint = backgroundColor2) == 0) {
                backgroundColorWithoutTint = this.mContainingNotification.getBackgroundColorWithoutTint();
            }
            int backgroundColorWithoutTint2;
            if ((backgroundColorWithoutTint2 = backgroundColor) == 0) {
                backgroundColorWithoutTint2 = this.mContainingNotification.getBackgroundColorWithoutTint();
            }
            interpolateColors = NotificationUtils.interpolateColors(backgroundColorWithoutTint, backgroundColorWithoutTint2, n);
        }
        this.mContainingNotification.updateBackgroundAlpha(n);
        this.mContainingNotification.setContentBackground(interpolateColors, false, this);
    }
    
    private void updateClipping() {
        if (this.mClipToActualHeight) {
            final int a = (int)(this.mClipTopAmount - this.getTranslationY());
            this.mClipBounds.set(0, a, this.getWidth(), Math.max(a, (int)(this.mUnrestrictedContentHeight - this.mClipBottomAmount - this.getTranslationY())));
            this.setClipBounds(this.mClipBounds);
        }
        else {
            this.setClipBounds((Rect)null);
        }
    }
    
    private void updateContentTransformation() {
        final int calculateVisibleType = this.calculateVisibleType();
        final int mVisibleType = this.mVisibleType;
        if (calculateVisibleType != mVisibleType) {
            this.mTransformationStartVisibleType = mVisibleType;
            final TransformableView transformableViewForVisibleType = this.getTransformableViewForVisibleType(calculateVisibleType);
            final TransformableView transformableViewForVisibleType2 = this.getTransformableViewForVisibleType(this.mTransformationStartVisibleType);
            transformableViewForVisibleType.transformFrom(transformableViewForVisibleType2, 0.0f);
            this.getViewForVisibleType(calculateVisibleType).setVisibility(0);
            transformableViewForVisibleType2.transformTo(transformableViewForVisibleType, 0.0f);
            this.mVisibleType = calculateVisibleType;
            this.updateBackgroundColor(true);
        }
        if (this.mForceSelectNextLayout) {
            this.forceUpdateVisibilities();
        }
        final int mTransformationStartVisibleType = this.mTransformationStartVisibleType;
        if (mTransformationStartVisibleType != -1 && this.mVisibleType != mTransformationStartVisibleType && this.getViewForVisibleType(mTransformationStartVisibleType) != null) {
            final TransformableView transformableViewForVisibleType3 = this.getTransformableViewForVisibleType(this.mVisibleType);
            final TransformableView transformableViewForVisibleType4 = this.getTransformableViewForVisibleType(this.mTransformationStartVisibleType);
            final float calculateTransformationAmount = this.calculateTransformationAmount();
            transformableViewForVisibleType3.transformFrom(transformableViewForVisibleType4, calculateTransformationAmount);
            transformableViewForVisibleType4.transformTo(transformableViewForVisibleType3, calculateTransformationAmount);
            this.updateBackgroundTransformation(calculateTransformationAmount);
        }
        else {
            this.updateViewVisibilities(calculateVisibleType);
            this.updateBackgroundColor(false);
        }
    }
    
    private boolean updateContractedHeaderWidth() {
        final NotificationHeaderView notificationHeader = this.mContractedWrapper.getNotificationHeader();
        if (notificationHeader != null) {
            if (this.mExpandedChild != null && this.mExpandedWrapper.getNotificationHeader() != null) {
                final int headerTextMarginEnd = this.mExpandedWrapper.getNotificationHeader().getHeaderTextMarginEnd();
                if (headerTextMarginEnd != notificationHeader.getHeaderTextMarginEnd()) {
                    notificationHeader.setHeaderTextMarginEnd(headerTextMarginEnd);
                    return true;
                }
            }
            else {
                int n = this.mNotificationContentMarginEnd;
                if (notificationHeader.getPaddingEnd() != n) {
                    int paddingLeft;
                    if (notificationHeader.isLayoutRtl()) {
                        paddingLeft = n;
                    }
                    else {
                        paddingLeft = notificationHeader.getPaddingLeft();
                    }
                    final int paddingTop = notificationHeader.getPaddingTop();
                    if (notificationHeader.isLayoutRtl()) {
                        n = notificationHeader.getPaddingLeft();
                    }
                    notificationHeader.setPadding(paddingLeft, paddingTop, n, notificationHeader.getPaddingBottom());
                    notificationHeader.setShowWorkBadgeAtEnd(false);
                    return true;
                }
            }
        }
        return false;
    }
    
    private void updateIconVisibilities() {
        final NotificationViewWrapper mContractedWrapper = this.mContractedWrapper;
        if (mContractedWrapper != null) {
            mContractedWrapper.setShelfIconVisible(this.mShelfIconVisible);
        }
        final NotificationViewWrapper mHeadsUpWrapper = this.mHeadsUpWrapper;
        if (mHeadsUpWrapper != null) {
            mHeadsUpWrapper.setShelfIconVisible(this.mShelfIconVisible);
        }
        final NotificationViewWrapper mExpandedWrapper = this.mExpandedWrapper;
        if (mExpandedWrapper != null) {
            mExpandedWrapper.setShelfIconVisible(this.mShelfIconVisible);
        }
    }
    
    private void updateLegacy() {
        if (this.mContractedChild != null) {
            this.mContractedWrapper.setLegacy(this.mLegacy);
        }
        if (this.mExpandedChild != null) {
            this.mExpandedWrapper.setLegacy(this.mLegacy);
        }
        if (this.mHeadsUpChild != null) {
            this.mHeadsUpWrapper.setLegacy(this.mLegacy);
        }
    }
    
    private void updateSingleLineView() {
        if (this.mIsChildInGroup) {
            final boolean b = this.mSingleLineView == null;
            final HybridNotificationView bindFromNotification = this.mHybridGroupManager.bindFromNotification(this.mSingleLineView, this.mStatusBarNotification.getNotification());
            this.mSingleLineView = bindFromNotification;
            if (b) {
                this.updateViewVisibility(this.mVisibleType, 3, (View)bindFromNotification, bindFromNotification);
            }
        }
        else {
            final HybridNotificationView mSingleLineView = this.mSingleLineView;
            if (mSingleLineView != null) {
                this.removeView((View)mSingleLineView);
                this.mSingleLineView = null;
            }
        }
    }
    
    private void updateViewVisibilities(final int n) {
        this.updateViewVisibility(n, 0, this.mContractedChild, this.mContractedWrapper);
        this.updateViewVisibility(n, 1, this.mExpandedChild, this.mExpandedWrapper);
        this.updateViewVisibility(n, 2, this.mHeadsUpChild, this.mHeadsUpWrapper);
        final HybridNotificationView mSingleLineView = this.mSingleLineView;
        this.updateViewVisibility(n, 3, (View)mSingleLineView, mSingleLineView);
        this.fireExpandedVisibleListenerIfVisible();
        this.mAnimationStartVisibleType = -1;
    }
    
    private void updateViewVisibility(final int n, final int n2, final View view, final TransformableView transformableView) {
        if (view != null) {
            transformableView.setVisible(n == n2);
        }
    }
    
    private void updateVisibility() {
        this.setVisible(this.isShown());
    }
    
    public int calculateVisibleType() {
        if (this.mUserExpanding) {
            int n;
            if (this.mIsChildInGroup && !this.isGroupExpanded() && !this.mContainingNotification.isExpanded(true)) {
                n = this.mContainingNotification.getShowingLayout().getMinHeight();
            }
            else {
                n = this.mContainingNotification.getMaxContentHeight();
            }
            int mContentHeight = n;
            if (n == 0) {
                mContentHeight = this.mContentHeight;
            }
            final int visualTypeForHeight = this.getVisualTypeForHeight((float)mContentHeight);
            int visualTypeForHeight2;
            if (this.mIsChildInGroup && !this.isGroupExpanded()) {
                visualTypeForHeight2 = 3;
            }
            else {
                visualTypeForHeight2 = this.getVisualTypeForHeight((float)this.mContainingNotification.getCollapsedHeight());
            }
            if (this.mTransformationStartVisibleType == visualTypeForHeight2) {
                visualTypeForHeight2 = visualTypeForHeight;
            }
            return visualTypeForHeight2;
        }
        final int intrinsicHeight = this.mContainingNotification.getIntrinsicHeight();
        int a = this.mContentHeight;
        if (intrinsicHeight != 0) {
            a = Math.min(a, intrinsicHeight);
        }
        return this.getVisualTypeForHeight((float)a);
    }
    
    public void closeRemoteInput() {
        final RemoteInputView mHeadsUpRemoteInput = this.mHeadsUpRemoteInput;
        if (mHeadsUpRemoteInput != null) {
            mHeadsUpRemoteInput.close();
        }
        final RemoteInputView mExpandedRemoteInput = this.mExpandedRemoteInput;
        if (mExpandedRemoteInput != null) {
            mExpandedRemoteInput.close();
        }
    }
    
    public boolean disallowSingleClick(final float n, final float n2) {
        final NotificationViewWrapper visibleWrapper = this.getVisibleWrapper(this.getVisibleType());
        return visibleWrapper != null && visibleWrapper.disallowSingleClick(n, n2);
    }
    
    public boolean dispatchTouchEvent(final MotionEvent motionEvent) {
        final float y = motionEvent.getY();
        final RemoteInputView remoteInputForView = this.getRemoteInputForView(this.getViewForVisibleType(this.mVisibleType));
        if (remoteInputForView != null && remoteInputForView.getVisibility() == 0) {
            final int n = this.mUnrestrictedContentHeight - remoteInputForView.getHeight();
            if (y <= this.mUnrestrictedContentHeight && y >= n) {
                motionEvent.offsetLocation(0.0f, (float)(-n));
                return remoteInputForView.dispatchTouchEvent(motionEvent);
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.print("    ");
        final StringBuilder sb = new StringBuilder();
        sb.append("contentView visibility: ");
        sb.append(this.getVisibility());
        printWriter.print(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(", alpha: ");
        sb2.append(this.getAlpha());
        printWriter.print(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append(", clipBounds: ");
        sb3.append(this.getClipBounds());
        printWriter.print(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append(", contentHeight: ");
        sb4.append(this.mContentHeight);
        printWriter.print(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append(", visibleType: ");
        sb5.append(this.mVisibleType);
        printWriter.print(sb5.toString());
        final View viewForVisibleType = this.getViewForVisibleType(this.mVisibleType);
        printWriter.print(", visibleView ");
        if (viewForVisibleType != null) {
            final StringBuilder sb6 = new StringBuilder();
            sb6.append(" visibility: ");
            sb6.append(viewForVisibleType.getVisibility());
            printWriter.print(sb6.toString());
            final StringBuilder sb7 = new StringBuilder();
            sb7.append(", alpha: ");
            sb7.append(viewForVisibleType.getAlpha());
            printWriter.print(sb7.toString());
            final StringBuilder sb8 = new StringBuilder();
            sb8.append(", clipBounds: ");
            sb8.append(viewForVisibleType.getClipBounds());
            printWriter.print(sb8.toString());
        }
        else {
            printWriter.print("null");
        }
        printWriter.println();
    }
    
    public CharSequence getActiveRemoteInputText() {
        final RemoteInputView mExpandedRemoteInput = this.mExpandedRemoteInput;
        if (mExpandedRemoteInput != null && mExpandedRemoteInput.isActive()) {
            return this.mExpandedRemoteInput.getText();
        }
        final RemoteInputView mHeadsUpRemoteInput = this.mHeadsUpRemoteInput;
        if (mHeadsUpRemoteInput != null && mHeadsUpRemoteInput.isActive()) {
            return this.mHeadsUpRemoteInput.getText();
        }
        return null;
    }
    
    public View[] getAllViews() {
        return new View[] { this.mContractedChild, this.mHeadsUpChild, this.mExpandedChild, (View)this.mSingleLineView };
    }
    
    public int getBackgroundColor(int customBackgroundColor) {
        final NotificationViewWrapper visibleWrapper = this.getVisibleWrapper(customBackgroundColor);
        if (visibleWrapper != null) {
            customBackgroundColor = visibleWrapper.getCustomBackgroundColor();
        }
        else {
            customBackgroundColor = 0;
        }
        return customBackgroundColor;
    }
    
    public int getBackgroundColorForExpansionState() {
        int n;
        if (!this.mContainingNotification.isGroupExpanded() && !this.mContainingNotification.isUserLocked()) {
            n = this.getVisibleType();
        }
        else {
            n = this.calculateVisibleType();
        }
        return this.getBackgroundColor(n);
    }
    
    public View getContractedChild() {
        return this.mContractedChild;
    }
    
    public InflatedSmartReplies.SmartRepliesAndActions getCurrentSmartRepliesAndActions() {
        return this.mCurrentSmartRepliesAndActions;
    }
    
    public int getExpandHeight() {
        int n;
        if (this.mExpandedChild != null) {
            n = 1;
        }
        else {
            if (this.mContractedChild == null) {
                return this.getMinHeight();
            }
            n = 0;
        }
        return this.getViewHeight(n) + this.getExtraRemoteInputHeight(this.mExpandedRemoteInput);
    }
    
    public View getExpandedChild() {
        return this.mExpandedChild;
    }
    
    public RemoteInputView getExpandedRemoteInput() {
        return this.mExpandedRemoteInput;
    }
    
    public View getHeadsUpChild() {
        return this.mHeadsUpChild;
    }
    
    public int getHeadsUpHeight(final boolean b) {
        int n;
        if (this.mHeadsUpChild != null) {
            n = 2;
        }
        else {
            if (this.mContractedChild == null) {
                return this.getMinHeight();
            }
            n = 0;
        }
        return this.getViewHeight(n, b) + this.getExtraRemoteInputHeight(this.mHeadsUpRemoteInput) + this.getExtraRemoteInputHeight(this.mExpandedRemoteInput);
    }
    
    public int getMaxHeight() {
        int n;
        int n2;
        if (this.mExpandedChild != null) {
            n = this.getViewHeight(1);
            n2 = this.getExtraRemoteInputHeight(this.mExpandedRemoteInput);
        }
        else if (this.mIsHeadsUp && this.mHeadsUpChild != null && this.mContainingNotification.canShowHeadsUp()) {
            n = this.getViewHeight(2);
            n2 = this.getExtraRemoteInputHeight(this.mHeadsUpRemoteInput);
        }
        else {
            if (this.mContractedChild != null) {
                return this.getViewHeight(0);
            }
            return this.mNotificationMaxHeight;
        }
        return n + n2;
    }
    
    public int getMinHeight() {
        return this.getMinHeight(false);
    }
    
    public int getMinHeight(final boolean b) {
        if (!b && this.mIsChildInGroup && !this.isGroupExpanded()) {
            return this.mSingleLineView.getHeight();
        }
        int n;
        if (this.mContractedChild != null) {
            n = this.getViewHeight(0);
        }
        else {
            n = this.mMinContractedHeight;
        }
        return n;
    }
    
    public NotificationHeaderView getNotificationHeader() {
        NotificationHeaderView notificationHeader;
        if (this.mContractedChild != null) {
            notificationHeader = this.mContractedWrapper.getNotificationHeader();
        }
        else {
            notificationHeader = null;
        }
        NotificationHeaderView notificationHeader2 = notificationHeader;
        if (notificationHeader == null) {
            notificationHeader2 = notificationHeader;
            if (this.mExpandedChild != null) {
                notificationHeader2 = this.mExpandedWrapper.getNotificationHeader();
            }
        }
        NotificationHeaderView notificationHeader3;
        if ((notificationHeader3 = notificationHeader2) == null) {
            notificationHeader3 = notificationHeader2;
            if (this.mHeadsUpChild != null) {
                notificationHeader3 = this.mHeadsUpWrapper.getNotificationHeader();
            }
        }
        return notificationHeader3;
    }
    
    public int getOriginalIconColor() {
        final NotificationViewWrapper visibleWrapper = this.getVisibleWrapper(this.mVisibleType);
        if (visibleWrapper != null) {
            return visibleWrapper.getOriginalIconColor();
        }
        return 1;
    }
    
    public View getShelfTransformationTarget() {
        final NotificationViewWrapper visibleWrapper = this.getVisibleWrapper(this.mVisibleType);
        if (visibleWrapper != null) {
            return visibleWrapper.getShelfTransformationTarget();
        }
        return null;
    }
    
    public HybridNotificationView getSingleLineView() {
        return this.mSingleLineView;
    }
    
    public NotificationHeaderView getVisibleNotificationHeader() {
        final NotificationViewWrapper visibleWrapper = this.getVisibleWrapper(this.mVisibleType);
        NotificationHeaderView notificationHeader;
        if (visibleWrapper == null) {
            notificationHeader = null;
        }
        else {
            notificationHeader = visibleWrapper.getNotificationHeader();
        }
        return notificationHeader;
    }
    
    public int getVisibleType() {
        return this.mVisibleType;
    }
    
    public NotificationViewWrapper getVisibleWrapper(final int n) {
        if (n == 0) {
            return this.mContractedWrapper;
        }
        if (n == 1) {
            return this.mExpandedWrapper;
        }
        if (n != 2) {
            return null;
        }
        return this.mHeadsUpWrapper;
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    public void initView() {
        this.mMinContractedHeight = this.getResources().getDimensionPixelSize(R$dimen.min_notification_layout_height);
        this.mNotificationContentMarginEnd = this.getResources().getDimensionPixelSize(17105343);
    }
    
    @VisibleForTesting
    boolean isAnimatingVisibleType() {
        return this.mAnimationStartVisibleType != -1;
    }
    
    public boolean isContentExpandable() {
        return this.mIsContentExpandable;
    }
    
    public boolean isContentViewInactive(final int n) {
        return this.isContentViewInactive(this.getViewForVisibleType(n));
    }
    
    public boolean isDimmable() {
        final NotificationViewWrapper mContractedWrapper = this.mContractedWrapper;
        return mContractedWrapper != null && mContractedWrapper.isDimmable();
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.updateVisibility();
    }
    
    protected void onChildVisibilityChanged(final View view, final int n, final int n2) {
        super.onChildVisibilityChanged(view, n, n2);
        if (this.isContentViewInactive(view)) {
            final Runnable runnable = (Runnable)this.mOnContentViewInactiveListeners.remove((Object)view);
            if (runnable != null) {
                runnable.run();
            }
        }
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.getViewTreeObserver().removeOnPreDrawListener(this.mEnableAnimationPredrawListener);
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        final View mExpandedChild = this.mExpandedChild;
        int height;
        if (mExpandedChild != null) {
            height = mExpandedChild.getHeight();
        }
        else {
            height = 0;
        }
        super.onLayout(b, n, n2, n3, n4);
        if (height != 0 && this.mExpandedChild.getHeight() != height) {
            this.mContentHeightAtAnimationStart = height;
        }
        this.updateClipping();
        this.invalidateOutline();
        this.selectLayout(false, this.mForceSelectNextLayout);
        this.mForceSelectNextLayout = false;
        this.updateExpandButtons(this.mExpandable);
    }
    
    protected void onMeasure(int measureSpec, int n) {
        final int mode = View$MeasureSpec.getMode(n);
        final int n2 = 1;
        final boolean b = mode == 1073741824;
        final boolean b2 = mode == Integer.MIN_VALUE;
        int size = 1073741823;
        final int size2 = View$MeasureSpec.getSize(measureSpec);
        if (b || b2) {
            size = View$MeasureSpec.getSize(n);
        }
        if (this.mExpandedChild != null) {
            final int mNotificationMaxHeight = this.mNotificationMaxHeight;
            final SmartReplyView mExpandedSmartReplyView = this.mExpandedSmartReplyView;
            n = mNotificationMaxHeight;
            if (mExpandedSmartReplyView != null) {
                n = mNotificationMaxHeight + mExpandedSmartReplyView.getHeightUpperLimit();
            }
            n += this.mExpandedWrapper.getExtraMeasureHeight();
            final int height = this.mExpandedChild.getLayoutParams().height;
            boolean b3;
            if (height >= 0) {
                n = Math.min(n, height);
                b3 = true;
            }
            else {
                b3 = false;
            }
            int n3;
            if (b3) {
                n3 = 1073741824;
            }
            else {
                n3 = Integer.MIN_VALUE;
            }
            n = View$MeasureSpec.makeMeasureSpec(n, n3);
            this.measureChildWithMargins(this.mExpandedChild, measureSpec, 0, n, 0);
            n = Math.max(0, this.mExpandedChild.getMeasuredHeight());
        }
        else {
            n = 0;
        }
        final View mContractedChild = this.mContractedChild;
        int a = n;
        if (mContractedChild != null) {
            int a2 = this.mSmallHeight;
            final int height2 = mContractedChild.getLayoutParams().height;
            boolean b4;
            if (height2 >= 0) {
                a2 = Math.min(a2, height2);
                b4 = true;
            }
            else {
                b4 = false;
            }
            int n4;
            if (!this.shouldContractedBeFixedSize() && !b4) {
                n4 = View$MeasureSpec.makeMeasureSpec(a2, Integer.MIN_VALUE);
            }
            else {
                n4 = View$MeasureSpec.makeMeasureSpec(a2, 1073741824);
            }
            this.measureChildWithMargins(this.mContractedChild, measureSpec, 0, n4, 0);
            final int measuredHeight = this.mContractedChild.getMeasuredHeight();
            final int mMinContractedHeight = this.mMinContractedHeight;
            if (measuredHeight < mMinContractedHeight) {
                n4 = View$MeasureSpec.makeMeasureSpec(mMinContractedHeight, 1073741824);
                this.measureChildWithMargins(this.mContractedChild, measureSpec, 0, n4, 0);
            }
            n = Math.max(n, measuredHeight);
            if (this.updateContractedHeaderWidth()) {
                this.measureChildWithMargins(this.mContractedChild, measureSpec, 0, n4, 0);
            }
            a = n;
            if (this.mExpandedChild != null) {
                a = n;
                if (this.mContractedChild.getMeasuredHeight() > this.mExpandedChild.getMeasuredHeight()) {
                    this.measureChildWithMargins(this.mExpandedChild, measureSpec, 0, View$MeasureSpec.makeMeasureSpec(this.mContractedChild.getMeasuredHeight(), 1073741824), 0);
                    a = n;
                }
            }
        }
        n = a;
        if (this.mHeadsUpChild != null) {
            final int mHeadsUpHeight = this.mHeadsUpHeight;
            final SmartReplyView mHeadsUpSmartReplyView = this.mHeadsUpSmartReplyView;
            n = mHeadsUpHeight;
            if (mHeadsUpSmartReplyView != null) {
                n = mHeadsUpHeight + mHeadsUpSmartReplyView.getHeightUpperLimit();
            }
            n += this.mHeadsUpWrapper.getExtraMeasureHeight();
            final int height3 = this.mHeadsUpChild.getLayoutParams().height;
            int n5;
            if (height3 >= 0) {
                n = Math.min(n, height3);
                n5 = n2;
            }
            else {
                n5 = 0;
            }
            final View mHeadsUpChild = this.mHeadsUpChild;
            int n6;
            if (n5 != 0) {
                n6 = 1073741824;
            }
            else {
                n6 = Integer.MIN_VALUE;
            }
            this.measureChildWithMargins(mHeadsUpChild, measureSpec, 0, View$MeasureSpec.makeMeasureSpec(n, n6), 0);
            n = Math.max(a, this.mHeadsUpChild.getMeasuredHeight());
        }
        int max = n;
        if (this.mSingleLineView != null) {
            if (this.mSingleLineWidthIndention != 0 && View$MeasureSpec.getMode(measureSpec) != 0) {
                measureSpec = View$MeasureSpec.makeMeasureSpec(size2 - this.mSingleLineWidthIndention + this.mSingleLineView.getPaddingEnd(), 1073741824);
            }
            this.mSingleLineView.measure(measureSpec, View$MeasureSpec.makeMeasureSpec(this.mNotificationMaxHeight, Integer.MIN_VALUE));
            max = Math.max(n, this.mSingleLineView.getMeasuredHeight());
        }
        this.setMeasuredDimension(size2, Math.min(max, size));
    }
    
    public void onNotificationUpdated(final NotificationEntry notificationEntry) {
        this.mStatusBarNotification = notificationEntry.getSbn();
        this.mBeforeN = (notificationEntry.targetSdk < 24);
        this.updateAllSingleLineViews();
        final ExpandableNotificationRow row = notificationEntry.getRow();
        if (this.mContractedChild != null) {
            this.mContractedWrapper.onContentUpdated(row);
        }
        if (this.mExpandedChild != null) {
            this.mExpandedWrapper.onContentUpdated(row);
        }
        if (this.mHeadsUpChild != null) {
            this.mHeadsUpWrapper.onContentUpdated(row);
        }
        this.applyRemoteInputAndSmartReply(notificationEntry);
        this.applyMediaTransfer(notificationEntry);
        this.updateLegacy();
        this.mForceSelectNextLayout = true;
        this.mPreviousExpandedRemoteInputIntent = null;
        this.mPreviousHeadsUpRemoteInputIntent = null;
    }
    
    public void onViewAdded(final View view) {
        super.onViewAdded(view);
        view.setTag(R$id.row_tag_for_content_view, (Object)this.mContainingNotification);
    }
    
    public void onVisibilityAggregated(final boolean b) {
        super.onVisibilityAggregated(b);
        if (b) {
            this.fireExpandedVisibleListenerIfVisible();
        }
    }
    
    protected void onVisibilityChanged(final View view, final int n) {
        super.onVisibilityChanged(view, n);
        this.updateVisibility();
        if (n != 0) {
            final Iterator<Runnable> iterator = this.mOnContentViewInactiveListeners.values().iterator();
            while (iterator.hasNext()) {
                iterator.next().run();
            }
            this.mOnContentViewInactiveListeners.clear();
        }
    }
    
    void performWhenContentInactive(final int n, final Runnable runnable) {
        final View viewForVisibleType = this.getViewForVisibleType(n);
        if (viewForVisibleType != null && !this.isContentViewInactive(n)) {
            this.mOnContentViewInactiveListeners.put((Object)viewForVisibleType, (Object)runnable);
            return;
        }
        runnable.run();
    }
    
    public boolean pointInView(final float n, final float n2, final float n3) {
        final float n4 = (float)this.mClipTopAmount;
        final float n5 = (float)this.mUnrestrictedContentHeight;
        return n >= -n3 && n2 >= n4 - n3 && n < super.mRight - super.mLeft + n3 && n2 < n5 + n3;
    }
    
    public void reInflateViews() {
        if (this.mIsChildInGroup) {
            final HybridNotificationView mSingleLineView = this.mSingleLineView;
            if (mSingleLineView != null) {
                this.removeView((View)mSingleLineView);
                this.mSingleLineView = null;
                this.updateAllSingleLineViews();
            }
        }
    }
    
    void removeContentInactiveRunnable(final int n) {
        final View viewForVisibleType = this.getViewForVisibleType(n);
        if (viewForVisibleType == null) {
            return;
        }
        this.mOnContentViewInactiveListeners.remove((Object)viewForVisibleType);
    }
    
    public void requestSelectLayout(final boolean b) {
        this.selectLayout(b, false);
    }
    
    public void setBackgroundTintColor(final int n) {
        final SmartReplyView mExpandedSmartReplyView = this.mExpandedSmartReplyView;
        if (mExpandedSmartReplyView != null) {
            mExpandedSmartReplyView.setBackgroundTintColor(n);
        }
        final SmartReplyView mHeadsUpSmartReplyView = this.mHeadsUpSmartReplyView;
        if (mHeadsUpSmartReplyView != null) {
            mHeadsUpSmartReplyView.setBackgroundTintColor(n);
        }
    }
    
    public void setClipBottomAmount(final int mClipBottomAmount) {
        this.mClipBottomAmount = mClipBottomAmount;
        this.updateClipping();
    }
    
    public void setClipChildren(final boolean b) {
        super.setClipChildren(b && !this.mRemoteInputVisible);
    }
    
    public void setClipToActualHeight(final boolean mClipToActualHeight) {
        this.mClipToActualHeight = mClipToActualHeight;
        this.updateClipping();
    }
    
    public void setClipTopAmount(final int mClipTopAmount) {
        this.mClipTopAmount = mClipTopAmount;
        this.updateClipping();
    }
    
    public void setContainingNotification(final ExpandableNotificationRow mContainingNotification) {
        this.mContainingNotification = mContainingNotification;
    }
    
    public void setContentHeight(int a) {
        this.mUnrestrictedContentHeight = Math.max(a, this.getMinHeight());
        final int intrinsicHeight = this.mContainingNotification.getIntrinsicHeight();
        a = this.getExtraRemoteInputHeight(this.mExpandedRemoteInput);
        this.mContentHeight = Math.min(this.mUnrestrictedContentHeight, intrinsicHeight - a - this.getExtraRemoteInputHeight(this.mHeadsUpRemoteInput));
        this.selectLayout(this.mAnimate, false);
        if (this.mContractedChild == null) {
            return;
        }
        a = this.getMinContentHeightHint();
        final NotificationViewWrapper visibleWrapper = this.getVisibleWrapper(this.mVisibleType);
        if (visibleWrapper != null) {
            visibleWrapper.setContentHeight(this.mUnrestrictedContentHeight, a);
        }
        final NotificationViewWrapper visibleWrapper2 = this.getVisibleWrapper(this.mTransformationStartVisibleType);
        if (visibleWrapper2 != null) {
            visibleWrapper2.setContentHeight(this.mUnrestrictedContentHeight, a);
        }
        this.updateClipping();
        this.invalidateOutline();
    }
    
    public void setContentHeightAnimating(final boolean b) {
        if (!b) {
            this.mContentHeightAtAnimationStart = -1;
        }
    }
    
    public void setContractedChild(final View mContractedChild) {
        final View mContractedChild2 = this.mContractedChild;
        if (mContractedChild2 != null) {
            this.mOnContentViewInactiveListeners.remove((Object)mContractedChild2);
            this.mContractedChild.animate().cancel();
            this.removeView(this.mContractedChild);
        }
        if (mContractedChild == null) {
            this.mContractedChild = null;
            this.mContractedWrapper = null;
            if (this.mTransformationStartVisibleType == 0) {
                this.mTransformationStartVisibleType = -1;
            }
            return;
        }
        this.addView(mContractedChild);
        this.mContractedChild = mContractedChild;
        this.mContractedWrapper = NotificationViewWrapper.wrap(this.getContext(), mContractedChild, this.mContainingNotification);
    }
    
    public void setExpandClickListener(final View$OnClickListener mExpandClickListener) {
        this.mExpandClickListener = mExpandClickListener;
    }
    
    public void setExpandedChild(final View mExpandedChild) {
        if (this.mExpandedChild != null) {
            this.mPreviousExpandedRemoteInputIntent = null;
            final RemoteInputView mExpandedRemoteInput = this.mExpandedRemoteInput;
            if (mExpandedRemoteInput != null) {
                mExpandedRemoteInput.onNotificationUpdateOrReset();
                if (this.mExpandedRemoteInput.isActive()) {
                    this.mPreviousExpandedRemoteInputIntent = this.mExpandedRemoteInput.getPendingIntent();
                    (this.mCachedExpandedRemoteInput = this.mExpandedRemoteInput).dispatchStartTemporaryDetach();
                    ((ViewGroup)this.mExpandedRemoteInput.getParent()).removeView((View)this.mExpandedRemoteInput);
                }
            }
            this.mOnContentViewInactiveListeners.remove((Object)this.mExpandedChild);
            this.mExpandedChild.animate().cancel();
            this.removeView(this.mExpandedChild);
            this.mExpandedRemoteInput = null;
        }
        if (mExpandedChild == null) {
            this.mExpandedChild = null;
            this.mExpandedWrapper = null;
            if (this.mTransformationStartVisibleType == 1) {
                this.mTransformationStartVisibleType = -1;
            }
            if (this.mVisibleType == 1) {
                this.selectLayout(false, true);
            }
            return;
        }
        this.addView(mExpandedChild);
        this.mExpandedChild = mExpandedChild;
        this.mExpandedWrapper = NotificationViewWrapper.wrap(this.getContext(), mExpandedChild, this.mContainingNotification);
    }
    
    public void setExpandedInflatedSmartReplies(final InflatedSmartReplies mExpandedInflatedSmartReplies) {
        this.mExpandedInflatedSmartReplies = mExpandedInflatedSmartReplies;
        if (mExpandedInflatedSmartReplies == null) {
            this.mExpandedSmartReplyView = null;
        }
    }
    
    public void setFocusOnVisibilityChange() {
        this.mFocusOnVisibilityChange = true;
    }
    
    public void setGroupManager(final NotificationGroupManager mGroupManager) {
        this.mGroupManager = mGroupManager;
    }
    
    public void setHeaderVisibleAmount(final float headerVisibleAmount) {
        final NotificationViewWrapper mContractedWrapper = this.mContractedWrapper;
        if (mContractedWrapper != null) {
            mContractedWrapper.setHeaderVisibleAmount(headerVisibleAmount);
        }
        final NotificationViewWrapper mHeadsUpWrapper = this.mHeadsUpWrapper;
        if (mHeadsUpWrapper != null) {
            mHeadsUpWrapper.setHeaderVisibleAmount(headerVisibleAmount);
        }
        final NotificationViewWrapper mExpandedWrapper = this.mExpandedWrapper;
        if (mExpandedWrapper != null) {
            mExpandedWrapper.setHeaderVisibleAmount(headerVisibleAmount);
        }
    }
    
    public void setHeadsUp(final boolean mIsHeadsUp) {
        this.mIsHeadsUp = mIsHeadsUp;
        this.selectLayout(false, true);
        this.updateExpandButtons(this.mExpandable);
    }
    
    public void setHeadsUpAnimatingAway(final boolean mHeadsUpAnimatingAway) {
        this.mHeadsUpAnimatingAway = mHeadsUpAnimatingAway;
        this.selectLayout(false, true);
    }
    
    public void setHeadsUpChild(final View mHeadsUpChild) {
        if (this.mHeadsUpChild != null) {
            this.mPreviousHeadsUpRemoteInputIntent = null;
            final RemoteInputView mHeadsUpRemoteInput = this.mHeadsUpRemoteInput;
            if (mHeadsUpRemoteInput != null) {
                mHeadsUpRemoteInput.onNotificationUpdateOrReset();
                if (this.mHeadsUpRemoteInput.isActive()) {
                    this.mPreviousHeadsUpRemoteInputIntent = this.mHeadsUpRemoteInput.getPendingIntent();
                    (this.mCachedHeadsUpRemoteInput = this.mHeadsUpRemoteInput).dispatchStartTemporaryDetach();
                    ((ViewGroup)this.mHeadsUpRemoteInput.getParent()).removeView((View)this.mHeadsUpRemoteInput);
                }
            }
            this.mOnContentViewInactiveListeners.remove((Object)this.mHeadsUpChild);
            this.mHeadsUpChild.animate().cancel();
            this.removeView(this.mHeadsUpChild);
            this.mHeadsUpRemoteInput = null;
        }
        if (mHeadsUpChild == null) {
            this.mHeadsUpChild = null;
            this.mHeadsUpWrapper = null;
            if (this.mTransformationStartVisibleType == 2) {
                this.mTransformationStartVisibleType = -1;
            }
            if (this.mVisibleType == 2) {
                this.selectLayout(false, true);
            }
            return;
        }
        this.addView(mHeadsUpChild);
        this.mHeadsUpChild = mHeadsUpChild;
        this.mHeadsUpWrapper = NotificationViewWrapper.wrap(this.getContext(), mHeadsUpChild, this.mContainingNotification);
    }
    
    public void setHeadsUpInflatedSmartReplies(final InflatedSmartReplies mHeadsUpInflatedSmartReplies) {
        this.mHeadsUpInflatedSmartReplies = mHeadsUpInflatedSmartReplies;
        if (mHeadsUpInflatedSmartReplies == null) {
            this.mHeadsUpSmartReplyView = null;
        }
    }
    
    public void setHeights(final int mSmallHeight, final int mHeadsUpHeight, final int mNotificationMaxHeight) {
        this.mSmallHeight = mSmallHeight;
        this.mHeadsUpHeight = mHeadsUpHeight;
        this.mNotificationMaxHeight = mNotificationMaxHeight;
    }
    
    public void setIsChildInGroup(final boolean b) {
        this.mIsChildInGroup = b;
        if (this.mContractedChild != null) {
            this.mContractedWrapper.setIsChildInGroup(b);
        }
        if (this.mExpandedChild != null) {
            this.mExpandedWrapper.setIsChildInGroup(this.mIsChildInGroup);
        }
        if (this.mHeadsUpChild != null) {
            this.mHeadsUpWrapper.setIsChildInGroup(this.mIsChildInGroup);
        }
        this.updateAllSingleLineViews();
    }
    
    public void setIsLowPriority(final boolean b) {
    }
    
    public void setLegacy(final boolean mLegacy) {
        this.mLegacy = mLegacy;
        this.updateLegacy();
    }
    
    public void setOnExpandedVisibleListener(final Runnable mExpandedVisibleListener) {
        this.mExpandedVisibleListener = mExpandedVisibleListener;
        this.fireExpandedVisibleListenerIfVisible();
    }
    
    public void setRecentlyAudiblyAlerted(final boolean recentlyAudiblyAlerted) {
        if (this.mContractedChild != null) {
            this.mContractedWrapper.setRecentlyAudiblyAlerted(recentlyAudiblyAlerted);
        }
        if (this.mExpandedChild != null) {
            this.mExpandedWrapper.setRecentlyAudiblyAlerted(recentlyAudiblyAlerted);
        }
        if (this.mHeadsUpChild != null) {
            this.mHeadsUpWrapper.setRecentlyAudiblyAlerted(recentlyAudiblyAlerted);
        }
    }
    
    public void setRemoteInputController(final RemoteInputController mRemoteInputController) {
        this.mRemoteInputController = mRemoteInputController;
    }
    
    public void setRemoteInputVisible(final boolean mRemoteInputVisible) {
        this.setClipChildren((this.mRemoteInputVisible = mRemoteInputVisible) ^ true);
    }
    
    public void setRemoved() {
        final RemoteInputView mExpandedRemoteInput = this.mExpandedRemoteInput;
        if (mExpandedRemoteInput != null) {
            mExpandedRemoteInput.setRemoved();
        }
        final RemoteInputView mHeadsUpRemoteInput = this.mHeadsUpRemoteInput;
        if (mHeadsUpRemoteInput != null) {
            mHeadsUpRemoteInput.setRemoved();
        }
        final NotificationViewWrapper mExpandedWrapper = this.mExpandedWrapper;
        if (mExpandedWrapper != null) {
            mExpandedWrapper.setRemoved();
            this.mMediaTransferManager.setRemoved(this.mExpandedChild);
        }
        final NotificationViewWrapper mContractedWrapper = this.mContractedWrapper;
        if (mContractedWrapper != null) {
            mContractedWrapper.setRemoved();
            this.mMediaTransferManager.setRemoved(this.mContractedChild);
        }
        final NotificationViewWrapper mHeadsUpWrapper = this.mHeadsUpWrapper;
        if (mHeadsUpWrapper != null) {
            mHeadsUpWrapper.setRemoved();
        }
    }
    
    public void setShelfIconVisible(final boolean mShelfIconVisible) {
        this.mShelfIconVisible = mShelfIconVisible;
        this.updateIconVisibilities();
    }
    
    public void setSingleLineWidthIndention(final int mSingleLineWidthIndention) {
        if (mSingleLineWidthIndention != this.mSingleLineWidthIndention) {
            this.mSingleLineWidthIndention = mSingleLineWidthIndention;
            this.mContainingNotification.forceLayout();
            this.forceLayout();
        }
    }
    
    public void setTranslationY(final float translationY) {
        super.setTranslationY(translationY);
        this.updateClipping();
    }
    
    public void setUserExpanding(final boolean mUserExpanding) {
        this.mUserExpanding = mUserExpanding;
        if (mUserExpanding) {
            this.mTransformationStartVisibleType = this.mVisibleType;
        }
        else {
            this.mTransformationStartVisibleType = -1;
            this.updateViewVisibilities(this.mVisibleType = this.calculateVisibleType());
            this.updateBackgroundColor(false);
        }
    }
    
    public boolean shouldClipToRounding(final boolean b, final boolean b2) {
        boolean shouldClipToRounding = this.shouldClipToRounding(this.getVisibleType(), b, b2);
        if (this.mUserExpanding) {
            shouldClipToRounding |= this.shouldClipToRounding(this.mTransformationStartVisibleType, b, b2);
        }
        return shouldClipToRounding;
    }
    
    public void showAppOpsIcons(final ArraySet<Integer> set) {
        if (this.mContractedChild != null) {
            this.mContractedWrapper.showAppOpsIcons(set);
        }
        if (this.mExpandedChild != null) {
            this.mExpandedWrapper.showAppOpsIcons(set);
        }
        if (this.mHeadsUpChild != null) {
            this.mHeadsUpWrapper.showAppOpsIcons(set);
        }
    }
    
    public void updateBackgroundColor(final boolean b) {
        final int backgroundColor = this.getBackgroundColor(this.mVisibleType);
        this.mContainingNotification.resetBackgroundAlpha();
        this.mContainingNotification.setContentBackground(backgroundColor, b, this);
    }
    
    public void updateExpandButtons(final boolean mExpandable) {
        this.mExpandable = mExpandable;
        final View mExpandedChild = this.mExpandedChild;
        boolean mIsContentExpandable = mExpandable;
        Label_0109: {
            if (mExpandedChild != null) {
                mIsContentExpandable = mExpandable;
                if (mExpandedChild.getHeight() != 0) {
                    if ((this.mIsHeadsUp || this.mHeadsUpAnimatingAway) && this.mHeadsUpChild != null && this.mContainingNotification.canShowHeadsUp()) {
                        mIsContentExpandable = mExpandable;
                        if (this.mExpandedChild.getHeight() > this.mHeadsUpChild.getHeight()) {
                            break Label_0109;
                        }
                    }
                    else if (this.mContractedChild != null) {
                        mIsContentExpandable = mExpandable;
                        if (this.mExpandedChild.getHeight() > this.mContractedChild.getHeight()) {
                            break Label_0109;
                        }
                    }
                    mIsContentExpandable = false;
                }
            }
        }
        if (this.mExpandedChild != null) {
            this.mExpandedWrapper.updateExpandability(mIsContentExpandable, this.mExpandClickListener);
        }
        if (this.mContractedChild != null) {
            this.mContractedWrapper.updateExpandability(mIsContentExpandable, this.mExpandClickListener);
        }
        if (this.mHeadsUpChild != null) {
            this.mHeadsUpWrapper.updateExpandability(mIsContentExpandable, this.mExpandClickListener);
        }
        this.mIsContentExpandable = mIsContentExpandable;
    }
}
