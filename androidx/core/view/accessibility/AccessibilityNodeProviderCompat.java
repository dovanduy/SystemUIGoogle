// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.view.accessibility;

public class AccessibilityNodeProviderCompat
{
    private final Object mProvider;
    
    public AccessibilityNodeProviderCompat(final Object mProvider) {
        this.mProvider = mProvider;
    }
    
    public Object getProvider() {
        return this.mProvider;
    }
}
