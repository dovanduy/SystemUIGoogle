// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.glwallpaper;

import java.util.concurrent.atomic.AtomicInteger;
import android.util.Size;
import java.util.function.Consumer;
import com.android.systemui.R$raw;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.opengl.GLES20;
import android.util.MathUtils;
import android.graphics.Bitmap;
import android.view.DisplayInfo;
import android.util.Log;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Rect;

public class ImageWallpaperRenderer implements GLWallpaperRenderer, RevealStateListener
{
    private static final String TAG = "ImageWallpaperRenderer";
    private final ImageProcessHelper mImageProcessHelper;
    private final ImageRevealHelper mImageRevealHelper;
    private final ImageGLProgram mProgram;
    private SurfaceProxy mProxy;
    private final Rect mScissor;
    private boolean mScissorMode;
    private final Rect mSurfaceSize;
    private final WallpaperTexture mTexture;
    private final Rect mViewport;
    private final ImageGLWallpaper mWallpaper;
    private float mXOffset;
    private float mYOffset;
    
    public ImageWallpaperRenderer(final Context context, final SurfaceProxy mProxy) {
        this.mSurfaceSize = new Rect();
        this.mViewport = new Rect();
        final WallpaperManager wallpaperManager = (WallpaperManager)context.getSystemService((Class)WallpaperManager.class);
        if (wallpaperManager == null) {
            Log.w(ImageWallpaperRenderer.TAG, "WallpaperManager not available");
        }
        this.mTexture = new WallpaperTexture(wallpaperManager);
        final DisplayInfo displayInfo = new DisplayInfo();
        context.getDisplay().getDisplayInfo(displayInfo);
        if (context.getResources().getConfiguration().orientation == 1) {
            this.mScissor = new Rect(0, 0, displayInfo.logicalWidth, displayInfo.logicalHeight);
        }
        else {
            this.mScissor = new Rect(0, 0, displayInfo.logicalHeight, displayInfo.logicalWidth);
        }
        this.mProxy = mProxy;
        this.mProgram = new ImageGLProgram(context);
        this.mWallpaper = new ImageGLWallpaper(this.mProgram);
        this.mImageProcessHelper = new ImageProcessHelper();
        this.mImageRevealHelper = new ImageRevealHelper((ImageRevealHelper.RevealStateListener)this);
        this.startProcessingImage();
    }
    
    private void scaleViewport(float n) {
        final Rect mScissor = this.mScissor;
        final int left = mScissor.left;
        final int top = mScissor.top;
        final int width = mScissor.width();
        final int height = this.mScissor.height();
        final float lerp = MathUtils.lerp(1.0f, 1.1f, n);
        final float n2 = (1.0f - lerp) / 2.0f;
        final Rect mViewport = this.mViewport;
        final float n3 = (float)left;
        n = (float)width;
        final int n4 = (int)(n3 + n * n2);
        final float n5 = (float)top;
        final float n6 = (float)height;
        mViewport.set(n4, (int)(n5 + n2 * n6), (int)(n * lerp), (int)(n6 * lerp));
        final Rect mViewport2 = this.mViewport;
        GLES20.glViewport(mViewport2.left, mViewport2.top, mViewport2.right, mViewport2.bottom);
    }
    
    @Override
    public void dump(final String s, final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.print(s);
        printWriter.print("mProxy=");
        printWriter.print(this.mProxy);
        printWriter.print(s);
        printWriter.print("mSurfaceSize=");
        printWriter.print(this.mSurfaceSize);
        printWriter.print(s);
        printWriter.print("mScissor=");
        printWriter.print(this.mScissor);
        printWriter.print(s);
        printWriter.print("mViewport=");
        printWriter.print(this.mViewport);
        printWriter.print(s);
        printWriter.print("mScissorMode=");
        printWriter.print(this.mScissorMode);
        printWriter.print(s);
        printWriter.print("mXOffset=");
        printWriter.print(this.mXOffset);
        printWriter.print(s);
        printWriter.print("mYOffset=");
        printWriter.print(this.mYOffset);
        printWriter.print(s);
        printWriter.print("threshold=");
        printWriter.print(this.mImageProcessHelper.getThreshold());
        printWriter.print(s);
        printWriter.print("mReveal=");
        printWriter.print(this.mImageRevealHelper.getReveal());
        printWriter.print(s);
        printWriter.print("mWcgContent=");
        printWriter.print(this.isWcgContent());
        this.mWallpaper.dump(s, fileDescriptor, printWriter, array);
    }
    
    @Override
    public void finish() {
        this.mProxy = null;
    }
    
    @Override
    public boolean isWcgContent() {
        return this.mTexture.isWcgContent();
    }
    
