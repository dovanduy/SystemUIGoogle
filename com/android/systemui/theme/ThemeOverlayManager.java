// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.theme;

import java.util.function.Predicate;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.HashSet;
import java.util.Iterator;
import android.util.Log;
import android.content.om.OverlayInfo;
import java.util.Collection;
import android.os.UserHandle;
import android.util.ArrayMap;
import com.google.android.collect.Sets;
import com.google.android.collect.Lists;
import android.content.om.OverlayManager;
import java.util.concurrent.Executor;
import java.util.Map;
import java.util.List;
import java.util.Set;

class ThemeOverlayManager
{
    static final String ANDROID_PACKAGE = "android";
    static final String OVERLAY_CATEGORY_COLOR = "android.theme.customization.accent_color";
    static final String OVERLAY_CATEGORY_FONT = "android.theme.customization.font";
    static final String OVERLAY_CATEGORY_ICON_ANDROID = "android.theme.customization.icon_pack.android";
    static final String OVERLAY_CATEGORY_ICON_LAUNCHER = "android.theme.customization.icon_pack.launcher";
    static final String OVERLAY_CATEGORY_ICON_SETTINGS = "android.theme.customization.icon_pack.settings";
    static final String OVERLAY_CATEGORY_ICON_SYSUI = "android.theme.customization.icon_pack.systemui";
    static final String OVERLAY_CATEGORY_ICON_THEME_PICKER = "android.theme.customization.icon_pack.themepicker";
    static final String OVERLAY_CATEGORY_SHAPE = "android.theme.customization.adaptive_icon_shape";
    static final String SETTINGS_PACKAGE = "com.android.settings";
    static final Set<String> SYSTEM_USER_CATEGORIES;
    static final String SYSUI_PACKAGE = "com.android.systemui";
    static final List<String> THEME_CATEGORIES;
    private final Map<String, String> mCategoryToTargetPackage;
    private final Executor mExecutor;
    private final String mLauncherPackage;
    private final OverlayManager mOverlayManager;
    private final Map<String, Set<String>> mTargetPackageToCategories;
    private final String mThemePickerPackage;
    
    static {
        THEME_CATEGORIES = Lists.newArrayList((Object[])new String[] { "android.theme.customization.icon_pack.launcher", "android.theme.customization.adaptive_icon_shape", "android.theme.customization.font", "android.theme.customization.accent_color", "android.theme.customization.icon_pack.android", "android.theme.customization.icon_pack.systemui", "android.theme.customization.icon_pack.settings", "android.theme.customization.icon_pack.themepicker" });
        SYSTEM_USER_CATEGORIES = Sets.newHashSet((Object[])new String[] { "android.theme.customization.accent_color", "android.theme.customization.font", "android.theme.customization.adaptive_icon_shape", "android.theme.customization.icon_pack.android", "android.theme.customization.icon_pack.systemui" });
    }
    
