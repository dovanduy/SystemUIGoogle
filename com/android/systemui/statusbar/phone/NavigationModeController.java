// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import android.content.pm.PackageManager$NameNotFoundException;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.content.om.OverlayInfo;
import android.provider.Settings$Secure;
import android.content.res.ApkAssets;
import android.os.RemoteException;
import java.util.Collection;
import java.util.Arrays;
import android.os.Handler;
import android.os.UserHandle;
import android.content.IntentFilter;
import android.content.om.IOverlayManager$Stub;
import android.os.ServiceManager;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import android.util.Log;
import android.content.Intent;
import java.util.concurrent.Executor;
import android.util.SparseBooleanArray;
import android.content.BroadcastReceiver;
import android.content.om.IOverlayManager;
import java.util.ArrayList;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import android.content.Context;
import com.android.systemui.Dumpable;

public class NavigationModeController implements Dumpable
{
    private static final String TAG = "NavigationModeController";
    private final Context mContext;
    private Context mCurrentUserContext;
    private final DeviceProvisionedController.DeviceProvisionedListener mDeviceProvisionedCallback;
    private final DeviceProvisionedController mDeviceProvisionedController;
    private ArrayList<ModeChangedListener> mListeners;
    private final IOverlayManager mOverlayManager;
    private BroadcastReceiver mReceiver;
    private SparseBooleanArray mRestoreGesturalNavBarMode;
    private final Executor mUiBgExecutor;
    
