// 
// Decompiled by Procyon v0.5.36
// 

package androidx.fragment.app;

import java.util.List;
import android.os.Parcel;
import java.util.ArrayList;
import android.os.Parcelable$Creator;
import android.annotation.SuppressLint;
import android.os.Parcelable;

@SuppressLint({ "BanParcelableUsage" })
final class FragmentManagerState implements Parcelable
{
    public static final Parcelable$Creator<FragmentManagerState> CREATOR;
    ArrayList<FragmentState> mActive;
    ArrayList<String> mAdded;
    BackStackState[] mBackStack;
    int mBackStackIndex;
    String mPrimaryNavActiveWho;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<FragmentManagerState>() {
            public FragmentManagerState createFromParcel(final Parcel parcel) {
                return new FragmentManagerState(parcel);
            }
            
            public FragmentManagerState[] newArray(final int n) {
                return new FragmentManagerState[n];
            }
        };
    }
    
    public FragmentManagerState() {
        this.mPrimaryNavActiveWho = null;
    }
    
    public FragmentManagerState(final Parcel parcel) {
        this.mPrimaryNavActiveWho = null;
        this.mActive = (ArrayList<FragmentState>)parcel.createTypedArrayList((Parcelable$Creator)FragmentState.CREATOR);
        this.mAdded = (ArrayList<String>)parcel.createStringArrayList();
        this.mBackStack = (BackStackState[])parcel.createTypedArray((Parcelable$Creator)BackStackState.CREATOR);
        this.mBackStackIndex = parcel.readInt();
        this.mPrimaryNavActiveWho = parcel.readString();
    }
    
    public int describeContents() {
        return 0;
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        parcel.writeTypedList((List)this.mActive);
        parcel.writeStringList((List)this.mAdded);
        parcel.writeTypedArray((Parcelable[])this.mBackStack, n);
        parcel.writeInt(this.mBackStackIndex);
        parcel.writeString(this.mPrimaryNavActiveWho);
    }
}
