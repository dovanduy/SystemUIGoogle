// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.app.PendingIntent;
import android.os.SystemClock;
import java.util.Collection;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup$LayoutParams;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.content.res.ColorStateList;
import android.graphics.drawable.RippleDrawable;
import android.app.PendingIntent$CanceledException;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import android.app.RemoteInput;
import android.view.accessibility.AccessibilityNodeInfo$AccessibilityAction;
import com.android.systemui.R$string;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.View$AccessibilityDelegate;
import com.android.internal.annotations.VisibleForTesting;
import android.view.View$OnClickListener;
import android.app.Notification$Action;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.SmartReplyController;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import java.util.Iterator;
import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import java.util.List;
import android.text.TextPaint;
import android.text.method.TransformationMethod;
import android.text.Layout;
import android.content.res.TypedArray;
import com.android.systemui.R$styleable;
import com.android.internal.util.ContrastColorUtil;
import android.graphics.Color;
import com.android.systemui.R$color;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.R$dimen;
import com.android.systemui.Dependency;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View$MeasureSpec;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil;
import android.widget.Button;
import java.util.PriorityQueue;
import java.text.BreakIterator;
import com.android.systemui.plugins.ActivityStarter;
import android.view.View;
import java.util.Comparator;
import android.view.ViewGroup;

public class SmartReplyView extends ViewGroup
{
    private static final Comparator<View> DECREASING_MEASURED_WIDTH_WITHOUT_PADDING_COMPARATOR;
    private static final int MEASURE_SPEC_ANY_LENGTH;
    private ActivityStarter mActivityStarter;
    private final BreakIterator mBreakIterator;
    private PriorityQueue<Button> mCandidateButtonQueueForSqueezing;
    private final SmartReplyConstants mConstants;
    private int mCurrentBackgroundColor;
    private final int mDefaultBackgroundColor;
    private final int mDefaultStrokeColor;
    private final int mDefaultTextColor;
    private final int mDefaultTextColorDarkBg;
    private final int mDoubleLineButtonPaddingHorizontal;
    private final int mHeightUpperLimit;
    private final KeyguardDismissUtil mKeyguardDismissUtil;
    private final double mMinStrokeContrast;
    private final NotificationRemoteInputManager mRemoteInputManager;
    private final int mRippleColor;
    private final int mRippleColorDarkBg;
    private final int mSingleLineButtonPaddingHorizontal;
    private final int mSingleToDoubleLineButtonWidthIncrease;
    private boolean mSmartRepliesGeneratedByAssistant;
    private View mSmartReplyContainer;
    private final int mSpacing;
    private final int mStrokeWidth;
    
    static {
        MEASURE_SPEC_ANY_LENGTH = View$MeasureSpec.makeMeasureSpec(0, 0);
        DECREASING_MEASURED_WIDTH_WITHOUT_PADDING_COMPARATOR = (Comparator)_$$Lambda$SmartReplyView$UA3QkbRzztEFRlbb86djKcGIV5E.INSTANCE;
    }
    
