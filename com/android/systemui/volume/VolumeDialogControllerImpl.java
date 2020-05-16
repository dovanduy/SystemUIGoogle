// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.volume;

import android.os.Message;
import android.media.AudioSystem;
import android.media.IVolumeController$Stub;
import android.database.ContentObserver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import java.io.Serializable;
import android.util.Slog;
import android.media.session.MediaController$PlaybackInfo;
import android.media.session.MediaSession$Token;
import java.util.Map;
import java.util.HashMap;
import android.os.VibrationEffect;
import com.android.systemui.qs.tiles.DndTile;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.os.Looper;
import android.content.ContentResolver;
import android.provider.Settings$Secure;
import android.provider.Settings$Global;
import android.app.NotificationManager$Policy;
import android.service.notification.ZenModeConfig;
import java.util.function.Function;
import android.os.RemoteException;
import android.net.Uri;
import android.media.IVolumeController;
import java.util.Iterator;
import android.util.Log;
import android.text.TextUtils;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager$NameNotFoundException;
import java.util.Objects;
import android.content.ComponentName;
import android.service.notification.Condition;
import android.view.accessibility.AccessibilityManager;
import android.media.IAudioService$Stub;
import android.os.ServiceManager;
import android.os.Handler;
import com.android.systemui.R$string;
import android.media.AudioAttributes$Builder;
import android.os.HandlerThread;
import android.media.VolumePolicy;
import android.os.Vibrator;
import com.android.internal.annotations.GuardedBy;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.Optional;
import android.app.NotificationManager;
import com.android.settingslib.volume.MediaSessions;
import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.media.IAudioService;
import android.media.AudioManager;
import android.util.ArrayMap;
import android.media.AudioAttributes;
import com.android.systemui.Dumpable;
import com.android.systemui.plugins.VolumeDialogController;

public class VolumeDialogControllerImpl implements VolumeDialogController, Dumpable
{
    private static final AudioAttributes SONIFICIATION_VIBRATION_ATTRIBUTES;
    static final ArrayMap<Integer, Integer> STREAMS;
    private static final String TAG;
    private AudioManager mAudio;
    private IAudioService mAudioService;
    protected final BroadcastDispatcher mBroadcastDispatcher;
    protected C mCallbacks;
    private final Context mContext;
    private boolean mDestroyed;
    private final boolean mHasVibrator;
    private long mLastToggledRingerOn;
    private final MediaSessions mMediaSessions;
    protected final MediaSessionsCallbacks mMediaSessionsCallbacksW;
    private final NotificationManager mNoMan;
    private final NotificationManager mNotificationManager;
    private final SettingObserver mObserver;
    private final Receiver mReceiver;
    private boolean mShowA11yStream;
    private boolean mShowDndTile;
    private boolean mShowSafetyWarning;
    private boolean mShowVolumeDialog;
    private final State mState;
    private final Optional<Lazy<StatusBar>> mStatusBarOptionalLazy;
    @GuardedBy({ "this" })
    private UserActivityListener mUserActivityListener;
    private final Vibrator mVibrator;
    protected final VC mVolumeController;
    private VolumePolicy mVolumePolicy;
    private final W mWorker;
    private final HandlerThread mWorkerThread;
    
    static {
        TAG = Util.logTag(VolumeDialogControllerImpl.class);
        SONIFICIATION_VIBRATION_ATTRIBUTES = new AudioAttributes$Builder().setContentType(4).setUsage(13).build();
        (STREAMS = new ArrayMap()).put((Object)4, (Object)R$string.stream_alarm);
        VolumeDialogControllerImpl.STREAMS.put((Object)6, (Object)R$string.stream_bluetooth_sco);
        VolumeDialogControllerImpl.STREAMS.put((Object)8, (Object)R$string.stream_dtmf);
        VolumeDialogControllerImpl.STREAMS.put((Object)3, (Object)R$string.stream_music);
        VolumeDialogControllerImpl.STREAMS.put((Object)10, (Object)R$string.stream_accessibility);
        VolumeDialogControllerImpl.STREAMS.put((Object)5, (Object)R$string.stream_notification);
        VolumeDialogControllerImpl.STREAMS.put((Object)2, (Object)R$string.stream_ring);
        VolumeDialogControllerImpl.STREAMS.put((Object)1, (Object)R$string.stream_system);
        VolumeDialogControllerImpl.STREAMS.put((Object)7, (Object)R$string.stream_system_enforced);
        VolumeDialogControllerImpl.STREAMS.put((Object)9, (Object)R$string.stream_tts);
        VolumeDialogControllerImpl.STREAMS.put((Object)0, (Object)R$string.stream_voice_call);
    }
    
    public VolumeDialogControllerImpl(final Context context, final BroadcastDispatcher mBroadcastDispatcher, final Optional<Lazy<StatusBar>> mStatusBarOptionalLazy) {
        this.mReceiver = new Receiver();
        this.mCallbacks = new C();
        this.mState = new State();
        this.mMediaSessionsCallbacksW = new MediaSessionsCallbacks();
        boolean mHasVibrator = true;
        this.mShowDndTile = true;
        this.mVolumeController = new VC();
        final Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        if (applicationContext.getPackageManager().hasSystemFeature("android.software.leanback")) {
            this.mStatusBarOptionalLazy = Optional.empty();
        }
        else {
            this.mStatusBarOptionalLazy = mStatusBarOptionalLazy;
        }
        this.mNotificationManager = (NotificationManager)this.mContext.getSystemService("notification");
        Events.writeEvent(5, new Object[0]);
        (this.mWorkerThread = new HandlerThread(VolumeDialogControllerImpl.class.getSimpleName())).start();
        this.mWorker = new W(this.mWorkerThread.getLooper());
        this.mMediaSessions = this.createMediaSessions(this.mContext, this.mWorkerThread.getLooper(), this.mMediaSessionsCallbacksW);
        this.mAudio = (AudioManager)this.mContext.getSystemService("audio");
        this.mNoMan = (NotificationManager)this.mContext.getSystemService("notification");
        final SettingObserver mObserver = new SettingObserver(this.mWorker);
        this.mObserver = mObserver;
        this.mBroadcastDispatcher = mBroadcastDispatcher;
        mObserver.init();
        this.mReceiver.init();
        final Vibrator mVibrator = (Vibrator)this.mContext.getSystemService("vibrator");
        this.mVibrator = mVibrator;
        if (mVibrator == null || !mVibrator.hasVibrator()) {
            mHasVibrator = false;
        }
        this.mHasVibrator = mHasVibrator;
        this.mAudioService = IAudioService$Stub.asInterface(ServiceManager.getService("audio"));
        this.mVolumeController.setA11yMode(((AccessibilityManager)context.getSystemService((Class)AccessibilityManager.class)).isAccessibilityVolumeStreamActive() ? 1 : 0);
    }
    
