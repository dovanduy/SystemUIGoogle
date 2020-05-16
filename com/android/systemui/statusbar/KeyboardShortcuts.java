// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar;

import android.view.accessibility.AccessibilityNodeInfo;
import android.view.WindowManager$KeyboardShortcutsReceiver;
import android.view.WindowManager;
import android.view.InputDevice;
import android.hardware.input.InputManager;
import android.view.View$AccessibilityDelegate;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Bitmap$Config;
import android.view.ViewGroup$LayoutParams;
import android.widget.RelativeLayout$LayoutParams;
import android.widget.ImageView;
import android.content.res.ColorStateList;
import com.android.systemui.R$color;
import com.android.settingslib.Utils;
import android.widget.TextView;
import com.android.systemui.R$drawable;
import android.view.View;
import com.android.systemui.R$id;
import android.widget.LinearLayout;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.app.AlertDialog$Builder;
import android.content.pm.ResolveInfo;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.ComponentName;
import java.util.Collections;
import android.graphics.drawable.Icon;
import com.android.systemui.R$string;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.app.AssistUtils;
import java.util.ArrayList;
import com.android.internal.logging.MetricsLogger;
import java.util.List;
import android.view.KeyboardShortcutGroup;
import android.app.AppGlobals;
import android.view.ContextThemeWrapper;
import android.content.DialogInterface;
import android.os.Looper;
import android.content.pm.IPackageManager;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.app.Dialog;
import android.os.Handler;
import android.content.DialogInterface$OnClickListener;
import android.content.Context;
import android.view.KeyCharacterMap;
import android.view.KeyboardShortcutInfo;
import java.util.Comparator;

public final class KeyboardShortcuts
{
    private static final String TAG = "KeyboardShortcuts";
    private static KeyboardShortcuts sInstance;
    private static final Object sLock;
    private final Comparator<KeyboardShortcutInfo> mApplicationItemsComparator;
    private KeyCharacterMap mBackupKeyCharacterMap;
    private final Context mContext;
    private final DialogInterface$OnClickListener mDialogCloseListener;
    private final Handler mHandler;
    private KeyCharacterMap mKeyCharacterMap;
    private Dialog mKeyboardShortcutsDialog;
    private final SparseArray<Drawable> mModifierDrawables;
    private final int[] mModifierList;
    private final SparseArray<String> mModifierNames;
    private final IPackageManager mPackageManager;
    private final SparseArray<Drawable> mSpecialCharacterDrawables;
    private final SparseArray<String> mSpecialCharacterNames;
    
    static {
        sLock = new Object();
    }
    
