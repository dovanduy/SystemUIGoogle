// 
// Decompiled by Procyon v0.5.36
// 

package androidx.viewpager2.adapter;

import android.os.Parcelable;

public interface StatefulAdapter
{
    void restoreState(final Parcelable p0);
    
    Parcelable saveState();
}
