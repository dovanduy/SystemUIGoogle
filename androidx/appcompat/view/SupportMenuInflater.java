// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.view;

import androidx.appcompat.widget.DrawableUtils;
import androidx.appcompat.widget.TintTypedArray;
import android.content.res.TypedArray;
import androidx.appcompat.R$styleable;
import android.view.SubMenu;
import androidx.core.view.MenuItemCompat;
import android.view.View;
import androidx.appcompat.view.menu.MenuItemWrapperICS;
import androidx.appcompat.view.menu.MenuItemImpl;
import java.lang.reflect.Constructor;
import android.util.Log;
import android.graphics.PorterDuff$Mode;
import android.content.res.ColorStateList;
import android.view.InflateException;
import android.view.MenuItem;
import java.lang.reflect.Method;
import android.view.MenuItem$OnMenuItemClickListener;
import android.content.res.XmlResourceParser;
import android.util.Xml;
import androidx.core.internal.view.SupportMenu;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import androidx.core.view.ActionProvider;
import android.view.Menu;
import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;
import android.content.ContextWrapper;
import android.app.Activity;
import android.content.Context;
import android.view.MenuInflater;

public class SupportMenuInflater extends MenuInflater
{
    static final Class<?>[] ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE;
    static final Class<?>[] ACTION_VIEW_CONSTRUCTOR_SIGNATURE;
    final Object[] mActionProviderConstructorArguments;
    final Object[] mActionViewConstructorArguments;
    Context mContext;
    private Object mRealOwner;
    
    static {
        ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE = (ACTION_VIEW_CONSTRUCTOR_SIGNATURE = new Class[] { Context.class });
    }
    
    public SupportMenuInflater(final Context mContext) {
        super(mContext);
        this.mContext = mContext;
        final Object[] array = { mContext };
        this.mActionViewConstructorArguments = array;
        this.mActionProviderConstructorArguments = array;
    }
    
    private Object findRealOwner(final Object o) {
        if (o instanceof Activity) {
            return o;
        }
        if (o instanceof ContextWrapper) {
            return this.findRealOwner(((ContextWrapper)o).getBaseContext());
        }
        return o;
    }
    
