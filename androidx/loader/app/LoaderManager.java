// 
// Decompiled by Procyon v0.5.36
// 

package androidx.loader.app;

import java.io.PrintWriter;
import java.io.FileDescriptor;
import androidx.lifecycle.ViewModelStoreOwner;

public abstract class LoaderManager
{
    public static <T extends LifecycleOwner> LoaderManager getInstance(final T t) {
        return new LoaderManagerImpl((LifecycleOwner)t, ((ViewModelStoreOwner)t).getViewModelStore());
    }
    
    @Deprecated
    public abstract void dump(final String p0, final FileDescriptor p1, final PrintWriter p2, final String[] p3);
    
    public abstract void markForRedelivery();
}
