// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.media;

import android.util.Log;
import android.media.IAudioService$Stub;
import android.os.ServiceManager;
import java.util.Iterator;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.content.pm.PackageManager$NameNotFoundException;
import android.os.IBinder$DeathRecipient;
import android.os.UserHandle;
import android.os.RemoteException;
import android.media.VolumeShaper$Configuration;
import android.media.AudioAttributes;
import android.database.Cursor;
import android.content.ContentResolver;
import java.io.IOException;
import android.provider.MediaStore$Audio$Media;
import android.os.ParcelFileDescriptor;
import android.media.Ringtone;
import android.os.Binder;
import android.net.Uri;
import android.media.IRingtonePlayer$Stub;
import android.content.Context;
import android.os.IBinder;
import java.util.HashMap;
import android.media.IRingtonePlayer;
import android.media.IAudioService;
import com.android.systemui.SystemUI;

public class RingtonePlayer extends SystemUI
{
    private final NotificationPlayer mAsyncPlayer;
    private IAudioService mAudioService;
    private IRingtonePlayer mCallback;
    private final HashMap<IBinder, Client> mClients;
    
    public RingtonePlayer(final Context context) {
        super(context);
        this.mAsyncPlayer = new NotificationPlayer("RingtonePlayer");
        this.mClients = new HashMap<IBinder, Client>();
        this.mCallback = (IRingtonePlayer)new IRingtonePlayer$Stub() {
            public String getTitle(final Uri uri) {
                return Ringtone.getTitle(RingtonePlayer.this.getContextForUser(Binder.getCallingUserHandle()), uri, false, false);
            }
            
            public boolean isPlaying(final IBinder key) {
                synchronized (RingtonePlayer.this.mClients) {
                    final Client client = RingtonePlayer.this.mClients.get(key);
                    // monitorexit(RingtonePlayer.access$100(this.this$0))
                    return client != null && client.mRingtone.isPlaying();
                }
            }
            
            public ParcelFileDescriptor openRingtone(final Uri obj) {
                final ContentResolver contentResolver = RingtonePlayer.this.getContextForUser(Binder.getCallingUserHandle()).getContentResolver();
                if (obj.toString().startsWith(MediaStore$Audio$Media.EXTERNAL_CONTENT_URI.toString())) {
                    final Cursor query = contentResolver.query(obj, new String[] { "is_ringtone", "is_alarm", "is_notification" }, (String)null, (String[])null, (String)null);
                    try {
                        Label_0135: {
                            if (query.moveToFirst()) {
                                if (query.getInt(0) == 0 && query.getInt(1) == 0) {
                                    if (query.getInt(2) == 0) {
                                        break Label_0135;
                                    }
                                }
                                try {
                                    final ParcelFileDescriptor openFileDescriptor = contentResolver.openFileDescriptor(obj, "r");
                                    if (query != null) {
                                        query.close();
                                    }
                                    return openFileDescriptor;
                                }
                                catch (IOException cause) {
                                    throw new SecurityException(cause);
                                }
                            }
                        }
                        if (query != null) {
                            query.close();
                        }
                    }
                    finally {
                        if (query != null) {
                            try {
                                query.close();
                            }
                            finally {
                                final Throwable exception;
                                ((Throwable)obj).addSuppressed(exception);
                            }
                        }
                    }
                }
                final StringBuilder sb = new StringBuilder();
                sb.append("Uri is not ringtone, alarm, or notification: ");
                sb.append(obj);
                throw new SecurityException(sb.toString());
            }
            
            public void play(final IBinder binder, final Uri uri, final AudioAttributes audioAttributes, final float n, final boolean b) throws RemoteException {
                this.playWithVolumeShaping(binder, uri, audioAttributes, n, b, null);
            }
            
            public void playAsync(final Uri uri, final UserHandle userHandle, final boolean b, final AudioAttributes audioAttributes) {
                if (Binder.getCallingUid() == 1000) {
                    UserHandle system = userHandle;
                    if (UserHandle.ALL.equals((Object)userHandle)) {
                        system = UserHandle.SYSTEM;
                    }
                    RingtonePlayer.this.mAsyncPlayer.play(RingtonePlayer.this.getContextForUser(system), uri, b, audioAttributes);
                    return;
                }
                throw new SecurityException("Async playback only available from system UID.");
            }
            
            public void playWithVolumeShaping(final IBinder binder, final Uri uri, final AudioAttributes audioAttributes, final float volume, final boolean looping, final VolumeShaper$Configuration volumeShaper$Configuration) throws RemoteException {
                synchronized (RingtonePlayer.this.mClients) {
                    Object value;
                    if ((value = RingtonePlayer.this.mClients.get(binder)) == null) {
                        value = new Client(binder, uri, Binder.getCallingUserHandle(), audioAttributes, volumeShaper$Configuration);
                        binder.linkToDeath((IBinder$DeathRecipient)value, 0);
                        RingtonePlayer.this.mClients.put(binder, value);
                    }
                    // monitorexit(RingtonePlayer.access$100(this.this$0))
                    ((Client)value).mRingtone.setLooping(looping);
                    ((Client)value).mRingtone.setVolume(volume);
                    ((Client)value).mRingtone.play();
                }
            }
            
            public void setPlaybackProperties(final IBinder key, final float volume, final boolean looping) {
                synchronized (RingtonePlayer.this.mClients) {
                    final Client client = RingtonePlayer.this.mClients.get(key);
                    // monitorexit(RingtonePlayer.access$100(this.this$0))
                    if (client != null) {
                        client.mRingtone.setVolume(volume);
                        client.mRingtone.setLooping(looping);
                    }
                }
            }
            
            public void stop(final IBinder key) {
                synchronized (RingtonePlayer.this.mClients) {
                    final Client client = RingtonePlayer.this.mClients.remove(key);
                    // monitorexit(RingtonePlayer.access$100(this.this$0))
                    if (client != null) {
                        client.mToken.unlinkToDeath((IBinder$DeathRecipient)client, 0);
                        client.mRingtone.stop();
                    }
                }
            }
            
            public void stopAsync() {
                if (Binder.getCallingUid() == 1000) {
                    RingtonePlayer.this.mAsyncPlayer.stop();
                    return;
                }
                throw new SecurityException("Async playback only available from system UID.");
            }
        };
    }
    
