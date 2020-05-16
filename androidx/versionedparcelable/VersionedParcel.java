// 
// Decompiled by Procyon v0.5.36
// 

package androidx.versionedparcelable;

import java.io.ObjectStreamClass;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import android.os.IBinder;
import java.io.Serializable;
import android.os.Parcelable;
import java.lang.reflect.Method;
import androidx.collection.SimpleArrayMap;

public abstract class VersionedParcel
{
    final SimpleArrayMap<String, Class<?>> mParcelizerCache;
    final SimpleArrayMap<String, Method> mReadCache;
    final SimpleArrayMap<String, Method> mWriteCache;
    
    VersionedParcel(final SimpleArrayMap<String, Method> mReadCache, final SimpleArrayMap<String, Method> mWriteCache, final SimpleArrayMap<String, Class<?>> mParcelizerCache) {
        this.mReadCache = mReadCache;
        this.mWriteCache = mWriteCache;
        this.mParcelizerCache = mParcelizerCache;
    }
    
    private Class<?> findParcelClass(final Class<?> clazz) throws ClassNotFoundException {
        Class<?> forName;
        if ((forName = this.mParcelizerCache.get(clazz.getName())) == null) {
            forName = Class.forName(String.format("%s.%sParcelizer", clazz.getPackage().getName(), clazz.getSimpleName()), false, clazz.getClassLoader());
            this.mParcelizerCache.put(clazz.getName(), forName);
        }
        return forName;
    }
    
    private Method getReadMethod(final String name) throws IllegalAccessException, NoSuchMethodException, ClassNotFoundException {
        Method declaredMethod;
        if ((declaredMethod = this.mReadCache.get(name)) == null) {
            declaredMethod = Class.forName(name, true, VersionedParcel.class.getClassLoader()).getDeclaredMethod("read", VersionedParcel.class);
            this.mReadCache.put(name, declaredMethod);
        }
        return declaredMethod;
    }
    
    private <T> int getType(final T t) {
        if (t instanceof String) {
            return 4;
        }
        if (t instanceof Parcelable) {
            return 2;
        }
        if (t instanceof VersionedParcelable) {
            return 1;
        }
        if (t instanceof Serializable) {
            return 3;
        }
        if (t instanceof IBinder) {
            return 5;
        }
        if (t instanceof Integer) {
            return 7;
        }
        if (t instanceof Float) {
            return 8;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(t.getClass().getName());
        sb.append(" cannot be VersionedParcelled");
        throw new IllegalArgumentException(sb.toString());
    }
    
    private Method getWriteMethod(final Class<?> clazz) throws IllegalAccessException, NoSuchMethodException, ClassNotFoundException {
        Method declaredMethod;
        if ((declaredMethod = this.mWriteCache.get(clazz.getName())) == null) {
            declaredMethod = this.findParcelClass(clazz).getDeclaredMethod("write", clazz, VersionedParcel.class);
            this.mWriteCache.put(clazz.getName(), declaredMethod);
        }
        return declaredMethod;
    }
    
    private void writeSerializable(final Serializable obj) {
        if (obj == null) {
            this.writeString(null);
            return;
        }
        final String name = obj.getClass().getName();
        this.writeString(name);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
            objectOutputStream.writeObject(obj);
            objectOutputStream.close();
            this.writeByteArray(out.toByteArray());
        }
        catch (IOException cause) {
            final StringBuilder sb = new StringBuilder();
            sb.append("VersionedParcelable encountered IOException writing serializable object (name = ");
            sb.append(name);
            sb.append(")");
            throw new RuntimeException(sb.toString(), cause);
        }
    }
    
    private void writeVersionedParcelableCreator(final VersionedParcelable versionedParcelable) {
        try {
            this.writeString(this.findParcelClass(versionedParcelable.getClass()).getName());
        }
        catch (ClassNotFoundException cause) {
            final StringBuilder sb = new StringBuilder();
            sb.append(versionedParcelable.getClass().getSimpleName());
            sb.append(" does not have a Parcelizer");
            throw new RuntimeException(sb.toString(), cause);
        }
    }
    
    protected abstract void closeField();
    
    protected abstract VersionedParcel createSubParcel();
    
    public boolean isStream() {
        return false;
    }
    
