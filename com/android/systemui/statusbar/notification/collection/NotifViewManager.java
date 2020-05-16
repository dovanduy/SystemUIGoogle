// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection;

import com.android.systemui.util.Assert;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import kotlin.ranges.RangesKt;
import java.util.ArrayList;
import android.view.ViewGroup;
import kotlin.TypeCastException;
import kotlin.jvm.functions.Function1;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import android.view.ViewParent;
import android.view.View;
import java.util.Iterator;
import com.android.systemui.statusbar.notification.stack.NotificationListItem;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.FeatureFlags;

public final class NotifViewManager
{
    private final FeatureFlags featureFlags;
    private SimpleNotificationListContainer listContainer;
    private final NotifViewBarn rowRegistry;
    private final VisualStabilityManager stabilityManager;
    
    public NotifViewManager(final NotifViewBarn rowRegistry, final VisualStabilityManager stabilityManager, final FeatureFlags featureFlags) {
        Intrinsics.checkParameterIsNotNull(rowRegistry, "rowRegistry");
        Intrinsics.checkParameterIsNotNull(stabilityManager, "stabilityManager");
        Intrinsics.checkParameterIsNotNull(featureFlags, "featureFlags");
        this.rowRegistry = rowRegistry;
        this.stabilityManager = stabilityManager;
        this.featureFlags = featureFlags;
        CollectionsKt.emptyList();
    }
    
    private final void attachRows(final List<? extends ListEntry> list) {
        final Iterator<ListEntry> iterator = list.iterator();
    Label_0007:
        while (true) {
            int n = 0;
            Label_0481: {
                Label_0476: {
                    Label_0439: {
                        Object class1 = null;
                        NotificationListItem requireView2 = null;
                        Block_8: {
                            while (true) {
                                final boolean hasNext = iterator.hasNext();
                                class1 = null;
                                if (!hasNext) {
                                    break Label_0481;
                                }
                                final ListEntry listEntry = iterator.next();
                                final NotifViewBarn rowRegistry = this.rowRegistry;
                                final NotificationEntry representativeEntry = listEntry.getRepresentativeEntry();
                                if (representativeEntry == null) {
                                    break Label_0476;
                                }
                                Intrinsics.checkExpressionValueIsNotNull(representativeEntry, "entry.representativeEntry!!");
                                final NotificationListItem requireView = rowRegistry.requireView(representativeEntry);
                                final View view = requireView.getView();
                                Intrinsics.checkExpressionValueIsNotNull(view, "listItem.view");
                                if (view.getParent() == null) {
                                    final SimpleNotificationListContainer listContainer = this.listContainer;
                                    if (listContainer == null) {
                                        break;
                                    }
                                    listContainer.addListItem(requireView);
                                    this.stabilityManager.notifyViewAddition(requireView.getView());
                                }
                                if (!(listEntry instanceof GroupEntry)) {
                                    continue;
                                }
                                final List<NotificationEntry> children = ((GroupEntry)listEntry).getChildren();
                                Intrinsics.checkExpressionValueIsNotNull(children, "entry.children");
                                final Iterator<Object> iterator2 = children.iterator();
                                int n2 = 0;
                                while (iterator2.hasNext()) {
                                    final NotificationEntry notificationEntry = iterator2.next();
                                    final NotifViewBarn rowRegistry2 = this.rowRegistry;
                                    Intrinsics.checkExpressionValueIsNotNull(notificationEntry, "childEntry");
                                    requireView2 = rowRegistry2.requireView(notificationEntry);
                                    Label_0433: {
                                        if (requireView.getNotificationChildren() != null) {
                                            final List<? extends NotificationListItem> notificationChildren = requireView.getNotificationChildren();
                                            Intrinsics.checkExpressionValueIsNotNull(notificationChildren, "listItem.notificationChildren");
                                            if (CollectionsKt.contains(notificationChildren, requireView2)) {
                                                break Label_0433;
                                            }
                                        }
                                        final View view2 = requireView2.getView();
                                        Intrinsics.checkExpressionValueIsNotNull(view2, "childListItem.view");
                                        if (view2.getParent() != null) {
                                            break Block_8;
                                        }
                                        requireView.addChildNotification(requireView2, n2);
                                        this.stabilityManager.notifyViewAddition(requireView2.getView());
                                        final SimpleNotificationListContainer listContainer2 = this.listContainer;
                                        if (listContainer2 == null) {
                                            break Label_0439;
                                        }
                                        final View view3 = requireView2.getView();
                                        Intrinsics.checkExpressionValueIsNotNull(view3, "childListItem.view");
                                        listContainer2.notifyGroupChildAdded(view3);
                                    }
                                    ++n2;
                                }
                                if (n == 0 && !requireView.applyChildOrder(this.getChildListFromParent(listEntry), this.stabilityManager, null)) {
                                    continue Label_0007;
                                }
                                n = 1;
                            }
                            Intrinsics.throwUninitializedPropertyAccessException("listContainer");
                            throw null;
                        }
                        final StringBuilder sb = new StringBuilder();
                        sb.append("trying to add a notification child that already has a parent. class: ");
                        final View view4 = requireView2.getView();
                        Intrinsics.checkExpressionValueIsNotNull(view4, "childListItem.view");
                        final ViewParent parent = view4.getParent();
                        if (parent != null) {
                            class1 = parent.getClass();
                        }
                        sb.append(class1);
                        sb.append(' ');
                        sb.append("\n child: ");
                        sb.append(requireView2.getView());
                        throw new IllegalStateException(sb.toString());
                    }
                    Intrinsics.throwUninitializedPropertyAccessException("listContainer");
                    throw null;
                }
                Intrinsics.throwNpe();
                throw null;
            }
            if (n != 0) {
                final SimpleNotificationListContainer listContainer3 = this.listContainer;
                if (listContainer3 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("listContainer");
                    throw null;
                }
                listContainer3.generateChildOrderChangedEvent();
            }
        }
    }
    
