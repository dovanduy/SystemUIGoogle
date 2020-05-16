// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection;

import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.Pluggable;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.Iterator;
import android.util.Pair;
import com.android.systemui.util.Assert;
import java.util.Collections;
import android.util.ArrayMap;
import java.util.ArrayList;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.util.time.SystemClock;
import com.android.systemui.statusbar.notification.collection.notifcollection.CollectionReadyForBuildListener;
import com.android.systemui.statusbar.notification.collection.listbuilder.PipelineState;
import com.android.systemui.statusbar.notification.collection.listbuilder.OnBeforeTransformGroupsListener;
import com.android.systemui.statusbar.notification.collection.listbuilder.OnBeforeSortListener;
import com.android.systemui.statusbar.notification.collection.listbuilder.OnBeforeRenderListListener;
import com.android.systemui.statusbar.notification.collection.listbuilder.OnBeforeFinalizeFilterListener;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifPromoter;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifFilter;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifComparator;
import java.util.List;
import com.android.systemui.statusbar.notification.collection.listbuilder.ShadeListBuilderLogger;
import java.util.Map;
import java.util.Collection;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifSection;
import java.util.Comparator;
import com.android.systemui.Dumpable;

public class ShadeListBuilder implements Dumpable
{
    private static final Comparator<NotificationEntry> sChildComparator;
    private static final NotifSection sDefaultSection;
    private Collection<NotificationEntry> mAllEntries;
    private final Map<String, GroupEntry> mGroups;
    private int mIterationCount;
    private final ShadeListBuilderLogger mLogger;
    private List<ListEntry> mNewNotifList;
    private final List<NotifComparator> mNotifComparators;
    private final List<NotifFilter> mNotifFinalizeFilters;
    private List<ListEntry> mNotifList;
    private final List<NotifFilter> mNotifPreGroupFilters;
    private final List<NotifPromoter> mNotifPromoters;
    private final List<NotifSection> mNotifSections;
    private final List<OnBeforeFinalizeFilterListener> mOnBeforeFinalizeFilterListeners;
    private final List<OnBeforeRenderListListener> mOnBeforeRenderListListeners;
    private final List<OnBeforeSortListener> mOnBeforeSortListeners;
    private final List<OnBeforeTransformGroupsListener> mOnBeforeTransformGroupsListeners;
    private OnRenderListListener mOnRenderListListener;
    private final PipelineState mPipelineState;
    private List<ListEntry> mReadOnlyNewNotifList;
    private List<ListEntry> mReadOnlyNotifList;
    private final CollectionReadyForBuildListener mReadyForBuildListener;
    private final SystemClock mSystemClock;
    private final Comparator<ListEntry> mTopLevelComparator;
    
    static {
        sChildComparator = (Comparator)_$$Lambda$ShadeListBuilder$c6onOLMSwF5woQj_UCc8sv1YwJM.INSTANCE;
        sDefaultSection = new NotifSection() {
            @Override
            public boolean isInSection(final ListEntry listEntry) {
                return true;
            }
        };
    }
    
    public ShadeListBuilder(final SystemClock mSystemClock, final ShadeListBuilderLogger mLogger, final DumpManager dumpManager) {
        this.mNotifList = new ArrayList<ListEntry>();
        this.mNewNotifList = new ArrayList<ListEntry>();
        this.mPipelineState = new PipelineState();
        this.mGroups = (Map<String, GroupEntry>)new ArrayMap();
        this.mAllEntries = (Collection<NotificationEntry>)Collections.emptyList();
        this.mIterationCount = 0;
        this.mNotifPreGroupFilters = new ArrayList<NotifFilter>();
        this.mNotifPromoters = new ArrayList<NotifPromoter>();
        this.mNotifFinalizeFilters = new ArrayList<NotifFilter>();
        this.mNotifComparators = new ArrayList<NotifComparator>();
        this.mNotifSections = new ArrayList<NotifSection>();
        this.mOnBeforeTransformGroupsListeners = new ArrayList<OnBeforeTransformGroupsListener>();
        this.mOnBeforeSortListeners = new ArrayList<OnBeforeSortListener>();
        this.mOnBeforeFinalizeFilterListeners = new ArrayList<OnBeforeFinalizeFilterListener>();
        this.mOnBeforeRenderListListeners = new ArrayList<OnBeforeRenderListListener>();
        this.mReadOnlyNotifList = Collections.unmodifiableList((List<? extends ListEntry>)this.mNotifList);
        this.mReadOnlyNewNotifList = Collections.unmodifiableList((List<? extends ListEntry>)this.mNewNotifList);
        this.mReadyForBuildListener = new CollectionReadyForBuildListener() {
            @Override
            public void onBuildList(final Collection<NotificationEntry> collection) {
                Assert.isMainThread();
                ShadeListBuilder.this.mPipelineState.requireIsBefore(1);
                ShadeListBuilder.this.mLogger.logOnBuildList();
                ShadeListBuilder.this.mAllEntries = collection;
                ShadeListBuilder.this.buildList();
            }
        };
        this.mTopLevelComparator = (Comparator<ListEntry>)new _$$Lambda$ShadeListBuilder$j4Y9_Xdxb2bsigQJC_JntCQwmx4(this);
        Assert.isMainThread();
        this.mSystemClock = mSystemClock;
        this.mLogger = mLogger;
        dumpManager.registerDumpable("NotifListBuilderImpl", this);
    }
    
