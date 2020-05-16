// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.keyboard;

import com.android.systemui.R$string;
import android.bluetooth.le.ScanResult;
import android.os.Message;
import android.os.Handler$Callback;
import android.os.Looper;
import android.util.Pair;
import android.content.DialogInterface$OnDismissListener;
import android.content.DialogInterface;
import android.content.DialogInterface$OnClickListener;
import android.os.HandlerThread;
import android.content.res.Configuration;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.bluetooth.le.ScanSettings;
import android.bluetooth.le.BluetoothLeScanner;
import java.util.List;
import java.util.Arrays;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings$Builder;
import android.bluetooth.le.ScanFilter$Builder;
import android.os.SystemClock;
import android.widget.Toast;
import android.content.ContentResolver;
import android.provider.Settings$Secure;
import android.os.Handler;
import android.hardware.input.InputManager;
import com.android.settingslib.bluetooth.BluetoothUtils;
import com.android.settingslib.bluetooth.BluetoothCallback;
import com.android.systemui.Dependency;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import android.text.TextUtils;
import java.util.Iterator;
import android.bluetooth.BluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import android.bluetooth.le.ScanCallback;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;
import android.content.Context;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import android.hardware.input.InputManager$OnTabletModeChangedListener;
import com.android.systemui.SystemUI;

public class KeyboardUI extends SystemUI implements InputManager$OnTabletModeChangedListener
{
    private boolean mBootCompleted;
    private long mBootCompletedTime;
    private CachedBluetoothDeviceManager mCachedDeviceManager;
    protected volatile Context mContext;
    private BluetoothDialog mDialog;
    private boolean mEnabled;
    private volatile KeyboardHandler mHandler;
    private int mInTabletMode;
    private String mKeyboardName;
    private LocalBluetoothAdapter mLocalBluetoothAdapter;
    private int mScanAttempt;
    private ScanCallback mScanCallback;
    private int mState;
    private volatile KeyboardUIHandler mUIHandler;
    
    public KeyboardUI(final Context context) {
        super(context);
        this.mInTabletMode = -1;
        this.mScanAttempt = 0;
    }
    
    private void bleAbortScanInternal(final int n) {
        if (this.mState == 3 && n == this.mScanAttempt) {
            this.stopScanning();
            this.mState = 9;
        }
    }
    
    private CachedBluetoothDevice getCachedBluetoothDevice(final BluetoothDevice bluetoothDevice) {
        CachedBluetoothDevice cachedBluetoothDevice;
        if ((cachedBluetoothDevice = this.mCachedDeviceManager.findDevice(bluetoothDevice)) == null) {
            cachedBluetoothDevice = this.mCachedDeviceManager.addDevice(bluetoothDevice);
        }
        return cachedBluetoothDevice;
    }
    
    private CachedBluetoothDevice getDiscoveredKeyboard() {
        for (final CachedBluetoothDevice cachedBluetoothDevice : this.mCachedDeviceManager.getCachedDevicesCopy()) {
            if (cachedBluetoothDevice.getName().equals(this.mKeyboardName)) {
                return cachedBluetoothDevice;
            }
        }
        return null;
    }
    
    private CachedBluetoothDevice getPairedKeyboard() {
        for (final BluetoothDevice bluetoothDevice : this.mLocalBluetoothAdapter.getBondedDevices()) {
            if (this.mKeyboardName.equals(bluetoothDevice.getName())) {
                return this.getCachedBluetoothDevice(bluetoothDevice);
            }
        }
        return null;
    }
    
    private void init() {
        final Context mContext = this.mContext;
        final String string = mContext.getString(17039933);
        this.mKeyboardName = string;
        if (TextUtils.isEmpty((CharSequence)string)) {
            return;
        }
        final LocalBluetoothManager localBluetoothManager = Dependency.get(LocalBluetoothManager.class);
        if (localBluetoothManager == null) {
            return;
        }
        this.mEnabled = true;
        this.mCachedDeviceManager = localBluetoothManager.getCachedDeviceManager();
        this.mLocalBluetoothAdapter = localBluetoothManager.getBluetoothAdapter();
        localBluetoothManager.getProfileManager();
        localBluetoothManager.getEventManager().registerCallback(new BluetoothCallbackHandler());
        BluetoothUtils.setErrorListener((BluetoothUtils.ErrorListener)new BluetoothErrorListener());
        final InputManager inputManager = (InputManager)mContext.getSystemService((Class)InputManager.class);
        inputManager.registerOnTabletModeChangedListener((InputManager$OnTabletModeChangedListener)this, (Handler)this.mHandler);
        this.mInTabletMode = inputManager.isInTabletMode();
        this.processKeyboardState();
        this.mUIHandler = new KeyboardUIHandler();
    }
    
