// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.util.AttributeSet;
import com.android.systemui.statusbar.phone.KeyguardPreviewContainer;
import android.content.pm.PackageManager$NameNotFoundException;
import android.util.Log;
import android.content.ComponentName;
import android.os.Bundle;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import java.util.List;
import com.android.keyguard.KeyguardUpdateMonitor;
import android.content.Intent;
import com.android.internal.widget.LockPatternUtils;
import android.content.Context;
import com.android.systemui.ActivityIntentHelper;

public class PreviewInflater
{
    private final ActivityIntentHelper mActivityIntentHelper;
    private Context mContext;
    
    public PreviewInflater(final Context mContext, final LockPatternUtils lockPatternUtils, final ActivityIntentHelper mActivityIntentHelper) {
        this.mContext = mContext;
        this.mActivityIntentHelper = mActivityIntentHelper;
    }
    
    private WidgetInfo getWidgetInfo(final Intent intent) {
        final PackageManager packageManager = this.mContext.getPackageManager();
        final List queryIntentActivitiesAsUser = packageManager.queryIntentActivitiesAsUser(intent, 851968, KeyguardUpdateMonitor.getCurrentUser());
        if (queryIntentActivitiesAsUser.size() == 0) {
            return null;
        }
        final ResolveInfo resolveActivityAsUser = packageManager.resolveActivityAsUser(intent, 852096, KeyguardUpdateMonitor.getCurrentUser());
        if (this.mActivityIntentHelper.wouldLaunchResolverActivity(resolveActivityAsUser, queryIntentActivitiesAsUser)) {
            return null;
        }
        if (resolveActivityAsUser != null) {
            final ActivityInfo activityInfo = resolveActivityAsUser.activityInfo;
            if (activityInfo != null) {
                return this.getWidgetInfoFromMetaData(activityInfo.packageName, activityInfo.metaData);
            }
        }
        return null;
    }
    
    private WidgetInfo getWidgetInfoFromMetaData(final String contextPackage, final Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        final int int1 = bundle.getInt("com.android.keyguard.layout");
        if (int1 == 0) {
            return null;
        }
        final WidgetInfo widgetInfo = new WidgetInfo();
        widgetInfo.contextPackage = contextPackage;
        widgetInfo.layoutId = int1;
        return widgetInfo;
    }
    
    private WidgetInfo getWidgetInfoFromService(final ComponentName componentName) {
        final PackageManager packageManager = this.mContext.getPackageManager();
        try {
            return this.getWidgetInfoFromMetaData(componentName.getPackageName(), packageManager.getServiceInfo(componentName, 128).metaData);
        }
        catch (PackageManager$NameNotFoundException ex) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Failed to load preview; ");
            sb.append(componentName.flattenToShortString());
            sb.append(" not found");
            Log.w("PreviewInflater", sb.toString(), (Throwable)ex);
            return null;
        }
    }
    
    private KeyguardPreviewContainer inflatePreview(final WidgetInfo widgetInfo) {
        if (widgetInfo == null) {
            return null;
        }
        final View inflateWidgetView = this.inflateWidgetView(widgetInfo);
        if (inflateWidgetView == null) {
            return null;
        }
        final KeyguardPreviewContainer keyguardPreviewContainer = new KeyguardPreviewContainer(this.mContext, null);
        keyguardPreviewContainer.addView(inflateWidgetView);
        return keyguardPreviewContainer;
    }
    
    private View inflateWidgetView(final WidgetInfo widgetInfo) {
        final View view = null;
        View inflate;
        try {
            final Context packageContext = this.mContext.createPackageContext(widgetInfo.contextPackage, 4);
            inflate = ((LayoutInflater)packageContext.getSystemService("layout_inflater")).cloneInContext(packageContext).inflate(widgetInfo.layoutId, (ViewGroup)null, false);
        }
        catch (PackageManager$NameNotFoundException | RuntimeException ex) {
            final Throwable t;
            Log.w("PreviewInflater", "Error creating widget view", t);
            inflate = view;
        }
        return inflate;
    }
    
    public View inflatePreview(final Intent intent) {
        return (View)this.inflatePreview(this.getWidgetInfo(intent));
    }
    
    public View inflatePreviewFromService(final ComponentName componentName) {
        return (View)this.inflatePreview(this.getWidgetInfoFromService(componentName));
    }
    
    private static class WidgetInfo
    {
        String contextPackage;
        int layoutId;
    }
}
