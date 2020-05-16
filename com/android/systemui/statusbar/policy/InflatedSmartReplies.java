// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import java.util.Collections;
import com.android.systemui.statusbar.SmartReplyController;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.app.PendingIntent;
import java.util.Iterator;
import java.util.ArrayList;
import com.android.systemui.shared.system.DevicePolicyManagerWrapper;
import com.android.systemui.shared.system.PackageManagerWrapper;
import android.util.Pair;
import android.app.Notification;
import com.android.systemui.Dependency;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import java.util.Collection;
import java.util.Arrays;
import android.app.Notification$Action;
import com.android.internal.util.ArrayUtils;
import android.app.RemoteInput;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.statusbar.NotificationUiAdjustment;
import android.util.Log;
import android.widget.Button;
import java.util.List;

public class InflatedSmartReplies
{
    private static final boolean DEBUG;
    private final SmartRepliesAndActions mSmartRepliesAndActions;
    private final SmartReplyView mSmartReplyView;
    private final List<Button> mSmartSuggestionButtons;
    
    static {
        DEBUG = Log.isLoggable("InflatedSmartReplies", 3);
    }
    
    private InflatedSmartReplies(final SmartReplyView mSmartReplyView, final List<Button> mSmartSuggestionButtons, final SmartRepliesAndActions mSmartRepliesAndActions) {
        this.mSmartReplyView = mSmartReplyView;
        this.mSmartSuggestionButtons = mSmartSuggestionButtons;
        this.mSmartRepliesAndActions = mSmartRepliesAndActions;
    }
    
    @VisibleForTesting
    static boolean areSuggestionsSimilar(final SmartRepliesAndActions smartRepliesAndActions, final SmartRepliesAndActions smartRepliesAndActions2) {
        return smartRepliesAndActions == smartRepliesAndActions2 || (smartRepliesAndActions != null && smartRepliesAndActions2 != null && smartRepliesAndActions.getSmartReplies().equals(smartRepliesAndActions2.getSmartReplies()) && (NotificationUiAdjustment.areDifferent(smartRepliesAndActions.getSmartActions(), smartRepliesAndActions2.getSmartActions()) ^ true));
    }
    
