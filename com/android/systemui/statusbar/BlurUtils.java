// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.app.ActivityManager;
import java.io.Writer;
import com.android.internal.util.IndentingPrintWriter;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.util.MathUtils;
import android.view.SurfaceControl$Transaction;
import android.view.SurfaceControl;
import java.io.Closeable;
import kotlin.io.CloseableKt;
import kotlin.Unit;
import android.view.ViewRootImpl;
import android.os.SystemProperties;
import com.android.systemui.R$dimen;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.dump.DumpManager;
import android.content.res.Resources;
import com.android.systemui.Dumpable;

public class BlurUtils implements Dumpable
{
    private final boolean blurDisabledSysProp;
    private final boolean blurSupportedSysProp;
    private final int maxBlurRadius;
    private final int minBlurRadius;
    private final Resources resources;
    
    public BlurUtils(final Resources resources, final DumpManager dumpManager) {
        Intrinsics.checkParameterIsNotNull(resources, "resources");
        Intrinsics.checkParameterIsNotNull(dumpManager, "dumpManager");
        this.resources = resources;
        this.minBlurRadius = resources.getDimensionPixelSize(R$dimen.min_window_blur_radius);
        this.maxBlurRadius = this.resources.getDimensionPixelSize(R$dimen.max_window_blur_radius);
        this.blurSupportedSysProp = SystemProperties.getBoolean("ro.surface_flinger.supports_background_blur", false);
        this.blurDisabledSysProp = SystemProperties.getBoolean("persist.sys.sf.disable_blurs", false);
        final String name = BlurUtils.class.getName();
        Intrinsics.checkExpressionValueIsNotNull(name, "javaClass.name");
        dumpManager.registerDumpable(name, this);
    }
    
    public final void applyBlur(final ViewRootImpl viewRootImpl, final int n) {
        if (viewRootImpl != null) {
            final SurfaceControl surfaceControl = viewRootImpl.getSurfaceControl();
            Intrinsics.checkExpressionValueIsNotNull(surfaceControl, "viewRootImpl.surfaceControl");
            if (surfaceControl.isValid()) {
                if (this.supportsBlursOnWindows()) {
                    final SurfaceControl$Transaction transaction = this.createTransaction();
                    try {
                        transaction.setBackgroundBlurRadius(viewRootImpl.getSurfaceControl(), n);
                        transaction.apply();
                        final Unit instance = Unit.INSTANCE;
                        CloseableKt.closeFinally((Closeable)transaction, null);
                    }
                    finally {
                        try {}
                        finally {
                            CloseableKt.closeFinally((Closeable)transaction, (Throwable)viewRootImpl);
                        }
                    }
                }
            }
        }
    }
    
    public final int blurRadiusOfRatio(final float n) {
        if (n == 0.0f) {
            return 0;
        }
        return (int)MathUtils.lerp((float)this.minBlurRadius, (float)this.maxBlurRadius, n);
    }
    
    public SurfaceControl$Transaction createTransaction() {
        return new SurfaceControl$Transaction();
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(array, "args");
        final IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter((Writer)printWriter, "  ");
        indentingPrintWriter.println("BlurUtils:");
        indentingPrintWriter.increaseIndent();
        final StringBuilder sb = new StringBuilder();
        sb.append("minBlurRadius: ");
        sb.append(this.minBlurRadius);
        indentingPrintWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("maxBlurRadius: ");
        sb2.append(this.maxBlurRadius);
        indentingPrintWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("blurSupportedSysProp: ");
        sb3.append(this.blurSupportedSysProp);
        indentingPrintWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("blurDisabledSysProp: ");
        sb4.append(this.blurDisabledSysProp);
        indentingPrintWriter.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append("supportsBlursOnWindows: ");
        sb5.append(this.supportsBlursOnWindows());
        indentingPrintWriter.println(sb5.toString());
    }
    
    public final float ratioOfBlurRadius(final int n) {
        if (n == 0) {
            return 0.0f;
        }
        return MathUtils.map((float)this.minBlurRadius, (float)this.maxBlurRadius, 0.0f, 1.0f, (float)n);
    }
    
    public boolean supportsBlursOnWindows() {
        return this.blurSupportedSysProp && !this.blurDisabledSysProp && ActivityManager.isHighEndGfx();
    }
}
