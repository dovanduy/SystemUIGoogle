// 
// Decompiled by Procyon v0.5.36
// 

package androidx.activity.result;

import android.os.Parcel;
import android.content.Intent;
import android.os.Parcelable$Creator;
import android.annotation.SuppressLint;
import android.os.Parcelable;

@SuppressLint({ "BanParcelableUsage" })
public final class ActivityResult implements Parcelable
{
    public static final Parcelable$Creator<ActivityResult> CREATOR;
    private final Intent mData;
    private final int mResultCode;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<ActivityResult>() {
            public ActivityResult createFromParcel(final Parcel parcel) {
                return new ActivityResult(parcel);
            }
            
            public ActivityResult[] newArray(final int n) {
                return new ActivityResult[n];
            }
        };
    }
    
    public ActivityResult(final int mResultCode, final Intent mData) {
        this.mResultCode = mResultCode;
        this.mData = mData;
    }
    
    ActivityResult(final Parcel parcel) {
        this.mResultCode = parcel.readInt();
        Intent mData;
        if (parcel.readInt() == 0) {
            mData = null;
        }
        else {
            mData = (Intent)Intent.CREATOR.createFromParcel(parcel);
        }
        this.mData = mData;
    }
    
    public static String resultCodeToString(final int i) {
        if (i == -1) {
            return "RESULT_OK";
        }
        if (i != 0) {
            return String.valueOf(i);
        }
        return "RESULT_CANCELED";
    }
    
    public int describeContents() {
        return 0;
    }
    
    public Intent getData() {
        return this.mData;
    }
    
    public int getResultCode() {
        return this.mResultCode;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ActivityResult{resultCode=");
        sb.append(resultCodeToString(this.mResultCode));
        sb.append(", data=");
        sb.append(this.mData);
        sb.append('}');
        return sb.toString();
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        parcel.writeInt(this.mResultCode);
        int n2;
        if (this.mData == null) {
            n2 = 0;
        }
        else {
            n2 = 1;
        }
        parcel.writeInt(n2);
        final Intent mData = this.mData;
        if (mData != null) {
            mData.writeToParcel(parcel, n);
        }
    }
}
