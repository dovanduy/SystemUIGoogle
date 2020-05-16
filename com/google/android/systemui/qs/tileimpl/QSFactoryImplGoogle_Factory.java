// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.qs.tileimpl;

import dagger.internal.DoubleCheck;
import com.android.systemui.qs.tiles.WorkModeTile;
import com.android.systemui.qs.tiles.WifiTile;
import com.android.systemui.qs.tiles.UserTile;
import com.android.systemui.qs.tiles.UiModeNightTile;
import com.android.systemui.qs.tiles.ScreenRecordTile;
import com.android.systemui.qs.tiles.RotationLockTile;
import com.google.android.systemui.qs.tiles.ReverseChargingTile;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tiles.NightDisplayTile;
import com.android.systemui.qs.tiles.NfcTile;
import com.android.systemui.util.leak.GarbageMonitor;
import com.android.systemui.qs.tiles.LocationTile;
import com.android.systemui.qs.tiles.HotspotTile;
import com.android.systemui.qs.tiles.FlashlightTile;
import com.android.systemui.qs.tiles.DndTile;
import com.android.systemui.qs.tiles.DataSaverTile;
import com.android.systemui.qs.tiles.ColorInversionTile;
import com.android.systemui.qs.tiles.CellularTile;
import com.android.systemui.qs.tiles.CastTile;
import com.android.systemui.qs.tiles.BluetoothTile;
import com.android.systemui.qs.tiles.BatterySaverTile;
import com.android.systemui.qs.tiles.AirplaneModeTile;
import javax.inject.Provider;
import dagger.internal.Factory;

public final class QSFactoryImplGoogle_Factory implements Factory<QSFactoryImplGoogle>
{
    private final Provider<AirplaneModeTile> airplaneModeTileProvider;
    private final Provider<BatterySaverTile> batterySaverTileProvider;
    private final Provider<BluetoothTile> bluetoothTileProvider;
    private final Provider<CastTile> castTileProvider;
    private final Provider<CellularTile> cellularTileProvider;
    private final Provider<ColorInversionTile> colorInversionTileProvider;
    private final Provider<DataSaverTile> dataSaverTileProvider;
    private final Provider<DndTile> dndTileProvider;
    private final Provider<FlashlightTile> flashlightTileProvider;
    private final Provider<HotspotTile> hotspotTileProvider;
    private final Provider<LocationTile> locationTileProvider;
    private final Provider<GarbageMonitor.MemoryTile> memoryTileProvider;
    private final Provider<NfcTile> nfcTileProvider;
    private final Provider<NightDisplayTile> nightDisplayTileProvider;
    private final Provider<QSHost> qsHostLazyProvider;
    private final Provider<ReverseChargingTile> reverseChargingTileProvider;
    private final Provider<RotationLockTile> rotationLockTileProvider;
    private final Provider<ScreenRecordTile> screenRecordTileProvider;
    private final Provider<UiModeNightTile> uiModeNightTileProvider;
    private final Provider<UserTile> userTileProvider;
    private final Provider<WifiTile> wifiTileProvider;
    private final Provider<WorkModeTile> workModeTileProvider;
    
