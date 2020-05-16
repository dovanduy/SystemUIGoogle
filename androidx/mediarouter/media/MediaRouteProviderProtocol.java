// 
// Decompiled by Procyon v0.5.36
// 

package androidx.mediarouter.media;

import android.os.IBinder;
import android.os.Messenger;

abstract class MediaRouteProviderProtocol
{
    public static boolean isValidRemoteMessenger(final Messenger messenger) {
        boolean b2;
        final boolean b = b2 = false;
        if (messenger == null) {
            return b2;
        }
        try {
            final IBinder binder = messenger.getBinder();
            b2 = b;
            if (binder != null) {
                b2 = true;
            }
            return b2;
        }
        catch (NullPointerException ex) {
            b2 = b;
            return b2;
        }
    }
}
