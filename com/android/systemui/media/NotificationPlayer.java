// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.media;

import android.media.AudioAttributes$Builder;
import android.os.PowerManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.content.Context;
import android.util.Log;
import android.os.SystemClock;
import android.media.AudioManager$OnAudioFocusChangeListener;
import android.os.PowerManager$WakeLock;
import android.media.MediaPlayer;
import android.os.Looper;
import java.util.LinkedList;
import com.android.internal.annotations.GuardedBy;
import android.media.AudioManager;
import android.media.MediaPlayer$OnErrorListener;
import android.media.MediaPlayer$OnCompletionListener;

public class NotificationPlayer implements MediaPlayer$OnCompletionListener, MediaPlayer$OnErrorListener
{
    @GuardedBy({ "mQueueAudioFocusLock" })
    private AudioManager mAudioManagerWithAudioFocus;
    private final LinkedList<Command> mCmdQueue;
    private final Object mCompletionHandlingLock;
    @GuardedBy({ "mCompletionHandlingLock" })
    private CreationAndCompletionThread mCompletionThread;
    @GuardedBy({ "mCompletionHandlingLock" })
    private Looper mLooper;
    private int mNotificationRampTimeMs;
    @GuardedBy({ "mPlayerLock" })
    private MediaPlayer mPlayer;
    private final Object mPlayerLock;
    private final Object mQueueAudioFocusLock;
    private int mState;
    private String mTag;
    @GuardedBy({ "mCmdQueue" })
    private CmdThread mThread;
    @GuardedBy({ "mCmdQueue" })
    private PowerManager$WakeLock mWakeLock;
    
    public NotificationPlayer(final String mTag) {
        this.mCmdQueue = new LinkedList<Command>();
        this.mCompletionHandlingLock = new Object();
        this.mPlayerLock = new Object();
        this.mQueueAudioFocusLock = new Object();
        this.mNotificationRampTimeMs = 0;
        this.mState = 2;
        if (mTag != null) {
            this.mTag = mTag;
        }
        else {
            this.mTag = "NotificationPlayer";
        }
    }
    
    private void abandonAudioFocusAfterError() {
        synchronized (this.mQueueAudioFocusLock) {
            if (this.mAudioManagerWithAudioFocus != null) {
                this.mAudioManagerWithAudioFocus.abandonAudioFocus((AudioManager$OnAudioFocusChangeListener)null);
                this.mAudioManagerWithAudioFocus = null;
            }
        }
    }
    
    @GuardedBy({ "mCmdQueue" })
    private void acquireWakeLock() {
        final PowerManager$WakeLock mWakeLock = this.mWakeLock;
        if (mWakeLock != null) {
            mWakeLock.acquire();
        }
    }
    
    @GuardedBy({ "mCmdQueue" })
    private void enqueueLocked(final Command e) {
        this.mCmdQueue.add(e);
        if (this.mThread == null) {
            this.acquireWakeLock();
            (this.mThread = new CmdThread()).start();
        }
    }
    
    @GuardedBy({ "mCmdQueue" })
    private void releaseWakeLock() {
        final PowerManager$WakeLock mWakeLock = this.mWakeLock;
        if (mWakeLock != null) {
            mWakeLock.release();
        }
    }
    
    private void startSound(final Command command) {
        try {
            Object o = this.mCompletionHandlingLock;
            synchronized (o) {
                if (this.mLooper != null && this.mLooper.getThread().getState() != Thread.State.TERMINATED) {
                    this.mLooper.quit();
                }
                final CreationAndCompletionThread mCompletionThread = new CreationAndCompletionThread(command);
                synchronized (this.mCompletionThread = mCompletionThread) {
                    this.mCompletionThread.start();
                    this.mCompletionThread.wait();
                    // monitorexit(mCompletionThread)
                    // monitorexit(o)
                    final long lng = SystemClock.uptimeMillis() - command.requestTime;
                    if (lng > 1000L) {
                        o = this.mTag;
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Notification sound delayed by ");
                        sb.append(lng);
                        sb.append("msecs");
                        Log.w((String)o, sb.toString());
                    }
                }
            }
        }
        catch (Exception ex) {
            final String mTag = this.mTag;
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("error loading sound for ");
            sb2.append(command.uri);
            Log.w(mTag, sb2.toString(), (Throwable)ex);
        }
    }
    
