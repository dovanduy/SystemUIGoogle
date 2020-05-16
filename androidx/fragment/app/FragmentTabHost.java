// 
// Decompiled by Procyon v0.5.36
// 

package androidx.fragment.app;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.view.View$BaseSavedState;
import android.annotation.SuppressLint;
import android.os.Parcelable;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import java.util.ArrayList;
import android.content.Context;
import android.widget.TabHost$OnTabChangeListener;
import android.widget.TabHost;

@Deprecated
public class FragmentTabHost extends TabHost implements TabHost$OnTabChangeListener
{
    private boolean mAttached;
    private int mContainerId;
    private Context mContext;
    private FragmentManager mFragmentManager;
    private TabInfo mLastTab;
    private TabHost$OnTabChangeListener mOnTabChangeListener;
    private final ArrayList<TabInfo> mTabs;
    
    @Deprecated
    public FragmentTabHost(final Context context, final AttributeSet set) {
        super(context, set);
        this.mTabs = new ArrayList<TabInfo>();
        this.initFragmentTabHost(context, set);
    }
    
    private FragmentTransaction doTabChanged(final String s, final FragmentTransaction fragmentTransaction) {
        final TabInfo tabInfoForTag = this.getTabInfoForTag(s);
        FragmentTransaction beginTransaction = fragmentTransaction;
        if (this.mLastTab != tabInfoForTag) {
            if ((beginTransaction = fragmentTransaction) == null) {
                beginTransaction = this.mFragmentManager.beginTransaction();
            }
            final TabInfo mLastTab = this.mLastTab;
            if (mLastTab != null) {
                final Fragment fragment = mLastTab.fragment;
                if (fragment != null) {
                    beginTransaction.detach(fragment);
                }
            }
            if (tabInfoForTag != null) {
                final Fragment fragment2 = tabInfoForTag.fragment;
                if (fragment2 == null) {
                    (tabInfoForTag.fragment = this.mFragmentManager.getFragmentFactory().instantiate(this.mContext.getClassLoader(), tabInfoForTag.clss.getName())).setArguments(tabInfoForTag.args);
                    beginTransaction.add(this.mContainerId, tabInfoForTag.fragment, tabInfoForTag.tag);
                }
                else {
                    beginTransaction.attach(fragment2);
                }
            }
            this.mLastTab = tabInfoForTag;
        }
        return beginTransaction;
    }
    
    private TabInfo getTabInfoForTag(final String anObject) {
        for (int size = this.mTabs.size(), i = 0; i < size; ++i) {
            final TabInfo tabInfo = this.mTabs.get(i);
            if (tabInfo.tag.equals(anObject)) {
                return tabInfo;
            }
        }
        return null;
    }
    
    private void initFragmentTabHost(final Context context, final AttributeSet set) {
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, new int[] { 16842995 }, 0, 0);
        this.mContainerId = obtainStyledAttributes.getResourceId(0, 0);
        obtainStyledAttributes.recycle();
        super.setOnTabChangedListener((TabHost$OnTabChangeListener)this);
    }
    
    @Deprecated
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final String currentTabTag = this.getCurrentTabTag();
        final int size = this.mTabs.size();
        FragmentTransaction fragmentTransaction = null;
        FragmentTransaction beginTransaction;
        for (int i = 0; i < size; ++i, fragmentTransaction = beginTransaction) {
            final TabInfo mLastTab = this.mTabs.get(i);
            final Fragment fragmentByTag = this.mFragmentManager.findFragmentByTag(mLastTab.tag);
            mLastTab.fragment = fragmentByTag;
            beginTransaction = fragmentTransaction;
            if (fragmentByTag != null) {
                beginTransaction = fragmentTransaction;
                if (!fragmentByTag.isDetached()) {
                    if (mLastTab.tag.equals(currentTabTag)) {
                        this.mLastTab = mLastTab;
                        beginTransaction = fragmentTransaction;
                    }
                    else {
                        if ((beginTransaction = fragmentTransaction) == null) {
                            beginTransaction = this.mFragmentManager.beginTransaction();
                        }
                        beginTransaction.detach(mLastTab.fragment);
                    }
                }
            }
        }
        this.mAttached = true;
        final FragmentTransaction doTabChanged = this.doTabChanged(currentTabTag, fragmentTransaction);
        if (doTabChanged != null) {
            doTabChanged.commit();
            this.mFragmentManager.executePendingTransactions();
        }
    }
    
    @Deprecated
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mAttached = false;
    }
    
    @Deprecated
    protected void onRestoreInstanceState(@SuppressLint({ "UnknownNullness" }) final Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        final SavedState savedState = (SavedState)parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.setCurrentTabByTag(savedState.curTab);
    }
    
    @Deprecated
    protected Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.curTab = this.getCurrentTabTag();
        return (Parcelable)savedState;
    }
    
    @Deprecated
    public void onTabChanged(final String s) {
        if (this.mAttached) {
            final FragmentTransaction doTabChanged = this.doTabChanged(s, null);
            if (doTabChanged != null) {
                doTabChanged.commit();
            }
        }
        final TabHost$OnTabChangeListener mOnTabChangeListener = this.mOnTabChangeListener;
        if (mOnTabChangeListener != null) {
            mOnTabChangeListener.onTabChanged(s);
        }
    }
    
    @Deprecated
    public void setOnTabChangedListener(final TabHost$OnTabChangeListener mOnTabChangeListener) {
        this.mOnTabChangeListener = mOnTabChangeListener;
    }
    
    @Deprecated
    public void setup() {
        throw new IllegalStateException("Must call setup() that takes a Context and FragmentManager");
    }
    
    static class SavedState extends View$BaseSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        String curTab;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$Creator<SavedState>() {
                public SavedState createFromParcel(final Parcel parcel) {
                    return new SavedState(parcel);
                }
                
                public SavedState[] newArray(final int n) {
                    return new SavedState[n];
                }
            };
        }
        
        SavedState(final Parcel parcel) {
            super(parcel);
            this.curTab = parcel.readString();
        }
        
        SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("FragmentTabHost.SavedState{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(" curTab=");
            sb.append(this.curTab);
            sb.append("}");
            return sb.toString();
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeString(this.curTab);
        }
    }
    
    static final class TabInfo
    {
        final Bundle args;
        final Class<?> clss;
        Fragment fragment;
        final String tag;
    }
}
