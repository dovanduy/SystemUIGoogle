// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.dreamliner;

import android.os.IBinder;
import android.app.PendingIntent;
import java.util.Iterator;
import android.os.SystemClock;
import android.os.PowerManager;
import android.os.ResultReceiver;
import android.content.ServiceConnection;
import com.google.android.systemui.elmyra.gates.KeyguardVisibility;
import android.content.ComponentName;
import android.view.View;
import java.util.concurrent.Executors;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.dreams.IDreamManager$Stub;
import android.os.ServiceManager;
import android.service.dreams.IDreamManager;
import android.content.IntentFilter;
import android.os.Bundle;
import android.content.Intent;
import java.util.ArrayList;
import android.os.Looper;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.util.Log;
import com.android.systemui.settings.CurrentUserTracker;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptSuppressor;
import android.os.Handler;
import android.widget.ImageView;
import android.content.Context;
import java.util.List;
import java.util.concurrent.ExecutorService;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.dock.DockManager;
import android.content.BroadcastReceiver;

public class DockObserver extends BroadcastReceiver implements DockManager
{
    @VisibleForTesting
    static final String ACTION_ALIGN_STATE_CHANGE = "com.google.android.systemui.dreamliner.ALIGNMENT_CHANGE";
    @VisibleForTesting
    static final String ACTION_CHALLENGE = "com.google.android.systemui.dreamliner.ACTION_CHALLENGE";
    @VisibleForTesting
    static final String ACTION_DOCK_UI_ACTIVE = "com.google.android.systemui.dreamliner.ACTION_DOCK_UI_ACTIVE";
    @VisibleForTesting
    static final String ACTION_DOCK_UI_IDLE = "com.google.android.systemui.dreamliner.ACTION_DOCK_UI_IDLE";
    @VisibleForTesting
    static final String ACTION_GET_DOCK_INFO = "com.google.android.systemui.dreamliner.ACTION_GET_DOCK_INFO";
    @VisibleForTesting
    static final String ACTION_KEY_EXCHANGE = "com.google.android.systemui.dreamliner.ACTION_KEY_EXCHANGE";
    @VisibleForTesting
    static final String ACTION_REBIND_DOCK_SERVICE = "com.google.android.systemui.dreamliner.ACTION_REBIND_DOCK_SERVICE";
    @VisibleForTesting
    static final String ACTION_START_DREAMLINER_CONTROL_SERVICE = "com.google.android.apps.dreamliner.START";
    @VisibleForTesting
    static final String COMPONENTNAME_DREAMLINER_CONTROL_SERVICE = "com.google.android.apps.dreamliner/.DreamlinerControlService";
    private static final boolean DEBUG;
    @VisibleForTesting
    static final String EXTRA_ALIGN_STATE = "align_state";
    @VisibleForTesting
    static final String EXTRA_CHALLENGE_DATA = "challenge_data";
    @VisibleForTesting
    static final String EXTRA_CHALLENGE_DOCK_ID = "challenge_dock_id";
    @VisibleForTesting
    static final String EXTRA_PUBLIC_KEY = "public_key";
    @VisibleForTesting
    static final String KEY_SHOWING = "showing";
    @VisibleForTesting
    static final int RESULT_NOT_FOUND = 1;
    @VisibleForTesting
    static final int RESULT_OK = 0;
    @VisibleForTesting
    static volatile ExecutorService mSingleThreadExecutor;
    private static boolean sIsDockingUiShowing;
    private final List<AlignmentStateListener> mAlignmentStateListeners;
    private final List<DockEventListener> mClients;
    private final Context mContext;
    private DockAlignmentController mDockAlignmentController;
    @VisibleForTesting
    DockGestureController mDockGestureController;
    @VisibleForTesting
    int mDockState;
    private ImageView mDreamlinerGear;
    @VisibleForTesting
    final DreamlinerBroadcastReceiver mDreamlinerReceiver;
    @VisibleForTesting
    DreamlinerServiceConn mDreamlinerServiceConn;
    private final Handler mHandler;
    private DockIndicationController mIndicationController;
    private final NotificationInterruptSuppressor mInterruptSuppressor;
    @VisibleForTesting
    int mLastAlignState;
    private final StatusBarStateController mStatusBarStateController;
    private final CurrentUserTracker mUserTracker;
    private final WirelessCharger mWirelessCharger;
    
