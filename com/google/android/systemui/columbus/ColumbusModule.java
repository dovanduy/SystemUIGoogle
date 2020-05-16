// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import kotlin.collections.MapsKt;
import kotlin.Pair;
import android.provider.Settings$Secure;
import kotlin.text.StringsKt;
import java.util.Locale;
import android.os.Build;
import kotlin.TypeCastException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.SpreadBuilder;
import kotlin.jvm.internal.Intrinsics;
import kotlin.collections.SetsKt;
import java.util.Map;
import com.google.android.systemui.columbus.actions.LaunchOverview;
import com.google.android.systemui.columbus.actions.TakeScreenshot;
import com.google.android.systemui.columbus.actions.ManageMedia;
import com.google.android.systemui.columbus.actions.LaunchCamera;
import com.google.android.systemui.columbus.actions.LaunchOpa;
import com.google.android.systemui.columbus.sensors.GestureSensor;
import com.google.android.systemui.columbus.sensors.GestureSensorImpl;
import com.google.android.systemui.columbus.sensors.CHREGestureSensor;
import dagger.Lazy;
import android.content.Context;
import com.google.android.systemui.columbus.sensors.config.Adjustment;
import com.google.android.systemui.columbus.actions.SettingsAction;
import com.google.android.systemui.columbus.actions.SilenceCall;
import com.google.android.systemui.columbus.actions.SnoozeAlarm;
import com.google.android.systemui.columbus.actions.DismissTimer;
import com.android.internal.logging.MetricsLogger;
import com.google.android.systemui.columbus.gates.Gate;
import com.google.android.systemui.columbus.gates.PowerSaveState;
import com.google.android.systemui.columbus.gates.CameraVisibility;
import com.google.android.systemui.columbus.gates.KeyguardDeferredSetup;
import com.google.android.systemui.columbus.gates.VrMode;
import com.google.android.systemui.columbus.gates.TelephonyActivity;
import com.google.android.systemui.columbus.gates.SystemKeyPress;
import com.google.android.systemui.columbus.gates.NavigationBarVisibility;
import com.google.android.systemui.columbus.gates.SetupWizard;
import com.google.android.systemui.columbus.gates.KeyguardProximity;
import com.google.android.systemui.columbus.gates.UsbState;
import com.google.android.systemui.columbus.gates.ChargingState;
import com.google.android.systemui.columbus.gates.WakeMode;
import com.google.android.systemui.columbus.gates.FlagEnabled;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import com.google.android.systemui.columbus.feedback.UserActivity;
import com.google.android.systemui.columbus.feedback.NavUndimEffect;
import com.google.android.systemui.columbus.feedback.HapticClick;
import com.google.android.systemui.columbus.actions.UserSelectedAction;
import com.google.android.systemui.columbus.actions.SetupWizardAction;
import com.google.android.systemui.columbus.actions.UnpinNotifications;
import com.google.android.systemui.columbus.actions.Action;
import java.util.List;
import java.util.Set;

public abstract class ColumbusModule
{
    public static final Companion Companion;
    
    static {
        Companion = new Companion(null);
    }
    
    public static final Set<Integer> provideBlockingSystemKeys() {
        return ColumbusModule.Companion.provideBlockingSystemKeys();
    }
    
    public static final List<Action> provideColumbusActions(final List<Action> list, final UnpinNotifications unpinNotifications, final SetupWizardAction setupWizardAction, final UserSelectedAction userSelectedAction) {
        return ColumbusModule.Companion.provideColumbusActions(list, unpinNotifications, setupWizardAction, userSelectedAction);
    }
    
    public static final Set<FeedbackEffect> provideColumbusEffects(final HapticClick hapticClick, final NavUndimEffect navUndimEffect, final UserActivity userActivity) {
        return ColumbusModule.Companion.provideColumbusEffects(hapticClick, navUndimEffect, userActivity);
    }
    
    public static final Set<Gate> provideColumbusGates(final FlagEnabled flagEnabled, final WakeMode wakeMode, final ChargingState chargingState, final UsbState usbState, final KeyguardProximity keyguardProximity, final SetupWizard setupWizard, final NavigationBarVisibility navigationBarVisibility, final SystemKeyPress systemKeyPress, final TelephonyActivity telephonyActivity, final VrMode vrMode, final KeyguardDeferredSetup keyguardDeferredSetup, final CameraVisibility cameraVisibility, final PowerSaveState powerSaveState) {
        return ColumbusModule.Companion.provideColumbusGates(flagEnabled, wakeMode, chargingState, usbState, keyguardProximity, setupWizard, navigationBarVisibility, systemKeyPress, telephonyActivity, vrMode, keyguardDeferredSetup, cameraVisibility, powerSaveState);
    }
    
    public static final MetricsLogger provideColumbusLogger() {
        return ColumbusModule.Companion.provideColumbusLogger();
    }
    
