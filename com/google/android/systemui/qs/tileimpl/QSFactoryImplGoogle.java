// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.qs.tileimpl;

import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.qs.tiles.ScreenRecordTile;
import com.android.systemui.qs.tiles.UiModeNightTile;
import com.android.systemui.util.leak.GarbageMonitor;
import com.android.systemui.qs.tiles.NfcTile;
import com.android.systemui.qs.tiles.NightDisplayTile;
import com.android.systemui.qs.tiles.DataSaverTile;
import com.android.systemui.qs.tiles.BatterySaverTile;
import com.android.systemui.qs.tiles.UserTile;
import com.android.systemui.qs.tiles.HotspotTile;
import com.android.systemui.qs.tiles.CastTile;
import com.android.systemui.qs.tiles.LocationTile;
import com.android.systemui.qs.tiles.FlashlightTile;
import com.android.systemui.qs.tiles.RotationLockTile;
import com.android.systemui.qs.tiles.WorkModeTile;
import com.android.systemui.qs.tiles.AirplaneModeTile;
import com.android.systemui.qs.tiles.ColorInversionTile;
import com.android.systemui.qs.tiles.DndTile;
import com.android.systemui.qs.tiles.CellularTile;
import com.android.systemui.qs.tiles.BluetoothTile;
import com.android.systemui.qs.tiles.WifiTile;
import com.android.systemui.qs.QSHost;
import dagger.Lazy;
import com.google.android.systemui.qs.tiles.ReverseChargingTile;
import javax.inject.Provider;
import com.android.systemui.qs.tileimpl.QSFactoryImpl;

public class QSFactoryImplGoogle extends QSFactoryImpl
{
    private final Provider<ReverseChargingTile> mReverseChargingTileProvider;
    
    public QSFactoryImplGoogle(final Lazy<QSHost> lazy, final Provider<WifiTile> provider, final Provider<BluetoothTile> provider2, final Provider<CellularTile> provider3, final Provider<DndTile> provider4, final Provider<ColorInversionTile> provider5, final Provider<AirplaneModeTile> provider6, final Provider<WorkModeTile> provider7, final Provider<RotationLockTile> provider8, final Provider<FlashlightTile> provider9, final Provider<LocationTile> provider10, final Provider<CastTile> provider11, final Provider<HotspotTile> provider12, final Provider<UserTile> provider13, final Provider<BatterySaverTile> provider14, final Provider<DataSaverTile> provider15, final Provider<NightDisplayTile> provider16, final Provider<NfcTile> provider17, final Provider<GarbageMonitor.MemoryTile> provider18, final Provider<UiModeNightTile> provider19, final Provider<ScreenRecordTile> provider20, final Provider<ReverseChargingTile> mReverseChargingTileProvider) {
        super(lazy, provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16, provider17, provider18, provider19, provider20);
        this.mReverseChargingTileProvider = mReverseChargingTileProvider;
    }
    
    private QSTileImpl createTileInternal(final String s) {
        int n = 0;
        Label_0028: {
            if (s.hashCode() == 1099846370) {
                if (s.equals("reverse")) {
                    n = 0;
                    break Label_0028;
                }
            }
            n = -1;
        }
        if (n != 0) {
            return null;
        }
        return this.mReverseChargingTileProvider.get();
    }
    
    @Override
    public QSTile createTile(final String s) {
        final QSTileImpl tileInternal = this.createTileInternal(s);
        if (tileInternal != null) {
            return tileInternal;
        }
        return super.createTile(s);
    }
}