    private Context getContextForUser(final UserHandle userHandle) {
        try {
            return super.mContext.createPackageContextAsUser(super.mContext.getPackageName(), 0, userHandle);
        }
        catch (PackageManager$NameNotFoundException cause) {
            throw new RuntimeException((Throwable)cause);
        }
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("Clients:");
        synchronized (this.mClients) {
            for (final Client client : this.mClients.values()) {
                printWriter.print("  mToken=");
                printWriter.print(client.mToken);
                printWriter.print(" mUri=");
                printWriter.println(client.mRingtone.getUri());
            }
        }
    }
    
    @Override
    public void start() {
        this.mAsyncPlayer.setUsesWakeLock(super.mContext);
        final IAudioService interface1 = IAudioService$Stub.asInterface(ServiceManager.getService("audio"));
        this.mAudioService = interface1;
        try {
            interface1.setRingtonePlayer(this.mCallback);
        }
        catch (RemoteException obj) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Problem registering RingtonePlayer: ");
            sb.append(obj);
            Log.e("RingtonePlayer", sb.toString());
        }
    }
    
    private class Client implements IBinder$DeathRecipient
    {
        private final Ringtone mRingtone;
        private final IBinder mToken;
        
        Client(final IBinder mToken, final Uri uri, final UserHandle userHandle, final AudioAttributes audioAttributes, final VolumeShaper$Configuration volumeShaper$Configuration) {
            this.mToken = mToken;
            (this.mRingtone = new Ringtone(RingtonePlayer.this.getContextForUser(userHandle), false)).setAudioAttributes(audioAttributes);
            this.mRingtone.setUri(uri, volumeShaper$Configuration);
        }
        
        public void binderDied() {
            synchronized (RingtonePlayer.this.mClients) {
                RingtonePlayer.this.mClients.remove(this.mToken);
                // monitorexit(RingtonePlayer.access$100(this.this$0))
                this.mRingtone.stop();
            }
        }
    }
}
