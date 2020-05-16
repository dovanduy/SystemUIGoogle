// 
// Decompiled by Procyon v0.5.36
// 

package android.support.v4.media.session;

import android.os.IInterface;
import android.media.session.MediaController$TransportControls;
import android.os.IBinder;
import androidx.versionedparcelable.ParcelUtils;
import androidx.core.app.BundleCompat;
import java.util.Iterator;
import android.os.ResultReceiver;
import android.media.session.MediaSession$Token;
import java.util.ArrayList;
import android.media.session.MediaController;
import java.util.HashMap;
import android.os.RemoteException;
import android.os.Looper;
import android.media.session.MediaSession$QueueItem;
import android.media.session.PlaybackState;
import android.media.MediaMetadata;
import androidx.media.AudioAttributesCompat;
import android.media.session.MediaController$PlaybackInfo;
import java.lang.ref.WeakReference;
import android.os.Message;
import java.util.List;
import android.os.Bundle;
import android.media.session.MediaController$Callback;
import android.os.IBinder$DeathRecipient;
import android.util.Log;
import android.os.Handler;
import android.app.PendingIntent;
import android.support.v4.media.MediaMetadataCompat;
import android.os.Build$VERSION;
import android.content.Context;
import java.util.concurrent.ConcurrentHashMap;

public final class MediaControllerCompat
{
    private final MediaControllerImpl mImpl;
    private final ConcurrentHashMap<Callback, Boolean> mRegisteredCallbacks;
    
    public MediaControllerCompat(final Context context, final MediaSessionCompat.Token token) {
        this.mRegisteredCallbacks = new ConcurrentHashMap<Callback, Boolean>();
        if (token != null) {
            if (Build$VERSION.SDK_INT >= 21) {
                this.mImpl = (MediaControllerImpl)new MediaControllerImplApi21(context, token);
            }
            else {
                this.mImpl = (MediaControllerImpl)new MediaControllerImplBase(token);
            }
            return;
        }
        throw new IllegalArgumentException("sessionToken must not be null");
    }
    
    public MediaMetadataCompat getMetadata() {
        return this.mImpl.getMetadata();
    }
    
    public PlaybackStateCompat getPlaybackState() {
        return this.mImpl.getPlaybackState();
    }
    
    public PendingIntent getSessionActivity() {
        return this.mImpl.getSessionActivity();
    }
    
    public TransportControls getTransportControls() {
        return this.mImpl.getTransportControls();
    }
    
    public void registerCallback(final Callback callback) {
        this.registerCallback(callback, null);
    }
    
