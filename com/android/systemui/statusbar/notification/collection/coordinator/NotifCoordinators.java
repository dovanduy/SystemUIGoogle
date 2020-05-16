// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.coordinator;

import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import java.util.Iterator;
import java.util.ArrayList;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifSection;
import java.util.List;
import com.android.systemui.Dumpable;

public class NotifCoordinators implements Dumpable
{
    private final List<Coordinator> mCoordinators;
    private final List<NotifSection> mOrderedSections;
    
    public NotifCoordinators(final DumpManager dumpManager, final FeatureFlags featureFlags, final HideNotifsForOtherUsersCoordinator hideNotifsForOtherUsersCoordinator, final KeyguardCoordinator keyguardCoordinator, final RankingCoordinator rankingCoordinator, final ForegroundCoordinator foregroundCoordinator, final DeviceProvisionedCoordinator deviceProvisionedCoordinator, final BubbleCoordinator bubbleCoordinator, final HeadsUpCoordinator headsUpCoordinator, final ConversationCoordinator conversationCoordinator, final PreparationCoordinator preparationCoordinator) {
        this.mCoordinators = new ArrayList<Coordinator>();
        this.mOrderedSections = new ArrayList<NotifSection>();
        dumpManager.registerDumpable("NotifCoordinators", this);
        this.mCoordinators.add(new HideLocallyDismissedNotifsCoordinator());
        this.mCoordinators.add(hideNotifsForOtherUsersCoordinator);
        this.mCoordinators.add(keyguardCoordinator);
        this.mCoordinators.add(rankingCoordinator);
        this.mCoordinators.add(foregroundCoordinator);
        this.mCoordinators.add(deviceProvisionedCoordinator);
        this.mCoordinators.add(bubbleCoordinator);
        if (featureFlags.isNewNotifPipelineRenderingEnabled()) {
            this.mCoordinators.add(conversationCoordinator);
            this.mCoordinators.add(headsUpCoordinator);
            this.mCoordinators.add(preparationCoordinator);
        }
        for (final Coordinator coordinator : this.mCoordinators) {
            if (coordinator.getSection() != null) {
                this.mOrderedSections.add(coordinator.getSection());
            }
        }
    }
    
    public void attach(final NotifPipeline notifPipeline) {
        final Iterator<Coordinator> iterator = this.mCoordinators.iterator();
        while (iterator.hasNext()) {
            iterator.next().attach(notifPipeline);
        }
        notifPipeline.setSections(this.mOrderedSections);
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println();
        printWriter.println("NotifCoordinators:");
        for (final Coordinator coordinator : this.mCoordinators) {
            final StringBuilder sb = new StringBuilder();
            sb.append("\t");
            sb.append(coordinator.getClass());
            printWriter.println(sb.toString());
        }
        for (final NotifSection notifSection : this.mOrderedSections) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("\t");
            sb2.append(notifSection.getName());
            printWriter.println(sb2.toString());
        }
    }
}
