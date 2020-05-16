// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.drawer;

import android.content.pm.ProviderInfo;
import android.os.Parcel;

public class ProviderTile extends Tile
{
    ProviderTile(final Parcel parcel) {
        super(parcel);
        final String authority = ((ProviderInfo)super.mComponentInfo).authority;
        this.getMetaData().getString("com.android.settings.keyhint");
    }
}
