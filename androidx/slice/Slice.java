// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice;

import android.app.RemoteInput;
import java.util.Collection;
import androidx.core.util.Preconditions;
import android.app.PendingIntent;
import java.util.Arrays;
import java.util.List;
import androidx.core.graphics.drawable.IconCompat;
import android.app.slice.SliceManager;
import androidx.slice.compat.SliceProviderCompat;
import android.os.Build$VERSION;
import java.util.Set;
import android.content.Context;
import android.net.Uri;
import java.util.ArrayList;
import android.os.Parcelable;
import android.os.Bundle;
import androidx.versionedparcelable.VersionedParcelable;
import androidx.versionedparcelable.CustomVersionedParcelable;

public final class Slice extends CustomVersionedParcelable implements VersionedParcelable
{
    static final String[] NO_HINTS;
    static final SliceItem[] NO_ITEMS;
    String[] mHints;
    SliceItem[] mItems;
    SliceSpec mSpec;
    String mUri;
    
    static {
        NO_HINTS = new String[0];
        NO_ITEMS = new SliceItem[0];
    }
    
    public Slice() {
        this.mSpec = null;
        this.mItems = Slice.NO_ITEMS;
        this.mHints = Slice.NO_HINTS;
        this.mUri = null;
    }
    
    public Slice(final Bundle bundle) {
        SliceSpec mSpec = null;
        this.mSpec = null;
        this.mItems = Slice.NO_ITEMS;
        this.mHints = Slice.NO_HINTS;
        this.mUri = null;
        this.mHints = bundle.getStringArray("hints");
        final Parcelable[] parcelableArray = bundle.getParcelableArray("items");
        this.mItems = new SliceItem[parcelableArray.length];
        int n = 0;
        while (true) {
            final SliceItem[] mItems = this.mItems;
            if (n >= mItems.length) {
                break;
            }
            if (parcelableArray[n] instanceof Bundle) {
                mItems[n] = new SliceItem((Bundle)parcelableArray[n]);
            }
            ++n;
        }
        this.mUri = bundle.getParcelable("uri").toString();
        if (bundle.containsKey("type")) {
            mSpec = new SliceSpec(bundle.getString("type"), bundle.getInt("revision"));
        }
        this.mSpec = mSpec;
    }
    
    Slice(final ArrayList<SliceItem> list, final String[] mHints, final Uri uri, final SliceSpec mSpec) {
        this.mSpec = null;
        this.mItems = Slice.NO_ITEMS;
        this.mHints = Slice.NO_HINTS;
        this.mUri = null;
        this.mHints = mHints;
        this.mItems = list.toArray(new SliceItem[list.size()]);
        this.mUri = uri.toString();
        this.mSpec = mSpec;
    }
    
    public static void appendHints(final StringBuilder sb, final String[] array) {
        if (array != null) {
            if (array.length != 0) {
                sb.append('(');
                final int n = array.length - 1;
                for (int i = 0; i < n; ++i) {
                    sb.append(array[i]);
                    sb.append(", ");
                }
                sb.append(array[n]);
                sb.append(")");
            }
        }
    }
    
    public static Slice bindSlice(final Context context, final Uri uri, final Set<SliceSpec> set) {
        if (Build$VERSION.SDK_INT >= 28) {
            return callBindSlice(context, uri, set);
        }
        return SliceProviderCompat.bindSlice(context, uri, set);
    }
    
    private static Slice callBindSlice(final Context context, final Uri uri, final Set<SliceSpec> set) {
        return SliceConvert.wrap(((SliceManager)context.getSystemService((Class)SliceManager.class)).bindSlice(uri, (Set)SliceConvert.unwrap(set)), context);
    }
    
