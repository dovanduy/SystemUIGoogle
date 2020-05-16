// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.glwallpaper;

import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.graphics.Rect;
import android.opengl.GLUtils;
import android.util.Log;
import android.graphics.Bitmap;
import java.nio.Buffer;
import android.opengl.GLES20;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

class ImageGLWallpaper
{
    private static final String TAG = "ImageGLWallpaper";
    private static final float[] TEXTURES;
    private static final float[] VERTICES;
    private int mAttrPosition;
    private int mAttrTextureCoordinates;
    private float[] mCurrentTexCoordinate;
    private final ImageGLProgram mProgram;
    private final FloatBuffer mTextureBuffer;
    private int mTextureId;
    private int mUniAod2Opacity;
    private int mUniPer85;
    private int mUniReveal;
    private int mUniTexture;
    private final FloatBuffer mVertexBuffer;
    
    static {
        VERTICES = new float[] { -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f };
        TEXTURES = new float[] { 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f };
    }
    
    ImageGLWallpaper(final ImageGLProgram mProgram) {
        final float[] textures = ImageGLWallpaper.TEXTURES;
        final float[] vertices = ImageGLWallpaper.VERTICES;
        this.mProgram = mProgram;
        (this.mVertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()).put(vertices);
        this.mVertexBuffer.position(0);
        (this.mTextureBuffer = ByteBuffer.allocateDirect(textures.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()).put(textures);
        this.mTextureBuffer.position(0);
    }
    
    private void setupAttributes() {
        this.mAttrPosition = this.mProgram.getAttributeHandle("aPosition");
        this.mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(this.mAttrPosition, 2, 5126, false, 0, (Buffer)this.mVertexBuffer);
        GLES20.glEnableVertexAttribArray(this.mAttrPosition);
        this.mAttrTextureCoordinates = this.mProgram.getAttributeHandle("aTextureCoordinates");
        this.mTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(this.mAttrTextureCoordinates, 2, 5126, false, 0, (Buffer)this.mTextureBuffer);
        GLES20.glEnableVertexAttribArray(this.mAttrTextureCoordinates);
    }
    
    private void setupTexture(final Bitmap bitmap) {
        final int[] array = { 0 };
        if (bitmap == null) {
            Log.w(ImageGLWallpaper.TAG, "setupTexture: invalid bitmap");
            return;
        }
        GLES20.glGenTextures(1, array, 0);
        if (array[0] == 0) {
            Log.w(ImageGLWallpaper.TAG, "setupTexture: glGenTextures() failed");
            return;
        }
        GLES20.glBindTexture(3553, array[0]);
        GLUtils.texImage2D(3553, 0, bitmap, 0);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        this.mTextureId = array[0];
    }
    
    private void setupUniforms() {
        this.mUniAod2Opacity = this.mProgram.getUniformHandle("uAod2Opacity");
        this.mUniPer85 = this.mProgram.getUniformHandle("uPer85");
        this.mUniReveal = this.mProgram.getUniformHandle("uReveal");
        this.mUniTexture = this.mProgram.getUniformHandle("uTexture");
    }
    
    void adjustTextureCoordinates(final Rect rect, final Rect rect2, float n, float n2) {
        this.mCurrentTexCoordinate = ImageGLWallpaper.TEXTURES.clone();
        if (rect != null && rect2 != null) {
            final int width = rect.width();
            final int height = rect.height();
            final int width2 = rect2.width();
            final int height2 = rect2.height();
            if (width > width2) {
                final float n3 = (float)Math.round((width - width2) * n);
                n = (float)width;
                final float n4 = n3 / n;
                final float n5 = n = width2 / n;
                if (height < height2) {
                    n = n5 * (height / (float)height2);
                }
                float n6 = n4;
                if (n4 + n > 1.0f) {
                    n6 = 1.0f - n;
                }
                int n7 = 0;
                while (true) {
                    final float[] mCurrentTexCoordinate = this.mCurrentTexCoordinate;
                    if (n7 >= mCurrentTexCoordinate.length) {
                        break;
                    }
                    if (n7 != 2 && n7 != 4 && n7 != 6) {
                        mCurrentTexCoordinate[n7] = n6;
                    }
                    else {
                        this.mCurrentTexCoordinate[n7] = Math.min(1.0f, n6 + n);
                    }
                    n7 += 2;
                }
            }
            if (height > height2) {
                n2 = (float)Math.round((height - height2) * n2);
                n = (float)height;
                final float n8 = n2 / n;
                n2 = (n = height2 / n);
                if (width < width2) {
                    n = n2 * (width / (float)width2);
                }
                n2 = n8;
                if (n8 + n > 1.0f) {
                    n2 = 1.0f - n;
                }
                int n9 = 1;
                while (true) {
                    final float[] mCurrentTexCoordinate2 = this.mCurrentTexCoordinate;
                    if (n9 >= mCurrentTexCoordinate2.length) {
                        break;
                    }
                    if (n9 != 1 && n9 != 3 && n9 != 11) {
                        mCurrentTexCoordinate2[n9] = n2;
                    }
                    else {
                        this.mCurrentTexCoordinate[n9] = Math.min(1.0f, n2 + n);
                    }
                    n9 += 2;
                }
            }
            this.mTextureBuffer.put(this.mCurrentTexCoordinate);
            this.mTextureBuffer.position(0);
            return;
        }
        this.mTextureBuffer.put(this.mCurrentTexCoordinate);
        this.mTextureBuffer.position(0);
    }
    
    void draw() {
        GLES20.glDrawArrays(4, 0, ImageGLWallpaper.VERTICES.length / 2);
    }
    
    public void dump(final String s, final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        final StringBuilder sb = new StringBuilder();
        sb.append('{');
        if (this.mCurrentTexCoordinate != null) {
            int n = 0;
            while (true) {
                final float[] mCurrentTexCoordinate = this.mCurrentTexCoordinate;
                if (n >= mCurrentTexCoordinate.length) {
                    break;
                }
                sb.append(mCurrentTexCoordinate[n]);
                sb.append(',');
                if (n == this.mCurrentTexCoordinate.length - 1) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                ++n;
            }
        }
        sb.append('}');
        printWriter.print(s);
        printWriter.print("mTexCoordinates=");
        printWriter.println(sb.toString());
    }
    
    int getHandle(final String s) {
        int n = 0;
        Label_0153: {
            switch (s.hashCode()) {
                case 1583025322: {
                    if (s.equals("aPosition")) {
                        n = 0;
                        break Label_0153;
                    }
                    break;
                }
                case 17245217: {
                    if (s.equals("aTextureCoordinates")) {
                        n = 1;
                        break Label_0153;
                    }
                    break;
                }
                case -868354715: {
                    if (s.equals("uPer85")) {
                        n = 3;
                        break Label_0153;
                    }
                    break;
                }
                case -1091770206: {
                    if (s.equals("uReveal")) {
                        n = 4;
                        break Label_0153;
                    }
                    break;
                }
                case -1971276870: {
                    if (s.equals("uAod2Opacity")) {
                        n = 2;
                        break Label_0153;
                    }
                    break;
                }
                case -2002784538: {
                    if (s.equals("uTexture")) {
                        n = 5;
                        break Label_0153;
                    }
                    break;
                }
            }
            n = -1;
        }
        if (n == 0) {
            return this.mAttrPosition;
        }
        if (n == 1) {
            return this.mAttrTextureCoordinates;
        }
        if (n == 2) {
            return this.mUniAod2Opacity;
        }
        if (n == 3) {
            return this.mUniPer85;
        }
        if (n == 4) {
            return this.mUniReveal;
        }
        if (n != 5) {
            return -1;
        }
        return this.mUniTexture;
    }
    
    void setup(final Bitmap bitmap) {
        this.setupAttributes();
        this.setupUniforms();
        this.setupTexture(bitmap);
    }
    
    void useTexture() {
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, this.mTextureId);
        GLES20.glUniform1i(this.mUniTexture, 0);
    }
}
