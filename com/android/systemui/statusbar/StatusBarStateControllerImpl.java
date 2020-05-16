// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.text.format.DateFormat;
import android.util.Log;
import java.util.function.Predicate;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.animation.TimeInterpolator;
import android.util.Property;
import android.animation.ObjectAnimator;
import java.util.Collection;
import com.android.systemui.DejankUtils;
import com.android.internal.annotations.GuardedBy;
import java.util.Iterator;
import com.android.systemui.Interpolators;
import java.util.function.ToIntFunction;
import java.util.ArrayList;
import android.view.animation.Interpolator;
import android.animation.ValueAnimator;
import java.util.Comparator;
import android.util.FloatProperty;
import com.android.systemui.Dumpable;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.CallbackController;

public class StatusBarStateControllerImpl implements SysuiStatusBarStateController, CallbackController<StateListener>, Dumpable
{
    private static final FloatProperty<StatusBarStateControllerImpl> SET_DARK_AMOUNT_PROPERTY;
    private static final Comparator<RankedListener> sComparator;
    private ValueAnimator mDarkAnimator;
    private float mDozeAmount;
    private float mDozeAmountTarget;
    private Interpolator mDozeInterpolator;
    private HistoricalState[] mHistoricalRecords;
    private int mHistoryIndex;
    private boolean mIsDozing;
    private boolean mIsFullscreen;
    private boolean mIsImmersive;
    private boolean mKeyguardRequested;
    private int mLastState;
    private boolean mLeaveOpenOnKeyguardHide;
    private final ArrayList<RankedListener> mListeners;
    private boolean mPulsing;
    private int mState;
    
    static {
        sComparator = Comparator.comparingInt((ToIntFunction<? super RankedListener>)_$$Lambda$StatusBarStateControllerImpl$7y8VOe44iFeEd9HPscwVVB7kUfw.INSTANCE);
        SET_DARK_AMOUNT_PROPERTY = new FloatProperty<StatusBarStateControllerImpl>() {
            public Float get(final StatusBarStateControllerImpl statusBarStateControllerImpl) {
                return statusBarStateControllerImpl.mDozeAmount;
            }
            
            public void setValue(final StatusBarStateControllerImpl statusBarStateControllerImpl, final float n) {
                statusBarStateControllerImpl.setDozeAmountInternal(n);
            }
        };
    }
    
    public StatusBarStateControllerImpl() {
        this.mListeners = new ArrayList<RankedListener>();
        int i = 0;
        this.mHistoryIndex = 0;
        this.mHistoricalRecords = new HistoricalState[32];
        this.mIsFullscreen = false;
        this.mIsImmersive = false;
        this.mDozeInterpolator = Interpolators.FAST_OUT_SLOW_IN;
        while (i < 32) {
            this.mHistoricalRecords[i] = new HistoricalState();
            ++i;
        }
    }
    