    public static final boolean provideDebugBuildType() {
        return ColumbusModule.Companion.provideDebugBuildType();
    }
    
    public static final List<Action> provideFullscreenActions(final DismissTimer dismissTimer, final SnoozeAlarm snoozeAlarm, final SilenceCall silenceCall, final SettingsAction settingsAction) {
        return ColumbusModule.Companion.provideFullscreenActions(dismissTimer, snoozeAlarm, silenceCall, settingsAction);
    }
    
    public static final Set<Adjustment> provideGestureAdjustments() {
        return ColumbusModule.Companion.provideGestureAdjustments();
    }
    
    public static final GestureSensor provideGestureSensor(final Context context, final Lazy<CHREGestureSensor> lazy, final Lazy<GestureSensorImpl> lazy2) {
        return ColumbusModule.Companion.provideGestureSensor(context, lazy, lazy2);
    }
    
    public static final long provideTransientGateDuration() {
        return ColumbusModule.Companion.provideTransientGateDuration();
    }
    
    public static final Map<String, Action> provideUserSelectedActions(final LaunchOpa launchOpa, final LaunchCamera launchCamera, final ManageMedia manageMedia, final TakeScreenshot takeScreenshot, final LaunchOverview launchOverview) {
        return ColumbusModule.Companion.provideUserSelectedActions(launchOpa, launchCamera, manageMedia, takeScreenshot, launchOverview);
    }
    
    public static final class Companion
    {
        private Companion() {
        }
        
        public final Set<Integer> provideBlockingSystemKeys() {
            return SetsKt.setOf(new Integer[] { 24, 25, 26 });
        }
        
        public final List<Action> provideColumbusActions(final List<Action> list, final UnpinNotifications unpinNotifications, final SetupWizardAction setupWizardAction, final UserSelectedAction userSelectedAction) {
            Intrinsics.checkParameterIsNotNull(list, "fullscreenActions");
            Intrinsics.checkParameterIsNotNull(unpinNotifications, "unpinNotifications");
            Intrinsics.checkParameterIsNotNull(setupWizardAction, "setupWizardAction");
            Intrinsics.checkParameterIsNotNull(userSelectedAction, "userSelectedAction");
            final SpreadBuilder spreadBuilder = new SpreadBuilder(4);
            final Action[] array = (Action[])list.toArray((Object[])new Action[0]);
            if (array != null) {
                spreadBuilder.addSpread(array);
                spreadBuilder.add(unpinNotifications);
                spreadBuilder.add(setupWizardAction);
                spreadBuilder.add(userSelectedAction);
                return CollectionsKt.listOf((Action[])spreadBuilder.toArray(new Action[spreadBuilder.size()]));
            }
            throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<T>");
        }
        
        public final Set<FeedbackEffect> provideColumbusEffects(final HapticClick hapticClick, final NavUndimEffect navUndimEffect, final UserActivity userActivity) {
            Intrinsics.checkParameterIsNotNull(hapticClick, "hapticClick");
            Intrinsics.checkParameterIsNotNull(navUndimEffect, "navUndimEffect");
            Intrinsics.checkParameterIsNotNull(userActivity, "userActivity");
            return SetsKt.setOf(new FeedbackEffect[] { hapticClick, navUndimEffect, userActivity });
        }
        
        public final Set<Gate> provideColumbusGates(final FlagEnabled flagEnabled, final WakeMode wakeMode, final ChargingState chargingState, final UsbState usbState, final KeyguardProximity keyguardProximity, final SetupWizard setupWizard, final NavigationBarVisibility navigationBarVisibility, final SystemKeyPress systemKeyPress, final TelephonyActivity telephonyActivity, final VrMode vrMode, final KeyguardDeferredSetup keyguardDeferredSetup, final CameraVisibility cameraVisibility, final PowerSaveState powerSaveState) {
            Intrinsics.checkParameterIsNotNull(flagEnabled, "flagEnabled");
            Intrinsics.checkParameterIsNotNull(wakeMode, "wakeMode");
            Intrinsics.checkParameterIsNotNull(chargingState, "chargingState");
            Intrinsics.checkParameterIsNotNull(usbState, "usbState");
            Intrinsics.checkParameterIsNotNull(keyguardProximity, "keyguardProximity");
            Intrinsics.checkParameterIsNotNull(setupWizard, "setupWizard");
            Intrinsics.checkParameterIsNotNull(navigationBarVisibility, "navigationBarVisibility");
            Intrinsics.checkParameterIsNotNull(systemKeyPress, "systemKeyPress");
            Intrinsics.checkParameterIsNotNull(telephonyActivity, "telephonyActivity");
            Intrinsics.checkParameterIsNotNull(vrMode, "vrMode");
            Intrinsics.checkParameterIsNotNull(keyguardDeferredSetup, "keyguardDeferredSetup");
            Intrinsics.checkParameterIsNotNull(cameraVisibility, "cameraVisibility");
            Intrinsics.checkParameterIsNotNull(powerSaveState, "powerSaveState");
            return SetsKt.setOf(new Gate[] { flagEnabled, wakeMode, chargingState, usbState, keyguardProximity, setupWizard, navigationBarVisibility, systemKeyPress, telephonyActivity, vrMode, keyguardDeferredSetup, cameraVisibility, powerSaveState });
        }
        
