// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.recents.model;

import android.graphics.Rect;
import android.app.ActivityManager$TaskSnapshot;
import android.graphics.Bitmap;

public class ThumbnailData
{
    public final Bitmap thumbnail;
    
    public ThumbnailData(final ActivityManager$TaskSnapshot activityManager$TaskSnapshot) {
        this.thumbnail = Bitmap.wrapHardwareBuffer(activityManager$TaskSnapshot.getSnapshot(), activityManager$TaskSnapshot.getColorSpace());
        new Rect(activityManager$TaskSnapshot.getContentInsets());
        activityManager$TaskSnapshot.getOrientation();
        activityManager$TaskSnapshot.getRotation();
        activityManager$TaskSnapshot.isLowResolution();
        this.thumbnail.getWidth();
        final int x = activityManager$TaskSnapshot.getTaskSize().x;
        activityManager$TaskSnapshot.isRealSnapshot();
        activityManager$TaskSnapshot.isTranslucent();
        activityManager$TaskSnapshot.getWindowingMode();
        activityManager$TaskSnapshot.getSystemUiVisibility();
        activityManager$TaskSnapshot.getId();
    }
}
