// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.volume;

import android.view.GestureDetector$OnGestureListener;
import android.os.Handler;
import com.android.systemui.R$drawable;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import android.widget.ImageButton;
import androidx.core.view.accessibility.AccessibilityViewCommand;
import android.view.View;
import com.android.systemui.R$string;
import android.view.MotionEvent;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.R$attr;
import android.view.GestureDetector$SimpleOnGestureListener;
import android.view.GestureDetector;
import com.android.keyguard.AlphaOptimizedImageButton;

public class CaptionsToggleImageButton extends AlphaOptimizedImageButton
{
    private static final int[] OPTED_OUT_STATE;
    private boolean mCaptionsEnabled;
    private ConfirmedTapListener mConfirmedTapListener;
    private GestureDetector mGestureDetector;
    private GestureDetector$SimpleOnGestureListener mGestureListener;
    private boolean mOptedOut;
    
    static {
        OPTED_OUT_STATE = new int[] { R$attr.optedOut };
    }
    
    public CaptionsToggleImageButton(final Context context, final AttributeSet set) {
        super(context, set);
        this.mCaptionsEnabled = false;
        this.mOptedOut = false;
        this.mGestureListener = new GestureDetector$SimpleOnGestureListener() {
            public boolean onSingleTapConfirmed(final MotionEvent motionEvent) {
                return CaptionsToggleImageButton.this.tryToSendTapConfirmedEvent();
            }
        };
        this.setContentDescription((CharSequence)this.getContext().getString(R$string.volume_odi_captions_content_description));
    }
    
    private boolean tryToSendTapConfirmedEvent() {
        final ConfirmedTapListener mConfirmedTapListener = this.mConfirmedTapListener;
        if (mConfirmedTapListener != null) {
            mConfirmedTapListener.onConfirmedTap();
            return true;
        }
        return false;
    }
    
    boolean getCaptionsEnabled() {
        return this.mCaptionsEnabled;
    }
    
    boolean getOptedOut() {
        return this.mOptedOut;
    }
    
    public int[] onCreateDrawableState(final int n) {
        final int[] onCreateDrawableState = super.onCreateDrawableState(n + 1);
        if (this.mOptedOut) {
            ImageButton.mergeDrawableStates(onCreateDrawableState, CaptionsToggleImageButton.OPTED_OUT_STATE);
        }
        return onCreateDrawableState;
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        final GestureDetector mGestureDetector = this.mGestureDetector;
        if (mGestureDetector != null) {
            mGestureDetector.onTouchEvent(motionEvent);
        }
        return super.onTouchEvent(motionEvent);
    }
    
    Runnable setCaptionsEnabled(final boolean mCaptionsEnabled) {
        this.mCaptionsEnabled = mCaptionsEnabled;
        final AccessibilityNodeInfoCompat.AccessibilityActionCompat action_CLICK = AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK;
        String s;
        if (mCaptionsEnabled) {
            s = this.getContext().getString(R$string.volume_odi_captions_hint_disable);
        }
        else {
            s = this.getContext().getString(R$string.volume_odi_captions_hint_enable);
        }
        ViewCompat.replaceAccessibilityAction((View)this, action_CLICK, s, new _$$Lambda$CaptionsToggleImageButton$G1CrD_3iT19JR_3d_rnIgC4b3Mg(this));
        int imageResourceAsync;
        if (this.mCaptionsEnabled) {
            imageResourceAsync = R$drawable.ic_volume_odi_captions;
        }
        else {
            imageResourceAsync = R$drawable.ic_volume_odi_captions_disabled;
        }
        return this.setImageResourceAsync(imageResourceAsync);
    }
    
    void setOnConfirmedTapListener(final ConfirmedTapListener mConfirmedTapListener, final Handler handler) {
        this.mConfirmedTapListener = mConfirmedTapListener;
        if (this.mGestureDetector == null) {
            this.mGestureDetector = new GestureDetector(this.getContext(), (GestureDetector$OnGestureListener)this.mGestureListener, handler);
        }
    }
    
    void setOptedOut(final boolean mOptedOut) {
        this.mOptedOut = mOptedOut;
        this.refreshDrawableState();
    }
    
    interface ConfirmedTapListener
    {
        void onConfirmedTap();
    }
}
