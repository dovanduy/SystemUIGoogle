// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus;

import android.os.Parcel;
import android.os.Binder;
import android.os.RemoteException;
import android.os.IBinder;
import android.os.IInterface;

public interface IColumbusService extends IInterface
{
    void registerGestureListener(final IBinder p0, final IBinder p1) throws RemoteException;
    
    void registerServiceListener(final IBinder p0, final IBinder p1) throws RemoteException;
    
    void triggerAction() throws RemoteException;
    
    public abstract static class Stub extends Binder implements IColumbusService
    {
        public Stub() {
            this.attachInterface((IInterface)this, "com.google.android.systemui.columbus.IColumbusService");
        }
        
        public static IColumbusService asInterface(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.google.android.systemui.columbus.IColumbusService");
            if (queryLocalInterface != null && queryLocalInterface instanceof IColumbusService) {
                return (IColumbusService)queryLocalInterface;
            }
            return new Proxy(binder);
        }
        
        public static IColumbusService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
        
        public IBinder asBinder() {
            return (IBinder)this;
        }
        
        public boolean onTransact(final int n, final Parcel parcel, final Parcel parcel2, final int n2) throws RemoteException {
            if (n == 1) {
                parcel.enforceInterface("com.google.android.systemui.columbus.IColumbusService");
                this.registerGestureListener(parcel.readStrongBinder(), parcel.readStrongBinder());
                return true;
            }
            if (n == 2) {
                parcel.enforceInterface("com.google.android.systemui.columbus.IColumbusService");
                this.triggerAction();
                return true;
            }
            if (n == 3) {
                parcel.enforceInterface("com.google.android.systemui.columbus.IColumbusService");
                this.registerServiceListener(parcel.readStrongBinder(), parcel.readStrongBinder());
                return true;
            }
            if (n != 1598968902) {
                return super.onTransact(n, parcel, parcel2, n2);
            }
            parcel2.writeString("com.google.android.systemui.columbus.IColumbusService");
            return true;
        }
        
        private static class Proxy implements IColumbusService
        {
            public static IColumbusService sDefaultImpl;
            private IBinder mRemote;
            
            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }
            
            public IBinder asBinder() {
                return this.mRemote;
            }
            
            @Override
            public void registerGestureListener(final IBinder binder, final IBinder binder2) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.systemui.columbus.IColumbusService");
                    obtain.writeStrongBinder(binder);
                    obtain.writeStrongBinder(binder2);
                    if (!this.mRemote.transact(1, obtain, (Parcel)null, 1) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().registerGestureListener(binder, binder2);
                    }
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void registerServiceListener(final IBinder binder, final IBinder binder2) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.systemui.columbus.IColumbusService");
                    obtain.writeStrongBinder(binder);
                    obtain.writeStrongBinder(binder2);
                    if (!this.mRemote.transact(3, obtain, (Parcel)null, 1) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().registerServiceListener(binder, binder2);
                    }
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void triggerAction() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.systemui.columbus.IColumbusService");
                    if (!this.mRemote.transact(2, obtain, (Parcel)null, 1) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().triggerAction();
                    }
                }
                finally {
                    obtain.recycle();
                }
            }
        }
    }
}
