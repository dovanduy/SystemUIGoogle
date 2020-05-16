// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.glwallpaper;

import android.content.res.Resources;
import android.content.res.Resources$NotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.util.Log;
import android.opengl.GLES20;
import android.content.Context;

class ImageGLProgram
{
    private static final String TAG = "ImageGLProgram";
    private Context mContext;
    private int mProgramHandle;
    
    ImageGLProgram(final Context context) {
        this.mContext = context.getApplicationContext();
    }
    
    private int getProgramHandle(final int n, final int n2) {
        final int glCreateProgram = GLES20.glCreateProgram();
        if (glCreateProgram == 0) {
            Log.d(ImageGLProgram.TAG, "Can not create OpenGL ES program");
            return 0;
        }
        GLES20.glAttachShader(glCreateProgram, n);
        GLES20.glAttachShader(glCreateProgram, n2);
        GLES20.glLinkProgram(glCreateProgram);
        return glCreateProgram;
    }
    
    private int getShaderHandle(final int i, final String s) {
        final int glCreateShader = GLES20.glCreateShader(i);
        if (glCreateShader == 0) {
            final String tag = ImageGLProgram.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("Create shader failed, type=");
            sb.append(i);
            Log.d(tag, sb.toString());
            return 0;
        }
        GLES20.glShaderSource(glCreateShader, s);
        GLES20.glCompileShader(glCreateShader);
        return glCreateShader;
    }
    
    private String getShaderResource(final int n) {
        final Resources resources = this.mContext.getResources();
        final StringBuilder sb = new StringBuilder();
        Throwable t = null;
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resources.openRawResource(n)));
            try {
                while (true) {
                    final String line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    sb.append(line);
                    sb.append("\n");
                }
                bufferedReader.close();
            }
            finally {
                try {
                    bufferedReader.close();
                }
                finally {
                    final Throwable exception;
                    t.addSuppressed(exception);
                }
            }
        }
        catch (IOException | Resources$NotFoundException ex) {
            final Throwable t2;
            Log.d(ImageGLProgram.TAG, "Can not read the shader source", t2);
            t = null;
        }
        String string;
        if (t == null) {
            string = "";
        }
        else {
            string = ((StringBuilder)t).toString();
        }
        return string;
    }
    
    private int loadShaderProgram(final int n, final int n2) {
        return this.getProgramHandle(this.getShaderHandle(35633, this.getShaderResource(n)), this.getShaderHandle(35632, this.getShaderResource(n2)));
    }
    
    int getAttributeHandle(final String s) {
        return GLES20.glGetAttribLocation(this.mProgramHandle, s);
    }
    
    int getUniformHandle(final String s) {
        return GLES20.glGetUniformLocation(this.mProgramHandle, s);
    }
    
    boolean useGLProgram(int loadShaderProgram, final int n) {
        loadShaderProgram = this.loadShaderProgram(loadShaderProgram, n);
        GLES20.glUseProgram(this.mProgramHandle = loadShaderProgram);
        return true;
    }
}
