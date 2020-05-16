// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.bluetooth;

import android.net.Uri;
import java.io.IOException;
import com.android.settingslib.widget.AdaptiveOutlineDrawable;
import android.graphics.Bitmap;
import android.provider.MediaStore$Images$Media;
import android.util.Log;
import com.android.settingslib.R$dimen;
import java.util.Iterator;
import android.bluetooth.BluetoothClass;
import com.android.settingslib.R$string;
import android.util.Pair;
import android.bluetooth.BluetoothDevice;
import android.content.res.Resources;
import com.android.settingslib.widget.AdaptiveIcon;
import com.android.settingslib.R$array;
import android.graphics.drawable.Drawable;
import android.content.Context;

public class BluetoothUtils
{
    private static ErrorListener sErrorListener;
    
    public static Drawable buildBtRainbowDrawable(final Context context, final Drawable drawable, int abs) {
        final Resources resources = context.getResources();
        final int[] intArray = resources.getIntArray(R$array.bt_icon_fg_colors);
        final int[] intArray2 = resources.getIntArray(R$array.bt_icon_bg_colors);
        abs = Math.abs(abs % intArray2.length);
        drawable.setTint(intArray[abs]);
        final AdaptiveIcon adaptiveIcon = new AdaptiveIcon(context, drawable);
        adaptiveIcon.setBackgroundColor(intArray2[abs]);
        return (Drawable)adaptiveIcon;
    }
    
    public static Drawable getBluetoothDrawable(final Context context, final int n) {
        return context.getDrawable(n);
    }
    
    public static boolean getBooleanMetaData(final BluetoothDevice bluetoothDevice, final int n) {
        if (bluetoothDevice == null) {
            return false;
        }
        final byte[] metadata = bluetoothDevice.getMetadata(n);
        return metadata != null && Boolean.parseBoolean(new String(metadata));
    }
    
    public static Pair<Drawable, String> getBtClassDrawableWithDescription(final Context context, final CachedBluetoothDevice cachedBluetoothDevice) {
        final BluetoothClass btClass = cachedBluetoothDevice.getBtClass();
        if (btClass != null) {
            final int majorDeviceClass = btClass.getMajorDeviceClass();
            if (majorDeviceClass == 256) {
                return (Pair<Drawable, String>)new Pair((Object)getBluetoothDrawable(context, 17302323), (Object)context.getString(R$string.bluetooth_talkback_computer));
            }
            if (majorDeviceClass == 512) {
                return (Pair<Drawable, String>)new Pair((Object)getBluetoothDrawable(context, 17302783), (Object)context.getString(R$string.bluetooth_talkback_phone));
            }
            if (majorDeviceClass == 1280) {
                return (Pair<Drawable, String>)new Pair((Object)getBluetoothDrawable(context, HidProfile.getHidClassDrawable(btClass)), (Object)context.getString(R$string.bluetooth_talkback_input_peripheral));
            }
            if (majorDeviceClass == 1536) {
                return (Pair<Drawable, String>)new Pair((Object)getBluetoothDrawable(context, 17302815), (Object)context.getString(R$string.bluetooth_talkback_imaging));
            }
        }
        final Iterator<LocalBluetoothProfile> iterator = cachedBluetoothDevice.getProfiles().iterator();
        while (iterator.hasNext()) {
            final int drawableResource = iterator.next().getDrawableResource(btClass);
            if (drawableResource != 0) {
                return (Pair<Drawable, String>)new Pair((Object)getBluetoothDrawable(context, drawableResource), (Object)null);
            }
        }
        if (btClass != null) {
            if (btClass.doesClassMatch(0)) {
                return (Pair<Drawable, String>)new Pair((Object)getBluetoothDrawable(context, 17302321), (Object)context.getString(R$string.bluetooth_talkback_headset));
            }
            if (btClass.doesClassMatch(1)) {
                return (Pair<Drawable, String>)new Pair((Object)getBluetoothDrawable(context, 17302320), (Object)context.getString(R$string.bluetooth_talkback_headphone));
            }
        }
        return (Pair<Drawable, String>)new Pair((Object)getBluetoothDrawable(context, 17302813).mutate(), (Object)context.getString(R$string.bluetooth_talkback_bluetooth));
    }
    
    public static Pair<Drawable, String> getBtRainbowDrawableWithDescription(final Context context, final CachedBluetoothDevice cachedBluetoothDevice) {
        final Pair<Drawable, String> btClassDrawableWithDescription = getBtClassDrawableWithDescription(context, cachedBluetoothDevice);
        final BluetoothDevice device = cachedBluetoothDevice.getDevice();
        final boolean booleanMetaData = getBooleanMetaData(device, 6);
        final int dimensionPixelSize = context.getResources().getDimensionPixelSize(R$dimen.bt_nearby_icon_size);
        final Resources resources = context.getResources();
        if (booleanMetaData) {
            final Uri uriMetaData = getUriMetaData(device, 5);
            if (uriMetaData != null) {
                try {
                    context.getContentResolver().takePersistableUriPermission(uriMetaData, 1);
                }
                catch (SecurityException ex) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Failed to take persistable permission for: ");
                    sb.append(uriMetaData);
                    Log.e("BluetoothUtils", sb.toString(), (Throwable)ex);
                }
                try {
                    final Bitmap bitmap = MediaStore$Images$Media.getBitmap(context.getContentResolver(), uriMetaData);
                    if (bitmap != null) {
                        final Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, dimensionPixelSize, dimensionPixelSize, false);
                        bitmap.recycle();
                        return (Pair<Drawable, String>)new Pair((Object)new AdaptiveOutlineDrawable(resources, scaledBitmap), (Object)btClassDrawableWithDescription.second);
                    }
                }
                catch (SecurityException ex2) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("Failed to get permission for: ");
                    sb2.append(uriMetaData);
                    Log.e("BluetoothUtils", sb2.toString(), (Throwable)ex2);
                }
                catch (IOException ex3) {
                    final StringBuilder sb3 = new StringBuilder();
                    sb3.append("Failed to get drawable for: ");
                    sb3.append(uriMetaData);
                    Log.e("BluetoothUtils", sb3.toString(), (Throwable)ex3);
                }
            }
        }
        return (Pair<Drawable, String>)new Pair((Object)buildBtRainbowDrawable(context, (Drawable)btClassDrawableWithDescription.first, cachedBluetoothDevice.getAddress().hashCode()), (Object)btClassDrawableWithDescription.second);
    }
    
    public static String getStringMetaData(final BluetoothDevice bluetoothDevice, final int n) {
        if (bluetoothDevice == null) {
            return null;
        }
        final byte[] metadata = bluetoothDevice.getMetadata(n);
        if (metadata == null) {
            return null;
        }
        return new String(metadata);
    }
    
    public static Uri getUriMetaData(final BluetoothDevice bluetoothDevice, final int n) {
        final String stringMetaData = getStringMetaData(bluetoothDevice, n);
        if (stringMetaData == null) {
            return null;
        }
        return Uri.parse(stringMetaData);
    }
    
    public static void setErrorListener(final ErrorListener sErrorListener) {
        BluetoothUtils.sErrorListener = sErrorListener;
    }
    
    static void showError(final Context context, final String s, final int n) {
        final ErrorListener sErrorListener = BluetoothUtils.sErrorListener;
        if (sErrorListener != null) {
            sErrorListener.onShowError(context, s, n);
        }
    }
    
    public interface ErrorListener
    {
        void onShowError(final Context p0, final String p1, final int p2);
    }
}
