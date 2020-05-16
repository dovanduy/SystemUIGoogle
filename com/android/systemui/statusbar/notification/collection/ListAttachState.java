// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection;

import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifSection;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifPromoter;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifFilter;

public final class ListAttachState
{
    public static final Companion Companion;
    private NotifFilter excludingFilter;
    private GroupEntry parent;
    private NotifPromoter promoter;
    private NotifSection section;
    private int sectionIndex;
    
    static {
        Companion = new Companion(null);
    }
    
    private ListAttachState(final GroupEntry parent, final NotifSection section, final int sectionIndex, final NotifFilter excludingFilter, final NotifPromoter promoter) {
        this.parent = parent;
        this.section = section;
        this.sectionIndex = sectionIndex;
        this.excludingFilter = excludingFilter;
        this.promoter = promoter;
    }
    
    public static final ListAttachState create() {
        return ListAttachState.Companion.create();
    }
    
    public final void clone(final ListAttachState listAttachState) {
        Intrinsics.checkParameterIsNotNull(listAttachState, "other");
        this.parent = listAttachState.parent;
        this.section = listAttachState.section;
        this.sectionIndex = listAttachState.sectionIndex;
        this.excludingFilter = listAttachState.excludingFilter;
        this.promoter = listAttachState.promoter;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this != o) {
            if (o instanceof ListAttachState) {
                final ListAttachState listAttachState = (ListAttachState)o;
                if (Intrinsics.areEqual(this.parent, listAttachState.parent) && Intrinsics.areEqual(this.section, listAttachState.section) && this.sectionIndex == listAttachState.sectionIndex && Intrinsics.areEqual(this.excludingFilter, listAttachState.excludingFilter) && Intrinsics.areEqual(this.promoter, listAttachState.promoter)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public final NotifFilter getExcludingFilter() {
        return this.excludingFilter;
    }
    
    public final GroupEntry getParent() {
        return this.parent;
    }
    
    public final NotifPromoter getPromoter() {
        return this.promoter;
    }
    
    public final NotifSection getSection() {
        return this.section;
    }
    
    public final int getSectionIndex() {
        return this.sectionIndex;
    }
    
    @Override
    public int hashCode() {
        final GroupEntry parent = this.parent;
        int hashCode = 0;
        int hashCode2;
        if (parent != null) {
            hashCode2 = parent.hashCode();
        }
        else {
            hashCode2 = 0;
        }
        final NotifSection section = this.section;
        int hashCode3;
        if (section != null) {
            hashCode3 = section.hashCode();
        }
        else {
            hashCode3 = 0;
        }
        final int hashCode4 = Integer.hashCode(this.sectionIndex);
        final NotifFilter excludingFilter = this.excludingFilter;
        int hashCode5;
        if (excludingFilter != null) {
            hashCode5 = excludingFilter.hashCode();
        }
        else {
            hashCode5 = 0;
        }
        final NotifPromoter promoter = this.promoter;
        if (promoter != null) {
            hashCode = promoter.hashCode();
        }
        return (((hashCode2 * 31 + hashCode3) * 31 + hashCode4) * 31 + hashCode5) * 31 + hashCode;
    }
    
    public final void reset() {
        this.parent = null;
        this.section = null;
        this.sectionIndex = -1;
        this.excludingFilter = null;
        this.promoter = null;
    }
    
    public final void setExcludingFilter(final NotifFilter excludingFilter) {
        this.excludingFilter = excludingFilter;
    }
    
    public final void setParent(final GroupEntry parent) {
        this.parent = parent;
    }
    
    public final void setPromoter(final NotifPromoter promoter) {
        this.promoter = promoter;
    }
    
    public final void setSection(final NotifSection section) {
        this.section = section;
    }
    
    public final void setSectionIndex(final int sectionIndex) {
        this.sectionIndex = sectionIndex;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ListAttachState(parent=");
        sb.append(this.parent);
        sb.append(", section=");
        sb.append(this.section);
        sb.append(", sectionIndex=");
        sb.append(this.sectionIndex);
        sb.append(", excludingFilter=");
        sb.append(this.excludingFilter);
        sb.append(", promoter=");
        sb.append(this.promoter);
        sb.append(")");
        return sb.toString();
    }
    
    public static final class Companion
    {
        private Companion() {
        }
        
        public final ListAttachState create() {
            return new ListAttachState(null, null, -1, null, null, null);
        }
    }
}
