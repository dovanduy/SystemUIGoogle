// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui;

import android.util.Log;
import android.text.TextUtils;
import android.provider.Settings$Global;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import java.util.List;
import android.os.RemoteException;
import android.content.om.OverlayInfo;
import android.content.om.IOverlayManager$Stub;
import android.os.ServiceManager;
import android.os.Handler;
import android.content.om.IOverlayManager;
import android.database.ContentObserver;
import android.content.Context;

public class DisplayCutoutEmulationAdapter
{
    private final Context mContext;
    private final ContentObserver mObserver;
    private final IOverlayManager mOverlayManager;
    
    public DisplayCutoutEmulationAdapter(final Context mContext) {
        this.mObserver = new ContentObserver(Handler.getMain()) {
            public void onChange(final boolean b) {
                DisplayCutoutEmulationAdapter.this.update();
            }
        };
        this.mContext = mContext;
        this.mOverlayManager = IOverlayManager$Stub.asInterface(ServiceManager.getService("overlay"));
        this.register();
        this.update();
    }
    
    private OverlayInfo[] getOverlayInfos() {
        try {
            final List overlayInfosForTarget = this.mOverlayManager.getOverlayInfosForTarget("android", 0);
            for (int i = overlayInfosForTarget.size() - 1; i >= 0; --i) {
                if (!overlayInfosForTarget.get(i).packageName.startsWith("com.android.internal.display.cutout.emulation")) {
                    overlayInfosForTarget.remove(i);
                }
            }
            return overlayInfosForTarget.toArray(new OverlayInfo[overlayInfosForTarget.size()]);
        }
        catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }
    
    private SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(this.mContext);
    }
    
    private void register() {
        this.mContext.getContentResolver().registerContentObserver(Settings$Global.getUriFor("com.google.android.systemui.display_cutout_emulation"), false, this.mObserver, 0);
    }
    
    private void setEmulationOverlay(final String s) {
        final OverlayInfo[] overlayInfos = this.getOverlayInfos();
        final int length = overlayInfos.length;
        CharSequence packageName = null;
        for (final OverlayInfo overlayInfo : overlayInfos) {
            if (overlayInfo.isEnabled()) {
                packageName = overlayInfo.packageName;
            }
        }
        if ((TextUtils.isEmpty((CharSequence)s) && TextUtils.isEmpty(packageName)) || TextUtils.equals((CharSequence)s, packageName)) {
            return;
        }
        for (final OverlayInfo overlayInfo2 : overlayInfos) {
            final boolean enabled = overlayInfo2.isEnabled();
            final boolean equals = TextUtils.equals((CharSequence)overlayInfo2.packageName, (CharSequence)s);
            if (enabled != equals) {
                try {
                    this.mOverlayManager.setEnabled(overlayInfo2.packageName, equals, 0);
                }
                catch (RemoteException ex) {
                    throw ex.rethrowFromSystemServer();
                }
            }
        }
    }
    
    private void update() {
        final String stringForUser = Settings$Global.getStringForUser(this.mContext.getContentResolver(), "com.google.android.systemui.display_cutout_emulation", 0);
        if (stringForUser == null) {
            return;
        }
        final String[] split = stringForUser.split(":", 2);
        try {
            final int int1 = Integer.parseInt(split[0]);
            final String emulationOverlay = split[1];
            if (int1 <= this.getPrefs().getInt("com.google.android.systemui.display_cutout_emulation.VERSION", -1)) {
                return;
            }
            if (!emulationOverlay.isEmpty() && !emulationOverlay.startsWith("com.android.internal.display.cutout.emulation")) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Invalid overlay prefix: ");
                sb.append(stringForUser);
                Log.e("CutoutEmulationAdapter", sb.toString());
                return;
            }
            this.setEmulationOverlay(emulationOverlay);
            this.getPrefs().edit().putInt("com.google.android.systemui.display_cutout_emulation.VERSION", int1).apply();
        }
        catch (NumberFormatException | IndexOutOfBoundsException ex) {
            final Object o2;
            final Object o = o2;
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Invalid configuration: ");
            sb2.append(stringForUser);
            Log.e("CutoutEmulationAdapter", sb2.toString(), (Throwable)o);
        }
    }
}
