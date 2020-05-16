// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import com.android.systemui.glwallpaper.ImageWallpaperRenderer;
import java.io.Serializable;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.util.Size;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.app.ActivityManager;
import android.os.Trace;
import java.util.function.Supplier;
import android.os.Handler;
import com.android.systemui.glwallpaper.EglHelper;
import android.view.DisplayInfo;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.glwallpaper.GLWallpaperRenderer;
import android.content.Context;
import android.service.wallpaper.WallpaperService$Engine;
import android.os.HandlerThread;
import com.android.systemui.statusbar.phone.DozeParameters;
import android.service.wallpaper.WallpaperService;

public class ImageWallpaper extends WallpaperService
{
    private static final String TAG;
    private final DozeParameters mDozeParameters;
    private HandlerThread mWorker;
    
    static {
        TAG = ImageWallpaper.class.getSimpleName();
    }
    
    public ImageWallpaper(final DozeParameters mDozeParameters) {
        this.mDozeParameters = mDozeParameters;
    }
    
    public void onCreate() {
        super.onCreate();
        (this.mWorker = new HandlerThread(ImageWallpaper.TAG)).start();
    }
    
    public WallpaperService$Engine onCreateEngine() {
        return new GLEngine((Context)this, this.mDozeParameters);
    }
    
    public void onDestroy() {
        super.onDestroy();
        this.mWorker.quitSafely();
        this.mWorker = null;
    }
    
    class GLEngine extends WallpaperService$Engine implements SurfaceProxy, StateListener
    {
        @VisibleForTesting
        static final int MIN_SURFACE_HEIGHT = 64;
        @VisibleForTesting
        static final int MIN_SURFACE_WIDTH = 64;
        private StatusBarStateController mController;
        private final DisplayInfo mDisplayInfo;
        private boolean mDisplayNeedsBlanking;
        private EglHelper mEglHelper;
        private final Runnable mFinishRenderingTask;
        @VisibleForTesting
        boolean mIsHighEndGfx;
        private final Object mMonitor;
        private boolean mNeedRedraw;
        private boolean mNeedTransition;
        private GLWallpaperRenderer mRenderer;
        private boolean mShouldStopTransition;
        private boolean mWaitingForRendering;
        
        GLEngine(final Context context, final DozeParameters dozeParameters) {
            super((WallpaperService)ImageWallpaper.this);
            this.mFinishRenderingTask = new _$$Lambda$ImageWallpaper$GLEngine$4IwqG_0jMNtMT6yCqqj_KAFKSvE(this);
            this.mDisplayInfo = new DisplayInfo();
            this.mMonitor = new Object();
            this.init(dozeParameters);
        }
        
        @VisibleForTesting
        GLEngine(final DozeParameters dozeParameters, final Handler handler) {
            super((WallpaperService)ImageWallpaper.this, (Supplier)_$$Lambda$87Do_TfJA3qVM7QF6F_6BpQlQTA.INSTANCE, handler);
            this.mFinishRenderingTask = new _$$Lambda$ImageWallpaper$GLEngine$4IwqG_0jMNtMT6yCqqj_KAFKSvE(this);
            this.mDisplayInfo = new DisplayInfo();
            this.mMonitor = new Object();
            this.init(dozeParameters);
        }
        
        private void cancelFinishRenderingTask() {
            if (ImageWallpaper.this.mWorker == null) {
                return;
            }
            ImageWallpaper.this.mWorker.getThreadHandler().removeCallbacks(this.mFinishRenderingTask);
        }
        
        private void drawFrame() {
            this.preRender();
            this.requestRender();
            this.postRender();
        }
        
        private void finishRendering() {
            Trace.beginSection("ImageWallpaper#finishRendering");
            final EglHelper mEglHelper = this.mEglHelper;
            if (mEglHelper != null) {
                mEglHelper.destroyEglSurface();
                if (!this.needPreserveEglContext()) {
                    this.mEglHelper.destroyEglContext();
                }
            }
            Trace.endSection();
        }
        
