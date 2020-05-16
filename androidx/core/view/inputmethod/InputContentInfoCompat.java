// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.view.inputmethod;

import android.view.inputmethod.InputContentInfo;
import android.os.Build$VERSION;
import android.content.ClipDescription;
import android.net.Uri;

public final class InputContentInfoCompat
{
    private final InputContentInfoCompatImpl mImpl;
    
    public InputContentInfoCompat(final Uri uri, final ClipDescription clipDescription, final Uri uri2) {
        if (Build$VERSION.SDK_INT >= 25) {
            this.mImpl = (InputContentInfoCompatImpl)new InputContentInfoCompatApi25Impl(uri, clipDescription, uri2);
        }
        else {
            this.mImpl = (InputContentInfoCompatImpl)new InputContentInfoCompatBaseImpl(uri, clipDescription, uri2);
        }
    }
    
    private InputContentInfoCompat(final InputContentInfoCompatImpl mImpl) {
        this.mImpl = mImpl;
    }
    
    public static InputContentInfoCompat wrap(final Object o) {
        if (o == null) {
            return null;
        }
        if (Build$VERSION.SDK_INT < 25) {
            return null;
        }
        return new InputContentInfoCompat((InputContentInfoCompatImpl)new InputContentInfoCompatApi25Impl(o));
    }
    
    public Uri getContentUri() {
        return this.mImpl.getContentUri();
    }
    
    public ClipDescription getDescription() {
        return this.mImpl.getDescription();
    }
    
    private static final class InputContentInfoCompatApi25Impl implements InputContentInfoCompatImpl
    {
        final InputContentInfo mObject;
        
        InputContentInfoCompatApi25Impl(final Uri uri, final ClipDescription clipDescription, final Uri uri2) {
            this.mObject = new InputContentInfo(uri, clipDescription, uri2);
        }
        
        InputContentInfoCompatApi25Impl(final Object o) {
            this.mObject = (InputContentInfo)o;
        }
        
        @Override
        public Uri getContentUri() {
            return this.mObject.getContentUri();
        }
        
        @Override
        public ClipDescription getDescription() {
            return this.mObject.getDescription();
        }
    }
    
    private static final class InputContentInfoCompatBaseImpl implements InputContentInfoCompatImpl
    {
        private final Uri mContentUri;
        private final ClipDescription mDescription;
        
        InputContentInfoCompatBaseImpl(final Uri mContentUri, final ClipDescription mDescription, final Uri uri) {
            this.mContentUri = mContentUri;
            this.mDescription = mDescription;
        }
        
        @Override
        public Uri getContentUri() {
            return this.mContentUri;
        }
        
        @Override
        public ClipDescription getDescription() {
            return this.mDescription;
        }
    }
    
    private interface InputContentInfoCompatImpl
    {
        Uri getContentUri();
        
        ClipDescription getDescription();
    }
}
