// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.os.Parcelable;
import android.text.TextUtils;
import android.os.Bundle;
import java.util.Collections;
import android.util.Log;
import android.content.res.TypedArray;
import androidx.core.content.res.TypedArrayUtils;
import java.util.ArrayList;
import android.util.AttributeSet;
import android.content.Context;
import java.util.List;
import androidx.collection.SimpleArrayMap;
import android.os.Handler;

public abstract class PreferenceGroup extends Preference
{
    private boolean mAttachedToHierarchy;
    private final Runnable mClearRecycleCacheRunnable;
    private int mCurrentPreferenceOrder;
    private final Handler mHandler;
    final SimpleArrayMap<String, Long> mIdRecycleCache;
    private int mInitialExpandedChildrenCount;
    private OnExpandButtonClickListener mOnExpandButtonClickListener;
    private boolean mOrderingAsAdded;
    private List<Preference> mPreferences;
    
    public PreferenceGroup(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public PreferenceGroup(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public PreferenceGroup(final Context context, final AttributeSet set, int n, final int n2) {
        super(context, set, n, n2);
        this.mIdRecycleCache = new SimpleArrayMap<String, Long>();
        this.mHandler = new Handler();
        this.mOrderingAsAdded = true;
        this.mCurrentPreferenceOrder = 0;
        this.mAttachedToHierarchy = false;
        this.mInitialExpandedChildrenCount = Integer.MAX_VALUE;
        this.mOnExpandButtonClickListener = null;
        this.mClearRecycleCacheRunnable = new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    PreferenceGroup.this.mIdRecycleCache.clear();
                }
            }
        };
        this.mPreferences = new ArrayList<Preference>();
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.PreferenceGroup, n, n2);
        n = R$styleable.PreferenceGroup_orderingFromXml;
        this.mOrderingAsAdded = TypedArrayUtils.getBoolean(obtainStyledAttributes, n, n, true);
        if (obtainStyledAttributes.hasValue(R$styleable.PreferenceGroup_initialExpandedChildrenCount)) {
            n = R$styleable.PreferenceGroup_initialExpandedChildrenCount;
            this.setInitialExpandedChildrenCount(TypedArrayUtils.getInt(obtainStyledAttributes, n, n, Integer.MAX_VALUE));
        }
        obtainStyledAttributes.recycle();
    }
    
    private boolean removePreferenceInt(final Preference preference) {
        synchronized (this) {
            preference.onPrepareForRemoval();
            if (preference.getParent() == this) {
                preference.assignParent(null);
            }
            final boolean remove = this.mPreferences.remove(preference);
            if (remove) {
                final String key = preference.getKey();
                if (key != null) {
                    this.mIdRecycleCache.put(key, preference.getId());
                    this.mHandler.removeCallbacks(this.mClearRecycleCacheRunnable);
                    this.mHandler.post(this.mClearRecycleCacheRunnable);
                }
                if (this.mAttachedToHierarchy) {
                    preference.onDetached();
                }
            }
            return remove;
        }
    }
    
    public void addItemFromInflater(final Preference preference) {
        this.addPreference(preference);
    }
    
    public boolean addPreference(final Preference key) {
        if (this.mPreferences.contains(key)) {
            return true;
        }
        if (key.getKey() != null) {
            PreferenceGroup parent;
            for (parent = this; parent.getParent() != null; parent = parent.getParent()) {}
            final String key2 = key.getKey();
            if (parent.findPreference(key2) != null) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Found duplicated key: \"");
                sb.append(key2);
                sb.append("\". This can cause unintended behaviour, please use unique keys for every preference.");
                Log.e("PreferenceGroup", sb.toString());
            }
        }
        if (key.getOrder() == Integer.MAX_VALUE) {
            if (this.mOrderingAsAdded) {
                key.setOrder(this.mCurrentPreferenceOrder++);
            }
            if (key instanceof PreferenceGroup) {
                ((PreferenceGroup)key).setOrderingAsAdded(this.mOrderingAsAdded);
            }
        }
        final int binarySearch = Collections.binarySearch(this.mPreferences, key);
        int n;
        if ((n = binarySearch) < 0) {
            n = binarySearch * -1 - 1;
        }
        if (!this.onPrepareAddPreference(key)) {
            return false;
        }
        synchronized (this) {
            this.mPreferences.add(n, key);
            // monitorexit(this)
            final PreferenceManager preferenceManager = this.getPreferenceManager();
            final String key3 = key.getKey();
            long n2;
            if (key3 != null && this.mIdRecycleCache.containsKey(key3)) {
                n2 = this.mIdRecycleCache.get(key3);
                this.mIdRecycleCache.remove(key3);
            }
            else {
                n2 = preferenceManager.getNextId();
            }
            key.onAttachedToHierarchy(preferenceManager, n2);
            key.assignParent(this);
            if (this.mAttachedToHierarchy) {
                key.onAttached();
            }
            this.notifyHierarchyChanged();
            return true;
        }
    }
    
    protected void dispatchRestoreInstanceState(final Bundle bundle) {
        super.dispatchRestoreInstanceState(bundle);
        for (int preferenceCount = this.getPreferenceCount(), i = 0; i < preferenceCount; ++i) {
            this.getPreference(i).dispatchRestoreInstanceState(bundle);
        }
    }
    
    protected void dispatchSaveInstanceState(final Bundle bundle) {
        super.dispatchSaveInstanceState(bundle);
        for (int preferenceCount = this.getPreferenceCount(), i = 0; i < preferenceCount; ++i) {
            this.getPreference(i).dispatchSaveInstanceState(bundle);
        }
    }
    
    public <T extends Preference> T findPreference(final CharSequence charSequence) {
        if (charSequence == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (TextUtils.equals((CharSequence)this.getKey(), charSequence)) {
            return (T)this;
        }
        for (int preferenceCount = this.getPreferenceCount(), i = 0; i < preferenceCount; ++i) {
            final Preference preference = this.getPreference(i);
            if (TextUtils.equals((CharSequence)preference.getKey(), charSequence)) {
                return (T)preference;
            }
            if (preference instanceof PreferenceGroup) {
                final Preference preference2 = ((PreferenceGroup)preference).findPreference(charSequence);
                if (preference2 != null) {
                    return (T)preference2;
                }
            }
        }
        return null;
    }
    
    public int getInitialExpandedChildrenCount() {
        return this.mInitialExpandedChildrenCount;
    }
    
    public OnExpandButtonClickListener getOnExpandButtonClickListener() {
        return this.mOnExpandButtonClickListener;
    }
    
    public Preference getPreference(final int n) {
        return this.mPreferences.get(n);
    }
    
    public int getPreferenceCount() {
        return this.mPreferences.size();
    }
    
    protected boolean isOnSameScreenAsChildren() {
        return true;
    }
    
    @Override
    public void notifyDependencyChange(final boolean b) {
        super.notifyDependencyChange(b);
        for (int preferenceCount = this.getPreferenceCount(), i = 0; i < preferenceCount; ++i) {
            this.getPreference(i).onParentChanged(this, b);
        }
    }
    
    @Override
    public void onAttached() {
        super.onAttached();
        this.mAttachedToHierarchy = true;
        for (int preferenceCount = this.getPreferenceCount(), i = 0; i < preferenceCount; ++i) {
            this.getPreference(i).onAttached();
        }
    }
    
    @Override
    public void onDetached() {
        super.onDetached();
        int i = 0;
        this.mAttachedToHierarchy = false;
        while (i < this.getPreferenceCount()) {
            this.getPreference(i).onDetached();
            ++i;
        }
    }
    
    protected boolean onPrepareAddPreference(final Preference preference) {
        preference.onParentChanged(this, this.shouldDisableDependents());
        return true;
    }
    
    @Override
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable != null && parcelable.getClass().equals(SavedState.class)) {
            final SavedState savedState = (SavedState)parcelable;
            this.mInitialExpandedChildrenCount = savedState.mInitialExpandedChildrenCount;
            super.onRestoreInstanceState(savedState.getSuperState());
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }
    
    @Override
    protected Parcelable onSaveInstanceState() {
        return (Parcelable)new SavedState(super.onSaveInstanceState(), this.mInitialExpandedChildrenCount);
    }
    
    public boolean removePreference(final Preference preference) {
        final boolean removePreferenceInt = this.removePreferenceInt(preference);
        this.notifyHierarchyChanged();
        return removePreferenceInt;
    }
    
    public void setInitialExpandedChildrenCount(final int mInitialExpandedChildrenCount) {
        if (mInitialExpandedChildrenCount != Integer.MAX_VALUE && !this.hasKey()) {
            final StringBuilder sb = new StringBuilder();
            sb.append(this.getClass().getSimpleName());
            sb.append(" should have a key defined if it contains an expandable preference");
            Log.e("PreferenceGroup", sb.toString());
        }
        this.mInitialExpandedChildrenCount = mInitialExpandedChildrenCount;
    }
    
    public void setOrderingAsAdded(final boolean mOrderingAsAdded) {
        this.mOrderingAsAdded = mOrderingAsAdded;
    }
    
    void sortPreferences() {
        synchronized (this) {
            Collections.sort(this.mPreferences);
        }
    }
    
    public interface OnExpandButtonClickListener
    {
        void onExpandButtonClick();
    }
    
    static class SavedState extends BaseSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        int mInitialExpandedChildrenCount;
        
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
            this.mInitialExpandedChildrenCount = parcel.readInt();
        }
        
        SavedState(final Parcelable parcelable, final int mInitialExpandedChildrenCount) {
            super(parcelable);
            this.mInitialExpandedChildrenCount = mInitialExpandedChildrenCount;
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeInt(this.mInitialExpandedChildrenCount);
        }
    }
}
