// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.bubbles;

import android.view.ViewGroup$LayoutParams;
import android.widget.LinearLayout$LayoutParams;
import com.android.systemui.R$string;
import android.view.View$OnApplyWindowInsetsListener;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.Shape;
import com.android.systemui.recents.TriangleShape;
import android.graphics.Insets;
import android.view.ViewRootImpl;
import com.android.systemui.R$id;
import android.os.RemoteException;
import android.app.ActivityTaskManager;
import android.content.res.TypedArray;
import com.android.internal.policy.ScreenDecorationsUtils;
import android.service.notification.StatusBarNotification;
import com.android.systemui.shared.system.SysUiStatsLog;
import android.view.WindowInsets;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.content.res.Resources;
import com.android.systemui.R$dimen;
import android.content.ComponentName;
import android.util.Log;
import android.content.Intent;
import android.app.ActivityOptions;
import com.android.systemui.Dependency;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.content.Context;
import android.view.WindowManager;
import android.app.ActivityView$StateCallback;
import com.android.systemui.statusbar.AlphaOptimizedButton;
import android.view.View;
import android.graphics.drawable.ShapeDrawable;
import android.app.PendingIntent;
import android.graphics.Point;
import android.app.ActivityView;
import android.view.View$OnClickListener;
import android.widget.LinearLayout;

public class BubbleExpandedView extends LinearLayout implements View$OnClickListener
{
    private ActivityView mActivityView;
    private ActivityViewStatus mActivityViewStatus;
    private Bubble mBubble;
    private BubbleController mBubbleController;
    private Point mDisplaySize;
    private boolean mIsOverflow;
    private boolean mKeyboardVisible;
    private int mMinHeight;
    private boolean mNeedsNewHeight;
    private int mOverflowHeight;
    private PendingIntent mPendingIntent;
    private ShapeDrawable mPointerDrawable;
    private int mPointerHeight;
    private int mPointerMargin;
    private View mPointerView;
    private int mPointerWidth;
    private AlphaOptimizedButton mSettingsIcon;
    private int mSettingsIconHeight;
    private BubbleStackView mStackView;
    private ActivityView$StateCallback mStateCallback;
    private int mTaskId;
    private int[] mTempLoc;
    private WindowManager mWindowManager;
    
    public BubbleExpandedView(final Context context) {
        this(context, null);
    }
    