    public void registerCallback(final Callback key, final Handler handler) {
        if (key == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        if (this.mRegisteredCallbacks.putIfAbsent(key, Boolean.TRUE) != null) {
            Log.w("MediaControllerCompat", "the callback has already been registered");
            return;
        }
        Handler handler2;
        if ((handler2 = handler) == null) {
            handler2 = new Handler();
        }
        key.setHandler(handler2);
        this.mImpl.registerCallback(key, handler2);
    }
    
    public void unregisterCallback(final Callback key) {
        if (key != null) {
            if (this.mRegisteredCallbacks.remove(key) == null) {
                Log.w("MediaControllerCompat", "the callback has never been registered");
                return;
            }
            try {
                this.mImpl.unregisterCallback(key);
                return;
            }
            finally {
                key.setHandler(null);
            }
        }
        throw new IllegalArgumentException("callback must not be null");
    }
    
    public abstract static class Callback implements IBinder$DeathRecipient
    {
        final MediaController$Callback mCallbackFwk;
        MessageHandler mHandler;
        IMediaControllerCallback mIControllerCallback;
        
        public Callback() {
            if (Build$VERSION.SDK_INT >= 21) {
                this.mCallbackFwk = new MediaControllerCallbackApi21(this);
            }
            else {
                this.mCallbackFwk = null;
                this.mIControllerCallback = new StubCompat(this);
            }
        }
        
        public void binderDied() {
            this.postToHandler(8, null, null);
        }
        
        public void onAudioInfoChanged(final PlaybackInfo playbackInfo) {
        }
        
        public void onCaptioningEnabledChanged(final boolean b) {
        }
        
        public void onExtrasChanged(final Bundle bundle) {
        }
        
        public void onMetadataChanged(final MediaMetadataCompat mediaMetadataCompat) {
        }
        
        public void onPlaybackStateChanged(final PlaybackStateCompat playbackStateCompat) {
        }
        
        public void onQueueChanged(final List<MediaSessionCompat.QueueItem> list) {
        }
        
        public void onQueueTitleChanged(final CharSequence charSequence) {
        }
        
        public void onRepeatModeChanged(final int n) {
        }
        
        public void onSessionDestroyed() {
        }
        
        public void onSessionEvent(final String s, final Bundle bundle) {
        }
        
        public void onSessionReady() {
        }
        
        public void onShuffleModeChanged(final int n) {
        }
        
        void postToHandler(final int n, final Object o, final Bundle data) {
            final MessageHandler mHandler = this.mHandler;
            if (mHandler != null) {
                final Message obtainMessage = mHandler.obtainMessage(n, o);
                obtainMessage.setData(data);
                obtainMessage.sendToTarget();
            }
        }
        
        void setHandler(final Handler handler) {
            if (handler == null) {
                final MessageHandler mHandler = this.mHandler;
                if (mHandler != null) {
                    mHandler.mRegistered = false;
                    mHandler.removeCallbacksAndMessages((Object)null);
                    this.mHandler = null;
                }
            }
            else {
                final MessageHandler mHandler2 = new MessageHandler(handler.getLooper());
                this.mHandler = mHandler2;
                mHandler2.mRegistered = true;
            }
        }
        
        private static class MediaControllerCallbackApi21 extends MediaController$Callback
        {
            private final WeakReference<Callback> mCallback;
            
            MediaControllerCallbackApi21(final Callback referent) {
                this.mCallback = new WeakReference<Callback>(referent);
            }
            
            public void onAudioInfoChanged(final MediaController$PlaybackInfo mediaController$PlaybackInfo) {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.onAudioInfoChanged(new PlaybackInfo(mediaController$PlaybackInfo.getPlaybackType(), AudioAttributesCompat.wrap(mediaController$PlaybackInfo.getAudioAttributes()), mediaController$PlaybackInfo.getVolumeControl(), mediaController$PlaybackInfo.getMaxVolume(), mediaController$PlaybackInfo.getCurrentVolume()));
                }
            }
            
            public void onExtrasChanged(final Bundle bundle) {
                MediaSessionCompat.ensureClassLoader(bundle);
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.onExtrasChanged(bundle);
                }
            }
            
            public void onMetadataChanged(final MediaMetadata mediaMetadata) {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.onMetadataChanged(MediaMetadataCompat.fromMediaMetadata(mediaMetadata));
                }
            }
            
