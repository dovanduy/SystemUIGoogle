// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.recents;

import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.view.MotionEvent;
import android.os.Bundle;
import android.graphics.Insets;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.graphics.Rect;
import android.os.IInterface;

public interface ISystemUiProxy extends IInterface
{
    Rect getNonMinimizedSplitScreenSecondaryBounds() throws RemoteException;
    
    void handleImageAsScreenshot(final Bitmap p0, final Rect p1, final Insets p2, final int p3) throws RemoteException;
    
    Bundle monitorGestureInput(final String p0, final int p1) throws RemoteException;
    
    void notifyAccessibilityButtonClicked(final int p0) throws RemoteException;
    
    void notifyAccessibilityButtonLongClicked() throws RemoteException;
    
    void notifySwipeToHomeFinished() throws RemoteException;
    
    void onAssistantGestureCompletion(final float p0) throws RemoteException;
    
    void onAssistantProgress(final float p0) throws RemoteException;
    
    void onOverviewShown(final boolean p0) throws RemoteException;
    
    void onQuickSwitchToNewTask(final int p0) throws RemoteException;
    
    void onSplitScreenInvoked() throws RemoteException;
    
    void onStatusBarMotionEvent(final MotionEvent p0) throws RemoteException;
    
    void setBackButtonAlpha(final float p0, final boolean p1) throws RemoteException;
    
    void setNavBarButtonAlpha(final float p0, final boolean p1) throws RemoteException;
    
    void setPinnedStackAnimationListener(final IPinnedStackAnimationListener p0) throws RemoteException;
    
    void setShelfHeight(final boolean p0, final int p1) throws RemoteException;
    
    void setSplitScreenMinimized(final boolean p0) throws RemoteException;
    
    void startAssistant(final Bundle p0) throws RemoteException;
    
    void startScreenPinning(final int p0) throws RemoteException;
    
    void stopScreenPinning() throws RemoteException;
    
    public abstract static class Stub extends Binder implements ISystemUiProxy
    {
        public Stub() {
            this.attachInterface((IInterface)this, "com.android.systemui.shared.recents.ISystemUiProxy");
        }
        
        public IBinder asBinder() {
            return (IBinder)this;
        }
        
