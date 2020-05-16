// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.media;

import android.media.session.MediaController;
import android.media.session.MediaSession$Token;
import android.content.Context;

public class MediaControllerFactory
{
    private final Context mContext;
    
    public MediaControllerFactory(final Context mContext) {
        this.mContext = mContext;
    }
    
    public MediaController create(final MediaSession$Token mediaSession$Token) {
        return new MediaController(this.mContext, mediaSession$Token);
    }
}