    private boolean isUserSetupComplete() {
        final ContentResolver contentResolver = this.mContext.getContentResolver();
        boolean b = false;
        if (Settings$Secure.getIntForUser(contentResolver, "user_setup_complete", 0, -2) != 0) {
            b = true;
        }
        return b;
    }
    
    private void onBleScanFailedInternal() {
        this.mScanCallback = null;
        if (this.mState == 3) {
            this.mState = 9;
        }
    }
    
    private void onBluetoothStateChangedInternal(final int n) {
        if (n == 12 && this.mState == 4) {
            this.processKeyboardState();
        }
    }
    
    private void onDeviceAddedInternal(final CachedBluetoothDevice cachedBluetoothDevice) {
        if (this.mState == 3 && cachedBluetoothDevice.getName().equals(this.mKeyboardName)) {
            this.stopScanning();
            cachedBluetoothDevice.startPairing();
            this.mState = 5;
        }
    }
    
    private void onDeviceBondStateChangedInternal(final CachedBluetoothDevice cachedBluetoothDevice, final int n) {
        if (this.mState == 5 && cachedBluetoothDevice.getName().equals(this.mKeyboardName)) {
            if (n == 12) {
                this.mState = 6;
            }
            else if (n == 10) {
                this.mState = 7;
            }
        }
    }
    
    private void onShowErrorInternal(final Context context, final String anObject, final int n) {
        final int mState = this.mState;
        if ((mState == 5 || mState == 7) && this.mKeyboardName.equals(anObject)) {
            Toast.makeText(context, (CharSequence)context.getString(n, new Object[] { anObject }), 0).show();
        }
    }
    
    private void processKeyboardState() {
        this.mHandler.removeMessages(2);
        if (!this.mEnabled) {
            this.mState = -1;
            return;
        }
        if (!this.mBootCompleted) {
            this.mState = 1;
            return;
        }
        if (this.mInTabletMode != 0) {
            final int mState = this.mState;
            if (mState == 3) {
                this.stopScanning();
            }
            else if (mState == 4) {
                this.mUIHandler.sendEmptyMessage(9);
            }
            this.mState = 2;
            return;
        }
        final int state = this.mLocalBluetoothAdapter.getState();
        if ((state == 11 || state == 12) && this.mState == 4) {
            this.mUIHandler.sendEmptyMessage(9);
        }
        if (state == 11) {
            this.mState = 4;
            return;
        }
        if (state != 12) {
            this.mState = 4;
            this.showBluetoothDialog();
            return;
        }
        final CachedBluetoothDevice pairedKeyboard = this.getPairedKeyboard();
        final int mState2 = this.mState;
        if (mState2 == 2 || mState2 == 4) {
            if (pairedKeyboard != null) {
                this.mState = 6;
                pairedKeyboard.connect(false);
                return;
            }
            this.mCachedDeviceManager.clearNonBondedDevices();
        }
        final CachedBluetoothDevice discoveredKeyboard = this.getDiscoveredKeyboard();
        if (discoveredKeyboard != null) {
            this.mState = 5;
            discoveredKeyboard.startPairing();
        }
        else {
            this.mState = 3;
            this.startScanning();
        }
    }
    
    private void showBluetoothDialog() {
        if (this.isUserSetupComplete()) {
            final long uptimeMillis = SystemClock.uptimeMillis();
            final long n = this.mBootCompletedTime + 10000L;
            if (n < uptimeMillis) {
                this.mUIHandler.sendEmptyMessage(8);
            }
            else {
                this.mHandler.sendEmptyMessageAtTime(2, n);
            }
        }
        else {
            this.mLocalBluetoothAdapter.enable();
        }
    }
    
    private void startScanning() {
        final BluetoothLeScanner bluetoothLeScanner = this.mLocalBluetoothAdapter.getBluetoothLeScanner();
        final ScanFilter build = new ScanFilter$Builder().setDeviceName(this.mKeyboardName).build();
        final ScanSettings build2 = new ScanSettings$Builder().setCallbackType(1).setNumOfMatches(1).setScanMode(2).setReportDelay(0L).build();
        this.mScanCallback = new KeyboardScanCallback();
        bluetoothLeScanner.startScan((List)Arrays.asList(build), build2, this.mScanCallback);
        final KeyboardHandler mHandler = this.mHandler;
        final int mScanAttempt = this.mScanAttempt + 1;
        this.mScanAttempt = mScanAttempt;
        this.mHandler.sendMessageDelayed(mHandler.obtainMessage(10, mScanAttempt, 0), 30000L);
    }
    
