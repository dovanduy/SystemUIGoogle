// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import android.content.res.TypedArray;
import android.icu.text.DisplayContext;
import java.util.Locale;
import android.content.IntentFilter;
import com.android.systemui.Dependency;
import com.android.systemui.R$string;
import com.android.systemui.R$styleable;
import android.os.Handler;
import android.content.Intent;
import android.util.AttributeSet;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.icu.text.DateFormat;
import java.util.Date;
import com.android.systemui.broadcast.BroadcastDispatcher;
import android.widget.TextView;

public class DateView extends TextView
{
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final Date mCurrentTime;
    private DateFormat mDateFormat;
    private String mDatePattern;
    private BroadcastReceiver mIntentReceiver;
    private String mLastText;
    
    public DateView(final Context context, AttributeSet obtainStyledAttributes) {
        super(context, obtainStyledAttributes);
        this.mCurrentTime = new Date();
        this.mIntentReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                final Handler handler = DateView.this.getHandler();
                if (handler == null) {
                    return;
                }
                final String action = intent.getAction();
                if ("android.intent.action.TIME_TICK".equals(action) || "android.intent.action.TIME_SET".equals(action) || "android.intent.action.TIMEZONE_CHANGED".equals(action) || "android.intent.action.LOCALE_CHANGED".equals(action)) {
                    if ("android.intent.action.LOCALE_CHANGED".equals(action) || "android.intent.action.TIMEZONE_CHANGED".equals(action)) {
                        handler.post((Runnable)new _$$Lambda$DateView$1$I_3qZI4QmwEIAfQqo2b2oUNiPII(this));
                    }
                    handler.post((Runnable)new _$$Lambda$DateView$1$v1y3JoGtv68dyea2Bk7AdwrkpMI(this));
                }
            }
        };
        obtainStyledAttributes = (AttributeSet)context.getTheme().obtainStyledAttributes(obtainStyledAttributes, R$styleable.DateView, 0, 0);
        try {
            this.mDatePattern = ((TypedArray)obtainStyledAttributes).getString(R$styleable.DateView_datePattern);
            ((TypedArray)obtainStyledAttributes).recycle();
            if (this.mDatePattern == null) {
                this.mDatePattern = this.getContext().getString(R$string.system_ui_date_pattern);
            }
            this.mBroadcastDispatcher = Dependency.get(BroadcastDispatcher.class);
        }
        finally {
            ((TypedArray)obtainStyledAttributes).recycle();
        }
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIME_TICK");
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        intentFilter.addAction("android.intent.action.LOCALE_CHANGED");
        this.mBroadcastDispatcher.registerReceiverWithHandler(this.mIntentReceiver, intentFilter, Dependency.get(Dependency.TIME_TICK_HANDLER));
        this.updateClock();
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mDateFormat = null;
        this.mBroadcastDispatcher.unregisterReceiver(this.mIntentReceiver);
    }
    
    protected void updateClock() {
        if (this.mDateFormat == null) {
            final DateFormat instanceForSkeleton = DateFormat.getInstanceForSkeleton(this.mDatePattern, Locale.getDefault());
            instanceForSkeleton.setContext(DisplayContext.CAPITALIZATION_FOR_STANDALONE);
            this.mDateFormat = instanceForSkeleton;
        }
        this.mCurrentTime.setTime(System.currentTimeMillis());
        final String format = this.mDateFormat.format(this.mCurrentTime);
        if (!format.equals(this.mLastText)) {
            this.setText((CharSequence)format);
            this.mLastText = format;
        }
    }
}
