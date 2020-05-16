// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.settings;

import android.content.ContentResolver;
import android.database.ContentObserver;
import java.util.Iterator;
import android.os.AsyncTask;
import com.android.internal.logging.MetricsLogger;
import android.util.MathUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.display.BrightnessUtils;
import com.android.internal.BrightnessSynchronizer;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.service.vr.IVrManager$Stub;
import android.os.ServiceManager;
import android.os.PowerManager;
import com.android.systemui.Dependency;
import android.os.Looper;
import android.os.Message;
import android.service.vr.IVrStateCallbacks$Stub;
import android.os.RemoteException;
import android.util.Log;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.provider.Settings$System;
import android.service.vr.IVrStateCallbacks;
import android.service.vr.IVrManager;
import android.animation.ValueAnimator;
import android.hardware.display.DisplayManager;
import android.content.Context;
import java.util.ArrayList;
import android.os.Handler;
import android.net.Uri;

public class BrightnessController implements Listener
{
    private static final Uri BRIGHTNESS_FLOAT_URI;
    private static final Uri BRIGHTNESS_FOR_VR_FLOAT_URI;
    private static final Uri BRIGHTNESS_MODE_URI;
    private static final Uri BRIGHTNESS_URI;
    private volatile boolean mAutomatic;
    private final boolean mAutomaticAvailable;
    private final Handler mBackgroundHandler;
    private final BrightnessObserver mBrightnessObserver;
    private ArrayList<BrightnessStateChangeCallback> mChangeCallbacks;
    private final Context mContext;
    private final ToggleSlider mControl;
    private boolean mControlValueInitialized;
    private final float mDefaultBacklight;
    private final float mDefaultBacklightForVr;
    private final DisplayManager mDisplayManager;
    private boolean mExternalChange;
    private final Handler mHandler;
    private volatile boolean mIsVrModeEnabled;
    private boolean mListening;
    private final float mMaximumBacklight;
    private final float mMaximumBacklightForVr;
    private final float mMinimumBacklight;
    private final float mMinimumBacklightForVr;
    private ValueAnimator mSliderAnimator;
    private final Runnable mStartListeningRunnable;
    private final Runnable mStopListeningRunnable;
    private final Runnable mUpdateModeRunnable;
    private final Runnable mUpdateSliderRunnable;
    private final CurrentUserTracker mUserTracker;
    private final IVrManager mVrManager;
    private final IVrStateCallbacks mVrStateCallbacks;
    
    static {
        BRIGHTNESS_MODE_URI = Settings$System.getUriFor("screen_brightness_mode");
        BRIGHTNESS_URI = Settings$System.getUriFor("screen_brightness");
        BRIGHTNESS_FLOAT_URI = Settings$System.getUriFor("screen_brightness_float");
        BRIGHTNESS_FOR_VR_FLOAT_URI = Settings$System.getUriFor("screen_brightness_for_vr_float");
    }
    
