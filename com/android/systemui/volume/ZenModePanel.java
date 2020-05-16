// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.volume;

import android.content.SharedPreferences;
import android.util.MathUtils;
import com.android.systemui.R$integer;
import android.content.SharedPreferences$OnSharedPreferenceChangeListener;
import android.animation.LayoutTransition;
import android.util.ArraySet;
import android.animation.LayoutTransition$TransitionListener;
import android.os.Message;
import android.os.Looper;
import android.os.Handler;
import android.content.res.Configuration;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.R$id;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import android.text.format.DateFormat;
import java.util.Objects;
import android.app.ActivityManager;
import android.view.View$OnClickListener;
import android.text.TextUtils;
import android.widget.CompoundButton;
import android.widget.CompoundButton$OnCheckedChangeListener;
import android.widget.RadioButton;
import com.android.systemui.R$string;
import com.android.systemui.R$layout;
import com.android.settingslib.volume.Util;
import android.os.AsyncTask;
import com.android.systemui.Prefs;
import com.android.internal.logging.MetricsLogger;
import android.service.notification.ZenModeConfig$ZenRule;
import android.util.AttributeSet;
import java.util.Arrays;
import android.service.notification.ZenModeConfig;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.net.Uri;
import android.service.notification.Condition;
import android.widget.ImageView;
import android.view.ViewGroup;
import com.android.systemui.statusbar.policy.ZenModeController;
import android.content.Context;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.widget.FrameLayout;

public class ZenModePanel extends FrameLayout
{
    private static final boolean DEBUG;
    private static final int DEFAULT_BUCKET_INDEX;
    private static final int MAX_BUCKET_MINUTES;
    private static final int[] MINUTE_BUCKETS;
    private static final int MIN_BUCKET_MINUTES;
    public static final Intent ZEN_PRIORITY_SETTINGS;
    public static final Intent ZEN_SETTINGS;
    private boolean mAttached;
    private int mAttachedZen;
    private View mAutoRule;
    private TextView mAutoTitle;
    private int mBucketIndex;
    private Callback mCallback;
    private final ConfigurableTexts mConfigurableTexts;
    private final Context mContext;
    private ZenModeController mController;
    private ViewGroup mEdit;
    private View mEmpty;
    private ImageView mEmptyIcon;
    private TextView mEmptyText;
    private Condition mExitCondition;
    private boolean mExpanded;
    private final Uri mForeverId;
    private final H mHandler;
    private boolean mHidden;
    protected final LayoutInflater mInflater;
    private final Interaction.Callback mInteractionCallback;
    private final ZenPrefs mPrefs;
    private Condition mSessionExitCondition;
    private int mSessionZen;
    private int mState;
    private String mTag;
    private final TransitionHelper mTransitionHelper;
    private boolean mVoiceCapable;
    private TextView mZenAlarmWarning;
    protected SegmentedButtons mZenButtons;
    protected final SegmentedButtons.Callback mZenButtonsCallback;
    private final ZenModeController.Callback mZenCallback;
    protected LinearLayout mZenConditions;
    private View mZenIntroduction;
    private View mZenIntroductionConfirm;
    private TextView mZenIntroductionCustomize;
    private TextView mZenIntroductionMessage;
    protected int mZenModeButtonLayoutId;
    protected int mZenModeConditionLayoutId;
    private RadioGroup mZenRadioGroup;
    private LinearLayout mZenRadioGroupContent;
    
    static {
        DEBUG = Log.isLoggable("ZenModePanel", 3);
        final int[] a = MINUTE_BUCKETS = ZenModeConfig.MINUTE_BUCKETS;
        MIN_BUCKET_MINUTES = a[0];
        MAX_BUCKET_MINUTES = a[a.length - 1];
        DEFAULT_BUCKET_INDEX = Arrays.binarySearch(a, 60);
        ZEN_SETTINGS = new Intent("android.settings.ZEN_MODE_SETTINGS");
        ZEN_PRIORITY_SETTINGS = new Intent("android.settings.ZEN_MODE_PRIORITY_SETTINGS");
    }
    
    public ZenModePanel(final Context mContext, final AttributeSet set) {
        super(mContext, set);
        this.mHandler = new H();
        this.mTransitionHelper = new TransitionHelper();
        final StringBuilder sb = new StringBuilder();
        sb.append("ZenModePanel/");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        this.mTag = sb.toString();
        this.mBucketIndex = -1;
        this.mState = 0;
        this.mZenCallback = new ZenModeController.Callback() {
            @Override
            public void onManualRuleChanged(final ZenModeConfig$ZenRule zenModeConfig$ZenRule) {
                ZenModePanel.this.mHandler.obtainMessage(2, (Object)zenModeConfig$ZenRule).sendToTarget();
            }
        };
        this.mZenButtonsCallback = new SegmentedButtons.Callback() {
            @Override
            public void onInteraction() {
                ZenModePanel.this.fireInteraction();
            }
            
            @Override
            public void onSelected(final Object o, final boolean b) {
                if (o != null && ZenModePanel.this.mZenButtons.isShown() && ZenModePanel.this.isAttachedToWindow()) {
                    final int intValue = (int)o;
                    if (b) {
                        MetricsLogger.action(ZenModePanel.this.mContext, 165, intValue);
                    }
                    if (ZenModePanel.DEBUG) {
                        final String access$800 = ZenModePanel.this.mTag;
                        final StringBuilder sb = new StringBuilder();
                        sb.append("mZenButtonsCallback selected=");
                        sb.append(intValue);
                        Log.d(access$800, sb.toString());
                    }
                    final ZenModePanel this$0 = ZenModePanel.this;
                    AsyncTask.execute((Runnable)new Runnable() {
                        final /* synthetic */ Uri val$realConditionId = this$0.getRealConditionId(this$0.mSessionExitCondition);
                        
                        @Override
                        public void run() {
                            ZenModePanel.this.mController.setZen(intValue, this.val$realConditionId, "ZenModePanel.selectZen");
                            if (intValue != 0) {
                                Prefs.putInt(ZenModePanel.this.mContext, "DndFavoriteZen", intValue);
                            }
                        }
                    });
                }
            }
        };
        this.mInteractionCallback = new Interaction.Callback() {
            @Override
            public void onInteraction() {
                ZenModePanel.this.fireInteraction();
            }
        };
        this.mContext = mContext;
        this.mPrefs = new ZenPrefs();
        this.mInflater = LayoutInflater.from(this.mContext);
        this.mForeverId = Condition.newId(this.mContext).appendPath("forever").build();
        this.mConfigurableTexts = new ConfigurableTexts(this.mContext);
        this.mVoiceCapable = Util.isVoiceCapable(this.mContext);
        this.mZenModeConditionLayoutId = R$layout.zen_mode_condition;
        this.mZenModeButtonLayoutId = R$layout.zen_mode_button;
        if (ZenModePanel.DEBUG) {
            Log.d(this.mTag, "new ZenModePanel");
        }
    }
    
