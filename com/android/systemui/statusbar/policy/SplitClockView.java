// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import com.android.systemui.R$id;
import android.os.Handler;
import android.os.UserHandle;
import android.content.IntentFilter;
import android.text.format.DateFormat;
import android.app.ActivityManager;
import android.content.Intent;
import android.util.AttributeSet;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.widget.TextClock;
import android.widget.LinearLayout;

public class SplitClockView extends LinearLayout
{
    private TextClock mAmPmView;
    private BroadcastReceiver mIntentReceiver;
    private TextClock mTimeView;
    
    public SplitClockView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mIntentReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                final String action = intent.getAction();
                if ("android.intent.action.TIME_SET".equals(action) || "android.intent.action.TIMEZONE_CHANGED".equals(action) || "android.intent.action.LOCALE_CHANGED".equals(action) || "android.intent.action.CONFIGURATION_CHANGED".equals(action) || "android.intent.action.USER_SWITCHED".equals(action)) {
                    SplitClockView.this.updatePatterns();
                }
            }
        };
    }
    
    private static int getAmPmPartEndIndex(final String s) {
        final int n = s.length() - 1;
        final int n2 = 0;
        int index = n;
        boolean b = false;
        while (true) {
            final int n3 = -1;
            if (index < 0) {
                int n4;
                if (b) {
                    n4 = n2;
                }
                else {
                    n4 = -1;
                }
                return n4;
            }
            final char char1 = s.charAt(index);
            final boolean b2 = char1 == 'a';
            final boolean whitespace = Character.isWhitespace(char1);
            if (b2) {
                b = true;
            }
            if (!b2 && !whitespace) {
                if (index == n) {
                    return -1;
                }
                int n5 = n3;
                if (b) {
                    n5 = index + 1;
                }
                return n5;
            }
            else {
                --index;
            }
        }
    }
    
    private void updatePatterns() {
        final String timeFormatString = DateFormat.getTimeFormatString(this.getContext(), ActivityManager.getCurrentUser());
        final int amPmPartEndIndex = getAmPmPartEndIndex(timeFormatString);
        String substring;
        String substring2;
        if (amPmPartEndIndex == -1) {
            substring = "";
            substring2 = timeFormatString;
        }
        else {
            substring2 = timeFormatString.substring(0, amPmPartEndIndex);
            substring = timeFormatString.substring(amPmPartEndIndex);
        }
        this.mTimeView.setFormat12Hour((CharSequence)substring2);
        this.mTimeView.setFormat24Hour((CharSequence)substring2);
        this.mTimeView.setContentDescriptionFormat12Hour((CharSequence)timeFormatString);
        this.mTimeView.setContentDescriptionFormat24Hour((CharSequence)timeFormatString);
        this.mAmPmView.setFormat12Hour((CharSequence)substring);
        this.mAmPmView.setFormat24Hour((CharSequence)substring);
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        intentFilter.addAction("android.intent.action.LOCALE_CHANGED");
        intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        this.getContext().registerReceiverAsUser(this.mIntentReceiver, UserHandle.ALL, intentFilter, (String)null, (Handler)null);
        this.updatePatterns();
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.getContext().unregisterReceiver(this.mIntentReceiver);
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mTimeView = (TextClock)this.findViewById(R$id.time_view);
        this.mAmPmView = (TextClock)this.findViewById(R$id.am_pm_view);
        this.mTimeView.setShowCurrentUserTime(true);
        this.mAmPmView.setShowCurrentUserTime(true);
    }
}
