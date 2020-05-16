// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import java.io.Writer;
import com.android.internal.util.IndentingPrintWriter;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.util.MathUtils;
import com.android.systemui.Interpolators;
import java.util.function.Consumer;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.dump.DumpManager;
import android.app.WallpaperManager;
import android.view.Choreographer$FrameCallback;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.statusbar.notification.ActivityLaunchAnimator;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.animation.Animator;
import android.view.Choreographer;
import android.view.View;
import com.android.systemui.statusbar.phone.BiometricUnlockController;
import com.android.systemui.Dumpable;
import com.android.systemui.statusbar.phone.PanelExpansionListener;

public final class NotificationShadeDepthController implements PanelExpansionListener, Dumpable
{
    private final BiometricUnlockController biometricUnlockController;
    private View blurRoot;
    private final BlurUtils blurUtils;
    private DepthAnimation brightnessMirrorSpring;
    private final Choreographer choreographer;
    private DepthAnimation globalActionsSpring;
    private boolean ignoreShadeBlurUntilHidden;
    private Animator keyguardAnimator;
    private final NotificationShadeDepthController$keyguardStateCallback.NotificationShadeDepthController$keyguardStateCallback$1 keyguardStateCallback;
    private final KeyguardStateController keyguardStateController;
    private Animator notificationAnimator;
    private ActivityLaunchAnimator.ExpandAnimationParameters notificationLaunchAnimationParams;
    private final NotificationShadeWindowController notificationShadeWindowController;
    public View root;
    private boolean scrimsVisible;
    private float shadeExpansion;
    private DepthAnimation shadeSpring;
    private boolean showingHomeControls;
    private final NotificationShadeDepthController$statusBarStateCallback.NotificationShadeDepthController$statusBarStateCallback$1 statusBarStateCallback;
    private final StatusBarStateController statusBarStateController;
    private final Choreographer$FrameCallback updateBlurCallback;
    private boolean updateScheduled;
    private int wakeAndUnlockBlurRadius;
    private final WallpaperManager wallpaperManager;
    
    public NotificationShadeDepthController(final StatusBarStateController statusBarStateController, final BlurUtils blurUtils, final BiometricUnlockController biometricUnlockController, final KeyguardStateController keyguardStateController, final Choreographer choreographer, final WallpaperManager wallpaperManager, final NotificationShadeWindowController notificationShadeWindowController, final DumpManager dumpManager) {
        Intrinsics.checkParameterIsNotNull(statusBarStateController, "statusBarStateController");
        Intrinsics.checkParameterIsNotNull(blurUtils, "blurUtils");
        Intrinsics.checkParameterIsNotNull(biometricUnlockController, "biometricUnlockController");
        Intrinsics.checkParameterIsNotNull(keyguardStateController, "keyguardStateController");
        Intrinsics.checkParameterIsNotNull(choreographer, "choreographer");
        Intrinsics.checkParameterIsNotNull(wallpaperManager, "wallpaperManager");
        Intrinsics.checkParameterIsNotNull(notificationShadeWindowController, "notificationShadeWindowController");
        Intrinsics.checkParameterIsNotNull(dumpManager, "dumpManager");
        this.statusBarStateController = statusBarStateController;
        this.blurUtils = blurUtils;
        this.biometricUnlockController = biometricUnlockController;
        this.keyguardStateController = keyguardStateController;
        this.choreographer = choreographer;
        this.wallpaperManager = wallpaperManager;
        this.notificationShadeWindowController = notificationShadeWindowController;
        this.shadeSpring = new DepthAnimation();
        this.globalActionsSpring = new DepthAnimation();
        this.brightnessMirrorSpring = new DepthAnimation();
        this.updateBlurCallback = (Choreographer$FrameCallback)new NotificationShadeDepthController$updateBlurCallback.NotificationShadeDepthController$updateBlurCallback$1(this);
        this.keyguardStateCallback = new NotificationShadeDepthController$keyguardStateCallback.NotificationShadeDepthController$keyguardStateCallback$1(this);
        this.statusBarStateCallback = new NotificationShadeDepthController$statusBarStateCallback.NotificationShadeDepthController$statusBarStateCallback$1(this);
        final String name = NotificationShadeDepthController.class.getName();
        Intrinsics.checkExpressionValueIsNotNull(name, "javaClass.name");
        dumpManager.registerDumpable(name, this);
        this.keyguardStateController.addCallback((KeyguardStateController.Callback)this.keyguardStateCallback);
        this.statusBarStateController.addCallback((StatusBarStateController.StateListener)this.statusBarStateCallback);
        this.notificationShadeWindowController.setScrimsVisibilityListener(new Consumer<Object>() {
            final /* synthetic */ NotificationShadeDepthController this$0;
            
            @Override
            public final void accept(final Integer n) {
                final NotificationShadeDepthController this$0 = this.this$0;
                boolean b = false;
                Label_0027: {
                    if (n != null) {
                        if (n == 2) {
                            b = true;
                            break Label_0027;
                        }
                    }
                    b = false;
                }
                this$0.setScrimsVisible(b);
            }
        });
    }
    
