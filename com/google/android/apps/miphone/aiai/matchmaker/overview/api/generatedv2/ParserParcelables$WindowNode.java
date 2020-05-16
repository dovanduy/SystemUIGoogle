// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generatedv2;

import android.os.Bundle;
import android.support.annotation.Nullable;

public final class ParserParcelables$WindowNode
{
    private int displayId;
    private int height;
    private int left;
    @Nullable
    private ParserParcelables$ViewNode rootViewNode;
    private int top;
    private int width;
    
    private ParserParcelables$WindowNode() {
    }
    
    public Bundle writeToBundle() {
        final Bundle bundle = new Bundle();
        bundle.putInt("displayId", this.displayId);
        final ParserParcelables$ViewNode rootViewNode = this.rootViewNode;
        if (rootViewNode == null) {
            bundle.putBundle("rootViewNode", (Bundle)null);
        }
        else {
            bundle.putBundle("rootViewNode", rootViewNode.writeToBundle());
        }
        bundle.putInt("left", this.left);
        bundle.putInt("top", this.top);
        bundle.putInt("width", this.width);
        bundle.putInt("height", this.height);
        return bundle;
    }
}
