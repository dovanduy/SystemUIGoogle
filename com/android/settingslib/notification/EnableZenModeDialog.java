// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.notification;

import java.util.GregorianCalendar;
import java.util.Locale;
import android.text.format.DateFormat;
import android.app.AlarmManager$AlarmClockInfo;
import com.android.settingslib.R$id;
import android.widget.ScrollView;
import android.view.ViewGroup;
import com.android.settingslib.R$layout;
import com.android.internal.policy.PhoneWindow;
import android.content.DialogInterface;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import android.app.Dialog;
import com.android.settingslib.R$string;
import android.util.Slog;
import android.widget.CompoundButton;
import android.widget.CompoundButton$OnCheckedChangeListener;
import android.widget.RadioButton;
import java.util.Objects;
import android.widget.ImageView;
import android.view.View$OnClickListener;
import android.text.TextUtils;
import java.util.Calendar;
import android.app.ActivityManager;
import com.android.internal.logging.MetricsLogger;
import android.view.View;
import android.service.notification.Condition;
import java.util.Arrays;
import android.service.notification.ZenModeConfig;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.app.NotificationManager;
import android.view.LayoutInflater;
import android.net.Uri;
import android.content.Context;
import android.app.AlarmManager;
import com.android.internal.annotations.VisibleForTesting;

public class EnableZenModeDialog
{
    @VisibleForTesting
    protected static final int COUNTDOWN_ALARM_CONDITION_INDEX = 2;
    @VisibleForTesting
    protected static final int COUNTDOWN_CONDITION_INDEX = 1;
    private static final boolean DEBUG;
    private static final int DEFAULT_BUCKET_INDEX;
    @VisibleForTesting
    protected static final int FOREVER_CONDITION_INDEX = 0;
    private static final int MAX_BUCKET_MINUTES;
    private static final int[] MINUTE_BUCKETS;
    private static final int MIN_BUCKET_MINUTES;
    private int MAX_MANUAL_DND_OPTIONS;
    private AlarmManager mAlarmManager;
    private boolean mAttached;
    private int mBucketIndex;
    @VisibleForTesting
    protected Context mContext;
    @VisibleForTesting
    protected Uri mForeverId;
    @VisibleForTesting
    protected LayoutInflater mLayoutInflater;
    @VisibleForTesting
    protected NotificationManager mNotificationManager;
    private int mUserId;
    @VisibleForTesting
    protected TextView mZenAlarmWarning;
    private RadioGroup mZenRadioGroup;
    @VisibleForTesting
    protected LinearLayout mZenRadioGroupContent;
    
    static {
        DEBUG = Log.isLoggable("EnableZenModeDialog", 3);
        final int[] a = MINUTE_BUCKETS = ZenModeConfig.MINUTE_BUCKETS;
        MIN_BUCKET_MINUTES = a[0];
        MAX_BUCKET_MINUTES = a[a.length - 1];
        DEFAULT_BUCKET_INDEX = Arrays.binarySearch(a, 60);
    }
    
    public EnableZenModeDialog(final Context mContext) {
        this.mBucketIndex = -1;
        this.MAX_MANUAL_DND_OPTIONS = 3;
        this.mContext = mContext;
    }
    
    private String foreverSummary(final Context context) {
        return context.getString(17041506);
    }
    
