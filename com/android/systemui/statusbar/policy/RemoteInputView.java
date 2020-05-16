// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.view.inputmethod.InputMethodManager;
import android.content.pm.PackageManager$NameNotFoundException;
import android.content.ClipDescription;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.CompletionInfo;
import android.graphics.Rect;
import com.android.systemui.statusbar.phone.LightBarController;
import android.graphics.drawable.Drawable;
import android.widget.EditText;
import android.app.Notification$Action;
import android.os.Bundle;
import com.android.systemui.R$string;
import java.util.Map;
import java.util.HashMap;
import android.net.Uri;
import android.view.accessibility.AccessibilityEvent;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView$OnEditorActionListener;
import com.android.systemui.R$id;
import android.text.Editable;
import android.app.PendingIntent$CanceledException;
import android.util.Log;
import android.content.pm.ShortcutManager;
import android.os.SystemClock;
import android.text.SpannedString;
import com.android.internal.logging.MetricsLogger;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import com.android.systemui.Interpolators;
import android.view.View;
import android.view.ViewAnimationUtils;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.app.ActivityManager;
import android.os.UserHandle;
import android.content.Intent;
import com.android.internal.statusbar.IStatusBarService$Stub;
import android.os.ServiceManager;
import com.android.systemui.Dependency;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper;
import android.widget.ImageButton;
import android.app.RemoteInput;
import android.widget.ProgressBar;
import android.app.PendingIntent;
import java.util.function.Consumer;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.RemoteInputController;
import android.text.TextWatcher;
import android.view.View$OnClickListener;
import android.widget.LinearLayout;

public class RemoteInputView extends LinearLayout implements View$OnClickListener, TextWatcher
{
    public static final Object VIEW_TAG;
    private RemoteInputController mController;
    private RemoteEditText mEditText;
    private NotificationEntry mEntry;
    private Consumer<Boolean> mOnVisibilityChangedListener;
    private PendingIntent mPendingIntent;
    private ProgressBar mProgressBar;
    private RemoteInput mRemoteInput;
    private RemoteInputQuickSettingsDisabler mRemoteInputQuickSettingsDisabler;
    private RemoteInput[] mRemoteInputs;
    private boolean mRemoved;
    private boolean mResetting;
    private int mRevealCx;
    private int mRevealCy;
    private int mRevealR;
    private ImageButton mSendButton;
    public final Object mToken;
    private NotificationViewWrapper mWrapper;
    
    static {
        VIEW_TAG = new Object();
    }
    
