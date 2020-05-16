// 
// Decompiled by Procyon v0.5.36
// 

package com.android.framework.protobuf.nano.android;

import android.os.Parcel;
import android.os.Parcelable;
import com.android.framework.protobuf.nano.MessageNano;

public abstract class ParcelableMessageNano extends MessageNano implements Parcelable
{
    public int describeContents() {
        return 0;
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        ParcelableMessageNanoCreator.writeToParcel(ParcelableMessageNano.class, this, parcel);
    }
}
