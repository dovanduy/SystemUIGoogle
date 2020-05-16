// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.graphics.Xfermode;
import android.graphics.drawable.Drawable$ConstantState;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.content.res.Resources;
import android.graphics.drawable.DrawableWrapper;
import android.app.WallpaperColors;
import android.os.ParcelFileDescriptor;
import libcore.io.IoUtils;
import android.graphics.Rect;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap$Config;
import android.graphics.BitmapFactory$Options;
import java.io.Writer;
import com.android.internal.util.IndentingPrintWriter;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import android.app.IWallpaperManagerCallback;
import android.app.ActivityManager;
import com.android.systemui.dump.DumpManager;
import android.app.IWallpaperManager;
import android.app.WallpaperManager;
import com.android.keyguard.KeyguardUpdateMonitor;
import android.os.UserHandle;
import com.android.systemui.statusbar.NotificationMediaManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.graphics.Bitmap;
import com.android.systemui.Dumpable;
import android.app.IWallpaperManagerCallback$Stub;

public class LockscreenWallpaper extends IWallpaperManagerCallback$Stub implements Runnable, Dumpable
{
    private Bitmap mCache;
    private boolean mCached;
    private int mCurrentUserId;
    private final Handler mH;
    private AsyncTask<Void, Void, LoaderResult> mLoader;
    private final NotificationMediaManager mMediaManager;
    private UserHandle mSelectedUser;
    private final KeyguardUpdateMonitor mUpdateMonitor;
    private final WallpaperManager mWallpaperManager;
    
    public LockscreenWallpaper(final WallpaperManager mWallpaperManager, final IWallpaperManager wallpaperManager, final KeyguardUpdateMonitor mUpdateMonitor, final DumpManager dumpManager, final NotificationMediaManager mMediaManager, final Handler mh) {
        dumpManager.registerDumpable(LockscreenWallpaper.class.getSimpleName(), this);
        this.mWallpaperManager = mWallpaperManager;
        this.mCurrentUserId = ActivityManager.getCurrentUser();
        this.mUpdateMonitor = mUpdateMonitor;
        this.mMediaManager = mMediaManager;
        this.mH = mh;
        if (wallpaperManager != null) {
            try {
                wallpaperManager.setLockWallpaperCallback((IWallpaperManagerCallback)this);
            }
            catch (RemoteException obj) {
                final StringBuilder sb = new StringBuilder();
                sb.append("System dead?");
                sb.append(obj);
                Log.e("LockscreenWallpaper", sb.toString());
            }
        }
    }
    
