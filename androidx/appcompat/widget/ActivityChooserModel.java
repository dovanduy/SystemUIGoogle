// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.widget;

import android.content.pm.ResolveInfo;
import android.content.Intent;
import android.database.DataSetObservable;

class ActivityChooserModel extends DataSetObservable
{
    public abstract Intent chooseActivity(final int p0);
    
    public abstract ResolveInfo getActivity(final int p0);
    
    public abstract int getActivityCount();
    
    public abstract int getActivityIndex(final ResolveInfo p0);
    
    public abstract ResolveInfo getDefaultActivity();
    
    public abstract int getHistorySize();
    
    public abstract void setDefaultActivity(final int p0);
}
