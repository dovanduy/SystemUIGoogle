// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.listbuilder;

import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifSection;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifPromoter;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.ListEntry;
import java.util.List;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifFilter;
import com.android.systemui.statusbar.notification.collection.GroupEntry;
import com.android.systemui.log.LogMessageImpl;
import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import com.android.systemui.log.LogLevel;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.log.LogBuffer;

public final class ShadeListBuilderLogger
{
    private final LogBuffer buffer;
    
    public ShadeListBuilderLogger(final LogBuffer buffer) {
        Intrinsics.checkParameterIsNotNull(buffer, "buffer");
        this.buffer = buffer;
    }
    
    public final void logDuplicateSummary(final int int1, final String str1, final String str2, final String str3) {
        Intrinsics.checkParameterIsNotNull(str1, "groupKey");
        Intrinsics.checkParameterIsNotNull(str2, "existingKey");
        Intrinsics.checkParameterIsNotNull(str3, "newKey");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("ShadeListBuilder", LogLevel.WARNING, (Function1<? super LogMessage, String>)ShadeListBuilderLogger$logDuplicateSummary.ShadeListBuilderLogger$logDuplicateSummary$2.INSTANCE);
        obtain.setInt1(int1);
        obtain.setStr1(str1);
        obtain.setStr2(str2);
        obtain.setStr3(str3);
        buffer.push(obtain);
    }
    
    public final void logDuplicateTopLevelKey(final int int1, final String str1) {
        Intrinsics.checkParameterIsNotNull(str1, "topLevelKey");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("ShadeListBuilder", LogLevel.WARNING, (Function1<? super LogMessage, String>)ShadeListBuilderLogger$logDuplicateTopLevelKey.ShadeListBuilderLogger$logDuplicateTopLevelKey$2.INSTANCE);
        obtain.setInt1(int1);
        obtain.setStr1(str1);
        buffer.push(obtain);
    }
    
    public final void logEndBuildList(final int n, final int int1, final int int2) {
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("ShadeListBuilder", LogLevel.INFO, (Function1<? super LogMessage, String>)ShadeListBuilderLogger$logEndBuildList.ShadeListBuilderLogger$logEndBuildList$2.INSTANCE);
        obtain.setLong1(n);
        obtain.setInt1(int1);
        obtain.setInt2(int2);
        buffer.push(obtain);
    }
    
    public final void logEntryAttachStateChanged(final int int1, String str3, final GroupEntry groupEntry, final GroupEntry groupEntry2) {
        Intrinsics.checkParameterIsNotNull(str3, "key");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("ShadeListBuilder", LogLevel.INFO, (Function1<? super LogMessage, String>)ShadeListBuilderLogger$logEntryAttachStateChanged.ShadeListBuilderLogger$logEntryAttachStateChanged$2.INSTANCE);
        obtain.setInt1(int1);
        obtain.setStr1(str3);
        final String s = null;
        if (groupEntry != null) {
            str3 = groupEntry.getKey();
        }
        else {
            str3 = null;
        }
        obtain.setStr2(str3);
        str3 = s;
        if (groupEntry2 != null) {
            str3 = groupEntry2.getKey();
        }
        obtain.setStr3(str3);
        buffer.push(obtain);
    }
    
    public final void logFilterChanged(final int int1, final NotifFilter notifFilter, final NotifFilter notifFilter2) {
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("ShadeListBuilder", LogLevel.INFO, (Function1<? super LogMessage, String>)ShadeListBuilderLogger$logFilterChanged.ShadeListBuilderLogger$logFilterChanged$2.INSTANCE);
        obtain.setInt1(int1);
        final String s = null;
        String name;
        if (notifFilter != null) {
            name = notifFilter.getName();
        }
        else {
            name = null;
        }
        obtain.setStr1(name);
        String name2 = s;
        if (notifFilter2 != null) {
            name2 = notifFilter2.getName();
        }
        obtain.setStr2(name2);
        buffer.push(obtain);
    }
    
    public final void logFinalList(final List<? extends ListEntry> list) {
        Intrinsics.checkParameterIsNotNull(list, "entries");
        if (list.isEmpty()) {
            final LogBuffer buffer = this.buffer;
            buffer.push(buffer.obtain("ShadeListBuilder", LogLevel.DEBUG, (Function1<? super LogMessage, String>)ShadeListBuilderLogger$logFinalList.ShadeListBuilderLogger$logFinalList$2.INSTANCE));
        }
        for (int size = list.size(), i = 0; i < size; ++i) {
            final ListEntry listEntry = (ListEntry)list.get(i);
            final LogBuffer buffer2 = this.buffer;
            final LogMessageImpl obtain = buffer2.obtain("ShadeListBuilder", LogLevel.DEBUG, (Function1<? super LogMessage, String>)ShadeListBuilderLogger$logFinalList.ShadeListBuilderLogger$logFinalList$4.INSTANCE);
            obtain.setInt1(i);
            obtain.setStr1(listEntry.getKey());
            buffer2.push(obtain);
            if (listEntry instanceof GroupEntry) {
                final GroupEntry groupEntry = (GroupEntry)listEntry;
                final NotificationEntry summary = groupEntry.getSummary();
                if (summary != null) {
                    final LogBuffer buffer3 = this.buffer;
                    final LogMessageImpl obtain2 = buffer3.obtain("ShadeListBuilder", LogLevel.DEBUG, (Function1<? super LogMessage, String>)ShadeListBuilderLogger$logFinalList$5.ShadeListBuilderLogger$logFinalList$5$2.INSTANCE);
                    Intrinsics.checkExpressionValueIsNotNull(summary, "it");
                    obtain2.setStr1(summary.getKey());
                    buffer3.push(obtain2);
                }
                final List<NotificationEntry> children = groupEntry.getChildren();
                Intrinsics.checkExpressionValueIsNotNull(children, "entry.children");
                for (int size2 = children.size(), j = 0; j < size2; ++j) {
                    final NotificationEntry notificationEntry = groupEntry.getChildren().get(j);
                    final LogBuffer buffer4 = this.buffer;
                    final LogMessageImpl obtain3 = buffer4.obtain("ShadeListBuilder", LogLevel.DEBUG, (Function1<? super LogMessage, String>)ShadeListBuilderLogger$logFinalList.ShadeListBuilderLogger$logFinalList$7.INSTANCE);
                    obtain3.setInt1(j);
                    Intrinsics.checkExpressionValueIsNotNull(notificationEntry, "child");
                    obtain3.setStr1(notificationEntry.getKey());
                    buffer4.push(obtain3);
                }
            }
        }
    }
    
