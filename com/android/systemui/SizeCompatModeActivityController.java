// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.widget.Button;
import android.widget.PopupWindow$OnDismissListener;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.WindowManager$InvalidDisplayException;
import android.view.ViewGroup$LayoutParams;
import android.view.WindowManager;
import android.os.RemoteException;
import android.app.ActivityTaskManager;
import android.view.View;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.GradientDrawable;
import android.content.res.ColorStateList;
import android.view.WindowManager$LayoutParams;
import android.widget.PopupWindow;
import android.view.View$OnLongClickListener;
import android.view.View$OnClickListener;
import android.widget.ImageButton;
import android.util.Log;
import android.view.Display;
import android.hardware.display.DisplayManager;
import com.android.internal.annotations.VisibleForTesting;
import android.os.IBinder;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import android.content.Context;
import java.lang.ref.WeakReference;
import android.util.SparseArray;
import com.android.systemui.statusbar.CommandQueue;

public class SizeCompatModeActivityController extends SystemUI implements Callbacks
{
    private final SparseArray<RestartActivityButton> mActiveButtons;
    private final CommandQueue mCommandQueue;
    private final SparseArray<WeakReference<Context>> mDisplayContextCache;
    private boolean mHasShownHint;
    
    @VisibleForTesting
    SizeCompatModeActivityController(final Context context, final ActivityManagerWrapper activityManagerWrapper, final CommandQueue mCommandQueue) {
        super(context);
        this.mActiveButtons = (SparseArray<RestartActivityButton>)new SparseArray(1);
        this.mDisplayContextCache = (SparseArray<WeakReference<Context>>)new SparseArray(0);
        this.mCommandQueue = mCommandQueue;
        activityManagerWrapper.registerTaskStackListener(new TaskStackChangeListener() {
            @Override
            public void onSizeCompatModeActivityChanged(final int n, final IBinder binder) {
                SizeCompatModeActivityController.this.updateRestartButton(n, binder);
            }
        });
    }
    
    private Context getOrCreateDisplayContext(final int n) {
        if (n == 0) {
            return super.mContext;
        }
        Context context = null;
        final WeakReference weakReference = (WeakReference)this.mDisplayContextCache.get(n);
        if (weakReference != null) {
            context = weakReference.get();
        }
        Context displayContext;
        if ((displayContext = context) == null) {
            final Display display = ((DisplayManager)super.mContext.getSystemService((Class)DisplayManager.class)).getDisplay(n);
            displayContext = context;
            if (display != null) {
                displayContext = super.mContext.createDisplayContext(display);
                this.mDisplayContextCache.put(n, (Object)new WeakReference(displayContext));
            }
        }
        return displayContext;
    }
    
    private void removeRestartButton(final int n) {
        final RestartActivityButton restartActivityButton = (RestartActivityButton)this.mActiveButtons.get(n);
        if (restartActivityButton != null) {
            restartActivityButton.remove();
            this.mActiveButtons.remove(n);
        }
    }
    
    private void updateRestartButton(final int i, final IBinder binder) {
        if (binder == null) {
            this.removeRestartButton(i);
            return;
        }
        final RestartActivityButton restartActivityButton = (RestartActivityButton)this.mActiveButtons.get(i);
        if (restartActivityButton != null) {
            restartActivityButton.updateLastTargetActivity(binder);
            return;
        }
        final Context orCreateDisplayContext = this.getOrCreateDisplayContext(i);
        if (orCreateDisplayContext == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Cannot get context for display ");
            sb.append(i);
            Log.i("SizeCompatMode", sb.toString());
            return;
        }
        final RestartActivityButton restartButton = this.createRestartButton(orCreateDisplayContext);
        restartButton.updateLastTargetActivity(binder);
        if (restartButton.show()) {
            this.mActiveButtons.append(i, (Object)restartButton);
        }
        else {
            this.onDisplayRemoved(i);
        }
    }
    
    @VisibleForTesting
    RestartActivityButton createRestartButton(final Context context) {
        final RestartActivityButton restartActivityButton = new RestartActivityButton(context, this.mHasShownHint);
        this.mHasShownHint = true;
        return restartActivityButton;
    }
    
    @Override
    public void onDisplayRemoved(final int n) {
        this.mDisplayContextCache.remove(n);
        this.removeRestartButton(n);
    }
    
    @Override
    public void setImeWindowStatus(int n, final IBinder binder, int visibility, int n2, final boolean b) {
        final RestartActivityButton restartActivityButton = (RestartActivityButton)this.mActiveButtons.get(n);
        if (restartActivityButton == null) {
            return;
        }
        n2 = 0;
        if ((visibility & 0x2) != 0x0) {
            n = 1;
        }
        else {
            n = 0;
        }
        visibility = n2;
        if (n != 0) {
            visibility = 8;
        }
        if (restartActivityButton.getVisibility() != visibility) {
            restartActivityButton.setVisibility(visibility);
        }
    }
    
    @Override
    public void start() {
        this.mCommandQueue.addCallback((CommandQueue.Callbacks)this);
    }
    
    @VisibleForTesting
    static class RestartActivityButton extends ImageButton implements View$OnClickListener, View$OnLongClickListener
    {
        IBinder mLastActivityToken;
        final int mPopupOffsetX;
        final int mPopupOffsetY;
        final boolean mShouldShowHint;
        PopupWindow mShowingHint;
        final WindowManager$LayoutParams mWinParams;
        
