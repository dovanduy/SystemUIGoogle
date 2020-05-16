// 
// Decompiled by Procyon v0.5.36
// 

package android.support.v4.os;

import android.os.IInterface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Handler;
import android.os.Parcelable$Creator;
import android.annotation.SuppressLint;
import android.os.Parcelable;

@SuppressLint({ "BanParcelableUsage" })
public class ResultReceiver implements Parcelable
{
    public static final Parcelable$Creator<ResultReceiver> CREATOR;
    final Handler mHandler;
    IResultReceiver mReceiver;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<ResultReceiver>() {
            public ResultReceiver createFromParcel(final Parcel parcel) {
                return new ResultReceiver(parcel);
            }
            
            public ResultReceiver[] newArray(final int n) {
                return new ResultReceiver[n];
            }
        };
    }
    
    ResultReceiver(final Parcel parcel) {
        this.mHandler = null;
        this.mReceiver = IResultReceiver.Stub.asInterface(parcel.readStrongBinder());
    }
    
    public int describeContents() {
        return 0;
    }
    
    protected void onReceiveResult(final int n, final Bundle bundle) {
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        synchronized (this) {
            if (this.mReceiver == null) {
                this.mReceiver = new MyResultReceiver();
            }
            parcel.writeStrongBinder(((IInterface)this.mReceiver).asBinder());
        }
    }
    
    class MyResultReceiver extends Stub
    {
        public void send(final int n, final Bundle bundle) {
            final ResultReceiver this$0 = ResultReceiver.this;
            final Handler mHandler = this$0.mHandler;
            if (mHandler != null) {
                mHandler.post((Runnable)this$0.new MyRunnable(n, bundle));
            }
            else {
                this$0.onReceiveResult(n, bundle);
            }
        }
    }
    
    class MyRunnable implements Runnable
    {
        final int mResultCode;
        final Bundle mResultData;
        
        MyRunnable(final int mResultCode, final Bundle mResultData) {
            this.mResultCode = mResultCode;
            this.mResultData = mResultData;
        }
        
        @Override
        public void run() {
            ResultReceiver.this.onReceiveResult(this.mResultCode, this.mResultData);
        }
    }
}
