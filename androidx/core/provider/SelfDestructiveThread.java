// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.provider;

import java.util.concurrent.Callable;
import android.os.Message;
import android.os.HandlerThread;
import android.os.Handler;
import android.os.Handler$Callback;

public class SelfDestructiveThread
{
    private Handler$Callback mCallback;
    private final int mDestructAfterMillisec;
    private int mGeneration;
    private Handler mHandler;
    private final Object mLock;
    private final int mPriority;
    private HandlerThread mThread;
    private final String mThreadName;
    
    public SelfDestructiveThread(final String mThreadName, final int mPriority, final int mDestructAfterMillisec) {
        this.mLock = new Object();
        this.mCallback = (Handler$Callback)new Handler$Callback() {
            public boolean handleMessage(final Message message) {
                final int what = message.what;
                if (what == 0) {
                    SelfDestructiveThread.this.onDestruction();
                    return true;
                }
                if (what != 1) {
                    return true;
                }
                SelfDestructiveThread.this.onInvokeRunnable((Runnable)message.obj);
                return true;
            }
        };
        this.mThreadName = mThreadName;
        this.mPriority = mPriority;
        this.mDestructAfterMillisec = mDestructAfterMillisec;
        this.mGeneration = 0;
    }
    
    private void post(final Runnable runnable) {
        synchronized (this.mLock) {
            if (this.mThread == null) {
                (this.mThread = new HandlerThread(this.mThreadName, this.mPriority)).start();
                this.mHandler = new Handler(this.mThread.getLooper(), this.mCallback);
                ++this.mGeneration;
            }
            this.mHandler.removeMessages(0);
            this.mHandler.sendMessage(this.mHandler.obtainMessage(1, (Object)runnable));
        }
    }
    
    public int getGeneration() {
        synchronized (this.mLock) {
            return this.mGeneration;
        }
    }
    
    public boolean isRunning() {
        synchronized (this.mLock) {
            return this.mThread != null;
        }
    }
    
    void onDestruction() {
        synchronized (this.mLock) {
            if (this.mHandler.hasMessages(1)) {
                return;
            }
            this.mThread.quit();
            this.mThread = null;
            this.mHandler = null;
        }
    }
    
    void onInvokeRunnable(final Runnable runnable) {
        runnable.run();
        synchronized (this.mLock) {
            this.mHandler.removeMessages(0);
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(0), (long)this.mDestructAfterMillisec);
        }
    }
    
    public <T> void postAndReply(final Callable<T> callable, final ReplyCallback<T> replyCallback) {
        this.post(new Runnable(this) {
            final /* synthetic */ Handler val$callingHandler = new Handler();
            
            @Override
            public void run() {
                Object call;
                try {
                    call = callable.call();
                }
                catch (Exception ex) {
                    call = null;
                }
                this.val$callingHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        replyCallback.onReply(call);
                    }
                });
            }
        });
    }
    
    public <T> T postAndWait(final Callable<T> p0, final int p1) throws InterruptedException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: dup            
        //     4: invokespecial   java/util/concurrent/locks/ReentrantLock.<init>:()V
        //     7: astore_3       
        //     8: aload_3        
        //     9: invokevirtual   java/util/concurrent/locks/ReentrantLock.newCondition:()Ljava/util/concurrent/locks/Condition;
        //    12: astore          4
        //    14: new             Ljava/util/concurrent/atomic/AtomicReference;
        //    17: dup            
        //    18: invokespecial   java/util/concurrent/atomic/AtomicReference.<init>:()V
        //    21: astore          5
        //    23: new             Ljava/util/concurrent/atomic/AtomicBoolean;
        //    26: dup            
        //    27: iconst_1       
        //    28: invokespecial   java/util/concurrent/atomic/AtomicBoolean.<init>:(Z)V
        //    31: astore          6
        //    33: aload_0        
        //    34: new             Landroidx/core/provider/SelfDestructiveThread$3;
        //    37: dup            
        //    38: aload_0        
        //    39: aload           5
        //    41: aload_1        
        //    42: aload_3        
        //    43: aload           6
        //    45: aload           4
        //    47: invokespecial   androidx/core/provider/SelfDestructiveThread$3.<init>:(Landroidx/core/provider/SelfDestructiveThread;Ljava/util/concurrent/atomic/AtomicReference;Ljava/util/concurrent/Callable;Ljava/util/concurrent/locks/ReentrantLock;Ljava/util/concurrent/atomic/AtomicBoolean;Ljava/util/concurrent/locks/Condition;)V
        //    50: invokespecial   androidx/core/provider/SelfDestructiveThread.post:(Ljava/lang/Runnable;)V
        //    53: aload_3        
        //    54: invokevirtual   java/util/concurrent/locks/ReentrantLock.lock:()V
        //    57: aload           6
        //    59: invokevirtual   java/util/concurrent/atomic/AtomicBoolean.get:()Z
        //    62: ifne            77
        //    65: aload           5
        //    67: invokevirtual   java/util/concurrent/atomic/AtomicReference.get:()Ljava/lang/Object;
        //    70: astore_1       
        //    71: aload_3        
        //    72: invokevirtual   java/util/concurrent/locks/ReentrantLock.unlock:()V
        //    75: aload_1        
        //    76: areturn        
        //    77: getstatic       java/util/concurrent/TimeUnit.MILLISECONDS:Ljava/util/concurrent/TimeUnit;
        //    80: iload_2        
        //    81: i2l            
        //    82: invokevirtual   java/util/concurrent/TimeUnit.toNanos:(J)J
        //    85: lstore          7
        //    87: aload           4
        //    89: lload           7
        //    91: invokeinterface java/util/concurrent/locks/Condition.awaitNanos:(J)J
        //    96: lstore          9
        //    98: lload           9
        //   100: lstore          7
        //   102: aload           6
        //   104: invokevirtual   java/util/concurrent/atomic/AtomicBoolean.get:()Z
        //   107: ifne            122
        //   110: aload           5
        //   112: invokevirtual   java/util/concurrent/atomic/AtomicReference.get:()Ljava/lang/Object;
        //   115: astore_1       
        //   116: aload_3        
        //   117: invokevirtual   java/util/concurrent/locks/ReentrantLock.unlock:()V
        //   120: aload_1        
        //   121: areturn        
        //   122: lload           7
        //   124: lconst_0       
        //   125: lcmp           
        //   126: ifle            132
        //   129: goto            87
        //   132: new             Ljava/lang/InterruptedException;
        //   135: astore_1       
        //   136: aload_1        
        //   137: ldc             "timeout"
        //   139: invokespecial   java/lang/InterruptedException.<init>:(Ljava/lang/String;)V
        //   142: aload_1        
        //   143: athrow         
        //   144: astore_1       
        //   145: aload_3        
        //   146: invokevirtual   java/util/concurrent/locks/ReentrantLock.unlock:()V
        //   149: aload_1        
        //   150: athrow         
        //   151: astore_1       
        //   152: goto            102
        //    Exceptions:
        //  throws java.lang.InterruptedException
        //    Signature:
        //  <T:Ljava/lang/Object;>(Ljava/util/concurrent/Callable<TT;>;I)TT;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                            
        //  -----  -----  -----  -----  --------------------------------
        //  57     71     144    151    Any
        //  77     87     144    151    Any
        //  87     98     151    155    Ljava/lang/InterruptedException;
        //  87     98     144    151    Any
        //  102    116    144    151    Any
        //  132    144    144    151    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0087:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2596)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
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
    
    public interface ReplyCallback<T>
    {
        void onReply(final T p0);
    }
}