        RestartActivityButton(final Context context, final boolean b) {
            super(context);
            this.mShouldShowHint = (b ^ true);
            final Drawable drawable = context.getDrawable(R$drawable.btn_restart);
            this.setImageDrawable(drawable);
            this.setContentDescription((CharSequence)context.getString(R$string.restart_button_description));
            final int intrinsicWidth = drawable.getIntrinsicWidth();
            final int intrinsicHeight = drawable.getIntrinsicHeight();
            this.mPopupOffsetX = intrinsicWidth / 2;
            final int n = intrinsicHeight * 2;
            this.mPopupOffsetY = n;
            final ColorStateList value = ColorStateList.valueOf(-3355444);
            final GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(1);
            gradientDrawable.setColor(value);
            this.setBackground((Drawable)new RippleDrawable(value, (Drawable)null, (Drawable)gradientDrawable));
            this.setOnClickListener((View$OnClickListener)this);
            this.setOnLongClickListener((View$OnLongClickListener)this);
            final WindowManager$LayoutParams mWinParams = new WindowManager$LayoutParams();
            this.mWinParams = mWinParams;
            mWinParams.gravity = getGravity(this.getResources().getConfiguration().getLayoutDirection());
            final WindowManager$LayoutParams mWinParams2 = this.mWinParams;
            mWinParams2.width = intrinsicWidth * 2;
            mWinParams2.height = n;
            mWinParams2.type = 2038;
            mWinParams2.flags = 40;
            mWinParams2.format = -3;
            mWinParams2.privateFlags |= 0x10;
            final StringBuilder sb = new StringBuilder();
            sb.append(SizeCompatModeActivityController.class.getSimpleName());
            sb.append(context.getDisplayId());
            mWinParams2.setTitle((CharSequence)sb.toString());
        }
        
        private static int getGravity(int n) {
            if (n == 1) {
                n = 8388611;
            }
            else {
                n = 8388613;
            }
            return n | 0x50;
        }
        
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (this.mShouldShowHint) {
                this.showHint();
            }
        }
        
        public void onClick(final View view) {
            try {
                ActivityTaskManager.getService().restartActivityProcessIfVisible(this.mLastActivityToken);
            }
            catch (RemoteException ex) {
                Log.w("SizeCompatMode", "Unable to restart activity", (Throwable)ex);
            }
        }
        
        public boolean onLongClick(final View view) {
            this.showHint();
            return true;
        }
        
        void remove() {
            final PopupWindow mShowingHint = this.mShowingHint;
            if (mShowingHint != null) {
                mShowingHint.dismiss();
            }
            ((WindowManager)this.getContext().getSystemService((Class)WindowManager.class)).removeViewImmediate((View)this);
        }
        
        public void setLayoutDirection(final int layoutDirection) {
            final int gravity = getGravity(layoutDirection);
            final WindowManager$LayoutParams mWinParams = this.mWinParams;
            if (mWinParams.gravity != gravity) {
                mWinParams.gravity = gravity;
                final PopupWindow mShowingHint = this.mShowingHint;
                if (mShowingHint != null) {
                    mShowingHint.dismiss();
                    this.showHint();
                }
                ((WindowManager)this.getContext().getSystemService((Class)WindowManager.class)).updateViewLayout((View)this, (ViewGroup$LayoutParams)this.mWinParams);
            }
            super.setLayoutDirection(layoutDirection);
        }
        
        boolean show() {
            try {
                ((WindowManager)this.getContext().getSystemService((Class)WindowManager.class)).addView((View)this, (ViewGroup$LayoutParams)this.mWinParams);
                return true;
            }
            catch (WindowManager$InvalidDisplayException ex) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Cannot show on display ");
                sb.append(this.getContext().getDisplayId());
                Log.w("SizeCompatMode", sb.toString(), (Throwable)ex);
                return false;
            }
        }
        
        void showHint() {
            if (this.mShowingHint != null) {
                return;
            }
            final View inflate = LayoutInflater.from(this.getContext()).inflate(R$layout.size_compat_mode_hint, (ViewGroup)null);
            final PopupWindow mShowingHint = new PopupWindow(inflate, -2, -2);
            mShowingHint.setWindowLayoutType(this.mWinParams.type);
            mShowingHint.setElevation(this.getResources().getDimension(R$dimen.bubble_elevation));
            mShowingHint.setAnimationStyle(16973910);
            mShowingHint.setClippingEnabled(false);
            mShowingHint.setOnDismissListener((PopupWindow$OnDismissListener)new _$$Lambda$SizeCompatModeActivityController$RestartActivityButton$rxc8GUe9hnz5kAfzl4xmCIiwi3Y(this));
            this.mShowingHint = mShowingHint;
            final Button button = (Button)inflate.findViewById(R$id.got_it);
            button.setBackground((Drawable)new RippleDrawable(ColorStateList.valueOf(-3355444), (Drawable)null, (Drawable)null));
            button.setOnClickListener((View$OnClickListener)new _$$Lambda$SizeCompatModeActivityController$RestartActivityButton$tZJkvUnAETgfbkQvNUGL2mQWd9s(mShowingHint));
            mShowingHint.showAtLocation((View)this, this.mWinParams.gravity, this.mPopupOffsetX, this.mPopupOffsetY);
        }
        
        void updateLastTargetActivity(final IBinder mLastActivityToken) {
            this.mLastActivityToken = mLastActivityToken;
        }
    }
}
