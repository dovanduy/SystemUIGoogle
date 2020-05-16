// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.interruption;

import android.media.MediaMetadata;
import android.content.ContentResolver;
import android.provider.Settings$Secure;
import com.android.keyguard.KeyguardUpdateMonitor;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.content.Context;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;

public final class BypassHeadsUpNotifier implements StateListener, MediaListener
{
    private final KeyguardBypassController bypassController;
    private final Context context;
    private NotificationEntry currentMediaEntry;
    private boolean enabled;
    private final NotificationEntryManager entryManager;
    private boolean fullyAwake;
    private final HeadsUpManagerPhone headsUpManager;
    private final NotificationMediaManager mediaManager;
    private final NotificationLockscreenUserManager notificationLockscreenUserManager;
    private final StatusBarStateController statusBarStateController;
    
    public BypassHeadsUpNotifier(final Context context, final KeyguardBypassController bypassController, final StatusBarStateController statusBarStateController, final HeadsUpManagerPhone headsUpManager, final NotificationLockscreenUserManager notificationLockscreenUserManager, final NotificationMediaManager mediaManager, final NotificationEntryManager entryManager, final TunerService tunerService) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(bypassController, "bypassController");
        Intrinsics.checkParameterIsNotNull(statusBarStateController, "statusBarStateController");
        Intrinsics.checkParameterIsNotNull(headsUpManager, "headsUpManager");
        Intrinsics.checkParameterIsNotNull(notificationLockscreenUserManager, "notificationLockscreenUserManager");
        Intrinsics.checkParameterIsNotNull(mediaManager, "mediaManager");
        Intrinsics.checkParameterIsNotNull(entryManager, "entryManager");
        Intrinsics.checkParameterIsNotNull(tunerService, "tunerService");
        this.context = context;
        this.bypassController = bypassController;
        this.statusBarStateController = statusBarStateController;
        this.headsUpManager = headsUpManager;
        this.notificationLockscreenUserManager = notificationLockscreenUserManager;
        this.mediaManager = mediaManager;
        this.entryManager = entryManager;
        this.enabled = true;
        statusBarStateController.addCallback((StatusBarStateController.StateListener)this);
        tunerService.addTunable((TunerService.Tunable)new TunerService.Tunable() {
            final /* synthetic */ BypassHeadsUpNotifier this$0;
            
            @Override
            public final void onTuningChanged(final String s, final String s2) {
                final BypassHeadsUpNotifier this$0 = this.this$0;
                final ContentResolver contentResolver = BypassHeadsUpNotifier.access$getContext$p(this$0).getContentResolver();
                final int currentUser = KeyguardUpdateMonitor.getCurrentUser();
                boolean b = false;
                if (Settings$Secure.getIntForUser(contentResolver, "show_media_when_bypassing", 0, currentUser) != 0) {
                    b = true;
                }
                BypassHeadsUpNotifier.access$setEnabled$p(this$0, b);
            }
        }, "show_media_when_bypassing");
    }
    
    public static final /* synthetic */ Context access$getContext$p(final BypassHeadsUpNotifier bypassHeadsUpNotifier) {
        return bypassHeadsUpNotifier.context;
    }
    
    public static final /* synthetic */ void access$setEnabled$p(final BypassHeadsUpNotifier bypassHeadsUpNotifier, final boolean enabled) {
        bypassHeadsUpNotifier.enabled = enabled;
    }
    
    private final boolean canAutoHeadsUp(final NotificationEntry notificationEntry) {
        return this.isAutoHeadsUpAllowed() && !notificationEntry.isSensitive() && this.notificationLockscreenUserManager.shouldShowOnKeyguard(notificationEntry) && this.entryManager.getActiveNotificationUnfiltered(notificationEntry.getKey()) == null;
    }
    
    private final boolean isAutoHeadsUpAllowed() {
        return this.enabled && this.bypassController.getBypassEnabled() && this.statusBarStateController.getState() == 1 && this.fullyAwake;
    }
    
    private final void updateAutoHeadsUp(final NotificationEntry notificationEntry) {
        if (notificationEntry != null) {
            final boolean autoHeadsUp = Intrinsics.areEqual(notificationEntry, this.currentMediaEntry) && this.canAutoHeadsUp(notificationEntry);
            notificationEntry.setAutoHeadsUp(autoHeadsUp);
            if (autoHeadsUp) {
                this.headsUpManager.showNotification(notificationEntry);
            }
        }
    }
    
    @Override
    public void onMetadataOrStateChanged(final MediaMetadata mediaMetadata, final int n) {
        final NotificationEntry currentMediaEntry = this.currentMediaEntry;
        NotificationEntry activeNotificationUnfiltered = this.entryManager.getActiveNotificationUnfiltered(this.mediaManager.getMediaNotificationKey());
        if (!NotificationMediaManager.isPlayingState(n)) {
            activeNotificationUnfiltered = null;
        }
        this.currentMediaEntry = activeNotificationUnfiltered;
        this.updateAutoHeadsUp(currentMediaEntry);
        this.updateAutoHeadsUp(this.currentMediaEntry);
    }
    
    @Override
    public void onStatePostChange() {
        this.updateAutoHeadsUp(this.currentMediaEntry);
    }
    
    public final void setFullyAwake(final boolean fullyAwake) {
        this.fullyAwake = fullyAwake;
        if (fullyAwake) {
            this.updateAutoHeadsUp(this.currentMediaEntry);
        }
    }
    
    public final void setUp() {
        this.mediaManager.addCallback((NotificationMediaManager.MediaListener)this);
    }
}
