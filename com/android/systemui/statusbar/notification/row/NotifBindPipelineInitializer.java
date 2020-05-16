// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.row;

public class NotifBindPipelineInitializer
{
    NotifBindPipeline mNotifBindPipeline;
    RowContentBindStage mRowContentBindStage;
    
    NotifBindPipelineInitializer(final NotifBindPipeline mNotifBindPipeline, final RowContentBindStage mRowContentBindStage) {
        this.mNotifBindPipeline = mNotifBindPipeline;
        this.mRowContentBindStage = mRowContentBindStage;
    }
    
    public void initialize() {
        this.mNotifBindPipeline.setStage(this.mRowContentBindStage);
    }
}
