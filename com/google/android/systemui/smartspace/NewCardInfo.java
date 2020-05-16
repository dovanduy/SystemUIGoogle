// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.smartspace;

import java.io.OutputStream;
import android.graphics.Bitmap$CompressFormat;
import java.io.ByteArrayOutputStream;
import com.android.systemui.smartspace.nano.SmartspaceProto$CardWrapper;
import android.util.Log;
import android.provider.MediaStore$Images$Media;
import android.net.Uri;
import android.text.TextUtils;
import android.content.res.Resources;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.content.Context;
import android.content.Intent$ShortcutIconResource;
import android.content.pm.PackageInfo;
import android.content.Intent;
import com.android.systemui.smartspace.nano.SmartspaceProto$SmartspaceUpdate;

public class NewCardInfo
{
    private final SmartspaceProto$SmartspaceUpdate.SmartspaceCard mCard;
    private final Intent mIntent;
    private final boolean mIsPrimary;
    private final PackageInfo mPackageInfo;
    private final long mPublishTime;
    
    public NewCardInfo(final SmartspaceProto$SmartspaceUpdate.SmartspaceCard mCard, final Intent mIntent, final boolean mIsPrimary, final long mPublishTime, final PackageInfo mPackageInfo) {
        this.mCard = mCard;
        this.mIsPrimary = mIsPrimary;
        this.mIntent = mIntent;
        this.mPublishTime = mPublishTime;
        this.mPackageInfo = mPackageInfo;
    }
    
    static Bitmap createIconBitmap(final Intent$ShortcutIconResource intent$ShortcutIconResource, final Context context) {
        final PackageManager packageManager = context.getPackageManager();
        try {
            final Resources resourcesForApplication = packageManager.getResourcesForApplication(intent$ShortcutIconResource.packageName);
            if (resourcesForApplication != null) {
                return BitmapFactory.decodeResource(resourcesForApplication, resourcesForApplication.getIdentifier(intent$ShortcutIconResource.resourceName, (String)null, (String)null));
            }
            return null;
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    private static <T> T retrieveFromIntent(final String s, final Intent intent) {
        if (!TextUtils.isEmpty((CharSequence)s)) {
            return (T)intent.getParcelableExtra(s);
        }
        return null;
    }
    
    public int getUserId() {
        return this.mIntent.getIntExtra("uid", -1);
    }
    
    public boolean isPrimary() {
        return this.mIsPrimary;
    }
    
    public Bitmap retrieveIcon(final Context context) {
        final SmartspaceProto$SmartspaceUpdate.SmartspaceCard.Image icon = this.mCard.icon;
        if (icon == null) {
            return null;
        }
        final Bitmap bitmap = retrieveFromIntent(icon.key, this.mIntent);
        if (bitmap != null) {
            return bitmap;
        }
        try {
            if (!TextUtils.isEmpty((CharSequence)icon.uri)) {
                return MediaStore$Images$Media.getBitmap(context.getContentResolver(), Uri.parse(icon.uri));
            }
            if (!TextUtils.isEmpty((CharSequence)icon.gsaResourceName)) {
                final Intent$ShortcutIconResource intent$ShortcutIconResource = new Intent$ShortcutIconResource();
                intent$ShortcutIconResource.packageName = "com.google.android.googlequicksearchbox";
                intent$ShortcutIconResource.resourceName = icon.gsaResourceName;
                return createIconBitmap(intent$ShortcutIconResource, context);
            }
        }
        catch (Exception ex) {
            final StringBuilder sb = new StringBuilder();
            sb.append("retrieving bitmap uri=");
            sb.append(icon.uri);
            sb.append(" gsaRes=");
            sb.append(icon.gsaResourceName);
            Log.e("NewCardInfo", sb.toString());
        }
        return null;
    }
    
    public boolean shouldDiscard() {
        final SmartspaceProto$SmartspaceUpdate.SmartspaceCard mCard = this.mCard;
        return mCard == null || mCard.shouldDiscard;
    }
    
    public SmartspaceProto$CardWrapper toWrapper(final Context context) {
        final SmartspaceProto$CardWrapper smartspaceProto$CardWrapper = new SmartspaceProto$CardWrapper();
        final Bitmap retrieveIcon = this.retrieveIcon(context);
        if (retrieveIcon != null) {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            retrieveIcon.compress(Bitmap$CompressFormat.PNG, 100, (OutputStream)byteArrayOutputStream);
            smartspaceProto$CardWrapper.icon = byteArrayOutputStream.toByteArray();
        }
        smartspaceProto$CardWrapper.card = this.mCard;
        smartspaceProto$CardWrapper.publishTime = this.mPublishTime;
        final PackageInfo mPackageInfo = this.mPackageInfo;
        if (mPackageInfo != null) {
            smartspaceProto$CardWrapper.gsaVersionCode = mPackageInfo.versionCode;
            smartspaceProto$CardWrapper.gsaUpdateTime = mPackageInfo.lastUpdateTime;
        }
        return smartspaceProto$CardWrapper;
    }
}
