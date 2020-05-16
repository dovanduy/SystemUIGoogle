// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import android.metrics.LogMaker;
import android.text.TextUtils;
import android.widget.Switch;
import android.util.Log;
import android.provider.Settings$Global;
import android.content.Intent;
import java.time.LocalTime;
import java.util.TimeZone;
import android.text.format.DateFormat;
import java.util.Calendar;
import com.android.systemui.R$string;
import android.os.Handler;
import android.os.Looper;
import com.android.systemui.qs.QSHost;
import android.hardware.display.ColorDisplayManager;
import android.hardware.display.NightDisplayListener;
import android.hardware.display.NightDisplayListener$Callback;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;

public class NightDisplayTile extends QSTileImpl<BooleanState> implements NightDisplayListener$Callback
{
    private boolean mIsListening;
    private NightDisplayListener mListener;
    private final ColorDisplayManager mManager;
    
    public NightDisplayTile(final QSHost qsHost) {
        super(qsHost);
        this.mManager = (ColorDisplayManager)super.mContext.getSystemService((Class)ColorDisplayManager.class);
        this.mListener = new NightDisplayListener(super.mContext, new Handler(Looper.myLooper()));
    }
    
    private String getSecondaryLabel(final boolean b) {
        final int nightDisplayAutoMode = this.mManager.getNightDisplayAutoMode();
        if (nightDisplayAutoMode == 1) {
            LocalTime localTime;
            int n;
            if (b) {
                localTime = this.mManager.getNightDisplayCustomEndTime();
                n = R$string.quick_settings_secondary_label_until;
            }
            else {
                localTime = this.mManager.getNightDisplayCustomStartTime();
                n = R$string.quick_settings_night_secondary_label_on_at;
            }
            final Calendar instance = Calendar.getInstance();
            final java.text.DateFormat timeFormat = DateFormat.getTimeFormat(super.mContext);
            timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            instance.setTimeZone(timeFormat.getTimeZone());
            instance.set(11, localTime.getHour());
            instance.set(12, localTime.getMinute());
            instance.set(13, 0);
            instance.set(14, 0);
            return super.mContext.getString(n, new Object[] { timeFormat.format(instance.getTime()) });
        }
        if (nightDisplayAutoMode != 2) {
            return null;
        }
        String s;
        if (b) {
            s = super.mContext.getString(R$string.quick_settings_night_secondary_label_until_sunrise);
        }
        else {
            s = super.mContext.getString(R$string.quick_settings_night_secondary_label_on_at_sunset);
        }
        return s;
    }
    
    @Override
    public Intent getLongClickIntent() {
        return new Intent("android.settings.NIGHT_DISPLAY_SETTINGS");
    }
    
    @Override
    public int getMetricsCategory() {
        return 491;
    }
    
    public CharSequence getTileLabel() {
        return super.mContext.getString(R$string.quick_settings_night_display_label);
    }
    
    @Override
    protected void handleClick() {
        if ("1".equals(Settings$Global.getString(super.mContext.getContentResolver(), "night_display_forced_auto_mode_available")) && this.mManager.getNightDisplayAutoModeRaw() == -1) {
            this.mManager.setNightDisplayAutoMode(1);
            Log.i("NightDisplayTile", "Enrolled in forced night display auto mode");
        }
        this.mManager.setNightDisplayActivated(((BooleanState)super.mState).value ^ true);
    }
    
    @Override
    protected void handleSetListening(final boolean mIsListening) {
        super.handleSetListening(mIsListening);
        this.mIsListening = mIsListening;
        if (mIsListening) {
            this.mListener.setCallback((NightDisplayListener$Callback)this);
            this.refreshState();
        }
        else {
            this.mListener.setCallback((NightDisplayListener$Callback)null);
        }
    }
    
    @Override
    protected void handleUpdateState(final BooleanState booleanState, final Object o) {
        booleanState.value = this.mManager.isNightDisplayActivated();
        booleanState.label = super.mContext.getString(R$string.quick_settings_night_display_label);
        booleanState.icon = ResourceIcon.get(17302799);
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        int state;
        if (booleanState.value) {
            state = 2;
        }
        else {
            state = 1;
        }
        booleanState.state = state;
        final String secondaryLabel = this.getSecondaryLabel(booleanState.value);
        booleanState.secondaryLabel = secondaryLabel;
        CharSequence contentDescription;
        if (TextUtils.isEmpty((CharSequence)secondaryLabel)) {
            contentDescription = booleanState.label;
        }
        else {
            contentDescription = TextUtils.concat(new CharSequence[] { booleanState.label, ", ", booleanState.secondaryLabel });
        }
        booleanState.contentDescription = contentDescription;
    }
    
    @Override
    protected void handleUserSwitch(final int n) {
        if (this.mIsListening) {
            this.mListener.setCallback((NightDisplayListener$Callback)null);
        }
        final NightDisplayListener mListener = new NightDisplayListener(super.mContext, n, new Handler(Looper.myLooper()));
        this.mListener = mListener;
        if (this.mIsListening) {
            mListener.setCallback((NightDisplayListener$Callback)this);
        }
        super.handleUserSwitch(n);
    }
    
    @Override
    public boolean isAvailable() {
        return ColorDisplayManager.isNightDisplayAvailable(super.mContext);
    }
    
    @Override
    public BooleanState newTileState() {
        return new QSTile.BooleanState();
    }
    
    public void onActivated(final boolean b) {
        this.refreshState();
    }
    
    @Override
    public LogMaker populate(final LogMaker logMaker) {
        return super.populate(logMaker).addTaggedData(1311, (Object)this.mManager.getNightDisplayAutoModeRaw());
    }
}
