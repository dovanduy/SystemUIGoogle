// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.notification.icon;

import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.statusbar.StatusBarIconView;

public final class IconPack
{
    private final StatusBarIconView mAodIcon;
    private final boolean mAreIconsAvailable;
    private final StatusBarIconView mCenteredIcon;
    private boolean mIsImportantConversation;
    private StatusBarIcon mPeopleAvatarDescriptor;
    private final StatusBarIconView mShelfIcon;
    private StatusBarIcon mSmallIconDescriptor;
    private final StatusBarIconView mStatusBarIcon;
    
    private IconPack(final boolean mAreIconsAvailable, final StatusBarIconView mStatusBarIcon, final StatusBarIconView mShelfIcon, final StatusBarIconView mAodIcon, final StatusBarIconView mCenteredIcon, final IconPack iconPack) {
        this.mAreIconsAvailable = mAreIconsAvailable;
        this.mStatusBarIcon = mStatusBarIcon;
        this.mShelfIcon = mShelfIcon;
        this.mCenteredIcon = mCenteredIcon;
        this.mAodIcon = mAodIcon;
        if (iconPack != null) {
            this.mIsImportantConversation = iconPack.mIsImportantConversation;
        }
    }
    
    public static IconPack buildEmptyPack(final IconPack iconPack) {
        return new IconPack(false, null, null, null, null, iconPack);
    }
    
    public static IconPack buildPack(final StatusBarIconView statusBarIconView, final StatusBarIconView statusBarIconView2, final StatusBarIconView statusBarIconView3, final StatusBarIconView statusBarIconView4, final IconPack iconPack) {
        return new IconPack(true, statusBarIconView, statusBarIconView2, statusBarIconView3, statusBarIconView4, iconPack);
    }
    
    public StatusBarIconView getAodIcon() {
        return this.mAodIcon;
    }
    
    public boolean getAreIconsAvailable() {
        return this.mAreIconsAvailable;
    }
    
    public StatusBarIconView getCenteredIcon() {
        return this.mCenteredIcon;
    }
    
    StatusBarIcon getPeopleAvatarDescriptor() {
        return this.mPeopleAvatarDescriptor;
    }
    
    public StatusBarIconView getShelfIcon() {
        return this.mShelfIcon;
    }
    
    StatusBarIcon getSmallIconDescriptor() {
        return this.mSmallIconDescriptor;
    }
    
    public StatusBarIconView getStatusBarIcon() {
        return this.mStatusBarIcon;
    }
    
    boolean isImportantConversation() {
        return this.mIsImportantConversation;
    }
    
    void setImportantConversation(final boolean mIsImportantConversation) {
        this.mIsImportantConversation = mIsImportantConversation;
    }
    
    void setPeopleAvatarDescriptor(final StatusBarIcon mPeopleAvatarDescriptor) {
        this.mPeopleAvatarDescriptor = mPeopleAvatarDescriptor;
    }
    
    void setSmallIconDescriptor(final StatusBarIcon mSmallIconDescriptor) {
        this.mSmallIconDescriptor = mSmallIconDescriptor;
    }
}
