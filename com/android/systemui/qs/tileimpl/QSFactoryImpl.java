// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tileimpl;

import com.android.systemui.plugins.qs.QSIconView;
import android.content.Context;
import android.view.ContextThemeWrapper;
import com.android.systemui.R$style;
import com.android.systemui.plugins.qs.QSTileView;
import com.android.systemui.plugins.qs.QSTile;
import android.util.Log;
import android.os.Build;
import com.android.systemui.qs.external.CustomTile;
import com.android.systemui.qs.tiles.WorkModeTile;
import com.android.systemui.qs.tiles.WifiTile;
import com.android.systemui.qs.tiles.UserTile;
import com.android.systemui.qs.tiles.UiModeNightTile;
import com.android.systemui.qs.tiles.ScreenRecordTile;
import com.android.systemui.qs.tiles.RotationLockTile;
import com.android.systemui.qs.QSHost;
import dagger.Lazy;
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
import com.android.systemui.plugins.qs.QSFactory;

public class QSFactoryImpl implements QSFactory
{
    private final Provider<AirplaneModeTile> mAirplaneModeTileProvider;
    private final Provider<BatterySaverTile> mBatterySaverTileProvider;
    private final Provider<BluetoothTile> mBluetoothTileProvider;
    private final Provider<CastTile> mCastTileProvider;
    private final Provider<CellularTile> mCellularTileProvider;
    private final Provider<ColorInversionTile> mColorInversionTileProvider;
    private final Provider<DataSaverTile> mDataSaverTileProvider;
    private final Provider<DndTile> mDndTileProvider;
    private final Provider<FlashlightTile> mFlashlightTileProvider;
    private final Provider<HotspotTile> mHotspotTileProvider;
    private final Provider<LocationTile> mLocationTileProvider;
    private final Provider<GarbageMonitor.MemoryTile> mMemoryTileProvider;
    private final Provider<NfcTile> mNfcTileProvider;
    private final Provider<NightDisplayTile> mNightDisplayTileProvider;
    private final Lazy<QSHost> mQsHostLazy;
    private final Provider<RotationLockTile> mRotationLockTileProvider;
    private final Provider<ScreenRecordTile> mScreenRecordTileProvider;
    private final Provider<UiModeNightTile> mUiModeNightTileProvider;
    private final Provider<UserTile> mUserTileProvider;
    private final Provider<WifiTile> mWifiTileProvider;
    private final Provider<WorkModeTile> mWorkModeTileProvider;
    
    public QSFactoryImpl(final Lazy<QSHost> mQsHostLazy, final Provider<WifiTile> mWifiTileProvider, final Provider<BluetoothTile> mBluetoothTileProvider, final Provider<CellularTile> mCellularTileProvider, final Provider<DndTile> mDndTileProvider, final Provider<ColorInversionTile> mColorInversionTileProvider, final Provider<AirplaneModeTile> mAirplaneModeTileProvider, final Provider<WorkModeTile> mWorkModeTileProvider, final Provider<RotationLockTile> mRotationLockTileProvider, final Provider<FlashlightTile> mFlashlightTileProvider, final Provider<LocationTile> mLocationTileProvider, final Provider<CastTile> mCastTileProvider, final Provider<HotspotTile> mHotspotTileProvider, final Provider<UserTile> mUserTileProvider, final Provider<BatterySaverTile> mBatterySaverTileProvider, final Provider<DataSaverTile> mDataSaverTileProvider, final Provider<NightDisplayTile> mNightDisplayTileProvider, final Provider<NfcTile> mNfcTileProvider, final Provider<GarbageMonitor.MemoryTile> mMemoryTileProvider, final Provider<UiModeNightTile> mUiModeNightTileProvider, final Provider<ScreenRecordTile> mScreenRecordTileProvider) {
        this.mQsHostLazy = mQsHostLazy;
        this.mWifiTileProvider = mWifiTileProvider;
        this.mBluetoothTileProvider = mBluetoothTileProvider;
        this.mCellularTileProvider = mCellularTileProvider;
        this.mDndTileProvider = mDndTileProvider;
        this.mColorInversionTileProvider = mColorInversionTileProvider;
        this.mAirplaneModeTileProvider = mAirplaneModeTileProvider;
        this.mWorkModeTileProvider = mWorkModeTileProvider;
        this.mRotationLockTileProvider = mRotationLockTileProvider;
        this.mFlashlightTileProvider = mFlashlightTileProvider;
        this.mLocationTileProvider = mLocationTileProvider;
        this.mCastTileProvider = mCastTileProvider;
        this.mHotspotTileProvider = mHotspotTileProvider;
        this.mUserTileProvider = mUserTileProvider;
        this.mBatterySaverTileProvider = mBatterySaverTileProvider;
        this.mDataSaverTileProvider = mDataSaverTileProvider;
        this.mNightDisplayTileProvider = mNightDisplayTileProvider;
        this.mNfcTileProvider = mNfcTileProvider;
        this.mMemoryTileProvider = mMemoryTileProvider;
        this.mUiModeNightTileProvider = mUiModeNightTileProvider;
        this.mScreenRecordTileProvider = mScreenRecordTileProvider;
    }
    
