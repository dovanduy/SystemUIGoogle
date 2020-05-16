// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.volume;

import android.os.Message;
import android.media.session.MediaController$Callback;
import java.util.Collection;
import java.util.HashSet;
import android.os.Handler;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager$NameNotFoundException;
import java.util.Objects;
import android.util.Log;
import java.util.Iterator;
import android.app.PendingIntent;
import android.media.session.PlaybackState;
import android.media.MediaMetadata;
import android.os.Bundle;
import android.media.session.MediaSession$QueueItem;
import java.io.PrintWriter;
import android.media.session.MediaController$PlaybackInfo;
import android.os.RemoteException;
import android.media.IRemoteVolumeController$Stub;
import android.media.session.MediaController;
import java.util.List;
import java.util.HashMap;
import android.os.Looper;
import android.media.session.MediaSessionManager$OnActiveSessionsChangedListener;
import android.media.IRemoteVolumeController;
import android.media.session.MediaSession$Token;
import java.util.Map;
import android.media.session.MediaSessionManager;
import android.content.Context;

public class MediaSessions
{
    private static final String TAG;
    private final Callbacks mCallbacks;
    private final Context mContext;
    private final H mHandler;
    private boolean mInit;
    private final MediaSessionManager mMgr;
    private final Map<MediaSession$Token, MediaControllerRecord> mRecords;
    private final IRemoteVolumeController mRvc;
    private final MediaSessionManager$OnActiveSessionsChangedListener mSessionsListener;
    
    static {
        TAG = Util.logTag(MediaSessions.class);
    }
    
    public MediaSessions(final Context mContext, final Looper looper, final Callbacks mCallbacks) {
        this.mRecords = new HashMap<MediaSession$Token, MediaControllerRecord>();
        this.mSessionsListener = (MediaSessionManager$OnActiveSessionsChangedListener)new MediaSessionManager$OnActiveSessionsChangedListener() {
            public void onActiveSessionsChanged(final List<MediaController> list) {
                MediaSessions.this.onActiveSessionsUpdatedH(list);
            }
        };
        this.mRvc = (IRemoteVolumeController)new IRemoteVolumeController$Stub() {
            public void remoteVolumeChanged(final MediaSession$Token mediaSession$Token, final int n) throws RemoteException {
                MediaSessions.this.mHandler.obtainMessage(2, n, 0, (Object)mediaSession$Token).sendToTarget();
            }
            
            public void updateRemoteController(final MediaSession$Token mediaSession$Token) throws RemoteException {
                MediaSessions.this.mHandler.obtainMessage(3, (Object)mediaSession$Token).sendToTarget();
            }
        };
        this.mContext = mContext;
        this.mHandler = new H(looper);
        this.mMgr = (MediaSessionManager)mContext.getSystemService("media_session");
        this.mCallbacks = mCallbacks;
    }
    
