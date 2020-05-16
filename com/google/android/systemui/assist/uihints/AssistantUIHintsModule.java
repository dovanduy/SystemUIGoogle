// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.view.ViewGroup;
import android.util.Log;
import android.content.Intent;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;
import com.google.android.systemui.assist.uihints.input.NgaInputHandler;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsController;

public abstract class AssistantUIHintsModule
{
    static Set<NgaMessageHandler.EdgeLightsInfoListener> bindEdgeLightsInfoListeners(final EdgeLightsController edgeLightsController, final NgaInputHandler ngaInputHandler) {
        return new HashSet<NgaMessageHandler.EdgeLightsInfoListener>(Arrays.asList(edgeLightsController, ngaInputHandler));
    }
    
    static NgaMessageHandler.StartActivityInfoListener provideActivityStarter(final Lazy<StatusBar> lazy) {
        return new NgaMessageHandler.StartActivityInfoListener() {
            @Override
            public void onStartActivityInfo(final Intent intent, final boolean b) {
                if (intent == null) {
                    Log.e("ActivityStarter", "Null intent; cannot start activity");
                    return;
                }
                lazy.get().startActivity(intent, b);
            }
        };
    }
    
    static Set<NgaMessageHandler.AudioInfoListener> provideAudioInfoListeners(final EdgeLightsController edgeLightsController, final GlowController glowController) {
        return new HashSet<NgaMessageHandler.AudioInfoListener>(Arrays.asList(edgeLightsController, glowController));
    }
    
    static Set<NgaMessageHandler.CardInfoListener> provideCardInfoListeners(final GlowController glowController, final ScrimController scrimController, final TranscriptionController transcriptionController, final LightnessProvider lightnessProvider) {
        return new HashSet<NgaMessageHandler.CardInfoListener>(Arrays.asList(glowController, scrimController, transcriptionController, lightnessProvider));
    }
    
    static Set<NgaMessageHandler.ConfigInfoListener> provideConfigInfoListeners(final AssistantPresenceHandler assistantPresenceHandler, final TouchInsideHandler touchInsideHandler, final TouchOutsideHandler touchOutsideHandler, final TaskStackNotifier taskStackNotifier, final KeyboardMonitor keyboardMonitor, final ColorChangeHandler colorChangeHandler, final ConfigurationHandler configurationHandler) {
        return new HashSet<NgaMessageHandler.ConfigInfoListener>(Arrays.asList(assistantPresenceHandler, touchInsideHandler, touchOutsideHandler, taskStackNotifier, keyboardMonitor, colorChangeHandler, configurationHandler));
    }
    
    static ViewGroup provideParentViewGroup(final OverlayUiHost overlayUiHost) {
        return overlayUiHost.getParent();
    }
}