    protected <T> T[] readArray(final T[] a) {
        int i = this.readInt();
        if (i < 0) {
            return null;
        }
        final ArrayList list = new ArrayList<String>(i);
        if (i != 0) {
            final int int1 = this.readInt();
            if (i < 0) {
                return null;
            }
            int j = i;
            if (int1 != 1) {
                int k = i;
                if (int1 != 2) {
                    int l = i;
                    if (int1 != 3) {
                        int n = i;
                        if (int1 != 4) {
                            if (int1 == 5) {
                                while (i > 0) {
                                    list.add((String)this.readStrongBinder());
                                    --i;
                                }
                            }
                        }
                        else {
                            while (n > 0) {
                                list.add(this.readString());
                                --n;
                            }
                        }
                    }
                    else {
                        while (l > 0) {
                            list.add((String)this.readSerializable());
                            --l;
                        }
                    }
                }
                else {
                    while (k > 0) {
                        list.add(this.readParcelable());
                        --k;
                    }
                }
            }
            else {
                while (j > 0) {
                    list.add(this.readVersionedParcelable());
                    --j;
                }
            }
        }
        return list.toArray(a);
    }
    
    public <T> T[] readArray(final T[] array, final int n) {
        if (!this.readField(n)) {
            return array;
        }
        return this.readArray(array);
    }
    
    protected abstract boolean readBoolean();
    
    public boolean readBoolean(final boolean b, final int n) {
        if (!this.readField(n)) {
            return b;
        }
        return this.readBoolean();
    }
    
    protected abstract byte[] readByteArray();
    
    public byte[] readByteArray(final byte[] array, final int n) {
        if (!this.readField(n)) {
            return array;
        }
        return this.readByteArray();
    }
    
    protected abstract CharSequence readCharSequence();
    
    public CharSequence readCharSequence(final CharSequence charSequence, final int n) {
        if (!this.readField(n)) {
            return charSequence;
        }
        return this.readCharSequence();
    }
    
    protected abstract boolean readField(final int p0);
    
    protected <T extends VersionedParcelable> T readFromParcel(final String s, final VersionedParcel versionedParcel) {
        try {
            return (T)this.getReadMethod(s).invoke(null, versionedParcel);
        }
        catch (ClassNotFoundException cause) {
            throw new RuntimeException(cause);
        }
        catch (NoSuchMethodException cause2) {
            throw new RuntimeException(cause2);
        }
        catch (InvocationTargetException cause4) {
            final Throwable cause3 = cause4.getCause();
            if (cause3 instanceof RuntimeException) {
                throw (RuntimeException)cause3;
            }
            if (cause3 instanceof Error) {
                throw (Error)cause3;
            }
            throw new RuntimeException(cause4);
        }
        catch (IllegalAccessException cause5) {
            throw new RuntimeException(cause5);
        }
    }
    
    protected abstract int readInt();
    
    public int readInt(final int n, final int n2) {
        if (!this.readField(n2)) {
            return n;
        }
        return this.readInt();
    }
    
    protected abstract long readLong();
    
    public long readLong(final long n, final int n2) {
        if (!this.readField(n2)) {
            return n;
        }
        return this.readLong();
    }
    
    protected abstract <T extends Parcelable> T readParcelable();
    
    public <T extends Parcelable> T readParcelable(final T t, final int n) {
        if (!this.readField(n)) {
            return t;
        }
        return this.readParcelable();
    }
    
    protected Serializable readSerializable() {
        final String string = this.readString();
        if (string == null) {
            return null;
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.readByteArray());
        try {
            return (Serializable)new ObjectInputStream(this, byteArrayInputStream) {
                @Override
                protected Class<?> resolveClass(final ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                    final Class<?> forName = Class.forName(desc.getName(), false, VersionedParcel$1.class.getClassLoader());
                    if (forName != null) {
                        return forName;
                    }
                    return super.resolveClass(desc);
                }
            }.readObject();
        }
        catch (ClassNotFoundException cause) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Unable to read Serializable object (name = ");
            sb.append(string);
            sb.append(")");
            throw new RuntimeException(sb.toString(), cause);
        }
        catch (IOException cause2) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Unable to read Serializable object (name = ");
            sb2.append(string);
            sb2.append(")");
            throw new RuntimeException(sb2.toString(), cause2);
        }
    }
    
    protected abstract String readString();
    
    public String readString(final String s, final int n) {
        if (!this.readField(n)) {
            return s;
        }
        return this.readString();
    }
    
    protected abstract IBinder readStrongBinder();
    
    protected <T extends VersionedParcelable> T readVersionedParcelable() {
        final String string = this.readString();
        if (string == null) {
            return null;
        }
        return (T)this.readFromParcel(string, this.createSubParcel());
    }
    
    public <T extends VersionedParcelable> T readVersionedParcelable(final T t, final int n) {
        if (!this.readField(n)) {
            return t;
        }
        return this.readVersionedParcelable();
    }
    