    private void postUpdateWallpaper() {
        final Handler mh = this.mH;
        if (mh == null) {
            Log.wtfStack("LockscreenWallpaper", "Trying to use LockscreenWallpaper before initialization.");
            return;
        }
        mh.removeCallbacks((Runnable)this);
        this.mH.post((Runnable)this);
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        final StringBuilder sb = new StringBuilder();
        sb.append(LockscreenWallpaper.class.getSimpleName());
        sb.append(":");
        printWriter.println(sb.toString());
        final IndentingPrintWriter increaseIndent = new IndentingPrintWriter((Writer)printWriter, "  ").increaseIndent();
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("mCached=");
        sb2.append(this.mCached);
        increaseIndent.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("mCache=");
        sb3.append(this.mCache);
        increaseIndent.println(sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("mCurrentUserId=");
        sb4.append(this.mCurrentUserId);
        increaseIndent.println(sb4.toString());
        final StringBuilder sb5 = new StringBuilder();
        sb5.append("mSelectedUser=");
        sb5.append(this.mSelectedUser);
        increaseIndent.println(sb5.toString());
    }
    
    public Bitmap getBitmap() {
        if (this.mCached) {
            return this.mCache;
        }
        final boolean wallpaperSupported = this.mWallpaperManager.isWallpaperSupported();
        boolean hasLockscreenWallpaper = true;
        if (!wallpaperSupported) {
            this.mCached = true;
            return this.mCache = null;
        }
        final LoaderResult loadBitmap = this.loadBitmap(this.mCurrentUserId, this.mSelectedUser);
        if (loadBitmap.success) {
            this.mCached = true;
            final KeyguardUpdateMonitor mUpdateMonitor = this.mUpdateMonitor;
            if (loadBitmap.bitmap == null) {
                hasLockscreenWallpaper = false;
            }
            mUpdateMonitor.setHasLockscreenWallpaper(hasLockscreenWallpaper);
            this.mCache = loadBitmap.bitmap;
        }
        return this.mCache;
    }
    
    public LoaderResult loadBitmap(int identifier, final UserHandle userHandle) {
        if (!this.mWallpaperManager.isWallpaperSupported()) {
            return LoaderResult.success(null);
        }
        if (userHandle != null) {
            identifier = userHandle.getIdentifier();
        }
        final ParcelFileDescriptor wallpaperFile = this.mWallpaperManager.getWallpaperFile(2, identifier);
        if (wallpaperFile != null) {
            try {
                try {
                    final BitmapFactory$Options bitmapFactory$Options = new BitmapFactory$Options();
                    bitmapFactory$Options.inPreferredConfig = Bitmap$Config.HARDWARE;
                    final LoaderResult success = LoaderResult.success(BitmapFactory.decodeFileDescriptor(wallpaperFile.getFileDescriptor(), (Rect)null, bitmapFactory$Options));
                    IoUtils.closeQuietly((AutoCloseable)wallpaperFile);
                    return success;
                }
                finally {}
            }
            catch (OutOfMemoryError outOfMemoryError) {
                Log.w("LockscreenWallpaper", "Can't decode file", (Throwable)outOfMemoryError);
                final LoaderResult fail = LoaderResult.fail();
                IoUtils.closeQuietly((AutoCloseable)wallpaperFile);
                return fail;
            }
            IoUtils.closeQuietly((AutoCloseable)wallpaperFile);
        }
        else {
            if (userHandle != null) {
                return LoaderResult.success(this.mWallpaperManager.getBitmapAsUser(userHandle.getIdentifier(), true));
            }
            return LoaderResult.success(null);
        }
    }
    
    public void onWallpaperChanged() {
        this.postUpdateWallpaper();
    }
    
    public void onWallpaperColorsChanged(final WallpaperColors wallpaperColors, final int n, final int n2) {
    }
    
    public void run() {
        final AsyncTask<Void, Void, LoaderResult> mLoader = this.mLoader;
        if (mLoader != null) {
            mLoader.cancel(false);
        }
        this.mLoader = (AsyncTask<Void, Void, LoaderResult>)new AsyncTask<Void, Void, LoaderResult>() {
            final /* synthetic */ int val$currentUser = LockscreenWallpaper.this.mCurrentUserId;
            final /* synthetic */ UserHandle val$selectedUser = LockscreenWallpaper.this.mSelectedUser;
            
            protected LoaderResult doInBackground(final Void... array) {
                return LockscreenWallpaper.this.loadBitmap(this.val$currentUser, this.val$selectedUser);
            }
            
            protected void onPostExecute(final LoaderResult loaderResult) {
                super.onPostExecute((Object)loaderResult);
                if (this.isCancelled()) {
                    return;
                }
                if (loaderResult.success) {
                    LockscreenWallpaper.this.mCached = true;
                    LockscreenWallpaper.this.mCache = loaderResult.bitmap;
                    LockscreenWallpaper.this.mUpdateMonitor.setHasLockscreenWallpaper(loaderResult.bitmap != null);
                    LockscreenWallpaper.this.mMediaManager.updateMediaMetaData(true, true);
                }
                LockscreenWallpaper.this.mLoader = null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object[])new Void[0]);
    }
    
    public void setCurrentUser(final int mCurrentUserId) {
        if (mCurrentUserId != this.mCurrentUserId) {
            final UserHandle mSelectedUser = this.mSelectedUser;
            if (mSelectedUser == null || mCurrentUserId != mSelectedUser.getIdentifier()) {
                this.mCached = false;
            }
            this.mCurrentUserId = mCurrentUserId;
        }
    }
    
    private static class LoaderResult
    {
        public final Bitmap bitmap;
        public final boolean success;
        
        LoaderResult(final boolean success, final Bitmap bitmap) {
            this.success = success;
            this.bitmap = bitmap;
        }
        
        static LoaderResult fail() {
            return new LoaderResult(false, null);
        }
        
        static LoaderResult success(final Bitmap bitmap) {
            return new LoaderResult(true, bitmap);
        }
    }
    
    public static class WallpaperDrawable extends DrawableWrapper
    {
        private final ConstantState mState;
        private final Rect mTmpRect;
        
        public WallpaperDrawable(final Resources resources, final Bitmap bitmap) {
            this(resources, new ConstantState(bitmap));
        }
        
        private WallpaperDrawable(final Resources resources, final ConstantState mState) {
            super((Drawable)new BitmapDrawable(resources, mState.mBackground));
            this.mTmpRect = new Rect();
            this.mState = mState;
        }
        
        public ConstantState getConstantState() {
            return this.mState;
        }
        
        public int getIntrinsicHeight() {
            return -1;
        }
        
        public int getIntrinsicWidth() {
            return -1;
        }
        
        protected void onBoundsChange(final Rect rect) {
            final int width = this.getBounds().width();
            final int height = this.getBounds().height();
            final int width2 = this.mState.mBackground.getWidth();
            final int height2 = this.mState.mBackground.getHeight();
            float n;
            float n2;
            if (width2 * height > width * height2) {
                n = (float)height;
                n2 = (float)height2;
            }
            else {
                n = (float)width;
                n2 = (float)width2;
            }
            float n3;
            if ((n3 = n / n2) <= 1.0f) {
                n3 = 1.0f;
            }
            final float n4 = (float)height;
            final float n5 = height2 * n3;
            final float a = (n4 - n5) * 0.5f;
            this.mTmpRect.set(rect.left, rect.top + Math.round(a), rect.left + Math.round(width2 * n3), rect.top + Math.round(n5 + a));
            super.onBoundsChange(this.mTmpRect);
        }
        
        public void setXfermode(final Xfermode xfermode) {
            this.getDrawable().setXfermode(xfermode);
        }
        
        static class ConstantState extends Drawable$ConstantState
        {
            private final Bitmap mBackground;
            
            ConstantState(final Bitmap mBackground) {
                this.mBackground = mBackground;
            }
            
            public int getChangingConfigurations() {
                return 0;
            }
            
            public Drawable newDrawable() {
                return this.newDrawable(null);
            }
            
            public Drawable newDrawable(final Resources resources) {
                return (Drawable)new WallpaperDrawable(resources, this);
            }
        }
    }
}
