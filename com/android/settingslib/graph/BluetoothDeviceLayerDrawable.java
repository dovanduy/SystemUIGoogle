// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.graph;

import android.content.res.Resources;
import android.graphics.ColorFilter;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuff$Mode;
import com.android.settingslib.Utils;
import com.android.settingslib.R$fraction;
import android.graphics.drawable.Drawable$ConstantState;
import com.android.settingslib.R$dimen;
import com.android.settingslib.R$color;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

public class BluetoothDeviceLayerDrawable extends LayerDrawable
{
    private BluetoothDeviceLayerDrawableState mState;
    
    private BluetoothDeviceLayerDrawable(final Drawable[] array) {
        super(array);
    }
    
    public static BluetoothDeviceLayerDrawable createLayerDrawable(final Context context, final int n, final int n2, final float n3) {
        final Drawable drawable = context.getDrawable(n);
        final BatteryMeterDrawable batteryMeterDrawable = new BatteryMeterDrawable(context, context.getColor(R$color.meter_background_color), n2);
        final int dimensionPixelSize = context.getResources().getDimensionPixelSize(R$dimen.bt_battery_padding);
        batteryMeterDrawable.setPadding(dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize);
        final BluetoothDeviceLayerDrawable bluetoothDeviceLayerDrawable = new BluetoothDeviceLayerDrawable(new Drawable[] { drawable, batteryMeterDrawable });
        bluetoothDeviceLayerDrawable.setLayerGravity(0, 8388611);
        bluetoothDeviceLayerDrawable.setLayerInsetStart(1, drawable.getIntrinsicWidth());
        bluetoothDeviceLayerDrawable.setLayerInsetTop(1, (int)(drawable.getIntrinsicHeight() * (1.0f - n3)));
        bluetoothDeviceLayerDrawable.setConstantState(context, n, n2, n3);
        return bluetoothDeviceLayerDrawable;
    }
    
    public Drawable$ConstantState getConstantState() {
        return this.mState;
    }
    
    public void setConstantState(final Context context, final int n, final int n2, final float n3) {
        this.mState = new BluetoothDeviceLayerDrawableState(context, n, n2, n3);
    }
    
    static class BatteryMeterDrawable extends BatteryMeterDrawableBase
    {
        private final float mAspectRatio;
        int mFrameColor;
        
        public BatteryMeterDrawable(final Context context, final int mFrameColor, final int batteryLevel) {
            super(context, mFrameColor);
            final Resources resources = context.getResources();
            super.mButtonHeightFraction = resources.getFraction(R$fraction.bt_battery_button_height_fraction, 1, 1);
            this.mAspectRatio = resources.getFraction(R$fraction.bt_battery_ratio_fraction, 1, 1);
            this.setColorFilter((ColorFilter)new PorterDuffColorFilter(Utils.getColorAttrDefaultColor(context, 16843817), PorterDuff$Mode.SRC_IN));
            this.setBatteryLevel(batteryLevel);
            this.mFrameColor = mFrameColor;
        }
        
        @Override
        protected float getAspectRatio() {
            return this.mAspectRatio;
        }
        
        @Override
        protected float getRadiusRatio() {
            return 0.0f;
        }
    }
    
    private static class BluetoothDeviceLayerDrawableState extends Drawable$ConstantState
    {
        int batteryLevel;
        Context context;
        float iconScale;
        int resId;
        
        public BluetoothDeviceLayerDrawableState(final Context context, final int resId, final int batteryLevel, final float iconScale) {
            this.context = context;
            this.resId = resId;
            this.batteryLevel = batteryLevel;
            this.iconScale = iconScale;
        }
        
        public int getChangingConfigurations() {
            return 0;
        }
        
        public Drawable newDrawable() {
            return (Drawable)BluetoothDeviceLayerDrawable.createLayerDrawable(this.context, this.resId, this.batteryLevel, this.iconScale);
        }
    }
}
