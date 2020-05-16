// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import java.util.Iterator;
import android.app.ITransientNotificationCallback;
import android.hardware.biometrics.IBiometricServiceReceiverInternal;
import android.os.Bundle;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.internal.view.AppearanceRegion;
import android.os.Message;
import com.android.internal.os.SomeArgs;
import android.content.ComponentName;
import android.os.IBinder;
import android.hardware.display.DisplayManager;
import android.os.Looper;
import android.content.Context;
import com.android.systemui.tracing.ProtoTracer;
import android.os.Handler;
import android.util.Pair;
import android.util.SparseArray;
import java.util.ArrayList;
import android.hardware.display.DisplayManager$DisplayListener;
import com.android.systemui.statusbar.policy.CallbackController;
import com.android.internal.statusbar.IStatusBar$Stub;

public class CommandQueue extends IStatusBar$Stub implements CallbackController<Callbacks>, DisplayManager$DisplayListener
{
    private ArrayList<Callbacks> mCallbacks;
    private SparseArray<Pair<Integer, Integer>> mDisplayDisabled;
    private Handler mHandler;
    private int mLastUpdatedImeDisplayId;
    private final Object mLock;
    private ProtoTracer mProtoTracer;
    
    public CommandQueue(final Context context, final ProtoTracer mProtoTracer) {
        this.mLock = new Object();
        this.mCallbacks = new ArrayList<Callbacks>();
        this.mHandler = new H(Looper.getMainLooper());
        this.mDisplayDisabled = (SparseArray<Pair<Integer, Integer>>)new SparseArray();
        this.mLastUpdatedImeDisplayId = -1;
        this.mProtoTracer = mProtoTracer;
        ((DisplayManager)context.getSystemService((Class)DisplayManager.class)).registerDisplayListener((DisplayManager$DisplayListener)this, this.mHandler);
        this.setDisabled(0, 0, 0);
    }
    
    private Pair<Integer, Integer> getDisabled(final int n) {
        Pair pair;
        if ((pair = (Pair)this.mDisplayDisabled.get(n)) == null) {
            pair = new Pair((Object)0, (Object)0);
            this.mDisplayDisabled.put(n, (Object)pair);
        }
        return (Pair<Integer, Integer>)pair;
    }
    
    private int getDisabled1(final int n) {
        return (int)this.getDisabled(n).first;
    }
    
    private int getDisabled2(final int n) {
        return (int)this.getDisabled(n).second;
    }
    