    public NavigationModeController(final Context context, final DeviceProvisionedController mDeviceProvisionedController, final Executor mUiBgExecutor) {
        this.mRestoreGesturalNavBarMode = new SparseBooleanArray();
        this.mListeners = new ArrayList<ModeChangedListener>();
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                final String action = intent.getAction();
                int n = 0;
                Label_0033: {
                    if (action.hashCode() == -1946981856) {
                        if (action.equals("android.intent.action.OVERLAY_CHANGED")) {
                            n = 0;
                            break Label_0033;
                        }
                    }
                    n = -1;
                }
                if (n == 0) {
                    Log.d(NavigationModeController.TAG, "ACTION_OVERLAY_CHANGED");
                    NavigationModeController.this.updateCurrentInteractionMode(true);
                }
            }
        };
        this.mDeviceProvisionedCallback = new DeviceProvisionedController.DeviceProvisionedListener() {
            @Override
            public void onDeviceProvisionedChanged() {
                final String access$000 = NavigationModeController.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append("onDeviceProvisionedChanged: ");
                sb.append(NavigationModeController.this.mDeviceProvisionedController.isDeviceProvisioned());
                Log.d(access$000, sb.toString());
                NavigationModeController.this.restoreGesturalNavOverlayIfNecessary();
            }
            
            @Override
            public void onUserSetupChanged() {
                final String access$000 = NavigationModeController.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append("onUserSetupChanged: ");
                sb.append(NavigationModeController.this.mDeviceProvisionedController.isCurrentUserSetup());
                Log.d(access$000, sb.toString());
                NavigationModeController.this.restoreGesturalNavOverlayIfNecessary();
            }
            
            @Override
            public void onUserSwitched() {
                final String access$000 = NavigationModeController.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append("onUserSwitched: ");
                sb.append(ActivityManagerWrapper.getInstance().getCurrentUserId());
                Log.d(access$000, sb.toString());
                NavigationModeController.this.updateCurrentInteractionMode(true);
                NavigationModeController.this.deferGesturalNavOverlayIfNecessary();
            }
        };
        this.mContext = context;
        this.mCurrentUserContext = context;
        this.mOverlayManager = IOverlayManager$Stub.asInterface(ServiceManager.getService("overlay"));
        this.mUiBgExecutor = mUiBgExecutor;
        (this.mDeviceProvisionedController = mDeviceProvisionedController).addCallback(this.mDeviceProvisionedCallback);
        final IntentFilter intentFilter = new IntentFilter("android.intent.action.OVERLAY_CHANGED");
        intentFilter.addDataScheme("package");
        intentFilter.addDataSchemeSpecificPart("android", 0);
        this.mContext.registerReceiverAsUser(this.mReceiver, UserHandle.ALL, intentFilter, (String)null, (Handler)null);
        this.mContext.registerReceiverAsUser(this.mReceiver, UserHandle.ALL, new IntentFilter("android.intent.action.ACTION_PREFERRED_ACTIVITY_CHANGED"), (String)null, (Handler)null);
        this.updateCurrentInteractionMode(false);
        this.deferGesturalNavOverlayIfNecessary();
    }
    
    private void deferGesturalNavOverlayIfNecessary() {
        final int currentUser = this.mDeviceProvisionedController.getCurrentUser();
        this.mRestoreGesturalNavBarMode.put(currentUser, false);
        if (this.mDeviceProvisionedController.isDeviceProvisioned() && this.mDeviceProvisionedController.isCurrentUserSetup()) {
            Log.d(NavigationModeController.TAG, "deferGesturalNavOverlayIfNecessary: device is provisioned and user is setup");
            return;
        }
        final ArrayList obj = new ArrayList();
        try {
            obj.addAll(Arrays.asList(this.mOverlayManager.getDefaultOverlayPackages()));
        }
        catch (RemoteException ex) {
            Log.e(NavigationModeController.TAG, "deferGesturalNavOverlayIfNecessary: failed to fetch default overlays");
        }
        if (!obj.contains("com.android.internal.systemui.navbar.gestural")) {
            final String tag = NavigationModeController.TAG;
            final StringBuilder sb = new StringBuilder();
            sb.append("deferGesturalNavOverlayIfNecessary: no default gestural overlay, default=");
            sb.append(obj);
            Log.d(tag, sb.toString());
            return;
        }
        this.setModeOverlay("com.android.internal.systemui.navbar.threebutton", -2);
        this.mRestoreGesturalNavBarMode.put(currentUser, true);
        Log.d(NavigationModeController.TAG, "deferGesturalNavOverlayIfNecessary: setting to 3 button mode");
    }
    
    private void dumpAssetPaths(final Context context) {
        final String tag = NavigationModeController.TAG;
        final StringBuilder sb = new StringBuilder();
        sb.append("  contextUser=");
        sb.append(this.mCurrentUserContext.getUserId());
        Log.d(tag, sb.toString());
        Log.d(NavigationModeController.TAG, "  assetPaths=");
        for (final ApkAssets apkAssets2 : context.getResources().getAssets().getApkAssets()) {
            final String tag2 = NavigationModeController.TAG;
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("    ");
            sb2.append(apkAssets2.getAssetPath());
            Log.d(tag2, sb2.toString());
        }
    }
    
    private int getCurrentInteractionMode(final Context context) {
        final int integer = context.getResources().getInteger(17694849);
        final String tag = NavigationModeController.TAG;
        final StringBuilder sb = new StringBuilder();
        sb.append("getCurrentInteractionMode: mode=");
        sb.append(integer);
        sb.append(" contextUser=");
        sb.append(context.getUserId());
        Log.d(tag, sb.toString());
        return integer;
    }
    
    private void restoreGesturalNavOverlayIfNecessary() {
        final String tag = NavigationModeController.TAG;
        final StringBuilder sb = new StringBuilder();
        sb.append("restoreGesturalNavOverlayIfNecessary: needs restore=");
        sb.append(this.mRestoreGesturalNavBarMode);
        Log.d(tag, sb.toString());
        final int currentUser = this.mDeviceProvisionedController.getCurrentUser();
        if (this.mRestoreGesturalNavBarMode.get(currentUser)) {
            this.setGestureModeOverlayForMainLauncher();
            this.mRestoreGesturalNavBarMode.put(currentUser, false);
        }
    }
    
    private boolean setGestureModeOverlayForMainLauncher() {
        if (this.getCurrentInteractionMode(this.mCurrentUserContext) == 2) {
            return true;
        }
        final String tag = NavigationModeController.TAG;
        final StringBuilder sb = new StringBuilder();
        sb.append("Switching system navigation to full-gesture mode: contextUser=");
        sb.append(this.mCurrentUserContext.getUserId());
        Log.d(tag, sb.toString());
        this.setModeOverlay("com.android.internal.systemui.navbar.gestural", -2);
        return true;
    }
    
    private void switchToDefaultGestureNavOverlayIfNecessary() {
        final int userId = this.mCurrentUserContext.getUserId();
        try {
            final IOverlayManager mOverlayManager = this.mOverlayManager;
            final OverlayInfo overlayInfo = mOverlayManager.getOverlayInfo("com.android.internal.systemui.navbar.gestural", userId);
            if (overlayInfo != null && !overlayInfo.isEnabled()) {
                final int dimensionPixelSize = this.mCurrentUserContext.getResources().getDimensionPixelSize(17105053);
                mOverlayManager.setEnabledExclusiveInCategory("com.android.internal.systemui.navbar.gestural", userId);
                final int dimensionPixelSize2 = this.mCurrentUserContext.getResources().getDimensionPixelSize(17105053);
                float f;
                if (dimensionPixelSize2 == 0) {
                    f = 1.0f;
                }
                else {
                    f = dimensionPixelSize / (float)dimensionPixelSize2;
                }
                Settings$Secure.putFloat(this.mCurrentUserContext.getContentResolver(), "back_gesture_inset_scale_left", f);
                Settings$Secure.putFloat(this.mCurrentUserContext.getContentResolver(), "back_gesture_inset_scale_right", f);
                final String tag = NavigationModeController.TAG;
                final StringBuilder sb = new StringBuilder();
                sb.append("Moved back sensitivity for user ");
                sb.append(userId);
                sb.append(" to scale ");
                sb.append(f);
                Log.v(tag, sb.toString());
            }
        }
        catch (SecurityException | IllegalStateException | RemoteException ex) {
            final String tag2 = NavigationModeController.TAG;
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Failed to switch to default gesture nav overlay for user ");
            sb2.append(userId);
            Log.e(tag2, sb2.toString());
        }
    }
    
    public int addListener(final ModeChangedListener e) {
        this.mListeners.add(e);
        return this.getCurrentInteractionMode(this.mCurrentUserContext);
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("NavigationModeController:");
        final StringBuilder sb = new StringBuilder();
        sb.append("  mode=");
        sb.append(this.getCurrentInteractionMode(this.mCurrentUserContext));
        printWriter.println(sb.toString());
        String join;
        try {
            join = String.join(", ", (CharSequence[])this.mOverlayManager.getDefaultOverlayPackages());
        }
        catch (RemoteException ex) {
            join = "failed_to_fetch";
        }
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("  defaultOverlays=");
        sb2.append(join);
        printWriter.println(sb2.toString());
        printWriter.println("  restoreGesturalNavMode:");
        for (int i = 0; i < this.mRestoreGesturalNavBarMode.size(); ++i) {
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("    userId=");
            sb3.append(this.mRestoreGesturalNavBarMode.keyAt(i));
            sb3.append(" shouldRestore=");
            sb3.append(this.mRestoreGesturalNavBarMode.valueAt(i));
            printWriter.println(sb3.toString());
        }
        this.dumpAssetPaths(this.mCurrentUserContext);
    }
    
    public Context getCurrentUserContext() {
        final int currentUserId = ActivityManagerWrapper.getInstance().getCurrentUserId();
        final String tag = NavigationModeController.TAG;
        final StringBuilder sb = new StringBuilder();
        sb.append("getCurrentUserContext: contextUser=");
        sb.append(this.mContext.getUserId());
        sb.append(" currentUser=");
        sb.append(currentUserId);
        Log.d(tag, sb.toString());
        if (this.mContext.getUserId() == currentUserId) {
            return this.mContext;
        }
        try {
            return this.mContext.createPackageContextAsUser(this.mContext.getPackageName(), 0, UserHandle.of(currentUserId));
        }
        catch (PackageManager$NameNotFoundException ex) {
            Log.e(NavigationModeController.TAG, "Failed to create package context", (Throwable)ex);
            return null;
        }
    }
    
    public void removeListener(final ModeChangedListener o) {
        this.mListeners.remove(o);
    }
    
    public void setModeOverlay(final String s, final int n) {
        this.mUiBgExecutor.execute(new _$$Lambda$NavigationModeController$XNbfE14hTqTsqzjGfhml_ek2wAw(this, s, n));
    }
    
    public void updateCurrentInteractionMode(final boolean b) {
        final Context currentUserContext = this.getCurrentUserContext();
        this.mCurrentUserContext = currentUserContext;
        final int currentInteractionMode = this.getCurrentInteractionMode(currentUserContext);
        if (currentInteractionMode == 2) {
            this.switchToDefaultGestureNavOverlayIfNecessary();
        }
        this.mUiBgExecutor.execute(new _$$Lambda$NavigationModeController$Az4iHIVUWwUXS_IGosEIyzFux8w(this, currentInteractionMode));
        final String tag = NavigationModeController.TAG;
        final StringBuilder sb = new StringBuilder();
        sb.append("updateCurrentInteractionMode: mode=");
        sb.append(currentInteractionMode);
        Log.e(tag, sb.toString());
        this.dumpAssetPaths(this.mCurrentUserContext);
        if (b) {
            for (int i = 0; i < this.mListeners.size(); ++i) {
                this.mListeners.get(i).onNavigationModeChanged(currentInteractionMode);
            }
        }
    }
    
    public interface ModeChangedListener
    {
        void onNavigationModeChanged(final int p0);
    }
}
