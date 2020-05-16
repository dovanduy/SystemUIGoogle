// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import kotlin.jvm.internal.Intrinsics;
import android.graphics.drawable.Drawable;
import java.util.List;

public final class KeyguardMedia
{
    private final List<Drawable> actionIcons;
    private final String app;
    private final Drawable appIcon;
    private final String artist;
    private final Drawable artwork;
    private final int backgroundColor;
    private final int foregroundColor;
    private final String song;
    
    public KeyguardMedia(final int foregroundColor, final int backgroundColor, final String app, final Drawable appIcon, final String artist, final String song, final Drawable artwork, final List<Drawable> actionIcons) {
        Intrinsics.checkParameterIsNotNull(actionIcons, "actionIcons");
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.app = app;
        this.appIcon = appIcon;
        this.artist = artist;
        this.song = song;
        this.artwork = artwork;
        this.actionIcons = actionIcons;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this != o) {
            if (o instanceof KeyguardMedia) {
                final KeyguardMedia keyguardMedia = (KeyguardMedia)o;
                if (this.foregroundColor == keyguardMedia.foregroundColor && this.backgroundColor == keyguardMedia.backgroundColor && Intrinsics.areEqual(this.app, keyguardMedia.app) && Intrinsics.areEqual(this.appIcon, keyguardMedia.appIcon) && Intrinsics.areEqual(this.artist, keyguardMedia.artist) && Intrinsics.areEqual(this.song, keyguardMedia.song) && Intrinsics.areEqual(this.artwork, keyguardMedia.artwork) && Intrinsics.areEqual(this.actionIcons, keyguardMedia.actionIcons)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public final List<Drawable> getActionIcons() {
        return this.actionIcons;
    }
    
    public final String getApp() {
        return this.app;
    }
    
    public final Drawable getAppIcon() {
        return this.appIcon;
    }
    
    public final String getArtist() {
        return this.artist;
    }
    
    public final Drawable getArtwork() {
        return this.artwork;
    }
    
    public final int getBackgroundColor() {
        return this.backgroundColor;
    }
    
    public final int getForegroundColor() {
        return this.foregroundColor;
    }
    
    public final String getSong() {
        return this.song;
    }
    
    @Override
    public int hashCode() {
        final int hashCode = Integer.hashCode(this.foregroundColor);
        final int hashCode2 = Integer.hashCode(this.backgroundColor);
        final String app = this.app;
        int hashCode3 = 0;
        int hashCode4;
        if (app != null) {
            hashCode4 = app.hashCode();
        }
        else {
            hashCode4 = 0;
        }
        final Drawable appIcon = this.appIcon;
        int hashCode5;
        if (appIcon != null) {
            hashCode5 = appIcon.hashCode();
        }
        else {
            hashCode5 = 0;
        }
        final String artist = this.artist;
        int hashCode6;
        if (artist != null) {
            hashCode6 = artist.hashCode();
        }
        else {
            hashCode6 = 0;
        }
        final String song = this.song;
        int hashCode7;
        if (song != null) {
            hashCode7 = song.hashCode();
        }
        else {
            hashCode7 = 0;
        }
        final Drawable artwork = this.artwork;
        int hashCode8;
        if (artwork != null) {
            hashCode8 = artwork.hashCode();
        }
        else {
            hashCode8 = 0;
        }
        final List<Drawable> actionIcons = this.actionIcons;
        if (actionIcons != null) {
            hashCode3 = actionIcons.hashCode();
        }
        return ((((((hashCode * 31 + hashCode2) * 31 + hashCode4) * 31 + hashCode5) * 31 + hashCode6) * 31 + hashCode7) * 31 + hashCode8) * 31 + hashCode3;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("KeyguardMedia(foregroundColor=");
        sb.append(this.foregroundColor);
        sb.append(", backgroundColor=");
        sb.append(this.backgroundColor);
        sb.append(", app=");
        sb.append(this.app);
        sb.append(", appIcon=");
        sb.append(this.appIcon);
        sb.append(", artist=");
        sb.append(this.artist);
        sb.append(", song=");
        sb.append(this.song);
        sb.append(", artwork=");
        sb.append(this.artwork);
        sb.append(", actionIcons=");
        sb.append(this.actionIcons);
        sb.append(")");
        return sb.toString();
    }
}
