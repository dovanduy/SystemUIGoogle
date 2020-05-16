// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import java.util.Objects;
import java.util.Map;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.app.NotificationChannel;
import java.util.Collection;
import java.util.ArrayList;
import android.util.Log;
import java.util.Iterator;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.Dependency;
import android.util.ArraySet;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import java.util.HashMap;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;

public class NotificationGroupManager implements OnHeadsUpChangedListener, StateListener
{
    private int mBarState;
    private BubbleController mBubbleController;
    private final HashMap<String, NotificationGroup> mGroupMap;
    private HeadsUpManager mHeadsUpManager;
    private boolean mIsUpdatingUnchangedGroup;
    private HashMap<String, StatusBarNotification> mIsolatedEntries;
    private final ArraySet<OnGroupChangeListener> mListeners;
    
    public NotificationGroupManager(final StatusBarStateController statusBarStateController) {
        this.mGroupMap = new HashMap<String, NotificationGroup>();
        this.mListeners = (ArraySet<OnGroupChangeListener>)new ArraySet();
        this.mBarState = -1;
        this.mIsolatedEntries = new HashMap<String, StatusBarNotification>();
        this.mBubbleController = null;
        statusBarStateController.addCallback((StatusBarStateController.StateListener)this);
    }
    
    private BubbleController getBubbleController() {
        if (this.mBubbleController == null) {
            this.mBubbleController = Dependency.get(BubbleController.class);
        }
        return this.mBubbleController;
    }
    
    private String getGroupKey(final String s, final String s2) {
        if (this.isIsolated(s)) {
            return s;
        }
        return s2;
    }
    
    private NotificationEntry getGroupSummary(final String key) {
        final NotificationGroup notificationGroup = this.mGroupMap.get(key);
        NotificationEntry summary;
        if (notificationGroup == null) {
            summary = null;
        }
        else {
            summary = notificationGroup.summary;
        }
        return summary;
    }
    
    private int getNumberOfIsolatedChildren(final String anObject) {
        final Iterator<StatusBarNotification> iterator = this.mIsolatedEntries.values().iterator();
        int n = 0;
        while (iterator.hasNext()) {
            final StatusBarNotification statusBarNotification = iterator.next();
            if (statusBarNotification.getGroupKey().equals(anObject) && this.isIsolated(statusBarNotification.getKey())) {
                ++n;
            }
        }
        return n;
    }
    
    private int getTotalNumberOfChildren(final StatusBarNotification statusBarNotification) {
        final int numberOfIsolatedChildren = this.getNumberOfIsolatedChildren(statusBarNotification.getGroupKey());
        final NotificationGroup notificationGroup = this.mGroupMap.get(statusBarNotification.getGroupKey());
        int size;
        if (notificationGroup != null) {
            size = notificationGroup.children.size();
        }
        else {
            size = 0;
        }
        return numberOfIsolatedChildren + size;
    }
    
    private boolean hasIsolatedChildren(final NotificationGroup notificationGroup) {
        return this.getNumberOfIsolatedChildren(notificationGroup.summary.getSbn().getGroupKey()) != 0;
    }
    
    private boolean isGroupChild(final String s, final boolean b, final boolean b2) {
        final boolean isolated = this.isIsolated(s);
        final boolean b3 = false;
        if (isolated) {
            return false;
        }
        boolean b4 = b3;
        if (b) {
            b4 = b3;
            if (!b2) {
                b4 = true;
            }
        }
        return b4;
    }
    
    private boolean isGroupNotFullyVisible(final NotificationGroup notificationGroup) {
        final NotificationEntry summary = notificationGroup.summary;
        return summary == null || summary.isGroupNotFullyVisible();
    }
    
    private boolean isGroupSuppressed(final String key) {
        final NotificationGroup notificationGroup = this.mGroupMap.get(key);
        return notificationGroup != null && notificationGroup.suppressed;
    }
    
    private boolean isIsolated(final String key) {
        return this.mIsolatedEntries.containsKey(key);
    }
    
    private boolean isOnlyChild(final StatusBarNotification statusBarNotification) {
        final boolean groupSummary = statusBarNotification.getNotification().isGroupSummary();
        boolean b = true;
        if (groupSummary || this.getTotalNumberOfChildren(statusBarNotification) != 1) {
            b = false;
        }
        return b;
    }
    
