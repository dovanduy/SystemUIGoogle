// 
// Decompiled by Procyon v0.5.36
// 

package androidx.activity;

import androidx.lifecycle.LifecycleOwner;

public interface OnBackPressedDispatcherOwner extends LifecycleOwner
{
    OnBackPressedDispatcher getOnBackPressedDispatcher();
}
