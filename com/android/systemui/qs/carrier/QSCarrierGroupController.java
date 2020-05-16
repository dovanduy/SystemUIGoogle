// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.carrier;

import android.os.Message;
import android.telephony.SubscriptionManager;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View$OnAttachStateChangeListener;
import java.util.function.Consumer;
import android.view.View$OnClickListener;
import android.util.Log;
import android.os.Looper;
import android.widget.TextView;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.keyguard.CarrierTextController;
import android.view.View;
import android.os.Handler;
import com.android.systemui.plugins.ActivityStarter;

public class QSCarrierGroupController
{
    private final ActivityStarter mActivityStarter;
    private final Handler mBgHandler;
    private final Callback mCallback;
    private View[] mCarrierDividers;
    private QSCarrier[] mCarrierGroups;
    private final CarrierTextController mCarrierTextController;
    private final CellSignalState[] mInfos;
    private boolean mListening;
    private final H mMainHandler;
    private final NetworkController mNetworkController;
    private final TextView mNoSimTextView;
    private final NetworkController.SignalCallback mSignalCallback;
    
    private QSCarrierGroupController(final QSCarrierGroup qsCarrierGroup, final ActivityStarter mActivityStarter, final Handler mBgHandler, final Looper looper, final NetworkController mNetworkController, final CarrierTextController.Builder builder) {
        this.mInfos = new CellSignalState[3];
        this.mCarrierDividers = new View[2];
        this.mCarrierGroups = new QSCarrier[3];
        this.mSignalCallback = new NetworkController.SignalCallback() {
            @Override
            public void setMobileDataIndicators(final IconState iconState, final IconState iconState2, int slotIndex, final int n, final boolean b, final boolean b2, final CharSequence charSequence, final CharSequence charSequence2, final CharSequence charSequence3, final boolean b3, final int i, final boolean b4) {
                slotIndex = QSCarrierGroupController.this.getSlotIndex(i);
                if (slotIndex >= 3) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("setMobileDataIndicators - slot: ");
                    sb.append(slotIndex);
                    Log.w("QSCarrierGroup", sb.toString());
                    return;
                }
                if (slotIndex == -1) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("Invalid SIM slot index for subscription: ");
                    sb2.append(i);
                    Log.e("QSCarrierGroup", sb2.toString());
                    return;
                }
                QSCarrierGroupController.this.mInfos[slotIndex] = new CellSignalState(iconState.visible, iconState.icon, iconState.contentDescription, charSequence.toString(), b4);
                QSCarrierGroupController.this.mMainHandler.obtainMessage(1).sendToTarget();
            }
            