    public static Uri getConditionId(final Condition condition) {
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
    
    private void hideAllConditions() {
        for (int childCount = this.mZenRadioGroupContent.getChildCount(), i = 0; i < childCount; ++i) {
            this.mZenRadioGroupContent.getChildAt(i).setVisibility(8);
        }
        this.mZenAlarmWarning.setVisibility(8);
    }
    
    private boolean isForever(final Condition condition) {
        return condition != null && this.mForeverId.equals((Object)condition.id);
    }
    
    private void onClickTimeButton(final View view, final ConditionTag conditionTag, final boolean b, final int n) {
        MetricsLogger.action(this.mContext, 163, b);
        final int length = EnableZenModeDialog.MINUTE_BUCKETS.length;
        final int mBucketIndex = this.mBucketIndex;
        int i = 0;
        final int n2 = -1;
        Condition condition = null;
        Label_0251: {
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
                        final int n3 = EnableZenModeDialog.MINUTE_BUCKETS[mBucketIndex2];
                        final long n4 = currentTimeMillis + 60000 * n3;
                        if ((b && n4 > tryParseCountdownConditionId) || (!b && n4 < tryParseCountdownConditionId)) {
                            this.mBucketIndex = mBucketIndex2;
                            final Condition timeCondition = ZenModeConfig.toTimeCondition(this.mContext, n4, n3, ActivityManager.getCurrentUser(), false);
                            condition = timeCondition;
                            if (timeCondition == null) {
                                final int default_BUCKET_INDEX = EnableZenModeDialog.DEFAULT_BUCKET_INDEX;
                                this.mBucketIndex = default_BUCKET_INDEX;
                                condition = ZenModeConfig.toTimeCondition(this.mContext, EnableZenModeDialog.MINUTE_BUCKETS[default_BUCKET_INDEX], ActivityManager.getCurrentUser());
                            }
                            break Label_0251;
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
            condition = ZenModeConfig.toTimeCondition(this.mContext, EnableZenModeDialog.MINUTE_BUCKETS[max], ActivityManager.getCurrentUser());
        }
        this.bind(condition, view, n);
        this.updateAlarmWarningText(conditionTag.condition);
        conditionTag.rb.setChecked(true);
    }
    
    private static void setToMidnight(final Calendar calendar) {
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
    }
    
    private void updateAlarmWarningText(final Condition condition) {
        final String computeAlarmWarningText = this.computeAlarmWarningText(condition);
        this.mZenAlarmWarning.setText((CharSequence)computeAlarmWarningText);
        final TextView mZenAlarmWarning = this.mZenAlarmWarning;
        int visibility;
        if (computeAlarmWarningText == null) {
            visibility = 8;
        }
        else {
            visibility = 0;
        }
        mZenAlarmWarning.setVisibility(visibility);
    }
    
    private void updateUi(final ConditionTag conditionTag, final View view, final Condition condition, final boolean enabled, int mBucketIndex, final Uri uri) {
        if (conditionTag.lines == null) {
            conditionTag.lines = view.findViewById(16908290);
        }
        if (conditionTag.line1 == null) {
            conditionTag.line1 = (TextView)view.findViewById(16908308);
        }
        if (conditionTag.line2 == null) {
            conditionTag.line2 = (TextView)view.findViewById(16908309);
        }
        String text;
        if (!TextUtils.isEmpty((CharSequence)condition.line1)) {
            text = condition.line1;
        }
        else {
            text = condition.summary;
        }
        final String line2 = condition.line2;
        conditionTag.line1.setText((CharSequence)text);
        final boolean empty = TextUtils.isEmpty((CharSequence)line2);
        final boolean b = false;
        final boolean b2 = false;
        if (empty) {
            conditionTag.line2.setVisibility(8);
        }
        else {
            conditionTag.line2.setVisibility(0);
            conditionTag.line2.setText((CharSequence)line2);
        }
        conditionTag.lines.setEnabled(enabled);
        final View lines = conditionTag.lines;
        final float n = 1.0f;
        float alpha;
        if (enabled) {
            alpha = 1.0f;
        }
        else {
            alpha = 0.4f;
        }
        lines.setAlpha(alpha);
        conditionTag.lines.setOnClickListener((View$OnClickListener)new View$OnClickListener(this) {
            public void onClick(final View view) {
                conditionTag.rb.setChecked(true);
            }
        });
        final ImageView imageView = (ImageView)view.findViewById(16908313);
        imageView.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                EnableZenModeDialog.this.onClickTimeButton(view, conditionTag, false, mBucketIndex);
                conditionTag.lines.setAccessibilityLiveRegion(1);
            }
        });
        final ImageView imageView2 = (ImageView)view.findViewById(16908314);
        imageView2.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                EnableZenModeDialog.this.onClickTimeButton(view, conditionTag, true, mBucketIndex);
                conditionTag.lines.setAccessibilityLiveRegion(1);
            }
        });
        final long tryParseCountdownConditionId = ZenModeConfig.tryParseCountdownConditionId(uri);
        if (mBucketIndex == 1 && tryParseCountdownConditionId > 0L) {
            imageView.setVisibility(0);
            imageView2.setVisibility(0);
            mBucketIndex = this.mBucketIndex;
            if (mBucketIndex > -1) {
                imageView.setEnabled(mBucketIndex > 0);
                boolean enabled2 = b2;
                if (this.mBucketIndex < EnableZenModeDialog.MINUTE_BUCKETS.length - 1) {
                    enabled2 = true;
                }
                imageView2.setEnabled(enabled2);
            }
            else {
                boolean enabled3 = b;
                if (tryParseCountdownConditionId - System.currentTimeMillis() > EnableZenModeDialog.MIN_BUCKET_MINUTES * 60000) {
                    enabled3 = true;
                }
                imageView.setEnabled(enabled3);
                imageView2.setEnabled(Objects.equals(condition.summary, ZenModeConfig.toTimeCondition(this.mContext, EnableZenModeDialog.MAX_BUCKET_MINUTES, ActivityManager.getCurrentUser()).summary) ^ true);
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
                alpha3 = n;
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
    }
    
    @VisibleForTesting
    protected void bind(final Condition condition, final View view, final int n) {
        if (condition != null) {
            final int state = condition.state;
            boolean b = true;
            final boolean enabled = state == 1;
            ConditionTag tag;
            if (view.getTag() != null) {
                tag = (ConditionTag)view.getTag();
            }
            else {
                tag = new ConditionTag();
            }
            view.setTag((Object)tag);
            if (tag.rb != null) {
                b = false;
            }
            if (tag.rb == null) {
                tag.rb = (RadioButton)this.mZenRadioGroup.getChildAt(n);
            }
            tag.condition = condition;
            final Uri conditionId = getConditionId(condition);
            if (EnableZenModeDialog.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("bind i=");
                sb.append(this.mZenRadioGroupContent.indexOfChild(view));
                sb.append(" first=");
                sb.append(b);
                sb.append(" condition=");
                sb.append(conditionId);
                Log.d("EnableZenModeDialog", sb.toString());
            }
            tag.rb.setEnabled(enabled);
            tag.rb.setOnCheckedChangeListener((CompoundButton$OnCheckedChangeListener)new CompoundButton$OnCheckedChangeListener() {
                public void onCheckedChanged(final CompoundButton compoundButton, final boolean b) {
                    if (b) {
                        tag.rb.setChecked(true);
                        if (EnableZenModeDialog.DEBUG) {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("onCheckedChanged ");
                            sb.append(conditionId);
                            Log.d("EnableZenModeDialog", sb.toString());
                        }
                        MetricsLogger.action(EnableZenModeDialog.this.mContext, 164);
                        EnableZenModeDialog.this.updateAlarmWarningText(tag.condition);
                    }
                }
            });
            this.updateUi(tag, view, condition, enabled, n, conditionId);
            view.setVisibility(0);
            return;
        }
        throw new IllegalArgumentException("condition must not be null");
    }
    
    @VisibleForTesting
    protected void bindConditions(final Condition obj) {
        this.bind(this.forever(), this.mZenRadioGroupContent.getChildAt(0), 0);
        if (obj == null) {
            this.bindGenericCountdown();
            this.bindNextAlarm(this.getTimeUntilNextAlarmCondition());
        }
        else if (this.isForever(obj)) {
            this.getConditionTagAt(0).rb.setChecked(true);
            this.bindGenericCountdown();
            this.bindNextAlarm(this.getTimeUntilNextAlarmCondition());
        }
        else if (this.isAlarm(obj)) {
            this.bindGenericCountdown();
            this.bindNextAlarm(obj);
            this.getConditionTagAt(2).rb.setChecked(true);
        }
        else if (this.isCountdown(obj)) {
            this.bindNextAlarm(this.getTimeUntilNextAlarmCondition());
            this.bind(obj, this.mZenRadioGroupContent.getChildAt(1), 1);
            this.getConditionTagAt(1).rb.setChecked(true);
        }
        else {
            final StringBuilder sb = new StringBuilder();
            sb.append("Invalid manual condition: ");
            sb.append(obj);
            Slog.d("EnableZenModeDialog", sb.toString());
        }
    }
    
    @VisibleForTesting
    protected void bindGenericCountdown() {
        final int default_BUCKET_INDEX = EnableZenModeDialog.DEFAULT_BUCKET_INDEX;
        this.mBucketIndex = default_BUCKET_INDEX;
        final Condition timeCondition = ZenModeConfig.toTimeCondition(this.mContext, EnableZenModeDialog.MINUTE_BUCKETS[default_BUCKET_INDEX], ActivityManager.getCurrentUser());
        if (!this.mAttached || this.getConditionTagAt(1).condition == null) {
            this.bind(timeCondition, this.mZenRadioGroupContent.getChildAt(1), 1);
        }
    }
    
    @VisibleForTesting
    protected void bindNextAlarm(final Condition condition) {
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
            visibility = 8;
        }
        child2.setVisibility(visibility);
        int visibility2;
        if (b) {
            visibility2 = n;
        }
        else {
            visibility2 = 8;
        }
        child.setVisibility(visibility2);
    }
    
    @VisibleForTesting
    protected String computeAlarmWarningText(final Condition condition) {
        if ((this.mNotificationManager.getNotificationPolicy().priorityCategories & 0x20) != 0x0) {
            return null;
        }
        final long currentTimeMillis = System.currentTimeMillis();
        final long nextAlarm = this.getNextAlarm();
        if (nextAlarm < currentTimeMillis) {
            return null;
        }
        int n;
        if (condition != null && !this.isForever(condition)) {
            final long tryParseCountdownConditionId = ZenModeConfig.tryParseCountdownConditionId(condition.id);
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
        return this.mContext.getResources().getString(n, new Object[] { this.getTime(nextAlarm, currentTimeMillis) });
    }
    
    public Dialog createDialog() {
        this.mNotificationManager = (NotificationManager)this.mContext.getSystemService("notification");
        this.mForeverId = Condition.newId(this.mContext).appendPath("forever").build();
        this.mAlarmManager = (AlarmManager)this.mContext.getSystemService("alarm");
        this.mUserId = this.mContext.getUserId();
        this.mAttached = false;
        final AlertDialog$Builder setPositiveButton = new AlertDialog$Builder(this.mContext).setTitle(R$string.zen_mode_settings_turn_on_dialog_title).setNegativeButton(R$string.cancel, (DialogInterface$OnClickListener)null).setPositiveButton(R$string.zen_mode_enable_dialog_turn_on, (DialogInterface$OnClickListener)new DialogInterface$OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, int checkedRadioButtonId) {
                checkedRadioButtonId = EnableZenModeDialog.this.mZenRadioGroup.getCheckedRadioButtonId();
                final ConditionTag conditionTag = EnableZenModeDialog.this.getConditionTagAt(checkedRadioButtonId);
                if (EnableZenModeDialog.this.isForever(conditionTag.condition)) {
                    MetricsLogger.action(EnableZenModeDialog.this.mContext, 1259);
                }
                else if (EnableZenModeDialog.this.isAlarm(conditionTag.condition)) {
                    MetricsLogger.action(EnableZenModeDialog.this.mContext, 1261);
                }
                else if (EnableZenModeDialog.this.isCountdown(conditionTag.condition)) {
                    MetricsLogger.action(EnableZenModeDialog.this.mContext, 1260);
                }
                else {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Invalid manual condition: ");
                    sb.append(conditionTag.condition);
                    Slog.d("EnableZenModeDialog", sb.toString());
                }
                final EnableZenModeDialog this$0 = EnableZenModeDialog.this;
                this$0.mNotificationManager.setZenMode(1, this$0.getRealConditionId(conditionTag.condition), "EnableZenModeDialog");
            }
        });
        final View contentView = this.getContentView();
        this.bindConditions(this.forever());
        setPositiveButton.setView(contentView);
        return (Dialog)setPositiveButton.create();
    }
    
    public Condition forever() {
        return new Condition(Condition.newId(this.mContext).appendPath("forever").build(), this.foreverSummary(this.mContext), "", "", 0, 1, 0);
    }
    
    @VisibleForTesting
    protected ConditionTag getConditionTagAt(final int n) {
        return (ConditionTag)this.mZenRadioGroupContent.getChildAt(n).getTag();
    }
    
    protected View getContentView() {
        if (this.mLayoutInflater == null) {
            this.mLayoutInflater = new PhoneWindow(this.mContext).getLayoutInflater();
        }
        final View inflate = this.mLayoutInflater.inflate(R$layout.zen_mode_turn_on_dialog_container, (ViewGroup)null);
        final ScrollView scrollView = (ScrollView)inflate.findViewById(R$id.container);
        this.mZenRadioGroup = (RadioGroup)scrollView.findViewById(R$id.zen_radio_buttons);
        this.mZenRadioGroupContent = (LinearLayout)scrollView.findViewById(R$id.zen_radio_buttons_content);
        this.mZenAlarmWarning = (TextView)scrollView.findViewById(R$id.zen_alarm_warning);
        for (int i = 0; i < this.MAX_MANUAL_DND_OPTIONS; ++i) {
            final View inflate2 = this.mLayoutInflater.inflate(R$layout.zen_mode_radio_button, (ViewGroup)this.mZenRadioGroup, false);
            this.mZenRadioGroup.addView(inflate2);
            inflate2.setId(i);
            final View inflate3 = this.mLayoutInflater.inflate(R$layout.zen_mode_condition, (ViewGroup)this.mZenRadioGroupContent, false);
            inflate3.setId(this.MAX_MANUAL_DND_OPTIONS + i);
            this.mZenRadioGroupContent.addView(inflate3);
        }
        this.hideAllConditions();
        return inflate;
    }
    
    public long getNextAlarm() {
        final AlarmManager$AlarmClockInfo nextAlarmClock = this.mAlarmManager.getNextAlarmClock(this.mUserId);
        long triggerTime;
        if (nextAlarmClock != null) {
            triggerTime = nextAlarmClock.getTriggerTime();
        }
        else {
            triggerTime = 0L;
        }
        return triggerTime;
    }
    
    @VisibleForTesting
    protected String getTime(final long n, final long n2) {
        final boolean b = n - n2 < 86400000L;
        final boolean is24HourFormat = DateFormat.is24HourFormat(this.mContext, ActivityManager.getCurrentUser());
        String s;
        if (b) {
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
        final CharSequence format = DateFormat.format((CharSequence)DateFormat.getBestDateTimePattern(Locale.getDefault(), s), n);
        int n3;
        if (b) {
            n3 = R$string.alarm_template;
        }
        else {
            n3 = R$string.alarm_template_far;
        }
        return this.mContext.getResources().getString(n3, new Object[] { format });
    }
    
    @VisibleForTesting
    protected Condition getTimeUntilNextAlarmCondition() {
        final GregorianCalendar toMidnight = new GregorianCalendar();
        setToMidnight(toMidnight);
        toMidnight.add(5, 6);
        final long nextAlarm = this.getNextAlarm();
        if (nextAlarm > 0L) {
            final GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTimeInMillis(nextAlarm);
            setToMidnight(gregorianCalendar);
            if (toMidnight.compareTo((Calendar)gregorianCalendar) >= 0) {
                return ZenModeConfig.toNextAlarmCondition(this.mContext, nextAlarm, ActivityManager.getCurrentUser());
            }
        }
        return null;
    }
    
    @VisibleForTesting
    protected boolean isAlarm(final Condition condition) {
        return condition != null && ZenModeConfig.isValidCountdownToAlarmConditionId(condition.id);
    }
    
    @VisibleForTesting
    protected boolean isCountdown(final Condition condition) {
        return condition != null && ZenModeConfig.isValidCountdownConditionId(condition.id);
    }
    
    @VisibleForTesting
    protected static class ConditionTag
    {
        public Condition condition;
        public TextView line1;
        public TextView line2;
        public View lines;
        public RadioButton rb;
    }
}
