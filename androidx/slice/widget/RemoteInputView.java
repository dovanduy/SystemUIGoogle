// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.widget;

import androidx.core.widget.TextViewCompat;
import android.view.ActionMode$Callback;
import androidx.core.content.ContextCompat;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.CompletionInfo;
import android.graphics.Rect;
import android.os.Build$VERSION;
import android.graphics.drawable.Drawable;
import android.widget.EditText;
import android.app.PendingIntent$CanceledException;
import android.widget.Toast;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView$OnEditorActionListener;
import androidx.slice.view.R$id;
import android.animation.Animator;
import android.view.ViewAnimationUtils;
import android.view.View;
import android.text.Editable;
import androidx.slice.view.R$layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.ImageButton;
import android.app.RemoteInput;
import android.widget.ProgressBar;
import androidx.slice.SliceItem;
import android.text.TextWatcher;
import android.view.View$OnClickListener;
import android.widget.LinearLayout;

public class RemoteInputView extends LinearLayout implements View$OnClickListener, TextWatcher
{
    public static final Object VIEW_TAG;
    private SliceItem mAction;
    RemoteEditText mEditText;
    private ProgressBar mProgressBar;
    private RemoteInput mRemoteInput;
    private RemoteInput[] mRemoteInputs;
    private boolean mResetting;
    private int mRevealCx;
    private int mRevealCy;
    private int mRevealR;
    private ImageButton mSendButton;
    
    static {
        VIEW_TAG = new Object();
    }
    
    public RemoteInputView(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    private void focus() {
        this.setVisibility(0);
        this.mEditText.setInnerFocusable(true);
        final RemoteEditText mEditText = this.mEditText;
        mEditText.mShowImeOnInputConnection = true;
        mEditText.setSelection(mEditText.getText().length());
        this.mEditText.requestFocus();
        this.updateSendButton();
    }
    
    public static RemoteInputView inflate(final Context context, final ViewGroup viewGroup) {
        final RemoteInputView remoteInputView = (RemoteInputView)LayoutInflater.from(context).inflate(R$layout.abc_slice_remote_input, viewGroup, false);
        remoteInputView.setTag(RemoteInputView.VIEW_TAG);
        return remoteInputView;
    }
    
    public static final boolean isConfirmKey(final int n) {
        return n == 23 || n == 62 || n == 66 || n == 160;
    }
    
    private void reset() {
        this.mResetting = true;
        this.mEditText.getText().clear();
        this.mEditText.setEnabled(true);
        this.mSendButton.setVisibility(0);
        this.mProgressBar.setVisibility(4);
        this.updateSendButton();
        this.onDefocus();
        this.mResetting = false;
    }
    
    private void updateSendButton() {
        this.mSendButton.setEnabled(this.mEditText.getText().length() != 0);
    }
    
    public void afterTextChanged(final Editable editable) {
        this.updateSendButton();
    }
    
    public void beforeTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
    }
    
    public void dispatchFinishTemporaryDetach() {
        if (this.isAttachedToWindow()) {
            final RemoteEditText mEditText = this.mEditText;
            this.attachViewToParent((View)mEditText, 0, mEditText.getLayoutParams());
        }
        else {
            this.removeDetachedView((View)this.mEditText, false);
        }
        super.dispatchFinishTemporaryDetach();
    }
    
    public void dispatchStartTemporaryDetach() {
        super.dispatchStartTemporaryDetach();
        this.detachViewFromParent((View)this.mEditText);
    }
    
    public void focusAnimated() {
        if (this.getVisibility() != 0) {
            final Animator circularReveal = ViewAnimationUtils.createCircularReveal((View)this, this.mRevealCx, this.mRevealCy, 0.0f, (float)this.mRevealR);
            circularReveal.setDuration(200L);
            circularReveal.start();
        }
        this.focus();
    }
    
    public void onClick(final View view) {
        if (view == this.mSendButton) {
            this.sendRemoteInput();
        }
    }
    
    void onDefocus() {
        this.setVisibility(4);
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mProgressBar = (ProgressBar)this.findViewById(R$id.remote_input_progress);
        (this.mSendButton = (ImageButton)this.findViewById(R$id.remote_input_send)).setOnClickListener((View$OnClickListener)this);
        (this.mEditText = (RemoteEditText)this.getChildAt(0)).setOnEditorActionListener((TextView$OnEditorActionListener)new TextView$OnEditorActionListener() {
            public boolean onEditorAction(final TextView textView, int n, final KeyEvent keyEvent) {
                if (keyEvent == null && (n == 6 || n == 5 || n == 4)) {
                    n = 1;
                }
                else {
                    n = 0;
                }
                final boolean b = keyEvent != null && RemoteInputView.isConfirmKey(keyEvent.getKeyCode()) && keyEvent.getAction() == 0;
                if (n == 0 && !b) {
                    return false;
                }
                if (RemoteInputView.this.mEditText.length() > 0) {
                    RemoteInputView.this.sendRemoteInput();
                }
                return true;
            }
        });
        this.mEditText.addTextChangedListener((TextWatcher)this);
        this.mEditText.setInnerFocusable(false);
        this.mEditText.mRemoteInputView = this;
    }
    
    public boolean onRequestSendAccessibilityEvent(final View view, final AccessibilityEvent accessibilityEvent) {
        return (!this.mResetting || view != this.mEditText) && super.onRequestSendAccessibilityEvent(view, accessibilityEvent);
    }
    
