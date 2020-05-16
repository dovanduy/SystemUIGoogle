// 
// Decompiled by Procyon v0.5.36
// 

package android.support.v4.media.session;

import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.support.v4.media.MediaMetadataCompat;
import android.os.RemoteException;
import android.app.PendingIntent;
import android.os.IInterface;

public interface IMediaSession extends IInterface
{
    PendingIntent getLaunchPendingIntent() throws RemoteException;
    
    MediaMetadataCompat getMetadata() throws RemoteException;
    
    PlaybackStateCompat getPlaybackState() throws RemoteException;
    
    void pause() throws RemoteException;
    
    void play() throws RemoteException;
    
    void registerCallbackListener(final IMediaControllerCallback p0) throws RemoteException;
    
    void stop() throws RemoteException;
    
    void unregisterCallbackListener(final IMediaControllerCallback p0) throws RemoteException;
    
    public abstract static class Stub extends Binder implements IMediaSession
    {
        public static IMediaSession asInterface(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("android.support.v4.media.session.IMediaSession");
            if (queryLocalInterface != null && queryLocalInterface instanceof IMediaSession) {
                return (IMediaSession)queryLocalInterface;
            }
            return new Proxy(binder);
        }
        
        public static IMediaSession getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
        
        private static class Proxy implements IMediaSession
        {
            public static IMediaSession sDefaultImpl;
            private IBinder mRemote;
            
            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }
            
            public IBinder asBinder() {
                return this.mRemote;
            }
            
            @Override
            public PendingIntent getLaunchPendingIntent() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    if (!this.mRemote.transact(8, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getLaunchPendingIntent();
                    }
                    obtain2.readException();
                    PendingIntent pendingIntent;
                    if (obtain2.readInt() != 0) {
                        pendingIntent = (PendingIntent)PendingIntent.CREATOR.createFromParcel(obtain2);
                    }
                    else {
                        pendingIntent = null;
                    }
                    return pendingIntent;
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public MediaMetadataCompat getMetadata() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    if (!this.mRemote.transact(27, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getMetadata();
                    }
                    obtain2.readException();
                    MediaMetadataCompat mediaMetadataCompat;
                    if (obtain2.readInt() != 0) {
                        mediaMetadataCompat = (MediaMetadataCompat)MediaMetadataCompat.CREATOR.createFromParcel(obtain2);
                    }
                    else {
                        mediaMetadataCompat = null;
                    }
                    return mediaMetadataCompat;
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public PlaybackStateCompat getPlaybackState() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    if (!this.mRemote.transact(28, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getPlaybackState();
                    }
                    obtain2.readException();
                    PlaybackStateCompat playbackStateCompat;
                    if (obtain2.readInt() != 0) {
                        playbackStateCompat = (PlaybackStateCompat)PlaybackStateCompat.CREATOR.createFromParcel(obtain2);
                    }
                    else {
                        playbackStateCompat = null;
                    }
                    return playbackStateCompat;
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void pause() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    if (!this.mRemote.transact(18, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().pause();
                        return;
                    }
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void play() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    if (!this.mRemote.transact(13, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().play();
                        return;
                    }
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void registerCallbackListener(final IMediaControllerCallback mediaControllerCallback) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    IBinder binder;
                    if (mediaControllerCallback != null) {
                        binder = ((IInterface)mediaControllerCallback).asBinder();
                    }
                    else {
                        binder = null;
                    }
                    obtain.writeStrongBinder(binder);
                    if (!this.mRemote.transact(3, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().registerCallbackListener(mediaControllerCallback);
                        return;
                    }
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void stop() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    if (!this.mRemote.transact(19, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().stop();
                        return;
                    }
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void unregisterCallbackListener(final IMediaControllerCallback mediaControllerCallback) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    IBinder binder;
                    if (mediaControllerCallback != null) {
                        binder = ((IInterface)mediaControllerCallback).asBinder();
                    }
                    else {
                        binder = null;
                    }
                    obtain.writeStrongBinder(binder);
                    if (!this.mRemote.transact(4, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().unregisterCallbackListener(mediaControllerCallback);
                        return;
                    }
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