    static {
        DEBUG = Log.isLoggable("DLObserver", 3);
        DockObserver.sIsDockingUiShowing = false;
    }
    
    public DockObserver(final Context mContext, final WirelessCharger mWirelessCharger, final BroadcastDispatcher broadcastDispatcher, final StatusBarStateController mStatusBarStateController, final NotificationInterruptStateProvider notificationInterruptStateProvider) {
        this.mDreamlinerReceiver = new DreamlinerBroadcastReceiver();
        this.mDockState = 0;
        this.mLastAlignState = -1;
        this.mInterruptSuppressor = new NotificationInterruptSuppressor() {
            @Override
            public String getName() {
                return "DLObserver";
            }
            
            @Override
            public boolean suppressInterruptions(final NotificationEntry notificationEntry) {
                return DockObserver.isDockingUiShowing();
            }
        };
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mContext = mContext;
        this.mClients = new ArrayList<DockEventListener>();
        this.mAlignmentStateListeners = new ArrayList<AlignmentStateListener>();
        this.mUserTracker = new CurrentUserTracker(broadcastDispatcher) {
            @Override
            public void onUserSwitched(final int n) {
                DockObserver.this.stopDreamlinerService(mContext);
                DockObserver.this.updateCurrentDockingStatus(mContext);
            }
        };
        this.mWirelessCharger = mWirelessCharger;
        this.mStatusBarStateController = mStatusBarStateController;
        mContext.registerReceiver((BroadcastReceiver)this, this.getPowerConnectedIntentFilter());
        this.mDockAlignmentController = new DockAlignmentController(mWirelessCharger, this);
        notificationInterruptStateProvider.addSuppressor(this.mInterruptSuppressor);
    }
    
    private boolean assertNotNull(final Object o, final String str) {
        if (o == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(" is null");
            Log.w("DLObserver", sb.toString());
            return false;
        }
        return true;
    }
    
    private byte[] convertArrayListToPrimitiveArray(final ArrayList<Byte> list) {
        if (list != null && !list.isEmpty()) {
            final int size = list.size();
            final byte[] array = new byte[size];
            for (int i = 0; i < size; ++i) {
                array[i] = list.get(i);
            }
            return array;
        }
        return null;
    }
    
    private Bundle createChallengeResponseBundle(final ArrayList<Byte> list) {
        if (list != null && !list.isEmpty()) {
            final byte[] convertArrayListToPrimitiveArray = this.convertArrayListToPrimitiveArray(list);
            final Bundle bundle = new Bundle();
            bundle.putByteArray("challenge_response", convertArrayListToPrimitiveArray);
            return bundle;
        }
        return null;
    }
    
    private Bundle createKeyExchangeResponseBundle(final byte b, final ArrayList<Byte> list) {
        if (list != null && !list.isEmpty()) {
            final byte[] convertArrayListToPrimitiveArray = this.convertArrayListToPrimitiveArray(list);
            final Bundle bundle = new Bundle();
            bundle.putByte("dock_id", b);
            bundle.putByteArray("dock_public_key", convertArrayListToPrimitiveArray);
            return bundle;
        }
        return null;
    }
    
    private void dispatchDockEvent(final DockEventListener dockEventListener) {
        if (DockObserver.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("onDockEvent mDockState = ");
            sb.append(this.mDockState);
            Log.d("DLObserver", sb.toString());
        }
        dockEventListener.onEvent(this.mDockState);
    }
    
