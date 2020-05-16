// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.net.Uri;
import android.service.notification.StatusBarNotification;
import java.util.Objects;
import android.app.RemoteInput;
import java.util.List;
import android.os.Bundle;
import android.app.Notification$Builder;
import android.app.Notification$Action;
import android.app.Notification$WearableExtender;
import android.content.Context;
import android.app.Notification;
import android.os.SystemProperties;
import android.util.ArrayMap;
import com.android.systemui.statusbar.policy.RemoteInputUriController;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.lang.ref.WeakReference;
import android.util.Pair;
import java.util.ArrayList;

public class RemoteInputController
{
    private static final boolean ENABLE_REMOTE_INPUT;
    private final ArrayList<Callback> mCallbacks;
    private final Delegate mDelegate;
    private final ArrayList<Pair<WeakReference<NotificationEntry>, Object>> mOpen;
    private final RemoteInputUriController mRemoteInputUriController;
    private final ArrayMap<String, Object> mSpinning;
    
    static {
        ENABLE_REMOTE_INPUT = SystemProperties.getBoolean("debug.enable_remote_input", true);
    }
    
    public RemoteInputController(final Delegate mDelegate, final RemoteInputUriController mRemoteInputUriController) {
        this.mOpen = new ArrayList<Pair<WeakReference<NotificationEntry>, Object>>();
        this.mSpinning = (ArrayMap<String, Object>)new ArrayMap();
        this.mCallbacks = new ArrayList<Callback>(3);
        this.mDelegate = mDelegate;
        this.mRemoteInputUriController = mRemoteInputUriController;
    }
    
    private void apply(final NotificationEntry notificationEntry) {
        this.mDelegate.setRemoteInputActive(notificationEntry, this.isRemoteInputActive(notificationEntry));
        final boolean remoteInputActive = this.isRemoteInputActive();
        for (int size = this.mCallbacks.size(), i = 0; i < size; ++i) {
            this.mCallbacks.get(i).onRemoteInputActive(remoteInputActive);
        }
    }
    
    public static void processForRemoteInput(final Notification notification, final Context context) {
        if (!RemoteInputController.ENABLE_REMOTE_INPUT) {
            return;
        }
        final Bundle extras = notification.extras;
        if (extras != null && extras.containsKey("android.wearable.EXTENSIONS")) {
            final Notification$Action[] actions = notification.actions;
            if (actions == null || actions.length == 0) {
                Notification$Action notification$Action = null;
                final List actions2 = new Notification$WearableExtender(notification).getActions();
                final int size = actions2.size();
                int n = 0;
                Notification$Action notification$Action2;
                while (true) {
                    notification$Action2 = notification$Action;
                    if (n >= size) {
                        break;
                    }
                    final Notification$Action notification$Action3 = actions2.get(n);
                    if (notification$Action3 != null) {
                        final RemoteInput[] remoteInputs = notification$Action3.getRemoteInputs();
                        if (remoteInputs != null) {
                            final int length = remoteInputs.length;
                            int n2 = 0;
                            while (true) {
                                notification$Action2 = notification$Action;
                                if (n2 >= length) {
                                    break;
                                }
                                if (remoteInputs[n2].getAllowFreeFormInput()) {
                                    notification$Action2 = notification$Action3;
                                    break;
                                }
                                ++n2;
                            }
                            if ((notification$Action = notification$Action2) != null) {
                                break;
                            }
                        }
                    }
                    ++n;
                }
                if (notification$Action2 != null) {
                    final Notification$Builder recoverBuilder = Notification$Builder.recoverBuilder(context, notification);
                    recoverBuilder.setActions(new Notification$Action[] { notification$Action2 });
                    recoverBuilder.build();
                }
            }
        }
    }
    
    private boolean pruneWeakThenRemoveAndContains(final NotificationEntry notificationEntry, final NotificationEntry notificationEntry2, final Object o) {
        int i = this.mOpen.size() - 1;
        boolean b = false;
        while (i >= 0) {
            final NotificationEntry notificationEntry3 = (NotificationEntry)((WeakReference)this.mOpen.get(i).first).get();
            final Object second = this.mOpen.get(i).second;
            final boolean b2 = o == null || second == o;
            boolean b3;
            if (notificationEntry3 != null && (notificationEntry3 != notificationEntry2 || !b2)) {
                b3 = b;
                if (notificationEntry3 == notificationEntry) {
                    if (o != null && o != second) {
                        this.mOpen.remove(i);
                        b3 = b;
                    }
                    else {
                        b3 = true;
                    }
                }
            }
            else {
                this.mOpen.remove(i);
                b3 = b;
            }
            --i;
            b = b3;
        }
        return b;
    }
    
