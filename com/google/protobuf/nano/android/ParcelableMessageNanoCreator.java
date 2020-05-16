// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf.nano.android;

import android.os.Parcel;
import android.os.Parcelable$Creator;
import com.google.protobuf.nano.MessageNano;

public final class ParcelableMessageNanoCreator<T extends MessageNano> implements Parcelable$Creator<T>
{
    static <T extends MessageNano> void writeToParcel(final Class<T> clazz, final MessageNano messageNano, final Parcel parcel) {
        parcel.writeString(clazz.getName());
        parcel.writeByteArray(MessageNano.toByteArray(messageNano));
    }
}