    private KeyboardShortcuts(final Context context) {
        this.mSpecialCharacterNames = (SparseArray<String>)new SparseArray();
        this.mModifierNames = (SparseArray<String>)new SparseArray();
        this.mSpecialCharacterDrawables = (SparseArray<Drawable>)new SparseArray();
        this.mModifierDrawables = (SparseArray<Drawable>)new SparseArray();
        this.mModifierList = new int[] { 65536, 4096, 2, 1, 4, 8 };
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mDialogCloseListener = (DialogInterface$OnClickListener)new DialogInterface$OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                KeyboardShortcuts.this.dismissKeyboardShortcuts();
            }
        };
        this.mApplicationItemsComparator = new Comparator<KeyboardShortcutInfo>() {
            @Override
            public int compare(final KeyboardShortcutInfo keyboardShortcutInfo, final KeyboardShortcutInfo keyboardShortcutInfo2) {
                final boolean b = keyboardShortcutInfo.getLabel() == null || keyboardShortcutInfo.getLabel().toString().isEmpty();
                final boolean b2 = keyboardShortcutInfo2.getLabel() == null || keyboardShortcutInfo2.getLabel().toString().isEmpty();
                if (b && b2) {
                    return 0;
                }
                if (b) {
                    return 1;
                }
                if (b2) {
                    return -1;
                }
                return keyboardShortcutInfo.getLabel().toString().compareToIgnoreCase(keyboardShortcutInfo2.getLabel().toString());
            }
        };
        this.mContext = (Context)new ContextThemeWrapper(context, 16974371);
        this.mPackageManager = AppGlobals.getPackageManager();
        this.loadResources(context);
    }
    
    public static void dismiss() {
        synchronized (KeyboardShortcuts.sLock) {
            if (KeyboardShortcuts.sInstance != null) {
                MetricsLogger.hidden(KeyboardShortcuts.sInstance.mContext, 500);
                KeyboardShortcuts.sInstance.dismissKeyboardShortcuts();
                KeyboardShortcuts.sInstance = null;
            }
        }
    }
    
    private void dismissKeyboardShortcuts() {
        final Dialog mKeyboardShortcutsDialog = this.mKeyboardShortcutsDialog;
        if (mKeyboardShortcutsDialog != null) {
            mKeyboardShortcutsDialog.dismiss();
            this.mKeyboardShortcutsDialog = null;
        }
    }
    
    private KeyboardShortcutGroup getDefaultApplicationShortcuts() {
        final int userId = this.mContext.getUserId();
        final ArrayList<KeyboardShortcutInfo> list = new ArrayList<KeyboardShortcutInfo>();
        final ComponentName assistComponentForUser = new AssistUtils(this.mContext).getAssistComponentForUser(userId);
        if (assistComponentForUser != null) {
            PackageInfo packageInfo;
            try {
                packageInfo = this.mPackageManager.getPackageInfo(assistComponentForUser.getPackageName(), 0, userId);
            }
            catch (RemoteException ex) {
                Log.e(KeyboardShortcuts.TAG, "PackageManagerService is dead");
                packageInfo = null;
            }
            if (packageInfo != null) {
                final ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                list.add(new KeyboardShortcutInfo((CharSequence)this.mContext.getString(R$string.keyboard_shortcut_group_applications_assist), Icon.createWithResource(applicationInfo.packageName, applicationInfo.icon), 0, 65536));
            }
        }
        final Icon iconForIntentCategory = this.getIconForIntentCategory("android.intent.category.APP_BROWSER", userId);
        if (iconForIntentCategory != null) {
            list.add(new KeyboardShortcutInfo((CharSequence)this.mContext.getString(R$string.keyboard_shortcut_group_applications_browser), iconForIntentCategory, 30, 65536));
        }
        final Icon iconForIntentCategory2 = this.getIconForIntentCategory("android.intent.category.APP_CONTACTS", userId);
        if (iconForIntentCategory2 != null) {
            list.add(new KeyboardShortcutInfo((CharSequence)this.mContext.getString(R$string.keyboard_shortcut_group_applications_contacts), iconForIntentCategory2, 31, 65536));
        }
        final Icon iconForIntentCategory3 = this.getIconForIntentCategory("android.intent.category.APP_EMAIL", userId);
        if (iconForIntentCategory3 != null) {
            list.add(new KeyboardShortcutInfo((CharSequence)this.mContext.getString(R$string.keyboard_shortcut_group_applications_email), iconForIntentCategory3, 33, 65536));
        }
        final Icon iconForIntentCategory4 = this.getIconForIntentCategory("android.intent.category.APP_MESSAGING", userId);
        if (iconForIntentCategory4 != null) {
            list.add(new KeyboardShortcutInfo((CharSequence)this.mContext.getString(R$string.keyboard_shortcut_group_applications_sms), iconForIntentCategory4, 47, 65536));
        }
        final Icon iconForIntentCategory5 = this.getIconForIntentCategory("android.intent.category.APP_MUSIC", userId);
        if (iconForIntentCategory5 != null) {
            list.add(new KeyboardShortcutInfo((CharSequence)this.mContext.getString(R$string.keyboard_shortcut_group_applications_music), iconForIntentCategory5, 44, 65536));
        }
        final Icon iconForIntentCategory6 = this.getIconForIntentCategory("android.intent.category.APP_CALENDAR", userId);
        if (iconForIntentCategory6 != null) {
            list.add(new KeyboardShortcutInfo((CharSequence)this.mContext.getString(R$string.keyboard_shortcut_group_applications_calendar), iconForIntentCategory6, 40, 65536));
        }
        if (list.size() == 0) {
            return null;
        }
        Collections.sort((List<Object>)list, (Comparator<? super Object>)this.mApplicationItemsComparator);
        return new KeyboardShortcutGroup((CharSequence)this.mContext.getString(R$string.keyboard_shortcut_group_applications), (List)list, true);
    }
    
    private List<StringDrawableContainer> getHumanReadableModifiers(final KeyboardShortcutInfo keyboardShortcutInfo) {
        final ArrayList<StringDrawableContainer> list = new ArrayList<StringDrawableContainer>();
        int modifiers = keyboardShortcutInfo.getModifiers();
        if (modifiers == 0) {
            return list;
        }
        int n = 0;
        while (true) {
            final int[] mModifierList = this.mModifierList;
            if (n >= mModifierList.length) {
                break;
            }
            final int n2 = mModifierList[n];
            int n3 = modifiers;
            if ((modifiers & n2) != 0x0) {
                list.add(new StringDrawableContainer((String)this.mModifierNames.get(n2), (Drawable)this.mModifierDrawables.get(n2)));
                n3 = (modifiers & n2);
            }
            ++n;
            modifiers = n3;
        }
        if (modifiers != 0) {
            return null;
        }
        return list;
    }
    
    private List<StringDrawableContainer> getHumanReadableShortcutKeys(final KeyboardShortcutInfo keyboardShortcutInfo) {
        final List<StringDrawableContainer> humanReadableModifiers = this.getHumanReadableModifiers(keyboardShortcutInfo);
        Drawable drawable = null;
        if (humanReadableModifiers == null) {
            return null;
        }
        String s;
        if (keyboardShortcutInfo.getBaseCharacter() > '\0') {
            s = String.valueOf(keyboardShortcutInfo.getBaseCharacter());
        }
        else if (this.mSpecialCharacterDrawables.get(keyboardShortcutInfo.getKeycode()) != null) {
            drawable = (Drawable)this.mSpecialCharacterDrawables.get(keyboardShortcutInfo.getKeycode());
            s = (String)this.mSpecialCharacterNames.get(keyboardShortcutInfo.getKeycode());
        }
        else if (this.mSpecialCharacterNames.get(keyboardShortcutInfo.getKeycode()) != null) {
            s = (String)this.mSpecialCharacterNames.get(keyboardShortcutInfo.getKeycode());
        }
        else {
            if (keyboardShortcutInfo.getKeycode() == 0) {
                return humanReadableModifiers;
            }
            final char displayLabel = this.mKeyCharacterMap.getDisplayLabel(keyboardShortcutInfo.getKeycode());
            if (displayLabel != '\0') {
                s = String.valueOf(displayLabel);
            }
            else {
                final char displayLabel2 = this.mBackupKeyCharacterMap.getDisplayLabel(keyboardShortcutInfo.getKeycode());
                if (displayLabel2 == '\0') {
                    return null;
                }
                s = String.valueOf(displayLabel2);
            }
        }
        if (s != null) {
            humanReadableModifiers.add(new StringDrawableContainer(s, drawable));
        }
        else {
            Log.w(KeyboardShortcuts.TAG, "Keyboard Shortcut does not have a text representation, skipping.");
        }
        return humanReadableModifiers;
    }
    
    private Icon getIconForIntentCategory(final String s, int icon) {
        final Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory(s);
        final PackageInfo packageInfoForIntent = this.getPackageInfoForIntent(intent, icon);
        if (packageInfoForIntent != null) {
            final ApplicationInfo applicationInfo = packageInfoForIntent.applicationInfo;
            icon = applicationInfo.icon;
            if (icon != 0) {
                return Icon.createWithResource(applicationInfo.packageName, icon);
            }
        }
        return null;
    }
    
    private static KeyboardShortcuts getInstance(final Context context) {
        if (KeyboardShortcuts.sInstance == null) {
            KeyboardShortcuts.sInstance = new KeyboardShortcuts(context);
        }
        return KeyboardShortcuts.sInstance;
    }
    
    private PackageInfo getPackageInfoForIntent(final Intent intent, final int n) {
        try {
            final ResolveInfo resolveIntent = this.mPackageManager.resolveIntent(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), 0, n);
            if (resolveIntent != null && resolveIntent.activityInfo != null) {
                return this.mPackageManager.getPackageInfo(resolveIntent.activityInfo.packageName, 0, n);
            }
            return null;
        }
        catch (RemoteException ex) {
            Log.e(KeyboardShortcuts.TAG, "PackageManagerService is dead", (Throwable)ex);
            return null;
        }
    }
    
    private KeyboardShortcutGroup getSystemShortcuts() {
        final KeyboardShortcutGroup keyboardShortcutGroup = new KeyboardShortcutGroup((CharSequence)this.mContext.getString(R$string.keyboard_shortcut_group_system), true);
        keyboardShortcutGroup.addItem(new KeyboardShortcutInfo((CharSequence)this.mContext.getString(R$string.keyboard_shortcut_group_system_home), 66, 65536));
        keyboardShortcutGroup.addItem(new KeyboardShortcutInfo((CharSequence)this.mContext.getString(R$string.keyboard_shortcut_group_system_back), 67, 65536));
        keyboardShortcutGroup.addItem(new KeyboardShortcutInfo((CharSequence)this.mContext.getString(R$string.keyboard_shortcut_group_system_recents), 61, 2));
        keyboardShortcutGroup.addItem(new KeyboardShortcutInfo((CharSequence)this.mContext.getString(R$string.keyboard_shortcut_group_system_notifications), 42, 65536));
        keyboardShortcutGroup.addItem(new KeyboardShortcutInfo((CharSequence)this.mContext.getString(R$string.keyboard_shortcut_group_system_shortcuts_helper), 76, 65536));
        keyboardShortcutGroup.addItem(new KeyboardShortcutInfo((CharSequence)this.mContext.getString(R$string.keyboard_shortcut_group_system_switch_input), 62, 65536));
        return keyboardShortcutGroup;
    }
    
    private void handleShowKeyboardShortcuts(final List<KeyboardShortcutGroup> list) {
        final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(this.mContext);
        final View inflate = ((LayoutInflater)this.mContext.getSystemService("layout_inflater")).inflate(R$layout.keyboard_shortcuts_view, (ViewGroup)null);
        this.populateKeyboardShortcuts((LinearLayout)inflate.findViewById(R$id.keyboard_shortcuts_container), list);
        alertDialog$Builder.setView(inflate);
        alertDialog$Builder.setPositiveButton(R$string.quick_settings_done, this.mDialogCloseListener);
        (this.mKeyboardShortcutsDialog = (Dialog)alertDialog$Builder.create()).setCanceledOnTouchOutside(true);
        this.mKeyboardShortcutsDialog.getWindow().setType(2008);
        synchronized (KeyboardShortcuts.sLock) {
            if (KeyboardShortcuts.sInstance != null) {
                this.mKeyboardShortcutsDialog.show();
            }
        }
    }
    
    private static boolean isShowing() {
        final KeyboardShortcuts sInstance = KeyboardShortcuts.sInstance;
        if (sInstance != null) {
            final Dialog mKeyboardShortcutsDialog = sInstance.mKeyboardShortcutsDialog;
            if (mKeyboardShortcutsDialog != null && mKeyboardShortcutsDialog.isShowing()) {
                return true;
            }
        }
        return false;
    }
    
    private void loadResources(final Context context) {
        this.mSpecialCharacterNames.put(3, (Object)context.getString(R$string.keyboard_key_home));
        this.mSpecialCharacterNames.put(4, (Object)context.getString(R$string.keyboard_key_back));
        this.mSpecialCharacterNames.put(19, (Object)context.getString(R$string.keyboard_key_dpad_up));
        this.mSpecialCharacterNames.put(20, (Object)context.getString(R$string.keyboard_key_dpad_down));
        this.mSpecialCharacterNames.put(21, (Object)context.getString(R$string.keyboard_key_dpad_left));
        this.mSpecialCharacterNames.put(22, (Object)context.getString(R$string.keyboard_key_dpad_right));
        this.mSpecialCharacterNames.put(23, (Object)context.getString(R$string.keyboard_key_dpad_center));
        this.mSpecialCharacterNames.put(56, (Object)".");
        this.mSpecialCharacterNames.put(61, (Object)context.getString(R$string.keyboard_key_tab));
        this.mSpecialCharacterNames.put(62, (Object)context.getString(R$string.keyboard_key_space));
        this.mSpecialCharacterNames.put(66, (Object)context.getString(R$string.keyboard_key_enter));
        this.mSpecialCharacterNames.put(67, (Object)context.getString(R$string.keyboard_key_backspace));
        this.mSpecialCharacterNames.put(85, (Object)context.getString(R$string.keyboard_key_media_play_pause));
        this.mSpecialCharacterNames.put(86, (Object)context.getString(R$string.keyboard_key_media_stop));
        this.mSpecialCharacterNames.put(87, (Object)context.getString(R$string.keyboard_key_media_next));
        this.mSpecialCharacterNames.put(88, (Object)context.getString(R$string.keyboard_key_media_previous));
        this.mSpecialCharacterNames.put(89, (Object)context.getString(R$string.keyboard_key_media_rewind));
        this.mSpecialCharacterNames.put(90, (Object)context.getString(R$string.keyboard_key_media_fast_forward));
        this.mSpecialCharacterNames.put(92, (Object)context.getString(R$string.keyboard_key_page_up));
        this.mSpecialCharacterNames.put(93, (Object)context.getString(R$string.keyboard_key_page_down));
        this.mSpecialCharacterNames.put(96, (Object)context.getString(R$string.keyboard_key_button_template, new Object[] { "A" }));
        this.mSpecialCharacterNames.put(97, (Object)context.getString(R$string.keyboard_key_button_template, new Object[] { "B" }));
        this.mSpecialCharacterNames.put(98, (Object)context.getString(R$string.keyboard_key_button_template, new Object[] { "C" }));
        this.mSpecialCharacterNames.put(99, (Object)context.getString(R$string.keyboard_key_button_template, new Object[] { "X" }));
        this.mSpecialCharacterNames.put(100, (Object)context.getString(R$string.keyboard_key_button_template, new Object[] { "Y" }));
        this.mSpecialCharacterNames.put(101, (Object)context.getString(R$string.keyboard_key_button_template, new Object[] { "Z" }));
        this.mSpecialCharacterNames.put(102, (Object)context.getString(R$string.keyboard_key_button_template, new Object[] { "L1" }));
        this.mSpecialCharacterNames.put(103, (Object)context.getString(R$string.keyboard_key_button_template, new Object[] { "R1" }));
        this.mSpecialCharacterNames.put(104, (Object)context.getString(R$string.keyboard_key_button_template, new Object[] { "L2" }));
        this.mSpecialCharacterNames.put(105, (Object)context.getString(R$string.keyboard_key_button_template, new Object[] { "R2" }));
        this.mSpecialCharacterNames.put(108, (Object)context.getString(R$string.keyboard_key_button_template, new Object[] { "Start" }));
        this.mSpecialCharacterNames.put(109, (Object)context.getString(R$string.keyboard_key_button_template, new Object[] { "Select" }));
        this.mSpecialCharacterNames.put(110, (Object)context.getString(R$string.keyboard_key_button_template, new Object[] { "Mode" }));
        this.mSpecialCharacterNames.put(112, (Object)context.getString(R$string.keyboard_key_forward_del));
        this.mSpecialCharacterNames.put(111, (Object)"Esc");
        this.mSpecialCharacterNames.put(120, (Object)"SysRq");
        this.mSpecialCharacterNames.put(121, (Object)"Break");
        this.mSpecialCharacterNames.put(116, (Object)"Scroll Lock");
        this.mSpecialCharacterNames.put(122, (Object)context.getString(R$string.keyboard_key_move_home));
        this.mSpecialCharacterNames.put(123, (Object)context.getString(R$string.keyboard_key_move_end));
        this.mSpecialCharacterNames.put(124, (Object)context.getString(R$string.keyboard_key_insert));
        this.mSpecialCharacterNames.put(131, (Object)"F1");
        this.mSpecialCharacterNames.put(132, (Object)"F2");
        this.mSpecialCharacterNames.put(133, (Object)"F3");
        this.mSpecialCharacterNames.put(134, (Object)"F4");
        this.mSpecialCharacterNames.put(135, (Object)"F5");
        this.mSpecialCharacterNames.put(136, (Object)"F6");
        this.mSpecialCharacterNames.put(137, (Object)"F7");
        this.mSpecialCharacterNames.put(138, (Object)"F8");
        this.mSpecialCharacterNames.put(139, (Object)"F9");
        this.mSpecialCharacterNames.put(140, (Object)"F10");
        this.mSpecialCharacterNames.put(141, (Object)"F11");
        this.mSpecialCharacterNames.put(142, (Object)"F12");
        this.mSpecialCharacterNames.put(143, (Object)context.getString(R$string.keyboard_key_num_lock));
        this.mSpecialCharacterNames.put(144, (Object)context.getString(R$string.keyboard_key_numpad_template, new Object[] { "0" }));
        this.mSpecialCharacterNames.put(145, (Object)context.getString(R$string.keyboard_key_numpad_template, new Object[] { "1" }));
        this.mSpecialCharacterNames.put(146, (Object)context.getString(R$string.keyboard_key_numpad_template, new Object[] { "2" }));
        this.mSpecialCharacterNames.put(147, (Object)context.getString(R$string.keyboard_key_numpad_template, new Object[] { "3" }));
        this.mSpecialCharacterNames.put(148, (Object)context.getString(R$string.keyboard_key_numpad_template, new Object[] { "4" }));
        this.mSpecialCharacterNames.put(149, (Object)context.getString(R$string.keyboard_key_numpad_template, new Object[] { "5" }));
        this.mSpecialCharacterNames.put(150, (Object)context.getString(R$string.keyboard_key_numpad_template, new Object[] { "6" }));
        this.mSpecialCharacterNames.put(151, (Object)context.getString(R$string.keyboard_key_numpad_template, new Object[] { "7" }));
        this.mSpecialCharacterNames.put(152, (Object)context.getString(R$string.keyboard_key_numpad_template, new Object[] { "8" }));
        this.mSpecialCharacterNames.put(153, (Object)context.getString(R$string.keyboard_key_numpad_template, new Object[] { "9" }));
        this.mSpecialCharacterNames.put(154, (Object)context.getString(R$string.keyboard_key_numpad_template, new Object[] { "/" }));
        this.mSpecialCharacterNames.put(155, (Object)context.getString(R$string.keyboard_key_numpad_template, new Object[] { "*" }));
        this.mSpecialCharacterNames.put(156, (Object)context.getString(R$string.keyboard_key_numpad_template, new Object[] { "-" }));
        this.mSpecialCharacterNames.put(157, (Object)context.getString(R$string.keyboard_key_numpad_template, new Object[] { "+" }));
        this.mSpecialCharacterNames.put(158, (Object)context.getString(R$string.keyboard_key_numpad_template, new Object[] { "." }));
        this.mSpecialCharacterNames.put(159, (Object)context.getString(R$string.keyboard_key_numpad_template, new Object[] { "," }));
        this.mSpecialCharacterNames.put(160, (Object)context.getString(R$string.keyboard_key_numpad_template, new Object[] { context.getString(R$string.keyboard_key_enter) }));
        this.mSpecialCharacterNames.put(161, (Object)context.getString(R$string.keyboard_key_numpad_template, new Object[] { "=" }));
        this.mSpecialCharacterNames.put(162, (Object)context.getString(R$string.keyboard_key_numpad_template, new Object[] { "(" }));
        this.mSpecialCharacterNames.put(163, (Object)context.getString(R$string.keyboard_key_numpad_template, new Object[] { ")" }));
        this.mSpecialCharacterNames.put(211, (Object)"\u534a\u89d2/\u5168\u89d2");
        this.mSpecialCharacterNames.put(212, (Object)"\u82f1\u6570");
        this.mSpecialCharacterNames.put(213, (Object)"\u7121\u5909\u63db");
        this.mSpecialCharacterNames.put(214, (Object)"\u5909\u63db");
        this.mSpecialCharacterNames.put(215, (Object)"\u304b\u306a");
        this.mModifierNames.put(65536, (Object)"Meta");
        this.mModifierNames.put(4096, (Object)"Ctrl");
        this.mModifierNames.put(2, (Object)"Alt");
        this.mModifierNames.put(1, (Object)"Shift");
        this.mModifierNames.put(4, (Object)"Sym");
        this.mModifierNames.put(8, (Object)"Fn");
        this.mSpecialCharacterDrawables.put(67, (Object)context.getDrawable(R$drawable.ic_ksh_key_backspace));
        this.mSpecialCharacterDrawables.put(66, (Object)context.getDrawable(R$drawable.ic_ksh_key_enter));
        this.mSpecialCharacterDrawables.put(19, (Object)context.getDrawable(R$drawable.ic_ksh_key_up));
        this.mSpecialCharacterDrawables.put(22, (Object)context.getDrawable(R$drawable.ic_ksh_key_right));
        this.mSpecialCharacterDrawables.put(20, (Object)context.getDrawable(R$drawable.ic_ksh_key_down));
        this.mSpecialCharacterDrawables.put(21, (Object)context.getDrawable(R$drawable.ic_ksh_key_left));
        this.mModifierDrawables.put(65536, (Object)context.getDrawable(R$drawable.ic_ksh_key_meta));
    }
    
    private void populateKeyboardShortcuts(final LinearLayout linearLayout, final List<KeyboardShortcutGroup> list) {
        LinearLayout linearLayout2 = linearLayout;
        final LayoutInflater from = LayoutInflater.from(this.mContext);
        int size = list.size();
        final int keyboard_shortcuts_key_view = R$layout.keyboard_shortcuts_key_view;
        int n = 0;
        final TextView textView = (TextView)from.inflate(keyboard_shortcuts_key_view, (ViewGroup)null, false);
        textView.measure(0, 0);
        final int measuredHeight = textView.getMeasuredHeight();
        int n2 = textView.getMeasuredHeight() - textView.getPaddingTop() - textView.getPaddingBottom();
        int n3;
        int n9;
        for (int i = 0; i < size; ++i, size = n3, n = n9) {
            final KeyboardShortcutGroup keyboardShortcutGroup = list.get(i);
            final TextView textView2 = (TextView)from.inflate(R$layout.keyboard_shortcuts_category_title, (ViewGroup)linearLayout2, (boolean)(n != 0));
            textView2.setText(keyboardShortcutGroup.getLabel());
            ColorStateList textColor;
            if (keyboardShortcutGroup.isSystemGroup()) {
                textColor = Utils.getColorAccent(this.mContext);
            }
            else {
                textColor = ColorStateList.valueOf(this.mContext.getColor(R$color.ksh_application_group_color));
            }
            textView2.setTextColor(textColor);
            linearLayout2.addView((View)textView2);
            final LinearLayout linearLayout3 = (LinearLayout)from.inflate(R$layout.keyboard_shortcuts_container, (ViewGroup)linearLayout2, (boolean)(n != 0));
            final int size2 = keyboardShortcutGroup.getItems().size();
            int j = n;
            final KeyboardShortcutGroup keyboardShortcutGroup2 = keyboardShortcutGroup;
            boolean b = n != 0;
            n3 = size;
            while (j < size2) {
                final KeyboardShortcutInfo keyboardShortcutInfo = keyboardShortcutGroup2.getItems().get(j);
                final List<StringDrawableContainer> humanReadableShortcutKeys = this.getHumanReadableShortcutKeys(keyboardShortcutInfo);
                int n5;
                int n6;
                if (humanReadableShortcutKeys == null) {
                    Log.w(KeyboardShortcuts.TAG, "Keyboard Shortcut contains unsupported keys, skipping.");
                    final int n4 = i;
                    n5 = n2;
                    n6 = n4;
                }
                else {
                    final View inflate = from.inflate(R$layout.keyboard_shortcut_app_item, (ViewGroup)linearLayout3, b);
                    if (keyboardShortcutInfo.getIcon() != null) {
                        final ImageView imageView = (ImageView)inflate.findViewById(R$id.keyboard_shortcuts_icon);
                        imageView.setImageIcon(keyboardShortcutInfo.getIcon());
                        imageView.setVisibility(0);
                    }
                    final TextView textView3 = (TextView)inflate.findViewById(R$id.keyboard_shortcuts_keyword);
                    textView3.setText(keyboardShortcutInfo.getLabel());
                    if (keyboardShortcutInfo.getIcon() != null) {
                        final RelativeLayout$LayoutParams layoutParams = (RelativeLayout$LayoutParams)textView3.getLayoutParams();
                        layoutParams.removeRule(20);
                        textView3.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
                    }
                    final ViewGroup viewGroup = (ViewGroup)inflate.findViewById(R$id.keyboard_shortcuts_item_container);
                    for (int size3 = humanReadableShortcutKeys.size(), k = 0; k < size3; ++k) {
                        final StringDrawableContainer stringDrawableContainer = humanReadableShortcutKeys.get(k);
                        if (stringDrawableContainer.mDrawable != null) {
                            final ImageView imageView2 = (ImageView)from.inflate(R$layout.keyboard_shortcuts_key_icon_view, viewGroup, false);
                            final Bitmap bitmap = Bitmap.createBitmap(n2, n2, Bitmap$Config.ARGB_8888);
                            final Canvas canvas = new Canvas(bitmap);
                            stringDrawableContainer.mDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                            stringDrawableContainer.mDrawable.draw(canvas);
                            imageView2.setImageBitmap(bitmap);
                            imageView2.setImportantForAccessibility(1);
                            imageView2.setAccessibilityDelegate((View$AccessibilityDelegate)new ShortcutKeyAccessibilityDelegate(stringDrawableContainer.mString));
                            viewGroup.addView((View)imageView2);
                        }
                        else if (stringDrawableContainer.mString != null) {
                            final TextView textView4 = (TextView)from.inflate(R$layout.keyboard_shortcuts_key_view, viewGroup, false);
                            textView4.setMinimumWidth(measuredHeight);
                            textView4.setText((CharSequence)stringDrawableContainer.mString);
                            textView4.setAccessibilityDelegate((View$AccessibilityDelegate)new ShortcutKeyAccessibilityDelegate(stringDrawableContainer.mString));
                            viewGroup.addView((View)textView4);
                        }
                    }
                    final int n7 = i;
                    n5 = n2;
                    linearLayout3.addView(inflate);
                    n6 = n7;
                }
                ++j;
                linearLayout2 = linearLayout;
                final int n8 = n5;
                b = false;
                i = n6;
                n2 = n8;
            }
            linearLayout2.addView((View)linearLayout3);
            if (i < n3 - 1) {
                linearLayout2.addView(from.inflate(R$layout.keyboard_shortcuts_category_separator, (ViewGroup)linearLayout2, false));
            }
            n9 = 0;
        }
    }
    
    private void retrieveKeyCharacterMap(int i) {
        final InputManager instance = InputManager.getInstance();
        this.mBackupKeyCharacterMap = instance.getInputDevice(-1).getKeyCharacterMap();
        if (i != -1) {
            final InputDevice inputDevice = instance.getInputDevice(i);
            if (inputDevice != null) {
                this.mKeyCharacterMap = inputDevice.getKeyCharacterMap();
                return;
            }
        }
        int[] inputDeviceIds;
        InputDevice inputDevice2;
        for (inputDeviceIds = instance.getInputDeviceIds(), i = 0; i < inputDeviceIds.length; ++i) {
            inputDevice2 = instance.getInputDevice(inputDeviceIds[i]);
            if (inputDevice2.getId() != -1 && inputDevice2.isFullKeyboard()) {
                this.mKeyCharacterMap = inputDevice2.getKeyCharacterMap();
                return;
            }
        }
        this.mKeyCharacterMap = this.mBackupKeyCharacterMap;
    }
    
    public static void show(final Context obj, final int n) {
        MetricsLogger.visible(obj, 500);
        synchronized (KeyboardShortcuts.sLock) {
            if (KeyboardShortcuts.sInstance != null && !KeyboardShortcuts.sInstance.mContext.equals(obj)) {
                dismiss();
            }
            getInstance(obj).showKeyboardShortcuts(n);
        }
    }
    
    private void showKeyboardShortcuts(final int n) {
        this.retrieveKeyCharacterMap(n);
        ((WindowManager)this.mContext.getSystemService("window")).requestAppKeyboardShortcuts((WindowManager$KeyboardShortcutsReceiver)new WindowManager$KeyboardShortcutsReceiver() {
            public void onKeyboardShortcutsReceived(final List<KeyboardShortcutGroup> list) {
                list.add(KeyboardShortcuts.this.getSystemShortcuts());
                final KeyboardShortcutGroup access$200 = KeyboardShortcuts.this.getDefaultApplicationShortcuts();
                if (access$200 != null) {
                    list.add(access$200);
                }
                KeyboardShortcuts.this.showKeyboardShortcutsDialog(list);
            }
        }, n);
    }
    
    private void showKeyboardShortcutsDialog(final List<KeyboardShortcutGroup> list) {
        this.mHandler.post((Runnable)new Runnable() {
            @Override
            public void run() {
                KeyboardShortcuts.this.handleShowKeyboardShortcuts(list);
            }
        });
    }
    
    public static void toggle(final Context context, final int n) {
        synchronized (KeyboardShortcuts.sLock) {
            if (isShowing()) {
                dismiss();
            }
            else {
                show(context, n);
            }
        }
    }
    
    private final class ShortcutKeyAccessibilityDelegate extends View$AccessibilityDelegate
    {
        private String mContentDescription;
        
        ShortcutKeyAccessibilityDelegate(final KeyboardShortcuts keyboardShortcuts, final String mContentDescription) {
            this.mContentDescription = mContentDescription;
        }
        
        public void onInitializeAccessibilityNodeInfo(final View view, final AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
            final String mContentDescription = this.mContentDescription;
            if (mContentDescription != null) {
                accessibilityNodeInfo.setContentDescription((CharSequence)mContentDescription.toLowerCase());
            }
        }
    }
    
    private static final class StringDrawableContainer
    {
        public Drawable mDrawable;
        public String mString;
        
        StringDrawableContainer(final String mString, final Drawable mDrawable) {
            this.mString = mString;
            this.mDrawable = mDrawable;
        }
    }
}
