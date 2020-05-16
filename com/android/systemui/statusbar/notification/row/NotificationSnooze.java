// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityEvent;
import com.android.systemui.R$dimen;
import com.android.systemui.R$array;
import com.android.systemui.R$integer;
import android.util.Log;
import android.provider.Settings$Global;
import java.util.ArrayList;
import com.android.internal.annotations.VisibleForTesting;
import java.util.concurrent.TimeUnit;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.content.res.Resources;
import android.text.style.StyleSpan;
import android.text.SpannableString;
import android.service.notification.SnoozeCriterion;
import android.view.accessibility.AccessibilityNodeInfo$AccessibilityAction;
import com.android.systemui.R$string;
import com.android.systemui.R$plurals;
import android.view.animation.Interpolator;
import android.util.Property;
import android.animation.Animator$AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.R$id;
import java.util.List;
import android.view.ViewGroup;
import android.widget.TextView;
import android.service.notification.StatusBarNotification;
import android.util.KeyValueListParser;
import com.android.internal.logging.MetricsLogger;
import android.widget.ImageView;
import android.animation.AnimatorSet;
import android.view.View;
import com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper;
import android.metrics.LogMaker;
import android.view.View$OnClickListener;
import android.widget.LinearLayout;

public class NotificationSnooze extends LinearLayout implements GutsContent, View$OnClickListener
{
    private static final LogMaker OPTIONS_CLOSE_LOG;
    private static final LogMaker OPTIONS_OPEN_LOG;
    private static final LogMaker UNDO_LOG;
    private static final int[] sAccessibilityActions;
    private int mCollapsedHeight;
    private NotificationSwipeActionHelper.SnoozeOption mDefaultOption;
    private View mDivider;
    private AnimatorSet mExpandAnimation;
    private ImageView mExpandButton;
    private boolean mExpanded;
    private NotificationGuts mGutsContainer;
    private MetricsLogger mMetricsLogger;
    private KeyValueListParser mParser;
    private StatusBarNotification mSbn;
    private NotificationSwipeActionHelper.SnoozeOption mSelectedOption;
    private TextView mSelectedOptionText;
    private NotificationSwipeActionHelper mSnoozeListener;
    private ViewGroup mSnoozeOptionContainer;
    private List<NotificationSwipeActionHelper.SnoozeOption> mSnoozeOptions;
    private boolean mSnoozing;
    private TextView mUndoButton;
    
    static {
        OPTIONS_OPEN_LOG = new LogMaker(1142).setType(1);
        OPTIONS_CLOSE_LOG = new LogMaker(1142).setType(2);
        UNDO_LOG = new LogMaker(1141).setType(4);
        sAccessibilityActions = new int[] { R$id.action_snooze_shorter, R$id.action_snooze_short, R$id.action_snooze_long, R$id.action_snooze_longer };
    }
    
    public NotificationSnooze(final Context context, final AttributeSet set) {
        super(context, set);
        this.mMetricsLogger = new MetricsLogger();
        this.mParser = new KeyValueListParser(',');
    }
    
