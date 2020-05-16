// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import com.android.systemui.R$id;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.graphics.Point;
import android.os.Bundle;
import com.android.systemui.R$style;
import android.view.View;
import com.android.internal.annotations.VisibleForTesting;
import android.media.MediaRouter$Callback;
import com.android.systemui.statusbar.phone.NavigationBarView;
import android.view.WindowManager$InvalidDisplayException;
import android.content.DialogInterface$OnDismissListener;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.media.MediaRouter$RouteInfo;
import android.view.Display;
import com.android.systemui.Dependency;
import android.view.DisplayInfo;
import android.app.Presentation;
import android.util.SparseArray;
import com.android.systemui.statusbar.NavigationBarController;
import android.media.MediaRouter$SimpleCallback;
import android.media.MediaRouter;
import com.android.systemui.util.InjectionInflationController;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager$DisplayListener;
import android.content.Context;

public class KeyguardDisplayManager
{
    private static boolean DEBUG = false;
    private final Context mContext;
    private final DisplayManager$DisplayListener mDisplayListener;
    private final DisplayManager mDisplayService;
    private final InjectionInflationController mInjectableInflater;
    private final MediaRouter mMediaRouter;
    private final MediaRouter$SimpleCallback mMediaRouterCallback;
    private final NavigationBarController mNavBarController;
    private final SparseArray<Presentation> mPresentations;
    private boolean mShowing;
    private final DisplayInfo mTmpDisplayInfo;
    
    public KeyguardDisplayManager(final Context mContext, final InjectionInflationController mInjectableInflater) {
        this.mTmpDisplayInfo = new DisplayInfo();
        this.mPresentations = (SparseArray<Presentation>)new SparseArray();
        this.mNavBarController = Dependency.get(NavigationBarController.class);
        this.mDisplayListener = (DisplayManager$DisplayListener)new DisplayManager$DisplayListener() {
            public void onDisplayAdded(final int n) {
                final Display display = KeyguardDisplayManager.this.mDisplayService.getDisplay(n);
                if (KeyguardDisplayManager.this.mShowing) {
                    KeyguardDisplayManager.this.updateNavigationBarVisibility(n, false);
                    KeyguardDisplayManager.this.showPresentation(display);
                }
            }
            
            public void onDisplayChanged(final int n) {
                if (n == 0) {
                    return;
                }
                if (KeyguardDisplayManager.this.mPresentations.get(n) != null && KeyguardDisplayManager.this.mShowing) {
                    KeyguardDisplayManager.this.hidePresentation(n);
                    final Display display = KeyguardDisplayManager.this.mDisplayService.getDisplay(n);
                    if (display != null) {
                        KeyguardDisplayManager.this.showPresentation(display);
                    }
                }
            }
            
            public void onDisplayRemoved(final int n) {
                KeyguardDisplayManager.this.hidePresentation(n);
            }
        };
        this.mMediaRouterCallback = new MediaRouter$SimpleCallback() {
            public void onRoutePresentationDisplayChanged(final MediaRouter mediaRouter, final MediaRouter$RouteInfo obj) {
                if (KeyguardDisplayManager.DEBUG) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("onRoutePresentationDisplayChanged: info=");
                    sb.append(obj);
                    Log.d("KeyguardDisplayManager", sb.toString());
                }
                final KeyguardDisplayManager this$0 = KeyguardDisplayManager.this;
                this$0.updateDisplays(this$0.mShowing);
            }
            
            public void onRouteSelected(final MediaRouter mediaRouter, final int i, final MediaRouter$RouteInfo obj) {
                if (KeyguardDisplayManager.DEBUG) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("onRouteSelected: type=");
                    sb.append(i);
                    sb.append(", info=");
                    sb.append(obj);
                    Log.d("KeyguardDisplayManager", sb.toString());
                }
                final KeyguardDisplayManager this$0 = KeyguardDisplayManager.this;
                this$0.updateDisplays(this$0.mShowing);
            }
            