    public SmartReplyView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mSmartRepliesGeneratedByAssistant = false;
        this.mConstants = Dependency.get(SmartReplyConstants.class);
        this.mKeyguardDismissUtil = Dependency.get(KeyguardDismissUtil.class);
        this.mRemoteInputManager = Dependency.get(NotificationRemoteInputManager.class);
        this.mHeightUpperLimit = NotificationUtils.getFontScaledHeight(super.mContext, R$dimen.smart_reply_button_max_height);
        final int color = context.getColor(R$color.smart_reply_button_background);
        this.mCurrentBackgroundColor = color;
        this.mDefaultBackgroundColor = color;
        this.mDefaultTextColor = super.mContext.getColor(R$color.smart_reply_button_text);
        this.mDefaultTextColorDarkBg = super.mContext.getColor(R$color.smart_reply_button_text_dark_bg);
        this.mDefaultStrokeColor = super.mContext.getColor(R$color.smart_reply_button_stroke);
        final int color2 = super.mContext.getColor(R$color.notification_ripple_untinted_color);
        this.mRippleColor = color2;
        this.mRippleColorDarkBg = Color.argb(Color.alpha(color2), 255, 255, 255);
        this.mMinStrokeContrast = ContrastColorUtil.calculateContrast(this.mDefaultStrokeColor, this.mDefaultBackgroundColor);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.SmartReplyView, 0, 0);
        final int indexCount = obtainStyledAttributes.getIndexCount();
        final int n = 0;
        int mSpacing;
        final int n2 = mSpacing = n;
        int mDoubleLineButtonPaddingHorizontal;
        int mSingleLineButtonPaddingHorizontal = mDoubleLineButtonPaddingHorizontal = mSpacing;
        int dimensionPixelSize = n2;
        int dimensionPixelSize2;
        int dimensionPixelSize3;
        int dimensionPixelSize4;
        for (int i = n; i < indexCount; ++i, mSpacing = dimensionPixelSize2, mSingleLineButtonPaddingHorizontal = dimensionPixelSize3, mDoubleLineButtonPaddingHorizontal = dimensionPixelSize4) {
            final int index = obtainStyledAttributes.getIndex(i);
            if (index == R$styleable.SmartReplyView_spacing) {
                dimensionPixelSize2 = obtainStyledAttributes.getDimensionPixelSize(i, 0);
                dimensionPixelSize3 = mSingleLineButtonPaddingHorizontal;
                dimensionPixelSize4 = mDoubleLineButtonPaddingHorizontal;
            }
            else if (index == R$styleable.SmartReplyView_singleLineButtonPaddingHorizontal) {
                dimensionPixelSize3 = obtainStyledAttributes.getDimensionPixelSize(i, 0);
                dimensionPixelSize2 = mSpacing;
                dimensionPixelSize4 = mDoubleLineButtonPaddingHorizontal;
            }
            else if (index == R$styleable.SmartReplyView_doubleLineButtonPaddingHorizontal) {
                dimensionPixelSize4 = obtainStyledAttributes.getDimensionPixelSize(i, 0);
                dimensionPixelSize2 = mSpacing;
                dimensionPixelSize3 = mSingleLineButtonPaddingHorizontal;
            }
            else {
                dimensionPixelSize2 = mSpacing;
                dimensionPixelSize3 = mSingleLineButtonPaddingHorizontal;
                dimensionPixelSize4 = mDoubleLineButtonPaddingHorizontal;
                if (index == R$styleable.SmartReplyView_buttonStrokeWidth) {
                    dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(i, 0);
                    dimensionPixelSize4 = mDoubleLineButtonPaddingHorizontal;
                    dimensionPixelSize3 = mSingleLineButtonPaddingHorizontal;
                    dimensionPixelSize2 = mSpacing;
                }
            }
        }
        obtainStyledAttributes.recycle();
        this.mStrokeWidth = dimensionPixelSize;
        this.mSpacing = mSpacing;
        this.mSingleLineButtonPaddingHorizontal = mSingleLineButtonPaddingHorizontal;
        this.mDoubleLineButtonPaddingHorizontal = mDoubleLineButtonPaddingHorizontal;
        this.mSingleToDoubleLineButtonWidthIncrease = (mDoubleLineButtonPaddingHorizontal - mSingleLineButtonPaddingHorizontal) * 2;
        this.mBreakIterator = BreakIterator.getLineInstance();
        this.reallocateCandidateButtonQueueForSqueezing();
    }
    
    private int estimateOptimalSqueezedButtonTextWidth(final Button button) {
        String text = button.getText().toString();
        final TransformationMethod transformationMethod = button.getTransformationMethod();
        if (transformationMethod != null) {
            text = transformationMethod.getTransformation((CharSequence)text, (View)button).toString();
        }
        final int length = text.length();
        this.mBreakIterator.setText(text);
        if (this.mBreakIterator.preceding(length / 2) == -1 && this.mBreakIterator.next() == -1) {
            return -1;
        }
        final TextPaint paint = button.getPaint();
        final int current = this.mBreakIterator.current();
        final float desiredWidth = Layout.getDesiredWidth((CharSequence)text, 0, current, paint);
        final float desiredWidth2 = Layout.getDesiredWidth((CharSequence)text, current, length, paint);
        float max = Math.max(desiredWidth, desiredWidth2);
        final float n = fcmpl(desiredWidth, desiredWidth2);
        float n2 = max;
        if (n != 0) {
            final boolean b = n > 0;
            final int maxSqueezeRemeasureAttempts = this.mConstants.getMaxSqueezeRemeasureAttempts();
            int n3 = 0;
            while (true) {
                n2 = max;
                if (n3 >= maxSqueezeRemeasureAttempts) {
                    break;
                }
                final BreakIterator mBreakIterator = this.mBreakIterator;
                int n4;
                if (b) {
                    n4 = mBreakIterator.previous();
                }
                else {
                    n4 = mBreakIterator.next();
                }
                if (n4 == -1) {
                    n2 = max;
                    break;
                }
                final float desiredWidth3 = Layout.getDesiredWidth((CharSequence)text, 0, n4, paint);
                final float desiredWidth4 = Layout.getDesiredWidth((CharSequence)text, n4, length, paint);
                final float max2 = Math.max(desiredWidth3, desiredWidth4);
                n2 = max;
                if (max2 >= max) {
                    break;
                }
                if (b ? (desiredWidth3 <= desiredWidth4) : (desiredWidth3 >= desiredWidth4)) {
                    n2 = max2;
                    break;
                }
                ++n3;
                max = max2;
            }
        }
        return (int)Math.ceil(n2);
    }
    
    private List<View> filterActionsOrReplies(final SmartButtonType smartButtonType) {
        final ArrayList<View> list = new ArrayList<View>();
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
            if (child.getVisibility() == 0) {
                if (child instanceof Button) {
                    if (layoutParams.buttonType == smartButtonType) {
                        list.add(child);
                    }
                }
            }
        }
        return list;
    }
    
    private ActivityStarter getActivityStarter() {
        if (this.mActivityStarter == null) {
            this.mActivityStarter = Dependency.get(ActivityStarter.class);
        }
        return this.mActivityStarter;
    }
    
    private int getLeftCompoundDrawableWidthWithPadding(final Button button) {
        final Drawable drawable = button.getCompoundDrawables()[0];
        if (drawable == null) {
            return 0;
        }
        return drawable.getBounds().width() + button.getCompoundDrawablePadding();
    }
    
    private boolean gotEnoughSmartReplies(final List<View> list) {
        final Iterator<View> iterator = list.iterator();
        int n = 0;
        while (iterator.hasNext()) {
            if (((LayoutParams)iterator.next().getLayoutParams()).show) {
                ++n;
            }
        }
        return n == 0 || n >= this.mConstants.getMinNumSystemGeneratedReplies();
    }
    
    public static SmartReplyView inflate(final Context context) {
        return (SmartReplyView)LayoutInflater.from(context).inflate(R$layout.smart_reply_view, (ViewGroup)null);
    }
    
    @VisibleForTesting
    static Button inflateActionButton(final SmartReplyView smartReplyView, final Context context, final Context context2, final int n, final SmartActions smartActions, final SmartReplyController smartReplyController, final NotificationEntry notificationEntry, final HeadsUpManager headsUpManager, final boolean b) {
        final Notification$Action notification$Action = smartActions.actions.get(n);
        final Button button = (Button)LayoutInflater.from(context).inflate(R$layout.smart_action_button, (ViewGroup)smartReplyView, false);
        button.setText(notification$Action.title);
        final Drawable loadDrawable = notification$Action.getIcon().loadDrawable(context2);
        final int dimensionPixelSize = context.getResources().getDimensionPixelSize(R$dimen.smart_action_button_icon_size);
        loadDrawable.setBounds(0, 0, dimensionPixelSize, dimensionPixelSize);
        button.setCompoundDrawables(loadDrawable, (Drawable)null, (Drawable)null, (Drawable)null);
        Object onClickListener;
        final _$$Lambda$SmartReplyView$tct0o0Zp_9czv90IHtUOrdcaxl0 $$Lambda$SmartReplyView$tct0o0Zp_9czv90IHtUOrdcaxl0 = (_$$Lambda$SmartReplyView$tct0o0Zp_9czv90IHtUOrdcaxl0)(onClickListener = new _$$Lambda$SmartReplyView$tct0o0Zp_9czv90IHtUOrdcaxl0(smartReplyView, notification$Action, smartReplyController, notificationEntry, n, smartActions, headsUpManager));
        if (b) {
            onClickListener = new DelayedOnClickListener((View$OnClickListener)$$Lambda$SmartReplyView$tct0o0Zp_9czv90IHtUOrdcaxl0, smartReplyView.mConstants.getOnClickInitDelay());
        }
        button.setOnClickListener((View$OnClickListener)onClickListener);
        ((LayoutParams)button.getLayoutParams()).buttonType = SmartButtonType.ACTION;
        return button;
    }
    
    @VisibleForTesting
    static Button inflateReplyButton(final SmartReplyView smartReplyView, final Context context, final int n, final SmartReplies smartReplies, final SmartReplyController smartReplyController, final NotificationEntry notificationEntry, final boolean b) {
        final Button button = (Button)LayoutInflater.from(context).inflate(R$layout.smart_reply_button, (ViewGroup)smartReplyView, false);
        final CharSequence text = smartReplies.choices.get(n);
        button.setText(text);
        Object onClickListener;
        final _$$Lambda$SmartReplyView$zCSq2JAz_cY64WTEY4XQsF_yGXs $$Lambda$SmartReplyView$zCSq2JAz_cY64WTEY4XQsF_yGXs = (_$$Lambda$SmartReplyView$zCSq2JAz_cY64WTEY4XQsF_yGXs)(onClickListener = new _$$Lambda$SmartReplyView$zCSq2JAz_cY64WTEY4XQsF_yGXs(smartReplyView, new _$$Lambda$SmartReplyView$rVuoX0krA_dMy7xAwdbzCHW8AzI(smartReplyView, smartReplies, text, n, button, smartReplyController, notificationEntry, context), notificationEntry));
        if (b) {
            onClickListener = new DelayedOnClickListener((View$OnClickListener)$$Lambda$SmartReplyView$zCSq2JAz_cY64WTEY4XQsF_yGXs, smartReplyView.mConstants.getOnClickInitDelay());
        }
        button.setOnClickListener((View$OnClickListener)onClickListener);
        button.setAccessibilityDelegate((View$AccessibilityDelegate)new View$AccessibilityDelegate() {
            public void onInitializeAccessibilityNodeInfo(final View view, final AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                accessibilityNodeInfo.addAction(new AccessibilityNodeInfo$AccessibilityAction(16, (CharSequence)smartReplyView.getResources().getString(R$string.accessibility_send_smart_reply)));
            }
        });
        setButtonColors(button, smartReplyView.mCurrentBackgroundColor, smartReplyView.mDefaultStrokeColor, smartReplyView.mDefaultTextColor, smartReplyView.mRippleColor, smartReplyView.mStrokeWidth);
        return button;
    }
    
    private void markButtonsWithPendingSqueezeStatusAs(final int n, final List<View> list) {
        final Iterator<View> iterator = list.iterator();
        while (iterator.hasNext()) {
            final LayoutParams layoutParams = (LayoutParams)iterator.next().getLayoutParams();
            if (layoutParams.squeezeStatus == 1) {
                layoutParams.squeezeStatus = n;
            }
        }
    }
    
    private void reallocateCandidateButtonQueueForSqueezing() {
        this.mCandidateButtonQueueForSqueezing = new PriorityQueue<Button>(Math.max(this.getChildCount(), 1), (Comparator<? super Button>)SmartReplyView.DECREASING_MEASURED_WIDTH_WITHOUT_PADDING_COMPARATOR);
    }
    
    private void remeasureButtonsIfNecessary(final int n, final int n2) {
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(n2, 1073741824);
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
            if (layoutParams.show) {
                int measuredWidth = child.getMeasuredWidth();
                final int access$400 = layoutParams.squeezeStatus;
                final int n3 = 1;
                int n4;
                if (access$400 == 3) {
                    measuredWidth = Integer.MAX_VALUE;
                    n4 = 1;
                }
                else {
                    n4 = 0;
                }
                int n5 = measuredWidth;
                if (child.getPaddingLeft() != n) {
                    int n6;
                    if ((n6 = measuredWidth) != Integer.MAX_VALUE) {
                        if (n == this.mSingleLineButtonPaddingHorizontal) {
                            n6 = measuredWidth - this.mSingleToDoubleLineButtonWidthIncrease;
                        }
                        else {
                            n6 = measuredWidth + this.mSingleToDoubleLineButtonWidthIncrease;
                        }
                    }
                    child.setPadding(n, child.getPaddingTop(), n, child.getPaddingBottom());
                    final int n7 = 1;
                    n5 = n6;
                    n4 = n7;
                }
                if (child.getMeasuredHeight() != n2) {
                    n4 = n3;
                }
                if (n4 != 0) {
                    child.measure(View$MeasureSpec.makeMeasureSpec(n5, Integer.MIN_VALUE), measureSpec);
                }
            }
        }
    }
    
    private void resetButtonsLayoutParams() {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final LayoutParams layoutParams = (LayoutParams)this.getChildAt(i).getLayoutParams();
            layoutParams.show = false;
            layoutParams.squeezeStatus = 0;
        }
    }
    
    private static void setButtonColors(final Button button, final int color, final int n, final int textColor, final int n2, final int n3) {
        final Drawable background = button.getBackground();
        if (background instanceof RippleDrawable) {
            final Drawable mutate = background.mutate();
            final RippleDrawable rippleDrawable = (RippleDrawable)mutate;
            rippleDrawable.setColor(ColorStateList.valueOf(n2));
            final Drawable drawable = rippleDrawable.getDrawable(0);
            if (drawable instanceof InsetDrawable) {
                final Drawable drawable2 = ((InsetDrawable)drawable).getDrawable();
                if (drawable2 instanceof GradientDrawable) {
                    final GradientDrawable gradientDrawable = (GradientDrawable)drawable2;
                    gradientDrawable.setColor(color);
                    gradientDrawable.setStroke(n3, n);
                }
            }
            button.setBackground(mutate);
        }
        button.setTextColor(textColor);
    }
    
    private void setCornerRadius(final Button button, final float cornerRadius) {
        final Drawable background = button.getBackground();
        if (background instanceof RippleDrawable) {
            final Drawable drawable = ((RippleDrawable)background.mutate()).getDrawable(0);
            if (drawable instanceof InsetDrawable) {
                final Drawable drawable2 = ((InsetDrawable)drawable).getDrawable();
                if (drawable2 instanceof GradientDrawable) {
                    ((GradientDrawable)drawable2).setCornerRadius(cornerRadius);
                }
            }
        }
    }
    
    private int squeezeButton(final Button button, final int n) {
        final int estimateOptimalSqueezedButtonTextWidth = this.estimateOptimalSqueezedButtonTextWidth(button);
        if (estimateOptimalSqueezedButtonTextWidth == -1) {
            return -1;
        }
        return this.squeezeButtonToTextWidth(button, n, estimateOptimalSqueezedButtonTextWidth);
    }
    
    private int squeezeButtonToTextWidth(final Button button, int measuredWidth, final int n) {
        int measuredWidth2;
        final int n2 = measuredWidth2 = button.getMeasuredWidth();
        if (button.getPaddingLeft() != this.mDoubleLineButtonPaddingHorizontal) {
            measuredWidth2 = n2 + this.mSingleToDoubleLineButtonWidthIncrease;
        }
        button.setPadding(this.mDoubleLineButtonPaddingHorizontal, button.getPaddingTop(), this.mDoubleLineButtonPaddingHorizontal, button.getPaddingBottom());
        button.measure(View$MeasureSpec.makeMeasureSpec(this.mDoubleLineButtonPaddingHorizontal * 2 + n + this.getLeftCompoundDrawableWidthWithPadding(button), Integer.MIN_VALUE), measuredWidth);
        measuredWidth = button.getMeasuredWidth();
        final LayoutParams layoutParams = (LayoutParams)button.getLayoutParams();
        if (button.getLineCount() <= 2 && measuredWidth < measuredWidth2) {
            layoutParams.squeezeStatus = 1;
            return measuredWidth2 - measuredWidth;
        }
        layoutParams.squeezeStatus = 3;
        return -1;
    }
    
    public void addPreInflatedButtons(final List<Button> list) {
        final Iterator<Button> iterator = list.iterator();
        while (iterator.hasNext()) {
            this.addView((View)iterator.next());
        }
        this.reallocateCandidateButtonQueueForSqueezing();
    }
    
    protected boolean drawChild(final Canvas canvas, final View view, final long n) {
        return ((LayoutParams)view.getLayoutParams()).show && super.drawChild(canvas, view, n);
    }
    
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }
    
    protected ViewGroup$LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return new LayoutParams(viewGroup$LayoutParams.width, viewGroup$LayoutParams.height);
    }
    
    public LayoutParams generateLayoutParams(final AttributeSet set) {
        return new LayoutParams(super.mContext, set);
    }
    
    public int getHeightUpperLimit() {
        return this.mHeightUpperLimit;
    }
    
    public List<Button> inflateRepliesFromRemoteInput(final SmartReplies smartReplies, final SmartReplyController smartReplyController, final NotificationEntry notificationEntry, final boolean b) {
        final ArrayList<Button> list = new ArrayList<Button>();
        if (smartReplies.remoteInput != null && smartReplies.pendingIntent != null && smartReplies.choices != null) {
            for (int i = 0; i < smartReplies.choices.size(); ++i) {
                list.add(inflateReplyButton(this, this.getContext(), i, smartReplies, smartReplyController, notificationEntry, b));
            }
            this.mSmartRepliesGeneratedByAssistant = smartReplies.fromAssistant;
        }
        return list;
    }
    
    public List<Button> inflateSmartActions(final Context context, final SmartActions smartActions, final SmartReplyController smartReplyController, final NotificationEntry notificationEntry, final HeadsUpManager headsUpManager, final boolean b) {
        final ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, super.mContext.getTheme());
        final ArrayList<Button> list = new ArrayList<Button>();
        for (int size = smartActions.actions.size(), i = 0; i < size; ++i) {
            if (smartActions.actions.get(i).actionIntent != null) {
                list.add(inflateActionButton(this, this.getContext(), (Context)contextThemeWrapper, i, smartActions, smartReplyController, notificationEntry, headsUpManager, b));
            }
        }
        return list;
    }
    
    protected void onLayout(final boolean b, int mPaddingLeft, int n, int i, int layoutDirection) {
        layoutDirection = this.getLayoutDirection();
        n = 1;
        if (layoutDirection != 1) {
            n = 0;
        }
        if (n != 0) {
            mPaddingLeft = i - mPaddingLeft - super.mPaddingRight;
        }
        else {
            mPaddingLeft = super.mPaddingLeft;
        }
        int childCount;
        View child;
        int measuredWidth;
        int measuredHeight;
        for (childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            child = this.getChildAt(i);
            if (((LayoutParams)child.getLayoutParams()).show) {
                measuredWidth = child.getMeasuredWidth();
                measuredHeight = child.getMeasuredHeight();
                if (n != 0) {
                    layoutDirection = mPaddingLeft - measuredWidth;
                }
                else {
                    layoutDirection = mPaddingLeft;
                }
                child.layout(layoutDirection, 0, layoutDirection + measuredWidth, measuredHeight);
                layoutDirection = measuredWidth + this.mSpacing;
                if (n != 0) {
                    mPaddingLeft -= layoutDirection;
                }
                else {
                    mPaddingLeft += layoutDirection;
                }
            }
        }
    }
    
    protected void onMeasure(final int n, final int n2) {
        int size;
        if (View$MeasureSpec.getMode(n) == 0) {
            size = Integer.MAX_VALUE;
        }
        else {
            size = View$MeasureSpec.getSize(n);
        }
        this.resetButtonsLayoutParams();
        if (!this.mCandidateButtonQueueForSqueezing.isEmpty()) {
            Log.wtf("SmartReplyView", "Single line button queue leaked between onMeasure calls");
            this.mCandidateButtonQueueForSqueezing.clear();
        }
        SmartSuggestionMeasures smartSuggestionMeasures = new SmartSuggestionMeasures(super.mPaddingLeft + super.mPaddingRight, 0, this.mSingleLineButtonPaddingHorizontal);
        final List<View> filterActionsOrReplies = this.filterActionsOrReplies(SmartButtonType.ACTION);
        final List<View> filterActionsOrReplies2 = this.filterActionsOrReplies(SmartButtonType.REPLY);
        final ArrayList list = new ArrayList<View>(filterActionsOrReplies);
        list.addAll((Collection<?>)filterActionsOrReplies2);
        final ArrayList<View> list2 = new ArrayList<View>();
        SmartSuggestionMeasures smartSuggestionMeasures2 = null;
        final int maxNumActions = this.mConstants.getMaxNumActions();
        final Iterator<View> iterator = (Iterator<View>)list.iterator();
        int n4;
        int n3 = n4 = 0;
        while (iterator.hasNext()) {
            final View view = iterator.next();
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            SmartSuggestionMeasures smartSuggestionMeasures3;
            int n5;
            int n6;
            if (maxNumActions != -1 && layoutParams.buttonType == SmartButtonType.ACTION && n3 >= maxNumActions) {
                smartSuggestionMeasures3 = smartSuggestionMeasures2;
                n5 = n3;
                n6 = n4;
            }
            else {
                view.setPadding(smartSuggestionMeasures.mButtonPaddingHorizontal, view.getPaddingTop(), smartSuggestionMeasures.mButtonPaddingHorizontal, view.getPaddingBottom());
                view.measure(SmartReplyView.MEASURE_SPEC_ANY_LENGTH, n2);
                list2.add(view);
                final Button e = (Button)view;
                final int lineCount = e.getLineCount();
                smartSuggestionMeasures3 = smartSuggestionMeasures2;
                n5 = n3;
                n6 = n4;
                if (lineCount >= 1) {
                    if (lineCount > 2) {
                        smartSuggestionMeasures3 = smartSuggestionMeasures2;
                        n5 = n3;
                        n6 = n4;
                    }
                    else {
                        if (lineCount == 1) {
                            this.mCandidateButtonQueueForSqueezing.add(e);
                        }
                        final SmartSuggestionMeasures clone = smartSuggestionMeasures.clone();
                        SmartSuggestionMeasures clone2;
                        if ((clone2 = smartSuggestionMeasures2) == null) {
                            clone2 = smartSuggestionMeasures2;
                            if (layoutParams.buttonType == SmartButtonType.REPLY) {
                                clone2 = smartSuggestionMeasures.clone();
                            }
                        }
                        int mSpacing;
                        if (n4 == 0) {
                            mSpacing = 0;
                        }
                        else {
                            mSpacing = this.mSpacing;
                        }
                        final int measuredWidth = view.getMeasuredWidth();
                        final int measuredHeight = view.getMeasuredHeight();
                        smartSuggestionMeasures.mMeasuredWidth += mSpacing + measuredWidth;
                        smartSuggestionMeasures.mMaxChildHeight = Math.max(smartSuggestionMeasures.mMaxChildHeight, measuredHeight);
                        if (smartSuggestionMeasures.mButtonPaddingHorizontal == this.mSingleLineButtonPaddingHorizontal && (lineCount == 2 || smartSuggestionMeasures.mMeasuredWidth > size)) {
                            smartSuggestionMeasures.mMeasuredWidth += (n4 + 1) * this.mSingleToDoubleLineButtonWidthIncrease;
                            smartSuggestionMeasures.mButtonPaddingHorizontal = this.mDoubleLineButtonPaddingHorizontal;
                        }
                        if (smartSuggestionMeasures.mMeasuredWidth > size) {
                            while (smartSuggestionMeasures.mMeasuredWidth > size && !this.mCandidateButtonQueueForSqueezing.isEmpty()) {
                                final Button button = this.mCandidateButtonQueueForSqueezing.poll();
                                final int squeezeButton = this.squeezeButton(button, n2);
                                if (squeezeButton != -1) {
                                    smartSuggestionMeasures.mMaxChildHeight = Math.max(smartSuggestionMeasures.mMaxChildHeight, button.getMeasuredHeight());
                                    smartSuggestionMeasures.mMeasuredWidth -= squeezeButton;
                                }
                            }
                            if (smartSuggestionMeasures.mMeasuredWidth > size) {
                                this.markButtonsWithPendingSqueezeStatusAs(3, list2);
                                smartSuggestionMeasures = clone;
                                smartSuggestionMeasures2 = clone2;
                                continue;
                            }
                            this.markButtonsWithPendingSqueezeStatusAs(2, list2);
                        }
                        layoutParams.show = true;
                        ++n4;
                        smartSuggestionMeasures3 = clone2;
                        n5 = n3;
                        n6 = n4;
                        if (layoutParams.buttonType == SmartButtonType.ACTION) {
                            n5 = n3 + 1;
                            n6 = n4;
                            smartSuggestionMeasures3 = clone2;
                        }
                    }
                }
            }
            n4 = n6;
            n3 = n5;
            smartSuggestionMeasures2 = smartSuggestionMeasures3;
        }
        SmartSuggestionMeasures smartSuggestionMeasures4 = smartSuggestionMeasures;
        if (this.mSmartRepliesGeneratedByAssistant) {
            smartSuggestionMeasures4 = smartSuggestionMeasures;
            if (!this.gotEnoughSmartReplies(filterActionsOrReplies2)) {
                final Iterator<View> iterator2 = filterActionsOrReplies2.iterator();
                while (iterator2.hasNext()) {
                    ((LayoutParams)iterator2.next().getLayoutParams()).show = false;
                }
                smartSuggestionMeasures4 = smartSuggestionMeasures2;
            }
        }
        this.mCandidateButtonQueueForSqueezing.clear();
        this.remeasureButtonsIfNecessary(smartSuggestionMeasures4.mButtonPaddingHorizontal, smartSuggestionMeasures4.mMaxChildHeight);
        final int max = Math.max(this.getSuggestedMinimumHeight(), super.mPaddingTop + smartSuggestionMeasures4.mMaxChildHeight + super.mPaddingBottom);
        final Iterator<View> iterator3 = (Iterator<View>)list.iterator();
        while (iterator3.hasNext()) {
            this.setCornerRadius((Button)iterator3.next(), max / 2.0f);
        }
        this.setMeasuredDimension(ViewGroup.resolveSize(Math.max(this.getSuggestedMinimumWidth(), smartSuggestionMeasures4.mMeasuredWidth), n), ViewGroup.resolveSize(max, n2));
    }
    
    public void resetSmartSuggestions(final View mSmartReplyContainer) {
        this.mSmartReplyContainer = mSmartReplyContainer;
        this.removeAllViews();
        this.mCurrentBackgroundColor = this.mDefaultBackgroundColor;
    }
    
    public void setBackgroundTintColor(final int mCurrentBackgroundColor) {
        if (mCurrentBackgroundColor == this.mCurrentBackgroundColor) {
            return;
        }
        this.mCurrentBackgroundColor = mCurrentBackgroundColor;
        final boolean b = ContrastColorUtil.isColorLight(mCurrentBackgroundColor) ^ true;
        int n;
        if (b) {
            n = this.mDefaultTextColorDarkBg;
        }
        else {
            n = this.mDefaultTextColor;
        }
        final int n2 = 0xFF000000 | mCurrentBackgroundColor;
        final int ensureTextContrast = ContrastColorUtil.ensureTextContrast(n, n2, b);
        final int ensureContrast = ContrastColorUtil.ensureContrast(this.mDefaultStrokeColor, n2, b, this.mMinStrokeContrast);
        int n3;
        if (b) {
            n3 = this.mRippleColorDarkBg;
        }
        else {
            n3 = this.mRippleColor;
        }
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            setButtonColors((Button)this.getChildAt(i), mCurrentBackgroundColor, ensureContrast, ensureTextContrast, n3, this.mStrokeWidth);
        }
    }
    
    private static class DelayedOnClickListener implements View$OnClickListener
    {
        private final View$OnClickListener mActualListener;
        private final long mInitDelayMs;
        private final long mInitTimeMs;
        
        DelayedOnClickListener(final View$OnClickListener mActualListener, final long mInitDelayMs) {
            this.mActualListener = mActualListener;
            this.mInitDelayMs = mInitDelayMs;
            this.mInitTimeMs = SystemClock.elapsedRealtime();
        }
        
        private boolean hasFinishedInitialization() {
            return SystemClock.elapsedRealtime() >= this.mInitTimeMs + this.mInitDelayMs;
        }
        
        public void onClick(final View view) {
            if (this.hasFinishedInitialization()) {
                this.mActualListener.onClick(view);
            }
            else {
                final StringBuilder sb = new StringBuilder();
                sb.append("Accidental Smart Suggestion click registered, delay: ");
                sb.append(this.mInitDelayMs);
                Log.i("SmartReplyView", sb.toString());
            }
        }
    }
    
    @VisibleForTesting
    static class LayoutParams extends ViewGroup$LayoutParams
    {
        private SmartButtonType buttonType;
        private boolean show;
        private int squeezeStatus;
        
        private LayoutParams(final int n, final int n2) {
            super(n, n2);
            this.show = false;
            this.squeezeStatus = 0;
            this.buttonType = SmartButtonType.REPLY;
        }
        
        private LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
            this.show = false;
            this.squeezeStatus = 0;
            this.buttonType = SmartButtonType.REPLY;
        }
        
        @VisibleForTesting
        boolean isShown() {
            return this.show;
        }
    }
    
    public static class SmartActions
    {
        public final List<Notification$Action> actions;
        public final boolean fromAssistant;
        
        public SmartActions(final List<Notification$Action> actions, final boolean fromAssistant) {
            this.actions = actions;
            this.fromAssistant = fromAssistant;
        }
    }
    
    private enum SmartButtonType
    {
        ACTION, 
        REPLY;
    }
    
    public static class SmartReplies
    {
        public final List<CharSequence> choices;
        public final boolean fromAssistant;
        public final PendingIntent pendingIntent;
        public final RemoteInput remoteInput;
        
        public SmartReplies(final List<CharSequence> choices, final RemoteInput remoteInput, final PendingIntent pendingIntent, final boolean fromAssistant) {
            this.choices = choices;
            this.remoteInput = remoteInput;
            this.pendingIntent = pendingIntent;
            this.fromAssistant = fromAssistant;
        }
    }
    
    private static class SmartSuggestionMeasures
    {
        int mButtonPaddingHorizontal;
        int mMaxChildHeight;
        int mMeasuredWidth;
        
        SmartSuggestionMeasures(final int mMeasuredWidth, final int mMaxChildHeight, final int mButtonPaddingHorizontal) {
            this.mMeasuredWidth = -1;
            this.mMaxChildHeight = -1;
            this.mButtonPaddingHorizontal = -1;
            this.mMeasuredWidth = mMeasuredWidth;
            this.mMaxChildHeight = mMaxChildHeight;
            this.mButtonPaddingHorizontal = mButtonPaddingHorizontal;
        }
        
        public SmartSuggestionMeasures clone() {
            return new SmartSuggestionMeasures(this.mMeasuredWidth, this.mMaxChildHeight, this.mButtonPaddingHorizontal);
        }
    }
}