    private final Intent getBatteryStatus(final Context context) {
        return context.registerReceiver((BroadcastReceiver)null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
    }
    
    private IDreamManager getDreamManagerInstance() {
        return IDreamManager$Stub.asInterface(ServiceManager.checkService("dreams"));
    }
    
    private IntentFilter getPowerConnectedIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED");
        intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
        intentFilter.addAction("android.intent.action.BOOT_COMPLETED");
        intentFilter.addAction("com.google.android.systemui.dreamliner.ACTION_REBIND_DOCK_SERVICE");
        intentFilter.setPriority(1000);
        return intentFilter;
    }
    
    private boolean isChargingOrFull(final Intent intent) {
        final int intExtra = intent.getIntExtra("status", -1);
        return intExtra == 2 || intExtra == 5;
    }
    
    public static boolean isDockingUiShowing() {
        return DockObserver.sIsDockingUiShowing;
    }
    
    private void notifyDreamlinerAlignStateChanged(final int n) {
        if (this.isDocked()) {
            this.mContext.sendBroadcastAsUser(new Intent("com.google.android.systemui.dreamliner.ALIGNMENT_CHANGE").putExtra("align_state", n).addFlags(1073741824), UserHandle.CURRENT);
        }
    }
    
    private void notifyForceEnabledAmbientDisplay(final boolean b) {
        final IDreamManager dreamManagerInstance = this.getDreamManagerInstance();
        Label_0019: {
            if (dreamManagerInstance == null) {
                break Label_0019;
            }
            try {
                dreamManagerInstance.forceAmbientDisplayEnabled(b);
                return;
                Log.e("DLObserver", "DreamManager not found");
            }
            catch (RemoteException ex) {}
        }
    }
    
    private void onDockStateChanged(final int n) {
        if (this.mDockState == n) {
            return;
        }
        if (DockObserver.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("dock state changed from ");
            sb.append(this.mDockState);
            sb.append(" to ");
            sb.append(n);
            Log.d("DLObserver", sb.toString());
        }
        final int mDockState = this.mDockState;
        this.mDockState = n;
        for (int i = 0; i < this.mClients.size(); ++i) {
            this.dispatchDockEvent(this.mClients.get(i));
        }
        final DockIndicationController mIndicationController = this.mIndicationController;
        if (mIndicationController != null) {
            mIndicationController.setDocking(this.isDocked());
        }
        if (mDockState == 0 && n == 1) {
            this.notifyDreamlinerAlignStateChanged(this.mLastAlignState);
        }
    }
    
    private static void runOnBackgroundThread(final Runnable runnable) {
        if (DockObserver.mSingleThreadExecutor == null) {
            DockObserver.mSingleThreadExecutor = Executors.newSingleThreadExecutor();
        }
        DockObserver.mSingleThreadExecutor.execute(runnable);
    }
    
    private void sendDockActiveIntent(final Context context) {
        if (DockObserver.DEBUG) {
            Log.d("DLObserver", "sendDockActiveIntent()");
        }
        context.sendBroadcast(new Intent("android.intent.action.DOCK_ACTIVE").addFlags(1073741824));
    }
    
    private void sendDockIdleIntent(final Context context) {
        if (DockObserver.DEBUG) {
            Log.d("DLObserver", "sendDockIdleIntent()");
        }
        context.sendBroadcast(new Intent("android.intent.action.DOCK_IDLE").addFlags(1073741824));
    }
    