        private void init(final DozeParameters dozeParameters) {
            this.mIsHighEndGfx = ActivityManager.isHighEndGfx();
            this.mDisplayNeedsBlanking = dozeParameters.getDisplayNeedsBlanking();
            this.mNeedTransition = false;
            final StatusBarStateController mController = Dependency.get(StatusBarStateController.class);
            this.mController = mController;
            if (mController != null) {
                mController.addCallback((StatusBarStateController.StateListener)this);
            }
        }
        
        private boolean needPreserveEglContext() {
            final boolean mNeedTransition = this.mNeedTransition;
            boolean b = true;
            if (mNeedTransition) {
                final StatusBarStateController mController = this.mController;
                if (mController != null && mController.getState() == 1) {
                    return b;
                }
            }
            b = false;
            return b;
        }
        
        private boolean needSupportWideColorGamut() {
            return this.mRenderer.isWcgContent();
        }
        
        private void notifyWaitingThread() {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: getfield        com/android/systemui/ImageWallpaper$GLEngine.mMonitor:Ljava/lang/Object;
            //     4: astore_1       
            //     5: aload_1        
            //     6: monitorenter   
            //     7: aload_0        
            //     8: getfield        com/android/systemui/ImageWallpaper$GLEngine.mWaitingForRendering:Z
            //    11: istore_2       
            //    12: iload_2        
            //    13: ifeq            28
            //    16: aload_0        
            //    17: iconst_0       
            //    18: putfield        com/android/systemui/ImageWallpaper$GLEngine.mWaitingForRendering:Z
            //    21: aload_0        
            //    22: getfield        com/android/systemui/ImageWallpaper$GLEngine.mMonitor:Ljava/lang/Object;
            //    25: invokevirtual   java/lang/Object.notify:()V
            //    28: aload_1        
            //    29: monitorexit    
            //    30: return         
            //    31: astore_3       
            //    32: aload_1        
            //    33: monitorexit    
            //    34: aload_3        
            //    35: athrow         
            //    36: astore_3       
            //    37: goto            28
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                                    
            //  -----  -----  -----  -----  ----------------------------------------
            //  7      12     31     36     Any
            //  16     28     36     40     Ljava/lang/IllegalMonitorStateException;
            //  16     28     31     36     Any
            //  28     30     31     36     Any
            //  32     34     31     36     Any
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: Label_0028:
            //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
            //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2596)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:576)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
            //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
            //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
            //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
            // 
            throw new IllegalStateException("An error occurred while decompiling this method.");
        }
        
        private void preRenderInternal() {
            final Rect surfaceFrame = this.getSurfaceHolder().getSurfaceFrame();
            this.cancelFinishRenderingTask();
            boolean b = false;
            Label_0060: {
                if (!this.mEglHelper.hasEglContext()) {
                    this.mEglHelper.destroyEglSurface();
                    if (this.mEglHelper.createEglContext()) {
                        b = true;
                        break Label_0060;
                    }
                    Log.w(ImageWallpaper.TAG, "recreate egl context failed!");
                }
                b = false;
            }
            if (this.mEglHelper.hasEglContext() && !this.mEglHelper.hasEglSurface() && !this.mEglHelper.createEglSurface(this.getSurfaceHolder(), this.needSupportWideColorGamut())) {
                Log.w(ImageWallpaper.TAG, "recreate egl surface failed!");
            }
            if (this.mEglHelper.hasEglContext() && this.mEglHelper.hasEglSurface() && b) {
                this.mRenderer.onSurfaceCreated();
                this.mRenderer.onSurfaceChanged(surfaceFrame.width(), surfaceFrame.height());
            }
        }
        
        private void requestRenderInternal() {
            final Rect surfaceFrame = this.getSurfaceHolder().getSurfaceFrame();
            if (this.mEglHelper.hasEglContext() && this.mEglHelper.hasEglSurface() && surfaceFrame.width() > 0 && surfaceFrame.height() > 0) {
                this.mRenderer.onDrawFrame();
                if (!this.mEglHelper.swapBuffer()) {
                    Log.e(ImageWallpaper.TAG, "drawFrame failed!");
                }
            }
            else {
                final String access$100 = ImageWallpaper.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append("requestRender: not ready, has context=");
                sb.append(this.mEglHelper.hasEglContext());
                sb.append(", has surface=");
                sb.append(this.mEglHelper.hasEglSurface());
                sb.append(", frame=");
                sb.append(surfaceFrame);
                Log.e(access$100, sb.toString());
            }
        }
        