    private static String stateToString(final int i) {
        switch (i) {
            default: {
                final StringBuilder sb = new StringBuilder();
                sb.append("STATE_UNKNOWN (");
                sb.append(i);
                sb.append(")");
                return sb.toString();
            }
            case 9: {
                return "STATE_DEVICE_NOT_FOUND";
            }
            case 8: {
                return "STATE_USER_CANCELLED";
            }
            case 7: {
                return "STATE_PAIRING_FAILED";
            }
            case 6: {
                return "STATE_PAIRED";
            }
            case 5: {
                return "STATE_PAIRING";
            }
            case 4: {
                return "STATE_WAITING_FOR_BLUETOOTH";
            }
            case 3: {
                return "STATE_WAITING_FOR_DEVICE_DISCOVERY";
            }
            case 2: {
                return "STATE_WAITING_FOR_TABLET_MODE_EXIT";
            }
            case 1: {
                return "STATE_WAITING_FOR_BOOT_COMPLETED";
            }
            case -1: {
                return "STATE_NOT_ENABLED";
            }
        }
    }
    
    private void stopScanning() {
        if (this.mScanCallback != null) {
            final BluetoothLeScanner bluetoothLeScanner = this.mLocalBluetoothAdapter.getBluetoothLeScanner();
            if (bluetoothLeScanner != null) {
                bluetoothLeScanner.stopScan(this.mScanCallback);
            }
            this.mScanCallback = null;
        }
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("KeyboardUI:");
        final StringBuilder sb = new StringBuilder();
        sb.append("  mEnabled=");
        sb.append(this.mEnabled);
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("  mBootCompleted=");
        sb2.append(this.mEnabled);
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("  mBootCompletedTime=");
        sb3.append(this.mBootCompletedTime);
        printWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("  mKeyboardName=");
        sb4.append(this.mKeyboardName);
        printWriter.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append("  mInTabletMode=");
        sb5.append(this.mInTabletMode);
        printWriter.println(sb5.toString());
        final StringBuilder sb6 = new StringBuilder();
        sb6.append("  mState=");
        sb6.append(stateToString(this.mState));
        printWriter.println(sb6.toString());
    }
    
    @Override
    protected void onBootCompleted() {
        this.mHandler.sendEmptyMessage(1);
    }
    
    public void onBootCompletedInternal() {
        this.mBootCompleted = true;
        this.mBootCompletedTime = SystemClock.uptimeMillis();
        if (this.mState == 1) {
            this.processKeyboardState();
        }
    }
    
    @Override
    protected void onConfigurationChanged(final Configuration configuration) {
    }
    
    public void onTabletModeChanged(final long n, final boolean mInTabletMode) {
        if ((mInTabletMode && this.mInTabletMode != 1) || (!mInTabletMode && this.mInTabletMode != 0)) {
            this.mInTabletMode = (mInTabletMode ? 1 : 0);
            this.processKeyboardState();
        }
    }
    
    @Override
    public void start() {
        this.mContext = super.mContext;
        final HandlerThread handlerThread = new HandlerThread("Keyboard", 10);
        handlerThread.start();
        (this.mHandler = new KeyboardHandler(handlerThread.getLooper())).sendEmptyMessage(0);
    }
    
    private final class BluetoothCallbackHandler implements BluetoothCallback
    {
        @Override
        public void onBluetoothStateChanged(final int n) {
            KeyboardUI.this.mHandler.obtainMessage(4, n, 0).sendToTarget();
        }
        
        @Override
        public void onDeviceBondStateChanged(final CachedBluetoothDevice cachedBluetoothDevice, final int n) {
            KeyboardUI.this.mHandler.obtainMessage(5, n, 0, (Object)cachedBluetoothDevice).sendToTarget();
        }
    }
    
    private final class BluetoothDialogClickListener implements DialogInterface$OnClickListener
    {
        public void onClick(final DialogInterface dialogInterface, int n) {
            if (-1 == n) {
                n = 1;
            }
            else {
                n = 0;
            }
            KeyboardUI.this.mHandler.obtainMessage(3, n, 0).sendToTarget();
            KeyboardUI.this.mDialog = null;
        }
    }
    
    private final class BluetoothDialogDismissListener implements DialogInterface$OnDismissListener
    {
        public void onDismiss(final DialogInterface dialogInterface) {
            KeyboardUI.this.mDialog = null;
        }
    }
    
    private final class BluetoothErrorListener implements ErrorListener
    {
        @Override
        public void onShowError(final Context context, final String s, final int n) {
            KeyboardUI.this.mHandler.obtainMessage(11, n, 0, (Object)new Pair((Object)context, (Object)s)).sendToTarget();
        }
    }
    