    private void startDreamlinerService(final Context context, final int n, final int n2, final int n3) {
        synchronized (this) {
            this.notifyForceEnabledAmbientDisplay(true);
            if (this.mDreamlinerServiceConn == null) {
                this.mDreamlinerReceiver.registerReceiver(context);
                this.mDockGestureController = new DockGestureController(context, this.mDreamlinerGear, (View)this.mDreamlinerGear.getParent(), this.mStatusBarStateController);
                final Intent obj = new Intent("com.google.android.apps.dreamliner.START");
                obj.setComponent(ComponentName.unflattenFromString("com.google.android.apps.dreamliner/.DreamlinerControlService"));
                obj.putExtra("type", n);
                obj.putExtra("orientation", n2);
                obj.putExtra("id", n3);
                obj.putExtra("occluded", new KeyguardVisibility(context).isKeyguardOccluded());
                try {
                    final DreamlinerServiceConn mDreamlinerServiceConn = new DreamlinerServiceConn(context);
                    this.mDreamlinerServiceConn = mDreamlinerServiceConn;
                    if (context.bindServiceAsUser(obj, (ServiceConnection)mDreamlinerServiceConn, 1, new UserHandle(this.mUserTracker.getCurrentUserId()))) {
                        this.mUserTracker.startTracking();
                        return;
                    }
                }
                catch (SecurityException ex) {
                    Log.e("DLObserver", ex.getMessage(), (Throwable)ex);
                }
                this.mDreamlinerServiceConn = null;
                final StringBuilder sb = new StringBuilder();
                sb.append("Unable to bind Dreamliner service: ");
                sb.append(obj);
                Log.w("DLObserver", sb.toString());
            }
        }
    }
    
    private void stopDreamlinerService(final Context context) {
        this.notifyForceEnabledAmbientDisplay(false);
        this.onDockStateChanged(0);
        try {
            if (this.mDreamlinerServiceConn != null) {
                if (this.assertNotNull(this.mDockGestureController, DockGestureController.class.getSimpleName())) {
                    this.mDockGestureController.stopMonitoring();
                    this.mDockGestureController = null;
                }
                this.mUserTracker.stopTracking();
                this.mDreamlinerReceiver.unregisterReceiver(context);
                context.unbindService((ServiceConnection)this.mDreamlinerServiceConn);
                this.mDreamlinerServiceConn = null;
            }
        }
        catch (IllegalArgumentException ex) {
            Log.e("DLObserver", ex.getMessage(), (Throwable)ex);
        }
    }
    
    private void triggerChallengeWithDock(final Intent intent) {
        if (DockObserver.DEBUG) {
            Log.d("DLObserver", "triggerChallengeWithDock");
        }
        if (intent == null) {
            return;
        }
        final ResultReceiver resultReceiver = (ResultReceiver)intent.getParcelableExtra("android.intent.extra.RESULT_RECEIVER");
        if (resultReceiver != null) {
            final byte byteExtra = intent.getByteExtra("challenge_dock_id", (byte)(-1));
            final byte[] byteArrayExtra = intent.getByteArrayExtra("challenge_data");
            if (byteArrayExtra != null && byteArrayExtra.length > 0 && byteExtra >= 0) {
                runOnBackgroundThread(new ChallengeWithDock(resultReceiver, byteExtra, byteArrayExtra));
            }
            else {
                resultReceiver.send(1, (Bundle)null);
            }
        }
    }
    
    private void triggerKeyExchangeWithDock(final Intent intent) {
        if (DockObserver.DEBUG) {
            Log.d("DLObserver", "triggerKeyExchangeWithDock");
        }
        if (intent == null) {
            return;
        }
        final ResultReceiver resultReceiver = (ResultReceiver)intent.getParcelableExtra("android.intent.extra.RESULT_RECEIVER");
        if (resultReceiver != null) {
            final byte[] byteArrayExtra = intent.getByteArrayExtra("public_key");
            if (byteArrayExtra != null && byteArrayExtra.length > 0) {
                runOnBackgroundThread(new KeyExchangeWithDock(resultReceiver, byteArrayExtra));
            }
            else {
                resultReceiver.send(1, (Bundle)null);
            }
        }
    }
    
    private void triggerPhotoPromo(final Intent intent) {
        if (DockObserver.DEBUG) {
            Log.d("DLObserver", "triggerPhotoPromo");
        }
        if (intent == null) {
            return;
        }
        final ResultReceiver resultReceiver = (ResultReceiver)intent.getParcelableExtra("android.intent.extra.RESULT_RECEIVER");
        if (resultReceiver != null) {
            final DockIndicationController mIndicationController = this.mIndicationController;
            if (mIndicationController != null) {
                mIndicationController.showPromo(resultReceiver);
            }
        }
    }
    
