// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.pip.phone;

import android.view.MotionEvent;
import java.io.PrintWriter;
import android.app.ActivityManager$StackInfo;
import android.os.RemoteException;
import android.os.UserHandle;
import android.app.ActivityOptions;
import android.os.Parcelable;
import android.content.Intent;
import android.app.ActivityTaskManager;
import android.graphics.Rect;
import android.util.Log;
import android.os.SystemClock;
import android.app.RemoteAction;
import java.util.List;
import java.util.function.Consumer;
import android.os.Message;
import android.os.Bundle;
import android.os.Messenger;
import java.util.ArrayList;
import com.android.systemui.shared.system.InputConsumerController;
import android.os.Handler;
import android.content.Context;
import android.content.pm.ParceledListSlice;

public class PipMenuActivityController
{
    private ParceledListSlice mAppActions;
    private Context mContext;
    private Handler mHandler;
    private InputConsumerController mInputConsumerController;
    private ArrayList<Listener> mListeners;
    private PipMediaController.ActionListener mMediaActionListener;
    private ParceledListSlice mMediaActions;
    private PipMediaController mMediaController;
    private int mMenuState;
    private Messenger mMessenger;
    private Runnable mOnAnimationEndRunnable;
    private boolean mStartActivityRequested;
    private long mStartActivityRequestedTime;
    private Runnable mStartActivityRequestedTimeoutRunnable;
    private Bundle mTmpDismissFractionData;
    private Messenger mToActivityMessenger;
    
    public PipMenuActivityController(final Context mContext, final PipMediaController mMediaController, final InputConsumerController mInputConsumerController) {
        this.mListeners = new ArrayList<Listener>();
        this.mTmpDismissFractionData = new Bundle();
        this.mHandler = new Handler() {
            public void handleMessage(final Message message) {
                final int what = message.what;
                final boolean b = true;
                boolean b2 = true;
                if (what != 100) {
                    if (what != 101) {
                        if (what != 103) {
                            if (what != 104) {
                                if (what == 107) {
                                    PipMenuActivityController.this.mListeners.forEach((Consumer)_$$Lambda$PipMenuActivityController$1$rDXDKqpw1CLC0fwevwYEng68Bps.INSTANCE);
                                }
                            }
                            else {
                                PipMenuActivityController.this.mToActivityMessenger = message.replyTo;
                                PipMenuActivityController.this.setStartActivityRequested(false);
                                if (PipMenuActivityController.this.mOnAnimationEndRunnable != null) {
                                    PipMenuActivityController.this.mOnAnimationEndRunnable.run();
                                    PipMenuActivityController.this.mOnAnimationEndRunnable = null;
                                }
                                if (PipMenuActivityController.this.mToActivityMessenger == null) {
                                    if (message.arg1 == 0) {
                                        b2 = false;
                                    }
                                    PipMenuActivityController.this.onMenuStateChanged(0, b2);
                                }
                            }
                        }
                        else {
                            PipMenuActivityController.this.mListeners.forEach((Consumer)_$$Lambda$PipMenuActivityController$1$o9fLqvuiKIYwdsSexRT0X4Ty0V4.INSTANCE);
                        }
                    }
                    else {
                        PipMenuActivityController.this.mListeners.forEach((Consumer)_$$Lambda$PipMenuActivityController$1$8btqC3E6FFjbjLWUhiNmbnKUlfI.INSTANCE);
                    }
                }
                else {
                    PipMenuActivityController.this.onMenuStateChanged(message.arg1, message.arg2 != 0 && b);
                }
            }
        };
        this.mMessenger = new Messenger(this.mHandler);
        this.mStartActivityRequestedTimeoutRunnable = new _$$Lambda$PipMenuActivityController$46Yr3xVHMZsGyZiGhSKF_IPBnzk(this);
        this.mMediaActionListener = new PipMediaController.ActionListener() {
            @Override
            public void onMediaActionsChanged(final List<RemoteAction> list) {
                PipMenuActivityController.this.mMediaActions = new ParceledListSlice((List)list);
                PipMenuActivityController.this.updateMenuActions();
            }
        };
        this.mContext = mContext;
        this.mMediaController = mMediaController;
        this.mInputConsumerController = mInputConsumerController;
    }
    
    private boolean isStartActivityRequestedElapsed() {
        return SystemClock.uptimeMillis() - this.mStartActivityRequestedTime >= 300L;
    }
    
    private boolean isValidActions(final ParceledListSlice parceledListSlice) {
        return parceledListSlice != null && parceledListSlice.getList().size() > 0;
    }
    
