// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.content.Context;
import com.android.systemui.assist.ui.DefaultUiController;

public class GoogleDefaultUiController extends DefaultUiController
{
    public GoogleDefaultUiController(final Context context) {
        super(context);
        context.getResources();
        this.setGoogleAssistant(false);
        final AssistantInvocationLightsView mInvocationLightsView = (AssistantInvocationLightsView)LayoutInflater.from(context).inflate(R$layout.invocation_lights, (ViewGroup)super.mRoot, false);
        super.mInvocationLightsView = mInvocationLightsView;
        super.mRoot.addView((View)mInvocationLightsView);
    }
    
    public void setGoogleAssistant(final boolean googleAssistant) {
        ((AssistantInvocationLightsView)super.mInvocationLightsView).setGoogleAssistant(googleAssistant);
    }
}