    private boolean checkRoutedToBluetoothW(final int n) {
        boolean b = false;
        if (n == 3) {
            b = (false | this.updateStreamRoutedToBluetoothW(n, (this.mAudio.getDevicesForStream(3) & 0x380) != 0x0));
        }
        return b;
    }
    
    private static String getApplicationName(Context packageName, final ComponentName componentName) {
        if (componentName == null) {
            return null;
        }
        final PackageManager packageManager = packageName.getPackageManager();
        packageName = (Context)componentName.getPackageName();
        try {
            final String trim = Objects.toString(packageManager.getApplicationInfo((String)packageName, 0).loadLabel(packageManager), "").trim();
            if (trim.length() > 0) {
                return trim;
            }
            return (String)packageName;
        }
        catch (PackageManager$NameNotFoundException ex) {
            return (String)packageName;
        }
    }
    
    private static boolean isLogWorthy(final int n) {
        return n == 0 || n == 1 || n == 2 || n == 3 || n == 4 || n == 6;
    }
    
    private static boolean isRinger(final int n) {
        return n == 2 || n == 5;
    }
    
    private void onAccessibilityModeChanged(final Boolean b) {
        this.mCallbacks.onAccessibilityModeChanged(b);
    }
    
    private void onDismissRequestedW(final int n) {
        this.mCallbacks.onDismissRequested(n);
    }
    
    private void onGetCaptionsComponentStateW(final boolean b) {
        final Boolean false = Boolean.FALSE;
        try {
            final String string = this.mContext.getString(17039879);
            if (TextUtils.isEmpty((CharSequence)string)) {
                this.mCallbacks.onCaptionComponentStateChanged(false, b);
                return;
            }
            final boolean bug = D.BUG;
            boolean b2 = false;
            if (bug) {
                Log.i(VolumeDialogControllerImpl.TAG, String.format("isCaptionsServiceEnabled componentNameString=%s", string));
            }
            final ComponentName unflattenFromString = ComponentName.unflattenFromString(string);
            if (unflattenFromString == null) {
                this.mCallbacks.onCaptionComponentStateChanged(false, b);
                return;
            }
            final PackageManager packageManager = this.mContext.getPackageManager();
            final C mCallbacks = this.mCallbacks;
            if (packageManager.getComponentEnabledSetting(unflattenFromString) == 1) {
                b2 = true;
            }
            mCallbacks.onCaptionComponentStateChanged(b2, b);
        }
        catch (Exception ex) {
            Log.e(VolumeDialogControllerImpl.TAG, "isCaptionsServiceEnabled failed to check for captions component", (Throwable)ex);
            this.mCallbacks.onCaptionComponentStateChanged(false, b);
        }
    }
    
    private void onGetStateW() {
        for (final int intValue : VolumeDialogControllerImpl.STREAMS.keySet()) {
            this.updateStreamLevelW(intValue, this.getAudioManagerStreamVolume(intValue));
            this.streamStateW(intValue).levelMin = this.getAudioManagerStreamMinVolume(intValue);
            this.streamStateW(intValue).levelMax = Math.max(1, this.getAudioManagerStreamMaxVolume(intValue));
            this.updateStreamMuteW(intValue, this.mAudio.isStreamMute(intValue));
            final StreamState streamStateW = this.streamStateW(intValue);
            streamStateW.muteSupported = this.mAudio.isStreamAffectedByMute(intValue);
            streamStateW.name = (int)VolumeDialogControllerImpl.STREAMS.get((Object)intValue);
            this.checkRoutedToBluetoothW(intValue);
        }
        this.updateRingerModeExternalW(this.mAudio.getRingerMode());
        this.updateZenModeW();
        this.updateZenConfig();
        this.updateEffectsSuppressorW(this.mNoMan.getEffectsSuppressor());
        this.mCallbacks.onStateChanged(this.mState);
    }
    
    private void onNotifyVisibleW(final boolean b) {
        if (this.mDestroyed) {
            return;
        }
        this.mAudio.notifyVolumeControllerVisible((IVolumeController)this.mVolumeController, b);
        if (!b && this.updateActiveStreamW(-1)) {
            this.mCallbacks.onStateChanged(this.mState);
        }
    }
    
    private void onSetActiveStreamW(final int n) {
        if (this.updateActiveStreamW(n)) {
            this.mCallbacks.onStateChanged(this.mState);
        }
    }
    
    private void onSetExitConditionW(final Condition condition) {
        final NotificationManager mNoMan = this.mNoMan;
        final int zenMode = this.mState.zenMode;
        Uri id;
        if (condition != null) {
            id = condition.id;
        }
        else {
            id = null;
        }
        mNoMan.setZenMode(zenMode, id, VolumeDialogControllerImpl.TAG);
    }
    
    private void onSetRingerModeW(final int n, final boolean b) {
        if (b) {
            this.mAudio.setRingerMode(n);
        }
        else {
            this.mAudio.setRingerModeInternal(n);
        }
    }
    
    private void onSetStreamMuteW(final int n, final boolean b) {
        final AudioManager mAudio = this.mAudio;
        int n2;
        if (b) {
            n2 = -100;
        }
        else {
            n2 = 100;
        }
        mAudio.adjustStreamVolume(n, n2, 0);
    }
    
    private void onSetStreamVolumeW(final int i, final int j) {
        if (D.BUG) {
            final String tag = VolumeDialogControllerImpl.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("onSetStreamVolume ");
            sb.append(i);
            sb.append(" level=");
            sb.append(j);
            Log.d(tag, sb.toString());
        }
        if (i >= 100) {
            this.mMediaSessionsCallbacksW.setStreamVolume(i, j);
            return;
        }
        this.setAudioManagerStreamVolume(i, j, 0);
    }
    
    private void onSetZenModeW(final int i) {
        if (D.BUG) {
            final String tag = VolumeDialogControllerImpl.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("onSetZenModeW ");
            sb.append(i);
            Log.d(tag, sb.toString());
        }
        this.mNoMan.setZenMode(i, (Uri)null, VolumeDialogControllerImpl.TAG);
    }
    
    private void onShowSafetyWarningW(final int n) {
        if (this.mShowSafetyWarning) {
            this.mCallbacks.onShowSafetyWarning(n);
        }
    }
    
    private void onUserActivityW() {
        synchronized (this) {
            if (this.mUserActivityListener != null) {
                this.mUserActivityListener.onUserActivity();
            }
        }
    }
    
