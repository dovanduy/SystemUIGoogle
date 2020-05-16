// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.widget.ListAdapter;
import android.content.pm.PackageManager$NameNotFoundException;
import java.util.Collection;
import java.util.Comparator;
import android.content.pm.ApplicationInfo$DisplayNameComparator;
import java.util.ArrayList;
import android.widget.TextView;
import android.widget.ImageView;
import android.content.pm.PackageManager;
import android.util.IconDrawableFactory;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import android.widget.AdapterView;
import com.android.internal.app.AlertController$AlertParams;
import android.util.Log;
import android.view.ViewGroup;
import android.content.Context;
import android.os.Bundle;
import android.net.Uri;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.DialogInterface;
import com.android.internal.logging.MetricsLogger;
import android.view.LayoutInflater;
import com.android.internal.app.AlertController$AlertParams$OnPrepareListViewListener;
import android.content.DialogInterface$OnClickListener;
import android.widget.AdapterView$OnItemSelectedListener;
import com.android.internal.app.AlertActivity;

public final class ForegroundServicesDialog extends AlertActivity implements AdapterView$OnItemSelectedListener, DialogInterface$OnClickListener, AlertController$AlertParams$OnPrepareListViewListener
{
    private PackageItemAdapter mAdapter;
    private DialogInterface$OnClickListener mAppClickListener;
    LayoutInflater mInflater;
    private MetricsLogger mMetricsLogger;
    private String[] mPackages;
    
    ForegroundServicesDialog() {
        this.mAppClickListener = (DialogInterface$OnClickListener)new DialogInterface$OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                final String packageName = ((ApplicationInfo)ForegroundServicesDialog.this.mAdapter.getItem(n)).packageName;
                final Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", packageName, (String)null));
                ForegroundServicesDialog.this.startActivity(intent);
                ForegroundServicesDialog.this.finish();
            }
        };
    }
    
    public void onClick(final DialogInterface dialogInterface, final int n) {
        this.finish();
    }
    
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.mMetricsLogger = Dependency.get(MetricsLogger.class);
        this.mInflater = LayoutInflater.from((Context)this);
        final PackageItemAdapter packageItemAdapter = new PackageItemAdapter((Context)this);
        this.mAdapter = packageItemAdapter;
        final AlertController$AlertParams mAlertParams = super.mAlertParams;
        mAlertParams.mAdapter = (ListAdapter)packageItemAdapter;
        mAlertParams.mOnClickListener = this.mAppClickListener;
        mAlertParams.mCustomTitleView = this.mInflater.inflate(R$layout.foreground_service_title, (ViewGroup)null);
        mAlertParams.mIsSingleChoice = true;
        mAlertParams.mOnItemSelectedListener = (AdapterView$OnItemSelectedListener)this;
        mAlertParams.mPositiveButtonText = this.getString(17040063);
        mAlertParams.mPositiveButtonListener = (DialogInterface$OnClickListener)this;
        ((ForegroundServicesDialog)(mAlertParams.mOnPrepareListViewListener = (AlertController$AlertParams$OnPrepareListViewListener)this)).updateApps(this.getIntent());
        if (this.mPackages == null) {
            Log.w("ForegroundServicesDialog", "No packages supplied");
            this.finish();
            return;
        }
        this.setupAlert();
    }
    
    public void onItemSelected(final AdapterView adapterView, final View view, final int n, final long n2) {
    }
    
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        this.updateApps(intent);
    }
    
    public void onNothingSelected(final AdapterView adapterView) {
    }
    
    protected void onPause() {
        super.onPause();
        this.mMetricsLogger.hidden(944);
    }
    
    public void onPrepareListView(final ListView listView) {
    }
    
    protected void onResume() {
        super.onResume();
        this.mMetricsLogger.visible(944);
    }
    
    protected void onStop() {
        super.onStop();
        if (!this.isChangingConfigurations()) {
            this.finish();
        }
    }
    
    void updateApps(final Intent intent) {
        final String[] stringArrayExtra = intent.getStringArrayExtra("packages");
        this.mPackages = stringArrayExtra;
        if (stringArrayExtra != null) {
            this.mAdapter.setPackages(stringArrayExtra);
        }
    }
    
    private static class PackageItemAdapter extends ArrayAdapter<ApplicationInfo>
    {
        final IconDrawableFactory mIconDrawableFactory;
        final LayoutInflater mInflater;
        final PackageManager mPm;
        
        public PackageItemAdapter(final Context context) {
            super(context, R$layout.foreground_service_item);
            this.mPm = context.getPackageManager();
            this.mInflater = LayoutInflater.from(context);
            this.mIconDrawableFactory = IconDrawableFactory.newInstance(context, true);
        }
        
        public View getView(final int n, final View view, final ViewGroup viewGroup) {
            View inflate = view;
            if (view == null) {
                inflate = this.mInflater.inflate(R$layout.foreground_service_item, viewGroup, false);
            }
            ((ImageView)inflate.findViewById(R$id.app_icon)).setImageDrawable(this.mIconDrawableFactory.getBadgedIcon((ApplicationInfo)this.getItem(n)));
            ((TextView)inflate.findViewById(R$id.app_name)).setText(((ApplicationInfo)this.getItem(n)).loadLabel(this.mPm));
            return inflate;
        }
        
        public void setPackages(final String[] array) {
            this.clear();
            final ArrayList<ApplicationInfo> list = new ArrayList<ApplicationInfo>();
            int n = 0;
        Label_0037_Outer:
            while (true) {
                Label_0043: {
                    if (n >= array.length) {
                        break Label_0043;
                    }
                    while (true) {
                        try {
                            list.add(this.mPm.getApplicationInfo(array[n], 4202496));
                            ++n;
                            continue Label_0037_Outer;
                            list.sort((Comparator<? super ApplicationInfo>)new ApplicationInfo$DisplayNameComparator(this.mPm));
                            this.addAll((Collection)list);
                        }
                        catch (PackageManager$NameNotFoundException ex) {
                            continue;
                        }
                        break;
                    }
                }
            }
        }
    }
}
