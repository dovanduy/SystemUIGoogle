// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.content.res.Resources;
import android.content.Context;
import kotlin.TypeCastException;
import android.util.PathParser;
import kotlin.text.StringsKt;
import java.util.Iterator;
import kotlin.math.MathKt;
import android.graphics.RectF;
import java.util.ArrayList;
import kotlin.jvm.internal.Intrinsics;
import java.util.List;
import java.util.concurrent.Executor;
import android.graphics.Path;
import android.graphics.Rect;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraManager$AvailabilityCallback;

public final class CameraAvailabilityListener
{
    public static final Factory Factory;
    private final CameraManager$AvailabilityCallback availabilityCallback;
    private final CameraManager cameraManager;
    private Rect cutoutBounds;
    private final Path cutoutProtectionPath;
    private final Executor executor;
    private final List<CameraTransitionCallback> listeners;
    private final String targetCameraId;
    
    static {
        Factory = new Factory(null);
    }
    
    public CameraAvailabilityListener(final CameraManager cameraManager, final Path cutoutProtectionPath, final String targetCameraId, final Executor executor) {
        Intrinsics.checkParameterIsNotNull(cameraManager, "cameraManager");
        Intrinsics.checkParameterIsNotNull(cutoutProtectionPath, "cutoutProtectionPath");
        Intrinsics.checkParameterIsNotNull(targetCameraId, "targetCameraId");
        Intrinsics.checkParameterIsNotNull(executor, "executor");
        this.cameraManager = cameraManager;
        this.cutoutProtectionPath = cutoutProtectionPath;
        this.targetCameraId = targetCameraId;
        this.executor = executor;
        this.cutoutBounds = new Rect();
        this.listeners = new ArrayList<CameraTransitionCallback>();
        this.availabilityCallback = (CameraManager$AvailabilityCallback)new CameraAvailabilityListener$availabilityCallback.CameraAvailabilityListener$availabilityCallback$1(this);
        final RectF rectF = new RectF();
        this.cutoutProtectionPath.computeBounds(rectF, false);
        this.cutoutBounds.set(MathKt.roundToInt(rectF.left), MathKt.roundToInt(rectF.top), MathKt.roundToInt(rectF.right), MathKt.roundToInt(rectF.bottom));
    }
    
    private final void notifyCameraActive() {
        final Iterator<CameraTransitionCallback> iterator = this.listeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onApplyCameraProtection(this.cutoutProtectionPath, this.cutoutBounds);
        }
    }
    
    private final void notifyCameraInactive() {
        final Iterator<CameraTransitionCallback> iterator = this.listeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onHideCameraProtection();
        }
    }
    
    private final void registerCameraListener() {
        this.cameraManager.registerAvailabilityCallback(this.executor, this.availabilityCallback);
    }
    
    public final void addTransitionCallback(final CameraTransitionCallback cameraTransitionCallback) {
        Intrinsics.checkParameterIsNotNull(cameraTransitionCallback, "callback");
        this.listeners.add(cameraTransitionCallback);
    }
    
    public final void startListening() {
        this.registerCameraListener();
    }
    
    public interface CameraTransitionCallback
    {
        void onApplyCameraProtection(final Path p0, final Rect p1);
        
        void onHideCameraProtection();
    }
    
    public static final class Factory
    {
        private Factory() {
        }
        
        private final Path pathFromString(String string) {
            if (string != null) {
                string = StringsKt.trim(string).toString();
                try {
                    final Path pathFromPathData = PathParser.createPathFromPathData(string);
                    Intrinsics.checkExpressionValueIsNotNull(pathFromPathData, "PathParser.createPathFromPathData(spec)");
                    return pathFromPathData;
                }
                finally {
                    final Throwable cause;
                    throw new IllegalArgumentException("Invalid protection path", cause);
                }
            }
            throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
        }
        
        public final CameraAvailabilityListener build(final Context context, final Executor executor) {
            Intrinsics.checkParameterIsNotNull(context, "context");
            Intrinsics.checkParameterIsNotNull(executor, "executor");
            final Object systemService = context.getSystemService("camera");
            if (systemService != null) {
                final CameraManager cameraManager = (CameraManager)systemService;
                final Resources resources = context.getResources();
                final String string = resources.getString(R$string.config_frontBuiltInDisplayCutoutProtection);
                final String string2 = resources.getString(R$string.config_protectedCameraId);
                Intrinsics.checkExpressionValueIsNotNull(string, "pathString");
                final Path pathFromString = this.pathFromString(string);
                Intrinsics.checkExpressionValueIsNotNull(string2, "cameraId");
                return new CameraAvailabilityListener(cameraManager, pathFromString, string2, executor);
            }
            throw new TypeCastException("null cannot be cast to non-null type android.hardware.camera2.CameraManager");
        }
    }
}