    public BubbleExpandedView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public BubbleExpandedView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public BubbleExpandedView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mActivityViewStatus = ActivityViewStatus.INITIALIZING;
        this.mTaskId = -1;
        new Rect();
        this.mTempLoc = new int[2];
        this.mBubbleController = Dependency.get(BubbleController.class);
        this.mStateCallback = new ActivityView$StateCallback() {
            public void onActivityViewDestroyed(final ActivityView activityView) {
                BubbleExpandedView.this.mActivityViewStatus = ActivityViewStatus.RELEASED;
            }
            
            public void onActivityViewReady(final ActivityView activityView) {
                final int n = BubbleExpandedView$2.$SwitchMap$com$android$systemui$bubbles$BubbleExpandedView$ActivityViewStatus[BubbleExpandedView.this.mActivityViewStatus.ordinal()];
                if (n == 1 || n == 2) {
                    final ActivityOptions customAnimation = ActivityOptions.makeCustomAnimation(BubbleExpandedView.this.getContext(), 0, 0);
                    customAnimation.setTaskAlwaysOnTop(true);
                    BubbleExpandedView.this.post((Runnable)new _$$Lambda$BubbleExpandedView$1$g0YjNvBWtSGWit8uywvLlkarcag(this, customAnimation));
                    BubbleExpandedView.this.mActivityViewStatus = ActivityViewStatus.ACTIVITY_STARTED;
                }
            }
            
            public void onTaskCreated(final int n, final ComponentName componentName) {
                BubbleExpandedView.this.mTaskId = n;
            }
            
            public void onTaskRemovalStarted(final int n) {
                if (BubbleExpandedView.this.mBubble != null && !BubbleExpandedView.this.mBubbleController.isUserCreatedBubble(BubbleExpandedView.this.mBubble.getKey())) {
                    BubbleExpandedView.this.post((Runnable)new _$$Lambda$BubbleExpandedView$1$wFmGYWDvx1tFURTJCp8j5qJlvAk(this));
                }
            }
        };
        this.mDisplaySize = new Point();
        final WindowManager mWindowManager = (WindowManager)context.getSystemService("window");
        this.mWindowManager = mWindowManager;
        mWindowManager.getDefaultDisplay().getRealSize(this.mDisplaySize);
        final Resources resources = this.getResources();
        this.mMinHeight = resources.getDimensionPixelSize(R$dimen.bubble_expanded_default_height);
        this.mOverflowHeight = resources.getDimensionPixelSize(R$dimen.bubble_overflow_height);
        this.mPointerMargin = resources.getDimensionPixelSize(R$dimen.bubble_pointer_margin);
        resources.getDimensionPixelSize(R$dimen.bubble_expanded_view_slop);
    }
    
    private NotificationEntry getBubbleEntry() {
        final Bubble mBubble = this.mBubble;
        NotificationEntry entry;
        if (mBubble != null) {
            entry = mBubble.getEntry();
        }
        else {
            entry = null;
        }
        return entry;
    }
    
    private String getBubbleKey() {
        final Bubble mBubble = this.mBubble;
        String key;
        if (mBubble != null) {
            key = mBubble.getKey();
        }
        else {
            key = "null";
        }
        return key;
    }
    
    private int getMaxExpandedHeight() {
        this.mWindowManager.getDefaultDisplay().getRealSize(this.mDisplaySize);
        final int[] locationOnScreen = this.mActivityView.getLocationOnScreen();
        int stableInsetBottom;
        if (this.getRootWindowInsets() != null) {
            stableInsetBottom = this.getRootWindowInsets().getStableInsetBottom();
        }
        else {
            stableInsetBottom = 0;
        }
        return this.mDisplaySize.y - locationOnScreen[1] - this.mSettingsIconHeight - this.mPointerHeight - this.mPointerMargin - stableInsetBottom;
    }
    
    private void logBubbleClickEvent(final Bubble bubble, final int n) {
        final StatusBarNotification sbn = bubble.getEntry().getSbn();
        final String packageName = sbn.getPackageName();
        final String channelId = sbn.getNotification().getChannelId();
        final int id = sbn.getId();
        final BubbleStackView mStackView = this.mStackView;
        SysUiStatsLog.write(149, packageName, channelId, id, mStackView.getBubbleIndex(mStackView.getExpandedBubble()), this.mStackView.getBubbleCount(), n, this.mStackView.getNormalizedXPosition(), this.mStackView.getNormalizedYPosition(), bubble.showInShade(), bubble.isOngoing(), false);
    }
    
    private boolean usingActivityView() {
        return (this.mPendingIntent != null || this.mBubble.getShortcutInfo() != null) && this.mActivityView != null;
    }
    
    void applyThemeAttrs() {
        final TypedArray obtainStyledAttributes = super.mContext.obtainStyledAttributes(new int[] { 16844002, 16844145 });
        final int color = obtainStyledAttributes.getColor(0, -1);
        final float cornerRadius = (float)obtainStyledAttributes.getDimensionPixelSize(1, 0);
        obtainStyledAttributes.recycle();
        this.mPointerDrawable.setTint(color);
        if (this.mActivityView != null && ScreenDecorationsUtils.supportsRoundedCornersOnWindows(super.mContext.getResources())) {
            this.mActivityView.setCornerRadius(cornerRadius);
        }
    }
    
    public void cleanUpExpandedState() {
        if (this.mActivityView == null) {
            return;
        }
        final int n = BubbleExpandedView$2.$SwitchMap$com$android$systemui$bubbles$BubbleExpandedView$ActivityViewStatus[this.mActivityViewStatus.ordinal()];
        if (n == 2 || n == 3) {
            this.mActivityView.release();
        }
        if (this.mTaskId != -1) {
            try {
                ActivityTaskManager.getService().removeTask(this.mTaskId);
            }
            catch (RemoteException ex) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Failed to remove taskId ");
                sb.append(this.mTaskId);
                Log.w("Bubbles", sb.toString());
            }
            this.mTaskId = -1;
        }
        this.removeView((View)this.mActivityView);
        this.mActivityView = null;
    }
    
    public Rect getManageButtonLocationOnScreen() {
        this.mTempLoc = this.mSettingsIcon.getLocationOnScreen();
        final int[] mTempLoc = this.mTempLoc;
        return new Rect(mTempLoc[0], mTempLoc[1], mTempLoc[0] + this.mSettingsIcon.getWidth(), this.mTempLoc[1] + this.mSettingsIcon.getHeight());
    }
    
    public int getVirtualDisplayId() {
        if (this.usingActivityView()) {
            return this.mActivityView.getVirtualDisplayId();
        }
        return -1;
    }
    
    void notifyDisplayEmpty() {
        if (this.mActivityViewStatus == ActivityViewStatus.ACTIVITY_STARTED) {
            this.mActivityViewStatus = ActivityViewStatus.INITIALIZED;
        }
    }
    
    public void onClick(final View view) {
        if (this.mBubble == null) {
            return;
        }
        if (view.getId() == R$id.settings_button) {
            this.mStackView.collapseStack(new _$$Lambda$BubbleExpandedView$XQ8EtpMuqcgm2Mp9k3PIXQcqU9E(this, this.mBubble.getSettingsIntent()));
        }
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mKeyboardVisible = false;
        this.mNeedsNewHeight = false;
        final ActivityView mActivityView = this.mActivityView;
        if (mActivityView != null) {
            if (ViewRootImpl.sNewInsetsMode == 2) {
                this.mStackView.animate().setDuration(100L).translationY(0.0f);
            }
            else {
                mActivityView.setForwardedInsets(Insets.of(0, 0, 0, 0));
            }
        }
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        final Resources resources = this.getResources();
        this.mPointerView = this.findViewById(R$id.pointer_view);
        this.mPointerWidth = resources.getDimensionPixelSize(R$dimen.bubble_pointer_width);
        this.mPointerHeight = resources.getDimensionPixelSize(R$dimen.bubble_pointer_height);
        final ShapeDrawable shapeDrawable = new ShapeDrawable((Shape)TriangleShape.create((float)this.mPointerWidth, (float)this.mPointerHeight, true));
        this.mPointerDrawable = shapeDrawable;
        this.mPointerView.setBackground((Drawable)shapeDrawable);
        this.mPointerView.setVisibility(4);
        this.mSettingsIconHeight = this.getContext().getResources().getDimensionPixelSize(R$dimen.bubble_manage_button_height);
        (this.mSettingsIcon = (AlphaOptimizedButton)this.findViewById(R$id.settings_button)).setOnClickListener((View$OnClickListener)this);
        this.mActivityView = new ActivityView(super.mContext, (AttributeSet)null, 0, true);
        this.setContentVisibility(false);
        this.addView((View)this.mActivityView);
        this.bringChildToFront((View)this.mActivityView);
        this.bringChildToFront((View)this.mSettingsIcon);
        this.applyThemeAttrs();
        this.setOnApplyWindowInsetsListener((View$OnApplyWindowInsetsListener)new _$$Lambda$BubbleExpandedView$BUIzmdcN6x4TJwxemNSjSITgNeY(this));
    }
    
    boolean performBackPressIfNeeded() {
        if (!this.usingActivityView()) {
            return false;
        }
        this.mActivityView.performBackPress();
        return true;
    }
    
    void populateExpandedView() {
        if (this.usingActivityView()) {
            this.mActivityView.setCallback(this.mStateCallback);
        }
        else {
            Log.e("Bubbles", "Cannot populate expanded view.");
        }
    }
    
    void setContentVisibility(final boolean b) {
        float n;
        if (b) {
            n = 1.0f;
        }
        else {
            n = 0.0f;
        }
        this.mPointerView.setAlpha(n);
        final ActivityView mActivityView = this.mActivityView;
        if (mActivityView != null) {
            mActivityView.setAlpha(n);
        }
    }
    
    public void setOverflow(final boolean mIsOverflow) {
        this.mIsOverflow = mIsOverflow;
        this.mPendingIntent = PendingIntent.getActivity(super.mContext, 0, new Intent(super.mContext, (Class)BubbleOverflowActivity.class), 134217728);
        this.mSettingsIcon.setVisibility(8);
    }
    
    public void setPointerPosition(final float n) {
        this.mPointerView.setTranslationX(n - this.mPointerWidth / 2.0f);
        this.mPointerView.setVisibility(0);
    }
    
    void setStackView(final BubbleStackView mStackView) {
        this.mStackView = mStackView;
    }
    
    void update(final Bubble mBubble) {
        final boolean b = this.mBubble == null;
        if (!b && (mBubble == null || !mBubble.getKey().equals(this.mBubble.getKey()))) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Trying to update entry with different key, new bubble: ");
            sb.append(mBubble.getKey());
            sb.append(" old bubble: ");
            sb.append(mBubble.getKey());
            Log.w("Bubbles", sb.toString());
        }
        else {
            this.mBubble = mBubble;
            this.mSettingsIcon.setContentDescription((CharSequence)this.getResources().getString(R$string.bubbles_settings_button_description, new Object[] { mBubble.getAppName() }));
            if (b) {
                final PendingIntent bubbleIntent = this.mBubble.getBubbleIntent();
                this.mPendingIntent = bubbleIntent;
                if (bubbleIntent != null || this.mBubble.getShortcutInfo() != null) {
                    this.setContentVisibility(false);
                    this.mActivityView.setVisibility(0);
                }
            }
            this.applyThemeAttrs();
        }
    }
    
    void updateHeight() {
        if (this.usingActivityView()) {
            float max = (float)this.mOverflowHeight;
            if (!this.mIsOverflow) {
                max = Math.max(this.mBubble.getDesiredHeight(super.mContext), (float)this.mMinHeight);
            }
            final float min = Math.min(max, (float)this.getMaxExpandedHeight());
            int n;
            if (this.mIsOverflow) {
                n = this.mOverflowHeight;
            }
            else {
                n = this.mMinHeight;
            }
            final float max2 = Math.max(min, (float)n);
            final LinearLayout$LayoutParams layoutParams = (LinearLayout$LayoutParams)this.mActivityView.getLayoutParams();
            this.mNeedsNewHeight = (layoutParams.height != max2);
            if (!this.mKeyboardVisible) {
                layoutParams.height = (int)max2;
                this.mActivityView.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
                this.mNeedsNewHeight = false;
            }
        }
    }
    
    void updateInsets(final WindowInsets windowInsets) {
        if (this.usingActivityView()) {
            final int n = this.mActivityView.getLocationOnScreen()[1];
            final int height = this.mActivityView.getHeight();
            final int y = this.mDisplaySize.y;
            final int systemWindowInsetBottom = windowInsets.getSystemWindowInsetBottom();
            int safeInsetBottom;
            if (windowInsets.getDisplayCutout() != null) {
                safeInsetBottom = windowInsets.getDisplayCutout().getSafeInsetBottom();
            }
            else {
                safeInsetBottom = 0;
            }
            final int max = Math.max(n + height - (y - Math.max(systemWindowInsetBottom, safeInsetBottom)), 0);
            if (ViewRootImpl.sNewInsetsMode == 2) {
                this.mStackView.animate().setDuration(100L).translationY((float)(-max)).withEndAction((Runnable)new _$$Lambda$BubbleExpandedView$nnDy6_sXCBIclTrUTGfk9Rr_UGc(this));
            }
            else {
                this.mActivityView.setForwardedInsets(Insets.of(0, 0, 0, max));
            }
        }
    }
    
    public void updateView() {
        if (this.usingActivityView() && this.mActivityView.getVisibility() == 0 && this.mActivityView.isAttachedToWindow()) {
            this.mActivityView.onLocationChanged();
        }
        this.updateHeight();
    }
    
    private enum ActivityViewStatus
    {
        ACTIVITY_STARTED, 
        INITIALIZED, 
        INITIALIZING, 
        RELEASED;
    }
}
