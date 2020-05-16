// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.glwallpaper;

import android.util.Size;
import java.io.PrintWriter;
import java.io.FileDescriptor;

public interface GLWallpaperRenderer
{
    void dump(final String p0, final FileDescriptor p1, final PrintWriter p2, final String[] p3);
    
    void finish();
    
    boolean isWcgContent();
    
    void onDrawFrame();
    
    void onSurfaceChanged(final int p0, final int p1);
    
    void onSurfaceCreated();
    
    Size reportSurfaceSize();
    
    void updateAmbientMode(final boolean p0, final long p1);
    
    void updateOffsets(final float p0, final float p1);
    
    public interface SurfaceProxy
    {
        void postRender();
        
        void preRender();
        
        void requestRender();
    }
}
