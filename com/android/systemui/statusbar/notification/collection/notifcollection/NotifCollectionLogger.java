// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.collection.notifcollection;

import android.os.RemoteException;
import android.service.notification.NotificationListenerService$RankingMap;
import com.android.systemui.log.LogMessageImpl;
import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import com.android.systemui.log.LogLevel;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.log.LogBuffer;

public final class NotifCollectionLogger
{
    private final LogBuffer buffer;
    
    public NotifCollectionLogger(final LogBuffer buffer) {
        Intrinsics.checkParameterIsNotNull(buffer, "buffer");
        this.buffer = buffer;
    }
    
    public final void logLifetimeExtended(final String str1, final NotifLifetimeExtender notifLifetimeExtender) {
        Intrinsics.checkParameterIsNotNull(str1, "key");
        Intrinsics.checkParameterIsNotNull(notifLifetimeExtender, "extender");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotifCollection", LogLevel.INFO, (Function1<? super LogMessage, String>)NotifCollectionLogger$logLifetimeExtended.NotifCollectionLogger$logLifetimeExtended$2.INSTANCE);
        obtain.setStr1(str1);
        obtain.setStr2(notifLifetimeExtender.getName());
        buffer.push(obtain);
    }
    
    public final void logLifetimeExtensionEnded(final String str1, final NotifLifetimeExtender notifLifetimeExtender, final int int1) {
        Intrinsics.checkParameterIsNotNull(str1, "key");
        Intrinsics.checkParameterIsNotNull(notifLifetimeExtender, "extender");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotifCollection", LogLevel.INFO, (Function1<? super LogMessage, String>)NotifCollectionLogger$logLifetimeExtensionEnded.NotifCollectionLogger$logLifetimeExtensionEnded$2.INSTANCE);
        obtain.setStr1(str1);
        obtain.setStr2(notifLifetimeExtender.getName());
        obtain.setInt1(int1);
        buffer.push(obtain);
    }
    
    public final void logNotifClearAllDismissalIntercepted(final String str1) {
        Intrinsics.checkParameterIsNotNull(str1, "key");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotifCollection", LogLevel.INFO, (Function1<? super LogMessage, String>)NotifCollectionLogger$logNotifClearAllDismissalIntercepted.NotifCollectionLogger$logNotifClearAllDismissalIntercepted$2.INSTANCE);
        obtain.setStr1(str1);
        buffer.push(obtain);
    }
    
    public final void logNotifDismissed(final String str1) {
        Intrinsics.checkParameterIsNotNull(str1, "key");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotifCollection", LogLevel.INFO, (Function1<? super LogMessage, String>)NotifCollectionLogger$logNotifDismissed.NotifCollectionLogger$logNotifDismissed$2.INSTANCE);
        obtain.setStr1(str1);
        buffer.push(obtain);
    }
    
    public final void logNotifDismissedIntercepted(final String str1) {
        Intrinsics.checkParameterIsNotNull(str1, "key");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotifCollection", LogLevel.INFO, (Function1<? super LogMessage, String>)NotifCollectionLogger$logNotifDismissedIntercepted.NotifCollectionLogger$logNotifDismissedIntercepted$2.INSTANCE);
        obtain.setStr1(str1);
        buffer.push(obtain);
    }
    
    public final void logNotifGroupPosted(final String str1, final int int1) {
        Intrinsics.checkParameterIsNotNull(str1, "groupKey");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotifCollection", LogLevel.INFO, (Function1<? super LogMessage, String>)NotifCollectionLogger$logNotifGroupPosted.NotifCollectionLogger$logNotifGroupPosted$2.INSTANCE);
        obtain.setStr1(str1);
        obtain.setInt1(int1);
        buffer.push(obtain);
    }
    
    public final void logNotifPosted(final String str1) {
        Intrinsics.checkParameterIsNotNull(str1, "key");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotifCollection", LogLevel.INFO, (Function1<? super LogMessage, String>)NotifCollectionLogger$logNotifPosted.NotifCollectionLogger$logNotifPosted$2.INSTANCE);
        obtain.setStr1(str1);
        buffer.push(obtain);
    }
    
    public final void logNotifRemoved(final String str1, final int int1) {
        Intrinsics.checkParameterIsNotNull(str1, "key");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotifCollection", LogLevel.INFO, (Function1<? super LogMessage, String>)NotifCollectionLogger$logNotifRemoved.NotifCollectionLogger$logNotifRemoved$2.INSTANCE);
        obtain.setStr1(str1);
        obtain.setInt1(int1);
        buffer.push(obtain);
    }
    
    public final void logNotifUpdated(final String str1) {
        Intrinsics.checkParameterIsNotNull(str1, "key");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotifCollection", LogLevel.INFO, (Function1<? super LogMessage, String>)NotifCollectionLogger$logNotifUpdated.NotifCollectionLogger$logNotifUpdated$2.INSTANCE);
        obtain.setStr1(str1);
        buffer.push(obtain);
    }
    
    public final void logRankingMissing(final String str1, final NotificationListenerService$RankingMap notificationListenerService$RankingMap) {
        Intrinsics.checkParameterIsNotNull(str1, "key");
        Intrinsics.checkParameterIsNotNull(notificationListenerService$RankingMap, "rankingMap");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotifCollection", LogLevel.WARNING, (Function1<? super LogMessage, String>)NotifCollectionLogger$logRankingMissing.NotifCollectionLogger$logRankingMissing$2.INSTANCE);
        obtain.setStr1(str1);
        buffer.push(obtain);
        final LogBuffer buffer2 = this.buffer;
        buffer2.push(buffer2.obtain("NotifCollection", LogLevel.DEBUG, (Function1<? super LogMessage, String>)NotifCollectionLogger$logRankingMissing.NotifCollectionLogger$logRankingMissing$4.INSTANCE));
        for (final String str2 : notificationListenerService$RankingMap.getOrderedKeys()) {
            final LogBuffer buffer3 = this.buffer;
            final LogMessageImpl obtain2 = buffer3.obtain("NotifCollection", LogLevel.DEBUG, (Function1<? super LogMessage, String>)NotifCollectionLogger$logRankingMissing.NotifCollectionLogger$logRankingMissing$6.INSTANCE);
            obtain2.setStr1(str2);
            buffer3.push(obtain2);
        }
    }
    
    public final void logRemoteExceptionOnClearAllNotifications(final RemoteException ex) {
        Intrinsics.checkParameterIsNotNull(ex, "e");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotifCollection", LogLevel.WTF, (Function1<? super LogMessage, String>)NotifCollectionLogger$logRemoteExceptionOnClearAllNotifications.NotifCollectionLogger$logRemoteExceptionOnClearAllNotifications$2.INSTANCE);
        obtain.setStr1(ex.toString());
        buffer.push(obtain);
    }
    
    public final void logRemoteExceptionOnNotificationClear(final String str1, final RemoteException ex) {
        Intrinsics.checkParameterIsNotNull(str1, "key");
        Intrinsics.checkParameterIsNotNull(ex, "e");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("NotifCollection", LogLevel.WTF, (Function1<? super LogMessage, String>)NotifCollectionLogger$logRemoteExceptionOnNotificationClear.NotifCollectionLogger$logRemoteExceptionOnNotificationClear$2.INSTANCE);
        obtain.setStr1(str1);
        obtain.setStr2(ex.toString());
        buffer.push(obtain);
    }
}
