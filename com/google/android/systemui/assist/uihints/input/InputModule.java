// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints.input;

import com.google.android.systemui.assist.uihints.NgaMessageHandler;
import com.google.android.systemui.assist.uihints.ScrimController;
import com.google.android.systemui.assist.uihints.GlowController;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;
import com.google.android.systemui.assist.uihints.TranscriptionController;
import com.google.android.systemui.assist.uihints.IconController;

public abstract class InputModule
{
    static Set<TouchActionRegion> provideTouchActionRegions(final IconController iconController, final TranscriptionController transcriptionController) {
        return new HashSet<TouchActionRegion>(Arrays.asList(iconController, transcriptionController));
    }
    
    static Set<TouchInsideRegion> provideTouchInsideRegions(final GlowController glowController, final ScrimController scrimController, final TranscriptionController transcriptionController) {
        return new HashSet<TouchInsideRegion>((Collection<? extends TouchInsideRegion>)Arrays.asList(glowController, scrimController, transcriptionController));
    }
}
