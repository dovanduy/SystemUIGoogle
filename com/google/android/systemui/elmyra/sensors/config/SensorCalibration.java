// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.elmyra.sensors.config;

import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;
import android.util.Log;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.util.ArrayMap;
import java.io.InputStream;
import java.util.Map;

public class SensorCalibration
{
    private final Map<String, Float> mProperties;
    
    SensorCalibration(InputStream in) {
        this.mProperties = (Map<String, Float>)new ArrayMap();
        in = (IOException)new BufferedReader(new InputStreamReader((InputStream)in));
        while (true) {
            String substring = null;
            String substring2 = null;
            try {
                while (true) {
                    final String line = ((BufferedReader)in).readLine();
                    if (line == null) {
                        return;
                    }
                    final int index = line.indexOf(58);
                    if (index == -1) {
                        continue;
                    }
                    substring = line.substring(0, index);
                    substring2 = line.substring(index + 1);
                    final SensorCalibration sensorCalibration = this;
                    final Map<String, Float> map = sensorCalibration.mProperties;
                    final String s = substring;
                    final String s2 = s.trim();
                    final String s3 = substring2;
                    final float n = Float.parseFloat(s3);
                    final Float n2 = n;
                    map.put(s2, n2);
                }
            }
            catch (IOException in) {
                Log.e("Elmyra/SensorCalibration", "Error reading calibration file", (Throwable)in);
            }
            try {
                final SensorCalibration sensorCalibration = this;
                final Map<String, Float> map = sensorCalibration.mProperties;
                final String s = substring;
                final String s2 = s.trim();
                final String s3 = substring2;
                final float n = Float.parseFloat(s3);
                final Float n2 = n;
                map.put(s2, n2);
                continue;
            }
            catch (NumberFormatException ex) {
                continue;
            }
            break;
        }
    }
    
    public static SensorCalibration getCalibration(final int i) {
        try {
            return new SensorCalibration(new FileInputStream(String.format("/persist/sensors/elmyra/calibration.%d", i)));
        }
        catch (SecurityException ex) {
            Log.e("Elmyra/SensorCalibration", "Could not open calibration file", (Throwable)ex);
        }
        catch (FileNotFoundException ex2) {
            Log.e("Elmyra/SensorCalibration", "Could not find calibration file", (Throwable)ex2);
        }
        return null;
    }
    
    public boolean contains(final String s) {
        return this.mProperties.containsKey(s);
    }
    
    public float get(final String s) {
        return this.mProperties.get(s);
    }
}
