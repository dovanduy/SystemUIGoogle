// 
// Decompiled by Procyon v0.5.36
// 

package androidx.activity.result;

import android.annotation.SuppressLint;

public interface ActivityResultCallback<O>
{
    void onActivityResult(@SuppressLint({ "UnknownNullness" }) final O p0);
}
