// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.dreamliner;

import android.os.Bundle;

public class DockInfo
{
    private int accessoryType;
    private String manufacturer;
    private String model;
    private String serialNumber;
    
    public DockInfo(final String manufacturer, final String model, final String serialNumber, final int accessoryType) {
        this.manufacturer = "";
        this.model = "";
        this.serialNumber = "";
        this.accessoryType = -1;
        this.manufacturer = manufacturer;
        this.model = model;
        this.serialNumber = serialNumber;
        this.accessoryType = accessoryType;
    }
    
    Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putString("manufacturer", this.manufacturer);
        bundle.putString("model", this.model);
        bundle.putString("serialNumber", this.serialNumber);
        bundle.putInt("accessoryType", this.accessoryType);
        return bundle;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.manufacturer);
        sb.append(", ");
        sb.append(this.model);
        sb.append(", ");
        sb.append(this.serialNumber);
        sb.append(", ");
        sb.append(this.accessoryType);
        return sb.toString();
    }
}
