// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import java.util.Map;
import android.view.View;
import java.util.Iterator;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import androidx.collection.LruCache;

class ViewsStateBundle
{
    private LruCache<String, SparseArray<Parcelable>> mChildStates;
    private int mSavePolicy;
    
    public ViewsStateBundle() {
        this.mSavePolicy = 0;
    }
    
    static String getSaveStatesKey(final int i) {
        return Integer.toString(i);
    }
    
    public void clear() {
        final LruCache<String, SparseArray<Parcelable>> mChildStates = this.mChildStates;
        if (mChildStates != null) {
            mChildStates.evictAll();
        }
    }
    
    public final void loadFromBundle(final Bundle bundle) {
        final LruCache<String, SparseArray<Parcelable>> mChildStates = this.mChildStates;
        if (mChildStates != null && bundle != null) {
            mChildStates.evictAll();
            for (final String s : bundle.keySet()) {
                this.mChildStates.put(s, (SparseArray<Parcelable>)bundle.getSparseParcelableArray(s));
            }
        }
    }
    
    public final void loadView(final View view, final int n) {
        if (this.mChildStates != null) {
            final SparseArray<Parcelable> sparseArray = this.mChildStates.remove(getSaveStatesKey(n));
            if (sparseArray != null) {
                view.restoreHierarchyState((SparseArray)sparseArray);
            }
        }
    }
    
    public void remove(final int n) {
        final LruCache<String, SparseArray<Parcelable>> mChildStates = this.mChildStates;
        if (mChildStates != null && mChildStates.size()) {
            this.mChildStates.remove(getSaveStatesKey(n));
        }
    }
    
    public final Bundle saveAsBundle() {
        final LruCache<String, SparseArray<Parcelable>> mChildStates = this.mChildStates;
        if (mChildStates != null && mChildStates.size()) {
            final Map<String, SparseArray<Parcelable>> snapshot = this.mChildStates.snapshot();
            final Bundle bundle = new Bundle();
            for (final Map.Entry<String, SparseArray<Parcelable>> entry : snapshot.entrySet()) {
                bundle.putSparseParcelableArray((String)entry.getKey(), (SparseArray)entry.getValue());
            }
            return bundle;
        }
        return null;
    }
    
    public final void saveOffscreenView(final View view, final int n) {
        final int mSavePolicy = this.mSavePolicy;
        if (mSavePolicy != 1) {
            if (mSavePolicy == 2 || mSavePolicy == 3) {
                this.saveViewUnchecked(view, n);
            }
        }
        else {
            this.remove(n);
        }
    }
    
    public final Bundle saveOnScreenView(final Bundle bundle, final View view, final int n) {
        Bundle bundle2 = bundle;
        if (this.mSavePolicy != 0) {
            final String saveStatesKey = getSaveStatesKey(n);
            final SparseArray sparseArray = new SparseArray();
            view.saveHierarchyState(sparseArray);
            Bundle bundle3;
            if ((bundle3 = bundle) == null) {
                bundle3 = new Bundle();
            }
            bundle3.putSparseParcelableArray(saveStatesKey, sparseArray);
            bundle2 = bundle3;
        }
        return bundle2;
    }
    
    protected final void saveViewUnchecked(final View view, final int n) {
        if (this.mChildStates != null) {
            final String saveStatesKey = getSaveStatesKey(n);
            final SparseArray sparseArray = new SparseArray();
            view.saveHierarchyState(sparseArray);
            this.mChildStates.put(saveStatesKey, (SparseArray<Parcelable>)sparseArray);
        }
    }
}
