// 
// Decompiled by Procyon v0.5.36
// 

package android.support.v4.media.session;

import android.os.IBinder;
import androidx.versionedparcelable.VersionedParcelable;
import android.os.ResultReceiver;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import android.os.Build$VERSION;
import android.media.session.MediaSession$QueueItem;
import android.os.Parcel;
import android.support.v4.media.MediaDescriptionCompat;
import android.os.Parcelable$Creator;
import android.annotation.SuppressLint;
import android.os.Parcelable;
import android.os.BadParcelableException;
import android.util.Log;
import android.os.Bundle;

public class MediaSessionCompat
{
    private final MediaSessionImpl mImpl;
    
    public static void ensureClassLoader(final Bundle bundle) {
        if (bundle != null) {
            bundle.setClassLoader(MediaSessionCompat.class.getClassLoader());
        }
    }
    
    public static Bundle unparcelWithClassLoader(final Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        ensureClassLoader(bundle);
        try {
            bundle.isEmpty();
            return bundle;
        }
        catch (BadParcelableException ex) {
            Log.e("MediaSessionCompat", "Could not unparcel the data.");
            return null;
        }
    }
    
    public Token getSessionToken() {
        return this.mImpl.getSessionToken();
    }
    
    interface MediaSessionImpl
    {
        Token getSessionToken();
    }
    
    @SuppressLint({ "BanParcelableUsage" })
    public static final class QueueItem implements Parcelable
    {
        public static final Parcelable$Creator<QueueItem> CREATOR;
        private final MediaDescriptionCompat mDescription;
        private final long mId;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$Creator<QueueItem>() {
                public QueueItem createFromParcel(final Parcel parcel) {
                    return new QueueItem(parcel);
                }
                
                public QueueItem[] newArray(final int n) {
                    return new QueueItem[n];
                }
            };
        }
        
        private QueueItem(final MediaSession$QueueItem mediaSession$QueueItem, final MediaDescriptionCompat mDescription, final long mId) {
            if (mDescription == null) {
                throw new IllegalArgumentException("Description cannot be null");
            }
            if (mId != -1L) {
                this.mDescription = mDescription;
                this.mId = mId;
                return;
            }
            throw new IllegalArgumentException("Id cannot be QueueItem.UNKNOWN_ID");
        }
        
        QueueItem(final Parcel parcel) {
            this.mDescription = (MediaDescriptionCompat)MediaDescriptionCompat.CREATOR.createFromParcel(parcel);
            this.mId = parcel.readLong();
        }
        
        public static QueueItem fromQueueItem(final Object o) {
            if (o != null && Build$VERSION.SDK_INT >= 21) {
                final MediaSession$QueueItem mediaSession$QueueItem = (MediaSession$QueueItem)o;
                return new QueueItem(mediaSession$QueueItem, MediaDescriptionCompat.fromMediaDescription(mediaSession$QueueItem.getDescription()), mediaSession$QueueItem.getQueueId());
            }
            return null;
        }
        
        public static List<QueueItem> fromQueueItemList(final List<?> list) {
            if (list != null && Build$VERSION.SDK_INT >= 21) {
                final ArrayList<QueueItem> list2 = new ArrayList<QueueItem>();
                final Iterator<?> iterator = list.iterator();
                while (iterator.hasNext()) {
                    list2.add(fromQueueItem(iterator.next()));
                }
                return list2;
            }
            return null;
        }
        
        public int describeContents() {
            return 0;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("MediaSession.QueueItem {Description=");
            sb.append(this.mDescription);
            sb.append(", Id=");
            sb.append(this.mId);
            sb.append(" }");
            return sb.toString();
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            this.mDescription.writeToParcel(parcel, n);
            parcel.writeLong(this.mId);
        }
    }
    
    @SuppressLint({ "BanParcelableUsage" })
    static final class ResultReceiverWrapper implements Parcelable
    {
        public static final Parcelable$Creator<ResultReceiverWrapper> CREATOR;
        ResultReceiver mResultReceiver;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$Creator<ResultReceiverWrapper>() {
                public ResultReceiverWrapper createFromParcel(final Parcel parcel) {
                    return new ResultReceiverWrapper(parcel);
                }
                
                public ResultReceiverWrapper[] newArray(final int n) {
                    return new ResultReceiverWrapper[n];
                }
            };
        }
        
        ResultReceiverWrapper(final Parcel parcel) {
            this.mResultReceiver = (ResultReceiver)ResultReceiver.CREATOR.createFromParcel(parcel);
        }
        
        public int describeContents() {
            return 0;
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            this.mResultReceiver.writeToParcel(parcel, n);
        }
    }
    
    @SuppressLint({ "BanParcelableUsage" })
    public static final class Token implements Parcelable
    {
        public static final Parcelable$Creator<Token> CREATOR;
        private IMediaSession mExtraBinder;
        private final Object mInner;
        private final Object mLock;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$Creator<Token>() {
                public Token createFromParcel(final Parcel parcel) {
                    Object o;
                    if (Build$VERSION.SDK_INT >= 21) {
                        o = parcel.readParcelable((ClassLoader)null);
                    }
                    else {
                        o = parcel.readStrongBinder();
                    }
                    return new Token(o);
                }
                
                public Token[] newArray(final int n) {
                    return new Token[n];
                }
            };
        }
        
        Token(final Object o) {
            this(o, null, null);
        }
        
        Token(final Object mInner, final IMediaSession mExtraBinder, final VersionedParcelable versionedParcelable) {
            this.mLock = new Object();
            this.mInner = mInner;
            this.mExtraBinder = mExtraBinder;
        }
        
        public int describeContents() {
            return 0;
        }
        
        @Override
        public boolean equals(Object mInner) {
            boolean b = true;
            if (this == mInner) {
                return true;
            }
            if (!(mInner instanceof Token)) {
                return false;
            }
            final Token token = (Token)mInner;
            mInner = this.mInner;
            if (mInner == null) {
                if (token.mInner != null) {
                    b = false;
                }
                return b;
            }
            final Object mInner2 = token.mInner;
            return mInner2 != null && mInner.equals(mInner2);
        }
        
        public IMediaSession getExtraBinder() {
            synchronized (this.mLock) {
                return this.mExtraBinder;
            }
        }
        
        public Object getToken() {
            return this.mInner;
        }
        
        @Override
        public int hashCode() {
            final Object mInner = this.mInner;
            if (mInner == null) {
                return 0;
            }
            return mInner.hashCode();
        }
        
        public void setExtraBinder(final IMediaSession mExtraBinder) {
            synchronized (this.mLock) {
                this.mExtraBinder = mExtraBinder;
            }
        }
        
        public void setSession2Token(final VersionedParcelable versionedParcelable) {
            synchronized (this.mLock) {
            }
            // monitorexit(this.mLock)
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            if (Build$VERSION.SDK_INT >= 21) {
                parcel.writeParcelable((Parcelable)this.mInner, n);
            }
            else {
                parcel.writeStrongBinder((IBinder)this.mInner);
            }
        }
    }
}
