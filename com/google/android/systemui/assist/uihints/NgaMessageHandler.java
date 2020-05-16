// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.app.PendingIntent$CanceledException;
import android.content.Context;
import androidx.core.math.MathUtils;
import android.os.Looper;
import java.util.List;
import android.content.Intent;
import android.app.PendingIntent;
import java.util.ArrayList;
import java.util.Iterator;
import android.util.Log;
import android.os.Bundle;
import java.util.Locale;
import android.os.Build;
import android.os.Handler;
import java.util.Set;

public class NgaMessageHandler
{
    private static final boolean VERBOSE;
    private final AssistantPresenceHandler mAssistantPresenceHandler;
    private final Set<AudioInfoListener> mAudioInfoListeners;
    private final Set<CardInfoListener> mCardInfoListeners;
    private final Set<ChipsInfoListener> mChipsInfoListeners;
    private final Set<ClearListener> mClearListeners;
    private final Set<ConfigInfoListener> mConfigInfoListeners;
    private final Set<EdgeLightsInfoListener> mEdgeLightsInfoListeners;
    private final Set<GoBackListener> mGoBackListeners;
    private final Set<GreetingInfoListener> mGreetingInfoListeners;
    private final Handler mHandler;
    private final Set<KeepAliveListener> mKeepAliveListeners;
    private final Set<KeyboardInfoListener> mKeyboardInfoListeners;
    private final NgaUiController mNgaUiController;
    private final Set<StartActivityInfoListener> mStartActivityInfoListeners;
    private final Set<TakeScreenshotListener> mTakeScreenshotListeners;
    private final Set<TranscriptionInfoListener> mTranscriptionInfoListeners;
    private final Set<WarmingListener> mWarmingListeners;
    private final Set<ZerostateInfoListener> mZerostateInfoListeners;
    
    static {
        VERBOSE = (Build.TYPE.toLowerCase(Locale.ROOT).contains("debug") || Build.TYPE.toLowerCase(Locale.ROOT).equals("eng"));
    }
    
    NgaMessageHandler(final NgaUiController mNgaUiController, final AssistantPresenceHandler mAssistantPresenceHandler, final Set<KeepAliveListener> mKeepAliveListeners, final Set<AudioInfoListener> mAudioInfoListeners, final Set<CardInfoListener> mCardInfoListeners, final Set<ConfigInfoListener> mConfigInfoListeners, final Set<EdgeLightsInfoListener> mEdgeLightsInfoListeners, final Set<TranscriptionInfoListener> mTranscriptionInfoListeners, final Set<GreetingInfoListener> mGreetingInfoListeners, final Set<ChipsInfoListener> mChipsInfoListeners, final Set<ClearListener> mClearListeners, final Set<StartActivityInfoListener> mStartActivityInfoListeners, final Set<KeyboardInfoListener> mKeyboardInfoListeners, final Set<ZerostateInfoListener> mZerostateInfoListeners, final Set<GoBackListener> mGoBackListeners, final Set<TakeScreenshotListener> mTakeScreenshotListeners, final Set<WarmingListener> mWarmingListeners, final Handler mHandler) {
        this.mNgaUiController = mNgaUiController;
        this.mAssistantPresenceHandler = mAssistantPresenceHandler;
        this.mKeepAliveListeners = mKeepAliveListeners;
        this.mAudioInfoListeners = mAudioInfoListeners;
        this.mCardInfoListeners = mCardInfoListeners;
        this.mConfigInfoListeners = mConfigInfoListeners;
        this.mEdgeLightsInfoListeners = mEdgeLightsInfoListeners;
        this.mTranscriptionInfoListeners = mTranscriptionInfoListeners;
        this.mGreetingInfoListeners = mGreetingInfoListeners;
        this.mChipsInfoListeners = mChipsInfoListeners;
        this.mClearListeners = mClearListeners;
        this.mStartActivityInfoListeners = mStartActivityInfoListeners;
        this.mKeyboardInfoListeners = mKeyboardInfoListeners;
        this.mZerostateInfoListeners = mZerostateInfoListeners;
        this.mGoBackListeners = mGoBackListeners;
        this.mTakeScreenshotListeners = mTakeScreenshotListeners;
        this.mWarmingListeners = mWarmingListeners;
        this.mHandler = mHandler;
    }
    