    @GuardedBy({ "mListeners" })
    private void addListenerInternalLocked(final StateListener obj, final int n) {
        final Iterator<RankedListener> iterator = this.mListeners.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().mListener.equals(obj)) {
                return;
            }
        }
        this.mListeners.add(new RankedListener(obj, n));
        this.mListeners.sort(StatusBarStateControllerImpl.sComparator);
    }
    
    public static String describe(final int n) {
        return StatusBarState.toShortString(n);
    }
    
    private void recordHistoricalState(final int mState, final int mLastState) {
        final int mHistoryIndex = (this.mHistoryIndex + 1) % 32;
        this.mHistoryIndex = mHistoryIndex;
        final HistoricalState historicalState = this.mHistoricalRecords[mHistoryIndex];
        historicalState.mState = mState;
        historicalState.mLastState = mLastState;
        historicalState.mTimestamp = System.currentTimeMillis();
    }
    
    private void setDozeAmountInternal(float interpolation) {
        this.mDozeAmount = interpolation;
        interpolation = this.mDozeInterpolator.getInterpolation(interpolation);
        synchronized (this.mListeners) {
            final StringBuilder sb = new StringBuilder();
            sb.append(this.getClass().getSimpleName());
            sb.append("#setDozeAmount");
            final String string = sb.toString();
            DejankUtils.startDetectingBlockingIpcs(string);
            final Iterator<RankedListener> iterator = new ArrayList<RankedListener>(this.mListeners).iterator();
            while (iterator.hasNext()) {
                iterator.next().mListener.onDozeAmountChanged(this.mDozeAmount, interpolation);
            }
            DejankUtils.stopDetectingBlockingIpcs(string);
        }
    }
    
    private void startDozeAnimation() {
        final float mDozeAmount = this.mDozeAmount;
        if (mDozeAmount == 0.0f || mDozeAmount == 1.0f) {
            Interpolator mDozeInterpolator;
            if (this.mIsDozing) {
                mDozeInterpolator = Interpolators.FAST_OUT_SLOW_IN;
            }
            else {
                mDozeInterpolator = Interpolators.TOUCH_RESPONSE_REVERSE;
            }
            this.mDozeInterpolator = mDozeInterpolator;
        }
        (this.mDarkAnimator = (ValueAnimator)ObjectAnimator.ofFloat((Object)this, (Property)StatusBarStateControllerImpl.SET_DARK_AMOUNT_PROPERTY, new float[] { this.mDozeAmountTarget })).setInterpolator((TimeInterpolator)Interpolators.LINEAR);
        this.mDarkAnimator.setDuration(500L);
        this.mDarkAnimator.start();
    }
    
    @Override
    public void addCallback(final StateListener stateListener) {
        synchronized (this.mListeners) {
            this.addListenerInternalLocked(stateListener, Integer.MAX_VALUE);
        }
    }
    
    @Deprecated
    @Override
    public void addCallback(final StateListener stateListener, final int n) {
        synchronized (this.mListeners) {
            this.addListenerInternalLocked(stateListener, n);
        }
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("StatusBarStateController: ");
        final StringBuilder sb = new StringBuilder();
        sb.append(" mState=");
        sb.append(this.mState);
        sb.append(" (");
        sb.append(describe(this.mState));
        sb.append(")");
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(" mLastState=");
        sb2.append(this.mLastState);
        sb2.append(" (");
        sb2.append(describe(this.mLastState));
        sb2.append(")");
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append(" mLeaveOpenOnKeyguardHide=");
        sb3.append(this.mLeaveOpenOnKeyguardHide);
        printWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append(" mKeyguardRequested=");
        sb4.append(this.mKeyguardRequested);
        printWriter.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append(" mIsDozing=");
        sb5.append(this.mIsDozing);
        printWriter.println(sb5.toString());
        printWriter.println(" Historical states:");
        int i = 0;
        int n = 0;
        while (i < 32) {
            int n2 = n;
            if (this.mHistoricalRecords[i].mTimestamp != 0L) {
                n2 = n + 1;
            }
            ++i;
            n = n2;
        }
        for (int j = this.mHistoryIndex + 32; j >= this.mHistoryIndex + 32 - n + 1; --j) {
            final StringBuilder sb6 = new StringBuilder();
            sb6.append("  (");
            sb6.append(this.mHistoryIndex + 32 - j + 1);
            sb6.append(")");
            sb6.append(this.mHistoricalRecords[j & 0x1F]);
            printWriter.println(sb6.toString());
        }
    }
    
    @Override
    public boolean fromShadeLocked() {
        return this.mLastState == 2;
    }
    
    @Override
    public float getDozeAmount() {
        return this.mDozeAmount;
    }
    
    @Override
    public float getInterpolatedDozeAmount() {
        return this.mDozeInterpolator.getInterpolation(this.mDozeAmount);
    }
    
    @Override
    public int getState() {
        return this.mState;
    }
    
    @Override
    public boolean goingToFullShade() {
        return this.mState == 0 && this.mLeaveOpenOnKeyguardHide;
    }
    
    @Override
    public boolean isDozing() {
        return this.mIsDozing;
    }
    
    @Override
    public boolean isKeyguardRequested() {
        return this.mKeyguardRequested;
    }
    
    @Override
    public boolean leaveOpenOnKeyguardHide() {
        return this.mLeaveOpenOnKeyguardHide;
    }
    
    @Override
    public void removeCallback(final StateListener stateListener) {
        synchronized (this.mListeners) {
            this.mListeners.removeIf(new _$$Lambda$StatusBarStateControllerImpl$TAyHbKlLKq3j8NJBke8nEPo5OK4(stateListener));
        }
    }
    
    @Override
    public void setDozeAmount(final float n, final boolean b) {
        final ValueAnimator mDarkAnimator = this.mDarkAnimator;
        if (mDarkAnimator != null && mDarkAnimator.isRunning()) {
            if (b && this.mDozeAmountTarget == n) {
                return;
            }
            this.mDarkAnimator.cancel();
        }
        this.mDozeAmountTarget = n;
        if (b) {
            this.startDozeAnimation();
        }
        else {
            this.setDozeAmountInternal(n);
        }
    }
    
    @Override
    public void setFullscreenState(final boolean mIsFullscreen, final boolean mIsImmersive) {
        if (this.mIsFullscreen == mIsFullscreen && this.mIsImmersive == mIsImmersive) {
            return;
        }
        this.mIsFullscreen = mIsFullscreen;
        this.mIsImmersive = mIsImmersive;
        synchronized (this.mListeners) {
            final Iterator<RankedListener> iterator = new ArrayList<RankedListener>(this.mListeners).iterator();
            while (iterator.hasNext()) {
                iterator.next().mListener.onFullscreenStateChanged(mIsFullscreen, mIsImmersive);
            }
        }
    }
    
    @Override
    public boolean setIsDozing(final boolean mIsDozing) {
        if (this.mIsDozing == mIsDozing) {
            return false;
        }
        this.mIsDozing = mIsDozing;
        synchronized (this.mListeners) {
            final StringBuilder sb = new StringBuilder();
            sb.append(this.getClass().getSimpleName());
            sb.append("#setIsDozing");
            final String string = sb.toString();
            DejankUtils.startDetectingBlockingIpcs(string);
            final Iterator<RankedListener> iterator = new ArrayList<RankedListener>(this.mListeners).iterator();
            while (iterator.hasNext()) {
                iterator.next().mListener.onDozingChanged(mIsDozing);
            }
            DejankUtils.stopDetectingBlockingIpcs(string);
            return true;
        }
    }
    
    @Override
    public void setKeyguardRequested(final boolean mKeyguardRequested) {
        this.mKeyguardRequested = mKeyguardRequested;
    }
    
    @Override
    public void setLeaveOpenOnKeyguardHide(final boolean mLeaveOpenOnKeyguardHide) {
        this.mLeaveOpenOnKeyguardHide = mLeaveOpenOnKeyguardHide;
    }
    
    @Override
    public void setPulsing(final boolean mPulsing) {
        if (this.mPulsing != mPulsing) {
            this.mPulsing = mPulsing;
            synchronized (this.mListeners) {
                final Iterator<RankedListener> iterator = new ArrayList<RankedListener>(this.mListeners).iterator();
                while (iterator.hasNext()) {
                    iterator.next().mListener.onPulsingChanged(mPulsing);
                }
            }
        }
    }
    
    @Override
    public boolean setState(final int i) {
        if (i <= 3 && i >= 0) {
            final int mState = this.mState;
            if (i == mState) {
                return false;
            }
            this.recordHistoricalState(i, mState);
            if (this.mState == 0 && i == 2) {
                Log.e("SbStateController", "Invalid state transition: SHADE -> SHADE_LOCKED", new Throwable());
            }
            synchronized (this.mListeners) {
                final StringBuilder sb = new StringBuilder();
                sb.append(this.getClass().getSimpleName());
                sb.append("#setState(");
                sb.append(i);
                sb.append(")");
                final String string = sb.toString();
                DejankUtils.startDetectingBlockingIpcs(string);
                final Iterator<RankedListener> iterator = new ArrayList<RankedListener>(this.mListeners).iterator();
                while (iterator.hasNext()) {
                    iterator.next().mListener.onStatePreChange(this.mState, i);
                }
                this.mLastState = this.mState;
                this.mState = i;
                final Iterator<RankedListener> iterator2 = new ArrayList<RankedListener>(this.mListeners).iterator();
                while (iterator2.hasNext()) {
                    iterator2.next().mListener.onStateChanged(this.mState);
                }
                final Iterator<RankedListener> iterator3 = new ArrayList<RankedListener>(this.mListeners).iterator();
                while (iterator3.hasNext()) {
                    iterator3.next().mListener.onStatePostChange();
                }
                DejankUtils.stopDetectingBlockingIpcs(string);
                return true;
            }
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("Invalid state ");
        sb2.append(i);
        throw new IllegalArgumentException(sb2.toString());
    }
    
    private static class HistoricalState
    {
        int mLastState;
        int mState;
        long mTimestamp;
        
        @Override
        public String toString() {
            if (this.mTimestamp != 0L) {
                final StringBuilder sb = new StringBuilder();
                sb.append("state=");
                sb.append(this.mState);
                sb.append(" (");
                sb.append(StatusBarStateControllerImpl.describe(this.mState));
                sb.append(")");
                sb.append("lastState=");
                sb.append(this.mLastState);
                sb.append(" (");
                sb.append(StatusBarStateControllerImpl.describe(this.mLastState));
                sb.append(")");
                sb.append("timestamp=");
                sb.append(DateFormat.format((CharSequence)"MM-dd HH:mm:ss", this.mTimestamp));
                return sb.toString();
            }
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Empty ");
            sb2.append(HistoricalState.class.getSimpleName());
            return sb2.toString();
        }
    }
}