    private void onMenuStateChanged(final int mMenuState, final boolean b) {
        if (mMenuState != this.mMenuState) {
            this.mListeners.forEach(new _$$Lambda$PipMenuActivityController$oZuzXTzYX29YiUgUX8_q8QZcGtw(mMenuState, b));
            if (mMenuState == 2) {
                this.mMediaController.addListener(this.mMediaActionListener);
            }
            else {
                this.mMediaController.removeListener(this.mMediaActionListener);
            }
        }
        this.mMenuState = mMenuState;
    }
    
    private ParceledListSlice resolveMenuActions() {
        if (this.isValidActions(this.mAppActions)) {
            return this.mAppActions;
        }
        return this.mMediaActions;
    }
    
    private void setStartActivityRequested(final boolean mStartActivityRequested) {
        this.mHandler.removeCallbacks(this.mStartActivityRequestedTimeoutRunnable);
        this.mStartActivityRequested = mStartActivityRequested;
        long uptimeMillis;
        if (mStartActivityRequested) {
            uptimeMillis = SystemClock.uptimeMillis();
        }
        else {
            uptimeMillis = 0L;
        }
        this.mStartActivityRequestedTime = uptimeMillis;
    }
    
    private void startMenuActivity(final int n, final Rect rect, final Rect rect2, final boolean b, final boolean b2) {
        try {
            final ActivityManager$StackInfo stackInfo = ActivityTaskManager.getService().getStackInfo(2, 0);
            if (stackInfo != null && stackInfo.taskIds != null && stackInfo.taskIds.length > 0) {
                final Intent intent = new Intent(this.mContext, (Class)PipMenuActivity.class);
                intent.setFlags(268435456);
                intent.putExtra("messenger", (Parcelable)this.mMessenger);
                intent.putExtra("actions", (Parcelable)this.resolveMenuActions());
                if (rect != null) {
                    intent.putExtra("stack_bounds", (Parcelable)rect);
                }
                if (rect2 != null) {
                    intent.putExtra("movement_bounds", (Parcelable)rect2);
                }
                intent.putExtra("menu_state", n);
                intent.putExtra("allow_timeout", b);
                intent.putExtra("resize_menu_on_show", b2);
                final ActivityOptions customAnimation = ActivityOptions.makeCustomAnimation(this.mContext, 0, 0);
                customAnimation.setLaunchTaskId(stackInfo.taskIds[stackInfo.taskIds.length - 1]);
                customAnimation.setTaskOverlay(true, true);
                this.mContext.startActivityAsUser(intent, customAnimation.toBundle(), UserHandle.CURRENT);
                this.setStartActivityRequested(true);
            }
            else {
                Log.e("PipMenuActController", "No PIP tasks found");
            }
        }
        catch (RemoteException ex) {
            this.setStartActivityRequested(false);
            Log.e("PipMenuActController", "Error showing PIP menu activity", (Throwable)ex);
        }
    }
    
    private void updateMenuActions() {
        if (this.mToActivityMessenger != null) {
            final Parcelable parcelable = null;
            Object bounds;
            try {
                final ActivityManager$StackInfo stackInfo = ActivityTaskManager.getService().getStackInfo(2, 0);
                bounds = parcelable;
                if (stackInfo != null) {
                    bounds = stackInfo.bounds;
                }
            }
            catch (RemoteException ex) {
                Log.e("PipMenuActController", "Error showing PIP menu activity", (Throwable)ex);
                bounds = parcelable;
            }
            final Bundle obj = new Bundle();
            obj.putParcelable("stack_bounds", (Parcelable)bounds);
            obj.putParcelable("actions", (Parcelable)this.resolveMenuActions());
            final Message obtain = Message.obtain();
            obtain.what = 4;
            obtain.obj = obj;
            try {
                this.mToActivityMessenger.send(obtain);
            }
            catch (RemoteException ex2) {
                Log.e("PipMenuActController", "Could not notify menu activity to update actions", (Throwable)ex2);
            }
        }
    }
    
    public void addListener(final Listener listener) {
        if (!this.mListeners.contains(listener)) {
            this.mListeners.add(listener);
        }
    }
    
    public void dump(final PrintWriter printWriter, final String s) {
        final StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb.append("  ");
        final String string = sb.toString();
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(s);
        sb2.append("PipMenuActController");
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append(string);
        sb3.append("mMenuState=");
        sb3.append(this.mMenuState);
        printWriter.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append(string);
        sb4.append("mToActivityMessenger=");
        sb4.append(this.mToActivityMessenger);
        printWriter.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append(string);
        sb5.append("mListeners=");
        sb5.append(this.mListeners.size());
        printWriter.println(sb5.toString());
        final StringBuilder sb6 = new StringBuilder();
        sb6.append(string);
        sb6.append("mStartActivityRequested=");
        sb6.append(this.mStartActivityRequested);
        printWriter.println(sb6.toString());
        final StringBuilder sb7 = new StringBuilder();
        sb7.append(string);
        sb7.append("mStartActivityRequestedTime=");
        sb7.append(this.mStartActivityRequestedTime);
        printWriter.println(sb7.toString());
    }
    