    public void onTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        super.onTouchEvent(motionEvent);
        return true;
    }
    
    void sendRemoteInput() {
        final Bundle bundle = new Bundle();
        bundle.putString(this.mRemoteInput.getResultKey(), this.mEditText.getText().toString());
        final Intent addFlags = new Intent().addFlags(268435456);
        RemoteInput.addResultsToIntent(this.mRemoteInputs, addFlags, bundle);
        this.mEditText.setEnabled(false);
        this.mSendButton.setVisibility(4);
        this.mProgressBar.setVisibility(0);
        this.mEditText.mShowImeOnInputConnection = false;
        try {
            this.mAction.fireAction(this.getContext(), addFlags);
            this.reset();
        }
        catch (PendingIntent$CanceledException ex) {
            Log.i("RemoteInput", "Unable to send remote input result", (Throwable)ex);
            Toast.makeText(this.getContext(), (CharSequence)"Failure sending pending intent for inline reply :(", 0).show();
            this.reset();
        }
    }
    
    public void setAction(final SliceItem mAction) {
        this.mAction = mAction;
    }
    
    public void setRemoteInput(final RemoteInput[] mRemoteInputs, final RemoteInput mRemoteInput) {
        this.mRemoteInputs = mRemoteInputs;
        this.mRemoteInput = mRemoteInput;
        this.mEditText.setHint(mRemoteInput.getLabel());
    }
    
    public void setRevealParameters(final int mRevealCx, final int mRevealCy, final int mRevealR) {
        this.mRevealCx = mRevealCx;
        this.mRevealCy = mRevealCy;
        this.mRevealR = mRevealR;
    }
    
    public static class RemoteEditText extends EditText
    {
        private final Drawable mBackground;
        RemoteInputView mRemoteInputView;
        boolean mShowImeOnInputConnection;
        
        public RemoteEditText(final Context context, final AttributeSet set) {
            super(context, set);
            this.mBackground = this.getBackground();
        }
        
        private void defocusIfNeeded() {
            if (this.mRemoteInputView == null && !this.isTemporarilyDetachedCompat()) {
                if (this.isFocusable() && this.isEnabled()) {
                    this.setInnerFocusable(false);
                    final RemoteInputView mRemoteInputView = this.mRemoteInputView;
                    if (mRemoteInputView != null) {
                        mRemoteInputView.onDefocus();
                    }
                    this.mShowImeOnInputConnection = false;
                }
                return;
            }
            this.isTemporarilyDetachedCompat();
        }
        
        private boolean isTemporarilyDetachedCompat() {
            return Build$VERSION.SDK_INT >= 24 && this.isTemporarilyDetached();
        }
        
        public void getFocusedRect(final Rect rect) {
            super.getFocusedRect(rect);
            rect.top = this.getScrollY();
            rect.bottom = this.getScrollY() + (this.getBottom() - this.getTop());
        }
        
        public void onCommitCompletion(final CompletionInfo completionInfo) {
            this.clearComposingText();
            this.setText(completionInfo.getText());
            this.setSelection(this.getText().length());
        }
        
        public InputConnection onCreateInputConnection(final EditorInfo editorInfo) {
            final InputConnection onCreateInputConnection = super.onCreateInputConnection(editorInfo);
            if (this.mShowImeOnInputConnection && onCreateInputConnection != null) {
                final InputMethodManager inputMethodManager = ContextCompat.getSystemService(this.getContext(), InputMethodManager.class);
                if (inputMethodManager != null) {
                    this.post((Runnable)new Runnable() {
                        @Override
                        public void run() {
                            inputMethodManager.viewClicked((View)RemoteEditText.this);
                            inputMethodManager.showSoftInput((View)RemoteEditText.this, 0);
                        }
                    });
                }
            }
            return onCreateInputConnection;
        }
        
        protected void onFocusChanged(final boolean b, final int n, final Rect rect) {
            super.onFocusChanged(b, n, rect);
            if (!b) {
                this.defocusIfNeeded();
            }
        }
        
        public boolean onKeyDown(final int n, final KeyEvent keyEvent) {
            return n == 4 || super.onKeyDown(n, keyEvent);
        }
        
        public boolean onKeyUp(final int n, final KeyEvent keyEvent) {
            if (n == 4) {
                this.defocusIfNeeded();
                return true;
            }
            return super.onKeyUp(n, keyEvent);
        }
        
        protected void onVisibilityChanged(final View view, final int n) {
            super.onVisibilityChanged(view, n);
            if (!this.isShown()) {
                this.defocusIfNeeded();
            }
        }
        
        public void setCustomSelectionActionModeCallback(final ActionMode$Callback actionMode$Callback) {
            super.setCustomSelectionActionModeCallback(TextViewCompat.wrapCustomSelectionActionModeCallback((TextView)this, actionMode$Callback));
        }
        
        void setInnerFocusable(final boolean cursorVisible) {
            this.setFocusableInTouchMode(cursorVisible);
            this.setFocusable(cursorVisible);
            this.setCursorVisible(cursorVisible);
            if (cursorVisible) {
                this.requestFocus();
                this.setBackground(this.mBackground);
            }
            else {
                this.setBackground((Drawable)null);
            }
        }
    }
}