    private void parseMenu(final XmlPullParser xmlPullParser, final AttributeSet set, final Menu menu) throws XmlPullParserException, IOException {
        final MenuState menuState = new MenuState(menu);
        int i = xmlPullParser.getEventType();
        String name3;
        while (true) {
            while (i != 2) {
                int n = xmlPullParser.next();
                if ((i = n) == 1) {
                    String anObject = null;
                    int n2;
                    int n3;
                    int n4;
                    String name;
                    int next;
                    for (int j = n2 = 0; j == 0; j = n3, n2 = n4, anObject = name, n = next) {
                        if (n == 1) {
                            throw new RuntimeException("Unexpected end of document");
                        }
                        if (n != 2) {
                            if (n != 3) {
                                n3 = j;
                                n4 = n2;
                                name = anObject;
                            }
                            else {
                                final String name2 = xmlPullParser.getName();
                                if (n2 != 0 && name2.equals(anObject)) {
                                    name = null;
                                    n4 = 0;
                                    n3 = j;
                                }
                                else if (name2.equals("group")) {
                                    menuState.resetGroup();
                                    n3 = j;
                                    n4 = n2;
                                    name = anObject;
                                }
                                else if (name2.equals("item")) {
                                    n3 = j;
                                    n4 = n2;
                                    name = anObject;
                                    if (!menuState.hasAddedItem()) {
                                        final ActionProvider itemActionProvider = menuState.itemActionProvider;
                                        if (itemActionProvider != null && itemActionProvider.hasSubMenu()) {
                                            menuState.addSubMenuItem();
                                            n3 = j;
                                            n4 = n2;
                                            name = anObject;
                                        }
                                        else {
                                            menuState.addItem();
                                            n3 = j;
                                            n4 = n2;
                                            name = anObject;
                                        }
                                    }
                                }
                                else {
                                    n3 = j;
                                    n4 = n2;
                                    name = anObject;
                                    if (name2.equals("menu")) {
                                        n3 = 1;
                                        n4 = n2;
                                        name = anObject;
                                    }
                                }
                            }
                        }
                        else if (n2 != 0) {
                            n3 = j;
                            n4 = n2;
                            name = anObject;
                        }
                        else {
                            name = xmlPullParser.getName();
                            if (name.equals("group")) {
                                menuState.readGroup(set);
                                n3 = j;
                                n4 = n2;
                                name = anObject;
                            }
                            else if (name.equals("item")) {
                                menuState.readItem(set);
                                n3 = j;
                                n4 = n2;
                                name = anObject;
                            }
                            else if (name.equals("menu")) {
                                this.parseMenu(xmlPullParser, set, (Menu)menuState.addSubMenuItem());
                                n3 = j;
                                n4 = n2;
                                name = anObject;
                            }
                            else {
                                n4 = 1;
                                n3 = j;
                            }
                        }
                        next = xmlPullParser.next();
                    }
                    return;
                }
            }
            name3 = xmlPullParser.getName();
            if (name3.equals("menu")) {
                final int n = xmlPullParser.next();
                continue;
            }
            break;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Expecting menu, got ");
        sb.append(name3);
        throw new RuntimeException(sb.toString());
    }
    
    Object getRealOwner() {
        if (this.mRealOwner == null) {
            this.mRealOwner = this.findRealOwner(this.mContext);
        }
        return this.mRealOwner;
    }
    
    public void inflate(final int n, final Menu menu) {
        if (!(menu instanceof SupportMenu)) {
            super.inflate(n, menu);
            return;
        }
        XmlResourceParser layout = null;
        try {
            try {
                final XmlResourceParser xmlResourceParser = layout = this.mContext.getResources().getLayout(n);
                this.parseMenu((XmlPullParser)xmlResourceParser, Xml.asAttributeSet((XmlPullParser)xmlResourceParser), menu);
                if (xmlResourceParser != null) {
                    xmlResourceParser.close();
                }
            }
            finally {
                if (layout != null) {
                    layout.close();
                }
            }
        }
        catch (IOException ex) {}
        catch (XmlPullParserException ex2) {}
    }
    
    private static class InflatedOnMenuItemClickListener implements MenuItem$OnMenuItemClickListener
    {
        private static final Class<?>[] PARAM_TYPES;
        private Method mMethod;
        private Object mRealOwner;
        
        static {
            PARAM_TYPES = new Class[] { MenuItem.class };
        }
        
        public InflatedOnMenuItemClickListener(final Object mRealOwner, final String s) {
            this.mRealOwner = mRealOwner;
            final Class<?> class1 = mRealOwner.getClass();
            try {
                this.mMethod = class1.getMethod(s, InflatedOnMenuItemClickListener.PARAM_TYPES);
            }
            catch (Exception ex2) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Couldn't resolve menu item onClick handler ");
                sb.append(s);
                sb.append(" in class ");
                sb.append(class1.getName());
                final InflateException ex = new InflateException(sb.toString());
                ex.initCause((Throwable)ex2);
                throw ex;
            }
        }
        
        public boolean onMenuItemClick(final MenuItem menuItem) {
            try {
                if (this.mMethod.getReturnType() == Boolean.TYPE) {
                    return (boolean)this.mMethod.invoke(this.mRealOwner, menuItem);
                }
                this.mMethod.invoke(this.mRealOwner, menuItem);
                return true;
            }
            catch (Exception cause) {
                throw new RuntimeException(cause);
            }
        }
    }
    
    private class MenuState
    {
        private int groupCategory;
        private int groupCheckable;
        private boolean groupEnabled;
        private int groupId;
        private int groupOrder;
        private boolean groupVisible;
        ActionProvider itemActionProvider;
        private String itemActionProviderClassName;
        private String itemActionViewClassName;
        private int itemActionViewLayout;
        private boolean itemAdded;
        private int itemAlphabeticModifiers;
        private char itemAlphabeticShortcut;
        private int itemCategoryOrder;
        private int itemCheckable;
        private boolean itemChecked;
        private CharSequence itemContentDescription;
        private boolean itemEnabled;
        private int itemIconResId;
        private ColorStateList itemIconTintList;
        private PorterDuff$Mode itemIconTintMode;
        private int itemId;
        private String itemListenerMethodName;
        private int itemNumericModifiers;
        private char itemNumericShortcut;
        private int itemShowAsAction;
        private CharSequence itemTitle;
        private CharSequence itemTitleCondensed;
        private CharSequence itemTooltipText;
        private boolean itemVisible;
        private Menu menu;
        
        public MenuState(final Menu menu) {
            this.itemIconTintList = null;
            this.itemIconTintMode = null;
            this.menu = menu;
            this.resetGroup();
        }
        
        private char getShortcut(final String s) {
            if (s == null) {
                return '\0';
            }
            return s.charAt(0);
        }
        
        private <T> T newInstance(final String s, final Class<?>[] parameterTypes, final Object[] initargs) {
            try {
                final Constructor<?> constructor = Class.forName(s, false, SupportMenuInflater.this.mContext.getClassLoader()).getConstructor(parameterTypes);
                constructor.setAccessible(true);
                return (T)constructor.newInstance(initargs);
            }
            catch (Exception ex) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Cannot instantiate class: ");
                sb.append(s);
                Log.w("SupportMenuInflater", sb.toString(), (Throwable)ex);
                return null;
            }
        }
        
