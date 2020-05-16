// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util.wakelock;

import android.util.Log;
import java.util.HashMap;
import android.os.PowerManager;
import android.os.PowerManager$WakeLock;
import android.content.Context;

public interface WakeLock
{
    default WakeLock createPartial(final Context context, final String s) {
        return createPartial(context, s, 20000L);
    }
    
    default WakeLock createPartial(final Context context, final String s, final long n) {
        return wrap(createPartialInner(context, s), n);
    }
    
    default PowerManager$WakeLock createPartialInner(final Context context, final String s) {
        return ((PowerManager)context.getSystemService((Class)PowerManager.class)).newWakeLock(1, s);
    }
    
    default WakeLock wrap(final PowerManager$WakeLock powerManager$WakeLock, final long n) {
        return new WakeLock() {
            private final HashMap<String, Integer> mActiveClients = new HashMap<String, Integer>();
            
            @Override
            public void acquire(final String key) {
                this.mActiveClients.putIfAbsent(key, 0);
                final HashMap<String, Integer> mActiveClients = this.mActiveClients;
                mActiveClients.put(key, mActiveClients.get(key) + 1);
                powerManager$WakeLock.acquire(n);
            }
            
            @Override
            public void release(final String s) {
                final Integer n = this.mActiveClients.get(s);
                if (n == null) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Releasing WakeLock with invalid reason: ");
                    sb.append(s);
                    Log.wtf("WakeLock", sb.toString(), new Throwable());
                }
                else if (n == 1) {
                    this.mActiveClients.remove(s);
                }
                else {
                    this.mActiveClients.put(s, n - 1);
                }
                powerManager$WakeLock.release();
            }
            
            @Override
            public String toString() {
                final StringBuilder sb = new StringBuilder();
                sb.append("active clients= ");
                sb.append(this.mActiveClients.toString());
                return sb.toString();
            }
            
            @Override
            public Runnable wrap(final Runnable runnable) {
                return WakeLock.wrapImpl(this, runnable);
            }
        };
    }
    
    default Runnable wrapImpl(final WakeLock wakeLock, final Runnable runnable) {
        wakeLock.acquire("wrap");
        return new _$$Lambda$WakeLock$Rdut1DSGlHtP_OM8Y87P7galvtM(runnable, wakeLock);
    }
    
    void acquire(final String p0);
    
    void release(final String p0);
    
    Runnable wrap(final Runnable p0);
    
    public static class Builder
    {
        private final Context mContext;
        private long mMaxTimeout;
        private String mTag;
        
        public Builder(final Context mContext) {
            this.mMaxTimeout = 20000L;
            this.mContext = mContext;
        }
        
        public WakeLock build() {
            return WakeLock.createPartial(this.mContext, this.mTag, this.mMaxTimeout);
        }
        
        public Builder setTag(final String mTag) {
            this.mTag = mTag;
            return this;
        }
    }
}
