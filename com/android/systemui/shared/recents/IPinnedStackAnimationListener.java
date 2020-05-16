// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.recents;

import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.os.RemoteException;
import android.os.IInterface;

public interface IPinnedStackAnimationListener extends IInterface
{
    void onPinnedStackAnimationStarted() throws RemoteException;
    
    public abstract static class Stub extends Binder implements IPinnedStackAnimationListener
    {
        public static IPinnedStackAnimationListener asInterface(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.android.systemui.shared.recents.IPinnedStackAnimationListener");
            if (queryLocalInterface != null && queryLocalInterface instanceof IPinnedStackAnimationListener) {
                return (IPinnedStackAnimationListener)queryLocalInterface;
            }
            return new Proxy(binder);
        }
        
        public static IPinnedStackAnimationListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
        
        private static class Proxy implements IPinnedStackAnimationListener
        {
            public static IPinnedStackAnimationListener sDefaultImpl;
            private IBinder mRemote;
            
            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }
            
            public IBinder asBinder() {
                return this.mRemote;
            }
            
            @Override
            public void onPinnedStackAnimationStarted() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.systemui.shared.recents.IPinnedStackAnimationListener");
                    if (!this.mRemote.transact(1, obtain, (Parcel)null, 1) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onPinnedStackAnimationStarted();
                    }
                }
                finally {
                    obtain.recycle();
                }
            }
        }
    }
}
