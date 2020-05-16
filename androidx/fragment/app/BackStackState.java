// 
// Decompiled by Procyon v0.5.36
// 

package androidx.fragment.app;

import java.util.List;
import androidx.lifecycle.Lifecycle;
import android.util.Log;
import android.text.TextUtils;
import android.os.Parcel;
import java.util.ArrayList;
import android.os.Parcelable$Creator;
import android.annotation.SuppressLint;
import android.os.Parcelable;

@SuppressLint({ "BanParcelableUsage" })
final class BackStackState implements Parcelable
{
    public static final Parcelable$Creator<BackStackState> CREATOR;
    final int mBreadCrumbShortTitleRes;
    final CharSequence mBreadCrumbShortTitleText;
    final int mBreadCrumbTitleRes;
    final CharSequence mBreadCrumbTitleText;
    final int[] mCurrentMaxLifecycleStates;
    final ArrayList<String> mFragmentWhos;
    final int mIndex;
    final String mName;
    final int[] mOldMaxLifecycleStates;
    final int[] mOps;
    final boolean mReorderingAllowed;
    final ArrayList<String> mSharedElementSourceNames;
    final ArrayList<String> mSharedElementTargetNames;
    final int mTransition;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<BackStackState>() {
            public BackStackState createFromParcel(final Parcel parcel) {
                return new BackStackState(parcel);
            }
            
            public BackStackState[] newArray(final int n) {
                return new BackStackState[n];
            }
        };
    }
    
    public BackStackState(final Parcel parcel) {
        this.mOps = parcel.createIntArray();
        this.mFragmentWhos = (ArrayList<String>)parcel.createStringArrayList();
        this.mOldMaxLifecycleStates = parcel.createIntArray();
        this.mCurrentMaxLifecycleStates = parcel.createIntArray();
        this.mTransition = parcel.readInt();
        this.mName = parcel.readString();
        this.mIndex = parcel.readInt();
        this.mBreadCrumbTitleRes = parcel.readInt();
        this.mBreadCrumbTitleText = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        this.mBreadCrumbShortTitleRes = parcel.readInt();
        this.mBreadCrumbShortTitleText = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        this.mSharedElementSourceNames = (ArrayList<String>)parcel.createStringArrayList();
        this.mSharedElementTargetNames = (ArrayList<String>)parcel.createStringArrayList();
        this.mReorderingAllowed = (parcel.readInt() != 0);
    }
    
    public BackStackState(final BackStackRecord backStackRecord) {
        final int size = backStackRecord.mOps.size();
        this.mOps = new int[size * 5];
        if (backStackRecord.mAddToBackStack) {
            this.mFragmentWhos = new ArrayList<String>(size);
            this.mOldMaxLifecycleStates = new int[size];
            this.mCurrentMaxLifecycleStates = new int[size];
            for (int i = 0, n = 0; i < size; ++i, ++n) {
                final FragmentTransaction.Op op = backStackRecord.mOps.get(i);
                final int[] mOps = this.mOps;
                final int n2 = n + 1;
                mOps[n] = op.mCmd;
                final ArrayList<String> mFragmentWhos = this.mFragmentWhos;
                final Fragment mFragment = op.mFragment;
                String mWho;
                if (mFragment != null) {
                    mWho = mFragment.mWho;
                }
                else {
                    mWho = null;
                }
                mFragmentWhos.add(mWho);
                final int[] mOps2 = this.mOps;
                final int n3 = n2 + 1;
                mOps2[n2] = op.mEnterAnim;
                final int n4 = n3 + 1;
                mOps2[n3] = op.mExitAnim;
                n = n4 + 1;
                mOps2[n4] = op.mPopEnterAnim;
                mOps2[n] = op.mPopExitAnim;
                this.mOldMaxLifecycleStates[i] = op.mOldMaxState.ordinal();
                this.mCurrentMaxLifecycleStates[i] = op.mCurrentMaxState.ordinal();
            }
            this.mTransition = backStackRecord.mTransition;
            this.mName = backStackRecord.mName;
            this.mIndex = backStackRecord.mIndex;
            this.mBreadCrumbTitleRes = backStackRecord.mBreadCrumbTitleRes;
            this.mBreadCrumbTitleText = backStackRecord.mBreadCrumbTitleText;
            this.mBreadCrumbShortTitleRes = backStackRecord.mBreadCrumbShortTitleRes;
            this.mBreadCrumbShortTitleText = backStackRecord.mBreadCrumbShortTitleText;
            this.mSharedElementSourceNames = backStackRecord.mSharedElementSourceNames;
            this.mSharedElementTargetNames = backStackRecord.mSharedElementTargetNames;
            this.mReorderingAllowed = backStackRecord.mReorderingAllowed;
            return;
        }
        throw new IllegalStateException("Not on back stack");
    }
    
    public int describeContents() {
        return 0;
    }
    
    public BackStackRecord instantiate(final FragmentManager fragmentManager) {
        final BackStackRecord obj = new BackStackRecord(fragmentManager);
        int i = 0;
        int n = 0;
        while (i < this.mOps.length) {
            final FragmentTransaction.Op op = new FragmentTransaction.Op();
            final int[] mOps = this.mOps;
            final int n2 = i + 1;
            op.mCmd = mOps[i];
            if (FragmentManager.isLoggingEnabled(2)) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Instantiate ");
                sb.append(obj);
                sb.append(" op #");
                sb.append(n);
                sb.append(" base fragment #");
                sb.append(this.mOps[n2]);
                Log.v("FragmentManager", sb.toString());
            }
            final String s = this.mFragmentWhos.get(n);
            if (s != null) {
                op.mFragment = fragmentManager.findActiveFragment(s);
            }
            else {
                op.mFragment = null;
            }
            op.mOldMaxState = Lifecycle.State.values()[this.mOldMaxLifecycleStates[n]];
            op.mCurrentMaxState = Lifecycle.State.values()[this.mCurrentMaxLifecycleStates[n]];
            final int[] mOps2 = this.mOps;
            final int n3 = n2 + 1;
            final int n4 = mOps2[n2];
            op.mEnterAnim = n4;
            final int n5 = n3 + 1;
            final int n6 = mOps2[n3];
            op.mExitAnim = n6;
            i = n5 + 1;
            final int n7 = mOps2[n5];
            op.mPopEnterAnim = n7;
            final int n8 = mOps2[i];
            op.mPopExitAnim = n8;
            obj.mEnterAnim = n4;
            obj.mExitAnim = n6;
            obj.mPopEnterAnim = n7;
            obj.mPopExitAnim = n8;
            obj.addOp(op);
            ++n;
            ++i;
        }
        obj.mTransition = this.mTransition;
        obj.mName = this.mName;
        obj.mIndex = this.mIndex;
        obj.mAddToBackStack = true;
        obj.mBreadCrumbTitleRes = this.mBreadCrumbTitleRes;
        obj.mBreadCrumbTitleText = this.mBreadCrumbTitleText;
        obj.mBreadCrumbShortTitleRes = this.mBreadCrumbShortTitleRes;
        obj.mBreadCrumbShortTitleText = this.mBreadCrumbShortTitleText;
        obj.mSharedElementSourceNames = this.mSharedElementSourceNames;
        obj.mSharedElementTargetNames = this.mSharedElementTargetNames;
        obj.mReorderingAllowed = this.mReorderingAllowed;
        obj.bumpBackStackNesting(1);
        return obj;
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        parcel.writeIntArray(this.mOps);
        parcel.writeStringList((List)this.mFragmentWhos);
        parcel.writeIntArray(this.mOldMaxLifecycleStates);
        parcel.writeIntArray(this.mCurrentMaxLifecycleStates);
        parcel.writeInt(this.mTransition);
        parcel.writeString(this.mName);
        parcel.writeInt(this.mIndex);
        parcel.writeInt(this.mBreadCrumbTitleRes);
        TextUtils.writeToParcel(this.mBreadCrumbTitleText, parcel, 0);
        parcel.writeInt(this.mBreadCrumbShortTitleRes);
        TextUtils.writeToParcel(this.mBreadCrumbShortTitleText, parcel, 0);
        parcel.writeStringList((List)this.mSharedElementSourceNames);
        parcel.writeStringList((List)this.mSharedElementTargetNames);
        parcel.writeInt((int)(this.mReorderingAllowed ? 1 : 0));
    }
}
