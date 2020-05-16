// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.app;

import androidx.core.graphics.drawable.IconCompat;
import android.app.PendingIntent;
import androidx.versionedparcelable.VersionedParcelable;

public final class RemoteActionCompat implements VersionedParcelable
{
    public PendingIntent mActionIntent;
    public CharSequence mContentDescription;
    public boolean mEnabled;
    public IconCompat mIcon;
    public boolean mShouldShowIcon;
    public CharSequence mTitle;
}
