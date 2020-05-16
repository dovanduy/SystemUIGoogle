// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib;

import android.os.Parcel;
import android.util.SparseBooleanArray;
import android.os.Parcelable$Creator;
import android.os.Parcelable;

public class AppItem implements Comparable<AppItem>, Parcelable
{
    public static final Parcelable$Creator<AppItem> CREATOR;
    public int category;
    public final int key;
    public long total;
    public SparseBooleanArray uids;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<AppItem>() {
            public AppItem createFromParcel(final Parcel parcel) {
                return new AppItem(parcel);
            }
            
            public AppItem[] newArray(final int n) {
                return new AppItem[n];
            }
        };
    }
    
    public AppItem() {
        this.uids = new SparseBooleanArray();
        this.key = 0;
    }
    
    public AppItem(final Parcel parcel) {
        this.uids = new SparseBooleanArray();
        this.key = parcel.readInt();
        this.uids = parcel.readSparseBooleanArray();
        this.total = parcel.readLong();
    }
    
    @Override
    public int compareTo(final AppItem appItem) {
        int n;
        if ((n = Integer.compare(this.category, appItem.category)) == 0) {
            n = Long.compare(appItem.total, this.total);
        }
        return n;
    }
    
    public int describeContents() {
        return 0;
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        parcel.writeInt(this.key);
        parcel.writeSparseBooleanArray(this.uids);
        parcel.writeLong(this.total);
    }
}
