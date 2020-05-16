// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.screenshot;

import android.os.IBinder;
import android.content.Intent;
import android.graphics.Insets;
import android.graphics.Rect;
import android.graphics.Bitmap;
import android.util.Log;
import java.util.function.Consumer;
import android.os.RemoteException;
import android.os.Message;
import android.net.Uri;
import android.os.Messenger;
import android.os.Looper;
import android.os.UserManager;
import android.os.Handler;
import android.app.Service;

public class TakeScreenshotService extends Service
{
    private Handler mHandler;
    private final GlobalScreenshot mScreenshot;
    private final GlobalScreenshotLegacy mScreenshotLegacy;
    private final UserManager mUserManager;
    
    public TakeScreenshotService(final GlobalScreenshot mScreenshot, final GlobalScreenshotLegacy mScreenshotLegacy, final UserManager mUserManager) {
        this.mHandler = new Handler(Looper.myLooper()) {
            public void handleMessage(final Message message) {
                final _$$Lambda$TakeScreenshotService$1$EY2hnVM8TKV01VPpEfsX4eJxa2g $$Lambda$TakeScreenshotService$1$EY2hnVM8TKV01VPpEfsX4eJxa2g = new _$$Lambda$TakeScreenshotService$1$EY2hnVM8TKV01VPpEfsX4eJxa2g(message.replyTo);
                if (!TakeScreenshotService.this.mUserManager.isUserUnlocked()) {
                    Log.w("TakeScreenshotService", "Skipping screenshot because storage is locked!");
                    this.post((Runnable)new _$$Lambda$TakeScreenshotService$1$UvW6LdPRBGkaRTqnAoCLKZSfJwE($$Lambda$TakeScreenshotService$1$EY2hnVM8TKV01VPpEfsX4eJxa2g));
                    return;
                }
                final int what = message.what;
                if (what != 1) {
                    if (what != 2) {
                        if (what != 3) {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("Invalid screenshot option: ");
                            sb.append(message.what);
                            Log.d("TakeScreenshotService", sb.toString());
                        }
                        else {
                            TakeScreenshotService.this.mScreenshot.handleImageAsScreenshot((Bitmap)message.getData().getParcelable("screenshot_screen_bitmap"), (Rect)message.getData().getParcelable("screenshot_screen_bounds"), (Insets)message.getData().getParcelable("screenshot_insets"), message.getData().getInt("screenshot_task_id"), $$Lambda$TakeScreenshotService$1$EY2hnVM8TKV01VPpEfsX4eJxa2g);
                        }
                    }
                    else {
                        TakeScreenshotService.this.mScreenshot.takeScreenshotPartial($$Lambda$TakeScreenshotService$1$EY2hnVM8TKV01VPpEfsX4eJxa2g);
                    }
                }
                else {
                    TakeScreenshotService.this.mScreenshot.takeScreenshot($$Lambda$TakeScreenshotService$1$EY2hnVM8TKV01VPpEfsX4eJxa2g);
                }
            }
        };
        this.mScreenshot = mScreenshot;
        this.mScreenshotLegacy = mScreenshotLegacy;
        this.mUserManager = mUserManager;
    }
    
    public IBinder onBind(final Intent intent) {
        return new Messenger(this.mHandler).getBinder();
    }
    
    public boolean onUnbind(final Intent intent) {
        final GlobalScreenshot mScreenshot = this.mScreenshot;
        if (mScreenshot != null) {
            mScreenshot.stopScreenshot();
        }
        final GlobalScreenshotLegacy mScreenshotLegacy = this.mScreenshotLegacy;
        if (mScreenshotLegacy != null) {
            mScreenshotLegacy.stopScreenshot();
        }
        return true;
    }
}