    public static SmartRepliesAndActions chooseSmartRepliesAndActions(final SmartReplyConstants smartReplyConstants, final NotificationEntry notificationEntry) {
        final Notification notification = notificationEntry.getSbn().getNotification();
        final boolean b = false;
        final Pair remoteInputActionPair = notification.findRemoteInputActionPair(false);
        final Pair remoteInputActionPair2 = notification.findRemoteInputActionPair(true);
        final boolean enabled = smartReplyConstants.isEnabled();
        Object o = null;
        if (!enabled) {
            if (InflatedSmartReplies.DEBUG) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Smart suggestions not enabled, not adding suggestions for ");
                sb.append(notificationEntry.getSbn().getKey());
                Log.d("InflatedSmartReplies", sb.toString());
            }
            return new SmartRepliesAndActions(null, null);
        }
        final boolean b2 = (!smartReplyConstants.requiresTargetingP() || notificationEntry.targetSdk >= 28) && remoteInputActionPair != null && !ArrayUtils.isEmpty((Object[])((RemoteInput)remoteInputActionPair.first).getChoices()) && ((Notification$Action)remoteInputActionPair.second).actionIntent != null;
        final List contextualActions = notification.getContextualActions();
        final boolean b3 = contextualActions.isEmpty() ^ true;
        Object o2;
        if (b2) {
            o2 = new SmartReplyView.SmartReplies(Arrays.asList(((RemoteInput)remoteInputActionPair.first).getChoices()), (RemoteInput)remoteInputActionPair.first, ((Notification$Action)remoteInputActionPair.second).actionIntent, false);
        }
        else {
            o2 = null;
        }
        if (b3) {
            o = new SmartReplyView.SmartActions(contextualActions, false);
        }
        Object o3 = o;
        SmartReplyView.SmartReplies smartReplies = (SmartReplyView.SmartReplies)o2;
        if (!b2) {
            o3 = o;
            smartReplies = (SmartReplyView.SmartReplies)o2;
            if (!b3) {
                if (!ArrayUtils.isEmpty((Collection)notificationEntry.getSmartReplies()) && remoteInputActionPair2 != null && ((Notification$Action)remoteInputActionPair2.second).getAllowGeneratedReplies() && ((Notification$Action)remoteInputActionPair2.second).actionIntent != null) {
                    o2 = new SmartReplyView.SmartReplies(notificationEntry.getSmartReplies(), (RemoteInput)remoteInputActionPair2.first, ((Notification$Action)remoteInputActionPair2.second).actionIntent, true);
                }
                int n = b ? 1 : 0;
                if (!ArrayUtils.isEmpty((Collection)notificationEntry.getSmartActions())) {
                    n = (b ? 1 : 0);
                    if (notification.getAllowSystemGeneratedContextualActions()) {
                        n = 1;
                    }
                }
                o3 = o;
                smartReplies = (SmartReplyView.SmartReplies)o2;
                if (n != 0) {
                    List<Notification$Action> list = notificationEntry.getSmartActions();
                    if (Dependency.get(ActivityManagerWrapper.class).isLockTaskKioskModeActive()) {
                        list = filterWhiteListedLockTaskApps(list);
                    }
                    o3 = new SmartReplyView.SmartActions(list, true);
                    smartReplies = (SmartReplyView.SmartReplies)o2;
                }
            }
        }
        return new SmartRepliesAndActions(smartReplies, (SmartReplyView.SmartActions)o3);
    }
    
    private static List<Notification$Action> filterWhiteListedLockTaskApps(final List<Notification$Action> list) {
        final PackageManagerWrapper packageManagerWrapper = Dependency.get(PackageManagerWrapper.class);
        final DevicePolicyManagerWrapper devicePolicyManagerWrapper = Dependency.get(DevicePolicyManagerWrapper.class);
        final ArrayList<Notification$Action> list2 = new ArrayList<Notification$Action>();
        for (final Notification$Action notification$Action : list) {
            final PendingIntent actionIntent = notification$Action.actionIntent;
            if (actionIntent == null) {
                continue;
            }
            final ResolveInfo resolveActivity = packageManagerWrapper.resolveActivity(actionIntent.getIntent(), 0);
            if (resolveActivity == null || !devicePolicyManagerWrapper.isLockTaskPermitted(resolveActivity.activityInfo.packageName)) {
                continue;
            }
            list2.add(notification$Action);
        }
        return list2;
    }
    
    public static boolean hasFreeformRemoteInput(final NotificationEntry notificationEntry) {
        final Notification notification = notificationEntry.getSbn().getNotification();
        boolean b = true;
        if (notification.findRemoteInputActionPair(true) == null) {
            b = false;
        }
        return b;
    }
    
    public static InflatedSmartReplies inflate(final Context context, final Context context2, final NotificationEntry notificationEntry, final SmartReplyConstants smartReplyConstants, final SmartReplyController smartReplyController, final HeadsUpManager headsUpManager, final SmartRepliesAndActions smartRepliesAndActions) {
        final SmartRepliesAndActions chooseSmartRepliesAndActions = chooseSmartRepliesAndActions(smartReplyConstants, notificationEntry);
        if (!shouldShowSmartReplyView(notificationEntry, chooseSmartRepliesAndActions)) {
            return new InflatedSmartReplies(null, null, chooseSmartRepliesAndActions);
        }
        final boolean b = areSuggestionsSimilar(smartRepliesAndActions, chooseSmartRepliesAndActions) ^ true;
        final SmartReplyView inflate = SmartReplyView.inflate(context);
        final ArrayList<Object> list = new ArrayList<Object>();
        final SmartReplyView.SmartReplies smartReplies = chooseSmartRepliesAndActions.smartReplies;
        if (smartReplies != null) {
            list.addAll(inflate.inflateRepliesFromRemoteInput(smartReplies, smartReplyController, notificationEntry, b));
        }
        final SmartReplyView.SmartActions smartActions = chooseSmartRepliesAndActions.smartActions;
        if (smartActions != null) {
            list.addAll(inflate.inflateSmartActions(context2, smartActions, smartReplyController, notificationEntry, headsUpManager, b));
        }
        return new InflatedSmartReplies(inflate, (List<Button>)list, chooseSmartRepliesAndActions);
    }
    
    public static boolean shouldShowSmartReplyView(final NotificationEntry notificationEntry, final SmartRepliesAndActions smartRepliesAndActions) {
        return (smartRepliesAndActions.smartReplies != null || smartRepliesAndActions.smartActions != null) && !notificationEntry.getSbn().getNotification().extras.getBoolean("android.remoteInputSpinner", false) && !notificationEntry.getSbn().getNotification().extras.getBoolean("android.hideSmartReplies", false);
    }
    
    public SmartRepliesAndActions getSmartRepliesAndActions() {
        return this.mSmartRepliesAndActions;
    }
    
    public SmartReplyView getSmartReplyView() {
        return this.mSmartReplyView;
    }
    
    public List<Button> getSmartSuggestionButtons() {
        return this.mSmartSuggestionButtons;
    }
    
    public static class SmartRepliesAndActions
    {
        public final SmartReplyView.SmartActions smartActions;
        public final SmartReplyView.SmartReplies smartReplies;
        
        SmartRepliesAndActions(final SmartReplyView.SmartReplies smartReplies, final SmartReplyView.SmartActions smartActions) {
            this.smartReplies = smartReplies;
            this.smartActions = smartActions;
        }
        
        public List<Notification$Action> getSmartActions() {
            final SmartReplyView.SmartActions smartActions = this.smartActions;
            List<Notification$Action> list;
            if (smartActions == null) {
                list = Collections.emptyList();
            }
            else {
                list = smartActions.actions;
            }
            return list;
        }
        
        public List<CharSequence> getSmartReplies() {
            final SmartReplyView.SmartReplies smartReplies = this.smartReplies;
            List<CharSequence> list;
            if (smartReplies == null) {
                list = Collections.emptyList();
            }
            else {
                list = smartReplies.choices;
            }
            return list;
        }
    }
}