    private static void dump(int ratingType, final PrintWriter printWriter, final MediaController mediaController) {
        final StringBuilder sb = new StringBuilder();
        sb.append("  Controller ");
        sb.append(ratingType);
        sb.append(": ");
        sb.append(mediaController.getPackageName());
        printWriter.println(sb.toString());
        final Bundle extras = mediaController.getExtras();
        final long flags = mediaController.getFlags();
        final MediaMetadata metadata = mediaController.getMetadata();
        final MediaController$PlaybackInfo playbackInfo = mediaController.getPlaybackInfo();
        final PlaybackState playbackState = mediaController.getPlaybackState();
        final List queue = mediaController.getQueue();
        final CharSequence queueTitle = mediaController.getQueueTitle();
        ratingType = mediaController.getRatingType();
        final PendingIntent sessionActivity = mediaController.getSessionActivity();
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("    PlaybackState: ");
        sb2.append(Util.playbackStateToString(playbackState));
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("    PlaybackInfo: ");
        sb3.append(Util.playbackInfoToString(playbackInfo));
        printWriter.println(sb3.toString());
        if (metadata != null) {
            final StringBuilder sb4 = new StringBuilder();
            sb4.append("  MediaMetadata.desc=");
            sb4.append(metadata.getDescription());
            printWriter.println(sb4.toString());
        }
        final StringBuilder sb5 = new StringBuilder();
        sb5.append("    RatingType: ");
        sb5.append(ratingType);
        printWriter.println(sb5.toString());
        final StringBuilder sb6 = new StringBuilder();
        sb6.append("    Flags: ");
        sb6.append(flags);
        printWriter.println(sb6.toString());
        if (extras != null) {
            printWriter.println("    Extras:");
            for (final String str : extras.keySet()) {
                final StringBuilder sb7 = new StringBuilder();
                sb7.append("      ");
                sb7.append(str);
                sb7.append("=");
                sb7.append(extras.get(str));
                printWriter.println(sb7.toString());
            }
        }
        if (queueTitle != null) {
            final StringBuilder sb8 = new StringBuilder();
            sb8.append("    QueueTitle: ");
            sb8.append((Object)queueTitle);
            printWriter.println(sb8.toString());
        }
        if (queue != null && !queue.isEmpty()) {
            printWriter.println("    Queue:");
            for (final MediaSession$QueueItem obj : queue) {
                final StringBuilder sb9 = new StringBuilder();
                sb9.append("      ");
                sb9.append(obj);
                printWriter.println(sb9.toString());
            }
        }
        if (playbackInfo != null) {
            final StringBuilder sb10 = new StringBuilder();
            sb10.append("    sessionActivity: ");
            sb10.append(sessionActivity);
            printWriter.println(sb10.toString());
        }
    }
    
    private static boolean isRemote(final MediaController$PlaybackInfo mediaController$PlaybackInfo) {
        return mediaController$PlaybackInfo != null && mediaController$PlaybackInfo.getPlaybackType() == 2;
    }
    
    private void onRemoteVolumeChangedH(MediaSession$Token sessionToken, final int n) {
        final MediaController mediaController = new MediaController(this.mContext, sessionToken);
        if (D.BUG) {
            final String tag = MediaSessions.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("remoteVolumeChangedH ");
            sb.append(mediaController.getPackageName());
            sb.append(" ");
            sb.append(Util.audioManagerFlagsToString(n));
            Log.d(tag, sb.toString());
        }
        sessionToken = mediaController.getSessionToken();
        this.mCallbacks.onRemoteVolumeChanged(sessionToken, n);
    }
    
    private void onUpdateRemoteControllerH(final MediaSession$Token mediaSession$Token) {
        final String s = null;
        MediaController mediaController;
        if (mediaSession$Token != null) {
            mediaController = new MediaController(this.mContext, mediaSession$Token);
        }
        else {
            mediaController = null;
        }
        String packageName = s;
        if (mediaController != null) {
            packageName = mediaController.getPackageName();
        }
        if (D.BUG) {
            final String tag = MediaSessions.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("updateRemoteControllerH ");
            sb.append(packageName);
            Log.d(tag, sb.toString());
        }
        this.postUpdateSessions();
    }
    
    private void updateRemoteH(final MediaSession$Token mediaSession$Token, final String s, final MediaController$PlaybackInfo mediaController$PlaybackInfo) {
        final Callbacks mCallbacks = this.mCallbacks;
        if (mCallbacks != null) {
            mCallbacks.onRemoteUpdate(mediaSession$Token, s, mediaController$PlaybackInfo);
        }
    }
    
    public void dump(final PrintWriter printWriter) {
        final StringBuilder sb = new StringBuilder();
        sb.append(MediaSessions.class.getSimpleName());
        sb.append(" state:");
        printWriter.println(sb.toString());
        printWriter.print("  mInit: ");
        printWriter.println(this.mInit);
        printWriter.print("  mRecords.size: ");
        printWriter.println(this.mRecords.size());
        final Iterator<MediaControllerRecord> iterator = this.mRecords.values().iterator();
        int n = 0;
        while (iterator.hasNext()) {
            dump(++n, printWriter, iterator.next().controller);
        }
    }
    
