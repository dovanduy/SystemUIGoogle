// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.keyguard;

import android.content.IntentFilter;
import android.service.notification.ZenModeConfig;
import android.os.Trace;
import androidx.slice.Slice;
import android.database.ContentObserver;
import com.android.systemui.util.wakelock.WakeLock;
import com.android.systemui.SystemUIFactory;
import android.icu.text.DisplayContext;
import java.util.Locale;
import androidx.slice.builders.SliceAction;
import com.android.systemui.R$drawable;
import android.graphics.drawable.Icon;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.builders.ListBuilder;
import java.util.concurrent.TimeUnit;
import android.app.ActivityManager;
import com.android.systemui.R$string;
import android.text.TextUtils;
import android.media.MediaMetadata;
import com.android.systemui.Dependency;
import com.android.keyguard.KeyguardUpdateMonitor;
import java.util.TimeZone;
import android.content.Intent;
import android.content.Context;
import android.text.style.StyleSpan;
import android.app.AlarmManager$OnAlarmListener;
import android.app.PendingIntent;
import android.app.AlarmManager$AlarmClockInfo;
import com.android.systemui.util.wakelock.SettableWakeLock;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import android.content.BroadcastReceiver;
import android.os.Handler;
import com.android.systemui.statusbar.phone.DozeParameters;
import android.icu.text.DateFormat;
import java.util.Date;
import android.content.ContentResolver;
import android.net.Uri;
import android.app.AlarmManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.SystemUIAppComponentFactory;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.statusbar.policy.NextAlarmController;
import androidx.slice.SliceProvider;

public class KeyguardSliceProvider extends SliceProvider implements NextAlarmChangeCallback, Callback, MediaListener, StateListener, ContextInitializer
{
    @VisibleForTesting
    static final int ALARM_VISIBILITY_HOURS = 12;
    private static KeyguardSliceProvider sInstance;
    private static final Object sInstanceLock;
    public AlarmManager mAlarmManager;
    protected final Uri mAlarmUri;
    public ContentResolver mContentResolver;
    private ContextAvailableCallback mContextAvailableCallback;
    private final Date mCurrentTime;
    private DateFormat mDateFormat;
    private String mDatePattern;
    protected final Uri mDateUri;
    protected final Uri mDndUri;
    public DozeParameters mDozeParameters;
    protected boolean mDozing;
    private final Handler mHandler;
    protected final Uri mHeaderUri;
    @VisibleForTesting
    final BroadcastReceiver mIntentReceiver;
    public KeyguardBypassController mKeyguardBypassController;
    @VisibleForTesting
    final KeyguardUpdateMonitorCallback mKeyguardUpdateMonitorCallback;
    private String mLastText;
    private CharSequence mMediaArtist;
    private final Handler mMediaHandler;
    private boolean mMediaIsVisible;
    public NotificationMediaManager mMediaManager;
    private CharSequence mMediaTitle;
    protected final Uri mMediaUri;
    @VisibleForTesting
    protected SettableWakeLock mMediaWakeLock;
    private String mNextAlarm;
    public NextAlarmController mNextAlarmController;
    private AlarmManager$AlarmClockInfo mNextAlarmInfo;
    private PendingIntent mPendingIntent;
    private boolean mRegistered;
    protected final Uri mSliceUri;
    private int mStatusBarState;
    public StatusBarStateController mStatusBarStateController;
    private final AlarmManager$OnAlarmListener mUpdateNextAlarm;
    public ZenModeController mZenModeController;
    
    static {
        new StyleSpan(1);
        sInstanceLock = new Object();
    }
    