    private void handleShowImeButton(final int mLastUpdatedImeDisplayId, final IBinder binder, final int n, final int n2, final boolean b, final boolean b2) {
        if (mLastUpdatedImeDisplayId == -1) {
            return;
        }
        if (!b2) {
            final int mLastUpdatedImeDisplayId2 = this.mLastUpdatedImeDisplayId;
            if (mLastUpdatedImeDisplayId2 != mLastUpdatedImeDisplayId && mLastUpdatedImeDisplayId2 != -1) {
                this.sendImeInvisibleStatusForPrevNavBar();
            }
        }
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            this.mCallbacks.get(i).setImeWindowStatus(mLastUpdatedImeDisplayId, binder, n, n2, b);
        }
        this.mLastUpdatedImeDisplayId = mLastUpdatedImeDisplayId;
    }
    
    private void sendImeInvisibleStatusForPrevNavBar() {
        for (int i = 0; i < this.mCallbacks.size(); ++i) {
            this.mCallbacks.get(i).setImeWindowStatus(this.mLastUpdatedImeDisplayId, null, 4, 0, false);
        }
    }
    
    private void setDisabled(final int n, final int i, final int j) {
        this.mDisplayDisabled.put(n, (Object)new Pair((Object)i, (Object)j));
    }
    
    public void abortTransient(final int n, final int[] array) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(3211264, n, 0, (Object)array).sendToTarget();
        }
    }
    
    public void addCallback(final Callbacks e) {
        this.mCallbacks.add(e);
        for (int i = 0; i < this.mDisplayDisabled.size(); ++i) {
            final int key = this.mDisplayDisabled.keyAt(i);
            e.disable(key, this.getDisabled1(key), this.getDisabled2(key), false);
        }
    }
    
    public void addQsTile(final ComponentName componentName) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(1769472, (Object)componentName).sendToTarget();
        }
    }
    
    public void animateCollapsePanels() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(262144);
            this.mHandler.obtainMessage(262144, 0, 0).sendToTarget();
        }
    }
    
    public void animateCollapsePanels(final int n, final boolean b) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(262144);
            final Handler mHandler = this.mHandler;
            int n2;
            if (b) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            mHandler.obtainMessage(262144, n, n2).sendToTarget();
        }
    }
    
    public void animateExpandNotificationsPanel() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(196608);
            this.mHandler.sendEmptyMessage(196608);
        }
    }
    
    public void animateExpandSettingsPanel(final String s) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(327680);
            this.mHandler.obtainMessage(327680, (Object)s).sendToTarget();
        }
    }
    
    public void appTransitionCancelled(final int n) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(1310720, n, 0).sendToTarget();
        }
    }
    
    public void appTransitionFinished(final int n) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(2031616, n, 0).sendToTarget();
        }
    }
    
    public void appTransitionPending(final int n) {
        this.appTransitionPending(n, false);
    }
    
    public void appTransitionPending(final int n, final boolean b) {
        synchronized (this.mLock) {
            final Handler mHandler = this.mHandler;
            int n2;
            if (b) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            mHandler.obtainMessage(1245184, n, n2).sendToTarget();
        }
    }
    
    public void appTransitionStarting(final int n, final long n2, final long n3) {
        this.appTransitionStarting(n, n2, n3, false);
    }
    
    public void appTransitionStarting(int n, final long l, final long i, final boolean b) {
        synchronized (this.mLock) {
            final SomeArgs obtain = SomeArgs.obtain();
            obtain.argi1 = n;
            if (b) {
                n = 1;
            }
            else {
                n = 0;
            }
            obtain.argi2 = n;
            obtain.arg1 = l;
            obtain.arg2 = i;
            this.mHandler.obtainMessage(1376256, (Object)obtain).sendToTarget();
        }
    }
    
    public void cancelPreloadRecentApps() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(720896);
            this.mHandler.obtainMessage(720896, 0, 0, (Object)null).sendToTarget();
        }
    }
    
    public void clickQsTile(final ComponentName componentName) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(1900544, (Object)componentName).sendToTarget();
        }
    }
    
    public void disable(final int n, final int n2, final int n3) {
        this.disable(n, n2, n3, true);
    }
    
    public void disable(int n, final int argi2, final int argi3, final boolean b) {
        synchronized (this.mLock) {
            this.setDisabled(n, argi2, argi3);
            this.mHandler.removeMessages(131072);
            final SomeArgs obtain = SomeArgs.obtain();
            obtain.argi1 = n;
            obtain.argi2 = argi2;
            obtain.argi3 = argi3;
            if (b) {
                n = 1;
            }
            else {
                n = 0;
            }
            obtain.argi4 = n;
            final Message obtainMessage = this.mHandler.obtainMessage(131072, (Object)obtain);
            if (Looper.myLooper() == this.mHandler.getLooper()) {
                this.mHandler.handleMessage(obtainMessage);
                obtainMessage.recycle();
            }
            else {
                obtainMessage.sendToTarget();
            }
        }
    }
    
    public void dismissInattentiveSleepWarning(final boolean b) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(3407872, (Object)b).sendToTarget();
        }
    }
    
    public void dismissKeyboardShortcutsMenu() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(2097152);
            this.mHandler.obtainMessage(2097152).sendToTarget();
        }
    }
    
    public void handleSystemKey(final int n) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(2162688, n, 0).sendToTarget();
        }
    }
    
    public void hideAuthenticationDialog() {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(2818048).sendToTarget();
        }
    }
    
    public void hideRecentApps(final boolean b, final boolean b2) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(917504);
            final Handler mHandler = this.mHandler;
            int n;
            if (b2) {
                n = 1;
            }
            else {
                n = 0;
            }
            mHandler.obtainMessage(917504, (int)(b ? 1 : 0), n, (Object)null).sendToTarget();
        }
    }
    
    public void hideToast(final String arg1, final IBinder arg2) {
        synchronized (this.mLock) {
            final SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = arg1;
            obtain.arg2 = arg2;
            this.mHandler.obtainMessage(3538944, (Object)obtain).sendToTarget();
        }
    }
    
    public void onBiometricAuthenticated() {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(2621440).sendToTarget();
        }
    }
    
    public void onBiometricError(final int argi1, final int argi2, final int argi3) {
        synchronized (this.mLock) {
            final SomeArgs obtain = SomeArgs.obtain();
            obtain.argi1 = argi1;
            obtain.argi2 = argi2;
            obtain.argi3 = argi3;
            this.mHandler.obtainMessage(2752512, (Object)obtain).sendToTarget();
        }
    }
    
    public void onBiometricHelp(final String s) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(2686976, (Object)s).sendToTarget();
        }
    }
    
    public void onCameraLaunchGestureDetected(final int n) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(1572864);
            this.mHandler.obtainMessage(1572864, n, 0).sendToTarget();
        }
    }
    
    public void onDisplayAdded(final int n) {
    }
    
    public void onDisplayChanged(final int n) {
    }
    
    public void onDisplayReady(final int n) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(458752, n, 0).sendToTarget();
        }
    }
    
    public void onDisplayRemoved(final int n) {
        synchronized (this.mLock) {
            this.mDisplayDisabled.remove(n);
            // monitorexit(this.mLock)
            for (int i = this.mCallbacks.size() - 1; i >= 0; --i) {
                this.mCallbacks.get(i).onDisplayRemoved(n);
            }
        }
    }
    
    public void onProposedRotationChanged(final int n, final boolean b) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(2490368);
            final Handler mHandler = this.mHandler;
            int n2;
            if (b) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            mHandler.obtainMessage(2490368, n, n2, (Object)null).sendToTarget();
        }
    }
    
    public void onRecentsAnimationStateChanged(final boolean b) {
        synchronized (this.mLock) {
            final Handler mHandler = this.mHandler;
            int n;
            if (b) {
                n = 1;
            }
            else {
                n = 0;
            }
            mHandler.obtainMessage(3080192, n, 0).sendToTarget();
        }
    }
    
    public void onSystemBarAppearanceChanged(int n, final int argi2, final AppearanceRegion[] arg1, final boolean b) {
        synchronized (this.mLock) {
            final SomeArgs obtain = SomeArgs.obtain();
            obtain.argi1 = n;
            obtain.argi2 = argi2;
            if (b) {
                n = 1;
            }
            else {
                n = 0;
            }
            obtain.argi3 = n;
            obtain.arg1 = arg1;
            this.mHandler.obtainMessage(393216, (Object)obtain).sendToTarget();
        }
    }
    
    public boolean panelsEnabled() {
        final boolean b = false;
        final int disabled1 = this.getDisabled1(0);
        final int disabled2 = this.getDisabled2(0);
        boolean b2 = b;
        if ((disabled1 & 0x10000) == 0x0) {
            b2 = b;
            if ((disabled2 & 0x4) == 0x0) {
                b2 = b;
                if (!StatusBar.ONLY_CORE_APPS) {
                    b2 = true;
                }
            }
        }
        return b2;
    }
    
    public void preloadRecentApps() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(655360);
            this.mHandler.obtainMessage(655360, 0, 0, (Object)null).sendToTarget();
        }
    }
    
    public void recomputeDisableFlags(final int n, final boolean b) {
        this.disable(n, this.getDisabled1(n), this.getDisabled2(n), b);
    }
    
    public void remQsTile(final ComponentName componentName) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(1835008, (Object)componentName).sendToTarget();
        }
    }
    
    public void removeCallback(final Callbacks o) {
        this.mCallbacks.remove(o);
    }
    
    public void removeIcon(final String s) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(65536, 2, 0, (Object)s).sendToTarget();
        }
    }
    
    public void setIcon(final String s, final StatusBarIcon statusBarIcon) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(65536, 1, 0, (Object)new Pair((Object)s, (Object)statusBarIcon)).sendToTarget();
        }
    }
    
    public void setImeWindowStatus(int argi5, final IBinder arg1, int argi6, final int argi7, final boolean b, final boolean b2) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(524288);
            final SomeArgs obtain = SomeArgs.obtain();
            obtain.argi1 = argi5;
            obtain.argi2 = argi6;
            obtain.argi3 = argi7;
            argi6 = 1;
            if (b) {
                argi5 = 1;
            }
            else {
                argi5 = 0;
            }
            obtain.argi4 = argi5;
            if (b2) {
                argi5 = argi6;
            }
            else {
                argi5 = 0;
            }
            obtain.argi5 = argi5;
            obtain.arg1 = arg1;
            this.mHandler.obtainMessage(524288, (Object)obtain).sendToTarget();
        }
    }
    
    public void setTopAppHidesStatusBar(final boolean b) {
        this.mHandler.removeMessages(2424832);
        this.mHandler.obtainMessage(2424832, (int)(b ? 1 : 0), 0).sendToTarget();
    }
    
    public void setWindowState(final int n, final int n2, final int i) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(786432, n, n2, (Object)i).sendToTarget();
        }
    }
    
    public void showAssistDisclosure() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(1441792);
            this.mHandler.obtainMessage(1441792).sendToTarget();
        }
    }
    
    public void showAuthenticationDialog(final Bundle arg1, final IBiometricServiceReceiverInternal arg2, final int argi1, final boolean b, final int argi2, final String arg3, final long l) {
        synchronized (this.mLock) {
            final SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = arg1;
            obtain.arg2 = arg2;
            obtain.argi1 = argi1;
            obtain.arg3 = b;
            obtain.argi2 = argi2;
            obtain.arg4 = arg3;
            obtain.arg5 = l;
            this.mHandler.obtainMessage(2555904, (Object)obtain).sendToTarget();
        }
    }
    
    public void showGlobalActionsMenu() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(2228224);
            this.mHandler.obtainMessage(2228224).sendToTarget();
        }
    }
    
    public void showInattentiveSleepWarning() {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(3342336).sendToTarget();
        }
    }
    
    public void showPictureInPictureMenu() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(1703936);
            this.mHandler.obtainMessage(1703936).sendToTarget();
        }
    }
    
    public void showPinningEnterExitToast(final boolean b) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(2949120, (Object)b).sendToTarget();
        }
    }
    
    public void showPinningEscapeToast() {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(3014656).sendToTarget();
        }
    }
    
    public void showRecentApps(final boolean b) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(851968);
            final Handler mHandler = this.mHandler;
            int n;
            if (b) {
                n = 1;
            }
            else {
                n = 0;
            }
            mHandler.obtainMessage(851968, n, 0, (Object)null).sendToTarget();
        }
    }
    
    public void showScreenPinningRequest(final int n) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(1179648, n, 0, (Object)null).sendToTarget();
        }
    }
    
    public void showShutdownUi(final boolean b, final String s) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(2359296);
            final Handler mHandler = this.mHandler;
            int n;
            if (b) {
                n = 1;
            }
            else {
                n = 0;
            }
            mHandler.obtainMessage(2359296, n, 0, (Object)s).sendToTarget();
        }
    }
    
    public void showToast(final int argi1, final String arg1, final IBinder arg2, final CharSequence arg3, final IBinder arg4, final int argi2, final ITransientNotificationCallback arg5) {
        synchronized (this.mLock) {
            final SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = arg1;
            obtain.arg2 = arg2;
            obtain.arg3 = arg3;
            obtain.arg4 = arg4;
            obtain.arg5 = arg5;
            obtain.argi1 = argi1;
            obtain.argi2 = argi2;
            this.mHandler.obtainMessage(3473408, (Object)obtain).sendToTarget();
        }
    }
    
    public void showTransient(final int n, final int[] array) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(3145728, n, 0, (Object)array).sendToTarget();
        }
    }
    
    public void showWirelessChargingAnimation(final int n) {
        this.mHandler.removeMessages(2883584);
        this.mHandler.obtainMessage(2883584, n, 0).sendToTarget();
    }
    
    public void startAssist(final Bundle bundle) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(1507328);
            this.mHandler.obtainMessage(1507328, (Object)bundle).sendToTarget();
        }
    }
    
    public void startTracing() {
        synchronized (this.mLock) {
            if (this.mProtoTracer != null) {
                this.mProtoTracer.start();
            }
            this.mHandler.obtainMessage(3604480, (Object)Boolean.TRUE).sendToTarget();
        }
    }
    
    public void stopTracing() {
        synchronized (this.mLock) {
            if (this.mProtoTracer != null) {
                this.mProtoTracer.stop();
            }
            this.mHandler.obtainMessage(3604480, (Object)Boolean.FALSE).sendToTarget();
        }
    }
    
    public void suppressAmbientDisplay(final boolean b) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(3670016, (Object)b).sendToTarget();
        }
    }
    
    public void toggleKeyboardShortcutsMenu(final int n) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(1638400);
            this.mHandler.obtainMessage(1638400, n, 0).sendToTarget();
        }
    }
    
    public void togglePanel() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(2293760);
            this.mHandler.obtainMessage(2293760, 0, 0).sendToTarget();
        }
    }
    
    public void toggleRecentApps() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(589824);
            final Message obtainMessage = this.mHandler.obtainMessage(589824, 0, 0, (Object)null);
            obtainMessage.setAsynchronous(true);
            obtainMessage.sendToTarget();
        }
    }
    
    public void toggleSplitScreen() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(1966080);
            this.mHandler.obtainMessage(1966080, 0, 0, (Object)null).sendToTarget();
        }
    }
    
    public void topAppWindowChanged(int argi3, final boolean b, final boolean b2) {
        synchronized (this.mLock) {
            final SomeArgs obtain = SomeArgs.obtain();
            obtain.argi1 = argi3;
            final int n = 1;
            if (b) {
                argi3 = 1;
            }
            else {
                argi3 = 0;
            }
            obtain.argi2 = argi3;
            if (b2) {
                argi3 = n;
            }
            else {
                argi3 = 0;
            }
            obtain.argi3 = argi3;
            this.mHandler.obtainMessage(3276800, (Object)obtain).sendToTarget();
        }
    }
    
    public interface Callbacks
    {
        default void abortTransient(final int n, final int[] array) {
        }
        
        default void addQsTile(final ComponentName componentName) {
        }
        
        default void animateCollapsePanels(final int n, final boolean b) {
        }
        
        default void animateExpandNotificationsPanel() {
        }
        
        default void animateExpandSettingsPanel(final String s) {
        }
        
        default void appTransitionCancelled(final int n) {
        }
        
        default void appTransitionFinished(final int n) {
        }
        
        default void appTransitionPending(final int n, final boolean b) {
        }
        
        default void appTransitionStarting(final int n, final long n2, final long n3, final boolean b) {
        }
        
        default void cancelPreloadRecentApps() {
        }
        
        default void clickTile(final ComponentName componentName) {
        }
        
        default void disable(final int n, final int n2, final int n3, final boolean b) {
        }
        
        default void dismissInattentiveSleepWarning(final boolean b) {
        }
        
        default void dismissKeyboardShortcutsMenu() {
        }
        
        default void handleShowGlobalActionsMenu() {
        }
        
        default void handleShowShutdownUi(final boolean b, final String s) {
        }
        
        default void handleSystemKey(final int n) {
        }
        
        default void hideAuthenticationDialog() {
        }
        
        default void hideRecentApps(final boolean b, final boolean b2) {
        }
        
        default void hideToast(final String s, final IBinder binder) {
        }
        
        default void onBiometricAuthenticated() {
        }
        
        default void onBiometricError(final int n, final int n2, final int n3) {
        }
        
        default void onBiometricHelp(final String s) {
        }
        
        default void onCameraLaunchGestureDetected(final int n) {
        }
        
        default void onDisplayReady(final int n) {
        }
        
        default void onDisplayRemoved(final int n) {
        }
        
        default void onRecentsAnimationStateChanged(final boolean b) {
        }
        
        default void onRotationProposal(final int n, final boolean b) {
        }
        
        default void onSystemBarAppearanceChanged(final int n, final int n2, final AppearanceRegion[] array, final boolean b) {
        }
        
        default void onTracingStateChanged(final boolean b) {
        }
        
        default void preloadRecentApps() {
        }
        
        default void remQsTile(final ComponentName componentName) {
        }
        
        default void removeIcon(final String s) {
        }
        
        default void setIcon(final String s, final StatusBarIcon statusBarIcon) {
        }
        
        default void setImeWindowStatus(final int n, final IBinder binder, final int n2, final int n3, final boolean b) {
        }
        
        default void setTopAppHidesStatusBar(final boolean b) {
        }
        
        default void setWindowState(final int n, final int n2, final int n3) {
        }
        
        default void showAssistDisclosure() {
        }
        
        default void showAuthenticationDialog(final Bundle bundle, final IBiometricServiceReceiverInternal biometricServiceReceiverInternal, final int n, final boolean b, final int n2, final String s, final long n3) {
        }
        
        default void showInattentiveSleepWarning() {
        }
        
        default void showPictureInPictureMenu() {
        }
        
        default void showPinningEnterExitToast(final boolean b) {
        }
        
        default void showPinningEscapeToast() {
        }
        
        default void showRecentApps(final boolean b) {
        }
        
        default void showScreenPinningRequest(final int n) {
        }
        
        default void showToast(final int n, final String s, final IBinder binder, final CharSequence charSequence, final IBinder binder2, final int n2, final ITransientNotificationCallback transientNotificationCallback) {
        }
        
        default void showTransient(final int n, final int[] array) {
        }
        
        default void showWirelessChargingAnimation(final int n) {
        }
        
        default void startAssist(final Bundle bundle) {
        }
        
        default void suppressAmbientDisplay(final boolean b) {
        }
        
        default void toggleKeyboardShortcutsMenu(final int n) {
        }
        
        default void togglePanel() {
        }
        
        default void toggleRecentApps() {
        }
        
        default void toggleSplitScreen() {
        }
        
        default void topAppWindowChanged(final int n, final boolean b, final boolean b2) {
        }
    }
    
    private final class H extends Handler
    {
        private H(final Looper looper) {
            super(looper);
        }
        
        public void handleMessage(final Message message) {
            final int what = message.what;
            int i = 0;
            int j = 0;
            final int n = 0;
            final int n2 = 0;
            int k = 0;
            int l = 0;
            int index = 0;
            int index2 = 0;
            final int n3 = 0;
            int index3 = 0;
            int index4 = 0;
            final int n4 = 0;
            int index5 = 0;
            int index6 = 0;
            int index7 = 0;
            int index8 = 0;
            int index9 = 0;
            int index10 = 0;
            int index11 = 0;
            int index12 = 0;
            int index13 = 0;
            int index14 = 0;
            int index15 = 0;
            int index16 = 0;
            int index17 = 0;
            int index18 = 0;
            int index19 = 0;
            int index20 = 0;
            int index21 = 0;
            int index22 = 0;
            int index23 = 0;
            int index24 = 0;
            int index25 = 0;
            int index26 = 0;
            int index27 = 0;
            final int n5 = 0;
            final int n6 = 0;
            int index28 = 0;
            switch (what & 0xFFFF0000) {
                case 3670016: {
                    final Iterator<Callbacks> iterator = (Iterator<Callbacks>)CommandQueue.this.mCallbacks.iterator();
                    while (iterator.hasNext()) {
                        iterator.next().suppressAmbientDisplay((boolean)message.obj);
                    }
                    break;
                }
                case 3604480: {
                    while (index28 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index28)).onTracingStateChanged((boolean)message.obj);
                        ++index28;
                    }
                    break;
                }
                case 3538944: {
                    final SomeArgs someArgs = (SomeArgs)message.obj;
                    final String s = (String)someArgs.arg1;
                    final IBinder binder = (IBinder)someArgs.arg2;
                    final Iterator<Callbacks> iterator2 = (Iterator<Callbacks>)CommandQueue.this.mCallbacks.iterator();
                    while (iterator2.hasNext()) {
                        iterator2.next().hideToast(s, binder);
                    }
                    break;
                }
                case 3473408: {
                    final SomeArgs someArgs2 = (SomeArgs)message.obj;
                    final String s2 = (String)someArgs2.arg1;
                    final IBinder binder2 = (IBinder)someArgs2.arg2;
                    final CharSequence charSequence = (CharSequence)someArgs2.arg3;
                    final IBinder binder3 = (IBinder)someArgs2.arg4;
                    final ITransientNotificationCallback transientNotificationCallback = (ITransientNotificationCallback)someArgs2.arg5;
                    final int argi1 = someArgs2.argi1;
                    final int argi2 = someArgs2.argi2;
                    final Iterator<Callbacks> iterator3 = (Iterator<Callbacks>)CommandQueue.this.mCallbacks.iterator();
                    while (iterator3.hasNext()) {
                        iterator3.next().showToast(argi1, s2, binder2, charSequence, binder3, argi2, transientNotificationCallback);
                    }
                    break;
                }
                case 3407872: {
                    while (i < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(i)).dismissInattentiveSleepWarning((boolean)message.obj);
                        ++i;
                    }
                    break;
                }
                case 3342336: {
                    while (j < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(j)).showInattentiveSleepWarning();
                        ++j;
                    }
                    break;
                }
                case 3276800: {
                    final SomeArgs someArgs3 = (SomeArgs)message.obj;
                    for (int index29 = 0; index29 < CommandQueue.this.mCallbacks.size(); ++index29) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index29)).topAppWindowChanged(someArgs3.argi1, someArgs3.argi2 != 0, someArgs3.argi3 != 0);
                    }
                    someArgs3.recycle();
                    break;
                }
                case 3211264: {
                    final int arg1 = message.arg1;
                    final int[] array = (int[])message.obj;
                    for (int index30 = n; index30 < CommandQueue.this.mCallbacks.size(); ++index30) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index30)).abortTransient(arg1, array);
                    }
                    break;
                }
                case 3145728: {
                    final int arg2 = message.arg1;
                    final int[] array2 = (int[])message.obj;
                    for (int index31 = n2; index31 < CommandQueue.this.mCallbacks.size(); ++index31) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index31)).showTransient(arg2, array2);
                    }
                    break;
                }
                case 3080192: {
                    for (int index32 = 0; index32 < CommandQueue.this.mCallbacks.size(); ++index32) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index32)).onRecentsAnimationStateChanged(message.arg1 > 0);
                    }
                    break;
                }
                case 3014656: {
                    while (k < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(k)).showPinningEscapeToast();
                        ++k;
                    }
                    break;
                }
                case 2949120: {
                    while (l < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(l)).showPinningEnterExitToast((boolean)message.obj);
                        ++l;
                    }
                    break;
                }
                case 2883584: {
                    while (index < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index)).showWirelessChargingAnimation(message.arg1);
                        ++index;
                    }
                    break;
                }
                case 2818048: {
                    while (index2 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index2)).hideAuthenticationDialog();
                        ++index2;
                    }
                    break;
                }
                case 2752512: {
                    final SomeArgs someArgs4 = (SomeArgs)message.obj;
                    for (int index33 = n3; index33 < CommandQueue.this.mCallbacks.size(); ++index33) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index33)).onBiometricError(someArgs4.argi1, someArgs4.argi2, someArgs4.argi3);
                    }
                    someArgs4.recycle();
                    break;
                }
                case 2686976: {
                    while (index3 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index3)).onBiometricHelp((String)message.obj);
                        ++index3;
                    }
                    break;
                }
                case 2621440: {
                    while (index4 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index4)).onBiometricAuthenticated();
                        ++index4;
                    }
                    break;
                }
                case 2555904: {
                    CommandQueue.this.mHandler.removeMessages(2752512);
                    CommandQueue.this.mHandler.removeMessages(2686976);
                    CommandQueue.this.mHandler.removeMessages(2621440);
                    final SomeArgs someArgs5 = (SomeArgs)message.obj;
                    for (int index34 = n4; index34 < CommandQueue.this.mCallbacks.size(); ++index34) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index34)).showAuthenticationDialog((Bundle)someArgs5.arg1, (IBiometricServiceReceiverInternal)someArgs5.arg2, someArgs5.argi1, (boolean)someArgs5.arg3, someArgs5.argi2, (String)someArgs5.arg4, (long)someArgs5.arg5);
                    }
                    someArgs5.recycle();
                    break;
                }
                case 2490368: {
                    for (int index35 = 0; index35 < CommandQueue.this.mCallbacks.size(); ++index35) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index35)).onRotationProposal(message.arg1, message.arg2 != 0);
                    }
                    break;
                }
                case 2424832: {
                    for (int index36 = 0; index36 < CommandQueue.this.mCallbacks.size(); ++index36) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index36)).setTopAppHidesStatusBar(message.arg1 != 0);
                    }
                    break;
                }
                case 2359296: {
                    for (int index37 = 0; index37 < CommandQueue.this.mCallbacks.size(); ++index37) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index37)).handleShowShutdownUi(message.arg1 != 0, (String)message.obj);
                    }
                    break;
                }
                case 2293760: {
                    while (index5 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index5)).togglePanel();
                        ++index5;
                    }
                    break;
                }
                case 2228224: {
                    while (index6 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index6)).handleShowGlobalActionsMenu();
                        ++index6;
                    }
                    break;
                }
                case 2162688: {
                    while (index7 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index7)).handleSystemKey(message.arg1);
                        ++index7;
                    }
                    break;
                }
                case 2097152: {
                    while (index8 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index8)).dismissKeyboardShortcutsMenu();
                        ++index8;
                    }
                    break;
                }
                case 2031616: {
                    while (index9 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index9)).appTransitionFinished(message.arg1);
                        ++index9;
                    }
                    break;
                }
                case 1966080: {
                    while (index10 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index10)).toggleSplitScreen();
                        ++index10;
                    }
                    break;
                }
                case 1900544: {
                    while (index11 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index11)).clickTile((ComponentName)message.obj);
                        ++index11;
                    }
                    break;
                }
                case 1835008: {
                    while (index12 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index12)).remQsTile((ComponentName)message.obj);
                        ++index12;
                    }
                    break;
                }
                case 1769472: {
                    while (index13 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index13)).addQsTile((ComponentName)message.obj);
                        ++index13;
                    }
                    break;
                }
                case 1703936: {
                    while (index14 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index14)).showPictureInPictureMenu();
                        ++index14;
                    }
                    break;
                }
                case 1638400: {
                    while (index15 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index15)).toggleKeyboardShortcutsMenu(message.arg1);
                        ++index15;
                    }
                    break;
                }
                case 1572864: {
                    while (index16 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index16)).onCameraLaunchGestureDetected(message.arg1);
                        ++index16;
                    }
                    break;
                }
                case 1507328: {
                    while (index17 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index17)).startAssist((Bundle)message.obj);
                        ++index17;
                    }
                    break;
                }
                case 1441792: {
                    while (index18 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index18)).showAssistDisclosure();
                        ++index18;
                    }
                    break;
                }
                case 1376256: {
                    final SomeArgs someArgs6 = (SomeArgs)message.obj;
                    for (int index38 = 0; index38 < CommandQueue.this.mCallbacks.size(); ++index38) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index38)).appTransitionStarting(someArgs6.argi1, (long)someArgs6.arg1, (long)someArgs6.arg2, someArgs6.argi2 != 0);
                    }
                    break;
                }
                case 1310720: {
                    while (index19 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index19)).appTransitionCancelled(message.arg1);
                        ++index19;
                    }
                    break;
                }
                case 1245184: {
                    for (int index39 = 0; index39 < CommandQueue.this.mCallbacks.size(); ++index39) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index39)).appTransitionPending(message.arg1, message.arg2 != 0);
                    }
                    break;
                }
                case 1179648: {
                    while (index20 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index20)).showScreenPinningRequest(message.arg1);
                        ++index20;
                    }
                    break;
                }
                case 917504: {
                    for (int index40 = 0; index40 < CommandQueue.this.mCallbacks.size(); ++index40) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index40)).hideRecentApps(message.arg1 != 0, message.arg2 != 0);
                    }
                    break;
                }
                case 851968: {
                    for (int index41 = 0; index41 < CommandQueue.this.mCallbacks.size(); ++index41) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index41)).showRecentApps(message.arg1 != 0);
                    }
                    break;
                }
                case 786432: {
                    while (index21 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index21)).setWindowState(message.arg1, message.arg2, (int)message.obj);
                        ++index21;
                    }
                    break;
                }
                case 720896: {
                    while (index22 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index22)).cancelPreloadRecentApps();
                        ++index22;
                    }
                    break;
                }
                case 655360: {
                    while (index23 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index23)).preloadRecentApps();
                        ++index23;
                    }
                    break;
                }
                case 589824: {
                    while (index24 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index24)).toggleRecentApps();
                        ++index24;
                    }
                    break;
                }
                case 524288: {
                    final SomeArgs someArgs7 = (SomeArgs)message.obj;
                    CommandQueue.this.handleShowImeButton(someArgs7.argi1, (IBinder)someArgs7.arg1, someArgs7.argi2, someArgs7.argi3, someArgs7.argi4 != 0, someArgs7.argi5 != 0);
                    break;
                }
                case 458752: {
                    while (index25 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index25)).onDisplayReady(message.arg1);
                        ++index25;
                    }
                    break;
                }
                case 393216: {
                    final SomeArgs someArgs8 = (SomeArgs)message.obj;
                    for (int index42 = 0; index42 < CommandQueue.this.mCallbacks.size(); ++index42) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index42)).onSystemBarAppearanceChanged(someArgs8.argi1, someArgs8.argi2, (AppearanceRegion[])someArgs8.arg1, someArgs8.argi3 == 1);
                    }
                    someArgs8.recycle();
                    break;
                }
                case 327680: {
                    while (index26 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index26)).animateExpandSettingsPanel((String)message.obj);
                        ++index26;
                    }
                    break;
                }
                case 262144: {
                    for (int index43 = 0; index43 < CommandQueue.this.mCallbacks.size(); ++index43) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index43)).animateCollapsePanels(message.arg1, message.arg2 != 0);
                    }
                    break;
                }
                case 196608: {
                    while (index27 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index27)).animateExpandNotificationsPanel();
                        ++index27;
                    }
                    break;
                }
                case 131072: {
                    final SomeArgs someArgs9 = (SomeArgs)message.obj;
                    for (int index44 = 0; index44 < CommandQueue.this.mCallbacks.size(); ++index44) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index44)).disable(someArgs9.argi1, someArgs9.argi2, someArgs9.argi3, someArgs9.argi4 != 0);
                    }
                    break;
                }
                case 65536: {
                    final int arg3 = message.arg1;
                    if (arg3 == 1) {
                        final Pair pair = (Pair)message.obj;
                        for (int index45 = n6; index45 < CommandQueue.this.mCallbacks.size(); ++index45) {
                            ((Callbacks)CommandQueue.this.mCallbacks.get(index45)).setIcon((String)pair.first, (StatusBarIcon)pair.second);
                        }
                        break;
                    }
                    int index46 = n5;
                    if (arg3 != 2) {
                        break;
                    }
                    while (index46 < CommandQueue.this.mCallbacks.size()) {
                        ((Callbacks)CommandQueue.this.mCallbacks.get(index46)).removeIcon((String)message.obj);
                        ++index46;
                    }
                    break;
                }
            }
        }
    }
}
