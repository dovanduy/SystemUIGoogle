// 
// Decompiled by Procyon v0.5.36
// 

package androidx.collection;

import java.lang.reflect.Array;
import java.util.Objects;
import java.util.Iterator;
import java.util.Map;
import java.util.ConcurrentModificationException;
import java.util.Set;
import java.util.Collection;

public final class ArraySet<E> implements Collection<E>, Set<E>
{
    private static final int[] INT;
    private static final Object[] OBJECT;
    private static Object[] sBaseCache;
    private static final Object sBaseCacheLock;
    private static int sBaseCacheSize;
    private static Object[] sTwiceBaseCache;
    private static final Object sTwiceBaseCacheLock;
    private static int sTwiceBaseCacheSize;
    Object[] mArray;
    private MapCollections<E, E> mCollections;
    private int[] mHashes;
    int mSize;
    
    static {
        INT = new int[0];
        OBJECT = new Object[0];
        sBaseCacheLock = new Object();
        sTwiceBaseCacheLock = new Object();
    }
    
    public ArraySet() {
        this(0);
    }
    
    public ArraySet(final int n) {
        if (n == 0) {
            this.mHashes = ArraySet.INT;
            this.mArray = ArraySet.OBJECT;
        }
        else {
            this.allocArrays(n);
        }
        this.mSize = 0;
    }
    
    public ArraySet(final Collection<E> collection) {
        this();
        if (collection != null) {
            this.addAll((Collection<? extends E>)collection);
        }
    }
    