    private void playTouchFeedback() {
        if (System.currentTimeMillis() - this.mLastToggledRingerOn >= 1000L) {
            return;
        }
        try {
            this.mAudioService.playSoundEffect(5);
        }
        catch (RemoteException ex) {}
    }
    
    private boolean shouldShowUI(final int n) {
        final Optional<Boolean> map = this.mStatusBarOptionalLazy.map((Function<? super Lazy<StatusBar>, ? extends Boolean>)new _$$Lambda$VolumeDialogControllerImpl$ZC0tNHMtDXeM_l4n7hjZubonHuc(this, n));
        final boolean mShowVolumeDialog = this.mShowVolumeDialog;
        boolean b = true;
        if (!mShowVolumeDialog || (n & 0x1) == 0x0) {
            b = false;
        }
        return map.orElse(b);
    }
    
    private StreamState streamStateW(final int n) {
        StreamState streamState;
        if ((streamState = (StreamState)this.mState.states.get(n)) == null) {
            streamState = new StreamState();
            this.mState.states.put(n, (Object)streamState);
        }
        return streamState;
    }
    
    private boolean updateActiveStreamW(int n) {
        final State mState = this.mState;
        if (n == mState.activeStream) {
            return false;
        }
        mState.activeStream = n;
        Events.writeEvent(2, n);
        if (D.BUG) {
            final String tag = VolumeDialogControllerImpl.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("updateActiveStreamW ");
            sb.append(n);
            Log.d(tag, sb.toString());
        }
        if (n >= 100) {
            n = -1;
        }
        if (D.BUG) {
            final String tag2 = VolumeDialogControllerImpl.TAG;
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("forceVolumeControlStream ");
            sb2.append(n);
            Log.d(tag2, sb2.toString());
        }
        this.mAudio.forceVolumeControlStream(n);
        return true;
    }
    
    private boolean updateEffectsSuppressorW(final ComponentName componentName) {
        if (Objects.equals(this.mState.effectsSuppressor, componentName)) {
            return false;
        }
        final State mState = this.mState;
        mState.effectsSuppressor = componentName;
        mState.effectsSuppressorName = getApplicationName(this.mContext, componentName);
        final State mState2 = this.mState;
        Events.writeEvent(14, mState2.effectsSuppressor, mState2.effectsSuppressorName);
        return true;
    }
    
    private boolean updateRingerModeExternalW(final int n) {
        final State mState = this.mState;
        if (n == mState.ringerModeExternal) {
            return false;
        }
        mState.ringerModeExternal = n;
        Events.writeEvent(12, n);
        return true;
    }
    
    private boolean updateRingerModeInternalW(final int n) {
        final State mState = this.mState;
        if (n == mState.ringerModeInternal) {
            return false;
        }
        mState.ringerModeInternal = n;
        Events.writeEvent(11, n);
        if (this.mState.ringerModeInternal == 2) {
            this.playTouchFeedback();
        }
        return true;
    }
    
    private boolean updateStreamLevelW(final int i, final int n) {
        final StreamState streamStateW = this.streamStateW(i);
        if (streamStateW.level == n) {
            return false;
        }
        streamStateW.level = n;
        if (isLogWorthy(i)) {
            Events.writeEvent(10, i, n);
        }
        return true;
    }
    
    private boolean updateStreamMuteW(final int i, final boolean b) {
        final StreamState streamStateW = this.streamStateW(i);
        if (streamStateW.muted == b) {
            return false;
        }
        streamStateW.muted = b;
        if (isLogWorthy(i)) {
            Events.writeEvent(15, i, b);
        }
        if (b && isRinger(i)) {
            this.updateRingerModeInternalW(this.mAudio.getRingerModeInternal());
        }
        return true;
    }
    
    private boolean updateStreamRoutedToBluetoothW(final int i, final boolean b) {
        final StreamState streamStateW = this.streamStateW(i);
        if (streamStateW.routedToBluetooth == b) {
            return false;
        }
        streamStateW.routedToBluetooth = b;
        if (D.BUG) {
            final String tag = VolumeDialogControllerImpl.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("updateStreamRoutedToBluetoothW stream=");
            sb.append(i);
            sb.append(" routedToBluetooth=");
            sb.append(b);
            Log.d(tag, sb.toString());
        }
        return true;
    }
    
    private boolean updateZenConfig() {
        final NotificationManager$Policy consolidatedNotificationPolicy = this.mNotificationManager.getConsolidatedNotificationPolicy();
        final boolean b = (consolidatedNotificationPolicy.priorityCategories & 0x20) == 0x0;
        final boolean b2 = (consolidatedNotificationPolicy.priorityCategories & 0x40) == 0x0;
        final boolean b3 = (consolidatedNotificationPolicy.priorityCategories & 0x80) == 0x0;
        final boolean allPriorityOnlyRingerSoundsMuted = ZenModeConfig.areAllPriorityOnlyRingerSoundsMuted(consolidatedNotificationPolicy);
        final State mState = this.mState;
        if (mState.disallowAlarms == b && mState.disallowMedia == b2 && mState.disallowRinger == allPriorityOnlyRingerSoundsMuted && mState.disallowSystem == b3) {
            return false;
        }
        final State mState2 = this.mState;
        mState2.disallowAlarms = b;
        mState2.disallowMedia = b2;
        mState2.disallowSystem = b3;
        mState2.disallowRinger = allPriorityOnlyRingerSoundsMuted;
        final StringBuilder sb = new StringBuilder();
        sb.append("disallowAlarms=");
        sb.append(b);
        sb.append(" disallowMedia=");
        sb.append(b2);
        sb.append(" disallowSystem=");
        sb.append(b3);
        sb.append(" disallowRinger=");
        sb.append(allPriorityOnlyRingerSoundsMuted);
        Events.writeEvent(17, sb.toString());
        return true;
    }
    
    private boolean updateZenModeW() {
        final int int1 = Settings$Global.getInt(this.mContext.getContentResolver(), "zen_mode", 0);
        final State mState = this.mState;
        if (mState.zenMode == int1) {
            return false;
        }
        mState.zenMode = int1;
        Events.writeEvent(13, int1);
        return true;
    }
    
    @Override
    public void addCallback(final Callbacks callbacks, final Handler handler) {
        this.mCallbacks.add(callbacks, handler);
        callbacks.onAccessibilityModeChanged(this.mShowA11yStream);
    }
    
    @Override
    public boolean areCaptionsEnabled() {
        final ContentResolver contentResolver = this.mContext.getContentResolver();
        boolean b = false;
        if (Settings$Secure.getIntForUser(contentResolver, "odi_captions_enabled", 0, -2) == 1) {
            b = true;
        }
        return b;
    }
    