    public void onCompletion(final MediaPlayer mediaPlayer) {
        synchronized (this.mQueueAudioFocusLock) {
            if (this.mAudioManagerWithAudioFocus != null) {
                this.mAudioManagerWithAudioFocus.abandonAudioFocus((AudioManager$OnAudioFocusChangeListener)null);
                this.mAudioManagerWithAudioFocus = null;
            }
            // monitorexit(this.mQueueAudioFocusLock)
            Object o = this.mCmdQueue;
            synchronized (this.mQueueAudioFocusLock) {
                synchronized (this.mCompletionHandlingLock) {
                    if (this.mCmdQueue.size() == 0) {
                        if (this.mLooper != null) {
                            this.mLooper.quit();
                        }
                        this.mCompletionThread = null;
                    }
                    // monitorexit(this.mCompletionHandlingLock)
                    // monitorexit(this.mQueueAudioFocusLock)
                    o = this.mPlayerLock;
                    synchronized (this.mQueueAudioFocusLock) {
                        if (mediaPlayer == this.mPlayer) {
                            this.mPlayer = null;
                        }
                        // monitorexit(this.mQueueAudioFocusLock)
                        if (mediaPlayer != null) {
                            mediaPlayer.release();
                        }
                    }
                }
            }
        }
    }
    
    public boolean onError(final MediaPlayer mediaPlayer, final int i, final int j) {
        final String mTag = this.mTag;
        final StringBuilder sb = new StringBuilder();
        sb.append("error ");
        sb.append(i);
        sb.append(" (extra=");
        sb.append(j);
        sb.append(") playing notification");
        Log.e(mTag, sb.toString());
        this.onCompletion(mediaPlayer);
        return true;
    }
    
    public void play(final Context context, final Uri uri, final boolean looping, final AudioAttributes attributes) {
        final Command command = new Command();
        command.requestTime = SystemClock.uptimeMillis();
        command.code = 1;
        command.context = context;
        command.uri = uri;
        command.looping = looping;
        command.attributes = attributes;
        synchronized (this.mCmdQueue) {
            this.enqueueLocked(command);
            this.mState = 1;
        }
    }
    
