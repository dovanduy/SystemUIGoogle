// 
// Decompiled by Procyon v0.5.36
// 

package androidx.loader.content;

import java.io.PrintWriter;
import java.io.FileDescriptor;

public class Loader<D>
{
    public abstract void abandon();
    
    public abstract boolean cancelLoad();
    
    public abstract String dataToString(final D p0);
    
    @Deprecated
    public abstract void dump(final String p0, final FileDescriptor p1, final PrintWriter p2, final String[] p3);
    
    public abstract void reset();
    
    public final void startLoading() {
        throw null;
    }
    
    public abstract void stopLoading();
    
    public abstract void unregisterListener(final OnLoadCompleteListener<D> p0);
    
    public interface OnLoadCompleteListener<D>
    {
    }
}
