// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection;

import java.util.Comparator;
import com.android.internal.annotations.VisibleForTesting;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class GroupEntry extends ListEntry
{
    public static final GroupEntry ROOT_ENTRY;
    private final List<NotificationEntry> mChildren;
    private NotificationEntry mSummary;
    private final List<NotificationEntry> mUnmodifiableChildren;
    
    static {
        ROOT_ENTRY = new GroupEntry("<root>");
    }
    
    @VisibleForTesting
    public GroupEntry(final String s) {
        super(s);
        final ArrayList<NotificationEntry> list = new ArrayList<NotificationEntry>();
        this.mChildren = list;
        this.mUnmodifiableChildren = (List<NotificationEntry>)Collections.unmodifiableList((List<?>)list);
    }
    
    void addChild(final NotificationEntry notificationEntry) {
        this.mChildren.add(notificationEntry);
    }
    
    void clearChildren() {
        this.mChildren.clear();
    }
    
    public List<NotificationEntry> getChildren() {
        return this.mUnmodifiableChildren;
    }
    
    List<NotificationEntry> getRawChildren() {
        return this.mChildren;
    }
    
    @Override
    public NotificationEntry getRepresentativeEntry() {
        return this.mSummary;
    }
    
    public NotificationEntry getSummary() {
        return this.mSummary;
    }
    
    @VisibleForTesting
    public void setSummary(final NotificationEntry mSummary) {
        this.mSummary = mSummary;
    }
    
    void sortChildren(final Comparator<? super NotificationEntry> c) {
        this.mChildren.sort(c);
    }
}
