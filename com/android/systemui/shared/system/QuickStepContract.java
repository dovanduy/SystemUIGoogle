// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.system;

import java.util.StringJoiner;
import android.view.ViewConfiguration;
import android.content.Context;

public class QuickStepContract
{
    public static final float getQuickStepTouchSlopPx(final Context context) {
        return ViewConfiguration.get(context).getScaledTouchSlop() * 3.0f;
    }
    
    public static String getSystemUiStateString(final int n) {
        final StringJoiner stringJoiner = new StringJoiner("|");
        final String s = "";
        String newElement;
        if ((n & 0x1) != 0x0) {
            newElement = "screen_pinned";
        }
        else {
            newElement = "";
        }
        stringJoiner.add(newElement);
        String newElement2;
        if ((n & 0x80) != 0x0) {
            newElement2 = "overview_disabled";
        }
        else {
            newElement2 = "";
        }
        stringJoiner.add(newElement2);
        String newElement3;
        if ((n & 0x100) != 0x0) {
            newElement3 = "home_disabled";
        }
        else {
            newElement3 = "";
        }
        stringJoiner.add(newElement3);
        String newElement4;
        if ((n & 0x400) != 0x0) {
            newElement4 = "search_disabled";
        }
        else {
            newElement4 = "";
        }
        stringJoiner.add(newElement4);
        String newElement5;
        if ((n & 0x2) != 0x0) {
            newElement5 = "navbar_hidden";
        }
        else {
            newElement5 = "";
        }
        stringJoiner.add(newElement5);
        String newElement6;
        if ((n & 0x4) != 0x0) {
            newElement6 = "notif_visible";
        }
        else {
            newElement6 = "";
        }
        stringJoiner.add(newElement6);
        String newElement7;
        if ((n & 0x800) != 0x0) {
            newElement7 = "qs_visible";
        }
        else {
            newElement7 = "";
        }
        stringJoiner.add(newElement7);
        String newElement8;
        if ((n & 0x40) != 0x0) {
            newElement8 = "keygrd_visible";
        }
        else {
            newElement8 = "";
        }
        stringJoiner.add(newElement8);
        String newElement9;
        if ((n & 0x200) != 0x0) {
            newElement9 = "keygrd_occluded";
        }
        else {
            newElement9 = "";
        }
        stringJoiner.add(newElement9);
        String newElement10;
        if ((n & 0x8) != 0x0) {
            newElement10 = "bouncer_visible";
        }
        else {
            newElement10 = "";
        }
        stringJoiner.add(newElement10);
        String newElement11;
        if ((n & 0x10) != 0x0) {
            newElement11 = "a11y_click";
        }
        else {
            newElement11 = "";
        }
        stringJoiner.add(newElement11);
        String newElement12;
        if ((n & 0x20) != 0x0) {
            newElement12 = "a11y_long_click";
        }
        else {
            newElement12 = "";
        }
        stringJoiner.add(newElement12);
        String newElement13;
        if ((n & 0x1000) != 0x0) {
            newElement13 = "tracing";
        }
        else {
            newElement13 = "";
        }
        stringJoiner.add(newElement13);
        String newElement14;
        if ((n & 0x2000) != 0x0) {
            newElement14 = "asst_gesture_constrain";
        }
        else {
            newElement14 = "";
        }
        stringJoiner.add(newElement14);
        String newElement15 = s;
        if ((n & 0x4000) != 0x0) {
            newElement15 = "bubbles_expanded";
        }
        stringJoiner.add(newElement15);
        return stringJoiner.toString();
    }
    
    public static boolean isAssistantGestureDisabled(final int n) {
        return (n & 0xC0B) != 0x0 || ((n & 0x4) != 0x0 && (n & 0x40) == 0x0);
    }
    
    public static boolean isBackGestureDisabled(final int n) {
        boolean b = false;
        if ((n & 0x8) != 0x0) {
            return false;
        }
        if ((n & 0x46) != 0x0) {
            b = true;
        }
        return b;
    }
    
    public static boolean isGesturalMode(final int n) {
        return n == 2;
    }
    
    public static boolean isLegacyMode(final int n) {
        return n == 0;
    }
    
    public static boolean isSwipeUpMode(final int n) {
        boolean b = true;
        if (n != 1) {
            b = false;
        }
        return b;
    }
}
