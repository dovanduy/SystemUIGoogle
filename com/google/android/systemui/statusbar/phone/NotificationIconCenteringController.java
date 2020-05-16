// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.statusbar.phone;

import java.util.Iterator;
import java.util.Objects;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import com.android.systemui.util.Assert;
import com.android.systemui.statusbar.phone.NotificationIconAreaController;
import android.os.Handler;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.plugins.SensorManagerPlugin;

public class NotificationIconCenteringController implements SensorEventListener
{
    private NotificationEntry mEntryCentered;
    private final NotificationEntryManager mEntryManager;
    @VisibleForTesting
    protected boolean mIsSkipGestureEnabled;
    @VisibleForTesting
    protected Handler mMainThreadHandler;
    @VisibleForTesting
    protected String mMusicPlayingPkg;
    private NotificationIconAreaController mNotificationIconAreaController;
    private boolean mRegistered;
    @VisibleForTesting
    protected final Runnable mResetCenteredIconRunnable;
    
    private boolean isMusicPlaying() {
        return this.mMusicPlayingPkg != null;
    }
    
    private boolean isSkipGestureEnabled() {
        return this.mIsSkipGestureEnabled;
    }
    
    private void showIconCentered(final NotificationEntry mEntryCentered) {
        Assert.isMainThread();
        this.mMainThreadHandler.removeCallbacks(this.mResetCenteredIconRunnable);
        if (mEntryCentered == null) {
            this.mMainThreadHandler.postDelayed(this.mResetCenteredIconRunnable, 250L);
        }
        else {
            this.mNotificationIconAreaController.showIconCentered(mEntryCentered);
            this.mEntryCentered = mEntryCentered;
        }
    }
    
    public void dump(final FileDescriptor fileDescriptor, PrintWriter printWriter, final String[] array) {
        printWriter = printWriter.append("NotifIconCenterContr").append(": ");
        final StringBuilder sb = new StringBuilder();
        sb.append("\nisMusicPlaying: ");
        sb.append(this.isMusicPlaying());
        printWriter = printWriter.append(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("\nisSkipGestureEnabled: ");
        sb2.append(this.isSkipGestureEnabled());
        printWriter = printWriter.append(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("\nmSkipStatusRegistered: ");
        sb3.append(this.mRegistered);
        printWriter = printWriter.append(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("\nmEntryCentered: ");
        sb4.append(this.mEntryCentered);
        sb4.append("\n");
        printWriter.append(sb4.toString());
    }
    
    @Override
    public void onSensorChanged(final SensorEvent sensorEvent) {
        this.mMainThreadHandler.post((Runnable)new _$$Lambda$NotificationIconCenteringController$5iFRCdUf5KG7FCugqZInFqFHUEs(this, sensorEvent));
    }
    
    @VisibleForTesting
    protected void updateCenteredIcon() {
        if (this.isMusicPlaying() && this.isSkipGestureEnabled()) {
            for (final NotificationEntry notificationEntry : this.mEntryManager.getVisibleNotifications()) {
                if (notificationEntry.isMediaNotification() && Objects.equals(notificationEntry.getSbn().getPackageName(), this.mMusicPlayingPkg)) {
                    this.showIconCentered(notificationEntry);
                    return;
                }
            }
        }
        this.showIconCentered(null);
    }
}