    protected MediaSessions createMediaSessions(final Context context, final Looper looper, final MediaSessions.Callbacks callbacks) {
        return new MediaSessions(context, looper, callbacks);
    }
    
    public void dismiss() {
        this.mCallbacks.onDismissRequested(2);
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        final StringBuilder sb = new StringBuilder();
        sb.append(VolumeDialogControllerImpl.class.getSimpleName());
        sb.append(" state:");
        printWriter.println(sb.toString());
        printWriter.print("  mDestroyed: ");
        printWriter.println(this.mDestroyed);
        printWriter.print("  mVolumePolicy: ");
        printWriter.println(this.mVolumePolicy);
        printWriter.print("  mState: ");
        printWriter.println(this.mState.toString(4));
        printWriter.print("  mShowDndTile: ");
        printWriter.println(this.mShowDndTile);
        printWriter.print("  mHasVibrator: ");
        printWriter.println(this.mHasVibrator);
        synchronized (this.mMediaSessionsCallbacksW.mRemoteStreams) {
            printWriter.print("  mRemoteStreams: ");
            printWriter.println(this.mMediaSessionsCallbacksW.mRemoteStreams.values());
            // monitorexit(MediaSessionsCallbacks.access$200(this.mMediaSessionsCallbacksW))
            printWriter.print("  mShowA11yStream: ");
            printWriter.println(this.mShowA11yStream);
            printWriter.println();
            this.mMediaSessions.dump(printWriter);
        }
    }
    
    @Override
    public AudioManager getAudioManager() {
        return this.mAudio;
    }
    
    protected int getAudioManagerStreamMaxVolume(final int n) {
        return this.mAudio.getStreamMaxVolume(n);
    }
    
    protected int getAudioManagerStreamMinVolume(final int n) {
        return this.mAudio.getStreamMinVolumeInt(n);
    }
    
    protected int getAudioManagerStreamVolume(final int n) {
        return this.mAudio.getLastAudibleStreamVolume(n);
    }
    
    @Override
    public void getCaptionsComponentState(final boolean b) {
        if (this.mDestroyed) {
            return;
        }
        this.mWorker.obtainMessage(16, (Object)b).sendToTarget();
    }
    
    @Override
    public void getState() {
        if (this.mDestroyed) {
            return;
        }
        this.mWorker.sendEmptyMessage(3);
    }
    
    @Override
    public boolean hasVibrator() {
        return this.mHasVibrator;
    }
    
    @Override
    public boolean isCaptionStreamOptedOut() {
        return false;
    }
    
    @Override
    public void notifyVisible(final boolean b) {
        if (this.mDestroyed) {
            return;
        }
        this.mWorker.obtainMessage(12, (int)(b ? 1 : 0), 0).sendToTarget();
    }
    
    boolean onVolumeChangedW(final int i, int n) {
        final boolean shouldShowUI = this.shouldShowUI(n);
        final boolean b = (n & 0x1000) != 0x0;
        final boolean b2 = (n & 0x800) != 0x0;
        if ((n & 0x80) != 0x0) {
            n = 1;
        }
        else {
            n = 0;
        }
        final boolean b3 = shouldShowUI && (this.updateActiveStreamW(i) | false);
        final int audioManagerStreamVolume = this.getAudioManagerStreamVolume(i);
        final boolean updateStreamLevelW = this.updateStreamLevelW(i, audioManagerStreamVolume);
        int n2;
        if (shouldShowUI) {
            n2 = 3;
        }
        else {
            n2 = i;
        }
        final boolean b4 = b3 | updateStreamLevelW | this.checkRoutedToBluetoothW(n2);
        if (b4) {
            this.mCallbacks.onStateChanged(this.mState);
        }
        if (shouldShowUI) {
            this.mCallbacks.onShowRequested(1);
        }
        if (b2) {
            this.mCallbacks.onShowVibrateHint();
        }
        if (n != 0) {
            this.mCallbacks.onShowSilentHint();
        }
        if (b4 && b) {
            Events.writeEvent(4, i, audioManagerStreamVolume);
        }
        return b4;
    }
    
    public void register() {
        this.setVolumeController();
        this.setVolumePolicy(this.mVolumePolicy);
        this.showDndTile(this.mShowDndTile);
        try {
            this.mMediaSessions.init();
        }
        catch (SecurityException ex) {
            Log.w(VolumeDialogControllerImpl.TAG, "No access to media sessions", (Throwable)ex);
        }
    }
    
    @Override
    public void removeCallback(final Callbacks callbacks) {
        this.mCallbacks.remove(callbacks);
    }
    
    @Override
    public void scheduleTouchFeedback() {
        this.mLastToggledRingerOn = System.currentTimeMillis();
    }
    
    @Override
    public void setActiveStream(final int n) {
        if (this.mDestroyed) {
            return;
        }
        this.mWorker.obtainMessage(11, n, 0).sendToTarget();
    }
    
    protected void setAudioManagerStreamVolume(final int n, final int n2, final int n3) {
        this.mAudio.setStreamVolume(n, n2, n3);
    }
    
    @Override
    public void setCaptionsEnabled(final boolean b) {
        Settings$Secure.putIntForUser(this.mContext.getContentResolver(), "odi_captions_enabled", (int)(b ? 1 : 0), -2);
    }
    
    public void setEnableDialogs(final boolean mShowVolumeDialog, final boolean mShowSafetyWarning) {
        this.mShowVolumeDialog = mShowVolumeDialog;
        this.mShowSafetyWarning = mShowSafetyWarning;
    }
    
    @Override
    public void setRingerMode(final int n, final boolean b) {
        if (this.mDestroyed) {
            return;
        }
        this.mWorker.obtainMessage(4, n, (int)(b ? 1 : 0)).sendToTarget();
    }
    
    @Override
    public void setStreamVolume(final int n, final int n2) {
        if (this.mDestroyed) {
            return;
        }
        this.mWorker.obtainMessage(10, n, n2).sendToTarget();
    }
    
    public void setUserActivityListener(final UserActivityListener mUserActivityListener) {
        if (this.mDestroyed) {
            return;
        }
        synchronized (this) {
            this.mUserActivityListener = mUserActivityListener;
        }
    }
    
    protected void setVolumeController() {
        try {
            this.mAudio.setVolumeController((IVolumeController)this.mVolumeController);
        }
        catch (SecurityException ex) {
            Log.w(VolumeDialogControllerImpl.TAG, "Unable to set the volume controller", (Throwable)ex);
        }
    }
    
    public void setVolumePolicy(final VolumePolicy volumePolicy) {
        this.mVolumePolicy = volumePolicy;
        if (volumePolicy == null) {
            return;
        }
        try {
            this.mAudio.setVolumePolicy(volumePolicy);
        }
        catch (NoSuchMethodError noSuchMethodError) {
            Log.w(VolumeDialogControllerImpl.TAG, "No volume policy api");
        }
    }
    
