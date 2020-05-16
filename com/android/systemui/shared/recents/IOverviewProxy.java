// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.recents;

import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.os.Bundle;
import android.os.RemoteException;
import android.graphics.Region;
import android.os.IInterface;

public interface IOverviewProxy extends IInterface
{
    void onActiveNavBarRegionChanges(final Region p0) throws RemoteException;
    
    void onAssistantAvailable(final boolean p0) throws RemoteException;
    
    void onAssistantVisibilityChanged(final float p0) throws RemoteException;
    
    void onBackAction(final boolean p0, final int p1, final int p2, final boolean p3, final boolean p4) throws RemoteException;
    
    void onInitialize(final Bundle p0) throws RemoteException;
    
    void onOverviewHidden(final boolean p0, final boolean p1) throws RemoteException;
    
    void onOverviewShown(final boolean p0) throws RemoteException;
    
    void onOverviewToggle() throws RemoteException;
    
    void onSystemUiStateChanged(final int p0) throws RemoteException;
    
    void onTip(final int p0, final int p1) throws RemoteException;
    
    public abstract static class Stub extends Binder implements IOverviewProxy
    {
        public static IOverviewProxy asInterface(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.android.systemui.shared.recents.IOverviewProxy");
            if (queryLocalInterface != null && queryLocalInterface instanceof IOverviewProxy) {
                return (IOverviewProxy)queryLocalInterface;
            }
            return new Proxy(binder);
        }
        
        public static IOverviewProxy getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
        
        private static class Proxy implements IOverviewProxy
        {
            public static IOverviewProxy sDefaultImpl;
            private IBinder mRemote;
            
            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }
            
            public IBinder asBinder() {
                return this.mRemote;
            }
            
            @Override
            public void onActiveNavBarRegionChanges(final Region region) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.systemui.shared.recents.IOverviewProxy");
                    if (region != null) {
                        obtain.writeInt(1);
                        region.writeToParcel(obtain, 0);
                    }
                    else {
                        obtain.writeInt(0);
                    }
                    if (!this.mRemote.transact(12, obtain, (Parcel)null, 1) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onActiveNavBarRegionChanges(region);
                    }
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onAssistantAvailable(final boolean b) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.systemui.shared.recents.IOverviewProxy");
                    int n;
                    if (b) {
                        n = 1;
                    }
                    else {
                        n = 0;
                    }
                    obtain.writeInt(n);
                    if (!this.mRemote.transact(14, obtain, (Parcel)null, 1) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onAssistantAvailable(b);
                    }
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onAssistantVisibilityChanged(final float n) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.systemui.shared.recents.IOverviewProxy");
                    obtain.writeFloat(n);
                    if (!this.mRemote.transact(15, obtain, (Parcel)null, 1) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onAssistantVisibilityChanged(n);
                    }
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onBackAction(final boolean b, final int n, final int n2, final boolean b2, final boolean b3) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.systemui.shared.recents.IOverviewProxy");
                    final int n3 = 0;
                    int n4;
                    if (b) {
                        n4 = 1;
                    }
                    else {
                        n4 = 0;
                    }
                    obtain.writeInt(n4);
                    obtain.writeInt(n);
                    obtain.writeInt(n2);
                    int n5;
                    if (b2) {
                        n5 = 1;
                    }
                    else {
                        n5 = 0;
                    }
                    obtain.writeInt(n5);
                    int n6 = n3;
                    if (b3) {
                        n6 = 1;
                    }
                    obtain.writeInt(n6);
                    if (!this.mRemote.transact(16, obtain, (Parcel)null, 1) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onBackAction(b, n, n2, b2, b3);
                    }
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onInitialize(final Bundle bundle) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.systemui.shared.recents.IOverviewProxy");
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    }
                    else {
                        obtain.writeInt(0);
                    }
                    if (!this.mRemote.transact(13, obtain, (Parcel)null, 1) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onInitialize(bundle);
                    }
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onOverviewHidden(final boolean b, final boolean b2) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.systemui.shared.recents.IOverviewProxy");
                    final int n = 0;
                    int n2;
                    if (b) {
                        n2 = 1;
                    }
                    else {
                        n2 = 0;
                    }
                    obtain.writeInt(n2);
                    int n3 = n;
                    if (b2) {
                        n3 = 1;
                    }
                    obtain.writeInt(n3);
                    if (!this.mRemote.transact(9, obtain, (Parcel)null, 1) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onOverviewHidden(b, b2);
                    }
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onOverviewShown(final boolean b) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.systemui.shared.recents.IOverviewProxy");
                    int n;
                    if (b) {
                        n = 1;
                    }
                    else {
                        n = 0;
                    }
                    obtain.writeInt(n);
                    if (!this.mRemote.transact(8, obtain, (Parcel)null, 1) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onOverviewShown(b);
                    }
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onOverviewToggle() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.systemui.shared.recents.IOverviewProxy");
                    if (!this.mRemote.transact(7, obtain, (Parcel)null, 1) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onOverviewToggle();
                    }
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onSystemUiStateChanged(final int n) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.systemui.shared.recents.IOverviewProxy");
                    obtain.writeInt(n);
                    if (!this.mRemote.transact(17, obtain, (Parcel)null, 1) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onSystemUiStateChanged(n);
                    }
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onTip(final int n, final int n2) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.systemui.shared.recents.IOverviewProxy");
                    obtain.writeInt(n);
                    obtain.writeInt(n2);
                    if (!this.mRemote.transact(11, obtain, (Parcel)null, 1) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onTip(n, n2);
                    }
                }
                finally {
                    obtain.recycle();
                }
            }
        }
    }
}