            public void onRouteUnselected(final MediaRouter mediaRouter, final int i, final MediaRouter$RouteInfo obj) {
                if (KeyguardDisplayManager.DEBUG) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("onRouteUnselected: type=");
                    sb.append(i);
                    sb.append(", info=");
                    sb.append(obj);
                    Log.d("KeyguardDisplayManager", sb.toString());
                }
                final KeyguardDisplayManager this$0 = KeyguardDisplayManager.this;
                this$0.updateDisplays(this$0.mShowing);
            }
        };
        this.mContext = mContext;
        this.mInjectableInflater = mInjectableInflater;
        this.mMediaRouter = (MediaRouter)mContext.getSystemService((Class)MediaRouter.class);
        (this.mDisplayService = (DisplayManager)this.mContext.getSystemService((Class)DisplayManager.class)).registerDisplayListener(this.mDisplayListener, (Handler)null);
    }
    
    private void hidePresentation(final int n) {
        final Presentation presentation = (Presentation)this.mPresentations.get(n);
        if (presentation != null) {
            presentation.dismiss();
            this.mPresentations.remove(n);
        }
    }
    
    private boolean isKeyguardShowable(final Display display) {
        if (display == null) {
            if (KeyguardDisplayManager.DEBUG) {
                Log.i("KeyguardDisplayManager", "Cannot show Keyguard on null display");
            }
            return false;
        }
        if (display.getDisplayId() == 0) {
            if (KeyguardDisplayManager.DEBUG) {
                Log.i("KeyguardDisplayManager", "Do not show KeyguardPresentation on the default display");
            }
            return false;
        }
        display.getDisplayInfo(this.mTmpDisplayInfo);
        if ((this.mTmpDisplayInfo.flags & 0x4) != 0x0) {
            if (KeyguardDisplayManager.DEBUG) {
                Log.i("KeyguardDisplayManager", "Do not show KeyguardPresentation on a private display");
            }
            return false;
        }
        return true;
    }
    
    private boolean showPresentation(final Display obj) {
        if (!this.isKeyguardShowable(obj)) {
            return false;
        }
        if (KeyguardDisplayManager.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Keyguard enabled on display: ");
            sb.append(obj);
            Log.i("KeyguardDisplayManager", sb.toString());
        }
        final int displayId = obj.getDisplayId();
        if (this.mPresentations.get(displayId) == null) {
            Presentation presentation = new KeyguardPresentation(this.mContext, obj, this.mInjectableInflater);
            presentation.setOnDismissListener((DialogInterface$OnDismissListener)new _$$Lambda$KeyguardDisplayManager$WcC7zwdycYHh9dpCnEiRCOObKEQ(this, presentation, displayId));
            try {
                presentation.show();
            }
            catch (WindowManager$InvalidDisplayException ex) {
                Log.w("KeyguardDisplayManager", "Invalid display:", (Throwable)ex);
                presentation = null;
            }
            if (presentation != null) {
                this.mPresentations.append(displayId, (Object)presentation);
                return true;
            }
        }
        return false;
    }
    
    private void updateNavigationBarVisibility(final int n, final boolean b) {
        if (n == 0) {
            return;
        }
        final NavigationBarView navigationBarView = this.mNavBarController.getNavigationBarView(n);
        if (navigationBarView == null) {
            return;
        }
        if (b) {
            navigationBarView.getRootView().setVisibility(0);
        }
        else {
            navigationBarView.getRootView().setVisibility(8);
        }
    }
    
    public void hide() {
        if (this.mShowing) {
            if (KeyguardDisplayManager.DEBUG) {
                Log.v("KeyguardDisplayManager", "hide");
            }
            this.mMediaRouter.removeCallback((MediaRouter$Callback)this.mMediaRouterCallback);
            this.updateDisplays(false);
        }
        this.mShowing = false;
    }
    
    public void show() {
        if (!this.mShowing) {
            if (KeyguardDisplayManager.DEBUG) {
                Log.v("KeyguardDisplayManager", "show");
            }
            this.mMediaRouter.addCallback(4, (MediaRouter$Callback)this.mMediaRouterCallback, 8);
            this.updateDisplays(true);
        }
        this.mShowing = true;
    }
    
    protected boolean updateDisplays(final boolean b) {
        boolean b2 = false;
        boolean b4;
        if (b) {
            final Display[] displays = this.mDisplayService.getDisplays();
            final int length = displays.length;
            boolean b3;
            int n = (b3 = false) ? 1 : 0;
            while (true) {
                b4 = b3;
                if (n >= length) {
                    break;
                }
                final Display display = displays[n];
                this.updateNavigationBarVisibility(display.getDisplayId(), false);
                b3 |= this.showPresentation(display);
                ++n;
            }
        }
        else {
            if (this.mPresentations.size() > 0) {
                b2 = true;
            }
            for (int i = this.mPresentations.size() - 1; i >= 0; --i) {
                this.updateNavigationBarVisibility(this.mPresentations.keyAt(i), true);
                ((Presentation)this.mPresentations.valueAt(i)).dismiss();
            }
            this.mPresentations.clear();
            b4 = b2;
        }
        return b4;
    }
    
    @VisibleForTesting
    static final class KeyguardPresentation extends Presentation
    {
        private View mClock;
        private final InjectionInflationController mInjectableInflater;
        private int mMarginLeft;
        private int mMarginTop;
        Runnable mMoveTextRunnable;
        private int mUsableHeight;
        private int mUsableWidth;
        
        KeyguardPresentation(final Context context, final Display display, final InjectionInflationController mInjectableInflater) {
            super(context, display, R$style.Theme_SystemUI_KeyguardPresentation);
            this.mMoveTextRunnable = new Runnable() {
                @Override
                public void run() {
                    final int access$700 = KeyguardPresentation.this.mMarginLeft;
                    final int n = (int)(Math.random() * (KeyguardPresentation.this.mUsableWidth - KeyguardPresentation.this.mClock.getWidth()));
                    final int access$701 = KeyguardPresentation.this.mMarginTop;
                    final int n2 = (int)(Math.random() * (KeyguardPresentation.this.mUsableHeight - KeyguardPresentation.this.mClock.getHeight()));
                    KeyguardPresentation.this.mClock.setTranslationX((float)(access$700 + n));
                    KeyguardPresentation.this.mClock.setTranslationY((float)(access$701 + n2));
                    KeyguardPresentation.this.mClock.postDelayed(KeyguardPresentation.this.mMoveTextRunnable, 10000L);
                }
            };
            this.mInjectableInflater = mInjectableInflater;
            this.getWindow().setType(2009);
            this.setCancelable(false);
        }
        
        public void cancel() {
        }
        
        protected void onCreate(final Bundle bundle) {
            super.onCreate(bundle);
            final Point point = new Point();
            this.getDisplay().getSize(point);
            final int x = point.x;
            this.mUsableWidth = x * 80 / 100;
            final int y = point.y;
            this.mUsableHeight = y * 80 / 100;
            this.mMarginLeft = x * 20 / 200;
            this.mMarginTop = y * 20 / 200;
            this.setContentView(this.mInjectableInflater.injectable(LayoutInflater.from(this.getContext())).inflate(R$layout.keyguard_presentation, (ViewGroup)null));
            this.getWindow().getDecorView().setSystemUiVisibility(1792);
            this.getWindow().getAttributes().setFitInsetsTypes(0);
            this.getWindow().setNavigationBarContrastEnforced(false);
            this.getWindow().setNavigationBarColor(0);
            (this.mClock = this.findViewById(R$id.clock)).post(this.mMoveTextRunnable);
        }
        
        public void onDetachedFromWindow() {
            this.mClock.removeCallbacks(this.mMoveTextRunnable);
        }
    }
}