        private void scheduleFinishRendering() {
            if (ImageWallpaper.this.mWorker == null) {
                return;
            }
            this.cancelFinishRenderingTask();
            ImageWallpaper.this.mWorker.getThreadHandler().postDelayed(this.mFinishRenderingTask, 1000L);
        }
        
        private void updateSurfaceSize() {
            final SurfaceHolder surfaceHolder = this.getSurfaceHolder();
            final Size reportSurfaceSize = this.mRenderer.reportSurfaceSize();
            surfaceHolder.setFixedSize(Math.max(64, reportSurfaceSize.getWidth()), Math.max(64, reportSurfaceSize.getHeight()));
        }
        
        private void waitForBackgroundRendering() {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: getfield        com/android/systemui/ImageWallpaper$GLEngine.mMonitor:Ljava/lang/Object;
            //     4: astore_1       
            //     5: aload_1        
            //     6: monitorenter   
            //     7: aload_0        
            //     8: iconst_1       
            //     9: putfield        com/android/systemui/ImageWallpaper$GLEngine.mWaitingForRendering:Z
            //    12: iconst_1       
            //    13: istore_2       
            //    14: aload_0        
            //    15: getfield        com/android/systemui/ImageWallpaper$GLEngine.mWaitingForRendering:Z
            //    18: ifeq            65
            //    21: aload_0        
            //    22: getfield        com/android/systemui/ImageWallpaper$GLEngine.mMonitor:Ljava/lang/Object;
            //    25: ldc2_w          100
            //    28: invokevirtual   java/lang/Object.wait:(J)V
            //    31: aload_0        
            //    32: getfield        com/android/systemui/ImageWallpaper$GLEngine.mWaitingForRendering:Z
            //    35: istore_3       
            //    36: iload_2        
            //    37: bipush          10
            //    39: if_icmpge       48
            //    42: iconst_1       
            //    43: istore          4
            //    45: goto            51
            //    48: iconst_0       
            //    49: istore          4
            //    51: aload_0        
            //    52: iload_3        
            //    53: iload           4
            //    55: iand           
            //    56: putfield        com/android/systemui/ImageWallpaper$GLEngine.mWaitingForRendering:Z
            //    59: iinc            2, 1
            //    62: goto            14
            //    65: aload_0        
            //    66: iconst_0       
            //    67: putfield        com/android/systemui/ImageWallpaper$GLEngine.mWaitingForRendering:Z
            //    70: goto            83
            //    73: astore          5
            //    75: aload_0        
            //    76: iconst_0       
            //    77: putfield        com/android/systemui/ImageWallpaper$GLEngine.mWaitingForRendering:Z
            //    80: aload           5
            //    82: athrow         
            //    83: aload_1        
            //    84: monitorexit    
            //    85: return         
            //    86: astore          5
            //    88: aload_1        
            //    89: monitorexit    
            //    90: aload           5
            //    92: athrow         
            //    93: astore          5
            //    95: goto            65
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                            
            //  -----  -----  -----  -----  --------------------------------
            //  7      12     93     98     Ljava/lang/InterruptedException;
            //  7      12     73     83     Any
            //  14     36     93     98     Ljava/lang/InterruptedException;
            //  14     36     73     83     Any
            //  51     59     93     98     Ljava/lang/InterruptedException;
            //  51     59     73     83     Any
            //  65     70     86     93     Any
            //  75     83     86     93     Any
            //  83     85     86     93     Any
            //  88     90     86     93     Any
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: Label_0065:
            //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
            //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2596)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:576)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
            //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
            //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
            //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
            // 
            throw new IllegalStateException("An error occurred while decompiling this method.");
        }
        