    static boolean isValidIcon(final IconCompat iconCompat) {
        if (iconCompat == null) {
            return false;
        }
        if (iconCompat.mType == 2 && iconCompat.getResId() == 0) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Failed to add icon, invalid resource id: ");
            sb.append(iconCompat.getResId());
            throw new IllegalArgumentException(sb.toString());
        }
        return true;
    }
    
    public String[] getHintArray() {
        return this.mHints;
    }
    
    public List<String> getHints() {
        return Arrays.asList(this.mHints);
    }
    
    public SliceItem[] getItemArray() {
        return this.mItems;
    }
    
    public List<SliceItem> getItems() {
        return Arrays.asList(this.mItems);
    }
    
    public SliceSpec getSpec() {
        return this.mSpec;
    }
    
    public Uri getUri() {
        return Uri.parse(this.mUri);
    }
    
    public boolean hasHint(final String s) {
        return ArrayUtils.contains(this.mHints, s);
    }
    
    public void onPostParceling() {
        for (int i = this.mItems.length - 1; i >= 0; --i) {
            final SliceItem[] mItems = this.mItems;
            if (mItems[i].mObj == null && (this.mItems = ArrayUtils.removeElement(SliceItem.class, mItems, mItems[i])) == null) {
                this.mItems = new SliceItem[0];
            }
        }
    }
    
    public void onPreParceling(final boolean b) {
    }
    
    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putStringArray("hints", this.mHints);
        final Parcelable[] array = new Parcelable[this.mItems.length];
        int n = 0;
        while (true) {
            final SliceItem[] mItems = this.mItems;
            if (n >= mItems.length) {
                break;
            }
            array[n] = (Parcelable)mItems[n].toBundle();
            ++n;
        }
        bundle.putParcelableArray("items", array);
        bundle.putParcelable("uri", (Parcelable)Uri.parse(this.mUri));
        final SliceSpec mSpec = this.mSpec;
        if (mSpec != null) {
            bundle.putString("type", mSpec.getType());
            bundle.putInt("revision", this.mSpec.getRevision());
        }
        return bundle;
    }
    
    @Override
    public String toString() {
        return this.toString("");
    }
    
    public String toString(final String str) {
        final StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("Slice ");
        final String[] mHints = this.mHints;
        if (mHints.length > 0) {
            appendHints(sb, mHints);
            sb.append(' ');
        }
        sb.append('[');
        sb.append(this.mUri);
        sb.append("] {\n");
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        sb2.append("  ");
        final String string = sb2.toString();
        int n = 0;
        while (true) {
            final SliceItem[] mItems = this.mItems;
            if (n >= mItems.length) {
                break;
            }
            sb.append(mItems[n].toString(string));
            ++n;
        }
        sb.append(str);
        sb.append('}');
        return sb.toString();
    }
    
    public static class Builder
    {
        private int mChildId;
        private ArrayList<String> mHints;
        private ArrayList<SliceItem> mItems;
        private SliceSpec mSpec;
        private final Uri mUri;
        
        public Builder(final Uri mUri) {
            this.mItems = new ArrayList<SliceItem>();
            this.mHints = new ArrayList<String>();
            this.mUri = mUri;
        }
        
        public Builder(final Builder builder) {
            this.mItems = new ArrayList<SliceItem>();
            this.mHints = new ArrayList<String>();
            this.mUri = builder.getChildUri();
        }
        
        private Uri getChildUri() {
            return this.mUri.buildUpon().appendPath("_gen").appendPath(String.valueOf(this.mChildId++)).build();
        }
        
        public Builder addAction(final PendingIntent pendingIntent, final Slice slice, final String s) {
            Preconditions.checkNotNull(pendingIntent);
            Preconditions.checkNotNull(slice);
            this.mItems.add(new SliceItem(pendingIntent, slice, "action", s, slice.getHintArray()));
            return this;
        }
        
        public Builder addHints(final List<String> list) {
            this.addHints((String[])list.toArray(new String[list.size()]));
            return this;
        }
        
        public Builder addHints(final String... a) {
            this.mHints.addAll(Arrays.asList(a));
            return this;
        }
        
        public Builder addIcon(final IconCompat iconCompat, final String s, final List<String> list) {
            Preconditions.checkNotNull(iconCompat);
            if (Slice.isValidIcon(iconCompat)) {
                this.addIcon(iconCompat, s, (String[])list.toArray(new String[list.size()]));
            }
            return this;
        }
        
        public Builder addIcon(final IconCompat iconCompat, final String s, final String... array) {
            Preconditions.checkNotNull(iconCompat);
            if (Slice.isValidIcon(iconCompat)) {
                this.mItems.add(new SliceItem(iconCompat, "image", s, array));
            }
            return this;
        }
        
        public Builder addInt(final int n, final String s, final List<String> list) {
            this.addInt(n, s, (String[])list.toArray(new String[list.size()]));
            return this;
        }
        
        public Builder addInt(final int i, final String s, final String... array) {
            this.mItems.add(new SliceItem(i, "int", s, array));
            return this;
        }
        
        public Builder addItem(final SliceItem e) {
            this.mItems.add(e);
            return this;
        }
        
        public Builder addLong(final long n, final String s, final List<String> list) {
            this.addLong(n, s, (String[])list.toArray(new String[list.size()]));
            return this;
        }
        
        public Builder addLong(final long l, final String s, final String... array) {
            this.mItems.add(new SliceItem(l, "long", s, array));
            return this;
        }
        
        public Builder addRemoteInput(final RemoteInput remoteInput, final String s, final List<String> list) {
            Preconditions.checkNotNull(remoteInput);
            this.addRemoteInput(remoteInput, s, (String[])list.toArray(new String[list.size()]));
            return this;
        }
        
        public Builder addRemoteInput(final RemoteInput remoteInput, final String s, final String... array) {
            Preconditions.checkNotNull(remoteInput);
            this.mItems.add(new SliceItem(remoteInput, "input", s, array));
            return this;
        }
        
        public Builder addSubSlice(final Slice slice) {
            Preconditions.checkNotNull(slice);
            this.addSubSlice(slice, null);
            return this;
        }
        
        public Builder addSubSlice(final Slice slice, final String s) {
            Preconditions.checkNotNull(slice);
            this.mItems.add(new SliceItem(slice, "slice", s, slice.getHintArray()));
            return this;
        }
        
        public Builder addText(final CharSequence charSequence, final String s, final List<String> list) {
            this.addText(charSequence, s, (String[])list.toArray(new String[list.size()]));
            return this;
        }
        
        public Builder addText(final CharSequence charSequence, final String s, final String... array) {
            this.mItems.add(new SliceItem(charSequence, "text", s, array));
            return this;
        }
        
        @Deprecated
        public Builder addTimestamp(final long l, final String s, final String... array) {
            this.mItems.add(new SliceItem(l, "long", s, array));
            return this;
        }
        
        public Slice build() {
            final ArrayList<SliceItem> mItems = this.mItems;
            final ArrayList<String> mHints = this.mHints;
            return new Slice(mItems, mHints.toArray(new String[mHints.size()]), this.mUri, this.mSpec);
        }
        
        public Builder setSpec(final SliceSpec mSpec) {
            this.mSpec = mSpec;
            return this;
        }
    }
}
