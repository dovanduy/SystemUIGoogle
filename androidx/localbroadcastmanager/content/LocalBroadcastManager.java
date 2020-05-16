// 
// Decompiled by Procyon v0.5.36
// 

package androidx.localbroadcastmanager.content;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.os.Looper;
import android.os.Handler;
import android.content.BroadcastReceiver;
import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;

@Deprecated
public final class LocalBroadcastManager
{
    private static LocalBroadcastManager mInstance;
    private static final Object mLock;
    private final HashMap<String, ArrayList<ReceiverRecord>> mActions;
    private final Context mAppContext;
    private final ArrayList<BroadcastRecord> mPendingBroadcasts;
    private final HashMap<BroadcastReceiver, ArrayList<ReceiverRecord>> mReceivers;
    
    static {
        mLock = new Object();
    }
    
    private LocalBroadcastManager(final Context mAppContext) {
        this.mReceivers = new HashMap<BroadcastReceiver, ArrayList<ReceiverRecord>>();
        this.mActions = new HashMap<String, ArrayList<ReceiverRecord>>();
        this.mPendingBroadcasts = new ArrayList<BroadcastRecord>();
        this.mAppContext = mAppContext;
        new Handler(mAppContext.getMainLooper()) {
            public void handleMessage(final Message message) {
                if (message.what != 1) {
                    super.handleMessage(message);
                }
                else {
                    LocalBroadcastManager.this.executePendingBroadcasts();
                }
            }
        };
    }
    
    public static LocalBroadcastManager getInstance(final Context context) {
        synchronized (LocalBroadcastManager.mLock) {
            if (LocalBroadcastManager.mInstance == null) {
                LocalBroadcastManager.mInstance = new LocalBroadcastManager(context.getApplicationContext());
            }
            return LocalBroadcastManager.mInstance;
        }
    }
    
    void executePendingBroadcasts() {
        while (true) {
            Object mReceivers = this.mReceivers;
            synchronized (mReceivers) {
                final int size = this.mPendingBroadcasts.size();
                if (size <= 0) {
                    return;
                }
                final BroadcastRecord[] a = new BroadcastRecord[size];
                this.mPendingBroadcasts.toArray(a);
                this.mPendingBroadcasts.clear();
                // monitorexit(mReceivers)
                for (final BroadcastRecord broadcastRecord : a) {
                    for (int size2 = broadcastRecord.receivers.size(), j = 0; j < size2; ++j) {
                        mReceivers = broadcastRecord.receivers.get(j);
                        if (!((ReceiverRecord)mReceivers).dead) {
                            ((ReceiverRecord)mReceivers).receiver.onReceive(this.mAppContext, broadcastRecord.intent);
                        }
                    }
                }
            }
        }
    }
    
    public void registerReceiver(final BroadcastReceiver broadcastReceiver, final IntentFilter intentFilter) {
        synchronized (this.mReceivers) {
            final ReceiverRecord receiverRecord = new ReceiverRecord(intentFilter, broadcastReceiver);
            ArrayList<ReceiverRecord> value;
            if ((value = this.mReceivers.get(broadcastReceiver)) == null) {
                value = new ArrayList<ReceiverRecord>(1);
                this.mReceivers.put(broadcastReceiver, value);
            }
            value.add(receiverRecord);
            for (int i = 0; i < intentFilter.countActions(); ++i) {
                final String action = intentFilter.getAction(i);
                ArrayList<ReceiverRecord> value2;
                if ((value2 = this.mActions.get(action)) == null) {
                    value2 = new ArrayList<ReceiverRecord>(1);
                    this.mActions.put(action, value2);
                }
                value2.add(receiverRecord);
            }
        }
    }
    
    public void unregisterReceiver(final BroadcastReceiver key) {
        synchronized (this.mReceivers) {
            final ArrayList<ReceiverRecord> list = this.mReceivers.remove(key);
            if (list == null) {
                return;
            }
            for (int i = list.size() - 1; i >= 0; --i) {
                final ReceiverRecord receiverRecord = list.get(i);
                receiverRecord.dead = true;
                for (int j = 0; j < receiverRecord.filter.countActions(); ++j) {
                    final String action = receiverRecord.filter.getAction(j);
                    final ArrayList<ReceiverRecord> list2 = this.mActions.get(action);
                    if (list2 != null) {
                        for (int k = list2.size() - 1; k >= 0; --k) {
                            final ReceiverRecord receiverRecord2 = list2.get(k);
                            if (receiverRecord2.receiver == key) {
                                receiverRecord2.dead = true;
                                list2.remove(k);
                            }
                        }
                        if (list2.size() <= 0) {
                            this.mActions.remove(action);
                        }
                    }
                }
            }
        }
    }
    
    private static final class BroadcastRecord
    {
        final Intent intent;
        final ArrayList<ReceiverRecord> receivers;
    }
    
    private static final class ReceiverRecord
    {
        boolean dead;
        final IntentFilter filter;
        final BroadcastReceiver receiver;
        
        ReceiverRecord(final IntentFilter filter, final BroadcastReceiver receiver) {
            this.filter = filter;
            this.receiver = receiver;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(128);
            sb.append("Receiver{");
            sb.append(this.receiver);
            sb.append(" filter=");
            sb.append(this.filter);
            if (this.dead) {
                sb.append(" DEAD");
            }
            sb.append("}");
            return sb.toString();
        }
    }
}
