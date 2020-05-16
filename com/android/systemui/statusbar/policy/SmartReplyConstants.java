// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.policy;

import java.util.concurrent.Executor;
import android.text.TextUtils;
import android.content.res.Resources;
import com.android.systemui.R$integer;
import com.android.systemui.R$bool;
import android.util.Log;
import android.provider.DeviceConfig$Properties;
import android.util.KeyValueListParser;
import android.provider.DeviceConfig$OnPropertiesChangedListener;
import android.os.Handler;
import com.android.systemui.util.DeviceConfigProxy;
import android.content.Context;

public final class SmartReplyConstants
{
    private final Context mContext;
    private final boolean mDefaultEditChoicesBeforeSending;
    private final boolean mDefaultEnabled;
    private final int mDefaultMaxNumActions;
    private final int mDefaultMaxSqueezeRemeasureAttempts;
    private final int mDefaultMinNumSystemGeneratedReplies;
    private final int mDefaultOnClickInitDelay;
    private final boolean mDefaultRequiresP;
    private final boolean mDefaultShowInHeadsUp;
    private final DeviceConfigProxy mDeviceConfig;
    private volatile boolean mEditChoicesBeforeSending;
    private volatile boolean mEnabled;
    private final Handler mHandler;
    private volatile int mMaxNumActions;
    private volatile int mMaxSqueezeRemeasureAttempts;
    private volatile int mMinNumSystemGeneratedReplies;
    private volatile long mOnClickInitDelay;
    private final DeviceConfig$OnPropertiesChangedListener mOnPropertiesChangedListener;
    private volatile boolean mRequiresTargetingP;
    private volatile boolean mShowInHeadsUp;
    
    public SmartReplyConstants(final Handler mHandler, final Context mContext, final DeviceConfigProxy mDeviceConfig) {
        new KeyValueListParser(',');
        this.mOnPropertiesChangedListener = (DeviceConfig$OnPropertiesChangedListener)new DeviceConfig$OnPropertiesChangedListener() {
            public void onPropertiesChanged(final DeviceConfig$Properties deviceConfig$Properties) {
                if (!"systemui".equals(deviceConfig$Properties.getNamespace())) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Received update from DeviceConfig for unrelated namespace: ");
                    sb.append(deviceConfig$Properties.getNamespace());
                    Log.e("SmartReplyConstants", sb.toString());
                    return;
                }
                SmartReplyConstants.this.updateConstants();
            }
        };
        this.mHandler = mHandler;
        this.mContext = mContext;
        final Resources resources = mContext.getResources();
        this.mDefaultEnabled = resources.getBoolean(R$bool.config_smart_replies_in_notifications_enabled);
        this.mDefaultRequiresP = resources.getBoolean(R$bool.config_smart_replies_in_notifications_requires_targeting_p);
        this.mDefaultMaxSqueezeRemeasureAttempts = resources.getInteger(R$integer.config_smart_replies_in_notifications_max_squeeze_remeasure_attempts);
        this.mDefaultEditChoicesBeforeSending = resources.getBoolean(R$bool.config_smart_replies_in_notifications_edit_choices_before_sending);
        this.mDefaultShowInHeadsUp = resources.getBoolean(R$bool.config_smart_replies_in_notifications_show_in_heads_up);
        this.mDefaultMinNumSystemGeneratedReplies = resources.getInteger(R$integer.config_smart_replies_in_notifications_min_num_system_generated_replies);
        this.mDefaultMaxNumActions = resources.getInteger(R$integer.config_smart_replies_in_notifications_max_num_actions);
        this.mDefaultOnClickInitDelay = resources.getInteger(R$integer.config_smart_replies_in_notifications_onclick_init_delay);
        this.mDeviceConfig = mDeviceConfig;
        this.registerDeviceConfigListener();
        this.updateConstants();
    }
    
    private void postToHandler(final Runnable runnable) {
        this.mHandler.post(runnable);
    }
    
    private boolean readDeviceConfigBooleanOrDefaultIfEmpty(String property, final boolean b) {
        property = this.mDeviceConfig.getProperty("systemui", property);
        if (TextUtils.isEmpty((CharSequence)property)) {
            return b;
        }
        return "true".equals(property) || (!"false".equals(property) && b);
    }
    
    private void registerDeviceConfigListener() {
        this.mDeviceConfig.addOnPropertiesChangedListener("systemui", new _$$Lambda$SmartReplyConstants$6OXW9pAAXeePuUfPuGxYU98bifc(this), this.mOnPropertiesChangedListener);
    }
    
    private void updateConstants() {
        synchronized (this) {
            this.mEnabled = this.readDeviceConfigBooleanOrDefaultIfEmpty("ssin_enabled", this.mDefaultEnabled);
            this.mRequiresTargetingP = this.readDeviceConfigBooleanOrDefaultIfEmpty("ssin_requires_targeting_p", this.mDefaultRequiresP);
            this.mMaxSqueezeRemeasureAttempts = this.mDeviceConfig.getInt("systemui", "ssin_max_squeeze_remeasure_attempts", this.mDefaultMaxSqueezeRemeasureAttempts);
            this.mEditChoicesBeforeSending = this.readDeviceConfigBooleanOrDefaultIfEmpty("ssin_edit_choices_before_sending", this.mDefaultEditChoicesBeforeSending);
            this.mShowInHeadsUp = this.readDeviceConfigBooleanOrDefaultIfEmpty("ssin_show_in_heads_up", this.mDefaultShowInHeadsUp);
            this.mMinNumSystemGeneratedReplies = this.mDeviceConfig.getInt("systemui", "ssin_min_num_system_generated_replies", this.mDefaultMinNumSystemGeneratedReplies);
            this.mMaxNumActions = this.mDeviceConfig.getInt("systemui", "ssin_max_num_actions", this.mDefaultMaxNumActions);
            this.mOnClickInitDelay = this.mDeviceConfig.getInt("systemui", "ssin_onclick_init_delay", this.mDefaultOnClickInitDelay);
        }
    }
    
    public boolean getEffectiveEditChoicesBeforeSending(final int n) {
        return n != 1 && (n == 2 || this.mEditChoicesBeforeSending);
    }
    
    public int getMaxNumActions() {
        return this.mMaxNumActions;
    }
    
    public int getMaxSqueezeRemeasureAttempts() {
        return this.mMaxSqueezeRemeasureAttempts;
    }
    
    public int getMinNumSystemGeneratedReplies() {
        return this.mMinNumSystemGeneratedReplies;
    }
    
    public long getOnClickInitDelay() {
        return this.mOnClickInitDelay;
    }
    
    public boolean getShowInHeadsUp() {
        return this.mShowInHeadsUp;
    }
    
    public boolean isEnabled() {
        return this.mEnabled;
    }
    
    public boolean requiresTargetingP() {
        return this.mRequiresTargetingP;
    }
}