        public boolean onTransact(final int n, final Parcel parcel, final Parcel parcel2, final int n2) throws RemoteException {
            if (n == 2) {
                parcel.enforceInterface("com.android.systemui.shared.recents.ISystemUiProxy");
                this.startScreenPinning(parcel.readInt());
                parcel2.writeNoException();
                return true;
            }
            if (n == 1598968902) {
                parcel2.writeString("com.android.systemui.shared.recents.ISystemUiProxy");
                return true;
            }
            Bundle bundle = null;
            final MotionEvent motionEvent = null;
            Insets insets = null;
            final boolean b = false;
            final boolean b2 = false;
            final boolean b3 = false;
            final boolean b4 = false;
            boolean splitScreenMinimized = false;
            switch (n) {
                default: {
                    switch (n) {
                        default: {
                            return super.onTransact(n, parcel, parcel2, n2);
                        }
                        case 26: {
                            parcel.enforceInterface("com.android.systemui.shared.recents.ISystemUiProxy");
                            this.onQuickSwitchToNewTask(parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        }
                        case 25: {
                            parcel.enforceInterface("com.android.systemui.shared.recents.ISystemUiProxy");
                            this.setPinnedStackAnimationListener(IPinnedStackAnimationListener.Stub.asInterface(parcel.readStrongBinder()));
                            parcel2.writeNoException();
                            return true;
                        }
                        case 24: {
                            parcel.enforceInterface("com.android.systemui.shared.recents.ISystemUiProxy");
                            this.notifySwipeToHomeFinished();
                            parcel2.writeNoException();
                            return true;
                        }
                        case 23: {
                            parcel.enforceInterface("com.android.systemui.shared.recents.ISystemUiProxy");
                            if (parcel.readInt() != 0) {
                                splitScreenMinimized = true;
                            }
                            this.setSplitScreenMinimized(splitScreenMinimized);
                            parcel2.writeNoException();
                            return true;
                        }
                        case 22: {
                            parcel.enforceInterface("com.android.systemui.shared.recents.ISystemUiProxy");
                            Bitmap bitmap;
                            if (parcel.readInt() != 0) {
                                bitmap = (Bitmap)Bitmap.CREATOR.createFromParcel(parcel);
                            }
                            else {
                                bitmap = null;
                            }
                            Rect rect;
                            if (parcel.readInt() != 0) {
                                rect = (Rect)Rect.CREATOR.createFromParcel(parcel);
                            }
                            else {
                                rect = null;
                            }
                            if (parcel.readInt() != 0) {
                                insets = (Insets)Insets.CREATOR.createFromParcel(parcel);
                            }
                            this.handleImageAsScreenshot(bitmap, rect, insets, parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        }
                        case 21: {
                            parcel.enforceInterface("com.android.systemui.shared.recents.ISystemUiProxy");
                            boolean b5 = b;
                            if (parcel.readInt() != 0) {
                                b5 = true;
                            }
                            this.setShelfHeight(b5, parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        }
                        case 20: {
                            parcel.enforceInterface("com.android.systemui.shared.recents.ISystemUiProxy");
                            final float float1 = parcel.readFloat();
                            boolean b6 = b2;
                            if (parcel.readInt() != 0) {
                                b6 = true;
                            }
                            this.setNavBarButtonAlpha(float1, b6);
                            parcel2.writeNoException();
                            return true;
                        }
                        case 19: {
                            parcel.enforceInterface("com.android.systemui.shared.recents.ISystemUiProxy");
                            this.onAssistantGestureCompletion(parcel.readFloat());
                            parcel2.writeNoException();
                            return true;
                        }
                        case 18: {
                            parcel.enforceInterface("com.android.systemui.shared.recents.ISystemUiProxy");
                            this.stopScreenPinning();
                            parcel2.writeNoException();
                            return true;
                        }
                        case 17: {
                            parcel.enforceInterface("com.android.systemui.shared.recents.ISystemUiProxy");
                            this.notifyAccessibilityButtonLongClicked();
                            parcel2.writeNoException();
                            return true;
                        }
                        case 16: {
                            parcel.enforceInterface("com.android.systemui.shared.recents.ISystemUiProxy");
                            this.notifyAccessibilityButtonClicked(parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        }
                        case 15: {
                            parcel.enforceInterface("com.android.systemui.shared.recents.ISystemUiProxy");
                            final Bundle monitorGestureInput = this.monitorGestureInput(parcel.readString(), parcel.readInt());
                            parcel2.writeNoException();
                            if (monitorGestureInput != null) {
                                parcel2.writeInt(1);
                                monitorGestureInput.writeToParcel(parcel2, 1);
                            }
                            else {
                                parcel2.writeInt(0);
                            }
                            return true;
                        }
                        case 14: {
                            parcel.enforceInterface("com.android.systemui.shared.recents.ISystemUiProxy");
                            if (parcel.readInt() != 0) {
                                bundle = (Bundle)Bundle.CREATOR.createFromParcel(parcel);
                            }
                            this.startAssistant(bundle);
                            parcel2.writeNoException();
                            return true;
                        }
                        case 13: {
                            parcel.enforceInterface("com.android.systemui.shared.recents.ISystemUiProxy");
                            this.onAssistantProgress(parcel.readFloat());
                            parcel2.writeNoException();
                            return true;
                        }
                    }
                    break;
                }
                case 10: {
                    parcel.enforceInterface("com.android.systemui.shared.recents.ISystemUiProxy");
                    MotionEvent motionEvent2 = motionEvent;
                    if (parcel.readInt() != 0) {
                        motionEvent2 = (MotionEvent)MotionEvent.CREATOR.createFromParcel(parcel);
                    }
                    this.onStatusBarMotionEvent(motionEvent2);
                    parcel2.writeNoException();
                    return true;
                }
                case 9: {
                    parcel.enforceInterface("com.android.systemui.shared.recents.ISystemUiProxy");
                    final float float2 = parcel.readFloat();
                    boolean b7 = b3;
                    if (parcel.readInt() != 0) {
                        b7 = true;
                    }
                    this.setBackButtonAlpha(float2, b7);
                    parcel2.writeNoException();
                    return true;
                }
                case 8: {
                    parcel.enforceInterface("com.android.systemui.shared.recents.ISystemUiProxy");
                    final Rect nonMinimizedSplitScreenSecondaryBounds = this.getNonMinimizedSplitScreenSecondaryBounds();
                    parcel2.writeNoException();
                    if (nonMinimizedSplitScreenSecondaryBounds != null) {
                        parcel2.writeInt(1);
                        nonMinimizedSplitScreenSecondaryBounds.writeToParcel(parcel2, 1);
                    }
                    else {
                        parcel2.writeInt(0);
                    }
                    return true;
                }
                case 7: {
                    parcel.enforceInterface("com.android.systemui.shared.recents.ISystemUiProxy");
                    boolean b8 = b4;
                    if (parcel.readInt() != 0) {
                        b8 = true;
                    }
                    this.onOverviewShown(b8);
                    parcel2.writeNoException();
                    return true;
                }
                case 6: {
                    parcel.enforceInterface("com.android.systemui.shared.recents.ISystemUiProxy");
                    this.onSplitScreenInvoked();
                    parcel2.writeNoException();
                    return true;
                }
            }
        }
    }
}
