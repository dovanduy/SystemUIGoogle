// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.internal.logging.MetricsLogger;
import android.view.View$OnAttachStateChangeListener;
import android.view.ViewGroup;
import java.util.LinkedHashMap;
import com.android.systemui.qs.QSDetailItems;
import android.widget.Button;
import com.android.systemui.R$drawable;
import android.util.Log;
import android.media.MediaRouter$RouteInfo;
import com.android.systemui.plugins.qs.DetailAdapter;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import android.view.View$OnClickListener;
import com.android.internal.app.MediaRouteDialogPresenter;
import android.view.View;
import com.android.systemui.R$string;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import androidx.lifecycle.LifecycleOwner;
import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.app.Dialog;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.plugins.ActivityStarter;
import android.content.Intent;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;

public class CastTile extends QSTileImpl<BooleanState>
{
    private static final Intent CAST_SETTINGS;
    private final ActivityStarter mActivityStarter;
    private final Callback mCallback;
    private final CastController mController;
    private final CastDetailAdapter mDetailAdapter;
    private Dialog mDialog;
    private final KeyguardStateController mKeyguard;
    private final NetworkController mNetworkController;
    private final NetworkController.SignalCallback mSignalCallback;
    private boolean mWifiConnected;
    
    static {
        CAST_SETTINGS = new Intent("android.settings.CAST_SETTINGS");
    }
    
    public CastTile(final QSHost qsHost, final CastController mController, final KeyguardStateController mKeyguard, final NetworkController mNetworkController, final ActivityStarter mActivityStarter) {
        super(qsHost);
        this.mCallback = new Callback();
        this.mSignalCallback = new NetworkController.SignalCallback() {
            @Override
            public void setWifiIndicators(final boolean b, final IconState iconState, final IconState iconState2, final boolean b2, final boolean b3, final String s, final boolean b4, final String s2) {
                final boolean b5 = b && iconState2.visible;
                if (b5 != CastTile.this.mWifiConnected) {
                    CastTile.this.mWifiConnected = b5;
                    CastTile.this.refreshState();
                }
            }
        };
        this.mController = mController;
        this.mDetailAdapter = new CastDetailAdapter();
        this.mKeyguard = mKeyguard;
        this.mNetworkController = mNetworkController;
        this.mActivityStarter = mActivityStarter;
        this.mController.observe(this, (CastController.Callback)this.mCallback);
        this.mKeyguard.observe(this, (KeyguardStateController.Callback)this.mCallback);
        this.mNetworkController.observe(this, this.mSignalCallback);
    }
    
    private List<CastController.CastDevice> getActiveDevices() {
        final ArrayList<CastController.CastDevice> list = new ArrayList<CastController.CastDevice>();
        for (final CastController.CastDevice e : this.mController.getCastDevices()) {
            final int state = e.state;
            if (state == 2 || state == 1) {
                list.add(e);
            }
        }
        return list;
    }
    
    private String getDeviceName(final CastController.CastDevice castDevice) {
        String s = castDevice.name;
        if (s == null) {
            s = super.mContext.getString(R$string.quick_settings_cast_device_default_name);
        }
        return s;
    }
    
    @Override
    protected String composeChangeAnnouncement() {
        if (!((BooleanState)super.mState).value) {
            return super.mContext.getString(R$string.accessibility_casting_turned_off);
        }
        return null;
    }
    
    @Override
    public DetailAdapter getDetailAdapter() {
        return this.mDetailAdapter;
    }
    
    @Override
    public Intent getLongClickIntent() {
        return new Intent("android.settings.CAST_SETTINGS");
    }
    
    @Override
    public int getMetricsCategory() {
        return 114;
    }
    
    @Override
    public CharSequence getTileLabel() {
        return super.mContext.getString(R$string.quick_settings_cast_title);
    }
    
    @Override
    protected void handleClick() {
        if (this.getState().state == 0) {
            return;
        }
        final List<CastController.CastDevice> activeDevices = this.getActiveDevices();
        if (!activeDevices.isEmpty() && !(activeDevices.get(0).tag instanceof MediaRouter$RouteInfo)) {
            this.mController.stopCasting((CastController.CastDevice)activeDevices.get(0));
        }
        else {
            this.mActivityStarter.postQSRunnableDismissingKeyguard(new _$$Lambda$CastTile$0TU5SvbFGUs5F0udF1tvlhHVObs(this));
        }
    }
    
    @Override
    protected void handleLongClick() {
        this.handleClick();
    }
    
    @Override
    protected void handleSecondaryClick() {
        this.handleClick();
    }
    