    private void logBundle(final Bundle bundle) {
        if (NgaMessageHandler.VERBOSE) {
            if (!"audio_info".equals(bundle.get("action"))) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Contents of NGA Bundle:");
                for (final String anObject : bundle.keySet()) {
                    sb.append("\n   ");
                    sb.append(anObject);
                    sb.append(": ");
                    if ("text".equals(anObject)) {
                        sb.append("(");
                        sb.append(bundle.getString(anObject).length());
                        sb.append(" characters)");
                    }
                    else if ("chips".equals(anObject)) {
                        final ArrayList parcelableArrayList = bundle.getParcelableArrayList("chips");
                        if (parcelableArrayList == null) {
                            continue;
                        }
                        for (final Bundle bundle2 : parcelableArrayList) {
                            sb.append("\n      Chip:");
                            for (final String str : bundle2.keySet()) {
                                sb.append("\n         ");
                                sb.append(str);
                                sb.append(": ");
                                sb.append(bundle2.get(str));
                            }
                        }
                    }
                    else {
                        sb.append(bundle.get(anObject));
                    }
                }
                Log.v("NgaMessageHandler", sb.toString());
            }
        }
    }
    
    private boolean processAlwaysAvailableActions(final String anObject, final Bundle bundle) {
        if ("config".equals(anObject)) {
            final ConfigInfo configInfo = new ConfigInfo(bundle);
            final Iterator<ConfigInfoListener> iterator = this.mConfigInfoListeners.iterator();
            while (iterator.hasNext()) {
                iterator.next().onConfigInfo(configInfo);
            }
            this.mNgaUiController.onUiMessageReceived();
            return true;
        }
        return false;
    }
    
    private boolean processNgaActions(final String s, final Bundle bundle) {
        int n = 0;
        Label_0136: {
            switch (s.hashCode()) {
                case 1124416317: {
                    if (s.equals("warming")) {
                        n = 4;
                        break Label_0136;
                    }
                    break;
                }
                case 777739294: {
                    if (s.equals("take_screenshot")) {
                        n = 3;
                        break Label_0136;
                    }
                    break;
                }
                case 371207756: {
                    if (s.equals("start_activity")) {
                        n = 1;
                        break Label_0136;
                    }
                    break;
                }
                case 192184798: {
                    if (s.equals("go_back")) {
                        n = 2;
                        break Label_0136;
                    }
                    break;
                }
                case 3046160: {
                    if (s.equals("card")) {
                        n = 0;
                        break Label_0136;
                    }
                    break;
                }
            }
            n = -1;
        }
        if (n != 0) {
            if (n != 1) {
                if (n != 2) {
                    if (n != 3) {
                        if (n != 4) {
                            return false;
                        }
                        final WarmingRequest warmingRequest = new WarmingRequest((PendingIntent)bundle.getParcelable("intent"), bundle.getFloat("threshold", 0.1f));
                        final Iterator<WarmingListener> iterator = this.mWarmingListeners.iterator();
                        while (iterator.hasNext()) {
                            iterator.next().onWarmingRequest(warmingRequest);
                        }
                    }
                    else {
                        final PendingIntent pendingIntent = (PendingIntent)bundle.getParcelable("on_finish");
                        final Iterator<TakeScreenshotListener> iterator2 = this.mTakeScreenshotListeners.iterator();
                        while (iterator2.hasNext()) {
                            iterator2.next().onTakeScreenshot(pendingIntent);
                        }
                    }
                }
                else {
                    final Iterator<GoBackListener> iterator3 = this.mGoBackListeners.iterator();
                    while (iterator3.hasNext()) {
                        iterator3.next().onGoBack();
                    }
                }
            }
            else {
                final Intent intent = (Intent)bundle.getParcelable("intent");
                final boolean boolean1 = bundle.getBoolean("dismiss_shade");
                final Iterator<StartActivityInfoListener> iterator4 = this.mStartActivityInfoListeners.iterator();
                while (iterator4.hasNext()) {
                    iterator4.next().onStartActivityInfo(intent, boolean1);
                }
            }
        }
        else {
            final boolean boolean2 = bundle.getBoolean("is_visible");
            final int int1 = bundle.getInt("sysui_color", 0);
            final boolean boolean3 = bundle.getBoolean("animate_transition", true);
            final boolean boolean4 = bundle.getBoolean("card_forces_scrim");
            final Iterator<CardInfoListener> iterator5 = this.mCardInfoListeners.iterator();
            while (iterator5.hasNext()) {
                iterator5.next().onCardInfo(boolean2, int1, boolean3, boolean4);
            }
        }
        return true;
    }
    
    private boolean processSysUiNgaUiActions(String s, final Bundle bundle) {
        final Iterator<KeepAliveListener> iterator = this.mKeepAliveListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onKeepAlive(s);
        }
        int n = -1;
        switch (s.hashCode()) {
            case 1642639251: {
                if (s.equals("keep_alive")) {
                    n = 0;
                    break;
                }
                break;
            }
            case 1549039479: {
                if (s.equals("audio_info")) {
                    n = 1;
                    break;
                }
                break;
            }
            case 771587807: {
                if (s.equals("edge_lights")) {
                    n = 2;
                    break;
                }
                break;
            }
            case 205422649: {
                if (s.equals("greeting")) {
                    n = 4;
                    break;
                }
                break;
            }
            case 94746189: {
                if (s.equals("clear")) {
                    n = 6;
                    break;
                }
                break;
            }
            case 94631335: {
                if (s.equals("chips")) {
                    n = 5;
                    break;
                }
                break;
            }
            case -207201236: {
                if (s.equals("hide_zerostate")) {
                    n = 10;
                    break;
                }
                break;
            }
            case -241763182: {
                if (s.equals("transcription")) {
                    n = 3;
                    break;
                }
                break;
            }
            case -1160605116: {
                if (s.equals("hide_keyboard")) {
                    n = 8;
                    break;
                }
                break;
            }
            case -2040419289: {
                if (s.equals("show_zerostate")) {
                    n = 9;
                    break;
                }
                break;
            }
            case -2051025175: {
                if (s.equals("show_keyboard")) {
                    n = 7;
                    break;
                }
                break;
            }
        }
        while (true) {
            switch (n) {
                default: {
                    return false;
                }
                case 0: {
                    this.mNgaUiController.onUiMessageReceived();
                    return true;
                }
                case 10: {
                    final Iterator<ZerostateInfoListener> iterator2 = this.mZerostateInfoListeners.iterator();
                    while (iterator2.hasNext()) {
                        iterator2.next().onHideZerostate();
                    }
                    continue;
                }
                case 9: {
                    final PendingIntent pendingIntent = (PendingIntent)bundle.getParcelable("tap_action");
                    final Iterator<ZerostateInfoListener> iterator3 = this.mZerostateInfoListeners.iterator();
                    while (iterator3.hasNext()) {
                        iterator3.next().onShowZerostate(pendingIntent);
                    }
                    continue;
                }
                case 8: {
                    final Iterator<KeyboardInfoListener> iterator4 = this.mKeyboardInfoListeners.iterator();
                    while (iterator4.hasNext()) {
                        iterator4.next().onHideKeyboard();
                    }
                    continue;
                }
                case 7: {
                    final PendingIntent pendingIntent2 = (PendingIntent)bundle.getParcelable("tap_action");
                    final Iterator<KeyboardInfoListener> iterator5 = this.mKeyboardInfoListeners.iterator();
                    while (iterator5.hasNext()) {
                        iterator5.next().onShowKeyboard(pendingIntent2);
                    }
                    continue;
                }
                case 6: {
                    final boolean boolean1 = bundle.getBoolean("show_animation", true);
                    final Iterator<ClearListener> iterator6 = this.mClearListeners.iterator();
                    while (iterator6.hasNext()) {
                        iterator6.next().onClear(boolean1);
                    }
                    continue;
                }
                case 5: {
                    final ArrayList parcelableArrayList = bundle.getParcelableArrayList("chips");
                    final Iterator<ChipsInfoListener> iterator7 = this.mChipsInfoListeners.iterator();
                    while (iterator7.hasNext()) {
                        iterator7.next().onChipsInfo(parcelableArrayList);
                    }
                    continue;
                }
                case 4: {
                    s = bundle.getString("text");
                    final PendingIntent pendingIntent3 = (PendingIntent)bundle.getParcelable("tap_action");
                    final Iterator<GreetingInfoListener> iterator8 = this.mGreetingInfoListeners.iterator();
                    while (iterator8.hasNext()) {
                        iterator8.next().onGreetingInfo(s, pendingIntent3);
                    }
                    continue;
                }
                case 3: {
                    final String string = bundle.getString("text");
                    final PendingIntent pendingIntent4 = (PendingIntent)bundle.getParcelable("tap_action");
                    final int int1 = bundle.getInt("text_color");
                    final Iterator<TranscriptionInfoListener> iterator9 = this.mTranscriptionInfoListeners.iterator();
                    while (iterator9.hasNext()) {
                        iterator9.next().onTranscriptionInfo(string, pendingIntent4, int1);
                    }
                    continue;
                }
                case 2: {
                    s = bundle.getString("state", "");
                    final boolean boolean2 = bundle.getBoolean("listening");
                    final Iterator<EdgeLightsInfoListener> iterator10 = this.mEdgeLightsInfoListeners.iterator();
                    while (iterator10.hasNext()) {
                        iterator10.next().onEdgeLightsInfo(s, boolean2);
                    }
                    continue;
                }
                case 1: {
                    final float float1 = bundle.getFloat("volume");
                    final float float2 = bundle.getFloat("speech_confidence");
                    final Iterator<AudioInfoListener> iterator11 = this.mAudioInfoListeners.iterator();
                    while (iterator11.hasNext()) {
                        iterator11.next().onAudioInfo(float1, float2);
                    }
                    continue;
                }
            }
            break;
        }
    }
    
    public void processBundle(final Bundle bundle, final Runnable runnable) {
        if (Looper.myLooper() != this.mHandler.getLooper()) {
            this.mHandler.post((Runnable)new _$$Lambda$NgaMessageHandler$DBnQjPAdeuW2OjHXeNGpzuN_pbw(this, bundle, runnable));
            return;
        }
        this.logBundle(bundle);
        final String string = bundle.getString("action", "");
        if (string.isEmpty()) {
            Log.w("NgaMessageHandler", "No action specified, ignoring");
            return;
        }
        final boolean ngaAssistant = this.mAssistantPresenceHandler.isNgaAssistant();
        final boolean sysUiNgaUi = this.mAssistantPresenceHandler.isSysUiNgaUi();
        final boolean processAlwaysAvailableActions = this.processAlwaysAvailableActions(string, bundle);
        int processSysUiNgaUiActions;
        if ((processSysUiNgaUiActions = (processAlwaysAvailableActions ? 1 : 0)) == 0) {
            processSysUiNgaUiActions = (processAlwaysAvailableActions ? 1 : 0);
            if (ngaAssistant) {
                final boolean processNgaActions = this.processNgaActions(string, bundle);
                if ((processSysUiNgaUiActions = (processNgaActions ? 1 : 0)) == 0) {
                    processSysUiNgaUiActions = (processNgaActions ? 1 : 0);
                    if (sysUiNgaUi) {
                        processSysUiNgaUiActions = (this.processSysUiNgaUiActions(string, bundle) ? 1 : 0);
                    }
                }
            }
        }
        if (processSysUiNgaUiActions == 0) {
            Log.w("NgaMessageHandler", String.format("Invalid action \"%s\" for state:\n  NGA is Assistant = %b\n  SysUI is NGA UI = %b", string, ngaAssistant, sysUiNgaUi));
        }
        runnable.run();
    }
    
    public interface AudioInfoListener
    {
        void onAudioInfo(final float p0, final float p1);
    }
    
    public interface CardInfoListener
    {
        void onCardInfo(final boolean p0, final int p1, final boolean p2, final boolean p3);
    }
    
    public interface ChipsInfoListener
    {
        void onChipsInfo(final List<Bundle> p0);
    }
    
    public interface ClearListener
    {
        void onClear(final boolean p0);
    }
    
    public static class ConfigInfo
    {
        public final PendingIntent configurationCallback;
        public final boolean ngaIsAssistant;
        public PendingIntent onColorChanged;
        public final PendingIntent onKeyboardShowingChange;
        public final PendingIntent onTaskChange;
        public final PendingIntent onTouchInside;
        public final PendingIntent onTouchOutside;
        public final boolean sysUiIsNgaUi;
        
        ConfigInfo(final Bundle bundle) {
            final boolean boolean1 = bundle.getBoolean("is_available");
            final boolean boolean2 = bundle.getBoolean("nga_is_assistant", boolean1);
            this.ngaIsAssistant = boolean2;
            this.sysUiIsNgaUi = (boolean1 && boolean2);
            this.onColorChanged = (PendingIntent)bundle.getParcelable("color_changed");
            this.onTouchOutside = (PendingIntent)bundle.getParcelable("touch_outside");
            this.onTouchInside = (PendingIntent)bundle.getParcelable("touch_inside");
            this.onTaskChange = (PendingIntent)bundle.getParcelable("task_stack_changed");
            this.onKeyboardShowingChange = (PendingIntent)bundle.getParcelable("keyboard_showing_changed");
            this.configurationCallback = (PendingIntent)bundle.getParcelable("configuration");
        }
    }
    
    public interface ConfigInfoListener
    {
        void onConfigInfo(final ConfigInfo p0);
    }
    
    public interface EdgeLightsInfoListener
    {
        void onEdgeLightsInfo(final String p0, final boolean p1);
    }
    
    public interface GoBackListener
    {
        void onGoBack();
    }
    
    public interface GreetingInfoListener
    {
        void onGreetingInfo(final String p0, final PendingIntent p1);
    }
    
    public interface KeepAliveListener
    {
        void onKeepAlive(final String p0);
    }
    
    public interface KeyboardInfoListener
    {
        void onHideKeyboard();
        
        void onShowKeyboard(final PendingIntent p0);
    }
    
    public interface StartActivityInfoListener
    {
        void onStartActivityInfo(final Intent p0, final boolean p1);
    }
    
    public interface TakeScreenshotListener
    {
        void onTakeScreenshot(final PendingIntent p0);
    }
    
    public interface TranscriptionInfoListener
    {
        void onTranscriptionInfo(final String p0, final PendingIntent p1, final int p2);
    }
    
    public interface WarmingListener
    {
        void onWarmingRequest(final WarmingRequest p0);
    }
    
    public static class WarmingRequest
    {
        private final PendingIntent onWarm;
        private final float threshold;
        
        public WarmingRequest(final PendingIntent onWarm, final float n) {
            this.onWarm = onWarm;
            this.threshold = MathUtils.clamp(n, 0.0f, 1.0f);
        }
        
        public float getThreshold() {
            return this.threshold;
        }
        
        public void notify(final Context context, final boolean b) {
            final PendingIntent onWarm = this.onWarm;
            if (onWarm != null) {
                try {
                    onWarm.send(context, 0, new Intent().putExtra("primed", b));
                }
                catch (PendingIntent$CanceledException ex) {
                    Log.e("NgaMessageHandler", "Unable to warm assistant, PendingIntent cancelled", (Throwable)ex);
                }
            }
        }
    }
    
    public interface ZerostateInfoListener
    {
        void onHideZerostate();
        
        void onShowZerostate(final PendingIntent p0);
    }
}