    void handlePointerEvent(final MotionEvent obj) {
        if (this.mToActivityMessenger != null) {
            final Message obtain = Message.obtain();
            obtain.what = 7;
            obtain.obj = obj;
            try {
                this.mToActivityMessenger.send(obtain);
            }
            catch (RemoteException ex) {
                Log.e("PipMenuActController", "Could not dispatch touch event", (Throwable)ex);
            }
        }
    }
    
    public void hideMenu() {
        if (this.mToActivityMessenger != null) {
            final Message obtain = Message.obtain();
            obtain.what = 3;
            try {
                this.mToActivityMessenger.send(obtain);
            }
            catch (RemoteException ex) {
                Log.e("PipMenuActController", "Could not notify menu to hide", (Throwable)ex);
            }
        }
    }
    
    public void hideMenuWithoutResize() {
        this.onMenuStateChanged(0, false);
    }
    
    public boolean isMenuActivityVisible() {
        return this.mToActivityMessenger != null;
    }
    
    public void onActivityPinned() {
        this.mInputConsumerController.registerInputConsumer();
    }
    
    public void onActivityUnpinned() {
        this.hideMenu();
        this.mInputConsumerController.unregisterInputConsumer();
        this.setStartActivityRequested(false);
    }
    
    public void onPinnedStackAnimationEnded() {
        if (this.mToActivityMessenger != null) {
            final Message obtain = Message.obtain();
            obtain.what = 6;
            try {
                this.mToActivityMessenger.send(obtain);
            }
            catch (RemoteException ex) {
                Log.e("PipMenuActController", "Could not notify menu pinned animation ended", (Throwable)ex);
            }
        }
    }
    
    public void pokeMenu() {
        if (this.mToActivityMessenger != null) {
            final Message obtain = Message.obtain();
            obtain.what = 2;
            try {
                this.mToActivityMessenger.send(obtain);
            }
            catch (RemoteException ex) {
                Log.e("PipMenuActController", "Could not notify poke menu", (Throwable)ex);
            }
        }
    }
    
    public void setAppActions(final ParceledListSlice mAppActions) {
        this.mAppActions = mAppActions;
        this.updateMenuActions();
    }
    
    public void setDismissFraction(final float n) {
        if (this.mToActivityMessenger != null) {
            this.mTmpDismissFractionData.clear();
            this.mTmpDismissFractionData.putFloat("dismiss_fraction", n);
            final Message obtain = Message.obtain();
            obtain.what = 5;
            obtain.obj = this.mTmpDismissFractionData;
            try {
                this.mToActivityMessenger.send(obtain);
            }
            catch (RemoteException ex) {
                Log.e("PipMenuActController", "Could not notify menu to update dismiss fraction", (Throwable)ex);
            }
        }
        else if (!this.mStartActivityRequested || this.isStartActivityRequestedElapsed()) {
            this.startMenuActivity(0, null, null, false, false);
        }
    }
    
    public void showMenu(final int n, final Rect rect, final Rect rect2, final boolean b, final boolean b2) {
        if (this.mToActivityMessenger != null) {
            final Bundle obj = new Bundle();
            obj.putInt("menu_state", n);
            if (rect != null) {
                obj.putParcelable("stack_bounds", (Parcelable)rect);
            }
            obj.putParcelable("movement_bounds", (Parcelable)rect2);
            obj.putBoolean("allow_timeout", b);
            obj.putBoolean("resize_menu_on_show", b2);
            final Message obtain = Message.obtain();
            obtain.what = 1;
            obtain.obj = obj;
            try {
                this.mToActivityMessenger.send(obtain);
            }
            catch (RemoteException ex) {
                Log.e("PipMenuActController", "Could not notify menu to show", (Throwable)ex);
            }
        }
        else if (!this.mStartActivityRequested || this.isStartActivityRequestedElapsed()) {
            this.startMenuActivity(n, rect, rect2, b, b2);
        }
    }
    
    public interface Listener
    {
        void onPipDismiss();
        
        void onPipExpand();
        
        void onPipMenuStateChanged(final int p0, final boolean p1);
        
        void onPipShowMenu();
    }
}