    public void setUsesWakeLock(final Context context) {
        synchronized (this.mCmdQueue) {
            if (this.mWakeLock == null && this.mThread == null) {
                this.mWakeLock = ((PowerManager)context.getSystemService("power")).newWakeLock(1, this.mTag);
                return;
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("assertion failed mWakeLock=");
            sb.append(this.mWakeLock);
            sb.append(" mThread=");
            sb.append(this.mThread);
            throw new RuntimeException(sb.toString());
        }
    }
    
    public void stop() {
        synchronized (this.mCmdQueue) {
            if (this.mState != 2) {
                final Command command = new Command();
                command.requestTime = SystemClock.uptimeMillis();
                command.code = 2;
                this.enqueueLocked(command);
                this.mState = 2;
            }
        }
    }
    
    private final class CmdThread extends Thread
    {
        CmdThread() {
            final StringBuilder sb = new StringBuilder();
            sb.append("NotificationPlayer-");
            sb.append(NotificationPlayer.this.mTag);
            super(sb.toString());
        }
        
        @Override
        public void run() {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: getfield        com/android/systemui/media/NotificationPlayer$CmdThread.this$0:Lcom/android/systemui/media/NotificationPlayer;
            //     4: invokestatic    com/android/systemui/media/NotificationPlayer.access$800:(Lcom/android/systemui/media/NotificationPlayer;)Ljava/util/LinkedList;
            //     7: astore_1       
            //     8: aload_1        
            //     9: monitorenter   
            //    10: aload_0        
            //    11: getfield        com/android/systemui/media/NotificationPlayer$CmdThread.this$0:Lcom/android/systemui/media/NotificationPlayer;
            //    14: invokestatic    com/android/systemui/media/NotificationPlayer.access$800:(Lcom/android/systemui/media/NotificationPlayer;)Ljava/util/LinkedList;
            //    17: invokevirtual   java/util/LinkedList.removeFirst:()Ljava/lang/Object;
            //    20: checkcast       Lcom/android/systemui/media/NotificationPlayer$Command;
            //    23: astore_2       
            //    24: aload_1        
            //    25: monitorexit    
            //    26: aload_2        
            //    27: getfield        com/android/systemui/media/NotificationPlayer$Command.code:I
            //    30: istore_3       
            //    31: iload_3        
            //    32: iconst_1       
            //    33: if_icmpeq       287
            //    36: iload_3        
            //    37: iconst_2       
            //    38: if_icmpeq       44
            //    41: goto            295
            //    44: aload_0        
            //    45: getfield        com/android/systemui/media/NotificationPlayer$CmdThread.this$0:Lcom/android/systemui/media/NotificationPlayer;
            //    48: invokestatic    com/android/systemui/media/NotificationPlayer.access$600:(Lcom/android/systemui/media/NotificationPlayer;)Ljava/lang/Object;
            //    51: astore          4
            //    53: aload           4
            //    55: monitorenter   
            //    56: aload_0        
            //    57: getfield        com/android/systemui/media/NotificationPlayer$CmdThread.this$0:Lcom/android/systemui/media/NotificationPlayer;
            //    60: invokestatic    com/android/systemui/media/NotificationPlayer.access$700:(Lcom/android/systemui/media/NotificationPlayer;)Landroid/media/MediaPlayer;
            //    63: astore_1       
            //    64: aload_0        
            //    65: getfield        com/android/systemui/media/NotificationPlayer$CmdThread.this$0:Lcom/android/systemui/media/NotificationPlayer;
            //    68: aconst_null    
            //    69: invokestatic    com/android/systemui/media/NotificationPlayer.access$702:(Lcom/android/systemui/media/NotificationPlayer;Landroid/media/MediaPlayer;)Landroid/media/MediaPlayer;
            //    72: pop            
            //    73: aload           4
            //    75: monitorexit    
            //    76: aload_1        
            //    77: ifnull          265
            //    80: invokestatic    android/os/SystemClock.uptimeMillis:()J
            //    83: aload_2        
            //    84: getfield        com/android/systemui/media/NotificationPlayer$Command.requestTime:J
            //    87: lsub           
            //    88: lstore          5
            //    90: lload           5
            //    92: ldc2_w          1000
            //    95: lcmp           
            //    96: ifle            150
            //    99: aload_0        
            //   100: getfield        com/android/systemui/media/NotificationPlayer$CmdThread.this$0:Lcom/android/systemui/media/NotificationPlayer;
            //   103: invokestatic    com/android/systemui/media/NotificationPlayer.access$400:(Lcom/android/systemui/media/NotificationPlayer;)Ljava/lang/String;
            //   106: astore_2       
            //   107: new             Ljava/lang/StringBuilder;
            //   110: dup            
            //   111: invokespecial   java/lang/StringBuilder.<init>:()V
            //   114: astore          4
            //   116: aload           4
            //   118: ldc             "Notification stop delayed by "
            //   120: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   123: pop            
            //   124: aload           4
            //   126: lload           5
            //   128: invokevirtual   java/lang/StringBuilder.append:(J)Ljava/lang/StringBuilder;
            //   131: pop            
            //   132: aload           4
            //   134: ldc             "msecs"
            //   136: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   139: pop            
            //   140: aload_2        
            //   141: aload           4
            //   143: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   146: invokestatic    android/util/Log.w:(Ljava/lang/String;Ljava/lang/String;)I
            //   149: pop            
            //   150: aload_1        
            //   151: invokevirtual   android/media/MediaPlayer.stop:()V
            //   154: aload_1        
            //   155: invokevirtual   android/media/MediaPlayer.release:()V
            //   158: aload_0        
            //   159: getfield        com/android/systemui/media/NotificationPlayer$CmdThread.this$0:Lcom/android/systemui/media/NotificationPlayer;
            //   162: invokestatic    com/android/systemui/media/NotificationPlayer.access$100:(Lcom/android/systemui/media/NotificationPlayer;)Ljava/lang/Object;
            //   165: astore_1       
            //   166: aload_1        
            //   167: monitorenter   
            //   168: aload_0        
            //   169: getfield        com/android/systemui/media/NotificationPlayer$CmdThread.this$0:Lcom/android/systemui/media/NotificationPlayer;
            //   172: invokestatic    com/android/systemui/media/NotificationPlayer.access$200:(Lcom/android/systemui/media/NotificationPlayer;)Landroid/media/AudioManager;
            //   175: ifnull          199
            //   178: aload_0        
            //   179: getfield        com/android/systemui/media/NotificationPlayer$CmdThread.this$0:Lcom/android/systemui/media/NotificationPlayer;
            //   182: invokestatic    com/android/systemui/media/NotificationPlayer.access$200:(Lcom/android/systemui/media/NotificationPlayer;)Landroid/media/AudioManager;
            //   185: aconst_null    
            //   186: invokevirtual   android/media/AudioManager.abandonAudioFocus:(Landroid/media/AudioManager$OnAudioFocusChangeListener;)I
            //   189: pop            
            //   190: aload_0        
            //   191: getfield        com/android/systemui/media/NotificationPlayer$CmdThread.this$0:Lcom/android/systemui/media/NotificationPlayer;
            //   194: aconst_null    
            //   195: invokestatic    com/android/systemui/media/NotificationPlayer.access$202:(Lcom/android/systemui/media/NotificationPlayer;Landroid/media/AudioManager;)Landroid/media/AudioManager;
            //   198: pop            
            //   199: aload_1        
            //   200: monitorexit    
            //   201: aload_0        
            //   202: getfield        com/android/systemui/media/NotificationPlayer$CmdThread.this$0:Lcom/android/systemui/media/NotificationPlayer;
            //   205: invokestatic    com/android/systemui/media/NotificationPlayer.access$1000:(Lcom/android/systemui/media/NotificationPlayer;)Ljava/lang/Object;
            //   208: astore_1       
            //   209: aload_1        
            //   210: monitorenter   
            //   211: aload_0        
            //   212: getfield        com/android/systemui/media/NotificationPlayer$CmdThread.this$0:Lcom/android/systemui/media/NotificationPlayer;
            //   215: invokestatic    com/android/systemui/media/NotificationPlayer.access$000:(Lcom/android/systemui/media/NotificationPlayer;)Landroid/os/Looper;
            //   218: ifnull          250
            //   221: aload_0        
            //   222: getfield        com/android/systemui/media/NotificationPlayer$CmdThread.this$0:Lcom/android/systemui/media/NotificationPlayer;
            //   225: invokestatic    com/android/systemui/media/NotificationPlayer.access$000:(Lcom/android/systemui/media/NotificationPlayer;)Landroid/os/Looper;
            //   228: invokevirtual   android/os/Looper.getThread:()Ljava/lang/Thread;
            //   231: invokevirtual   java/lang/Thread.getState:()Ljava/lang/Thread$State;
            //   234: getstatic       java/lang/Thread$State.TERMINATED:Ljava/lang/Thread$State;
            //   237: if_acmpeq       250
            //   240: aload_0        
            //   241: getfield        com/android/systemui/media/NotificationPlayer$CmdThread.this$0:Lcom/android/systemui/media/NotificationPlayer;
            //   244: invokestatic    com/android/systemui/media/NotificationPlayer.access$000:(Lcom/android/systemui/media/NotificationPlayer;)Landroid/os/Looper;
            //   247: invokevirtual   android/os/Looper.quit:()V
            //   250: aload_1        
            //   251: monitorexit    
            //   252: goto            295
            //   255: astore_2       
            //   256: aload_1        
            //   257: monitorexit    
            //   258: aload_2        
            //   259: athrow         
            //   260: astore_2       
            //   261: aload_1        
            //   262: monitorexit    
            //   263: aload_2        
            //   264: athrow         
            //   265: aload_0        
            //   266: getfield        com/android/systemui/media/NotificationPlayer$CmdThread.this$0:Lcom/android/systemui/media/NotificationPlayer;
            //   269: invokestatic    com/android/systemui/media/NotificationPlayer.access$400:(Lcom/android/systemui/media/NotificationPlayer;)Ljava/lang/String;
            //   272: ldc             "STOP command without a player"
            //   274: invokestatic    android/util/Log.w:(Ljava/lang/String;Ljava/lang/String;)I
            //   277: pop            
            //   278: goto            295
            //   281: astore_1       
            //   282: aload           4
            //   284: monitorexit    
            //   285: aload_1        
            //   286: athrow         
            //   287: aload_0        
            //   288: getfield        com/android/systemui/media/NotificationPlayer$CmdThread.this$0:Lcom/android/systemui/media/NotificationPlayer;
            //   291: aload_2        
            //   292: invokestatic    com/android/systemui/media/NotificationPlayer.access$900:(Lcom/android/systemui/media/NotificationPlayer;Lcom/android/systemui/media/NotificationPlayer$Command;)V
            //   295: aload_0        
            //   296: getfield        com/android/systemui/media/NotificationPlayer$CmdThread.this$0:Lcom/android/systemui/media/NotificationPlayer;
            //   299: invokestatic    com/android/systemui/media/NotificationPlayer.access$800:(Lcom/android/systemui/media/NotificationPlayer;)Ljava/util/LinkedList;
            //   302: astore_1       
            //   303: aload_1        
            //   304: monitorenter   
            //   305: aload_0        
            //   306: getfield        com/android/systemui/media/NotificationPlayer$CmdThread.this$0:Lcom/android/systemui/media/NotificationPlayer;
            //   309: invokestatic    com/android/systemui/media/NotificationPlayer.access$800:(Lcom/android/systemui/media/NotificationPlayer;)Ljava/util/LinkedList;
            //   312: invokevirtual   java/util/LinkedList.size:()I
            //   315: ifne            337
            //   318: aload_0        
            //   319: getfield        com/android/systemui/media/NotificationPlayer$CmdThread.this$0:Lcom/android/systemui/media/NotificationPlayer;
            //   322: aconst_null    
            //   323: invokestatic    com/android/systemui/media/NotificationPlayer.access$1102:(Lcom/android/systemui/media/NotificationPlayer;Lcom/android/systemui/media/NotificationPlayer$CmdThread;)Lcom/android/systemui/media/NotificationPlayer$CmdThread;
            //   326: pop            
            //   327: aload_0        
            //   328: getfield        com/android/systemui/media/NotificationPlayer$CmdThread.this$0:Lcom/android/systemui/media/NotificationPlayer;
            //   331: invokestatic    com/android/systemui/media/NotificationPlayer.access$1200:(Lcom/android/systemui/media/NotificationPlayer;)V
            //   334: aload_1        
            //   335: monitorexit    
            //   336: return         
            //   337: aload_1        
            //   338: monitorexit    
            //   339: goto            0
            //   342: astore_2       
            //   343: aload_1        
            //   344: monitorexit    
            //   345: aload_2        
            //   346: athrow         
            //   347: astore_2       
            //   348: aload_1        
            //   349: monitorexit    
            //   350: aload_2        
            //   351: athrow         
            //   352: astore_2       
            //   353: goto            154
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                 
            //  -----  -----  -----  -----  ---------------------
            //  10     26     347    352    Any
            //  56     76     281    287    Any
            //  150    154    352    356    Ljava/lang/Exception;
            //  168    199    260    265    Any
            //  199    201    260    265    Any
            //  211    250    255    260    Any
            //  250    252    255    260    Any
            //  256    258    255    260    Any
            //  261    263    260    265    Any
            //  282    285    281    287    Any
            //  305    336    342    347    Any
            //  337    339    342    347    Any
            //  343    345    342    347    Any
            //  348    350    347    352    Any
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: Label_0150:
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
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:576)
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
    }
    
    private static final class Command
    {
        AudioAttributes attributes;
        int code;
        Context context;
        boolean looping;
        long requestTime;
        Uri uri;
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("{ code=");
            sb.append(this.code);
            sb.append(" looping=");
            sb.append(this.looping);
            sb.append(" attributes=");
            sb.append(this.attributes);
            sb.append(" uri=");
            sb.append(this.uri);
            sb.append(" }");
            return sb.toString();
        }
    }
    
