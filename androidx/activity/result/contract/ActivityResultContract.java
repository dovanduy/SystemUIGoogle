// 
// Decompiled by Procyon v0.5.36
// 

package androidx.activity.result.contract;

import android.annotation.SuppressLint;
import android.content.Intent;

public abstract class ActivityResultContract<I, O>
{
    @SuppressLint({ "UnknownNullness" })
    public abstract O parseResult(final int p0, final Intent p1);
}
