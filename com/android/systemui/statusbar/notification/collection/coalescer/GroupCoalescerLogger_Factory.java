// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coalescer;

import com.android.systemui.log.LogBuffer;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class GroupCoalescerLogger_Factory implements Factory<GroupCoalescerLogger>
{
    private final Provider<LogBuffer> bufferProvider;
    
    public GroupCoalescerLogger_Factory(final Provider<LogBuffer> bufferProvider) {
        this.bufferProvider = bufferProvider;
    }
    
    public static GroupCoalescerLogger_Factory create(final Provider<LogBuffer> provider) {
        return new GroupCoalescerLogger_Factory(provider);
    }
    
    public static GroupCoalescerLogger provideInstance(final Provider<LogBuffer> provider) {
        return new GroupCoalescerLogger(provider.get());
    }
    
    @Override
    public GroupCoalescerLogger get() {
        return provideInstance(this.bufferProvider);
    }
}
