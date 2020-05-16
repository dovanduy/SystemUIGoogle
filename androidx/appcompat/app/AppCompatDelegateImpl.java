// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.app;

import android.os.Parcel;
import android.os.Parcelable$ClassLoaderCreator;
import android.os.Parcelable$Creator;
import android.annotation.SuppressLint;
import android.os.Parcelable;
import androidx.appcompat.R$style;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.view.menu.ListMenuPresenter;
import androidx.appcompat.content.res.AppCompatResources;
import android.view.MotionEvent;
import android.os.LocaleList;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.PowerManager;
import androidx.appcompat.view.SupportActionModeWrapper;
import android.view.ActionMode$Callback;
import android.view.KeyboardShortcutGroup;
import java.util.List;
import androidx.appcompat.view.WindowCallbackWrapper;
import android.widget.FrameLayout$LayoutParams;
import android.view.ViewGroup$MarginLayoutParams;
import androidx.appcompat.view.StandaloneActionMode;
import androidx.appcompat.widget.ViewStubCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;
import androidx.core.widget.PopupWindowCompat;
import android.view.MenuItem;
import androidx.core.app.NavUtils;
import android.app.UiModeManager;
import androidx.core.view.LayoutInflaterCompat;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.core.view.KeyEventDispatcher;
import androidx.appcompat.widget.VectorEnabledTintResources;
import org.xmlpull.v1.XmlPullParser;
import androidx.core.content.ContextCompat;
import androidx.appcompat.R$color;
import android.content.res.Resources;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import android.util.DisplayMetrics;
import androidx.core.app.ActivityCompat;
import android.content.ContextWrapper;
import android.util.AndroidRuntimeException;
import android.os.Bundle;
import android.view.KeyCharacterMap;
import android.view.ViewParent;
import android.view.WindowManager$LayoutParams;
import android.view.ViewGroup$LayoutParams;
import android.view.WindowManager;
import android.view.Menu;
import android.media.AudioManager;
import android.view.ViewConfiguration;
import android.view.KeyEvent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager$NameNotFoundException;
import android.util.Log;
import android.content.ComponentName;
import android.content.res.Resources$Theme;
import androidx.appcompat.view.menu.MenuPresenter;
import androidx.core.util.ObjectsCompat;
import android.text.TextUtils;
import android.widget.FrameLayout;
import androidx.appcompat.widget.ViewUtils;
import androidx.appcompat.widget.FitWindowsViewGroup;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.appcompat.R$id;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.R$attr;
import android.util.TypedValue;
import androidx.appcompat.R$layout;
import android.view.LayoutInflater;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import androidx.appcompat.widget.TintTypedArray;
import android.view.Window$Callback;
import android.content.res.TypedArray;
import androidx.appcompat.R$styleable;
import androidx.appcompat.widget.ContentFrameLayout;
import androidx.appcompat.widget.AppCompatDrawableManager;
import android.app.Dialog;
import android.app.Activity;
import android.content.res.Resources$NotFoundException;
import android.os.Build;
import android.os.Build$VERSION;
import android.view.Window;
import android.widget.TextView;
import android.graphics.Rect;
import android.view.ViewGroup;
import android.view.View;
import android.view.MenuInflater;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.appcompat.widget.DecorContentParent;
import android.content.Context;
import androidx.appcompat.widget.ActionBarContextView;
import android.widget.PopupWindow;
import androidx.appcompat.view.ActionMode;
import androidx.collection.SimpleArrayMap;
import android.view.LayoutInflater$Factory2;
import androidx.appcompat.view.menu.MenuBuilder;

class AppCompatDelegateImpl extends AppCompatDelegate implements Callback, LayoutInflater$Factory2
{
    private static final boolean IS_PRE_LOLLIPOP;
    private static final boolean sCanApplyOverrideConfiguration;
    private static final boolean sCanReturnDifferentContext;
    private static boolean sInstalledExceptionHandler;
    private static final SimpleArrayMap<String, Integer> sLocalNightModes;
    private static final int[] sWindowBackgroundStyleable;
    ActionBar mActionBar;
    private ActionMenuPresenterCallback mActionMenuPresenterCallback;
    ActionMode mActionMode;
    PopupWindow mActionModePopup;
    ActionBarContextView mActionModeView;
    private boolean mActivityHandlesUiMode;
    private boolean mActivityHandlesUiModeChecked;
    final AppCompatCallback mAppCompatCallback;
    private AppCompatViewInflater mAppCompatViewInflater;
    private AppCompatWindowCallback mAppCompatWindowCallback;
    private AutoNightModeManager mAutoBatteryNightModeManager;
    private AutoNightModeManager mAutoTimeNightModeManager;
    private boolean mBaseContextAttached;
    private boolean mClosingActionMenu;
    final Context mContext;
    private boolean mCreated;
    private DecorContentParent mDecorContentParent;
    private boolean mEnableDefaultActionBarUp;
    ViewPropertyAnimatorCompat mFadeAnim;
    private boolean mFeatureIndeterminateProgress;
    private boolean mFeatureProgress;
    private boolean mHandleNativeActionModes;
    boolean mHasActionBar;
    final Object mHost;
    int mInvalidatePanelMenuFeatures;
    boolean mInvalidatePanelMenuPosted;
    private final Runnable mInvalidatePanelMenuRunnable;
    boolean mIsDestroyed;
    boolean mIsFloating;
    private int mLocalNightMode;
    private boolean mLongPressBackDown;
    MenuInflater mMenuInflater;
    boolean mOverlayActionBar;
    boolean mOverlayActionMode;
    private PanelMenuPresenterCallback mPanelMenuPresenterCallback;
    private PanelFeatureState[] mPanels;
    private PanelFeatureState mPreparedPanel;
    Runnable mShowActionModePopup;
    private boolean mStarted;
    private View mStatusGuard;
    ViewGroup mSubDecor;
    private boolean mSubDecorInstalled;
    private Rect mTempRect1;
    private Rect mTempRect2;
    private int mThemeResId;
    private CharSequence mTitle;
    private TextView mTitleView;
    Window mWindow;
    boolean mWindowNoTitle;
    
