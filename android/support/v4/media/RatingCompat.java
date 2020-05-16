// 
// Decompiled by Procyon v0.5.36
// 

package android.support.v4.media;

import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.annotation.SuppressLint;
import android.os.Parcelable;

@SuppressLint({ "BanParcelableUsage" })
public final class RatingCompat implements Parcelable
{
    public static final Parcelable$Creator<RatingCompat> CREATOR;
    private final int mRatingStyle;
    private final float mRatingValue;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<RatingCompat>() {
            public RatingCompat createFromParcel(final Parcel parcel) {
                return new RatingCompat(parcel.readInt(), parcel.readFloat());
            }
            
            public RatingCompat[] newArray(final int n) {
                return new RatingCompat[n];
            }
        };
    }
    
    RatingCompat(final int mRatingStyle, final float mRatingValue) {
        this.mRatingStyle = mRatingStyle;
        this.mRatingValue = mRatingValue;
    }
    
    public int describeContents() {
        return this.mRatingStyle;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Rating:style=");
        sb.append(this.mRatingStyle);
        sb.append(" rating=");
        final float mRatingValue = this.mRatingValue;
        String value;
        if (mRatingValue < 0.0f) {
            value = "unrated";
        }
        else {
            value = String.valueOf(mRatingValue);
        }
        sb.append(value);
        return sb.toString();
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        parcel.writeInt(this.mRatingStyle);
        parcel.writeFloat(this.mRatingValue);
    }
}