    @Override
    public void onDrawFrame() {
        final float threshold = this.mImageProcessHelper.getThreshold();
        final float reveal = this.mImageRevealHelper.getReveal();
        GLES20.glUniform1f(this.mWallpaper.getHandle("uAod2Opacity"), 1.0f);
        GLES20.glUniform1f(this.mWallpaper.getHandle("uPer85"), threshold);
        GLES20.glUniform1f(this.mWallpaper.getHandle("uReveal"), reveal);
        GLES20.glClear(16384);
        if (this.mScissorMode) {
            this.scaleViewport(reveal);
        }
        else {
            GLES20.glViewport(0, 0, this.mSurfaceSize.width(), this.mSurfaceSize.height());
        }
        this.mWallpaper.useTexture();
        this.mWallpaper.draw();
    }
    
    @Override
    public void onRevealEnd() {
        if (this.mScissorMode) {
            this.mScissorMode = false;
            this.mWallpaper.adjustTextureCoordinates(null, null, 0.0f, 0.0f);
            this.mProxy.requestRender();
        }
        this.mProxy.postRender();
    }
    
    @Override
    public void onRevealStart(final boolean b) {
        if (b) {
            this.mScissorMode = true;
            this.mWallpaper.adjustTextureCoordinates(this.mSurfaceSize, this.mScissor, this.mXOffset, this.mYOffset);
        }
        this.mProxy.preRender();
    }
    
    @Override
    public void onRevealStateChanged() {
        this.mProxy.requestRender();
    }
    
    @Override
    public void onSurfaceChanged(final int n, final int n2) {
        GLES20.glViewport(0, 0, n, n2);
    }
    
    @Override
    public void onSurfaceCreated() {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        this.mProgram.useGLProgram(R$raw.image_wallpaper_vertex_shader, R$raw.image_wallpaper_fragment_shader);
        this.mTexture.use(new _$$Lambda$ImageWallpaperRenderer$tbObMkzj33QDR6llFkRAHA0TWow(this));
    }
    
    @Override
    public Size reportSurfaceSize() {
        this.mTexture.use(null);
        this.mSurfaceSize.set(this.mTexture.getTextureDimensions());
        return new Size(this.mSurfaceSize.width(), this.mSurfaceSize.height());
    }
    
    protected void startProcessingImage() {
        this.mImageProcessHelper.start(this.mTexture);
    }
    
    @Override
    public void updateAmbientMode(final boolean b, final long n) {
        this.mImageRevealHelper.updateAwake(b ^ true, n);
    }
    
    @Override
    public void updateOffsets(final float mxOffset, final float myOffset) {
        this.mXOffset = mxOffset;
        this.mYOffset = myOffset;
        final int n = (int)((this.mSurfaceSize.width() - this.mScissor.width()) * mxOffset);
        final int width = this.mScissor.width();
        final Rect mScissor = this.mScissor;
        mScissor.set(n, mScissor.top, width + n, mScissor.bottom);
    }
    
    static class WallpaperTexture
    {
        private Bitmap mBitmap;
        private final Rect mDimensions;
        private final AtomicInteger mRefCount;
        private final WallpaperManager mWallpaperManager;
        private boolean mWcgContent;
        
        private WallpaperTexture(final WallpaperManager mWallpaperManager) {
            this.mWallpaperManager = mWallpaperManager;
            this.mRefCount = new AtomicInteger();
            this.mDimensions = new Rect();
        }
        
        private String getHash() {
            final Bitmap mBitmap = this.mBitmap;
            String hexString;
            if (mBitmap != null) {
                hexString = Integer.toHexString(mBitmap.hashCode());
            }
            else {
                hexString = "null";
            }
            return hexString;
        }
        
        private Rect getTextureDimensions() {
            return this.mDimensions;
        }
        
        private boolean isWcgContent() {
            return this.mWcgContent;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append(this.getHash());
            sb.append(", ");
            sb.append(this.mRefCount.get());
            sb.append("}");
            return sb.toString();
        }
        
        public void use(final Consumer<Bitmap> consumer) {
            this.mRefCount.incrementAndGet();
            synchronized (this.mRefCount) {
                if (this.mBitmap == null) {
                    this.mBitmap = this.mWallpaperManager.getBitmap(false);
                    this.mWcgContent = this.mWallpaperManager.wallpaperSupportsWcg(1);
                    this.mWallpaperManager.forgetLoadedWallpaper();
                    if (this.mBitmap != null) {
                        this.mDimensions.set(0, 0, this.mBitmap.getWidth(), this.mBitmap.getHeight());
                    }
                    else {
                        Log.w(ImageWallpaperRenderer.TAG, "Can't get bitmap");
                    }
                }
                // monitorexit(this.mRefCount)
                if (consumer != null) {
                    consumer.accept(this.mBitmap);
                }
                synchronized (this.mRefCount) {
                    final int decrementAndGet = this.mRefCount.decrementAndGet();
                    if (decrementAndGet == 0 && this.mBitmap != null) {
                        final String access$300 = ImageWallpaperRenderer.TAG;
                        final StringBuilder sb = new StringBuilder();
                        sb.append("WallpaperTexture: release 0x");
                        sb.append(this.getHash());
                        sb.append(", refCount=");
                        sb.append(decrementAndGet);
                        Log.v(access$300, sb.toString());
                        this.mBitmap.recycle();
                        this.mBitmap = null;
                    }
                }
            }
        }
    }
}
