// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.internal.logging.MetricsLogger;
import android.view.ViewGroup;
import android.view.View;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import com.android.systemui.qs.QSDetailItems;
import com.android.settingslib.graph.BluetoothDeviceLayerDrawable;
import com.android.systemui.R$drawable;
import android.graphics.drawable.Drawable;
import android.widget.Switch;
import android.text.TextUtils;
import com.android.systemui.plugins.qs.DetailAdapter;
import android.bluetooth.BluetoothClass;
import java.util.List;
import com.android.settingslib.Utils;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.systemui.R$plurals;
import com.android.systemui.R$string;
import android.content.Context;
import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.BluetoothController;
import com.android.systemui.plugins.ActivityStarter;
import android.content.Intent;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;

public class BluetoothTile extends QSTileImpl<BooleanState>
{
    private static final Intent BLUETOOTH_SETTINGS;
    private final ActivityStarter mActivityStarter;
    private final BluetoothController.Callback mCallback;
    private final BluetoothController mController;
    private final BluetoothDetailAdapter mDetailAdapter;
    
    static {
        BLUETOOTH_SETTINGS = new Intent("android.settings.BLUETOOTH_SETTINGS");
    }
    
    public BluetoothTile(final QSHost qsHost, final BluetoothController mController, final ActivityStarter mActivityStarter) {
        super(qsHost);
        this.mCallback = new BluetoothController.Callback() {
            @Override
            public void onBluetoothDevicesChanged() {
                BluetoothTile.this.refreshState();
                if (QSTileImpl.this.isShowingDetail()) {
                    BluetoothTile.this.mDetailAdapter.updateItems();
                }
            }
            
            @Override
            public void onBluetoothStateChange(final boolean b) {
                BluetoothTile.this.refreshState();
                if (QSTileImpl.this.isShowingDetail()) {
                    BluetoothTile.this.mDetailAdapter.updateItems();
                    final BluetoothTile this$0 = BluetoothTile.this;
                    this$0.fireToggleStateChanged(this$0.mDetailAdapter.getToggleState());
                }
            }
        };
        this.mController = mController;
        this.mActivityStarter = mActivityStarter;
        this.mDetailAdapter = (BluetoothDetailAdapter)this.createDetailAdapter();
        this.mController.observe(this.getLifecycle(), this.mCallback);
    }
    
    private String getSecondaryLabel(final boolean b, final boolean b2, final boolean b3, final boolean b4) {
        if (b2) {
            return super.mContext.getString(R$string.quick_settings_connecting);
        }
        if (b4) {
            return super.mContext.getString(R$string.quick_settings_bluetooth_secondary_label_transient);
        }
        final List<CachedBluetoothDevice> connectedDevices = this.mController.getConnectedDevices();
        if (b && b3 && !connectedDevices.isEmpty()) {
            if (connectedDevices.size() > 1) {
                return super.mContext.getResources().getQuantityString(R$plurals.quick_settings_hotspot_secondary_label_num_devices, connectedDevices.size(), new Object[] { connectedDevices.size() });
            }
            final CachedBluetoothDevice cachedBluetoothDevice = connectedDevices.get(0);
            final int batteryLevel = cachedBluetoothDevice.getBatteryLevel();
            if (batteryLevel > -1) {
                return super.mContext.getString(R$string.quick_settings_bluetooth_secondary_label_battery_level, new Object[] { Utils.formatPercentage(batteryLevel) });
            }
            final BluetoothClass btClass = cachedBluetoothDevice.getBtClass();
            if (btClass != null) {
                if (cachedBluetoothDevice.isHearingAidDevice()) {
                    return super.mContext.getString(R$string.quick_settings_bluetooth_secondary_label_hearing_aids);
                }
                if (btClass.doesClassMatch(1)) {
                    return super.mContext.getString(R$string.quick_settings_bluetooth_secondary_label_audio);
                }
                if (btClass.doesClassMatch(0)) {
                    return super.mContext.getString(R$string.quick_settings_bluetooth_secondary_label_headset);
                }
                if (btClass.doesClassMatch(3)) {
                    return super.mContext.getString(R$string.quick_settings_bluetooth_secondary_label_input);
                }
            }
        }
        return null;
    }
    
    @Override
    protected String composeChangeAnnouncement() {
        if (((BooleanState)super.mState).value) {
            return super.mContext.getString(R$string.accessibility_quick_settings_bluetooth_changed_on);
        }
        return super.mContext.getString(R$string.accessibility_quick_settings_bluetooth_changed_off);
    }
    
    protected DetailAdapter createDetailAdapter() {
        return new BluetoothDetailAdapter();
    }
    
    @Override
    public DetailAdapter getDetailAdapter() {
        return this.mDetailAdapter;
    }
    
    @Override
    public Intent getLongClickIntent() {
        return new Intent("android.settings.BLUETOOTH_SETTINGS");
    }
    
    @Override
    public int getMetricsCategory() {
        return 113;
    }
    