    private final class KeyboardHandler extends Handler
    {
        public KeyboardHandler(final Looper looper) {
            super(looper, (Handler$Callback)null, true);
        }
        
        public void handleMessage(final Message message) {
            switch (message.what) {
                case 11: {
                    final Pair pair = (Pair)message.obj;
                    KeyboardUI.this.onShowErrorInternal((Context)pair.first, (String)pair.second, message.arg1);
                    break;
                }
                case 10: {
                    KeyboardUI.this.bleAbortScanInternal(message.arg1);
                    break;
                }
                case 7: {
                    KeyboardUI.this.onBleScanFailedInternal();
                    break;
                }
                case 6: {
                    KeyboardUI.this.onDeviceAddedInternal(KeyboardUI.this.getCachedBluetoothDevice((BluetoothDevice)message.obj));
                    break;
                }
                case 5: {
                    KeyboardUI.this.onDeviceBondStateChangedInternal((CachedBluetoothDevice)message.obj, message.arg1);
                    break;
                }
                case 4: {
                    KeyboardUI.this.onBluetoothStateChangedInternal(message.arg1);
                    break;
                }
                case 3: {
                    final int arg1 = message.arg1;
                    boolean b = true;
                    if (arg1 != 1) {
                        b = false;
                    }
                    if (b) {
                        KeyboardUI.this.mLocalBluetoothAdapter.enable();
                        break;
                    }
                    KeyboardUI.this.mState = 8;
                    break;
                }
                case 2: {
                    KeyboardUI.this.processKeyboardState();
                    break;
                }
                case 1: {
                    KeyboardUI.this.onBootCompletedInternal();
                    break;
                }
                case 0: {
                    KeyboardUI.this.init();
                    break;
                }
            }
        }
    }
    
    private final class KeyboardScanCallback extends ScanCallback
    {
        private boolean isDeviceDiscoverable(final ScanResult scanResult) {
            return (scanResult.getScanRecord().getAdvertiseFlags() & 0x3) != 0x0;
        }
        
        public void onBatchScanResults(final List<ScanResult> list) {
            final Iterator<ScanResult> iterator = list.iterator();
            Object device = null;
            int rssi = Integer.MIN_VALUE;
            while (iterator.hasNext()) {
                final ScanResult scanResult = iterator.next();
                if (this.isDeviceDiscoverable(scanResult) && scanResult.getRssi() > rssi) {
                    device = scanResult.getDevice();
                    rssi = scanResult.getRssi();
                }
            }
            if (device != null) {
                KeyboardUI.this.mHandler.obtainMessage(6, device).sendToTarget();
            }
        }
        
        public void onScanFailed(final int n) {
            KeyboardUI.this.mHandler.obtainMessage(7).sendToTarget();
        }
        
        public void onScanResult(final int n, final ScanResult scanResult) {
            if (this.isDeviceDiscoverable(scanResult)) {
                KeyboardUI.this.mHandler.obtainMessage(6, (Object)scanResult.getDevice()).sendToTarget();
            }
        }
    }
    
    private final class KeyboardUIHandler extends Handler
    {
        public KeyboardUIHandler() {
            super(Looper.getMainLooper(), (Handler$Callback)null, true);
        }
        
        public void handleMessage(final Message message) {
            final int what = message.what;
            if (what != 8) {
                if (what == 9) {
                    if (KeyboardUI.this.mDialog != null) {
                        KeyboardUI.this.mDialog.dismiss();
                    }
                }
            }
            else if (KeyboardUI.this.mDialog == null) {
                final BluetoothDialogClickListener bluetoothDialogClickListener = new BluetoothDialogClickListener();
                final BluetoothDialogDismissListener onDismissListener = new BluetoothDialogDismissListener();
                KeyboardUI.this.mDialog = new BluetoothDialog(KeyboardUI.this.mContext);
                KeyboardUI.this.mDialog.setTitle(R$string.enable_bluetooth_title);
                KeyboardUI.this.mDialog.setMessage(R$string.enable_bluetooth_message);
                KeyboardUI.this.mDialog.setPositiveButton(R$string.enable_bluetooth_confirmation_ok, (DialogInterface$OnClickListener)bluetoothDialogClickListener);
                KeyboardUI.this.mDialog.setNegativeButton(17039360, (DialogInterface$OnClickListener)bluetoothDialogClickListener);
                KeyboardUI.this.mDialog.setOnDismissListener((DialogInterface$OnDismissListener)onDismissListener);
                KeyboardUI.this.mDialog.show();
            }
        }
    }
}
