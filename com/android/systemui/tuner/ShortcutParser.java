// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import java.util.Iterator;
import android.util.Xml;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import android.content.res.TypedArray;
import org.xmlpull.v1.XmlPullParser;
import android.content.Intent;
import android.graphics.drawable.Icon;
import com.android.internal.R$styleable;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager$NameNotFoundException;
import android.content.ComponentName;
import android.content.res.Resources;
import android.content.Context;
import android.util.AttributeSet;

public class ShortcutParser
{
    private AttributeSet mAttrs;
    private final Context mContext;
    private final String mName;
    private final String mPkg;
    private final int mResId;
    private Resources mResources;
    
    public ShortcutParser(final Context context, final ComponentName componentName) throws PackageManager$NameNotFoundException {
        this(context, componentName.getPackageName(), componentName.getClassName(), getResId(context, componentName));
    }
    
    public ShortcutParser(final Context mContext, final String mPkg, final String mName, final int mResId) {
        this.mContext = mContext;
        this.mPkg = mPkg;
        this.mResId = mResId;
        this.mName = mName;
    }
    
    private static int getResId(final Context context, final ComponentName componentName) throws PackageManager$NameNotFoundException {
        final ActivityInfo activityInfo = context.getPackageManager().getActivityInfo(componentName, 128);
        final Bundle metaData = activityInfo.metaData;
        int int1;
        if (metaData != null && metaData.containsKey("android.app.shortcuts")) {
            int1 = activityInfo.metaData.getInt("android.app.shortcuts");
        }
        else {
            int1 = 0;
        }
        return int1;
    }
    
    private Shortcut parseShortcut(final XmlResourceParser xmlResourceParser) throws IOException, XmlPullParserException {
        final TypedArray obtainAttributes = this.mResources.obtainAttributes(this.mAttrs, R$styleable.Shortcut);
        final Shortcut shortcut = new Shortcut();
        if (!obtainAttributes.getBoolean(1, true)) {
            return null;
        }
        final String string = obtainAttributes.getString(2);
        final int resourceId = obtainAttributes.getResourceId(0, 0);
        final int resourceId2 = obtainAttributes.getResourceId(3, 0);
        final String mPkg = this.mPkg;
        shortcut.pkg = mPkg;
        shortcut.icon = Icon.createWithResource(mPkg, resourceId);
        shortcut.id = string;
        shortcut.label = this.mResources.getString(resourceId2);
        shortcut.name = this.mName;
        while (true) {
            final int next = xmlResourceParser.next();
            if (next == 3) {
                break;
            }
            if (next != 2) {
                continue;
            }
            if (!xmlResourceParser.getName().equals("intent")) {
                continue;
            }
            shortcut.intent = Intent.parseIntent(this.mResources, (XmlPullParser)xmlResourceParser, this.mAttrs);
        }
        Shortcut shortcut2;
        if (shortcut.intent != null) {
            shortcut2 = shortcut;
        }
        else {
            shortcut2 = null;
        }
        return shortcut2;
    }
    
    public List<Shortcut> getShortcuts() {
        final ArrayList<Shortcut> list = new ArrayList<Shortcut>();
        if (this.mResId != 0) {
            try {
                final Resources resourcesForApplication = this.mContext.getPackageManager().getResourcesForApplication(this.mPkg);
                this.mResources = resourcesForApplication;
                final XmlResourceParser xml = resourcesForApplication.getXml(this.mResId);
                this.mAttrs = Xml.asAttributeSet((XmlPullParser)xml);
                while (true) {
                    final int next = xml.next();
                    if (next == 1) {
                        break;
                    }
                    if (next != 2) {
                        continue;
                    }
                    if (!xml.getName().equals("shortcut")) {
                        continue;
                    }
                    final Shortcut shortcut = this.parseShortcut(xml);
                    if (shortcut == null) {
                        continue;
                    }
                    list.add(shortcut);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return list;
    }
    
    public static class Shortcut
    {
        public Icon icon;
        public String id;
        public Intent intent;
        public String label;
        public String name;
        public String pkg;
        
        public static Shortcut create(final Context context, final String s) {
            final String[] split = s.split("::");
            try {
                for (final Shortcut shortcut : new ShortcutParser(context, new ComponentName(split[0], split[1])).getShortcuts()) {
                    if (shortcut.id.equals(split[2])) {
                        return shortcut;
                    }
                }
                return null;
            }
            catch (PackageManager$NameNotFoundException ex) {
                return null;
            }
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(this.pkg);
            sb.append("::");
            sb.append(this.name);
            sb.append("::");
            sb.append(this.id);
            return sb.toString();
        }
    }
}
