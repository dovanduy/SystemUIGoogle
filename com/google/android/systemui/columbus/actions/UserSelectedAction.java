// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.actions;

import com.google.android.systemui.columbus.sensors.GestureSensor;
import java.util.Iterator;
import java.util.concurrent.Executor;
import android.util.Log;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import java.util.Map;
import android.provider.DeviceConfig$OnPropertiesChangedListener;
import android.os.Handler;
import com.android.systemui.assist.DeviceConfigHelper;

public final class UserSelectedAction extends Action
{
    private final DeviceConfigHelper deviceConfigHelper;
    private final Handler handler;
    private final LaunchOpa launchOpa;
    private final DeviceConfig$OnPropertiesChangedListener propertiesChangedListener;
    private Action selectedAction;
    private final Map<String, Action> userSelectedActions;
    
    public UserSelectedAction(final Context context, final DeviceConfigHelper deviceConfigHelper, final Map<String, Action> userSelectedActions, final LaunchOpa launchOpa, final Handler handler) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(deviceConfigHelper, "deviceConfigHelper");
        Intrinsics.checkParameterIsNotNull(userSelectedActions, "userSelectedActions");
        Intrinsics.checkParameterIsNotNull(launchOpa, "launchOpa");
        Intrinsics.checkParameterIsNotNull(handler, "handler");
        super(context, null);
        this.deviceConfigHelper = deviceConfigHelper;
        this.userSelectedActions = userSelectedActions;
        this.launchOpa = launchOpa;
        this.handler = handler;
        this.propertiesChangedListener = (DeviceConfig$OnPropertiesChangedListener)new UserSelectedAction$propertiesChangedListener.UserSelectedAction$propertiesChangedListener$1(this);
        this.selectedAction = getSelectedAction$default(this, null, 1, null);
        final StringBuilder sb = new StringBuilder();
        sb.append("User Action selected: ");
        sb.append(this.selectedAction);
        Log.i("Columbus/SelectedAction", sb.toString());
        this.deviceConfigHelper.addOnPropertiesChangedListener(new Executor() {
            final /* synthetic */ UserSelectedAction this$0;
            
            @Override
            public final void execute(final Runnable runnable) {
                UserSelectedAction.access$getHandler$p(this.this$0).post(runnable);
            }
        }, this.propertiesChangedListener);
        final UserSelectedAction$sublistener.UserSelectedAction$sublistener$1 listener = new UserSelectedAction$sublistener.UserSelectedAction$sublistener$1(this);
        final Iterator<Action> iterator = (Iterator<Action>)this.userSelectedActions.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().setListener((Listener)listener);
        }
    }
    
    public static final /* synthetic */ Handler access$getHandler$p(final UserSelectedAction userSelectedAction) {
        return userSelectedAction.handler;
    }
    
    private final Action getSelectedAction(String string) {
        if (string == null) {
            string = this.deviceConfigHelper.getString("systemui_google_columbus_user_action", "assistant");
        }
        if (string == null) {
            string = "";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Selected action name: ");
        sb.append(string);
        Log.i("Columbus/SelectedAction", sb.toString());
        return this.userSelectedActions.getOrDefault(string, this.launchOpa);
    }
    
    static /* synthetic */ Action getSelectedAction$default(final UserSelectedAction userSelectedAction, String s, final int n, final Object o) {
        if ((n & 0x1) != 0x0) {
            s = null;
        }
        return userSelectedAction.getSelectedAction(s);
    }
    
    public final boolean isAssistant() {
        return Intrinsics.areEqual(this.selectedAction, this.launchOpa);
    }
    
    @Override
    public boolean isAvailable() {
        return this.selectedAction.isAvailable();
    }
    
    @Override
    public void onProgress(final int n, final GestureSensor.DetectionProperties detectionProperties) {
        this.selectedAction.onProgress(n, detectionProperties);
    }
    
    @Override
    public void onTrigger() {
        this.selectedAction.onTrigger();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [selectedAction -> ");
        sb.append(this.selectedAction);
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public void updateFeedbackEffects(final int n, final GestureSensor.DetectionProperties detectionProperties) {
        this.selectedAction.updateFeedbackEffects(n, detectionProperties);
    }
}
