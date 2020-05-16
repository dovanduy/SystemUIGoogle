// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.drawer;

import java.util.ArrayList;
import android.os.Parcel;
import java.util.List;
import android.os.Parcelable$Creator;
import android.os.Parcelable;

public class DashboardCategory implements Parcelable
{
    public static final Parcelable$Creator<DashboardCategory> CREATOR;
    public final String key;
    private List<Tile> mTiles;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<DashboardCategory>() {
            public DashboardCategory createFromParcel(final Parcel parcel) {
                return new DashboardCategory(parcel);
            }
            
            public DashboardCategory[] newArray(final int n) {
                return new DashboardCategory[n];
            }
        };
    }
    
    DashboardCategory(final Parcel parcel) {
        this.mTiles = new ArrayList<Tile>();
        this.key = parcel.readString();
        for (int int1 = parcel.readInt(), i = 0; i < int1; ++i) {
            this.mTiles.add((Tile)Tile.CREATOR.createFromParcel(parcel));
        }
    }
    
    public int describeContents() {
        return 0;
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        parcel.writeString(this.key);
        final int size = this.mTiles.size();
        parcel.writeInt(size);
        for (int i = 0; i < size; ++i) {
            this.mTiles.get(i).writeToParcel(parcel, n);
        }
    }
}