    public RemoteInputView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mToken = new Object();
        this.mRemoteInputQuickSettingsDisabler = Dependency.get(RemoteInputQuickSettingsDisabler.class);
        IStatusBarService$Stub.asInterface(ServiceManager.getService("statusbar"));
    }
    
    private static UserHandle computeTextOperationUser(final UserHandle userHandle) {
        UserHandle of = userHandle;
        if (UserHandle.ALL.equals((Object)userHandle)) {
            of = UserHandle.of(ActivityManager.getCurrentUser());
        }
        return of;
    }
    
    public static RemoteInputView inflate(final Context context, final ViewGroup viewGroup, final NotificationEntry mEntry, final RemoteInputController mController) {
        final RemoteInputView remoteInputView = (RemoteInputView)LayoutInflater.from(context).inflate(R$layout.remote_input, viewGroup, false);
        remoteInputView.mController = mController;
        remoteInputView.mEntry = mEntry;
        final UserHandle computeTextOperationUser = computeTextOperationUser(mEntry.getSbn().getUser());
        final RemoteEditText mEditText = remoteInputView.mEditText;
        mEditText.setTextOperationUser(mEditText.mUser = computeTextOperationUser);
        remoteInputView.setTag(RemoteInputView.VIEW_TAG);
        return remoteInputView;
    }
    
    private void onDefocus(final boolean b) {
        this.mController.removeRemoteInput(this.mEntry, this.mToken);
        this.mEntry.remoteInputText = (CharSequence)this.mEditText.getText();
        Label_0118: {
            if (!this.mRemoved) {
                if (b) {
                    final int mRevealR = this.mRevealR;
                    if (mRevealR > 0) {
                        final Animator circularReveal = ViewAnimationUtils.createCircularReveal((View)this, this.mRevealCx, this.mRevealCy, (float)mRevealR, 0.0f);
                        circularReveal.setInterpolator((TimeInterpolator)Interpolators.FAST_OUT_LINEAR_IN);
                        circularReveal.setDuration(150L);
                        circularReveal.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                            public void onAnimationEnd(final Animator animator) {
                                RemoteInputView.this.setVisibility(4);
                                if (RemoteInputView.this.mWrapper != null) {
                                    RemoteInputView.this.mWrapper.setRemoteInputVisible(false);
                                }
                            }
                        });
                        circularReveal.start();
                        break Label_0118;
                    }
                }
                this.setVisibility(4);
                final NotificationViewWrapper mWrapper = this.mWrapper;
                if (mWrapper != null) {
                    mWrapper.setRemoteInputVisible(false);
                }
            }
        }
        this.mRemoteInputQuickSettingsDisabler.setRemoteInputActive(false);
        MetricsLogger.action(super.mContext, 400, this.mEntry.getSbn().getPackageName());
    }
    
    private void reset() {
        this.mResetting = true;
        this.mEntry.remoteInputTextWhenReset = (CharSequence)SpannedString.valueOf((CharSequence)this.mEditText.getText());
        this.mEditText.getText().clear();
        this.mEditText.setEnabled(true);
        this.mSendButton.setVisibility(0);
        this.mProgressBar.setVisibility(4);
        this.mController.removeSpinning(this.mEntry.getKey(), this.mToken);
        this.updateSendButton();
        this.onDefocus(false);
        this.mResetting = false;
    }
    
    private void sendRemoteInput(final Intent intent) {
        this.mEditText.setEnabled(false);
        this.mSendButton.setVisibility(4);
        this.mProgressBar.setVisibility(0);
        this.mEntry.lastRemoteInputSent = SystemClock.elapsedRealtime();
        this.mController.addSpinning(this.mEntry.getKey(), this.mToken);
        this.mController.removeRemoteInput(this.mEntry, this.mToken);
        this.mEditText.mShowImeOnInputConnection = false;
        this.mController.remoteInputSent(this.mEntry);
        this.mEntry.setHasSentReply();
        ((ShortcutManager)this.getContext().getSystemService((Class)ShortcutManager.class)).onApplicationActive(this.mEntry.getSbn().getPackageName(), this.mEntry.getSbn().getUser().getIdentifier());
        MetricsLogger.action(super.mContext, 398, this.mEntry.getSbn().getPackageName());
        try {
            this.mPendingIntent.send(super.mContext, 0, intent);
        }
        catch (PendingIntent$CanceledException ex) {
            Log.i("RemoteInput", "Unable to send remote input result", (Throwable)ex);
            MetricsLogger.action(super.mContext, 399, this.mEntry.getSbn().getPackageName());
        }
    }
    
    private void updateSendButton() {
        this.mSendButton.setEnabled(this.mEditText.getText().length() != 0);
    }
    
    public void afterTextChanged(final Editable editable) {
        this.updateSendButton();
    }
    
    public void beforeTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
    }
    
    public void close() {
        this.mEditText.defocusIfNeeded(false);
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
    
    public void focus() {
        MetricsLogger.action(super.mContext, 397, this.mEntry.getSbn().getPackageName());
        this.setVisibility(0);
        final NotificationViewWrapper mWrapper = this.mWrapper;
        if (mWrapper != null) {
            mWrapper.setRemoteInputVisible(true);
        }
        this.mEditText.setInnerFocusable(true);
        final RemoteEditText mEditText = this.mEditText;
        mEditText.mShowImeOnInputConnection = true;
        mEditText.setText(this.mEntry.remoteInputText);
        final RemoteEditText mEditText2 = this.mEditText;
        mEditText2.setSelection(mEditText2.getText().length());
        this.mEditText.requestFocus();
        this.mController.addRemoteInput(this.mEntry, this.mToken);
        this.mRemoteInputQuickSettingsDisabler.setRemoteInputActive(true);
        this.updateSendButton();
    }
    
    public void focusAnimated() {
        if (this.getVisibility() != 0) {
            final Animator circularReveal = ViewAnimationUtils.createCircularReveal((View)this, this.mRevealCx, this.mRevealCy, 0.0f, (float)this.mRevealR);
            circularReveal.setDuration(360L);
            circularReveal.setInterpolator((TimeInterpolator)Interpolators.LINEAR_OUT_SLOW_IN);
            circularReveal.start();
        }
        this.focus();
    }
    
    public PendingIntent getPendingIntent() {
        return this.mPendingIntent;
    }
    
    public CharSequence getText() {
        return (CharSequence)this.mEditText.getText();
    }
    
    public boolean isActive() {
        return this.mEditText.isFocused() && this.mEditText.isEnabled();
    }
    
    public boolean isSending() {
        return this.getVisibility() == 0 && this.mController.isSpinning(this.mEntry.getKey(), this.mToken);
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mEntry.getRow().isChangingPosition() && this.getVisibility() == 0 && this.mEditText.isFocusable()) {
            this.mEditText.requestFocus();
        }
    }
    
    public void onClick(final View view) {
        if (view == this.mSendButton) {
            this.sendRemoteInput(this.prepareRemoteInputFromText());
        }
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!this.mEntry.getRow().isChangingPosition()) {
            if (!this.isTemporarilyDetached()) {
                this.mController.removeRemoteInput(this.mEntry, this.mToken);
                this.mController.removeSpinning(this.mEntry.getKey(), this.mToken);
            }
        }
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
                final boolean b = keyEvent != null && KeyEvent.isConfirmKey(keyEvent.getKeyCode()) && keyEvent.getAction() == 0;
                if (n == 0 && !b) {
                    return false;
                }
                if (RemoteInputView.this.mEditText.length() > 0) {
                    final RemoteInputView this$0 = RemoteInputView.this;
                    this$0.sendRemoteInput(this$0.prepareRemoteInputFromText());
                }
                return true;
            }
        });
        this.mEditText.addTextChangedListener((TextWatcher)this);
        this.mEditText.setInnerFocusable(false);
        this.mEditText.mRemoteInputView = this;
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.mController.requestDisallowLongPressAndDismiss();
        }
        return super.onInterceptTouchEvent(motionEvent);
    }
    
    public void onNotificationUpdateOrReset() {
        if (this.mProgressBar.getVisibility() == 0) {
            this.reset();
        }
        if (this.isActive()) {
            final NotificationViewWrapper mWrapper = this.mWrapper;
            if (mWrapper != null) {
                mWrapper.setRemoteInputVisible(true);
            }
        }
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
    
    protected void onVisibilityChanged(final View view, final int n) {
        super.onVisibilityChanged(view, n);
        if (view == this) {
            final Consumer<Boolean> mOnVisibilityChangedListener = this.mOnVisibilityChangedListener;
            if (mOnVisibilityChangedListener != null) {
                mOnVisibilityChangedListener.accept(n == 0);
            }
        }
    }
    
    protected Intent prepareRemoteInputFromData(final String s, final Uri uri) {
        final HashMap<String, Uri> hashMap = new HashMap<String, Uri>();
        hashMap.put(s, uri);
        this.mController.grantInlineReplyUriPermission(this.mEntry.getSbn(), uri);
        final Intent addFlags = new Intent().addFlags(268435456);
        RemoteInput.addDataResultToIntent(this.mRemoteInput, addFlags, (Map)hashMap);
        this.mEntry.remoteInputText = super.mContext.getString(R$string.remote_input_image_insertion_text);
        final NotificationEntry mEntry = this.mEntry;
        mEntry.remoteInputMimeType = s;
        mEntry.remoteInputUri = uri;
        return addFlags;
    }
    
    protected Intent prepareRemoteInputFromText() {
        final Bundle bundle = new Bundle();
        bundle.putString(this.mRemoteInput.getResultKey(), this.mEditText.getText().toString());
        final Intent addFlags = new Intent().addFlags(268435456);
        RemoteInput.addResultsToIntent(this.mRemoteInputs, addFlags, bundle);
        this.mEntry.remoteInputText = (CharSequence)this.mEditText.getText();
        final NotificationEntry mEntry = this.mEntry;
        mEntry.remoteInputUri = null;
        mEntry.remoteInputMimeType = null;
        if (mEntry.editedSuggestionInfo == null) {
            RemoteInput.setResultsSource(addFlags, 0);
        }
        else {
            RemoteInput.setResultsSource(addFlags, 1);
        }
        return addFlags;
    }
    
    public boolean requestScrollTo() {
        this.mController.lockScrollTo(this.mEntry);
        return true;
    }
    
    public void setOnVisibilityChangedListener(final Consumer<Boolean> mOnVisibilityChangedListener) {
        this.mOnVisibilityChangedListener = mOnVisibilityChangedListener;
    }
    
    public void setPendingIntent(final PendingIntent mPendingIntent) {
        this.mPendingIntent = mPendingIntent;
    }
    
    public void setRemoteInput(final RemoteInput[] mRemoteInputs, final RemoteInput mRemoteInput, final NotificationEntry.EditedSuggestionInfo editedSuggestionInfo) {
        this.mRemoteInputs = mRemoteInputs;
        this.mRemoteInput = mRemoteInput;
        this.mEditText.setHint(mRemoteInput.getLabel());
        final NotificationEntry mEntry = this.mEntry;
        mEntry.editedSuggestionInfo = editedSuggestionInfo;
        if (editedSuggestionInfo != null) {
            mEntry.remoteInputText = editedSuggestionInfo.originalText;
        }
    }
    
    public void setRemoved() {
        this.mRemoved = true;
    }
    
    public void setRevealParameters(final int mRevealCx, final int mRevealCy, final int mRevealR) {
        this.mRevealCx = mRevealCx;
        this.mRevealCy = mRevealCy;
        this.mRevealR = mRevealR;
    }
    
    public void setWrapper(final NotificationViewWrapper mWrapper) {
        this.mWrapper = mWrapper;
    }
    
    public void stealFocusFrom(final RemoteInputView remoteInputView) {
        remoteInputView.close();
        this.setPendingIntent(remoteInputView.mPendingIntent);
        this.setRemoteInput(remoteInputView.mRemoteInputs, remoteInputView.mRemoteInput, this.mEntry.editedSuggestionInfo);
        this.setRevealParameters(remoteInputView.mRevealCx, remoteInputView.mRevealCy, remoteInputView.mRevealR);
        this.focus();
    }
    
    public boolean updatePendingIntentFromActions(final Notification$Action[] array) {
        final PendingIntent mPendingIntent = this.mPendingIntent;
        if (mPendingIntent != null) {
            if (array != null) {
                final Intent intent = mPendingIntent.getIntent();
                if (intent == null) {
                    return false;
                }
                for (final Notification$Action notification$Action : array) {
                    final RemoteInput[] remoteInputs = notification$Action.getRemoteInputs();
                    final PendingIntent actionIntent = notification$Action.actionIntent;
                    if (actionIntent != null) {
                        if (remoteInputs != null) {
                            if (intent.filterEquals(actionIntent.getIntent())) {
                                final int length2 = remoteInputs.length;
                                int j = 0;
                                RemoteInput remoteInput = null;
                                while (j < length2) {
                                    final RemoteInput remoteInput2 = remoteInputs[j];
                                    if (remoteInput2.getAllowFreeFormInput()) {
                                        remoteInput = remoteInput2;
                                    }
                                    ++j;
                                }
                                if (remoteInput != null) {
                                    this.setPendingIntent(notification$Action.actionIntent);
                                    this.setRemoteInput(remoteInputs, remoteInput, null);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public static class RemoteEditText extends EditText
    {
        private final Drawable mBackground;
        private LightBarController mLightBarController;
        private RemoteInputView mRemoteInputView;
        boolean mShowImeOnInputConnection;
        UserHandle mUser;
        
        public RemoteEditText(final Context context, final AttributeSet set) {
            super(context, set);
            this.mBackground = this.getBackground();
            this.mLightBarController = Dependency.get(LightBarController.class);
        }
        
        private void defocusIfNeeded(final boolean b) {
            final RemoteInputView mRemoteInputView = this.mRemoteInputView;
            if ((mRemoteInputView != null && mRemoteInputView.mEntry.getRow().isChangingPosition()) || this.isTemporarilyDetached()) {
                if (this.isTemporarilyDetached()) {
                    final RemoteInputView mRemoteInputView2 = this.mRemoteInputView;
                    if (mRemoteInputView2 != null) {
                        mRemoteInputView2.mEntry.remoteInputText = (CharSequence)this.getText();
                    }
                }
                return;
            }
            if (this.isFocusable() && this.isEnabled()) {
                this.setInnerFocusable(false);
                final RemoteInputView mRemoteInputView3 = this.mRemoteInputView;
                if (mRemoteInputView3 != null) {
                    mRemoteInputView3.onDefocus(b);
                }
                this.mShowImeOnInputConnection = false;
            }
        }
        
        public void getFocusedRect(final Rect rect) {
            super.getFocusedRect(rect);
            final int mScrollY = super.mScrollY;
            rect.top = mScrollY;
            rect.bottom = mScrollY + (super.mBottom - super.mTop);
        }
        
        public boolean onCheckIsTextEditor() {
            final RemoteInputView mRemoteInputView = this.mRemoteInputView;
            boolean b = true;
            if ((mRemoteInputView != null && mRemoteInputView.mRemoved) || !super.onCheckIsTextEditor()) {
                b = false;
            }
            return b;
        }
        
        public void onCommitCompletion(final CompletionInfo completionInfo) {
            this.clearComposingText();
            this.setText(completionInfo.getText());
            this.setSelection(this.getText().length());
        }
        
        public InputConnection onCreateInputConnection(final EditorInfo editorInfo) {
            EditorInfoCompat.setContentMimeTypes(editorInfo, this.mRemoteInputView.mRemoteInput.getAllowedDataTypes().toArray(new String[0]));
            final InputConnection onCreateInputConnection = super.onCreateInputConnection(editorInfo);
            final InputConnectionCompat.OnCommitContentListener onCommitContentListener = new InputConnectionCompat.OnCommitContentListener() {
                @Override
                public boolean onCommitContent(final InputContentInfoCompat inputContentInfoCompat, final int n, final Bundle bundle) {
                    final Uri contentUri = inputContentInfoCompat.getContentUri();
                    final ClipDescription description = inputContentInfoCompat.getDescription();
                    String mimeType;
                    if (description != null && description.getMimeTypeCount() > 0) {
                        mimeType = description.getMimeType(0);
                    }
                    else {
                        mimeType = null;
                    }
                    if (mimeType != null) {
                        RemoteEditText.this.mRemoteInputView.sendRemoteInput(RemoteEditText.this.mRemoteInputView.prepareRemoteInputFromData(mimeType, contentUri));
                    }
                    return true;
                }
            };
            final Context context = null;
            InputConnection wrapper;
            if (onCreateInputConnection == null) {
                wrapper = null;
            }
            else {
                wrapper = InputConnectionCompat.createWrapper(onCreateInputConnection, editorInfo, (InputConnectionCompat.OnCommitContentListener)onCommitContentListener);
            }
            Context context2;
            try {
                context2 = super.mContext.createPackageContextAsUser(super.mContext.getPackageName(), 0, this.mUser);
            }
            catch (PackageManager$NameNotFoundException ex) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Unable to create user context:");
                sb.append(ex.getMessage());
                Log.e("RemoteInput", sb.toString(), (Throwable)ex);
                context2 = context;
            }
            if (this.mShowImeOnInputConnection && wrapper != null) {
                if (context2 == null) {
                    context2 = this.getContext();
                }
                final InputMethodManager inputMethodManager = (InputMethodManager)context2.getSystemService((Class)InputMethodManager.class);
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
            return wrapper;
        }
        
        protected void onFocusChanged(final boolean directReplying, final int n, final Rect rect) {
            super.onFocusChanged(directReplying, n, rect);
            if (!directReplying) {
                this.defocusIfNeeded(true);
            }
            if (!this.mRemoteInputView.mRemoved) {
                this.mLightBarController.setDirectReplying(directReplying);
            }
        }
        
        public boolean onKeyDown(final int n, final KeyEvent keyEvent) {
            return n == 4 || super.onKeyDown(n, keyEvent);
        }
        
        public boolean onKeyPreIme(final int n, final KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == 4 && keyEvent.getAction() == 1) {
                this.defocusIfNeeded(true);
            }
            return super.onKeyPreIme(n, keyEvent);
        }
        
        public boolean onKeyUp(final int n, final KeyEvent keyEvent) {
            if (n == 4) {
                this.defocusIfNeeded(true);
                return true;
            }
            return super.onKeyUp(n, keyEvent);
        }
        
        protected void onVisibilityChanged(final View view, final int n) {
            super.onVisibilityChanged(view, n);
            if (!this.isShown()) {
                this.defocusIfNeeded(false);
            }
        }
        
        public boolean requestRectangleOnScreen(final Rect rect) {
            return this.mRemoteInputView.requestScrollTo();
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