        @VisibleForTesting
        boolean checkIfShouldStopTransition() {
            final int orientation = this.getDisplayContext().getResources().getConfiguration().orientation;
            final Rect surfaceFrame = this.getSurfaceHolder().getSurfaceFrame();
            final Rect rect = new Rect();
            final boolean b = true;
            if (orientation == 1) {
                final DisplayInfo mDisplayInfo = this.mDisplayInfo;
                rect.set(0, 0, mDisplayInfo.logicalWidth, mDisplayInfo.logicalHeight);
            }
            else {
                final DisplayInfo mDisplayInfo2 = this.mDisplayInfo;
                rect.set(0, 0, mDisplayInfo2.logicalHeight, mDisplayInfo2.logicalWidth);
            }
            if (this.mNeedTransition) {
                boolean b2 = b;
                if (surfaceFrame.width() < rect.width()) {
                    return b2;
                }
                if (surfaceFrame.height() < rect.height()) {
                    b2 = b;
                    return b2;
                }
            }
            return false;
        }
        
        protected void dump(final String s, final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
            super.dump(s, fileDescriptor, printWriter, array);
            printWriter.print(s);
            printWriter.print("Engine=");
            printWriter.println(this);
            printWriter.print(s);
            printWriter.print("isHighEndGfx=");
            printWriter.println(this.mIsHighEndGfx);
            printWriter.print(s);
            printWriter.print("displayNeedsBlanking=");
            printWriter.println(this.mDisplayNeedsBlanking);
            printWriter.print(s);
            printWriter.print("displayInfo=");
            printWriter.print(this.mDisplayInfo);
            printWriter.print(s);
            printWriter.print("mNeedTransition=");
            printWriter.println(this.mNeedTransition);
            printWriter.print(s);
            printWriter.print("mShouldStopTransition=");
            printWriter.println(this.mShouldStopTransition);
            printWriter.print(s);
            printWriter.print("StatusBarState=");
            final StatusBarStateController mController = this.mController;
            final String s2 = "null";
            Serializable value;
            if (mController != null) {
                value = mController.getState();
            }
            else {
                value = "null";
            }
            printWriter.println(value);
            printWriter.print(s);
            printWriter.print("valid surface=");
            Serializable value2;
            if (this.getSurfaceHolder() != null && this.getSurfaceHolder().getSurface() != null) {
                value2 = this.getSurfaceHolder().getSurface().isValid();
            }
            else {
                value2 = "null";
            }
            printWriter.println(value2);
            printWriter.print(s);
            printWriter.print("surface frame=");
            Object surfaceFrame = s2;
            if (this.getSurfaceHolder() != null) {
                surfaceFrame = this.getSurfaceHolder().getSurfaceFrame();
            }
            printWriter.println(surfaceFrame);
            this.mEglHelper.dump(s, fileDescriptor, printWriter, array);
            this.mRenderer.dump(s, fileDescriptor, printWriter, array);
        }
        
        EglHelper getEglHelperInstance() {
            return new EglHelper();
        }
        
        ImageWallpaperRenderer getRendererInstance() {
            return new ImageWallpaperRenderer(this.getDisplayContext(), this);
        }
        
        public void onAmbientModeChanged(final boolean b, final long n) {
            if (ImageWallpaper.this.mWorker != null) {
                if (this.mNeedTransition) {
                    long lng;
                    if (this.mShouldStopTransition) {
                        lng = 0L;
                    }
                    else {
                        lng = n;
                    }
                    final String access$100 = ImageWallpaper.TAG;
                    final StringBuilder sb = new StringBuilder();
                    sb.append("onAmbientModeChanged: inAmbient=");
                    sb.append(b);
                    sb.append(", duration=");
                    sb.append(lng);
                    sb.append(", mShouldStopTransition=");
                    sb.append(this.mShouldStopTransition);
                    Log.d(access$100, sb.toString());
                    ImageWallpaper.this.mWorker.getThreadHandler().post((Runnable)new _$$Lambda$ImageWallpaper$GLEngine$w2dgQ1kcC5UhS4OuTNdpiCJsVqQ(this, b, lng));
                    if (b && n == 0L) {
                        this.waitForBackgroundRendering();
                    }
                }
            }
        }
        
