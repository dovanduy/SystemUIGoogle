// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.schedulesprovider;

import android.os.Parcel;
import android.app.PendingIntent;
import android.os.Parcelable$Creator;
import android.os.Parcelable;

public class ScheduleInfo implements Parcelable
{
    public static final Parcelable$Creator<ScheduleInfo> CREATOR;
    private final PendingIntent mPendingIntent;
    private final String mSummary;
    private final String mTitle;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<ScheduleInfo>() {
            public ScheduleInfo createFromParcel(final Parcel parcel) {
                return new ScheduleInfo(parcel, null);
            }
            
            public ScheduleInfo[] newArray(final int n) {
                return new ScheduleInfo[n];
            }
        };
    }
    
    private ScheduleInfo(final Parcel parcel) {
        this.mTitle = parcel.readString();
        this.mSummary = parcel.readString();
        this.mPendingIntent = (PendingIntent)parcel.readParcelable(PendingIntent.class.getClassLoader());
    }
    
    public int describeContents() {
        return 0;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("title: ");
        sb.append(this.mTitle);
        sb.append(", summary: ");
        sb.append(this.mSummary);
        sb.append(", pendingIntent: ");
        sb.append(this.mPendingIntent);
        return sb.toString();
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        parcel.writeString(this.mTitle);
        parcel.writeString(this.mSummary);
        parcel.writeParcelable((Parcelable)this.mPendingIntent, n);
    }
}
