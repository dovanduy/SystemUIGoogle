// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.view.inputmethod;

import android.content.ClipDescription;
import android.net.Uri;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.os.Bundle;
import android.view.inputmethod.InputContentInfo;
import android.view.inputmethod.InputConnectionWrapper;
import android.os.Build$VERSION;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public final class InputConnectionCompat
{
    public static InputConnection createWrapper(final InputConnection inputConnection, final EditorInfo editorInfo, final OnCommitContentListener onCommitContentListener) {
        if (inputConnection == null) {
            throw new IllegalArgumentException("inputConnection must be non-null");
        }
        if (editorInfo == null) {
            throw new IllegalArgumentException("editorInfo must be non-null");
        }
        if (onCommitContentListener == null) {
            throw new IllegalArgumentException("onCommitContentListener must be non-null");
        }
        if (Build$VERSION.SDK_INT >= 25) {
            return (InputConnection)new InputConnectionWrapper(inputConnection, false) {
                public boolean commitContent(final InputContentInfo inputContentInfo, final int n, final Bundle bundle) {
                    return onCommitContentListener.onCommitContent(InputContentInfoCompat.wrap(inputContentInfo), n, bundle) || super.commitContent(inputContentInfo, n, bundle);
                }
            };
        }
        if (EditorInfoCompat.getContentMimeTypes(editorInfo).length == 0) {
            return inputConnection;
        }
        return (InputConnection)new InputConnectionWrapper(inputConnection, false) {
            public boolean performPrivateCommand(final String s, final Bundle bundle) {
                return InputConnectionCompat.handlePerformPrivateCommand(s, bundle, onCommitContentListener) || super.performPrivateCommand(s, bundle);
            }
        };
    }
    
    static boolean handlePerformPrivateCommand(String s, final Bundle bundle, final OnCommitContentListener onCommitContentListener) {
        final int n = 0;
        if (bundle == null) {
            return false;
        }
        boolean b;
        if (TextUtils.equals((CharSequence)"androidx.core.view.inputmethod.InputConnectionCompat.COMMIT_CONTENT", (CharSequence)s)) {
            b = false;
        }
        else {
            if (!TextUtils.equals((CharSequence)"android.support.v13.view.inputmethod.InputConnectionCompat.COMMIT_CONTENT", (CharSequence)s)) {
                return false;
            }
            b = true;
        }
        if (b) {
            s = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_RESULT_RECEIVER";
        }
        else {
            s = "androidx.core.view.inputmethod.InputConnectionCompat.CONTENT_RESULT_RECEIVER";
        }
        ResultReceiver resultReceiver2;
        try {
            final ResultReceiver resultReceiver = (ResultReceiver)bundle.getParcelable(s);
            if (b) {
                s = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_URI";
            }
            else {
                s = "androidx.core.view.inputmethod.InputConnectionCompat.CONTENT_URI";
            }
            try {
                final Uri uri = (Uri)bundle.getParcelable(s);
                if (b) {
                    s = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_DESCRIPTION";
                }
                else {
                    s = "androidx.core.view.inputmethod.InputConnectionCompat.CONTENT_DESCRIPTION";
                }
                final ClipDescription clipDescription = (ClipDescription)bundle.getParcelable(s);
                if (b) {
                    s = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_LINK_URI";
                }
                else {
                    s = "androidx.core.view.inputmethod.InputConnectionCompat.CONTENT_LINK_URI";
                }
                final Uri uri2 = (Uri)bundle.getParcelable(s);
                if (b) {
                    s = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_FLAGS";
                }
                else {
                    s = "androidx.core.view.inputmethod.InputConnectionCompat.CONTENT_FLAGS";
                }
                final int int1 = bundle.getInt(s);
                if (b) {
                    s = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_OPTS";
                }
                else {
                    s = "androidx.core.view.inputmethod.InputConnectionCompat.CONTENT_OPTS";
                }
                final Bundle bundle2 = (Bundle)bundle.getParcelable(s);
                int onCommitContent = n;
                if (uri != null) {
                    onCommitContent = n;
                    if (clipDescription != null) {
                        onCommitContent = (onCommitContentListener.onCommitContent(new InputContentInfoCompat(uri, clipDescription, uri2), int1, bundle2) ? 1 : 0);
                    }
                }
                if (resultReceiver != null) {
                    resultReceiver.send(onCommitContent, (Bundle)null);
                }
                return onCommitContent != 0;
            }
            finally {}
        }
        finally {
            resultReceiver2 = null;
        }
        if (resultReceiver2 != null) {
            resultReceiver2.send(0, (Bundle)null);
        }
    }
    
    public interface OnCommitContentListener
    {
        boolean onCommitContent(final InputContentInfoCompat p0, final int p1, final Bundle p2);
    }
}