    public BrightnessController(final Context mContext, final ToggleSlider mControl, final BroadcastDispatcher broadcastDispatcher) {
        this.mChangeCallbacks = new ArrayList<BrightnessStateChangeCallback>();
        this.mStartListeningRunnable = new Runnable() {
            @Override
            public void run() {
                if (BrightnessController.this.mListening) {
                    return;
                }
                BrightnessController.this.mListening = true;
                if (BrightnessController.this.mVrManager != null) {
                    try {
                        BrightnessController.this.mVrManager.registerListener(BrightnessController.this.mVrStateCallbacks);
                        BrightnessController.this.mIsVrModeEnabled = BrightnessController.this.mVrManager.getVrModeState();
                    }
                    catch (RemoteException ex) {
                        Log.e("StatusBar.BrightnessController", "Failed to register VR mode state listener: ", (Throwable)ex);
                    }
                }
                BrightnessController.this.mBrightnessObserver.startObserving();
                BrightnessController.this.mUserTracker.startTracking();
                BrightnessController.this.mUpdateModeRunnable.run();
                BrightnessController.this.mUpdateSliderRunnable.run();
                BrightnessController.this.mHandler.sendEmptyMessage(3);
            }
        };
        this.mStopListeningRunnable = new Runnable() {
            @Override
            public void run() {
                if (!BrightnessController.this.mListening) {
                    return;
                }
                BrightnessController.this.mListening = false;
                if (BrightnessController.this.mVrManager != null) {
                    try {
                        BrightnessController.this.mVrManager.unregisterListener(BrightnessController.this.mVrStateCallbacks);
                    }
                    catch (RemoteException ex) {
                        Log.e("StatusBar.BrightnessController", "Failed to unregister VR mode state listener: ", (Throwable)ex);
                    }
                }
                BrightnessController.this.mBrightnessObserver.stopObserving();
                BrightnessController.this.mUserTracker.stopTracking();
                BrightnessController.this.mHandler.sendEmptyMessage(4);
            }
        };
        this.mUpdateModeRunnable = new Runnable() {
            @Override
            public void run() {
                final boolean access$1600 = BrightnessController.this.mAutomaticAvailable;
                boolean b = false;
                if (access$1600) {
                    final int intForUser = Settings$System.getIntForUser(BrightnessController.this.mContext.getContentResolver(), "screen_brightness_mode", 0, -2);
                    final BrightnessController this$0 = BrightnessController.this;
                    if (intForUser != 0) {
                        b = true;
                    }
                    this$0.mAutomatic = b;
                }
                else {
                    BrightnessController.this.mHandler.obtainMessage(2, (Object)0).sendToTarget();
                }
            }
        };
        this.mUpdateSliderRunnable = new Runnable() {
            @Override
            public void run() {
                final int access$1200 = BrightnessController.this.mIsVrModeEnabled ? 1 : 0;
                float value;
                if (access$1200 != 0) {
                    value = Settings$System.getFloatForUser(BrightnessController.this.mContext.getContentResolver(), "screen_brightness_for_vr_float", BrightnessController.this.mDefaultBacklightForVr, -2);
                }
                else {
                    value = Settings$System.getFloatForUser(BrightnessController.this.mContext.getContentResolver(), "screen_brightness_float", BrightnessController.this.mDefaultBacklight, -2);
                }
                BrightnessController.this.mHandler.obtainMessage(1, Float.floatToIntBits(value), access$1200).sendToTarget();
            }
        };
        this.mVrStateCallbacks = (IVrStateCallbacks)new IVrStateCallbacks$Stub() {
            public void onVrStateChanged(final boolean b) {
                BrightnessController.this.mHandler.obtainMessage(5, (int)(b ? 1 : 0), 0).sendToTarget();
            }
        };
        this.mHandler = new Handler() {
            public void handleMessage(final Message message) {
                final BrightnessController this$0 = BrightnessController.this;
                final boolean b = true;
                final boolean b2 = true;
                boolean b3 = true;
                this$0.mExternalChange = true;
                try {
                    final int what = message.what;
                    if (what != 1) {
                        if (what != 2) {
                            if (what != 3) {
                                if (what != 4) {
                                    if (what != 5) {
                                        super.handleMessage(message);
                                    }
                                    else {
                                        final BrightnessController this$2 = BrightnessController.this;
                                        if (message.arg1 == 0) {
                                            b3 = false;
                                        }
                                        this$2.updateVrMode(b3);
                                    }
                                }
                                else {
                                    BrightnessController.this.mControl.setOnChangedListener(null);
                                }
                            }
                            else {
                                BrightnessController.this.mControl.setOnChangedListener((ToggleSlider.Listener)BrightnessController.this);
                            }
                        }
                        else {
                            BrightnessController.this.mControl.setChecked(message.arg1 != 0 && b);
                        }
                    }
                    else {
                        BrightnessController.this.updateSlider(Float.intBitsToFloat(message.arg1), message.arg2 != 0 && b2);
                    }
                }
                finally {
                    BrightnessController.this.mExternalChange = false;
                }
            }
        };
        this.mContext = mContext;
        (this.mControl = mControl).setMax(65535);
        this.mBackgroundHandler = new Handler((Looper)Dependency.get(Dependency.BG_LOOPER));
        this.mUserTracker = new CurrentUserTracker(broadcastDispatcher) {
            @Override
            public void onUserSwitched(final int n) {
                BrightnessController.this.mBackgroundHandler.post(BrightnessController.this.mUpdateModeRunnable);
                BrightnessController.this.mBackgroundHandler.post(BrightnessController.this.mUpdateSliderRunnable);
            }
        };
        this.mBrightnessObserver = new BrightnessObserver(this.mHandler);
        final PowerManager powerManager = (PowerManager)mContext.getSystemService((Class)PowerManager.class);
        this.mMinimumBacklight = powerManager.getBrightnessConstraint(0);
        this.mMaximumBacklight = powerManager.getBrightnessConstraint(1);
        this.mDefaultBacklight = powerManager.getBrightnessConstraint(2);
        this.mMinimumBacklightForVr = powerManager.getBrightnessConstraint(5);
        this.mMaximumBacklightForVr = powerManager.getBrightnessConstraint(6);
        this.mDefaultBacklightForVr = powerManager.getBrightnessConstraint(7);
        this.mAutomaticAvailable = mContext.getResources().getBoolean(17891369);
        this.mDisplayManager = (DisplayManager)mContext.getSystemService((Class)DisplayManager.class);
        this.mVrManager = IVrManager$Stub.asInterface(ServiceManager.getService("vrmanager"));
    }
    
