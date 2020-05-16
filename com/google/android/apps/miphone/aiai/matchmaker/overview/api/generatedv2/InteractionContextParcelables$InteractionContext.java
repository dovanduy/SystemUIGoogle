// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import java.util.Iterator;
import java.util.ArrayList;
import android.os.Bundle;
import java.util.List;
import android.support.annotation.Nullable;

public final class InteractionContextParcelables$InteractionContext
{
    private boolean disallowCopyPaste;
    private boolean expandFocusRect;
    @Nullable
    private SuggestParcelables$OnScreenRect focusRect;
    private int focusRectExpandPx;
    @Nullable
    private List<InteractionContextParcelables$InteractionEvent> interactionEvents;
    private SuggestParcelables$InteractionType interactionType;
    private boolean isPrimaryTask;
    private boolean isRtlContent;
    @Nullable
    private ContentParcelables$Contents previousContents;
    private boolean requestDebugInfo;
    private boolean requestStats;
    private long screenSessionId;
    private int versionCode;
    
    private InteractionContextParcelables$InteractionContext() {
    }
    
    public static InteractionContextParcelables$InteractionContext create() {
        return new InteractionContextParcelables$InteractionContext();
    }
    
    public void setDisallowCopyPaste(final boolean disallowCopyPaste) {
        this.disallowCopyPaste = disallowCopyPaste;
    }
    
    public void setInteractionType(final SuggestParcelables$InteractionType interactionType) {
        this.interactionType = interactionType;
    }
    
    public void setIsPrimaryTask(final boolean isPrimaryTask) {
        this.isPrimaryTask = isPrimaryTask;
    }
    
    public void setVersionCode(final int versionCode) {
        this.versionCode = versionCode;
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        bundle.putLong("screenSessionId", this.screenSessionId);
        final SuggestParcelables$OnScreenRect focusRect = this.focusRect;
        if (focusRect == null) {
            bundle.putBundle("focusRect", (Bundle)null);
        }
        else {
            bundle.putBundle("focusRect", focusRect.writeToBundle());
        }
        bundle.putBoolean("expandFocusRect", this.expandFocusRect);
        bundle.putInt("focusRectExpandPx", this.focusRectExpandPx);
        final ContentParcelables$Contents previousContents = this.previousContents;
        if (previousContents == null) {
            bundle.putBundle("previousContents", (Bundle)null);
        }
        else {
            bundle.putBundle("previousContents", previousContents.writeToBundle());
        }
        bundle.putBoolean("requestStats", this.requestStats);
        bundle.putBoolean("requestDebugInfo", this.requestDebugInfo);
        bundle.putBoolean("isRtlContent", this.isRtlContent);
        bundle.putBoolean("disallowCopyPaste", this.disallowCopyPaste);
        bundle.putInt("versionCode", this.versionCode);
        if (this.interactionEvents == null) {
            bundle.putParcelableArrayList("interactionEvents", (ArrayList)null);
        }
        else {
            final ArrayList<Bundle> list = new ArrayList<Bundle>(this.interactionEvents.size());
            for (final InteractionContextParcelables$InteractionEvent interactionContextParcelables$InteractionEvent : this.interactionEvents) {
                if (interactionContextParcelables$InteractionEvent == null) {
                    list.add(null);
                }
                else {
                    list.add(interactionContextParcelables$InteractionEvent.writeToBundle());
                }
            }
            bundle.putParcelableArrayList("interactionEvents", (ArrayList)list);
        }
        final SuggestParcelables$InteractionType interactionType = this.interactionType;
        if (interactionType == null) {
            bundle.putBundle("interactionType", (Bundle)null);
        }
        else {
            bundle.putBundle("interactionType", interactionType.writeToBundle());
        }
        bundle.putBoolean("isPrimaryTask", this.isPrimaryTask);
        return bundle;
    }
}
