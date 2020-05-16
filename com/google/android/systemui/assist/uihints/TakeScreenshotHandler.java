// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import java.util.function.Consumer;
import android.app.PendingIntent$CanceledException;
import android.util.Log;
import android.os.Parcelable;
import android.content.Intent;
import android.net.Uri;
import android.app.PendingIntent;
import android.os.Looper;
import com.android.internal.util.ScreenshotHelper;
import android.os.Handler;
import android.content.Context;

final class TakeScreenshotHandler implements TakeScreenshotListener
{
    private final Context mContext;
    private final Handler mHandler;
    private final ScreenshotHelper mScreenshotHelper;
    
    TakeScreenshotHandler(final Context mContext) {
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mContext = mContext;
        this.mScreenshotHelper = new ScreenshotHelper(mContext);
    }
    
    @Override
    public void onTakeScreenshot(final PendingIntent pendingIntent) {
        this.mScreenshotHelper.takeScreenshot(1, true, true, this.mHandler, (Consumer)new _$$Lambda$TakeScreenshotHandler$pLLNKmv7VNurR42TiXGhVQxXOV0(this, pendingIntent));
    }
}