    private void animateSliderTo(final int value) {
        if (!this.mControlValueInitialized) {
            this.mControl.setValue(value);
            this.mControlValueInitialized = true;
        }
        final ValueAnimator mSliderAnimator = this.mSliderAnimator;
        if (mSliderAnimator != null && mSliderAnimator.isStarted()) {
            this.mSliderAnimator.cancel();
        }
        (this.mSliderAnimator = ValueAnimator.ofInt(new int[] { this.mControl.getValue(), value })).addUpdateListener((ValueAnimator$AnimatorUpdateListener)new _$$Lambda$BrightnessController$T5g_am3jK_it6CD1eLLpr05aFxc(this));
        this.mSliderAnimator.setDuration((long)(Math.abs(this.mControl.getValue() - value) * 3000 / 65535));
        this.mSliderAnimator.start();
    }
    
    private void setBrightness(final float temporaryBrightness) {
        this.mDisplayManager.setTemporaryBrightness(temporaryBrightness);
    }
    
    private void updateSlider(final float n, final boolean b) {
        float n2;
        float n3;
        if (b) {
            n2 = this.mMinimumBacklightForVr;
            n3 = this.mMaximumBacklightForVr;
        }
        else {
            n2 = this.mMinimumBacklight;
            n3 = this.mMaximumBacklight;
        }
        if (BrightnessSynchronizer.brightnessFloatToInt(this.mContext, n) == BrightnessSynchronizer.brightnessFloatToInt(this.mContext, BrightnessUtils.convertGammaToLinearFloat(this.mControl.getValue(), n2, n3))) {
            return;
        }
        this.animateSliderTo(BrightnessUtils.convertLinearToGammaFloat(n, n2, n3));
    }
    
    private void updateVrMode(final boolean mIsVrModeEnabled) {
        if (this.mIsVrModeEnabled != mIsVrModeEnabled) {
            this.mIsVrModeEnabled = mIsVrModeEnabled;
            this.mBackgroundHandler.post(this.mUpdateSliderRunnable);
        }
    }
    
    public void checkRestrictionAndSetEnabled() {
        this.mBackgroundHandler.post((Runnable)new Runnable() {
            @Override
            public void run() {
                ((ToggleSliderView)BrightnessController.this.mControl).setEnforcedAdmin(RestrictedLockUtilsInternal.checkIfRestrictionEnforced(BrightnessController.this.mContext, "no_config_brightness", BrightnessController.this.mUserTracker.getCurrentUserId()));
            }
        });
    }
    
