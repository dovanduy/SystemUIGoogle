// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui;

import com.android.systemui.statusbar.phone.ScrimState;
import android.app.WallpaperInfo;
import android.os.RemoteException;
import java.util.function.Supplier;
import com.android.systemui.DejankUtils;
import android.app.ActivityManager;
import com.android.systemui.statusbar.BlurUtils;
import com.android.systemui.dock.DockManager;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.keyguard.KeyguardUpdateMonitor;
import android.os.Handler;
import com.android.systemui.util.wakelock.DelayedWakeLock;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import android.app.AlarmManager;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.LightBarController;
import com.google.android.collect.Sets;
import android.app.IWallpaperManager;
import com.android.systemui.statusbar.phone.LockscreenWallpaper;
import android.content.ComponentName;
import android.util.ArraySet;
import com.android.systemui.statusbar.phone.ScrimController;

public class LiveWallpaperScrimController extends ScrimController
{
    private static ArraySet<ComponentName> REDUCED_SCRIM_WALLPAPERS;
    private int mCurrentUser;
    private final LockscreenWallpaper mLockscreenWallpaper;
    private final IWallpaperManager mWallpaperManager;
    
    static {
        LiveWallpaperScrimController.REDUCED_SCRIM_WALLPAPERS = (ArraySet<ComponentName>)Sets.newArraySet((Object[])new ComponentName[] { new ComponentName("com.breel.geswallpapers", "com.breel.geswallpapers.wallpapers.EarthWallpaperService"), new ComponentName("com.breel.wallpapers18", "com.breel.wallpapers18.delight.wallpapers.DelightWallpaperV1"), new ComponentName("com.breel.wallpapers18", "com.breel.wallpapers18.delight.wallpapers.DelightWallpaperV2"), new ComponentName("com.breel.wallpapers18", "com.breel.wallpapers18.delight.wallpapers.DelightWallpaperV3"), new ComponentName("com.breel.wallpapers18", "com.breel.wallpapers18.surfandturf.wallpapers.variations.SurfAndTurfWallpaperV2"), new ComponentName("com.breel.wallpapers18", "com.breel.wallpapers18.cities.wallpapers.variations.SanFranciscoWallpaper"), new ComponentName("com.breel.wallpapers18", "com.breel.wallpapers18.cities.wallpapers.variations.NewYorkWallpaper") });
    }
    
    public LiveWallpaperScrimController(final LightBarController lightBarController, final DozeParameters dozeParameters, final AlarmManager alarmManager, final KeyguardStateController keyguardStateController, final DelayedWakeLock.Builder builder, final Handler handler, final IWallpaperManager mWallpaperManager, final LockscreenWallpaper mLockscreenWallpaper, final KeyguardUpdateMonitor keyguardUpdateMonitor, final SysuiColorExtractor sysuiColorExtractor, final DockManager dockManager, final BlurUtils blurUtils) {
        super(lightBarController, dozeParameters, alarmManager, keyguardStateController, builder, handler, keyguardUpdateMonitor, sysuiColorExtractor, dockManager, blurUtils);
        this.mCurrentUser = ActivityManager.getCurrentUser();
        this.mWallpaperManager = mWallpaperManager;
        this.mLockscreenWallpaper = mLockscreenWallpaper;
    }
    
    private boolean isReducedScrimWallpaperSet() {
        return DejankUtils.whitelistIpcs((Supplier<Boolean>)new _$$Lambda$LiveWallpaperScrimController$VuRvuZxiMRs5DcX_Xoytnz_NbrQ(this));
    }
    
    private void updateScrimValues() {
        if (this.isReducedScrimWallpaperSet()) {
            this.setScrimBehindValues(0.25f);
        }
        else {
            this.setScrimBehindValues(0.2f);
        }
    }
    
    @Override
    public void setCurrentUser(final int mCurrentUser) {
        this.mCurrentUser = mCurrentUser;
        this.updateScrimValues();
    }
    
    @Override
    public void transitionTo(final ScrimState scrimState) {
        if (scrimState == ScrimState.KEYGUARD) {
            this.updateScrimValues();
        }
        super.transitionTo(scrimState);
    }
}
