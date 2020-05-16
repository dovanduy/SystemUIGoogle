// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.glwallpaper;

import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.view.SurfaceHolder;
import java.util.Collection;
import java.util.Collections;
import android.text.TextUtils;
import android.util.Log;
import android.opengl.GLUtils;
import android.opengl.EGL14;
import java.util.HashSet;
import java.util.Set;
import android.opengl.EGLSurface;
import android.opengl.EGLDisplay;
import android.opengl.EGLContext;
import android.opengl.EGLConfig;

public class EglHelper
{
    private static final String TAG = "EglHelper";
    private EGLConfig mEglConfig;
    private EGLContext mEglContext;
    private EGLDisplay mEglDisplay;
    private boolean mEglReady;
    private EGLSurface mEglSurface;
    private final int[] mEglVersion;
    private final Set<String> mExts;
    
    public EglHelper() {
        this.mEglVersion = new int[2];
        this.mExts = new HashSet<String>();
        this.connectDisplay();
    }
    
    private EGLConfig chooseEglConfig() {
        final int[] array = { 0 };
        final EGLConfig[] array2 = { null };
        if (!EGL14.eglChooseConfig(this.mEglDisplay, this.getConfig(), 0, array2, 0, 1, array, 0)) {
            final String tag = EglHelper.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("eglChooseConfig failed: ");
            sb.append(GLUtils.getEGLErrorString(EGL14.eglGetError()));
            Log.w(tag, sb.toString());
            return null;
        }
        if (array[0] <= 0) {
            final String tag2 = EglHelper.TAG;
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("eglChooseConfig failed, invalid configs count: ");
            sb2.append(array[0]);
            Log.w(tag2, sb2.toString());
            return null;
        }
        return array2[0];
    }
    
    private boolean connectDisplay() {
        this.mExts.clear();
        this.mEglDisplay = EGL14.eglGetDisplay(0);
        if (!this.hasEglDisplay()) {
            final String tag = EglHelper.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("eglGetDisplay failed: ");
            sb.append(GLUtils.getEGLErrorString(EGL14.eglGetError()));
            Log.w(tag, sb.toString());
            return false;
        }
        final String eglQueryString = EGL14.eglQueryString(this.mEglDisplay, 12373);
        if (!TextUtils.isEmpty((CharSequence)eglQueryString)) {
            Collections.addAll(this.mExts, eglQueryString.split(" "));
        }
        return true;
    }
    
    private int[] getConfig() {
        return new int[] { 12324, 8, 12323, 8, 12322, 8, 12321, 0, 12325, 0, 12326, 0, 12352, 4, 12327, 12344, 12344 };
    }
    
    EGLSurface askCreatingEglWindowSurface(final SurfaceHolder surfaceHolder, final int[] array, final int n) {
        return EGL14.eglCreateWindowSurface(this.mEglDisplay, this.mEglConfig, (Object)surfaceHolder, array, n);
    }
    
    boolean checkExtensionCapability(final String s) {
        return this.mExts.contains(s);
    }
    
    public boolean createEglContext() {
        Log.d(EglHelper.TAG, "createEglContext start");
        final int[] array = new int[5];
        array[0] = 12440;
        int n = 2;
        array[1] = 2;
        if (this.checkExtensionCapability("EGL_IMG_context_priority")) {
            array[2] = 12544;
            n = 4;
            array[3] = 12547;
        }
        array[n] = 12344;
        if (!this.hasEglDisplay()) {
            Log.w(EglHelper.TAG, "mEglDisplay is null");
            return false;
        }
        this.mEglContext = EGL14.eglCreateContext(this.mEglDisplay, this.mEglConfig, EGL14.EGL_NO_CONTEXT, array, 0);
        if (!this.hasEglContext()) {
            final String tag = EglHelper.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("eglCreateContext failed: ");
            sb.append(GLUtils.getEGLErrorString(EGL14.eglGetError()));
            Log.w(tag, sb.toString());
            return false;
        }
        Log.d(EglHelper.TAG, "createEglContext done");
        return true;
    }
    