    private void allocArrays(final int p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: bipush          8
        //     3: if_icmpne       151
        //     6: getstatic       androidx/collection/ArraySet.sTwiceBaseCacheLock:Ljava/lang/Object;
        //     9: astore_2       
        //    10: aload_2        
        //    11: monitorenter   
        //    12: getstatic       androidx/collection/ArraySet.sTwiceBaseCache:[Ljava/lang/Object;
        //    15: ifnull          141
        //    18: getstatic       androidx/collection/ArraySet.sTwiceBaseCache:[Ljava/lang/Object;
        //    21: astore_3       
        //    22: aload_0        
        //    23: aload_3        
        //    24: putfield        androidx/collection/ArraySet.mArray:[Ljava/lang/Object;
        //    27: aload_3        
        //    28: iconst_0       
        //    29: aaload         
        //    30: checkcast       [Ljava/lang/Object;
        //    33: putstatic       androidx/collection/ArraySet.sTwiceBaseCache:[Ljava/lang/Object;
        //    36: aload_3        
        //    37: iconst_1       
        //    38: aaload         
        //    39: checkcast       [I
        //    42: astore          4
        //    44: aload_0        
        //    45: aload           4
        //    47: putfield        androidx/collection/ArraySet.mHashes:[I
        //    50: aload           4
        //    52: ifnull          74
        //    55: aload_3        
        //    56: iconst_1       
        //    57: aconst_null    
        //    58: aastore        
        //    59: aload_3        
        //    60: iconst_0       
        //    61: aconst_null    
        //    62: aastore        
        //    63: getstatic       androidx/collection/ArraySet.sTwiceBaseCacheSize:I
        //    66: iconst_1       
        //    67: isub           
        //    68: putstatic       androidx/collection/ArraySet.sTwiceBaseCacheSize:I
        //    71: aload_2        
        //    72: monitorexit    
        //    73: return         
        //    74: getstatic       java/lang/System.out:Ljava/io/PrintStream;
        //    77: astore          4
        //    79: new             Ljava/lang/StringBuilder;
        //    82: astore          5
        //    84: aload           5
        //    86: invokespecial   java/lang/StringBuilder.<init>:()V
        //    89: aload           5
        //    91: ldc             "ArraySet Found corrupt ArraySet cache: [0]="
        //    93: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    96: pop            
        //    97: aload           5
        //    99: aload_3        
        //   100: iconst_0       
        //   101: aaload         
        //   102: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   105: pop            
        //   106: aload           5
        //   108: ldc             " [1]="
        //   110: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   113: pop            
        //   114: aload           5
        //   116: aload_3        
        //   117: iconst_1       
        //   118: aaload         
        //   119: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   122: pop            
        //   123: aload           4
        //   125: aload           5
        //   127: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   130: invokevirtual   java/io/PrintStream.println:(Ljava/lang/String;)V
        //   133: aconst_null    
        //   134: putstatic       androidx/collection/ArraySet.sTwiceBaseCache:[Ljava/lang/Object;
        //   137: iconst_0       
        //   138: putstatic       androidx/collection/ArraySet.sTwiceBaseCacheSize:I
        //   141: aload_2        
        //   142: monitorexit    
        //   143: goto            301
        //   146: astore_3       
        //   147: aload_2        
        //   148: monitorexit    
        //   149: aload_3        
        //   150: athrow         
        //   151: iload_1        
        //   152: iconst_4       
        //   153: if_icmpne       301
        //   156: getstatic       androidx/collection/ArraySet.sBaseCacheLock:Ljava/lang/Object;
        //   159: astore_2       
        //   160: aload_2        
        //   161: monitorenter   
        //   162: getstatic       androidx/collection/ArraySet.sBaseCache:[Ljava/lang/Object;
        //   165: ifnull          291
        //   168: getstatic       androidx/collection/ArraySet.sBaseCache:[Ljava/lang/Object;
        //   171: astore_3       
        //   172: aload_0        
        //   173: aload_3        
        //   174: putfield        androidx/collection/ArraySet.mArray:[Ljava/lang/Object;
        //   177: aload_3        
        //   178: iconst_0       
        //   179: aaload         
        //   180: checkcast       [Ljava/lang/Object;
        //   183: putstatic       androidx/collection/ArraySet.sBaseCache:[Ljava/lang/Object;
        //   186: aload_3        
        //   187: iconst_1       
        //   188: aaload         
        //   189: checkcast       [I
        //   192: astore          4
        //   194: aload_0        
        //   195: aload           4
        //   197: putfield        androidx/collection/ArraySet.mHashes:[I
        //   200: aload           4
        //   202: ifnull          224
        //   205: aload_3        
        //   206: iconst_1       
        //   207: aconst_null    
        //   208: aastore        
        //   209: aload_3        
        //   210: iconst_0       
        //   211: aconst_null    
        //   212: aastore        
        //   213: getstatic       androidx/collection/ArraySet.sBaseCacheSize:I
        //   216: iconst_1       
        //   217: isub           
        //   218: putstatic       androidx/collection/ArraySet.sBaseCacheSize:I
        //   221: aload_2        
        //   222: monitorexit    
        //   223: return         
        //   224: getstatic       java/lang/System.out:Ljava/io/PrintStream;
        //   227: astore          4
        //   229: new             Ljava/lang/StringBuilder;
        //   232: astore          5
        //   234: aload           5
        //   236: invokespecial   java/lang/StringBuilder.<init>:()V
        //   239: aload           5
        //   241: ldc             "ArraySet Found corrupt ArraySet cache: [0]="
        //   243: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   246: pop            
        //   247: aload           5
        //   249: aload_3        
        //   250: iconst_0       
        //   251: aaload         
        //   252: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   255: pop            
        //   256: aload           5
        //   258: ldc             " [1]="
        //   260: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   263: pop            
        //   264: aload           5
        //   266: aload_3        
        //   267: iconst_1       
        //   268: aaload         
        //   269: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   272: pop            
        //   273: aload           4
        //   275: aload           5
        //   277: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   280: invokevirtual   java/io/PrintStream.println:(Ljava/lang/String;)V
        //   283: aconst_null    
        //   284: putstatic       androidx/collection/ArraySet.sBaseCache:[Ljava/lang/Object;
        //   287: iconst_0       
        //   288: putstatic       androidx/collection/ArraySet.sBaseCacheSize:I
        //   291: aload_2        
        //   292: monitorexit    
        //   293: goto            301
        //   296: astore_3       
        //   297: aload_2        
        //   298: monitorexit    
        //   299: aload_3        
        //   300: athrow         
        //   301: aload_0        
        //   302: iload_1        
        //   303: newarray        I
        //   305: putfield        androidx/collection/ArraySet.mHashes:[I
        //   308: aload_0        
        //   309: iload_1        
        //   310: anewarray       Ljava/lang/Object;
        //   313: putfield        androidx/collection/ArraySet.mArray:[Ljava/lang/Object;
        //   316: return         
        //   317: astore          4
        //   319: goto            74
        //   322: astore          4
        //   324: goto            224
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                          
        //  -----  -----  -----  -----  ------------------------------
        //  12     22     146    151    Any
        //  22     50     317    322    Ljava/lang/ClassCastException;
        //  22     50     146    151    Any
        //  63     71     317    322    Ljava/lang/ClassCastException;
        //  63     71     146    151    Any
        //  71     73     146    151    Any
        //  74     141    146    151    Any
        //  141    143    146    151    Any
        //  147    149    146    151    Any
        //  162    172    296    301    Any
        //  172    200    322    327    Ljava/lang/ClassCastException;
        //  172    200    296    301    Any
        //  213    221    322    327    Ljava/lang/ClassCastException;
        //  213    221    296    301    Any
        //  221    223    296    301    Any
        //  224    291    296    301    Any
        //  291    293    296    301    Any
        //  297    299    296    301    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.assembler.ir.StackMappingVisitor.push(StackMappingVisitor.java:290)
        //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.execute(StackMappingVisitor.java:833)
        //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.visit(StackMappingVisitor.java:398)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2030)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:211)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private int binarySearch(final int[] array, int binarySearch) {
        try {
            binarySearch = ContainerHelpers.binarySearch(array, this.mSize, binarySearch);
            return binarySearch;
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            throw new ConcurrentModificationException();
        }
    }
    