        private void setItem(final MenuItem menuItem) {
            final MenuItem setEnabled = menuItem.setChecked(this.itemChecked).setVisible(this.itemVisible).setEnabled(this.itemEnabled);
            final int itemCheckable = this.itemCheckable;
            boolean b = false;
            setEnabled.setCheckable(itemCheckable >= 1).setTitleCondensed(this.itemTitleCondensed).setIcon(this.itemIconResId);
            final int itemShowAsAction = this.itemShowAsAction;
            if (itemShowAsAction >= 0) {
                menuItem.setShowAsAction(itemShowAsAction);
            }
            if (this.itemListenerMethodName != null) {
                if (SupportMenuInflater.this.mContext.isRestricted()) {
                    throw new IllegalStateException("The android:onClick attribute cannot be used within a restricted context");
                }
                menuItem.setOnMenuItemClickListener((MenuItem$OnMenuItemClickListener)new InflatedOnMenuItemClickListener(SupportMenuInflater.this.getRealOwner(), this.itemListenerMethodName));
            }
            if (this.itemCheckable >= 2) {
                if (menuItem instanceof MenuItemImpl) {
                    ((MenuItemImpl)menuItem).setExclusiveCheckable(true);
                }
                else if (menuItem instanceof MenuItemWrapperICS) {
                    ((MenuItemWrapperICS)menuItem).setExclusiveCheckable(true);
                }
            }
            final String itemActionViewClassName = this.itemActionViewClassName;
            if (itemActionViewClassName != null) {
                menuItem.setActionView((View)this.newInstance(itemActionViewClassName, SupportMenuInflater.ACTION_VIEW_CONSTRUCTOR_SIGNATURE, SupportMenuInflater.this.mActionViewConstructorArguments));
                b = true;
            }
            final int itemActionViewLayout = this.itemActionViewLayout;
            if (itemActionViewLayout > 0) {
                if (!b) {
                    menuItem.setActionView(itemActionViewLayout);
                }
                else {
                    Log.w("SupportMenuInflater", "Ignoring attribute 'itemActionViewLayout'. Action view already specified.");
                }
            }
            final ActionProvider itemActionProvider = this.itemActionProvider;
            if (itemActionProvider != null) {
                MenuItemCompat.setActionProvider(menuItem, itemActionProvider);
            }
            MenuItemCompat.setContentDescription(menuItem, this.itemContentDescription);
            MenuItemCompat.setTooltipText(menuItem, this.itemTooltipText);
            MenuItemCompat.setAlphabeticShortcut(menuItem, this.itemAlphabeticShortcut, this.itemAlphabeticModifiers);
            MenuItemCompat.setNumericShortcut(menuItem, this.itemNumericShortcut, this.itemNumericModifiers);
            final PorterDuff$Mode itemIconTintMode = this.itemIconTintMode;
            if (itemIconTintMode != null) {
                MenuItemCompat.setIconTintMode(menuItem, itemIconTintMode);
            }
            final ColorStateList itemIconTintList = this.itemIconTintList;
            if (itemIconTintList != null) {
                MenuItemCompat.setIconTintList(menuItem, itemIconTintList);
            }
        }
        
        public void addItem() {
            this.itemAdded = true;
            this.setItem(this.menu.add(this.groupId, this.itemId, this.itemCategoryOrder, this.itemTitle));
        }
        
        public SubMenu addSubMenuItem() {
            this.itemAdded = true;
            final SubMenu addSubMenu = this.menu.addSubMenu(this.groupId, this.itemId, this.itemCategoryOrder, this.itemTitle);
            this.setItem(addSubMenu.getItem());
            return addSubMenu;
        }
        
        public boolean hasAddedItem() {
            return this.itemAdded;
        }
        
        public void readGroup(final AttributeSet set) {
            final TypedArray obtainStyledAttributes = SupportMenuInflater.this.mContext.obtainStyledAttributes(set, R$styleable.MenuGroup);
            this.groupId = obtainStyledAttributes.getResourceId(R$styleable.MenuGroup_android_id, 0);
            this.groupCategory = obtainStyledAttributes.getInt(R$styleable.MenuGroup_android_menuCategory, 0);
            this.groupOrder = obtainStyledAttributes.getInt(R$styleable.MenuGroup_android_orderInCategory, 0);
            this.groupCheckable = obtainStyledAttributes.getInt(R$styleable.MenuGroup_android_checkableBehavior, 0);
            this.groupVisible = obtainStyledAttributes.getBoolean(R$styleable.MenuGroup_android_visible, true);
            this.groupEnabled = obtainStyledAttributes.getBoolean(R$styleable.MenuGroup_android_enabled, true);
            obtainStyledAttributes.recycle();
        }
        