    public void showDndTile(final boolean b) {
        if (D.BUG) {
            Log.d(VolumeDialogControllerImpl.TAG, "showDndTile");
        }
        DndTile.setVisible(this.mContext, b);
    }
    
    @Override
    public void userActivity() {
        if (this.mDestroyed) {
            return;
        }
        this.mWorker.removeMessages(13);
        this.mWorker.sendEmptyMessage(13);
    }
    
    @Override
    public void vibrate(final VibrationEffect vibrationEffect) {
        if (this.mHasVibrator) {
            this.mVibrator.vibrate(vibrationEffect, VolumeDialogControllerImpl.SONIFICIATION_VIBRATION_ATTRIBUTES);
        }
    }
    
    class C implements Callbacks
    {
        private final HashMap<Callbacks, Handler> mCallbackMap;
        
        C(final VolumeDialogControllerImpl volumeDialogControllerImpl) {
            this.mCallbackMap = new HashMap<Callbacks, Handler>();
        }
        
        public void add(final Callbacks key, final Handler value) {
            if (key != null && value != null) {
                this.mCallbackMap.put(key, value);
                return;
            }
            throw new IllegalArgumentException();
        }
        
        @Override
        public void onAccessibilityModeChanged(final Boolean b) {
            final boolean b2 = b != null && b;
            for (final Map.Entry<Callbacks, Handler> entry : this.mCallbackMap.entrySet()) {
                entry.getValue().post((Runnable)new Runnable(this) {
                    @Override
                    public void run() {
                        ((Callbacks)entry.getKey()).onAccessibilityModeChanged(b2);
                    }
                });
            }
        }
        
        @Override
        public void onCaptionComponentStateChanged(final Boolean b, final Boolean b2) {
            final boolean b3 = b != null && b;
            for (final Map.Entry<Callbacks, Handler> entry : this.mCallbackMap.entrySet()) {
                entry.getValue().post((Runnable)new _$$Lambda$VolumeDialogControllerImpl$C$Q4oXmUMuqtOXvcXaIdydaXsm_80((Map.Entry)entry, b3, b2));
            }
        }
        
        @Override
        public void onConfigurationChanged() {
            for (final Map.Entry<Callbacks, Handler> entry : this.mCallbackMap.entrySet()) {
                entry.getValue().post((Runnable)new Runnable(this) {
                    @Override
                    public void run() {
                        ((Callbacks)entry.getKey()).onConfigurationChanged();
                    }
                });
            }
        }
        
        @Override
        public void onDismissRequested(final int n) {
            for (final Map.Entry<Callbacks, Handler> entry : this.mCallbackMap.entrySet()) {
                entry.getValue().post((Runnable)new Runnable(this) {
                    @Override
                    public void run() {
                        ((Callbacks)entry.getKey()).onDismissRequested(n);
                    }
                });
            }
        }
        
        @Override
        public void onLayoutDirectionChanged(final int n) {
            for (final Map.Entry<Callbacks, Handler> entry : this.mCallbackMap.entrySet()) {
                entry.getValue().post((Runnable)new Runnable(this) {
                    @Override
                    public void run() {
                        ((Callbacks)entry.getKey()).onLayoutDirectionChanged(n);
                    }
                });
            }
        }
        
        @Override
        public void onScreenOff() {
            for (final Map.Entry<Callbacks, Handler> entry : this.mCallbackMap.entrySet()) {
                entry.getValue().post((Runnable)new Runnable(this) {
                    @Override
                    public void run() {
                        ((Callbacks)entry.getKey()).onScreenOff();
                    }
                });
            }
        }
        
        @Override
        public void onShowRequested(final int n) {
            for (final Map.Entry<Callbacks, Handler> entry : this.mCallbackMap.entrySet()) {
                entry.getValue().post((Runnable)new Runnable(this) {
                    @Override
                    public void run() {
                        ((Callbacks)entry.getKey()).onShowRequested(n);
                    }
                });
            }
        }
        
        @Override
        public void onShowSafetyWarning(final int n) {
            for (final Map.Entry<Callbacks, Handler> entry : this.mCallbackMap.entrySet()) {
                entry.getValue().post((Runnable)new Runnable(this) {
                    @Override
                    public void run() {
                        ((Callbacks)entry.getKey()).onShowSafetyWarning(n);
                    }
                });
            }
        }
        
        @Override
        public void onShowSilentHint() {
            for (final Map.Entry<Callbacks, Handler> entry : this.mCallbackMap.entrySet()) {
                entry.getValue().post((Runnable)new Runnable(this) {
                    @Override
                    public void run() {
                        ((Callbacks)entry.getKey()).onShowSilentHint();
                    }
                });
            }
        }
        
        @Override
        public void onShowVibrateHint() {
            for (final Map.Entry<Callbacks, Handler> entry : this.mCallbackMap.entrySet()) {
                entry.getValue().post((Runnable)new Runnable(this) {
                    @Override
                    public void run() {
                        ((Callbacks)entry.getKey()).onShowVibrateHint();
                    }
                });
            }
        }
        
        @Override
        public void onStateChanged(final State state) {
            final long currentTimeMillis = System.currentTimeMillis();
            final State copy = state.copy();
            for (final Map.Entry<Callbacks, Handler> entry : this.mCallbackMap.entrySet()) {
                entry.getValue().post((Runnable)new Runnable(this) {
                    @Override
                    public void run() {
                        ((Callbacks)entry.getKey()).onStateChanged(copy);
                    }
                });
            }
            Events.writeState(currentTimeMillis, copy);
        }
        
        public void remove(final Callbacks key) {
            this.mCallbackMap.remove(key);
        }
    }
    
    protected final class MediaSessionsCallbacks implements MediaSessions.Callbacks
    {
        private int mNextStream;
        private final HashMap<MediaSession$Token, Integer> mRemoteStreams;
        
        protected MediaSessionsCallbacks() {
            this.mRemoteStreams = new HashMap<MediaSession$Token, Integer>();
            this.mNextStream = 100;
        }
        
        private void addStream(final MediaSession$Token mediaSession$Token, final String str) {
            synchronized (this.mRemoteStreams) {
                if (!this.mRemoteStreams.containsKey(mediaSession$Token)) {
                    this.mRemoteStreams.put(mediaSession$Token, this.mNextStream);
                    final String access$300 = VolumeDialogControllerImpl.TAG;
                    final StringBuilder sb = new StringBuilder();
                    sb.append(str);
                    sb.append(": added stream ");
                    sb.append(this.mNextStream);
                    sb.append(" from token + ");
                    sb.append(mediaSession$Token.toString());
                    Log.d(access$300, sb.toString());
                    ++this.mNextStream;
                }
            }
        }
        
