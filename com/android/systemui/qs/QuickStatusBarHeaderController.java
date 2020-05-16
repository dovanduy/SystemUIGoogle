// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import com.android.systemui.R$id;
import com.android.systemui.qs.carrier.QSCarrierGroup;
import com.android.systemui.qs.carrier.QSCarrierGroupController;

public class QuickStatusBarHeaderController
{
    private final QSCarrierGroupController mQSCarrierGroupController;
    private final QuickStatusBarHeader mView;
    
    private QuickStatusBarHeaderController(final QuickStatusBarHeader mView, final QSCarrierGroupController.Builder builder) {
        this.mView = mView;
        builder.setQSCarrierGroup((QSCarrierGroup)mView.findViewById(R$id.carrier_group));
        this.mQSCarrierGroupController = builder.build();
    }
    
    public void setListening(final boolean b) {
        this.mQSCarrierGroupController.setListening(b);
        this.mView.setListening(b);
    }
    
    public static class Builder
    {
        private final QSCarrierGroupController.Builder mQSCarrierGroupControllerBuilder;
        private QuickStatusBarHeader mView;
        
        public Builder(final QSCarrierGroupController.Builder mqsCarrierGroupControllerBuilder) {
            this.mQSCarrierGroupControllerBuilder = mqsCarrierGroupControllerBuilder;
        }
        
        public QuickStatusBarHeaderController build() {
            return new QuickStatusBarHeaderController(this.mView, this.mQSCarrierGroupControllerBuilder, null);
        }
        
        public Builder setQuickStatusBarHeader(final QuickStatusBarHeader mView) {
            this.mView = mView;
            return this;
        }
    }
}