    private final void detachRows(final List<? extends ListEntry> list) {
        final SimpleNotificationListContainer listContainer = this.listContainer;
        if (listContainer != null) {
            for (final NotificationListItem notificationListItem : SequencesKt.filter((Sequence<? extends NotificationListItem>)this.getListItems(listContainer), (Function1<? super NotificationListItem, Boolean>)NotifViewManager$detachRows.NotifViewManager$detachRows$1.INSTANCE)) {
                final NotificationEntry entry = notificationListItem.getEntry();
                Intrinsics.checkExpressionValueIsNotNull(entry, "listItem.entry");
                final boolean b = Intrinsics.areEqual(entry.getParent(), GroupEntry.ROOT_ENTRY) ^ true;
                boolean b2 = false;
                Label_0112: {
                    if (b) {
                        final NotificationEntry entry2 = notificationListItem.getEntry();
                        Intrinsics.checkExpressionValueIsNotNull(entry2, "listItem.entry");
                        if (entry2.getParent() != null) {
                            b2 = true;
                            break Label_0112;
                        }
                    }
                    b2 = false;
                }
                final int index = list.indexOf(notificationListItem.getEntry());
                if (b) {
                    if (notificationListItem.isSummaryWithChildren()) {
                        notificationListItem.removeAllChildren();
                    }
                    if (b2) {
                        final SimpleNotificationListContainer listContainer2 = this.listContainer;
                        if (listContainer2 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("listContainer");
                            throw null;
                        }
                        listContainer2.setChildTransferInProgress(true);
                    }
                    final SimpleNotificationListContainer listContainer3 = this.listContainer;
                    if (listContainer3 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("listContainer");
                        throw null;
                    }
                    listContainer3.removeListItem(notificationListItem);
                    final SimpleNotificationListContainer listContainer4 = this.listContainer;
                    if (listContainer4 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("listContainer");
                        throw null;
                    }
                    listContainer4.setChildTransferInProgress(false);
                }
                else {
                    if (!(list.get(index) instanceof GroupEntry)) {
                        continue;
                    }
                    final ListEntry value = (ListEntry)list.get(index);
                    if (value == null) {
                        throw new TypeCastException("null cannot be cast to non-null type com.android.systemui.statusbar.notification.collection.GroupEntry");
                    }
                    final List<NotificationEntry> children = ((GroupEntry)value).getChildren();
                    Intrinsics.checkExpressionValueIsNotNull(children, "(entries[idx] as GroupEntry).children");
                    final List<? extends NotificationListItem> notificationChildren = notificationListItem.getNotificationChildren();
                    if (notificationChildren == null) {
                        continue;
                    }
                    for (final NotificationListItem notificationListItem2 : notificationChildren) {
                        Intrinsics.checkExpressionValueIsNotNull(notificationListItem2, "listChild");
                        if (!children.contains(notificationListItem2.getEntry())) {
                            notificationListItem.removeChildNotification(notificationListItem2);
                            final SimpleNotificationListContainer listContainer5 = this.listContainer;
                            if (listContainer5 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("listContainer");
                                throw null;
                            }
                            final View view = notificationListItem2.getView();
                            Intrinsics.checkExpressionValueIsNotNull(view, "listChild.view");
                            final View view2 = notificationListItem2.getView();
                            Intrinsics.checkExpressionValueIsNotNull(view2, "listChild.view");
                            final ViewParent parent = view2.getParent();
                            if (parent == null) {
                                throw new TypeCastException("null cannot be cast to non-null type android.view.ViewGroup");
                            }
                            listContainer5.notifyGroupChildRemoved(view, (ViewGroup)parent);
                        }
                    }
                }
            }
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("listContainer");
        throw null;
    }
    
    private final List<NotificationListItem> getChildListFromParent(final ListEntry listEntry) {
        if (listEntry instanceof GroupEntry) {
            final List<NotificationEntry> children = ((GroupEntry)listEntry).getChildren();
            Intrinsics.checkExpressionValueIsNotNull(children, "parent.children");
            final ArrayList list = new ArrayList<NotificationListItem>(CollectionsKt.collectionSizeOrDefault((Iterable<?>)children, 10));
            for (final NotificationEntry notificationEntry : children) {
                final NotifViewBarn rowRegistry = this.rowRegistry;
                Intrinsics.checkExpressionValueIsNotNull(notificationEntry, "child");
                list.add(rowRegistry.requireView(notificationEntry));
            }
            return CollectionsKt.toList((Iterable<? extends NotificationListItem>)list);
        }
        return CollectionsKt.emptyList();
    }
    
    private final Sequence<NotificationListItem> getListItems(final SimpleNotificationListContainer simpleNotificationListContainer) {
        final Sequence<NotificationListItem> filter = SequencesKt.filter(SequencesKt.map(CollectionsKt.asSequence((Iterable<?>)RangesKt.until(0, simpleNotificationListContainer.getContainerChildCount())), (Function1<? super Object, ? extends NotificationListItem>)new NotifViewManager$getListItems.NotifViewManager$getListItems$1(simpleNotificationListContainer)), (Function1<? super NotificationListItem, Boolean>)NotifViewManager$getListItems$$inlined$filterIsInstance.NotifViewManager$getListItems$$inlined$filterIsInstance$1.INSTANCE);
        if (filter != null) {
            return filter;
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.sequences.Sequence<R>");
    }
    
    public final void attach(final ShadeListBuilder shadeListBuilder) {
        Intrinsics.checkParameterIsNotNull(shadeListBuilder, "listBuilder");
        if (this.featureFlags.isNewNotifPipelineRenderingEnabled()) {
            shadeListBuilder.setOnRenderListListener((ShadeListBuilder.OnRenderListListener)new NotifViewManager$attach.NotifViewManager$attach$1(this));
        }
    }
    
    public final void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(array, "args");
    }
    
    public final void onNotifTreeBuilt(final List<? extends ListEntry> list) {
        Intrinsics.checkParameterIsNotNull(list, "notifList");
        Assert.isMainThread();
        this.detachRows(list);
        this.attachRows(list);
    }
    
    public final void setViewConsumer(final SimpleNotificationListContainer listContainer) {
        Intrinsics.checkParameterIsNotNull(listContainer, "consumer");
        this.listContainer = listContainer;
    }
}
