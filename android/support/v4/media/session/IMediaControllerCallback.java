// 
// Decompiled by Procyon v0.5.36
// 

package android.support.v4.media.session;

import android.os.Parcelable$Creator;
import android.text.TextUtils;
import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import java.util.List;
import android.support.v4.media.MediaMetadataCompat;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.IInterface;

public interface IMediaControllerCallback extends IInterface
{
    void onCaptioningEnabledChanged(final boolean p0) throws RemoteException;
    
    void onEvent(final String p0, final Bundle p1) throws RemoteException;
    
    void onExtrasChanged(final Bundle p0) throws RemoteException;
    
    void onMetadataChanged(final MediaMetadataCompat p0) throws RemoteException;
    
    void onPlaybackStateChanged(final PlaybackStateCompat p0) throws RemoteException;
    
    void onQueueChanged(final List<MediaSessionCompat.QueueItem> p0) throws RemoteException;
    
    void onQueueTitleChanged(final CharSequence p0) throws RemoteException;
    
    void onRepeatModeChanged(final int p0) throws RemoteException;
    
    void onSessionDestroyed() throws RemoteException;
    
    void onSessionReady() throws RemoteException;
    
    void onShuffleModeChanged(final int p0) throws RemoteException;
    
    void onShuffleModeChangedRemoved(final boolean p0) throws RemoteException;
    
    void onVolumeInfoChanged(final ParcelableVolumeInfo p0) throws RemoteException;
    
    public abstract static class Stub extends Binder implements IMediaControllerCallback
    {
        public Stub() {
            this.attachInterface((IInterface)this, "android.support.v4.media.session.IMediaControllerCallback");
        }
        
        public IBinder asBinder() {
            return (IBinder)this;
        }
        
        public boolean onTransact(final int n, final Parcel parcel, final Parcel parcel2, final int n2) throws RemoteException {
            if (n == 1598968902) {
                parcel2.writeString("android.support.v4.media.session.IMediaControllerCallback");
                return true;
            }
            final boolean b = false;
            boolean b2 = false;
            final Bundle bundle = null;
            final CharSequence charSequence = null;
            final MediaMetadataCompat mediaMetadataCompat = null;
            final PlaybackStateCompat playbackStateCompat = null;
            final Bundle bundle2 = null;
            final ParcelableVolumeInfo parcelableVolumeInfo = null;
            switch (n) {
                default: {
                    return super.onTransact(n, parcel, parcel2, n2);
                }
                case 13: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    this.onSessionReady();
                    return true;
                }
                case 12: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    this.onShuffleModeChanged(parcel.readInt());
                    return true;
                }
                case 11: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    if (parcel.readInt() != 0) {
                        b2 = true;
                    }
                    this.onCaptioningEnabledChanged(b2);
                    return true;
                }
                case 10: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    boolean b3 = b;
                    if (parcel.readInt() != 0) {
                        b3 = true;
                    }
                    this.onShuffleModeChangedRemoved(b3);
                    return true;
                }
                case 9: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    this.onRepeatModeChanged(parcel.readInt());
                    return true;
                }
                case 8: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    ParcelableVolumeInfo parcelableVolumeInfo2 = parcelableVolumeInfo;
                    if (parcel.readInt() != 0) {
                        parcelableVolumeInfo2 = (ParcelableVolumeInfo)ParcelableVolumeInfo.CREATOR.createFromParcel(parcel);
                    }
                    this.onVolumeInfoChanged(parcelableVolumeInfo2);
                    return true;
                }
                case 7: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    Bundle bundle3 = bundle;
                    if (parcel.readInt() != 0) {
                        bundle3 = (Bundle)Bundle.CREATOR.createFromParcel(parcel);
                    }
                    this.onExtrasChanged(bundle3);
                    return true;
                }
                case 6: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    CharSequence charSequence2 = charSequence;
                    if (parcel.readInt() != 0) {
                        charSequence2 = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
                    }
                    this.onQueueTitleChanged(charSequence2);
                    return true;
                }
                case 5: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    this.onQueueChanged(parcel.createTypedArrayList((Parcelable$Creator)MediaSessionCompat.QueueItem.CREATOR));
                    return true;
                }
                case 4: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    MediaMetadataCompat mediaMetadataCompat2 = mediaMetadataCompat;
                    if (parcel.readInt() != 0) {
                        mediaMetadataCompat2 = (MediaMetadataCompat)MediaMetadataCompat.CREATOR.createFromParcel(parcel);
                    }
                    this.onMetadataChanged(mediaMetadataCompat2);
                    return true;
                }
                case 3: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    PlaybackStateCompat playbackStateCompat2 = playbackStateCompat;
                    if (parcel.readInt() != 0) {
                        playbackStateCompat2 = (PlaybackStateCompat)PlaybackStateCompat.CREATOR.createFromParcel(parcel);
                    }
                    this.onPlaybackStateChanged(playbackStateCompat2);
                    return true;
                }
                case 2: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    this.onSessionDestroyed();
                    return true;
                }
                case 1: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
                    final String string = parcel.readString();
                    Bundle bundle4 = bundle2;
                    if (parcel.readInt() != 0) {
                        bundle4 = (Bundle)Bundle.CREATOR.createFromParcel(parcel);
                    }
                    this.onEvent(string, bundle4);
                    return true;
                }
            }
        }
    }
}
