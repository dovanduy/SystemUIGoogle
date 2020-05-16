// 
// Decompiled by Procyon v0.5.36
// 

package androidx.versionedparcelable;

import android.os.IBinder;
import android.os.Parcelable;
import android.text.TextUtils;
import java.lang.reflect.Method;
import androidx.collection.SimpleArrayMap;
import android.util.SparseIntArray;
import android.os.Parcel;

class VersionedParcelParcel extends VersionedParcel
{
    private int mCurrentField;
    private final int mEnd;
    private int mFieldId;
    private int mNextRead;
    private final int mOffset;
    private final Parcel mParcel;
    private final SparseIntArray mPositionLookup;
    private final String mPrefix;
    
    VersionedParcelParcel(final Parcel parcel) {
        this(parcel, parcel.dataPosition(), parcel.dataSize(), "", new SimpleArrayMap<String, Method>(), new SimpleArrayMap<String, Method>(), new SimpleArrayMap<String, Class<?>>());
    }
    
    private VersionedParcelParcel(final Parcel mParcel, final int n, final int mEnd, final String mPrefix, final SimpleArrayMap<String, Method> simpleArrayMap, final SimpleArrayMap<String, Method> simpleArrayMap2, final SimpleArrayMap<String, Class<?>> simpleArrayMap3) {
        super(simpleArrayMap, simpleArrayMap2, simpleArrayMap3);
        this.mPositionLookup = new SparseIntArray();
        this.mCurrentField = -1;
        this.mNextRead = 0;
        this.mFieldId = -1;
        this.mParcel = mParcel;
        this.mOffset = n;
        this.mEnd = mEnd;
        this.mNextRead = n;
        this.mPrefix = mPrefix;
    }
    
    public void closeField() {
        final int mCurrentField = this.mCurrentField;
        if (mCurrentField >= 0) {
            final int value = this.mPositionLookup.get(mCurrentField);
            final int dataPosition = this.mParcel.dataPosition();
            this.mParcel.setDataPosition(value);
            this.mParcel.writeInt(dataPosition - value);
            this.mParcel.setDataPosition(dataPosition);
        }
    }
    
    @Override
    protected VersionedParcel createSubParcel() {
        final Parcel mParcel = this.mParcel;
        final int dataPosition = mParcel.dataPosition();
        int n;
        if ((n = this.mNextRead) == this.mOffset) {
            n = this.mEnd;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(this.mPrefix);
        sb.append("  ");
        return new VersionedParcelParcel(mParcel, dataPosition, n, sb.toString(), super.mReadCache, super.mWriteCache, super.mParcelizerCache);
    }
    
    public boolean readBoolean() {
        return this.mParcel.readInt() != 0;
    }
    
    public byte[] readByteArray() {
        final int int1 = this.mParcel.readInt();
        if (int1 < 0) {
            return null;
        }
        final byte[] array = new byte[int1];
        this.mParcel.readByteArray(array);
        return array;
    }
    
    @Override
    protected CharSequence readCharSequence() {
        return (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(this.mParcel);
    }
    
    public boolean readField(final int i) {
        while (true) {
            final int mNextRead = this.mNextRead;
            final int mEnd = this.mEnd;
            boolean b = true;
            if (mNextRead >= mEnd) {
                if (this.mFieldId != i) {
                    b = false;
                }
                return b;
            }
            final int mFieldId = this.mFieldId;
            if (mFieldId == i) {
                return true;
            }
            if (String.valueOf(mFieldId).compareTo(String.valueOf(i)) > 0) {
                return false;
            }
            this.mParcel.setDataPosition(this.mNextRead);
            final int int1 = this.mParcel.readInt();
            this.mFieldId = this.mParcel.readInt();
            this.mNextRead += int1;
        }
    }
    
    public int readInt() {
        return this.mParcel.readInt();
    }
    
    public long readLong() {
        return this.mParcel.readLong();
    }
    
    public <T extends Parcelable> T readParcelable() {
        return (T)this.mParcel.readParcelable(VersionedParcelParcel.class.getClassLoader());
    }
    
    public String readString() {
        return this.mParcel.readString();
    }
    
    public IBinder readStrongBinder() {
        return this.mParcel.readStrongBinder();
    }
    
    public void setOutputField(final int mCurrentField) {
        this.closeField();
        this.mCurrentField = mCurrentField;
        this.mPositionLookup.put(mCurrentField, this.mParcel.dataPosition());
        this.writeInt(0);
        this.writeInt(mCurrentField);
    }
    
    public void writeBoolean(final boolean b) {
        this.mParcel.writeInt((int)(b ? 1 : 0));
    }
    
    public void writeByteArray(final byte[] array) {
        if (array != null) {
            this.mParcel.writeInt(array.length);
            this.mParcel.writeByteArray(array);
        }
        else {
            this.mParcel.writeInt(-1);
        }
    }
    
    @Override
    protected void writeCharSequence(final CharSequence charSequence) {
        TextUtils.writeToParcel(charSequence, this.mParcel, 0);
    }
    
    public void writeInt(final int n) {
        this.mParcel.writeInt(n);
    }
    
    public void writeLong(final long n) {
        this.mParcel.writeLong(n);
    }
    
    public void writeParcelable(final Parcelable parcelable) {
        this.mParcel.writeParcelable(parcelable, 0);
    }
    
    public void writeString(final String s) {
        this.mParcel.writeString(s);
    }
    
    public void writeStrongBinder(final IBinder binder) {
        this.mParcel.writeStrongBinder(binder);
    }
}