            public void onPlaybackStateChanged(final PlaybackState playbackState) {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    if (callback.mIControllerCallback == null) {
                        callback.onPlaybackStateChanged(PlaybackStateCompat.fromPlaybackState(playbackState));
                    }
                }
            }
            
            public void onQueueChanged(final List<MediaSession$QueueItem> list) {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.onQueueChanged(MediaSessionCompat.QueueItem.fromQueueItemList(list));
                }
            }
            
            public void onQueueTitleChanged(final CharSequence charSequence) {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.onQueueTitleChanged(charSequence);
                }
            }
            
            public void onSessionDestroyed() {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.onSessionDestroyed();
                }
            }
            
            public void onSessionEvent(final String s, final Bundle bundle) {
                MediaSessionCompat.ensureClassLoader(bundle);
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    if (callback.mIControllerCallback == null || Build$VERSION.SDK_INT >= 23) {
                        callback.onSessionEvent(s, bundle);
                    }
                }
            }
        }
        
        private class MessageHandler extends Handler
        {
            boolean mRegistered;
            
            MessageHandler(final Looper looper) {
                super(looper);
                this.mRegistered = false;
            }
            
            public void handleMessage(final Message message) {
                if (!this.mRegistered) {
                    return;
                }
                switch (message.what) {
                    case 13: {
                        Callback.this.onSessionReady();
                        break;
                    }
                    case 12: {
                        Callback.this.onShuffleModeChanged((int)message.obj);
                        break;
                    }
                    case 11: {
                        Callback.this.onCaptioningEnabledChanged((boolean)message.obj);
                        break;
                    }
                    case 9: {
                        Callback.this.onRepeatModeChanged((int)message.obj);
                        break;
                    }
                    case 8: {
                        Callback.this.onSessionDestroyed();
                        break;
                    }
                    case 7: {
                        final Bundle bundle = (Bundle)message.obj;
                        MediaSessionCompat.ensureClassLoader(bundle);
                        Callback.this.onExtrasChanged(bundle);
                        break;
                    }
                    case 6: {
                        Callback.this.onQueueTitleChanged((CharSequence)message.obj);
                        break;
                    }
                    case 5: {
                        Callback.this.onQueueChanged((List<MediaSessionCompat.QueueItem>)message.obj);
                        break;
                    }
                    case 4: {
                        Callback.this.onAudioInfoChanged((PlaybackInfo)message.obj);
                        break;
                    }
                    case 3: {
                        Callback.this.onMetadataChanged((MediaMetadataCompat)message.obj);
                        break;
                    }
                    case 2: {
                        Callback.this.onPlaybackStateChanged((PlaybackStateCompat)message.obj);
                        break;
                    }
                    case 1: {
                        final Bundle data = message.getData();
                        MediaSessionCompat.ensureClassLoader(data);
                        Callback.this.onSessionEvent((String)message.obj, data);
                        break;
                    }
                }
            }
        }
        
        private static class StubCompat extends Stub
        {
            private final WeakReference<Callback> mCallback;
            
            StubCompat(final Callback referent) {
                this.mCallback = new WeakReference<Callback>(referent);
            }
            
            public void onCaptioningEnabledChanged(final boolean b) throws RemoteException {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.postToHandler(11, b, null);
                }
            }
            
            public void onEvent(final String s, final Bundle bundle) throws RemoteException {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.postToHandler(1, s, bundle);
                }
            }
            
            public void onExtrasChanged(final Bundle bundle) throws RemoteException {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.postToHandler(7, bundle, null);
                }
            }
            
            public void onMetadataChanged(final MediaMetadataCompat mediaMetadataCompat) throws RemoteException {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.postToHandler(3, mediaMetadataCompat, null);
                }
            }
            
            public void onPlaybackStateChanged(final PlaybackStateCompat playbackStateCompat) throws RemoteException {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.postToHandler(2, playbackStateCompat, null);
                }
            }
            
            public void onQueueChanged(final List<MediaSessionCompat.QueueItem> list) throws RemoteException {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.postToHandler(5, list, null);
                }
            }
            
            public void onQueueTitleChanged(final CharSequence charSequence) throws RemoteException {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.postToHandler(6, charSequence, null);
                }
            }
            
            public void onRepeatModeChanged(final int i) throws RemoteException {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.postToHandler(9, i, null);
                }
            }
            
            public void onSessionDestroyed() throws RemoteException {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.postToHandler(8, null, null);
                }
            }
            
            public void onSessionReady() throws RemoteException {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.postToHandler(13, null, null);
                }
            }
            
            public void onShuffleModeChanged(final int i) throws RemoteException {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.postToHandler(12, i, null);
                }
            }
            
            public void onShuffleModeChangedRemoved(final boolean b) throws RemoteException {
            }
            
            public void onVolumeInfoChanged(final ParcelableVolumeInfo parcelableVolumeInfo) throws RemoteException {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    PlaybackInfo playbackInfo;
                    if (parcelableVolumeInfo != null) {
                        playbackInfo = new PlaybackInfo(parcelableVolumeInfo.volumeType, parcelableVolumeInfo.audioStream, parcelableVolumeInfo.controlType, parcelableVolumeInfo.maxVolume, parcelableVolumeInfo.currentVolume);
                    }
                    else {
                        playbackInfo = null;
                    }
                    callback.postToHandler(4, playbackInfo, null);
                }
            }
        }
    }
    
    interface MediaControllerImpl
    {
        MediaMetadataCompat getMetadata();
        
        PlaybackStateCompat getPlaybackState();
        
        PendingIntent getSessionActivity();
        
        TransportControls getTransportControls();
        
        void registerCallback(final Callback p0, final Handler p1);
        
        void unregisterCallback(final Callback p0);
    }
    
    static class MediaControllerImplApi21 implements MediaControllerImpl
    {
        private HashMap<Callback, ExtraCallback> mCallbackMap;
        protected final MediaController mControllerFwk;
        final Object mLock;
        private final List<Callback> mPendingCallbacks;
        final MediaSessionCompat.Token mSessionToken;
        
        MediaControllerImplApi21(final Context context, final MediaSessionCompat.Token mSessionToken) {
            this.mLock = new Object();
            this.mPendingCallbacks = new ArrayList<Callback>();
            this.mCallbackMap = new HashMap<Callback, ExtraCallback>();
            this.mSessionToken = mSessionToken;
            this.mControllerFwk = new MediaController(context, (MediaSession$Token)this.mSessionToken.getToken());
            if (this.mSessionToken.getExtraBinder() == null) {
                this.requestExtraBinder();
            }
        }
        
        private void requestExtraBinder() {
            this.sendCommand("android.support.v4.media.session.command.GET_EXTRA_BINDER", null, new ExtraBinderRequestResultReceiver(this));
        }
        
        @Override
        public MediaMetadataCompat getMetadata() {
            final MediaMetadata metadata = this.mControllerFwk.getMetadata();
            MediaMetadataCompat fromMediaMetadata;
            if (metadata != null) {
                fromMediaMetadata = MediaMetadataCompat.fromMediaMetadata(metadata);
            }
            else {
                fromMediaMetadata = null;
            }
            return fromMediaMetadata;
        }
        
        @Override
        public PlaybackStateCompat getPlaybackState() {
            if (this.mSessionToken.getExtraBinder() != null) {
                try {
                    return this.mSessionToken.getExtraBinder().getPlaybackState();
                }
                catch (RemoteException ex) {
                    Log.e("MediaControllerCompat", "Dead object in getPlaybackState.", (Throwable)ex);
                }
            }
            final PlaybackState playbackState = this.mControllerFwk.getPlaybackState();
            PlaybackStateCompat fromPlaybackState;
            if (playbackState != null) {
                fromPlaybackState = PlaybackStateCompat.fromPlaybackState(playbackState);
            }
            else {
                fromPlaybackState = null;
            }
            return fromPlaybackState;
        }
        
        @Override
        public PendingIntent getSessionActivity() {
            return this.mControllerFwk.getSessionActivity();
        }
        
        @Override
        public TransportControls getTransportControls() {
            return new TransportControlsApi21(this.mControllerFwk.getTransportControls());
        }
        
        void processPendingCallbacksLocked() {
            if (this.mSessionToken.getExtraBinder() == null) {
                return;
            }
            for (final Callback key : this.mPendingCallbacks) {
                final ExtraCallback extraCallback = new ExtraCallback(key);
                this.mCallbackMap.put(key, extraCallback);
                key.mIControllerCallback = extraCallback;
                try {
                    this.mSessionToken.getExtraBinder().registerCallbackListener(extraCallback);
                    key.postToHandler(13, null, null);
                    continue;
                }
                catch (RemoteException ex) {
                    Log.e("MediaControllerCompat", "Dead object in registerCallback.", (Throwable)ex);
                }
                break;
            }
            this.mPendingCallbacks.clear();
        }
        
        @Override
        public final void registerCallback(final Callback key, final Handler handler) {
            this.mControllerFwk.registerCallback(key.mCallbackFwk, handler);
            synchronized (this.mLock) {
                if (this.mSessionToken.getExtraBinder() != null) {
                    final ExtraCallback extraCallback = new ExtraCallback(key);
                    this.mCallbackMap.put(key, extraCallback);
                    key.mIControllerCallback = extraCallback;
                    try {
                        this.mSessionToken.getExtraBinder().registerCallbackListener(extraCallback);
                        key.postToHandler(13, null, null);
                    }
                    catch (RemoteException ex) {
                        Log.e("MediaControllerCompat", "Dead object in registerCallback.", (Throwable)ex);
                    }
                }
                else {
                    key.mIControllerCallback = null;
                    this.mPendingCallbacks.add(key);
                }
            }
        }
        
        public void sendCommand(final String s, final Bundle bundle, final ResultReceiver resultReceiver) {
            this.mControllerFwk.sendCommand(s, bundle, resultReceiver);
        }
        
        @Override
        public final void unregisterCallback(final Callback key) {
            this.mControllerFwk.unregisterCallback(key.mCallbackFwk);
            synchronized (this.mLock) {
                if (this.mSessionToken.getExtraBinder() != null) {
                    try {
                        final ExtraCallback extraCallback = this.mCallbackMap.remove(key);
                        if (extraCallback != null) {
                            key.mIControllerCallback = null;
                            this.mSessionToken.getExtraBinder().unregisterCallbackListener(extraCallback);
                        }
                    }
                    catch (RemoteException ex) {
                        Log.e("MediaControllerCompat", "Dead object in unregisterCallback.", (Throwable)ex);
                    }
                }
                else {
                    this.mPendingCallbacks.remove(key);
                }
            }
        }
        
        private static class ExtraBinderRequestResultReceiver extends ResultReceiver
        {
            private WeakReference<MediaControllerImplApi21> mMediaControllerImpl;
            
            ExtraBinderRequestResultReceiver(final MediaControllerImplApi21 referent) {
                super((Handler)null);
                this.mMediaControllerImpl = new WeakReference<MediaControllerImplApi21>(referent);
            }
            
            protected void onReceiveResult(final int n, final Bundle bundle) {
                final MediaControllerImplApi21 mediaControllerImplApi21 = this.mMediaControllerImpl.get();
                if (mediaControllerImplApi21 != null) {
                    if (bundle != null) {
                        synchronized (mediaControllerImplApi21.mLock) {
                            mediaControllerImplApi21.mSessionToken.setExtraBinder(IMediaSession.Stub.asInterface(BundleCompat.getBinder(bundle, "android.support.v4.media.session.EXTRA_BINDER")));
                            mediaControllerImplApi21.mSessionToken.setSession2Token(ParcelUtils.getVersionedParcelable(bundle, "android.support.v4.media.session.SESSION_TOKEN2"));
                            mediaControllerImplApi21.processPendingCallbacksLocked();
                        }
                    }
                }
            }
        }
        
        private static class ExtraCallback extends StubCompat
        {
            ExtraCallback(final Callback callback) {
                super(callback);
            }
            
            @Override
            public void onExtrasChanged(final Bundle bundle) throws RemoteException {
                throw new AssertionError();
            }
            
            @Override
            public void onMetadataChanged(final MediaMetadataCompat mediaMetadataCompat) throws RemoteException {
                throw new AssertionError();
            }
            
            @Override
            public void onQueueChanged(final List<MediaSessionCompat.QueueItem> list) throws RemoteException {
                throw new AssertionError();
            }
            
            @Override
            public void onQueueTitleChanged(final CharSequence charSequence) throws RemoteException {
                throw new AssertionError();
            }
            
            @Override
            public void onSessionDestroyed() throws RemoteException {
                throw new AssertionError();
            }
            
            @Override
            public void onVolumeInfoChanged(final ParcelableVolumeInfo parcelableVolumeInfo) throws RemoteException {
                throw new AssertionError();
            }
        }
    }
    
    static class MediaControllerImplBase implements MediaControllerImpl
    {
        private IMediaSession mBinder;
        private TransportControls mTransportControls;
        
        MediaControllerImplBase(final MediaSessionCompat.Token token) {
            this.mBinder = IMediaSession.Stub.asInterface((IBinder)token.getToken());
        }
        
        @Override
        public MediaMetadataCompat getMetadata() {
            try {
                return this.mBinder.getMetadata();
            }
            catch (RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in getMetadata.", (Throwable)ex);
                return null;
            }
        }
        
        @Override
        public PlaybackStateCompat getPlaybackState() {
            try {
                return this.mBinder.getPlaybackState();
            }
            catch (RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in getPlaybackState.", (Throwable)ex);
                return null;
            }
        }
        
        @Override
        public PendingIntent getSessionActivity() {
            try {
                return this.mBinder.getLaunchPendingIntent();
            }
            catch (RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in getSessionActivity.", (Throwable)ex);
                return null;
            }
        }
        
        @Override
        public TransportControls getTransportControls() {
            if (this.mTransportControls == null) {
                this.mTransportControls = new TransportControlsBase(this.mBinder);
            }
            return this.mTransportControls;
        }
        
        @Override
        public void registerCallback(final Callback callback, final Handler handler) {
            if (callback != null) {
                try {
                    ((IInterface)this.mBinder).asBinder().linkToDeath((IBinder$DeathRecipient)callback, 0);
                    this.mBinder.registerCallbackListener(callback.mIControllerCallback);
                    callback.postToHandler(13, null, null);
                }
                catch (RemoteException ex) {
                    Log.e("MediaControllerCompat", "Dead object in registerCallback.", (Throwable)ex);
                    callback.postToHandler(8, null, null);
                }
                return;
            }
            throw new IllegalArgumentException("callback may not be null.");
        }
        
        @Override
        public void unregisterCallback(final Callback callback) {
            if (callback != null) {
                try {
                    this.mBinder.unregisterCallbackListener(callback.mIControllerCallback);
                    ((IInterface)this.mBinder).asBinder().unlinkToDeath((IBinder$DeathRecipient)callback, 0);
                }
                catch (RemoteException ex) {
                    Log.e("MediaControllerCompat", "Dead object in unregisterCallback.", (Throwable)ex);
                }
                return;
            }
            throw new IllegalArgumentException("callback may not be null.");
        }
    }
    
    public static final class PlaybackInfo
    {
        PlaybackInfo(final int n, final int legacyStreamType, final int n2, final int n3, final int n4) {
            final AudioAttributesCompat.Builder builder = new AudioAttributesCompat.Builder();
            builder.setLegacyStreamType(legacyStreamType);
            this(n, builder.build(), n2, n3, n4);
        }
        
        PlaybackInfo(final int n, final AudioAttributesCompat audioAttributesCompat, final int n2, final int n3, final int n4) {
        }
    }
    
    public abstract static class TransportControls
    {
        TransportControls() {
        }
        
        public abstract void pause();
        
        public abstract void play();
        
        public abstract void stop();
    }
    
    static class TransportControlsApi21 extends TransportControls
    {
        protected final MediaController$TransportControls mControlsFwk;
        
        TransportControlsApi21(final MediaController$TransportControls mControlsFwk) {
            this.mControlsFwk = mControlsFwk;
        }
        
        @Override
        public void pause() {
            this.mControlsFwk.pause();
        }
        
        @Override
        public void play() {
            this.mControlsFwk.play();
        }
        
        @Override
        public void stop() {
            this.mControlsFwk.stop();
        }
    }
    
    static class TransportControlsBase extends TransportControls
    {
        private IMediaSession mBinder;
        
        public TransportControlsBase(final IMediaSession mBinder) {
            this.mBinder = mBinder;
        }
        
        @Override
        public void pause() {
            try {
                this.mBinder.pause();
            }
            catch (RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in pause.", (Throwable)ex);
            }
        }
        
        @Override
        public void play() {
            try {
                this.mBinder.play();
            }
            catch (RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in play.", (Throwable)ex);
            }
        }
        
        @Override
        public void stop() {
            try {
                this.mBinder.stop();
            }
            catch (RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in stop.", (Throwable)ex);
            }
        }
    }
}
