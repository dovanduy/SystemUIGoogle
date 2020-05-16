// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.core;

import androidx.core.graphics.drawable.IconCompat;

public interface SliceAction
{
    IconCompat getIcon();
    
    int getImageMode();
    
    int getPriority();
    
    boolean isToggle();
}