    private void isolateNotification(final NotificationEntry notificationEntry) {
        final StatusBarNotification sbn = notificationEntry.getSbn();
        this.onEntryRemovedInternal(notificationEntry, notificationEntry.getSbn());
        this.mIsolatedEntries.put(sbn.getKey(), sbn);
        this.onEntryAddedInternal(notificationEntry);
        this.updateSuppression(this.mGroupMap.get(notificationEntry.getSbn().getGroupKey()));
        final Iterator iterator = this.mListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onGroupsChanged();
        }
    }
    
    private void onEntryAddedInternal(final NotificationEntry notificationEntry) {
        if (notificationEntry.isRowRemoved()) {
            notificationEntry.setDebugThrowable(new Throwable());
        }
        final StatusBarNotification sbn = notificationEntry.getSbn();
        final boolean groupChild = this.isGroupChild(sbn);
        final String groupKey = this.getGroupKey(sbn);
        NotificationGroup notificationGroup;
        if ((notificationGroup = this.mGroupMap.get(groupKey)) == null) {
            final NotificationGroup value = new NotificationGroup();
            this.mGroupMap.put(groupKey, value);
            final Iterator iterator = this.mListeners.iterator();
            while (true) {
                notificationGroup = value;
                if (!iterator.hasNext()) {
                    break;
                }
                iterator.next().onGroupCreated(value, groupKey);
            }
        }
        if (groupChild) {
            final NotificationEntry notificationEntry2 = notificationGroup.children.get(notificationEntry.getKey());
            if (notificationEntry2 != null && notificationEntry2 != notificationEntry) {
                final Throwable debugThrowable = notificationEntry2.getDebugThrowable();
                final StringBuilder sb = new StringBuilder();
                sb.append("Inconsistent entries found with the same key ");
                sb.append(notificationEntry.getKey());
                sb.append("existing removed: ");
                sb.append(notificationEntry2.isRowRemoved());
                String string;
                if (debugThrowable != null) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append(Log.getStackTraceString(debugThrowable));
                    sb2.append("\n");
                    string = sb2.toString();
                }
                else {
                    string = "";
                }
                sb.append(string);
                sb.append(" added removed");
                sb.append(notificationEntry.isRowRemoved());
                Log.wtf("NotificationGroupManager", sb.toString(), new Throwable());
            }
            notificationGroup.children.put(notificationEntry.getKey(), notificationEntry);
            this.updateSuppression(notificationGroup);
        }
        else {
            notificationGroup.summary = notificationEntry;
            notificationGroup.expanded = notificationEntry.areChildrenExpanded();
            this.updateSuppression(notificationGroup);
            if (!notificationGroup.children.isEmpty()) {
                final Iterator<NotificationEntry> iterator2 = new ArrayList<NotificationEntry>(notificationGroup.children.values()).iterator();
                while (iterator2.hasNext()) {
                    this.onEntryBecomingChild(iterator2.next());
                }
                final Iterator iterator3 = this.mListeners.iterator();
                while (iterator3.hasNext()) {
                    iterator3.next().onGroupCreatedFromChildren(notificationGroup);
                }
            }
        }
    }
    
    private void onEntryBecomingChild(final NotificationEntry notificationEntry) {
        this.updateIsolation(notificationEntry);
    }
    
    private void onEntryRemovedInternal(final NotificationEntry notificationEntry, final StatusBarNotification statusBarNotification) {
        this.onEntryRemovedInternal(notificationEntry, statusBarNotification.getGroupKey(), statusBarNotification.isGroup(), statusBarNotification.getNotification().isGroupSummary());
    }
    
    private void onEntryRemovedInternal(final NotificationEntry notificationEntry, final String s, final boolean b, final boolean b2) {
        final String groupKey = this.getGroupKey(notificationEntry.getKey(), s);
        final NotificationGroup notificationGroup = this.mGroupMap.get(groupKey);
        if (notificationGroup == null) {
            return;
        }
        if (this.isGroupChild(notificationEntry.getKey(), b, b2)) {
            notificationGroup.children.remove(notificationEntry.getKey());
        }
        else {
            notificationGroup.summary = null;
        }
        this.updateSuppression(notificationGroup);
        if (notificationGroup.children.isEmpty() && notificationGroup.summary == null) {
            this.mGroupMap.remove(groupKey);
            final Iterator iterator = this.mListeners.iterator();
            while (iterator.hasNext()) {
                iterator.next().onGroupRemoved(notificationGroup, groupKey);
            }
        }
    }
    
    private void setGroupExpanded(final NotificationGroup notificationGroup, final boolean expanded) {
        notificationGroup.expanded = expanded;
        if (notificationGroup.summary != null) {
            final Iterator iterator = this.mListeners.iterator();
            while (iterator.hasNext()) {
                iterator.next().onGroupExpansionChanged(notificationGroup.summary.getRow(), expanded);
            }
        }
    }
    
    private void setStatusBarState(final int mBarState) {
        this.mBarState = mBarState;
        if (mBarState == 1) {
            this.collapseAllGroups();
        }
    }
    
    private boolean shouldIsolate(final NotificationEntry notificationEntry) {
        final StatusBarNotification sbn = notificationEntry.getSbn();
        final boolean group = sbn.isGroup();
        boolean b2;
        final boolean b = b2 = false;
        if (group) {
            if (sbn.getNotification().isGroupSummary()) {
                b2 = b;
            }
            else {
                final NotificationChannel channel = notificationEntry.getChannel();
                if (channel != null && channel.isImportantConversation()) {
                    return true;
                }
                final HeadsUpManager mHeadsUpManager = this.mHeadsUpManager;
                if (mHeadsUpManager != null && !mHeadsUpManager.isAlerting(notificationEntry.getKey())) {
                    return false;
                }
                final NotificationGroup notificationGroup = this.mGroupMap.get(sbn.getGroupKey());
                if (sbn.getNotification().fullScreenIntent == null && notificationGroup != null && notificationGroup.expanded) {
                    b2 = b;
                    if (!this.isGroupNotFullyVisible(notificationGroup)) {
                        return b2;
                    }
                }
                b2 = true;
            }
        }
        return b2;
    }
    
    private void stopIsolatingNotification(final NotificationEntry notificationEntry) {
        final StatusBarNotification sbn = notificationEntry.getSbn();
        if (this.isIsolated(sbn.getKey())) {
            this.onEntryRemovedInternal(notificationEntry, notificationEntry.getSbn());
            this.mIsolatedEntries.remove(sbn.getKey());
            this.onEntryAddedInternal(notificationEntry);
            final Iterator iterator = this.mListeners.iterator();
            while (iterator.hasNext()) {
                iterator.next().onGroupsChanged();
            }
        }
    }
    
    private void updateSuppression(final NotificationGroup notificationGroup) {
        if (notificationGroup == null) {
            return;
        }
        final Iterator<NotificationEntry> iterator = notificationGroup.children.values().iterator();
        final boolean b = false;
        int n2;
        int n = n2 = 0;
        while (iterator.hasNext()) {
            if (!this.getBubbleController().isBubbleNotificationSuppressedFromShade(iterator.next())) {
                ++n;
            }
            else {
                n2 = 1;
            }
        }
        final boolean suppressed = notificationGroup.suppressed;
        final NotificationEntry summary = notificationGroup.summary;
        boolean suppressed2 = b;
        Label_0148: {
            if (summary != null) {
                suppressed2 = b;
                if (!notificationGroup.expanded) {
                    if (n != 1) {
                        suppressed2 = b;
                        if (n != 0) {
                            break Label_0148;
                        }
                        suppressed2 = b;
                        if (!summary.getSbn().getNotification().isGroupSummary()) {
                            break Label_0148;
                        }
                        if (!this.hasIsolatedChildren(notificationGroup)) {
                            suppressed2 = b;
                            if (n2 == 0) {
                                break Label_0148;
                            }
                        }
                    }
                    suppressed2 = true;
                }
            }
        }
        if (suppressed != (notificationGroup.suppressed = suppressed2)) {
            for (final OnGroupChangeListener onGroupChangeListener : this.mListeners) {
                if (!this.mIsUpdatingUnchangedGroup) {
                    onGroupChangeListener.onGroupSuppressionChanged(notificationGroup, notificationGroup.suppressed);
                    onGroupChangeListener.onGroupsChanged();
                }
            }
        }
    }
    
    public void addOnGroupChangeListener(final OnGroupChangeListener onGroupChangeListener) {
        this.mListeners.add((Object)onGroupChangeListener);
    }
    
    public void collapseAllGroups() {
        final ArrayList<NotificationGroup> list = new ArrayList<NotificationGroup>(this.mGroupMap.values());
        for (int size = list.size(), i = 0; i < size; ++i) {
            final NotificationGroup notificationGroup = list.get(i);
            if (notificationGroup.expanded) {
                this.setGroupExpanded(notificationGroup, false);
            }
            this.updateSuppression(notificationGroup);
        }
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("GroupManager state:");
        final StringBuilder sb = new StringBuilder();
        sb.append("  number of groups: ");
        sb.append(this.mGroupMap.size());
        printWriter.println(sb.toString());
        for (final Map.Entry<String, NotificationGroup> entry : this.mGroupMap.entrySet()) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("\n    key: ");
            sb2.append(entry.getKey());
            printWriter.println(sb2.toString());
            printWriter.println(entry.getValue());
        }
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("\n    isolated entries: ");
        sb3.append(this.mIsolatedEntries.size());
        printWriter.println(sb3.toString());
        for (final Map.Entry<String, StatusBarNotification> entry2 : this.mIsolatedEntries.entrySet()) {
            printWriter.print("      ");
            printWriter.print(entry2.getKey());
            printWriter.print(", ");
            printWriter.println(entry2.getValue());
        }
    }
    
    public ArrayList<NotificationEntry> getChildren(final StatusBarNotification statusBarNotification) {
        final NotificationGroup notificationGroup = this.mGroupMap.get(statusBarNotification.getGroupKey());
        if (notificationGroup == null) {
            return null;
        }
        return new ArrayList<NotificationEntry>(notificationGroup.children.values());
    }
    
    public String getGroupKey(final StatusBarNotification statusBarNotification) {
        return this.getGroupKey(statusBarNotification.getKey(), statusBarNotification.getGroupKey());
    }
    
    public NotificationEntry getGroupSummary(final StatusBarNotification statusBarNotification) {
        return this.getGroupSummary(this.getGroupKey(statusBarNotification));
    }
    
    public ArrayList<NotificationEntry> getLogicalChildren(final StatusBarNotification statusBarNotification) {
        final NotificationGroup notificationGroup = this.mGroupMap.get(statusBarNotification.getGroupKey());
        if (notificationGroup == null) {
            return null;
        }
        final ArrayList list = new ArrayList<NotificationEntry>(notificationGroup.children.values());
        for (final StatusBarNotification statusBarNotification2 : this.mIsolatedEntries.values()) {
            if (statusBarNotification2.getGroupKey().equals(statusBarNotification.getGroupKey())) {
                list.add(this.mGroupMap.get(statusBarNotification2.getKey()).summary);
            }
        }
        return (ArrayList<NotificationEntry>)list;
    }
    
    public NotificationEntry getLogicalGroupSummary(final StatusBarNotification statusBarNotification) {
        return this.getGroupSummary(statusBarNotification.getGroupKey());
    }
    
    public boolean isChildInGroupWithSummary(final StatusBarNotification statusBarNotification) {
        if (!this.isGroupChild(statusBarNotification)) {
            return false;
        }
        final NotificationGroup notificationGroup = this.mGroupMap.get(this.getGroupKey(statusBarNotification));
        return notificationGroup != null && notificationGroup.summary != null && !notificationGroup.suppressed && !notificationGroup.children.isEmpty();
    }
    
    public boolean isGroupChild(final StatusBarNotification statusBarNotification) {
        return this.isGroupChild(statusBarNotification.getKey(), statusBarNotification.isGroup(), statusBarNotification.getNotification().isGroupSummary());
    }
    
    public boolean isGroupExpanded(final StatusBarNotification statusBarNotification) {
        final NotificationGroup notificationGroup = this.mGroupMap.get(this.getGroupKey(statusBarNotification));
        return notificationGroup != null && notificationGroup.expanded;
    }
    
    public boolean isGroupSummary(final StatusBarNotification statusBarNotification) {
        return this.isIsolated(statusBarNotification.getKey()) || statusBarNotification.getNotification().isGroupSummary();
    }
    
    public boolean isLogicalGroupExpanded(final StatusBarNotification statusBarNotification) {
        final NotificationGroup notificationGroup = this.mGroupMap.get(statusBarNotification.getGroupKey());
        return notificationGroup != null && notificationGroup.expanded;
    }
    
    public boolean isOnlyChildInGroup(final StatusBarNotification obj) {
        final boolean onlyChild = this.isOnlyChild(obj);
        final boolean b = false;
        if (!onlyChild) {
            return false;
        }
        final NotificationEntry logicalGroupSummary = this.getLogicalGroupSummary(obj);
        boolean b2 = b;
        if (logicalGroupSummary != null) {
            b2 = b;
            if (!logicalGroupSummary.getSbn().equals(obj)) {
                b2 = true;
            }
        }
        return b2;
    }
    
    public boolean isSummaryOfGroup(final StatusBarNotification b) {
        final boolean groupSummary = this.isGroupSummary(b);
        final boolean b2 = false;
        if (!groupSummary) {
            return false;
        }
        final NotificationGroup notificationGroup = this.mGroupMap.get(this.getGroupKey(b));
        boolean b3 = b2;
        if (notificationGroup != null) {
            if (notificationGroup.summary == null) {
                b3 = b2;
            }
            else {
                b3 = b2;
                if (!notificationGroup.children.isEmpty()) {
                    b3 = b2;
                    if (Objects.equals(notificationGroup.summary.getSbn(), b)) {
                        b3 = true;
                    }
                }
            }
        }
        return b3;
    }
    
    public boolean isSummaryOfSuppressedGroup(final StatusBarNotification statusBarNotification) {
        return this.isGroupSuppressed(this.getGroupKey(statusBarNotification)) && statusBarNotification.getNotification().isGroupSummary();
    }
    
    public void onEntryAdded(final NotificationEntry notificationEntry) {
        this.updateIsolation(notificationEntry);
        this.onEntryAddedInternal(notificationEntry);
    }
    
    public void onEntryRemoved(final NotificationEntry notificationEntry) {
        this.onEntryRemovedInternal(notificationEntry, notificationEntry.getSbn());
        this.mIsolatedEntries.remove(notificationEntry.getKey());
    }
    
    public void onEntryUpdated(final NotificationEntry notificationEntry, final StatusBarNotification statusBarNotification) {
        this.onEntryUpdated(notificationEntry, statusBarNotification.getGroupKey(), statusBarNotification.isGroup(), statusBarNotification.getNotification().isGroupSummary());
    }
    
    public void onEntryUpdated(final NotificationEntry notificationEntry, final String key, final boolean b, final boolean b2) {
        final String groupKey = notificationEntry.getSbn().getGroupKey();
        final boolean equals = key.equals(groupKey);
        boolean mIsUpdatingUnchangedGroup = true;
        final boolean b3 = equals ^ true;
        final boolean groupChild = this.isGroupChild(notificationEntry.getKey(), b, b2);
        final boolean groupChild2 = this.isGroupChild(notificationEntry.getSbn());
        if (b3 || groupChild != groupChild2) {
            mIsUpdatingUnchangedGroup = false;
        }
        this.mIsUpdatingUnchangedGroup = mIsUpdatingUnchangedGroup;
        if (this.mGroupMap.get(this.getGroupKey(notificationEntry.getKey(), key)) != null) {
            this.onEntryRemovedInternal(notificationEntry, key, b, b2);
        }
        this.onEntryAddedInternal(notificationEntry);
        this.mIsUpdatingUnchangedGroup = false;
        if (this.isIsolated(notificationEntry.getSbn().getKey())) {
            this.mIsolatedEntries.put(notificationEntry.getKey(), notificationEntry.getSbn());
            if (b3) {
                this.updateSuppression(this.mGroupMap.get(key));
                this.updateSuppression(this.mGroupMap.get(groupKey));
            }
        }
        else if (!groupChild && groupChild2) {
            this.onEntryBecomingChild(notificationEntry);
        }
    }
    
    @Override
    public void onHeadsUpStateChanged(final NotificationEntry notificationEntry, final boolean b) {
        this.updateIsolation(notificationEntry);
    }
    
    @Override
    public void onStateChanged(final int statusBarState) {
        this.setStatusBarState(statusBarState);
    }
    
    public void setGroupExpanded(final StatusBarNotification statusBarNotification, final boolean b) {
        final NotificationGroup notificationGroup = this.mGroupMap.get(this.getGroupKey(statusBarNotification));
        if (notificationGroup == null) {
            return;
        }
        this.setGroupExpanded(notificationGroup, b);
    }
    
    public void setHeadsUpManager(final HeadsUpManager mHeadsUpManager) {
        this.mHeadsUpManager = mHeadsUpManager;
    }
    
    public boolean toggleGroupExpansion(final StatusBarNotification statusBarNotification) {
        final NotificationGroup notificationGroup = this.mGroupMap.get(this.getGroupKey(statusBarNotification));
        if (notificationGroup == null) {
            return false;
        }
        this.setGroupExpanded(notificationGroup, notificationGroup.expanded ^ true);
        return notificationGroup.expanded;
    }
    
    public void updateIsolation(final NotificationEntry notificationEntry) {
        final boolean isolated = this.isIsolated(notificationEntry.getSbn().getKey());
        if (this.shouldIsolate(notificationEntry)) {
            if (!isolated) {
                this.isolateNotification(notificationEntry);
            }
        }
        else if (isolated) {
            this.stopIsolatingNotification(notificationEntry);
        }
    }
    
    public void updateSuppression(final NotificationEntry notificationEntry) {
        final NotificationGroup notificationGroup = this.mGroupMap.get(this.getGroupKey(notificationEntry.getSbn()));
        if (notificationGroup != null) {
            this.updateSuppression(notificationGroup);
        }
    }
    
    public static class NotificationGroup
    {
        public final HashMap<String, NotificationEntry> children;
        public boolean expanded;
        public NotificationEntry summary;
        public boolean suppressed;
        
        public NotificationGroup() {
            this.children = new HashMap<String, NotificationEntry>();
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("    summary:\n      ");
            final NotificationEntry summary = this.summary;
            Object sbn;
            if (summary != null) {
                sbn = summary.getSbn();
            }
            else {
                sbn = "null";
            }
            sb.append(sbn);
            final NotificationEntry summary2 = this.summary;
            String stackTraceString;
            if (summary2 != null && summary2.getDebugThrowable() != null) {
                stackTraceString = Log.getStackTraceString(this.summary.getDebugThrowable());
            }
            else {
                stackTraceString = "";
            }
            sb.append(stackTraceString);
            final String string = sb.toString();
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(string);
            sb2.append("\n    children size: ");
            sb2.append(this.children.size());
            String s = sb2.toString();
            for (final NotificationEntry notificationEntry : this.children.values()) {
                final StringBuilder sb3 = new StringBuilder();
                sb3.append(s);
                sb3.append("\n      ");
                sb3.append(notificationEntry.getSbn());
                String stackTraceString2;
                if (notificationEntry.getDebugThrowable() != null) {
                    stackTraceString2 = Log.getStackTraceString(notificationEntry.getDebugThrowable());
                }
                else {
                    stackTraceString2 = "";
                }
                sb3.append(stackTraceString2);
                s = sb3.toString();
            }
            final StringBuilder sb4 = new StringBuilder();
            sb4.append(s);
            sb4.append("\n    summary suppressed: ");
            sb4.append(this.suppressed);
            return sb4.toString();
        }
    }
    
    public interface OnGroupChangeListener
    {
        default void onGroupCreated(final NotificationGroup notificationGroup, final String s) {
        }
        
        default void onGroupCreatedFromChildren(final NotificationGroup notificationGroup) {
        }
        
        default void onGroupExpansionChanged(final ExpandableNotificationRow expandableNotificationRow, final boolean b) {
        }
        
        default void onGroupRemoved(final NotificationGroup notificationGroup, final String s) {
        }
        
        default void onGroupSuppressionChanged(final NotificationGroup notificationGroup, final boolean b) {
        }
        
        default void onGroupsChanged() {
        }
    }
}