        public void onCreate(final SurfaceHolder surfaceHolder) {
            this.mEglHelper = this.getEglHelperInstance();
            this.mRenderer = this.getRendererInstance();
            this.getDisplayContext().getDisplay().getDisplayInfo(this.mDisplayInfo);
            this.setFixedSizeAllowed(true);
            this.setOffsetNotificationsEnabled(this.mNeedTransition);
            this.updateSurfaceSize();
        }
        
        public void onDestroy() {
            final StatusBarStateController mController = this.mController;
            if (mController != null) {
                mController.removeCallback((StatusBarStateController.StateListener)this);
            }
            this.mController = null;
            ImageWallpaper.this.mWorker.getThreadHandler().post((Runnable)new _$$Lambda$ImageWallpaper$GLEngine$Rhxb7oaAcAGNLCxy2rNqC6pp_0w(this));
        }
        
        public void onOffsetsChanged(final float n, final float n2, final float n3, final float n4, final int n5, final int n6) {
            if (ImageWallpaper.this.mWorker == null) {
                return;
            }
            ImageWallpaper.this.mWorker.getThreadHandler().post((Runnable)new _$$Lambda$ImageWallpaper$GLEngine$g3IyjqoMJVi1L9x8yfO51WpEVxQ(this, n, n2));
        }
        
        public void onStatePostChange() {
            if (ImageWallpaper.this.mWorker != null && this.mController.getState() == 0) {
                ImageWallpaper.this.mWorker.getThreadHandler().post((Runnable)new _$$Lambda$ImageWallpaper$GLEngine$8Tw1AsmyFt_Lr4VSDxpiW6fEz7g(this));
            }
        }
        
        public void onSurfaceChanged(final SurfaceHolder surfaceHolder, final int n, final int n2, final int n3) {
            if (ImageWallpaper.this.mWorker == null) {
                return;
            }
            ImageWallpaper.this.mWorker.getThreadHandler().post((Runnable)new _$$Lambda$ImageWallpaper$GLEngine$syj9B_tRzmYbOUFqEOGp6WsQqI0(this, n2, n3));
        }
        
        public void onSurfaceCreated(final SurfaceHolder surfaceHolder) {
            this.mShouldStopTransition = this.checkIfShouldStopTransition();
            if (ImageWallpaper.this.mWorker == null) {
                return;
            }
            ImageWallpaper.this.mWorker.getThreadHandler().post((Runnable)new _$$Lambda$ImageWallpaper$GLEngine$WwPnKXUZbkazdjOcqYKAzWQFvTQ(this, surfaceHolder));
        }
        
        public void onSurfaceRedrawNeeded(final SurfaceHolder surfaceHolder) {
            if (ImageWallpaper.this.mWorker == null) {
                return;
            }
            final String access$100 = ImageWallpaper.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("onSurfaceRedrawNeeded: mNeedRedraw=");
            sb.append(this.mNeedRedraw);
            Log.d(access$100, sb.toString());
            ImageWallpaper.this.mWorker.getThreadHandler().post((Runnable)new _$$Lambda$ImageWallpaper$GLEngine$nUXqEeCVFkWFioUicXPSoLlcN1s(this));
        }
        
        public void onVisibilityChanged(final boolean b) {
            final String access$100 = ImageWallpaper.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("wallpaper visibility changes: ");
            sb.append(b);
            Log.d(access$100, sb.toString());
        }
        
        public void postRender() {
            Trace.beginSection("ImageWallpaper#postRender");
            this.notifyWaitingThread();
            this.scheduleFinishRendering();
            Trace.endSection();
        }
        
        public void preRender() {
            Trace.beginSection("ImageWallpaper#preRender");
            this.preRenderInternal();
            Trace.endSection();
        }
        
        public void requestRender() {
            Trace.beginSection("ImageWallpaper#requestRender");
            this.requestRenderInternal();
            Trace.endSection();
        }
        
        public boolean shouldZoomOutWallpaper() {
            return true;
        }
    }
}
