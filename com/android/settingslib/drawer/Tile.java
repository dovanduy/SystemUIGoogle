// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.drawer;

import android.content.Intent;
import android.os.Parcel;
import android.os.UserHandle;
import java.util.ArrayList;
import android.os.Bundle;
import android.content.pm.ComponentInfo;
import android.os.Parcelable$Creator;
import android.os.Parcelable;

public abstract class Tile implements Parcelable
{
    public static final Parcelable$Creator<Tile> CREATOR;
    private String mCategory;
    protected ComponentInfo mComponentInfo;
    private final String mComponentName;
    private final String mComponentPackage;
    long mLastUpdateTime;
    private Bundle mMetaData;
    public ArrayList<UserHandle> userHandle;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<Tile>() {
            public Tile createFromParcel(final Parcel parcel) {
                final boolean boolean1 = parcel.readBoolean();
                parcel.setDataPosition(0);
                Tile tile;
                if (boolean1) {
                    tile = new ProviderTile(parcel);
                }
                else {
                    tile = new ActivityTile(parcel);
                }
                return tile;
            }
            
            public Tile[] newArray(final int n) {
                return new Tile[n];
            }
        };
        final -$$Lambda$Tile$5_ETnVHzVG6DF0RKPoy76eRI-QM instance = _$$Lambda$Tile$5_ETnVHzVG6DF0RKPoy76eRI_QM.INSTANCE;
    }
    
    Tile(final Parcel parcel) {
        this.userHandle = new ArrayList<UserHandle>();
        parcel.readBoolean();
        this.mComponentPackage = parcel.readString();
        this.mComponentName = parcel.readString();
        new Intent().setClassName(this.mComponentPackage, this.mComponentName);
        for (int int1 = parcel.readInt(), i = 0; i < int1; ++i) {
            this.userHandle.add((UserHandle)UserHandle.CREATOR.createFromParcel(parcel));
        }
        this.mCategory = parcel.readString();
        this.mMetaData = parcel.readBundle();
    }
    
    public int describeContents() {
        return 0;
    }
    
    public Bundle getMetaData() {
        return this.mMetaData;
    }
    
    public int getOrder() {
        if (this.hasOrder()) {
            return this.mMetaData.getInt("com.android.settings.order");
        }
        return 0;
    }
    
    public boolean hasOrder() {
        return this.mMetaData.containsKey("com.android.settings.order") && this.mMetaData.get("com.android.settings.order") instanceof Integer;
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        parcel.writeBoolean(this instanceof ProviderTile);
        parcel.writeString(this.mComponentPackage);
        parcel.writeString(this.mComponentName);
        final int size = this.userHandle.size();
        parcel.writeInt(size);
        for (int i = 0; i < size; ++i) {
            this.userHandle.get(i).writeToParcel(parcel, n);
        }
        parcel.writeString(this.mCategory);
        parcel.writeBundle(this.mMetaData);
    }
}
