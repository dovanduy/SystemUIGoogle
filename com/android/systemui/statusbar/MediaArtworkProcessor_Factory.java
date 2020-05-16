// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import dagger.internal.Factory;

public final class MediaArtworkProcessor_Factory implements Factory<MediaArtworkProcessor>
{
    private static final MediaArtworkProcessor_Factory INSTANCE;
    
    static {
        INSTANCE = new MediaArtworkProcessor_Factory();
    }
    
    public static MediaArtworkProcessor_Factory create() {
        return MediaArtworkProcessor_Factory.INSTANCE;
    }
    
    public static MediaArtworkProcessor provideInstance() {
        return new MediaArtworkProcessor();
    }
    
    @Override
    public MediaArtworkProcessor get() {
        return provideInstance();
    }
}