    @Override
    public CharSequence getTileLabel() {
        return super.mContext.getString(R$string.quick_settings_bluetooth_label);
    }
    
    @Override
    protected void handleClick() {
        final boolean value = ((BooleanState)super.mState).value;
        Object arg_SHOW_TRANSIENT_ENABLING;
        if (value) {
            arg_SHOW_TRANSIENT_ENABLING = null;
        }
        else {
            arg_SHOW_TRANSIENT_ENABLING = QSTileImpl.ARG_SHOW_TRANSIENT_ENABLING;
        }
        this.refreshState(arg_SHOW_TRANSIENT_ENABLING);
        this.mController.setBluetoothEnabled(value ^ true);
    }
    
    @Override
    protected void handleSecondaryClick() {
        if (!this.mController.canConfigBluetooth()) {
            this.mActivityStarter.postStartActivityDismissingKeyguard(new Intent("android.settings.BLUETOOTH_SETTINGS"), 0);
            return;
        }
        this.showDetail(true);
        if (!((BooleanState)super.mState).value) {
            this.mController.setBluetoothEnabled(true);
        }
    }
    
    @Override
    protected void handleUpdateState(final BooleanState booleanState, Object o) {
        final boolean b = o == QSTileImpl.ARG_SHOW_TRANSIENT_ENABLING;
        final boolean value = b || this.mController.isBluetoothEnabled();
        final boolean bluetoothConnected = this.mController.isBluetoothConnected();
        final boolean bluetoothConnecting = this.mController.isBluetoothConnecting();
        booleanState.isTransient = (b || bluetoothConnecting || this.mController.getBluetoothState() == 11);
        booleanState.dualTarget = true;
        booleanState.value = value;
        if (booleanState.slash == null) {
            booleanState.slash = new QSTile.SlashState();
        }
        booleanState.slash.isSlashed = (value ^ true);
        booleanState.label = super.mContext.getString(R$string.quick_settings_bluetooth_label);
        booleanState.secondaryLabel = TextUtils.emptyIfNull(this.getSecondaryLabel(value, bluetoothConnecting, bluetoothConnected, booleanState.isTransient));
        booleanState.contentDescription = booleanState.label;
        booleanState.stateDescription = "";
        if (value) {
            if (bluetoothConnected) {
                booleanState.icon = new BluetoothConnectedTileIcon();
                if (!TextUtils.isEmpty((CharSequence)this.mController.getConnectedDeviceName())) {
                    booleanState.label = this.mController.getConnectedDeviceName();
                }
                o = new StringBuilder();
                ((StringBuilder)o).append(super.mContext.getString(R$string.accessibility_bluetooth_name, new Object[] { booleanState.label }));
                ((StringBuilder)o).append(", ");
                ((StringBuilder)o).append((Object)booleanState.secondaryLabel);
                booleanState.stateDescription = ((StringBuilder)o).toString();
            }
            else if (booleanState.isTransient) {
                booleanState.icon = ResourceIcon.get(17302318);
                booleanState.stateDescription = booleanState.secondaryLabel;
            }
            else {
                booleanState.icon = ResourceIcon.get(17302796);
                booleanState.contentDescription = super.mContext.getString(R$string.accessibility_quick_settings_bluetooth);
                booleanState.stateDescription = super.mContext.getString(R$string.accessibility_not_connected);
            }
            booleanState.state = 2;
        }
        else {
            booleanState.icon = ResourceIcon.get(17302796);
            booleanState.contentDescription = super.mContext.getString(R$string.accessibility_quick_settings_bluetooth);
            booleanState.state = 1;
        }
        booleanState.dualLabelContentDescription = super.mContext.getResources().getString(R$string.accessibility_quick_settings_open_settings, new Object[] { this.getTileLabel() });
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
    }
    
    @Override
    public boolean isAvailable() {
        return this.mController.isBluetoothSupported();
    }
    
    @Override
    public BooleanState newTileState() {
        return new QSTile.BooleanState();
    }
    
    private class BluetoothBatteryTileIcon extends Icon
    {
        private int mBatteryLevel;
        private float mIconScale;
        
        BluetoothBatteryTileIcon(final BluetoothTile bluetoothTile, final int mBatteryLevel, final float mIconScale) {
            this.mBatteryLevel = mBatteryLevel;
            this.mIconScale = mIconScale;
        }
        
        @Override
        public Drawable getDrawable(final Context context) {
            return (Drawable)BluetoothDeviceLayerDrawable.createLayerDrawable(context, R$drawable.ic_bluetooth_connected, this.mBatteryLevel, this.mIconScale);
        }
    }
    
    private class BluetoothConnectedTileIcon extends Icon
    {
        BluetoothConnectedTileIcon(final BluetoothTile bluetoothTile) {
        }
        
        @Override
        public Drawable getDrawable(final Context context) {
            return context.getDrawable(R$drawable.ic_bluetooth_connected);
        }
    }
    
    protected class BluetoothDetailAdapter implements DetailAdapter, QSDetailItems.Callback
    {
        private QSDetailItems mItems;
        