    static {
        final int sdk_INT = Build$VERSION.SDK_INT;
        sLocalNightModes = new SimpleArrayMap<String, Integer>();
        final boolean b = false;
        IS_PRE_LOLLIPOP = (sdk_INT < 21);
        sWindowBackgroundStyleable = new int[] { 16842836 };
        sCanReturnDifferentContext = ("robolectric".equals(Build.FINGERPRINT) ^ true);
        boolean sCanApplyOverrideConfiguration2 = b;
        if (sdk_INT >= 17) {
            sCanApplyOverrideConfiguration2 = true;
        }
        sCanApplyOverrideConfiguration = sCanApplyOverrideConfiguration2;
        if (AppCompatDelegateImpl.IS_PRE_LOLLIPOP && !AppCompatDelegateImpl.sInstalledExceptionHandler) {
            Thread.setDefaultUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new Thread.UncaughtExceptionHandler() {
                final /* synthetic */ UncaughtExceptionHandler val$defHandler;
                
                private boolean shouldWrapException(final Throwable t) {
                    final boolean b = t instanceof Resources$NotFoundException;
                    boolean b3;
                    final boolean b2 = b3 = false;
                    if (b) {
                        final String message = t.getMessage();
                        b3 = b2;
                        if (message != null) {
                            if (!message.contains("drawable")) {
                                b3 = b2;
                                if (!message.contains("Drawable")) {
                                    return b3;
                                }
                            }
                            b3 = true;
                        }
                    }
                    return b3;
                }
                
                @Override
                public void uncaughtException(final Thread thread, final Throwable t) {
                    if (this.shouldWrapException(t)) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append(t.getMessage());
                        sb.append(". If the resource you are trying to use is a vector resource, you may be referencing it in an unsupported way. See AppCompatDelegate.setCompatVectorFromResourcesEnabled() for more info.");
                        final Resources$NotFoundException ex = new Resources$NotFoundException(sb.toString());
                        ((Throwable)ex).initCause(t.getCause());
                        ((Throwable)ex).setStackTrace(t.getStackTrace());
                        this.val$defHandler.uncaughtException(thread, (Throwable)ex);
                    }
                    else {
                        this.val$defHandler.uncaughtException(thread, t);
                    }
                }
            });
            AppCompatDelegateImpl.sInstalledExceptionHandler = true;
        }
    }
    
    AppCompatDelegateImpl(final Activity activity, final AppCompatCallback appCompatCallback) {
        this((Context)activity, null, appCompatCallback, activity);
    }
    
    AppCompatDelegateImpl(final Dialog dialog, final AppCompatCallback appCompatCallback) {
        this(dialog.getContext(), dialog.getWindow(), appCompatCallback, dialog);
    }
    
    private AppCompatDelegateImpl(final Context mContext, final Window window, final AppCompatCallback mAppCompatCallback, final Object mHost) {
        this.mFadeAnim = null;
        this.mHandleNativeActionModes = true;
        this.mLocalNightMode = -100;
        this.mInvalidatePanelMenuRunnable = new Runnable() {
            @Override
            public void run() {
                final AppCompatDelegateImpl this$0 = AppCompatDelegateImpl.this;
                if ((this$0.mInvalidatePanelMenuFeatures & 0x1) != 0x0) {
                    this$0.doInvalidatePanelMenu(0);
                }
                final AppCompatDelegateImpl this$2 = AppCompatDelegateImpl.this;
                if ((this$2.mInvalidatePanelMenuFeatures & 0x1000) != 0x0) {
                    this$2.doInvalidatePanelMenu(108);
                }
                final AppCompatDelegateImpl this$3 = AppCompatDelegateImpl.this;
                this$3.mInvalidatePanelMenuPosted = false;
                this$3.mInvalidatePanelMenuFeatures = 0;
            }
        };
        this.mContext = mContext;
        this.mAppCompatCallback = mAppCompatCallback;
        this.mHost = mHost;
        if (this.mLocalNightMode == -100 && mHost instanceof Dialog) {
            final AppCompatActivity tryUnwrapContext = this.tryUnwrapContext();
            if (tryUnwrapContext != null) {
                this.mLocalNightMode = tryUnwrapContext.getDelegate().getLocalNightMode();
            }
        }
        if (this.mLocalNightMode == -100) {
            final Integer n = AppCompatDelegateImpl.sLocalNightModes.get(this.mHost.getClass().getName());
            if (n != null) {
                this.mLocalNightMode = n;
                AppCompatDelegateImpl.sLocalNightModes.remove(this.mHost.getClass().getName());
            }
        }
        if (window != null) {
            this.attachToWindow(window);
        }
        AppCompatDrawableManager.preload();
    }
    
    private boolean applyDayNight(final boolean b) {
        if (this.mIsDestroyed) {
            return false;
        }
        final int calculateNightMode = this.calculateNightMode();
        final boolean updateForNightMode = this.updateForNightMode(this.mapNightMode(this.mContext, calculateNightMode), b);
        if (calculateNightMode == 0) {
            this.getAutoTimeNightModeManager(this.mContext).setup();
        }
        else {
            final AutoNightModeManager mAutoTimeNightModeManager = this.mAutoTimeNightModeManager;
            if (mAutoTimeNightModeManager != null) {
                mAutoTimeNightModeManager.cleanup();
            }
        }
        if (calculateNightMode == 3) {
            this.getAutoBatteryNightModeManager(this.mContext).setup();
        }
        else {
            final AutoNightModeManager mAutoBatteryNightModeManager = this.mAutoBatteryNightModeManager;
            if (mAutoBatteryNightModeManager != null) {
                mAutoBatteryNightModeManager.cleanup();
            }
        }
        return updateForNightMode;
    }
    
    private void applyFixedSizeWindow() {
        final ContentFrameLayout contentFrameLayout = (ContentFrameLayout)this.mSubDecor.findViewById(16908290);
        final View decorView = this.mWindow.getDecorView();
        contentFrameLayout.setDecorPadding(decorView.getPaddingLeft(), decorView.getPaddingTop(), decorView.getPaddingRight(), decorView.getPaddingBottom());
        final TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(R$styleable.AppCompatTheme);
        obtainStyledAttributes.getValue(R$styleable.AppCompatTheme_windowMinWidthMajor, contentFrameLayout.getMinWidthMajor());
        obtainStyledAttributes.getValue(R$styleable.AppCompatTheme_windowMinWidthMinor, contentFrameLayout.getMinWidthMinor());
        if (obtainStyledAttributes.hasValue(R$styleable.AppCompatTheme_windowFixedWidthMajor)) {
            obtainStyledAttributes.getValue(R$styleable.AppCompatTheme_windowFixedWidthMajor, contentFrameLayout.getFixedWidthMajor());
        }
        if (obtainStyledAttributes.hasValue(R$styleable.AppCompatTheme_windowFixedWidthMinor)) {
            obtainStyledAttributes.getValue(R$styleable.AppCompatTheme_windowFixedWidthMinor, contentFrameLayout.getFixedWidthMinor());
        }
        if (obtainStyledAttributes.hasValue(R$styleable.AppCompatTheme_windowFixedHeightMajor)) {
            obtainStyledAttributes.getValue(R$styleable.AppCompatTheme_windowFixedHeightMajor, contentFrameLayout.getFixedHeightMajor());
        }
        if (obtainStyledAttributes.hasValue(R$styleable.AppCompatTheme_windowFixedHeightMinor)) {
            obtainStyledAttributes.getValue(R$styleable.AppCompatTheme_windowFixedHeightMinor, contentFrameLayout.getFixedHeightMinor());
        }
        obtainStyledAttributes.recycle();
        contentFrameLayout.requestLayout();
    }
    
    private void attachToWindow(final Window mWindow) {
        if (this.mWindow != null) {
            throw new IllegalStateException("AppCompat has already installed itself into the Window");
        }
        final Window$Callback callback = mWindow.getCallback();
        if (!(callback instanceof AppCompatWindowCallback)) {
            mWindow.setCallback((Window$Callback)(this.mAppCompatWindowCallback = new AppCompatWindowCallback(callback)));
            final TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(this.mContext, null, AppCompatDelegateImpl.sWindowBackgroundStyleable);
            final Drawable drawableIfKnown = obtainStyledAttributes.getDrawableIfKnown(0);
            if (drawableIfKnown != null) {
                mWindow.setBackgroundDrawable(drawableIfKnown);
            }
            obtainStyledAttributes.recycle();
            this.mWindow = mWindow;
            return;
        }
        throw new IllegalStateException("AppCompat has already installed itself into the Window");
    }
    
    private int calculateNightMode() {
        int n = this.mLocalNightMode;
        if (n == -100) {
            n = AppCompatDelegate.getDefaultNightMode();
        }
        return n;
    }
    
    private void cleanupAutoManagers() {
        final AutoNightModeManager mAutoTimeNightModeManager = this.mAutoTimeNightModeManager;
        if (mAutoTimeNightModeManager != null) {
            mAutoTimeNightModeManager.cleanup();
        }
        final AutoNightModeManager mAutoBatteryNightModeManager = this.mAutoBatteryNightModeManager;
        if (mAutoBatteryNightModeManager != null) {
            mAutoBatteryNightModeManager.cleanup();
        }
    }
    
    private Configuration createOverrideConfigurationForDayNight(final Context context, int n, final Configuration to) {
        if (n != 1) {
            if (n != 2) {
                n = (context.getApplicationContext().getResources().getConfiguration().uiMode & 0x30);
            }
            else {
                n = 32;
            }
        }
        else {
            n = 16;
        }
        final Configuration configuration = new Configuration();
        configuration.fontScale = 0.0f;
        if (to != null) {
            configuration.setTo(to);
        }
        configuration.uiMode = (n | (configuration.uiMode & 0xFFFFFFCF));
        return configuration;
    }
    
    private ViewGroup createSubDecor() {
        final TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(R$styleable.AppCompatTheme);
        if (!obtainStyledAttributes.hasValue(R$styleable.AppCompatTheme_windowActionBar)) {
            obtainStyledAttributes.recycle();
            throw new IllegalStateException("You need to use a Theme.AppCompat theme (or descendant) with this activity.");
        }
        if (obtainStyledAttributes.getBoolean(R$styleable.AppCompatTheme_windowNoTitle, false)) {
            this.requestWindowFeature(1);
        }
        else if (obtainStyledAttributes.getBoolean(R$styleable.AppCompatTheme_windowActionBar, false)) {
            this.requestWindowFeature(108);
        }
        if (obtainStyledAttributes.getBoolean(R$styleable.AppCompatTheme_windowActionBarOverlay, false)) {
            this.requestWindowFeature(109);
        }
        if (obtainStyledAttributes.getBoolean(R$styleable.AppCompatTheme_windowActionModeOverlay, false)) {
            this.requestWindowFeature(10);
        }
        this.mIsFloating = obtainStyledAttributes.getBoolean(R$styleable.AppCompatTheme_android_windowIsFloating, false);
        obtainStyledAttributes.recycle();
        this.ensureWindow();
        this.mWindow.getDecorView();
        final LayoutInflater from = LayoutInflater.from(this.mContext);
        ViewGroup contentView;
        if (!this.mWindowNoTitle) {
            if (this.mIsFloating) {
                contentView = (ViewGroup)from.inflate(R$layout.abc_dialog_title_material, (ViewGroup)null);
                this.mOverlayActionBar = false;
                this.mHasActionBar = false;
            }
            else if (this.mHasActionBar) {
                final TypedValue typedValue = new TypedValue();
                this.mContext.getTheme().resolveAttribute(R$attr.actionBarTheme, typedValue, true);
                Object mContext;
                if (typedValue.resourceId != 0) {
                    mContext = new ContextThemeWrapper(this.mContext, typedValue.resourceId);
                }
                else {
                    mContext = this.mContext;
                }
                final ViewGroup viewGroup = (ViewGroup)LayoutInflater.from((Context)mContext).inflate(R$layout.abc_screen_toolbar, (ViewGroup)null);
                (this.mDecorContentParent = (DecorContentParent)viewGroup.findViewById(R$id.decor_content_parent)).setWindowCallback(this.getWindowCallback());
                if (this.mOverlayActionBar) {
                    this.mDecorContentParent.initFeature(109);
                }
                if (this.mFeatureProgress) {
                    this.mDecorContentParent.initFeature(2);
                }
                contentView = viewGroup;
                if (this.mFeatureIndeterminateProgress) {
                    this.mDecorContentParent.initFeature(5);
                    contentView = viewGroup;
                }
            }
            else {
                contentView = null;
            }
        }
        else if (this.mOverlayActionMode) {
            contentView = (ViewGroup)from.inflate(R$layout.abc_screen_simple_overlay_action_mode, (ViewGroup)null);
        }
        else {
            contentView = (ViewGroup)from.inflate(R$layout.abc_screen_simple, (ViewGroup)null);
        }
        if (contentView != null) {
            if (Build$VERSION.SDK_INT >= 21) {
                ViewCompat.setOnApplyWindowInsetsListener((View)contentView, new OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsetsCompat onApplyWindowInsets(final View view, final WindowInsetsCompat windowInsetsCompat) {
                        final int systemWindowInsetTop = windowInsetsCompat.getSystemWindowInsetTop();
                        final int updateStatusGuard = AppCompatDelegateImpl.this.updateStatusGuard(windowInsetsCompat, null);
                        WindowInsetsCompat replaceSystemWindowInsets = windowInsetsCompat;
                        if (systemWindowInsetTop != updateStatusGuard) {
                            replaceSystemWindowInsets = windowInsetsCompat.replaceSystemWindowInsets(windowInsetsCompat.getSystemWindowInsetLeft(), updateStatusGuard, windowInsetsCompat.getSystemWindowInsetRight(), windowInsetsCompat.getSystemWindowInsetBottom());
                        }
                        return ViewCompat.onApplyWindowInsets(view, replaceSystemWindowInsets);
                    }
                });
            }
            else if (contentView instanceof FitWindowsViewGroup) {
                ((FitWindowsViewGroup)contentView).setOnFitSystemWindowsListener((FitWindowsViewGroup.OnFitSystemWindowsListener)new FitWindowsViewGroup.OnFitSystemWindowsListener() {
                    @Override
                    public void onFitSystemWindows(final Rect rect) {
                        rect.top = AppCompatDelegateImpl.this.updateStatusGuard(null, rect);
                    }
                });
            }
            if (this.mDecorContentParent == null) {
                this.mTitleView = (TextView)contentView.findViewById(R$id.title);
            }
            ViewUtils.makeOptionalFitsSystemWindows((View)contentView);
            final ContentFrameLayout contentFrameLayout = (ContentFrameLayout)contentView.findViewById(R$id.action_bar_activity_content);
            final ViewGroup viewGroup2 = (ViewGroup)this.mWindow.findViewById(16908290);
            if (viewGroup2 != null) {
                while (viewGroup2.getChildCount() > 0) {
                    final View child = viewGroup2.getChildAt(0);
                    viewGroup2.removeViewAt(0);
                    contentFrameLayout.addView(child);
                }
                viewGroup2.setId(-1);
                contentFrameLayout.setId(16908290);
                if (viewGroup2 instanceof FrameLayout) {
                    ((FrameLayout)viewGroup2).setForeground((Drawable)null);
                }
            }
            this.mWindow.setContentView((View)contentView);
            contentFrameLayout.setAttachListener((ContentFrameLayout.OnAttachListener)new ContentFrameLayout.OnAttachListener() {
                @Override
                public void onAttachedFromWindow() {
                }
                
                @Override
                public void onDetachedFromWindow() {
                    AppCompatDelegateImpl.this.dismissPopups();
                }
            });
            return contentView;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("AppCompat does not support the current theme features: { windowActionBar: ");
        sb.append(this.mHasActionBar);
        sb.append(", windowActionBarOverlay: ");
        sb.append(this.mOverlayActionBar);
        sb.append(", android:windowIsFloating: ");
        sb.append(this.mIsFloating);
        sb.append(", windowActionModeOverlay: ");
        sb.append(this.mOverlayActionMode);
        sb.append(", windowNoTitle: ");
        sb.append(this.mWindowNoTitle);
        sb.append(" }");
        throw new IllegalArgumentException(sb.toString());
    }
    
    private void ensureSubDecor() {
        if (!this.mSubDecorInstalled) {
            this.mSubDecor = this.createSubDecor();
            final CharSequence title = this.getTitle();
            if (!TextUtils.isEmpty(title)) {
                final DecorContentParent mDecorContentParent = this.mDecorContentParent;
                if (mDecorContentParent != null) {
                    mDecorContentParent.setWindowTitle(title);
                }
                else if (this.peekSupportActionBar() != null) {
                    this.peekSupportActionBar().setWindowTitle(title);
                }
                else {
                    final TextView mTitleView = this.mTitleView;
                    if (mTitleView != null) {
                        mTitleView.setText(title);
                    }
                }
            }
            this.applyFixedSizeWindow();
            this.onSubDecorInstalled(this.mSubDecor);
            this.mSubDecorInstalled = true;
            final PanelFeatureState panelState = this.getPanelState(0, false);
            if (!this.mIsDestroyed && (panelState == null || panelState.menu == null)) {
                this.invalidatePanelMenu(108);
            }
        }
    }
    
    private void ensureWindow() {
        if (this.mWindow == null) {
            final Object mHost = this.mHost;
            if (mHost instanceof Activity) {
                this.attachToWindow(((Activity)mHost).getWindow());
            }
        }
        if (this.mWindow != null) {
            return;
        }
        throw new IllegalStateException("We have not been given a Window");
    }
    
    private static Configuration generateConfigDelta(final Configuration configuration, final Configuration configuration2) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        final Configuration configuration3 = new Configuration();
        configuration3.fontScale = 0.0f;
        if (configuration2 != null) {
            if (configuration.diff(configuration2) != 0) {
                final float fontScale = configuration.fontScale;
                final float fontScale2 = configuration2.fontScale;
                if (fontScale != fontScale2) {
                    configuration3.fontScale = fontScale2;
                }
                final int mcc = configuration.mcc;
                final int mcc2 = configuration2.mcc;
                if (mcc != mcc2) {
                    configuration3.mcc = mcc2;
                }
                final int mnc = configuration.mnc;
                final int mnc2 = configuration2.mnc;
                if (mnc != mnc2) {
                    configuration3.mnc = mnc2;
                }
                if (sdk_INT >= 24) {
                    ConfigurationImplApi24.generateConfigDelta_locale(configuration, configuration2, configuration3);
                }
                else if (!ObjectsCompat.equals(configuration.locale, configuration2.locale)) {
                    configuration3.locale = configuration2.locale;
                }
                final int touchscreen = configuration.touchscreen;
                final int touchscreen2 = configuration2.touchscreen;
                if (touchscreen != touchscreen2) {
                    configuration3.touchscreen = touchscreen2;
                }
                final int keyboard = configuration.keyboard;
                final int keyboard2 = configuration2.keyboard;
                if (keyboard != keyboard2) {
                    configuration3.keyboard = keyboard2;
                }
                final int keyboardHidden = configuration.keyboardHidden;
                final int keyboardHidden2 = configuration2.keyboardHidden;
                if (keyboardHidden != keyboardHidden2) {
                    configuration3.keyboardHidden = keyboardHidden2;
                }
                final int navigation = configuration.navigation;
                final int navigation2 = configuration2.navigation;
                if (navigation != navigation2) {
                    configuration3.navigation = navigation2;
                }
                final int navigationHidden = configuration.navigationHidden;
                final int navigationHidden2 = configuration2.navigationHidden;
                if (navigationHidden != navigationHidden2) {
                    configuration3.navigationHidden = navigationHidden2;
                }
                final int orientation = configuration.orientation;
                final int orientation2 = configuration2.orientation;
                if (orientation != orientation2) {
                    configuration3.orientation = orientation2;
                }
                final int screenLayout = configuration.screenLayout;
                final int screenLayout2 = configuration2.screenLayout;
                if ((screenLayout & 0xF) != (screenLayout2 & 0xF)) {
                    configuration3.screenLayout |= (screenLayout2 & 0xF);
                }
                final int screenLayout3 = configuration.screenLayout;
                final int screenLayout4 = configuration2.screenLayout;
                if ((screenLayout3 & 0xC0) != (screenLayout4 & 0xC0)) {
                    configuration3.screenLayout |= (screenLayout4 & 0xC0);
                }
                final int screenLayout5 = configuration.screenLayout;
                final int screenLayout6 = configuration2.screenLayout;
                if ((screenLayout5 & 0x30) != (screenLayout6 & 0x30)) {
                    configuration3.screenLayout |= (screenLayout6 & 0x30);
                }
                final int screenLayout7 = configuration.screenLayout;
                final int screenLayout8 = configuration2.screenLayout;
                if ((screenLayout7 & 0x300) != (screenLayout8 & 0x300)) {
                    configuration3.screenLayout |= (screenLayout8 & 0x300);
                }
                if (sdk_INT >= 26) {
                    ConfigurationImplApi26.generateConfigDelta_colorMode(configuration, configuration2, configuration3);
                }
                final int uiMode = configuration.uiMode;
                final int uiMode2 = configuration2.uiMode;
                if ((uiMode & 0xF) != (uiMode2 & 0xF)) {
                    configuration3.uiMode |= (uiMode2 & 0xF);
                }
                final int uiMode3 = configuration.uiMode;
                final int uiMode4 = configuration2.uiMode;
                if ((uiMode3 & 0x30) != (uiMode4 & 0x30)) {
                    configuration3.uiMode |= (uiMode4 & 0x30);
                }
                final int screenWidthDp = configuration.screenWidthDp;
                final int screenWidthDp2 = configuration2.screenWidthDp;
                if (screenWidthDp != screenWidthDp2) {
                    configuration3.screenWidthDp = screenWidthDp2;
                }
                final int screenHeightDp = configuration.screenHeightDp;
                final int screenHeightDp2 = configuration2.screenHeightDp;
                if (screenHeightDp != screenHeightDp2) {
                    configuration3.screenHeightDp = screenHeightDp2;
                }
                final int smallestScreenWidthDp = configuration.smallestScreenWidthDp;
                final int smallestScreenWidthDp2 = configuration2.smallestScreenWidthDp;
                if (smallestScreenWidthDp != smallestScreenWidthDp2) {
                    configuration3.smallestScreenWidthDp = smallestScreenWidthDp2;
                }
                if (sdk_INT >= 17) {
                    ConfigurationImplApi17.generateConfigDelta_densityDpi(configuration, configuration2, configuration3);
                }
            }
        }
        return configuration3;
    }
    
    private AutoNightModeManager getAutoBatteryNightModeManager(final Context context) {
        if (this.mAutoBatteryNightModeManager == null) {
            this.mAutoBatteryNightModeManager = (AutoNightModeManager)new AutoBatteryNightModeManager(context);
        }
        return this.mAutoBatteryNightModeManager;
    }
    
    private AutoNightModeManager getAutoTimeNightModeManager(final Context context) {
        if (this.mAutoTimeNightModeManager == null) {
            this.mAutoTimeNightModeManager = (AutoNightModeManager)new AutoTimeNightModeManager(TwilightManager.getInstance(context));
        }
        return this.mAutoTimeNightModeManager;
    }
    
    private void initWindowDecorActionBar() {
        this.ensureSubDecor();
        if (this.mHasActionBar) {
            if (this.mActionBar == null) {
                final Object mHost = this.mHost;
                if (mHost instanceof Activity) {
                    this.mActionBar = new WindowDecorActionBar((Activity)this.mHost, this.mOverlayActionBar);
                }
                else if (mHost instanceof Dialog) {
                    this.mActionBar = new WindowDecorActionBar((Dialog)this.mHost);
                }
                final ActionBar mActionBar = this.mActionBar;
                if (mActionBar != null) {
                    mActionBar.setDefaultDisplayHomeAsUpEnabled(this.mEnableDefaultActionBarUp);
                }
            }
        }
    }
    
    private boolean initializePanelContent(final PanelFeatureState panelFeatureState) {
        final View createdPanelView = panelFeatureState.createdPanelView;
        boolean b = true;
        if (createdPanelView != null) {
            panelFeatureState.shownPanelView = createdPanelView;
            return true;
        }
        if (panelFeatureState.menu == null) {
            return false;
        }
        if (this.mPanelMenuPresenterCallback == null) {
            this.mPanelMenuPresenterCallback = new PanelMenuPresenterCallback();
        }
        if ((panelFeatureState.shownPanelView = (View)panelFeatureState.getListMenuView(this.mPanelMenuPresenterCallback)) == null) {
            b = false;
        }
        return b;
    }
    
    private boolean initializePanelDecor(final PanelFeatureState panelFeatureState) {
        panelFeatureState.setStyle(this.getActionBarThemedContext());
        panelFeatureState.decorView = (ViewGroup)new ListMenuDecorView(panelFeatureState.listPresenterContext);
        panelFeatureState.gravity = 81;
        return true;
    }
    
    private boolean initializePanelMenu(final PanelFeatureState panelFeatureState) {
        final Context mContext = this.mContext;
        final int featureId = panelFeatureState.featureId;
        Object o = null;
        Label_0202: {
            if (featureId != 0) {
                o = mContext;
                if (featureId != 108) {
                    break Label_0202;
                }
            }
            o = mContext;
            if (this.mDecorContentParent != null) {
                final TypedValue typedValue = new TypedValue();
                final Resources$Theme theme = mContext.getTheme();
                theme.resolveAttribute(R$attr.actionBarTheme, typedValue, true);
                Resources$Theme theme2 = null;
                if (typedValue.resourceId != 0) {
                    theme2 = mContext.getResources().newTheme();
                    theme2.setTo(theme);
                    theme2.applyStyle(typedValue.resourceId, true);
                    theme2.resolveAttribute(R$attr.actionBarWidgetTheme, typedValue, true);
                }
                else {
                    theme.resolveAttribute(R$attr.actionBarWidgetTheme, typedValue, true);
                }
                Resources$Theme theme3 = theme2;
                if (typedValue.resourceId != 0) {
                    if ((theme3 = theme2) == null) {
                        theme3 = mContext.getResources().newTheme();
                        theme3.setTo(theme);
                    }
                    theme3.applyStyle(typedValue.resourceId, true);
                }
                o = mContext;
                if (theme3 != null) {
                    o = new ContextThemeWrapper(mContext, 0);
                    ((Context)o).getTheme().setTo(theme3);
                }
            }
        }
        final MenuBuilder menu = new MenuBuilder((Context)o);
        menu.setCallback((MenuBuilder.Callback)this);
        panelFeatureState.setMenu(menu);
        return true;
    }
    
    private void invalidatePanelMenu(final int n) {
        this.mInvalidatePanelMenuFeatures |= 1 << n;
        if (!this.mInvalidatePanelMenuPosted) {
            ViewCompat.postOnAnimation(this.mWindow.getDecorView(), this.mInvalidatePanelMenuRunnable);
            this.mInvalidatePanelMenuPosted = true;
        }
    }
    
    private boolean isActivityManifestHandlingUiMode() {
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (!this.mActivityHandlesUiModeChecked && this.mHost instanceof Activity) {
            final PackageManager packageManager = this.mContext.getPackageManager();
            if (packageManager == null) {
                return false;
            }
            int n;
            if (sdk_INT >= 29) {
                n = 269221888;
            }
            else if (sdk_INT >= 24) {
                n = 786432;
            }
            else {
                n = 0;
            }
            try {
                final ActivityInfo activityInfo = packageManager.getActivityInfo(new ComponentName(this.mContext, (Class)this.mHost.getClass()), n);
                this.mActivityHandlesUiMode = (activityInfo != null && (activityInfo.configChanges & 0x200) != 0x0);
            }
            catch (PackageManager$NameNotFoundException ex) {
                Log.d("AppCompatDelegate", "Exception while getting ActivityInfo", (Throwable)ex);
                this.mActivityHandlesUiMode = false;
            }
        }
        this.mActivityHandlesUiModeChecked = true;
        return this.mActivityHandlesUiMode;
    }
    
    private boolean onKeyDownPanel(final int n, final KeyEvent keyEvent) {
        if (keyEvent.getRepeatCount() == 0) {
            final PanelFeatureState panelState = this.getPanelState(n, true);
            if (!panelState.isOpen) {
                return this.preparePanel(panelState, keyEvent);
            }
        }
        return false;
    }
    
    private boolean onKeyUpPanel(final int n, final KeyEvent keyEvent) {
        if (this.mActionMode != null) {
            return false;
        }
        final boolean b = true;
        final PanelFeatureState panelState = this.getPanelState(n, true);
        boolean b2 = false;
        Label_0208: {
            Label_0188: {
                if (n == 0) {
                    final DecorContentParent mDecorContentParent = this.mDecorContentParent;
                    if (mDecorContentParent != null && mDecorContentParent.canShowOverflowMenu() && !ViewConfiguration.get(this.mContext).hasPermanentMenuKey()) {
                        if (this.mDecorContentParent.isOverflowMenuShowing()) {
                            b2 = this.mDecorContentParent.hideOverflowMenu();
                            break Label_0208;
                        }
                        if (!this.mIsDestroyed && this.preparePanel(panelState, keyEvent)) {
                            b2 = this.mDecorContentParent.showOverflowMenu();
                            break Label_0208;
                        }
                        break Label_0188;
                    }
                }
                if (panelState.isOpen || panelState.isHandled) {
                    b2 = panelState.isOpen;
                    this.closePanel(panelState, true);
                    break Label_0208;
                }
                if (panelState.isPrepared) {
                    boolean preparePanel;
                    if (panelState.refreshMenuContent) {
                        panelState.isPrepared = false;
                        preparePanel = this.preparePanel(panelState, keyEvent);
                    }
                    else {
                        preparePanel = true;
                    }
                    if (preparePanel) {
                        this.openPanel(panelState, keyEvent);
                        b2 = b;
                        break Label_0208;
                    }
                }
            }
            b2 = false;
        }
        if (b2) {
            final AudioManager audioManager = (AudioManager)this.mContext.getApplicationContext().getSystemService("audio");
            if (audioManager != null) {
                audioManager.playSoundEffect(0);
            }
            else {
                Log.w("AppCompatDelegate", "Couldn't get audio manager");
            }
        }
        return b2;
    }
    
    private void openPanel(final PanelFeatureState panelFeatureState, final KeyEvent keyEvent) {
        if (!panelFeatureState.isOpen) {
            if (!this.mIsDestroyed) {
                if (panelFeatureState.featureId == 0 && (this.mContext.getResources().getConfiguration().screenLayout & 0xF) == 0x4) {
                    return;
                }
                final Window$Callback windowCallback = this.getWindowCallback();
                if (windowCallback != null && !windowCallback.onMenuOpened(panelFeatureState.featureId, (Menu)panelFeatureState.menu)) {
                    this.closePanel(panelFeatureState, true);
                    return;
                }
                final WindowManager windowManager = (WindowManager)this.mContext.getSystemService("window");
                if (windowManager == null) {
                    return;
                }
                if (!this.preparePanel(panelFeatureState, keyEvent)) {
                    return;
                }
                int n = 0;
                Label_0339: {
                    if (panelFeatureState.decorView != null && !panelFeatureState.refreshDecorView) {
                        final View createdPanelView = panelFeatureState.createdPanelView;
                        if (createdPanelView != null) {
                            final ViewGroup$LayoutParams layoutParams = createdPanelView.getLayoutParams();
                            if (layoutParams != null && layoutParams.width == -1) {
                                n = -1;
                                break Label_0339;
                            }
                        }
                    }
                    else {
                        final ViewGroup decorView = panelFeatureState.decorView;
                        if (decorView == null) {
                            if (!this.initializePanelDecor(panelFeatureState) || panelFeatureState.decorView == null) {
                                return;
                            }
                        }
                        else if (panelFeatureState.refreshDecorView && decorView.getChildCount() > 0) {
                            panelFeatureState.decorView.removeAllViews();
                        }
                        if (!this.initializePanelContent(panelFeatureState) || !panelFeatureState.hasPanelItems()) {
                            panelFeatureState.refreshDecorView = true;
                            return;
                        }
                        ViewGroup$LayoutParams layoutParams2;
                        if ((layoutParams2 = panelFeatureState.shownPanelView.getLayoutParams()) == null) {
                            layoutParams2 = new ViewGroup$LayoutParams(-2, -2);
                        }
                        panelFeatureState.decorView.setBackgroundResource(panelFeatureState.background);
                        final ViewParent parent = panelFeatureState.shownPanelView.getParent();
                        if (parent instanceof ViewGroup) {
                            ((ViewGroup)parent).removeView(panelFeatureState.shownPanelView);
                        }
                        panelFeatureState.decorView.addView(panelFeatureState.shownPanelView, layoutParams2);
                        if (!panelFeatureState.shownPanelView.hasFocus()) {
                            panelFeatureState.shownPanelView.requestFocus();
                        }
                    }
                    n = -2;
                }
                panelFeatureState.isHandled = false;
                final WindowManager$LayoutParams windowManager$LayoutParams = new WindowManager$LayoutParams(n, -2, panelFeatureState.x, panelFeatureState.y, 1002, 8519680, -3);
                windowManager$LayoutParams.gravity = panelFeatureState.gravity;
                windowManager$LayoutParams.windowAnimations = panelFeatureState.windowAnimations;
                windowManager.addView((View)panelFeatureState.decorView, (ViewGroup$LayoutParams)windowManager$LayoutParams);
                panelFeatureState.isOpen = true;
            }
        }
    }
    
    private boolean performPanelShortcut(final PanelFeatureState panelFeatureState, final int n, final KeyEvent keyEvent, final int n2) {
        final boolean system = keyEvent.isSystem();
        final boolean b = false;
        if (system) {
            return false;
        }
        boolean performShortcut = false;
        Label_0062: {
            if (!panelFeatureState.isPrepared) {
                performShortcut = b;
                if (!this.preparePanel(panelFeatureState, keyEvent)) {
                    break Label_0062;
                }
            }
            final MenuBuilder menu = panelFeatureState.menu;
            performShortcut = b;
            if (menu != null) {
                performShortcut = menu.performShortcut(n, keyEvent, n2);
            }
        }
        if (performShortcut && (n2 & 0x1) == 0x0 && this.mDecorContentParent == null) {
            this.closePanel(panelFeatureState, true);
        }
        return performShortcut;
    }
    
    private boolean preparePanel(final PanelFeatureState mPreparedPanel, final KeyEvent keyEvent) {
        if (this.mIsDestroyed) {
            return false;
        }
        if (mPreparedPanel.isPrepared) {
            return true;
        }
        final PanelFeatureState mPreparedPanel2 = this.mPreparedPanel;
        if (mPreparedPanel2 != null && mPreparedPanel2 != mPreparedPanel) {
            this.closePanel(mPreparedPanel2, false);
        }
        final Window$Callback windowCallback = this.getWindowCallback();
        if (windowCallback != null) {
            mPreparedPanel.createdPanelView = windowCallback.onCreatePanelView(mPreparedPanel.featureId);
        }
        final int featureId = mPreparedPanel.featureId;
        final boolean b = featureId == 0 || featureId == 108;
        if (b) {
            final DecorContentParent mDecorContentParent = this.mDecorContentParent;
            if (mDecorContentParent != null) {
                mDecorContentParent.setMenuPrepared();
            }
        }
        if (mPreparedPanel.createdPanelView == null) {
            if (b) {
                this.peekSupportActionBar();
            }
            if (mPreparedPanel.menu == null || mPreparedPanel.refreshMenuContent) {
                if (mPreparedPanel.menu == null && (!this.initializePanelMenu(mPreparedPanel) || mPreparedPanel.menu == null)) {
                    return false;
                }
                if (b && this.mDecorContentParent != null) {
                    if (this.mActionMenuPresenterCallback == null) {
                        this.mActionMenuPresenterCallback = new ActionMenuPresenterCallback();
                    }
                    this.mDecorContentParent.setMenu((Menu)mPreparedPanel.menu, this.mActionMenuPresenterCallback);
                }
                mPreparedPanel.menu.stopDispatchingItemsChanged();
                if (!windowCallback.onCreatePanelMenu(mPreparedPanel.featureId, (Menu)mPreparedPanel.menu)) {
                    mPreparedPanel.setMenu(null);
                    if (b) {
                        final DecorContentParent mDecorContentParent2 = this.mDecorContentParent;
                        if (mDecorContentParent2 != null) {
                            mDecorContentParent2.setMenu(null, this.mActionMenuPresenterCallback);
                        }
                    }
                    return false;
                }
                mPreparedPanel.refreshMenuContent = false;
            }
            mPreparedPanel.menu.stopDispatchingItemsChanged();
            final Bundle frozenActionViewState = mPreparedPanel.frozenActionViewState;
            if (frozenActionViewState != null) {
                mPreparedPanel.menu.restoreActionViewStates(frozenActionViewState);
                mPreparedPanel.frozenActionViewState = null;
            }
            if (!windowCallback.onPreparePanel(0, mPreparedPanel.createdPanelView, (Menu)mPreparedPanel.menu)) {
                if (b) {
                    final DecorContentParent mDecorContentParent3 = this.mDecorContentParent;
                    if (mDecorContentParent3 != null) {
                        mDecorContentParent3.setMenu(null, this.mActionMenuPresenterCallback);
                    }
                }
                mPreparedPanel.menu.startDispatchingItemsChanged();
                return false;
            }
            int deviceId;
            if (keyEvent != null) {
                deviceId = keyEvent.getDeviceId();
            }
            else {
                deviceId = -1;
            }
            final boolean b2 = KeyCharacterMap.load(deviceId).getKeyboardType() != 1;
            mPreparedPanel.qwertyMode = b2;
            mPreparedPanel.menu.setQwertyMode(b2);
            mPreparedPanel.menu.startDispatchingItemsChanged();
        }
        mPreparedPanel.isPrepared = true;
        mPreparedPanel.isHandled = false;
        this.mPreparedPanel = mPreparedPanel;
        return true;
    }
    
    private void reopenMenu(final boolean b) {
        final DecorContentParent mDecorContentParent = this.mDecorContentParent;
        if (mDecorContentParent != null && mDecorContentParent.canShowOverflowMenu() && (!ViewConfiguration.get(this.mContext).hasPermanentMenuKey() || this.mDecorContentParent.isOverflowMenuShowPending())) {
            final Window$Callback windowCallback = this.getWindowCallback();
            if (this.mDecorContentParent.isOverflowMenuShowing() && b) {
                this.mDecorContentParent.hideOverflowMenu();
                if (!this.mIsDestroyed) {
                    windowCallback.onPanelClosed(108, (Menu)this.getPanelState(0, true).menu);
                }
            }
            else if (windowCallback != null && !this.mIsDestroyed) {
                if (this.mInvalidatePanelMenuPosted && (this.mInvalidatePanelMenuFeatures & 0x1) != 0x0) {
                    this.mWindow.getDecorView().removeCallbacks(this.mInvalidatePanelMenuRunnable);
                    this.mInvalidatePanelMenuRunnable.run();
                }
                final PanelFeatureState panelState = this.getPanelState(0, true);
                final MenuBuilder menu = panelState.menu;
                if (menu != null && !panelState.refreshMenuContent && windowCallback.onPreparePanel(0, panelState.createdPanelView, (Menu)menu)) {
                    windowCallback.onMenuOpened(108, (Menu)panelState.menu);
                    this.mDecorContentParent.showOverflowMenu();
                }
            }
            return;
        }
        final PanelFeatureState panelState2 = this.getPanelState(0, true);
        panelState2.refreshDecorView = true;
        this.closePanel(panelState2, false);
        this.openPanel(panelState2, null);
    }
    
    private int sanitizeWindowFeatureId(final int n) {
        if (n == 8) {
            Log.i("AppCompatDelegate", "You should now use the AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR id when requesting this feature.");
            return 108;
        }
        if (n == 9) {
            Log.i("AppCompatDelegate", "You should now use the AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY id when requesting this feature.");
            return 109;
        }
        return n;
    }
    
    private boolean shouldInheritContext(ViewParent parent) {
        if (parent == null) {
            return false;
        }
        final View decorView = this.mWindow.getDecorView();
        while (parent != null) {
            if (parent == decorView || !(parent instanceof View) || ViewCompat.isAttachedToWindow((View)parent)) {
                return false;
            }
            parent = parent.getParent();
        }
        return true;
    }
    
    private void throwFeatureRequestIfSubDecorInstalled() {
        if (!this.mSubDecorInstalled) {
            return;
        }
        throw new AndroidRuntimeException("Window feature must be requested before adding content");
    }
    
    private AppCompatActivity tryUnwrapContext() {
        for (Context context = this.mContext; context != null; context = ((ContextWrapper)context).getBaseContext()) {
            if (context instanceof AppCompatActivity) {
                return (AppCompatActivity)context;
            }
            if (!(context instanceof ContextWrapper)) {
                break;
            }
        }
        return null;
    }
    
    private boolean updateForNightMode(final int n, final boolean b) {
        final Configuration overrideConfigurationForDayNight = this.createOverrideConfigurationForDayNight(this.mContext, n, null);
        final boolean activityManifestHandlingUiMode = this.isActivityManifestHandlingUiMode();
        final int n2 = this.mContext.getResources().getConfiguration().uiMode & 0x30;
        final int n3 = overrideConfigurationForDayNight.uiMode & 0x30;
        final boolean b2 = true;
        boolean b3 = false;
        Label_0122: {
            if (n2 != n3 && b && !activityManifestHandlingUiMode && this.mBaseContextAttached && (AppCompatDelegateImpl.sCanReturnDifferentContext || this.mCreated)) {
                final Object mHost = this.mHost;
                if (mHost instanceof Activity && !((Activity)mHost).isChild()) {
                    ActivityCompat.recreate((Activity)this.mHost);
                    b3 = true;
                    break Label_0122;
                }
            }
            b3 = false;
        }
        if (!b3 && n2 != n3) {
            this.updateResourcesConfigurationForNightMode(n3, activityManifestHandlingUiMode, null);
            b3 = b2;
        }
        if (b3) {
            final Object mHost2 = this.mHost;
            if (mHost2 instanceof AppCompatActivity) {
                ((AppCompatActivity)mHost2).onNightModeChanged(n);
            }
        }
        return b3;
    }
    
    private void updateResourcesConfigurationForNightMode(int mThemeResId, final boolean b, final Configuration configuration) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        final Resources resources = this.mContext.getResources();
        final Configuration configuration2 = new Configuration(resources.getConfiguration());
        if (configuration != null) {
            configuration2.updateFrom(configuration);
        }
        configuration2.uiMode = (mThemeResId | (resources.getConfiguration().uiMode & 0xFFFFFFCF));
        resources.updateConfiguration(configuration2, (DisplayMetrics)null);
        if (sdk_INT < 26) {
            ResourcesFlusher.flush(resources);
        }
        mThemeResId = this.mThemeResId;
        if (mThemeResId != 0) {
            this.mContext.setTheme(mThemeResId);
            if (sdk_INT >= 23) {
                this.mContext.getTheme().applyStyle(this.mThemeResId, true);
            }
        }
        if (b) {
            final Object mHost = this.mHost;
            if (mHost instanceof Activity) {
                final Activity activity = (Activity)mHost;
                if (activity instanceof LifecycleOwner) {
                    if (((LifecycleOwner)activity).getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                        activity.onConfigurationChanged(configuration2);
                    }
                }
                else if (this.mStarted) {
                    activity.onConfigurationChanged(configuration2);
                }
            }
        }
    }
    
    private void updateStatusGuardColor(final View view) {
        int backgroundColor;
        if ((ViewCompat.getWindowSystemUiVisibility(view) & 0x2000) != 0x0) {
            backgroundColor = ContextCompat.getColor(this.mContext, R$color.abc_decor_view_status_guard_light);
        }
        else {
            backgroundColor = ContextCompat.getColor(this.mContext, R$color.abc_decor_view_status_guard);
        }
        view.setBackgroundColor(backgroundColor);
    }
    
    @Override
    public void addContentView(final View view, final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        this.ensureSubDecor();
        ((ViewGroup)this.mSubDecor.findViewById(16908290)).addView(view, viewGroup$LayoutParams);
        this.mAppCompatWindowCallback.getWrapped().onContentChanged();
    }
    
    public boolean applyDayNight() {
        return this.applyDayNight(true);
    }
    
    @Override
    public Context attachBaseContext2(final Context p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: istore_2       
        //     2: aload_0        
        //     3: iconst_1       
        //     4: putfield        androidx/appcompat/app/AppCompatDelegateImpl.mBaseContextAttached:Z
        //     7: aload_0        
        //     8: aload_1        
        //     9: aload_0        
        //    10: invokespecial   androidx/appcompat/app/AppCompatDelegateImpl.calculateNightMode:()I
        //    13: invokevirtual   androidx/appcompat/app/AppCompatDelegateImpl.mapNightMode:(Landroid/content/Context;I)I
        //    16: istore_3       
        //    17: getstatic       androidx/appcompat/app/AppCompatDelegateImpl.sCanApplyOverrideConfiguration:Z
        //    20: istore          4
        //    22: aconst_null    
        //    23: astore          5
        //    25: iload           4
        //    27: ifeq            57
        //    30: aload_1        
        //    31: instanceof      Landroid/view/ContextThemeWrapper;
        //    34: ifeq            57
        //    37: aload_0        
        //    38: aload_1        
        //    39: iload_3        
        //    40: aconst_null    
        //    41: invokespecial   androidx/appcompat/app/AppCompatDelegateImpl.createOverrideConfigurationForDayNight:(Landroid/content/Context;ILandroid/content/res/Configuration;)Landroid/content/res/Configuration;
        //    44: astore          6
        //    46: aload_1        
        //    47: checkcast       Landroid/view/ContextThemeWrapper;
        //    50: aload           6
        //    52: invokestatic    androidx/appcompat/app/AppCompatDelegateImpl$ContextThemeWrapperCompatApi17Impl.applyOverrideConfiguration:(Landroid/view/ContextThemeWrapper;Landroid/content/res/Configuration;)V
        //    55: aload_1        
        //    56: areturn        
        //    57: aload_1        
        //    58: instanceof      Landroidx/appcompat/view/ContextThemeWrapper;
        //    61: ifeq            84
        //    64: aload_0        
        //    65: aload_1        
        //    66: iload_3        
        //    67: aconst_null    
        //    68: invokespecial   androidx/appcompat/app/AppCompatDelegateImpl.createOverrideConfigurationForDayNight:(Landroid/content/Context;ILandroid/content/res/Configuration;)Landroid/content/res/Configuration;
        //    71: astore          6
        //    73: aload_1        
        //    74: checkcast       Landroidx/appcompat/view/ContextThemeWrapper;
        //    77: aload           6
        //    79: invokevirtual   androidx/appcompat/view/ContextThemeWrapper.applyOverrideConfiguration:(Landroid/content/res/Configuration;)V
        //    82: aload_1        
        //    83: areturn        
        //    84: getstatic       androidx/appcompat/app/AppCompatDelegateImpl.sCanReturnDifferentContext:Z
        //    87: ifne            98
        //    90: aload_0        
        //    91: aload_1        
        //    92: invokespecial   androidx/appcompat/app/AppCompatDelegate.attachBaseContext2:(Landroid/content/Context;)Landroid/content/Context;
        //    95: pop            
        //    96: aload_1        
        //    97: areturn        
        //    98: aload_1        
        //    99: invokevirtual   android/content/Context.getPackageManager:()Landroid/content/pm/PackageManager;
        //   102: aload_1        
        //   103: invokevirtual   android/content/Context.getApplicationInfo:()Landroid/content/pm/ApplicationInfo;
        //   106: invokevirtual   android/content/pm/PackageManager.getResourcesForApplication:(Landroid/content/pm/ApplicationInfo;)Landroid/content/res/Resources;
        //   109: invokevirtual   android/content/res/Resources.getConfiguration:()Landroid/content/res/Configuration;
        //   112: astore          6
        //   114: aload_1        
        //   115: invokevirtual   android/content/Context.getResources:()Landroid/content/res/Resources;
        //   118: invokevirtual   android/content/res/Resources.getConfiguration:()Landroid/content/res/Configuration;
        //   121: astore          7
        //   123: aload           6
        //   125: aload           7
        //   127: invokevirtual   android/content/res/Configuration.equals:(Landroid/content/res/Configuration;)Z
        //   130: ifne            142
        //   133: aload           6
        //   135: aload           7
        //   137: invokestatic    androidx/appcompat/app/AppCompatDelegateImpl.generateConfigDelta:(Landroid/content/res/Configuration;Landroid/content/res/Configuration;)Landroid/content/res/Configuration;
        //   140: astore          5
        //   142: aload_0        
        //   143: aload_1        
        //   144: iload_3        
        //   145: aload           5
        //   147: invokespecial   androidx/appcompat/app/AppCompatDelegateImpl.createOverrideConfigurationForDayNight:(Landroid/content/Context;ILandroid/content/res/Configuration;)Landroid/content/res/Configuration;
        //   150: astore          6
        //   152: new             Landroidx/appcompat/view/ContextThemeWrapper;
        //   155: dup            
        //   156: aload_1        
        //   157: getstatic       androidx/appcompat/R$style.Theme_AppCompat_Empty:I
        //   160: invokespecial   androidx/appcompat/view/ContextThemeWrapper.<init>:(Landroid/content/Context;I)V
        //   163: astore          5
        //   165: aload           5
        //   167: aload           6
        //   169: invokevirtual   androidx/appcompat/view/ContextThemeWrapper.applyOverrideConfiguration:(Landroid/content/res/Configuration;)V
        //   172: iconst_0       
        //   173: istore_3       
        //   174: aload_1        
        //   175: invokevirtual   android/content/Context.getTheme:()Landroid/content/res/Resources$Theme;
        //   178: astore_1       
        //   179: aload_1        
        //   180: ifnull          186
        //   183: goto            188
        //   186: iconst_0       
        //   187: istore_2       
        //   188: iload_2        
        //   189: ifeq            200
        //   192: aload           5
        //   194: invokevirtual   androidx/appcompat/view/ContextThemeWrapper.getTheme:()Landroid/content/res/Resources$Theme;
        //   197: invokestatic    androidx/core/content/res/ResourcesCompat$ThemeCompat.rebase:(Landroid/content/res/Resources$Theme;)V
        //   200: aload_0        
        //   201: aload           5
        //   203: invokespecial   androidx/appcompat/app/AppCompatDelegate.attachBaseContext2:(Landroid/content/Context;)Landroid/content/Context;
        //   206: pop            
        //   207: aload           5
        //   209: areturn        
        //   210: astore_1       
        //   211: new             Ljava/lang/RuntimeException;
        //   214: dup            
        //   215: ldc_w           "Application failed to obtain resources from itself"
        //   218: aload_1        
        //   219: invokespecial   java/lang/RuntimeException.<init>:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   222: athrow         
        //   223: astore          6
        //   225: goto            57
        //   228: astore          6
        //   230: goto            84
        //   233: astore_1       
        //   234: iload_3        
        //   235: istore_2       
        //   236: goto            188
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                                     
        //  -----  -----  -----  -----  ---------------------------------------------------------
        //  46     55     223    228    Ljava/lang/IllegalStateException;
        //  73     82     228    233    Ljava/lang/IllegalStateException;
        //  98     114    210    223    Landroid/content/pm/PackageManager$NameNotFoundException;
        //  174    179    233    239    Ljava/lang/NullPointerException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 125 out of bounds for length 125
        //     at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
        //     at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
        //     at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:248)
        //     at java.base/java.util.Objects.checkIndex(Objects.java:372)
        //     at java.base/java.util.ArrayList.get(ArrayList.java:458)
        //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3321)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:113)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:211)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    void callOnPanelClosed(final int n, final PanelFeatureState panelFeatureState, final Menu menu) {
        PanelFeatureState panelFeatureState2 = panelFeatureState;
        Object menu2 = menu;
        if (menu == null) {
            PanelFeatureState panelFeatureState3;
            if ((panelFeatureState3 = panelFeatureState) == null) {
                panelFeatureState3 = panelFeatureState;
                if (n >= 0) {
                    final PanelFeatureState[] mPanels = this.mPanels;
                    panelFeatureState3 = panelFeatureState;
                    if (n < mPanels.length) {
                        panelFeatureState3 = mPanels[n];
                    }
                }
            }
            panelFeatureState2 = panelFeatureState3;
            menu2 = menu;
            if (panelFeatureState3 != null) {
                menu2 = panelFeatureState3.menu;
                panelFeatureState2 = panelFeatureState3;
            }
        }
        if (panelFeatureState2 != null && !panelFeatureState2.isOpen) {
            return;
        }
        if (!this.mIsDestroyed) {
            this.mAppCompatWindowCallback.getWrapped().onPanelClosed(n, (Menu)menu2);
        }
    }
    
    void checkCloseActionMenu(final MenuBuilder menuBuilder) {
        if (this.mClosingActionMenu) {
            return;
        }
        this.mClosingActionMenu = true;
        this.mDecorContentParent.dismissPopups();
        final Window$Callback windowCallback = this.getWindowCallback();
        if (windowCallback != null && !this.mIsDestroyed) {
            windowCallback.onPanelClosed(108, (Menu)menuBuilder);
        }
        this.mClosingActionMenu = false;
    }
    
    void closePanel(final int n) {
        this.closePanel(this.getPanelState(n, true), true);
    }
    
    void closePanel(final PanelFeatureState panelFeatureState, final boolean b) {
        if (b && panelFeatureState.featureId == 0) {
            final DecorContentParent mDecorContentParent = this.mDecorContentParent;
            if (mDecorContentParent != null && mDecorContentParent.isOverflowMenuShowing()) {
                this.checkCloseActionMenu(panelFeatureState.menu);
                return;
            }
        }
        final WindowManager windowManager = (WindowManager)this.mContext.getSystemService("window");
        if (windowManager != null && panelFeatureState.isOpen) {
            final ViewGroup decorView = panelFeatureState.decorView;
            if (decorView != null) {
                windowManager.removeView((View)decorView);
                if (b) {
                    this.callOnPanelClosed(panelFeatureState.featureId, panelFeatureState, null);
                }
            }
        }
        panelFeatureState.isPrepared = false;
        panelFeatureState.isHandled = false;
        panelFeatureState.isOpen = false;
        panelFeatureState.shownPanelView = null;
        panelFeatureState.refreshDecorView = true;
        if (this.mPreparedPanel == panelFeatureState) {
            this.mPreparedPanel = null;
        }
    }
    
    public View createView(final View view, final String s, final Context context, final AttributeSet set) {
        final AppCompatViewInflater mAppCompatViewInflater = this.mAppCompatViewInflater;
        final boolean b = false;
        if (mAppCompatViewInflater == null) {
            final String string = this.mContext.obtainStyledAttributes(R$styleable.AppCompatTheme).getString(R$styleable.AppCompatTheme_viewInflaterClass);
            if (string == null) {
                this.mAppCompatViewInflater = new AppCompatViewInflater();
            }
            else {
                try {
                    this.mAppCompatViewInflater = (AppCompatViewInflater)Class.forName(string).getDeclaredConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                }
                finally {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Failed to instantiate custom view inflater ");
                    sb.append(string);
                    sb.append(". Falling back to default.");
                    final Throwable t;
                    Log.i("AppCompatDelegate", sb.toString(), t);
                    this.mAppCompatViewInflater = new AppCompatViewInflater();
                }
            }
        }
        boolean shouldInheritContext = b;
        if (AppCompatDelegateImpl.IS_PRE_LOLLIPOP) {
            if (set instanceof XmlPullParser) {
                shouldInheritContext = b;
                if (((XmlPullParser)set).getDepth() > 1) {
                    shouldInheritContext = true;
                }
            }
            else {
                shouldInheritContext = this.shouldInheritContext((ViewParent)view);
            }
        }
        return this.mAppCompatViewInflater.createView(view, s, context, set, shouldInheritContext, AppCompatDelegateImpl.IS_PRE_LOLLIPOP, true, VectorEnabledTintResources.shouldBeUsed());
    }
    
    void dismissPopups() {
        final DecorContentParent mDecorContentParent = this.mDecorContentParent;
        if (mDecorContentParent != null) {
            mDecorContentParent.dismissPopups();
        }
        Label_0059: {
            if (this.mActionModePopup == null) {
                break Label_0059;
            }
            this.mWindow.getDecorView().removeCallbacks(this.mShowActionModePopup);
            while (true) {
                if (!this.mActionModePopup.isShowing()) {
                    break Label_0054;
                }
                try {
                    this.mActionModePopup.dismiss();
                    this.mActionModePopup = null;
                    this.endOnGoingFadeAnimation();
                    final PanelFeatureState panelState = this.getPanelState(0, false);
                    if (panelState != null) {
                        final MenuBuilder menu = panelState.menu;
                        if (menu != null) {
                            menu.close();
                        }
                    }
                }
                catch (IllegalArgumentException ex) {
                    continue;
                }
                break;
            }
        }
    }
    
    boolean dispatchKeyEvent(final KeyEvent keyEvent) {
        final Object mHost = this.mHost;
        final boolean b = mHost instanceof KeyEventDispatcher.Component;
        boolean b2 = true;
        if (b || mHost instanceof AppCompatDialog) {
            final View decorView = this.mWindow.getDecorView();
            if (decorView != null && KeyEventDispatcher.dispatchBeforeHierarchy(decorView, keyEvent)) {
                return true;
            }
        }
        if (keyEvent.getKeyCode() == 82 && this.mAppCompatWindowCallback.getWrapped().dispatchKeyEvent(keyEvent)) {
            return true;
        }
        final int keyCode = keyEvent.getKeyCode();
        if (keyEvent.getAction() != 0) {
            b2 = false;
        }
        boolean b3;
        if (b2) {
            b3 = this.onKeyDown(keyCode, keyEvent);
        }
        else {
            b3 = this.onKeyUp(keyCode, keyEvent);
        }
        return b3;
    }
    
    void doInvalidatePanelMenu(final int n) {
        final PanelFeatureState panelState = this.getPanelState(n, true);
        if (panelState.menu != null) {
            final Bundle frozenActionViewState = new Bundle();
            panelState.menu.saveActionViewStates(frozenActionViewState);
            if (frozenActionViewState.size() > 0) {
                panelState.frozenActionViewState = frozenActionViewState;
            }
            panelState.menu.stopDispatchingItemsChanged();
            panelState.menu.clear();
        }
        panelState.refreshMenuContent = true;
        panelState.refreshDecorView = true;
        if ((n == 108 || n == 0) && this.mDecorContentParent != null) {
            final PanelFeatureState panelState2 = this.getPanelState(0, false);
            if (panelState2 != null) {
                panelState2.isPrepared = false;
                this.preparePanel(panelState2, null);
            }
        }
    }
    
    void endOnGoingFadeAnimation() {
        final ViewPropertyAnimatorCompat mFadeAnim = this.mFadeAnim;
        if (mFadeAnim != null) {
            mFadeAnim.cancel();
        }
    }
    
    PanelFeatureState findMenuPanel(final Menu menu) {
        final PanelFeatureState[] mPanels = this.mPanels;
        int i = 0;
        int length;
        if (mPanels != null) {
            length = mPanels.length;
        }
        else {
            length = 0;
        }
        while (i < length) {
            final PanelFeatureState panelFeatureState = mPanels[i];
            if (panelFeatureState != null && panelFeatureState.menu == menu) {
                return panelFeatureState;
            }
            ++i;
        }
        return null;
    }
    
    @Override
    public <T extends View> T findViewById(final int n) {
        this.ensureSubDecor();
        return (T)this.mWindow.findViewById(n);
    }
    
    final Context getActionBarThemedContext() {
        final ActionBar supportActionBar = this.getSupportActionBar();
        Context themedContext;
        if (supportActionBar != null) {
            themedContext = supportActionBar.getThemedContext();
        }
        else {
            themedContext = null;
        }
        Context mContext = themedContext;
        if (themedContext == null) {
            mContext = this.mContext;
        }
        return mContext;
    }
    
    final AutoNightModeManager getAutoTimeNightModeManager() {
        return this.getAutoTimeNightModeManager(this.mContext);
    }
    
    @Override
    public int getLocalNightMode() {
        return this.mLocalNightMode;
    }
    
    @Override
    public MenuInflater getMenuInflater() {
        if (this.mMenuInflater == null) {
            this.initWindowDecorActionBar();
            final ActionBar mActionBar = this.mActionBar;
            Context context;
            if (mActionBar != null) {
                context = mActionBar.getThemedContext();
            }
            else {
                context = this.mContext;
            }
            this.mMenuInflater = new SupportMenuInflater(context);
        }
        return this.mMenuInflater;
    }
    
    protected PanelFeatureState getPanelState(final int n, final boolean b) {
        final PanelFeatureState[] mPanels = this.mPanels;
        PanelFeatureState[] mPanels2 = null;
        Label_0046: {
            if (mPanels != null) {
                mPanels2 = mPanels;
                if (mPanels.length > n) {
                    break Label_0046;
                }
            }
            mPanels2 = new PanelFeatureState[n + 1];
            if (mPanels != null) {
                System.arraycopy(mPanels, 0, mPanels2, 0, mPanels.length);
            }
            this.mPanels = mPanels2;
        }
        PanelFeatureState panelFeatureState;
        if ((panelFeatureState = mPanels2[n]) == null) {
            panelFeatureState = new PanelFeatureState(n);
            mPanels2[n] = panelFeatureState;
        }
        return panelFeatureState;
    }
    
    @Override
    public ActionBar getSupportActionBar() {
        this.initWindowDecorActionBar();
        return this.mActionBar;
    }
    
    final CharSequence getTitle() {
        final Object mHost = this.mHost;
        if (mHost instanceof Activity) {
            return ((Activity)mHost).getTitle();
        }
        return this.mTitle;
    }
    
    final Window$Callback getWindowCallback() {
        return this.mWindow.getCallback();
    }
    
    @Override
    public void installViewFactory() {
        final LayoutInflater from = LayoutInflater.from(this.mContext);
        if (from.getFactory() == null) {
            LayoutInflaterCompat.setFactory2(from, (LayoutInflater$Factory2)this);
        }
        else if (!(from.getFactory2() instanceof AppCompatDelegateImpl)) {
            Log.i("AppCompatDelegate", "The Activity's LayoutInflater already has a Factory installed so we can not install AppCompat's");
        }
    }
    
    @Override
    public void invalidateOptionsMenu() {
        final ActionBar supportActionBar = this.getSupportActionBar();
        if (supportActionBar != null && supportActionBar.invalidateOptionsMenu()) {
            return;
        }
        this.invalidatePanelMenu(0);
    }
    
    public boolean isHandleNativeActionModesEnabled() {
        return this.mHandleNativeActionModes;
    }
    
    int mapNightMode(final Context context, final int n) {
        if (n != -100) {
            if (n != -1) {
                if (n != 0) {
                    if (n != 1 && n != 2) {
                        if (n == 3) {
                            return this.getAutoBatteryNightModeManager(context).getApplyableNightMode();
                        }
                        throw new IllegalStateException("Unknown value set for night mode. Please use one of the MODE_NIGHT values from AppCompatDelegate.");
                    }
                }
                else {
                    if (Build$VERSION.SDK_INT >= 23 && ((UiModeManager)context.getApplicationContext().getSystemService((Class)UiModeManager.class)).getNightMode() == 0) {
                        return -1;
                    }
                    return this.getAutoTimeNightModeManager(context).getApplyableNightMode();
                }
            }
            return n;
        }
        return -1;
    }
    
    boolean onBackPressed() {
        final ActionMode mActionMode = this.mActionMode;
        if (mActionMode != null) {
            mActionMode.finish();
            return true;
        }
        final ActionBar supportActionBar = this.getSupportActionBar();
        return supportActionBar != null && supportActionBar.collapseActionView();
    }
    
    @Override
    public void onConfigurationChanged(final Configuration configuration) {
        if (this.mHasActionBar && this.mSubDecorInstalled) {
            final ActionBar supportActionBar = this.getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.onConfigurationChanged(configuration);
            }
        }
        AppCompatDrawableManager.get().onConfigurationChanged(this.mContext);
        this.applyDayNight(false);
    }
    
    @Override
    public void onCreate(Bundle bundle) {
        this.mBaseContextAttached = true;
        this.applyDayNight(false);
        this.ensureWindow();
        final Object mHost = this.mHost;
        Label_0065: {
            if (!(mHost instanceof Activity)) {
                break Label_0065;
            }
            bundle = null;
            while (true) {
                try {
                    bundle = (Bundle)NavUtils.getParentActivityName((Activity)mHost);
                    if (bundle != null) {
                        bundle = (Bundle)this.peekSupportActionBar();
                        if (bundle == null) {
                            this.mEnableDefaultActionBarUp = true;
                        }
                        else {
                            ((ActionBar)bundle).setDefaultDisplayHomeAsUpEnabled(true);
                        }
                    }
                    AppCompatDelegate.addActiveDelegate(this);
                    this.mCreated = true;
                }
                catch (IllegalArgumentException ex) {
                    continue;
                }
                break;
            }
        }
    }
    
    public final View onCreateView(final View view, final String s, final Context context, final AttributeSet set) {
        return this.createView(view, s, context, set);
    }
    
    public View onCreateView(final String s, final Context context, final AttributeSet set) {
        return this.onCreateView(null, s, context, set);
    }
    
    @Override
    public void onDestroy() {
        AppCompatDelegate.removeActiveDelegate(this);
        if (this.mInvalidatePanelMenuPosted) {
            this.mWindow.getDecorView().removeCallbacks(this.mInvalidatePanelMenuRunnable);
        }
        this.mStarted = false;
        this.mIsDestroyed = true;
        Label_0111: {
            if (this.mLocalNightMode != -100) {
                final Object mHost = this.mHost;
                if (mHost instanceof Activity && ((Activity)mHost).isChangingConfigurations()) {
                    AppCompatDelegateImpl.sLocalNightModes.put(this.mHost.getClass().getName(), this.mLocalNightMode);
                    break Label_0111;
                }
            }
            AppCompatDelegateImpl.sLocalNightModes.remove(this.mHost.getClass().getName());
        }
        final ActionBar mActionBar = this.mActionBar;
        if (mActionBar != null) {
            mActionBar.onDestroy();
        }
        this.cleanupAutoManagers();
    }
    
    boolean onKeyDown(final int n, final KeyEvent keyEvent) {
        boolean mLongPressBackDown = true;
        if (n != 4) {
            if (n == 82) {
                this.onKeyDownPanel(0, keyEvent);
                return true;
            }
        }
        else {
            if ((keyEvent.getFlags() & 0x80) == 0x0) {
                mLongPressBackDown = false;
            }
            this.mLongPressBackDown = mLongPressBackDown;
        }
        return false;
    }
    
    boolean onKeyShortcut(final int n, final KeyEvent keyEvent) {
        final ActionBar supportActionBar = this.getSupportActionBar();
        if (supportActionBar != null && supportActionBar.onKeyShortcut(n, keyEvent)) {
            return true;
        }
        final PanelFeatureState mPreparedPanel = this.mPreparedPanel;
        if (mPreparedPanel != null && this.performPanelShortcut(mPreparedPanel, keyEvent.getKeyCode(), keyEvent, 1)) {
            final PanelFeatureState mPreparedPanel2 = this.mPreparedPanel;
            if (mPreparedPanel2 != null) {
                mPreparedPanel2.isHandled = true;
            }
            return true;
        }
        if (this.mPreparedPanel == null) {
            final PanelFeatureState panelState = this.getPanelState(0, true);
            this.preparePanel(panelState, keyEvent);
            final boolean performPanelShortcut = this.performPanelShortcut(panelState, keyEvent.getKeyCode(), keyEvent, 1);
            panelState.isPrepared = false;
            if (performPanelShortcut) {
                return true;
            }
        }
        return false;
    }
    
    boolean onKeyUp(final int n, final KeyEvent keyEvent) {
        if (n != 4) {
            if (n == 82) {
                this.onKeyUpPanel(0, keyEvent);
                return true;
            }
        }
        else {
            final boolean mLongPressBackDown = this.mLongPressBackDown;
            this.mLongPressBackDown = false;
            final PanelFeatureState panelState = this.getPanelState(0, false);
            if (panelState != null && panelState.isOpen) {
                if (!mLongPressBackDown) {
                    this.closePanel(panelState, true);
                }
                return true;
            }
            if (this.onBackPressed()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean onMenuItemSelected(final MenuBuilder menuBuilder, final MenuItem menuItem) {
        final Window$Callback windowCallback = this.getWindowCallback();
        if (windowCallback != null && !this.mIsDestroyed) {
            final PanelFeatureState menuPanel = this.findMenuPanel((Menu)menuBuilder.getRootMenu());
            if (menuPanel != null) {
                return windowCallback.onMenuItemSelected(menuPanel.featureId, menuItem);
            }
        }
        return false;
    }
    
    @Override
    public void onMenuModeChange(final MenuBuilder menuBuilder) {
        this.reopenMenu(true);
    }
    
    void onMenuOpened(final int n) {
        if (n == 108) {
            final ActionBar supportActionBar = this.getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.dispatchMenuVisibilityChanged(true);
            }
        }
    }
    
    void onPanelClosed(final int n) {
        if (n == 108) {
            final ActionBar supportActionBar = this.getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.dispatchMenuVisibilityChanged(false);
            }
        }
        else if (n == 0) {
            final PanelFeatureState panelState = this.getPanelState(n, true);
            if (panelState.isOpen) {
                this.closePanel(panelState, false);
            }
        }
    }
    
    @Override
    public void onPostCreate(final Bundle bundle) {
        this.ensureSubDecor();
    }
    
    @Override
    public void onPostResume() {
        final ActionBar supportActionBar = this.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setShowHideAnimationEnabled(true);
        }
    }
    
    @Override
    public void onSaveInstanceState(final Bundle bundle) {
    }
    
    @Override
    public void onStart() {
        this.mStarted = true;
        this.applyDayNight();
    }
    
    @Override
    public void onStop() {
        this.mStarted = false;
        final ActionBar supportActionBar = this.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setShowHideAnimationEnabled(false);
        }
    }
    
    void onSubDecorInstalled(final ViewGroup viewGroup) {
    }
    
    final ActionBar peekSupportActionBar() {
        return this.mActionBar;
    }
    
    @Override
    public boolean requestWindowFeature(int sanitizeWindowFeatureId) {
        sanitizeWindowFeatureId = this.sanitizeWindowFeatureId(sanitizeWindowFeatureId);
        if (this.mWindowNoTitle && sanitizeWindowFeatureId == 108) {
            return false;
        }
        if (this.mHasActionBar && sanitizeWindowFeatureId == 1) {
            this.mHasActionBar = false;
        }
        if (sanitizeWindowFeatureId == 1) {
            this.throwFeatureRequestIfSubDecorInstalled();
            return this.mWindowNoTitle = true;
        }
        if (sanitizeWindowFeatureId == 2) {
            this.throwFeatureRequestIfSubDecorInstalled();
            return this.mFeatureProgress = true;
        }
        if (sanitizeWindowFeatureId == 5) {
            this.throwFeatureRequestIfSubDecorInstalled();
            return this.mFeatureIndeterminateProgress = true;
        }
        if (sanitizeWindowFeatureId == 10) {
            this.throwFeatureRequestIfSubDecorInstalled();
            return this.mOverlayActionMode = true;
        }
        if (sanitizeWindowFeatureId == 108) {
            this.throwFeatureRequestIfSubDecorInstalled();
            return this.mHasActionBar = true;
        }
        if (sanitizeWindowFeatureId != 109) {
            return this.mWindow.requestFeature(sanitizeWindowFeatureId);
        }
        this.throwFeatureRequestIfSubDecorInstalled();
        return this.mOverlayActionBar = true;
    }
    
    @Override
    public void setContentView(final int n) {
        this.ensureSubDecor();
        final ViewGroup viewGroup = (ViewGroup)this.mSubDecor.findViewById(16908290);
        viewGroup.removeAllViews();
        LayoutInflater.from(this.mContext).inflate(n, viewGroup);
        this.mAppCompatWindowCallback.getWrapped().onContentChanged();
    }
    
    @Override
    public void setContentView(final View view) {
        this.ensureSubDecor();
        final ViewGroup viewGroup = (ViewGroup)this.mSubDecor.findViewById(16908290);
        viewGroup.removeAllViews();
        viewGroup.addView(view);
        this.mAppCompatWindowCallback.getWrapped().onContentChanged();
    }
    
    @Override
    public void setContentView(final View view, final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        this.ensureSubDecor();
        final ViewGroup viewGroup = (ViewGroup)this.mSubDecor.findViewById(16908290);
        viewGroup.removeAllViews();
        viewGroup.addView(view, viewGroup$LayoutParams);
        this.mAppCompatWindowCallback.getWrapped().onContentChanged();
    }
    
    @Override
    public void setTheme(final int mThemeResId) {
        this.mThemeResId = mThemeResId;
    }
    
    @Override
    public final void setTitle(final CharSequence charSequence) {
        this.mTitle = charSequence;
        final DecorContentParent mDecorContentParent = this.mDecorContentParent;
        if (mDecorContentParent != null) {
            mDecorContentParent.setWindowTitle(charSequence);
        }
        else if (this.peekSupportActionBar() != null) {
            this.peekSupportActionBar().setWindowTitle(charSequence);
        }
        else {
            final TextView mTitleView = this.mTitleView;
            if (mTitleView != null) {
                mTitleView.setText(charSequence);
            }
        }
    }
    
    final boolean shouldAnimateActionModeView() {
        if (this.mSubDecorInstalled) {
            final ViewGroup mSubDecor = this.mSubDecor;
            if (mSubDecor != null && ViewCompat.isLaidOut((View)mSubDecor)) {
                return true;
            }
        }
        return false;
    }
    
    public ActionMode startSupportActionMode(final ActionMode.Callback callback) {
        if (callback != null) {
            final ActionMode mActionMode = this.mActionMode;
            if (mActionMode != null) {
                mActionMode.finish();
            }
            final ActionModeCallbackWrapperV9 actionModeCallbackWrapperV9 = new ActionModeCallbackWrapperV9(callback);
            final ActionBar supportActionBar = this.getSupportActionBar();
            if (supportActionBar != null) {
                final ActionMode startActionMode = supportActionBar.startActionMode(actionModeCallbackWrapperV9);
                if ((this.mActionMode = startActionMode) != null) {
                    final AppCompatCallback mAppCompatCallback = this.mAppCompatCallback;
                    if (mAppCompatCallback != null) {
                        mAppCompatCallback.onSupportActionModeStarted(startActionMode);
                    }
                }
            }
            if (this.mActionMode == null) {
                this.mActionMode = this.startSupportActionModeFromWindow(actionModeCallbackWrapperV9);
            }
            return this.mActionMode;
        }
        throw new IllegalArgumentException("ActionMode callback can not be null.");
    }
    
    ActionMode startSupportActionModeFromWindow(final ActionMode.Callback callback) {
        this.endOnGoingFadeAnimation();
        final ActionMode mActionMode = this.mActionMode;
        if (mActionMode != null) {
            mActionMode.finish();
        }
        Object mAppCompatCallback = callback;
        if (!(callback instanceof ActionModeCallbackWrapperV9)) {
            mAppCompatCallback = new ActionModeCallbackWrapperV9(callback);
        }
        final AppCompatCallback mAppCompatCallback2 = this.mAppCompatCallback;
        while (true) {
            if (mAppCompatCallback2 == null || this.mIsDestroyed) {
                break Label_0063;
            }
            try {
                ActionMode onWindowStartingSupportActionMode = mAppCompatCallback2.onWindowStartingSupportActionMode((ActionMode.Callback)mAppCompatCallback);
                while (true) {
                    if (onWindowStartingSupportActionMode != null) {
                        this.mActionMode = onWindowStartingSupportActionMode;
                    }
                    else {
                        final ActionBarContextView mActionModeView = this.mActionModeView;
                        boolean b = true;
                        if (mActionModeView == null) {
                            if (this.mIsFloating) {
                                final TypedValue typedValue = new TypedValue();
                                final Resources$Theme theme = this.mContext.getTheme();
                                theme.resolveAttribute(R$attr.actionBarTheme, typedValue, true);
                                Object mContext;
                                if (typedValue.resourceId != 0) {
                                    final Resources$Theme theme2 = this.mContext.getResources().newTheme();
                                    theme2.setTo(theme);
                                    theme2.applyStyle(typedValue.resourceId, true);
                                    mContext = new ContextThemeWrapper(this.mContext, 0);
                                    ((Context)mContext).getTheme().setTo(theme2);
                                }
                                else {
                                    mContext = this.mContext;
                                }
                                this.mActionModeView = new ActionBarContextView((Context)mContext);
                                PopupWindowCompat.setWindowLayoutType(this.mActionModePopup = new PopupWindow((Context)mContext, (AttributeSet)null, R$attr.actionModePopupWindowStyle), 2);
                                this.mActionModePopup.setContentView((View)this.mActionModeView);
                                this.mActionModePopup.setWidth(-1);
                                ((Context)mContext).getTheme().resolveAttribute(R$attr.actionBarSize, typedValue, true);
                                this.mActionModeView.setContentHeight(TypedValue.complexToDimensionPixelSize(typedValue.data, ((Context)mContext).getResources().getDisplayMetrics()));
                                this.mActionModePopup.setHeight(-2);
                                this.mShowActionModePopup = new Runnable() {
                                    @Override
                                    public void run() {
                                        final AppCompatDelegateImpl this$0 = AppCompatDelegateImpl.this;
                                        this$0.mActionModePopup.showAtLocation((View)this$0.mActionModeView, 55, 0, 0);
                                        AppCompatDelegateImpl.this.endOnGoingFadeAnimation();
                                        if (AppCompatDelegateImpl.this.shouldAnimateActionModeView()) {
                                            AppCompatDelegateImpl.this.mActionModeView.setAlpha(0.0f);
                                            final AppCompatDelegateImpl this$2 = AppCompatDelegateImpl.this;
                                            final ViewPropertyAnimatorCompat animate = ViewCompat.animate((View)this$2.mActionModeView);
                                            animate.alpha(1.0f);
                                            this$2.mFadeAnim = animate;
                                            AppCompatDelegateImpl.this.mFadeAnim.setListener(new ViewPropertyAnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationEnd(final View view) {
                                                    AppCompatDelegateImpl.this.mActionModeView.setAlpha(1.0f);
                                                    AppCompatDelegateImpl.this.mFadeAnim.setListener(null);
                                                    AppCompatDelegateImpl.this.mFadeAnim = null;
                                                }
                                                
                                                @Override
                                                public void onAnimationStart(final View view) {
                                                    AppCompatDelegateImpl.this.mActionModeView.setVisibility(0);
                                                }
                                            });
                                        }
                                        else {
                                            AppCompatDelegateImpl.this.mActionModeView.setAlpha(1.0f);
                                            AppCompatDelegateImpl.this.mActionModeView.setVisibility(0);
                                        }
                                    }
                                };
                            }
                            else {
                                final ViewStubCompat viewStubCompat = (ViewStubCompat)this.mSubDecor.findViewById(R$id.action_mode_bar_stub);
                                if (viewStubCompat != null) {
                                    viewStubCompat.setLayoutInflater(LayoutInflater.from(this.getActionBarThemedContext()));
                                    this.mActionModeView = (ActionBarContextView)viewStubCompat.inflate();
                                }
                            }
                        }
                        if (this.mActionModeView != null) {
                            this.endOnGoingFadeAnimation();
                            this.mActionModeView.killMode();
                            final Context context = this.mActionModeView.getContext();
                            final ActionBarContextView mActionModeView2 = this.mActionModeView;
                            if (this.mActionModePopup != null) {
                                b = false;
                            }
                            final StandaloneActionMode mActionMode2 = new StandaloneActionMode(context, mActionModeView2, (ActionMode.Callback)mAppCompatCallback, b);
                            if (((ActionMode.Callback)mAppCompatCallback).onCreateActionMode(mActionMode2, mActionMode2.getMenu())) {
                                mActionMode2.invalidate();
                                this.mActionModeView.initForMode(mActionMode2);
                                this.mActionMode = mActionMode2;
                                if (this.shouldAnimateActionModeView()) {
                                    this.mActionModeView.setAlpha(0.0f);
                                    final ViewPropertyAnimatorCompat animate = ViewCompat.animate((View)this.mActionModeView);
                                    animate.alpha(1.0f);
                                    (this.mFadeAnim = animate).setListener(new ViewPropertyAnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(final View view) {
                                            AppCompatDelegateImpl.this.mActionModeView.setAlpha(1.0f);
                                            AppCompatDelegateImpl.this.mFadeAnim.setListener(null);
                                            AppCompatDelegateImpl.this.mFadeAnim = null;
                                        }
                                        
                                        @Override
                                        public void onAnimationStart(final View view) {
                                            AppCompatDelegateImpl.this.mActionModeView.setVisibility(0);
                                            AppCompatDelegateImpl.this.mActionModeView.sendAccessibilityEvent(32);
                                            if (AppCompatDelegateImpl.this.mActionModeView.getParent() instanceof View) {
                                                ViewCompat.requestApplyInsets((View)AppCompatDelegateImpl.this.mActionModeView.getParent());
                                            }
                                        }
                                    });
                                }
                                else {
                                    this.mActionModeView.setAlpha(1.0f);
                                    this.mActionModeView.setVisibility(0);
                                    this.mActionModeView.sendAccessibilityEvent(32);
                                    if (this.mActionModeView.getParent() instanceof View) {
                                        ViewCompat.requestApplyInsets((View)this.mActionModeView.getParent());
                                    }
                                }
                                if (this.mActionModePopup != null) {
                                    this.mWindow.getDecorView().post(this.mShowActionModePopup);
                                }
                            }
                            else {
                                this.mActionMode = null;
                            }
                        }
                    }
                    final ActionMode mActionMode3 = this.mActionMode;
                    if (mActionMode3 != null) {
                        mAppCompatCallback = this.mAppCompatCallback;
                        if (mAppCompatCallback != null) {
                            ((AppCompatCallback)mAppCompatCallback).onSupportActionModeStarted(mActionMode3);
                        }
                    }
                    return this.mActionMode;
                    onWindowStartingSupportActionMode = null;
                    continue;
                }
            }
            catch (AbstractMethodError abstractMethodError) {
                continue;
            }
            break;
        }
    }
    
    final int updateStatusGuard(WindowInsetsCompat rootWindowInsets, final Rect rect) {
        final int n = 0;
        int n2;
        if (rootWindowInsets != null) {
            n2 = rootWindowInsets.getSystemWindowInsetTop();
        }
        else if (rect != null) {
            n2 = rect.top;
        }
        else {
            n2 = 0;
        }
        final ActionBarContextView mActionModeView = this.mActionModeView;
        int n9;
        int n10;
        if (mActionModeView != null && mActionModeView.getLayoutParams() instanceof ViewGroup$MarginLayoutParams) {
            final ViewGroup$MarginLayoutParams layoutParams = (ViewGroup$MarginLayoutParams)this.mActionModeView.getLayoutParams();
            final boolean shown = this.mActionModeView.isShown();
            int n3 = 1;
            final int n4 = 1;
            int n8;
            if (shown) {
                if (this.mTempRect1 == null) {
                    this.mTempRect1 = new Rect();
                    this.mTempRect2 = new Rect();
                }
                final Rect mTempRect1 = this.mTempRect1;
                final Rect mTempRect2 = this.mTempRect2;
                if (rootWindowInsets == null) {
                    mTempRect1.set(rect);
                }
                else {
                    mTempRect1.set(rootWindowInsets.getSystemWindowInsetLeft(), rootWindowInsets.getSystemWindowInsetTop(), rootWindowInsets.getSystemWindowInsetRight(), rootWindowInsets.getSystemWindowInsetBottom());
                }
                ViewUtils.computeFitSystemWindows((View)this.mSubDecor, mTempRect1, mTempRect2);
                final int top = mTempRect1.top;
                final int left = mTempRect1.left;
                final int right = mTempRect1.right;
                rootWindowInsets = ViewCompat.getRootWindowInsets((View)this.mSubDecor);
                int systemWindowInsetLeft;
                if (rootWindowInsets == null) {
                    systemWindowInsetLeft = 0;
                }
                else {
                    systemWindowInsetLeft = rootWindowInsets.getSystemWindowInsetLeft();
                }
                int systemWindowInsetRight;
                if (rootWindowInsets == null) {
                    systemWindowInsetRight = 0;
                }
                else {
                    systemWindowInsetRight = rootWindowInsets.getSystemWindowInsetRight();
                }
                int n5;
                if (layoutParams.topMargin == top && layoutParams.leftMargin == left && layoutParams.rightMargin == right) {
                    n5 = 0;
                }
                else {
                    layoutParams.topMargin = top;
                    layoutParams.leftMargin = left;
                    layoutParams.rightMargin = right;
                    n5 = 1;
                }
                if (top > 0 && this.mStatusGuard == null) {
                    (this.mStatusGuard = new View(this.mContext)).setVisibility(8);
                    final FrameLayout$LayoutParams frameLayout$LayoutParams = new FrameLayout$LayoutParams(-1, layoutParams.topMargin, 51);
                    frameLayout$LayoutParams.leftMargin = systemWindowInsetLeft;
                    frameLayout$LayoutParams.rightMargin = systemWindowInsetRight;
                    this.mSubDecor.addView(this.mStatusGuard, -1, (ViewGroup$LayoutParams)frameLayout$LayoutParams);
                }
                else {
                    final View mStatusGuard = this.mStatusGuard;
                    if (mStatusGuard != null) {
                        final ViewGroup$MarginLayoutParams layoutParams2 = (ViewGroup$MarginLayoutParams)mStatusGuard.getLayoutParams();
                        if (layoutParams2.height != layoutParams.topMargin || layoutParams2.leftMargin != systemWindowInsetLeft || layoutParams2.rightMargin != systemWindowInsetRight) {
                            layoutParams2.height = layoutParams.topMargin;
                            layoutParams2.leftMargin = systemWindowInsetLeft;
                            layoutParams2.rightMargin = systemWindowInsetRight;
                            this.mStatusGuard.setLayoutParams((ViewGroup$LayoutParams)layoutParams2);
                        }
                    }
                }
                int n6;
                if (this.mStatusGuard != null) {
                    n6 = n4;
                }
                else {
                    n6 = 0;
                }
                if (n6 != 0 && this.mStatusGuard.getVisibility() != 0) {
                    this.updateStatusGuardColor(this.mStatusGuard);
                }
                int n7 = n2;
                if (!this.mOverlayActionMode) {
                    n7 = n2;
                    if (n6 != 0) {
                        n7 = 0;
                    }
                }
                n2 = n7;
                n3 = n5;
                n8 = n6;
            }
            else if (layoutParams.topMargin != 0) {
                layoutParams.topMargin = 0;
                n8 = 0;
            }
            else {
                n8 = (n3 = 0);
            }
            n9 = n2;
            n10 = n8;
            if (n3 != 0) {
                this.mActionModeView.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
                n9 = n2;
                n10 = n8;
            }
        }
        else {
            n10 = 0;
            n9 = n2;
        }
        final View mStatusGuard2 = this.mStatusGuard;
        if (mStatusGuard2 != null) {
            int visibility;
            if (n10 != 0) {
                visibility = n;
            }
            else {
                visibility = 8;
            }
            mStatusGuard2.setVisibility(visibility);
        }
        return n9;
    }
    
    private final class ActionMenuPresenterCallback implements MenuPresenter.Callback
    {
        ActionMenuPresenterCallback() {
        }
        
        @Override
        public void onCloseMenu(final MenuBuilder menuBuilder, final boolean b) {
            AppCompatDelegateImpl.this.checkCloseActionMenu(menuBuilder);
        }
        
        @Override
        public boolean onOpenSubMenu(final MenuBuilder menuBuilder) {
            final Window$Callback windowCallback = AppCompatDelegateImpl.this.getWindowCallback();
            if (windowCallback != null) {
                windowCallback.onMenuOpened(108, (Menu)menuBuilder);
            }
            return true;
        }
    }
    
    class ActionModeCallbackWrapperV9 implements ActionMode.Callback
    {
        private ActionMode.Callback mWrapped;
        
        public ActionModeCallbackWrapperV9(final ActionMode.Callback mWrapped) {
            this.mWrapped = mWrapped;
        }
        
        @Override
        public boolean onActionItemClicked(final ActionMode actionMode, final MenuItem menuItem) {
            return this.mWrapped.onActionItemClicked(actionMode, menuItem);
        }
        
        @Override
        public boolean onCreateActionMode(final ActionMode actionMode, final Menu menu) {
            return this.mWrapped.onCreateActionMode(actionMode, menu);
        }
        
        @Override
        public void onDestroyActionMode(final ActionMode actionMode) {
            this.mWrapped.onDestroyActionMode(actionMode);
            final AppCompatDelegateImpl this$0 = AppCompatDelegateImpl.this;
            if (this$0.mActionModePopup != null) {
                this$0.mWindow.getDecorView().removeCallbacks(AppCompatDelegateImpl.this.mShowActionModePopup);
            }
            final AppCompatDelegateImpl this$2 = AppCompatDelegateImpl.this;
            if (this$2.mActionModeView != null) {
                this$2.endOnGoingFadeAnimation();
                final AppCompatDelegateImpl this$3 = AppCompatDelegateImpl.this;
                final ViewPropertyAnimatorCompat animate = ViewCompat.animate((View)this$3.mActionModeView);
                animate.alpha(0.0f);
                this$3.mFadeAnim = animate;
                AppCompatDelegateImpl.this.mFadeAnim.setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(final View view) {
                        AppCompatDelegateImpl.this.mActionModeView.setVisibility(8);
                        final AppCompatDelegateImpl this$0 = AppCompatDelegateImpl.this;
                        final PopupWindow mActionModePopup = this$0.mActionModePopup;
                        if (mActionModePopup != null) {
                            mActionModePopup.dismiss();
                        }
                        else if (this$0.mActionModeView.getParent() instanceof View) {
                            ViewCompat.requestApplyInsets((View)AppCompatDelegateImpl.this.mActionModeView.getParent());
                        }
                        AppCompatDelegateImpl.this.mActionModeView.removeAllViews();
                        AppCompatDelegateImpl.this.mFadeAnim.setListener(null);
                        final AppCompatDelegateImpl this$2 = AppCompatDelegateImpl.this;
                        this$2.mFadeAnim = null;
                        ViewCompat.requestApplyInsets((View)this$2.mSubDecor);
                    }
                });
            }
            final AppCompatDelegateImpl this$4 = AppCompatDelegateImpl.this;
            final AppCompatCallback mAppCompatCallback = this$4.mAppCompatCallback;
            if (mAppCompatCallback != null) {
                mAppCompatCallback.onSupportActionModeFinished(this$4.mActionMode);
            }
            final AppCompatDelegateImpl this$5 = AppCompatDelegateImpl.this;
            this$5.mActionMode = null;
            ViewCompat.requestApplyInsets((View)this$5.mSubDecor);
        }
        
        @Override
        public boolean onPrepareActionMode(final ActionMode actionMode, final Menu menu) {
            ViewCompat.requestApplyInsets((View)AppCompatDelegateImpl.this.mSubDecor);
            return this.mWrapped.onPrepareActionMode(actionMode, menu);
        }
    }
    
    class AppCompatWindowCallback extends WindowCallbackWrapper
    {
        AppCompatWindowCallback(final Window$Callback window$Callback) {
            super(window$Callback);
        }
        
        @Override
        public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
            return AppCompatDelegateImpl.this.dispatchKeyEvent(keyEvent) || super.dispatchKeyEvent(keyEvent);
        }
        
        @Override
        public boolean dispatchKeyShortcutEvent(final KeyEvent keyEvent) {
            return super.dispatchKeyShortcutEvent(keyEvent) || AppCompatDelegateImpl.this.onKeyShortcut(keyEvent.getKeyCode(), keyEvent);
        }
        
        public void onContentChanged() {
        }
        
        @Override
        public boolean onCreatePanelMenu(final int n, final Menu menu) {
            return (n != 0 || menu instanceof MenuBuilder) && super.onCreatePanelMenu(n, menu);
        }
        
        @Override
        public boolean onMenuOpened(final int n, final Menu menu) {
            super.onMenuOpened(n, menu);
            AppCompatDelegateImpl.this.onMenuOpened(n);
            return true;
        }
        
        @Override
        public void onPanelClosed(final int n, final Menu menu) {
            super.onPanelClosed(n, menu);
            AppCompatDelegateImpl.this.onPanelClosed(n);
        }
        
        @Override
        public boolean onPreparePanel(final int n, final View view, final Menu menu) {
            MenuBuilder menuBuilder;
            if (menu instanceof MenuBuilder) {
                menuBuilder = (MenuBuilder)menu;
            }
            else {
                menuBuilder = null;
            }
            if (n == 0 && menuBuilder == null) {
                return false;
            }
            if (menuBuilder != null) {
                menuBuilder.setOverrideVisibleItems(true);
            }
            final boolean onPreparePanel = super.onPreparePanel(n, view, menu);
            if (menuBuilder != null) {
                menuBuilder.setOverrideVisibleItems(false);
            }
            return onPreparePanel;
        }
        
        @Override
        public void onProvideKeyboardShortcuts(final List<KeyboardShortcutGroup> list, final Menu menu, final int n) {
            final PanelFeatureState panelState = AppCompatDelegateImpl.this.getPanelState(0, true);
            if (panelState != null) {
                final MenuBuilder menu2 = panelState.menu;
                if (menu2 != null) {
                    super.onProvideKeyboardShortcuts(list, (Menu)menu2, n);
                    return;
                }
            }
            super.onProvideKeyboardShortcuts(list, menu, n);
        }
        
        @Override
        public android.view.ActionMode onWindowStartingActionMode(final ActionMode$Callback actionMode$Callback) {
            if (Build$VERSION.SDK_INT >= 23) {
                return null;
            }
            if (AppCompatDelegateImpl.this.isHandleNativeActionModesEnabled()) {
                return this.startAsSupportActionMode(actionMode$Callback);
            }
            return super.onWindowStartingActionMode(actionMode$Callback);
        }
        
        @Override
        public android.view.ActionMode onWindowStartingActionMode(final ActionMode$Callback actionMode$Callback, final int n) {
            if (AppCompatDelegateImpl.this.isHandleNativeActionModesEnabled() && n == 0) {
                return this.startAsSupportActionMode(actionMode$Callback);
            }
            return super.onWindowStartingActionMode(actionMode$Callback, n);
        }
        
        final android.view.ActionMode startAsSupportActionMode(final ActionMode$Callback actionMode$Callback) {
            final SupportActionModeWrapper.CallbackWrapper callbackWrapper = new SupportActionModeWrapper.CallbackWrapper(AppCompatDelegateImpl.this.mContext, actionMode$Callback);
            final ActionMode startSupportActionMode = AppCompatDelegateImpl.this.startSupportActionMode(callbackWrapper);
            if (startSupportActionMode != null) {
                return callbackWrapper.getActionModeWrapper(startSupportActionMode);
            }
            return null;
        }
    }
    
    private class AutoBatteryNightModeManager extends AutoNightModeManager
    {
        private final PowerManager mPowerManager;
        
        AutoBatteryNightModeManager(final Context context) {
            this.mPowerManager = (PowerManager)context.getApplicationContext().getSystemService("power");
        }
        
        @Override
        IntentFilter createIntentFilterForBroadcastReceiver() {
            if (Build$VERSION.SDK_INT >= 21) {
                final IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
                return intentFilter;
            }
            return null;
        }
        
        public int getApplyableNightMode() {
            final int sdk_INT = Build$VERSION.SDK_INT;
            int n = 1;
            if (sdk_INT >= 21) {
                n = n;
                if (this.mPowerManager.isPowerSaveMode()) {
                    n = 2;
                }
            }
            return n;
        }
        
        public void onChange() {
            AppCompatDelegateImpl.this.applyDayNight();
        }
    }
    
    abstract class AutoNightModeManager
    {
        private BroadcastReceiver mReceiver;
        
        void cleanup() {
            final BroadcastReceiver mReceiver = this.mReceiver;
            if (mReceiver == null) {
                return;
            }
            while (true) {
                try {
                    AppCompatDelegateImpl.this.mContext.unregisterReceiver(mReceiver);
                    this.mReceiver = null;
                }
                catch (IllegalArgumentException ex) {
                    continue;
                }
                break;
            }
        }
        
        abstract IntentFilter createIntentFilterForBroadcastReceiver();
        
        abstract int getApplyableNightMode();
        
        abstract void onChange();
        
        void setup() {
            this.cleanup();
            final IntentFilter intentFilterForBroadcastReceiver = this.createIntentFilterForBroadcastReceiver();
            if (intentFilterForBroadcastReceiver != null) {
                if (intentFilterForBroadcastReceiver.countActions() != 0) {
                    if (this.mReceiver == null) {
                        this.mReceiver = new BroadcastReceiver() {
                            public void onReceive(final Context context, final Intent intent) {
                                AutoNightModeManager.this.onChange();
                            }
                        };
                    }
                    AppCompatDelegateImpl.this.mContext.registerReceiver(this.mReceiver, intentFilterForBroadcastReceiver);
                }
            }
        }
    }
    
    private class AutoTimeNightModeManager extends AutoNightModeManager
    {
        private final TwilightManager mTwilightManager;
        
        AutoTimeNightModeManager(final TwilightManager mTwilightManager) {
            this.mTwilightManager = mTwilightManager;
        }
        
        @Override
        IntentFilter createIntentFilterForBroadcastReceiver() {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.TIME_SET");
            intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
            intentFilter.addAction("android.intent.action.TIME_TICK");
            return intentFilter;
        }
        
        public int getApplyableNightMode() {
            int n;
            if (this.mTwilightManager.isNight()) {
                n = 2;
            }
            else {
                n = 1;
            }
            return n;
        }
        
        public void onChange() {
            AppCompatDelegateImpl.this.applyDayNight();
        }
    }
    
    static class ConfigurationImplApi17
    {
        static void generateConfigDelta_densityDpi(final Configuration configuration, final Configuration configuration2, final Configuration configuration3) {
            final int densityDpi = configuration.densityDpi;
            final int densityDpi2 = configuration2.densityDpi;
            if (densityDpi != densityDpi2) {
                configuration3.densityDpi = densityDpi2;
            }
        }
    }
    
    static class ConfigurationImplApi24
    {
        static void generateConfigDelta_locale(final Configuration configuration, final Configuration configuration2, final Configuration configuration3) {
            final LocaleList locales = configuration.getLocales();
            final LocaleList locales2 = configuration2.getLocales();
            if (!locales.equals((Object)locales2)) {
                configuration3.setLocales(locales2);
                configuration3.locale = configuration2.locale;
            }
        }
    }
    
    static class ConfigurationImplApi26
    {
        static void generateConfigDelta_colorMode(final Configuration configuration, final Configuration configuration2, final Configuration configuration3) {
            final int colorMode = configuration.colorMode;
            final int colorMode2 = configuration2.colorMode;
            if ((colorMode & 0x3) != (colorMode2 & 0x3)) {
                configuration3.colorMode |= (colorMode2 & 0x3);
            }
            final int colorMode3 = configuration.colorMode;
            final int colorMode4 = configuration2.colorMode;
            if ((colorMode3 & 0xC) != (colorMode4 & 0xC)) {
                configuration3.colorMode |= (colorMode4 & 0xC);
            }
        }
    }
    
    private static class ContextThemeWrapperCompatApi17Impl
    {
        static void applyOverrideConfiguration(final android.view.ContextThemeWrapper contextThemeWrapper, final Configuration configuration) {
            contextThemeWrapper.applyOverrideConfiguration(configuration);
        }
    }
    
    private class ListMenuDecorView extends ContentFrameLayout
    {
        public ListMenuDecorView(final Context context) {
            super(context);
        }
        
        private boolean isOutOfBounds(final int n, final int n2) {
            return n < -5 || n2 < -5 || n > this.getWidth() + 5 || n2 > this.getHeight() + 5;
        }
        
        public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
            return AppCompatDelegateImpl.this.dispatchKeyEvent(keyEvent) || super.dispatchKeyEvent(keyEvent);
        }
        
        public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0 && this.isOutOfBounds((int)motionEvent.getX(), (int)motionEvent.getY())) {
                AppCompatDelegateImpl.this.closePanel(0);
                return true;
            }
            return super.onInterceptTouchEvent(motionEvent);
        }
        
        public void setBackgroundResource(final int n) {
            this.setBackgroundDrawable(AppCompatResources.getDrawable(this.getContext(), n));
        }
    }
    
    protected static final class PanelFeatureState
    {
        int background;
        View createdPanelView;
        ViewGroup decorView;
        int featureId;
        Bundle frozenActionViewState;
        int gravity;
        boolean isHandled;
        boolean isOpen;
        boolean isPrepared;
        ListMenuPresenter listMenuPresenter;
        Context listPresenterContext;
        MenuBuilder menu;
        public boolean qwertyMode;
        boolean refreshDecorView;
        boolean refreshMenuContent;
        View shownPanelView;
        int windowAnimations;
        int x;
        int y;
        
        PanelFeatureState(final int featureId) {
            this.featureId = featureId;
            this.refreshDecorView = false;
        }
        
        MenuView getListMenuView(final MenuPresenter.Callback callback) {
            if (this.menu == null) {
                return null;
            }
            if (this.listMenuPresenter == null) {
                (this.listMenuPresenter = new ListMenuPresenter(this.listPresenterContext, R$layout.abc_list_menu_item_layout)).setCallback(callback);
                this.menu.addMenuPresenter(this.listMenuPresenter);
            }
            return this.listMenuPresenter.getMenuView(this.decorView);
        }
        
        public boolean hasPanelItems() {
            final View shownPanelView = this.shownPanelView;
            boolean b = false;
            if (shownPanelView == null) {
                return false;
            }
            if (this.createdPanelView != null) {
                return true;
            }
            if (this.listMenuPresenter.getAdapter().getCount() > 0) {
                b = true;
            }
            return b;
        }
        
        void setMenu(final MenuBuilder menu) {
            final MenuBuilder menu2 = this.menu;
            if (menu == menu2) {
                return;
            }
            if (menu2 != null) {
                menu2.removeMenuPresenter(this.listMenuPresenter);
            }
            if ((this.menu = menu) != null) {
                final ListMenuPresenter listMenuPresenter = this.listMenuPresenter;
                if (listMenuPresenter != null) {
                    menu.addMenuPresenter(listMenuPresenter);
                }
            }
        }
        
        void setStyle(final Context context) {
            final TypedValue typedValue = new TypedValue();
            final Resources$Theme theme = context.getResources().newTheme();
            theme.setTo(context.getTheme());
            theme.resolveAttribute(R$attr.actionBarPopupTheme, typedValue, true);
            final int resourceId = typedValue.resourceId;
            if (resourceId != 0) {
                theme.applyStyle(resourceId, true);
            }
            theme.resolveAttribute(R$attr.panelMenuListTheme, typedValue, true);
            final int resourceId2 = typedValue.resourceId;
            if (resourceId2 != 0) {
                theme.applyStyle(resourceId2, true);
            }
            else {
                theme.applyStyle(R$style.Theme_AppCompat_CompactMenu, true);
            }
            final ContextThemeWrapper listPresenterContext = new ContextThemeWrapper(context, 0);
            ((Context)listPresenterContext).getTheme().setTo(theme);
            this.listPresenterContext = (Context)listPresenterContext;
            final TypedArray obtainStyledAttributes = ((Context)listPresenterContext).obtainStyledAttributes(R$styleable.AppCompatTheme);
            this.background = obtainStyledAttributes.getResourceId(R$styleable.AppCompatTheme_panelBackground, 0);
            this.windowAnimations = obtainStyledAttributes.getResourceId(R$styleable.AppCompatTheme_android_windowAnimationStyle, 0);
            obtainStyledAttributes.recycle();
        }
        
        @SuppressLint({ "BanParcelableUsage" })
        private static class SavedState implements Parcelable
        {
            public static final Parcelable$Creator<SavedState> CREATOR;
            int featureId;
            boolean isOpen;
            Bundle menuState;
            
            static {
                CREATOR = (Parcelable$Creator)new Parcelable$ClassLoaderCreator<SavedState>() {
                    public SavedState createFromParcel(final Parcel parcel) {
                        return SavedState.readFromParcel(parcel, null);
                    }
                    
                    public SavedState createFromParcel(final Parcel parcel, final ClassLoader classLoader) {
                        return SavedState.readFromParcel(parcel, classLoader);
                    }
                    
                    public SavedState[] newArray(final int n) {
                        return new SavedState[n];
                    }
                };
            }
            
            SavedState() {
            }
            
            static SavedState readFromParcel(final Parcel parcel, final ClassLoader classLoader) {
                final SavedState savedState = new SavedState();
                savedState.featureId = parcel.readInt();
                final int int1 = parcel.readInt();
                boolean isOpen = true;
                if (int1 != 1) {
                    isOpen = false;
                }
                savedState.isOpen = isOpen;
                if (isOpen) {
                    savedState.menuState = parcel.readBundle(classLoader);
                }
                return savedState;
            }
            
            public int describeContents() {
                return 0;
            }
            
            public void writeToParcel(final Parcel parcel, final int n) {
                parcel.writeInt(this.featureId);
                parcel.writeInt((int)(this.isOpen ? 1 : 0));
                if (this.isOpen) {
                    parcel.writeBundle(this.menuState);
                }
            }
        }
    }
    
    private final class PanelMenuPresenterCallback implements MenuPresenter.Callback
    {
        PanelMenuPresenterCallback() {
        }
        
        @Override
        public void onCloseMenu(MenuBuilder menuBuilder, final boolean b) {
            final Object rootMenu = menuBuilder.getRootMenu();
            final boolean b2 = rootMenu != menuBuilder;
            final AppCompatDelegateImpl this$0 = AppCompatDelegateImpl.this;
            if (b2) {
                menuBuilder = (MenuBuilder)rootMenu;
            }
            final PanelFeatureState menuPanel = this$0.findMenuPanel((Menu)menuBuilder);
            if (menuPanel != null) {
                if (b2) {
                    AppCompatDelegateImpl.this.callOnPanelClosed(menuPanel.featureId, menuPanel, (Menu)rootMenu);
                    AppCompatDelegateImpl.this.closePanel(menuPanel, true);
                }
                else {
                    AppCompatDelegateImpl.this.closePanel(menuPanel, b);
                }
            }
        }
        
        @Override
        public boolean onOpenSubMenu(final MenuBuilder menuBuilder) {
            if (menuBuilder == null) {
                final AppCompatDelegateImpl this$0 = AppCompatDelegateImpl.this;
                if (this$0.mHasActionBar) {
                    final Window$Callback windowCallback = this$0.getWindowCallback();
                    if (windowCallback != null && !AppCompatDelegateImpl.this.mIsDestroyed) {
                        windowCallback.onMenuOpened(108, (Menu)menuBuilder);
                    }
                }
            }
            return true;
        }
    }
}
