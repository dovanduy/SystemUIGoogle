// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coalescer;

import java.util.ArrayList;
import java.util.List;

public class EventBatch
{
    Runnable mCancelShortTimeout;
    final long mCreatedTimestamp;
    final String mGroupKey;
    final List<CoalescedEvent> mMembers;
    
    EventBatch(final long mCreatedTimestamp, final String mGroupKey) {
        this.mMembers = new ArrayList<CoalescedEvent>();
        this.mCreatedTimestamp = mCreatedTimestamp;
        this.mGroupKey = mGroupKey;
    }
}