    public void addCallback(final Callback callback) {
        Objects.requireNonNull(callback);
        this.mCallbacks.add(callback);
    }
    
    public void addRemoteInput(final NotificationEntry notificationEntry, final Object obj) {
        Objects.requireNonNull(notificationEntry);
        Objects.requireNonNull(obj);
        if (!this.pruneWeakThenRemoveAndContains(notificationEntry, null, obj)) {
            this.mOpen.add((Pair<WeakReference<NotificationEntry>, Object>)new Pair((Object)new WeakReference(notificationEntry), obj));
        }
        this.apply(notificationEntry);
    }
    
    public void addSpinning(final String obj, final Object obj2) {
        Objects.requireNonNull(obj);
        Objects.requireNonNull(obj2);
        this.mSpinning.put((Object)obj, obj2);
    }
    
    public void closeRemoteInputs() {
        if (this.mOpen.size() == 0) {
            return;
        }
        final ArrayList<NotificationEntry> list = new ArrayList<NotificationEntry>(this.mOpen.size());
        for (int i = this.mOpen.size() - 1; i >= 0; --i) {
            final NotificationEntry e = (NotificationEntry)((WeakReference)this.mOpen.get(i).first).get();
            if (e != null && e.rowExists()) {
                list.add(e);
            }
        }
        for (int j = list.size() - 1; j >= 0; --j) {
            final NotificationEntry notificationEntry = list.get(j);
            if (notificationEntry.rowExists()) {
                notificationEntry.closeRemoteInput();
            }
        }
    }
    
    public void grantInlineReplyUriPermission(final StatusBarNotification statusBarNotification, final Uri uri) {
        this.mRemoteInputUriController.grantInlineReplyUriPermission(statusBarNotification, uri);
    }
    
    public boolean isRemoteInputActive() {
        this.pruneWeakThenRemoveAndContains(null, null, null);
        return this.mOpen.isEmpty() ^ true;
    }
    
    public boolean isRemoteInputActive(final NotificationEntry notificationEntry) {
        return this.pruneWeakThenRemoveAndContains(notificationEntry, null, null);
    }
    
    public boolean isSpinning(final String s) {
        return this.mSpinning.containsKey((Object)s);
    }
    
    public boolean isSpinning(final String s, final Object o) {
        return this.mSpinning.get((Object)s) == o;
    }
    
    public void lockScrollTo(final NotificationEntry notificationEntry) {
        this.mDelegate.lockScrollTo(notificationEntry);
    }
    
    public void remoteInputSent(final NotificationEntry notificationEntry) {
        for (int size = this.mCallbacks.size(), i = 0; i < size; ++i) {
            this.mCallbacks.get(i).onRemoteInputSent(notificationEntry);
        }
    }
    
    public void removeRemoteInput(final NotificationEntry obj, final Object o) {
        Objects.requireNonNull(obj);
        this.pruneWeakThenRemoveAndContains(null, obj, o);
        this.apply(obj);
    }
    
    public void removeSpinning(final String obj, final Object o) {
        Objects.requireNonNull(obj);
        if (o == null || this.mSpinning.get((Object)obj) == o) {
            this.mSpinning.remove((Object)obj);
        }
    }
    
    public void requestDisallowLongPressAndDismiss() {
        this.mDelegate.requestDisallowLongPressAndDismiss();
    }
    
    public interface Callback
    {
        default void onRemoteInputActive(final boolean b) {
        }
        
        default void onRemoteInputSent(final NotificationEntry notificationEntry) {
        }
    }
    
    public interface Delegate
    {
        void lockScrollTo(final NotificationEntry p0);
        
        void requestDisallowLongPressAndDismiss();
        
        void setRemoteInputActive(final NotificationEntry p0, final boolean p1);
    }
}
