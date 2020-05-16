// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf.nano.android;

import com.google.protobuf.nano.MessageNano;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.protobuf.nano.ExtendableMessageNano;

public abstract class ParcelableExtendableMessageNano<M extends ExtendableMessageNano<M>> extends ExtendableMessageNano<M> implements Parcelable
{
    public int describeContents() {
        return 0;
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        ParcelableMessageNanoCreator.writeToParcel(ParcelableExtendableMessageNano.class, this, parcel);
    }
}
