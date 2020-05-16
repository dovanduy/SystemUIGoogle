// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra;

import com.google.android.systemui.elmyra.sensors.CHREGestureSensor;
import com.google.android.systemui.elmyra.sensors.JNIGestureSensor;
import com.google.android.systemui.elmyra.sensors.config.Adjustment;
import com.google.android.systemui.elmyra.sensors.config.GestureConfiguration;
import com.google.android.systemui.elmyra.sensors.config.ScreenStateAdjustment;
import com.google.android.systemui.elmyra.gates.PowerSaveState;
import com.google.android.systemui.elmyra.gates.CameraVisibility;
import com.google.android.systemui.elmyra.gates.KeyguardDeferredSetup;
import com.google.android.systemui.elmyra.gates.VrMode;
import com.google.android.systemui.elmyra.gates.TelephonyActivity;
import com.google.android.systemui.elmyra.gates.SystemKeyPress;
import com.google.android.systemui.elmyra.gates.NavigationBarVisibility;
import com.google.android.systemui.elmyra.gates.SetupWizard;
import com.google.android.systemui.elmyra.gates.KeyguardProximity;
import com.google.android.systemui.elmyra.gates.UsbState;
import com.google.android.systemui.elmyra.gates.ChargingState;
import com.google.android.systemui.elmyra.gates.WakeMode;
import com.google.android.systemui.elmyra.feedback.UserActivity;
import com.google.android.systemui.elmyra.feedback.NavUndimEffect;
import com.google.android.systemui.elmyra.feedback.HapticClick;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.google.android.systemui.elmyra.actions.SilenceCall;
import com.google.android.systemui.elmyra.actions.SnoozeAlarm;
import com.google.android.systemui.elmyra.actions.DismissTimer;
import com.google.android.systemui.elmyra.actions.UnpinNotifications;
import com.google.android.systemui.elmyra.feedback.SquishyNavigationButtons;
import com.google.android.systemui.elmyra.actions.SetupWizardAction;
import com.google.android.systemui.elmyra.actions.CameraAction;
import com.google.android.systemui.elmyra.actions.SettingsAction;
import com.google.android.systemui.elmyra.actions.LaunchOpa;
import com.google.android.systemui.elmyra.feedback.AssistInvocationEffect;
import android.content.Context;
import com.google.android.systemui.elmyra.sensors.GestureSensor;
import com.google.android.systemui.elmyra.gates.Gate;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import com.google.android.systemui.elmyra.actions.Action;
import java.util.List;

public class ServiceConfigurationGoogle implements ServiceConfiguration
{
    private final List<Action> mActions;
    private final List<FeedbackEffect> mFeedbackEffects;
    private final List<Gate> mGates;
    private final GestureSensor mGestureSensor;
    
    public ServiceConfigurationGoogle(final Context context, final AssistInvocationEffect assistInvocationEffect, final LaunchOpa.Builder builder, final SettingsAction.Builder builder2, final CameraAction.Builder builder3, final SetupWizardAction.Builder builder4, final SquishyNavigationButtons squishyNavigationButtons, final UnpinNotifications unpinNotifications) {
        builder.addFeedbackEffect(assistInvocationEffect);
        final LaunchOpa build = builder.build();
        builder2.setLaunchOpa(build);
        final SettingsAction build2 = builder2.build();
        final List<Action> list = Arrays.asList(new DismissTimer(context), new SnoozeAlarm(context), new SilenceCall(context), build2);
        builder3.addFeedbackEffect(assistInvocationEffect);
        final CameraAction build3 = builder3.build();
        (this.mActions = new ArrayList<Action>()).addAll(list);
        this.mActions.add(unpinNotifications);
        this.mActions.add(build3);
        final List<Action> mActions = this.mActions;
        builder4.setSettingsAction(build2);
        builder4.setLaunchOpa(build);
        mActions.add(builder4.build());
        this.mActions.add(build);
        (this.mFeedbackEffects = new ArrayList<FeedbackEffect>()).add(new HapticClick(context));
        this.mFeedbackEffects.add(squishyNavigationButtons);
        this.mFeedbackEffects.add(new NavUndimEffect());
        this.mFeedbackEffects.add(new UserActivity(context));
        (this.mGates = new ArrayList<Gate>()).add(new WakeMode(context));
        this.mGates.add(new ChargingState(context));
        this.mGates.add(new UsbState(context));
        this.mGates.add(new KeyguardProximity(context));
        this.mGates.add(new SetupWizard(context, Arrays.asList(build2)));
        this.mGates.add(new NavigationBarVisibility(context, list));
        this.mGates.add(new SystemKeyPress(context));
        this.mGates.add(new TelephonyActivity(context));
        this.mGates.add(new VrMode(context));
        this.mGates.add(new KeyguardDeferredSetup(context, list));
        this.mGates.add(new CameraVisibility(context, build3, list));
        this.mGates.add(new PowerSaveState(context));
        final ArrayList<ScreenStateAdjustment> list2 = new ArrayList<ScreenStateAdjustment>();
        list2.add(new ScreenStateAdjustment(context));
        final GestureConfiguration gestureConfiguration = new GestureConfiguration(context, (List<Adjustment>)list2);
        if (JNIGestureSensor.isAvailable(context)) {
            this.mGestureSensor = new JNIGestureSensor(context, gestureConfiguration);
        }
        else {
            this.mGestureSensor = new CHREGestureSensor(context, gestureConfiguration, new SnapshotConfiguration(context));
        }
    }
    
    @Override
    public List<Action> getActions() {
        return this.mActions;
    }
    
    @Override
    public List<FeedbackEffect> getFeedbackEffects() {
        return this.mFeedbackEffects;
    }
    
    @Override
    public List<Gate> getGates() {
        return this.mGates;
    }
    
    @Override
    public GestureSensor getGestureSensor() {
        return this.mGestureSensor;
    }
}
