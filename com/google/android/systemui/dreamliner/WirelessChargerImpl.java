// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.dreamliner;

import vendor.google.wireless_charger.V1_1.AlignInfo;
import vendor.google.wireless_charger.V1_0.KeyExchangeResponse;
import vendor.google.wireless_charger.V1_0.DockInfo;
import vendor.google.wireless_charger.V1_1.IWirelessChargerInfoCallback;
import com.android.internal.annotations.VisibleForTesting$Visibility;
import com.android.internal.annotations.VisibleForTesting;
import android.util.Log;
import java.util.ArrayList;
import android.os.Looper;
import java.util.concurrent.TimeUnit;
import android.os.Handler;
import vendor.google.wireless_charger.V1_0.IWirelessCharger;
import android.os.IHwBinder$DeathRecipient;

public class WirelessChargerImpl extends WirelessCharger implements IHwBinder$DeathRecipient, isDockPresentCallback
{
    private static final long MAX_POLLING_TIMEOUT_NS;
    private isDockPresentCallback mCallback;
    private final Handler mHandler;
    private long mPollingStartedTimeNs;
    private final Runnable mRunnable;
    private vendor.google.wireless_charger.V1_2.IWirelessCharger mWirelessCharger;
    
    static {
        MAX_POLLING_TIMEOUT_NS = TimeUnit.SECONDS.toNanos(5L);
    }
    
    public WirelessChargerImpl() {
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mRunnable = new Runnable() {
            @Override
            public void run() {
                final WirelessChargerImpl this$0 = WirelessChargerImpl.this;
                this$0.isDockPresentInternal(this$0);
            }
        };
    }
    
    private ArrayList<Byte> convertPrimitiveArrayToArrayList(final byte[] array) {
        if (array != null && array.length > 0) {
            final ArrayList<Byte> list = new ArrayList<Byte>();
            for (int length = array.length, i = 0; i < length; ++i) {
                list.add(array[i]);
            }
            return list;
        }
        return null;
    }
    
    private void initHALInterface() {
        if (this.mWirelessCharger == null) {
            try {
                (this.mWirelessCharger = vendor.google.wireless_charger.V1_2.IWirelessCharger.getService()).linkToDeath((IHwBinder$DeathRecipient)this, 0L);
            }
            catch (Exception ex) {
                final StringBuilder sb = new StringBuilder();
                sb.append("no wireless charger hal found: ");
                sb.append(ex.getMessage());
                Log.i("Dreamliner-WLC_HAL", sb.toString());
                this.mWirelessCharger = null;
            }
        }
    }
    
    private void isDockPresentInternal(final isDockPresentCallback isDockPresentCallback) {
        this.initHALInterface();
        final vendor.google.wireless_charger.V1_2.IWirelessCharger mWirelessCharger = this.mWirelessCharger;
        if (mWirelessCharger != null) {
            try {
                mWirelessCharger.isDockPresent(isDockPresentCallback);
            }
            catch (Exception ex) {
                final StringBuilder sb = new StringBuilder();
                sb.append("isDockPresent fail: ");
                sb.append(ex.getMessage());
                Log.i("Dreamliner-WLC_HAL", sb.toString());
            }
        }
    }
    
    @VisibleForTesting(visibility = VisibleForTesting$Visibility.PACKAGE)
    @Override
    public void asyncIsDockPresent(final IsDockPresentCallback isDockPresentCallback) {
        this.initHALInterface();
        if (this.mWirelessCharger != null) {
            this.mPollingStartedTimeNs = System.nanoTime();
            this.mCallback = new IsDockPresentCallbackWrapper(isDockPresentCallback);
            this.mHandler.removeCallbacks(this.mRunnable);
            this.mHandler.postDelayed(this.mRunnable, 100L);
        }
    }
    
    @VisibleForTesting(visibility = VisibleForTesting$Visibility.PACKAGE)
    @Override
    public void challenge(final byte b, final byte[] array, final ChallengeCallback challengeCallback) {
        this.initHALInterface();
        if (this.mWirelessCharger != null) {
            try {
                this.mWirelessCharger.challenge(b, this.convertPrimitiveArrayToArrayList(array), (IWirelessCharger.challengeCallback)new ChallengeCallbackWrapper(challengeCallback));
            }
            catch (Exception ex) {
                final StringBuilder sb = new StringBuilder();
                sb.append("challenge fail: ");
                sb.append(ex.getMessage());
                Log.i("Dreamliner-WLC_HAL", sb.toString());
            }
        }
    }
    
    @VisibleForTesting(visibility = VisibleForTesting$Visibility.PACKAGE)
    @Override
    public void getInformation(final GetInformationCallback getInformationCallback) {
        this.initHALInterface();
        final vendor.google.wireless_charger.V1_2.IWirelessCharger mWirelessCharger = this.mWirelessCharger;
        if (mWirelessCharger != null) {
            try {
                mWirelessCharger.getInformation((IWirelessCharger.getInformationCallback)new GetInformationCallbackWrapper(getInformationCallback));
            }
            catch (Exception ex) {
                final StringBuilder sb = new StringBuilder();
                sb.append("getInformation fail: ");
                sb.append(ex.getMessage());
                Log.i("Dreamliner-WLC_HAL", sb.toString());
            }
        }
    }
    