    private final class CreationAndCompletionThread extends Thread
    {
        public Command mCmd;
        
        public CreationAndCompletionThread(final Command mCmd) {
            this.mCmd = mCmd;
        }
        
        @Override
        public void run() {
            Looper.prepare();
            NotificationPlayer.this.mLooper = Looper.myLooper();
            synchronized (this) {
                final AudioManager audioManager = (AudioManager)this.mCmd.context.getSystemService("audio");
                final MediaPlayer mediaPlayer = null;
                MediaPlayer mediaPlayer2;
                try {
                    mediaPlayer2 = new MediaPlayer();
                    try {
                        if (this.mCmd.attributes == null) {
                            this.mCmd.attributes = new AudioAttributes$Builder().setUsage(5).setContentType(4).build();
                        }
                        mediaPlayer2.setAudioAttributes(this.mCmd.attributes);
                        mediaPlayer2.setDataSource(this.mCmd.context, this.mCmd.uri);
                        mediaPlayer2.setLooping(this.mCmd.looping);
                        mediaPlayer2.setOnCompletionListener((MediaPlayer$OnCompletionListener)NotificationPlayer.this);
                        mediaPlayer2.setOnErrorListener((MediaPlayer$OnErrorListener)NotificationPlayer.this);
                        mediaPlayer2.prepare();
                        if (this.mCmd.uri != null && this.mCmd.uri.getEncodedPath() != null && this.mCmd.uri.getEncodedPath().length() > 0 && !audioManager.isMusicActiveRemotely()) {
                            synchronized (NotificationPlayer.this.mQueueAudioFocusLock) {
                                if (NotificationPlayer.this.mAudioManagerWithAudioFocus == null) {
                                    int n = 3;
                                    if (this.mCmd.looping) {
                                        n = 1;
                                    }
                                    NotificationPlayer.this.mNotificationRampTimeMs = audioManager.getFocusRampTimeMs(n, this.mCmd.attributes);
                                    audioManager.requestAudioFocus((AudioManager$OnAudioFocusChangeListener)null, this.mCmd.attributes, n, 0);
                                    NotificationPlayer.this.mAudioManagerWithAudioFocus = audioManager;
                                }
                            }
                        }
                        try {
                            Thread.sleep(NotificationPlayer.this.mNotificationRampTimeMs);
                        }
                        catch (InterruptedException ex) {
                            Log.e(NotificationPlayer.this.mTag, "Exception while sleeping to sync notification playback with ducking", (Throwable)ex);
                        }
                        mediaPlayer2.start();
                    }
                    catch (Exception ex2) {}
                }
                catch (Exception ex2) {
                    mediaPlayer2 = null;
                }
                if (mediaPlayer2 != null) {
                    mediaPlayer2.release();
                    mediaPlayer2 = mediaPlayer;
                }
                final String access$400 = NotificationPlayer.this.mTag;
                final StringBuilder sb = new StringBuilder();
                sb.append("error loading sound for ");
                sb.append(this.mCmd.uri);
                final Exception ex2;
                Log.w(access$400, sb.toString(), (Throwable)ex2);
                NotificationPlayer.this.abandonAudioFocusAfterError();
                synchronized (NotificationPlayer.this.mPlayerLock) {
                    final MediaPlayer access$401 = NotificationPlayer.this.mPlayer;
                    NotificationPlayer.this.mPlayer = mediaPlayer2;
                    // monitorexit(NotificationPlayer.access$600(this.this$0))
                    if (access$401 != null) {
                        access$401.release();
                    }
                    this.notify();
                    Looper.loop();
                }
            }
        }
    }
}
