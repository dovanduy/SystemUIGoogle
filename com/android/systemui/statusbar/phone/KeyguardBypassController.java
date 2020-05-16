// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.hardware.biometrics.BiometricSourceType;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.tuner.TunerService;
import android.content.Context;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.Dumpable;

public class KeyguardBypassController implements Dumpable
{
    private boolean bouncerShowing;
    private boolean bypassEnabled;
    private boolean hasFaceFeature;
    private boolean isPulseExpanding;
    private boolean launchingAffordance;
    private final KeyguardStateController mKeyguardStateController;
    private PendingUnlock pendingUnlock;
    private boolean qSExpanded;
    private final StatusBarStateController statusBarStateController;
    public BiometricUnlockController unlockController;
    
    public KeyguardBypassController(final Context context, final TunerService tunerService, final StatusBarStateController statusBarStateController, final NotificationLockscreenUserManager notificationLockscreenUserManager, final KeyguardStateController mKeyguardStateController, final DumpManager dumpManager) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(tunerService, "tunerService");
        Intrinsics.checkParameterIsNotNull(statusBarStateController, "statusBarStateController");
        Intrinsics.checkParameterIsNotNull(notificationLockscreenUserManager, "lockscreenUserManager");
        Intrinsics.checkParameterIsNotNull(mKeyguardStateController, "keyguardStateController");
        Intrinsics.checkParameterIsNotNull(dumpManager, "dumpManager");
        this.mKeyguardStateController = mKeyguardStateController;
        this.statusBarStateController = statusBarStateController;
        if (!(this.hasFaceFeature = context.getPackageManager().hasSystemFeature("android.hardware.biometrics.face"))) {
            return;
        }
        dumpManager.registerDumpable("KeyguardBypassController", this);
        statusBarStateController.addCallback((StatusBarStateController.StateListener)new StatusBarStateController.StateListener() {
            final /* synthetic */ KeyguardBypassController this$0;
            
            @Override
            public void onStateChanged(final int n) {
                if (n != 1) {
                    KeyguardBypassController.access$setPendingUnlock$p(this.this$0, null);
                }
            }
        });
        tunerService.addTunable((TunerService.Tunable)new TunerService.Tunable(tunerService) {
            final /* synthetic */ int $dismissByDefault;
            final /* synthetic */ TunerService $tunerService = context.getResources().getBoolean(17891459);
            final /* synthetic */ KeyguardBypassController this$0;
            
            @Override
            public void onTuningChanged(final String s, final String s2) {
                KeyguardBypassController.access$setBypassEnabled$p(this.this$0, this.$tunerService.getValue(s, this.$dismissByDefault) != 0);
            }
        }, "face_unlock_dismisses_keyguard");
        notificationLockscreenUserManager.addUserChangedListener((NotificationLockscreenUserManager.UserChangedListener)new NotificationLockscreenUserManager.UserChangedListener() {
            final /* synthetic */ KeyguardBypassController this$0;
            
            @Override
            public void onUserChanged(final int n) {
                KeyguardBypassController.access$setPendingUnlock$p(this.this$0, null);
            }
        });
    }
    
    public static final /* synthetic */ void access$setBypassEnabled$p(final KeyguardBypassController keyguardBypassController, final boolean bypassEnabled) {
        keyguardBypassController.bypassEnabled = bypassEnabled;
    }
    
    public static final /* synthetic */ void access$setPendingUnlock$p(final KeyguardBypassController keyguardBypassController, final PendingUnlock pendingUnlock) {
        keyguardBypassController.pendingUnlock = pendingUnlock;
    }
    
    public final boolean canBypass() {
        final boolean bypassEnabled = this.getBypassEnabled();
        boolean b2;
        final boolean b = b2 = false;
        if (bypassEnabled) {
            if (!this.bouncerShowing) {
                if (this.statusBarStateController.getState() != 1) {
                    b2 = b;
                    return b2;
                }
                if (this.launchingAffordance) {
                    b2 = b;
                    return b2;
                }
                b2 = b;
                if (this.isPulseExpanding) {
                    return b2;
                }
                if (this.qSExpanded) {
                    b2 = b;
                    return b2;
                }
            }
            b2 = true;
        }
        return b2;
    }
    
    public final boolean canPlaySubtleWindowAnimations() {
        final boolean bypassEnabled = this.getBypassEnabled();
        boolean b2;
        final boolean b = b2 = false;
        if (bypassEnabled) {
            if (this.statusBarStateController.getState() != 1) {
                b2 = b;
            }
            else {
                b2 = (!this.qSExpanded || b);
            }
        }
        return b2;
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(array, "args");
        printWriter.println("KeyguardBypassController:");
        if (this.pendingUnlock != null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("  mPendingUnlock.pendingUnlockType: ");
            final PendingUnlock pendingUnlock = this.pendingUnlock;
            if (pendingUnlock == null) {
                Intrinsics.throwNpe();
                throw null;
            }
            sb.append(pendingUnlock.getPendingUnlockType());
            printWriter.println(sb.toString());
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("  mPendingUnlock.isStrongBiometric: ");
            final PendingUnlock pendingUnlock2 = this.pendingUnlock;
            if (pendingUnlock2 == null) {
                Intrinsics.throwNpe();
                throw null;
            }
            sb2.append(pendingUnlock2.isStrongBiometric());
            printWriter.println(sb2.toString());
        }
        else {
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("  mPendingUnlock: ");
            sb3.append(this.pendingUnlock);
            printWriter.println(sb3.toString());
        }
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("  bypassEnabled: ");
        sb4.append(this.getBypassEnabled());
        printWriter.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append("  canBypass: ");
        sb5.append(this.canBypass());
        printWriter.println(sb5.toString());
        final StringBuilder sb6 = new StringBuilder();
        sb6.append("  bouncerShowing: ");
        sb6.append(this.bouncerShowing);
        printWriter.println(sb6.toString());
        final StringBuilder sb7 = new StringBuilder();
        sb7.append("  isPulseExpanding: ");
        sb7.append(this.isPulseExpanding);
        printWriter.println(sb7.toString());
        final StringBuilder sb8 = new StringBuilder();
        sb8.append("  launchingAffordance: ");
        sb8.append(this.launchingAffordance);
        printWriter.println(sb8.toString());
        final StringBuilder sb9 = new StringBuilder();
        sb9.append("  qSExpanded: ");
        sb9.append(this.qSExpanded);
        printWriter.println(sb9.toString());
        final StringBuilder sb10 = new StringBuilder();
        sb10.append("  hasFaceFeature: ");
        sb10.append(this.hasFaceFeature);
        printWriter.println(sb10.toString());
    }
    
    public final boolean getBypassEnabled() {
        return this.bypassEnabled && this.mKeyguardStateController.isFaceAuthEnabled();
    }
    
    public final void maybePerformPendingUnlock() {
        final PendingUnlock pendingUnlock = this.pendingUnlock;
        if (pendingUnlock != null) {
            if (pendingUnlock == null) {
                Intrinsics.throwNpe();
                throw null;
            }
            final BiometricSourceType pendingUnlockType = pendingUnlock.getPendingUnlockType();
            final PendingUnlock pendingUnlock2 = this.pendingUnlock;
            if (pendingUnlock2 == null) {
                Intrinsics.throwNpe();
                throw null;
            }
            if (this.onBiometricAuthenticated(pendingUnlockType, pendingUnlock2.isStrongBiometric())) {
                final BiometricUnlockController unlockController = this.unlockController;
                if (unlockController == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("unlockController");
                    throw null;
                }
                final PendingUnlock pendingUnlock3 = this.pendingUnlock;
                if (pendingUnlock3 == null) {
                    Intrinsics.throwNpe();
                    throw null;
                }
                final BiometricSourceType pendingUnlockType2 = pendingUnlock3.getPendingUnlockType();
                final PendingUnlock pendingUnlock4 = this.pendingUnlock;
                if (pendingUnlock4 == null) {
                    Intrinsics.throwNpe();
                    throw null;
                }
                unlockController.startWakeAndUnlock(pendingUnlockType2, pendingUnlock4.isStrongBiometric());
                this.pendingUnlock = null;
            }
        }
    }
    
    public final boolean onBiometricAuthenticated(final BiometricSourceType biometricSourceType, final boolean b) {
        Intrinsics.checkParameterIsNotNull(biometricSourceType, "biometricSourceType");
        if (this.getBypassEnabled()) {
            final boolean canBypass = this.canBypass();
            if (!canBypass && (this.isPulseExpanding || this.qSExpanded)) {
                this.pendingUnlock = new PendingUnlock(biometricSourceType, b);
            }
            return canBypass;
        }
        return true;
    }
    
    public final void onStartedGoingToSleep() {
        this.pendingUnlock = null;
    }
    
    public final void setBouncerShowing(final boolean bouncerShowing) {
        this.bouncerShowing = bouncerShowing;
    }
    
    public final void setLaunchingAffordance(final boolean launchingAffordance) {
        this.launchingAffordance = launchingAffordance;
    }
    
    public final void setPulseExpanding(final boolean isPulseExpanding) {
        this.isPulseExpanding = isPulseExpanding;
    }
    
    public final void setQSExpanded(final boolean qsExpanded) {
        final boolean b = this.qSExpanded != qsExpanded;
        this.qSExpanded = qsExpanded;
        if (b && !qsExpanded) {
            this.maybePerformPendingUnlock();
        }
    }
    
    public final void setUnlockController(final BiometricUnlockController unlockController) {
        Intrinsics.checkParameterIsNotNull(unlockController, "<set-?>");
        this.unlockController = unlockController;
    }
    
    private static final class PendingUnlock
    {
        private final boolean isStrongBiometric;
        private final BiometricSourceType pendingUnlockType;
        
        public PendingUnlock(final BiometricSourceType pendingUnlockType, final boolean isStrongBiometric) {
            Intrinsics.checkParameterIsNotNull(pendingUnlockType, "pendingUnlockType");
            this.pendingUnlockType = pendingUnlockType;
            this.isStrongBiometric = isStrongBiometric;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this != o) {
                if (o instanceof PendingUnlock) {
                    final PendingUnlock pendingUnlock = (PendingUnlock)o;
                    if (Intrinsics.areEqual(this.pendingUnlockType, pendingUnlock.pendingUnlockType) && this.isStrongBiometric == pendingUnlock.isStrongBiometric) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        }
        
        public final BiometricSourceType getPendingUnlockType() {
            return this.pendingUnlockType;
        }
        
        @Override
        public int hashCode() {
            final BiometricSourceType pendingUnlockType = this.pendingUnlockType;
            int hashCode;
            if (pendingUnlockType != null) {
                hashCode = pendingUnlockType.hashCode();
            }
            else {
                hashCode = 0;
            }
            int isStrongBiometric;
            if ((isStrongBiometric = (this.isStrongBiometric ? 1 : 0)) != 0) {
                isStrongBiometric = 1;
            }
            return hashCode * 31 + isStrongBiometric;
        }
        
        public final boolean isStrongBiometric() {
            return this.isStrongBiometric;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("PendingUnlock(pendingUnlockType=");
            sb.append(this.pendingUnlockType);
            sb.append(", isStrongBiometric=");
            sb.append(this.isStrongBiometric);
            sb.append(")");
            return sb.toString();
        }
    }
}