    private QSTileImpl createTileInternal(final String str) {
        int n = 0;
        Label_0452: {
            switch (str.hashCode()) {
                case 1901043637: {
                    if (str.equals("location")) {
                        n = 9;
                        break Label_0452;
                    }
                    break;
                }
                case 1099603663: {
                    if (str.equals("hotspot")) {
                        n = 11;
                        break Label_0452;
                    }
                    break;
                }
                case 109211285: {
                    if (str.equals("saver")) {
                        n = 14;
                        break Label_0452;
                    }
                    break;
                }
                case 104817688: {
                    if (str.equals("night")) {
                        n = 15;
                        break Label_0452;
                    }
                    break;
                }
                case 3655441: {
                    if (str.equals("work")) {
                        n = 6;
                        break Label_0452;
                    }
                    break;
                }
                case 3649301: {
                    if (str.equals("wifi")) {
                        n = 0;
                        break Label_0452;
                    }
                    break;
                }
                case 3599307: {
                    if (str.equals("user")) {
                        n = 12;
                        break Label_0452;
                    }
                    break;
                }
                case 3075958: {
                    if (str.equals("dark")) {
                        n = 17;
                        break Label_0452;
                    }
                    break;
                }
                case 3049826: {
                    if (str.equals("cell")) {
                        n = 2;
                        break Label_0452;
                    }
                    break;
                }
                case 3046207: {
                    if (str.equals("cast")) {
                        n = 10;
                        break Label_0452;
                    }
                    break;
                }
                case 108971: {
                    if (str.equals("nfc")) {
                        n = 16;
                        break Label_0452;
                    }
                    break;
                }
                case 99610: {
                    if (str.equals("dnd")) {
                        n = 3;
                        break Label_0452;
                    }
                    break;
                }
                case 3154: {
                    if (str.equals("bt")) {
                        n = 1;
                        break Label_0452;
                    }
                    break;
                }
                case -40300674: {
                    if (str.equals("rotation")) {
                        n = 7;
                        break Label_0452;
                    }
                    break;
                }
                case -331239923: {
                    if (str.equals("battery")) {
                        n = 13;
                        break Label_0452;
                    }
                    break;
                }
                case -677011630: {
                    if (str.equals("airplane")) {
                        n = 5;
                        break Label_0452;
                    }
                    break;
                }
                case -805491779: {
                    if (str.equals("screenrecord")) {
                        n = 18;
                        break Label_0452;
                    }
                    break;
                }
                case -1183073498: {
                    if (str.equals("flashlight")) {
                        n = 8;
                        break Label_0452;
                    }
                    break;
                }
                case -2016941037: {
                    if (str.equals("inversion")) {
                        n = 4;
                        break Label_0452;
                    }
                    break;
                }
            }
            n = -1;
        }
        switch (n) {
            default: {
                if (str.startsWith("custom(")) {
                    return CustomTile.create(this.mQsHostLazy.get(), str, this.mQsHostLazy.get().getUserContext());
                }
                if (Build.IS_DEBUGGABLE && str.equals("dbg:mem")) {
                    return this.mMemoryTileProvider.get();
                }
                final StringBuilder sb = new StringBuilder();
                sb.append("No stock tile spec: ");
                sb.append(str);
                Log.w("QSFactory", sb.toString());
                return null;
            }
            case 18: {
                return this.mScreenRecordTileProvider.get();
            }
            case 17: {
                return this.mUiModeNightTileProvider.get();
            }
            case 16: {
                return this.mNfcTileProvider.get();
            }
            case 15: {
                return this.mNightDisplayTileProvider.get();
            }
            case 14: {
                return this.mDataSaverTileProvider.get();
            }
            case 13: {
                return this.mBatterySaverTileProvider.get();
            }
            case 12: {
                return this.mUserTileProvider.get();
            }
            case 11: {
                return this.mHotspotTileProvider.get();
            }
            case 10: {
                return this.mCastTileProvider.get();
            }
            case 9: {
                return this.mLocationTileProvider.get();
            }
            case 8: {
                return this.mFlashlightTileProvider.get();
            }
            case 7: {
                return this.mRotationLockTileProvider.get();
            }
            case 6: {
                return this.mWorkModeTileProvider.get();
            }
            case 5: {
                return this.mAirplaneModeTileProvider.get();
            }
            case 4: {
                return this.mColorInversionTileProvider.get();
            }
            case 3: {
                return this.mDndTileProvider.get();
            }
            case 2: {
                return this.mCellularTileProvider.get();
            }
            case 1: {
                return this.mBluetoothTileProvider.get();
            }
            case 0: {
                return this.mWifiTileProvider.get();
            }
        }
    }
    
    @Override
    public QSTile createTile(final String s) {
        final QSTileImpl tileInternal = this.createTileInternal(s);
        if (tileInternal != null) {
            tileInternal.handleStale();
        }
        return tileInternal;
    }
    
    @Override
    public QSTileView createTileView(final QSTile qsTile, final boolean b) {
        final ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(this.mQsHostLazy.get().getContext(), R$style.qs_theme);
        final QSIconView tileView = qsTile.createTileView((Context)contextThemeWrapper);
        if (b) {
            return new QSTileBaseView((Context)contextThemeWrapper, tileView, b);
        }
        return new com.android.systemui.qs.tileimpl.QSTileView((Context)contextThemeWrapper, tileView);
    }
}