    protected String getControllerName(MediaController packageName) {
        final PackageManager packageManager = this.mContext.getPackageManager();
        packageName = (MediaController)packageName.getPackageName();
        try {
            final String trim = Objects.toString(packageManager.getApplicationInfo((String)packageName, 0).loadLabel(packageManager), "").trim();
            if (trim.length() > 0) {
                return trim;
            }
            return (String)packageName;
        }
        catch (PackageManager$NameNotFoundException ex) {
            return (String)packageName;
        }
    }
    
    public void init() {
        if (D.BUG) {
            Log.d(MediaSessions.TAG, "init");
        }
        this.mMgr.addOnActiveSessionsChangedListener(this.mSessionsListener, (ComponentName)null, (Handler)this.mHandler);
        this.mInit = true;
        this.postUpdateSessions();
        this.mMgr.registerRemoteVolumeController(this.mRvc);
    }
    
    protected void onActiveSessionsUpdatedH(final List<MediaController> list) {
        if (D.BUG) {
            final String tag = MediaSessions.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("onActiveSessionsUpdatedH n=");
            sb.append(list.size());
            Log.d(tag, sb.toString());
        }
        final HashSet<Object> set = new HashSet<Object>(this.mRecords.keySet());
        for (final MediaController mediaController : list) {
            final MediaSession$Token sessionToken = mediaController.getSessionToken();
            final MediaController$PlaybackInfo playbackInfo = mediaController.getPlaybackInfo();
            set.remove(sessionToken);
            if (!this.mRecords.containsKey(sessionToken)) {
                final MediaControllerRecord mediaControllerRecord = new MediaControllerRecord(mediaController);
                mediaControllerRecord.name = this.getControllerName(mediaController);
                this.mRecords.put(sessionToken, mediaControllerRecord);
                mediaController.registerCallback((MediaController$Callback)mediaControllerRecord, (Handler)this.mHandler);
            }
            final MediaControllerRecord mediaControllerRecord2 = this.mRecords.get(sessionToken);
            if (isRemote(playbackInfo)) {
                this.updateRemoteH(sessionToken, mediaControllerRecord2.name, playbackInfo);
                mediaControllerRecord2.sentRemote = true;
            }
        }
        for (final MediaSession$Token mediaSession$Token : set) {
            final MediaControllerRecord mediaControllerRecord3 = this.mRecords.get(mediaSession$Token);
            mediaControllerRecord3.controller.unregisterCallback((MediaController$Callback)mediaControllerRecord3);
            this.mRecords.remove(mediaSession$Token);
            if (D.BUG) {
                final String tag2 = MediaSessions.TAG;
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("Removing ");
                sb2.append(mediaControllerRecord3.name);
                sb2.append(" sentRemote=");
                sb2.append(mediaControllerRecord3.sentRemote);
                Log.d(tag2, sb2.toString());
            }
            if (mediaControllerRecord3.sentRemote) {
                this.mCallbacks.onRemoteRemoved(mediaSession$Token);
                mediaControllerRecord3.sentRemote = false;
            }
        }
    }
    
    protected void postUpdateSessions() {
        if (!this.mInit) {
            return;
        }
        this.mHandler.sendEmptyMessage(1);
    }
    
    public void setVolume(final MediaSession$Token obj, final int i) {
        final MediaControllerRecord mediaControllerRecord = this.mRecords.get(obj);
        if (mediaControllerRecord == null) {
            final String tag = MediaSessions.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("setVolume: No record found for token ");
            sb.append(obj);
            Log.w(tag, sb.toString());
            return;
        }
        if (D.BUG) {
            final String tag2 = MediaSessions.TAG;
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Setting level to ");
            sb2.append(i);
            Log.d(tag2, sb2.toString());
        }
        mediaControllerRecord.controller.setVolumeTo(i, 0);
    }
    
    public interface Callbacks
    {
        void onRemoteRemoved(final MediaSession$Token p0);
        
        void onRemoteUpdate(final MediaSession$Token p0, final String p1, final MediaController$PlaybackInfo p2);
        
