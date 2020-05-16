// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.util.Log;
import android.util.FeatureFlagUtils;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.view.ViewGroup;
import android.graphics.drawable.Drawable;
import com.android.settingslib.widget.AdaptiveIcon;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.GradientDrawable;
import android.content.res.ColorStateList;
import java.util.Iterator;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import android.view.ViewParent;
import android.app.Notification;
import com.android.settingslib.media.InfoMediaManager;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.systemui.Dependency;
import android.content.Intent;
import java.util.ArrayList;
import android.view.View;
import java.util.List;
import android.view.View$OnClickListener;
import com.android.settingslib.media.LocalMediaManager;
import com.android.settingslib.media.MediaDevice;
import android.content.Context;
import com.android.systemui.plugins.ActivityStarter;

public class MediaTransferManager
{
    private final ActivityStarter mActivityStarter;
    private final Context mContext;
    private MediaDevice mDevice;
    private LocalMediaManager mLocalMediaManager;
    private final LocalMediaManager.DeviceCallback mMediaDeviceCallback;
    private final View$OnClickListener mOnClickHandler;
    private List<View> mViews;
    
    public MediaTransferManager(final Context mContext) {
        this.mViews = new ArrayList<View>();
        this.mOnClickHandler = (View$OnClickListener)new View$OnClickListener() {
            private boolean handleMediaTransfer(final View view) {
                if (view.findViewById(16909145) == null) {
                    return false;
                }
                MediaTransferManager.this.mActivityStarter.startActivity(new Intent().setAction("com.android.settings.panel.action.MEDIA_OUTPUT").putExtra("com.android.settings.panel.extra.PACKAGE_NAME", MediaTransferManager.this.getRowForParent(view.getParent()).getEntry().getSbn().getPackageName()), false, true, 268468224);
                return true;
            }
            
            public void onClick(final View view) {
                if (this.handleMediaTransfer(view)) {}
            }
        };
        this.mMediaDeviceCallback = new LocalMediaManager.DeviceCallback() {
            @Override
            public void onDeviceListUpdate(final List<MediaDevice> list) {
                final MediaDevice currentConnectedDevice = MediaTransferManager.this.mLocalMediaManager.getCurrentConnectedDevice();
                if (MediaTransferManager.this.mDevice == null || !MediaTransferManager.this.mDevice.equals(currentConnectedDevice)) {
                    MediaTransferManager.this.mDevice = currentConnectedDevice;
                    MediaTransferManager.this.updateAllChips();
                }
            }
            
            @Override
            public void onSelectedDeviceStateChanged(final MediaDevice mediaDevice, final int n) {
                if (MediaTransferManager.this.mDevice == null || !MediaTransferManager.this.mDevice.equals(mediaDevice)) {
                    MediaTransferManager.this.mDevice = mediaDevice;
                    MediaTransferManager.this.updateAllChips();
                }
            }
        };
        this.mContext = mContext;
        this.mActivityStarter = Dependency.get(ActivityStarter.class);
        final LocalBluetoothManager localBluetoothManager = Dependency.get(LocalBluetoothManager.class);
        this.mLocalMediaManager = new LocalMediaManager(this.mContext, localBluetoothManager, new InfoMediaManager(this.mContext, null, null, localBluetoothManager), null);
    }
    
    private ExpandableNotificationRow getRowForParent(ViewParent parent) {
        while (parent != null) {
            if (parent instanceof ExpandableNotificationRow) {
                return (ExpandableNotificationRow)parent;
            }
            parent = parent.getParent();
        }
        return null;
    }
    
    private void updateAllChips() {
        final Iterator<View> iterator = this.mViews.iterator();
        while (iterator.hasNext()) {
            this.updateChip(iterator.next());
        }
    }
    
    private void updateChip(final View view) {
        final ExpandableNotificationRow rowForParent = this.getRowForParent(view.getParent());
        final int originalIconColor = rowForParent.getNotificationHeader().getOriginalIconColor();
        final ColorStateList value = ColorStateList.valueOf(originalIconColor);
        final int currentBackgroundTint = rowForParent.getCurrentBackgroundTint();
        final GradientDrawable gradientDrawable = (GradientDrawable)((RippleDrawable)((LinearLayout)view).getBackground()).getDrawable(0);
        gradientDrawable.setStroke(2, originalIconColor);
        gradientDrawable.setColor(currentBackgroundTint);
        final ImageView imageView = (ImageView)view.findViewById(16909146);
        final TextView textView = (TextView)view.findViewById(16909147);
        textView.setTextColor(value);
        final MediaDevice mDevice = this.mDevice;
        if (mDevice != null) {
            final Drawable icon = mDevice.getIcon();
            imageView.setVisibility(0);
            imageView.setImageTintList(value);
            if (icon instanceof AdaptiveIcon) {
                final AdaptiveIcon imageDrawable = (AdaptiveIcon)icon;
                imageDrawable.setBackgroundColor(currentBackgroundTint);
                imageView.setImageDrawable((Drawable)imageDrawable);
            }
            else {
                imageView.setImageDrawable(icon);
            }
            textView.setText((CharSequence)this.mDevice.getName());
        }
        else {
            imageView.setVisibility(8);
            textView.setText(17040123);
        }
    }
    
    public void applyMediaTransferView(final ViewGroup viewGroup, final NotificationEntry notificationEntry) {
        if (FeatureFlagUtils.isEnabled(this.mContext, "settings_seamless_transfer") && this.mLocalMediaManager != null) {
            if (viewGroup != null) {
                final View viewById = viewGroup.findViewById(16909145);
                if (viewById == null) {
                    return;
                }
                viewById.setVisibility(0);
                viewById.setOnClickListener(this.mOnClickHandler);
                if (!this.mViews.contains(viewById)) {
                    this.mViews.add(viewById);
                    if (this.mViews.size() == 1) {
                        this.mLocalMediaManager.registerCallback(this.mMediaDeviceCallback);
                    }
                }
                this.mLocalMediaManager.startScan();
                this.mDevice = this.mLocalMediaManager.getCurrentConnectedDevice();
                this.updateChip(viewById);
            }
        }
    }
    
    public void setRemoved(final View view) {
        if (FeatureFlagUtils.isEnabled(this.mContext, "settings_seamless_transfer") && this.mLocalMediaManager != null) {
            if (view != null) {
                final View viewById = view.findViewById(16909145);
                if (this.mViews.remove(viewById)) {
                    if (this.mViews.size() == 0) {
                        this.mLocalMediaManager.unregisterCallback(this.mMediaDeviceCallback);
                    }
                }
                else {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Tried to remove unknown view ");
                    sb.append(viewById);
                    Log.e("MediaTransferManager", sb.toString());
                }
            }
        }
    }
}