        private MediaSession$Token findToken(final int i) {
            synchronized (this.mRemoteStreams) {
                for (final Map.Entry<MediaSession$Token, Integer> entry : this.mRemoteStreams.entrySet()) {
                    if (entry.getValue().equals(i)) {
                        return entry.getKey();
                    }
                }
                return null;
            }
        }
        
        @Override
        public void onRemoteRemoved(final MediaSession$Token mediaSession$Token) {
            synchronized (this.mRemoteStreams) {
                if (!this.mRemoteStreams.containsKey(mediaSession$Token)) {
                    final String access$300 = VolumeDialogControllerImpl.TAG;
                    final StringBuilder sb = new StringBuilder();
                    sb.append("onRemoteRemoved: stream doesn't exist, aborting remote removed for token:");
                    sb.append(mediaSession$Token.toString());
                    Log.d(access$300, sb.toString());
                    return;
                }
                final int intValue = this.mRemoteStreams.get(mediaSession$Token);
                // monitorexit(this.mRemoteStreams)
                VolumeDialogControllerImpl.this.mState.states.remove(intValue);
                if (VolumeDialogControllerImpl.this.mState.activeStream == intValue) {
                    VolumeDialogControllerImpl.this.updateActiveStreamW(-1);
                }
                final VolumeDialogControllerImpl this$0 = VolumeDialogControllerImpl.this;
                this$0.mCallbacks.onStateChanged(this$0.mState);
            }
        }
        
        @Override
        public void onRemoteUpdate(final MediaSession$Token key, final String str, final MediaController$PlaybackInfo mediaController$PlaybackInfo) {
            this.addStream(key, "onRemoteUpdate");
            Serializable mRemoteStreams = this.mRemoteStreams;
            synchronized (mRemoteStreams) {
                final int intValue = this.mRemoteStreams.get(key);
                // monitorexit(mRemoteStreams)
                final String access$300 = VolumeDialogControllerImpl.TAG;
                mRemoteStreams = new StringBuilder();
                ((StringBuilder)mRemoteStreams).append("onRemoteUpdate: stream: ");
                ((StringBuilder)mRemoteStreams).append(intValue);
                ((StringBuilder)mRemoteStreams).append(" volume: ");
                ((StringBuilder)mRemoteStreams).append(mediaController$PlaybackInfo.getCurrentVolume());
                Slog.d(access$300, ((StringBuilder)mRemoteStreams).toString());
                final int indexOfKey = VolumeDialogControllerImpl.this.mState.states.indexOfKey(intValue);
                final int n = 1;
                int n2;
                if (indexOfKey < 0) {
                    n2 = 1;
                }
                else {
                    n2 = 0;
                }
                final StreamState access$301 = VolumeDialogControllerImpl.this.streamStateW(intValue);
                access$301.dynamic = true;
                access$301.levelMin = 0;
                access$301.levelMax = mediaController$PlaybackInfo.getMaxVolume();
                if (access$301.level != mediaController$PlaybackInfo.getCurrentVolume()) {
                    access$301.level = mediaController$PlaybackInfo.getCurrentVolume();
                    n2 = 1;
                }
                if (!Objects.equals(access$301.remoteLabel, str)) {
                    access$301.name = -1;
                    access$301.remoteLabel = str;
                    n2 = n;
                }
                if (n2 != 0) {
                    final String access$302 = VolumeDialogControllerImpl.TAG;
                    mRemoteStreams = new StringBuilder();
                    ((StringBuilder)mRemoteStreams).append("onRemoteUpdate: ");
                    ((StringBuilder)mRemoteStreams).append(str);
                    ((StringBuilder)mRemoteStreams).append(": ");
                    ((StringBuilder)mRemoteStreams).append(access$301.level);
                    ((StringBuilder)mRemoteStreams).append(" of ");
                    ((StringBuilder)mRemoteStreams).append(access$301.levelMax);
                    Log.d(access$302, ((StringBuilder)mRemoteStreams).toString());
                    final VolumeDialogControllerImpl this$0 = VolumeDialogControllerImpl.this;
                    this$0.mCallbacks.onStateChanged(this$0.mState);
                }
            }
        }
        
        @Override
        public void onRemoteVolumeChanged(final MediaSession$Token key, final int n) {
            this.addStream(key, "onRemoteVolumeChanged");
            Serializable s = this.mRemoteStreams;
            synchronized (s) {
                final int intValue = this.mRemoteStreams.get(key);
                // monitorexit(s)
                final boolean access$3200 = VolumeDialogControllerImpl.this.shouldShowUI(n);
                s = VolumeDialogControllerImpl.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append("onRemoteVolumeChanged: stream: ");
                sb.append(intValue);
                sb.append(" showui? ");
                sb.append(access$3200);
                Slog.d((String)s, sb.toString());
                boolean access$3201;
                final boolean b = access$3201 = VolumeDialogControllerImpl.this.updateActiveStreamW(intValue);
                if (access$3200) {
                    access$3201 = (b | VolumeDialogControllerImpl.this.checkRoutedToBluetoothW(3));
                }
                if (access$3201) {
                    Slog.d(VolumeDialogControllerImpl.TAG, "onRemoteChanged: updatingState");
                    final VolumeDialogControllerImpl this$0 = VolumeDialogControllerImpl.this;
                    this$0.mCallbacks.onStateChanged(this$0.mState);
                }
                if (access$3200) {
                    VolumeDialogControllerImpl.this.mCallbacks.onShowRequested(2);
                }
            }
        }
        
        public void setStreamVolume(final int i, final int n) {
            final MediaSession$Token token = this.findToken(i);
            if (token == null) {
                final String access$300 = VolumeDialogControllerImpl.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append("setStreamVolume: No token found for stream: ");
                sb.append(i);
                Log.w(access$300, sb.toString());
                return;
            }
            VolumeDialogControllerImpl.this.mMediaSessions.setVolume(token, n);
        }
    }
    