        private void updateItems() {
            if (this.mItems == null) {
                return;
            }
            if (BluetoothTile.this.mController.isBluetoothEnabled()) {
                this.mItems.setEmptyState(R$drawable.ic_qs_bluetooth_detail_empty, R$string.quick_settings_bluetooth_detail_empty_text);
            }
            else {
                this.mItems.setEmptyState(R$drawable.ic_qs_bluetooth_detail_empty, R$string.bt_is_off);
            }
            final ArrayList<Item> list = new ArrayList<Item>();
            final Collection<CachedBluetoothDevice> devices = BluetoothTile.this.mController.getDevices();
            if (devices != null) {
                final Iterator<CachedBluetoothDevice> iterator = devices.iterator();
                int n2;
                int n = n2 = 0;
                while (iterator.hasNext()) {
                    final CachedBluetoothDevice tag = iterator.next();
                    if (BluetoothTile.this.mController.getBondState(tag) == 10) {
                        continue;
                    }
                    final Item e = new QSDetailItems.Item();
                    e.iconResId = 17302796;
                    e.line1 = tag.getName();
                    e.tag = tag;
                    final int maxConnectionState = tag.getMaxConnectionState();
                    if (maxConnectionState == 2) {
                        e.iconResId = R$drawable.ic_bluetooth_connected;
                        final int batteryLevel = tag.getBatteryLevel();
                        if (batteryLevel > -1) {
                            e.icon = new BluetoothBatteryTileIcon(batteryLevel, 1.0f);
                            e.line2 = BluetoothTile.this.mContext.getString(R$string.quick_settings_connected_battery_level, new Object[] { Utils.formatPercentage(batteryLevel) });
                        }
                        else {
                            e.line2 = BluetoothTile.this.mContext.getString(R$string.quick_settings_connected);
                        }
                        e.canDisconnect = true;
                        list.add(n, e);
                        ++n;
                    }
                    else if (maxConnectionState == 1) {
                        e.iconResId = R$drawable.ic_qs_bluetooth_connecting;
                        e.line2 = BluetoothTile.this.mContext.getString(R$string.quick_settings_connecting);
                        list.add(n, e);
                    }
                    else {
                        list.add(e);
                    }
                    if (++n2 == 20) {
                        break;
                    }
                }
            }
            this.mItems.setItems((Item[])list.toArray(new Item[list.size()]));
        }
        
        @Override
        public View createDetailView(final Context context, final View view, final ViewGroup viewGroup) {
            (this.mItems = QSDetailItems.convertOrInflate(context, view, viewGroup)).setTagSuffix("Bluetooth");
            this.mItems.setCallback((QSDetailItems.Callback)this);
            this.updateItems();
            this.setItemsVisible(((BooleanState)BluetoothTile.this.mState).value);
            return (View)this.mItems;
        }
        
        @Override
        public int getMetricsCategory() {
            return 150;
        }
        
        @Override
        public Intent getSettingsIntent() {
            return BluetoothTile.BLUETOOTH_SETTINGS;
        }
        
        @Override
        public CharSequence getTitle() {
            return BluetoothTile.this.mContext.getString(R$string.quick_settings_bluetooth_label);
        }
        
        @Override
        public boolean getToggleEnabled() {
            return BluetoothTile.this.mController.getBluetoothState() == 10 || BluetoothTile.this.mController.getBluetoothState() == 12;
        }
        
        @Override
        public Boolean getToggleState() {
            return ((BooleanState)BluetoothTile.this.mState).value;
        }
        
        @Override
        public void onDetailItemClick(final Item item) {
            if (item != null) {
                final Object tag = item.tag;
                if (tag != null) {
                    final CachedBluetoothDevice cachedBluetoothDevice = (CachedBluetoothDevice)tag;
                    if (cachedBluetoothDevice != null && cachedBluetoothDevice.getMaxConnectionState() == 0) {
                        BluetoothTile.this.mController.connect(cachedBluetoothDevice);
                    }
                }
            }
        }
        
        @Override
        public void onDetailItemDisconnect(final Item item) {
            if (item != null) {
                final Object tag = item.tag;
                if (tag != null) {
                    final CachedBluetoothDevice cachedBluetoothDevice = (CachedBluetoothDevice)tag;
                    if (cachedBluetoothDevice != null) {
                        BluetoothTile.this.mController.disconnect(cachedBluetoothDevice);
                    }
                }
            }
        }
        
        public void setItemsVisible(final boolean itemsVisible) {
            final QSDetailItems mItems = this.mItems;
            if (mItems == null) {
                return;
            }
            mItems.setItemsVisible(itemsVisible);
        }
        
        @Override
        public void setToggleState(final boolean bluetoothEnabled) {
            MetricsLogger.action(BluetoothTile.this.mContext, 154, bluetoothEnabled);
            BluetoothTile.this.mController.setBluetoothEnabled(bluetoothEnabled);
        }
    }
}
