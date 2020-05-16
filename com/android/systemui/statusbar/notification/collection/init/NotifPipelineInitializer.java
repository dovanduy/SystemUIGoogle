// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.init;

import android.util.Log;
import com.android.systemui.statusbar.notification.collection.SimpleNotificationListContainer;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl;
import com.android.systemui.statusbar.NotificationListener;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotifViewManager;
import com.android.systemui.statusbar.notification.collection.coordinator.NotifCoordinators;
import com.android.systemui.statusbar.notification.collection.NotifInflaterImpl;
import com.android.systemui.statusbar.notification.collection.NotifCollection;
import com.android.systemui.statusbar.notification.collection.ShadeListBuilder;
import com.android.systemui.statusbar.notification.collection.coalescer.GroupCoalescer;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.Dumpable;

public class NotifPipelineInitializer implements Dumpable
{
    private final DumpManager mDumpManager;
    private final FeatureFlags mFeatureFlags;
    private final GroupCoalescer mGroupCoalescer;
    private final ShadeListBuilder mListBuilder;
    private final NotifCollection mNotifCollection;
    private final NotifInflaterImpl mNotifInflater;
    private final NotifCoordinators mNotifPluggableCoordinators;
    private final NotifViewManager mNotifViewManager;
    private final NotifPipeline mPipelineWrapper;
    
    public NotifPipelineInitializer(final NotifPipeline mPipelineWrapper, final GroupCoalescer mGroupCoalescer, final NotifCollection mNotifCollection, final ShadeListBuilder mListBuilder, final NotifCoordinators mNotifPluggableCoordinators, final NotifInflaterImpl mNotifInflater, final DumpManager mDumpManager, final FeatureFlags mFeatureFlags, final NotifViewManager mNotifViewManager) {
        this.mPipelineWrapper = mPipelineWrapper;
        this.mGroupCoalescer = mGroupCoalescer;
        this.mNotifCollection = mNotifCollection;
        this.mListBuilder = mListBuilder;
        this.mNotifPluggableCoordinators = mNotifPluggableCoordinators;
        this.mDumpManager = mDumpManager;
        this.mNotifInflater = mNotifInflater;
        this.mFeatureFlags = mFeatureFlags;
        this.mNotifViewManager = mNotifViewManager;
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        this.mNotifViewManager.dump(fileDescriptor, printWriter, array);
        this.mNotifPluggableCoordinators.dump(fileDescriptor, printWriter, array);
        this.mGroupCoalescer.dump(fileDescriptor, printWriter, array);
    }
    
    public void initialize(final NotificationListener notificationListener, final NotificationRowBinderImpl rowBinder, final NotificationListContainer viewConsumer) {
        this.mDumpManager.registerDumpable("NotifPipeline", this);
        if (this.mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
            this.mNotifInflater.setRowBinder(rowBinder);
        }
        this.mNotifPluggableCoordinators.attach(this.mPipelineWrapper);
        this.mNotifViewManager.setViewConsumer(viewConsumer);
        this.mNotifViewManager.attach(this.mListBuilder);
        this.mListBuilder.attach(this.mNotifCollection);
        this.mNotifCollection.attach(this.mGroupCoalescer);
        this.mGroupCoalescer.attach(notificationListener);
        Log.d("NotifPipeline", "Notif pipeline initialized");
    }
}