    private void announceConditionSelection(final ConditionTag conditionTag) {
        final int selectedZen = this.getSelectedZen(0);
        String s;
        if (selectedZen != 1) {
            if (selectedZen != 2) {
                if (selectedZen != 3) {
                    return;
                }
                s = this.mContext.getString(R$string.interruption_level_alarms);
            }
            else {
                s = this.mContext.getString(R$string.interruption_level_none);
            }
        }
        else {
            s = this.mContext.getString(R$string.interruption_level_priority);
        }
        this.announceForAccessibility((CharSequence)this.mContext.getString(R$string.zen_mode_and_condition, new Object[] { s, conditionTag.line1.getText() }));
    }
    
    private void bind(final Condition condition, final View view, int mBucketIndex) {
        if (condition != null) {
            final int state = condition.state;
            final boolean b = true;
            final boolean b2 = state == 1;
            ConditionTag tag;
            if (view.getTag() != null) {
                tag = (ConditionTag)view.getTag();
            }
            else {
                tag = new ConditionTag();
            }
            view.setTag((Object)tag);
            final boolean b3 = tag.rb == null;
            if (tag.rb == null) {
                tag.rb = (RadioButton)this.mZenRadioGroup.getChildAt(mBucketIndex);
            }
            tag.condition = condition;
            final Uri conditionId = getConditionId(condition);
            if (ZenModePanel.DEBUG) {
                final String mTag = this.mTag;
                final StringBuilder sb = new StringBuilder();
                sb.append("bind i=");
                sb.append(this.mZenRadioGroupContent.indexOfChild(view));
                sb.append(" first=");
                sb.append(b3);
                sb.append(" condition=");
                sb.append(conditionId);
                Log.d(mTag, sb.toString());
            }
            tag.rb.setEnabled(b2);
            tag.rb.setOnCheckedChangeListener((CompoundButton$OnCheckedChangeListener)new CompoundButton$OnCheckedChangeListener() {
                public void onCheckedChanged(final CompoundButton compoundButton, final boolean b) {
                    if (ZenModePanel.this.mExpanded && b) {
                        tag.rb.setChecked(true);
                        if (ZenModePanel.DEBUG) {
                            final String access$800 = ZenModePanel.this.mTag;
                            final StringBuilder sb = new StringBuilder();
                            sb.append("onCheckedChanged ");
                            sb.append(conditionId);
                            Log.d(access$800, sb.toString());
                        }
                        MetricsLogger.action(ZenModePanel.this.mContext, 164);
                        ZenModePanel.this.select(tag.condition);
                        ZenModePanel.this.announceConditionSelection(tag);
                    }
                }
            });
            if (tag.lines == null) {
                tag.lines = view.findViewById(16908290);
            }
            if (tag.line1 == null) {
                final TextView line1 = (TextView)view.findViewById(16908308);
                tag.line1 = line1;
                this.mConfigurableTexts.add(line1);
            }
            if (tag.line2 == null) {
                final TextView line2 = (TextView)view.findViewById(16908309);
                tag.line2 = line2;
                this.mConfigurableTexts.add(line2);
            }
            String text;
            if (!TextUtils.isEmpty((CharSequence)condition.line1)) {
                text = condition.line1;
            }
            else {
                text = condition.summary;
            }
            final String line3 = condition.line2;
            tag.line1.setText((CharSequence)text);
            if (TextUtils.isEmpty((CharSequence)line3)) {
                tag.line2.setVisibility(8);
            }
            else {
                tag.line2.setVisibility(0);
                tag.line2.setText((CharSequence)line3);
            }
            tag.lines.setEnabled(b2);
            final View lines = tag.lines;
            float alpha;
            if (b2) {
                alpha = 1.0f;
            }
            else {
                alpha = 0.4f;
            }
            lines.setAlpha(alpha);
            final ImageView imageView = (ImageView)view.findViewById(16908313);
            imageView.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
                public void onClick(final View view) {
                    ZenModePanel.this.onClickTimeButton(view, tag, false, mBucketIndex);
                }
            });
            final ImageView imageView2 = (ImageView)view.findViewById(16908314);
            imageView2.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
                public void onClick(final View view) {
                    ZenModePanel.this.onClickTimeButton(view, tag, true, mBucketIndex);
                }
            });
            tag.lines.setOnClickListener((View$OnClickListener)new View$OnClickListener(this) {
                public void onClick(final View view) {
                    tag.rb.setChecked(true);
                }
            });
            final long tryParseCountdownConditionId = ZenModeConfig.tryParseCountdownConditionId(conditionId);
            if (mBucketIndex != 2 && tryParseCountdownConditionId > 0L) {
                imageView.setVisibility(0);
                imageView2.setVisibility(0);
                mBucketIndex = this.mBucketIndex;
                if (mBucketIndex > -1) {
                    imageView.setEnabled(mBucketIndex > 0);
                    imageView2.setEnabled(this.mBucketIndex < ZenModePanel.MINUTE_BUCKETS.length - 1 && b);
                }
                else {
                    imageView.setEnabled(tryParseCountdownConditionId - System.currentTimeMillis() > ZenModePanel.MIN_BUCKET_MINUTES * 60000);
                    imageView2.setEnabled(Objects.equals(condition.summary, ZenModeConfig.toTimeCondition(this.mContext, ZenModePanel.MAX_BUCKET_MINUTES, ActivityManager.getCurrentUser()).summary) ^ true);
                }
                float alpha2;
                if (imageView.isEnabled()) {
                    alpha2 = 1.0f;
                }
                else {
                    alpha2 = 0.5f;
                }
                imageView.setAlpha(alpha2);
                float alpha3;
                if (imageView2.isEnabled()) {
                    alpha3 = 1.0f;
                }
                else {
                    alpha3 = 0.5f;
                }
                imageView2.setAlpha(alpha3);
            }
            else {
                imageView.setVisibility(8);
                imageView2.setVisibility(8);
            }
            if (b3) {
                Interaction.register((View)tag.rb, this.mInteractionCallback);
                Interaction.register(tag.lines, this.mInteractionCallback);
                Interaction.register((View)imageView, this.mInteractionCallback);
                Interaction.register((View)imageView2, this.mInteractionCallback);
            }
            view.setVisibility(0);
            return;
        }
        throw new IllegalArgumentException("condition must not be null");
    }
    
    private void bindGenericCountdown() {
        final int default_BUCKET_INDEX = ZenModePanel.DEFAULT_BUCKET_INDEX;
        this.mBucketIndex = default_BUCKET_INDEX;
        final Condition timeCondition = ZenModeConfig.toTimeCondition(this.mContext, ZenModePanel.MINUTE_BUCKETS[default_BUCKET_INDEX], ActivityManager.getCurrentUser());
        if (!this.mAttached || this.getConditionTagAt(1).condition == null) {
            this.bind(timeCondition, this.mZenRadioGroupContent.getChildAt(1), 1);
        }
    }
    
    private void bindNextAlarm(final Condition condition) {
        final View child = this.mZenRadioGroupContent.getChildAt(2);
        final ConditionTag conditionTag = (ConditionTag)child.getTag();
        if (condition != null && (!this.mAttached || conditionTag == null || conditionTag.condition == null)) {
            this.bind(condition, child, 2);
        }
        final ConditionTag conditionTag2 = (ConditionTag)child.getTag();
        final int n = 0;
        final boolean b = conditionTag2 != null && conditionTag2.condition != null;
        final View child2 = this.mZenRadioGroup.getChildAt(2);
        int visibility;
        if (b) {
            visibility = 0;
        }
        else {
            visibility = 4;
        }
        child2.setVisibility(visibility);
        int visibility2;
        if (b) {
            visibility2 = n;
        }
        else {
            visibility2 = 4;
        }
        child.setVisibility(visibility2);
    }
    
    private void checkForAttachedZenChange() {
        final int selectedZen = this.getSelectedZen(-1);
        if (ZenModePanel.DEBUG) {
            final String mTag = this.mTag;
            final StringBuilder sb = new StringBuilder();
            sb.append("selectedZen=");
            sb.append(selectedZen);
            Log.d(mTag, sb.toString());
        }
        if (selectedZen != this.mAttachedZen) {
            if (ZenModePanel.DEBUG) {
                final String mTag2 = this.mTag;
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("attachedZen: ");
                sb2.append(this.mAttachedZen);
                sb2.append(" -> ");
                sb2.append(selectedZen);
                Log.d(mTag2, sb2.toString());
            }
            if (selectedZen == 2) {
                this.mPrefs.trackNoneSelected();
            }
        }
    }
    
    private String computeAlarmWarningText(final boolean b) {
        if (!b) {
            return null;
        }
        final long currentTimeMillis = System.currentTimeMillis();
        final long nextAlarm = this.mController.getNextAlarm();
        if (nextAlarm < currentTimeMillis) {
            return null;
        }
        final Condition mSessionExitCondition = this.mSessionExitCondition;
        int n;
        if (mSessionExitCondition != null && !this.isForever(mSessionExitCondition)) {
            final long tryParseCountdownConditionId = ZenModeConfig.tryParseCountdownConditionId(this.mSessionExitCondition.id);
            if (tryParseCountdownConditionId > currentTimeMillis && nextAlarm < tryParseCountdownConditionId) {
                n = R$string.zen_alarm_warning;
            }
            else {
                n = 0;
            }
        }
        else {
            n = R$string.zen_alarm_warning_indef;
        }
        if (n == 0) {
            return null;
        }
        final boolean b2 = nextAlarm - currentTimeMillis < 86400000L;
        final boolean is24HourFormat = DateFormat.is24HourFormat(this.mContext, ActivityManager.getCurrentUser());
        String s;
        if (b2) {
            if (is24HourFormat) {
                s = "Hm";
            }
            else {
                s = "hma";
            }
        }
        else if (is24HourFormat) {
            s = "EEEHm";
        }
        else {
            s = "EEEhma";
        }
        final CharSequence format = DateFormat.format((CharSequence)DateFormat.getBestDateTimePattern(Locale.getDefault(), s), nextAlarm);
        int n2;
        if (b2) {
            n2 = R$string.alarm_template;
        }
        else {
            n2 = R$string.alarm_template_far;
        }
        return this.getResources().getString(n, new Object[] { this.getResources().getString(n2, new Object[] { format }) });
    }
    
    private void confirmZenIntroduction() {
        final String prefKeyForConfirmation = prefKeyForConfirmation(this.getSelectedZen(0));
        if (prefKeyForConfirmation == null) {
            return;
        }
        if (ZenModePanel.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("confirmZenIntroduction ");
            sb.append(prefKeyForConfirmation);
            Log.d("ZenModePanel", sb.toString());
        }
        Prefs.putBoolean(this.mContext, prefKeyForConfirmation, true);
        this.mHandler.sendEmptyMessage(3);
    }
    
    private static Condition copy(Condition copy) {
        if (copy == null) {
            copy = null;
        }
        else {
            copy = copy.copy();
        }
        return copy;
    }
    
    private Condition createCondition(final Uri uri) {
        if (ZenModeConfig.isValidCountdownToAlarmConditionId(uri)) {
            return ZenModeConfig.toNextAlarmCondition(this.mContext, ZenModeConfig.tryParseCountdownConditionId(uri), ActivityManager.getCurrentUser());
        }
        if (ZenModeConfig.isValidCountdownConditionId(uri)) {
            final long tryParseCountdownConditionId = ZenModeConfig.tryParseCountdownConditionId(uri);
            return ZenModeConfig.toTimeCondition(this.mContext, tryParseCountdownConditionId, (int)((tryParseCountdownConditionId - System.currentTimeMillis() + 30000L) / 60000L), ActivityManager.getCurrentUser(), false);
        }
        return this.forever();
    }
    
    private void fireExpanded() {
        final Callback mCallback = this.mCallback;
        if (mCallback != null) {
            mCallback.onExpanded(this.mExpanded);
        }
    }
    
    private void fireInteraction() {
        final Callback mCallback = this.mCallback;
        if (mCallback != null) {
            mCallback.onInteraction();
        }
    }
    
    private Condition forever() {
        return new Condition(this.mForeverId, foreverSummary(this.mContext), "", "", 0, 1, 0);
    }
    
    private static String foreverSummary(final Context context) {
        return context.getString(17041506);
    }
    
    private static Uri getConditionId(final Condition condition) {
        Uri id;
        if (condition != null) {
            id = condition.id;
        }
        else {
            id = null;
        }
        return id;
    }
    
    private Uri getRealConditionId(final Condition condition) {
        Uri conditionId;
        if (this.isForever(condition)) {
            conditionId = null;
        }
        else {
            conditionId = getConditionId(condition);
        }
        return conditionId;
    }
    
    private Condition getTimeUntilNextAlarmCondition() {
        final GregorianCalendar toMidnight = new GregorianCalendar();
        this.setToMidnight(toMidnight);
        toMidnight.add(5, 6);
        final long nextAlarm = this.mController.getNextAlarm();
        if (nextAlarm > 0L) {
            final GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTimeInMillis(nextAlarm);
            this.setToMidnight(gregorianCalendar);
            if (toMidnight.compareTo((Calendar)gregorianCalendar) >= 0) {
                return ZenModeConfig.toNextAlarmCondition(this.mContext, nextAlarm, ActivityManager.getCurrentUser());
            }
        }
        return null;
    }
    
    private View getView(final int n) {
        if (n == 1) {
            return this.mAutoRule;
        }
        if (n != 2) {
            return (View)this.mEdit;
        }
        return this.mEmpty;
    }
    
    private void handleUpdateZen(final int n) {
        final int mSessionZen = this.mSessionZen;
        if (mSessionZen != -1 && mSessionZen != n) {
            this.mSessionZen = n;
        }
        this.mZenButtons.setSelectedValue(n, false);
        this.updateWidgets();
    }
    
    private void hideAllConditions() {
        for (int childCount = this.mZenRadioGroupContent.getChildCount(), i = 0; i < childCount; ++i) {
            this.mZenRadioGroupContent.getChildAt(i).setVisibility(8);
        }
    }
    
    private static boolean isAlarm(final Condition condition) {
        return condition != null && ZenModeConfig.isValidCountdownToAlarmConditionId(condition.id);
    }
    
    private static boolean isCountdown(final Condition condition) {
        return condition != null && ZenModeConfig.isValidCountdownConditionId(condition.id);
    }
    
    private boolean isForever(final Condition condition) {
        return condition != null && this.mForeverId.equals((Object)condition.id);
    }
    
    private void onAttach() {
        this.setExpanded(true);
        this.mAttachedZen = this.mController.getZen();
        final ZenModeConfig$ZenRule manualRule = this.mController.getManualRule();
        Condition condition;
        if (manualRule != null) {
            condition = manualRule.condition;
        }
        else {
            condition = null;
        }
        this.mExitCondition = condition;
        if (ZenModePanel.DEBUG) {
            final String mTag = this.mTag;
            final StringBuilder sb = new StringBuilder();
            sb.append("onAttach ");
            sb.append(this.mAttachedZen);
            sb.append(" ");
            sb.append(manualRule);
            Log.d(mTag, sb.toString());
        }
        this.handleUpdateManualRule(manualRule);
        this.mZenButtons.setSelectedValue(this.mAttachedZen, false);
        this.mSessionZen = this.mAttachedZen;
        this.mTransitionHelper.clear();
        this.mController.addCallback(this.mZenCallback);
        this.setSessionExitCondition(copy(this.mExitCondition));
        this.updateWidgets();
        this.setAttached(true);
    }
    
    private void onClickTimeButton(final View view, final ConditionTag conditionTag, final boolean b, final int n) {
        MetricsLogger.action(this.mContext, 163, b);
        final int length = ZenModePanel.MINUTE_BUCKETS.length;
        final int mBucketIndex = this.mBucketIndex;
        int i = 0;
        final int n2 = -1;
        Condition condition = null;
        Label_0252: {
            if (mBucketIndex == -1) {
                final long tryParseCountdownConditionId = ZenModeConfig.tryParseCountdownConditionId(getConditionId(conditionTag.condition));
                final long currentTimeMillis = System.currentTimeMillis();
                while (true) {
                    while (i < length) {
                        int mBucketIndex2;
                        if (b) {
                            mBucketIndex2 = i;
                        }
                        else {
                            mBucketIndex2 = length - 1 - i;
                        }
                        final int n3 = ZenModePanel.MINUTE_BUCKETS[mBucketIndex2];
                        final long n4 = currentTimeMillis + 60000 * n3;
                        if ((b && n4 > tryParseCountdownConditionId) || (!b && n4 < tryParseCountdownConditionId)) {
                            this.mBucketIndex = mBucketIndex2;
                            final Condition timeCondition = ZenModeConfig.toTimeCondition(this.mContext, n4, n3, ActivityManager.getCurrentUser(), false);
                            condition = timeCondition;
                            if (timeCondition == null) {
                                final int default_BUCKET_INDEX = ZenModePanel.DEFAULT_BUCKET_INDEX;
                                this.mBucketIndex = default_BUCKET_INDEX;
                                condition = ZenModeConfig.toTimeCondition(this.mContext, ZenModePanel.MINUTE_BUCKETS[default_BUCKET_INDEX], ActivityManager.getCurrentUser());
                            }
                            break Label_0252;
                        }
                        else {
                            ++i;
                        }
                    }
                    final Condition timeCondition = null;
                    continue;
                }
            }
            int n5 = n2;
            if (b) {
                n5 = 1;
            }
            final int max = Math.max(0, Math.min(length - 1, mBucketIndex + n5));
            this.mBucketIndex = max;
            condition = ZenModeConfig.toTimeCondition(this.mContext, ZenModePanel.MINUTE_BUCKETS[max], ActivityManager.getCurrentUser());
        }
        this.bind(condition, view, n);
        conditionTag.rb.setChecked(true);
        this.select(condition);
        this.announceConditionSelection(conditionTag);
    }
    
    private void onDetach() {
        if (ZenModePanel.DEBUG) {
            Log.d(this.mTag, "onDetach");
        }
        this.setExpanded(false);
        this.checkForAttachedZenChange();
        this.setAttached(false);
        this.mAttachedZen = -1;
        this.mSessionZen = -1;
        this.mController.removeCallback(this.mZenCallback);
        this.setSessionExitCondition(null);
        this.mTransitionHelper.clear();
    }
    
    private static String prefKeyForConfirmation(final int n) {
        if (n == 1) {
            return "DndConfirmedPriorityIntroduction";
        }
        if (n == 2) {
            return "DndConfirmedSilenceIntroduction";
        }
        if (n != 3) {
            return null;
        }
        return "DndConfirmedAlarmIntroduction";
    }
    
    private void select(final Condition condition) {
        if (ZenModePanel.DEBUG) {
            final String mTag = this.mTag;
            final StringBuilder sb = new StringBuilder();
            sb.append("select ");
            sb.append(condition);
            Log.d(mTag, sb.toString());
        }
        final int mSessionZen = this.mSessionZen;
        if (mSessionZen != -1 && mSessionZen != 0) {
            final Uri realConditionId = this.getRealConditionId(condition);
            if (this.mController != null) {
                AsyncTask.execute((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        ZenModePanel.this.mController.setZen(ZenModePanel.this.mSessionZen, realConditionId, "ZenModePanel.selectCondition");
                    }
                });
            }
            this.setExitCondition(condition);
            if (realConditionId == null) {
                this.mPrefs.setMinuteIndex(-1);
            }
            else if (isAlarm(condition) || isCountdown(condition)) {
                final int mBucketIndex = this.mBucketIndex;
                if (mBucketIndex != -1) {
                    this.mPrefs.setMinuteIndex(mBucketIndex);
                }
            }
            this.setSessionExitCondition(copy(condition));
            return;
        }
        if (ZenModePanel.DEBUG) {
            Log.d(this.mTag, "Ignoring condition selection outside of manual zen");
        }
    }
    
    private void setExitCondition(final Condition condition) {
        if (Objects.equals(this.mExitCondition, condition)) {
            return;
        }
        this.mExitCondition = condition;
        if (ZenModePanel.DEBUG) {
            final String mTag = this.mTag;
            final StringBuilder sb = new StringBuilder();
            sb.append("mExitCondition=");
            sb.append(getConditionId(this.mExitCondition));
            Log.d(mTag, sb.toString());
        }
        this.updateWidgets();
    }
    
    private void setExpanded(final boolean b) {
        if (b == this.mExpanded) {
            return;
        }
        if (ZenModePanel.DEBUG) {
            final String mTag = this.mTag;
            final StringBuilder sb = new StringBuilder();
            sb.append("setExpanded ");
            sb.append(b);
            Log.d(mTag, sb.toString());
        }
        this.mExpanded = b;
        this.updateWidgets();
        this.fireExpanded();
    }
    
    private void setSessionExitCondition(final Condition condition) {
        if (Objects.equals(condition, this.mSessionExitCondition)) {
            return;
        }
        if (ZenModePanel.DEBUG) {
            final String mTag = this.mTag;
            final StringBuilder sb = new StringBuilder();
            sb.append("mSessionExitCondition=");
            sb.append(getConditionId(condition));
            Log.d(mTag, sb.toString());
        }
        this.mSessionExitCondition = condition;
    }
    
    private void setToMidnight(final Calendar calendar) {
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
    }
    
    private void transitionFrom(final View view, final View view2) {
        view.post((Runnable)new _$$Lambda$ZenModePanel$BhXvHb7L6APT_cYYehmMxR3OZv4(view2, view));
    }
    
    private void updateWidgets() {
        if (this.mTransitionHelper.isTransitioning()) {
            this.mTransitionHelper.pendingUpdateWidgets();
            return;
        }
        final int n = 0;
        final int selectedZen = this.getSelectedZen(0);
        final int n2 = 1;
        final boolean b = selectedZen == 1;
        final boolean b2 = selectedZen == 2;
        final boolean b3 = selectedZen == 3;
        int n3 = 0;
        Label_0127: {
            if (b) {
                n3 = n2;
                if (!this.mPrefs.mConfirmedPriorityIntroduction) {
                    break Label_0127;
                }
            }
            if (b2) {
                n3 = n2;
                if (!this.mPrefs.mConfirmedSilenceIntroduction) {
                    break Label_0127;
                }
            }
            if (b3 && !this.mPrefs.mConfirmedAlarmIntroduction) {
                n3 = n2;
            }
            else {
                n3 = 0;
            }
        }
        final SegmentedButtons mZenButtons = this.mZenButtons;
        int visibility;
        if (this.mHidden) {
            visibility = 8;
        }
        else {
            visibility = 0;
        }
        mZenButtons.setVisibility(visibility);
        final View mZenIntroduction = this.mZenIntroduction;
        int visibility2;
        if (n3 != 0) {
            visibility2 = 0;
        }
        else {
            visibility2 = 8;
        }
        mZenIntroduction.setVisibility(visibility2);
        if (n3 != 0) {
            int n4;
            if (b) {
                n4 = R$string.zen_priority_introduction;
            }
            else if (b3) {
                n4 = R$string.zen_alarms_introduction;
            }
            else if (this.mVoiceCapable) {
                n4 = R$string.zen_silence_introduction_voice;
            }
            else {
                n4 = R$string.zen_silence_introduction;
            }
            this.mConfigurableTexts.add(this.mZenIntroductionMessage, n4);
            this.mConfigurableTexts.update();
            final TextView mZenIntroductionCustomize = this.mZenIntroductionCustomize;
            int visibility3;
            if (b) {
                visibility3 = 0;
            }
            else {
                visibility3 = 8;
            }
            mZenIntroductionCustomize.setVisibility(visibility3);
        }
        final String computeAlarmWarningText = this.computeAlarmWarningText(b2);
        final TextView mZenAlarmWarning = this.mZenAlarmWarning;
        int visibility4;
        if (computeAlarmWarningText != null) {
            visibility4 = n;
        }
        else {
            visibility4 = 8;
        }
        mZenAlarmWarning.setVisibility(visibility4);
        this.mZenAlarmWarning.setText((CharSequence)computeAlarmWarningText);
    }
    
    protected void addZenConditions(final int n) {
        for (int i = 0; i < n; ++i) {
            final View inflate = this.mInflater.inflate(this.mZenModeButtonLayoutId, this.mEdit, false);
            inflate.setId(i);
            this.mZenRadioGroup.addView(inflate);
            final View inflate2 = this.mInflater.inflate(this.mZenModeConditionLayoutId, this.mEdit, false);
            inflate2.setId(i + n);
            this.mZenRadioGroupContent.addView(inflate2);
        }
    }
    
    protected void createZenButtons() {
        (this.mZenButtons = (SegmentedButtons)this.findViewById(R$id.zen_buttons)).addButton(R$string.interruption_level_none_twoline, R$string.interruption_level_none_with_warning, 2);
        this.mZenButtons.addButton(R$string.interruption_level_alarms_twoline, R$string.interruption_level_alarms, 3);
        this.mZenButtons.addButton(R$string.interruption_level_priority_twoline, R$string.interruption_level_priority, 1);
        this.mZenButtons.setCallback(this.mZenButtonsCallback);
    }
    
    @VisibleForTesting
    ConditionTag getConditionTagAt(final int n) {
        return (ConditionTag)this.mZenRadioGroupContent.getChildAt(n).getTag();
    }
    
    @VisibleForTesting
    int getSelectedZen(int intValue) {
        final Object selectedValue = this.mZenButtons.getSelectedValue();
        if (selectedValue != null) {
            intValue = (int)selectedValue;
        }
        return intValue;
    }
    
    @VisibleForTesting
    int getVisibleConditions() {
        int n;
        for (int childCount = this.mZenRadioGroupContent.getChildCount(), i = n = 0; i < childCount; ++i) {
            int n2;
            if (this.mZenRadioGroupContent.getChildAt(i).getVisibility() == 0) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            n += n2;
        }
        return n;
    }
    
    @VisibleForTesting
    void handleUpdateConditions(final Condition obj) {
        if (this.mTransitionHelper.isTransitioning()) {
            return;
        }
        final Condition forever = this.forever();
        final LinearLayout mZenRadioGroupContent = this.mZenRadioGroupContent;
        int visibility = 0;
        this.bind(forever, mZenRadioGroupContent.getChildAt(0), 0);
        if (obj == null) {
            this.bindGenericCountdown();
            this.bindNextAlarm(this.getTimeUntilNextAlarmCondition());
        }
        else if (this.isForever(obj)) {
            this.getConditionTagAt(0).rb.setChecked(true);
            this.bindGenericCountdown();
            this.bindNextAlarm(this.getTimeUntilNextAlarmCondition());
        }
        else if (isAlarm(obj)) {
            this.bindGenericCountdown();
            this.bindNextAlarm(obj);
            this.getConditionTagAt(2).rb.setChecked(true);
        }
        else if (isCountdown(obj)) {
            this.bindNextAlarm(this.getTimeUntilNextAlarmCondition());
            this.bind(obj, this.mZenRadioGroupContent.getChildAt(1), 1);
            this.getConditionTagAt(1).rb.setChecked(true);
        }
        else {
            final StringBuilder sb = new StringBuilder();
            sb.append("Invalid manual condition: ");
            sb.append(obj);
            Slog.wtf("ZenModePanel", sb.toString());
        }
        final LinearLayout mZenConditions = this.mZenConditions;
        if (this.mSessionZen == 0) {
            visibility = 8;
        }
        mZenConditions.setVisibility(visibility);
    }
    
    @VisibleForTesting
    void handleUpdateManualRule(final ZenModeConfig$ZenRule zenModeConfig$ZenRule) {
        int zenMode;
        if (zenModeConfig$ZenRule != null) {
            zenMode = zenModeConfig$ZenRule.zenMode;
        }
        else {
            zenMode = 0;
        }
        this.handleUpdateZen(zenMode);
        Condition condition;
        if (zenModeConfig$ZenRule == null) {
            condition = null;
        }
        else {
            final Condition condition2 = zenModeConfig$ZenRule.condition;
            if (condition2 != null) {
                condition = condition2;
            }
            else {
                condition = this.createCondition(zenModeConfig$ZenRule.conditionId);
            }
        }
        this.handleUpdateConditions(condition);
        this.setExitCondition(condition);
    }
    
    public void init(final ZenModeController mController) {
        this.mController = mController;
        this.addZenConditions(3);
        this.mSessionZen = this.getSelectedZen(-1);
        this.handleUpdateManualRule(this.mController.getManualRule());
        if (ZenModePanel.DEBUG) {
            final String mTag = this.mTag;
            final StringBuilder sb = new StringBuilder();
            sb.append("init mExitCondition=");
            sb.append(this.mExitCondition);
            Log.d(mTag, sb.toString());
        }
        this.hideAllConditions();
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mConfigurableTexts.update();
        final SegmentedButtons mZenButtons = this.mZenButtons;
        if (mZenButtons != null) {
            mZenButtons.update();
        }
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.createZenButtons();
        this.mZenIntroduction = this.findViewById(R$id.zen_introduction);
        this.mZenIntroductionMessage = (TextView)this.findViewById(R$id.zen_introduction_message);
        (this.mZenIntroductionConfirm = this.findViewById(R$id.zen_introduction_confirm)).setOnClickListener((View$OnClickListener)new _$$Lambda$ZenModePanel$lbJ8lHqFYfMZus_ckwTZAx6gp_I(this));
        (this.mZenIntroductionCustomize = (TextView)this.findViewById(R$id.zen_introduction_customize)).setOnClickListener((View$OnClickListener)new _$$Lambda$ZenModePanel$1BYa_z9Fn3nPDHjhUKHednhVOqQ(this));
        this.mConfigurableTexts.add(this.mZenIntroductionCustomize, R$string.zen_priority_customize_button);
        this.mZenConditions = (LinearLayout)this.findViewById(R$id.zen_conditions);
        this.mZenAlarmWarning = (TextView)this.findViewById(R$id.zen_alarm_warning);
        this.mZenRadioGroup = (RadioGroup)this.findViewById(R$id.zen_radio_buttons);
        this.mZenRadioGroupContent = (LinearLayout)this.findViewById(R$id.zen_radio_buttons_content);
        this.mEdit = (ViewGroup)this.findViewById(R$id.edit_container);
        (this.mEmpty = this.findViewById(16908292)).setVisibility(4);
        this.mEmptyText = (TextView)this.mEmpty.findViewById(16908310);
        this.mEmptyIcon = (ImageView)this.mEmpty.findViewById(16908294);
        final View viewById = this.findViewById(R$id.auto_rule);
        this.mAutoRule = viewById;
        this.mAutoTitle = (TextView)viewById.findViewById(16908310);
        this.mAutoRule.setVisibility(4);
    }
    
    public void onVisibilityAggregated(final boolean b) {
        super.onVisibilityAggregated(b);
        if (b == this.mAttached) {
            return;
        }
        if (b) {
            this.onAttach();
        }
        else {
            this.onDetach();
        }
    }
    
    @VisibleForTesting
    void setAttached(final boolean mAttached) {
        this.mAttached = mAttached;
    }
    
    public void setAutoText(final CharSequence charSequence) {
        this.mAutoTitle.post((Runnable)new _$$Lambda$ZenModePanel$B3Y2r55PL6J4kgbiM4zXPpDTjiA(this, charSequence));
    }
    
    public void setCallback(final Callback mCallback) {
        this.mCallback = mCallback;
    }
    
    public void setEmptyState(final int n, final int n2) {
        this.mEmptyIcon.post((Runnable)new _$$Lambda$ZenModePanel$HiD6qQcUVG9hPBXBbXjbkowbyWE(this, n, n2));
    }
    
    public void setState(final int mState) {
        final int mState2 = this.mState;
        if (mState2 == mState) {
            return;
        }
        this.transitionFrom(this.getView(mState2), this.getView(mState));
        this.mState = mState;
    }
    
    public interface Callback
    {
        void onExpanded(final boolean p0);
        
        void onInteraction();
        
        void onPrioritySettings();
    }
    
    @VisibleForTesting
    static class ConditionTag
    {
        Condition condition;
        TextView line1;
        TextView line2;
        View lines;
        RadioButton rb;
    }
    
    private final class H extends Handler
    {
        private H() {
            super(Looper.getMainLooper());
        }
        
        public void handleMessage(final Message message) {
            final int what = message.what;
            if (what != 2) {
                if (what == 3) {
                    ZenModePanel.this.updateWidgets();
                }
            }
            else {
                ZenModePanel.this.handleUpdateManualRule((ZenModeConfig$ZenRule)message.obj);
            }
        }
    }
    
    private final class TransitionHelper implements LayoutTransition$TransitionListener, Runnable
    {
        private boolean mPendingUpdateWidgets;
        private boolean mTransitioning;
        private final ArraySet<View> mTransitioningViews;
        
        private TransitionHelper() {
            this.mTransitioningViews = (ArraySet<View>)new ArraySet();
        }
        
        private void updateTransitioning() {
            final boolean transitioning = this.isTransitioning();
            if (this.mTransitioning == transitioning) {
                return;
            }
            this.mTransitioning = transitioning;
            if (ZenModePanel.DEBUG) {
                final String access$800 = ZenModePanel.this.mTag;
                final StringBuilder sb = new StringBuilder();
                sb.append("TransitionHelper mTransitioning=");
                sb.append(this.mTransitioning);
                Log.d(access$800, sb.toString());
            }
            if (!this.mTransitioning) {
                if (this.mPendingUpdateWidgets) {
                    ZenModePanel.this.mHandler.post((Runnable)this);
                }
                else {
                    this.mPendingUpdateWidgets = false;
                }
            }
        }
        
        public void clear() {
            this.mTransitioningViews.clear();
            this.mPendingUpdateWidgets = false;
        }
        
        public void endTransition(final LayoutTransition layoutTransition, final ViewGroup viewGroup, final View view, final int n) {
            this.mTransitioningViews.remove((Object)view);
            this.updateTransitioning();
        }
        
        public boolean isTransitioning() {
            return this.mTransitioningViews.isEmpty() ^ true;
        }
        
        public void pendingUpdateWidgets() {
            this.mPendingUpdateWidgets = true;
        }
        
        public void run() {
            if (ZenModePanel.DEBUG) {
                final String access$800 = ZenModePanel.this.mTag;
                final StringBuilder sb = new StringBuilder();
                sb.append("TransitionHelper run mPendingUpdateWidgets=");
                sb.append(this.mPendingUpdateWidgets);
                Log.d(access$800, sb.toString());
            }
            if (this.mPendingUpdateWidgets) {
                ZenModePanel.this.updateWidgets();
            }
            this.mPendingUpdateWidgets = false;
        }
        
        public void startTransition(final LayoutTransition layoutTransition, final ViewGroup viewGroup, final View view, final int n) {
            this.mTransitioningViews.add((Object)view);
            this.updateTransitioning();
        }
    }
    
    private final class ZenPrefs implements SharedPreferences$OnSharedPreferenceChangeListener
    {
        private boolean mConfirmedAlarmIntroduction;
        private boolean mConfirmedPriorityIntroduction;
        private boolean mConfirmedSilenceIntroduction;
        private int mMinuteIndex;
        private final int mNoneDangerousThreshold;
        private int mNoneSelected;
        
        private ZenPrefs() {
            this.mNoneDangerousThreshold = ZenModePanel.this.mContext.getResources().getInteger(R$integer.zen_mode_alarm_warning_threshold);
            Prefs.registerListener(ZenModePanel.this.mContext, (SharedPreferences$OnSharedPreferenceChangeListener)this);
            this.updateMinuteIndex();
            this.updateNoneSelected();
            this.updateConfirmedPriorityIntroduction();
            this.updateConfirmedSilenceIntroduction();
            this.updateConfirmedAlarmIntroduction();
        }
        
        private int clampIndex(final int n) {
            return MathUtils.constrain(n, -1, ZenModePanel.MINUTE_BUCKETS.length - 1);
        }
        
        private int clampNoneSelected(final int n) {
            return MathUtils.constrain(n, 0, Integer.MAX_VALUE);
        }
        
        private void updateConfirmedAlarmIntroduction() {
            final boolean boolean1 = Prefs.getBoolean(ZenModePanel.this.mContext, "DndConfirmedAlarmIntroduction", false);
            if (boolean1 == this.mConfirmedAlarmIntroduction) {
                return;
            }
            this.mConfirmedAlarmIntroduction = boolean1;
            if (ZenModePanel.DEBUG) {
                final String access$800 = ZenModePanel.this.mTag;
                final StringBuilder sb = new StringBuilder();
                sb.append("Confirmed alarm introduction: ");
                sb.append(this.mConfirmedAlarmIntroduction);
                Log.d(access$800, sb.toString());
            }
        }
        
        private void updateConfirmedPriorityIntroduction() {
            final boolean boolean1 = Prefs.getBoolean(ZenModePanel.this.mContext, "DndConfirmedPriorityIntroduction", false);
            if (boolean1 == this.mConfirmedPriorityIntroduction) {
                return;
            }
            this.mConfirmedPriorityIntroduction = boolean1;
            if (ZenModePanel.DEBUG) {
                final String access$800 = ZenModePanel.this.mTag;
                final StringBuilder sb = new StringBuilder();
                sb.append("Confirmed priority introduction: ");
                sb.append(this.mConfirmedPriorityIntroduction);
                Log.d(access$800, sb.toString());
            }
        }
        
        private void updateConfirmedSilenceIntroduction() {
            final boolean boolean1 = Prefs.getBoolean(ZenModePanel.this.mContext, "DndConfirmedSilenceIntroduction", false);
            if (boolean1 == this.mConfirmedSilenceIntroduction) {
                return;
            }
            this.mConfirmedSilenceIntroduction = boolean1;
            if (ZenModePanel.DEBUG) {
                final String access$800 = ZenModePanel.this.mTag;
                final StringBuilder sb = new StringBuilder();
                sb.append("Confirmed silence introduction: ");
                sb.append(this.mConfirmedSilenceIntroduction);
                Log.d(access$800, sb.toString());
            }
        }
        
        private void updateMinuteIndex() {
            this.mMinuteIndex = this.clampIndex(Prefs.getInt(ZenModePanel.this.mContext, "DndCountdownMinuteIndex", ZenModePanel.DEFAULT_BUCKET_INDEX));
            if (ZenModePanel.DEBUG) {
                final String access$800 = ZenModePanel.this.mTag;
                final StringBuilder sb = new StringBuilder();
                sb.append("Favorite minute index: ");
                sb.append(this.mMinuteIndex);
                Log.d(access$800, sb.toString());
            }
        }
        
        private void updateNoneSelected() {
            this.mNoneSelected = this.clampNoneSelected(Prefs.getInt(ZenModePanel.this.mContext, "DndNoneSelected", 0));
            if (ZenModePanel.DEBUG) {
                final String access$800 = ZenModePanel.this.mTag;
                final StringBuilder sb = new StringBuilder();
                sb.append("None selected: ");
                sb.append(this.mNoneSelected);
                Log.d(access$800, sb.toString());
            }
        }
        
        public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String s) {
            this.updateMinuteIndex();
            this.updateNoneSelected();
            this.updateConfirmedPriorityIntroduction();
            this.updateConfirmedSilenceIntroduction();
            this.updateConfirmedAlarmIntroduction();
        }
        
        public void setMinuteIndex(int clampIndex) {
            clampIndex = this.clampIndex(clampIndex);
            if (clampIndex == this.mMinuteIndex) {
                return;
            }
            this.mMinuteIndex = this.clampIndex(clampIndex);
            if (ZenModePanel.DEBUG) {
                final String access$800 = ZenModePanel.this.mTag;
                final StringBuilder sb = new StringBuilder();
                sb.append("Setting favorite minute index: ");
                sb.append(this.mMinuteIndex);
                Log.d(access$800, sb.toString());
            }
            Prefs.putInt(ZenModePanel.this.mContext, "DndCountdownMinuteIndex", this.mMinuteIndex);
        }
        
        public void trackNoneSelected() {
            this.mNoneSelected = this.clampNoneSelected(this.mNoneSelected + 1);
            if (ZenModePanel.DEBUG) {
                final String access$800 = ZenModePanel.this.mTag;
                final StringBuilder sb = new StringBuilder();
                sb.append("Setting none selected: ");
                sb.append(this.mNoneSelected);
                sb.append(" threshold=");
                sb.append(this.mNoneDangerousThreshold);
                Log.d(access$800, sb.toString());
            }
            Prefs.putInt(ZenModePanel.this.mContext, "DndNoneSelected", this.mNoneSelected);
        }
    }
}