    public boolean createEglSurface(final SurfaceHolder surfaceHolder, final boolean b) {
        Log.d(EglHelper.TAG, "createEglSurface start");
        if (!this.hasEglDisplay() || !surfaceHolder.getSurface().isValid()) {
            final String tag = EglHelper.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("Create EglSurface failed: hasEglDisplay=");
            sb.append(this.hasEglDisplay());
            sb.append(", has valid surface=");
            sb.append(surfaceHolder.getSurface().isValid());
            Log.w(tag, sb.toString());
            return false;
        }
        final int[] array = null;
        final int wcgCapability = this.getWcgCapability();
        int[] array2 = array;
        if (b) {
            array2 = array;
            if (this.checkExtensionCapability("EGL_KHR_gl_colorspace")) {
                array2 = array;
                if (wcgCapability > 0) {
                    array2 = new int[] { 12445, wcgCapability, 12344 };
                }
            }
        }
        this.mEglSurface = this.askCreatingEglWindowSurface(surfaceHolder, array2, 0);
        if (!this.hasEglSurface()) {
            final String tag2 = EglHelper.TAG;
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("createWindowSurface failed: ");
            sb2.append(GLUtils.getEGLErrorString(EGL14.eglGetError()));
            Log.w(tag2, sb2.toString());
            return false;
        }
        final EGLDisplay mEglDisplay = this.mEglDisplay;
        final EGLSurface mEglSurface = this.mEglSurface;
        if (!EGL14.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, this.mEglContext)) {
            final String tag3 = EglHelper.TAG;
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("eglMakeCurrent failed: ");
            sb3.append(GLUtils.getEGLErrorString(EGL14.eglGetError()));
            Log.w(tag3, sb3.toString());
            return false;
        }
        Log.d(EglHelper.TAG, "createEglSurface done");
        return true;
    }
    
    public void destroyEglContext() {
        if (this.hasEglContext()) {
            EGL14.eglDestroyContext(this.mEglDisplay, this.mEglContext);
            this.mEglContext = EGL14.EGL_NO_CONTEXT;
        }
    }
    
    public void destroyEglSurface() {
        if (this.hasEglSurface()) {
            final EGLDisplay mEglDisplay = this.mEglDisplay;
            final EGLSurface egl_NO_SURFACE = EGL14.EGL_NO_SURFACE;
            EGL14.eglMakeCurrent(mEglDisplay, egl_NO_SURFACE, egl_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            EGL14.eglDestroySurface(this.mEglDisplay, this.mEglSurface);
            this.mEglSurface = EGL14.EGL_NO_SURFACE;
        }
    }
    
    public void dump(final String s, final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        final StringBuilder sb = new StringBuilder();
        final int[] mEglVersion = this.mEglVersion;
        int i = 0;
        sb.append(mEglVersion[0]);
        sb.append(".");
        sb.append(this.mEglVersion[1]);
        final String string = sb.toString();
        printWriter.print(s);
        printWriter.print("EGL version=");
        printWriter.print(string);
        printWriter.print(", ");
        printWriter.print("EGL ready=");
        printWriter.print(this.mEglReady);
        printWriter.print(", ");
        printWriter.print("has EglContext=");
        printWriter.print(this.hasEglContext());
        printWriter.print(", ");
        printWriter.print("has EglSurface=");
        printWriter.println(this.hasEglSurface());
        final int[] config = this.getConfig();
        final StringBuilder sb2 = new StringBuilder();
        sb2.append('{');
        while (i < config.length) {
            final int j = config[i];
            sb2.append("0x");
            sb2.append(Integer.toHexString(j));
            sb2.append(",");
            ++i;
        }
        sb2.setCharAt(sb2.length() - 1, '}');
        printWriter.print(s);
        printWriter.print("EglConfig=");
        printWriter.println(sb2.toString());
    }
    
    public void finish() {
        if (this.hasEglSurface()) {
            this.destroyEglSurface();
        }
        if (this.hasEglContext()) {
            this.destroyEglContext();
        }
        if (this.hasEglDisplay()) {
            this.terminateEglDisplay();
        }
        this.mEglReady = false;
    }
    
    int getWcgCapability() {
        if (this.checkExtensionCapability("EGL_EXT_gl_colorspace_display_p3_passthrough")) {
            return 13456;
        }
        return 0;
    }
    
    public boolean hasEglContext() {
        final EGLContext mEglContext = this.mEglContext;
        return mEglContext != null && mEglContext != EGL14.EGL_NO_CONTEXT;
    }
    
    public boolean hasEglDisplay() {
        final EGLDisplay mEglDisplay = this.mEglDisplay;
        return mEglDisplay != null && mEglDisplay != EGL14.EGL_NO_DISPLAY;
    }
    
    public boolean hasEglSurface() {
        final EGLSurface mEglSurface = this.mEglSurface;
        return mEglSurface != null && mEglSurface != EGL14.EGL_NO_SURFACE;
    }
    
    public boolean init(final SurfaceHolder surfaceHolder, final boolean b) {
        if (!this.hasEglDisplay() && !this.connectDisplay()) {
            Log.w(EglHelper.TAG, "Can not connect display, abort!");
            return false;
        }
        final EGLDisplay mEglDisplay = this.mEglDisplay;
        final int[] mEglVersion = this.mEglVersion;
        if (!EGL14.eglInitialize(mEglDisplay, mEglVersion, 0, mEglVersion, 1)) {
            final String tag = EglHelper.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("eglInitialize failed: ");
            sb.append(GLUtils.getEGLErrorString(EGL14.eglGetError()));
            Log.w(tag, sb.toString());
            return false;
        }
        if ((this.mEglConfig = this.chooseEglConfig()) == null) {
            Log.w(EglHelper.TAG, "eglConfig not initialized!");
            return false;
        }
        if (!this.createEglContext()) {
            Log.w(EglHelper.TAG, "Can't create EGLContext!");
            return false;
        }
        if (!this.createEglSurface(surfaceHolder, b)) {
            Log.w(EglHelper.TAG, "Can't create EGLSurface!");
            return false;
        }
        return this.mEglReady = true;
    }
    
    public boolean swapBuffer() {
        final boolean eglSwapBuffers = EGL14.eglSwapBuffers(this.mEglDisplay, this.mEglSurface);
        final int eglGetError = EGL14.eglGetError();
        if (eglGetError != 12288) {
            final String tag = EglHelper.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("eglSwapBuffers failed: ");
            sb.append(GLUtils.getEGLErrorString(eglGetError));
            Log.w(tag, sb.toString());
        }
        return eglSwapBuffers;
    }
    
    void terminateEglDisplay() {
        EGL14.eglTerminate(this.mEglDisplay);
        this.mEglDisplay = EGL14.EGL_NO_DISPLAY;
    }
}
