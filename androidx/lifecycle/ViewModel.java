// 
// Decompiled by Procyon v0.5.36
// 

package androidx.lifecycle;

import java.util.Iterator;
import java.io.IOException;
import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

public abstract class ViewModel
{
    private final Map<String, Object> mBagOfTags;
    
    public ViewModel() {
        this.mBagOfTags = new HashMap<String, Object>();
    }
    
    private static void closeWithRuntimeException(final Object o) {
        if (o instanceof Closeable) {
            try {
                ((Closeable)o).close();
            }
            catch (IOException cause) {
                throw new RuntimeException(cause);
            }
        }
    }
    
    final void clear() {
        final Map<String, Object> mBagOfTags = this.mBagOfTags;
        if (mBagOfTags != null) {
            synchronized (mBagOfTags) {
                final Iterator<Object> iterator = this.mBagOfTags.values().iterator();
                while (iterator.hasNext()) {
                    closeWithRuntimeException(iterator.next());
                }
            }
        }
        this.onCleared();
    }
    
     <T> T getTag(final String s) {
        final Map<String, Object> mBagOfTags = this.mBagOfTags;
        if (mBagOfTags == null) {
            return null;
        }
        synchronized (mBagOfTags) {
            return (T)this.mBagOfTags.get(s);
        }
    }
    
    protected void onCleared() {
    }
}
