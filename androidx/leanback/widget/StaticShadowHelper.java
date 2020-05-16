// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.os.Build$VERSION;

final class StaticShadowHelper
{
    static boolean supportsShadow() {
        return Build$VERSION.SDK_INT >= 21;
    }
}
