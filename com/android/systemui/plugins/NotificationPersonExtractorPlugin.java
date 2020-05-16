// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import android.graphics.drawable.Drawable;
import android.service.notification.StatusBarNotification;
import com.android.systemui.plugins.annotations.ProvidesInterface;
import com.android.systemui.plugins.annotations.DependsOn;

@DependsOn(target = PersonData.class)
@ProvidesInterface(action = "com.android.systemui.action.PEOPLE_HUB_PERSON_EXTRACTOR", version = 1)
public interface NotificationPersonExtractorPlugin extends Plugin
{
    public static final String ACTION = "com.android.systemui.action.PEOPLE_HUB_PERSON_EXTRACTOR";
    public static final int VERSION = 1;
    
    PersonData extractPerson(final StatusBarNotification p0);
    
    default String extractPersonKey(final StatusBarNotification statusBarNotification) {
        return this.extractPerson(statusBarNotification).key;
    }
    
    default boolean isPersonNotification(final StatusBarNotification statusBarNotification) {
        return this.extractPersonKey(statusBarNotification) != null;
    }
    
    @ProvidesInterface(version = 0)
    public static final class PersonData
    {
        public static final int VERSION = 0;
        public final Drawable avatar;
        public final Runnable clickRunnable;
        public final String key;
        public final CharSequence name;
        
        public PersonData(final String key, final CharSequence name, final Drawable avatar, final Runnable clickRunnable) {
            this.key = key;
            this.name = name;
            this.avatar = avatar;
            this.clickRunnable = clickRunnable;
        }
    }
}