    private void tryTurnScreenOff(final Context context) {
        final PowerManager powerManager = (PowerManager)context.getSystemService((Class)PowerManager.class);
        if (powerManager.isScreenOn()) {
            powerManager.goToSleep(SystemClock.uptimeMillis());
        }
    }
    
    public void addAlignmentStateListener(final AlignmentStateListener obj) {
        if (DockObserver.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("add alignment listener: ");
            sb.append(obj);
            Log.d("DLObserver", sb.toString());
        }
        if (!this.mAlignmentStateListeners.contains(obj)) {
            this.mAlignmentStateListeners.add(obj);
        }
    }
    
    public void addListener(final DockEventListener obj) {
        if (DockObserver.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("add listener: ");
            sb.append(obj);
            Log.d("DLObserver", sb.toString());
        }
        if (!this.mClients.contains(obj)) {
            this.mClients.add(obj);
        }
        this.mHandler.post((Runnable)new _$$Lambda$DockObserver$ycI6ycPDbZNTYOt1A_aK_6LC3x8(this, obj));
    }
    
    public boolean isDocked() {
        final int mDockState = this.mDockState;
        boolean b = true;
        if (mDockState != 1) {
            b = (mDockState == 2 && b);
        }
        return b;
    }
    
    public boolean isHidden() {
        return this.mDockState == 2;
    }
    
