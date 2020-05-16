// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.os.RemoteException;
import android.os.IInterface;

public interface IColumbusServiceGestureListener extends IInterface
{
    void onGestureProgress(final int p0) throws RemoteException;
    
    public abstract static class Stub extends Binder implements IColumbusServiceGestureListener
    {
        public static IColumbusServiceGestureListener asInterface(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.google.android.systemui.columbus.IColumbusServiceGestureListener");
            if (queryLocalInterface != null && queryLocalInterface instanceof IColumbusServiceGestureListener) {
                return (IColumbusServiceGestureListener)queryLocalInterface;
            }
            return new Proxy(binder);
        }
        
        public static IColumbusServiceGestureListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
        
        private static class Proxy implements IColumbusServiceGestureListener
        {
            public static IColumbusServiceGestureListener sDefaultImpl;
            private IBinder mRemote;
            
            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }
            
            public IBinder asBinder() {
                return this.mRemote;
            }
            
            @Override
            public void onGestureProgress(final int n) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.systemui.columbus.IColumbusServiceGestureListener");
                    obtain.writeInt(n);
                    if (!this.mRemote.transact(1, obtain, (Parcel)null, 1) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onGestureProgress(n);
                    }
                }
                finally {
                    obtain.recycle();
                }
            }
        }
    }
}
