// 
// Decompiled by Procyon v0.5.36
// 

package androidx.preference;

import java.util.Set;

public abstract class PreferenceDataStore
{
    public abstract boolean getBoolean(final String p0, final boolean p1);
    
    public abstract int getInt(final String p0, final int p1);
    
    public abstract String getString(final String p0, final String p1);
    
    public abstract Set<String> getStringSet(final String p0, final Set<String> p1);
    
    public abstract void putBoolean(final String p0, final boolean p1);
    
    public abstract void putInt(final String p0, final int p1);
    
    public abstract void putString(final String p0, final String p1);
    
    public abstract void putStringSet(final String p0, final Set<String> p1);
}