    void onAlignStateChanged(final int n) {
        if (DockObserver.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("onAlignStateChanged alignState = ");
            sb.append(n);
            Log.d("DLObserver", sb.toString());
        }
        this.mLastAlignState = n;
        final Iterator<AlignmentStateListener> iterator = this.mAlignmentStateListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onAlignmentStateChanged(n);
        }
        this.notifyDreamlinerAlignStateChanged(n);
    }
    
    public void onReceive(final Context context, final Intent intent) {
        if (intent == null) {
            return;
        }
        if (DockObserver.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("onReceive(); ");
            sb.append(intent.getAction());
            Log.i("DLObserver", sb.toString());
        }
        final String action = intent.getAction();
        int n = -1;
        switch (action.hashCode()) {
            case 1318602046: {
                if (action.equals("com.google.android.systemui.dreamliner.ACTION_REBIND_DOCK_SERVICE")) {
                    n = 2;
                    break;
                }
                break;
            }
            case 1019184907: {
                if (action.equals("android.intent.action.ACTION_POWER_CONNECTED")) {
                    n = 0;
                    break;
                }
                break;
            }
            case 798292259: {
                if (action.equals("android.intent.action.BOOT_COMPLETED")) {
                    n = 3;
                    break;
                }
                break;
            }
            case -1886648615: {
                if (action.equals("android.intent.action.ACTION_POWER_DISCONNECTED")) {
                    n = 1;
                    break;
                }
                break;
            }
        }
        if (n != 0) {
            if (n != 1) {
                if (n == 2 || n == 3) {
                    this.updateCurrentDockingStatus(context);
                }
            }
            else {
                this.stopDreamlinerService(context);
                DockObserver.sIsDockingUiShowing = false;
            }
        }
        else if (this.mWirelessCharger != null) {
            runOnBackgroundThread(new IsDockPresent(context));
        }
    }
    
    public void registerDockAlignInfo() {
        this.mDockAlignmentController.registerAlignInfoListener();
    }
    
    public void removeListener(final DockEventListener obj) {
        if (DockObserver.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("remove listener: ");
            sb.append(obj);
            Log.d("DLObserver", sb.toString());
        }
        this.mClients.remove(obj);
    }
    
    public void setDreamlinerGear(final ImageView mDreamlinerGear) {
        this.mDreamlinerGear = mDreamlinerGear;
    }
    
    public void setIndicationController(final DockIndicationController mIndicationController) {
        this.mIndicationController = mIndicationController;
    }
    
    @VisibleForTesting
    final void updateCurrentDockingStatus(final Context context) {
        this.notifyForceEnabledAmbientDisplay(false);
        if (this.isChargingOrFull(this.getBatteryStatus(context)) && this.mWirelessCharger != null) {
            runOnBackgroundThread(new IsDockPresent(context));
        }
    }
    
    @VisibleForTesting
    final class ChallengeCallback implements WirelessCharger.ChallengeCallback
    {
        private final ResultReceiver mResultReceiver;
        
        ChallengeCallback(final ResultReceiver mResultReceiver) {
            this.mResultReceiver = mResultReceiver;
        }
        
        @Override
        public void onCallback(final int i, final ArrayList<Byte> obj) {
            if (DockObserver.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("challenge() Result: ");
                sb.append(i);
                Log.d("DLObserver", sb.toString());
            }
            if (i == 0) {
                if (DockObserver.DEBUG) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("challenge() response: ");
                    sb2.append(obj);
                    Log.d("DLObserver", sb2.toString());
                }
                this.mResultReceiver.send(0, DockObserver.this.createChallengeResponseBundle(obj));
            }
            else {
                this.mResultReceiver.send(1, (Bundle)null);
            }
        }
    }
    
    private class ChallengeWithDock implements Runnable
    {
        final byte[] challengeData;
        final byte dockId;
        final ResultReceiver resultReceiver;
        
        public ChallengeWithDock(final ResultReceiver resultReceiver, final byte b, final byte[] challengeData) {
            this.dockId = b;
            this.challengeData = challengeData;
            this.resultReceiver = resultReceiver;
        }
        
        @Override
        public void run() {
            if (DockObserver.this.mWirelessCharger == null) {
                return;
            }
            DockObserver.this.mWirelessCharger.challenge(this.dockId, this.challengeData, (WirelessCharger.ChallengeCallback)new ChallengeCallback(this.resultReceiver));
        }
    }
    
    @VisibleForTesting
    class DreamlinerBroadcastReceiver extends BroadcastReceiver
    {
        private boolean mListening;
        
        private IntentFilter getIntentFilter() {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.google.android.systemui.dreamliner.ACTION_GET_DOCK_INFO");
            intentFilter.addAction("com.google.android.systemui.dreamliner.ACTION_DOCK_UI_IDLE");
            intentFilter.addAction("com.google.android.systemui.dreamliner.ACTION_DOCK_UI_ACTIVE");
            intentFilter.addAction("com.google.android.systemui.dreamliner.ACTION_KEY_EXCHANGE");
            intentFilter.addAction("com.google.android.systemui.dreamliner.ACTION_CHALLENGE");
            intentFilter.addAction("com.google.android.systemui.dreamliner.dream");
            intentFilter.addAction("com.google.android.systemui.dreamliner.paired");
            intentFilter.addAction("com.google.android.systemui.dreamliner.pause");
            intentFilter.addAction("com.google.android.systemui.dreamliner.resume");
            intentFilter.addAction("com.google.android.systemui.dreamliner.undock");
            intentFilter.addAction("com.google.android.systemui.dreamliner.assistant_poodle");
            intentFilter.addAction("com.google.android.systemui.dreamliner.photo_promo");
            return intentFilter;
        }
        
        public void onReceive(final Context context, final Intent intent) {
            if (intent == null) {
                return;
            }
            if (DockObserver.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Dock Receiver.onReceive(): ");
                sb.append(intent.getAction());
                Log.d("DLObserver", sb.toString());
            }
            final String action = intent.getAction();
            switch (action) {
                case "com.google.android.systemui.dreamliner.photo_promo": {
                    DockObserver.this.triggerPhotoPromo(intent);
                    break;
                }
                case "com.google.android.systemui.dreamliner.assistant_poodle": {
                    if (DockObserver.this.mIndicationController != null) {
                        DockObserver.this.mIndicationController.setShowing(intent.getBooleanExtra("showing", false));
                        break;
                    }
                    break;
                }
                case "com.google.android.systemui.dreamliner.undock": {
                    DockObserver.this.onDockStateChanged(0);
                    final DockObserver this$0 = DockObserver.this;
                    if (this$0.assertNotNull(this$0.mDockGestureController, DockGestureController.class.getSimpleName())) {
                        DockObserver.this.mDockGestureController.stopMonitoring();
                        break;
                    }
                    break;
                }
                case "com.google.android.systemui.dreamliner.pause": {
                    DockObserver.this.onDockStateChanged(2);
                    final DockObserver this$2 = DockObserver.this;
                    if (this$2.assertNotNull(this$2.mDockGestureController, DockGestureController.class.getSimpleName())) {
                        DockObserver.this.mDockGestureController.stopMonitoring();
                        break;
                    }
                    break;
                }
                case "com.google.android.systemui.dreamliner.paired": {
                    final DockObserver this$3 = DockObserver.this;
                    if (this$3.assertNotNull(this$3.mDockGestureController, DockGestureController.class.getSimpleName())) {
                        DockObserver.this.mDockGestureController.setTapAction((PendingIntent)intent.getParcelableExtra("single_tap_action"));
                    }
                }
                case "com.google.android.systemui.dreamliner.resume": {
                    DockObserver.this.onDockStateChanged(1);
                    final DockObserver this$4 = DockObserver.this;
                    if (this$4.assertNotNull(this$4.mDockGestureController, DockGestureController.class.getSimpleName())) {
                        DockObserver.this.mDockGestureController.startMonitoring();
                        break;
                    }
                    break;
                }
                case "com.google.android.systemui.dreamliner.dream": {
                    DockObserver.this.tryTurnScreenOff(context);
                    break;
                }
                case "com.google.android.systemui.dreamliner.ACTION_CHALLENGE": {
                    DockObserver.this.triggerChallengeWithDock(intent);
                    break;
                }
                case "com.google.android.systemui.dreamliner.ACTION_KEY_EXCHANGE": {
                    DockObserver.this.triggerKeyExchangeWithDock(intent);
                    break;
                }
                case "com.google.android.systemui.dreamliner.ACTION_DOCK_UI_ACTIVE": {
                    DockObserver.this.sendDockActiveIntent(context);
                    DockObserver.sIsDockingUiShowing = false;
                    break;
                }
                case "com.google.android.systemui.dreamliner.ACTION_DOCK_UI_IDLE": {
                    DockObserver.this.sendDockIdleIntent(context);
                    DockObserver.sIsDockingUiShowing = true;
                    break;
                }
                case "com.google.android.systemui.dreamliner.ACTION_GET_DOCK_INFO": {
                    final ResultReceiver resultReceiver = (ResultReceiver)intent.getParcelableExtra("android.intent.extra.RESULT_RECEIVER");
                    if (resultReceiver != null) {
                        runOnBackgroundThread(new GetDockInfo(resultReceiver, context));
                        break;
                    }
                    break;
                }
            }
        }
        
        public void registerReceiver(final Context context) {
            if (!this.mListening) {
                context.registerReceiverAsUser((BroadcastReceiver)this, UserHandle.ALL, this.getIntentFilter(), "com.google.android.systemui.permission.WIRELESS_CHARGER_STATUS", (Handler)null);
                this.mListening = true;
            }
        }
        
        public void unregisterReceiver(final Context context) {
            if (this.mListening) {
                context.unregisterReceiver((BroadcastReceiver)this);
                this.mListening = false;
            }
        }
    }
    
    @VisibleForTesting
    final class DreamlinerServiceConn implements ServiceConnection
    {
        final Context mContext;
        
        public DreamlinerServiceConn(final Context mContext) {
            this.mContext = mContext;
        }
        
        public void onBindingDied(final ComponentName componentName) {
            DockObserver.this.stopDreamlinerService(this.mContext);
            DockObserver.sIsDockingUiShowing = false;
        }
        
        public void onServiceConnected(final ComponentName componentName, final IBinder binder) {
        }
        
        public void onServiceDisconnected(final ComponentName componentName) {
            DockObserver.this.sendDockActiveIntent(this.mContext);
        }
    }
    
    private class GetDockInfo implements Runnable
    {
        final ResultReceiver resultReceiver;
        
        public GetDockInfo(final ResultReceiver resultReceiver, final Context context) {
            this.resultReceiver = resultReceiver;
        }
        
        @Override
        public void run() {
            if (DockObserver.this.mWirelessCharger == null) {
                return;
            }
            DockObserver.this.mWirelessCharger.getInformation((WirelessCharger.GetInformationCallback)new GetInformationCallback(this.resultReceiver));
        }
    }
    
    @VisibleForTesting
    final class GetInformationCallback implements WirelessCharger.GetInformationCallback
    {
        private final ResultReceiver mResultReceiver;
        
        GetInformationCallback(final DockObserver dockObserver, final ResultReceiver mResultReceiver) {
            this.mResultReceiver = mResultReceiver;
        }
        
        @Override
        public void onCallback(final int i, final DockInfo dockInfo) {
            if (DockObserver.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("getInformation() Result: ");
                sb.append(i);
                Log.d("DLObserver", sb.toString());
            }
            if (i == 0) {
                if (DockObserver.DEBUG) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("getInformation() DockInfo: ");
                    sb2.append(dockInfo.toString());
                    Log.d("DLObserver", sb2.toString());
                }
                this.mResultReceiver.send(0, dockInfo.toBundle());
            }
            else if (i != 1) {
                this.mResultReceiver.send(1, (Bundle)null);
            }
        }
    }
    
    private class IsDockPresent implements Runnable
    {
        final Context context;
        
        public IsDockPresent(final Context context) {
            this.context = context;
        }
        
        @Override
        public void run() {
            if (DockObserver.this.mWirelessCharger == null) {
                return;
            }
            DockObserver.this.mWirelessCharger.asyncIsDockPresent((WirelessCharger.IsDockPresentCallback)new IsDockPresentCallback(this.context));
        }
    }
    
    @VisibleForTesting
    final class IsDockPresentCallback implements WirelessCharger.IsDockPresentCallback
    {
        private final Context mContext;
        
        IsDockPresentCallback(final Context mContext) {
            this.mContext = mContext;
        }
        
        @Override
        public void onCallback(final boolean b, final byte b2, final byte b3, final boolean b4, final int i) {
            if (DockObserver.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("isDockPresent() docked: ");
                sb.append(b);
                sb.append(", id: ");
                sb.append(i);
                Log.i("DLObserver", sb.toString());
            }
            if (b) {
                DockObserver.this.startDreamlinerService(this.mContext, b2, b3, i);
            }
        }
    }
    
    @VisibleForTesting
    final class KeyExchangeCallback implements WirelessCharger.KeyExchangeCallback
    {
        private final ResultReceiver mResultReceiver;
        
        KeyExchangeCallback(final ResultReceiver mResultReceiver) {
            this.mResultReceiver = mResultReceiver;
        }
        
        @Override
        public void onCallback(final int i, final byte b, final ArrayList<Byte> obj) {
            if (DockObserver.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("keyExchange() Result: ");
                sb.append(i);
                Log.d("DLObserver", sb.toString());
            }
            if (i == 0) {
                if (DockObserver.DEBUG) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("keyExchange() key: ");
                    sb2.append(obj);
                    Log.d("DLObserver", sb2.toString());
                }
                this.mResultReceiver.send(0, DockObserver.this.createKeyExchangeResponseBundle(b, obj));
            }
            else {
                this.mResultReceiver.send(1, (Bundle)null);
            }
        }
    }
    
    private class KeyExchangeWithDock implements Runnable
    {
        final byte[] publicKey;
        final ResultReceiver resultReceiver;
        
        public KeyExchangeWithDock(final ResultReceiver resultReceiver, final byte[] publicKey) {
            this.publicKey = publicKey;
            this.resultReceiver = resultReceiver;
        }
        
        @Override
        public void run() {
            if (DockObserver.this.mWirelessCharger == null) {
                return;
            }
            DockObserver.this.mWirelessCharger.keyExchange(this.publicKey, (WirelessCharger.KeyExchangeCallback)new KeyExchangeCallback(this.resultReceiver));
        }
    }
}