    public void handleSetListening(final boolean b) {
        super.handleSetListening(b);
        if (QSTileImpl.DEBUG) {
            final String tag = super.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("handleSetListening ");
            sb.append(b);
            Log.d(tag, sb.toString());
        }
        if (!b) {
            this.mController.setDiscovering(false);
        }
    }
    
    @Override
    protected void handleUpdateState(final BooleanState booleanState, final Object o) {
        final String string = super.mContext.getString(R$string.quick_settings_cast_title);
        booleanState.label = string;
        booleanState.contentDescription = string;
        booleanState.stateDescription = "";
        booleanState.value = false;
        final List<CastController.CastDevice> castDevices = this.mController.getCastDevices();
        final Iterator<CastController.CastDevice> iterator = castDevices.iterator();
        int n = 0;
        int n2;
        int n3;
        while (true) {
            final boolean hasNext = iterator.hasNext();
            n2 = 2;
            n3 = n;
            if (!hasNext) {
                break;
            }
            final CastController.CastDevice castDevice = iterator.next();
            final int state = castDevice.state;
            if (state == 2) {
                booleanState.value = true;
                booleanState.secondaryLabel = this.getDeviceName(castDevice);
                final StringBuilder sb = new StringBuilder();
                sb.append((Object)booleanState.stateDescription);
                sb.append(",");
                sb.append(super.mContext.getString(R$string.accessibility_cast_name, new Object[] { booleanState.label }));
                booleanState.stateDescription = sb.toString();
                n3 = 0;
                break;
            }
            if (state != 1) {
                continue;
            }
            n = 1;
        }
        if (n3 != 0 && !booleanState.value) {
            booleanState.secondaryLabel = super.mContext.getString(R$string.quick_settings_connecting);
        }
        int n4;
        if (booleanState.value) {
            n4 = R$drawable.ic_cast_connected;
        }
        else {
            n4 = R$drawable.ic_cast;
        }
        booleanState.icon = ResourceIcon.get(n4);
        if (!this.mWifiConnected && !booleanState.value) {
            booleanState.state = 0;
            booleanState.secondaryLabel = super.mContext.getString(R$string.quick_settings_cast_no_wifi);
        }
        else {
            int state2;
            if (booleanState.value) {
                state2 = n2;
            }
            else {
                state2 = 1;
            }
            booleanState.state = state2;
            if (!booleanState.value) {
                booleanState.secondaryLabel = "";
            }
            final StringBuilder sb2 = new StringBuilder();
            sb2.append((Object)booleanState.contentDescription);
            sb2.append(",");
            sb2.append(super.mContext.getString(R$string.accessibility_quick_settings_open_details));
            booleanState.contentDescription = sb2.toString();
            booleanState.expandedAccessibilityClassName = Button.class.getName();
        }
        final StringBuilder sb3 = new StringBuilder();
        sb3.append((Object)booleanState.stateDescription);
        sb3.append(", ");
        sb3.append((Object)booleanState.secondaryLabel);
        booleanState.stateDescription = sb3.toString();
        this.mDetailAdapter.updateItems(castDevices);
    }
    
    @Override
    protected void handleUserSwitch(final int currentUserId) {
        super.handleUserSwitch(currentUserId);
        this.mController.setCurrentUserId(currentUserId);
    }
    
    @Override
    public BooleanState newTileState() {
        final BooleanState booleanState = new QSTile.BooleanState();
        booleanState.handlesLongClick = false;
        return booleanState;
    }
    
    @Override
    public void showDetail(final boolean b) {
        super.mUiHandler.post((Runnable)new _$$Lambda$CastTile$WPXsuhhRJ1um_wt53q0kaFd3rzI(this));
    }
    
    private final class Callback implements CastController.Callback, KeyguardStateController.Callback
    {
        @Override
        public void onCastDevicesChanged() {
            CastTile.this.refreshState();
        }
        
        @Override
        public void onKeyguardShowingChanged() {
            CastTile.this.refreshState();
        }
    }
    
    private final class CastDetailAdapter implements DetailAdapter, QSDetailItems.Callback
    {
        private QSDetailItems mItems;
        private final LinkedHashMap<String, CastController.CastDevice> mVisibleOrder;
        
        private CastDetailAdapter() {
            this.mVisibleOrder = new LinkedHashMap<String, CastController.CastDevice>();
        }
        