    @Override
    public void onChanged(final ToggleSlider toggleSlider, final boolean b, final boolean b2, final int n, final boolean b3) {
        if (this.mExternalChange) {
            return;
        }
        final ValueAnimator mSliderAnimator = this.mSliderAnimator;
        if (mSliderAnimator != null) {
            mSliderAnimator.cancel();
        }
        int n2;
        float n3;
        float n4;
        String s;
        if (this.mIsVrModeEnabled) {
            n2 = 498;
            n3 = this.mMinimumBacklightForVr;
            n4 = this.mMaximumBacklightForVr;
            s = "screen_brightness_for_vr_float";
        }
        else {
            if (this.mAutomatic) {
                n2 = 219;
            }
            else {
                n2 = 218;
            }
            n3 = this.mMinimumBacklight;
            n4 = this.mMaximumBacklight;
            s = "screen_brightness_float";
        }
        final float min = MathUtils.min(BrightnessUtils.convertGammaToLinearFloat(n, n3, n4), 1.0f);
        if (b3) {
            final Context mContext = this.mContext;
            MetricsLogger.action(mContext, n2, BrightnessSynchronizer.brightnessFloatToInt(mContext, min));
        }
        this.setBrightness(min);
        if (!b) {
            AsyncTask.execute((Runnable)new Runnable() {
                @Override
                public void run() {
                    Settings$System.putFloatForUser(BrightnessController.this.mContext.getContentResolver(), s, min, -2);
                }
            });
        }
        final Iterator<BrightnessStateChangeCallback> iterator = this.mChangeCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().onBrightnessLevelChanged();
        }
    }
    
    @Override
    public void onInit(final ToggleSlider toggleSlider) {
    }
    
    public void registerCallbacks() {
        this.mBackgroundHandler.post(this.mStartListeningRunnable);
    }
    
    public void unregisterCallbacks() {
        this.mBackgroundHandler.post(this.mStopListeningRunnable);
        this.mControlValueInitialized = false;
    }
    
    private class BrightnessObserver extends ContentObserver
    {
        public BrightnessObserver(final Handler handler) {
            super(handler);
        }
        
        public void onChange(final boolean b) {
            this.onChange(b, null);
        }
        
        public void onChange(final boolean b, final Uri uri) {
            if (b) {
                return;
            }
            if (BrightnessController.BRIGHTNESS_MODE_URI.equals((Object)uri)) {
                BrightnessController.this.mBackgroundHandler.post(BrightnessController.this.mUpdateModeRunnable);
                BrightnessController.this.mBackgroundHandler.post(BrightnessController.this.mUpdateSliderRunnable);
            }
            else if (BrightnessController.BRIGHTNESS_FLOAT_URI.equals((Object)uri)) {
                BrightnessController.this.mBackgroundHandler.post(BrightnessController.this.mUpdateSliderRunnable);
            }
            else if (BrightnessController.BRIGHTNESS_FOR_VR_FLOAT_URI.equals((Object)uri)) {
                BrightnessController.this.mBackgroundHandler.post(BrightnessController.this.mUpdateSliderRunnable);
            }
            else {
                BrightnessController.this.mBackgroundHandler.post(BrightnessController.this.mUpdateModeRunnable);
                BrightnessController.this.mBackgroundHandler.post(BrightnessController.this.mUpdateSliderRunnable);
            }
            final Iterator<BrightnessStateChangeCallback> iterator = BrightnessController.this.mChangeCallbacks.iterator();
            while (iterator.hasNext()) {
                iterator.next().onBrightnessLevelChanged();
            }
        }
        
        public void startObserving() {
            final ContentResolver contentResolver = BrightnessController.this.mContext.getContentResolver();
            contentResolver.unregisterContentObserver((ContentObserver)this);
            contentResolver.registerContentObserver(BrightnessController.BRIGHTNESS_MODE_URI, false, (ContentObserver)this, -1);
            contentResolver.registerContentObserver(BrightnessController.BRIGHTNESS_URI, false, (ContentObserver)this, -1);
            contentResolver.registerContentObserver(BrightnessController.BRIGHTNESS_FLOAT_URI, false, (ContentObserver)this, -1);
            contentResolver.registerContentObserver(BrightnessController.BRIGHTNESS_FOR_VR_FLOAT_URI, false, (ContentObserver)this, -1);
        }
        
        public void stopObserving() {
            BrightnessController.this.mContext.getContentResolver().unregisterContentObserver((ContentObserver)this);
        }
    }
    
    public interface BrightnessStateChangeCallback
    {
        void onBrightnessLevelChanged();
    }
}
