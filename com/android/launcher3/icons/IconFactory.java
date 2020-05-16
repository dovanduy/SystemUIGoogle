// 
// Decompiled by Procyon v0.5.36
// 

package com.android.launcher3.icons;

import android.content.Context;

public class IconFactory extends BaseIconFactory
{
    private static IconFactory sPool;
    private static int sPoolId;
    private static final Object sPoolSync;
    private final int mPoolId;
    private IconFactory next;
    
    static {
        sPoolSync = new Object();
    }
    
    private IconFactory(final Context context, final int n, final int n2, final int mPoolId) {
        super(context, n, n2);
        this.mPoolId = mPoolId;
    }
    
    public static IconFactory obtain(final Context context) {
        synchronized (IconFactory.sPoolSync) {
            if (IconFactory.sPool != null) {
                final IconFactory sPool = IconFactory.sPool;
                IconFactory.sPool = sPool.next;
                sPool.next = null;
                return sPool;
            }
            final int sPoolId = IconFactory.sPoolId;
            // monitorexit(IconFactory.sPoolSync)
            return new IconFactory(context, context.getResources().getConfiguration().densityDpi, context.getResources().getDimensionPixelSize(R$dimen.default_icon_bitmap_size), sPoolId);
        }
    }
    
    @Override
    public void close() {
        this.recycle();
    }
    
    public void recycle() {
        synchronized (IconFactory.sPoolSync) {
            if (IconFactory.sPoolId != this.mPoolId) {
                return;
            }
            this.clear();
            this.next = IconFactory.sPool;
            IconFactory.sPool = this;
        }
    }
}
