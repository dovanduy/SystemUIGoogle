// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection;

import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifSection;
import java.util.List;
import java.util.Collection;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifPromoter;
import com.android.systemui.statusbar.notification.collection.listbuilder.OnBeforeFinalizeFilterListener;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifLifetimeExtender;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifDismissInterceptor;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifFilter;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.statusbar.notification.collection.notifcollection.CommonNotifCollection;

public class NotifPipeline implements CommonNotifCollection
{
    private final NotifCollection mNotifCollection;
    private final ShadeListBuilder mShadeListBuilder;
    
    public NotifPipeline(final NotifCollection mNotifCollection, final ShadeListBuilder mShadeListBuilder) {
        this.mNotifCollection = mNotifCollection;
        this.mShadeListBuilder = mShadeListBuilder;
    }
    
    @Override
    public void addCollectionListener(final NotifCollectionListener notifCollectionListener) {
        this.mNotifCollection.addCollectionListener(notifCollectionListener);
    }
    
    public void addFinalizeFilter(final NotifFilter notifFilter) {
        this.mShadeListBuilder.addFinalizeFilter(notifFilter);
    }
    
    public void addNotificationDismissInterceptor(final NotifDismissInterceptor notifDismissInterceptor) {
        this.mNotifCollection.addNotificationDismissInterceptor(notifDismissInterceptor);
    }
    
    public void addNotificationLifetimeExtender(final NotifLifetimeExtender notifLifetimeExtender) {
        this.mNotifCollection.addNotificationLifetimeExtender(notifLifetimeExtender);
    }
    
    public void addOnBeforeFinalizeFilterListener(final OnBeforeFinalizeFilterListener onBeforeFinalizeFilterListener) {
        this.mShadeListBuilder.addOnBeforeFinalizeFilterListener(onBeforeFinalizeFilterListener);
    }
    
    public void addPreGroupFilter(final NotifFilter notifFilter) {
        this.mShadeListBuilder.addPreGroupFilter(notifFilter);
    }
    
    public void addPromoter(final NotifPromoter notifPromoter) {
        this.mShadeListBuilder.addPromoter(notifPromoter);
    }
    
    @Override
    public Collection<NotificationEntry> getAllNotifs() {
        return this.mNotifCollection.getAllNotifs();
    }
    
    public List<ListEntry> getShadeList() {
        return this.mShadeListBuilder.getShadeList();
    }
    
    public int getShadeListCount() {
        final List<ListEntry> shadeList = this.getShadeList();
        int i = 0;
        int n = 0;
        while (i < shadeList.size()) {
            final ListEntry listEntry = shadeList.get(i);
            if (listEntry instanceof GroupEntry) {
                n = n + 1 + ((GroupEntry)listEntry).getChildren().size();
            }
            else {
                ++n;
            }
            ++i;
        }
        return n;
    }
    
    public void setSections(final List<NotifSection> sections) {
        this.mShadeListBuilder.setSections(sections);
    }
}
