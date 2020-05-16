// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.wifi;

import android.os.Parcel;
import android.net.ScoredNetwork;
import android.os.Parcelable$Creator;
import android.os.Parcelable;

class TimestampedScoredNetwork implements Parcelable
{
    public static final Parcelable$Creator<TimestampedScoredNetwork> CREATOR;
    private ScoredNetwork mScore;
    private long mUpdatedTimestampMillis;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<TimestampedScoredNetwork>() {
            public TimestampedScoredNetwork createFromParcel(final Parcel parcel) {
                return new TimestampedScoredNetwork(parcel);
            }
            
            public TimestampedScoredNetwork[] newArray(final int n) {
                return new TimestampedScoredNetwork[n];
            }
        };
    }
    
    TimestampedScoredNetwork(final ScoredNetwork mScore, final long mUpdatedTimestampMillis) {
        this.mScore = mScore;
        this.mUpdatedTimestampMillis = mUpdatedTimestampMillis;
    }
    
    protected TimestampedScoredNetwork(final Parcel parcel) {
        this.mScore = (ScoredNetwork)parcel.readParcelable(ScoredNetwork.class.getClassLoader());
        this.mUpdatedTimestampMillis = parcel.readLong();
    }
    
    public int describeContents() {
        return 0;
    }
    
    public ScoredNetwork getScore() {
        return this.mScore;
    }
    
    public long getUpdatedTimestampMillis() {
        return this.mUpdatedTimestampMillis;
    }
    
    public void update(final ScoredNetwork mScore, final long mUpdatedTimestampMillis) {
        this.mScore = mScore;
        this.mUpdatedTimestampMillis = mUpdatedTimestampMillis;
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        parcel.writeParcelable((Parcelable)this.mScore, n);
        parcel.writeLong(this.mUpdatedTimestampMillis);
    }
}