    public QSFactoryImplGoogle_Factory(final Provider<QSHost> qsHostLazyProvider, final Provider<WifiTile> wifiTileProvider, final Provider<BluetoothTile> bluetoothTileProvider, final Provider<CellularTile> cellularTileProvider, final Provider<DndTile> dndTileProvider, final Provider<ColorInversionTile> colorInversionTileProvider, final Provider<AirplaneModeTile> airplaneModeTileProvider, final Provider<WorkModeTile> workModeTileProvider, final Provider<RotationLockTile> rotationLockTileProvider, final Provider<FlashlightTile> flashlightTileProvider, final Provider<LocationTile> locationTileProvider, final Provider<CastTile> castTileProvider, final Provider<HotspotTile> hotspotTileProvider, final Provider<UserTile> userTileProvider, final Provider<BatterySaverTile> batterySaverTileProvider, final Provider<DataSaverTile> dataSaverTileProvider, final Provider<NightDisplayTile> nightDisplayTileProvider, final Provider<NfcTile> nfcTileProvider, final Provider<GarbageMonitor.MemoryTile> memoryTileProvider, final Provider<UiModeNightTile> uiModeNightTileProvider, final Provider<ScreenRecordTile> screenRecordTileProvider, final Provider<ReverseChargingTile> reverseChargingTileProvider) {
        this.qsHostLazyProvider = qsHostLazyProvider;
        this.wifiTileProvider = wifiTileProvider;
        this.bluetoothTileProvider = bluetoothTileProvider;
        this.cellularTileProvider = cellularTileProvider;
        this.dndTileProvider = dndTileProvider;
        this.colorInversionTileProvider = colorInversionTileProvider;
        this.airplaneModeTileProvider = airplaneModeTileProvider;
        this.workModeTileProvider = workModeTileProvider;
        this.rotationLockTileProvider = rotationLockTileProvider;
        this.flashlightTileProvider = flashlightTileProvider;
        this.locationTileProvider = locationTileProvider;
        this.castTileProvider = castTileProvider;
        this.hotspotTileProvider = hotspotTileProvider;
        this.userTileProvider = userTileProvider;
        this.batterySaverTileProvider = batterySaverTileProvider;
        this.dataSaverTileProvider = dataSaverTileProvider;
        this.nightDisplayTileProvider = nightDisplayTileProvider;
        this.nfcTileProvider = nfcTileProvider;
        this.memoryTileProvider = memoryTileProvider;
        this.uiModeNightTileProvider = uiModeNightTileProvider;
        this.screenRecordTileProvider = screenRecordTileProvider;
        this.reverseChargingTileProvider = reverseChargingTileProvider;
    }
    
    public static QSFactoryImplGoogle_Factory create(final Provider<QSHost> provider, final Provider<WifiTile> provider2, final Provider<BluetoothTile> provider3, final Provider<CellularTile> provider4, final Provider<DndTile> provider5, final Provider<ColorInversionTile> provider6, final Provider<AirplaneModeTile> provider7, final Provider<WorkModeTile> provider8, final Provider<RotationLockTile> provider9, final Provider<FlashlightTile> provider10, final Provider<LocationTile> provider11, final Provider<CastTile> provider12, final Provider<HotspotTile> provider13, final Provider<UserTile> provider14, final Provider<BatterySaverTile> provider15, final Provider<DataSaverTile> provider16, final Provider<NightDisplayTile> provider17, final Provider<NfcTile> provider18, final Provider<GarbageMonitor.MemoryTile> provider19, final Provider<UiModeNightTile> provider20, final Provider<ScreenRecordTile> provider21, final Provider<ReverseChargingTile> provider22) {
        return new QSFactoryImplGoogle_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16, provider17, provider18, provider19, provider20, provider21, provider22);
    }
    
    public static QSFactoryImplGoogle provideInstance(final Provider<QSHost> provider, final Provider<WifiTile> provider2, final Provider<BluetoothTile> provider3, final Provider<CellularTile> provider4, final Provider<DndTile> provider5, final Provider<ColorInversionTile> provider6, final Provider<AirplaneModeTile> provider7, final Provider<WorkModeTile> provider8, final Provider<RotationLockTile> provider9, final Provider<FlashlightTile> provider10, final Provider<LocationTile> provider11, final Provider<CastTile> provider12, final Provider<HotspotTile> provider13, final Provider<UserTile> provider14, final Provider<BatterySaverTile> provider15, final Provider<DataSaverTile> provider16, final Provider<NightDisplayTile> provider17, final Provider<NfcTile> provider18, final Provider<GarbageMonitor.MemoryTile> provider19, final Provider<UiModeNightTile> provider20, final Provider<ScreenRecordTile> provider21, final Provider<ReverseChargingTile> provider22) {
        return new QSFactoryImplGoogle(DoubleCheck.lazy(provider), provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16, provider17, provider18, provider19, provider20, provider21, provider22);
    }
    
    @Override
    public QSFactoryImplGoogle get() {
        return provideInstance(this.qsHostLazyProvider, this.wifiTileProvider, this.bluetoothTileProvider, this.cellularTileProvider, this.dndTileProvider, this.colorInversionTileProvider, this.airplaneModeTileProvider, this.workModeTileProvider, this.rotationLockTileProvider, this.flashlightTileProvider, this.locationTileProvider, this.castTileProvider, this.hotspotTileProvider, this.userTileProvider, this.batterySaverTileProvider, this.dataSaverTileProvider, this.nightDisplayTileProvider, this.nfcTileProvider, this.memoryTileProvider, this.uiModeNightTileProvider, this.screenRecordTileProvider, this.reverseChargingTileProvider);
    }
}