    private final class Receiver extends BroadcastReceiver
    {
        public void init() {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
            intentFilter.addAction("android.media.STREAM_DEVICES_CHANGED_ACTION");
            intentFilter.addAction("android.media.RINGER_MODE_CHANGED");
            intentFilter.addAction("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION");
            intentFilter.addAction("android.media.STREAM_MUTE_CHANGED_ACTION");
            intentFilter.addAction("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED");
            intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
            final VolumeDialogControllerImpl this$0 = VolumeDialogControllerImpl.this;
            this$0.mBroadcastDispatcher.registerReceiverWithHandler(this, intentFilter, this$0.mWorker);
        }
        
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            final boolean equals = action.equals("android.media.VOLUME_CHANGED_ACTION");
            final boolean b = false;
            int n;
            if (equals) {
                final int intExtra = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1);
                final int intExtra2 = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", -1);
                final int intExtra3 = intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", -1);
                if (D.BUG) {
                    final String access$300 = VolumeDialogControllerImpl.TAG;
                    final StringBuilder sb = new StringBuilder();
                    sb.append("onReceive VOLUME_CHANGED_ACTION stream=");
                    sb.append(intExtra);
                    sb.append(" level=");
                    sb.append(intExtra2);
                    sb.append(" oldLevel=");
                    sb.append(intExtra3);
                    Log.d(access$300, sb.toString());
                }
                n = (VolumeDialogControllerImpl.this.updateStreamLevelW(intExtra, intExtra2) ? 1 : 0);
            }
            else if (action.equals("android.media.STREAM_DEVICES_CHANGED_ACTION")) {
                final int intExtra4 = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1);
                final int intExtra5 = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_DEVICES", -1);
                final int intExtra6 = intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_DEVICES", -1);
                if (D.BUG) {
                    final String access$301 = VolumeDialogControllerImpl.TAG;
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("onReceive STREAM_DEVICES_CHANGED_ACTION stream=");
                    sb2.append(intExtra4);
                    sb2.append(" devices=");
                    sb2.append(intExtra5);
                    sb2.append(" oldDevices=");
                    sb2.append(intExtra6);
                    Log.d(access$301, sb2.toString());
                }
                n = ((VolumeDialogControllerImpl.this.checkRoutedToBluetoothW(intExtra4) | VolumeDialogControllerImpl.this.onVolumeChangedW(intExtra4, 0)) ? 1 : 0);
            }
            else if (action.equals("android.media.RINGER_MODE_CHANGED")) {
                final int intExtra7 = intent.getIntExtra("android.media.EXTRA_RINGER_MODE", -1);
                if (this.isInitialStickyBroadcast()) {
                    VolumeDialogControllerImpl.this.mState.ringerModeExternal = intExtra7;
                }
                if (D.BUG) {
                    final String access$302 = VolumeDialogControllerImpl.TAG;
                    final StringBuilder sb3 = new StringBuilder();
                    sb3.append("onReceive RINGER_MODE_CHANGED_ACTION rm=");
                    sb3.append(Util.ringerModeToString(intExtra7));
                    Log.d(access$302, sb3.toString());
                }
                n = (VolumeDialogControllerImpl.this.updateRingerModeExternalW(intExtra7) ? 1 : 0);
            }
            else if (action.equals("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION")) {
                final int intExtra8 = intent.getIntExtra("android.media.EXTRA_RINGER_MODE", -1);
                if (this.isInitialStickyBroadcast()) {
                    VolumeDialogControllerImpl.this.mState.ringerModeInternal = intExtra8;
                }
                if (D.BUG) {
                    final String access$303 = VolumeDialogControllerImpl.TAG;
                    final StringBuilder sb4 = new StringBuilder();
                    sb4.append("onReceive INTERNAL_RINGER_MODE_CHANGED_ACTION rm=");
                    sb4.append(Util.ringerModeToString(intExtra8));
                    Log.d(access$303, sb4.toString());
                }
                n = (VolumeDialogControllerImpl.this.updateRingerModeInternalW(intExtra8) ? 1 : 0);
            }
            else if (action.equals("android.media.STREAM_MUTE_CHANGED_ACTION")) {
                final int intExtra9 = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1);
                final boolean booleanExtra = intent.getBooleanExtra("android.media.EXTRA_STREAM_VOLUME_MUTED", false);
                if (D.BUG) {
                    final String access$304 = VolumeDialogControllerImpl.TAG;
                    final StringBuilder sb5 = new StringBuilder();
                    sb5.append("onReceive STREAM_MUTE_CHANGED_ACTION stream=");
                    sb5.append(intExtra9);
                    sb5.append(" muted=");
                    sb5.append(booleanExtra);
                    Log.d(access$304, sb5.toString());
                }
                n = (VolumeDialogControllerImpl.this.updateStreamMuteW(intExtra9, booleanExtra) ? 1 : 0);
            }
            else if (action.equals("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED")) {
                if (D.BUG) {
                    Log.d(VolumeDialogControllerImpl.TAG, "onReceive ACTION_EFFECTS_SUPPRESSOR_CHANGED");
                }
                final VolumeDialogControllerImpl this$0 = VolumeDialogControllerImpl.this;
                n = (this$0.updateEffectsSuppressorW(this$0.mNoMan.getEffectsSuppressor()) ? 1 : 0);
            }
            else if (action.equals("android.intent.action.CONFIGURATION_CHANGED")) {
                if (D.BUG) {
                    Log.d(VolumeDialogControllerImpl.TAG, "onReceive ACTION_CONFIGURATION_CHANGED");
                }
                VolumeDialogControllerImpl.this.mCallbacks.onConfigurationChanged();
                n = (b ? 1 : 0);
            }
            else if (action.equals("android.intent.action.SCREEN_OFF")) {
                if (D.BUG) {
                    Log.d(VolumeDialogControllerImpl.TAG, "onReceive ACTION_SCREEN_OFF");
                }
                VolumeDialogControllerImpl.this.mCallbacks.onScreenOff();
                n = (b ? 1 : 0);
            }
            else {
                n = (b ? 1 : 0);
                if (action.equals("android.intent.action.CLOSE_SYSTEM_DIALOGS")) {
                    if (D.BUG) {
                        Log.d(VolumeDialogControllerImpl.TAG, "onReceive ACTION_CLOSE_SYSTEM_DIALOGS");
                    }
                    VolumeDialogControllerImpl.this.dismiss();
                    n = (b ? 1 : 0);
                }
            }
            if (n != 0) {
                final VolumeDialogControllerImpl this$2 = VolumeDialogControllerImpl.this;
                this$2.mCallbacks.onStateChanged(this$2.mState);
            }
        }
    }
    
    private final class SettingObserver extends ContentObserver
    {
        private final Uri ZEN_MODE_CONFIG_URI;
        private final Uri ZEN_MODE_URI;
        
        public SettingObserver(final Handler handler) {
            super(handler);
            this.ZEN_MODE_URI = Settings$Global.getUriFor("zen_mode");
            this.ZEN_MODE_CONFIG_URI = Settings$Global.getUriFor("zen_mode_config_etag");
        }
        
        public void init() {
            VolumeDialogControllerImpl.this.mContext.getContentResolver().registerContentObserver(this.ZEN_MODE_URI, false, (ContentObserver)this);
            VolumeDialogControllerImpl.this.mContext.getContentResolver().registerContentObserver(this.ZEN_MODE_CONFIG_URI, false, (ContentObserver)this);
        }
        
        public void onChange(final boolean b, final Uri uri) {
            boolean b3;
            final boolean b2 = b3 = (this.ZEN_MODE_URI.equals((Object)uri) && VolumeDialogControllerImpl.this.updateZenModeW());
            if (this.ZEN_MODE_CONFIG_URI.equals((Object)uri)) {
                b3 = (b2 | VolumeDialogControllerImpl.this.updateZenConfig());
            }
            if (b3) {
                final VolumeDialogControllerImpl this$0 = VolumeDialogControllerImpl.this;
                this$0.mCallbacks.onStateChanged(this$0.mState);
            }
        }
    }
    
    public interface UserActivityListener
    {
        void onUserActivity();
    }
    
    private final class VC extends IVolumeController$Stub
    {
        private final String TAG;
        
        private VC() {
            final StringBuilder sb = new StringBuilder();
            sb.append(VolumeDialogControllerImpl.TAG);
            sb.append(".VC");
            this.TAG = sb.toString();
        }
        
        public void dismiss() throws RemoteException {
            if (D.BUG) {
                Log.d(this.TAG, "dismiss requested");
            }
            if (VolumeDialogControllerImpl.this.mDestroyed) {
                return;
            }
            VolumeDialogControllerImpl.this.mWorker.obtainMessage(2, 2, 0).sendToTarget();
            VolumeDialogControllerImpl.this.mWorker.sendEmptyMessage(2);
        }
        
        public void displaySafeVolumeWarning(final int n) throws RemoteException {
            if (D.BUG) {
                final String tag = this.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append("displaySafeVolumeWarning ");
                sb.append(com.android.settingslib.volume.Util.audioManagerFlagsToString(n));
                Log.d(tag, sb.toString());
            }
            if (VolumeDialogControllerImpl.this.mDestroyed) {
                return;
            }
            VolumeDialogControllerImpl.this.mWorker.obtainMessage(14, n, 0).sendToTarget();
        }
        
        public void masterMuteChanged(final int n) throws RemoteException {
            if (D.BUG) {
                Log.d(this.TAG, "masterMuteChanged");
            }
        }
        
        public void setA11yMode(final int n) {
            if (D.BUG) {
                final String tag = this.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append("setA11yMode to ");
                sb.append(n);
                Log.d(tag, sb.toString());
            }
            if (VolumeDialogControllerImpl.this.mDestroyed) {
                return;
            }
            if (n != 0) {
                if (n != 1) {
                    final String tag2 = this.TAG;
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("Invalid accessibility mode ");
                    sb2.append(n);
                    Log.e(tag2, sb2.toString());
                }
                else {
                    VolumeDialogControllerImpl.this.mShowA11yStream = true;
                }
            }
            else {
                VolumeDialogControllerImpl.this.mShowA11yStream = false;
            }
            VolumeDialogControllerImpl.this.mWorker.obtainMessage(15, (Object)VolumeDialogControllerImpl.this.mShowA11yStream).sendToTarget();
        }
        
        public void setLayoutDirection(final int n) throws RemoteException {
            if (D.BUG) {
                Log.d(this.TAG, "setLayoutDirection");
            }
            if (VolumeDialogControllerImpl.this.mDestroyed) {
                return;
            }
            VolumeDialogControllerImpl.this.mWorker.obtainMessage(8, n, 0).sendToTarget();
        }
        
        public void volumeChanged(final int n, final int n2) throws RemoteException {
            if (D.BUG) {
                final String tag = this.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append("volumeChanged ");
                sb.append(AudioSystem.streamToString(n));
                sb.append(" ");
                sb.append(com.android.settingslib.volume.Util.audioManagerFlagsToString(n2));
                Log.d(tag, sb.toString());
            }
            if (VolumeDialogControllerImpl.this.mDestroyed) {
                return;
            }
            VolumeDialogControllerImpl.this.mWorker.obtainMessage(1, n, n2).sendToTarget();
        }
    }
    
    private final class W extends Handler
    {
        W(final Looper looper) {
            super(looper);
        }
        
        public void handleMessage(final Message message) {
            final int what = message.what;
            final boolean b = true;
            final boolean b2 = true;
            boolean b3 = true;
            switch (what) {
                case 16: {
                    VolumeDialogControllerImpl.this.onGetCaptionsComponentStateW((boolean)message.obj);
                    break;
                }
                case 15: {
                    VolumeDialogControllerImpl.this.onAccessibilityModeChanged((Boolean)message.obj);
                    break;
                }
                case 14: {
                    VolumeDialogControllerImpl.this.onShowSafetyWarningW(message.arg1);
                    break;
                }
                case 13: {
                    VolumeDialogControllerImpl.this.onUserActivityW();
                    break;
                }
                case 12: {
                    final VolumeDialogControllerImpl this$0 = VolumeDialogControllerImpl.this;
                    if (message.arg1 == 0) {
                        b3 = false;
                    }
                    this$0.onNotifyVisibleW(b3);
                    break;
                }
                case 11: {
                    VolumeDialogControllerImpl.this.onSetActiveStreamW(message.arg1);
                    break;
                }
                case 10: {
                    VolumeDialogControllerImpl.this.onSetStreamVolumeW(message.arg1, message.arg2);
                    break;
                }
                case 9: {
                    VolumeDialogControllerImpl.this.mCallbacks.onConfigurationChanged();
                    break;
                }
                case 8: {
                    VolumeDialogControllerImpl.this.mCallbacks.onLayoutDirectionChanged(message.arg1);
                    break;
                }
                case 7: {
                    VolumeDialogControllerImpl.this.onSetStreamMuteW(message.arg1, message.arg2 != 0 && b);
                    break;
                }
                case 6: {
                    VolumeDialogControllerImpl.this.onSetExitConditionW((Condition)message.obj);
                    break;
                }
                case 5: {
                    VolumeDialogControllerImpl.this.onSetZenModeW(message.arg1);
                    break;
                }
                case 4: {
                    VolumeDialogControllerImpl.this.onSetRingerModeW(message.arg1, message.arg2 != 0 && b2);
                    break;
                }
                case 3: {
                    VolumeDialogControllerImpl.this.onGetStateW();
                    break;
                }
                case 2: {
                    VolumeDialogControllerImpl.this.onDismissRequestedW(message.arg1);
                    break;
                }
                case 1: {
                    VolumeDialogControllerImpl.this.onVolumeChangedW(message.arg1, message.arg2);
                    break;
                }
            }
        }
    }
}