    protected abstract void setOutputField(final int p0);
    
    public void setSerializationFlags(final boolean b, final boolean b2) {
    }
    
    protected <T> void writeArray(final T[] array) {
        if (array == null) {
            this.writeInt(-1);
            return;
        }
        final int length = array.length;
        this.writeInt(length);
        if (length > 0) {
            final int n = 0;
            final int n2 = 0;
            final int n3 = 0;
            int i = 0;
            final int n4 = 0;
            final int type = this.getType(array[0]);
            this.writeInt(type);
            if (type != 1) {
                int j = n3;
                if (type != 2) {
                    int k = n2;
                    if (type != 3) {
                        int l = n;
                        if (type != 4) {
                            int n5 = n4;
                            if (type == 5) {
                                while (n5 < length) {
                                    this.writeStrongBinder((IBinder)array[n5]);
                                    ++n5;
                                }
                            }
                        }
                        else {
                            while (l < length) {
                                this.writeString((String)array[l]);
                                ++l;
                            }
                        }
                    }
                    else {
                        while (k < length) {
                            this.writeSerializable((Serializable)array[k]);
                            ++k;
                        }
                    }
                }
                else {
                    while (j < length) {
                        this.writeParcelable((Parcelable)array[j]);
                        ++j;
                    }
                }
            }
            else {
                while (i < length) {
                    this.writeVersionedParcelable((VersionedParcelable)array[i]);
                    ++i;
                }
            }
        }
    }
    
    public <T> void writeArray(final T[] array, final int outputField) {
        this.setOutputField(outputField);
        this.writeArray(array);
    }
    
    protected abstract void writeBoolean(final boolean p0);
    
    public void writeBoolean(final boolean b, final int outputField) {
        this.setOutputField(outputField);
        this.writeBoolean(b);
    }
    
    protected abstract void writeByteArray(final byte[] p0);
    
    public void writeByteArray(final byte[] array, final int outputField) {
        this.setOutputField(outputField);
        this.writeByteArray(array);
    }
    
    protected abstract void writeCharSequence(final CharSequence p0);
    
    public void writeCharSequence(final CharSequence charSequence, final int outputField) {
        this.setOutputField(outputField);
        this.writeCharSequence(charSequence);
    }
    
    protected abstract void writeInt(final int p0);
    
    public void writeInt(final int n, final int outputField) {
        this.setOutputField(outputField);
        this.writeInt(n);
    }
    
    protected abstract void writeLong(final long p0);
    
    public void writeLong(final long n, final int outputField) {
        this.setOutputField(outputField);
        this.writeLong(n);
    }
    
    protected abstract void writeParcelable(final Parcelable p0);
    
    public void writeParcelable(final Parcelable parcelable, final int outputField) {
        this.setOutputField(outputField);
        this.writeParcelable(parcelable);
    }
    
    protected abstract void writeString(final String p0);
    
    public void writeString(final String s, final int outputField) {
        this.setOutputField(outputField);
        this.writeString(s);
    }
    
    protected abstract void writeStrongBinder(final IBinder p0);
    
    protected <T extends VersionedParcelable> void writeToParcel(final T t, final VersionedParcel versionedParcel) {
        try {
            this.getWriteMethod(t.getClass()).invoke(null, t, versionedParcel);
        }
        catch (ClassNotFoundException cause) {
            throw new RuntimeException(cause);
        }
        catch (NoSuchMethodException cause2) {
            throw new RuntimeException(cause2);
        }
        catch (InvocationTargetException cause4) {
            final Throwable cause3 = cause4.getCause();
            if (cause3 instanceof RuntimeException) {
                throw (RuntimeException)cause3;
            }
            if (cause3 instanceof Error) {
                throw (Error)cause3;
            }
            throw new RuntimeException(cause4);
        }
        catch (IllegalAccessException cause5) {
            throw new RuntimeException(cause5);
        }
    }
    
    protected void writeVersionedParcelable(final VersionedParcelable versionedParcelable) {
        if (versionedParcelable == null) {
            this.writeString(null);
            return;
        }
        this.writeVersionedParcelableCreator(versionedParcelable);
        final VersionedParcel subParcel = this.createSubParcel();
        this.writeToParcel(versionedParcelable, subParcel);
        subParcel.closeField();
    }
    
    public void writeVersionedParcelable(final VersionedParcelable versionedParcelable, final int outputField) {
        this.setOutputField(outputField);
        this.writeVersionedParcelable(versionedParcelable);
    }
}