    @VisibleForTesting(visibility = VisibleForTesting$Visibility.PACKAGE)
    @Override
    public void keyExchange(final byte[] array, final KeyExchangeCallback keyExchangeCallback) {
        this.initHALInterface();
        if (this.mWirelessCharger != null) {
            try {
                this.mWirelessCharger.keyExchange(this.convertPrimitiveArrayToArrayList(array), (IWirelessCharger.keyExchangeCallback)new KeyExchangeCallbackWrapper(keyExchangeCallback));
            }
            catch (Exception ex) {
                final StringBuilder sb = new StringBuilder();
                sb.append("keyExchange fail: ");
                sb.append(ex.getMessage());
                Log.i("Dreamliner-WLC_HAL", sb.toString());
            }
        }
    }
    
    public void onValues(final boolean b, final byte b2, final byte b3, final boolean b4, final int n) {
        if (System.nanoTime() < this.mPollingStartedTimeNs + WirelessChargerImpl.MAX_POLLING_TIMEOUT_NS && n == 0) {
            this.mHandler.postDelayed(this.mRunnable, 100L);
            return;
        }
        final isDockPresentCallback mCallback = this.mCallback;
        if (mCallback == null) {
            return;
        }
        mCallback.onValues(b, b2, b3, b4, n);
        this.mCallback = null;
    }
    
    @VisibleForTesting(visibility = VisibleForTesting$Visibility.PACKAGE)
    @Override
    public void registerAlignInfo(final AlignInfoListener alignInfoListener) {
        this.initHALInterface();
        final vendor.google.wireless_charger.V1_2.IWirelessCharger mWirelessCharger = this.mWirelessCharger;
        if (mWirelessCharger != null) {
            try {
                mWirelessCharger.registerCallback(new WirelessChargerInfoCallback(alignInfoListener));
            }
            catch (Exception ex) {
                final StringBuilder sb = new StringBuilder();
                sb.append("register alignInfo callback fail: ");
                sb.append(ex.getMessage());
                Log.i("Dreamliner-WLC_HAL", sb.toString());
            }
        }
    }
    
    public void serviceDied(final long n) {
        Log.i("Dreamliner-WLC_HAL", "serviceDied");
        this.mWirelessCharger = null;
    }
    
    final class ChallengeCallbackWrapper implements challengeCallback
    {
        private ChallengeCallback mCallback;
        
        public ChallengeCallbackWrapper(final WirelessChargerImpl wirelessChargerImpl, final ChallengeCallback mCallback) {
            this.mCallback = mCallback;
        }
        
        @Override
        public void onValues(final byte value, final ArrayList<Byte> list) {
            this.mCallback.onCallback(new Byte(value), list);
        }
    }
    
    final class GetInformationCallbackWrapper implements getInformationCallback
    {
        private GetInformationCallback mCallback;
        
        public GetInformationCallbackWrapper(final WirelessChargerImpl wirelessChargerImpl, final GetInformationCallback mCallback) {
            this.mCallback = mCallback;
        }
        
        private com.google.android.systemui.dreamliner.DockInfo convertDockInfo(final DockInfo dockInfo) {
            return new com.google.android.systemui.dreamliner.DockInfo(dockInfo.manufacturer, dockInfo.model, dockInfo.serial, new Byte(dockInfo.type));
        }
        
        @Override
        public void onValues(final byte value, final DockInfo dockInfo) {
            this.mCallback.onCallback(new Byte(value), this.convertDockInfo(dockInfo));
        }
    }
    
    final class IsDockPresentCallbackWrapper implements isDockPresentCallback
    {
        private IsDockPresentCallback mCallback;
        
        public IsDockPresentCallbackWrapper(final WirelessChargerImpl wirelessChargerImpl, final IsDockPresentCallback mCallback) {
            this.mCallback = mCallback;
        }
        
        @Override
        public void onValues(final boolean b, final byte b2, final byte b3, final boolean b4, final int n) {
            this.mCallback.onCallback(b, b2, b3, b4, n);
        }
    }
    
    final class KeyExchangeCallbackWrapper implements keyExchangeCallback
    {
        private KeyExchangeCallback mCallback;
        
        public KeyExchangeCallbackWrapper(final WirelessChargerImpl wirelessChargerImpl, final KeyExchangeCallback mCallback) {
            this.mCallback = mCallback;
        }
        
        @Override
        public void onValues(final byte b, final KeyExchangeResponse keyExchangeResponse) {
            if (keyExchangeResponse != null) {
                this.mCallback.onCallback(new Byte(b), keyExchangeResponse.dockId, keyExchangeResponse.dockPublicKey);
            }
            else {
                this.mCallback.onCallback(new Byte(b), (byte)(-1), null);
            }
        }
    }
    
    final class WirelessChargerInfoCallback extends Stub
    {
        private AlignInfoListener mListener;
        
        public WirelessChargerInfoCallback(final WirelessChargerImpl wirelessChargerImpl, final AlignInfoListener mListener) {
            this.mListener = mListener;
        }
        
        private DockAlignInfo convertAlignInfo(final AlignInfo alignInfo) {
            return new DockAlignInfo(new Byte(alignInfo.alignState), new Byte(alignInfo.alignPct));
        }
        
        public void alignInfoChanged(final AlignInfo alignInfo) {
            this.mListener.onAlignInfoChanged(this.convertAlignInfo(alignInfo));
        }
    }
}
