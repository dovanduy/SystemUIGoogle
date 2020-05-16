// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints.edgelights.mode;

import com.android.systemui.assist.ui.EdgeLight;
import com.android.systemui.assist.ui.PerimeterPathGuide;
import com.android.internal.logging.MetricsLogger;
import android.metrics.LogMaker;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsView;

public final class Gone implements Mode
{
    @Override
    public int getSubType() {
        return 0;
    }
    
    @Override
    public void logState() {
        MetricsLogger.action(new LogMaker(1716).setType(2));
    }
    
    @Override
    public void onNewModeRequest(final EdgeLightsView edgeLightsView, final Mode mode) {
        edgeLightsView.setVisibility(0);
        edgeLightsView.commitModeTransition(mode);
    }
    
    @Override
    public void start(final EdgeLightsView edgeLightsView, final PerimeterPathGuide perimeterPathGuide, final Mode mode) {
        edgeLightsView.setAssistLights(new EdgeLight[0]);
    }
}
