// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.charging;

import android.view.WindowManager$BadTokenException;
import android.view.ViewGroup$LayoutParams;
import android.util.Slog;
import android.os.Message;
import android.os.Handler$Callback;
import android.view.WindowManager;
import android.view.WindowManager$LayoutParams;
import android.view.View;
import android.os.Handler;
import android.os.Looper;
import android.content.Context;
import android.util.Log;

public class WirelessChargingAnimation
{
    private static final boolean DEBUG;
    private static WirelessChargingView mPreviousWirelessChargingView;
    private final WirelessChargingView mCurrentWirelessChargingView;
    
    static {
        DEBUG = Log.isLoggable("WirelessChargingView", 3);
    }
    
    public WirelessChargingAnimation(final Context context, final Looper looper, final int n, final Callback callback, final boolean b) {
        this.mCurrentWirelessChargingView = new WirelessChargingView(context, looper, n, callback, b);
    }
    
    public static WirelessChargingAnimation makeWirelessChargingAnimation(final Context context, final Looper looper, final int n, final Callback callback, final boolean b) {
        return new WirelessChargingAnimation(context, looper, n, callback, b);
    }
    
    public void show() {
        final WirelessChargingView mCurrentWirelessChargingView = this.mCurrentWirelessChargingView;
        if (mCurrentWirelessChargingView != null && mCurrentWirelessChargingView.mNextView != null) {
            final WirelessChargingView mPreviousWirelessChargingView = WirelessChargingAnimation.mPreviousWirelessChargingView;
            if (mPreviousWirelessChargingView != null) {
                mPreviousWirelessChargingView.hide(0L);
            }
            (WirelessChargingAnimation.mPreviousWirelessChargingView = this.mCurrentWirelessChargingView).show();
            this.mCurrentWirelessChargingView.hide(1133L);
            return;
        }
        throw new RuntimeException("setView must have been called");
    }
    
    public interface Callback
    {
        void onAnimationEnded();
        
        void onAnimationStarting();
    }
    
    private static class WirelessChargingView
    {
        private Callback mCallback;
        private final Handler mHandler;
        private View mNextView;
        private final WindowManager$LayoutParams mParams;
        private View mView;
        private WindowManager mWM;
        
        public WirelessChargingView(final Context context, final Looper looper, final int n, final Callback mCallback, final boolean b) {
            this.mParams = new WindowManager$LayoutParams();
            this.mCallback = mCallback;
            this.mNextView = (View)new WirelessChargingLayout(context, n, b);
            final WindowManager$LayoutParams mParams = this.mParams;
            mParams.height = -2;
            mParams.width = -1;
            mParams.format = -3;
            mParams.type = 2009;
            mParams.setTitle((CharSequence)"Charging Animation");
            mParams.flags = 26;
            mParams.dimAmount = 0.3f;
            Looper myLooper = looper;
            if (looper == null) {
                myLooper = Looper.myLooper();
                if (myLooper == null) {
                    throw new RuntimeException("Can't display wireless animation on a thread that has not called Looper.prepare()");
                }
            }
            this.mHandler = new Handler(myLooper, null) {
                public void handleMessage(final Message message) {
                    final int what = message.what;
                    if (what != 0) {
                        if (what == 1) {
                            WirelessChargingView.this.handleHide();
                            WirelessChargingView.this.mNextView = null;
                        }
                    }
                    else {
                        WirelessChargingView.this.handleShow();
                    }
                }
            };
        }
        
        private void handleHide() {
            if (WirelessChargingAnimation.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("HANDLE HIDE: ");
                sb.append(this);
                sb.append(" mView=");
                sb.append(this.mView);
                Slog.d("WirelessChargingView", sb.toString());
            }
            final View mView = this.mView;
            if (mView != null) {
                if (mView.getParent() != null) {
                    if (WirelessChargingAnimation.DEBUG) {
                        final StringBuilder sb2 = new StringBuilder();
                        sb2.append("REMOVE! ");
                        sb2.append(this.mView);
                        sb2.append(" in ");
                        sb2.append(this);
                        Slog.d("WirelessChargingView", sb2.toString());
                    }
                    final Callback mCallback = this.mCallback;
                    if (mCallback != null) {
                        mCallback.onAnimationEnded();
                    }
                    this.mWM.removeViewImmediate(this.mView);
                }
                this.mView = null;
            }
        }
        
        private void handleShow() {
            if (WirelessChargingAnimation.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("HANDLE SHOW: ");
                sb.append(this);
                sb.append(" mView=");
                sb.append(this.mView);
                sb.append(" mNextView=");
                sb.append(this.mNextView);
                Slog.d("WirelessChargingView", sb.toString());
            }
            if (this.mView != this.mNextView) {
                this.handleHide();
                final View mNextView = this.mNextView;
                this.mView = mNextView;
                final Context applicationContext = mNextView.getContext().getApplicationContext();
                final String opPackageName = this.mView.getContext().getOpPackageName();
                Context context;
                if ((context = applicationContext) == null) {
                    context = this.mView.getContext();
                }
                this.mWM = (WindowManager)context.getSystemService("window");
                final WindowManager$LayoutParams mParams = this.mParams;
                mParams.packageName = opPackageName;
                mParams.hideTimeoutMilliseconds = 1133L;
                if (this.mView.getParent() != null) {
                    if (WirelessChargingAnimation.DEBUG) {
                        final StringBuilder sb2 = new StringBuilder();
                        sb2.append("REMOVE! ");
                        sb2.append(this.mView);
                        sb2.append(" in ");
                        sb2.append(this);
                        Slog.d("WirelessChargingView", sb2.toString());
                    }
                    this.mWM.removeView(this.mView);
                }
                if (WirelessChargingAnimation.DEBUG) {
                    final StringBuilder sb3 = new StringBuilder();
                    sb3.append("ADD! ");
                    sb3.append(this.mView);
                    sb3.append(" in ");
                    sb3.append(this);
                    Slog.d("WirelessChargingView", sb3.toString());
                }
                try {
                    if (this.mCallback != null) {
                        this.mCallback.onAnimationStarting();
                    }
                    this.mWM.addView(this.mView, (ViewGroup$LayoutParams)this.mParams);
                }
                catch (WindowManager$BadTokenException obj) {
                    final StringBuilder sb4 = new StringBuilder();
                    sb4.append("Unable to add wireless charging view. ");
                    sb4.append(obj);
                    Slog.d("WirelessChargingView", sb4.toString());
                }
            }
        }
        
        public void hide(final long n) {
            this.mHandler.removeMessages(1);
            if (WirelessChargingAnimation.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("HIDE: ");
                sb.append(this);
                Slog.d("WirelessChargingView", sb.toString());
            }
            final Handler mHandler = this.mHandler;
            mHandler.sendMessageDelayed(Message.obtain(mHandler, 1), n);
        }
        
        public void show() {
            if (WirelessChargingAnimation.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("SHOW: ");
                sb.append(this);
                Slog.d("WirelessChargingView", sb.toString());
            }
            this.mHandler.obtainMessage(0).sendToTarget();
        }
    }
}
