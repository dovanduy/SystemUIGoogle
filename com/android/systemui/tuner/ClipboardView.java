// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import android.view.View;
import android.view.View$DragShadowBuilder;
import android.view.MotionEvent;
import com.android.systemui.R$drawable;
import android.view.DragEvent;
import android.util.AttributeSet;
import android.content.Context;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ClipboardManager$OnPrimaryClipChangedListener;
import android.widget.ImageView;

public class ClipboardView extends ImageView implements ClipboardManager$OnPrimaryClipChangedListener
{
    private final ClipboardManager mClipboardManager;
    private ClipData mCurrentClip;
    
    public ClipboardView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mClipboardManager = (ClipboardManager)context.getSystemService((Class)ClipboardManager.class);
    }
    
    private void setBackgroundDragTarget(final boolean b) {
        int backgroundColor;
        if (b) {
            backgroundColor = 1308622847;
        }
        else {
            backgroundColor = 0;
        }
        this.setBackgroundColor(backgroundColor);
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.startListening();
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.stopListening();
    }
    
    public boolean onDragEvent(final DragEvent dragEvent) {
        final int action = dragEvent.getAction();
        if (action != 3) {
            if (action != 4) {
                if (action == 5) {
                    this.setBackgroundDragTarget(true);
                    return true;
                }
                if (action != 6) {
                    return true;
                }
            }
        }
        else {
            this.mClipboardManager.setPrimaryClip(dragEvent.getClipData());
        }
        this.setBackgroundDragTarget(false);
        return true;
    }
    
    public void onPrimaryClipChanged() {
        final ClipData primaryClip = this.mClipboardManager.getPrimaryClip();
        this.mCurrentClip = primaryClip;
        int imageResource;
        if (primaryClip != null) {
            imageResource = R$drawable.clipboard_full;
        }
        else {
            imageResource = R$drawable.clipboard_empty;
        }
        this.setImageResource(imageResource);
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0 && this.mCurrentClip != null) {
            this.startPocketDrag();
        }
        return super.onTouchEvent(motionEvent);
    }
    
    public void startListening() {
        this.mClipboardManager.addPrimaryClipChangedListener((ClipboardManager$OnPrimaryClipChangedListener)this);
        this.onPrimaryClipChanged();
    }
    
    public void startPocketDrag() {
        this.startDragAndDrop(this.mCurrentClip, new View$DragShadowBuilder((View)this), (Object)null, 256);
    }
    
    public void stopListening() {
        this.mClipboardManager.removePrimaryClipChangedListener((ClipboardManager$OnPrimaryClipChangedListener)this);
    }
}
