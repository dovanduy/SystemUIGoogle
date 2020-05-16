// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import android.support.annotation.Nullable;
import java.util.HashMap;
import android.os.Parcel;
import android.app.PendingIntent;
import android.graphics.Bitmap;
import java.util.Map;
import android.os.Parcelable$Creator;
import android.os.Parcelable;

public class EntitiesData implements Parcelable
{
    public static final Parcelable$Creator<EntitiesData> CREATOR;
    private final Map<String, Bitmap> bitmapMap;
    private final SuggestParcelables$Entities entities;
    private final Map<String, PendingIntent> pendingIntentMap;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<EntitiesData>() {
            public EntitiesData createFromParcel(final Parcel parcel) {
                return EntitiesData.read(parcel);
            }
            
            public EntitiesData[] newArray(final int n) {
                return new EntitiesData[n];
            }
        };
    }
    
    private EntitiesData(final SuggestParcelables$Entities entities, final Map<String, Bitmap> bitmapMap, final Map<String, PendingIntent> pendingIntentMap) {
        this.entities = entities;
        this.bitmapMap = bitmapMap;
        this.pendingIntentMap = pendingIntentMap;
    }
    
    public static EntitiesData create(final SuggestParcelables$Entities suggestParcelables$Entities, final Map<String, Bitmap> map, final Map<String, PendingIntent> map2) {
        return new EntitiesData(suggestParcelables$Entities, map, map2);
    }
    
    public static EntitiesData read(final Parcel parcel) {
        final SuggestParcelables$Entities create = SuggestParcelables$Entities.create(parcel.readBundle());
        final HashMap<String, Bitmap> hashMap = new HashMap<String, Bitmap>();
        if (create.getExtrasInfo() != null && create.getExtrasInfo().getContainsBitmaps()) {
            parcel.readMap((Map)hashMap, Bitmap.class.getClassLoader());
        }
        final HashMap<String, PendingIntent> hashMap2 = new HashMap<String, PendingIntent>();
        if (create.getExtrasInfo() != null && create.getExtrasInfo().getContainsPendingIntents()) {
            parcel.readMap((Map)hashMap2, PendingIntent.class.getClassLoader());
        }
        return create(create, hashMap, hashMap2);
    }
    
    public int describeContents() {
        return 0;
    }
    
    public SuggestParcelables$Entities entities() {
        return this.entities;
    }
    
    @Nullable
    public Bitmap getBitmap(final String s) {
        return this.bitmapMap.get(s);
    }
    
    @Nullable
    public PendingIntent getPendingIntent(final String s) {
        return this.pendingIntentMap.get(s);
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        this.entities.writeToBundle().writeToParcel(parcel, 0);
        if (this.entities.getExtrasInfo() != null) {
            if (this.entities.getExtrasInfo().getContainsBitmaps()) {
                parcel.writeMap((Map)this.bitmapMap);
            }
            if (this.entities.getExtrasInfo().getContainsPendingIntents()) {
                parcel.writeMap((Map)this.pendingIntentMap);
            }
        }
    }
}