    public static final /* synthetic */ BlurUtils access$getBlurUtils$p(final NotificationShadeDepthController notificationShadeDepthController) {
        return notificationShadeDepthController.blurUtils;
    }
    
    private final void scheduleUpdate(final View blurRoot) {
        if (this.updateScheduled) {
            return;
        }
        this.updateScheduled = true;
        this.blurRoot = blurRoot;
        this.choreographer.postFrameCallback(this.updateBlurCallback);
    }
    
    static /* synthetic */ void scheduleUpdate$default(final NotificationShadeDepthController notificationShadeDepthController, View view, final int n, final Object o) {
        if ((n & 0x1) != 0x0) {
            view = null;
        }
        notificationShadeDepthController.scheduleUpdate(view);
    }
    
    private final void setScrimsVisible(final boolean scrimsVisible) {
        if (this.scrimsVisible == scrimsVisible) {
            return;
        }
        this.scrimsVisible = scrimsVisible;
        scheduleUpdate$default(this, null, 1, null);
    }
    
    private final void setWakeAndUnlockBlurRadius(final int wakeAndUnlockBlurRadius) {
        if (this.wakeAndUnlockBlurRadius == wakeAndUnlockBlurRadius) {
            return;
        }
        this.wakeAndUnlockBlurRadius = wakeAndUnlockBlurRadius;
        scheduleUpdate$default(this, null, 1, null);
    }
    
    private final void updateShadeBlur() {
        final int state = this.statusBarStateController.getState();
        int blurRadiusOfRatio;
        if (state != 0 && state != 2) {
            blurRadiusOfRatio = 0;
        }
        else {
            blurRadiusOfRatio = this.blurUtils.blurRadiusOfRatio(Interpolators.SHADE_ANIMATION.getInterpolation(MathUtils.constrain(this.shadeExpansion / 0.15f, 0.0f, 1.0f)) * 0.35f + this.shadeExpansion * 0.65f);
        }
        DepthAnimation.animateTo$default(this.shadeSpring, blurRadiusOfRatio, null, 2, null);
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(array, "args");
        final IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter((Writer)printWriter, "  ");
        indentingPrintWriter.println("StatusBarWindowBlurController:");
        indentingPrintWriter.increaseIndent();
        final StringBuilder sb = new StringBuilder();
        sb.append("shadeRadius: ");
        sb.append(this.shadeSpring.getRadius());
        indentingPrintWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("globalActionsRadius: ");
        sb2.append(this.globalActionsSpring.getRadius());
        indentingPrintWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("brightnessMirrorRadius: ");
        sb3.append(this.brightnessMirrorSpring.getRadius());
        indentingPrintWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("wakeAndUnlockBlur: ");
        sb4.append(this.wakeAndUnlockBlurRadius);
        indentingPrintWriter.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append("notificationLaunchAnimationProgress: ");
        final ActivityLaunchAnimator.ExpandAnimationParameters notificationLaunchAnimationParams = this.notificationLaunchAnimationParams;
        Float value;
        if (notificationLaunchAnimationParams != null) {
            value = notificationLaunchAnimationParams.linearProgress;
        }
        else {
            value = null;
        }
        sb5.append(value);
        indentingPrintWriter.println(sb5.toString());
        final StringBuilder sb6 = new StringBuilder();
        sb6.append("ignoreShadeBlurUntilHidden: ");
        sb6.append(this.ignoreShadeBlurUntilHidden);
        indentingPrintWriter.println(sb6.toString());
    }
    
    public final DepthAnimation getBrightnessMirrorSpring() {
        return this.brightnessMirrorSpring;
    }
    
    public final DepthAnimation getGlobalActionsSpring() {
        return this.globalActionsSpring;
    }
    
    public final ActivityLaunchAnimator.ExpandAnimationParameters getNotificationLaunchAnimationParams() {
        return this.notificationLaunchAnimationParams;
    }
    
    public final View getRoot() {
        final View root = this.root;
        if (root != null) {
            return root;
        }
        Intrinsics.throwUninitializedPropertyAccessException("root");
        throw null;
    }
    
    public final DepthAnimation getShadeSpring() {
        return this.shadeSpring;
    }
    
