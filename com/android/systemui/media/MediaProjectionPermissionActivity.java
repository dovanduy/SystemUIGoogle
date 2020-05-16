// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.media;

import android.view.Window;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager$NameNotFoundException;
import android.util.Log;
import android.app.AlertDialog$Builder;
import com.android.systemui.R$id;
import android.widget.TextView;
import android.view.ViewGroup;
import android.content.Context;
import android.view.View;
import com.android.systemui.R$layout;
import android.text.style.StyleSpan;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextUtils$TruncateAt;
import android.text.BidiFormatter;
import com.android.systemui.R$string;
import com.android.systemui.util.Utils;
import android.text.TextPaint;
import android.media.projection.IMediaProjectionManager$Stub;
import android.os.ServiceManager;
import android.os.Bundle;
import android.content.DialogInterface;
import android.os.RemoteException;
import android.media.projection.IMediaProjection;
import android.content.Intent;
import android.media.projection.IMediaProjectionManager;
import android.app.AlertDialog;
import android.content.DialogInterface$OnCancelListener;
import android.content.DialogInterface$OnClickListener;
import android.app.Activity;

public class MediaProjectionPermissionActivity extends Activity implements DialogInterface$OnClickListener, DialogInterface$OnCancelListener
{
    private AlertDialog mDialog;
    private String mPackageName;
    private IMediaProjectionManager mService;
    private int mUid;
    
    private Intent getMediaProjectionIntent(final int n, final String s) throws RemoteException {
        final IMediaProjection projection = this.mService.createProjection(n, s, 0, false);
        final Intent intent = new Intent();
        intent.putExtra("android.media.projection.extra.EXTRA_MEDIA_PROJECTION", projection.asBinder());
        return intent;
    }
    
    public void onCancel(final DialogInterface dialogInterface) {
        this.finish();
    }
    
    public void onClick(final DialogInterface dialogInterface, final int n) {
        if (n == -1) {
            try {
                try {
                    this.setResult(-1, this.getMediaProjectionIntent(this.mUid, this.mPackageName));
                }
                finally {
                    final AlertDialog mDialog = this.mDialog;
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                    this.finish();
                }
            }
            catch (RemoteException ex) {}
        }
        final AlertDialog mDialog2 = this.mDialog;
        Label_0088: {
            if (mDialog2 == null) {
                break Label_0088;
            }
            mDialog2.dismiss();
        }
        this.finish();
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.mPackageName = this.getCallingPackage();
        this.mService = IMediaProjectionManager$Stub.asInterface(ServiceManager.getService("media_projection"));
        if (this.mPackageName == null) {
            this.finish();
            return;
        }
        final PackageManager packageManager = this.getPackageManager();
        try {
            final ApplicationInfo applicationInfo = packageManager.getApplicationInfo(this.mPackageName, 0);
            final int uid = applicationInfo.uid;
            this.mUid = uid;
            try {
                if (this.mService.hasProjectionPermission(uid, this.mPackageName)) {
                    this.setResult(-1, this.getMediaProjectionIntent(this.mUid, this.mPackageName));
                    this.finish();
                    return;
                }
                final TextPaint textPaint = new TextPaint();
                textPaint.setTextSize(42.0f);
                Object string;
                String text;
                if (Utils.isHeadlessRemoteDisplayProvider(packageManager, this.mPackageName)) {
                    string = this.getString(R$string.media_projection_dialog_service_text);
                    text = this.getString(R$string.media_projection_dialog_service_title);
                }
                else {
                    final String string2 = applicationInfo.loadLabel(packageManager).toString();
                    final int length = string2.length();
                    int n = 0;
                    String string3;
                    while (true) {
                        string3 = string2;
                        if (n >= length) {
                            break;
                        }
                        final int codePoint = string2.codePointAt(n);
                        final int type = Character.getType(codePoint);
                        if (type == 13 || type == 15 || type == 14) {
                            final StringBuilder sb = new StringBuilder();
                            sb.append(string2.substring(0, n));
                            sb.append("\u2026");
                            string3 = sb.toString();
                            break;
                        }
                        n += Character.charCount(codePoint);
                    }
                    String mPackageName = string3;
                    if (string3.isEmpty()) {
                        mPackageName = this.mPackageName;
                    }
                    final String unicodeWrap = BidiFormatter.getInstance().unicodeWrap(TextUtils.ellipsize((CharSequence)mPackageName, textPaint, 500.0f, TextUtils$TruncateAt.END).toString());
                    final String string4 = this.getString(R$string.media_projection_dialog_text, new Object[] { unicodeWrap });
                    string = new SpannableString((CharSequence)string4);
                    final int index = string4.indexOf(unicodeWrap);
                    if (index >= 0) {
                        ((SpannableString)string).setSpan((Object)new StyleSpan(1), index, unicodeWrap.length() + index, 0);
                    }
                    text = this.getString(R$string.media_projection_dialog_title, new Object[] { unicodeWrap });
                }
                final View inflate = View.inflate((Context)this, R$layout.media_projection_dialog_title, (ViewGroup)null);
                ((TextView)inflate.findViewById(R$id.dialog_title)).setText((CharSequence)text);
                (this.mDialog = new AlertDialog$Builder((Context)this).setCustomTitle(inflate).setMessage((CharSequence)string).setPositiveButton(R$string.media_projection_action_text, (DialogInterface$OnClickListener)this).setNegativeButton(17039360, (DialogInterface$OnClickListener)this).setOnCancelListener((DialogInterface$OnCancelListener)this).create()).create();
                this.mDialog.getButton(-1).setFilterTouchesWhenObscured(true);
                final Window window = this.mDialog.getWindow();
                window.setType(2003);
                window.addSystemFlags(524288);
                this.mDialog.show();
            }
            catch (RemoteException ex) {
                Log.e("MediaProjectionPermissionActivity", "Error checking projection permissions", (Throwable)ex);
                this.finish();
            }
        }
        catch (PackageManager$NameNotFoundException ex2) {
            Log.e("MediaProjectionPermissionActivity", "unable to look up package name", (Throwable)ex2);
            this.finish();
        }
    }
    
    protected void onDestroy() {
        super.onDestroy();
        final AlertDialog mDialog = this.mDialog;
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }
}
