// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard.clock;

import com.android.systemui.R$id;
import android.text.format.DateFormat;
import java.text.SimpleDateFormat;
import android.util.AttributeSet;
import android.content.Context;
import java.util.TimeZone;
import java.util.Calendar;
import android.widget.ImageView;
import android.widget.FrameLayout;

public class ImageClock extends FrameLayout
{
    private String mDescFormat;
    private ImageView mHourHand;
    private ImageView mMinuteHand;
    private final Calendar mTime;
    private TimeZone mTimeZone;
    
    public ImageClock(final Context context) {
        this(context, null);
    }
    
    public ImageClock(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public ImageClock(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mTime = Calendar.getInstance(TimeZone.getDefault());
        this.mDescFormat = ((SimpleDateFormat)DateFormat.getTimeFormat(context)).toLocalizedPattern();
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final Calendar mTime = this.mTime;
        TimeZone timeZone = this.mTimeZone;
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        mTime.setTimeZone(timeZone);
        this.onTimeChanged();
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mHourHand = (ImageView)this.findViewById(R$id.hour_hand);
        this.mMinuteHand = (ImageView)this.findViewById(R$id.minute_hand);
    }
    
    public void onTimeChanged() {
        this.mTime.setTimeInMillis(System.currentTimeMillis());
        this.mHourHand.setRotation(this.mTime.get(10) * 30.0f + this.mTime.get(12) * 0.5f);
        this.mMinuteHand.setRotation(this.mTime.get(12) * 6.0f);
        this.setContentDescription(DateFormat.format((CharSequence)this.mDescFormat, this.mTime));
        this.invalidate();
    }
}
