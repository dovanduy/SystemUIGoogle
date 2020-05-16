// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra;

import com.google.android.systemui.elmyra.sensors.GestureSensor;
import com.google.android.systemui.elmyra.gates.Gate;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import java.util.Collections;
import com.google.android.systemui.elmyra.actions.Action;
import java.util.List;

public interface ServiceConfiguration
{
    default List<Action> getActions() {
        return Collections.emptyList();
    }
    
    default List<FeedbackEffect> getFeedbackEffects() {
        return Collections.emptyList();
    }
    
    default List<Gate> getGates() {
        return Collections.emptyList();
    }
    
    default GestureSensor getGestureSensor() {
        return null;
    }
}