            @Override
            public void setNoSims(final boolean b, final boolean b2) {
                if (b) {
                    for (int i = 0; i < 3; ++i) {
                        QSCarrierGroupController.this.mInfos[i] = QSCarrierGroupController.this.mInfos[i].changeVisibility(false);
                    }
                }
                QSCarrierGroupController.this.mMainHandler.obtainMessage(1).sendToTarget();
            }
        };
        this.mActivityStarter = mActivityStarter;
        this.mBgHandler = mBgHandler;
        this.mNetworkController = mNetworkController;
        int i = 0;
        builder.setShowAirplaneMode(false);
        builder.setShowMissingSim(false);
        this.mCarrierTextController = builder.build();
        final _$$Lambda$QSCarrierGroupController$_ZRGu7m0elMW0_O61dCq0iB2l54 onClickListener = new _$$Lambda$QSCarrierGroupController$_ZRGu7m0elMW0_O61dCq0iB2l54(this);
        qsCarrierGroup.setOnClickListener((View$OnClickListener)onClickListener);
        (this.mNoSimTextView = qsCarrierGroup.getNoSimTextView()).setOnClickListener((View$OnClickListener)onClickListener);
        final H mMainHandler = new H(looper, new _$$Lambda$QSCarrierGroupController$BE6GdsSHi45repDhlm_1nTVH2F0(this), new _$$Lambda$QSCarrierGroupController$zscTWUfQiEW8dLEhglq9U6PNKUo(this));
        this.mMainHandler = mMainHandler;
        this.mCallback = new Callback(mMainHandler);
        this.mCarrierGroups[0] = qsCarrierGroup.getCarrier1View();
        this.mCarrierGroups[1] = qsCarrierGroup.getCarrier2View();
        this.mCarrierGroups[2] = qsCarrierGroup.getCarrier3View();
        this.mCarrierDividers[0] = qsCarrierGroup.getCarrierDivider1();
        this.mCarrierDividers[1] = qsCarrierGroup.getCarrierDivider2();
        while (i < 3) {
            this.mInfos[i] = new CellSignalState();
            this.mCarrierGroups[i].setOnClickListener((View$OnClickListener)onClickListener);
            ++i;
        }
        qsCarrierGroup.setImportantForAccessibility(1);
        qsCarrierGroup.addOnAttachStateChangeListener((View$OnAttachStateChangeListener)new View$OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(final View view) {
            }
            
            public void onViewDetachedFromWindow(final View view) {
                QSCarrierGroupController.this.setListening(false);
            }
        });
    }
    
    private void handleUpdateCarrierInfo(final CarrierTextController.CarrierTextCallbackInfo carrierTextCallbackInfo) {
        if (!this.mMainHandler.getLooper().isCurrentThread()) {
            this.mMainHandler.obtainMessage(0, (Object)carrierTextCallbackInfo).sendToTarget();
            return;
        }
        this.mNoSimTextView.setVisibility(8);
        if (!carrierTextCallbackInfo.airplaneMode && carrierTextCallbackInfo.anySimReady) {
            final boolean[] array = new boolean[3];
            if (carrierTextCallbackInfo.listOfCarriers.length == carrierTextCallbackInfo.subscriptionIds.length) {
                for (int n = 0; n < 3 && n < carrierTextCallbackInfo.listOfCarriers.length; ++n) {
                    final int slotIndex = this.getSlotIndex(carrierTextCallbackInfo.subscriptionIds[n]);
                    if (slotIndex >= 3) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("updateInfoCarrier - slot: ");
                        sb.append(slotIndex);
                        Log.w("QSCarrierGroup", sb.toString());
                    }
                    else if (slotIndex == -1) {
                        final StringBuilder sb2 = new StringBuilder();
                        sb2.append("Invalid SIM slot index for subscription: ");
                        sb2.append(carrierTextCallbackInfo.subscriptionIds[n]);
                        Log.e("QSCarrierGroup", sb2.toString());
                    }
                    else {
                        final CellSignalState[] mInfos = this.mInfos;
                        mInfos[slotIndex] = mInfos[slotIndex].changeVisibility(true);
                        array[slotIndex] = true;
                        this.mCarrierGroups[slotIndex].setCarrierText(carrierTextCallbackInfo.listOfCarriers[n].toString().trim());
                        this.mCarrierGroups[slotIndex].setVisibility(0);
                    }
                }
                for (int i = 0; i < 3; ++i) {
                    if (!array[i]) {
                        final CellSignalState[] mInfos2 = this.mInfos;
                        mInfos2[i] = mInfos2[i].changeVisibility(false);
                        this.mCarrierGroups[i].setVisibility(8);
                    }
                }
            }
            else {
                Log.e("QSCarrierGroup", "Carrier information arrays not of same length");
            }
        }
        else {
            for (int j = 0; j < 3; ++j) {
                final CellSignalState[] mInfos3 = this.mInfos;
                mInfos3[j] = mInfos3[j].changeVisibility(false);
                this.mCarrierGroups[j].setCarrierText("");
                this.mCarrierGroups[j].setVisibility(8);
            }
            this.mNoSimTextView.setText(carrierTextCallbackInfo.carrierText);
            if (!TextUtils.isEmpty(carrierTextCallbackInfo.carrierText)) {
                this.mNoSimTextView.setVisibility(0);
            }
        }
        this.handleUpdateState();
    }
    
    private void handleUpdateState() {
        if (!this.mMainHandler.getLooper().isCurrentThread()) {
            this.mMainHandler.obtainMessage(1).sendToTarget();
            return;
        }
        final int n = 0;
        for (int i = 0; i < 3; ++i) {
            this.mCarrierGroups[i].updateState(this.mInfos[i]);
        }
        final View view = this.mCarrierDividers[0];
        final CellSignalState[] mInfos = this.mInfos;
        int visibility;
        if (mInfos[0].visible && mInfos[1].visible) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        view.setVisibility(visibility);
        final View view2 = this.mCarrierDividers[1];
        final CellSignalState[] mInfos2 = this.mInfos;
        int visibility2 = 0;
        Label_0166: {
            if (mInfos2[1].visible) {
                visibility2 = n;
                if (mInfos2[2].visible) {
                    break Label_0166;
                }
            }
            final CellSignalState[] mInfos3 = this.mInfos;
            if (mInfos3[0].visible && mInfos3[2].visible) {
                visibility2 = n;
            }
            else {
                visibility2 = 8;
            }
        }
        view2.setVisibility(visibility2);
    }
    
    private void updateListeners() {
        if (this.mListening) {
            if (this.mNetworkController.hasVoiceCallingFeature()) {
                this.mNetworkController.addCallback(this.mSignalCallback);
            }
            this.mCarrierTextController.setListening((CarrierTextController.CarrierTextCallback)this.mCallback);
        }
        else {
            this.mNetworkController.removeCallback(this.mSignalCallback);
            this.mCarrierTextController.setListening(null);
        }
    }
    
    protected int getSlotIndex(final int n) {
        return SubscriptionManager.getSlotIndex(n);
    }
    
    public void setListening(final boolean mListening) {
        if (mListening == this.mListening) {
            return;
        }
        this.mListening = mListening;
        this.mBgHandler.post((Runnable)new _$$Lambda$QSCarrierGroupController$pzlpJyLK2Dv9dx4tpL3T77ALFmY(this));
    }
    
    public static class Builder
    {
        private final ActivityStarter mActivityStarter;
        private final CarrierTextController.Builder mCarrierTextControllerBuilder;
        private final Handler mHandler;
        private final Looper mLooper;
        private final NetworkController mNetworkController;
        private QSCarrierGroup mView;
        
        public Builder(final ActivityStarter mActivityStarter, final Handler mHandler, final Looper mLooper, final NetworkController mNetworkController, final CarrierTextController.Builder mCarrierTextControllerBuilder) {
            this.mActivityStarter = mActivityStarter;
            this.mHandler = mHandler;
            this.mLooper = mLooper;
            this.mNetworkController = mNetworkController;
            this.mCarrierTextControllerBuilder = mCarrierTextControllerBuilder;
        }
        
        public QSCarrierGroupController build() {
            return new QSCarrierGroupController(this.mView, this.mActivityStarter, this.mHandler, this.mLooper, this.mNetworkController, this.mCarrierTextControllerBuilder, null);
        }
        
        public Builder setQSCarrierGroup(final QSCarrierGroup mView) {
            this.mView = mView;
            return this;
        }
    }
    
    private static class Callback implements CarrierTextCallback
    {
        private H mHandler;
        
        Callback(final H mHandler) {
            this.mHandler = mHandler;
        }
        
        @Override
        public void updateCarrierInfo(final CarrierTextCallbackInfo carrierTextCallbackInfo) {
            this.mHandler.obtainMessage(0, (Object)carrierTextCallbackInfo).sendToTarget();
        }
    }
    
    private static class H extends Handler
    {
        private Consumer<CarrierTextController.CarrierTextCallbackInfo> mUpdateCarrierInfo;
        private Runnable mUpdateState;
        
        H(final Looper looper, final Consumer<CarrierTextController.CarrierTextCallbackInfo> mUpdateCarrierInfo, final Runnable mUpdateState) {
            super(looper);
            this.mUpdateCarrierInfo = mUpdateCarrierInfo;
            this.mUpdateState = mUpdateState;
        }
        
        public void handleMessage(final Message message) {
            final int what = message.what;
            if (what != 0) {
                if (what != 1) {
                    super.handleMessage(message);
                }
                else {
                    this.mUpdateState.run();
                }
            }
            else {
                this.mUpdateCarrierInfo.accept((CarrierTextController.CarrierTextCallbackInfo)message.obj);
            }
        }
    }
}
