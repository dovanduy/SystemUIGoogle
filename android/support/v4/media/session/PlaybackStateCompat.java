// 
// Decompiled by Procyon v0.5.36
// 

package android.support.v4.media.session;

import android.media.session.PlaybackState$CustomAction;
import java.util.Iterator;
import android.media.session.PlaybackState;
import android.os.Build$VERSION;
import android.text.TextUtils;
import java.util.Collection;
import java.util.ArrayList;
import android.os.Parcel;
import android.os.Bundle;
import java.util.List;
import android.os.Parcelable$Creator;
import android.annotation.SuppressLint;
import android.os.Parcelable;

@SuppressLint({ "BanParcelableUsage" })
public final class PlaybackStateCompat implements Parcelable
{
    public static final Parcelable$Creator<PlaybackStateCompat> CREATOR;
    final long mActions;
    final long mActiveItemId;
    final long mBufferedPosition;
    List<CustomAction> mCustomActions;
    final int mErrorCode;
    final CharSequence mErrorMessage;
    final Bundle mExtras;
    final long mPosition;
    final float mSpeed;
    final int mState;
    final long mUpdateTime;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<PlaybackStateCompat>() {
            public PlaybackStateCompat createFromParcel(final Parcel parcel) {
                return new PlaybackStateCompat(parcel);
            }
            
            public PlaybackStateCompat[] newArray(final int n) {
                return new PlaybackStateCompat[n];
            }
        };
    }
    
    PlaybackStateCompat(final int mState, final long mPosition, final long mBufferedPosition, final float mSpeed, final long mActions, final int mErrorCode, final CharSequence mErrorMessage, final long mUpdateTime, final List<CustomAction> c, final long mActiveItemId, final Bundle mExtras) {
        this.mState = mState;
        this.mPosition = mPosition;
        this.mBufferedPosition = mBufferedPosition;
        this.mSpeed = mSpeed;
        this.mActions = mActions;
        this.mErrorCode = mErrorCode;
        this.mErrorMessage = mErrorMessage;
        this.mUpdateTime = mUpdateTime;
        this.mCustomActions = new ArrayList<CustomAction>(c);
        this.mActiveItemId = mActiveItemId;
        this.mExtras = mExtras;
    }
    
    PlaybackStateCompat(final Parcel parcel) {
        this.mState = parcel.readInt();
        this.mPosition = parcel.readLong();
        this.mSpeed = parcel.readFloat();
        this.mUpdateTime = parcel.readLong();
        this.mBufferedPosition = parcel.readLong();
        this.mActions = parcel.readLong();
        this.mErrorMessage = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        this.mCustomActions = (List<CustomAction>)parcel.createTypedArrayList((Parcelable$Creator)CustomAction.CREATOR);
        this.mActiveItemId = parcel.readLong();
        this.mExtras = parcel.readBundle(MediaSessionCompat.class.getClassLoader());
        this.mErrorCode = parcel.readInt();
    }
    
    public static PlaybackStateCompat fromPlaybackState(final Object o) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        Bundle extras = null;
        if (o != null && sdk_INT >= 21) {
            final PlaybackState playbackState = (PlaybackState)o;
            final List customActions = playbackState.getCustomActions();
            ArrayList<CustomAction> list;
            if (customActions != null) {
                list = new ArrayList<CustomAction>(customActions.size());
                final Iterator<Object> iterator = customActions.iterator();
                while (iterator.hasNext()) {
                    list.add(CustomAction.fromCustomAction(iterator.next()));
                }
            }
            else {
                list = null;
            }
            if (sdk_INT >= 22) {
                extras = playbackState.getExtras();
                MediaSessionCompat.ensureClassLoader(extras);
            }
            return new PlaybackStateCompat(playbackState.getState(), playbackState.getPosition(), playbackState.getBufferedPosition(), playbackState.getPlaybackSpeed(), playbackState.getActions(), 0, playbackState.getErrorMessage(), playbackState.getLastPositionUpdateTime(), list, playbackState.getActiveQueueItemId(), extras);
        }
        return null;
    }
    
    public int describeContents() {
        return 0;
    }
    
    public long getActions() {
        return this.mActions;
    }
    
    public int getState() {
        return this.mState;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PlaybackState {");
        sb.append("state=");
        sb.append(this.mState);
        sb.append(", position=");
        sb.append(this.mPosition);
        sb.append(", buffered position=");
        sb.append(this.mBufferedPosition);
        sb.append(", speed=");
        sb.append(this.mSpeed);
        sb.append(", updated=");
        sb.append(this.mUpdateTime);
        sb.append(", actions=");
        sb.append(this.mActions);
        sb.append(", error code=");
        sb.append(this.mErrorCode);
        sb.append(", error message=");
        sb.append(this.mErrorMessage);
        sb.append(", custom actions=");
        sb.append(this.mCustomActions);
        sb.append(", active item id=");
        sb.append(this.mActiveItemId);
        sb.append("}");
        return sb.toString();
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        parcel.writeInt(this.mState);
        parcel.writeLong(this.mPosition);
        parcel.writeFloat(this.mSpeed);
        parcel.writeLong(this.mUpdateTime);
        parcel.writeLong(this.mBufferedPosition);
        parcel.writeLong(this.mActions);
        TextUtils.writeToParcel(this.mErrorMessage, parcel, n);
        parcel.writeTypedList((List)this.mCustomActions);
        parcel.writeLong(this.mActiveItemId);
        parcel.writeBundle(this.mExtras);
        parcel.writeInt(this.mErrorCode);
    }
    
    public static final class CustomAction implements Parcelable
    {
        public static final Parcelable$Creator<CustomAction> CREATOR;
        private final String mAction;
        private final Bundle mExtras;
        private final int mIcon;
        private final CharSequence mName;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$Creator<CustomAction>() {
                public CustomAction createFromParcel(final Parcel parcel) {
                    return new CustomAction(parcel);
                }
                
                public CustomAction[] newArray(final int n) {
                    return new CustomAction[n];
                }
            };
        }
        
        CustomAction(final Parcel parcel) {
            this.mAction = parcel.readString();
            this.mName = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
            this.mIcon = parcel.readInt();
            this.mExtras = parcel.readBundle(MediaSessionCompat.class.getClassLoader());
        }
        
        CustomAction(final String mAction, final CharSequence mName, final int mIcon, final Bundle mExtras) {
            this.mAction = mAction;
            this.mName = mName;
            this.mIcon = mIcon;
            this.mExtras = mExtras;
        }
        
        public static CustomAction fromCustomAction(final Object o) {
            if (o != null && Build$VERSION.SDK_INT >= 21) {
                final PlaybackState$CustomAction playbackState$CustomAction = (PlaybackState$CustomAction)o;
                final Bundle extras = playbackState$CustomAction.getExtras();
                MediaSessionCompat.ensureClassLoader(extras);
                return new CustomAction(playbackState$CustomAction.getAction(), playbackState$CustomAction.getName(), playbackState$CustomAction.getIcon(), extras);
            }
            return null;
        }
        
        public int describeContents() {
            return 0;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("Action:mName='");
            sb.append((Object)this.mName);
            sb.append(", mIcon=");
            sb.append(this.mIcon);
            sb.append(", mExtras=");
            sb.append(this.mExtras);
            return sb.toString();
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            parcel.writeString(this.mAction);
            TextUtils.writeToParcel(this.mName, parcel, n);
            parcel.writeInt(this.mIcon);
            parcel.writeBundle(this.mExtras);
        }
    }
}
