// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.os.Build$VERSION;

final class ShadowHelper
{
    static boolean supportsDynamicShadow() {
        return Build$VERSION.SDK_INT >= 21;
    }
}
