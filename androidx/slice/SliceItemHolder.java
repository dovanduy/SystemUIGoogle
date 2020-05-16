// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice;

import java.util.ArrayList;
import android.app.PendingIntent;
import androidx.core.util.Pair;
import androidx.core.text.HtmlCompat;
import android.text.Spanned;
import android.os.Parcelable;
import androidx.versionedparcelable.VersionedParcelable;

public class SliceItemHolder implements VersionedParcelable
{
    public static HolderHandler sHandler;
    public static final Object sSerializeLock;
    Object mCallback;
    int mInt;
    long mLong;
    Parcelable mParcelable;
    private SliceItemPool mPool;
    String mStr;
    public VersionedParcelable mVersionedParcelable;
    
    static {
        sSerializeLock = new Object();
    }
    
    SliceItemHolder(final SliceItemPool mPool) {
        this.mVersionedParcelable = null;
        this.mParcelable = null;
        this.mStr = null;
        this.mInt = 0;
        this.mLong = 0L;
        this.mPool = mPool;
    }
    
    public SliceItemHolder(final String s, Object first, final boolean b) {
        this.mVersionedParcelable = null;
        this.mParcelable = null;
        this.mStr = null;
        this.mInt = 0;
        this.mLong = 0L;
        int n = 0;
        Label_0212: {
            switch (s.hashCode()) {
                case 109526418: {
                    if (s.equals("slice")) {
                        n = 2;
                        break Label_0212;
                    }
                    break;
                }
                case 100358090: {
                    if (s.equals("input")) {
                        n = 3;
                        break Label_0212;
                    }
                    break;
                }
                case 100313435: {
                    if (s.equals("image")) {
                        n = 1;
                        break Label_0212;
                    }
                    break;
                }
                case 3556653: {
                    if (s.equals("text")) {
                        n = 4;
                        break Label_0212;
                    }
                    break;
                }
                case 3327612: {
                    if (s.equals("long")) {
                        n = 6;
                        break Label_0212;
                    }
                    break;
                }
                case 104431: {
                    if (s.equals("int")) {
                        n = 5;
                        break Label_0212;
                    }
                    break;
                }
                case -1422950858: {
                    if (s.equals("action")) {
                        n = 0;
                        break Label_0212;
                    }
                    break;
                }
            }
            n = -1;
        }
        switch (n) {
            case 6: {
                this.mLong = (long)first;
                break;
            }
            case 5: {
                this.mInt = (int)first;
                break;
            }
            case 4: {
                String html;
                if (first instanceof Spanned) {
                    html = HtmlCompat.toHtml((Spanned)first, 0);
                }
                else {
                    html = (String)first;
                }
                this.mStr = html;
                break;
            }
            case 3: {
                this.mParcelable = (Parcelable)first;
                break;
            }
            case 1:
            case 2: {
                this.mVersionedParcelable = (VersionedParcelable)first;
                break;
            }
            case 0: {
                final Pair pair = (Pair)first;
                first = pair.first;
                if (first instanceof PendingIntent) {
                    this.mParcelable = (Parcelable)first;
                }
                else if (!b) {
                    throw new IllegalArgumentException("Cannot write callback to parcel");
                }
                this.mVersionedParcelable = (VersionedParcelable)pair.second;
                break;
            }
        }
        final HolderHandler sHandler = SliceItemHolder.sHandler;
        if (sHandler != null) {
            sHandler.handle(this, s);
        }
    }
    
    public Object getObj(String mStr) {
        final HolderHandler sHandler = SliceItemHolder.sHandler;
        if (sHandler != null) {
            sHandler.handle(this, mStr);
        }
        switch (mStr) {
            default: {
                final StringBuilder sb = new StringBuilder();
                sb.append("Unrecognized format ");
                sb.append(mStr);
                throw new IllegalArgumentException(sb.toString());
            }
            case "long": {
                return this.mLong;
            }
            case "int": {
                return this.mInt;
            }
            case "text": {
                mStr = this.mStr;
                if (mStr != null && mStr.length() != 0) {
                    return HtmlCompat.fromHtml(this.mStr, 0);
                }
                return "";
            }
            case "input": {
                return this.mParcelable;
            }
            case "image":
            case "slice": {
                return this.mVersionedParcelable;
            }
            case "action": {
                if (this.mParcelable == null && this.mVersionedParcelable == null) {
                    return null;
                }
                Object o = this.mParcelable;
                if (o == null) {
                    o = this.mCallback;
                }
                return new Pair(o, this.mVersionedParcelable);
            }
        }
    }
    
    public void release() {
        final SliceItemPool mPool = this.mPool;
        if (mPool != null) {
            mPool.release(this);
        }
    }
    
    public interface HolderHandler
    {
        void handle(final SliceItemHolder p0, final String p1);
    }
    
    public static class SliceItemPool
    {
        private final ArrayList<SliceItemHolder> mCached;
        
        public SliceItemPool() {
            this.mCached = new ArrayList<SliceItemHolder>();
        }
        
        public SliceItemHolder get() {
            if (this.mCached.size() > 0) {
                final ArrayList<SliceItemHolder> mCached = this.mCached;
                return mCached.remove(mCached.size() - 1);
            }
            return new SliceItemHolder(this);
        }
        
        public void release(final SliceItemHolder e) {
            e.mParcelable = null;
            e.mCallback = null;
            e.mVersionedParcelable = null;
            e.mInt = 0;
            e.mLong = 0L;
            e.mStr = null;
            this.mCached.add(e);
        }
    }
}
