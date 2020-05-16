// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.management;

import com.android.systemui.controls.controller.ControlInfo;
import java.util.List;

public interface ControlsModel
{
    void changeFavoriteStatus(final String p0, final boolean p1);
    
    List<ElementWrapper> getElements();
    
    List<ControlInfo.Builder> getFavorites();
}
