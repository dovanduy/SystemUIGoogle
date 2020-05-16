// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection;

import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifSection;

public abstract class ListEntry
{
    private final ListAttachState mAttachState;
    int mFirstAddedIteration;
    private final String mKey;
    private final ListAttachState mPreviousAttachState;
    
    ListEntry(final String mKey) {
        this.mFirstAddedIteration = -1;
        this.mPreviousAttachState = ListAttachState.create();
        this.mAttachState = ListAttachState.create();
        this.mKey = mKey;
    }
    
    void beginNewAttachState() {
        this.mPreviousAttachState.clone(this.mAttachState);
        this.mAttachState.reset();
    }
    
    ListAttachState getAttachState() {
        return this.mAttachState;
    }
    
    public String getKey() {
        return this.mKey;
    }
    
    public NotifSection getNotifSection() {
        return this.mAttachState.getSection();
    }
    
    public GroupEntry getParent() {
        return this.mAttachState.getParent();
    }
    
    ListAttachState getPreviousAttachState() {
        return this.mPreviousAttachState;
    }
    
    public abstract NotificationEntry getRepresentativeEntry();
    
    public int getSection() {
        return this.mAttachState.getSectionIndex();
    }
    
    void setParent(final GroupEntry parent) {
        this.mAttachState.setParent(parent);
    }
}