        public void readItem(final AttributeSet set) {
            final TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(SupportMenuInflater.this.mContext, set, R$styleable.MenuItem);
            this.itemId = obtainStyledAttributes.getResourceId(R$styleable.MenuItem_android_id, 0);
            this.itemCategoryOrder = ((obtainStyledAttributes.getInt(R$styleable.MenuItem_android_menuCategory, this.groupCategory) & 0xFFFF0000) | (obtainStyledAttributes.getInt(R$styleable.MenuItem_android_orderInCategory, this.groupOrder) & 0xFFFF));
            this.itemTitle = obtainStyledAttributes.getText(R$styleable.MenuItem_android_title);
            this.itemTitleCondensed = obtainStyledAttributes.getText(R$styleable.MenuItem_android_titleCondensed);
            this.itemIconResId = obtainStyledAttributes.getResourceId(R$styleable.MenuItem_android_icon, 0);
            this.itemAlphabeticShortcut = this.getShortcut(obtainStyledAttributes.getString(R$styleable.MenuItem_android_alphabeticShortcut));
            this.itemAlphabeticModifiers = obtainStyledAttributes.getInt(R$styleable.MenuItem_alphabeticModifiers, 4096);
            this.itemNumericShortcut = this.getShortcut(obtainStyledAttributes.getString(R$styleable.MenuItem_android_numericShortcut));
            this.itemNumericModifiers = obtainStyledAttributes.getInt(R$styleable.MenuItem_numericModifiers, 4096);
            if (obtainStyledAttributes.hasValue(R$styleable.MenuItem_android_checkable)) {
                this.itemCheckable = (obtainStyledAttributes.getBoolean(R$styleable.MenuItem_android_checkable, false) ? 1 : 0);
            }
            else {
                this.itemCheckable = this.groupCheckable;
            }
            this.itemChecked = obtainStyledAttributes.getBoolean(R$styleable.MenuItem_android_checked, false);
            this.itemVisible = obtainStyledAttributes.getBoolean(R$styleable.MenuItem_android_visible, this.groupVisible);
            this.itemEnabled = obtainStyledAttributes.getBoolean(R$styleable.MenuItem_android_enabled, this.groupEnabled);
            this.itemShowAsAction = obtainStyledAttributes.getInt(R$styleable.MenuItem_showAsAction, -1);
            this.itemListenerMethodName = obtainStyledAttributes.getString(R$styleable.MenuItem_android_onClick);
            this.itemActionViewLayout = obtainStyledAttributes.getResourceId(R$styleable.MenuItem_actionLayout, 0);
            this.itemActionViewClassName = obtainStyledAttributes.getString(R$styleable.MenuItem_actionViewClass);
            final String string = obtainStyledAttributes.getString(R$styleable.MenuItem_actionProviderClass);
            this.itemActionProviderClassName = string;
            final boolean b = string != null;
            if (b && this.itemActionViewLayout == 0 && this.itemActionViewClassName == null) {
                this.itemActionProvider = this.newInstance(this.itemActionProviderClassName, SupportMenuInflater.ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE, SupportMenuInflater.this.mActionProviderConstructorArguments);
            }
            else {
                if (b) {
                    Log.w("SupportMenuInflater", "Ignoring attribute 'actionProviderClass'. Action view already specified.");
                }
                this.itemActionProvider = null;
            }
            this.itemContentDescription = obtainStyledAttributes.getText(R$styleable.MenuItem_contentDescription);
            this.itemTooltipText = obtainStyledAttributes.getText(R$styleable.MenuItem_tooltipText);
            if (obtainStyledAttributes.hasValue(R$styleable.MenuItem_iconTintMode)) {
                this.itemIconTintMode = DrawableUtils.parseTintMode(obtainStyledAttributes.getInt(R$styleable.MenuItem_iconTintMode, -1), this.itemIconTintMode);
            }
            else {
                this.itemIconTintMode = null;
            }
            if (obtainStyledAttributes.hasValue(R$styleable.MenuItem_iconTint)) {
                this.itemIconTintList = obtainStyledAttributes.getColorStateList(R$styleable.MenuItem_iconTint);
            }
            else {
                this.itemIconTintList = null;
            }
            obtainStyledAttributes.recycle();
            this.itemAdded = false;
        }
        
        public void resetGroup() {
            this.groupId = 0;
            this.groupCategory = 0;
            this.groupOrder = 0;
            this.groupCheckable = 0;
            this.groupVisible = true;
            this.groupEnabled = true;
        }
    }
}
