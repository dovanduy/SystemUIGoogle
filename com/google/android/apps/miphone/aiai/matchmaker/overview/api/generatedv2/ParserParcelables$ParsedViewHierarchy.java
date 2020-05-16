// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import java.util.Iterator;
import java.util.ArrayList;
import android.os.Bundle;
import java.util.List;
import android.support.annotation.Nullable;

public final class ParserParcelables$ParsedViewHierarchy
{
    private long acquisitionEndTime;
    private long acquisitionStartTime;
    @Nullable
    private String activityClassName;
    private boolean hasKnownIssues;
    private boolean isHomeActivity;
    @Nullable
    private String packageName;
    @Nullable
    private List<ParserParcelables$WindowNode> windows;
    
    private ParserParcelables$ParsedViewHierarchy() {
    }
    
    public static ParserParcelables$ParsedViewHierarchy create() {
        return new ParserParcelables$ParsedViewHierarchy();
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        bundle.putLong("acquisitionStartTime", this.acquisitionStartTime);
        bundle.putLong("acquisitionEndTime", this.acquisitionEndTime);
        bundle.putBoolean("isHomeActivity", this.isHomeActivity);
        if (this.windows == null) {
            bundle.putParcelableArrayList("windows", (ArrayList)null);
        }
        else {
            final ArrayList<Bundle> list = new ArrayList<Bundle>(this.windows.size());
            for (final ParserParcelables$WindowNode parserParcelables$WindowNode : this.windows) {
                if (parserParcelables$WindowNode == null) {
                    list.add(null);
                }
                else {
                    list.add(parserParcelables$WindowNode.writeToBundle());
                }
            }
            bundle.putParcelableArrayList("windows", (ArrayList)list);
        }
        bundle.putBoolean("hasKnownIssues", this.hasKnownIssues);
        bundle.putString("packageName", this.packageName);
        bundle.putString("activityClassName", this.activityClassName);
        return bundle;
    }
}