    public final void logFinalizeFilterInvalidated(final String str1, final int int1) {
        Intrinsics.checkParameterIsNotNull(str1, "name");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("ShadeListBuilder", LogLevel.DEBUG, (Function1<? super LogMessage, String>)ShadeListBuilderLogger$logFinalizeFilterInvalidated.ShadeListBuilderLogger$logFinalizeFilterInvalidated$2.INSTANCE);
        obtain.setStr1(str1);
        obtain.setInt1(int1);
        buffer.push(obtain);
    }
    
    public final void logNotifSectionInvalidated(final String str1, final int int1) {
        Intrinsics.checkParameterIsNotNull(str1, "name");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("ShadeListBuilder", LogLevel.DEBUG, (Function1<? super LogMessage, String>)ShadeListBuilderLogger$logNotifSectionInvalidated.ShadeListBuilderLogger$logNotifSectionInvalidated$2.INSTANCE);
        obtain.setStr1(str1);
        obtain.setInt1(int1);
        buffer.push(obtain);
    }
    
    public final void logOnBuildList() {
        final LogBuffer buffer = this.buffer;
        buffer.push(buffer.obtain("ShadeListBuilder", LogLevel.INFO, (Function1<? super LogMessage, String>)ShadeListBuilderLogger$logOnBuildList.ShadeListBuilderLogger$logOnBuildList$2.INSTANCE));
    }
    
    public final void logParentChanged(final int int1, final GroupEntry groupEntry, final GroupEntry groupEntry2) {
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("ShadeListBuilder", LogLevel.INFO, (Function1<? super LogMessage, String>)ShadeListBuilderLogger$logParentChanged.ShadeListBuilderLogger$logParentChanged$2.INSTANCE);
        obtain.setInt1(int1);
        final String s = null;
        String key;
        if (groupEntry != null) {
            key = groupEntry.getKey();
        }
        else {
            key = null;
        }
        obtain.setStr1(key);
        String key2 = s;
        if (groupEntry2 != null) {
            key2 = groupEntry2.getKey();
        }
        obtain.setStr2(key2);
        buffer.push(obtain);
    }
    
    public final void logPreGroupFilterInvalidated(final String str1, final int int1) {
        Intrinsics.checkParameterIsNotNull(str1, "filterName");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("ShadeListBuilder", LogLevel.DEBUG, (Function1<? super LogMessage, String>)ShadeListBuilderLogger$logPreGroupFilterInvalidated.ShadeListBuilderLogger$logPreGroupFilterInvalidated$2.INSTANCE);
        obtain.setStr1(str1);
        obtain.setInt1(int1);
        buffer.push(obtain);
    }
    
    public final void logPromoterChanged(final int int1, final NotifPromoter notifPromoter, final NotifPromoter notifPromoter2) {
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("ShadeListBuilder", LogLevel.INFO, (Function1<? super LogMessage, String>)ShadeListBuilderLogger$logPromoterChanged.ShadeListBuilderLogger$logPromoterChanged$2.INSTANCE);
        obtain.setInt1(int1);
        final String s = null;
        String name;
        if (notifPromoter != null) {
            name = notifPromoter.getName();
        }
        else {
            name = null;
        }
        obtain.setStr1(name);
        String name2 = s;
        if (notifPromoter2 != null) {
            name2 = notifPromoter2.getName();
        }
        obtain.setStr2(name2);
        buffer.push(obtain);
    }
    
    public final void logPromoterInvalidated(final String str1, final int int1) {
        Intrinsics.checkParameterIsNotNull(str1, "name");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("ShadeListBuilder", LogLevel.DEBUG, (Function1<? super LogMessage, String>)ShadeListBuilderLogger$logPromoterInvalidated.ShadeListBuilderLogger$logPromoterInvalidated$2.INSTANCE);
        obtain.setStr1(str1);
        obtain.setInt1(int1);
        buffer.push(obtain);
    }
    
    public final void logSectionChanged(final int n, final NotifSection notifSection, final int int1, final NotifSection notifSection2, final int int2) {
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("ShadeListBuilder", LogLevel.INFO, (Function1<? super LogMessage, String>)ShadeListBuilderLogger$logSectionChanged.ShadeListBuilderLogger$logSectionChanged$2.INSTANCE);
        obtain.setLong1(n);
        final String s = null;
        String name;
        if (notifSection != null) {
            name = notifSection.getName();
        }
        else {
            name = null;
        }
        obtain.setStr1(name);
        obtain.setInt1(int1);
        String name2 = s;
        if (notifSection2 != null) {
            name2 = notifSection2.getName();
        }
        obtain.setStr2(name2);
        obtain.setInt2(int2);
        buffer.push(obtain);
    }
}