    private void annulAddition(final ListEntry listEntry) {
        listEntry.setParent(null);
        listEntry.getAttachState().setSectionIndex(-1);
        listEntry.getAttachState().setSection(null);
        listEntry.getAttachState().setPromoter(null);
        if (listEntry.mFirstAddedIteration == this.mIterationCount) {
            listEntry.mFirstAddedIteration = -1;
        }
    }
    
    private void annulAddition(final ListEntry listEntry, final List<ListEntry> list) {
        if (listEntry.getParent() == null || listEntry.mFirstAddedIteration == -1) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Cannot nullify addition of ");
            sb.append(listEntry.getKey());
            sb.append(": no such addition. (");
            sb.append(listEntry.getParent());
            sb.append(" ");
            sb.append(listEntry.mFirstAddedIteration);
            sb.append(")");
            throw new IllegalStateException(sb.toString());
        }
        if (listEntry.getParent() == GroupEntry.ROOT_ENTRY && list.contains(listEntry)) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Cannot nullify addition of ");
            sb2.append(listEntry.getKey());
            sb2.append(": it's still in the shade list.");
            throw new IllegalStateException(sb2.toString());
        }
        if (listEntry instanceof GroupEntry) {
            final GroupEntry groupEntry = (GroupEntry)listEntry;
            if (groupEntry.getSummary() != null) {
                final StringBuilder sb3 = new StringBuilder();
                sb3.append("Cannot nullify group ");
                sb3.append(groupEntry.getKey());
                sb3.append(": summary is not null");
                throw new IllegalStateException(sb3.toString());
            }
            if (!groupEntry.getChildren().isEmpty()) {
                final StringBuilder sb4 = new StringBuilder();
                sb4.append("Cannot nullify group ");
                sb4.append(groupEntry.getKey());
                sb4.append(": still has children");
                throw new IllegalStateException(sb4.toString());
            }
        }
        else if (listEntry instanceof NotificationEntry) {
            if (listEntry == listEntry.getParent().getSummary() || listEntry.getParent().getChildren().contains(listEntry)) {
                final StringBuilder sb5 = new StringBuilder();
                sb5.append("Cannot nullify addition of child ");
                sb5.append(listEntry.getKey());
                sb5.append(": it's still attached to its parent.");
                throw new IllegalStateException(sb5.toString());
            }
        }
        this.annulAddition(listEntry);
    }
    
    private boolean applyFilters(final NotificationEntry notificationEntry, final long n, final List<NotifFilter> list) {
        final NotifFilter rejectingFilter = findRejectingFilter(notificationEntry, n, list);
        notificationEntry.getAttachState().setExcludingFilter(rejectingFilter);
        return rejectingFilter != null;
    }
    
    private void applyNewNotifList() {
        this.mNotifList.clear();
        final List<ListEntry> mNotifList = this.mNotifList;
        this.mNotifList = this.mNewNotifList;
        this.mNewNotifList = mNotifList;
        final List<ListEntry> mReadOnlyNotifList = this.mReadOnlyNotifList;
        this.mReadOnlyNotifList = this.mReadOnlyNewNotifList;
        this.mReadOnlyNewNotifList = mReadOnlyNotifList;
    }
    
    private Pair<NotifSection, Integer> applySections(final ListEntry listEntry) {
        final Pair<NotifSection, Integer> section = this.findSection(listEntry);
        final NotifSection section2 = (NotifSection)section.first;
        final Integer n = (Integer)section.second;
        listEntry.getAttachState().setSection(section2);
        listEntry.getAttachState().setSectionIndex(n);
        return section;
    }
    
    private boolean applyTopLevelPromoters(final NotificationEntry notificationEntry) {
        final NotifPromoter promoter = this.findPromoter(notificationEntry);
        notificationEntry.getAttachState().setPromoter(promoter);
        return promoter != null;
    }
    
    private void buildList() {
        this.mPipelineState.requireIsBefore(1);
        this.mPipelineState.setState(1);
        this.mPipelineState.incrementTo(2);
        this.resetNotifs();
        this.mPipelineState.incrementTo(3);
        this.filterNotifs(this.mAllEntries, this.mNotifList, this.mNotifPreGroupFilters);
        this.mPipelineState.incrementTo(4);
        this.groupNotifs(this.mNotifList, this.mNewNotifList);
        this.applyNewNotifList();
        this.pruneIncompleteGroups(this.mNotifList);
        this.dispatchOnBeforeTransformGroups(this.mReadOnlyNotifList);
        this.mPipelineState.incrementTo(5);
        this.promoteNotifs(this.mNotifList);
        this.pruneIncompleteGroups(this.mNotifList);
        this.dispatchOnBeforeSort(this.mReadOnlyNotifList);
        this.mPipelineState.incrementTo(6);
        this.sortList();
        this.dispatchOnBeforeFinalizeFilter(this.mReadOnlyNotifList);
        this.mPipelineState.incrementTo(7);
        this.filterNotifs(this.mNotifList, this.mNewNotifList, this.mNotifFinalizeFilters);
        this.applyNewNotifList();
        this.pruneIncompleteGroups(this.mNotifList);
        this.mPipelineState.incrementTo(8);
        this.logChanges();
        this.freeEmptyGroups();
        this.dispatchOnBeforeRenderList(this.mReadOnlyNotifList);
        final OnRenderListListener mOnRenderListListener = this.mOnRenderListListener;
        if (mOnRenderListListener != null) {
            mOnRenderListListener.onRenderList(this.mReadOnlyNotifList);
        }
        this.mLogger.logEndBuildList(this.mIterationCount, this.mReadOnlyNotifList.size(), countChildren(this.mReadOnlyNotifList));
        if (this.mIterationCount % 10 == 0) {
            this.mLogger.logFinalList(this.mNotifList);
        }
        this.mPipelineState.setState(0);
        ++this.mIterationCount;
    }
    
    private static int countChildren(final List<ListEntry> list) {
        int i = 0;
        int n = 0;
        while (i < list.size()) {
            final ListEntry listEntry = list.get(i);
            int n2 = n;
            if (listEntry instanceof GroupEntry) {
                n2 = n + ((GroupEntry)listEntry).getChildren().size();
            }
            ++i;
            n = n2;
        }
        return n;
    }
    
    private void dispatchOnBeforeFinalizeFilter(final List<ListEntry> list) {
        for (int i = 0; i < this.mOnBeforeFinalizeFilterListeners.size(); ++i) {
            this.mOnBeforeFinalizeFilterListeners.get(i).onBeforeFinalizeFilter(list);
        }
    }
    
    private void dispatchOnBeforeRenderList(final List<ListEntry> list) {
        for (int i = 0; i < this.mOnBeforeRenderListListeners.size(); ++i) {
            this.mOnBeforeRenderListListeners.get(i).onBeforeRenderList(list);
        }
    }
    
    private void dispatchOnBeforeSort(final List<ListEntry> list) {
        for (int i = 0; i < this.mOnBeforeSortListeners.size(); ++i) {
            this.mOnBeforeSortListeners.get(i).onBeforeSort(list);
        }
    }
    
    private void dispatchOnBeforeTransformGroups(final List<ListEntry> list) {
        for (int i = 0; i < this.mOnBeforeTransformGroupsListeners.size(); ++i) {
            this.mOnBeforeTransformGroupsListeners.get(i).onBeforeTransformGroups(list);
        }
    }
    
    private void filterNotifs(final Collection<? extends ListEntry> collection, final List<ListEntry> list, final List<NotifFilter> list2) {
        final long uptimeMillis = this.mSystemClock.uptimeMillis();
        for (final ListEntry listEntry : collection) {
            if (listEntry instanceof GroupEntry) {
                final GroupEntry groupEntry = (GroupEntry)listEntry;
                final NotificationEntry representativeEntry = groupEntry.getRepresentativeEntry();
                if (this.applyFilters(representativeEntry, uptimeMillis, list2)) {
                    groupEntry.setSummary(null);
                    this.annulAddition(representativeEntry);
                }
                final List<NotificationEntry> rawChildren = groupEntry.getRawChildren();
                for (int i = rawChildren.size() - 1; i >= 0; --i) {
                    final NotificationEntry notificationEntry = rawChildren.get(i);
                    if (this.applyFilters(notificationEntry, uptimeMillis, list2)) {
                        rawChildren.remove(notificationEntry);
                        this.annulAddition(notificationEntry);
                    }
                }
                list.add(groupEntry);
            }
            else if (this.applyFilters((NotificationEntry)listEntry, uptimeMillis, list2)) {
                this.annulAddition(listEntry);
            }
            else {
                list.add(listEntry);
            }
        }
    }
    
    private NotifPromoter findPromoter(final NotificationEntry notificationEntry) {
        for (int i = 0; i < this.mNotifPromoters.size(); ++i) {
            final NotifPromoter notifPromoter = this.mNotifPromoters.get(i);
            if (notifPromoter.shouldPromoteToTopLevel(notificationEntry)) {
                return notifPromoter;
            }
        }
        return null;
    }
    
    private static NotifFilter findRejectingFilter(final NotificationEntry notificationEntry, final long n, final List<NotifFilter> list) {
        for (int size = list.size(), i = 0; i < size; ++i) {
            final NotifFilter notifFilter = list.get(i);
            if (notifFilter.shouldFilterOut(notificationEntry, n)) {
                return notifFilter;
            }
        }
        return null;
    }
    
    private Pair<NotifSection, Integer> findSection(final ListEntry listEntry) {
        for (int i = 0; i < this.mNotifSections.size(); ++i) {
            final NotifSection notifSection = this.mNotifSections.get(i);
            if (notifSection.isInSection(listEntry)) {
                return (Pair<NotifSection, Integer>)new Pair((Object)notifSection, (Object)i);
            }
        }
        return (Pair<NotifSection, Integer>)new Pair((Object)ShadeListBuilder.sDefaultSection, (Object)this.mNotifSections.size());
    }
    
    private void freeEmptyGroups() {
        this.mGroups.values().removeIf((Predicate<? super GroupEntry>)_$$Lambda$ShadeListBuilder$pkfdgVYB9WxpGP4Dl92u_QCynaw.INSTANCE);
    }
    
    private void groupNotifs(final List<ListEntry> list, final List<ListEntry> list2) {
        for (final NotificationEntry notificationEntry : list) {
            if (notificationEntry.getSbn().isGroup()) {
                final String groupKey = notificationEntry.getSbn().getGroupKey();
                GroupEntry parent;
                if ((parent = this.mGroups.get(groupKey)) == null) {
                    parent = new GroupEntry(groupKey);
                    parent.mFirstAddedIteration = this.mIterationCount;
                    this.mGroups.put(groupKey, parent);
                }
                if (parent.getParent() == null) {
                    parent.setParent(GroupEntry.ROOT_ENTRY);
                    list2.add(parent);
                }
                notificationEntry.setParent(parent);
                if (notificationEntry.getSbn().getNotification().isGroupSummary()) {
                    final NotificationEntry summary = parent.getSummary();
                    if (summary == null) {
                        parent.setSummary(notificationEntry);
                    }
                    else {
                        this.mLogger.logDuplicateSummary(this.mIterationCount, parent.getKey(), summary.getKey(), notificationEntry.getKey());
                        if (notificationEntry.getSbn().getPostTime() > summary.getSbn().getPostTime()) {
                            parent.setSummary(notificationEntry);
                            this.annulAddition(summary, list2);
                        }
                        else {
                            this.annulAddition(notificationEntry, list2);
                        }
                    }
                }
                else {
                    parent.addChild(notificationEntry);
                }
            }
            else {
                final String key = notificationEntry.getKey();
                if (this.mGroups.containsKey(key)) {
                    this.mLogger.logDuplicateTopLevelKey(this.mIterationCount, key);
                }
                else {
                    notificationEntry.setParent(GroupEntry.ROOT_ENTRY);
                    list2.add(notificationEntry);
                }
            }
        }
    }
    
    private void logAttachStateChanges(final ListEntry listEntry) {
        final ListAttachState attachState = listEntry.getAttachState();
        final ListAttachState previousAttachState = listEntry.getPreviousAttachState();
        if (!Objects.equals(attachState, previousAttachState)) {
            this.mLogger.logEntryAttachStateChanged(this.mIterationCount, listEntry.getKey(), previousAttachState.getParent(), attachState.getParent());
            if (attachState.getParent() != previousAttachState.getParent()) {
                this.mLogger.logParentChanged(this.mIterationCount, previousAttachState.getParent(), attachState.getParent());
            }
            if (attachState.getExcludingFilter() != previousAttachState.getExcludingFilter()) {
                this.mLogger.logFilterChanged(this.mIterationCount, previousAttachState.getExcludingFilter(), attachState.getExcludingFilter());
            }
            final boolean b = attachState.getParent() == null && previousAttachState.getParent() != null;
            if (!b && attachState.getPromoter() != previousAttachState.getPromoter()) {
                this.mLogger.logPromoterChanged(this.mIterationCount, previousAttachState.getPromoter(), attachState.getPromoter());
            }
            if (!b && attachState.getSection() != previousAttachState.getSection()) {
                this.mLogger.logSectionChanged(this.mIterationCount, previousAttachState.getSection(), previousAttachState.getSectionIndex(), attachState.getSection(), attachState.getSectionIndex());
            }
        }
    }
    
    private void logChanges() {
        final Iterator<NotificationEntry> iterator = this.mAllEntries.iterator();
        while (iterator.hasNext()) {
            this.logAttachStateChanges(iterator.next());
        }
        final Iterator<GroupEntry> iterator2 = this.mGroups.values().iterator();
        while (iterator2.hasNext()) {
            this.logAttachStateChanges(iterator2.next());
        }
    }
    
    private void onFinalizeFilterInvalidated(final NotifFilter notifFilter) {
        Assert.isMainThread();
        this.mLogger.logFinalizeFilterInvalidated(notifFilter.getName(), this.mPipelineState.getState());
        this.rebuildListIfBefore(7);
    }
    
    private void onNotifSectionInvalidated(final NotifSection notifSection) {
        Assert.isMainThread();
        this.mLogger.logNotifSectionInvalidated(notifSection.getName(), this.mPipelineState.getState());
        this.rebuildListIfBefore(6);
    }
    
    private void onPreGroupFilterInvalidated(final NotifFilter notifFilter) {
        Assert.isMainThread();
        this.mLogger.logPreGroupFilterInvalidated(notifFilter.getName(), this.mPipelineState.getState());
        this.rebuildListIfBefore(3);
    }
    
    private void onPromoterInvalidated(final NotifPromoter notifPromoter) {
        Assert.isMainThread();
        this.mLogger.logPromoterInvalidated(notifPromoter.getName(), this.mPipelineState.getState());
        this.rebuildListIfBefore(5);
    }
    
    private void promoteNotifs(final List<ListEntry> list) {
        for (int i = 0; i < list.size(); ++i) {
            final ListEntry listEntry = list.get(i);
            if (listEntry instanceof GroupEntry) {
                ((GroupEntry)listEntry).getRawChildren().removeIf(new _$$Lambda$ShadeListBuilder$ePmhZ1cn_R_Hisgrq179QhMPgfM(this, list));
            }
        }
    }
    
    private void pruneIncompleteGroups(final List<ListEntry> list) {
        int n;
        for (int i = 0; i < list.size(); i = n + 1) {
            final NotificationEntry notificationEntry = list.get(i);
            n = i;
            if (notificationEntry instanceof GroupEntry) {
                final GroupEntry groupEntry = (GroupEntry)notificationEntry;
                final List<NotificationEntry> rawChildren = groupEntry.getRawChildren();
                if (groupEntry.getSummary() != null && rawChildren.size() == 0) {
                    list.remove(i);
                    n = i - 1;
                    final NotificationEntry summary = groupEntry.getSummary();
                    summary.setParent(GroupEntry.ROOT_ENTRY);
                    list.add(summary);
                    groupEntry.setSummary(null);
                    this.annulAddition(groupEntry, list);
                }
                else {
                    if (groupEntry.getSummary() != null) {
                        n = i;
                        if (rawChildren.size() >= 2) {
                            continue;
                        }
                    }
                    list.remove(i);
                    --i;
                    if (groupEntry.getSummary() != null) {
                        final NotificationEntry summary2 = groupEntry.getSummary();
                        groupEntry.setSummary(null);
                        this.annulAddition(summary2, list);
                    }
                    for (int j = 0; j < rawChildren.size(); ++j) {
                        final NotificationEntry notificationEntry2 = rawChildren.get(j);
                        notificationEntry2.setParent(GroupEntry.ROOT_ENTRY);
                        list.add(notificationEntry2);
                    }
                    rawChildren.clear();
                    this.annulAddition(groupEntry, list);
                    n = i;
                }
            }
        }
    }
    
    private void rebuildListIfBefore(final int n) {
        this.mPipelineState.requireIsBefore(n);
        if (this.mPipelineState.is(0)) {
            this.buildList();
        }
    }
    
    private void resetNotifs() {
        for (final GroupEntry groupEntry : this.mGroups.values()) {
            groupEntry.beginNewAttachState();
            groupEntry.clearChildren();
            groupEntry.setSummary(null);
        }
        for (final NotificationEntry notificationEntry : this.mAllEntries) {
            notificationEntry.beginNewAttachState();
            if (notificationEntry.mFirstAddedIteration == -1) {
                notificationEntry.mFirstAddedIteration = this.mIterationCount;
            }
        }
        this.mNotifList.clear();
    }
    
    private void sortList() {
        for (final ListEntry listEntry : this.mNotifList) {
            final Pair<NotifSection, Integer> applySections = this.applySections(listEntry);
            if (listEntry instanceof GroupEntry) {
                final GroupEntry groupEntry = (GroupEntry)listEntry;
                for (final NotificationEntry notificationEntry : groupEntry.getChildren()) {
                    notificationEntry.getAttachState().setSection((NotifSection)applySections.first);
                    notificationEntry.getAttachState().setSectionIndex((int)applySections.second);
                }
                groupEntry.sortChildren(ShadeListBuilder.sChildComparator);
            }
        }
        this.mNotifList.sort(this.mTopLevelComparator);
    }
    
    void addFinalizeFilter(final NotifFilter notifFilter) {
        Assert.isMainThread();
        this.mPipelineState.requireState(0);
        this.mNotifFinalizeFilters.add(notifFilter);
        notifFilter.setInvalidationListener(new _$$Lambda$ShadeListBuilder$xeAx9GATmY7ZgJZ0F6oEQlc0G_0(this));
    }
    
    void addOnBeforeFinalizeFilterListener(final OnBeforeFinalizeFilterListener onBeforeFinalizeFilterListener) {
        Assert.isMainThread();
        this.mPipelineState.requireState(0);
        this.mOnBeforeFinalizeFilterListeners.add(onBeforeFinalizeFilterListener);
    }
    
    void addPreGroupFilter(final NotifFilter notifFilter) {
        Assert.isMainThread();
        this.mPipelineState.requireState(0);
        this.mNotifPreGroupFilters.add(notifFilter);
        notifFilter.setInvalidationListener(new _$$Lambda$ShadeListBuilder$nY0ibCyaS_Pniz4LEX1W2bWrRcs(this));
    }
    
    void addPromoter(final NotifPromoter notifPromoter) {
        Assert.isMainThread();
        this.mPipelineState.requireState(0);
        this.mNotifPromoters.add(notifPromoter);
        notifPromoter.setInvalidationListener(new _$$Lambda$ShadeListBuilder$WhP4dzR4yYnVTR1LdzWTnz4ov9k(this));
    }
    
    public void attach(final NotifCollection collection) {
        Assert.isMainThread();
        collection.setBuildListener(this.mReadyForBuildListener);
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("\tNotifListBuilderImpl shade notifications:");
        if (this.getShadeList().size() == 0) {
            printWriter.println("\t\t None");
        }
        printWriter.println(ListDumper.dumpTree(this.getShadeList(), true, "\t\t"));
    }
    
    List<ListEntry> getShadeList() {
        Assert.isMainThread();
        return this.mReadOnlyNotifList;
    }
    
    public void setOnRenderListListener(final OnRenderListListener mOnRenderListListener) {
        Assert.isMainThread();
        this.mPipelineState.requireState(0);
        this.mOnRenderListListener = mOnRenderListListener;
    }
    
    void setSections(final List<NotifSection> list) {
        Assert.isMainThread();
        this.mPipelineState.requireState(0);
        this.mNotifSections.clear();
        for (final NotifSection notifSection : list) {
            this.mNotifSections.add(notifSection);
            notifSection.setInvalidationListener(new _$$Lambda$ShadeListBuilder$bhojRXQ6I_zMsuyeOmu4rRbLGws(this));
        }
    }
    
    public interface OnRenderListListener
    {
        void onRenderList(final List<ListEntry> p0);
    }
}
