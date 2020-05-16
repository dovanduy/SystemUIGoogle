// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.people;

import kotlin.Unit;
import kotlin.sequences.SequenceScope;
import kotlin.jvm.functions.Function2;
import kotlin.coroutines.Continuation;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.R$id;
import kotlin.jvm.internal.Intrinsics;
import android.graphics.drawable.Drawable;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import kotlin.jvm.functions.Function1;
import kotlin.sequences.SequencesKt;
import android.view.View;
import kotlin.sequences.Sequence;
import android.view.ViewGroup;

public final class PeopleHubNotificationListenerKt
{
    private static final Sequence<View> childrenWithId(final ViewGroup viewGroup, final int n) {
        return SequencesKt.filter((Sequence<? extends View>)getChildren(viewGroup), (Function1<? super View, Boolean>)new PeopleHubNotificationListenerKt$childrenWithId.PeopleHubNotificationListenerKt$childrenWithId$1(n));
    }
    
    public static final Drawable extractAvatarFromRow(final NotificationEntry notificationEntry) {
        Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
        final ExpandableNotificationRow row = notificationEntry.getRow();
        if (row != null) {
            final Sequence<View> childrenWithId = childrenWithId((ViewGroup)row, R$id.expanded);
            if (childrenWithId != null) {
                final Sequence<Object> mapNotNull = SequencesKt.mapNotNull((Sequence<?>)childrenWithId, (Function1<? super Object, ?>)PeopleHubNotificationListenerKt$extractAvatarFromRow.PeopleHubNotificationListenerKt$extractAvatarFromRow$1.INSTANCE);
                if (mapNotNull != null) {
                    final Sequence<Object> flatMap = SequencesKt.flatMap((Sequence<?>)mapNotNull, (Function1<? super Object, ? extends Sequence<?>>)PeopleHubNotificationListenerKt$extractAvatarFromRow.PeopleHubNotificationListenerKt$extractAvatarFromRow$2.INSTANCE);
                    if (flatMap != null) {
                        final Sequence<Object> mapNotNull2 = SequencesKt.mapNotNull((Sequence<?>)flatMap, (Function1<? super Object, ?>)PeopleHubNotificationListenerKt$extractAvatarFromRow.PeopleHubNotificationListenerKt$extractAvatarFromRow$3.INSTANCE);
                        if (mapNotNull2 != null) {
                            final Sequence<Object> mapNotNull3 = SequencesKt.mapNotNull((Sequence<?>)mapNotNull2, (Function1<? super Object, ?>)PeopleHubNotificationListenerKt$extractAvatarFromRow.PeopleHubNotificationListenerKt$extractAvatarFromRow$4.INSTANCE);
                            if (mapNotNull3 != null) {
                                return SequencesKt.firstOrNull((Sequence<? extends Drawable>)mapNotNull3);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private static final Sequence<View> getChildren(final ViewGroup viewGroup) {
        return SequencesKt.sequence((Function2<? super SequenceScope<? super View>, ? super Continuation<? super Unit>, ?>)new PeopleHubNotificationListenerKt$children.PeopleHubNotificationListenerKt$children$1(viewGroup, (Continuation)null));
    }
}
