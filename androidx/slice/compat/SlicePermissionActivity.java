// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.compat;

import android.content.pm.PackageManager$NameNotFoundException;
import android.util.Log;
import androidx.slice.core.R$id;
import android.widget.TextView;
import androidx.slice.core.R$layout;
import androidx.slice.core.R$string;
import androidx.core.text.BidiFormatter;
import android.os.Bundle;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.text.TextUtils$TruncateAt;
import android.text.TextPaint;
import android.text.Html;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface$OnDismissListener;
import android.content.DialogInterface$OnClickListener;
import android.app.Activity;

public class SlicePermissionActivity extends Activity implements DialogInterface$OnClickListener, DialogInterface$OnDismissListener
{
    private String mCallingPkg;
    private AlertDialog mDialog;
    private String mProviderPkg;
    private Uri mUri;
    
    private CharSequence loadSafeLabel(final PackageManager packageManager, final ApplicationInfo applicationInfo) {
        String string = Html.fromHtml(applicationInfo.loadLabel(packageManager).toString()).toString();
        final int length = string.length();
        int endIndex = 0;
        String substring;
        while (true) {
            substring = string;
            if (endIndex >= length) {
                break;
            }
            final int codePoint = string.codePointAt(endIndex);
            final int type = Character.getType(codePoint);
            if (type == 13 || type == 15 || type == 14) {
                substring = string.substring(0, endIndex);
                break;
            }
            String string2 = string;
            if (type == 12) {
                final StringBuilder sb = new StringBuilder();
                sb.append(string.substring(0, endIndex));
                sb.append(" ");
                sb.append(string.substring(Character.charCount(codePoint) + endIndex));
                string2 = sb.toString();
            }
            endIndex += Character.charCount(codePoint);
            string = string2;
        }
        final String trim = substring.trim();
        if (trim.isEmpty()) {
            return applicationInfo.packageName;
        }
        final TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(42.0f);
        return TextUtils.ellipsize((CharSequence)trim, textPaint, 500.0f, TextUtils$TruncateAt.END);
    }
    
    public void onClick(final DialogInterface dialogInterface, final int n) {
        if (n == -1) {
            SliceProviderCompat.grantSlicePermission((Context)this, this.getPackageName(), this.mCallingPkg, this.mUri.buildUpon().path("").build());
        }
        this.finish();
    }
    
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.mUri = (Uri)this.getIntent().getParcelableExtra("slice_uri");
        this.mCallingPkg = this.getIntent().getStringExtra("pkg");
        this.mProviderPkg = this.getIntent().getStringExtra("provider_pkg");
        try {
            final PackageManager packageManager = this.getPackageManager();
            final String unicodeWrap = BidiFormatter.getInstance().unicodeWrap(this.loadSafeLabel(packageManager, packageManager.getApplicationInfo(this.mCallingPkg, 0)).toString());
            final String unicodeWrap2 = BidiFormatter.getInstance().unicodeWrap(this.loadSafeLabel(packageManager, packageManager.getApplicationInfo(this.mProviderPkg, 0)).toString());
            final AlertDialog.Builder builder = new AlertDialog.Builder((Context)this);
            builder.setTitle(this.getString(R$string.abc_slice_permission_title, new Object[] { unicodeWrap, unicodeWrap2 }));
            builder.setView(R$layout.abc_slice_permission_request);
            builder.setNegativeButton(R$string.abc_slice_permission_deny, (DialogInterface$OnClickListener)this);
            builder.setPositiveButton(R$string.abc_slice_permission_allow, (DialogInterface$OnClickListener)this);
            builder.setOnDismissListener((DialogInterface$OnDismissListener)this);
            final AlertDialog show = builder.show();
            this.mDialog = show;
            ((TextView)show.getWindow().getDecorView().findViewById(R$id.text1)).setText((CharSequence)this.getString(R$string.abc_slice_permission_text_1, new Object[] { unicodeWrap2 }));
            ((TextView)this.mDialog.getWindow().getDecorView().findViewById(R$id.text2)).setText((CharSequence)this.getString(R$string.abc_slice_permission_text_2, new Object[] { unicodeWrap2 }));
        }
        catch (PackageManager$NameNotFoundException ex) {
            Log.e("SlicePermissionActivity", "Couldn't find package", (Throwable)ex);
            this.finish();
        }
    }
    
    public void onDestroy() {
        super.onDestroy();
        final AlertDialog mDialog = this.mDialog;
        if (mDialog != null && mDialog.isShowing()) {
            this.mDialog.cancel();
        }
    }
    
    public void onDismiss(final DialogInterface dialogInterface) {
        this.finish();
    }
}