    public KeyguardSliceProvider() {
        this.mCurrentTime = new Date();
        this.mUpdateNextAlarm = (AlarmManager$OnAlarmListener)new _$$Lambda$KeyguardSliceProvider$IhzByd8TsqFuOrSyuGurVskyPLo(this);
        this.mIntentReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                final String action = intent.getAction();
                if ("android.intent.action.DATE_CHANGED".equals(action)) {
                    synchronized (this) {
                        KeyguardSliceProvider.this.updateClockLocked();
                        return;
                    }
                }
                if ("android.intent.action.LOCALE_CHANGED".equals(action)) {
                    synchronized (this) {
                        KeyguardSliceProvider.this.cleanDateFormatLocked();
                    }
                }
            }
        };
        this.mKeyguardUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onTimeChanged() {
                synchronized (this) {
                    KeyguardSliceProvider.this.updateClockLocked();
                }
            }
            
            @Override
            public void onTimeZoneChanged(final TimeZone timeZone) {
                synchronized (this) {
                    KeyguardSliceProvider.this.cleanDateFormatLocked();
                }
            }
        };
        this.mHandler = new Handler();
        this.mMediaHandler = new Handler();
        this.mSliceUri = Uri.parse("content://com.android.systemui.keyguard/main");
        this.mHeaderUri = Uri.parse("content://com.android.systemui.keyguard/header");
        this.mDateUri = Uri.parse("content://com.android.systemui.keyguard/date");
        this.mAlarmUri = Uri.parse("content://com.android.systemui.keyguard/alarm");
        this.mDndUri = Uri.parse("content://com.android.systemui.keyguard/dnd");
        this.mMediaUri = Uri.parse("content://com.android.systemui.keyguard/media");
    }
    
    public static KeyguardSliceProvider getAttachedInstance() {
        return KeyguardSliceProvider.sInstance;
    }
    
    private KeyguardUpdateMonitor getKeyguardUpdateMonitor() {
        return Dependency.get(KeyguardUpdateMonitor.class);
    }
    
    private void updateMediaStateLocked(final MediaMetadata mediaMetadata, final int n) {
        final boolean playingState = NotificationMediaManager.isPlayingState(n);
        final CharSequence charSequence = null;
        CharSequence mMediaTitle;
        if (mediaMetadata != null) {
            if (TextUtils.isEmpty(mMediaTitle = mediaMetadata.getText("android.media.metadata.TITLE"))) {
                mMediaTitle = this.getContext().getResources().getString(R$string.music_controls_no_title);
            }
        }
        else {
            mMediaTitle = null;
        }
        CharSequence text;
        if (mediaMetadata == null) {
            text = charSequence;
        }
        else {
            text = mediaMetadata.getText("android.media.metadata.ARTIST");
        }
        if (playingState == this.mMediaIsVisible && TextUtils.equals(mMediaTitle, this.mMediaTitle) && TextUtils.equals(text, this.mMediaArtist)) {
            return;
        }
        this.mMediaTitle = mMediaTitle;
        this.mMediaArtist = text;
        this.mMediaIsVisible = playingState;
        this.notifyChange();
    }
    
    private void updateNextAlarm() {
        synchronized (this) {
            if (this.withinNHoursLocked(this.mNextAlarmInfo, 12)) {
                String s;
                if (android.text.format.DateFormat.is24HourFormat(this.getContext(), ActivityManager.getCurrentUser())) {
                    s = "HH:mm";
                }
                else {
                    s = "h:mm";
                }
                this.mNextAlarm = android.text.format.DateFormat.format((CharSequence)s, this.mNextAlarmInfo.getTriggerTime()).toString();
            }
            else {
                this.mNextAlarm = "";
            }
            // monitorexit(this)
            this.notifyChange();
        }
    }
    
    private boolean withinNHoursLocked(final AlarmManager$AlarmClockInfo alarmManager$AlarmClockInfo, final int n) {
        boolean b = false;
        if (alarmManager$AlarmClockInfo == null) {
            return false;
        }
        if (this.mNextAlarmInfo.getTriggerTime() <= System.currentTimeMillis() + TimeUnit.HOURS.toMillis(n)) {
            b = true;
        }
        return b;
    }
    
    protected void addMediaLocked(final ListBuilder listBuilder) {
        if (TextUtils.isEmpty(this.mMediaTitle)) {
            return;
        }
        final ListBuilder.HeaderBuilder header = new ListBuilder.HeaderBuilder(this.mHeaderUri);
        header.setTitle(this.mMediaTitle);
        listBuilder.setHeader(header);
        if (!TextUtils.isEmpty(this.mMediaArtist)) {
            final ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder(this.mMediaUri);
            rowBuilder.setTitle(this.mMediaArtist);
            final NotificationMediaManager mMediaManager = this.mMediaManager;
            final IconCompat iconCompat = null;
            Icon mediaIcon;
            if (mMediaManager == null) {
                mediaIcon = null;
            }
            else {
                mediaIcon = mMediaManager.getMediaIcon();
            }
            IconCompat fromIcon;
            if (mediaIcon == null) {
                fromIcon = iconCompat;
            }
            else {
                fromIcon = IconCompat.createFromIcon(this.getContext(), mediaIcon);
            }
            if (fromIcon != null) {
                rowBuilder.addEndItem(fromIcon, 0);
            }
            listBuilder.addRow(rowBuilder);
        }
    }
    
    protected void addNextAlarmLocked(final ListBuilder listBuilder) {
        if (TextUtils.isEmpty((CharSequence)this.mNextAlarm)) {
            return;
        }
        final IconCompat withResource = IconCompat.createWithResource(this.getContext(), R$drawable.ic_access_alarms_big);
        final ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder(this.mAlarmUri);
        rowBuilder.setTitle(this.mNextAlarm);
        rowBuilder.addEndItem(withResource, 0);
        listBuilder.addRow(rowBuilder);
    }
    
    protected void addPrimaryActionLocked(final ListBuilder listBuilder) {
        final SliceAction deeplink = SliceAction.createDeeplink(this.mPendingIntent, IconCompat.createWithResource(this.getContext(), R$drawable.ic_access_alarms_big), 0, this.mLastText);
        final ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder(Uri.parse("content://com.android.systemui.keyguard/action"));
        rowBuilder.setPrimaryAction(deeplink);
        listBuilder.addRow(rowBuilder);
    }
    
    protected void addZenModeLocked(final ListBuilder listBuilder) {
        if (!this.isDndOn()) {
            return;
        }
        final ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder(this.mDndUri);
        rowBuilder.setContentDescription(this.getContext().getResources().getString(R$string.accessibility_quick_settings_dnd));
        rowBuilder.addEndItem(IconCompat.createWithResource(this.getContext(), R$drawable.stat_sys_dnd), 0);
        listBuilder.addRow(rowBuilder);
    }
    
    @VisibleForTesting
    void cleanDateFormatLocked() {
        this.mDateFormat = null;
    }
    
    protected String getFormattedDateLocked() {
        if (this.mDateFormat == null) {
            final DateFormat instanceForSkeleton = DateFormat.getInstanceForSkeleton(this.mDatePattern, Locale.getDefault());
            instanceForSkeleton.setContext(DisplayContext.CAPITALIZATION_FOR_STANDALONE);
            this.mDateFormat = instanceForSkeleton;
        }
        this.mCurrentTime.setTime(System.currentTimeMillis());
        return this.mDateFormat.format(this.mCurrentTime);
    }
    
    @VisibleForTesting
    protected void inject() {
        SystemUIFactory.getInstance().getRootComponent().inject(this);
        this.mMediaWakeLock = new SettableWakeLock(WakeLock.createPartial(this.getContext(), "media"), "media");
    }
    
    protected boolean isDndOn() {
        return this.mZenModeController.getZen() != 0;
    }
    
    @VisibleForTesting
    boolean isRegistered() {
        synchronized (this) {
            return this.mRegistered;
        }
    }
    
    protected boolean needsMediaLocked() {
        final KeyguardBypassController mKeyguardBypassController = this.mKeyguardBypassController;
        final boolean b = true;
        final boolean b2 = mKeyguardBypassController != null && mKeyguardBypassController.getBypassEnabled() && this.mDozeParameters.getAlwaysOn();
        final boolean b3 = this.mStatusBarState == 0 && this.mMediaIsVisible;
        if (!TextUtils.isEmpty(this.mMediaTitle) && this.mMediaIsVisible) {
            boolean b4 = b;
            if (this.mDozing) {
                return b4;
            }
            b4 = b;
            if (b2) {
                return b4;
            }
            if (b3) {
                b4 = b;
                return b4;
            }
        }
        return false;
    }
    
    protected void notifyChange() {
        this.mContentResolver.notifyChange(this.mSliceUri, (ContentObserver)null);
    }
    
    @Override
    public Slice onBindSlice(final Uri uri) {
        Trace.beginSection("KeyguardSliceProvider#onBindSlice");
        synchronized (this) {
            final ListBuilder listBuilder = new ListBuilder(this.getContext(), this.mSliceUri, -1L);
            if (this.needsMediaLocked()) {
                this.addMediaLocked(listBuilder);
            }
            else {
                final ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder(this.mDateUri);
                rowBuilder.setTitle(this.mLastText);
                listBuilder.addRow(rowBuilder);
            }
            this.addNextAlarmLocked(listBuilder);
            this.addZenModeLocked(listBuilder);
            this.addPrimaryActionLocked(listBuilder);
            final Slice build = listBuilder.build();
            // monitorexit(this)
            Trace.endSection();
            return build;
        }
    }
    
    @Override
    public void onConfigChanged(final ZenModeConfig zenModeConfig) {
        this.notifyChange();
    }
    
    @Override
    public boolean onCreateSliceProvider() {
        this.mContextAvailableCallback.onContextAvailable(this.getContext());
        this.inject();
        synchronized (KeyguardSliceProvider.sInstanceLock) {
            final KeyguardSliceProvider sInstance = KeyguardSliceProvider.sInstance;
            if (sInstance != null) {
                sInstance.onDestroy();
            }
            this.mDatePattern = this.getContext().getString(R$string.system_ui_aod_date_pattern);
            this.mPendingIntent = PendingIntent.getActivity(this.getContext(), 0, new Intent(this.getContext(), (Class)KeyguardSliceProvider.class), 0);
            this.mMediaManager.addCallback((NotificationMediaManager.MediaListener)this);
            this.mStatusBarStateController.addCallback((StatusBarStateController.StateListener)this);
            this.mNextAlarmController.addCallback((NextAlarmController.NextAlarmChangeCallback)this);
            this.mZenModeController.addCallback((ZenModeController.Callback)this);
            (KeyguardSliceProvider.sInstance = this).registerClockUpdate();
            this.updateClockLocked();
            return true;
        }
    }
    
    @VisibleForTesting
    protected void onDestroy() {
        synchronized (KeyguardSliceProvider.sInstanceLock) {
            this.mNextAlarmController.removeCallback((NextAlarmController.NextAlarmChangeCallback)this);
            this.mZenModeController.removeCallback((ZenModeController.Callback)this);
            this.mMediaWakeLock.setAcquired(false);
            this.mAlarmManager.cancel(this.mUpdateNextAlarm);
            if (this.mRegistered) {
                this.mRegistered = false;
                this.getKeyguardUpdateMonitor().removeCallback(this.mKeyguardUpdateMonitorCallback);
                this.getContext().unregisterReceiver(this.mIntentReceiver);
            }
            KeyguardSliceProvider.sInstance = null;
        }
    }
    
    @Override
    public void onDozingChanged(final boolean mDozing) {
        synchronized (this) {
            final boolean needsMediaLocked = this.needsMediaLocked();
            this.mDozing = mDozing;
            final boolean b = needsMediaLocked != this.needsMediaLocked();
            // monitorexit(this)
            if (b) {
                this.notifyChange();
            }
        }
    }
    
    @Override
    public void onMetadataOrStateChanged(final MediaMetadata mediaMetadata, final int n) {
        synchronized (this) {
            final boolean playingState = NotificationMediaManager.isPlayingState(n);
            this.mMediaHandler.removeCallbacksAndMessages((Object)null);
            if (this.mMediaIsVisible && !playingState && this.mStatusBarState != 0) {
                this.mMediaWakeLock.setAcquired(true);
                this.mMediaHandler.postDelayed((Runnable)new _$$Lambda$KeyguardSliceProvider$nRbfFxAPvCUbdEsypLUXXuYm6z0(this, mediaMetadata, n), 2000L);
            }
            else {
                this.mMediaWakeLock.setAcquired(false);
                this.updateMediaStateLocked(mediaMetadata, n);
            }
        }
    }
    
    @Override
    public void onNextAlarmChanged(final AlarmManager$AlarmClockInfo mNextAlarmInfo) {
        synchronized (this) {
            this.mNextAlarmInfo = mNextAlarmInfo;
            this.mAlarmManager.cancel(this.mUpdateNextAlarm);
            long n;
            if (this.mNextAlarmInfo == null) {
                n = -1L;
            }
            else {
                n = this.mNextAlarmInfo.getTriggerTime() - TimeUnit.HOURS.toMillis(12L);
            }
            if (n > 0L) {
                this.mAlarmManager.setExact(1, n, "lock_screen_next_alarm", this.mUpdateNextAlarm, this.mHandler);
            }
            // monitorexit(this)
            this.updateNextAlarm();
        }
    }
    
    @Override
    public void onStateChanged(int mStatusBarState) {
        synchronized (this) {
            final boolean needsMediaLocked = this.needsMediaLocked();
            this.mStatusBarState = mStatusBarState;
            if (needsMediaLocked != this.needsMediaLocked()) {
                mStatusBarState = 1;
            }
            else {
                mStatusBarState = 0;
            }
            // monitorexit(this)
            if (mStatusBarState != 0) {
                this.notifyChange();
            }
        }
    }
    
    @Override
    public void onZenChanged(final int n) {
        this.notifyChange();
    }
    
    @VisibleForTesting
    protected void registerClockUpdate() {
        synchronized (this) {
            if (this.mRegistered) {
                return;
            }
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.DATE_CHANGED");
            intentFilter.addAction("android.intent.action.LOCALE_CHANGED");
            this.getContext().registerReceiver(this.mIntentReceiver, intentFilter, (String)null, (Handler)null);
            this.getKeyguardUpdateMonitor().registerCallback(this.mKeyguardUpdateMonitorCallback);
            this.mRegistered = true;
        }
    }
    
    @Override
    public void setContextAvailableCallback(final ContextAvailableCallback mContextAvailableCallback) {
        this.mContextAvailableCallback = mContextAvailableCallback;
    }
    
    protected void updateClockLocked() {
        final String formattedDateLocked = this.getFormattedDateLocked();
        if (!formattedDateLocked.equals(this.mLastText)) {
            this.mLastText = formattedDateLocked;
            this.notifyChange();
        }
    }
}
