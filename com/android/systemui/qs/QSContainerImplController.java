// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import com.android.systemui.R$id;

public class QSContainerImplController
{
    private final QuickStatusBarHeaderController mQuickStatusBarHeaderController;
    private final QSContainerImpl mView;
    
    private QSContainerImplController(final QSContainerImpl mView, final QuickStatusBarHeaderController.Builder builder) {
        this.mView = mView;
        builder.setQuickStatusBarHeader((QuickStatusBarHeader)mView.findViewById(R$id.header));
        this.mQuickStatusBarHeaderController = builder.build();
    }
    
    public void setListening(final boolean listening) {
        this.mQuickStatusBarHeaderController.setListening(listening);
    }
    
    public static class Builder
    {
        private final QuickStatusBarHeaderController.Builder mQuickStatusBarHeaderControllerBuilder;
        private QSContainerImpl mView;
        
        public Builder(final QuickStatusBarHeaderController.Builder mQuickStatusBarHeaderControllerBuilder) {
            this.mQuickStatusBarHeaderControllerBuilder = mQuickStatusBarHeaderControllerBuilder;
        }
        
        public QSContainerImplController build() {
            return new QSContainerImplController(this.mView, this.mQuickStatusBarHeaderControllerBuilder, null);
        }
        
        public Builder setQSContainerImpl(final QSContainerImpl mView) {
            this.mView = mView;
            return this;
        }
    }
}
