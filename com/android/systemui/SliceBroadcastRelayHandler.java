// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import java.util.Iterator;
import android.content.ContentProvider;
import android.os.UserHandle;
import android.util.ArraySet;
import com.android.internal.annotations.VisibleForTesting;
import android.content.IntentFilter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.util.ArrayMap;
import android.content.BroadcastReceiver;
import com.android.systemui.broadcast.BroadcastDispatcher;

public class SliceBroadcastRelayHandler extends SystemUI
{
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final BroadcastReceiver mReceiver;
    private final ArrayMap<Uri, BroadcastRelay> mRelays;
    
    public SliceBroadcastRelayHandler(final Context context, final BroadcastDispatcher mBroadcastDispatcher) {
        super(context);
        this.mRelays = (ArrayMap<Uri, BroadcastRelay>)new ArrayMap();
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                SliceBroadcastRelayHandler.this.handleIntent(intent);
            }
        };
        this.mBroadcastDispatcher = mBroadcastDispatcher;
    }
    
    private BroadcastRelay getAndRemoveRelay(final Uri uri) {
        return (BroadcastRelay)this.mRelays.remove((Object)uri);
    }
    
    private BroadcastRelay getOrCreateRelay(final Uri uri) {
        BroadcastRelay broadcastRelay;
        if ((broadcastRelay = (BroadcastRelay)this.mRelays.get((Object)uri)) == null) {
            broadcastRelay = new BroadcastRelay(uri);
            this.mRelays.put((Object)uri, (Object)broadcastRelay);
        }
        return broadcastRelay;
    }
    
    @VisibleForTesting
    void handleIntent(final Intent intent) {
        if ("com.android.settingslib.action.REGISTER_SLICE_RECEIVER".equals(intent.getAction())) {
            this.getOrCreateRelay((Uri)intent.getParcelableExtra("uri")).register(super.mContext, (ComponentName)intent.getParcelableExtra("receiver"), (IntentFilter)intent.getParcelableExtra("filter"));
        }
        else if ("com.android.settingslib.action.UNREGISTER_SLICE_RECEIVER".equals(intent.getAction())) {
            final BroadcastRelay andRemoveRelay = this.getAndRemoveRelay((Uri)intent.getParcelableExtra("uri"));
            if (andRemoveRelay != null) {
                andRemoveRelay.unregister(super.mContext);
            }
        }
    }
    
    @Override
    public void start() {
        final IntentFilter intentFilter = new IntentFilter("com.android.settingslib.action.REGISTER_SLICE_RECEIVER");
        intentFilter.addAction("com.android.settingslib.action.UNREGISTER_SLICE_RECEIVER");
        this.mBroadcastDispatcher.registerReceiver(this.mReceiver, intentFilter);
    }
    
    private static class BroadcastRelay extends BroadcastReceiver
    {
        private final ArraySet<ComponentName> mReceivers;
        private final Uri mUri;
        private final UserHandle mUserId;
        
        public BroadcastRelay(final Uri mUri) {
            this.mReceivers = (ArraySet<ComponentName>)new ArraySet();
            this.mUserId = new UserHandle(ContentProvider.getUserIdFromUri(mUri));
            this.mUri = mUri;
        }
        
        public void onReceive(final Context context, final Intent intent) {
            intent.addFlags(268435456);
            final Iterator iterator = this.mReceivers.iterator();
            while (iterator.hasNext()) {
                intent.setComponent((ComponentName)iterator.next());
                intent.putExtra("uri", this.mUri.toString());
                context.sendBroadcastAsUser(intent, this.mUserId);
            }
        }
        
        public void register(final Context context, final ComponentName componentName, final IntentFilter intentFilter) {
            this.mReceivers.add((Object)componentName);
            context.registerReceiver((BroadcastReceiver)this, intentFilter);
        }
        
        public void unregister(final Context context) {
            context.unregisterReceiver((BroadcastReceiver)this);
        }
    }
}