        void onRemoteVolumeChanged(final MediaSession$Token p0, final int p1);
    }
    
    private final class H extends Handler
    {
        private H(final Looper looper) {
            super(looper);
        }
        
        public void handleMessage(final Message message) {
            final int what = message.what;
            if (what != 1) {
                if (what != 2) {
                    if (what == 3) {
                        MediaSessions.this.onUpdateRemoteControllerH((MediaSession$Token)message.obj);
                    }
                }
                else {
                    MediaSessions.this.onRemoteVolumeChangedH((MediaSession$Token)message.obj, message.arg1);
                }
            }
            else {
                final MediaSessions this$0 = MediaSessions.this;
                this$0.onActiveSessionsUpdatedH(this$0.mMgr.getActiveSessions((ComponentName)null));
            }
        }
    }
    
    private final class MediaControllerRecord extends MediaController$Callback
    {
        public final MediaController controller;
        public String name;
        public boolean sentRemote;
        
        private MediaControllerRecord(final MediaController controller) {
            this.controller = controller;
        }
        
        private String cb(final String str) {
            final StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(" ");
            sb.append(this.controller.getPackageName());
            sb.append(" ");
            return sb.toString();
        }
        
        public void onAudioInfoChanged(final MediaController$PlaybackInfo mediaController$PlaybackInfo) {
            if (D.BUG) {
                final String access$200 = MediaSessions.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append(this.cb("onAudioInfoChanged"));
                sb.append(Util.playbackInfoToString(mediaController$PlaybackInfo));
                sb.append(" sentRemote=");
                sb.append(this.sentRemote);
                Log.d(access$200, sb.toString());
            }
            final boolean access$201 = isRemote(mediaController$PlaybackInfo);
            if (!access$201 && this.sentRemote) {
                MediaSessions.this.mCallbacks.onRemoteRemoved(this.controller.getSessionToken());
                this.sentRemote = false;
            }
            else if (access$201) {
                MediaSessions.this.updateRemoteH(this.controller.getSessionToken(), this.name, mediaController$PlaybackInfo);
                this.sentRemote = true;
            }
        }
        
        public void onExtrasChanged(final Bundle obj) {
            if (D.BUG) {
                final String access$200 = MediaSessions.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append(this.cb("onExtrasChanged"));
                sb.append(obj);
                Log.d(access$200, sb.toString());
            }
        }
        
        public void onMetadataChanged(final MediaMetadata mediaMetadata) {
            if (D.BUG) {
                final String access$200 = MediaSessions.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append(this.cb("onMetadataChanged"));
                sb.append(Util.mediaMetadataToString(mediaMetadata));
                Log.d(access$200, sb.toString());
            }
        }
        
        public void onPlaybackStateChanged(final PlaybackState playbackState) {
            if (D.BUG) {
                final String access$200 = MediaSessions.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append(this.cb("onPlaybackStateChanged"));
                sb.append(Util.playbackStateToString(playbackState));
                Log.d(access$200, sb.toString());
            }
        }
        
        public void onQueueChanged(final List<MediaSession$QueueItem> obj) {
            if (D.BUG) {
                final String access$200 = MediaSessions.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append(this.cb("onQueueChanged"));
                sb.append(obj);
                Log.d(access$200, sb.toString());
            }
        }
        
        public void onQueueTitleChanged(final CharSequence obj) {
            if (D.BUG) {
                final String access$200 = MediaSessions.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append(this.cb("onQueueTitleChanged"));
                sb.append((Object)obj);
                Log.d(access$200, sb.toString());
            }
        }
        
        public void onSessionDestroyed() {
            if (D.BUG) {
                Log.d(MediaSessions.TAG, this.cb("onSessionDestroyed"));
            }
        }
        
        public void onSessionEvent(final String str, final Bundle obj) {
            if (D.BUG) {
                final String access$200 = MediaSessions.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append(this.cb("onSessionEvent"));
                sb.append("event=");
                sb.append(str);
                sb.append(" extras=");
                sb.append(obj);
                Log.d(access$200, sb.toString());
            }
        }
    }
}
