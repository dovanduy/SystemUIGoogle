// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.app;

import android.view.View;
import java.util.Map;
import java.util.List;

public abstract class SharedElementCallback
{
    public abstract void onMapSharedElements(final List<String> p0, final Map<String, View> p1);
    
    public abstract void onSharedElementEnd(final List<String> p0, final List<View> p1, final List<View> p2);
    
    public abstract void onSharedElementStart(final List<String> p0, final List<View> p1, final List<View> p2);
}
