// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra;

import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.os.RemoteException;
import android.os.IInterface;

public interface IElmyraServiceGestureListener extends IInterface
{
    void onGestureDetected() throws RemoteException;
    
    void onGestureProgress(final float p0, final int p1) throws RemoteException;
    
    public abstract static class Stub extends Binder implements IElmyraServiceGestureListener
    {
        public static IElmyraServiceGestureListener asInterface(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.google.android.systemui.elmyra.IElmyraServiceGestureListener");
            if (queryLocalInterface != null && queryLocalInterface instanceof IElmyraServiceGestureListener) {
                return (IElmyraServiceGestureListener)queryLocalInterface;
            }
            return new Proxy(binder);
        }
        
        public static IElmyraServiceGestureListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
        
        private static class Proxy implements IElmyraServiceGestureListener
        {
            public static IElmyraServiceGestureListener sDefaultImpl;
            private IBinder mRemote;
            
            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }
            
            public IBinder asBinder() {
                return this.mRemote;
            }
            
            @Override
            public void onGestureDetected() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.systemui.elmyra.IElmyraServiceGestureListener");
                    if (!this.mRemote.transact(2, obtain, (Parcel)null, 1) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onGestureDetected();
                    }
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onGestureProgress(final float n, final int n2) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.systemui.elmyra.IElmyraServiceGestureListener");
                    obtain.writeFloat(n);
                    obtain.writeInt(n2);
                    if (!this.mRemote.transact(1, obtain, (Parcel)null, 1) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onGestureProgress(n, n2);
                    }
                }
                finally {
                    obtain.recycle();
                }
            }
        }
    }
}