    ThemeOverlayManager(final OverlayManager mOverlayManager, final Executor mExecutor, final String mLauncherPackage, final String mThemePickerPackage) {
        this.mTargetPackageToCategories = (Map<String, Set<String>>)new ArrayMap();
        this.mCategoryToTargetPackage = (Map<String, String>)new ArrayMap();
        this.mOverlayManager = mOverlayManager;
        this.mExecutor = mExecutor;
        this.mLauncherPackage = mLauncherPackage;
        this.mThemePickerPackage = mThemePickerPackage;
        this.mTargetPackageToCategories.put("android", Sets.newHashSet((Object[])new String[] { "android.theme.customization.accent_color", "android.theme.customization.font", "android.theme.customization.adaptive_icon_shape", "android.theme.customization.icon_pack.android" }));
        this.mTargetPackageToCategories.put("com.android.systemui", Sets.newHashSet((Object[])new String[] { "android.theme.customization.icon_pack.systemui" }));
        this.mTargetPackageToCategories.put("com.android.settings", Sets.newHashSet((Object[])new String[] { "android.theme.customization.icon_pack.settings" }));
        this.mTargetPackageToCategories.put(this.mLauncherPackage, Sets.newHashSet((Object[])new String[] { "android.theme.customization.icon_pack.launcher" }));
        this.mTargetPackageToCategories.put(this.mThemePickerPackage, Sets.newHashSet((Object[])new String[] { "android.theme.customization.icon_pack.themepicker" }));
        this.mCategoryToTargetPackage.put("android.theme.customization.accent_color", "android");
        this.mCategoryToTargetPackage.put("android.theme.customization.font", "android");
        this.mCategoryToTargetPackage.put("android.theme.customization.adaptive_icon_shape", "android");
        this.mCategoryToTargetPackage.put("android.theme.customization.icon_pack.android", "android");
        this.mCategoryToTargetPackage.put("android.theme.customization.icon_pack.systemui", "com.android.systemui");
        this.mCategoryToTargetPackage.put("android.theme.customization.icon_pack.settings", "com.android.settings");
        this.mCategoryToTargetPackage.put("android.theme.customization.icon_pack.launcher", this.mLauncherPackage);
        this.mCategoryToTargetPackage.put("android.theme.customization.icon_pack.themepicker", this.mThemePickerPackage);
    }
    
    private void setEnabled(final String s, final String s2, final Set<UserHandle> set, final boolean b) {
        final Iterator<UserHandle> iterator = set.iterator();
        while (iterator.hasNext()) {
            this.setEnabledAsync(s, iterator.next(), b);
        }
        if (!set.contains(UserHandle.SYSTEM) && ThemeOverlayManager.SYSTEM_USER_CATEGORIES.contains(s2)) {
            this.setEnabledAsync(s, UserHandle.SYSTEM, b);
        }
    }
    
    private void setEnabledAsync(final String s, final UserHandle userHandle, final boolean b) {
        this.mExecutor.execute(new _$$Lambda$ThemeOverlayManager$Za_49vHyQK_Yiveq0XqQrGRFGHg(this, s, userHandle, b));
    }
    
    void applyCurrentUserOverlays(final Map<String, String> map, final Set<UserHandle> set) {
        final HashSet<Object> set2 = new HashSet<Object>(ThemeOverlayManager.THEME_CATEGORIES);
        set2.removeAll(map.keySet());
        final Set<Object> set3 = set2.stream().map((Function<? super Object, ?>)new _$$Lambda$ThemeOverlayManager$XHd3K8Vp7fhFb4ucZudIi42URZk(this)).collect((Collector<? super Object, ?, Set<Object>>)Collectors.toSet());
        final ArrayList<Object> list = new ArrayList<Object>();
        set3.forEach(new _$$Lambda$ThemeOverlayManager$Ce247HGCsGLtUA2wdEQCGlPUIx4(this, list));
        final Map<Object, Object> map2 = list.stream().filter(new _$$Lambda$ThemeOverlayManager$FzQkanwY8TEeM97QNlP4yjS7F4s(this)).filter(new _$$Lambda$ThemeOverlayManager$rD72NeWKvvYjih6pAWlvN555mFM(set2)).filter((Predicate<? super Object>)_$$Lambda$ThemeOverlayManager$vK2aROqMaNCgMb7ixs5bp0NF79c.INSTANCE).collect(Collectors.toMap((Function<? super Object, ?>)_$$Lambda$ThemeOverlayManager$tpreaivLMVK4R3Uf26BCg27_Af8.INSTANCE, (Function<? super Object, ?>)_$$Lambda$ThemeOverlayManager$GlioDk646gj_04NkaTcsRN_awI4.INSTANCE));
        for (final String s : ThemeOverlayManager.THEME_CATEGORIES) {
            if (map.containsKey(s)) {
                this.setEnabled(map.get(s), s, set, true);
            }
            else {
                if (!map2.containsKey(s)) {
                    continue;
                }
                this.setEnabled(map2.get(s), s, set, false);
            }
        }
    }
}
