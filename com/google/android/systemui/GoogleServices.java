// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui;

import com.google.android.systemui.face.FaceNotificationService;
import com.google.android.systemui.elmyra.ServiceConfiguration;
import com.google.android.systemui.elmyra.ElmyraService;
import com.google.android.systemui.elmyra.ElmyraContext;
import com.google.android.systemui.ambientmusic.AmbientIndicationService;
import com.android.systemui.R$id;
import com.google.android.systemui.ambientmusic.AmbientIndicationContainer;
import com.android.systemui.Dumpable;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.content.Context;
import com.android.systemui.statusbar.phone.StatusBar;
import java.util.ArrayList;
import com.google.android.systemui.elmyra.ServiceConfigurationGoogle;
import com.android.systemui.VendorServices;

public class GoogleServices extends VendorServices
{
    private final ServiceConfigurationGoogle mServiceConfigurationGoogle;
    private ArrayList<Object> mServices;
    private final StatusBar mStatusBar;
    
    public GoogleServices(final Context context, final ServiceConfigurationGoogle mServiceConfigurationGoogle, final StatusBar mStatusBar) {
        super(context);
        this.mServices = new ArrayList<Object>();
        this.mServiceConfigurationGoogle = mServiceConfigurationGoogle;
        this.mStatusBar = mStatusBar;
    }
    
    private void addService(final Object e) {
        if (e != null) {
            this.mServices.add(e);
        }
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        for (int i = 0; i < this.mServices.size(); ++i) {
            if (this.mServices.get(i) instanceof Dumpable) {
                ((Dumpable)this.mServices.get(i)).dump(fileDescriptor, printWriter, array);
            }
        }
    }
    
    @Override
    public void start() {
        final AmbientIndicationContainer ambientIndicationContainer = (AmbientIndicationContainer)this.mStatusBar.getNotificationShadeWindowView().findViewById(R$id.ambient_indication_container);
        ambientIndicationContainer.initializeView(this.mStatusBar);
        this.addService(new AmbientIndicationService(super.mContext, ambientIndicationContainer));
        this.addService(new DisplayCutoutEmulationAdapter(super.mContext));
        if (super.mContext.getPackageManager().hasSystemFeature("android.hardware.context_hub") && new ElmyraContext(super.mContext).isAvailable()) {
            this.addService(new ElmyraService(super.mContext, this.mServiceConfigurationGoogle));
        }
        if (super.mContext.getPackageManager().hasSystemFeature("android.hardware.biometrics.face")) {
            this.addService(new FaceNotificationService(super.mContext));
        }
    }
}