        private void updateItems(final List<CastController.CastDevice> list) {
            if (this.mItems == null) {
                return;
            }
            final QSDetailItems.Item[] array = null;
            final QSDetailItems.Item[] array2 = null;
            QSDetailItems.Item[] items = array;
            Label_0350: {
                if (list != null) {
                    items = array;
                    if (!list.isEmpty()) {
                        final Iterator<CastController.CastDevice> iterator = list.iterator();
                        while (true) {
                            CastController.CastDevice tag;
                            do {
                                final boolean hasNext = iterator.hasNext();
                                int n = 0;
                                final Object[] array3 = array2;
                                if (hasNext) {
                                    tag = iterator.next();
                                }
                                else {
                                    if ((items = (QSDetailItems.Item[])array3) != null) {
                                        break Label_0350;
                                    }
                                    for (final CastController.CastDevice value : list) {
                                        this.mVisibleOrder.put(value.id, value);
                                    }
                                    final Item[] array4 = new Item[list.size()];
                                    final Iterator<String> iterator3 = this.mVisibleOrder.keySet().iterator();
                                    while (true) {
                                        items = array4;
                                        if (!iterator3.hasNext()) {
                                            break Label_0350;
                                        }
                                        final CastController.CastDevice tag2 = this.mVisibleOrder.get(iterator3.next());
                                        if (!list.contains(tag2)) {
                                            continue;
                                        }
                                        final Item item = new QSDetailItems.Item();
                                        item.iconResId = R$drawable.ic_cast;
                                        item.line1 = CastTile.this.getDeviceName(tag2);
                                        if (tag2.state == 1) {
                                            item.line2 = CastTile.this.mContext.getString(R$string.quick_settings_connecting);
                                        }
                                        item.tag = tag2;
                                        array4[n] = item;
                                        ++n;
                                    }
                                }
                            } while (tag.state != 2);
                            final Item item2 = new QSDetailItems.Item();
                            item2.iconResId = R$drawable.ic_cast_connected;
                            item2.line1 = CastTile.this.getDeviceName(tag);
                            item2.line2 = CastTile.this.mContext.getString(R$string.quick_settings_connected);
                            item2.tag = tag;
                            item2.canDisconnect = true;
                            final Object[] array3 = { item2 };
                            continue;
                        }
                    }
                }
            }
            this.mItems.setItems(items);
        }
        
        @Override
        public View createDetailView(final Context context, final View view, final ViewGroup viewGroup) {
            (this.mItems = QSDetailItems.convertOrInflate(context, view, viewGroup)).setTagSuffix("Cast");
            if (view == null) {
                if (QSTileImpl.DEBUG) {
                    Log.d(CastTile.this.TAG, "addOnAttachStateChangeListener");
                }
                this.mItems.addOnAttachStateChangeListener((View$OnAttachStateChangeListener)new View$OnAttachStateChangeListener() {
                    public void onViewAttachedToWindow(final View view) {
                        if (QSTileImpl.DEBUG) {
                            Log.d(CastTile.this.TAG, "onViewAttachedToWindow");
                        }
                    }
                    
                    public void onViewDetachedFromWindow(final View view) {
                        if (QSTileImpl.DEBUG) {
                            Log.d(CastTile.this.TAG, "onViewDetachedFromWindow");
                        }
                        CastDetailAdapter.this.mVisibleOrder.clear();
                    }
                });
            }
            this.mItems.setEmptyState(R$drawable.ic_qs_cast_detail_empty, R$string.quick_settings_cast_detail_empty_text);
            this.mItems.setCallback((QSDetailItems.Callback)this);
            this.updateItems(CastTile.this.mController.getCastDevices());
            CastTile.this.mController.setDiscovering(true);
            return (View)this.mItems;
        }
        
        @Override
        public int getMetricsCategory() {
            return 151;
        }
        
        @Override
        public Intent getSettingsIntent() {
            return CastTile.CAST_SETTINGS;
        }
        
        @Override
        public CharSequence getTitle() {
            return CastTile.this.mContext.getString(R$string.quick_settings_cast_title);
        }
        
        @Override
        public Boolean getToggleState() {
            return null;
        }
        
        @Override
        public void onDetailItemClick(final Item item) {
            if (item != null) {
                if (item.tag != null) {
                    MetricsLogger.action(CastTile.this.mContext, 157);
                    CastTile.this.mController.startCasting((CastController.CastDevice)item.tag);
                }
            }
        }
        
        @Override
        public void onDetailItemDisconnect(final Item item) {
            if (item != null) {
                if (item.tag != null) {
                    MetricsLogger.action(CastTile.this.mContext, 158);
                    CastTile.this.mController.stopCasting((CastController.CastDevice)item.tag);
                }
            }
        }
        
        @Override
        public void setToggleState(final boolean b) {
        }
    }
}