    public final boolean getShowingHomeControls() {
        return this.showingHomeControls;
    }
    
    @Override
    public void onPanelExpansionChanged(final float shadeExpansion, final boolean b) {
        if (shadeExpansion == this.shadeExpansion) {
            return;
        }
        this.shadeExpansion = shadeExpansion;
        this.updateShadeBlur();
    }
    
    public final void setBrightnessMirrorVisible(final boolean b) {
        final DepthAnimation brightnessMirrorSpring = this.brightnessMirrorSpring;
        int blurRadiusOfRatio;
        if (b) {
            blurRadiusOfRatio = this.blurUtils.blurRadiusOfRatio(1.0f);
        }
        else {
            blurRadiusOfRatio = 0;
        }
        DepthAnimation.animateTo$default(brightnessMirrorSpring, blurRadiusOfRatio, null, 2, null);
    }
    
    public final void setNotificationLaunchAnimationParams(final ActivityLaunchAnimator.ExpandAnimationParameters notificationLaunchAnimationParams) {
        this.notificationLaunchAnimationParams = notificationLaunchAnimationParams;
        if (notificationLaunchAnimationParams != null) {
            scheduleUpdate$default(this, null, 1, null);
            return;
        }
        if (this.shadeSpring.getRadius() == 0) {
            return;
        }
        this.ignoreShadeBlurUntilHidden = true;
        DepthAnimation.animateTo$default(this.shadeSpring, 0, null, 2, null);
        this.shadeSpring.finishIfRunning();
    }
    
    public final void setRoot(final View root) {
        Intrinsics.checkParameterIsNotNull(root, "<set-?>");
        this.root = root;
    }
    
    public final void setShowingHomeControls(final boolean showingHomeControls) {
        this.showingHomeControls = showingHomeControls;
    }
    
    public final void updateGlobalDialogVisibility(final float n, final View view) {
        this.globalActionsSpring.animateTo(this.blurUtils.blurRadiusOfRatio(n), view);
    }
    
    public final class DepthAnimation
    {
        private int pendingRadius;
        private int radius;
        private SpringAnimation springAnimation;
        private View view;
        
        public DepthAnimation() {
            this.pendingRadius = -1;
            (this.springAnimation = new SpringAnimation((K)this, (FloatPropertyCompat<K>)new NotificationShadeDepthController$DepthAnimation$springAnimation.NotificationShadeDepthController$DepthAnimation$springAnimation$1(this, "blurRadius"))).setSpring(new SpringForce(0.0f));
            final SpringForce spring = this.springAnimation.getSpring();
            Intrinsics.checkExpressionValueIsNotNull(spring, "springAnimation.spring");
            spring.setDampingRatio(1.0f);
            final SpringForce spring2 = this.springAnimation.getSpring();
            Intrinsics.checkExpressionValueIsNotNull(spring2, "springAnimation.spring");
            spring2.setStiffness(10000.0f);
            this.springAnimation.addEndListener((DynamicAnimation.OnAnimationEndListener)new DynamicAnimation.OnAnimationEndListener() {
                final /* synthetic */ DepthAnimation this$0;
                
                @Override
                public final void onAnimationEnd(final DynamicAnimation<DynamicAnimation<?>> dynamicAnimation, final boolean b, final float n, final float n2) {
                    DepthAnimation.access$setPendingRadius$p(this.this$0, -1);
                }
            });
        }
        
        public static final /* synthetic */ void access$setPendingRadius$p(final DepthAnimation depthAnimation, final int pendingRadius) {
            depthAnimation.pendingRadius = pendingRadius;
        }
        
        public static /* synthetic */ void animateTo$default(final DepthAnimation depthAnimation, final int n, View view, final int n2, final Object o) {
            if ((n2 & 0x2) != 0x0) {
                view = null;
            }
            depthAnimation.animateTo(n, view);
        }
        
        public final void animateTo(final int pendingRadius, final View view) {
            if (this.pendingRadius == pendingRadius && Intrinsics.areEqual(this.view, view)) {
                return;
            }
            this.view = view;
            this.pendingRadius = pendingRadius;
            this.springAnimation.animateToFinalPosition((float)pendingRadius);
        }
        
        public final void finishIfRunning() {
            if (this.springAnimation.isRunning()) {
                this.springAnimation.skipToEnd();
            }
        }
        
        public final int getRadius() {
            return this.radius;
        }
        
        public final float getRatio() {
            return NotificationShadeDepthController.access$getBlurUtils$p(NotificationShadeDepthController.this).ratioOfBlurRadius(this.radius);
        }
        
        public final void setRadius(final int radius) {
            this.radius = radius;
        }
    }
}