    private static void freeArrays(final int[] array, final Object[] array2, int i) {
        if (array.length == 8) {
            synchronized (ArraySet.sTwiceBaseCacheLock) {
                if (ArraySet.sTwiceBaseCacheSize < 10) {
                    array2[0] = ArraySet.sTwiceBaseCache;
                    array2[1] = array;
                    --i;
                    while (i >= 2) {
                        array2[i] = null;
                        --i;
                    }
                    ArraySet.sTwiceBaseCache = array2;
                    ++ArraySet.sTwiceBaseCacheSize;
                }
                return;
            }
        }
        if (array.length == 4) {
            synchronized (ArraySet.sBaseCacheLock) {
                if (ArraySet.sBaseCacheSize < 10) {
                    array2[0] = ArraySet.sBaseCache;
                    array2[1] = array;
                    --i;
                    while (i >= 2) {
                        array2[i] = null;
                        --i;
                    }
                    ArraySet.sBaseCache = array2;
                    ++ArraySet.sBaseCacheSize;
                }
            }
        }
    }
    
    private MapCollections<E, E> getCollection() {
        if (this.mCollections == null) {
            this.mCollections = new MapCollections<E, E>() {
                @Override
                protected void colClear() {
                    throw new UnsupportedOperationException();
                }
                
                @Override
                protected Object colGetEntry(final int n, final int n2) {
                    return ArraySet.this.mArray[n];
                }
                
                @Override
                protected Map<E, E> colGetMap() {
                    throw new UnsupportedOperationException();
                }
                
                @Override
                protected int colGetSize() {
                    return ArraySet.this.mSize;
                }
                
                @Override
                protected int colIndexOfKey(final Object o) {
                    throw new UnsupportedOperationException();
                }
                
                @Override
                protected int colIndexOfValue(final Object o) {
                    throw new UnsupportedOperationException();
                }
                
                @Override
                protected void colPut(final E e, final E e2) {
                    throw new UnsupportedOperationException();
                }
                
                @Override
                protected void colRemoveAt(final int n) {
                    ArraySet.this.removeAt(n);
                }
                
                @Override
                protected E colSetValue(final int n, final E e) {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return this.mCollections;
    }
    
    private int indexOf(final Object o, final int n) {
        final int mSize = this.mSize;
        if (mSize == 0) {
            return -1;
        }
        final int binarySearch = this.binarySearch(this.mHashes, n);
        if (binarySearch < 0) {
            return binarySearch;
        }
        if (o.equals(this.mArray[binarySearch])) {
            return binarySearch;
        }
        int n2;
        for (n2 = binarySearch + 1; n2 < mSize && this.mHashes[n2] == n; ++n2) {
            if (o.equals(this.mArray[n2])) {
                return n2;
            }
        }
        for (int n3 = binarySearch - 1; n3 >= 0 && this.mHashes[n3] == n; --n3) {
            if (o.equals(this.mArray[n3])) {
                return n3;
            }
        }
        return n2;
    }
    
    private int indexOfNull() {
        final int mSize = this.mSize;
        if (mSize == 0) {
            return -1;
        }
        int binarySearch = this.binarySearch(this.mHashes, 0);
        if (binarySearch < 0) {
            return binarySearch;
        }
        if (this.mArray[binarySearch] == null) {
            return binarySearch;
        }
        int n;
        for (n = binarySearch + 1; n < mSize && this.mHashes[n] == 0; ++n) {
            if (this.mArray[n] == null) {
                return n;
            }
        }
        --binarySearch;
        while (binarySearch >= 0 && this.mHashes[binarySearch] == 0) {
            if (this.mArray[binarySearch] == null) {
                return binarySearch;
            }
            --binarySearch;
        }
        return n;
    }
    
    @Override
    public boolean add(final E e) {
        final int mSize = this.mSize;
        int n;
        int hashCode;
        if (e == null) {
            n = this.indexOfNull();
            hashCode = 0;
        }
        else {
            hashCode = e.hashCode();
            n = this.indexOf(e, hashCode);
        }
        if (n >= 0) {
            return false;
        }
        final int n2 = n;
        if (mSize >= this.mHashes.length) {
            int n3 = 4;
            if (mSize >= 8) {
                n3 = (mSize >> 1) + mSize;
            }
            else if (mSize >= 4) {
                n3 = 8;
            }
            final int[] mHashes = this.mHashes;
            final Object[] mArray = this.mArray;
            this.allocArrays(n3);
            if (mSize != this.mSize) {
                throw new ConcurrentModificationException();
            }
            final int[] mHashes2 = this.mHashes;
            if (mHashes2.length > 0) {
                System.arraycopy(mHashes, 0, mHashes2, 0, mHashes.length);
                System.arraycopy(mArray, 0, this.mArray, 0, mArray.length);
            }
            freeArrays(mHashes, mArray, mSize);
        }
        if (n2 < mSize) {
            final int[] mHashes3 = this.mHashes;
            final int n4 = n2 + 1;
            final int n5 = mSize - n2;
            System.arraycopy(mHashes3, n2, mHashes3, n4, n5);
            final Object[] mArray2 = this.mArray;
            System.arraycopy(mArray2, n2, mArray2, n4, n5);
        }
        final int mSize2 = this.mSize;
        if (mSize == mSize2) {
            final int[] mHashes4 = this.mHashes;
            if (n2 < mHashes4.length) {
                mHashes4[n2] = hashCode;
                this.mArray[n2] = e;
                this.mSize = mSize2 + 1;
                return true;
            }
        }
        throw new ConcurrentModificationException();
    }
    
    public void addAll(final ArraySet<? extends E> set) {
        final int mSize = set.mSize;
        this.ensureCapacity(this.mSize + mSize);
        final int mSize2 = this.mSize;
        int i = 0;
        if (mSize2 == 0) {
            if (mSize > 0) {
                System.arraycopy(set.mHashes, 0, this.mHashes, 0, mSize);
                System.arraycopy(set.mArray, 0, this.mArray, 0, mSize);
                if (this.mSize != 0) {
                    throw new ConcurrentModificationException();
                }
                this.mSize = mSize;
            }
        }
        else {
            while (i < mSize) {
                this.add(set.valueAt(i));
                ++i;
            }
        }
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> collection) {
        this.ensureCapacity(this.mSize + collection.size());
        final Iterator<? extends E> iterator = collection.iterator();
        boolean b = false;
        while (iterator.hasNext()) {
            b |= this.add(iterator.next());
        }
        return b;
    }
    
    @Override
    public void clear() {
        final int mSize = this.mSize;
        if (mSize != 0) {
            final int[] mHashes = this.mHashes;
            final Object[] mArray = this.mArray;
            this.mHashes = ArraySet.INT;
            this.mArray = ArraySet.OBJECT;
            this.mSize = 0;
            freeArrays(mHashes, mArray, mSize);
        }
        if (this.mSize == 0) {
            return;
        }
        throw new ConcurrentModificationException();
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.indexOf(o) >= 0;
    }
    
    @Override
    public boolean containsAll(final Collection<?> collection) {
        final Iterator<?> iterator = collection.iterator();
        while (iterator.hasNext()) {
            if (!this.contains(iterator.next())) {
                return false;
            }
        }
        return true;
    }
    
    public void ensureCapacity(int mSize) {
        final int mSize2 = this.mSize;
        final int[] mHashes = this.mHashes;
        if (mHashes.length < mSize) {
            final Object[] mArray = this.mArray;
            this.allocArrays(mSize);
            mSize = this.mSize;
            if (mSize > 0) {
                System.arraycopy(mHashes, 0, this.mHashes, 0, mSize);
                System.arraycopy(mArray, 0, this.mArray, 0, this.mSize);
            }
            freeArrays(mHashes, mArray, this.mSize);
        }
        if (this.mSize == mSize2) {
            return;
        }
        throw new ConcurrentModificationException();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Set)) {
            return false;
        }
        final Set set = (Set)o;
        if (this.size() != set.size()) {
            return false;
        }
        int i = 0;
        try {
            while (i < this.mSize) {
                if (!set.contains(this.valueAt(i))) {
                    return false;
                }
                ++i;
            }
            return true;
        }
        catch (NullPointerException | ClassCastException ex) {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        final int[] mHashes = this.mHashes;
        final int mSize = this.mSize;
        int i = 0;
        int n = 0;
        while (i < mSize) {
            n += mHashes[i];
            ++i;
        }
        return n;
    }
    
    public int indexOf(final Object o) {
        int n;
        if (o == null) {
            n = this.indexOfNull();
        }
        else {
            n = this.indexOf(o, o.hashCode());
        }
        return n;
    }
    
    @Override
    public boolean isEmpty() {
        return this.mSize <= 0;
    }
    
    @Override
    public Iterator<E> iterator() {
        final MapCollections<E, E> collection = this.getCollection();
        Objects.requireNonNull(collection);
        return (Iterator<E>)collection.new ArrayIterator(0);
    }
    
    @Override
    public boolean remove(final Object o) {
        final int index = this.indexOf(o);
        if (index >= 0) {
            this.removeAt(index);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean removeAll(final Collection<?> collection) {
        final Iterator<?> iterator = collection.iterator();
        boolean b = false;
        while (iterator.hasNext()) {
            b |= this.remove(iterator.next());
        }
        return b;
    }
    
    public E removeAt(final int n) {
        final int mSize = this.mSize;
        final Object o = this.mArray[n];
        if (mSize <= 1) {
            this.clear();
        }
        else {
            final int mSize2 = mSize - 1;
            final int[] mHashes = this.mHashes;
            final int length = mHashes.length;
            int n2 = 8;
            if (length > 8 && mSize < mHashes.length / 3) {
                if (mSize > 8) {
                    n2 = mSize + (mSize >> 1);
                }
                final int[] mHashes2 = this.mHashes;
                final Object[] mArray = this.mArray;
                this.allocArrays(n2);
                if (n > 0) {
                    System.arraycopy(mHashes2, 0, this.mHashes, 0, n);
                    System.arraycopy(mArray, 0, this.mArray, 0, n);
                }
                if (n < mSize2) {
                    final int n3 = n + 1;
                    final int[] mHashes3 = this.mHashes;
                    final int n4 = mSize2 - n;
                    System.arraycopy(mHashes2, n3, mHashes3, n, n4);
                    System.arraycopy(mArray, n3, this.mArray, n, n4);
                }
            }
            else {
                if (n < mSize2) {
                    final int[] mHashes4 = this.mHashes;
                    final int n5 = n + 1;
                    final int n6 = mSize2 - n;
                    System.arraycopy(mHashes4, n5, mHashes4, n, n6);
                    final Object[] mArray2 = this.mArray;
                    System.arraycopy(mArray2, n5, mArray2, n, n6);
                }
                this.mArray[mSize2] = null;
            }
            if (mSize != this.mSize) {
                throw new ConcurrentModificationException();
            }
            this.mSize = mSize2;
        }
        return (E)o;
    }
    
    @Override
    public boolean retainAll(final Collection<?> collection) {
        int i = this.mSize - 1;
        boolean b = false;
        while (i >= 0) {
            if (!collection.contains(this.mArray[i])) {
                this.removeAt(i);
                b = true;
            }
            --i;
        }
        return b;
    }
    
    @Override
    public int size() {
        return this.mSize;
    }
    
    @Override
    public Object[] toArray() {
        final int mSize = this.mSize;
        final Object[] array = new Object[mSize];
        System.arraycopy(this.mArray, 0, array, 0, mSize);
        return array;
    }
    
    @Override
    public <T> T[] toArray(final T[] array) {
        Object[] array2 = array;
        if (array.length < this.mSize) {
            array2 = (Object[])Array.newInstance(array.getClass().getComponentType(), this.mSize);
        }
        System.arraycopy(this.mArray, 0, array2, 0, this.mSize);
        final int length = ((T[])array2).length;
        final int mSize = this.mSize;
        if (length > mSize) {
            array2[mSize] = null;
        }
        return (T[])array2;
    }
    
    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "{}";
        }
        final StringBuilder sb = new StringBuilder(this.mSize * 14);
        sb.append('{');
        for (int i = 0; i < this.mSize; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            final E value = this.valueAt(i);
            if (value != this) {
                sb.append(value);
            }
            else {
                sb.append("(this Set)");
            }
        }
        sb.append('}');
        return sb.toString();
    }
    
    public E valueAt(final int n) {
        return (E)this.mArray[n];
    }
}