        public final MetricsLogger provideColumbusLogger() {
            return new MetricsLogger();
        }
        
        public final boolean provideDebugBuildType() {
            final String type = Build.TYPE;
            Intrinsics.checkExpressionValueIsNotNull(type, "Build.TYPE");
            final Locale root = Locale.ROOT;
            Intrinsics.checkExpressionValueIsNotNull(root, "Locale.ROOT");
            if (type != null) {
                final String lowerCase = type.toLowerCase(root);
                Intrinsics.checkExpressionValueIsNotNull(lowerCase, "(this as java.lang.String).toLowerCase(locale)");
                boolean b = false;
                if (!StringsKt.contains$default(lowerCase, "debug", false, 2, null)) {
                    final String type2 = Build.TYPE;
                    Intrinsics.checkExpressionValueIsNotNull(type2, "Build.TYPE");
                    final Locale root2 = Locale.ROOT;
                    Intrinsics.checkExpressionValueIsNotNull(root2, "Locale.ROOT");
                    if (type2 == null) {
                        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
                    }
                    final String lowerCase2 = type2.toLowerCase(root2);
                    Intrinsics.checkExpressionValueIsNotNull(lowerCase2, "(this as java.lang.String).toLowerCase(locale)");
                    if (!Intrinsics.areEqual(lowerCase2, "eng")) {
                        return b;
                    }
                }
                b = true;
                return b;
            }
            throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
        }
        
        public final List<Action> provideFullscreenActions(final DismissTimer dismissTimer, final SnoozeAlarm snoozeAlarm, final SilenceCall silenceCall, final SettingsAction settingsAction) {
            Intrinsics.checkParameterIsNotNull(dismissTimer, "dismissTimer");
            Intrinsics.checkParameterIsNotNull(snoozeAlarm, "snoozeAlarm");
            Intrinsics.checkParameterIsNotNull(silenceCall, "silenceCall");
            Intrinsics.checkParameterIsNotNull(settingsAction, "settingsAction");
            return CollectionsKt.listOf(new Action[] { dismissTimer, snoozeAlarm, silenceCall, settingsAction });
        }
        
        public final Set<Adjustment> provideGestureAdjustments() {
            return SetsKt.emptySet();
        }
        
        public final GestureSensor provideGestureSensor(final Context context, final Lazy<CHREGestureSensor> lazy, final Lazy<GestureSensorImpl> lazy2) {
            Intrinsics.checkParameterIsNotNull(context, "context");
            Intrinsics.checkParameterIsNotNull(lazy, "chreGestureSensor");
            Intrinsics.checkParameterIsNotNull(lazy2, "apGestureSensor");
            GestureSensor gestureSensor;
            if (Intrinsics.areEqual(Settings$Secure.getString(context.getContentResolver(), "systemui_google_columbus_sensor"), "CHRE") && context.getPackageManager().hasSystemFeature("contexthub")) {
                final CHREGestureSensor value = lazy.get();
                Intrinsics.checkExpressionValueIsNotNull(value, "chreGestureSensor.get()");
                gestureSensor = value;
            }
            else {
                final GestureSensorImpl value2 = lazy2.get();
                Intrinsics.checkExpressionValueIsNotNull(value2, "apGestureSensor.get()");
                gestureSensor = value2;
            }
            return gestureSensor;
        }
        
        public final long provideTransientGateDuration() {
            return 500L;
        }
        
        public final Map<String, Action> provideUserSelectedActions(final LaunchOpa launchOpa, final LaunchCamera launchCamera, final ManageMedia manageMedia, final TakeScreenshot takeScreenshot, final LaunchOverview launchOverview) {
            Intrinsics.checkParameterIsNotNull(launchOpa, "launchOpa");
            Intrinsics.checkParameterIsNotNull(launchCamera, "launchCamera");
            Intrinsics.checkParameterIsNotNull(manageMedia, "manageMedia");
            Intrinsics.checkParameterIsNotNull(takeScreenshot, "takeScreenshot");
            Intrinsics.checkParameterIsNotNull(launchOverview, "launchOverview");
            return MapsKt.mapOf(new Pair((A)"assistant", (B)launchOpa), new Pair((A)"camera", (B)launchCamera), new Pair((A)"media", (B)manageMedia), new Pair((A)"screenshot", (B)takeScreenshot), new Pair((A)"overview", (B)launchOverview));
        }
    }
}
