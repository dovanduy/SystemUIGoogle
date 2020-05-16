// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager$NameNotFoundException;
import android.util.Log;
import android.widget.TextView;
import android.content.Context;
import android.app.AlertDialog$Builder;
import android.text.BidiFormatter;
import android.os.Bundle;
import android.app.slice.SliceManager;
import android.content.DialogInterface;
import android.net.Uri;
import android.widget.CheckBox;
import android.content.DialogInterface$OnDismissListener;
import android.content.DialogInterface$OnClickListener;
import android.app.Activity;

public class SlicePermissionActivity extends Activity implements DialogInterface$OnClickListener, DialogInterface$OnDismissListener
{
    private CheckBox mAllCheckbox;
    private String mCallingPkg;
    private String mProviderPkg;
    private Uri mUri;
    
    public void onClick(final DialogInterface dialogInterface, final int n) {
        if (n == -1) {
            ((SliceManager)this.getSystemService((Class)SliceManager.class)).grantPermissionFromUser(this.mUri, this.mCallingPkg, this.mAllCheckbox.isChecked());
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
            final String unicodeWrap = BidiFormatter.getInstance().unicodeWrap(packageManager.getApplicationInfo(this.mCallingPkg, 0).loadSafeLabel(packageManager, 500.0f, 5).toString());
            final String unicodeWrap2 = BidiFormatter.getInstance().unicodeWrap(packageManager.getApplicationInfo(this.mProviderPkg, 0).loadSafeLabel(packageManager, 500.0f, 5).toString());
            final AlertDialog create = new AlertDialog$Builder((Context)this).setTitle((CharSequence)this.getString(R$string.slice_permission_title, new Object[] { unicodeWrap, unicodeWrap2 })).setView(R$layout.slice_permission_request).setNegativeButton(R$string.slice_permission_deny, (DialogInterface$OnClickListener)this).setPositiveButton(R$string.slice_permission_allow, (DialogInterface$OnClickListener)this).setOnDismissListener((DialogInterface$OnDismissListener)this).create();
            create.getWindow().addSystemFlags(524288);
            create.show();
            ((TextView)create.getWindow().getDecorView().findViewById(R$id.text1)).setText((CharSequence)this.getString(R$string.slice_permission_text_1, new Object[] { unicodeWrap2 }));
            ((TextView)create.getWindow().getDecorView().findViewById(R$id.text2)).setText((CharSequence)this.getString(R$string.slice_permission_text_2, new Object[] { unicodeWrap2 }));
            (this.mAllCheckbox = (CheckBox)create.getWindow().getDecorView().findViewById(R$id.slice_permission_checkbox)).setText((CharSequence)this.getString(R$string.slice_permission_checkbox, new Object[] { unicodeWrap }));
        }
        catch (PackageManager$NameNotFoundException ex) {
            Log.e("SlicePermissionActivity", "Couldn't find package", (Throwable)ex);
            this.finish();
        }
    }
    
    public void onDismiss(final DialogInterface dialogInterface) {
        this.finish();
    }
}