    private void animateSnoozeOptions(final boolean b) {
        final Property alpha = View.ALPHA;
        final AnimatorSet mExpandAnimation = this.mExpandAnimation;
        if (mExpandAnimation != null) {
            mExpandAnimation.cancel();
        }
        final View mDivider = this.mDivider;
        final float alpha2 = mDivider.getAlpha();
        final float n = 1.0f;
        float n2;
        if (b) {
            n2 = 1.0f;
        }
        else {
            n2 = 0.0f;
        }
        final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)mDivider, alpha, new float[] { alpha2, n2 });
        final ViewGroup mSnoozeOptionContainer = this.mSnoozeOptionContainer;
        final float alpha3 = mSnoozeOptionContainer.getAlpha();
        float n3;
        if (b) {
            n3 = n;
        }
        else {
            n3 = 0.0f;
        }
        final ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat((Object)mSnoozeOptionContainer, alpha, new float[] { alpha3, n3 });
        this.mSnoozeOptionContainer.setVisibility(0);
        (this.mExpandAnimation = new AnimatorSet()).playTogether(new Animator[] { (Animator)ofFloat, (Animator)ofFloat2 });
        this.mExpandAnimation.setDuration(150L);
        final AnimatorSet mExpandAnimation2 = this.mExpandAnimation;
        Interpolator interpolator;
        if (b) {
            interpolator = Interpolators.ALPHA_IN;
        }
        else {
            interpolator = Interpolators.ALPHA_OUT;
        }
        mExpandAnimation2.setInterpolator((TimeInterpolator)interpolator);
        this.mExpandAnimation.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            boolean cancelled = false;
            
            public void onAnimationCancel(final Animator animator) {
                this.cancelled = true;
            }
            
            public void onAnimationEnd(final Animator animator) {
                if (!b && !this.cancelled) {
                    NotificationSnooze.this.mSnoozeOptionContainer.setVisibility(4);
                    NotificationSnooze.this.mSnoozeOptionContainer.setAlpha(0.0f);
                }
            }
        });
        this.mExpandAnimation.start();
    }
    
    private NotificationSwipeActionHelper.SnoozeOption createOption(final int n, int index) {
        final Resources resources = this.getResources();
        final boolean b = n >= 60;
        int n2;
        if (b) {
            n2 = R$plurals.snoozeHourOptions;
        }
        else {
            n2 = R$plurals.snoozeMinuteOptions;
        }
        int i;
        if (b) {
            i = n / 60;
        }
        else {
            i = n;
        }
        final String quantityString = resources.getQuantityString(n2, i, new Object[] { i });
        final String format = String.format(resources.getString(R$string.snoozed_for_time), quantityString);
        final AccessibilityNodeInfo$AccessibilityAction accessibilityNodeInfo$AccessibilityAction = new AccessibilityNodeInfo$AccessibilityAction(index, (CharSequence)quantityString);
        index = format.indexOf(quantityString);
        if (index == -1) {
            return new NotificationSnoozeOption(null, n, quantityString, format, accessibilityNodeInfo$AccessibilityAction);
        }
        final SpannableString spannableString = new SpannableString((CharSequence)format);
        spannableString.setSpan((Object)new StyleSpan(1), index, quantityString.length() + index, 0);
        return new NotificationSnoozeOption(null, n, quantityString, (CharSequence)spannableString, accessibilityNodeInfo$AccessibilityAction);
    }
    
    private void createOptionViews() {
        this.mSnoozeOptionContainer.removeAllViews();
        final LayoutInflater layoutInflater = (LayoutInflater)this.getContext().getSystemService("layout_inflater");
        for (int i = 0; i < this.mSnoozeOptions.size(); ++i) {
            final NotificationSwipeActionHelper.SnoozeOption tag = this.mSnoozeOptions.get(i);
            final TextView textView = (TextView)layoutInflater.inflate(R$layout.notification_snooze_option, this.mSnoozeOptionContainer, false);
            this.mSnoozeOptionContainer.addView((View)textView);
            textView.setText(tag.getDescription());
            textView.setTag((Object)tag);
            textView.setOnClickListener((View$OnClickListener)this);
        }
    }
    
    private void hideSelectedOption() {
        for (int childCount = this.mSnoozeOptionContainer.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.mSnoozeOptionContainer.getChildAt(i);
            int visibility;
            if (child.getTag() == this.mSelectedOption) {
                visibility = 8;
            }
            else {
                visibility = 0;
            }
            child.setVisibility(visibility);
        }
    }
    
    private void logOptionSelection(final int n, final NotificationSwipeActionHelper.SnoozeOption snoozeOption) {
        this.mMetricsLogger.write(new LogMaker(n).setType(4).addTaggedData(1140, (Object)this.mSnoozeOptions.indexOf(snoozeOption)).addTaggedData(1139, (Object)TimeUnit.MINUTES.toMillis(snoozeOption.getMinutesToSnoozeFor())));
    }
    
    private void setSelected(final NotificationSwipeActionHelper.SnoozeOption mSelectedOption, final boolean b) {
        this.mSelectedOption = mSelectedOption;
        this.mSelectedOptionText.setText(mSelectedOption.getConfirmation());
        this.showSnoozeOptions(false);
        this.hideSelectedOption();
        this.sendAccessibilityEvent(32);
        if (b) {
            this.logOptionSelection(1138, mSelectedOption);
        }
    }
    
    private void showSnoozeOptions(final boolean mExpanded) {
        int imageResource;
        if (mExpanded) {
            imageResource = 17302362;
        }
        else {
            imageResource = 17302421;
        }
        this.mExpandButton.setImageResource(imageResource);
        if (this.mExpanded != mExpanded) {
            this.animateSnoozeOptions(this.mExpanded = mExpanded);
            final NotificationGuts mGutsContainer = this.mGutsContainer;
            if (mGutsContainer != null) {
                mGutsContainer.onHeightChanged();
            }
        }
    }
    
    private void undoSnooze(final View view) {
        this.mSelectedOption = null;
        final int[] array = new int[2];
        final int[] array2 = new int[2];
        this.mGutsContainer.getLocationOnScreen(array);
        view.getLocationOnScreen(array2);
        final int n = view.getWidth() / 2;
        final int n2 = view.getHeight() / 2;
        final int n3 = array2[0];
        final int n4 = array[0];
        final int n5 = array2[1];
        final int n6 = array[1];
        this.showSnoozeOptions(false);
        this.mGutsContainer.closeControls(n3 - n4 + n, n5 - n6 + n2, false, false);
    }
    
    public int getActualHeight() {
        int n;
        if (this.mExpanded) {
            n = this.getHeight();
        }
        else {
            n = this.mCollapsedHeight;
        }
        return n;
    }
    
    public View getContentView() {
        this.setSelected(this.mDefaultOption, false);
        return (View)this;
    }
    
    @VisibleForTesting
    NotificationSwipeActionHelper.SnoozeOption getDefaultOption() {
        return this.mDefaultOption;
    }
    
    @VisibleForTesting
    ArrayList<NotificationSwipeActionHelper.SnoozeOption> getDefaultSnoozeOptions() {
        final Resources resources = this.getContext().getResources();
        final ArrayList<NotificationSwipeActionHelper.SnoozeOption> list = new ArrayList<NotificationSwipeActionHelper.SnoozeOption>();
        try {
            this.mParser.setString(Settings$Global.getString(this.getContext().getContentResolver(), "notification_snooze_options"));
        }
        catch (IllegalArgumentException ex) {
            Log.e("NotificationSnooze", "Bad snooze constants");
        }
        final int int1 = this.mParser.getInt("default", resources.getInteger(R$integer.config_notification_snooze_time_default));
        final int[] intArray = this.mParser.getIntArray("options_array", resources.getIntArray(R$array.config_notification_snooze_times));
        for (int i = 0; i < intArray.length; ++i) {
            final int[] sAccessibilityActions = NotificationSnooze.sAccessibilityActions;
            if (i >= sAccessibilityActions.length) {
                break;
            }
            final int n = intArray[i];
            final NotificationSwipeActionHelper.SnoozeOption option = this.createOption(n, sAccessibilityActions[i]);
            if (i == 0 || n == int1) {
                this.mDefaultOption = option;
            }
            list.add(option);
        }
        return list;
    }
    
    public boolean handleCloseControls(final boolean b, final boolean b2) {
        if (this.mExpanded && !b2) {
            this.showSnoozeOptions(false);
            return true;
        }
        final NotificationSwipeActionHelper mSnoozeListener = this.mSnoozeListener;
        if (mSnoozeListener != null) {
            final NotificationSwipeActionHelper.SnoozeOption mSelectedOption = this.mSelectedOption;
            if (mSelectedOption != null) {
                this.mSnoozing = true;
                mSnoozeListener.snooze(this.mSbn, mSelectedOption);
                return true;
            }
        }
        this.setSelected(this.mSnoozeOptions.get(0), false);
        return false;
    }
    
    public boolean isExpanded() {
        return this.mExpanded;
    }
    
    public boolean isLeavebehind() {
        return true;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.logOptionSelection(1137, this.mDefaultOption);
    }
    
    public void onClick(final View view) {
        final NotificationGuts mGutsContainer = this.mGutsContainer;
        if (mGutsContainer != null) {
            mGutsContainer.resetFalsingCheck();
        }
        final int id = view.getId();
        final NotificationSwipeActionHelper.SnoozeOption snoozeOption = (NotificationSwipeActionHelper.SnoozeOption)view.getTag();
        if (snoozeOption != null) {
            this.setSelected(snoozeOption, true);
        }
        else if (id == R$id.notification_snooze) {
            this.showSnoozeOptions(this.mExpanded ^ true);
            final MetricsLogger mMetricsLogger = this.mMetricsLogger;
            LogMaker logMaker;
            if (!this.mExpanded) {
                logMaker = NotificationSnooze.OPTIONS_OPEN_LOG;
            }
            else {
                logMaker = NotificationSnooze.OPTIONS_CLOSE_LOG;
            }
            mMetricsLogger.write(logMaker);
        }
        else {
            this.undoSnooze(view);
            this.mMetricsLogger.write(NotificationSnooze.UNDO_LOG);
        }
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mCollapsedHeight = this.getResources().getDimensionPixelSize(R$dimen.snooze_snackbar_min_height);
        this.findViewById(R$id.notification_snooze).setOnClickListener((View$OnClickListener)this);
        this.mSelectedOptionText = (TextView)this.findViewById(R$id.snooze_option_default);
        (this.mUndoButton = (TextView)this.findViewById(R$id.undo)).setOnClickListener((View$OnClickListener)this);
        this.mExpandButton = (ImageView)this.findViewById(R$id.expand_button);
        (this.mDivider = this.findViewById(R$id.divider)).setAlpha(0.0f);
        (this.mSnoozeOptionContainer = (ViewGroup)this.findViewById(R$id.snooze_options)).setVisibility(4);
        this.mSnoozeOptionContainer.setAlpha(0.0f);
        this.mSnoozeOptions = this.getDefaultSnoozeOptions();
        this.createOptionViews();
        this.setSelected(this.mDefaultOption, false);
    }
    
    public void onInitializeAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        final NotificationGuts mGutsContainer = this.mGutsContainer;
        if (mGutsContainer != null && mGutsContainer.isExposed() && accessibilityEvent.getEventType() == 32) {
            accessibilityEvent.getText().add(this.mSelectedOptionText.getText());
        }
    }
    
    public void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.addAction(new AccessibilityNodeInfo$AccessibilityAction(R$id.action_snooze_undo, (CharSequence)this.getResources().getString(R$string.snooze_undo)));
        for (int size = this.mSnoozeOptions.size(), i = 0; i < size; ++i) {
            final AccessibilityNodeInfo$AccessibilityAction accessibilityAction = this.mSnoozeOptions.get(i).getAccessibilityAction();
            if (accessibilityAction != null) {
                accessibilityNodeInfo.addAction(accessibilityAction);
            }
        }
    }
    
    public boolean performAccessibilityActionInternal(final int n, final Bundle bundle) {
        if (super.performAccessibilityActionInternal(n, bundle)) {
            return true;
        }
        if (n == R$id.action_snooze_undo) {
            this.undoSnooze((View)this.mUndoButton);
            return true;
        }
        for (int i = 0; i < this.mSnoozeOptions.size(); ++i) {
            final NotificationSwipeActionHelper.SnoozeOption snoozeOption = this.mSnoozeOptions.get(i);
            if (snoozeOption.getAccessibilityAction() != null && snoozeOption.getAccessibilityAction().getId() == n) {
                this.setSelected(snoozeOption, true);
                return true;
            }
        }
        return false;
    }
    
    public void setGutsParent(final NotificationGuts mGutsContainer) {
        this.mGutsContainer = mGutsContainer;
    }
    
    @VisibleForTesting
    void setKeyValueListParser(final KeyValueListParser mParser) {
        this.mParser = mParser;
    }
    
    public void setSnoozeListener(final NotificationSwipeActionHelper mSnoozeListener) {
        this.mSnoozeListener = mSnoozeListener;
    }
    
    public void setSnoozeOptions(final List<SnoozeCriterion> list) {
        if (list == null) {
            return;
        }
        this.mSnoozeOptions.clear();
        this.mSnoozeOptions = this.getDefaultSnoozeOptions();
        for (int min = Math.min(1, list.size()), i = 0; i < min; ++i) {
            final SnoozeCriterion snoozeCriterion = list.get(i);
            this.mSnoozeOptions.add(new NotificationSnoozeOption(snoozeCriterion, 0, snoozeCriterion.getExplanation(), snoozeCriterion.getConfirmation(), new AccessibilityNodeInfo$AccessibilityAction(R$id.action_snooze_assistant_suggestion_1, snoozeCriterion.getExplanation())));
        }
        this.createOptionViews();
    }
    
    public void setStatusBarNotification(final StatusBarNotification mSbn) {
        this.mSbn = mSbn;
    }
    
    public boolean shouldBeSaved() {
        return true;
    }
    
    public boolean willBeRemoved() {
        return this.mSnoozing;
    }
    
    public class NotificationSnoozeOption implements SnoozeOption
    {
        private AccessibilityNodeInfo$AccessibilityAction mAction;
        private CharSequence mConfirmation;
        private SnoozeCriterion mCriterion;
        private CharSequence mDescription;
        private int mMinutesToSnoozeFor;
        
        public NotificationSnoozeOption(final NotificationSnooze notificationSnooze, final SnoozeCriterion mCriterion, final int mMinutesToSnoozeFor, final CharSequence mDescription, final CharSequence mConfirmation, final AccessibilityNodeInfo$AccessibilityAction mAction) {
            this.mCriterion = mCriterion;
            this.mMinutesToSnoozeFor = mMinutesToSnoozeFor;
            this.mDescription = mDescription;
            this.mConfirmation = mConfirmation;
            this.mAction = mAction;
        }
        
        @Override
        public AccessibilityNodeInfo$AccessibilityAction getAccessibilityAction() {
            return this.mAction;
        }
        
        @Override
        public CharSequence getConfirmation() {
            return this.mConfirmation;
        }
        
        @Override
        public CharSequence getDescription() {
            return this.mDescription;
        }
        
        @Override
        public int getMinutesToSnoozeFor() {
            return this.mMinutesToSnoozeFor;
        }
        
        @Override
        public SnoozeCriterion getSnoozeCriterion() {
            return this.mCriterion;
        }
    }
}
